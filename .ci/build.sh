#!/usr/bin/env bash

# Exit this script immediately if a command/function exits with a non-zero status.
set -e

SCRIPT_INCLUDES="log.bash utils.bash setup-secrets.bash openjdk.bash maven.bash github-releases-api.bash
                 sourceforge-api.bash pmd-doc.inc pmd-code-api.inc regression-tester.inc"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

function build() {
    pmd_ci_log_group_start "Prepare Java 7+11, Bundler"
        pmd_ci_openjdk_install_adoptopenjdk 11
        pmd_ci_openjdk_setdefault 11
        PMD_MAVEN_EXTRA_OPTS=()
        if [ "$(pmd_ci_utils_get_os)" = "linux" ]; then
            pmd_ci_log_info "Install openjdk7 for integration tests"
            pmd_ci_openjdk_install_zuluopenjdk 7
            PMD_MAVEN_EXTRA_OPTS=(-Djava7.home="${HOME}/openjdk7")
        fi
        pmd_ci_build_setup_bundler
    pmd_ci_log_group_end

    echo
    pmd_ci_maven_display_info_banner
    pmd_ci_utils_determine_build_env pmd/pmd
    echo

    if pmd_ci_utils_is_fork_or_pull_request; then
        pmd_ci_log_group_start "Build with mvnw"
            ./mvnw clean verify --show-version --errors --batch-mode --no-transfer-progress "${PMD_MAVEN_EXTRA_OPTS[@]}"
        pmd_ci_log_group_end

        # Danger is executed only on the linux runner
        if [ "$(pmd_ci_utils_get_os)" = "linux" ]; then
            pmd_ci_log_group_start "Executing danger"
                regression_tester_setup_ci
                regression_tester_executeDanger
            pmd_ci_log_group_end
        fi

        exit 0
    fi

    # stop early for invalid maven version and branch/tag combination
    pmd_ci_maven_verify_version || exit 0

    if [ "$(pmd_ci_utils_get_os)" != "linux" ]; then
        pmd_ci_log_group_start "Build with mvnw"
            ./mvnw clean verify --show-version --errors --batch-mode --no-transfer-progress "${PMD_MAVEN_EXTRA_OPTS[@]}"
        pmd_ci_log_group_end

        pmd_ci_log_info "Stopping build here, because os is not linux"
        exit 0
    fi

    # only builds on pmd/pmd continue here
    pmd_ci_log_group_start "Setup environment"
        pmd_ci_setup_secrets_private_env
        pmd_ci_setup_secrets_gpg_key
        pmd_ci_setup_secrets_ssh
        pmd_ci_maven_setup_settings
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Build and Deploy"
        pmd_ci_build_run
        pmd_ci_deploy_build_artifacts
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Build and Upload documentation"
        pmd_ci_build_and_upload_doc
    pmd_ci_log_group_end

    if pmd_ci_maven_isReleaseBuild; then
    pmd_ci_log_group_start "Publishing Release"
        pmd_ci_gh_releases_publishRelease "$GH_RELEASE"
        pmd_ci_sourceforge_selectDefault "${PMD_CI_MAVEN_PROJECT_VERSION}"
        pmd_ci_sourceforge_publishBlogPost "$SF_BLOG_URL"
    pmd_ci_log_group_end
    fi

    pmd_ci_log_group_start "Creating new baseline for regression tester"
        regression_tester_setup_ci
        regression_tester_uploadBaseline
    pmd_ci_log_group_end

    if pmd_ci_maven_isSnapshotBuild; then
    if [ "${PMD_CI_MAVEN_PROJECT_VERSION}" != "7.0.0-SNAPSHOT" ]; then
        pmd_ci_log_group_start "Executing PMD dogfood test with ${PMD_CI_MAVEN_PROJECT_VERSION}"
            ./mvnw versions:set -DnewVersion=${PMD_CI_MAVEN_PROJECT_VERSION}-dogfood -DgenerateBackupPoms=false
            ./mvnw verify --show-version --errors --batch-mode --no-transfer-progress "${PMD_MAVEN_EXTRA_OPTS[@]}" \
                -DskipTests \
                -Dmaven.javadoc.skip=true \
                -Dmaven.source.skip=true \
                -Dcheckstyle.skip=true \
                -Ppmd-dogfood \
                -Dpmd.dogfood.version=${PMD_CI_MAVEN_PROJECT_VERSION}
            ./mvnw versions:set -DnewVersion=${PMD_CI_MAVEN_PROJECT_VERSION} -DgenerateBackupPoms=false
        pmd_ci_log_group_end
    else
        # current maven-pmd-plugin is not compatible with PMD 7 yet.
        pmd_ci_log_info "Skipping PMD dogfood test with ${PMD_CI_MAVEN_PROJECT_VERSION}"
    fi

    pmd_ci_log_group_start "Executing build with sonar"
        # Note: Sonar also needs GITHUB_TOKEN (!)
        ./mvnw \
            -Dmaven.javadoc.skip=true \
            -Dmaven.source.skip \
            -Dcheckstyle.skip \
            -Dpmd.skip \
            --show-version --errors --batch-mode --no-transfer-progress \
            clean package \
            sonar:sonar -Dsonar.login="${SONAR_TOKEN}" -Psonar
        pmd_ci_log_success "New sonar results: https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd"
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Executing build with coveralls"
        export CI_NAME="github actions"
        export CI_BUILD_URL="${PMD_CI_JOB_URL}"
        export CI_BRANCH="${PMD_CI_BRANCH}"
        ./mvnw \
            -Dmaven.javadoc.skip=true \
            -Dmaven.source.skip \
            -Dcheckstyle.skip \
            -Dpmd.skip \
            -DrepoToken="${COVERALLS_REPO_TOKEN}" \
            --show-version --errors --batch-mode --no-transfer-progress \
            clean package jacoco:report \
            coveralls:report -Pcoveralls
        pmd_ci_log_success "New coveralls result: https://coveralls.io/github/pmd/pmd"
    pmd_ci_log_group_end
    fi
}


