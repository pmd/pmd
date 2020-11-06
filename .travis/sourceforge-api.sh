#
# The functions here require the following scripts:
# .travis/logger.sh
#
# The functions here require the following environment variables:
# PMD_SF_USER
# PMD_SF_APIKEY
#

#
# Uploads the release notes to sourceforge files as "ReadMe.md".
#
# Note: this function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function sourceforge_uploadReleaseNotes() {
    local pmdVersion="$1"
    local releaseNotes="$2"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd/${pmdVersion}"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function release_notes_fail() {
            log_error "Error while uploading release notes as ReadMe.md to sourceforge!"
            log_error "Please upload manually: ${targetUrl}"
            cleanup_temp_dir
        }

        function cleanup_temp_dir() {
            log_debug "Cleanup tempdir $releaseNotesTempDir"
            rm "${releaseNotesTempDir}/${pmdVersion}/ReadMe.md" || true
            rmdir "${releaseNotesTempDir}/${pmdVersion}" || true
            rmdir "${releaseNotesTempDir}" || true
        }

        # exit subshell after trap
        set -e
        trap release_notes_fail ERR

        local releaseNotesTempDir=$(mktemp -d)
        log_debug "Tempdir: $releaseNotesTempDir"
        mkdir -p "${releaseNotesTempDir}/${pmdVersion}"
        echo "$releaseNotes" > "${releaseNotesTempDir}/${pmdVersion}/ReadMe.md"

        log_info "Uploading release notes to sourceforge for version $pmdVersion"
        rsync -avz \
            "${releaseNotesTempDir}/" \
            "${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/"

        log_success "Successfully uploaded release notes as ReadMe.md to sourceforge: ${targetUrl}"

        cleanup_temp_dir
    )
    # restore errexit state
    eval "$errexitstate"
}

#
# Uploads the given file to sourceforge.
#
# Note: This function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function sourceforge_uploadFile() {
    local pmdVersion="$1"
    local filename="$2"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion filename=$filename"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd/${pmdVersion}"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function upload_failed() {
            log_error "Error while uploading ${filename} to sourceforge!"
            log_error "Please upload manually: ${targetUrl}"
        }

        # exit subshell after trap
        set -e
        trap upload_failed ERR

        log_info "Uploading $filename to sourceforge..."
        .travis/travis_wait "rsync -avh ${filename} ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${pmdVersion}/"
        log_success "Successfully uploaded ${filename} to sourceforge: ${targetUrl}"
    )
    # restore errexit state
    eval "$errexitstate"
}

#
# Select the given version as the new default download.
#
# Note: This function always succeeds, even if the request fails.
# In that case, just a error logging is provided.
#
function sourceforge_selectDefault() {
    local pmdVersion="$1"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion"
    local targetUrl="https://sourceforge.net/projects/pmd/files/pmd/${pmdVersion}"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function request_failed() {
            log_error "Error while selecting ${pmdVersion} as new default download on sourceforge!"
            log_error "Please do it manually: ${targetUrl}"
        }

        # exit subshell after trap
        set -e
        trap request_failed ERR

        log_info "Selecting $pmdVersion as new default on sourceforge..."
        local response
        response=$(curl --fail -s -H "Accept: application/json" \
            -X PUT \
            -d "api_key=${PMD_SF_APIKEY}" \
            -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
            "https://sourceforge.net/projects/pmd/files/pmd/${pmdVersion}/pmd-bin-${pmdVersion}.zip")
        log_debug " -> response: $response"
        log_success "Successfully selected $pmdVersion as new default on sourceforge: ${targetUrl}"
    )
    # restore errexit state
    eval "$errexitstate"
}

#
# Rsyncs the complete documentation to sourceforge.
#
# Note: This function always succeeds, even if the upload fails.
# In that case, just a error logging is provided.
#
function sourceforge_rsyncSnapshotDocumentation() {
    local pmdVersion="$1"
    local targetPath="$2"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion targetPath=$targetPath"
    local targetUrl="https://pmd.sourceforge.io/${targetPath}/"

    local errexitstate="$(shopt -po errexit)"
    set +e # disable errexit
    (
        # This handler is called if any command fails
        function upload_failed() {
            log_error "Couldn't upload the documentation. It won't be current on ${targetUrl}"
        }

        # exit subshell after trap
        set -e
        trap upload_failed ERR

        log_info "Uploading documentation to ${targetUrl}..."
        .travis/travis_wait "rsync -ah --stats --delete docs/pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/"
        log_success "Successfully uploaded documentation: ${targetUrl}"
    )
    # restore errexit state
    eval "$errexitstate"
}
