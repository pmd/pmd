#!/usr/bin/env bash

source $(dirname $0)/logger.inc
source $(dirname $0)/setup-secrets.inc
source $(dirname $0)/sourceforge-api.inc
source ${HOME}/java.env

set -e
#set -x

function pmd_ci_build_main() {
    pmd_ci_setup_env
    pmd_ci_setup_gpg_key
    pmd_ci_setup_ssh

    pmd_ci_build_setup_maven
    pmd_ci_build_setup_oraclejdk7
    pmd_ci_build_run

    # Deploy to sourceforge files
    VERSION=$(pmd_ci_build_get_pom_version)
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

    #build and upload doc

    pmd_ci_build_setup_regression-tester
    #regression-tester_uploadBaseline


    exit 0
}

function pmd_ci_build_get_pom_version() {
    echo $(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:3.0.0:exec)
}

function pmd_ci_build_setup_maven() {
    # configure maven
    echo "MAVEN_OPTS='-Xms1g -Xmx1g'" > ${HOME}/.mavenrc
    mkdir -p ${HOME}/.m2
    cp .ci/maven-settings.xml ${HOME}/.m2/settings.xml
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
    MVN_BUILD_FLAGS="-B -V  -Djava7.home=${HOME}/oraclejdk7"

    log_info "This is a snapshot build"
    ./mvnw deploy -Possrh,sign $MVN_BUILD_FLAGS
}

function pmd_ci_build_setup_regression-tester() {
    # install openjdk8 for pmd-regression-tests
    .ci/install-openjdk.sh 8
    gem install bundler
    bundle config set --local path vendor/bundle
    bundle config set --local with release_notes_preprocessing
    bundle install
}

pmd_ci_build_main
