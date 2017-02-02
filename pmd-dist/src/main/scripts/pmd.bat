@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.PMD

java -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
