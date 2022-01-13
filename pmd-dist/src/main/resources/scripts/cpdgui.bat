@echo off
set TOPDIR="%~dp0.."
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.cpd.GUI

java %PMD_JAVA_OPTS% -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
