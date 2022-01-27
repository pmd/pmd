@echo off
set TOPDIR="%~dp0.."
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.designer.Designer

java %PMD_JAVA_OPTS% -classpath %TOPDIR%\lib\* %OPTS% %MAIN_CLASS% %*
