#!/usr/bin/env bash

MODULE="pmd-code-api"
SCRIPT_INCLUDES="log.bash"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

PMD_CODE_SSH_USER=pmd
PMD_CODE_DOCS_PATH=/docs.pmd-code.org/

function pmd_code_uploadDocumentation() {
    local -r pmdVersion="$1"
    local -r filename="$2"
    local -r basefilename="$(basename "$filename")"

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion filename=$filename"

    scp "${filename}" ${PMD_CODE_SSH_USER}@pmd-code.org:${PMD_CODE_DOCS_PATH}
    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}\" && \
            unzip -qo \"${basefilename}\" && \
            rm \"${basefilename}\""
    pmd_ci_log_info "Docs updated: https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_removeDocumentation() {
    local pmdVersion="$1"

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion"

    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}\" && \
            rm -rf \"pmd-doc-${pmdVersion}/\""
    pmd_ci_log_info "Removed docs: https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_createSymlink() {
    local -r pmdVersion="$1"
    local -r name="$2"

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion name=$name"

    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}\" && \
            rm -f \"$name\" && \
            ln -s \"pmd-doc-${pmdVersion}\" \"$name\""
    pmd_ci_log_info "Symlink created: https://docs.pmd-code.org/$name/ -> https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_uploadJavadoc() {
    local -r pmdVersion="$1"
    local -r basePath="$2"

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion basePath=$basePath"

    for i in "${basePath}"/*/target/*-javadoc.jar */*/target/*-javadoc.jar; do
        pmd_code_uploadJavadocModule "$pmdVersion" "$i"
    done

    # make sure https://docs.pmd-code.org/apidocs/ shows directory index
    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}/apidocs\" && \
        echo 'Options +Indexes' > .htaccess"
    pmd_ci_log_info "Directory index enabled for https://docs.pmd-code.org/apidocs/"
}

function pmd_code_uploadJavadocModule() {
    local -r pmdVersion="$1"
    local -r moduleJavadocJar="$2"
    local -r moduleJavadocJarBasename="$(basename "$moduleJavadocJar")"
    local -r module=${moduleJavadocJarBasename%%-${pmdVersion}-javadoc.jar}

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion moduleJavadocJar=$moduleJavadocJar module=$module"

    scp "$moduleJavadocJar" ${PMD_CODE_SSH_USER}@pmd-code.org:${PMD_CODE_DOCS_PATH}
    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}\" && \
            mkdir -p \"apidocs/${module}/${pmdVersion}\" && \
            unzip -qo -d \"apidocs/${module}/${pmdVersion}\" \"${moduleJavadocJarBasename}\" && \
            rm \"${moduleJavadocJarBasename}\""
    pmd_ci_log_info "JavaDoc for $module uploaded: https://docs.pmd-code.org/apidocs/${module}/${pmdVersion}/"
}

function pmd_code_removeJavadoc() {
    local -r pmdVersion="$1"

    pmd_ci_log_debug "${FUNCNAME[0]} pmdVersion=$pmdVersion"
    # shellcheck disable=SC2029
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd \"${PMD_CODE_DOCS_PATH}\" && \
            rm -rf apidocs/*/\"${pmdVersion}\""
    pmd_ci_log_info "Removed Javadoc: https://docs.pmd-code.org/apidocs/*/${pmdVersion}/ is gone"
}
