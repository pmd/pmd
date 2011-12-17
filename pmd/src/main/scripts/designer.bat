@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.designer.Designer

java %OPTS% -Djava.ext.dirs="%TOPDIR%\lib" %MAIN_CLASS% %*

