#!/bin/bash
set -ev

VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
echo "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

if [[ "$VERSION" != *-SNAPSHOT && "$TRAVIS_TAG" != "" ]]; then
    mvn deploy -Possrh,pmd-release -B -V
else
    mvn deploy -Possrh -B -V
fi

bash .travis/build-site.sh

# create pmd-doc archive
(
    cd target
    mv staging pmd-doc-${VERSION}
    zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/
)

# Uploading pmd distribution to sourceforge
if [[ "$TRAVIS_TAG" != "" || "$VERSION" == *-SNAPSHOT ]]; then
    rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip target/pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
    rsync -avh src/site/markdown/overview/changelog.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md
fi

if [[ "$VERSION" == *-SNAPSHOT && "$TRAVIS_BRANCH" == "master" ]]; then
    # Uploading snapshot site...
    rsync -ah --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
fi


if [[ "$VERSION" == *-SNAPSHOT && "$TRAVIS_BRANCH" == "master" ]]; then
    # only do a clean build for sonar, if we are executing a snapshot build, otherwise we can't reuse the build from above for the release
    mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONAR_TOKEN} -B -V
fi

