#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
log_info "Building PMD Sonar ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not updating sonar, since this is not a push!"
    exit 0
fi

(
    # for sonar, we need to use java10, until sonarjava 5.8.0 is released (Sept. 2018)
    JAVA_HOME=$(bash ./install-jdk.sh -F 10 -L GPL -W $HOME/jdk --emit-java-home  | tail --lines 1)
    export JAVA_HOME
    export PATH=${JAVA_HOME}/bin:$PATH

    # disable fast fail, exit immediately, in this subshell
    set +e

    # Run the build
    ./mvnw clean package sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Psonar -B -V

    if [ $? -ne 0 ]; then
        log_error "Error updating sonar..."
    else
        log_success "New sonar results: https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd"
    fi
    true
)
