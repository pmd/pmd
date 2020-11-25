#!/usr/bin/env bash

#
# This script should check, that all needed commands are available
# and are in the correct version.
#

source $(dirname $0)/inc/logger.inc

set -e

function check() {
    local CMD=$1
    local VERSION_CMD=$2
    local VERSION_STRING=$3

    echo -n "Checking ${CMD}..."

    if hash "$CMD" 2>/dev/null; then
      local VERSION_FULL=$(${VERSION_CMD} 2>&1)
      local VERSION=$(echo "${VERSION_FULL}" | grep "${VERSION_STRING}" 2>&1)
      if [ -n "${VERSION}" ]; then
          echo -e "${COL_GREEN}OK${COL_RESET}"
          echo "    ${VERSION}"
      else
          echo -e "${COL_RED}wrong version${COL_RESET}. Expected: ${VERSION_STRING}"
          echo "    ${VERSION_FULL}"
      fi
    else
      echo -e "${COL_RED}not found!${COL_RESET}"
    fi
}

# every OS:
check "curl" "curl --version" "curl"
check "jq" "jq --version" "jq"

case "$(uname)" in
    Linux*)
        check "ruby" "ruby --version" "ruby 2.7"
        check "gpg" "gpg --version" "gpg (GnuPG) 2."
        check "printenv" "printenv --version" "printenv (GNU coreutils)"
        check "rsync" "rsync --version" "version"
        check "ssh" "ssh -V" "OpenSSH"
        check "git" "git --version" "git version"
        check "mvn" "mvn --version" "Apache Maven"
        check "unzip" "unzip --version" "UnZip"
        check "zip" "zip --version" "This is Zip"
        #check "7z" "7z -version" "7-Zip"
        ;;
    Darwin*)
        ;;
    CYGWIN*|MINGW*)
        check "7z" "7z -version" "7-Zip"
        ;;
    *)
        log_error "Unknown OS: $(uname)"
        exit 1
    ;;
esac
