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

    # install openjdk8 for integration test
    LOCAL_DIR=${HOME}/.cache/jdk8
    TARGET_DIR=${HOME}/openjdk8
    OPENJDK8_ARCHIVE=OpenJDK8U-jdk_x64_linux_hotspot_8u265b01.tar.gz
    DOWNLOAD_URL=https://pmd-code.org/openjdk/jdk-8u265b01/${OPENJDK8_ARCHIVE}
    mkdir -p ${LOCAL_DIR}
    mkdir -p ${TARGET_DIR}
    if [ ! -e ${LOCAL_DIR}/${OPENJDK8_ARCHIVE} ]; then
        log_info "Downloading from ${DOWNLOAD_URL} to ${LOCAL_DIR}"
        wget --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
    else
        log_info "Skipped download, file ${LOCAL_DIR}/${OPENJDK8_ARCHIVE} already exists"
    fi
    log_info "Extracting to ${TARGET_DIR}"
    tar --extract --file ${LOCAL_DIR}/${OPENJDK8_ARCHIVE} -C ${TARGET_DIR} --strip-components=1
    log_info "OpenJDK8 can be used via -Djava8.home=${TARGET_DIR}"

else
    log_info "Not setting up ruby for ${TRAVIS_OS_NAME}."
    exit 0
fi
