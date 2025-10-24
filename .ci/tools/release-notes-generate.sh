#!/bin/bash
set -e

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "$0 <last version, e.g. 7.6.0> <new version, e.g. 7.7.0>"
  exit 1
fi

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LAST_VERSION="$1"
RELEASE_VERSION="$2"

CURL_API_HEADER=(--header "X-GitHub-Api-Version: 2022-11-28")
CURL_AUTH_HEADER=()
if [ -n "$GITHUB_TOKEN" ]; then
  echo "Will use env var GITHUB_TOKEN for github REST API"
  CURL_AUTH_HEADER=(--header "Authorization: Bearer $GITHUB_TOKEN")
fi

# determine current milestone
MILESTONE_JSON=$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s "https://api.github.com/repos/pmd/pmd/milestones?state=all&direction=desc&per_page=10&page=1"|jq ".[] | select(.title == \"$RELEASE_VERSION\")")
#DEBUG ONLY
#MILESTONE_JSON='{"number":80,"closed_issues":40}'
#DEBUG ONLY
MILESTONE=$(echo "$MILESTONE_JSON" | jq .number)

PAGE="1"
HAS_NEXT="true"
ISSUES_JSON=""
while [ "$HAS_NEXT" = "true" ]; do
    echo "Fetching issues for milestone ${MILESTONE} page ${PAGE}..."
    URL="https://api.github.com/repos/pmd/pmd/issues?state=closed&sort=created&direction=asc&per_page=30&page=${PAGE}&milestone=${MILESTONE}"
    RESPONSE="$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s -w "\nLink: %header{link}" "$URL")"

    #DEBUG ONLY
    #echo "$RESPONSE" > issues-response-${PAGE}.txt
    #RESPONSE="$(cat issues-response-${PAGE}.txt)"
    #DEBUG ONLY

    LINK_HEADER="$(echo "$RESPONSE" | tail -1)"
    BODY="$(echo "$RESPONSE" | head -n -1)"

    #DEBUG ONLY
    #echo "$BODY" > "issues-response-page-${PAGE}.txt"
    #BODY="$(cat "issues-response-page-${PAGE}.txt")"
    #DEBUG ONLY

    COMMA=","
    if [ "$PAGE" -eq 1 ]; then
        COMMA=""
    fi
    ISSUES_JSON="${ISSUES_JSON}${COMMA}${BODY}"

    if [[ $LINK_HEADER == *"; rel=\"next\""* ]]; then
        HAS_NEXT="true"
    else
        HAS_NEXT="false"
    fi
    PAGE=$((PAGE + 1))

    #DEBUG ONLY
    #HAS_NEXT="true"
    #if [ "$PAGE" -gt 2 ]; then break; fi
    #DEBUG ONLY

    # stop after 10 pages
    if [ "$PAGE" -gt 10 ]; then
        echo
        echo
        echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        echo "!!!!!!!!!!!!!! reached page 10, stopping now !!!!!!!!!!!!!"
        echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        echo
        echo
        break;
    fi
done


ISSUES_JSON="$(echo "[ $ISSUES_JSON ]" | jq 'flatten(1)')"
echo "Found $(echo "$ISSUES_JSON" | jq length) issues/pull requests"
#DEBUG ONLY
#echo "$ISSUES_JSON" > issues-all.txt
#DEBUG ONLY

