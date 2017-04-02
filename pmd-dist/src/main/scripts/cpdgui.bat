@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.cpd.GUI

java -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
