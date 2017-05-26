#!/bin/bash

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
CURRENT_VERSION=$(./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec|tail -1)
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
MAJOR=$(echo $RELEASE_VERSION | cut -d . -f 1)
MINOR=$(echo $RELEASE_VERSION | cut -d . -f 2)
PATCH=$(echo $RELEASE_VERSION | cut -d . -f 3)
NEXT_MINOR=$(expr ${MINOR} + 1)
DEVELOPMENT_VERSION="$MAJOR.$NEXT_MINOR.0"
DEVELOPMENT_VERSION="${DEVELOPMENT_VERSION}-SNAPSHOT"

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

echo "*   Update version/release info in **src/site/markdown/overview/changelog.md**."
echo
echo "    ## $(date -u +%d-%B-%Y) - ${RELEASE_VERSION}"
echo
echo "*   Ensure all the new rules are listed in a the proper file:"
echo "    pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION}.xml file."
echo
echo "*   Update **../pmd.github.io/index.html** to mention the new release"
echo
echo "Press enter to continue..."
read
echo "Committing current changes (pmd)"
git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
(
    echo "Committing current changes (pmd.github.io)"
    cd ../pmd.github.io
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

* minor version with lots of bug fixes
* Changelog: https://pmd.github.io/pmd-${RELEASE_VERSION}/overview/changelog.html
* Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F${RELEASE_VERSION}
* Fixed Bugs: https://sourceforge.net/p/pmd/bugs/milestone/PMD-${RELEASE_VERSION}/
* Documentation: https://pmd.github.io/pmd-${RELEASE_VERSION}/
EOF
echo
echo "Press enter to continue..."
read

echo
echo "Check the milestone on sourceforge:"
echo "<https://sourceforge.net/p/pmd/bugs/milestones>"
echo
echo
echo
echo "Prepare Next development version:"
echo "*   Move version/release info from **src/site/markdown/overview/changelog.md** to **src/site/markdown/overview/changelog-old.md**."
echo "*   Update version/release info in **src/site/markdown/overview/changelog.md**."
echo
cat <<EOF
# PMD Release Notes

## ????? - ${DEVELOPMENT_VERSION}

The PMD team is pleased to announce PMD ${DEVELOPMENT_VERSION%-SNAPSHOT}.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

### Fixed Issues

### API Changes

### External Contributions

EOF
echo
echo "Press enter to continue..."
read
git commit -a -m "Prepare next development version"
git push origin ${CURRENT_BRANCH}
echo
echo
echo
echo "Verify the new release on github: <https://github.com/pmd/pmd/releases/tag/pmd_releases/${RELEASE_VERSION}>"
echo
echo
echo "Send out an announcement mail to the mailing list:"
echo "To: PMD Developers List <pmd-devel@lists.sourceforge.net>"
echo "Subject: [ANNOUNCE] PMD ${RELEASE_VERSION} Released"
echo "Body: !!Copy Changelog!!"
echo
echo
echo "------------------------------------------"
echo "Done."
echo "------------------------------------------"
echo



