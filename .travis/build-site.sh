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
    # Run the build, truncate output due to Travis log limits

    echo -e "\n\nExecuting ./mvnw install..."
    travis_wait ./mvnw install -DskipTests=true -B -V -q
    echo "Finished executing ./mvnw install"

    echo -e "\n\nExecuting ./mvnw site site:stage...
    travis_wait 40 ./mvnw site site:stage -DskipTests=true -Psite -B -V -q
    echo "Finished executing ./mvnw site site:stage..."
)

echo -e "\n\nCreating pmd-doc archive...\n\n"
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
        echo -e "\n\nUploading snapshot site...\n\n"
        travis_wait rsync -ah --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
    fi
)
