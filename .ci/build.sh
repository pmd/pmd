#!/usr/bin/env bash

# Exit this script immediately if a command/function exits with a non-zero status.
set -e

SCRIPT_INCLUDES="log.bash utils.bash setup-secrets.bash openjdk.bash maven.bash github-releases-api.bash
                 sourceforge-api.bash pmd-doc.inc pmd-code-api.inc regression-tester.inc"
# shellcheck source=inc/fetch_ci_scripts.bash
source "$(dirname "$0")/inc/fetch_ci_scripts.bash" && fetch_ci_scripts

function build() {
    pmd_ci_log_group_start "Prepare Java 8+11+17+21, Bundler"
        pmd_ci_openjdk_install_adoptium 11
        pmd_ci_openjdk_setdefault 11
        PMD_MAVEN_EXTRA_OPTS=()
        if [ "$(pmd_ci_utils_get_os)" = "linux" ]; then
            pmd_ci_log_info "Install openjdk8 for integration tests and pmd-regression-tests"
            pmd_ci_openjdk_install_adoptium 8
            pmd_ci_log_info "Install openjdk17 for integration tests and pmd-regression-tests"
            pmd_ci_openjdk_install_adoptium 17
            pmd_ci_log_info "Install openjdk21 for integration tests and pmd-regression-tests"
            pmd_ci_openjdk_install_adoptium 21
            PMD_MAVEN_EXTRA_OPTS=(
                -Djava8.home="${HOME}/openjdk8"
                -Djava17.home="${HOME}/openjdk17"
                -Djava21.home="${HOME}/openjdk21"
            )
        fi
        pmd_ci_build_setup_bundler
    pmd_ci_log_group_end

    echo
    pmd_ci_maven_display_info_banner
    pmd_ci_utils_determine_build_env pmd/pmd
    echo

    if pmd_ci_utils_is_fork_or_pull_request; then
        pmd_ci_log_group_start "Build with mvnw"
            ./mvnw clean install --show-version --errors --batch-mode  -Pgenerate-rule-docs "${PMD_MAVEN_EXTRA_OPTS[@]}"
        pmd_ci_log_group_end

        # Execute danger and dogfood only for pull requests in our own repository
        if [[ "${PMD_CI_IS_FORK}" = "false" && -n "${PMD_CI_PULL_REQUEST_NUMBER}" ]]; then
          # Danger is executed only on the linux runner
          if [ "$(pmd_ci_utils_get_os)" = "linux" ]; then
              pmd_ci_log_group_start "Executing danger"
                  regression_tester_setup_ci
                  regression_tester_executeDanger
              pmd_ci_log_group_end

              # also run dogfood for PRs (only on linux)
              pmd_ci_log_group_start "Executing PMD dogfood test with ${PMD_CI_MAVEN_PROJECT_VERSION}"
                  pmd_ci_dogfood
              pmd_ci_log_group_end
          fi
        fi

        exit 0
    fi

    # stop early for invalid maven version and branch/tag combination
    pmd_ci_maven_verify_version || exit 0

    # skip tests when doing a release build - this makes the process faster
    # it's a manual task now to verify that a release is only started, when the main branch
    # was green before. This is usually checked via a local build, see ./do-release.sh
    if pmd_ci_maven_isReleaseBuild; then
        PMD_MAVEN_EXTRA_OPTS+=(-DskipTests=true)
    fi

    # make sure, BUILD_CLI_DIST_ONLY is set to false by default
    pmd_ci_log_info "BUILD_CLI_DIST_ONLY=${BUILD_CLI_DIST_ONLY}"
    : "${BUILD_CLI_DIST_ONLY:=false}"
    pmd_ci_log_info "BUILD_CLI_DIST_ONLY=${BUILD_CLI_DIST_ONLY}"

    if [ "$(pmd_ci_utils_get_os)" != "linux" ]; then
        pmd_ci_log_group_start "Build with mvnw verify on $(pmd_ci_utils_get_os)"
        if pmd_ci_maven_isReleaseBuild; then
            pmd_ci_log_info "This is a release version build..."
            # There are two possible (release) builds:
            if [ "${BUILD_CLI_DIST_ONLY}" = "false" ]; then
                # a) everything without pmd-cli and pmd-dist
                ./mvnw clean verify -Dskip-cli-dist --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
            else
                # b) only pmd-cli and pmd-dist
                #
                # In the first stage build (without pmd-cli and pmd-dist), cyclonedx:makeAggregateBom tries to
                # fetch the jars of the to-be-released modules, which don't exist yet. This is recorded in *.lastUpdated
                # files in the local repo and might end up in the cache, that is used for this 2nd stage build.
                # Trying to delete the files now, if they exist.
                # Alternatively, we could run maven with flag "-U" to force update all dependencies...
                pmd_ci_log_info "Cleanup local maven repo..."
                find ~/.m2/repository -wholename "*/net/sourceforge/pmd/*/${PMD_CI_MAVEN_PROJECT_VERSION}/*.lastUpdated" | xargs rm -v
                pmd_ci_log_info "Cleanup local maven repo finished."

                ./mvnw clean verify -pl pmd-cli,pmd-dist --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
            fi
        else
            # snapshot build - just verify on the different OS
            ./mvnw clean verify --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
        fi
        pmd_ci_log_group_end

        pmd_ci_log_info "Stopping build here, because os is not linux"
        exit 0
    fi

    # only builds on pmd/pmd on linux continue here
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

    # release is published only for the case b) pmd-cli/pmd-dist release
    if pmd_ci_maven_isReleaseBuild && [ "${BUILD_CLI_DIST_ONLY}" = "true" ]; then
    pmd_ci_log_group_start "Publishing Release"
        pmd_ci_gh_releases_publishRelease "$GH_RELEASE"
        pmd_ci_sourceforge_selectDefault "${PMD_CI_MAVEN_PROJECT_VERSION}"
        # reconstruct the SF_BLOG_URL - the news entry has been created as draft when running the
        # first release stage
        local news_title
        news_title="PMD ${PMD_CI_MAVEN_PROJECT_VERSION} ($(date -u +%d-%B-%Y)) released"
        news_title="${news_title// /-}"
        news_title="${news_title//\./}"
        news_title="${news_title//\(/}"
        news_title="${news_title//\)/}"
        news_title="${news_title,,}" # convert to lowercase
        SF_BLOG_URL="https://sourceforge.net/rest/p/pmd/news/$(date -u +%Y)/$(date -u +%m)/${news_title// /_}"
        pmd_ci_sourceforge_publishBlogPost "$SF_BLOG_URL"
    pmd_ci_log_group_end
    fi

    # create a baseline for snapshot builds (when pmd-dist is built)
    # or for release builds for case b) when pmd-cli/pmd-dist is released
    if pmd_ci_maven_isSnapshotBuild || [ "${BUILD_CLI_DIST_ONLY}" = "true" ]; then
    pmd_ci_log_group_start "Creating new baseline for regression tester"
        regression_tester_setup_ci
        regression_tester_uploadBaseline
    pmd_ci_log_group_end
    fi

    #
    # everything from here runs only on snapshots, not on release builds
    #
    if pmd_ci_maven_isSnapshotBuild; then
    pmd_ci_log_group_start "Executing PMD dogfood test with ${PMD_CI_MAVEN_PROJECT_VERSION}"
        pmd_ci_dogfood
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Executing build with sonar"
        pmd_ci_openjdk_setdefault 17
        # Note: Sonar also needs GITHUB_TOKEN (!)
        ./mvnw \
            --show-version --errors --batch-mode \
            clean package \
            sonar:sonar -Dsonar.token="${SONAR_TOKEN}" -Psonar,fastSkip
        pmd_ci_log_success "New sonar results: https://sonarcloud.io/dashboard?id=net.sourceforge.pmd%3Apmd"
    pmd_ci_log_group_end

    pmd_ci_log_group_start "Executing build with coveralls"
        pmd_ci_openjdk_setdefault 11
        export CI_NAME="github actions"
        export CI_BUILD_URL="${PMD_CI_JOB_URL}"
        export CI_BRANCH="${PMD_CI_BRANCH}"
        # first create jacoco report
        ./mvnw \
            --show-version --errors --batch-mode \
            clean package \
            jacoco:report -Pcoveralls,fastSkip

        # workaround, maybe https://github.com/jacoco/jacoco/issues/654
        # we use $ as a regex separator, not a shell variable, so no expansion
        # shellcheck disable=SC2016
        sed -i 's$Comparisons.kt$ApexTreeBuilder.kt$g' pmd-apex/target/site/jacoco/jacoco.xml

        # then create and send coveralls report
        # note: generate-sources is needed, so that antlr4 generated directories are on the compileSourceRoots
        ./mvnw \
            --show-version --errors --batch-mode \
            generate-sources \
            coveralls:report -DrepoToken="${COVERALLS_REPO_TOKEN}" -Pcoveralls,fastSkip

        pmd_ci_log_success "New coveralls result: https://coveralls.io/github/pmd/pmd"
    pmd_ci_log_group_end
    fi
}


