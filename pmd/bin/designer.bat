@echo off
set TOPDIR=%~dp0/..
set VERSION=4.2.4
set PMDJAR=%TOPDIR%/lib/pmd-%VERSION%.jar
set JARPATH=%TOPDIR%/lib/asm-3.1.jar;%TOPDIR%/lib/jaxen-1.1.1.jar
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.designer.Designer

java %OPTS% -cp "%PMDJAR%;%JARPATH%;%TOPDIR%/build" %MAIN_CLASS% %*

