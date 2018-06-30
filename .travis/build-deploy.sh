#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

function push_docs() {
    if git diff --quiet docs; then
        log_info "No changes in docs..."
    else
        log_info "Found changes in docs..."

        if [ "$TRAVIS_BRANCH" == "master" ]; then
            git config user.name "Travis CI (pmd-bot)"
            git config user.email "andreas.dangel+pmd-bot@adangel.org"
            git add -A docs
            git commit -m "Update documentation"
            git push git@github.com:pmd/pmd.git HEAD:master
            log_success "Successfully pushed docs update"
        else
            log_info "Not on master branch, won't commit+push"
        fi
    fi
}


VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
log_info "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

MVN_BUILD_FLAGS="-B -V"

if travis_isPullRequest; then

    log_info "This is a pull-request build"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo -e "\n\n"
        log_info "This is a release build for tag ${TRAVIS_TAG}"
        echo -e "\n\n"
        ./mvnw deploy -Possrh,pmd-release $MVN_BUILD_FLAGS
    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        log_info "This is a snapshot build"
        ./mvnw deploy -Possrh $MVN_BUILD_FLAGS
        push_docs
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

        echo -e "\n\n"
        log_info "Uploading pmd distribution to sourceforge..."
        rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
        if [ $? -ne 0 ]; then
            log_error "Error while uploading pmd-*-${VERSION}.zip to sourceforge!"
            log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        else
            log_success "Successfully uploaded pmd-*-${VERSION}.zip to sourceforge"
        fi
        rsync -avh docs/pages/release_notes.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md
        if [ $? -ne 0 ]; then
            log_error "Error while uploading release_notes.md as ReadMe.md to sourceforge!"
            log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        else
            log_success "Successfully uploaded release_notes.md as ReadMe.md to sourceforge"
        fi
        true
    )

else
    log_info "This is neither a pull request nor a push. Not executing any build."
    exit 1
fi
