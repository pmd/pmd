@echo off
set CLASSPATH=../lib/pmd-0.3.jar
set FILE=%1%
set FORMAT=%2%
set RULESETFILE=%3%
java net.sourceforge.pmd.PMD %FILE% %FORMAT% %RULESETFILE%
