#!/usr/bin/env bash
set -e

# Make sure, everything is English...
export LANG=C.UTF-8

# verify the current directory
if [ ! -f pom.xml -o ! -d ../pmd.github.io ]; then
    echo "You seem to be in the wrong working directory or you don't have pmd.github.io checked out..."
    echo
    echo "Expected:"
    echo "*   You are currently in the pmd repository"
    echo "*   ../pmd.github.io is the pmd.github.io repository"
    echo
    exit 1
fi

LAST_VERSION=
RELEASE_VERSION=
DEVELOPMENT_VERSION=
CURRENT_BRANCH=

echo "-------------------------------------------"
echo "Releasing PMD"
echo "-------------------------------------------"

# see also https://gist.github.com/pdunnavant/4743895
CURRENT_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
MAJOR=$(echo $RELEASE_VERSION | cut -d . -f 1)
MINOR=$(echo $RELEASE_VERSION | cut -d . -f 2)
PATCH=$(echo $RELEASE_VERSION | cut -d . -f 3)
if [ "$PATCH" == "0" ]; then
    NEXT_MINOR=$(expr ${MINOR} + 1)
    NEXT_PATCH="0"
    LAST_MINOR=$(expr ${MINOR} - 1)
    LAST_PATCH="0"
else
    # this is a bugfixing release
    NEXT_MINOR="${MINOR}"
    NEXT_PATCH=$(expr ${PATCH} + 1)
    LAST_MINOR="${MINOR}"
    LAST_PATCH=$(expr ${PATCH} - 1)
fi
LAST_VERSION="$MAJOR.$LAST_MINOR.$LAST_PATCH"
DEVELOPMENT_VERSION="$MAJOR.$NEXT_MINOR.$NEXT_PATCH"
DEVELOPMENT_VERSION="${DEVELOPMENT_VERSION}-SNAPSHOT"

# allow to override the next version, e.g. via "NEXT_VERSION=7.0.0 ./do-release.sh"
if [ "$NEXT_VERSION" != "" ]; then
    DEVELOPMENT_VERSION="${NEXT_VERSION}-SNAPSHOT"
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
read

export LAST_VERSION
export RELEASE_VERSION
export DEVELOPMENT_VERSION
export CURRENT_BRANCH

RELEASE_RULESET="pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION//\./}.xml"

echo "*   Update date info in **docs/_config.yml**."
echo "    date: $(date -u +%d-%B-%Y)"
echo
echo "*   Update version info in **docs/_config.yml**."
echo "    remove the SNAPSHOT from site.pmd.version"
echo
echo "*   Ensure all the new rules are listed in the proper file:"
echo "    ${RELEASE_RULESET}"
echo
echo "*   Update **pmd-apex/src/main/resources/rulesets/apex/quickstart.xml** and"
echo "    **pmd-java/src/main/resources/rulesets/java/quickstart.xml** with the new rules."
echo
echo "*   Update **docs/pages/next_major_development.md** with the API changes for"
echo "    the new release based on the release notes"
echo
echo "*   Update **../pmd.github.io/_config.yml** to mention the new release"
echo
echo "*   Update property \`pmd-designer.version\` in **pom.xml** to reference the latest pmd-designer release"
echo "    See <https://search.maven.org/search?q=g:net.sourceforge.pmd%20AND%20a:pmd-ui&core=gav> for the available releases."
echo
echo "Press enter to continue..."
read


# calculating stats for release notes

STATS=$(
echo "### Stats"
echo "* $(git log pmd_releases/${LAST_VERSION}..HEAD --oneline --no-merges |wc -l) commits"
echo "* $(curl -s https://api.github.com/repos/pmd/pmd/milestones|jq ".[] | select(.title == \"$RELEASE_VERSION\") | .closed_issues") closed tickets & PRs"
echo "* Days since last release: $(( ( $(date +%s) - $(git log --max-count=1 --format="%at" pmd_releases/${LAST_VERSION}) ) / 86400))"
)

TEMP_RELEASE_NOTES=$(cat docs/pages/release_notes.md)
TEMP_RELEASE_NOTES=${TEMP_RELEASE_NOTES/\{\% endtocmaker \%\}/$STATS$'\n'$'\n'\{\% endtocmaker \%\}$'\n'}
echo "${TEMP_RELEASE_NOTES}" > docs/pages/release_notes.md

