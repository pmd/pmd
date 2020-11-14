#!/usr/bin/env bash

source $(dirname $0)/inc/logger.inc
source $(dirname $0)/inc/setup-secrets.inc
source $(dirname $0)/inc/sourceforge-api.inc
source $(dirname $0)/inc/pmd-doc.inc
source $(dirname $0)/inc/pmd-code-api.inc
source $(dirname $0)/inc/regression-tester.inc
source $(dirname $0)/inc/github-releases-api.inc
source $(dirname $0)/inc/maven-dependencies.inc
source $(dirname $0)/inc/install-openjdk.inc

set -e

function pmd_ci_build_main() {
    log_group_start "Setting up private secrets"
        pmd_ci_setup_private_env
        pmd_ci_setup_gpg_key
        pmd_ci_setup_ssh
    log_group_end

    log_group_start "Prepare Java 7+11, Maven, Bundler"
        install_openjdk_setdefault 11
        install_oraclejdk7
        pmd_ci_build_setup_maven
        pmd_ci_build_setup_bundler
        pmd_ci_build_setup_env
    log_group_end

    log_group_start "Downloading maven dependencies"
        maven_dependencies_resolve
    log_group_end

    log_group_start "Build and Deploy"
        pmd_ci_build_run
        pmd_ci_deploy_build_artifacts
    log_group_end

    log_group_start "Build and Upload documentation"
        pmd_ci_build_and_upload_doc
    log_group_end

    if pmd_ci_build_isRelease; then
    log_group_start "Publishing Release"
        gh_release_publishRelease "$GH_RELEASE"
        sourceforge_selectDefault "${VERSION}"
    log_group_end
    fi

    log_group_start "Creating new baseline for regression tester"
        regression_tester_setup_ci
        regression_tester_uploadBaseline
    log_group_end

    exit 0
}


#
# Configures maven.
# Needed for deploy to central (both snapshots and releases)
# and for signing the artifacts.
#
function pmd_ci_build_setup_maven() {
    mkdir -p ${HOME}/.m2
    cp .ci/files/maven-settings.xml ${HOME}/.m2/settings.xml
}

#
# Installs bundler, which is needed for doc generation and regression tester
#
function pmd_ci_build_setup_bundler() {
    log_info "Installing bundler..."
    gem install bundler
}

