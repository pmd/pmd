#!/bin/bash
set -e

source .travis/common-functions.sh
source .travis/colors.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec | tail -1)
echo_yellow "[ INFO] Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"

if ! travis_isPush; then
    echo_yellow "[ INFO] Not building site, since this is not a push!"
    exit 0
fi

pushd docs

# run jekyll
echo -e "\n\n"
echo_yellow "[ INFO] Building documentation using jekyll..."
bundle install
bundle exec jekyll build

# create pmd-doc archive
echo -e "\n\n"
echo_yellow "[ INFO] Creating pmd-doc archive..."
mv _site pmd-doc-${VERSION}
zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/

(
    # disable fast fail, exit immediately, in this subshell
    set +e

    if [[ "${TRAVIS_TAG}" != "" || "${VERSION}" == *-SNAPSHOT ]]; then
        echo -e "\n\n"
        echo_yellow "[ INFO] Uploading pmd doc distribution to sourceforge..."
        rsync -avh pmd-doc-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/
        if [ $? -ne 0 ]; then
            echo_red "[ERROR] Couldn't upload pmd-doc-${VERSION}.zip!"
            echo_red "[ERROR] Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        fi
    fi

    # rsync site to pmd.sourceforge.net/snapshot
    if [[ "${VERSION}" == *-SNAPSHOT && "${TRAVIS_BRANCH}" == "master" ]]; then
        echo -e "\n\n"
        echo_yellow "[ INFO] Uploading snapshot site to pmd.sourceforge.net/snapshot..."
        travis_wait rsync -ah --stats --delete pmd-doc-${VERSION}/ ${PMD_SF_USER}@web.sourceforge.net:/home/project-web/pmd/htdocs/snapshot/
        if [ $? -ne 0 ]; then
            echo_red "[ERROR] Couldn't upload the snapshot documentation. It won't be current on http://pmd.sourceforge.net/snapshot/"
        else
            echo_green "[ INFO] Successfully uploaded snapshot documentation: http://pmd.sourceforge.net/snapshot/"
        fi
    fi

    true
)


popd
