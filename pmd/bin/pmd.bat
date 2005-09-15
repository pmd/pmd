@echo off
set FILE=%1%
set FORMAT=%2%
set RULESETFILES=%3%
java -cp ..\lib\pmd-3.3.jar;..\lib\jaxen-1.1-beta-7.jar net.sourceforge.pmd.PMD %FILE% %FORMAT% %RULESETFILES% %4 %5 %6 %7 %8
