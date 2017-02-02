@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.viewer.Viewer

java -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
