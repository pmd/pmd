#
# Executes jekyll and generates the documentation
# The documentation will be generated in the directory "docs/_site".
#
function pmd_doc_generate_jekyll_site() {
    pushd docs

    echo -e "\n\n"
    log_info "Building documentation using jekyll..."
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
function pmd_doc_publish_to_github_pages() {
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
