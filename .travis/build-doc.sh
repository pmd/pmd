#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not building site, since this is not a push!"
    exit 0
fi

pushd docs

# run jekyll
echo -e "\n\nBuilding documentation using jekyll...\n\n"
bundle install
bundle exec jekyll build

# create pmd-doc archive
echo -e "\n\nCreating pmd-doc archive...\n\n"
mv _site pmd-doc-${VERSION}
zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/


# Uploading pmd doc distribution to sourceforge
if [[ "${TRAVIS_TAG}" != "" || "${VERSION}" == *-SNAPSHOT ]]; then
    echo -e "\n\nUploading pmd-doc archive to sourceforge...\n\n"
    rsync -avh pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
fi

# rsync site to pmd.sourceforge.net/snapshot
if [[ "${VERSION}" == *-SNAPSHOT && "${TRAVIS_BRANCH}" == "master" ]]; then
    echo -e "\n\nUploading snapshot site...\n\n"
    travis_wait rsync -ah --stats --delete pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
fi

popd
