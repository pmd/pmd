#
# The functions here require the following scripts:
# logger.sh
#
# The functions here require the following environment variables:
# GITHUB_OAUTH_TOKEN
# GITHUB_BASE_URL
#

#
# Creates a new release on github with the given tag and target_commit.
# The release is draft and not published.
#
# $RESULT = release json string
#
# See: https://developer.github.com/v3/repos/releases/#create-a-release
#
function gh_releases_createDraftRelease() {
    local tagName="$1"
    local targetCommitish="$2"

    log_debug "$FUNCNAME: Creating new draft release for tag=$tagName and commit=$targetCommitish"

    local request=$(cat <<-EOF
		{
		    "tag_name": "${tagName}",
		    "target_commitish": "${targetCommitish}",
		    "name": "${tagName}",
		    "draft": true
		}
		EOF
    )

    log_debug "POST $GITHUB_BASE_URL/releases"
    log_info "Creating github draft release"
    RESULT=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
                -H "Content-Type: application/json" \
                -X POST \
                --data "${request}" \
                "$GITHUB_BASE_URL/releases")
    log_debug " -> response: $RESULT"

    log_success "Created draft release with id $(echo $RESULT | jq --raw-output ".url")"
}

#
# Gets the latest release, if it is a draft and returns with 0.
# Returns with 1, if the latest release is not a draft - meaning, there is no
# draft release (yet?).
# 
# RESULT = release json string
#
# See: https://developer.github.com/v3/repos/releases/#list-releases-for-a-repository
#
function gh_releases_getLatestDraftRelease() {
    log_debug "$FUNCNAME"
    log_debug "GET $GITHUB_BASE_URL/releases?per_page=1"
    RESULT=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
                "$GITHUB_BASE_URL/releases?per_page=1" | jq ".[0]")
    log_debug " -> response: $RESULT"
    local draft=$(echo $RESULT | jq ".draft")
    if [ "$draft" != "true" ]; then
        RESULT=""
        log_error "Could not find draft release!"
        return 1
    fi
    log_info "Found draft release: $(echo $RESULT | jq --raw-output ".url")"
}

#
# Deletes a release.
#
# See: https://developer.github.com/v3/repos/releases/#delete-a-release
#
function gh_release_deleteRelease() {
    local release="$1"

    gh_release_getIdFromData "$release"
    local releaseId="$RESULT"
    log_debug "$FUNCNAME id=$releaseId"
    log_debug "DELETE $GITHUB_BASE_URL/releases/$releaseId"
    log_info "Deleting github release $releaseId"
    local response
    response=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
        -X DELETE \
        "$GITHUB_BASE_URL/releases/$releaseId")
    log_debug " -> response: $response"
    log_success "Deleted release with id $releaseId"
}

#
# Determines the release id from the given JSON release data.
#
# RESULT = "the release id"
#
function gh_release_getIdFromData() {
    local release="$1"

    RESULT=$(echo $release | jq --raw-output ".id")
}

#
# Determines the tag_name from the given JSON release data.
#
# RESULT = "the tag name"
#
function gh_release_getTagNameFromData() {
    local release="$1"

    RESULT=$(echo $release | jq --raw-output ".tag_name")
}

#
# Uploads a asset to an existing release.
#
# See: https://developer.github.com/v3/repos/releases/#upload-a-release-asset
#
function gh_release_uploadAsset() {
    local release="$1"
    local filename="$2"
    local name=$(basename $filename)

    gh_release_getIdFromData "$release"
    local releaseId="$RESULT"
    log_debug "$FUNCNAME: releaseId=$releaseId file=$filename name=$name"

    local uploadUrl=$(echo "$release" | jq --raw-output ".upload_url")
    uploadUrl="${uploadUrl%%\{\?name,label\}}"
    uploadUrl="${uploadUrl}?name=${name}"
    log_debug "POST $uploadUrl"
    log_info "Uploading $filename to github release $releaseId"
    local response
    response=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
                        -H "Content-Type: application/zip" \
                        --data-binary "@$filename" \
                        -X POST \
                        "${uploadUrl}")
    log_debug " -> response: $response"
    log_success "Uploaded release asset $filename for release $releaseId"
}

#
# Updates the release info: name and body.
# The body is escaped to fit into JSON, so it is allowed for the body to be
# a multi-line string.
#
# See: https://developer.github.com/v3/repos/releases/#edit-a-release
#
function gh_release_updateRelease() {
    local release="$1"
    local name="$2"
    local body="$3"

    gh_release_getIdFromData "$release"
    local releaseId="$RESULT"
    gh_release_getTagNameFromData "$release"
    local tagName="$RESULT"
    log_debug "$FUNCNAME releaseId=$releaseId name=$name tag_name=$tagName"

    body="${body//'\'/\\\\}"
    body="${body//$'\r'/}"
    body="${body//$'\n'/\\r\\n}"
    body="${body//'"'/\\\"}"

    local request=$(cat <<-EOF
		{
		    "tag_name": "${tagName}",
		    "name": "${name}",
		    "body": "${body}"
		}
		EOF
    )

    log_debug "PATCH $GITHUB_BASE_URL/releases/${releaseId}"
    log_debug " -> request: $request"
    log_info "Updating github release $releaseId"
    local response
    response=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
                         -H "Content-Type: application/json" \
                         --data "${request}" \
                         -X PATCH \
                         "$GITHUB_BASE_URL/releases/${releaseId}")
    log_debug " -> response: $response"
    log_success "Updated release with id=$releaseId"
}

#
# Publish a release by setting draft="false".
# Note: This will send out the notification emails if somebody
# watched the releases.
#
# See: https://developer.github.com/v3/repos/releases/#edit-a-release
#
function gh_release_publishRelease() {
    local release="$1"

    gh_release_getIdFromData "$release"
    local releaseId="$RESULT"
    log_debug "$FUNCNAME releaseId=$releaseId"

    local request='{"draft":false}'
    log_debug "PATCH $GITHUB_BASE_URL/releases/${releaseId}"
    log_debug " -> request: $request"
    log_info "Publishing github release $releaseId"
    local response
    response=$(curl --fail -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" \
                         -H "Content-Type: application/json" \
                         --data "${request}" \
                         -X PATCH \
                         "$GITHUB_BASE_URL/releases/${releaseId}")
    log_debug " -> response: $response"
    local htmlUrl=$(echo "$response" | jq --raw-output ".html_url")
    log_success "Published release with id=$releaseId at $htmlUrl"
}
