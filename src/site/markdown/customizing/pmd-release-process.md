<!--
        <author email="rpelisse@users.sourceforge.net">Romain Pelisse</author>
        <author email="adangel@users.sourceforge.net">Andreas Dangel</author>
-->

# PMD Release Process

This page describes the current status of the release process.

## Preparations

Make sure code is up to date and everything is committed and pushed with git:

    $ mvn clean
    $ git pull
    $ git status

**Checklist for release preparation:**

*   Ensure all the new rules are listed in a the proper file:
    `pmd-core/src/main/resources/rulesets/releases/<version>.xml` file.
*   Update version/release info in **src/site/markdown/changelog.md**.
*   Update **../pmd.github.io/latest/index.html** of our website, to redirect to the new version
*   Update **../pmd.github.io/index.html** to mention the new release


    $ mvn clean install

Unzip and test manually (just to be on the safe side of the road):

    cd pmd-dist/target; unzip pmd-bin-<version>.zip; cd pmd-bin-<version>
    ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R rulesets/java/unusedcode.xml
    ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R java-unusedcode
    ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f html -R rulesets/java/unusedcode.xml
    cd ..; cd ../..

Check in all (version) changes to branch master:

    $ git commit -a -m "Prepare pmd release <version>"

## Release Branch

Let's have maven create a release branch for us (<em>note - this is of course not
needed if you are already on a release/maintenance branch</em>). Maven will automatically
increase the version in branch master.

    $ mvn release:branch -DbranchName=pmd/<version>.x

<em>In case you create a alpha/preview release and you want to stay with the current
version in master, use these additional properties:</em>

    $ mvn -DupdateBranchVersions=true -DupdateWorkingCopyVersions=false ...

Note: The property pushChanges is set to false, so that we can manually
push all changes done during the release at the end, when we are sure,
that everything is fine.

## Create a release

Now checkout the created branch.

    $ git checkout pmd/<version>.x

Now let maven create a release tag.


    $ mvn release:clean
    $ mvn release:prepare

Note: For the tag/label name we currently use this naming pattern:
pmd_releases/&lt;major-version>.&lt;minor-version>.&lt;micro-version>

The next command will checkout PMD from the tag just created and will build
and deploy it to sonatype OSS repositories:

    $ mvn release:perform

### Push your local changes

If everything is fine, you can push your local changes.

    $ git push origin master
    $ git push origin pmd/<version>.x
    $ git push origin tag pmd_releases/<version>

### Rollback

Note: If you see a bug and fix it, you can rollback the release
(see the [maven release plugin documentation](http://maven.apache.org/plugins/maven-release-plugin/examples/rollback-release.html)):

    $ mvn release:rollback

You probably need to delete the release tag manually using the following command (and then
start again with release:clean release:prepare):

    $ git tag -d pmd_releases/<version>

## Publish maven artifacts

Finally, in order to publish the release to Maven central,
you need to release PMD via Sonatype Nexus:

*   Login to <https://oss.sonatype.org/>
*   Go to Staging Repositories page
*   Select a staging repository
*   Click the close button
*   Now you can download the artifacts again and verify they are working.
*   Once you are sure they are working, click "Release" and the artifacts are
    eventually available through
    [maven central](http://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd/).

## Create and publish the site / documentation

You can reuse the cleanly checked out local repository from maven-release-plugin.

    $ cd target/checkout/
    $ mvn site site:stage

*   Before you upload the zip files, unzip and test once (just to be on the safe side of the road):

        $ cd pmd-dist/target; unzip pmd-bin-<version>.zip; cd pmd-bin-<version>
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R rulesets/java/unusedcode.xml
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R java-unusedcode
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f html -R rulesets/java/unusedcode.xml
        $ cd ..; cd ../..

*   While the site will be deployed to github, it's still usefull, to have it downloadable at once:

        $ cd target
        $ mv staging pmd-doc-<version>
        $ zip -r pmd-doc-<version>.zip pmd-doc-<version>/
        $ cd ..

*   Add the site to the pmd.github.io repo:

        $ rsync -avhP target/pmd-doc-<version>/ ../../../pmd.github.io/pmd-<version>/
        $ (cd ../../../pmd.github.io; git add pmd-<version>; git commit -m "Added pmd-<version>")

*   Upload the zip-files to sourceforge's file section:

        $ rsync -avhP pmd-dist/target/pmd-*-<version>.zip target/pmd-doc-<version>.zip your_sf_login@web.sourceforge.net:/home/frs/project/pmd/pmd/<version>/
        $ rsync -avhP src/site/markdown/overview/changelog.md your_sf_login@web.sourceforge.net:/home/frs/project/pmd/pmd/<version>/ReadMe.md

*   Verify the MD5 sums on [Files](https://sourceforge.net/projects/pmd/files/pmd/) and locally:

        $ md5sum pmd-dist/target/pmd-*-<version>.zip target/pmd-doc-<version>.zip

*   Go to [Files](https://sourceforge.net/projects/pmd/files/pmd/), to folder "pmd/&lt;version>",
    and make the new binary pmd zip file the default download for all platforms.

## Push the repos

In case of releasing from master:

        $ git push origin master; git push origin tag pmd_releases/<version>

In case of releasing from a release branch:

        $ git push origin pmd/<version>.x; git push origin tag pmd_releases/<version>

## Create a new release on github

Go to <https://github.com/pmd/pmd/releases>

*   Select the just pushed tag: "pmd_releases/&lt;version>"
*   Set the title: "PMD &lt;version> (DD-MMMM-YYYY)"
*   Copy/Paste the changelog.md file
*   Upload the 3 zip files (pmd-&lt;version>-{src,bin,doc}.zip).
*   Publish the release

## Submit a news on SF

*   Submit news to SF on the [PMD Project News](https://sourceforge.net/p/pmd/news/) page. You can use
    the following template:

        PMD <version> released
        
        * minor version with lots of bug fixes
        * Changelog: https://pmd.github.io/pmd-<version>/overview/changelog.html
        * Downloads: https://github.com/pmd/pmd/releases/tag/pmd_releases%2F<version>
        * Fixed Bugs: https://sourceforge.net/p/pmd/bugs/milestone/PMD-<version>/
        * Documentation: https://pmd.github.io/pmd-<version>/


## Prepare the next version

### Create new milestone

Under <https://sourceforge.net/p/pmd/bugs/milestones> close the milestone "PMD-&lt;version>"
and create a new milestone for the next version (PMD-&lt;version+1>).

### Update changelog

*   Move version/releaseinfo from **src/site/markdown/overview/changelog.md** to **src/site/markdown/overview/changelog-old.md**.
*   Update version/release info in **src/site/markdown/overview/changelog.md**. Use the following template:

        # Changelog
        
        ## ????? - ${DEVELOPMENT_VERSION}
        
        **New Supported Languages:**
        
        **Feature Request and Improvements:**
        
        **New/Modified/Deprecated Rules:**
        
        **Pull Requests:**
        
        **Bugfixes:**
        
        **API Changes:**

*   Update pmd-java8/pom.xml - the version is probably wrong.
    Set it to the parent's=next development version.
*   If you released from a release-branch, now merge the branch back into master.
*   Commit and push

        $ git commit -m "Prepare next development version"
        $ git push origin master


