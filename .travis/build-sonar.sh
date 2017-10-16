#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Sonar ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not updating sonar, since this is not a push!"
    exit 0
fi

#
# for java9: enable all modules.
# sonar plugin seems to need java.xml.bind module
#
echo "MAVEN_OPTS='-Xms1g -Xmx1g --add-modules java.se.ee'" > ${HOME}/.mavenrc

# Run the build, truncate output due to Travis log limits
./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONAR_TOKEN} -B -V
