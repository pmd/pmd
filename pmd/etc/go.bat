@echo off
set MAIN=net.sourceforge.pmd.PMD
set TEST_FILE=c:\\data\\pmd\\pmd\\test-data\\%1%.java

java %MAIN% %TEST_FILE% xml rulesets\naming.xml
java %MAIN% %TEST_FILE% xml rulesets\basic.xml
java %MAIN% %TEST_FILE% xml rulesets\design.xml
java %MAIN% %TEST_FILE% xml rulesets\imports.xml
java %MAIN% %TEST_FILE% xml rulesets\unusedcode.xml
