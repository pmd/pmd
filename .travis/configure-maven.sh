#!/bin/bash
set -e

echo "MAVEN_OPTS='-Xms1g -Xmx1g'" > $HOME/.mavenrc
mkdir -p $HOME/.m2
cp .travis/travis-toolchains.xml $HOME/.m2/toolchains.xml
cp .travis/travis-settings.xml $HOME/.m2/settings.xml
