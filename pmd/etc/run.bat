@echo off
set CLASSPATH=../build/
java net.sourceforge.pmd.PMD %1%  %2%
