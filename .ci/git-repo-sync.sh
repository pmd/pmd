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
            git push pmd-sf "${PMD_CI_BRANCH}:${PMD_CI_BRANCH}"
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

git_repo_sync

exit 0
