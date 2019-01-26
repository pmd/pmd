#!/bin/bash

LAST_COMMIT_INFO=$(curl -s "https://api.github.com/repos/forcedotcom/salesforcedx-vscode/commits?sha=develop&path=packages%2Fsalesforcedx-vscode-apex%2Fout%2Fapex-jorje-lsp.jar&page=1&per_page=1")
LAST_COMMIT_DATE=$(echo $LAST_COMMIT_INFO | jq -r '.[0].commit.committer.date')
LAST_COMMIT_SHA=$(echo $LAST_COMMIT_INFO | jq -r '.[0].sha')

VERSION=${LAST_COMMIT_DATE%T*}-${LAST_COMMIT_SHA:0:6}

URL=https://raw.githubusercontent.com/forcedotcom/salesforcedx-vscode/${LAST_COMMIT_SHA}/packages/salesforcedx-vscode-apex/out/apex-jorje-lsp.jar
FILENAME=apex-jorje-lsp-${VERSION}.jar
FILENAME_MINIMIZED=apex-jorje-lsp-minimized-${VERSION}.jar

REPOPATH=$(dirname $0)

function deleteoldrepo() {
    git rm -r ${REPOPATH}/apex/apex-jorje-lsp-minimized/*
}

function install() {
    mvn install:install-file -Dfile=${FILENAME_MINIMIZED} \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-lsp-minimized \
                             -Dversion=${VERSION} \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=${REPOPATH}
    git add ${REPOPATH}
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

function updateversion() {
    sed -i -e "s/\(<apex\.jorje\.version>\).*\(<\/apex\.jorje\.version>\)/\1${VERSION}\2/" $(dirname $0)/../pom.xml
    git add $(dirname $0)/../pom.xml
}

function diffstat() {
    echo "Ready to commit:"
    git diff --cached --stat
}

download
minimize
deleteoldrepo
install
updateversion
cleanup
diffstat

