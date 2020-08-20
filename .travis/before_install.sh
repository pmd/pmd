#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

travis_debug

OPENJDK_VERSION=$1

bash .travis/setup-secrets.sh
bash .travis/configure-maven.sh
bash .travis/install-openjdk.sh $OPENJDK_VERSION


if travis_isLinux; then
    gem install bundler
    bundle config set --local path vendor/bundle
    bundle config set --local with release_notes_preprocessing
    bundle install

    # install jdk7 for integration test
    LOCAL_DIR=${HOME}/.cache/jdk7
    TARGET_DIR=${HOME}/oraclejdk7
    JDK7_ARCHIVE=jdk-7u80-linux-x64.tar.gz
    DOWNLOAD_URL=https://pmd-code.org/oraclejdk/${JDK7_ARCHIVE}
    mkdir -p ${LOCAL_DIR}
    mkdir -p ${TARGET_DIR}
    if [ ! -e ${LOCAL_DIR}/${JDK7_ARCHIVE} ]; then
        log_info "Downloading from ${DOWNLOAD_URL} to ${LOCAL_DIR}"
        wget --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
    else
        log_info "Skipped download, file ${LOCAL_DIR}/${JDK7_ARCHIVE} already exists"
    fi
    log_info "Extracting to ${TARGET_DIR}"
    tar --extract --file ${LOCAL_DIR}/${JDK7_ARCHIVE} -C ${TARGET_DIR} --strip-components=1
    log_info "OracleJDK7 can be used via -Djava7.home=${TARGET_DIR}"

else
    log_info "Not setting up ruby for ${TRAVIS_OS_NAME}."
    exit 0
fi
