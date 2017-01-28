#!/bin/bash

# work around a maven-scm bug with git while releasing
# this might also be necessary: git config --add status.displayCommentPrefix true
# see https://jira.codehaus.org/browse/SCM-740
export LANG=C

export MAVEN_OPTS="-XX:MaxPermSize=1g"


# check setup
if [ "" = "$PMD_SF_USER" ]; then
    echo "No env variable PMD_SF_USER specified. This is the sourceforge account name needed"
    echo "during the release process to upload files via ssh/scp/rsync."
    echo
    echo "Please set the variable, e.g. in your ~/.bashrc:"
    echo
    echo "PMD_SF_USER=sfuser"
    echo "export PMD_SF_USER"
    echo
    exit 1
fi
if [ "" = "$PMD_SF_APIKEY" ]; then
    echo "No env variable PMD_SF_APIKEY specified. This is the release api key,"
    echo "needed to set the default download file. You can create an api key in"
    echo "your user preferences on sourceforge.net"
    echo
    echo "Please set the variable, e.g. in your ~/.bashrc:"
    echo
    echo "PMD_SF_APIKEY=abcd-efgh-xxxx"
    echo "export PMD_SF_APIKEY"
    echo
    exit 1
fi

if [ "" = "$PMD_GPG_PROFILE" ]; then
    echo "No env variable PMD_GPG_PROFILE specified. This is your maven profile, which configures"
    echo "the properties gpg.keyname and gpg.passphrase used"
    echo "to sign the created release artifacts before uploading to maven central."
    echo
    echo "Please set the variable, e.g. in your ~/.bashrc:"
    echo
    echo "PMD_GPG_PROFILE=pmd-gpg"
    echo "export PMD_GPG_PROFILE"
    echo
    echo "And in your ~/.m2/settings.xml file:"
    echo
    echo "  <profiles>"
    echo "    <profile>"
    echo "      <id>pmd-gpg</id>"
    echo "        <properties>"
    echo "          <gpg.keyname>AB123CDE</gpg.keyname>"
    echo "          <gpg.passphrase></gpg.passphrase>"
    echo "        </properties>"
    echo "    </profile>"
    echo "  </profiles"
    echo
    exit 1
fi

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

##########################################################################


RELEASE_VERSION=
DEVELOPMENT_VERSION=
CURRENT_BRANCH=

echo "-------------------------------------------"
echo "Releasing PMD"
echo "-------------------------------------------"


if [ "" = "$RELEASE_VERSION" ]; then
    echo -n "What is the release version of PMD? (e.g. 5.4.0) "
    read RELEASE_VERSION
fi
if [ "" = "$DEVELOPMENT_VERSION" ]; then
    echo -n "What is the next development version of PMD? (e.g. 5.5.0-SNAPSHOT) "
    read DEVELOPMENT_VERSION
fi
if [ "" = "$CURRENT_BRANCH" ]; then
    echo -n "What is the branch you want to release from? (e.g. master or pmd/5.4.x or pmd/5.3.x) "
    read CURRENT_BRANCH
fi

export RELEASE_VERSION
export DEVELOPMENT_VERSION
export CURRENT_BRANCH

echo
echo
echo "*   Update version/release info in **src/site/markdown/overview/changelog.md**."
echo
echo "    ## $(date -u +%d-%B-%Y) - ${RELEASE_VERSION}"
echo
echo "*   Ensure all the new rules are listed in a the proper file:"
echo "    pmd-core/src/main/resources/rulesets/releases/${RELEASE_VERSION}.xml file."
echo
echo "*   Update **../pmd.github.io/latest/index.html** of our website, to redirect to the new version"
echo
echo "redirect_to: https://pmd.github.io/pmd-${RELEASE_VERSION}/"
echo
echo "*   Update **../pmd.github.io/index.html** to mention the new release"
echo
echo
echo "Press enter to continue..."
read

echo "first - running a test install..."

mvn clean install
if [ $? -ne 0 ]; then
    echo "Failure during test install...."
    exit 1
fi

echo "Running doclint..."
mvn -Pdoclint javadoc:javadoc javadoc:test-javadoc
if [ $? -ne 0 ]; then
    echo "Failure during doclint...."
    exit 1
fi

(
    cd pmd-dist/target
    unzip pmd-bin-${RELEASE_VERSION}-SNAPSHOT.zip
    cd pmd-bin-${RELEASE_VERSION}-SNAPSHOT
    ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R rulesets/java/unusedcode.xml
)

echo
echo "---- OK?"
echo "Press enter to continue..."
read

echo "Committing current changes (pmd)"
git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
(
    echo "Committing current changes (pmd.github.io)"
    cd ../pmd.github.io
    git commit -a -m "Prepare pmd release ${RELEASE_VERSION}"
)


mvn -B release:clean release:prepare \
    -Dtag=pmd_releases/${RELEASE_VERSION} \
    -DreleaseVersion=${RELEASE_VERSION} \
    -DdevelopmentVersion=${DEVELOPMENT_VERSION} \
    -P${PMD_GPG_PROFILE}
