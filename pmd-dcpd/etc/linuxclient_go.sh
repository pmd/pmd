#!/bin/bash

CLASSPATH=/home/tom/dcpdworker/outrigger-dl.jar
CLASSPATH=$CLASSPATH:/home/tom/dcpdworker/reggie.jar
CLASSPATH=$CLASSPATH:/home/tom/dcpdworker/pmd-1.0rc2.jar
CLASSPATH=$CLASSPATH:/home/tom/dcpdworker/dcpd.jar

MEMORY_ARG="-Xms128M -Xmx384M"
POLICY_ARG=-Djava.security.policy=/home/tom/dcpdworker/policy.all
SPACENAME_ARG=-Dcom.sun.jini.outrigger.spacename=JavaSpaces
GROUP_ARG=-Dcom.sun.jini.lookup.groups=public
CODEBASE=-Djava.rmi.server.codebase=http://mordor:8081/dcpd.jar

java $MEMORY_ARG $POLICY_ARG $SPACENAME_ARG $GROUP_ARG -cp $CLASSPATH $CODEBASE net.sourceforge.pmd.dcpd.DCPDWorker


