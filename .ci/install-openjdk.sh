#!/usr/bin/env bash

#
# Downloads openjdk from AdoptOpenJDK by accessing the API.
# The API is documented at https://api.adoptopenjdk.net/swagger-ui/
#

source $(dirname $0)/logger.inc


case "$(uname)" in
    Linux*)
        JDK_OS=linux
        JDK_EXT=tar.gz
        COMPONENTS_TO_STRIP=1 # e.g. openjdk-11.0.3+7/bin/java
        ;;
    Darwin*)
        JDK_OS=mac
        JDK_EXT=tar.gz
        COMPONENTS_TO_STRIP=3 # e.g. jdk-11.0.3+7/Contents/Home/bin/java
    ;;
    CYGWIN*|MINGW*)
        JDK_OS=windows
        JDK_EXT=zip
    ;;
    *)
        
    ;;
esac


JDK_VERSION=11
DOWNLOAD_URL=https://api.adoptopenjdk.net/v3/binary/latest/${JDK_VERSION}/ga/${JDK_OS}/x64/jdk/hotspot/normal/adoptopenjdk?project=jdk
OPENJDK_ARCHIVE=openjdk-${JDK_VERSION}-${JDK_OS}.${JDK_EXT}

CACHE_DIR=${HOME}/.cache/openjdk
TARGET_DIR=${HOME}/openjdk${OPENJDK_VERSION}

mkdir -p ${CACHE_DIR}
mkdir -p ${TARGET_DIR}

if [ ! -e ${CACHE_DIR}/${OPENJDK_ARCHIVE} ]; then
    log_info "Downloading from ${DOWNLOAD_URL} to ${CACHE_DIR}"
    wget --directory-prefix=${CACHE_DIR} --timestamping --continue --output-document=${OPENJDK_ARCHIVE} ${DOWNLOAD_URL}
else
    log_info "Skipped download, file ${CACHE_DIR}/${OPENJDK_ARCHIVE} already exists"
fi

log_info "Extracting to ${TARGET_DIR}"

if [ "${JDK_EXT}" = "zip" ]; then
    7z x ${CACHE_DIR}/${OPENJDK_ARCHIVE} -o${TARGET_DIR}
    mv ${TARGET_DIR}/*/* ${TARGET_DIR}/
else
    tar --extract --file ${CACHE_DIR}/${OPENJDK_ARCHIVE} -C ${TARGET_DIR} --strip-components=${COMPONENTS_TO_STRIP}
fi

cat > ${HOME}/java.env <<EOF
export JAVA_HOME="${TARGET_DIR}"
export PATH="${TARGET_DIR}/bin:${PATH}"
java -version
EOF

log_info "OpenJDK can be used via ${HOME}/java.env"
cat ${HOME}/java.env
source ${HOME}/java.env