echo
echo "Updated stats in release notes:"
echo "$STATS"
echo
echo "Please verify docs/pages/release_notes.md"
echo
echo "Press enter to continue..."
read

# install bundles needed for rendering release notes
bundle install --with=release_notes_preprocessing --path vendor/bundle

export RELEASE_NOTES_POST="_posts/$(date -u +%Y-%m-%d)-PMD-${RELEASE_VERSION}.md"
echo "Generating ../pmd.github.io/${RELEASE_NOTES_POST}..."
NEW_RELEASE_NOTES=$(bundle exec .travis/render_release_notes.rb docs/pages/release_notes.md | tail -n +6)
cat > ../pmd.github.io/${RELEASE_NOTES_POST} <<EOF
---
layout: post
title: PMD ${RELEASE_VERSION} released
---
${NEW_RELEASE_NOTES}
EOF

echo "Committing current changes (pmd)"

if [[ -e ${RELEASE_RULESET} ]]
then
    git add ${RELEASE_RULESET}
fi

git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
(
    cd ../pmd.github.io
    git add ${RELEASE_NOTES_POST}
    changes=$(git status --porcelain 2>/dev/null| egrep "^[AMDRC]" | wc -l)
    if [ $changes -gt 0 ]; then
        echo "Committing current changes (pmd.github.io)"
        git commit -a -m "Prepare pmd release ${RELEASE_VERSION}" && git push
    fi
)

./mvnw -B release:clean release:prepare \
    -Dtag=pmd_releases/${RELEASE_VERSION} \
    -DreleaseVersion=${RELEASE_VERSION} \
    -DdevelopmentVersion=${DEVELOPMENT_VERSION} \
    -Pgenerate-rule-docs


echo
echo "Tag has been pushed.... now check travis build: <https://travis-ci.org/pmd/pmd>"
echo
echo
echo "Press enter to continue..."
read

echo
echo "Check the milestone on github:"
echo "<https://github.com/pmd/pmd/milestones>"
echo " --> move any open issues to the next milestone, close the current milestone"
echo
echo
echo "Prepare Next development version:"
echo "*   Update version/date info in **docs/_config.yml**."
echo
echo
echo "Press enter to continue..."
read

# update release_notes_old
OLD_RELEASE_NOTES=$(tail -n +8 docs/pages/release_notes_old.md)
echo "$(head -n 7 docs/pages/release_notes_old.md)" > docs/pages/release_notes_old.md
echo "$NEW_RELEASE_NOTES" >> docs/pages/release_notes_old.md
echo >> docs/pages/release_notes_old.md
echo "$OLD_RELEASE_NOTES" >> docs/pages/release_notes_old.md

# reset release notes template
cat > docs/pages/release_notes.md <<EOF
---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

### API Changes

### External Contributions

{% endtocmaker %}

EOF

git commit -a -m "Prepare next development version"
git push origin ${CURRENT_BRANCH}
./mvnw -B release:clean
echo
echo
echo
echo "Verify the new release on github: <https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION}>"
echo
echo "*   Submit news to SF on <https://sourceforge.net/p/pmd/news/> page. Use same text as in the email below."
echo "*   Send out an announcement mail to the mailing list:"
echo
echo "To: PMD Developers List <pmd-devel@lists.sourceforge.net>"
echo "Subject: [ANNOUNCE] PMD ${RELEASE_VERSION} Released"
echo
echo "*   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${RELEASE_VERSION}"
echo "*   Documentation: https://pmd.github.io/pmd-${RELEASE_VERSION}/"
echo
echo "$NEW_RELEASE_NOTES"
echo
echo
echo
tweet="PMD ${RELEASE_VERSION} released: https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION} #PMD"
tweet="${tweet// /%20}"
tweet="${tweet//:/%3A}"
tweet="${tweet//#/%23}"
tweet="${tweet//\//%2F}"
tweet="${tweet//$'\r'//}"
tweet="${tweet//$'\n'//%0A}"
echo "*   Tweet about this release on https://twitter.com/pmd_analyzer:"
echo "        <https://twitter.com/intent/tweet?text=$tweet>"
echo
echo "------------------------------------------"
echo "Done."
echo "------------------------------------------"
echo


