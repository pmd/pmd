#!/usr/bin/env bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)

main() {
    check_branch
    setup_ssh
    upload_docs
    upload_javadocs
    build_aggregate_javadoc
}

check_branch() {
    if [[ "${VERSION}" != "7.0.0-SNAPSHOT" || "${TRAVIS_BRANCH}" != "pmd/7.0.x" ]]; then
        log_info "Not on PMD 7 branch - exiting $0"
        exit 0
    fi
    log_info "Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"
}

setup_ssh() {
    mkdir -p "$HOME/.ssh"
    echo "pmd-code.org ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDVsIeF6xU0oPb/bMbxG1nU1NDyBpR/cBEPZcm/PuJwdI9B0ydPHA6FysqAnt32fNFznC2SWisnWyY3iNsP3pa8RQJVwmnnv9OboGFlW2/61o3iRyydcpPbgl+ADdt8iU9fmMI7dC04UqgHGBoqOwVNna9VylTjp5709cK2qHnwU450F6YcOEiOKeZfJvV4PmpJCz/JcsUVqft6StviR31jKnqbnkZdP8qNoTbds6WmGKyXkhHdLSZE7X1CFQH28tk8XFqditX93ezeCiThFL7EleDexV/3+2+cs5878sDMUMzHS5KShTjkxzhHaodhtIEdNesinq/hOPbxAGkQ0FbD" >> $HOME/.ssh/known_hosts
}

upload_docs() {
    if has_docs_change; then
        scp docs/pmd-doc-${VERSION}.zip pmd@pmd-code.org:/docs.pmd-code.org/
        ssh pmd@pmd-code.org "cd /docs.pmd-code.org && \
            unzip -qo pmd-doc-${VERSION}.zip && \
            rm pmd-doc-${VERSION}.zip"
        log_info "Docs updated: https://docs.pmd-code.org/pmd-doc-${VERSION}/"
    fi
}

upload_javadocs() {
    upload_javadoc_module pmd-core
    upload_javadoc_module pmd-java
}

upload_javadoc_module() {
    local module=$1
    pushd $module/target
    scp "${module}-${VERSION}-javadoc.jar" pmd@pmd-code.org:/docs.pmd-code.org/
    ssh pmd@pmd-code.org "cd /docs.pmd-code.org && mkdir -p pmd-doc-${VERSION}/apidocs/${module} && \
            unzip -qo -d pmd-doc-${VERSION}/apidocs/${module} ${module}-${VERSION}-javadoc.jar && \
            rm ${module}-${VERSION}-javadoc.jar"
    log_info "JavaDoc for $module uploaded: https://docs.pmd-code.org/pmd-doc-${VERSION}/apidocs/${module}/"
    popd
}

build_aggregate_javadoc() {
    ./mvnw javadoc:aggregate-jar -Pjavadoc-aggregate
    scp target/pmd-${VERSION}-javadoc.jar pmd@pmd-code.org:/docs.pmd-code.org/
    ssh pmd@pmd-code.org "cd /docs.pmd-code.org && \
        mkdir -p pmd-doc-${VERSION}/apidocs && \
        unzip -qo -d pmd-doc-${VERSION}/apidocs pmd-${VERSION}-javadoc.jar && \
        rm pmd-${VERSION}-javadoc.jar"
    log_info "Aggregated JavaDoc: https://docs.pmd-code.org/pmd-doc-${VERSION}/apidocs/"
}


main
