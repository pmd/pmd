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

    # First get a copy of the current update site
    rsync -avhP sf-user@web.sourceforge.net:/home/project-web/pmd/htdocs/eclipse/ /location/of/local/update-site/
    # Create a release branch
    git branch pmd-eclipse-plugin-rb-<version>
    # Update master branch to the next -SNAPSHOT version.
    mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=<version+1>-SNAPSHOT
    # Update versions in n.s.p.e.p2updatesite/category.xml
    vim net.sourceforge.pmd.eclipse.p2updatesite/category.xml
    # Commit and push
    git commit -a -m "Prepare next pmd-eclipse-plugin development version <version+1>-SNAPSHOT"
    git push origin master
    
    # Checkout the release branch
    git checkout pmd-eclipse-plugin-rb-<version>
    # Pick a release BUILDQUALIFIER (e.g. v20130420-0001) and update versions
    mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=<version>.BUILDQUALIFIER
    # Update versions in n.s.p.e.p2updatesite/category.xml
    vim net.sourceforge.pmd.eclipse.p2updatesite/category.xml
    # Commit and tag
    git commit -a -m "Prepare release pmd-eclipse-plugin <version>.BUILDQUALIFIER"
    git tag pmd-eclipse-plugin/<version>.BUILDQUALIFIER
    # Build the plugin
    mvn clean install -Ppublish-to-update-site -Declipse.updatesite.path=/location/of/local/update-site/

    # Test the new update site with eclipse - it should contain the new version
    # If everything is fine, push the local changes
    git push origin master
    git push origin tag pmd-eclipse-plugin/<version>.BUILDQUALIFIER
    # upload the official update site
    rsync -avhP /location/of/local/update-site/ sf-user@web.sourceforge.net:/home/project-web/pmd/htdocs/eclipse/
    rsync -avhP /location/of/local/update-site/ sf-user@web.sourceforge.net:/home/frs/project/pmd/pmd-eclipse/update-site/
    # Cleanup the release branch which was only needed during the release process
    git branch -D pmd-eclipse-plugin-rb-<version>


Updating the used PMD version
-----------------------------
The parent pom contains the property `pmd.version`. This is used inside the plugin module, to resolve the dependencies.
In order to change the PMD version, change this property and rebuild (`mvn clean package`). In case PMD has some
changed (added/removed) transitive dependencies, you'll need to update `n.s.p.e.plugin/META-INF/MANIEFEST.MF` as well.
All transitive dependencies are copied into the folder `n.s.p.e.plugin/target/lib` during the build.


Useful References
-----------------
* http://wiki.eclipse.org/Equinox/p2/Publisher
* http://wiki.eclipse.org/Equinox_p2_Repository_Mirroring
* http://wiki.eclipse.org/Category:Tycho
* http://wiki.eclipse.org/Tycho/Additional_Tools
* http://codeiseasy.wordpress.com/2012/07/26/managing-a-p2-release-repository-with-tycho/
* http://wiki.eclipse.org/Tycho/Demo_Projects
* http://wiki.eclipse.org/Tycho/Reference_Card
* http://eclipse.org/tycho/sitedocs/index.html
* https://docs.sonatype.org/display/M2ECLIPSE/Staging+and+releasing+new+M2Eclipse+release
* http://wiki.eclipse.org/Tycho/Packaging_Types
* http://wiki.eclipse.org/Tycho/Reproducible_Version_Qualifiers
* http://www.vogella.com/articles/EclipseTycho/article.html
* http://git.eclipse.org/c/tycho/org.eclipse.tycho-demo.git/tree/itp01/tycho.demo.itp01.tests/pom.xml
* http://www.sonatype.com/people/2008/11/building-eclipse-plugins-with-maven-tycho/
* http://zeroturnaround.com/labs/building-eclipse-plug-ins-with-maven-3-and-tycho/
* https://github.com/open-archetypes/tycho-eclipse-plugin-archetype
* http://wiki.eclipse.org/Tycho/How_Tos/Dependency_on_pom-first_artifacts
