@echo off
set FILE=%1%
set FORMAT=%2%
set RULESETFILES=%3%
java -cp ..\lib\pmd-1.8.jar;..\lib\jaxen-core-1.0-fcs.jar;..\lib\saxpath-1.0-fcs.jar net.sourceforge.pmd.PMD %FILE% %FORMAT% %RULESETFILES%
