#!/bin/bash
set -ev

export PING_SLEEP=30s
export BUILD_OUTPUT=/tmp/build-sonar.out
export PING_PID_FILE=/tmp/build-sonar-ping.pid

source .travis/background-job-funcs.sh

# Run the build, redirect output into the file
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONAR_TOKEN} -B -V >> $BUILD_OUTPUT 2>&1

# The build finished without returning an error so dump a tail of the output
dump_output

# nicely terminate the ping output loop
kill_ping

