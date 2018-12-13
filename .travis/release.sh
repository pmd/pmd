#!/bin/bash
set -e

source .travis/logger.sh

echo "BUILD: $BUILD"
RELEASE_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo "RELEASE_VERSION: $RELEASE_VERSION"

if [ "${BUILD}" = "deploy" ]; then

# Deploy to ossrh has already been done with the usual build. See build-deploy.sh

(
    # disable fast fail, exit immediately, in this subshell
    set +e

    # The site has been built before, the files have already been uploaded to sourceforge.
    # Since this is a release, making the binary the new default file...
    log_info "Selecting pmd-bin-${RELEASE_VERSION} as default on sourceforge.net..."
    curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
         -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/pmd-bin-${RELEASE_VERSION}.zip
    if [ $? -ne 0 ]; then
        log_error "Couldn't select latest binary as default on sourceforge.net"
    else
        log_info "pmd-bin-${RELEASE_VERSION} is now the default download option."
    fi
    true
)

# renders, and skips the first 6 lines - the Jekyll front-matter
RENDERED_RELEASE_NOTES=$(bundle exec .travis/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)

# Assumes, the release has already been created by travis github releases provider
RELEASE_ID=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" https://api.github.com/repos/pmd/pmd/releases/tags/pmd_releases/${RELEASE_VERSION}|jq ".id")
RELEASE_NAME="PMD ${RELEASE_VERSION} ($(date -u +%d-%B-%Y))"
RELEASE_BODY="$RENDERED_RELEASE_NOTES"
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
log_info "Updating release at https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID}..."


RESPONSE=$(curl -i -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/json" --data "@release-edit-request.json" -X PATCH https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID})
if [[ "$RESPONSE" != *"HTTP/1.1 200"* ]]; then
    log_error "Github Request failed!"
    echo "Request:"
    cat release-edit-request.json
    echo
    echo "Response:"
    echo "$RESPONSE"
else
    log_success "Update OK"
fi

fi


if [ "${BUILD}" = "doc" ]; then

echo -e "\n\n"
log_info "Adding the new doc to pmd.github.io..."
# clone pmd.github.io. Note: This uses the ssh key setup earlier
# In order to speed things up, we use a sparse checkout - no need to checkout all directories here
mkdir pmd.github.io
(
    cd pmd.github.io
    git init
    git config user.name "Travis CI (pmd-bot)"
    git config user.email "andreas.dangel+pmd-bot@adangel.org"
    git config core.sparsecheckout true
    git remote add origin git@github.com:pmd/pmd.github.io.git
    echo "/latest/" > .git/info/sparse-checkout
    echo "/sitemap.xml" >> .git/info/sparse-checkout
    git pull --depth=1 origin master
    log_info "Copying documentation from ../docs/pmd-doc-${RELEASE_VERSION}/ to pmd-${RELEASE_VERSION}/ ..."
    rsync -ah --stats ../docs/pmd-doc-${RELEASE_VERSION}/ pmd-${RELEASE_VERSION}/
    git status
    echo "Executing: git add pmd-${RELEASE_VERSION}"
    git add pmd-${RELEASE_VERSION}
    echo "Executing: git commit..."
    git commit -q -m "Added pmd-${RELEASE_VERSION}"

    log_info "Copying pmd-${RELEASE_VERSION} to latest ..."
    git rm -qr latest
    cp -a pmd-${RELEASE_VERSION} latest
    echo "Executing: git add latest"
    git add latest
    echo "Executing: git commit..."
    git commit -q -m "Copying pmd-${RELEASE_VERSION} to latest"

    log_info "Generating sitemap.xml"
    ../.travis/sitemap_generator.sh > sitemap.xml
    echo "Executing: git add sitemap.xml"
    git add sitemap.xml
    echo "Executing: git commit..."
    git commit -q -m "Generated sitemap.xml"

    echo "Executing: git push origin master"
    git push origin master
)



(
    echo -e "\n\n"

    # disable fast fail, exit immediately, in this subshell
    set +e

    log_info "Uploading the new release to pmd.sourceforge.net which serves as an archive..."

    .travis/travis_wait "rsync -ah --stats docs/pmd-doc-${RELEASE_VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/pmd-${RELEASE_VERSION}/"

    if [ $? -ne 0 ]; then
        log_error "Uploading documentation to pmd.sourceforge.net failed..."
        log_error "Please upload manually (PMD Version: ${RELEASE_VERSION})"
    else
        log_success "The documentation is now available under http://pmd.sourceforge.net/pmd-${RELEASE_VERSION}/"
    fi
    true
)


fi

