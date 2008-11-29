@echo off
set TOPDIR=%~dp0/..
set VERSION=5.0
set PMDJAR=%TOPDIR%/lib/pmd-%VERSION%.jar
set JARPATH=%TOPDIR%/lib/asm-3.1.jar;%TOPDIR%/lib/jaxen-1.1.1.jar;%TOPDIR%/lib/saxon9.jar;%TOPDIR%/lib/js-cvs-11282008.jar
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.viewer.Viewer

java %OPTS% -cp "%PMDJAR%;%JARPATH%;%TOPDIR%/build" %MAIN_CLASS% %*

