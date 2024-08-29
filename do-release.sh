#!/usr/bin/env bash

# abort the script on the first failing sub command
set -e

# Make sure, everything is English...
export LANG=en_US.UTF-8

# verify the current directory
if [ ! -f pom.xml ] || [ ! -d ../pmd.github.io ]; then
    echo "You seem to be in the wrong working directory or you don't have pmd.github.io checked out..."
    echo
    echo "Expected:"
    echo "*   You are currently in the pmd repository"
    echo "*   ../pmd.github.io is the pmd.github.io repository"
    echo
    exit 1
fi

#
# Make sure, we have ruby and bundler available
#
set +e # don't stop for error "command not found" - it is handled
ruby_version_full=$(ruby --version 2>&1)
ruby_version=$(echo "${ruby_version_full}" | grep "ruby 3" | head -1 2>&1)
if [ $? -eq 0 ] && [ -n "${ruby_version}" ]; then
  echo "Using ${ruby_version_full}"
else
  echo "Wrong ruby version! Expected ruby 3"
  echo "${ruby_version_full}"
  exit 1
fi
bundler_version=$(bundler --version 2>&1)
if [ $? -eq 0 ]; then
  echo "Using ${bundler_version}"
else
  echo "Missing bundler!"
  echo "${bundler_version}"
  exit 1
fi
# abort the script on the first failing sub command
set -e

CURRENT_BRANCH=

echo "-------------------------------------------"
echo "Releasing PMD"
echo "-------------------------------------------"

# allow to override the release version, e.g. via "RELEASE_VERSION=7.0.0-rc1 ./do-release.sh"
if [ "$RELEASE_VERSION" = "" ]; then
    CURRENT_VERSION=$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)
    RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
fi

MAJOR=$(echo "$RELEASE_VERSION" | cut -d . -f 1)
MINOR=$(echo "$RELEASE_VERSION" | cut -d . -f 2)
PATCH=$(echo "$RELEASE_VERSION" | cut -d . -f 3)
if [ "$PATCH" == "0" ]; then
    NEXT_MINOR=$(("${MINOR}" + 1))
    NEXT_PATCH="0"
    LAST_MINOR=$(("${MINOR}" - 1))
    LAST_PATCH="0"
else
    # this is a bugfixing release
    NEXT_MINOR="${MINOR}"
    NEXT_PATCH=$(("${PATCH}" + 1))
    LAST_MINOR="${MINOR}"
    LAST_PATCH=$(("${PATCH}" - 1))
fi

# allow to override the next version, e.g. via "DEVELOPMENT_VERSION=7.0.0-SNAPSHOT ./do-release.sh"
if [ "$DEVELOPMENT_VERSION" = "" ]; then
    DEVELOPMENT_VERSION="$MAJOR.$NEXT_MINOR.$NEXT_PATCH-SNAPSHOT"
fi

# allow to override the last version, e.g. via "LAST_VERSION=6.55.0 ./do-release.sh"
if [ "$LAST_VERSION" = "" ]; then
    LAST_VERSION="$MAJOR.$LAST_MINOR.$LAST_PATCH"
fi

# http://stackoverflow.com/questions/1593051/how-to-programmatically-determine-the-current-checked-out-git-branch
CURRENT_BRANCH=$(git symbolic-ref -q HEAD)
CURRENT_BRANCH=${CURRENT_BRANCH##refs/heads/}
CURRENT_BRANCH=${CURRENT_BRANCH:-HEAD}

echo "LAST_VERSION: ${LAST_VERSION}"
echo "RELEASE_VERSION: ${RELEASE_VERSION} (this release)"
echo "DEVELOPMENT_VERSION: ${DEVELOPMENT_VERSION} (the next version after the release)"
echo "CURRENT_BRANCH: ${CURRENT_BRANCH}"

echo
echo "Is this correct?"
echo
echo "Press enter to continue... (or CTRL+C to cancel)"
read -r

export LAST_VERSION
export RELEASE_VERSION
export DEVELOPMENT_VERSION
export CURRENT_BRANCH

# check for SNAPSHOT version of pmd.build-tools.version
BUILD_TOOLS_VERSION=$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=pmd.build-tools.version -q -DforceStdout)
BUILD_TOOLS_VERSION_RELEASE=${BUILD_TOOLS_VERSION%-SNAPSHOT}
if [ "${BUILD_TOOLS_VERSION}" != "${BUILD_TOOLS_VERSION_RELEASE}" ]; then
  echo "Error: version pmd.build-tools.version is ${BUILD_TOOLS_VERSION} - snapshot is not allowed"
  exit 1
