#!/bin/bash
set -e

source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo "Not building site, since this is not a push!"
    exit 0
fi

cd docs

# run jekyll
echo -e "\n\nBuilding documentation using jekyll...\n\n"
export JEKYLL_VERSION=3.5
docker run --rm \
  --volume=$PWD:/srv/jekyll \
  -it jekyll/jekyll:$JEKYLL_VERSION \
  jekyll build

# create pmd-doc archive
echo -e "\n\nCreating pmd-doc archive...\n\n"

echo "ls -la:"
ls -la
echo "umask:"
umask
echo "id:"
id
echo


mv _site pmd-doc-${VERSION}
zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/


# Uploading pmd doc distribution to sourceforge
if [[ "$TRAVIS_TAG" != "" || "$VERSION" == *-SNAPSHOT ]]; then
    echo -e "\n\nUploading pmd-doc archive to sourceforge...\n\n"
    rsync -avh target/pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
fi

# rsync site to pmd.sourceforge.net/snapshot
if [[ "$VERSION" == *-SNAPSHOT && "$TRAVIS_BRANCH" == "master" ]]; then
    echo -e "\n\nUploading snapshot site...\n\n"
    travis_wait rsync -ah --stats --delete target/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
fi
