#!/bin/bash
set -e


function travis_debug() {
    echo "TRAVIS_REPO_SLUG: ${TRAVIS_REPO_SLUG}"
    echo "TRAVIS_PULL_REQUEST_SLUG: ${TRAVIS_PULL_REQUEST_SLUG}"
    echo "TRAVIS_PULL_REQUEST_BRANCH: ${TRAVIS_PULL_REQUEST_BRANCH}"
    echo "TRAVIS_PULL_REQUEST: ${TRAVIS_PULL_REQUEST}"
    echo "TRAVIS_SECURE_ENV_VARS: ${TRAVIS_SECURE_ENV_VARS}"
    echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
    echo "TRAVIS_TAG: ${TRAVIS_TAG}"
    echo "TRAVIS_ALLOW_FAILURE: ${TRAVIS_ALLOW_FAILURE}"
    echo "TRAVIS_OS_NAME: ${TRAVIS_OS_NAME}"
}

function travis_isPullRequest() {
    if [ "${TRAVIS_REPO_SLUG}" != "pmd/pmd" ] || [ "${TRAVIS_PULL_REQUEST}" != "false" ]; then
        return 0
    else
        return 1
    fi
}

function travis_isPush() {
    if [ "${TRAVIS_REPO_SLUG}" = "pmd/pmd" ] && [ "${TRAVIS_PULL_REQUEST}" = "false" ] && [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ]; then
        return 0
    else
        return 1
    fi
}

function travis_isOSX() {
    if [[ $TRAVIS_OS_NAME == 'osx' ]]; then
        return 0
    else
        return 1
    fi
}

function travis_isLinux() {
    if [[ $TRAVIS_OS_NAME == 'linux' ]]; then
        return 0
    else
        return 1
    fi
}

function travis_isWindows() {
    if [[ $TRAVIS_OS_NAME == 'windows' ]]; then
        return 0
    else
        return 1
    fi
}
