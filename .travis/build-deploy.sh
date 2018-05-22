#!/bin/bash
set -e

source .travis/common-functions.sh
source .travis/colors.sh

function push_docs() {
    if git diff --quiet docs; then
        echo_yellow "[ INFO] No changes in docs..."
    else
        echo_yellow "[ INFO] Found changes in docs..."

        if [ "$TRAVIS_BRANCH" == "master" ]; then
            git config user.name "Travis CI (pmd-bot)"
            git config user.email "andreas.dangel+pmd-bot@adangel.org"
            git add -A docs
            git commit -m "Update documentation"
            git push git@github.com:pmd/pmd.git HEAD:master
            echo_green  "[ INFO] Successfully pushed docs update"
        else
            echo_yellow "[ INFO] Not on master branch, won't commit+push"
        fi
    fi
}


VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo_yellow "[ INFO] Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

MVN_BUILD_FLAGS="-B -V"

if travis_isPullRequest; then

    echo_yellow "[ INFO] This is a pull-request build"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo -e "\n\n"
        echo_green "[ INFO] This is a release build for tag ${TRAVIS_TAG}"
        echo -e "\n\n"
        ./mvnw deploy -Possrh,pmd-release $MVN_BUILD_FLAGS
    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        echo_yellow "[ INFO] This is a snapshot build"
        ./mvnw deploy -Possrh $MVN_BUILD_FLAGS
        push_docs
    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        echo_yellow "[ INFO] This is some other build, probably during release: commit with a non-snapshot version on branch master..."
        ./mvnw verify $MVN_BUILD_FLAGS
        # we stop here - no need to execute further steps
        exit 0
    fi

    (
        # disable fast fail, exit immediately, in this subshell
        set +e

        echo -e "\n\n"
        echo_yellow "[ INFO] Uploading pmd distribution to sourceforge..."
        rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
        if [ $? -ne 0 ]; then
            echo_red "[ERROR] Error while uploading pmd-*-${VERSION}.zip to sourceforge!"
            echo_red "[ERROR] Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        fi
        rsync -avh docs/pages/release_notes.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md
        if [ $? -ne 0 ]; then
            echo_red "[ERROR] Error while uploading release_notes.md as ReadMe.md to sourceforge!"
            echo_red "[ERROR] Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        fi
        true
    )

else
    echo_yellow "[ INFO] This is neither a pull request nor a push. Not executing any build."
    exit 1
fi
