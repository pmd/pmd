#!/usr/bin/env bash

MODULE="pmd-doc"
SCRIPT_INCLUDES="log.bash openjdk.bash"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

#
# The functions here require the following environment variables:
# PMD_CI_BRANCH
#
# DANGER_GITHUB_API_TOKEN
# PMD_CI_CHUNK_TOKEN

function regression_tester_setup_ci() {
    # note: building spring needs java8. This is setup already by build.sh

    gpg --batch --yes --decrypt --passphrase="GnxdjywUEPveyCD1RLiTd7t8CImnefYr" \
        --output .ci/files/public-env .ci/files/public-env.gpg
    # shellcheck disable=SC1091
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
    pmd_ci_log_debug "${FUNCNAME[0]} branch=${baseline_branch}"

    pmd_ci_log_info "Generating and uploading baseline for pmdtester (${baseline_branch})..."
    pushd ..
    rm -f .bundle/config
    bundle config set --local gemfile pmd/Gemfile
    bundle exec pmdtester \
        --mode single \
        --local-git-repo ./pmd \
        --patch-branch "${baseline_branch}" \
        --patch-config ./pmd/.ci/files/all-java.xml \
        --list-of-project ./pmd/.ci/files/project-list.xml --html-flag \
        --error-recovery
    pushd target/reports || { echo "Directory 'target/reports' doesn't exist"; exit 1; }
    BRANCH_FILENAME="${baseline_branch/\//_}"
    zip -q -r "${BRANCH_FILENAME}-baseline.zip" "${BRANCH_FILENAME}/"
    # ssh-key for pmd-code.org is setup already by pmd_ci_setup_secrets_ssh
    scp "${BRANCH_FILENAME}-baseline.zip" pmd@pmd-code.org:/httpdocs/pmd-regression-tester/
    pmd_ci_log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to ${pmdcodeUrl}"
    popd || exit 1
    popd || exit 1
}

#
# Execute danger, which executes pmd-regression-tester (via Dangerfile).
#
function regression_tester_executeDanger() {
    pmd_ci_log_debug "${FUNCNAME[0]}"

    # git clone initially only fetched with depth 2. Danger and regression tester
    # need more history, so we'll fetch more here
    # and create local branches as well (${PMD_CI_BRANCH} and pr-fetch)

    pmd_ci_log_info "Fetching 25 commits for ${PMD_CI_BRANCH} and pull/${PMD_CI_PULL_REQUEST_NUMBER}/head"
    git fetch --no-tags --depth=25 origin "${PMD_CI_BRANCH}:${PMD_CI_BRANCH}" "pull/${PMD_CI_PULL_REQUEST_NUMBER}/head:pr-fetch"

    # if the PR is older, base might have advanced more than 25 commits... fetch more, up to 150
    for i in $(seq 1 3); do
        if [ -z "$( git merge-base "${PMD_CI_BRANCH}" "pr-fetch" )" ]; then
            pmd_ci_log_info "No merge-base yet - fetching more commits... (try $i)"
            git fetch --no-tags --deepen=50 origin "${PMD_CI_BRANCH}:" "pull/${PMD_CI_PULL_REQUEST_NUMBER}/head:pr-fetch"
        fi
    done
    pmd_ci_log_info "Merge base is: $( git merge-base "${PMD_CI_BRANCH}" "pr-fetch" )"

    pmd_ci_log_info "Running danger on branch ${PMD_CI_BRANCH}"
    bundle exec danger --verbose
    pmd_ci_log_success "Executed danger successfully"
}
