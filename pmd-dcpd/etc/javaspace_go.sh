#!/bin/bash

# outrigger is the Sun implementation of a JavaSpace
echo "Starting a transient JavaSpace - outrigger"
java -Djava.security.policy=/home/tom/projects/jini/sdk/policy/policy.all -Djava.rmi.server.codebase=http://mordor:8081/outrigger-dl.jar  -Dcom.sun.jini.outrigger.spaceName=JavaSpaces -jar /home/tom/projects/jini/sdk/lib/transient-outrigger.jar public
