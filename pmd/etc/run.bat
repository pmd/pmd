@echo off
set CLASSPATH=../lib/pmd-0.1.jar
set FILE=%1%
set FORMAT=%2%
java net.sourceforge.pmd.PMD %FILE% %FORMAT%
