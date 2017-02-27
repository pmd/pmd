#
# Helper functions to run a chatty, long task in the background,
# redirecting the output to file and keep travis happy by regularly
# writing to the log.
#
# This is to workaround the travis log length limit of 4MB
# Solution from http://stackoverflow.com/questions/26082444/how-to-work-around-travis-cis-4mb-output-limit/26082445#26082445
#
# Source this file into the shell script, that needs it.
#
# expected variables
# Name          |   Example Value
# PING_SLEEP    |   30s
# BUILD_OUTPUT  |   /tmp/build-step-logfile.out
# PING_PID_FILE |   /tmp/build-step-ping.pid

touch $BUILD_OUTPUT

dump_output() {
   echo Tailing the last 100 lines of output:
   tail -100 $BUILD_OUTPUT
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

