#!/bin/bash

MAVEN_HOME=/usr/local/maven
PATH=$MAVEN_HOME/bin:$PATH
CLASSPATH=../:../build/:../lib/jaxen-core-1.0-fcs.jar:../lib/pmd-1.1.jar:../lib/saxpath-1.0-fcs.jar:/usr/local/ant/lib/junit.jar:/usr/local/ant/lib/ant.jar
export CLASSPATH MAVEN_HOME PATH
