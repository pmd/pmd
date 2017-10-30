#!/bin/bash
set -e

source .travis/common-functions.sh

function push_docs() {
    if git diff --quiet docs; then
        echo "No changes in docs..."
    else
        echo "Found changes in docs..."

        if [ "$TRAVIS_BRANCH" == "master" ]; then
            git config user.name "Travis CI (pmd-bot)"
            git config user.email "andreas.dangel+pmd-bot@adangel.org"
            git add -A docs
            git commit -m "Update documentation"
            git push git@github.com:pmd/pmd.git HEAD:master
        else
            echo "Not on master branch, won't commit+push"
        fi
    fi
}


VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

# TODO : Once we release PMD 6.0.0 and have a compatible PMD plugin, enable PMD once again
MVN_BUILD_FLAGS="-B -V -Dpmd.skip=true"

if travis_isPullRequest; then

    echo "This is a pull-request build"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo "This is a release build for tag ${TRAVIS_TAG}"
        ./mvnw deploy -Possrh,pmd-release $MVN_BUILD_FLAGS
    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        echo "This is a snapshot build"
        ./mvnw deploy -Possrh $MVN_BUILD_FLAGS
        push_docs
    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        echo "This is some other build, probably during release: commit with a non-snapshot version on branch master..."
        ./mvnw verify -Possrh $MVN_BUILD_FLAGS
        # we stop here - no need to execute further steps
        exit 0
    fi

    # Uploading pmd distribution to sourceforge
    rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
    rsync -avh docs/pages/release_notes.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md

else
    echo "This is neither a pull request nor a push!"
    exit 1
fi
