@echo off
set TOPDIR="%~dp0.."
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.PMD

java %PMD_JAVA_OPTS% -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
