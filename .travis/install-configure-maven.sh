#!/bin/bash
set -ev

# remember the current directory
SOURCE_HOME=$PWD

cd $HOME
wget -q http://www-us.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar zxf apache-maven-3.3.9-bin.tar.gz
chmod +x apache-maven-3.3.9/bin/mvn

echo "MAVEN_OPTS='-Xms1g -Xmx1g'" > .mavenrc
mkdir -p .m2
cp $SOURCE_HOME/.travis/travis-toolchains.xml .m2/toolchains.xml
cp $SOURCE_HOME/.travis/travis-settings.xml .m2/settings.xml