#
# Bundler should be already installed - it should be included in the ruby distribution.
# Bundler is needed for doc generation and regression tester
#
function pmd_ci_build_setup_bundler() {
    pmd_ci_log_info "Checking bundler version..."
    bundle --version
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

        # There are two possible (release) builds:
        if [ "${BUILD_CLI_DIST_ONLY}" = "false" ]; then
            # a) everything without pmd-cli and pmd-dist
            ./mvnw clean deploy -P"${mvn_profiles}" -Dskip-cli-dist --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
        else
            # b) only pmd-cli and pmd-dist
            ./mvnw clean deploy -P"${mvn_profiles}" -pl pmd-cli,pmd-dist --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
        fi
    else
        pmd_ci_log_info "This is a snapshot build"
        ./mvnw clean deploy -P"${mvn_profiles}" --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}"
    fi

}

#
# Deploys the binary distribution
#
function pmd_ci_deploy_build_artifacts() {
    if pmd_ci_maven_isSnapshotBuild; then
        # Deploy to sourceforge files https://sourceforge.net/projects/pmd/files/pmd/
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-bin.zip"
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-src.zip"
        # Deploy SBOM
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.xml"
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.json"
    fi

    # release build case a): everything without pmd-cli and pmd-dist is released
    if pmd_ci_maven_isReleaseBuild && [ "${BUILD_CLI_DIST_ONLY}" = "false" ]; then
        # create a draft github release
        pmd_ci_gh_releases_createDraftRelease "${PMD_CI_TAG}" "$(git rev-list -n 1 "${PMD_CI_TAG}")"
        GH_RELEASE="$RESULT"
    fi

    # release build case b): only pmd-cli and pmd-dist are released
    if pmd_ci_maven_isReleaseBuild && [ "${BUILD_CLI_DIST_ONLY}" = "true" ]; then
        # Deploy to sourceforge files https://sourceforge.net/projects/pmd/files/pmd/
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-bin.zip"
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-src.zip"
        # Deploy SBOM
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.xml"
        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.json"

        # draft release has already been created
        pmd_ci_gh_releases_getLatestDraftRelease
        GH_RELEASE="$RESULT"

        # Deploy to github releases
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-bin.zip"
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-src.zip"
        # Deploy SBOM
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.xml"
        pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "pmd-dist/target/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}-cyclonedx.json"
    fi
}

