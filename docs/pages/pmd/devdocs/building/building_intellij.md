---
title: Building PMD with IntelliJ IDEA
tags: [devdocs]
permalink: pmd_devdocs_building_intellij.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>, Clément Fournier <clement.fournier76@gmail.com>
---

## Import PMD Project

1. Download the community edition of IntelliJ IDEA from [jetbrains](https://www.jetbrains.com/idea/download/).
2. Install it, that means, extract the archive
3. Start it with `bin/idea.sh`
4. In the startup dialog, choose the button `Open` and select the folder, into which PMD's repository has been cloned.
   Then select "Trust Project". After that, IJ will automatically detect that PMD is a maven project and import it.
   Make sure, you have previously built PMD on command line via `./mvnw clean verify`, as described in the preparation
   step.
5. If you are using IJ for the first time, you'll need to configure the path to your installed Java SDK. You
   have to use at least Java 9 here.
   If the dialog doesn't show automatically, you can open it with menu `File > Project Structure` (CTRL+ALT+SHIFT+S).
   On the left, choose "Platform Settings > SDK" and add your Java SDK. Then choose "Project Settings > Project" and
   select the SDK for PMD.
6. Finish the wizard and wait a bit, until all PMD modules appear on the left.

## Running Unit Tests

* Right click on "pmd-java" and select "Run 'All Tests'"
* You can run individual unit tests or a single unit test class by right-clicking, or CTRL + SHIFT + F10.
  You can debug the current Run configuration using SHIFT + F9.

## Setting up Checkstyle

If you don't have the Checkstyle plugin:

* Open `File > Settings` (CTRL+ALT+S) and navigate to "Plugins" on the left
* Click on "Browse repositories" on the bottom
* Search for "Checkstyle-IDEA" and click "Install"
* When done, restart IJ

Once you have the Checkstyle plugin:

* Open `File > Settings` (CTRL+ALT+S) and navigate to "Tools > Checkstyle" on the left
* Make sure to select the latest Checkstyle version and click Apply
* Add a configuration file with the Add button on the right
* Choose a descriptive name, then tick "Use a Checkstyle file accessible via HTTP" and enter the following URL:
    ```
    https://raw.githubusercontent.com/pmd/build-tools/master/src/main/resources/net/sourceforge/pmd/pmd-checkstyle-config.xml
    ```
* Tick the "Active" checkbox for this added checkstyle configuration

## Formatter and inspection configuration

Import the code style settings (formatter) so that it conforms with our Checkstyle config.
To do that, go to `File > Settings` (CTLR+ALT+S) then navigate to "Editor > Code Style > Java".
Click on the cogwheel symbol, choose "Import Scheme > IntelliJ IDEA code style XML" and choose the file
`intellij-idea/PMD-code-style.xml` from the build-tools repository.

Take some time to tweak the inspections so that they conform to the code style, for example flagging switch statements
with no `default` case. This takes some time but can make your code much cleaner.
To do that, go to `File > Settings` then navigate to "Inspections > Java".

## Running the designer

The designer lives now in a separate repository, that you'll need to clone first:

``` shell
$ git clone https://github.com/pmd/pmd-designer.git
```

See [Contributing Guide](https://github.com/pmd/pmd-designer/blob/master/CONTRIBUTING.md) of the Designer for details.

* We recommend creating and saving a new run configuration (see
  [IJ doc](https://www.jetbrains.com/help/idea/creating-and-editing-run-debug-configurations.html)),
  using the "Application" template. On IJ < 2017.3, there is also a "JavaFX Application" template.
* When editing the run configuration, add `-v` to the program arguments, to get debug output
* Be sure to use the classpath of the `pmd-ui` module

## Known Issues
* Some compilation errors: If you didn't build PMD from command line outside of IDEA, then the sources,
  that are usually generated during the build, are not available. You can right-click on the PMD project and
  select "Maven > Generate Sources and Update Folders". It seems, that IDEA doesn't use the correct JDK when
  executing this command. Try again - it seems, it works on the second try only. After that, the folder are there,
  but IDEA doesn't use them. Select "Maven > Reimport".
* When editing FXML or CSS files for the designer, IJ sometimes fails to put the updated version in the classpath
  when running. You need to run `mvn process-resources -pl pmd-ui`.
* As a quickfix for the two problems above, `mvn compile` is quick to execute when your Maven dependency cache
  is up-to-date.
