@echo off
set CLASSPATH=../build/
set FILE=%1%
set FORMAT=%2%
java net.sourceforge.pmd.PMD %FILE% %FORMAT%
