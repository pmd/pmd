PMD Eclipse Plugin
==================

Compilation
-----------
Simply run `mvn clean install`. The plugin's update site will be generated in
`net.sourceforge.pmd.eclipse.p2updatesite/target/repository`. You can use this directory as
an update site to install the new plugin version directly into your Eclipse.

Importing the projects in Eclipse
---------------------------------
Make sure you have the Maven Integration (m2e - http://eclipse.org/m2e/) installed. Then you can
import *Existing Maven Projects*.
You should see 6 projects:

* net.sourceforge.pmd.eclipse - that's the feature
* net.sourceforge.pmd.eclipse.p2updatesite - generates the update site
* net.sourceforge.pmd.eclipse.parent - the parent pom project
* net.sourceforge.pmd.eclipse.plugin - the actual plugin code
* net.sourceforge.pmd.eclipse.plugin.test - the (unit) tests for the plugin
* net.sourceforge.pmd.eclipse.plugin.test.fragment - an example extension of the plugin used during the tests


Releasing and updating the official eclipse update site
-------------------------------------------------------

    # Pick a release BUILDQUALIFIER (e.g. v20130420-0001) and update versions
    E.g. version is: "4.0.0" and BUILDQUALIFIER is "v20130420-0001".
    The complete version of the plugin will be "4.0.0.v20130420-0001
    export BUILDQUALIFIER=$(date -u +v%Y%m%d-%H%M) && echo $BUILDQUALIFIER
    
    # Pick the version of the new release and the next development version
    export VERSION=4.0.0
    export NEXT=4.0.1
    
    # Define the location of the local copy of the update site
    export UPDATE_SITE_MIRROR=/location/of/local/update-site/
    
    # Define your sourceforge login
    export SFUSER=sf-user
    
    # First get a copy of the current update site
    rsync -avhP $SFUSER@web.sourceforge.net:/home/frs/project/pmd/pmd-eclipse/update-site/ $UPDATE_SITE_MIRROR
    # Create a release branch
    git branch pmd-eclipse-plugin-rb-$VERSION
    # Update master branch to the next -SNAPSHOT version.
    mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEXT-SNAPSHOT
    # Update versions in n.s.p.e.p2updatesite/category.xml
    sed -i -e "s/$VERSION.qualifier/$NEXT.qualifier/" net.sourceforge.pmd.eclipse.p2updatesite/category.xml
    
    # Update the ReleaseNotes with the release date and version and add a next version entry
    vim ReleaseNotes.md
    # Commit and push
    git commit -a -m "Prepare next pmd-eclipse-plugin development version $NEXT-SNAPSHOT"
    git push origin master
    
    # Checkout the release branch
    git checkout pmd-eclipse-plugin-rb-$VERSION
    # update versions with the BUILDQUALIFIER
    mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$VERSION.$BUILDQUALIFIER
    # Update versions in n.s.p.e.p2updatesite/category.xml
    sed -i -e "s/$VERSION.qualifier/$VERSION.$BUILDQUALIFIER/" net.sourceforge.pmd.eclipse.p2updatesite/category.xml
    # Update the ReleaseNotes with the release date and version
    vim ReleaseNotes.md
    # Commit and tag
    git commit -a -m "Prepare release pmd-eclipse-plugin $VERSION.$BUILDQUALIFIER"
    git tag pmd-eclipse-plugin/$VERSION.$BUILDQUALIFIER
    # Build the plugin
    mvn clean install -Ppublish-to-update-site -Declipse.updatesite.path=$UPDATE_SITE_MIRROR

    # Test the new update site with eclipse - it should contain the new version
    # If everything is fine, push the local changes
    git push origin master
    git push origin tag pmd-eclipse-plugin/$VERSION.$BUILDQUALIFIER
    # upload the official update site
    rsync -avhP $UPDATE_SITE_MIRROR $SFUSER@web.sourceforge.net:/home/frs/project/pmd/pmd-eclipse/update-site/
    # Cleanup the release branch which was only needed during the release process
    git checkout master
    git branch -D pmd-eclipse-plugin-rb-$VERSION

Finally announce the new plugin version in the news section of SF: <https://sourceforge.net/p/pmd/news/>.
You can use the following template:

    PMD for Eclipse $VERSION.$BUILDQUALIFIER released
    
    A new PMD for Eclipse plugin version has been released.
    It is available via the update site: <https://sourceforge.net/projects/pmd/files/pmd-eclipse/update-site/>
    
    * Release Notes: <https://github.com/pmd/pmd/blob/pmd-eclipse-plugin/$VERSION/pmd-eclipse-plugin/ReleaseNotes.md>





Updating the used PMD version
-----------------------------
The parent pom contains the property `pmd.version`. This is used inside the plugin module, to resolve the dependencies.
In order to change the PMD version, change this property and rebuild (`mvn clean package`). In case PMD has some
changed (added/removed) transitive dependencies, you'll need to update `n.s.p.e.plugin/META-INF/MANIEFEST.MF` as well.
All transitive dependencies are copied into the folder `n.s.p.e.plugin/target/lib` during the build.


Useful References
-----------------

* <http://wiki.eclipse.org/Equinox/p2/Publisher>
* <http://wiki.eclipse.org/Equinox_p2_Repository_Mirroring>
* <http://wiki.eclipse.org/Category:Tycho>
* <http://wiki.eclipse.org/Tycho/Additional_Tools>
* <http://codeiseasy.wordpress.com/2012/07/26/managing-a-p2-release-repository-with-tycho/>
* <http://wiki.eclipse.org/Tycho/Demo_Projects>
* <http://wiki.eclipse.org/Tycho/Reference_Card>
* <http://eclipse.org/tycho/sitedocs/index.html>
* <https://docs.sonatype.org/display/M2ECLIPSE/Staging+and+releasing+new+M2Eclipse+release>
* <http://wiki.eclipse.org/Tycho/Packaging_Types>
* <http://wiki.eclipse.org/Tycho/Reproducible_Version_Qualifiers>
* <http://www.vogella.com/articles/EclipseTycho/article.html>
* <http://git.eclipse.org/c/tycho/org.eclipse.tycho-demo.git/tree/itp01/tycho.demo.itp01.tests/pom.xml>
* <http://www.sonatype.com/people/2008/11/building-eclipse-plugins-with-maven-tycho/>
* <http://zeroturnaround.com/labs/building-eclipse-plug-ins-with-maven-3-and-tycho/>
* <https://github.com/open-archetypes/tycho-eclipse-plugin-archetype>
* <http://wiki.eclipse.org/Tycho/How_Tos/Dependency_on_pom-first_artifacts>
