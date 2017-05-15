#!/bin/bash

function travis_isPullRequest() {
    if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
        return true
    else
        return false
    fi
}

function travis_isPush() {
    if [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ]; then
        return true
    else
        return false
    fi
}
