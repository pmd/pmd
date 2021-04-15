#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/setup-secrets.inc
source $(dirname $0)/inc/install-openjdk.inc

set -e

log_group_start "Setup private env and OpenJDK"
    pmd_ci_setup_private_env
    install_openjdk_setdefault 11
log_group_end

log_group_start "Executing build with sonar"
    ./mvnw \
        -Dmaven.javadoc.skip=true \
        -Dmaven.source.skip \
        -Dcheckstyle.skip \
        -B -V -e \
        clean package \
        sonar:sonar -Dsonar.login=${SONAR_TOKEN} -Psonar

    if [ $? -ne 0 ]; then
        log_error "Error updating sonar..."
    else
        log_success "New sonar results: https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd"
    fi
log_group_end