#
# Setups common build parameters:
# * Determines the VERSION of PMD, that is being built
# * Determines the PMD_CI_BRANCH or PMD_CI_TAG, that is being built
#
function pmd_ci_build_setup_env() {
    VERSION=$(pmd_ci_build_get_pom_version)

    if [[ "${PMD_CI_GIT_REF}" == refs/heads/* ]]; then
        PMD_CI_BRANCH=${PMD_CI_GIT_REF##refs/heads/}
        unset PMD_CI_TAG
        log_info "Building PMD ${VERSION} on branch ${PMD_CI_BRANCH}"
    elif [[ "${PMD_CI_GIT_REF}" == refs/tags/* ]]; then
        unset PMD_CI_BRANCH
        PMD_CI_TAG=${PMD_CI_GIT_REF##refs/tags/}
        log_info "Building PMD ${VERSION} on tag ${PMD_CI_TAG}"
    else
        log_error "Unknown branch/tag: PMD_CI_GIT_REF=${PMD_CI_GIT_REF}"
        exit 1
    fi

    if [[ "${VERSION}" == *-SNAPSHOT && -z "$PMD_CI_BRANCH" ]]; then
        log_error "Invalid combination: snapshot version ${VERSION} but no branch in PMD_CI_GIT_REF=${PMD_CI_GIT_REF}"
        exit 1
    fi

    if [[ "${VERSION}" != *-SNAPSHOT && -z "$PMD_CI_TAG" ]]; then
        log_error "Invalid combination: non-snapshot version ${VERSION} but no tag in PMD_CI_GIT_REF=${PMD_CI_GIT_REF}"
        exit 1
    fi
}

#
# Performs the actual build.
# Deploys the artifacts to maven central.
# Also generates rule documentation.
#
function pmd_ci_build_run() {
    local mvn_profiles="ossrh,sign,generate-rule-docs"

    if pmd_ci_build_isRelease; then
        log_info "This is a release build"
        mvn_profiles="${mvn_profiles},pmd-release"
    else
        log_info "This is a snapshot build"
    fi

    ./mvnw clean deploy -P${mvn_profiles} -e -V -Djava7.home=${HOME}/oraclejdk7
}

#
# Deploys the binary distribution
#
function pmd_ci_deploy_build_artifacts() {
    # Deploy to sourceforge files
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-bin-${VERSION}.zip"
    sourceforge_uploadFile "${VERSION}" "pmd-dist/target/pmd-src-${VERSION}.zip"

    if pmd_ci_build_isRelease; then
        # create a draft github release
        gh_releases_createDraftRelease "${PMD_CI_TAG}" "$(git rev-list -n 1 ${PMD_CI_TAG})"
        GH_RELEASE="$RESULT"

        # Deploy to github releases
        gh_release_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-bin-${VERSION}.zip"
        gh_release_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-src-${VERSION}.zip"
    fi
}

#
# Builds and uploads the documentation site
#
function pmd_ci_build_and_upload_doc() {
    pmd_doc_generate_jekyll_site
    pmd_doc_create_archive

    sourceforge_uploadFile "${VERSION}" "docs/pmd-doc-${VERSION}.zip"
    if pmd_ci_build_isRelease; then
        gh_release_uploadAsset "$GH_RELEASE" "docs/pmd-doc-${VERSION}.zip"
    fi

    # Deploy doc to https://docs.pmd-code.org/pmd-doc-${VERSION}/
    pmd_code_uploadDocumentation "${VERSION}" "docs/pmd-doc-${VERSION}.zip"
    # Deploy javadoc to https://docs.pmd-code.org/apidocs/*/${VERSION}/
    pmd_code_uploadJavadoc "${VERSION}" "$(pwd)"

    if [[ "${VERSION}" == *-SNAPSHOT && "${PMD_CI_BRANCH}" == "master" ]]; then
        # only for snapshot builds from branch master
        pmd_code_createSymlink "${VERSION}" "snapshot"

        # update github pages https://pmd.github.io/pmd/
        pmd_doc_publish_to_github_pages
        # rsync site to https://pmd.sourceforge.io/snapshot
        sourceforge_rsyncSnapshotDocumentation "${VERSION}" "snapshot"
    fi

    if pmd_ci_build_isRelease; then
        # documentation is already uploaded to https://docs.pmd-code.org/pmd-doc-${VERSION}
        # we only need to setup symlinks for the released version
        pmd_code_createSymlink "${VERSION}" "latest"
        # remove old doc and point to the new version
        pmd_code_removeDocumentation "${VERSION}-SNAPSHOT"
        pmd_code_createSymlink "${VERSION}" "${VERSION}-SNAPSHOT"
        # remove old javadoc
        pmd_code_removeJavadoc "${VERSION}-SNAPSHOT"

        # updating github release text
        # renders, and skips the first 6 lines - the Jekyll front-matter
        local rendered_release_notes=$(bundle exec .ci/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
        local release_name="PMD ${VERSION} ($(date -u +%d-%B-%Y))"
        gh_release_updateRelease "$GH_RELEASE" "$release_name" "$rendered_release_notes"
        sourceforge_uploadReleaseNotes "${VERSION}" "${rendered_release_notes}"

        publish_release_documentation_github
        sourceforge_rsyncSnapshotDocumentation "${VERSION}" "pmd-${VERSION}"
    fi
}


function pmd_ci_build_isRelease() {
    if [[ "${VERSION}" != *-SNAPSHOT && -n "${PMD_CI_TAG}" && -z "${PMD_CI_BRANCH}" ]]; then
        return 0
    else
        return 1
    fi
}

function pmd_ci_build_get_pom_version() {
    echo $(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:3.0.0:exec)
}


pmd_ci_build_main
