#
# Original sources:
# Linux: https://github.com/AdoptOpenJDK/openjdk11-upstream-binaries/releases/jdk-11.0.3%2B7/
#        https://github.com/AdoptOpenJDK/openjdk11-upstream-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-x64_linux_11.0.3_7.tar.gz
# MacOSX: https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/jdk-11.0.3%2B7/
#         https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_x64_mac_hotspot_11.0.3_7.tar.gz
#


if [[ "$OSTYPE" == "darwin"* ]]; then
    OPENJDK_ARCHIVE=OpenJDK11U-jdk_x64_mac_hotspot_11.0.3_7.tar.gz
    COMPONENTS_TO_STRIP=3 # e.g. jdk-11.0.3+7/Contents/Home/bin/java
else
    OPENJDK_ARCHIVE=OpenJDK11U-x64_linux_11.0.3_7.tar.gz
    COMPONENTS_TO_STRIP=1 # e.g. openjdk-11.0.3+7/bin/java
fi

DOWNLOAD_URL=https://pmd-code.org/${OPENJDK_ARCHIVE}
LOCAL_DIR=${HOME}/.cache/openjdk
TARGET_DIR=${HOME}/openjdk11

mkdir -p ${LOCAL_DIR}
mkdir -p ${TARGET_DIR}
wget --quiet --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
tar --extract --file ${LOCAL_DIR}/${OPENJDK_ARCHIVE} -C ${TARGET_DIR} --strip-components=${COMPONENTS_TO_STRIP}

export JAVA_HOME=${TARGET_DIR}
export PATH=${JAVA_HOME}/bin:$PATH

java -version
