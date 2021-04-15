#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/install-openjdk.inc
source $(dirname $0)/inc/regression-tester.inc

set -e

log_group_start "Installing Java"
    log_info "Install openjdk11 as default"
    install_openjdk_setdefault 11

    PMD_EXTRA_OPT=""
    if [[ "$(uname)" == Linux* ]]; then
        log_info "Install oracle7 for integration tests"
        install_oraclejdk7
        PMD_EXTRA_OPT="-Djava7.home=${HOME}/oraclejdk7"
    fi
log_group_end

log_group_start "Building with maven"
    ./mvnw -e -V -B clean verify ${PMD_EXTRA_OPT}
log_group_end


# Danger is executed only on the linux runner
case "$(uname)" in
    Linux*)
        log_group_start "Executing danger"
            regression_tester_setup_ci
            regression_tester_executeDanger
        log_group_end
        ;;
esac
