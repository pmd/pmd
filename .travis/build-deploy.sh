#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh

function upload_baseline() {
    log_info "Generating and uploading baseline for pmdtester..."
    cd ..
    bundle config --local gemfile pmd/Gemfile
    pmd/.travis/travis_wait "bundle exec pmdtester -m single -r ./pmd -p ${TRAVIS_BRANCH} -pc ./pmd/.travis/all-java.xml -l ./pmd/.travis/project-list.xml -f"
    cd target/reports
    BRANCH_FILENAME="${TRAVIS_BRANCH/\//_}"
    zip -q -r ${BRANCH_FILENAME}-baseline.zip ${BRANCH_FILENAME}/
    ../../pmd/.travis/travis_wait "rsync -avh ${BRANCH_FILENAME}-baseline.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd-regression-tester/"
    if [ $? -ne 0 ]; then
        log_error "Error while uploading ${BRANCH_FILENAME}-baseline.zip to sourceforge!"
        log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd-regression-tester/"
        exit 1
    else
        log_success "Successfully uploaded ${BRANCH_FILENAME}-baseline.zip to sourceforge"
    fi
}

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
log_info "Building PMD ${VERSION} on branch ${TRAVIS_BRANCH}"

MVN_BUILD_FLAGS="-B -V"

if travis_isOSX; then

    log_info "The build is running on OSX"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isWindows; then

    log_info "The build is running on Windows"
    ./mvnw verify $MVN_BUILD_FLAGS

elif travis_isPullRequest; then

    log_info "This is a pull-request build"
    ./mvnw verify $MVN_BUILD_FLAGS
	(
            set +e
            # Create a corresponding remote branch locally
            if ! git show-ref --verify --quiet refs/heads/${TRAVIS_BRANCH}; then
                git fetch --no-tags origin +refs/heads/${TRAVIS_BRANCH}:refs/remotes/origin/${TRAVIS_BRANCH}
                git branch ${TRAVIS_BRANCH} origin/${TRAVIS_BRANCH}
            fi
            log_info "Running danger"
            bundle exec danger --verbose
	)

elif travis_isPush; then

    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        echo -e "\n\n"
        log_info "This is a release build for tag ${TRAVIS_TAG}"
        echo -e "\n\n"
        ./mvnw deploy -Possrh,sign,pmd-release $MVN_BUILD_FLAGS
        echo -e "\n\n"

        # Deploy to ossrh has already been done with the usual maven build

        # Deploy to github releases
        RELEASE_ID=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" 'https://api.github.com/repos/pmd/pmd/releases?per_page=1'|jq ".[0].id")
        RELEASE_DATA=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" "https://api.github.com/repos/pmd/pmd/releases/${RELEASE_ID}")
        DRAFT=$(echo "$RELEASE_DATA"|jq ".draft")
        if [ "$DRAFT" != "true" ]; then
            REQUEST=$(cat <<EOF
{
    "tag_name": "${TRAVIS_TAG}",
    "target_commitish": "$(git show-ref --hash HEAD)",
    "name": "${TRAVIS_TAG}",
    "draft": true
}
EOF
            )
            RELEASE_ID=$(curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/json" \
                --data "${REQUEST}" -X POST https://api.github.com/repos/pmd/pmd/releases | jq ".id")
            echo $RELEASE_ID
            log_info "Created draft release with id ${RELEASE_ID}"
        else
            log_info "Using draft release with id "${RELEASE_ID}"
        fi
        UPLOAD_URL=$(echo "$RELEASE_DATA" | jq --raw-output ".upload_url")
        UPLOAD_URL=${UPLOAD_URL%%\{\?name,label\}}
        echo "Uploading pmd-bin-${VERSION}.zip ..."
        curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/zip" \
            --data-binary "@pmd-dist/target/pmd-bin-${VERSION}.zip" \
            -X POST "${UPLOAD_URL}?name=pmd-bin-${VERSION}.zip"
        echo "Uploading pmd-src-${VERSION}.zip ..."
        curl -s -H "Authorization: token ${GITHUB_OAUTH_TOKEN}" -H "Content-Type: application/zip" \
            --data-binary "@pmd-dist/target/pmd-src-${VERSION}.zip" \
            -X POST "${UPLOAD_URL}?name=pmd-src-${VERSION}.zip"


        # Deploy to sourceforge files
        (
            # disable fast fail, exit immediately, in this subshell
            set +e

            echo -e "\n\n"
            log_info "Uploading pmd distribution to sourceforge..."
            .travis/travis_wait "rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/"
            if [ $? -ne 0 ]; then
                log_error "Error while uploading pmd-*-${VERSION}.zip to sourceforge!"
                log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
                exit 1
            else
                log_success "Successfully uploaded pmd-*-${VERSION}.zip to sourceforge"
            fi

            true
        )


    elif [[ "${VERSION}" == *-SNAPSHOT ]]; then
        log_info "This is a snapshot build"
        ./mvnw deploy -Possrh,sign $MVN_BUILD_FLAGS
    else
        # other build. Can happen during release: the commit with a non snapshot version is built, but not from the tag.
        log_info "This is some other build, probably during release: commit with a non-snapshot version on branch master..."
        ./mvnw verify $MVN_BUILD_FLAGS
        # we stop here - no need to execute further steps
        exit 0
    fi

    # Deploy to sourceforge files
    (
        # disable fast fail, exit immediately, in this subshell
        set +e

        echo -e "\n\n"
        log_info "Uploading pmd distribution to sourceforge..."
        .travis/travis_wait "rsync -avh pmd-dist/target/pmd-*-${VERSION}.zip ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/"
        if [ $? -ne 0 ]; then
            log_error "Error while uploading pmd-*-${VERSION}.zip to sourceforge!"
            log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
            exit 1
        else
            log_success "Successfully uploaded pmd-*-${VERSION}.zip to sourceforge"
        fi

        if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
            # Since this is a release, making the binary the new default file...
            log_info "Selecting pmd-bin-${VERSION} as default on sourceforge.net..."
            curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
                 -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${VERSION}/pmd-bin-${VERSION}.zip
            if [ $? -ne 0 ]; then
                log_error "Couldn't select latest binary as default on sourceforge.net"
            else
                log_info "pmd-bin-${VERSION} is now the default download option."
            fi
        fi
    )
    (   # UPLOAD RELEASE NOTES TO SOURCEFORGE

        # This handler is called if any command fails
        function release_notes_fail() {
            log_error "Error while uploading release_notes.md as ReadMe.md to sourceforge!"
            log_error "Please upload manually: https://sourceforge.net/projects/pmd/files/pmd/"
        }

        # exit subshell after trap
        set -e
        trap release_notes_fail ERR

        RELEASE_NOTES_TMP=$(mktemp -t)

        bundle exec .travis/render_release_notes.rb docs/pages/release_notes.md | tail -n +6 > "$RELEASE_NOTES_TMP"

        rsync -avh "$RELEASE_NOTES_TMP" ${PMD_SF_USER}@web.sourceforge.net:/home/frs/project/pmd/pmd/${VERSION}/ReadMe.md

        log_success "Successfully uploaded release_notes.md as ReadMe.md to sourceforge"

    )


    (
        # disable fast fail, exit immediately, in this subshell
        set +e

        upload_baseline
    )

else
    log_info "This is neither a pull request nor a push. Not executing any build."
    exit 1
fi
