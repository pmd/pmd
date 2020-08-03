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
else
    log_info "Not setting up ruby for ${TRAVIS_OS_NAME}."
    exit 0
fi