#
# Builds and uploads the documentation site
# Renders release notes and uploads them as ReadMe.md to sourceforge
#
function pmd_ci_build_and_upload_doc() {
    # generate the site only for snapshots from master and for release builds for case a) (everything without cli/dist)
    # to avoid building it twice during a release...
    if pmd_ci_maven_isSnapshotBuild && [ "${PMD_CI_BRANCH}" = "master" ] || [ "${BUILD_CLI_DIST_ONLY}" = "false" ]; then
        pmd_doc_generate_jekyll_site
        pmd_doc_create_archive

        pmd_ci_sourceforge_uploadFile "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "docs/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-doc.zip"

        if pmd_ci_maven_isReleaseBuild; then
            pmd_ci_gh_releases_uploadAsset "$GH_RELEASE" "docs/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-doc.zip"
        fi

        # Deploy doc to https://docs.pmd-code.org/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/
        pmd_code_uploadDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "docs/pmd-dist-${PMD_CI_MAVEN_PROJECT_VERSION}-doc.zip"
        # Deploy javadoc to https://docs.pmd-code.org/apidocs/*/${PMD_CI_MAVEN_PROJECT_VERSION}/
        pmd_code_uploadJavadoc "${PMD_CI_MAVEN_PROJECT_VERSION}" "$(pwd)"

        # render release notes
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
        # Upload to https://sourceforge.net/projects/pmd/files/pmd/${PMD_CI_MAVEN_PROJECT_VERSION}/ReadMe.md
        pmd_ci_sourceforge_uploadReleaseNotes "pmd/${PMD_CI_MAVEN_PROJECT_VERSION}" "${rendered_release_notes}"
    fi

    if pmd_ci_maven_isSnapshotBuild && [ "${PMD_CI_BRANCH}" = "master" ]; then
        # only for snapshot builds from branch master: https://docs.pmd-code.org/snapshot -> pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "snapshot"

        # update github pages https://pmd.github.io/pmd/
        pmd_doc_publish_to_github_pages
        # rsync site to https://pmd.sourceforge.io/snapshot
        pmd_ci_sourceforge_rsyncSnapshotDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "snapshot"
    fi

    if pmd_ci_maven_isReleaseBuild && [ "${BUILD_CLI_DIST_ONLY}" = "false" ]; then
        # documentation is already uploaded to https://docs.pmd-code.org/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}
        # we only need to setup symlinks for the released version
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "latest"
        # remove old doc and point to the new version
        pmd_code_removeDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"
        pmd_code_createSymlink "${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"
        # remove old javadoc
        pmd_code_removeJavadoc "${PMD_CI_MAVEN_PROJECT_VERSION}-SNAPSHOT"

        # github release only for releases
        pmd_ci_gh_releases_updateRelease "$GH_RELEASE" "$release_name" "${rendered_release_notes}"

        local rendered_release_notes_with_links
        rendered_release_notes_with_links="
*   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${PMD_CI_MAVEN_PROJECT_VERSION}
*   Documentation: https://docs.pmd-code.org/pmd-doc-${PMD_CI_MAVEN_PROJECT_VERSION}/

${rendered_release_notes}"
        pmd_ci_sourceforge_createDraftBlogPost "${release_name} released" "${rendered_release_notes_with_links}" "pmd,release"
        SF_BLOG_URL="${RESULT}"

        # rsync site to https://pmd.sourceforge.io/pmd-${PMD_CI_MAVEN_PROJECT_VERSION}
        pmd_ci_sourceforge_rsyncSnapshotDocumentation "${PMD_CI_MAVEN_PROJECT_VERSION}" "pmd-${PMD_CI_MAVEN_PROJECT_VERSION}"
    fi
}

#
# Runs the dogfood ruleset with the currently built pmd against itself
#
function pmd_ci_dogfood() {
    local mpmdVersion=()
    ./mvnw versions:set -DnewVersion="${PMD_CI_MAVEN_PROJECT_VERSION%-SNAPSHOT}-dogfood-SNAPSHOT" -DgenerateBackupPoms=false
    sed -i 's/<version>[0-9]\{1,\}\.[0-9]\{1,\}\.[0-9]\{1,\}.*<\/version>\( *<!-- pmd.dogfood.version -->\)/<version>'"${PMD_CI_MAVEN_PROJECT_VERSION}"'<\/version>\1/' pom.xml
    ./mvnw verify --show-version --errors --batch-mode "${PMD_MAVEN_EXTRA_OPTS[@]}" \
        "${mpmdVersion[@]}" \
        -DskipTests \
        -Dmaven.javadoc.skip=true \
        -Dmaven.source.skip=true \
        -Dcheckstyle.skip=true
    ./mvnw versions:set -DnewVersion="${PMD_CI_MAVEN_PROJECT_VERSION}" -DgenerateBackupPoms=false
    git checkout -- pom.xml
}

build

exit 0
