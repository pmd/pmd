#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/setup-secrets.inc
source $(dirname $0)/inc/sourceforge-api.inc
source $(dirname $0)/inc/pmd-doc.inc
source $(dirname $0)/inc/pmd-code-api.inc
source $(dirname $0)/inc/regression-tester.inc
source ${HOME}/java.env

set -e
#set -x

function pmd_ci_build_main() {
    pmd_ci_setup_private_env
    pmd_ci_setup_gpg_key
    pmd_ci_setup_ssh

    pmd_ci_build_setup_maven
    pmd_ci_build_setup_oraclejdk7
    pmd_ci_build_setup_bundler

    VERSION=$(pmd_ci_build_get_pom_version)
    log_info "Building PMD ${VERSION}..."

    pmd_ci_build_run

    # Deploy to sourceforge files
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

    pmd_ci_build_and_upload_doc

    regression_tester_setup_ci
    regression_tester_uploadBaseline

    exit 0
}

function pmd_ci_build_and_upload_doc() {
    pmd_doc_generate_jekyll_site
    pmd_doc_create_archive

    sourceforge_uploadFile "${VERSION}" "docs/pmd-doc-${VERSION}.zip"

    # Deploy doc to https://docs.pmd-code.org/pmd-doc-${VERSION}/
    pmd_code_uploadDocumentation "${VERSION}" "docs/pmd-doc-${VERSION}.zip"
    # Deploy javadoc to https://docs.pmd-code.org/apidocs/*/${VERSION}/
    pmd_code_uploadJavadoc "${VERSION}" "$(pwd)"

    pmd_code_createSymlink "${VERSION}" "snapshot"

    # update github pages https://pmd.github.io/pmd/
    pmd_doc_publish_to_github_pages
    # rsync site to https://pmd.sourceforge.io/snapshot
    sourceforge_rsyncSnapshotDocumentation "${VERSION}" "snapshot"
}

function pmd_ci_build_get_pom_version() {
    echo $(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:3.0.0:exec)
}

function pmd_ci_build_setup_maven() {
    # configure maven
    mkdir -p ${HOME}/.m2
    cp .ci/files/maven-settings.xml ${HOME}/.m2/settings.xml
}

function pmd_ci_build_setup_oraclejdk7() {
    # install jdk7 for integration test

    LOCAL_DIR="${HOME}/.cache/jdk7"
    TARGET_DIR="${HOME}/oraclejdk7"
    DOWNLOAD_URL="https://pmd-code.org/oraclejdk/jdk-7u80-linux-x64.tar.gz"
    ARCHIVE=$(basename $DOWNLOAD_URL)

    mkdir -p ${LOCAL_DIR}
    mkdir -p ${TARGET_DIR}
    if [ ! -e ${LOCAL_DIR}/${ARCHIVE} ]; then
        log_info "Downloading from ${DOWNLOAD_URL} to ${LOCAL_DIR}"
        curl --location --output ${LOCAL_DIR}/${ARCHIVE} ${DOWNLOAD_URL}
    else
        log_info "Skipped download, file ${LOCAL_DIR}/${ARCHIVE} already exists"
    fi
    log_info "Extracting to ${TARGET_DIR}"
    tar --extract --file ${LOCAL_DIR}/${ARCHIVE} -C ${TARGET_DIR} --strip-components=1

    log_info "OracleJDK7 can be used via -Djava7.home=${HOME}/oraclejdk7"
}

function pmd_ci_build_run() {
    log_info "This is a snapshot build"
    #export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
    #export MAVEN_OPTS="-Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false"
    ./mvnw deploy -Possrh,sign,generate-rule-docs -e -V -Djava7.home=${HOME}/oraclejdk7
}

# Needed for doc generation and regression tester
function pmd_ci_build_setup_bundler() {
    log_info "Installing bundler..."
    gem install bundler
}

pmd_ci_build_main
