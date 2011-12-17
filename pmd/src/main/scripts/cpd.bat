@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.cpd.CPD

java %OPTS% -Djava.ext.dirs="%TOPDIR%\lib" %MAIN_CLASS% %*

