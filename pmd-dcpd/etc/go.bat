@echo off
set CLASSPATH=
set CLASSPATH=%CLASSPATH%;c:\data\pmd\pmd-dcpd\lib\runtimeonly\outrigger-dl.jar
set CLASSPATH=%CLASSPATH%;c:\data\pmd\pmd-dcpd\lib\runtimeonly\reggie.jar
set CLASSPATH=%CLASSPATH%;c:\data\pmd\pmd-dcpd\build
set CLASSPATH=%CLASSPATH%;c:\data\pmd\pmd-dcpd\lib\pmd-0.9.jar

set MAIN=net.sourceforge.pmd.dcpd.%1
set MEMORY_ARG=-Xms128M -Xmx384M
set POLICY_ARG=-Djava.security.policy=c:\jini-1_2_1\policy\policy.all
set SPACENAME_ARG=-Dcom.sun.jini.outrigger.spacename=JavaSpaces
set GROUP_ARG=-Dcom.sun.jini.lookup.groups=public
set CODEBASE=-Djava.rmi.server.codebase=http://mordor:8081/dcpd.jar

java %MEMORY_ARG% %POLICY_ARG% %SPACENAME_ARG% %GROUP_ARG% -cp %CLASSPATH% %CODEBASE% %MAIN%
