OPENJDK_ARCHIVE=OpenJDK11U-x64_linux_11.0.3_7.tar.gz
DOWNLOAD_URL=https://pmd-code.org/${OPENJDK_ARCHIVE}
LOCAL_DIR=${HOME}/.cache/openjdk
TARGET_DIR=${HOME}/openjdk11

mkdir -p ${LOCAL_DIR}
mkdir -p ${TARGET_DIR}
wget --quiet --directory-prefix ${LOCAL_DIR} --timestamping --continue ${DOWNLOAD_URL}
tar --extract --file ${LOCAL_DIR}/${OPENJDK_ARCHIVE} -C ${TARGET_DIR} --strip-components 1

export JAVA_HOME=${TARGET_DIR}
export PATH=${JAVA_HOME}/bin:$PATH

java -version
