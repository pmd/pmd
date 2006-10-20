@echo off
java -cp %~dp0\..\lib\pmd-3.8.jar;%~dp0\..\lib\jaxen-1.1-beta-10.jar;%~dp0\..\lib\jakarta-oro-2.0.8.jar;%CLASSPATH% net.sourceforge.pmd.PMD %*
