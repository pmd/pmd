@echo off
set MAIN=net.sourceforge.pmd.PMD
set TEST_FILE=c:\\data\\pmd\\pmd\\test-data\\%1%.java

java %MAIN% %TEST_FILE% xml c:\data\pmd\pmd\rulesets\basic.xml
