@echo off
set FILE=%1%
set FORMAT=%2%
set RULESETFILES=%3%
java -jar ../lib/pmd-1.0rc1.jar %FILE% %FORMAT% %RULESETFILES%
