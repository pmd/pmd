#!/usr/bin/env bash

source $(dirname $0)/logger.inc
source ${HOME}/java.env

set -e

# configure maven
# probably not needed? echo "MAVEN_OPTS='-Xms1g -Xmx1g'" > ${HOME}/.mavenrc
mkdir -p ${HOME}/.m2
cp .ci/maven-settings.xml ${HOME}/.m2/settings.xml


#MVN_BUILD_FLAGS="-B -V  -Djava7.home=${HOME}/oraclejdk7"
MVN_BUILD_FLAGS="-B -V"

log_info "This is a snapshot build"
./mvnw deploy -Possrh,sign $MVN_BUILD_FLAGS

# Deploy to sourceforge files
#sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
#sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

#regression-tester_uploadBaseline

#build and upload doc
