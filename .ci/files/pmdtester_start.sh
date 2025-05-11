#!/bin/bash

set -e

case "${GITHUB_EVENT_NAME}" in
  pull_request)
    echo "Detected pull request..."
    PMD_REGRESSION_TESTER_BASE_BRANCH="${GITHUB_BASE_REF}"
    PMD_REGRESSION_TESTER_HEAD_BRANCH="pull/${GITHUB_REF_NAME%/merge}/head"
    PMD_REGRESSION_TESTER_2ND_REF="${PMD_REGRESSION_TESTER_HEAD_BRANCH}_HEAD_FETCH"
  ;;
  push)
    echo "Detected push onto branch ${GITHUB_REF_NAME}...."
    case "${GITHUB_REF_NAME}" in
      main)
        PMD_REGRESSION_TESTER_BASE_BRANCH=main
        PMD_REGRESSION_TESTER_HEAD_BRANCH=main
        PMD_REGRESSION_TESTER_2ND_REF="${PMD_REGRESSION_TESTER_PUSH_BEFORE}"
        ;;
      *)
        PMD_REGRESSION_TESTER_BASE_BRANCH=main
        PMD_REGRESSION_TESTER_HEAD_BRANCH="${GITHUB_REF_NAME}"
        PMD_REGRESSION_TESTER_2ND_REF="${PMD_REGRESSION_TESTER_HEAD_BRANCH}_HEAD_FETCH"
        ;;
    esac
  ;;
  *)
  echo "Unsupported event: ${GITHUB_EVENT_NAME}"
  exit 1
esac


echo "PMD_REGRESSION_TESTER_BASE_BRANCH=${PMD_REGRESSION_TESTER_BASE_BRANCH}"
echo "PMD_REGRESSION_TESTER_HEAD_BRANCH=${PMD_REGRESSION_TESTER_HEAD_BRANCH}"
echo "PMD_REGRESSION_TESTER_2ND_REF=${PMD_REGRESSION_TESTER_2ND_REF}"

if [ "${PMD_REGRESSION_TESTER_BASE_BRANCH}" != "main" ]; then
  echo "Only main is supported as base branch, and not: ${PMD_REGRESSION_TESTER_BASE_BRANCH}"
  exit 1
fi

if [ "${PMD_REGRESSION_TESTER_2ND_REF}" == "0000000000000000000000000000000000000000" ]; then
  echo "No changes has been pushed."
  # create files that are added to artifact "pmd-regression-tester"
  mkdir -p ../target/reports/diff
  echo -n "No regression tested rules have been changed." > ../target/reports/diff/summary.txt
  echo -n "skipped" > ../target/reports/diff/conclusion.txt
  exit 0
fi

echo "::group::Fetching additional commits"
# actions/checkout (git clone) initially only fetched with depth 2. Regression tester
# needs more history, so we'll fetch more here
# we also create local branches, so that we can use them in "merge-base" calls
# the local branches are:
# - ${PMD_REGRESSION_TESTER_BASE_BRANCH}_BASE_FETCH
# - ${PMD_REGRESSION_TESTER_HEAD_BRANCH}_HEAD_FETCH

echo "Fetching 25 commits for ${PMD_REGRESSION_TESTER_BASE_BRANCH} and ${PMD_REGRESSION_TESTER_HEAD_BRANCH}"
git fetch --no-tags --depth=25 origin \
    "${PMD_REGRESSION_TESTER_BASE_BRANCH}:${PMD_REGRESSION_TESTER_BASE_BRANCH}_BASE_FETCH" \
    "${PMD_REGRESSION_TESTER_HEAD_BRANCH}:${PMD_REGRESSION_TESTER_HEAD_BRANCH}_HEAD_FETCH"

# if the PR/branch is older, base might have advanced more than 25 commits... fetch more, up to 150
# until we find a merge base, so that we are sure, regression tester can find all the changed files on
# the branch/pull request.
for i in $(seq 1 3); do
  if [ -z "$( git merge-base "${PMD_REGRESSION_TESTER_BASE_BRANCH}_BASE_FETCH" "${PMD_REGRESSION_TESTER_2ND_REF}" )" ]; then
    echo "No merge-base yet - fetching more commits... (try $i)"
    git fetch --no-tags --deepen=50 origin \
      "${PMD_REGRESSION_TESTER_BASE_BRANCH}:${PMD_REGRESSION_TESTER_BASE_BRANCH}_BASE_FETCH" \
      "${PMD_REGRESSION_TESTER_HEAD_BRANCH}:${PMD_REGRESSION_TESTER_HEAD_BRANCH}_HEAD_FETCH"
  fi
done
merge_base="$( git merge-base "${PMD_REGRESSION_TESTER_BASE_BRANCH}_BASE_FETCH" "${PMD_REGRESSION_TESTER_2ND_REF}" )"
echo "Found merge base: ${merge_base}"
if [ "$(git symbolic-ref HEAD 2>/dev/null)" = "refs/heads/main" ]; then
  # rename main branch to free up the name "main"
  git branch -m "original_main"
fi
git branch "main" "${merge_base}"
echo "::endgroup::"

export PMD_CI_BRANCH="main"
echo "::group::Running pmdtester against base branch ${PMD_CI_BRANCH}"
if [ "${GITHUB_EVENT_NAME}" = "pull_request" ]; then
  export PMD_CI_PULL_REQUEST_NUMBER="${GITHUB_REF_NAME%/merge}"
fi
bundle exec ruby .ci/files/pmdtester.rb
echo "::endgroup::"
