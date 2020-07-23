#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh
source .travis/github-releases-api.sh
source .travis/sourceforge-api.sh

VERSION=$(get_pom_version)
log_info "PMD Release ${VERSION}"

if ! travis_isPush; then
    log_info "Not publishing release, since this is not a push!"
    exit 0
fi

if [[ "${VERSION}" == *-SNAPSHOT || "${TRAVIS_TAG}" == "" ]]; then
    log_info "Not publishing release, since this is not a release!"
    exit 0
fi


# Publish github release
gh_releases_getLatestDraftRelease
GH_RELEASE="$RESULT"

gh_release_publishRelease "$GH_RELEASE"
sourceforge_selectDefault "${VERSION}"
