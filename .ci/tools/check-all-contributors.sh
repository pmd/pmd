#!/bin/bash
set -e

if [ -z "$1" ]; then
  echo "$0 <new version, e.g. 7.7.0>"
  exit 1
fi

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
NEW_VERSION="$1"

CURL_API_HEADER=(--header "X-GitHub-Api-Version: 2022-11-28")
CURL_AUTH_HEADER=()
if [ -z "$GITHUB_TOKEN" ]; then
  echo -n "GITHUB_TOKEN="
  IFS= read -r GITHUB_TOKEN
  if [ -n "$GITHUB_TOKEN" ]; then
    export GITHUB_TOKEN
    echo "Using provided GITHUB_TOKEN..."
  else
    echo "Not using GITHUB_TOKEN"
  fi
fi

if [ -n "$GITHUB_TOKEN" ]; then
  echo "Will use env var GITHUB_TOKEN for github REST API"
  CURL_AUTH_HEADER=(--header "Authorization: Bearer $GITHUB_TOKEN")
fi

# determine current milestone
MILESTONE_JSON=$(curl "${CURL_API_HEADER[@]}" "${CURL_AUTH_HEADER[@]}" -s "https://api.github.com/repos/pmd/pmd/milestones?state=all&direction=desc&per_page=10&page=1"|jq ".[] | select(.title == \"$NEW_VERSION\")")
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
# echo "$ISSUES_JSON" > issues-all.txt
#DEBUG ONLY

# Users which reported a bug, that has been fixed now (assigned to the milestone)
BUG_USERS="$(echo "$ISSUES_JSON" | jq 'map(select(has("pull_request") | not)) | [ .[].user.login ] | unique')"

COL_GREEN="\e[32m"
COL_RED="\e[31m"
COL_WHITE="\e[37m"
COL_RESET="\e[0m"

echo
echo "Checking for bug contributions..."
# for each user, check if "bug" is already present
for login in $(echo "$BUG_USERS" | jq --raw-output .[]); do
  USER_JSON="$(jq '.contributors[] | select(.login == "'"$login"'")' < "$BASEDIR"/.all-contributorsrc)"
  BUGS="$(echo "$ISSUES_JSON" |  jq 'map(select(has("pull_request") | not)) | map(select(.user.login == "'"$login"'")) | .[] | { number: .number, title: .title, login: .user.login }')"
  echo -en "${COL_WHITE}$login:${COL_RESET} "
  if [ -z "$USER_JSON" ]; then
    echo -e "${COL_RED}Missing user entirely${COL_RESET}"
    echo "$BUGS"
  else
    HAS_BUG="$(echo "$USER_JSON" | jq --raw-output 'if .contributions | contains(["bug"]) | not then "false" else "true" end')"
    if [ "$HAS_BUG" = "true" ]; then
      echo -e "${COL_GREEN}ok${COL_RESET}"
    else
      echo -e "${COL_RED}bug is missing${COL_RESET}"
      echo "$BUGS"
    fi
  fi
done

echo
echo "Checking for code contributions..."
# Users which submitted a pull request that has been merged (assigned to the milestone)
CODE_USERS="$(echo "$ISSUES_JSON" | jq 'map(select(has("pull_request"))) | [ .[].user.login ] | unique')"

# for each user, check if "code" is already present
for login in $(echo "$CODE_USERS" | jq --raw-output .[]); do
  USER_JSON="$(jq '.contributors[] | select(.login == "'"$login"'")' < "$BASEDIR"/.all-contributorsrc)"
  PRS="$(echo "$ISSUES_JSON" |  jq 'map(select(has("pull_request"))) | map(select(.user.login == "'"$login"'")) | .[] | { number: .number, title: .title, login: .user.login }')"
  echo -en "${COL_WHITE}$login:${COL_RESET} "
  if [ -z "$USER_JSON" ]; then
    echo -e "${COL_RED}Missing user entirely${COL_RESET}"
    echo "$PRS"
  else
    HAS_CODE="$(echo "$USER_JSON" | jq --raw-output 'if .contributions | contains(["code"]) | not then "false" else "true" end')"
    if [ "$HAS_CODE" = "true" ]; then
      echo -e "${COL_GREEN}ok${COL_RESET}"
    else
      echo -e "${COL_RED}code is missing${COL_RESET}"
      echo "$PRS"
    fi
  fi
done

