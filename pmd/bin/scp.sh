#!/bin/bash

PATH=/usr/local/java/bin/:$PATH
ANT_OPTS=-Xmx512m
PATH=$MAVEN_HOME/bin:$PATH
CVSROOT=:ext:tomcopeland@cvs.sourceforge.net:/cvsroot/pmd
CLASSPATH=../build/:../lib/jakarta-oro-2.0.8.jar:../lib/jaxen-1.1-beta-7.jar:/usr/local/junit/junit.jar:/usr/local/ant/lib/ant.jar:/usr/local/ant/lib/ant-nodeps.jar
#CLASSPATH=../build/:../lib/jaxen-1.1-beta-7.jar:/usr/local/junit/junit.jar:/usr/local/ant/lib/ant.jar:../lib/asm.jar:../lib/asm-util.jar:../lib/asm-tree.jar:../lib/kasm.jar
export CLASSPATH PATH CVSROOT ANT_OPTS
