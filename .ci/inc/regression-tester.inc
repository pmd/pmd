#
# The functions here require the following scripts:
# inc/logger
#
# The functions here require the following environment variables:
# PMD_SF_USER
# PMD_CI_BRANCH

#
# Generate a new baseline and upload it to sourceforge
#
# Note: this function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function regression_tester_uploadBaseline() {
    log_debug "$FUNCNAME branch=${PMD_CI_BRANCH}"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd-regression-tester/"

    PMD_CI_BRANCH=${PMD_CI_BRANCH##refs/heads/}
    log_debug "Using modified branch name: ${PMD_CI_BRANCH}"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function upload_failed() {
            log_error "Error while uploading ${BRANCH_FILENAME}-baseline.zip to sourceforge!"
            log_error "Please upload manually: ${targetUrl}"
        }

        # exit subshell after trap
        set -e
        trap upload_failed ERR

        log_info "Generating and uploading baseline for pmdtester..."
        cd ..
        bundle config --local gemfile pmd/Gemfile
        pmd/.ci/travis_wait "bundle exec pmdtester
            --mode single
            --local-git-repo ./pmd
            --patch-branch ${PMD_CI_BRANCH}
            --patch-config ./pmd/.ci/files/all-java.xml
            --list-of-project ./pmd/.ci/files/project-list.xml --html-flag"
        cd target/reports
        BRANCH_FILENAME="${PMD_CI_BRANCH/\//_}"
        zip -q -r ${BRANCH_FILENAME}-baseline.zip ${BRANCH_FILENAME}/
        ../../pmd/.ci/travis_wait "rsync -avh ${BRANCH_FILENAME}-baseline.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd-regression-tester/"
        log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to ${targetUrl}"
    )
    # restore errexit state
    eval "$errexitstate"
}

#
# Execute danger, which executes pmd-regression-tester (via Dangerfile).
#
# Note: this function always succeeds, even if the danger fails.
# In that case, just a error logging is provided.
#
function regression_tester_executeDanger() {
    change_ruby_version
    log_debug "$FUNCNAME"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function danger_failed() {
            log_error "Error while executing danger/pmd-regression-tester"
        }

        # exit subshell after trap
        set -e
        trap danger_failed ERR

        # Create a corresponding remote branch locally
        if ! git show-ref --verify --quiet refs/heads/${PMD_CI_BRANCH}; then
            git fetch --no-tags origin +refs/heads/${PMD_CI_BRANCH}:refs/remotes/origin/${PMD_CI_BRANCH}
            git branch ${PMD_CI_BRANCH} origin/${PMD_CI_BRANCH}
            log_debug "Created local branch ${PMD_CI_BRANCH}"
        fi

        log_info "Running danger on branch ${PMD_CI_BRANCH}"
        bundle exec danger --verbose
        log_success "Executing danger successfully"
    )
    # restore errexit state
    eval "$errexitstate"
}