fi

echo "*   Update version info in **docs/_config.yml**."
echo "    remove the SNAPSHOT from site.pmd.version"
echo
echo "*   Update date info in **docs/_config.yml**."
echo "    date: $(date -u +%Y-%m-%d)"
echo
echo "*   Update **pmd-apex/src/main/resources/rulesets/apex/quickstart.xml** and"
echo "    **pmd-java/src/main/resources/rulesets/java/quickstart.xml** with the new rules."
echo
echo "*   Update **../pmd.github.io/_config.yml** to mention the new release"
echo
echo "*   Update property \`pmd-designer.version\` in **pom.xml** to reference the version, that will be released"
echo "    later in this process."
echo
echo "Press enter to continue..."
read -r


# determine current milestone
MILESTONE_JSON=$(curl -s "https://api.github.com/repos/pmd/pmd/milestones?state=all&direction=desc&per_page=5"|jq ".[] | select(.title == \"$RELEASE_VERSION\")")
MILESTONE=$(echo "$MILESTONE_JSON" | jq .number)

# determine dependency updates
DEPENDENCIES_JSON=$(curl -s "https://api.github.com/repos/pmd/pmd/issues?labels=dependencies&state=closed&direction=asc&per_page=50&page=1&milestone=${MILESTONE}")
DEPENDENCIES_COUNT=$(echo "$DEPENDENCIES_JSON" | jq length)
DEPENDENCIES=""
if [ $DEPENDENCIES_COUNT -gt 0 ]; then
  DEPENDENCIES=$(
    echo "### 📦 Dependency updates"
    echo "$DEPENDENCIES_JSON" | jq --raw-output '.[] | "* [#\(.number)](https://github.com/pmd/pmd/issues/\(.number)): \(.title)"'
  )
else
  DEPENDENCIES=$(
    echo "### 📦 Dependency updates"
    echo "No dependency updates"
  )
fi

# calculating stats for release notes (excluding dependency updates)
STATS_CLOSED_ISSUES=$(echo "$MILESTONE_JSON" | jq .closed_issues)
STATS=$(
echo "### 📈 Stats"
echo "* $(git log pmd_releases/"${LAST_VERSION}"..HEAD --oneline --no-merges |wc -l) commits"
echo "* $(($STATS_CLOSED_ISSUES - $DEPENDENCIES_COUNT)) closed tickets & PRs"
echo "* Days since last release: $(( ( $(date +%s) - $(git log --max-count=1 --format="%at" pmd_releases/"${LAST_VERSION}") ) / 86400))"
)


