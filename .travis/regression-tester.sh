#
# The functions here require the following scripts:
# .travis/logger.sh
#
# The functions here require the following environment variables:
# PMD_SF_USER

#
# Generate a new baseline and upload it to sourceforge
#
# Note: this function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function regression-tester_uploadBaseline() {
    log_debug "$FUNCNAME branch=${TRAVIS_BRANCH}"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd-regression-tester/"

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
        pmd/.travis/travis_wait "bundle exec pmdtester -m single -r ./pmd -p ${TRAVIS_BRANCH} -pc ./pmd/.travis/all-java.xml -l ./pmd/.travis/project-list.xml -f"
        cd target/reports
        BRANCH_FILENAME="${TRAVIS_BRANCH/\//_}"
        zip -q -r ${BRANCH_FILENAME}-baseline.zip ${BRANCH_FILENAME}/
        ../../pmd/.travis/travis_wait "rsync -avh ${BRANCH_FILENAME}-baseline.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd-regression-tester/"
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
function regression-tester_executeDanger() {
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
        if ! git show-ref --verify --quiet refs/heads/${TRAVIS_BRANCH}; then
            git fetch --no-tags origin +refs/heads/${TRAVIS_BRANCH}:refs/remotes/origin/${TRAVIS_BRANCH}
            git branch ${TRAVIS_BRANCH} origin/${TRAVIS_BRANCH}
            log_debug "Created local branch ${TRAVIS_BRANCH}"
        fi

        log_info "Running danger on branch ${TRAVIS_BRANCH}"
        bundle exec danger --verbose
        log_success "Executing danger successfully"
    )
    # restore errexit state
    eval "$errexitstate"
}
