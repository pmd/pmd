@echo off
set TOPDIR="%~dp0.."
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.PMD
set PMD_CLASSPATH=%TOPDIR%\lib\*

if [%CLASSPATH%] NEQ [] (
    set PMD_CLASSPATH=%CLASSPATH%;%PMD_CLASSPATH%
)

java %PMD_JAVA_OPTS% -classpath %PMD_CLASSPATH% %OPTS% %MAIN_CLASS% %*
