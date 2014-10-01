@echo off
set TOPDIR=%~dp0..
set OPTS=-Xmx512m
set MAIN_CLASS=net.sourceforge.pmd.cpd.GUI

java %OPTS% -Djava.ext.dirs="%TOPDIR%\lib" %MAIN_CLASS% %*

