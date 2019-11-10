#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh
source .travis/github-releases-api.sh
source .travis/sourceforge-api.sh

function upload_baseline() {
    log_info "Generating and uploading baseline for pmdtester..."
    cd ..
    bundle config --local gemfile pmd/Gemfile
    pmd/.travis/travis_wait "bundle exec pmdtester -m single -r ./pmd -p ${TRAVIS_BRANCH} -pc ./pmd/.travis/all-java.xml -l ./pmd/.travis/project-list.xml -f"
    cd target/reports
    BRANCH_FILENAME="${TRAVIS_BRANCH/\//_}"
    zip -q -r ${BRANCH_FILENAME}-baseline.zip ${BRANCH_FILENAME}/
    ../../pmd/.travis/travis_wait "rsync -avh ${BRANCH_FILENAME}-baseline.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd-regression-tester/"
    if [ $? -ne 0 ]; then
        log_error "Error while uploading ${BRANCH_FILENAME}-baseline.zip to sourceforge!"
        log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd-regression-tester/"
        exit 1
    else
        log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to sourceforge"
    fi
}

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
	(
            set +e
            # Create a corresponding remote branch locally
            if ! git show-ref --verify --quiet refs/heads/${TRAVIS_BRANCH}; then
                git fetch --no-tags origin +refs/heads/${TRAVIS_BRANCH}:refs/remotes/origin/${TRAVIS_BRANCH}
                git branch ${TRAVIS_BRANCH} origin/${TRAVIS_BRANCH}
            fi
            log_info "Running danger"
            bundle exec danger --verbose
	)

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo -e "\n\n"
        log_info "This is a release build for tag ${TRAVIS_TAG}"
        echo -e "\n\n"

        # create a draft github release
        gh_releases_createDraftRelease "${TRAVIS_TAG}" "$(git show-ref --hash HEAD)"
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
        sourceforge_selectDefault "${VERSION}"

    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        log_info "This is a snapshot build"
        ./mvnw deploy -Possrh,sign $MVN_BUILD_FLAGS
        
        # Deploy to sourceforge files
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
        sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        log_info "This is some other build, probably during release: commit with a non-snapshot version on branch master..."
        ./mvnw verify $MVN_BUILD_FLAGS
        # we stop here - no need to execute further steps
        exit 0
    fi

    (
        # disable fast fail, exit immediately, in this subshell
        set +e

        upload_baseline
    )

else
    log_info "This is neither a pull request nor a push. Not executing any build."
    exit 1
fi
