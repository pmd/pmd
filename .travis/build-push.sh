#!/bin/bash
set -ev

VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.2:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

if [[ "$VERSION" == *-SNAPSHOT ]]; then
    mvn deploy -Possrh -B -V
else
    mvn deploy -Possrh,pmd-release -B -V
fi

mvn site site:stage -Psite

# Uploading pmd distribution to sourceforge
(
    cd target
    mv staging pmd-doc-${VERSION}
    zip -r pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/
)

rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip target/pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
rsync -avh src/site/markdown/overview/changelog.md ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md

if [[ "$VERSION" == *-SNAPSHOT ]]; then
    # Uploading snapshot site...
    rsync -avh --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
fi


if [[ "$VERSION" == *-SNAPSHOT ]]; then
    # only do a clean build for sonar, if we are executing a snapshot build, otherwise we can't reuse the build from above for the release
    mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONAR_TOKEN} -B -V
fi

