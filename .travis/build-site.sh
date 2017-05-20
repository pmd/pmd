#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Site ${VERSION} on branch ${TRAVIS_BRANCH}"

if [ ! travis_isPush ]; then
    echo "Not building site, since this is not a push!"
    exit 0
fi



export PING_SLEEP=30s
export BUILD_OUTPUT=/tmp/build-site.out
export PING_PID_FILE=/tmp/build-site-ping.pid

source .travis/background-job-funcs.sh

# Run the build, redirect output into the file
./mvnw verify site site:stage -Psite -B -V >> $BUILD_OUTPUT 2>&1

# The build finished without returning an error so dump a tail of the output
dump_output

# nicely terminate the ping output loop
kill_ping


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

if [[ "$VERSION" == *-SNAPSHOT && "$TRAVIS_BRANCH" == "master" ]]; then
    # Uploading snapshot site...
    rsync -ah --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
fi

