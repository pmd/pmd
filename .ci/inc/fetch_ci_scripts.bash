#!/usr/bin/env bash

function fetch_ci_scripts() {
    local inc_dir
    local inc_url
    inc_dir="$(dirname "$0")/inc"
    inc_url="${PMD_CI_SCRIPTS_URL:-https://raw.githubusercontent.com/pmd/build-tools/main/scripts}/inc"

    mkdir -p "${inc_dir}"

    for f in ${SCRIPT_INCLUDES}; do
        if [ ! -e "${inc_dir}/$f" ]; then
            curl -sSL "${inc_url}/$f" > "${inc_dir}/$f"
        fi
        [ "$PMD_CI_DEBUG" = "true" ] && echo "loading ${inc_dir}/$f in ${MODULE:-$0}"
        # shellcheck source=/dev/null
        source "${inc_dir}/$f" || exit 1
    done
}
