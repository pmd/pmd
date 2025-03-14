#!/bin/bash
set -e

if [ -z "$1" ]; then
  echo "$0 <pull-request-number, e.g. 5237>"
  exit 1
fi

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PULL_NUMBER="$1"

CURL_API_HEADER=(--header "X-GitHub-Api-Version: 2022-11-28")
CURL_AUTH_HEADER=()
if [ -n "$GITHUB_TOKEN" ]; then
  echo "Will use env var GITHUB_TOKEN for github REST API"
  CURL_AUTH_HEADER=(--header "Authorization: Bearer $GITHUB_TOKEN")
fi


PULL_JSON=$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s "https://api.github.com/repos/pmd/pmd/pulls/$PULL_NUMBER")
#echo "$PULL_JSON" > pull-response-$PULL_NUMBER.txt
#DEBUG ONLY
#PULL_JSON=$(cat pull-response-$PULL_NUMBER.txt)
#DEBUG ONLY

PULL_ITEM="$(echo "$PULL_JSON" | jq --raw-output '"* [#\(.number)](https://github.com/pmd/pmd/pull/\(.number)): \(.title | gsub("@"; "@<!-- -->") | gsub("\\["; "\\[")) - @\(.user.login)"')"

USER="$(echo "$PULL_JSON" | jq --raw-output .user.login)"
USER_JSON="$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s "https://api.github.com/users/$USER")"
#DEBUG ONLY
#USER_JSON="{\"login\": \"$USER\", \"name\": \"foo $USER\"}"
#DEBUG_ONLY
USER_NAME="$(echo "$USER_JSON" | jq --raw-output .name)"
search=" - \@$USER"
replacement=" - [$USER_NAME](https://github.com/$USER) (@$USER)"
PULL_ITEM="${PULL_ITEM//${search}/${replacement}}"

RELEASE_NOTES_FILE="${BASEDIR}/docs/pages/release_notes.md"
RELEASE_NOTES=$(cat "$RELEASE_NOTES_FILE")

line="$(echo "$RELEASE_NOTES" | grep -n "### 📦 Dependency updates" | cut -d ":" -f 1)"
RELEASE_NOTES="$(echo "$RELEASE_NOTES" | head -n "$((line - 1))")
$PULL_ITEM
$(echo "$RELEASE_NOTES" | tail -n "+$((line - 1))")
"
echo "$RELEASE_NOTES" > "$RELEASE_NOTES_FILE"
