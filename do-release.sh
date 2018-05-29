#!/bin/bash

# Make sure, everything is English...
export LANG=C.UTF8

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
else
    # this is a bugfixing release
    NEXT_MINOR="${MINOR}"
    NEXT_PATCH=$(expr ${PATCH} + 1)
fi
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

echo "RELEASE_VERSION: ${RELEASE_VERSION}"
echo "DEVELOPMENT_VERSION: ${DEVELOPMENT_VERSION}"
echo "CURRENT_BRANCH: ${CURRENT_BRANCH}"

echo
echo "Is this correct?"
echo
echo "Press enter to continue..."
read


export RELEASE_VERSION
export DEVELOPMENT_VERSION
export CURRENT_BRANCH

echo "*   Update version/release info in **docs/pages/release_notes.md**."
echo
echo "    ## $(date -u +%d-%B-%Y) - ${RELEASE_VERSION}"
echo
echo "*   Update date info in **docs/_config.yml**."
echo
echo "*   Ensure all the new rules are listed in a the proper file:"
echo "    pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION//\./}.xml file."
echo
echo "*   Update **../pmd.github.io/_config.yml** to mention the new release"
echo
echo "*   Add **../pmd.github.io/_posts/$(date -u +%Y-%m-%d)-PMD-${RELEASE_VERSION}.md"
echo
echo "Press enter to continue..."
read
echo "Committing current changes (pmd)"

if [[ -e pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION//\./}.xml ]]
    git add pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION//\./}.xml
fi

git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
(
    echo "Committing current changes (pmd.github.io)"
    cd ../pmd.github.io
    git add _posts/$(date -u +%Y-%m-%d)-PMD-${RELEASE_VERSION}.md
    git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
    git push
)

./mvnw -B release:clean release:prepare \
    -Dtag=pmd_releases/${RELEASE_VERSION} \
    -DreleaseVersion=${RELEASE_VERSION} \
    -DdevelopmentVersion=${DEVELOPMENT_VERSION}


echo
echo "Tag has been pushed.... now check travis build: <https://travis-ci.org/pmd/pmd>"
echo
echo
echo "Submit news to SF on <https://sourceforge.net/p/pmd/news/> page. You can use"
echo "the following template:"
echo
cat <<EOF
PMD ${RELEASE_VERSION} released

* Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${RELEASE_VERSION}
* Documentation: https://pmd.github.io/pmd-${RELEASE_VERSION}/

And Copy-Paste the release notes
EOF
echo
echo "Press enter to continue..."
read

echo
echo "Check the milestone on github:"
echo "<https://github.com/pmd/pmd/milestones>"
echo " --> move any open issues to the next milestone, close the current milestone"
echo " --> Maybe there are some milestones on sourceforge, too: <https://sourceforge.net/p/pmd/bugs/milestones>."
echo
echo
echo "Prepare Next development version:"
echo "*   Update version/date info in **docs/_config.yml**."
echo
echo
echo "Press enter to continue..."
read

# update release_notes_old
OLD_RELEASE_NOTES=$(tail -n +5 docs/pages/release_notes_old.md)
NEW_RELEASE_NOTES=$(tail -n +6 docs/pages/release_notes.md)
echo "$(head -n 5 docs/pages/release_notes_old.md)" > docs/pages/release_notes_old.md
echo "$NEW_RELEASE_NOTES" >> docs/pages/release_notes_old.md
echo "$OLD_RELEASE_NOTES" >> docs/pages/release_notes_old.md

# reset release notes template
cat > docs/pages/release_notes.md <<EOF
---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - ${DEVELOPMENT_VERSION}

The PMD team is pleased to announce PMD ${DEVELOPMENT_VERSION%-SNAPSHOT}.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

### Fixed Issues

### API Changes

### External Contributions

EOF

git commit -a -m "Prepare next development version"
git push origin ${CURRENT_BRANCH}
./mvwn -B release:clean
echo
echo
echo
echo "Verify the new release on github: <https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION}>"
echo
echo
echo "Send out an announcement mail to the mailing list:"
echo "To: PMD Developers List <pmd-devel@lists.sourceforge.net>"
echo "Subject: [ANNOUNCE] PMD ${RELEASE_VERSION} Released"
echo
echo "    *   Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${RELEASE_VERSION}"
echo "    *   Documentation: https://pmd.github.io/pmd-${RELEASE_VERSION}/"
echo
echo "    And Copy-Paste the release notes"
echo
echo
echo
echo "------------------------------------------"
echo "Done."
echo "------------------------------------------"
echo