FIXED_ISSUES_JSON="$(echo "$ISSUES_JSON" | jq 'map(select(has("pull_request") | not))')"
FIXED_ISSUES="$(echo "$FIXED_ISSUES_JSON" | jq --raw-output '.[] | "* [#\(.number)](https://github.com/pmd/pmd/issues/\(.number)): \(.title | gsub("@"; "@<!-- -->") | gsub("\\["; "\\["))"')"
FIXED_ISSUES="### 🐛️ Fixed Issues
<!-- content will be automatically generated, see /do-release.sh -->
$FIXED_ISSUES
"

PULL_REQUESTS_JSON="$(echo "$ISSUES_JSON" | jq 'map(select(has("pull_request"))) | map(select(contains({labels: [{name: "dependencies"}]}) | not))')"
PULL_REQUESTS="$(echo "$PULL_REQUESTS_JSON" | jq --raw-output '.[] | "* [#\(.number)](https://github.com/pmd/pmd/pull/\(.number)): \(.title | gsub("@"; "@<!-- -->") | gsub("\\["; "\\[")) - @\(.user.login)"')"

AUTHORS="$(echo "$PULL_REQUESTS_JSON" | jq --raw-output '.[].user.login' | sort | uniq)"
echo "Resolving $(echo "$AUTHORS" | wc -l) author names in pull requests..."
for login in $AUTHORS; do
    USER_JSON="$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s "https://api.github.com/users/$login")"
    #DEBUG ONLY
    #USER_JSON="{\"login\": \"$login\", \"name\": \"foo $login\"}"
    #DEBUG_ONLY
    USER_NAME="$(echo "$USER_JSON" | jq --raw-output ".name // \"$login\"")"
    search=" - \@$login"
    replacement=" - [$USER_NAME](https://github.com/$login) (@$login)"
    PULL_REQUESTS="${PULL_REQUESTS//${search}/${replacement}}"
done

PULL_REQUESTS="### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
$PULL_REQUESTS
"

DEPENDENCY_UPDATES_JSON="$(echo "$ISSUES_JSON" | jq 'map(select(has("pull_request"))) | map(select(contains({labels: [{name: "dependencies"}]})))')"
DEPENDENCY_UPDATES="$(echo "$DEPENDENCY_UPDATES_JSON" | jq --raw-output '.[] | "* [#\(.number)](https://github.com/pmd/pmd/pull/\(.number)): \(.title | gsub("@"; "@<!-- -->") | gsub("\\["; "\\["))"')"
DEPENDENCY_UPDATES_COUNT=$(echo "$DEPENDENCY_UPDATES_JSON" | jq length)
if [ -z "$DEPENDENCY_UPDATES" ]; then DEPENDENCY_UPDATES="No dependency updates."; fi
DEPENDENCY_UPDATES="### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
$DEPENDENCY_UPDATES
"

# calculating stats for release notes (excluding dependency updates)
STATS_CLOSED_ISSUES=$(echo "$MILESTONE_JSON" | jq .closed_issues)
STATS=$(
echo "### 📈️ Stats"
echo "<!-- content will be automatically generated, see /do-release.sh -->"
echo "* $(git log pmd_releases/"${LAST_VERSION}"..HEAD --oneline --no-merges |wc -l) commits"
echo "* $((STATS_CLOSED_ISSUES - DEPENDENCY_UPDATES_COUNT)) closed tickets & PRs"
echo "* Days since last release: $(( ( $(date +%s) - $(git log --max-count=1 --format="%at" pmd_releases/"${LAST_VERSION}") ) / 86400))"
echo
)

function insert() {
  local FULL_TEXT="$1"
  local FROM_MARKER="$2"
  local END_MARKER="$3"
  local INSERTION="$4"
  local fromLine
  local endLine
  local headText
  local tailText
  fromLine="$(echo "$FULL_TEXT" | grep -n "$FROM_MARKER" | cut -d ":" -f 1)"
  endLine="$(echo "$FULL_TEXT" | grep -n "$END_MARKER" | cut -d ":" -f 1)"
  headText="$(echo "$FULL_TEXT" | head -n "$((fromLine - 1))")"
  tailText="$(echo "$FULL_TEXT" | tail -n "+$endLine")"
  echo "$headText

$INSERTION

$tailText"
}

RELEASE_NOTES_FILE="${BASEDIR}/docs/pages/release_notes.md"
echo "Updating $RELEASE_NOTES_FILE now..."

RELEASE_NOTES=$(cat "$RELEASE_NOTES_FILE")
#RELEASE_NOTES="$(insert "$RELEASE_NOTES" "### 🐛️ Fixed Issues" "### ✨️ Merged pull requests" "$FIXED_ISSUES")"
RELEASE_NOTES="$(insert "$RELEASE_NOTES" "### ✨️ Merged pull requests" "### 📦️ Dependency updates" "$PULL_REQUESTS")"
RELEASE_NOTES="$(insert "$RELEASE_NOTES" "### 📦️ Dependency updates" "### 📈️ Stats" "$DEPENDENCY_UPDATES")"
RELEASE_NOTES="$(insert "$RELEASE_NOTES" "### 📈️ Stats" "{% endtocmaker %}" "$STATS")"

echo "$RELEASE_NOTES" > "$RELEASE_NOTES_FILE"
