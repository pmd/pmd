#!/bin/bash
set -e

#
# AdoptOpenJDK Builds from:
# https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/tag/jdk-11.0.4%2B11
#

source .travis/logger.sh
source .travis/common-functions.sh

# VERSION_TAG e.g. "11.0.4+11" or "13+33"
VERSION_TAG=$1
OPENJDK_MAJOR=${VERSION_TAG/.*/}
OPENJDK_MAJOR=${OPENJDK_MAJOR/+*/}
#BASE_URL=https://github.com/AdoptOpenJDK/openjdk${OPENJDK_MAJOR}-binaries/releases/download
BASE_URL=https://pmd-code.org/openjdk

log_info "Installing OpenJDK${OPENJDK_MAJOR}U ${VERSION_TAG} for ${TRAVIS_OS_NAME}"

if travis_isOSX; then
    DOWNLOAD_URL=${BASE_URL}/jdk-${VERSION_TAG/+/%2B}/OpenJDK${OPENJDK_MAJOR}U-jdk_x64_mac_hotspot_${VERSION_TAG/+/_}.tar.gz
    COMPONENTS_TO_STRIP=3 # e.g. jdk-11.0.3+7/Contents/Home/bin/java
elif travis_isWindows; then
    DOWNLOAD_URL=${BASE_URL}/jdk-${VERSION_TAG/+/%2B}/OpenJDK${OPENJDK_MAJOR}U-jdk_x64_windows_hotspot_${VERSION_TAG/+/_}.zip
else
    DOWNLOAD_URL=${BASE_URL}/jdk-${VERSION_TAG/+/%2B}/OpenJDK${OPENJDK_MAJOR}U-jdk_x64_linux_hotspot_${VERSION_TAG/+/_}.tar.gz
    COMPONENTS_TO_STRIP=1 # e.g. openjdk-11.0.3+7/bin/java
fi

OPENJDK_ARCHIVE=$(basename $DOWNLOAD_URL)

LOCAL_DIR=${HOME}/.cache/openjdk
TARGET_DIR=${HOME}/openjdk${OPENJDK_MAJOR}

mkdir -p ${LOCAL_DIR}
mkdir -p ${TARGET_DIR}
if [ ! -e ${LOCAL_DIR}/${OPENJDK_ARCHIVE} ]; then
    log_info "Downloading from ${DOWNLOAD_URL} to ${LOCAL_DIR}"
    wget --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
else
    log_info "Skipped download, file ${LOCAL_DIR}/${OPENJDK_ARCHIVE} already exists"
fi

log_info "Extracting to ${TARGET_DIR}"
if travis_isWindows; then
    7z x ${LOCAL_DIR}/${OPENJDK_ARCHIVE} -o${TARGET_DIR}
    mv ${TARGET_DIR}/*/* ${TARGET_DIR}/
else
    tar --extract --file ${LOCAL_DIR}/${OPENJDK_ARCHIVE} -C ${TARGET_DIR} --strip-components=${COMPONENTS_TO_STRIP}
fi

cat > ${HOME}/java.env <<EOF
export JAVA_HOME="${TARGET_DIR}"
export PATH="${TARGET_DIR}/bin:${PATH}"
java -version
EOF

log_info "OpenJDK can be used via ${HOME}/java.env"
cat ${HOME}/java.env
source ${HOME}/java.env

