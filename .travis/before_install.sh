#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

travis_debug

OPENJDK_VERSION=$1

bash .travis/setup-secrets.sh
bash .travis/configure-maven.sh
bash .travis/install-openjdk.sh $OPENJDK_VERSION


function install_jdk() {
    LOCAL_DIR=$1
    TARGET_DIR=$2
    DOWNLOAD_URL=$3
    ARCHIVE=$(basename $DOWNLOAD_URL)

    mkdir -p ${LOCAL_DIR}
    mkdir -p ${TARGET_DIR}
    if [ ! -e ${LOCAL_DIR}/${ARCHIVE} ]; then
        log_info "Downloading from ${DOWNLOAD_URL} to ${LOCAL_DIR}"
        wget --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
    else
        log_info "Skipped download, file ${LOCAL_DIR}/${ARCHIVE} already exists"
    fi
    log_info "Extracting to ${TARGET_DIR}"
    tar --extract --file ${LOCAL_DIR}/${ARCHIVE} -C ${TARGET_DIR} --strip-components=1
}

if travis_isLinux; then
    change_ruby_version
    gem install bundler
    bundle config set --local path vendor/bundle
    bundle config set --local with release_notes_preprocessing
    bundle install

    # install jdk7 for integration test
    install_jdk "${HOME}/.cache/jdk7" "${HOME}/oraclejdk7" "https://pmd-code.org/oraclejdk/jdk-7u80-linux-x64.tar.gz"
    log_info "OracleJDK7 can be used via -Djava7.home=${HOME}/oraclejdk7"

    # install openjdk8 for pmd-regression-tests
    install_jdk "${HOME}/.cache/openjdk" "${HOME}/openjdk8" "https://pmd-code.org/openjdk/latest/jdk-8-linux64.tar.gz"
    log_info "OpenJDK8 can be used from ${HOME}/openjdk8"
else
    log_info "Not setting up ruby and additional jvms for ${TRAVIS_OS_NAME}."
    exit 0
fi
