#!/bin/bash

LAST_COMMIT_INFO=$(curl -s "https://api.github.com/repos/forcedotcom/salesforcedx-vscode/commits?sha=develop&path=packages%2Fsalesforcedx-vscode-apex%2Fout%2Fapex-jorje-lsp.jar&page=1&per_page=1")
LAST_COMMIT_DATE=$(echo $LAST_COMMIT_INFO | jq -r '.[0].commit.committer.date')
LAST_COMMIT_SHA=$(echo $LAST_COMMIT_INFO | jq -r '.[0].sha')

VERSION=${LAST_COMMIT_DATE%T*}-${LAST_COMMIT_SHA:0:6}

URL=https://raw.githubusercontent.com/forcedotcom/salesforcedx-vscode/${LAST_COMMIT_SHA}/packages/salesforcedx-vscode-apex/out/apex-jorje-lsp.jar
FILENAME=apex-jorje-lsp-${VERSION}.jar
FILENAME_MINIMIZED=apex-jorje-lsp-minimized-${VERSION}.jar


function install() {
    mvn install:install-file -Dfile=${FILENAME_MINIMIZED} \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-lsp-minimized \
                             -Dversion=${VERSION} \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=$(dirname $0)
}

function download() {
    curl -o $FILENAME $URL
}


function minimize() {
    unzip -d temp ${FILENAME}
    pushd temp
    find . -not -path "." \
        -and -not -path ".." \
        -and -not -path "./apex*" \
        -and -not -path "./StandardApex*" \
        -and -not -path "./messages*" \
        -and -not -path "./com" \
        -and -not -path "./com/google" \
        -and -not -path "./com/google/common*" \
        -print0 | xargs -0 rm -rf
    popd
    jar --create --file ${FILENAME_MINIMIZED} -C temp/ .
    rm -rf temp
}

function cleanup() {
    rm ${FILENAME}
    rm ${FILENAME_MINIMIZED}
}


download
minimize
install
cleanup



