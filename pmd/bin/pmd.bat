@echo off
set TOPDIR=%~dp0/..
set VERSION=4.2.6
set PMDJAR=%TOPDIR%/lib/pmd-%VERSION%.jar
set JARPATH=%TOPDIR%/lib/asm-3.2.jar;%TOPDIR%/lib/jaxen-1.1.1.jar
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.PMD

java %OPTS% -cp "%PMDJAR%;%JARPATH%;%CLASSPATH%" %MAIN_CLASS% %*

