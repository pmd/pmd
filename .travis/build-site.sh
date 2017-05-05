#!/bin/bash
set -ev

export PING_SLEEP=30s
export BUILD_OUTPUT=/tmp/build-site.out
export PING_PID_FILE=/tmp/build-site-ping.pid

source .travis/background-job-funcs.sh

# Run the build, redirect output into the file
./mvnw site site:stage -Psite -B -V >> $BUILD_OUTPUT 2>&1

# The build finished without returning an error so dump a tail of the output
dump_output

# nicely terminate the ping output loop
kill_ping

