#!/usr/bin/env bash

MODULE="pmd-doc"
SCRIPT_INCLUDES="log.bash openjdk.bash"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

#
# The functions here require the following environment variables:
# PMD_SF_USER
# PMD_CI_BRANCH
#
# DANGER_GITHUB_API_TOKEN
# PMD_CI_CHUNK_TOKEN

function regression_tester_setup_ci() {
    pmd_ci_log_info "Install openjdk8 for pmd-regression-tests"
    pmd_ci_openjdk_install_adoptopenjdk 8

    gpg --batch --yes --decrypt --passphrase="GnxdjywUEPveyCD1RLiTd7t8CImnefYr" \
        --output .ci/files/public-env .ci/files/public-env.gpg
    source .ci/files/public-env >/dev/null 2>&1
    rm .ci/files/public-env

    if hash "bundler" 2>/dev/null; then
        pmd_ci_log_debug "Bundler is already installed"
    else
        pmd_ci_log_info "Installing bundler..."
        gem install bundler
    fi

    rm -f .bundle/config
    bundle config set --local path vendor/bundle
    bundle config set --local with release_notes_preprocessing
    bundle install
}

#
# Generate a new baseline and upload it to pmd-code.org
#
function regression_tester_uploadBaseline() {
    local pmdcodeUrl="https://pmd-code.org/pmd-regression-tester/"
    local baseline_branch="${PMD_CI_BRANCH:-$PMD_CI_TAG}"
    pmd_ci_log_debug "$FUNCNAME branch=${baseline_branch}"

    pmd_ci_log_info "Generating and uploading baseline for pmdtester (${baseline_branch})..."
    pushd ..
    rm -f .bundle/config
    bundle config set --local gemfile pmd/Gemfile
    bundle exec pmdtester \
        --mode single \
        --local-git-repo ./pmd \
        --patch-branch ${baseline_branch} \
        --patch-config ./pmd/.ci/files/all-java.xml \
        --list-of-project ./pmd/.ci/files/project-list.xml --html-flag \
        --error-recovery
    pushd target/reports
    BRANCH_FILENAME="${baseline_branch/\//_}"
    zip -q -r ${BRANCH_FILENAME}-baseline.zip ${BRANCH_FILENAME}/
    # ssh-key for pmd-code.org is setup already by pmd_ci_setup_secrets_ssh
    scp ${BRANCH_FILENAME}-baseline.zip pmd@pmd-code.org:/httpdocs/pmd-regression-tester/
    pmd_ci_log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to ${pmdcodeUrl}"
    popd
    popd
}

#
# Execute danger, which executes pmd-regression-tester (via Dangerfile).
#
function regression_tester_executeDanger() {
    pmd_ci_log_debug "$FUNCNAME"

    # Create a corresponding remote branch locally
    if ! git show-ref --verify --quiet refs/heads/${PMD_CI_BRANCH}; then
        git fetch --no-tags --depth=1 origin +refs/heads/${PMD_CI_BRANCH}:refs/remotes/origin/${PMD_CI_BRANCH}
        git branch ${PMD_CI_BRANCH} origin/${PMD_CI_BRANCH}
        log_debug "Created local branch ${PMD_CI_BRANCH}"
    fi
    # Fetch more commits of the PR for danger and regression tester
    git fetch --no-tags --depth=50 origin +$(git rev-parse HEAD^2):
    # Fetch more commits from master branch for regression tester
    if [[ "${PMD_CI_BRANCH}" != "master" ]]; then
        git fetch --no-tags --depth=50 origin +master:
        git branch master origin/master
    fi

    pmd_ci_log_info "Running danger on branch ${PMD_CI_BRANCH}"
    bundle exec danger --verbose
    pmd_ci_log_success "Executed danger successfully"
}
