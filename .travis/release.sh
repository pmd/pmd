#!/bin/bash
set -ev

RELEASE_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)

# Deploy to ossrh has already been done with the usual build. See build-push.sh

# The site has been built before, the files have already been uploaded to sourceforge.
# Since this is a release, making the binary the new default file...
curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
     -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/pmd-bin-${RELEASE_VERSION}.zip

# TODO: patch the release on github? Upload the changelog? https://developer.github.com/v3/repos/releases/#create-a-release
# TODO: deploy site from here? We could clone the site repo, and push using a secure token instead of a password...
# TODO: publish site on pmd.github.io
# TODO: Submit news on SF?


