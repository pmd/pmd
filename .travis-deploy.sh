#!/bin/bash

RELEASE_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

# closing on the staging area is still manual, add nexus-staging plugin and perform mvn nexus-staging:release?
mvn -B -Possrh sign release:perform -DreleaseProfiles=ossrh

mvn site site:stage -Psite

(
    cd target
    mv staging pmd-doc-${RELEASE_VERSION}
    zip -r pmd-doc-${RELEASE_VERSION}.zip pmd-doc-${RELEASE_VERSION}/
)

echo
echo "Uploading the zip files to SourceForge..."
RSYNC_PASSWORD=${PMD_SF_PASSWORD} rsync -avhP pmd-dist/target/pmd-*-${RELEASE_VERSION}.zip target/pmd-doc-${RELEASE_VERSION}.zip $PMD_SF_USER@web.sourceforge.net:/home/frs/project/pmd/pmd/${RELEASE_VERSION}/
RSYNC_PASSWORD=${PMD_SF_PASSWORD} rsync -avhP src/site/markdown/overview/changelog.md $PMD_SF_USER@web.sourceforge.net:/home/frs/project/pmd/pmd/${RELEASE_VERSION}/ReadMe.md
echo

# TODO : deploy site from here? We could clone the site repo, and push using a secure token instead of a password...
