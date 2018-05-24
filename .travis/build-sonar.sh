#!/bin/bash
set -e

source .travis/common-functions.sh
source .travis/colors.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
log_info "Building PMD Sonar ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not updating sonar, since this is not a push!"
    exit 0
fi

#
# for java9: enable all modules.
# sonar plugin seems to need java.xml.bind module
echo "MAVEN_OPTS='-Xms1g -Xmx1g --add-modules java.se.ee'" > ${HOME}/.mavenrc

(
    # disable fast fail, exit immediately, in this subshell
    set +e

    # Run the build
    ./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONAR_TOKEN} -B -V

    if [ $? -ne 0 ]; then
        log_error "Error updating sonar..."
    else
        log_success "New sonar results: https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd"
    fi
    true
)
