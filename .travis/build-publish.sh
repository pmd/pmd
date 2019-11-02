#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
log_info "PMD Release ${VERSION}"

if ! travis_isPush; then
    log_info "Not publishing release, since this is not a push!"
    exit 0
fi

if [[ "${VERSION}" == *-SNAPSHOT || "${TRAVIS_TAG}" == "" ]]; then
    log_info "Not publishing release, since this is not a release!"
    exit 0
fi

# Publish github release
RELEASE_ID=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" 'https://api.github.com/repos/pmd/pmd/releases?per_page=1'|jq ".[0].id")
RELEASE_DATA=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" "https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID}")
DRAFT=$(echo "$RELEASE_DATA"|jq ".draft")
if [ "$DRAFT" != "true" ]; then
    log_error "No draft release found! (needs to be the latest one)."
    exit 1;
fi

log_info "Using draft release with id "${RELEASE_ID}"

REQUEST=$(cat <<EOF
{
    "draft": false
}
EOF
)

RESPONSE=$(curl -i -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/json" \
    --data "${REQUEST}" -X PATCH https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID})
if [[ "$RESPONSE" != *"HTTP/1.1 200"* ]]; then
    log_error "Github Request failed!"
    echo "Request:"
    echo "$REQUEST"
    echo
    echo "Response:"
    echo "$RESPONSE"
else
    log_success "Update OK"
    
    HTML_URL=$(echo "$RELEASE_DATA" | jq --raw-output ".html_url")
    log_info "The release ${HTML_URL} is published"
fi
