@echo off
java -cp ..\lib\pmd-3.8.jar;..\lib\jaxen-1.1-beta-10.jar;..\lib\jakarta-oro-2.0.8.jar;%CLASSPATH% net.sourceforge.pmd.PMD %*
