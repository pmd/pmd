#!/bin/bash

JAVA_HOME=/usr/local/jdk1.3.1_11 && export JAVA_HOME
CLASSPATH=$CLASSPATH:/usr/local/junit3.8.1/junit.jar && export CLASSPATH
ant clean
JAVA_HOME=/usr/local/java && export JAVA_HOME
