@echo off
set FILE=%1%
set FORMAT=%2%
set RULESETFILE=%3%
java -jar ../lib/pmd-0.9.jar %FILE% %FORMAT% %RULESETFILE%
