#!/bin/bash

JINI_SDK=/home/tom/projects/jini/sdk/

# need a webserver, too
echo "Starting HTTP server on port 8081"
java -jar ${JINI_SDK}lib/tools.jar -dir ${JINI_SDK}lib -port 8081
