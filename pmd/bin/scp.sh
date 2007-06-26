#!/bin/bash

PATH=/usr/local/java/bin/:$PATH
ANT_OPTS="-Xmx512m -Xms64m"
PATH=$MAVEN_HOME/bin:$PATH
CVSROOT=:ext:tomcopeland@cvs.sourceforge.net:/cvsroot/pmd
CLASSPATH=../build/:../lib/asm-3.0.jar:../lib/jaxen-1.1.jar:/usr/local/junit4.1/junit-4.1.jar:/usr/local/ant/lib/ant.jar:/usr/local/ant/lib/ant-nodeps.jar
export CLASSPATH PATH CVSROOT ANT_OPTS
