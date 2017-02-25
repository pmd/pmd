#!/bin/bash
set -ev

# Do not log the output, to avoid the travis log length limit of 4MB
# Solution from http://stackoverflow.com/questions/26082444/how-to-work-around-travis-cis-4mb-output-limit/26082445#26082445



export PING_SLEEP=30s
export BUILD_OUTPUT=/tmp/build-site.out
export PING_PID_FILE=/tmp/build-site-ping.pid

touch $BUILD_OUTPUT

dump_output() {
   echo Tailing the last 500 lines of output:
   tail -500 $BUILD_OUTPUT  
}
kill_ping() {
  if [ -e $PING_PID_FILE ]; then
    PING_LOOP_PID=$(cat $PING_PID_FILE)
    kill $PING_LOOP_PID
    rm $PING_PID_FILE
  fi
}
error_handler() {
  kill_ping
  echo ERROR: An error was encountered with the build.
  dump_output
  exit 1
}
# If an error occurs, run our error handler to output a tail of the build
trap 'error_handler' ERR

# Set up a repeating loop to send some output to Travis.
bash -c "while true; do echo \$(date) - building ...; sleep $PING_SLEEP; done" &
PING_LOOP_PID=$!
echo "$PING_LOOP_PID" > $PING_PID_FILE

# Run the build, redirect output into the file
mvn site site:stage -Psite -B -V >> $BUILD_OUTPUT 2>&1

# The build finished without returning an error so dump a tail of the output
dump_output

# nicely terminate the ping output loop
kill_ping

