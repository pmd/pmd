@echo off
rem set FILE=%1%
rem set FORMAT=%2%
rem set RULESETFILES=%3%
java -cp ..\lib\pmd-3.3.jar;..\lib\jaxen-1.1-beta-7.jar net.sourceforge.pmd.PMD %*
