#!/bin/bash
set -e

source .travis/logger.sh
source .travis/common-functions.sh
source .travis/github-releases-api.sh
source .travis/sourceforge-api.sh

VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
log_info "Building PMD Documentation ${VERSION} on branch ${TRAVIS_BRANCH}"

#
# First step: build pmd with profile "generate-rule-docs"
# The docs should appear under "docs/pages/rules/..." for each language
#
./mvnw clean verify -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -P generate-rule-docs



if ! travis_isPush; then
    log_info "Not publishing site, since this is not a push!"
    exit 0
fi



pushd docs

# run jekyll
echo -e "\n\n"
log_info "Building documentation using jekyll..."
bundle install
bundle exec jekyll build

# create pmd-doc archive
echo -e "\n\n"
log_info "Creating pmd-doc archive..."
mv _site pmd-doc-${VERSION}
zip -qr pmd-doc-${VERSION}.zip pmd-doc-${VERSION}/
log_success "Successfully created pmd-doc-${VERSION}.zip:"
ls -lh pmd-doc-${VERSION}.zip

popd

if [[ "${VERSION}" != *-SNAPSHOT && "${TRAVIS_TAG}" != "" ]]; then
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

    sourceforge_rsyncSnapshotDocumentation "${VERSION}" "pmd-${VERSION}"
fi


# Deploy to sourceforge files
sourceforge_uploadFile "${VERSION}" "docs/pmd-doc-${VERSION}.zip"
# rsync site to https://pmd.sourceforge.io/snapshot
if [[ "${VERSION}" == *-SNAPSHOT && "${TRAVIS_BRANCH}" == "master" ]]; then
    sourceforge_rsyncSnapshotDocumentation "${VERSION}" "snapshot"
fi



#
# Push the generated site to gh-pages branch
# only for snapshot builds from branch master
#
if [[ "${VERSION}" == *-SNAPSHOT && "${TRAVIS_BRANCH}" == "master" ]]; then
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
fi