#
# Installs bundler, which is needed for doc generation and regression tester
#
function pmd_ci_build_setup_bundler() {
    pmd_ci_log_info "Installing bundler..."
    gem install bundler
}

#
# Performs the actual build.
# Deploys the artifacts to maven central.
# Also generates rule documentation.
#
function pmd_ci_build_run() {
    local mvn_profiles="sign,generate-rule-docs"

    if pmd_ci_maven_isReleaseBuild; then
        pmd_ci_log_info "This is a release build"
        mvn_profiles="${mvn_profiles},pmd-release"
    else
        pmd_ci_log_info "This is a snapshot build"
    fi

    ./mvnw clean deploy -P${mvn_profiles} --show-version --errors --batch-mode --no-transfer-progress "${PMD_MAVEN_EXTRA_OPTS[@]}"
}

#
# Deploys the binary distribution
#
function pmd_ci_deploy_build_artifacts() {
    # Deploy to sourceforge files
    pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-bin-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
    pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-src-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"

    if pmd_ci_maven_isReleaseBuild; then
        # create a draft github release
        pmd_ci_gh_releases_createDraftRelease "${PMD_CI_TAG}" "$(git rev-list -n 1 "${PMD_CI_TAG}")"
        GH_RELEASE="$RESULT"

        # Deploy to github releases
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-bin-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-src-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
    fi
}

#
# Builds and uploads the documentation site
#
function pmd_ci_build_and_upload_doc() {
    pmd_doc_generate_jekyll_site
    pmd_doc_create_archive

    pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
    if pmd_ci_maven_isReleaseBuild; then
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
    fi

    # Deploy doc to https://docs.pmd-code.org/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/
    pmd_code_uploadDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "docs/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}.zip"
    # Deploy javadoc to https://docs.pmd-code.org/apidocs/*/${PMD_CI_MAVEN_PROJECT_VERSION}/
    pmd_code_uploadJavadoc "${PMD_CI_MAVEN_PROJECT_VERSION}" "$(pwd)"

    if pmd_ci_maven_isSnapshotBuild && [ "${PMD_CI_BRANCH}" = "master" ]; then
        # only for snapshot builds from branch master
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "snapshot"

        # update github pages https://pmd.github.io/pmd/
        pmd_doc_publish_to_github_pages
        # rsync site to https://pmd.sourceforge.io/snapshot
        pmd_ci_sourceforge_rsyncSnapshotDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "snapshot"
    fi

    if pmd_ci_maven_isReleaseBuild; then
        # documentation is already uploaded to https://docs.pmd-code.org/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}
        # we only need to setup symlinks for the released version
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "latest"
        # remove old doc and point to the new version
        pmd_code_removeDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"
        # remove old javadoc
        pmd_code_removeJavadoc "${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"

        # updating github release text
        rm -f .bundle/config
        bundle config set --local path vendor/bundle
        bundle config set --local with release_notes_preprocessing
        bundle install
        # renders, and skips the first 6 lines - the Jekyll front-matter
        local rendered_release_notes
        rendered_release_notes=$(bundle exec docs/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
        local release_name
        release_name="PMD ${PMD_CI_MAVEN_PROJECT_VERSION} ($(date -u +%d-%B-%Y))"
        pmd_ci_gh_releases_updateRelease "$GH_RELEASE" "$release_name" "${rendered_release_notes}"
        pmd_ci_sourceforge_uploadReleaseNotes "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "${rendered_release_notes}"

        local rendered_release_notes_with_links
        rendered_release_notes_with_links="
*   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${PMD_CI_MAVEN_PROJECT_VERSION}
*   Documentation: https://pmd.github.io/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}/

${rendered_release_notes}"
        pmd_ci_sourceforge_createDraftBlogPost "${release_name} released" "${rendered_release_notes_with_links}" "pmd,release"
        SF_BLOG_URL="${RESULT}"

        # updates https://pmd.github.io/latest/ and https://pmd.github.io/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}
        publish_release_documentation_github
        pmd_ci_sourceforge_rsyncSnapshotDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-${PMD_CI_MAVEN_PROJECT_VERSION}"
    fi
}

build

exit 0
