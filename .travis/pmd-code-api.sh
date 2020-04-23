#
# The functions here require the following scripts:
# .travis/logger.sh
#

PMD_CODE_SSH_USER=pmd
PMD_CODE_DOCS_PATH=/docs.pmd-code.org/

function pmd_code_uploadDocumentation() {
    local pmdVersion="$1"
    local filename="$2"
    local basefilename="$(basename $filename)"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion filename=$filename"

    scp "${filename}" ${PMD_CODE_SSH_USER}@pmd-code.org:${PMD_CODE_DOCS_PATH}
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH} && \
            unzip -qo ${basefilename} && \
            rm ${basefilename}"
    log_info "Docs updated: https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_removeDocumentation() {
    local pmdVersion="$1"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion"

    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH} && \
            rm -rf pmd-doc-${pmdVersion}/"
    log_info "Removed docs: https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_createSymlink() {
    local pmdVersion="$1"
    local name="$2"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion name=$name"

    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH} && \
            rm -f $name && \
            ln -s pmd-doc-${pmdVersion} $name"
    log_info "Symlink created: https://docs.pmd-code.org/$name/ -> https://docs.pmd-code.org/pmd-doc-${pmdVersion}/"
}

function pmd_code_uploadJavadoc() {
    local pmdVersion="$1"
    local basePath="$2"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion basePath=$basePath"

    for i in ${basePath}/*/target/*-javadoc.jar; do
        pmd_code_uploadJavadocModule "$pmdVersion" "$i"
    done

    pmd_code_fixPmdLangTestStyle "${basePath}"

    # make sure https://docs.pmd-code.org/apidocs/ shows directory index
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH}/apidocs && \
        echo 'Options +Indexes' > .htaccess"
    log_info "Directory index enabled for https://docs.pmd-code.org/apidocs/"
}

function pmd_code_uploadJavadocModule() {
    local pmdVersion="$1"
    local moduleJavadocJar="$2"
    local moduleJavadocJarBasename="$(basename $moduleJavadocJar)"
    local module=${moduleJavadocJarBasename%%-${pmdVersion}-javadoc.jar}

    log_debug "$FUNCNAME pmdVersion=$pmdVersion moduleJavadocJar=$moduleJavadocJar module=$module"

    scp "$moduleJavadocJar" ${PMD_CODE_SSH_USER}@pmd-code.org:${PMD_CODE_DOCS_PATH}
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH} && \
            mkdir -p apidocs/${module}/${pmdVersion} && \
            unzip -qo -d apidocs/${module}/${pmdVersion} ${moduleJavadocJarBasename} && \
            rm ${moduleJavadocJarBasename}"
    log_info "JavaDoc for $module uploaded: https://docs.pmd-code.org/apidocs/${module}/${pmdVersion}/"
}

function pmd_code_fixPmdLangTestStyle {
    local basePath="$1"

    log_debug "$FUNCNAME basePath=$basePath"
    scp "${basePath}/pmd-lang-test/target/dokka/style.css" ${PMD_CODE_SSH_USER}@pmd-code.org:${PMD_CODE_DOCS_PATH}/apidocs/pmd-lang-test/
    log_info "Fixed style for https://docs.pmd-code.org/apidocs/pmd-lang-test/*/"
}

function pmd_code_removeJavadoc() {
    local pmdVersion="$1"

    log_debug "$FUNCNAME pmdVersion=$pmdVersion"
    ssh ${PMD_CODE_SSH_USER}@pmd-code.org "cd ${PMD_CODE_DOCS_PATH} && \
            rm -rf apidocs/*/${pmdVersion}"
    log_info "Removed Javadoc: https://docs.pmd-code.org/apidocs/*/${pmdVersion}/ is gone"
}
