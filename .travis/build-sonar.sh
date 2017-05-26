#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Sonar ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not updating sonar, since this is not a push!"
    exit 0
fi


export PING_SLEEP=30s
export BUILD_OUTPUT=/tmp/build-sonar.out
export PING_PID_FILE=/tmp/build-sonar-ping.pid

source .travis/background-job-funcs.sh

# Run the build, redirect output into the file
./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONAR_TOKEN} -B -V >> $BUILD_OUTPUT 2>&1

# The build finished without returning an error so dump a tail of the output
dump_output

# nicely terminate the ping output loop
kill_ping

