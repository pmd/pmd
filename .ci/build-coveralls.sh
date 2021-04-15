#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/setup-secrets.inc
source $(dirname $0)/inc/install-openjdk.inc

set -e

log_group_start "Setup private env and OpenJDK"
    pmd_ci_setup_private_env
    install_openjdk_setdefault 11
    export CI_NAME="github actions"
    export CI_BUILD_URL="${PMD_CI_JOB_URL}"
    export CI_BRANCH="${PMD_CI_GIT_REF##refs/heads/}"
log_group_end

log_group_start "Executing build with coveralls"
    ./mvnw \
        -Dmaven.javadoc.skip=true \
        -Dmaven.source.skip \
        -Dcheckstyle.skip \
        -DrepoToken=${COVERALLS_REPO_TOKEN} \
        -B -V -e \
        clean package jacoco:report \
        coveralls:report -Pcoveralls

    if [ $? -ne 0 ]; then
        log_error "Error creating coveralls report"
    else
        log_success "New coveralls result: https://coveralls.io/github/pmd/pmd"
    fi
log_group_end
