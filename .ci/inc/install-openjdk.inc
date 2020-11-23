
# needs:
# inc/logger

#
# Downloads openjdk from AdoptOpenJDK by accessing the API.
# The API is documented at https://api.adoptopenjdk.net/swagger-ui/
#

function install_openjdk() {
    OPENJDK_VERSION=$1

    case "$(uname)" in
        Linux*)
            JDK_OS=linux
            COMPONENTS_TO_STRIP=1 # e.g. openjdk-11.0.3+7/bin/java
            ;;
        Darwin*)
            JDK_OS=mac
            COMPONENTS_TO_STRIP=3 # e.g. jdk-11.0.3+7/Contents/Home/bin/java
        ;;
        CYGWIN*|MINGW*)
            JDK_OS=windows
        ;;
        *)
            log_error "Unknown OS: $(uname)"
            exit 1
        ;;
    esac


    DOWNLOAD_URL=$(curl --silent -X GET "https://api.adoptopenjdk.net/v3/assets/feature_releases/${OPENJDK_VERSION}/ga?architecture=x64&heap_size=normal&image_type=jdk&jvm_impl=hotspot&os=${JDK_OS}&page=0&page_size=1&project=jdk&sort_method=DEFAULT&sort_order=DESC&vendor=adoptopenjdk" \
        -H "accept: application/json" \
        | jq -r ".[0].binaries[0].package.link")

    OPENJDK_ARCHIVE=$(basename ${DOWNLOAD_URL})
    log_debug "Archive name: ${OPENJDK_ARCHIVE}"

    CACHE_DIR=${HOME}/.cache/openjdk
    TARGET_DIR=${HOME}/openjdk${OPENJDK_VERSION}

    mkdir -p ${CACHE_DIR}
    mkdir -p ${TARGET_DIR}

    if [ ! -e ${CACHE_DIR}/${OPENJDK_ARCHIVE} ]; then
        log_info "Downloading from ${DOWNLOAD_URL} to ${CACHE_DIR}"
        curl --location --output ${CACHE_DIR}/${OPENJDK_ARCHIVE} "${DOWNLOAD_URL}"
    else
        log_info "Skipped download, file ${CACHE_DIR}/${OPENJDK_ARCHIVE} already exists"
    fi

    log_info "Extracting to ${TARGET_DIR}"

    case "$OPENJDK_ARCHIVE" in
        *.zip)
            7z x ${CACHE_DIR}/${OPENJDK_ARCHIVE} -o${TARGET_DIR}
            mv ${TARGET_DIR}/*/* ${TARGET_DIR}/
            ;;
        *.tar.gz)
            tar --extract --file ${CACHE_DIR}/${OPENJDK_ARCHIVE} -C ${TARGET_DIR} --strip-components=${COMPONENTS_TO_STRIP}
            ;;
        *)
            log_error "Unknown filetype: ${OPENJDK_ARCHIVE}"
            exit 1
            ;;
    esac
}

function install_openjdk_setdefault() {
    OPENJDK_VERSION=$1

    install_openjdk $OPENJDK_VERSION

    log_info "Using OpenJDK ${OPENJDK_VERSION} as default"
    TARGET_DIR=${HOME}/openjdk${OPENJDK_VERSION}
    export JAVA_HOME="${TARGET_DIR}"
    export PATH="${TARGET_DIR}/bin:${PATH}"

    java -version
}

#
# Installs jdk7 for integration test
#
function install_oraclejdk7() {
    local local_dir="${HOME}/.cache/jdk7"
    local target_dir="${HOME}/oraclejdk7"
    local download_url="https://pmd-code.org/oraclejdk/jdk-7u80-linux-x64.tar.gz"
    local archive=$(basename $download_url)

    mkdir -p ${local_dir}
    mkdir -p ${target_dir}
    if [ ! -e ${local_dir}/${archive} ]; then
        log_info "Downloading from ${download_url} to ${local_dir}"
        curl --location --output ${local_dir}/${archive} ${download_url}
    else
        log_info "Skipped download, file ${local_dir}/${archive} already exists"
    fi
    log_info "Extracting to ${target_dir}"
    tar --extract --file ${local_dir}/${archive} -C ${target_dir} --strip-components=1

    log_info "OracleJDK7 can be used via -Djava7.home=${HOME}/oraclejdk7"
}
