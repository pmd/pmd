@echo off
set MAIN=com.infoether.pmd.PMD
set TEST_FILE=c:\\data\\pmd\\pmd\\test-data\\%1%.java

java %MAIN% %TEST_FILE%
