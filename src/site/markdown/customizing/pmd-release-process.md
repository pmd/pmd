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
*   Update **../pmd.sourceforge.net/.htaccess** of our website, to redirect to the new version
*   Update **../pmd.sourceforge.net/index.html** to mention the new release

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

    $ mvn release:branch -DbranchName=pmd/&lt;version&gt;.x

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
pmd_releases/<major-version>.<minor-version>.<micro-version>

The next command will checkout PMD from the tag just created and will build
and deploy it to sonatype OSS repositories:

    $ mvn release:perform

### Push your local changes

If everything is fine, you can push your local changes.

    $ git push origin master
    $ git push origin pmd/<version<.x
    $ git push origin tag pmd_releases/<version>

### Rollback

Note: If you see a bug and fix it, you can rollback the release
(see the [maven release plugin documentation](http://maven.apache.org/plugins/maven-release-plugin/examples/rollback-release.html)):

    $ mvn release:rollback

You probably need to delete the release tag manually using the following command (and then
start again with release:clean release:prepare):

    $ git tag -d pmd_releases/<version>

## Create new milestone

Under <https://sourceforge.net/p/pmd/bugs/> rename
the bug milestone "PMD-next" to "PMD-<version>" and create a new "PMD-next" milestone.

## Publish artifacts

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

## Publish the release site on sourceforge

Build it again - you can reuse the cleanly checked out local repository from maven-release-plugin.

Upload command below will first create the maven site and then upload it:

    $ cd target/checkout/
    $ mvn clean install site site:stage   # it's import to execute install,
                                          # so that all pmd-*.jars are created and included in the zip packages

*   Before you upload the zip files, unzip and test once (just to be on the safe side of the road):

        $ cd pmd-dist/target; unzip pmd-bin-<version>.zip; cd pmd-bin-<version>
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R rulesets/java/unusedcode.xml
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f xml -R java-unusedcode
        $ ./bin/run.sh pmd -d ../../../pmd-java/src/main/java -language java -f html -R rulesets/java/unusedcode.xml
        $ cd ..; cd ../..

*   While the site will be deployed to sourceforge, it's still usefull, to have it downloadable at once:

        $ cd target
        $ mv staging pmd-doc-<version>
        $ zip -r pmd-doc-<version>.zip pmd-doc-<version>/
        $ cd ..

*   Upload the site to sourceforge:

        $ rsync -avhzP target/pmd-doc-<version>/ your_sf_login@web.sourceforge.net:/home/project-web/pmd/htdocs/pmd-<version>/

*   Upload the zip-files to sourceforge's file section:

        $ rsync -avhP pmd-dist/target/pmd-*-<version>.zip target/pmd-doc-<version>.zip your_sf_login@web.sourceforge.net:/home/frs/project/pmd/pmd/<version>/
        $ rsync -avhP src/site/markdown/overview/changelog.md your_sf_login@web.sourceforge.net:/home/frs/project/pmd/pmd/<version>/ReadMe.md

*   Verify the MD5 sums on <a href="https://sourceforge.net/projects/pmd/files/pmd/">Files</a> and locally:

        $ md5sum pmd-dist/target/pmd-*-<version>.zip target/pmd-doc-<version>.zip

*   Go to [Files](https://sourceforge.net/projects/pmd/files/pmd/), to folder "pmd/<version>",
    and make the new binary pmd zip file the default download for all platforms.</li>

**Upload changes to pmd.sourceforge.net**

    cd ../pmd.sourceforge.net
    rsync -avhpz \
      --exclude=bin/ \
      --exclude=src/ \
      --exclude=.classpath \
      --exclude=.project \
      --exclude=.git \
      \
      ./ your_sf_login@web.sourceforge.net:/home/project-web/pmd/htdocs/

## Social side of release

*   Submit news to SF on the [PMD Project News](https://sourceforge.net/p/pmd/news/) page. You can use
    the following template:

    PMD <version> released
    
    * minor version with lots of bug fixes
    * Changelog: http://pmd.sourceforge.net/pmd-<version>/overview/changelog.html
    * Download: https://sourceforge.net/projects/pmd/files/pmd/<version>/
    * Fixed Bugs: https://sourceforge.net/p/pmd/bugs/milestone/PMD-<version>/
    * Documentation: http://pmd.sourceforge.net/pmd-<version>/

*   Facebook, Google+, Twitter, LinkedIn and Xing (add whatever you feel is missing here...)
