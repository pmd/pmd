#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Site ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not building site, since this is not a push!"
    exit 0
fi


(
    # Run the build, redirect output into the file
    travis_wait ./mvnw install -DskipTests=true -B -V
    travis_wait ./mvnw site site:stage -Psite -B -V
)

# create pmd-doc archive
(
    cd target
    mv staging pmd-doc-${VERSION}
    zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/
)

# Uploading pmd doc distribution to sourceforge
if [[ "$TRAVIS_TAG" != "" || "$VERSION" == *-SNAPSHOT ]]; then
    rsync -avh target/pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
fi

(
    if [[ "$VERSION" == *-SNAPSHOT && "$TRAVIS_BRANCH" == "master" ]]; then
        # Uploading snapshot site...
        travis_wait rsync -ah --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
    fi
)
