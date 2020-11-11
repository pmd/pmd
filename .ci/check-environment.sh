#!/usr/bin/env bash

#
# This script should check, that all needed commands are available
# and are in the correct version.
#

source $(dirname $0)/logger.inc

set -e


function check() {
    local CMD=$1
    local VERSION_CMD=$2

    echo -n "Checking ${CMD}..."

    if [ hash "$CMD" 2>/dev/null ]; then
      echo -e "${COL_GREEN}OK${COL_RESET}"
    else
      echo -e "${COL_RED}failure${COL_RESET}"
    fi
}

# every OS:
# curl
# jq

# linux only
ruby --version | grep "ruby 2.7" || (log_error "Ruby is missing"; exit 1)
check "ruby" "ruby --version"
# gpg
# printenv
# rsync
# ssh



# windows only
# 7zip
