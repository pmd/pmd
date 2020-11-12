#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/setup-secrets.inc
source $(dirname $0)/inc/regression-tester.inc
source ${HOME}/java.env

set -e
#set -x

export MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=180 -Dmaven.wagon.http.retryHandler.count=3"
#export MAVEN_OPTS="-Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false"
./mvnw -e -V clean verify

# Danger is executed only on the linux runner
case "$(uname)" in
    Linux*)
        log_info "Executing danger..."
        pmd_ci_setup_env
        regression_tester_setup_ci
        regression_tester_executeDanger
        ;;
esac
