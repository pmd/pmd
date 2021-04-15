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
    pushd docs

    echo -e "\n\n"
    pmd_ci_log_info "Building documentation using jekyll..."
    bundle config set --local path vendor/bundle
    bundle install
    bundle exec jekyll build

    popd
}

#
# Creates the pmd-doc.zip archive. It will be placed in "docs/".
#
function pmd_doc_create_archive() {
    pushd docs

    echo -e "\n\n"
    pmd_ci_log_info "Creating pmd-doc archive..."
    mv _site pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}
    zip -qr pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}.zip pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/
    pmd_ci_log_success "Successfully created pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"

    popd
}

#
# Publishes the site to https://pmd.github.io/pmd-${PMD_CI_MAVEN_PROJECT_VERSION} and
# https://pmd.github.io/latest/
#
function publish_release_documentation_github() {
    echo -e "\n\n"
    pmd_ci_log_info "Adding the new doc to pmd.github.io..."
    # clone pmd.github.io. Note: This uses the ssh key setup earlier
    # In order to speed things up, we use a sparse checkout - no need to checkout all directories here
    mkdir pmd.github.io
    (
        cd pmd.github.io
        git init
        git config user.name "PMD CI (pmd-bot)"
        git config user.email "andreas.dangel+pmd-bot@adangel.org"
        git config core.sparsecheckout true
        git remote add origin git@github.com:pmd/pmd.github.io.git
        echo "/latest/" > .git/info/sparse-checkout
        echo "/sitemap.xml" >> .git/info/sparse-checkout
        git pull --depth=1 origin master
        pmd_ci_log_info "Copying documentation from ../docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/ to pmd-${PMD_CI_MAVEN_PROJECT_VERSION}/ ..."
        rsync -ah --stats ../docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/ pmd-${PMD_CI_MAVEN_PROJECT_VERSION}/
        git status
        pmd_ci_log_debug "Executing: git add pmd-${PMD_CI_MAVEN_PROJECT_VERSION}"
        git add pmd-${PMD_CI_MAVEN_PROJECT_VERSION}
        pmd_ci_log_debug "Executing: git commit..."
        git commit -q -m "Added pmd-${PMD_CI_MAVEN_PROJECT_VERSION}"

        pmd_ci_log_info "Copying pmd-${PMD_CI_MAVEN_PROJECT_VERSION} to latest ..."
        git rm -qr latest
        cp -a pmd-${PMD_CI_MAVEN_PROJECT_VERSION} latest
        pmd_ci_log_debug "Executing: git add latest"
        git add latest
        pmd_ci_log_debug "Executing: git commit..."
        git commit -q -m "Copying pmd-${PMD_CI_MAVEN_PROJECT_VERSION} to latest"

        pmd_ci_log_info "Generating sitemap.xml"
        ../docs/sitemap_generator.sh > sitemap.xml
        pmd_ci_log_debug "Executing: git add sitemap.xml"
        git add sitemap.xml
        pmd_ci_log_debug "Executing: git commit..."
        git commit -q -m "Generated sitemap.xml"

        pmd_ci_log_info "Executing: git push origin master"
        git push origin master
    )
}

#
# Updates github pages of the main repository,
# so that https://pmd.github.io/pmd/ has the latest (snapshot) content
#
function pmd_doc_publish_to_github_pages() {
    echo -e "\n\n"
    pmd_ci_log_info "Pushing the new site to github pages..."
    git clone --branch gh-pages --depth 1 git@github.com:pmd/pmd.git pmd-gh-pages
    # clear the files first
    rm -rf pmd-gh-pages/*
    # copy the new site
    cp -a docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/* pmd-gh-pages/
    (
        cd pmd-gh-pages
        git config user.name "PMD CI (pmd-bot)"
        git config user.email "andreas.dangel+pmd-bot@adangel.org"
        git add -A
        MSG="Update documentation

${PMD_CI_JOB_URL}
${PMD_CI_PUSH_COMMIT_COMPARE}"
        git commit -q -m "$MSG"
        git push git@github.com:pmd/pmd.git HEAD:gh-pages
        pmd_ci_log_success "Successfully pushed site to https://pmd.github.io/pmd/"
    )
}
