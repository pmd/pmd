#!/bin/bash
set -e


echo "TRAVIS_REPO_SLUG: $TRAVIS_REPO_SLUG"
echo "TRAVIS_PULL_REQUEST_SLUG: $TRAVIS_PULL_REQUEST_SLUG"
echo "TRAVIS_PULL_REQUEST_BRANCH: $TRAVIS_PULL_REQUEST_BRANCH"
echo "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
echo "TRAVIS_SECURE_ENV_VARS: $TRAVIS_SECURE_ENV_VARS"
echo "TRAVIS_BRANCH: $TRAVIS_BRANCH"
echo "TRAVIS_TAG: $TRAVIS_TAG"
echo "TRAVIS_ALLOW_FAILURE: $TRAVIS_ALLOW_FAILURE"


function travis_isPullRequest() {
    if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
        return 0
    else
        return 1
    fi
}

function travis_isPush() {
    if [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ]; then
        return 0
    else
        return 1
    fi
}

#
# Since travis_wait outputs the "Still running" indication into the
# same stream as the command's output, we need to make sure, we
# output these indications to avoid a build timeout.
# But to workaround the log size limit, we shouldn't output everything.
# travis_wait_truncated now outputs the head immediately, while
# only outputting the tail of the log after the command is finished.
#
function travis_wait_truncated() {
    local log=$(tempfile)
    travis_wait "$@" | tee $log | head -100
    tail -100 $log
    rm -f $log
}