mvn -B release:perform \
    -P${PMD_GPG_PROFILE} \
    -DreleaseProfiles=pmd-release,${PMD_GPG_PROFILE}

(
    cd target/checkout/

    (
        cd pmd-dist/target
        unzip pmd-bin-${RELEASE_VERSION}.zip
        cd pmd-bin-${RELEASE_VERSION}
        ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R rulesets/java/unusedcode.xml
    )

echo
echo "Verify once again..."
echo "---- OK?"
echo "Press enter to continue..."
read

echo
echo "Generating site..."
mvn site site:stage -Psite
echo
echo "Press enter to continue..."
read


echo
echo "*   Login to <https://oss.sonatype.org/>"
echo "    Close and Release the staging repository"
echo "    Check here: <http://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/>"
echo
echo "Press enter to continue..."
read

echo
echo "Creating the pmd-doc-${RELEASE_VERSION}.zip archive..."
(
    cd target
    mv staging pmd-doc-${RELEASE_VERSION}
    zip -r pmd-doc-${RELEASE_VERSION}.zip pmd-doc-${RELEASE_VERSION}/
)

echo
echo "Adding the site to pmd.github.io..."
rsync -avhP target/pmd-doc-${RELEASE_VERSION}/ ../../../pmd.github.io/pmd-${RELEASE_VERSION}/
(
  cd ../../../pmd.github.io
  git add pmd-${RELEASE_VERSION}
  git commit -m "Added pmd-${RELEASE_VERSION}"
)

echo
echo "Uploading the zip files..."
rsync -avhP pmd-dist/target/pmd-*-${RELEASE_VERSION}.zip target/pmd-doc-${RELEASE_VERSION}.zip $PMD_SF_USER@web.sourceforge.net:/home/frs/project/pmd/pmd/${RELEASE_VERSION}/
rsync -avhP src/site/markdown/overview/changelog.md $PMD_SF_USER@web.sourceforge.net:/home/frs/project/pmd/pmd/${RELEASE_VERSION}/ReadMe.md
echo

if [ ! "" = "$PMD_LOCAL_BINARIES" -a -d $PMD_LOCAL_BINARIES ]; then
    echo "Copying the files to local storage directory $PMD_LOCAL_BINARIES..."
    cp -av pmd-dist/target/pmd-*-${RELEASE_VERSION}.zip target/pmd-doc-${RELEASE_VERSION}.zip $PMD_LOCAL_BINARIES
    echo
fi

echo "Making the binary the new default file..."
curl -H "Accept: application/json" -X PUT -d "default=windows&default=mac&default=linux&default=bsd&default=solaris&default=others" \
    -d "api_key=${PMD_SF_APIKEY}" https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/pmd-bin-${RELEASE_VERSION}.zip
echo

echo
echo "Verify the md5sums: <https://sourceforge.net/projects/pmd/files/pmd/${RELEASE_VERSION}/>"
md5sum pmd-dist/target/pmd-*-${RELEASE_VERSION}.zip target/pmd-doc-${RELEASE_VERSION}.zip

echo
echo "Press enter to continue..."
read
)

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
echo "Close the milestone on sourceforge and create a new one..."
echo "<https://sourceforge.net/p/pmd/bugs/milestones>"
echo
echo "Press enter to continue..."
read

echo "Last step - next development version:"
echo "*   Move version/release info from **src/site/markdown/overview/changelog.md** to **src/site/markdown/overview/changelog-old.md**."
echo "*   Update version/release info in **src/site/markdown/overview/changelog.md**."
echo "*   Update pmd-{java8,ui}/pom.xml - the version is probably wrong - set it to the parent's=next development version: ${DEVELOPMENT_VERSION}."
echo
cat <<EOF
# Changelog

## ????? - ${DEVELOPMENT_VERSION}

**New Supported Languages:**

**Feature Requests and Improvements:**

**New/Modified/Deprecated Rules:**

**Pull Requests:**

**Bugfixes:**

**API Changes:**

EOF
echo
echo "Press enter to continue..."
read
git commit -a -m "Prepare next development version"


echo
echo "Now - last step - pushing everything..."
echo
echo "Press enter to continue..."
read

git push origin ${CURRENT_BRANCH}
git push origin tag pmd_releases/${RELEASE_VERSION}

(
    echo "Pushing pmd.github.io..."
    echo "Press enter to continue..."
    read
    cd ../pmd.github.io
    git push origin master
)

echo
echo "Create a new release on github: <https://github.com/pmd/pmd/releases>"
echo
echo "   * Set the title: PMD ${RELEASE_VERSION} ($(date -u +%d-%B-%Y))"
echo "   * copy/paste the changelog.md"
echo "   * Upload the binary zip file"
echo "   * Upload the doc zip file"
echo "   * Upload the src zip file"
echo
echo "Press enter to continue..."
read


echo
echo "------------------------------------------"
echo "Done."
echo "------------------------------------------"
echo


