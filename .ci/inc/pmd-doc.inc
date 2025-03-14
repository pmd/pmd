#!/usr/bin/env bash

MODULE="pmd-doc"
SCRIPT_INCLUDES="log.bash"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

# Used env vars:
# PMD_CI_JOB_URL
# PMD_CI_PUSH_COMMIT_COMPARE


#
# Executes jekyll and generates the documentation
# The documentation will be generated in the directory "docs/_site".
#
function pmd_doc_generate_jekyll_site() {
    pushd docs || { echo "Directory 'docs' doesn't exist"; exit 1; }

    echo -e "\n\n"
    pmd_ci_log_info "Building documentation using jekyll..."
    bundle config set --local path vendor/bundle
    bundle install
    bundle exec jekyll build

    popd || exit 1
}

#
# Creates the pmd-doc.zip archive. It will be placed in "docs/".
#
function pmd_doc_create_archive() {
    pushd docs || { echo "Directory 'docs' doesn't exist"; exit 1; }

    echo -e "\n\n"
    pmd_ci_log_info "Creating pmd-doc archive..."
    mv _site "pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}"
    zip -qr "pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-doc.zip" "pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/"
    pmd_ci_log_success "Successfully created pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-doc.zip"

    popd || exit 1
}

#
# Updates github pages branch "gh-pages" of the main repository,
# so that https://pmd.github.io/pmd/ has the latest (snapshot) content
#
function pmd_doc_publish_to_github_pages() {
    echo -e "\n\n"
    pmd_ci_log_info "Pushing the new site to github pages..."
    git clone --branch gh-pages --depth 1 --origin origin https://github.com/pmd/pmd.git pmd-gh-pages
    # clear the files first
    rm -rf pmd-gh-pages/*
    # copy the new site
    cp -a "docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}"/* pmd-gh-pages/
    (
        cd pmd-gh-pages || { echo "Directory 'pmd-gh-pages' doesn't exist"; exit 1; }
        git config user.name "PMD CI (pmd-bot)"
        git config user.email "pmd-bot@users.noreply.github.com"
        git config --local http.https://github.com/.extraheader "AUTHORIZATION: basic $(echo -n "x-access-token:${GITHUB_TOKEN}"|base64)"
        git add -A
        MSG="Update documentation

${PMD_CI_JOB_URL}
${PMD_CI_PUSH_COMMIT_COMPARE}"
        git commit -q -m "$MSG"
        git push origin HEAD:gh-pages
        git config --local --unset-all http.https://github.com/.extraheader
        pmd_ci_log_success "Successfully pushed site to https://pmd.github.io/pmd/"
    )
}
