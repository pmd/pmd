#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
log_info "Building PMD Coveralls.io report ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not proceeding, since this is not a push!"
    exit 0
fi

#
# for java9: enable all modules.
# coveralls plugin seems to need java.xml.bind module
echo "MAVEN_OPTS='-Xms1g -Xmx1g --add-modules java.se'" > ${HOME}/.mavenrc

(
    # disable fast fail, exit immediately, in this subshell
    set +e

    ./mvnw clean install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
    ./mvnw test jacoco:report coveralls:report -Pcoveralls -B -V

    if [ $? -ne 0 ]; then
        log_error "Error creating coveralls report"
    else
        log_success "New coveralls result: https://coveralls.io/github/pmd/pmd"
    fi
    true
)
