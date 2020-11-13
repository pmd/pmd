#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/install-openjdk.inc
source $(dirname $0)/inc/regression-tester.inc
source $(dirname $0)/inc/maven-dependencies.inc

set -e
#set -x

install_openjdk_setdefault 11
maven_dependencies_resolve

log_group_start "Building with maven"
./mvnw -e -V clean verify
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
