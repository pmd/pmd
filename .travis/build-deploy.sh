#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh
source .travis/github-releases-api.sh
source .travis/sourceforge-api.sh
source .travis/regression-tester.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
log_info "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

MVN_BUILD_FLAGS="-B -V"

if travis_isOSX; then

    log_info "The build is running on OSX"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isWindows; then

    log_info "The build is running on Windows"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isPullRequest; then

    log_info "This is a pull-request build"
    ./mvnw verify $MVN_BUILD_FLAGS

    regression-tester_executeDanger

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo -e "\n\n"
        log_info "This is a release build for tag ${TRAVIS_TAG}"
        echo -e "\n\n"

        # create a draft github release
        gh_releases_createDraftRelease "${TRAVIS_TAG}" "$(git rev-list -n 1 ${TRAVIS_TAG})"
        GH_RELEASE="$RESULT"

        # Build and deploy to ossrh / maven-central
        ./mvnw deploy -Possrh,sign,pmd-release $MVN_BUILD_FLAGS
        echo -e "\n\n"

        # Deploy to github releases
        gh_release_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-bin-${VERSION}.zip"
        gh_release_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-src-${VERSION}.zip"

        # Deploy to sourceforge files
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

        regression-tester_uploadBaseline

    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        log_info "This is a snapshot build"
        ./mvnw deploy -Possrh,sign $MVN_BUILD_FLAGS
        
        # Deploy to sourceforge files
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

        regression-tester_uploadBaseline

    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        log_info "This is some other build, probably during release: commit with a non-snapshot version on branch master..."
        ./mvnw verify $MVN_BUILD_FLAGS
        # we stop here - no need to execute further steps
        exit 0
    fi

else
    log_info "This is neither a pull request nor a push. Not executing any build."
    exit 1
fi
