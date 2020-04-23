#!/usr/bin/env bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh
source .travis/github-releases-api.sh
source .travis/sourceforge-api.sh
source .travis/pmd-code-api.sh

function main() {
    VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
    log_info "Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"

    #
    # First step: build pmd with profile "generate-rule-docs"
    # The docs should appear under "docs/pages/rules/..." for each language
    # With this profile, also the checks are executed (e.g. DeadLinksChecker).
    #
    ./mvnw clean verify -Dmaven.test.skip=true -P generate-rule-docs

    if ! travis_isPush; then
        log_info "Not publishing site, since this is not a push!"
        exit 0
    fi

    #
    # For pushes, we'll update the online documentation
    #
    generate_jekyll_doc
    create_pmd-doc_archive

    # Deploy to sourceforge files
    sourceforge_uploadFile "${VERSION}" "docs/pmd-doc-${VERSION}.zip"

    # Deploy doc to https://docs.pmd-code.org/pmd-doc-${VERSION}/
    pmd_code_uploadDocumentation "${VERSION}" "docs/pmd-doc-${VERSION}.zip"
    # Deploy javadoc to https://docs.pmd-code.org/apidocs/*/${VERSION}/
    pmd_code_uploadJavadoc "${VERSION}" "$(pwd)"


    if [[ "${VERSION}" == *-SNAPSHOT && "${TRAVIS_BRANCH}" == "master" ]]; then
        # only for snapshot builds from branch master

        pmd_code_createSymlink "${VERSION}" "snapshot"

        # update github pages https://pmd.github.io/pmd/
        publish_to_github_pages
        # rsync site to https://pmd.sourceforge.io/snapshot
        sourceforge_rsyncSnapshotDocumentation "${VERSION}" "snapshot"
    fi


    if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
        log_info "This is a release documentation build for pmd ${VERSION}"

        # documentation is already uploaded to https://docs.pmd-code.org/pmd-doc-${VERSION}
        pmd_code_createSymlink "${VERSION}" "latest"
        # remove old doc and point to the new version
        pmd_code_removeDocumentation "${VERSION}-SNAPSHOT"
        pmd_code_createSymlink "${VERSION}" "${VERSION}-SNAPSHOT"
        # remove old javadoc
        pmd_code_removeJavadoc "${VERSION}-SNAPSHOT"

        # Deploy to github releases
        gh_releases_getLatestDraftRelease
        GH_RELEASE="$RESULT"

        gh_release_uploadAsset "$GH_RELEASE" "docs/pmd-doc-${VERSION}.zip"

        # updating github release text
        # renders, and skips the first 6 lines - the Jekyll front-matter
        RENDERED_RELEASE_NOTES=$(bundle exec .travis/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
        RELEASE_NAME="PMD ${VERSION} ($(date -u +%d-%B-%Y))"
        gh_release_updateRelease "$GH_RELEASE" "$RELEASE_NAME" "$RENDERED_RELEASE_NOTES"
        sourceforge_uploadReleaseNotes "${VERSION}" "${RENDERED_RELEASE_NOTES}"

        publish_release_documentation_github
        sourceforge_rsyncSnapshotDocumentation "${VERSION}" "pmd-${VERSION}"
    fi
}

#
# Executes jekyll and generates the documentation
# The documentation will be generated in the directory "docs/_site".
#
function generate_jekyll_doc() {
    pushd docs

    echo -e "\n\n"
    log_info "Building documentation using jekyll..."
    bundle install
    bundle exec jekyll build

    popd
}

#
# Creates the pmd-doc.zip archive. It will be placed in "docs/".
#
function create_pmd-doc_archive() {
    pushd docs

    echo -e "\n\n"
    log_info "Creating pmd-doc archive..."
    mv _site pmd-doc-${VERSION}
    zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/
    log_success "Successfully created pmd-doc-${VERSION}.zip"

    popd
}

#
# Publishes the site to https://pmd.github.io/pmd-${VERSION} and
# https://pmd.github.io/latest/
#
function publish_release_documentation_github() {
    echo -e "\n\n"
    log_info "Adding the new doc to pmd.github.io..."
    # clone pmd.github.io. Note: This uses the ssh key setup earlier
    # In order to speed things up, we use a sparse checkout - no need to checkout all directories here
    mkdir pmd.github.io
    (
        cd pmd.github.io
        git init
        git config user.name "Travis CI (pmd-bot)"
        git config user.email "andreas.dangel+pmd-bot@adangel.org"
        git config core.sparsecheckout true
        git remote add origin git@github.com:pmd/pmd.github.io.git
        echo "/latest/" > .git/info/sparse-checkout
        echo "/sitemap.xml" >> .git/info/sparse-checkout
        git pull --depth=1 origin master
        log_info "Copying documentation from ../docs/pmd-doc-${VERSION}/ to pmd-${VERSION}/ ..."
        rsync -ah --stats ../docs/pmd-doc-${VERSION}/ pmd-${VERSION}/
        git status
        echo "Executing: git add pmd-${VERSION}"
        git add pmd-${VERSION}
        echo "Executing: git commit..."
        git commit -q -m "Added pmd-${VERSION}"

        log_info "Copying pmd-${VERSION} to latest ..."
        git rm -qr latest
        cp -a pmd-${VERSION} latest
        echo "Executing: git add latest"
        git add latest
        echo "Executing: git commit..."
        git commit -q -m "Copying pmd-${VERSION} to latest"

        log_info "Generating sitemap.xml"
        ../.travis/sitemap_generator.sh > sitemap.xml
        echo "Executing: git add sitemap.xml"
        git add sitemap.xml
        echo "Executing: git commit..."
        git commit -q -m "Generated sitemap.xml"

        echo "Executing: git push origin master"
        git push origin master
    )
}

#
# Updates github pages of the main repository,
# so that https://pmd.github.io/pmd/ has the latest (snapshot) content
#
function publish_to_github_pages() {
    echo -e "\n\n"
    log_info "Pushing the new site to github pages..."
    git clone --branch gh-pages --depth 1 git@github.com:pmd/pmd.git pmd-gh-pages
    # clear the files first
    rm -rf pmd-gh-pages/*
    # copy the new site
    cp -a docs/pmd-doc-${VERSION}/* pmd-gh-pages/
    (
        cd pmd-gh-pages
        git config user.name "Travis CI (pmd-bot)"
        git config user.email "andreas.dangel+pmd-bot@adangel.org"
        git add -A
        MSG="Update documentation

TRAVIS_JOB_NUMBER=${TRAVIS_JOB_NUMBER}
TRAVIS_COMMIT_RANGE=${TRAVIS_COMMIT_RANGE}"
        git commit -q -m "$MSG"
        git push git@github.com:pmd/pmd.git HEAD:gh-pages
        log_success "Successfully pushed site to https://pmd.github.io/pmd/"
    )
}

main
