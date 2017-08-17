#!/bin/bash
set -e

echo "BUILD: $BUILD"
RELEASE_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "RELEASE_VERSION: $RELEASE_VERSION"

if [ "${BUILD}" = "deploy" ]; then

# Deploy to ossrh has already been done with the usual build. See build-deploy.sh

# The site has been built before, the files have already been uploaded to sourceforge.
# Since this is a release, making the binary the new default file...
curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
     -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/pmd-bin-${RELEASE_VERSION}.zip


# Assumes, the release has already been created by travis github releases provider
RELEASE_ID=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" https://api.github.com/repos/pmd/pmd/releases/tags/pmd_releases/${RELEASE_VERSION}|jq ".id")
RELEASE_NAME="PMD ${RELEASE_VERSION} ($(date -u +%d-%B-%Y))"
RELEASE_BODY=$(tail -n +6 docs/pages/release_notes.md) # skips the first 6 lines - the heading 'PMD Release Notes'
RELEASE_BODY="${RELEASE_BODY//'\'/\\\\}"
RELEASE_BODY="${RELEASE_BODY//$'\r'/}"
RELEASE_BODY="${RELEASE_BODY//$'\n'/\\r\\n}"
RELEASE_BODY="${RELEASE_BODY//'"'/\\\"}"
cat > release-edit-request.json <<EOF
{
  "name": "$RELEASE_NAME",
  "body": "$RELEASE_BODY"
}
EOF
echo "Updating release at https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID}..."


RESPONSE=$(curl -i -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/json" --data "@release-edit-request.json" -X PATCH https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID})
if [[ "$RESPONSE" != *"HTTP/1.1 200"* ]]; then
    echo "Request:"
    cat release-edit-request.json
    echo
    echo "Response:"
    echo "$RESPONSE"
else
    echo "Update OK"
fi

fi


if [ "${BUILD}" = "site" ]; then

echo "Adding the site to pmd.github.io..."
# clone pmd.github.io. Note: This uses the ssh key setup earlier
# In order to speed things up, we use a sparse checkout - no need to checkout all directories here
mkdir pmd.github.io
(
    cd pmd.github.io
    git init
    git config user.email "adangel+pmd-bot@users.sourceforge.net"
    git config user.name "Andreas Dangel (PMD Releases)"
    git config core.sparsecheckout true
    git remote add origin git@github.com:pmd/pmd.github.io.git
    echo "latest/" > .git/info/sparse-checkout
    git pull --depth=1 origin master
    rsync -a ../target/pmd-doc-${RELEASE_VERSION}/ pmd-${RELEASE_VERSION}/
    git add pmd-${RELEASE_VERSION}
    git commit -q -m "Added pmd-${RELEASE_VERSION}"
    git rm -qr latest
    cp -a pmd-${RELEASE_VERSION} latest
    git add latest
    git commit -q -m "Copying pmd-${RELEASE_VERSION} to latest"
    git push origin master
)

fi

