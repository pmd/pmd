@echo off
set CLASSPATH=
set CLASSPATH=/home/tom/dcpdworker/outrigger-dl.jar
set CLASSPATH=$CLASSPATH:/home/tom/dcpdworker/reggie.jar
set CLASSPATH=$CLASSPATH:/home/tom/dcpdworker/pmd-0.9.jar

set MAIN=net.sourceforge.pmd.dcpd.%1
set MEMORY_ARG=-Xms128M -Xmx384M
set POLICY_ARG=-Djava.security.policy=/home/tom/dcpdworker/policy.all
set SPACENAME_ARG=-Dcom.sun.jini.outrigger.spacename=JavaSpaces
set GROUP_ARG=-Dcom.sun.jini.lookup.groups=public
set CODEBASE=-Djava.rmi.server.codebase=http://mordor:8081/dcpd.jar

java $MEMORY_ARG $POLICY_ARG $SPACENAME_ARG $GROUP_ARG -cp $CLASSPATH $CODEBASE $MAIN $2
