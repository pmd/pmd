#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Coveralls.io report ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not proceeding, since this is not a push!"
    exit 0
fi

#
# for java9: enable all modules.
# coveralls plugin seems to need java.xml.bind module
#
echo "MAVEN_OPTS='-Xms1g -Xmx1g --add-modules java.se.ee'" > ${HOME}/.mavenrc

./mvnw clean test jacoco:report coveralls:report -Pcoveralls -B -V
