@echo off
set FILE=%1%
set FORMAT=%2%
set RULESETFILES=%3%
java -jar ../lib/pmd-1.03.jar %FILE% %FORMAT% %RULESETFILES%