TEMP_RELEASE_NOTES=$(cat docs/pages/release_notes.md)
TEMP_RELEASE_NOTES=${TEMP_RELEASE_NOTES/\{\% endtocmaker \%\}/${DEPENDENCIES//\&/\\\&}$'\n'$'\n'${STATS//\&/\\\&}$'\n'$'\n'\{\% endtocmaker \%\}}
echo "${TEMP_RELEASE_NOTES}" > docs/pages/release_notes.md

echo
echo "Updated dependencies and stats in release notes:"
echo "$DEPENDENCIES"
echo "$STATS"
echo
echo "Please verify docs/pages/release_notes.md"
echo
echo "Press enter to continue..."
read -r

# install bundles needed for rendering release notes
bundle config set --local path vendor/bundle
bundle config set --local with release_notes_preprocessing
bundle install

RELEASE_NOTES_POST="_posts/$(date -u +%Y-%m-%d)-PMD-${RELEASE_VERSION}.md"
export RELEASE_NOTES_POST
echo "Generating ../pmd.github.io/${RELEASE_NOTES_POST}..."
NEW_RELEASE_NOTES=$(bundle exec docs/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
cat > "../pmd.github.io/${RELEASE_NOTES_POST}" <<EOF
---
layout: post
title: PMD ${RELEASE_VERSION} released
---
${NEW_RELEASE_NOTES}
EOF

echo "Committing current changes (pmd)"

git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
(
    cd ../pmd.github.io
    git add "${RELEASE_NOTES_POST}"
    changes=$(git status --porcelain 2>/dev/null| grep -c -E "^[AMDRC]")
    if [ "$changes" -gt 0 ]; then
        echo "Committing current changes (pmd.github.io)"
        git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
    fi
)

# check that there are no uncommitted changes
UNCOMMITTED_CHANGES=$(git status --short --untracked-files=no)
if [ -n "${UNCOMMITTED_CHANGES}" ]; then
  echo "There are uncommitted changes:"
  echo "${UNCOMMITTED_CHANGES}"
  exit 1
fi
# check that there are no SNAPSHOT dependencies -> done by the enforcer plugin, see enforce-no-snapshots
echo "Change version in the POMs to ${RELEASE_VERSION} and update build timestamp"
./mvnw --quiet versions:set -DnewVersion="${RELEASE_VERSION}" -DgenerateBackupPoms=false -DupdateBuildOutputTimestampPolicy=always
echo "Transform the SCM information in the POM"
sed -i "s|<tag>HEAD</tag>|<tag>pmd_releases/${RELEASE_VERSION}</tag>|" pom.xml
echo "Run the project tests against the changed POMs to confirm everything is in running order (skipping cli and dist)"
# note: skipping pmd in order to avoid failures due to #4757
./mvnw clean verify -Dskip-cli-dist -Dpmd.skip=true -Dcpd.skip=true -Pgenerate-rule-docs
echo "Commit and create tag"
git commit -a -m "[release] prepare release pmd_releases/${RELEASE_VERSION}"
git tag -m "[release] copy for tag pmd_releases/${RELEASE_VERSION}" "pmd_releases/${RELEASE_VERSION}"
echo "Push tag pmd_releases/${RELEASE_VERSION}"
git push origin tag "pmd_releases/${RELEASE_VERSION}"


echo
echo "Tag has been pushed.... now check github actions: <https://github.com/pmd/pmd/actions>"
echo
echo "Now wait, until first stage of the release is finished successfully..."
echo "You don't need to wait until artifacts are in maven central, just the GitHub Action must be successful."
echo
echo "If it is failing, you can fix the code/scripts and force push the tag via"
echo
echo "    git tag -d \"pmd_releases/${RELEASE_VERSION}\""
echo "    git tag -m \"[release] copy for tag pmd_releases/${RELEASE_VERSION}\" \"pmd_releases/${RELEASE_VERSION}\""
echo "    git push origin tag \"pmd_releases/${RELEASE_VERSION}\" --force"
echo
echo "However: This is only possible, if the artefacts have not been pushed to maven central yet..."
echo
echo "Press enter to continue, once the GitHub Action finished successfully..."
read -r

echo
echo "Update POMs to set the new development version ${DEVELOPMENT_VERSION}"
./mvnw --quiet versions:set -DnewVersion="${DEVELOPMENT_VERSION}" -DgenerateBackupPoms=false -DupdateBuildOutputTimestampPolicy=never
sed -i "s|<tag>pmd_releases/${RELEASE_VERSION}</tag>|<tag>HEAD</tag>|" pom.xml

echo
echo "Check the milestone on github:"
echo "<https://github.com/pmd/pmd/milestones>"
echo " --> move any open issues to the next milestone, close the current milestone"
echo
echo
echo "Prepare Next development version:"
echo "*   Update version/date info in **docs/_config.yml**."
echo "    move version to previous_version, increase version, make sure it is a SNAPSHOT version"
echo "    otherwise the javadoc links won't work during development"
echo "    also update the date, e.g. ??-month-year."
echo
echo
echo "Press enter to continue..."
read -r

# update release_notes_old
OLD_RELEASE_NOTES=$(tail -n +8 docs/pages/release_notes_old.md)
OLD_RELEASE_NOTES_HEADER=$(head -n 7 docs/pages/release_notes_old.md)
echo "${OLD_RELEASE_NOTES_HEADER}
${NEW_RELEASE_NOTES}

${OLD_RELEASE_NOTES}" > docs/pages/release_notes_old.md

# reset release notes template
cat > docs/pages/release_notes.md <<EOF
---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

EOF

echo "Committing current changes on branch ${CURRENT_BRANCH}"
# note: using [skip ci] as only the first stage is done and the full build
# requires pmd-designer to be present, which might not be the case yet...
git commit -a -m "[release] Prepare next development version [skip ci]"
echo "Push branch ${CURRENT_BRANCH}"
git push origin "${CURRENT_BRANCH}"

echo
echo
echo
echo "*   Wait until the new version is synced to maven central and appears as latest version in"
echo "    <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/maven-metadata.xml>."
echo
echo
echo "Then proceed with releasing pmd-designer..."
echo "<https://github.com/pmd/pmd-designer/blob/master/releasing.md>"
echo
echo "Press enter to continue when pmd-designer is available in maven-central..."
echo "<https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-designer/maven-metadata.xml>."
echo
echo "Note: If there is no new pmd-designer release needed, you can directly proceed."
read -r

echo
echo "Continuing with release of pmd-cli and pmd-dist..."
echo "Before proceeding however, wait another 10 minutes, so that the freshly released artefacts"
echo "are indeed available from maven central. The GitHub runners might not yet see them..."
echo "If that happens, the build job needs to be started again, maybe the runner cache needs to be cleared as well."
echo
echo "Go to <https://github.com/pmd/pmd/actions/workflows/build.yml> and manually trigger a new build"
echo "from tag 'pmd_releases/${RELEASE_VERSION}' and with option 'Build only modules cli and dist' checked."
echo
echo "This triggers the second stage release and eventually publishes the release on GitHub."
echo
echo "Now check github actions: <https://github.com/pmd/pmd/actions>"
echo
echo
echo "Verification: (see also <https://docs.pmd-code.org/latest/pmd_projectdocs_committers_releasing.html>)"
echo "* Release on GitHub: <https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION}>"
echo "  * should contain release notes"
echo "  * should be published"
echo "  * should contain release assets (bin, src, doc, cyclondx.json, cyclondx.xml)"
echo "* Release Assets on sourceforge: <https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/>"
echo "  * Default download should be new version"
echo "  * All assets are there (bin, src, doc, cyclondx.json, cyclondx.xml, ReadMe.md)"
echo "* News entry on sourceforge: <https://sourceforge.net/p/pmd/news/>"
echo "* Latest documentation points to new release: <https://docs.pmd-code.org/latest/>"
echo "* JavaDoc API Doc is available: <https://docs.pmd-code.org/apidocs/pmd-core/${RELEASE_VERSION}/>"
echo "* All artefacts are on maven central, especially pmd-cli"
echo "  * <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-cli/${RELEASE_VERSION}/>"
echo "  * <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-core/${RELEASE_VERSION}/>"
echo "  * <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-java/${RELEASE_VERSION}/>"
echo "  * <https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-designer/${RELEASE_VERSION}/>"
echo "* Regression Tester baseline has been created: <https://pmd-code.org/pmd-regression-tester/>"
echo
echo "*   Send out an announcement mail to the mailing list:"
echo
echo "To: PMD Developers List <pmd-devel@lists.sourceforge.net>"
echo "Subject: [ANNOUNCE] PMD ${RELEASE_VERSION} released"
echo
echo "    You can copy the same text from <https://sourceforge.net/p/pmd/news/>."
echo
echo
tweet="PMD ${RELEASE_VERSION} released: https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION} #PMD"
tweet="${tweet// /%20}"
tweet="${tweet//:/%3A}"
tweet="${tweet//#/%23}"
tweet="${tweet//\//%2F}"
tweet="${tweet//$'\r'/}"
tweet="${tweet//$'\n'/%0A}"
echo "*   Tweet about this release on https://x.com/pmd_analyzer:"
echo "        <https://x.com/intent/post?text=$tweet>"
echo "*   Post this also into <https://matrix.to/#/#pmd_pmd:gitter.im>:"
echo "        PMD ${RELEASE_VERSION} released: https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION} #PMD"
echo
echo
echo "Now waiting for the release to be finished..."
echo "Press enter, to continue to push pmd.github.io changes"
read -r

echo "Pushing changes to <https://pmd.github.io>..."
(
    cd ../pmd.github.io
    git push
)

echo "------------------------------------------"
echo "Done."
echo "------------------------------------------"
echo
