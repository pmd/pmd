#!/bin/bash

ANT_OPTS=-Xmx512m
PATH=$MAVEN_HOME/bin:$PATH
CVSROOT=:ext:tomcopeland@cvs.sourceforge.net:/cvsroot/pmd
CLASSPATH=../build/:../lib/jaxen-core-1.0-fcs.jar:../lib/saxpath-1.0-fcs.jar:/usr/local/junit/junit.jar:/usr/local/ant/lib/ant.jar
#CLASSPATH=../build/:../lib/jaxen-core-1.0-fcs.jar:../lib/saxpath-1.0-fcs.jar:/usr/local/junit/junit.jar:/usr/local/ant/lib/ant.jar:../lib/asm.jar:../lib/asm-util.jar:../lib/asm-tree.jar:../lib/kasm.jar
export CLASSPATH PATH CVSROOT ANT_OPTS
