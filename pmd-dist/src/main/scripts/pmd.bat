@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.PMD

java %OPTS% -Djava.ext.dirs="%TOPDIR%\lib" %MAIN_CLASS% %*

