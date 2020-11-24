#
# The functions here require the following scripts:
# inc/logger.inc
# inc/install-openjdk.inc
#
# The functions here require the following environment variables:
# PMD_SF_USER
# PMD_CI_BRANCH
#
# DANGER_GITHUB_API_TOKEN
# PMD_CI_CHUNK_TOKEN

function regression_tester_setup_ci() {
    log_info "Install openjdk8 for pmd-regression-tests"
    install_openjdk 8

    gpg --batch --yes --decrypt --passphrase="GnxdjywUEPveyCD1RLiTd7t8CImnefYr" \
        --output .ci/files/public-env .ci/files/public-env.gpg
    source .ci/files/public-env >/dev/null 2>&1
    rm .ci/files/public-env

    if hash "bundler" 2>/dev/null; then
        log_debug "Bundler is already installed"
    else
        log_info "Installing bundler..."
        gem install bundler
    fi

    rm -f .bundle/config
    bundle config set --local path vendor/bundle
    bundle config set --local with release_notes_preprocessing
    bundle install
}

#
# Generate a new baseline and upload it to sourceforge
#
# Note: this function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function regression_tester_uploadBaseline() {
    log_debug "$FUNCNAME branch=${PMD_CI_BRANCH}"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd-regression-tester/"
    local pmdcodeUrl="https://pmd-code.org/pmd-regression-tester/"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function upload_failed() {
            log_error "Error while uploading ${BRANCH_FILENAME}-baseline.zip to pmd-code.org!"
            log_error "Please upload manually: ${pmdcodeUrl}"
            #log_error "Error while uploading ${BRANCH_FILENAME}-baseline.zip to sourceforge!"
            #log_error "Please upload manually: ${targetUrl}"
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
        # ssh-key for pmd-code.org is setup already by pmd_ci_setup_ssh
        scp ${BRANCH_FILENAME}-baseline.zip pmd@pmd-code.org:/httpdocs/pmd-regression-tester/
        log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to ${pmdcodeUrl}"
        #../../pmd/.ci/travis_wait "rsync -avh ${BRANCH_FILENAME}-baseline.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd-regression-tester/"
        #log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to ${targetUrl}"
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
            git fetch --no-tags --depth=1 origin +refs/heads/${PMD_CI_BRANCH}:refs/remotes/origin/${PMD_CI_BRANCH}
            git branch ${PMD_CI_BRANCH} origin/${PMD_CI_BRANCH}
            log_debug "Created local branch ${PMD_CI_BRANCH}"
        fi
        # Fetch more commits of the PR for danger
        git fetch --no-tags --depth=50 origin +$(git rev-parse HEAD^2):

        log_info "Running danger on branch ${PMD_CI_BRANCH}"
        bundle exec danger --verbose
        log_success "Executing danger successfully"
    )
    # restore errexit state
    eval "$errexitstate"
}
