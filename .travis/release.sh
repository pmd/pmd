#!/bin/bash
set -ev

RELEASE_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)

# Deploy to ossrh has already been done with the usual build. See build-push.sh

# The site has been built before, the files have already been uploaded to sourceforge.
# Since this is a release, making the binary the new default file...
curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
     -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/pmd-bin-${RELEASE_VERSION}.zip

# TODO: patch the release on github? Upload the changelog? https://developer.github.com/v3/repos/releases/#create-a-release


echo "Adding the site to pmd.github.io..."
# clone pmd.github.io. Note: This uses the ssh key setup earlier
git clone --depth 1 git@github.com:pmd/pmd.github.io.git
rsync -a target/pmd-doc-${RELEASE_VERSION}/ pmd.github.io/pmd-${RELEASE_VERSION}/
(
  cd pmd.github.io
  git add pmd-${RELEASE_VERSION}
  git commit -m "Added pmd-${RELEASE_VERSION}"
  git rm -qr latest
  cp -a pmd-${RELEASE_VERSION} latest
  git add latest
  git commit -m "Copying pmd-${RELEASE_VERSION} to latest"
  git push origin
)

