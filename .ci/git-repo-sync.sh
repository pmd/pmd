#!/usr/bin/env bash

# Exit this script immediately if a command/function exits with a non-zero status.
set -e

SCRIPT_INCLUDES="log.bash utils.bash setup-secrets.bash"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

function git_repo_sync() {
    echo
    pmd_ci_utils_determine_build_env pmd/pmd
    echo

    if pmd_ci_utils_is_fork_or_pull_request; then
        pmd_ci_log_error "This should not run on forked repositories or pull requests"
        exit 0
    fi

    # only builds on pmd/pmd continue here
    pmd_ci_log_group_start "Setup environment"
        pmd_ci_setup_secrets_private_env
        pmd_ci_setup_secrets_gpg_key
        pmd_ci_setup_secrets_ssh
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Git Sync"
        git remote add pmd-sf "${PMD_SF_USER}@git.code.sf.net:/p/pmd/code"
        if [ -n "${PMD_CI_BRANCH}" ]; then
            retry 5 git push pmd-sf "${PMD_CI_BRANCH}:${PMD_CI_BRANCH}"
            pmd_ci_log_success "Successfully pushed ${PMD_CI_BRANCH} to sourceforge"
        elif [ -n "${PMD_CI_TAG}" ]; then
            git push pmd-sf tag "${PMD_CI_TAG}"
            pmd_ci_log_success "Successfully pushed tag ${PMD_CI_TAG} to sourceforge"
        else
            pmd_ci_log_error "Don't know what to do: neither PMD_CI_BRANCH nor PMD_CI_TAG is set"
            exit 1
        fi
    pmd_ci_log_group_end
}


#
# From: https://gist.github.com/sj26/88e1c6584397bb7c13bd11108a579746
#
# Retry a command up to a specific number of times until it exits successfully,
# with exponential back off.
#
#  $ retry 5 echo Hello
#  Hello
#
#  $ retry 5 false
#  Retry 1/5 exited 1, retrying in 1 seconds...
#  Retry 2/5 exited 1, retrying in 2 seconds...
#  Retry 3/5 exited 1, retrying in 4 seconds...
#  Retry 4/5 exited 1, retrying in 8 seconds...
#  Retry 5/5 exited 1, no more retries left.
#
function retry {
  local retries=$1
  shift

  local count=0
  until "$@"; do
    exit=$?
    wait=$((2 ** $count))
    count=$(($count + 1))
    if [ $count -lt $retries ]; then
      echo "Retry $count/$retries exited $exit, retrying in $wait seconds..."
      sleep $wait
    else
      echo "Retry $count/$retries exited $exit, no more retries left."
      return $exit
    fi
  done
  return 0
}

git_repo_sync

exit 0
