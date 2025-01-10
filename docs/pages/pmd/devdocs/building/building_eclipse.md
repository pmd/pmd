---
title: Building PMD with Eclipse
tags: [devdocs]
permalink: pmd_devdocs_building_eclipse.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.10.0)
---

## Import PMD Project

1. Download the latest [Eclipse IDE for Enterprise Java and Web Developers](http://www.eclipse.org/downloads/eclipse-packages/)
2. Install it by extracting it into a directory of your choice
3. Start `eclipse`. You'll be asked for a workspace. It's recommended to use an extra, separate new workspace for PMD,
   because you'll import many projects. Otherwise you might clutter your existing workspace with PMD projects.
4. Close the welcome screen, if it is shown.
5. Select menu `File`, `Import...`. In the dialog, select `Maven / Existing Maven Projects` and click `Next`.
6. As `Root Directory` select the directory, into which PMD's repository has been cloned.
7. Then click `Select All` and then `Finish`.

Now all PMD projects are imported. This might take a while. All the projects will appear on the left inside
the `Project Explorer`.

While PMD still runs with Java 8, we need at least Java 11 for building and running unit tests.
To make sure, Eclipse uses Java 11 instead of Java 8, go to Window > Preferences and select
Java > Installed JREs. Make sure, you have a Java 11 or newer available. Then go to "Execution Environments",
choose "JavaSE-1.8" and check your Java 11 installation on the right.

## Running unit tests

To verify, that the basics work, right-click on the `pmd-core` project and select `Run As -> JUnit Test`.
If everything is well, the project will be built and the unit tests are executed.

If you do this for other projects, you might get some build errors. Try to ignore them for now - at least some unit test
should be executed. See also "Known Issues" below.

## Running / Debugging PMD

* Navigate to class `PmdCli`
* Right-Click and select "Run As -> Java Application".
* Ignore any build errors for now and choose "Proceed".
* In the console you should see the output from PMD.
* Now you have a run configuration: Modify this through menu "Run > Run Configurations..."
  * In tab arguments: use `check --help` as program arguments
  * In tab dependencies you can add the modules of languages you want to test with to the classpath, eg. pmd-java
* Instead of run, you can use the same configuration for debugging.

## Code Templates, Formatter

1. Menu `Window` -> `Preferences`
2. Under `Java / Code Style / Code Templates`:
   Click `Import...` and choose the file `eclipse/pmd-eclipse-codetemplates.xml` from the "build-tools" repository.
3. Under `Java / Code Style / Formatter`:
   Click `Import...` and choose the file `eclipse/pmd-eclipse-code-formatter.xml` from the "build-tools" repository.
4. Under `Java / Code Style / Clean Up`:
   Click `Import...` and choose the file `eclipse/pmd-eclipse-code-cleanup.xml` from the "build-tools" repository.
5. Under `Java / Code Style / Organize Imports`:
   Click `Import...` and choose the file `eclipse/pmd-eclipse-imports.importorder` from the build-tools" repository.
6. Click `Apply and Close`

## Checkstyle

We are going to install two plugins: The checkstyle plugin itself and the m2e-code-quality plugin, which seamlessly activates and configures checkstyle in eclipse according to the maven configuration of PMD.

1.  Menu `Help`, `Install New Software...`
2.  Enter the URL `https://checkstyle.org/eclipse-cs-update-site` into the text field and press enter.
3.  Select the checkbox for "Checkstyle" and click `Next`
4.  Restart eclipse if you are requested to do so.
5.  Install the next plugin for the URL `https://m2e-code-quality.github.io/m2e-code-quality-p2-site/`
6.  This time, select only "Checkstyle configuration plugin for M2Eclipse" and click `Next`
7.  Restart eclipse if you are requested to do so.
8.  Finally, right click on one project in the package explorer. Select `Maven / Update project...` in the context
    menu. In the dialog, select all projects, unselect the option "Clean projects" and click `OK`.


## Other settings

*   Consider displaying the white space characters: Window, Preferences; General / Editors / Text Editors: Show whitespace characters
*   Insert spaces for tabs also in text files: Window, Preferences; General / Editors / Text Editors: Insert spaces for tabs

## Running the designer

The designer lives in a separate repository, that you'll need to fork and clone first:
[Designer repository](https://github.com/pmd/pmd-designer)

```shell
git clone git@github.com:your_user_name/pmd-designer.git
```

See [Contributing Guide](https://github.com/pmd/pmd-designer/blob/master/CONTRIBUTING.md) of the Designer for details.

* Import the designer project via menu `File`, `Import...`. In the dialog, select `Maven / Existing Maven Projects` and click `Next`.
* Open the class `net.sourceforge.pmd.util.fxdesigner.DesignerStarter` via menu "Navigate / Open Type...".
* Right click in the editor window and select "Run as -> Java Application".

## Known Issues

* Eclipse is very slow without making progress when building the complete PMD project.
  * It could be, that eclipse runs out of memory. Open `eclipse.ini` and replace `-Xmx2048m` with `-Xmx4096m`. Restart eclipse.
  * Close the other languages modules, that you are not interested in. The fewer projects that are open the better
    for eclipse performance.
  * Temporarily disable "Project > Build Automatically" and build the project using Maven: Run `./mvnw compile` and
    after that select the top level project in Project Explorer ("pmd") and use "File > Refresh". Then enable
    "Build Automatically" again.

* There is no good Kotlin support for Eclipse. Many tests depend on Kotlin and at least pmd-apex uses Kotlin
  for the main sources. This means, you need to compile the project with Maven without Eclipse.

  As long as you don't need to change the Kotlin code, you can ignore this for now.
  In order to be able to execute the tests, there is a workaround:

    1. Build the project "pmd-lang-test" once from outside eclipse: `$ ./mvnw clean install -f pmd-lang-test/pom.xml`
       This installs this module in your local maven repository.
    2. In eclipse, close the project "pmd-lang-test". That way, eclipse will use the jar file (which contains
       the already compiled kotlin base test classes) from the local maven repository instead.

* pmd-scala project has no source code / tests: If you don't want to work on scala, then you can simply
  close all scala projects.

  For scala, there are two versions: 2.12 and 2.13. Both share the same code, which is in
  `pmd-scala-modules/pmd-scala-common`. However, this code is not used directly, but referenced from the two
  projects `pmd-scala-moduls/pmd-scala_2.12` and `pmd-scala-moduls/pmd-scala_2.13`. When working on scala,
  it is recommended to close pmd-scala_2.12 and only work on pmd-scala_2.13. Then you need to configure
  "pmd-scala_2.13" manually, so that eclipse finds the source folders:

    1. Right-click on the project "pmd-scala_2.13" and open the "Properties". On the left navigate to "Java Build Path".
    2. Open the tab "Source"
    3. Delete the all source folders
    4. Click on "Link Source..." and manually choose via "Linked folder location" the path in the repository
       to `pmd-scala-modules/pmd-scala-common/src/main/java`. Name it "src-main-java" and click "Finish".
    5. Repeat it for `pmd-scala-modules/pmd-scala-common/src/main/resources`. Name it "src-main-resources".
    6. Repeat it for `pmd-scala-modules/pmd-scala-common/src/test/java` and `pmd-scala-modules/pmd-scala-common/src/test/resources`.
    7. Change "src-test-java" to "Contains test sources: Yes", select "Source Output Folder > Specific Output Folder" and
       choose "target/test-classes".
    8. Repeat this for "src-test-resources".

