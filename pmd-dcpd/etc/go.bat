@echo off 
set CLASSPATH=
set CLASSPATH=%CLASSPATH%;c:\jini-1_2_1\lib\outrigger.jar
set CLASSPATH=%CLASSPATH%;c:\jini-1_2_1\lib\reggie.jar
set CLASSPATH=%CLASSPATH%;c:\data\jini
set CLASSPATH=%CLASSPATH%;c:\data\jini\pmd-0.9.jar

java -Xms128M -Xmx384M -Djava.security.policy=c:\jini-1_2_1\policy\policy.all -Dcom.sun.jini.outrigger.spacename=JavaSpaces -Dcom.sun.jini.lookup.groups=public -cp %CLASSPATH% -Djava.rmi.server.codebase=http://mordor:8081/space-examples-dl.jar Test


