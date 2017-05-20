#!/bin/bash
set -ev

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

if [ travis_isPullRequest() ]; then

    ./mvnw verify -B -V

elif [ travis_isPush() ]; then

    if [[ "$VERSION" != *-SNAPSHOT && "$TRAVIS_TAG" != "" ]]; then
        # release build
        ./mvnw deploy -Possrh,pmd-release -B -V
    elif [[ "$VERSION" == *-SNAPSHOT ]]; then
        # snapshot build
        ./mvnw deploy -Possrh -B -V
    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        ./mvnw verify -Possrh -B -V
        # we stop here - no need to execute further steps
        exit 0
    fi

    # Uploading pmd distribution to sourceforge
    rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip target/pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
    rsync -avh src/site/markdown/overview/changelog.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md

else
    echo "This is neither a pull request nor a push!"
    exit 1
fi
