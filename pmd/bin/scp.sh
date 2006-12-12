#!/bin/bash

PATH=/usr/local/java/bin/:$PATH
ANT_OPTS=-Xmx512m
PATH=$MAVEN_HOME/bin:$PATH
CVSROOT=:ext:tomcopeland@cvs.sourceforge.net:/cvsroot/pmd
CLASSPATH=../build/:../lib/asm-3.0.jar:../lib/backport-util-concurrent.jar:../lib/jaxen-1.1-beta-10.jar:/usr/local/junit/junit.jar:/usr/local/ant/lib/ant.jar:/usr/local/ant/lib/ant-nodeps.jar
export CLASSPATH PATH CVSROOT ANT_OPTS
