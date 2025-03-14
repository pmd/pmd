---
title: The rule designer
short_title: Rule designer
tags: [extending, userdocs]
summary: "Learn about the usage and features of the rule designer."
last_updated: March 2024 (7.0.0)
permalink: pmd_userdocs_extending_designer_reference.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

## Installing, running, updating

The designer is part of PMD's binary distributions. To **install a distribution**, see the
[documentation page about installing PMD](pmd_userdocs_installation.html).

The designer still works with Java 8 from Oracle, which includes JavaFX. If you use this Java version, then
all is set. However, it is recommended to use OpenJDK along with OpenJFX. The recommended Java Runtime is
Java 11 (or later) with OpenJFX 17 (or later).

You can get OpenJDK from [Adoptium](https://adoptium.net), [Azul](https://www.azul.com/downloads/#zulu),
[Microsoft](https://learn.microsoft.com/en-us/java/openjdk/download), [SAP](https://sap.github.io/SapMachine/),
[Amazon](https://downloads.corretto.aws/#/overview) and other OpenJDK vendors.
Note: Azul provides a JDK which includes JavaFX - this variant is currently not supported. You always need
to install OpenJFX separately.

[OpenJFX](https://openjfx.io/) is available from [JavaFX download page](https://gluonhq.com/products/javafx/).
You need the SDK. Extract the zip file, and set the `JAVAFX_HOME` environment variable to the extracted
directory. It should be the directory, that contain the sub-folder "lib" in it.

Example (for linux x64 only, with Java 21 and OpenJFX 21):

```shell
$ mkdir $HOME/openjdk
$ cd $HOME/openjdk
$ wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz
$ tar xfz OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz
$ export JAVA_HOME=$HOME/openjdk/jdk-21.0.2+13
$ export PATH=$JAVA_HOME/bin:$PATH
$ wget https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip
$ unzip -q openjfx-21.0.2_linux-x64_bin-sdk.zip
$ export JAVAFX_HOME=$HOME/openjdk/javafx-sdk-21.0.2
```

If the bin directory of your PMD distribution is on your shell's path, then you can then **launch the app** with

{% include cli_example.html
   id="pmd"
   linux="pmd designer"
   windows="pmd.bat designer" %}


{% include note.html content="pmd-designer.jar is not a runnable jar, because it doesn't include any PMD language module, or PMD Core. " %}


This is to allow easy updating, and let you choose the dependencies you're interested in.
The available language modules are those on the classpath of the app's JVM. That's why it's recommended to use the
standard PMD startup scripts, which setups the classpath with the available PMD libraries.


### Updating

The latest version of the designer currently **works with PMD 7.0.0 and above**. You can simply replace
pmd-designer-7.X.Y.jar with the [latest build](https://github.com/pmd/pmd-designer/releases) in the installation
folder of your PMD distribution, and run it normally. Note that updating may cause some persisted state
to get lost, for example the code snippet.


# Usage reference


The rule designer is both a tool to inspect the tree on which PMD rules run on, and to write XPath rules
in an integrated manner. This page describes the features that enable this.


## AST inspection


![Designer top UI](images/designer/designer-top.png)


You can enter source code in the middle zone.
* Make sure to select the correct language and version for your source code:
   * Language is set app-wide with the blue button in the menu-bar
   * If the language has several language versions, you can select a specific one with the choicebox just above the code area
* If the source is valid using this setting, the tree to the right will update to display the AST of the code
* When selecting a node, the left panel updates with information about a node

### Selecting nodes

There are several ways to focus a node for inspection:
* **From the tree view:** just click on an item
   * Since 6.16.0, the tree view is also searchable: press <kbd>Ctlr</kbd>+<kbd>F</kbd> when it's focused,
     or click on the `Search` button and enter a search query. You can cycle through results with
     <kbd>Ctrl</kbd>+<kbd>Tab</kbd> or <kbd>Ctrl</kbd>+<kbd>F3</kbd>, and cycle back with
     <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>Tab</kbd> or <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>F3</kbd>.
* **From the crumb bar:** the crumb bar below the code area shows the ancestors of the currently selected node,
  and is empty if you have no selection:

{% details Ancestor crumb bar demo %}

![Ancestor crumb bar demo](images/designer/parents-bar.gif)

{% enddetails %}


* **From the source code:** maintain <kbd>Ctrl</kbd> for a second until the code area becomes mostly blue.
  Then, each node you hover over on the code area will be selected automatically. Example:

{% details Ctrl-hover selection demo %}

![CTRL-hover selection demo](images/designer/hover-selection.gif)

{% enddetails %}

### Node inspection

The left panel displays the following information:

* **XPath attributes:** this basically are all the attributes available in XPath queries. Those attributes are
  wrappers around a Java getter, so you can obtain documentation on the relevant Javadoc (that's not yet
  integrated into the designer)
* **Metrics:** for nodes that support it, the values of metrics are displayed in this panel
* **Scopes:** This is java specific and displays some representation of the symbol table. You mostly don't need
  it. If you select e.g. a variable id, its usages are already highlighted automatically without opening the panel:

![Usages highlight example](images/designer/usages.gif)


## XPath rule design


The bottom part of the UI is dedicated to designing XPath rules:

![Bottom UI](images/designer/bottom-ui.png)


The center is an XPath expression. As you type it, the matched nodes are updated on the right, and highlighted
on the code area. Autocompletion is available on some languages.

Note: you can keep several rules in the editor (there's a tab for each of them).

### Rule properties

Above the XPath expression area, the **"Properties"** button allows you to
[define new properties](pmd_userdocs_extending_defining_properties.html#for-xpath-rules) for your prototype rule.
You can also edit the existing properties.

When you click on it, a small popup appears:

![Property definition popup](images/designer/property-defs.png)

The popup contains in the center a list of currently defined properties, displaying their name and expected type.

* **Adding**: the "Add property" button adds a row to the table
* **Deleting**: each item has a "Trash" button to delete the property
* **Editing**: each property can be further edited by clicking on the "Ellipsis" button on the right

#### Editing properties

The edit menu of a property looks like the following:

![Property edition popup](images/designer/property-edit.png)

* You can edit the name, description, expected type, and default value of the property
* All this information is exported with the rule definition (see [Exporting to an XML rule](#exporting-to-an-xml-rule))
* The default value is used unless you're editing a test case, and you set a custom value for the test case
  (see [Testing a rule](#testing-a-rule))

### Exporting to an XML rule

The little **export icon** next to the gear icon opens a menu to export your rule. This menu lets you fill-in the
metadata necessary for an XPath rule to be included in a ruleset.

{% details Rule export demo %}


![Rule export demo](images/designer/export-example.gif)

{% enddetails %}

## Testing a rule

PMD has its own XML format to describe rule tests and execute them using our test framework. The designer includes
a test editor, which allows you to edit such files or create a new one directly as you edit the rule.
This is what the panel left of the XPath expression area is for.

See also [the test framework documentation](pmd_userdocs_extending_testing.html).

### Testing model

A rule test describes
* the configuration of the rule
* the source on which to run
* the expected violations
* a description (to name the test)

When executing a test, the rule is run on the source with the given configuration, then the violations it finds
are compared to the expected ones.

### Adding tests

Tests can be added in one of four ways:
* **From an XML file:** if you already have a test XML file for your tests, you can load all the tests it defines easily.

{% details Test import demo %}

![Test import example](images/designer/tests/import.gif)

{% enddetails %}


* **From the current source:** A new test case with a default configuration is created, with the source that is
  currently in the editor

* **With an empty source:** A new test case with a default configuration is created, with an empty source file.
  You must edit the source yourself then.

* **From an existing test case:** Each test case list item has a "Copy" button which duplicates the test and loads
  the new one.

### Test status

In the designer, the test panel is a list of test cases. Their status (passing, failing, error, unknown) is color coded. 

{% details Test status color coding examples %}

All tests passing (green):

![All green](images/designer/tests/all-green.png)

A failing test (orange):

![Failing](images/designer/tests/failing.png)

{% enddetails %}

### Loading a test case

Each test has a piece of source, which you can edit independently of the others, when the test is
**loaded in the editor**. Additional rule configuration options can be chosen when the test is loaded.

Loading is done with the **Load** button:


{% details Test loading demo %}

![Test loading demo](images/designer/tests/load.gif)

{% enddetails %}


Only one test case may be loaded at a time. If the loaded test is unloaded, the editor reverts back to the state
it had before the first test case was loaded.

### Editing a loaded test case

When a test is loaded, *the source you edit in the code area is the source of the test*. Changes are independent
from other tests, and from the piece of source that was previously in the editor.

When a test is loaded, an additional toolbar shows up at the top of the code area:

![Failing](images/designer/tests/toolbar.png)

#### Expected violations

The **"Expected violations"** button is used to add or edit the expected violations.

Initially the list of violations is empty. You can add violations by **dragging and dropping nodes** onto the
button or its popup, from any control that displays nodes. For example:

{% details Adding a violation demo %}


![Add violation gif](images/designer/tests/add-violation.gif)

{% enddetails %}

#### Test case rule configuration

Rule properties can be configured for each test case independently using the **"Property mapping"** button. For example:

{% details Test rule property demo %}

![Configure properties demo](images/designer/tests/property.gif)

{% enddetails %}

This configuration will be used when executing the test to check its status.

### Exporting tests

When you're done editing tests, it's a good idea to save the test file to an XML file. Exporting is done using
the **"Export"** button above the list of test cases:

{% details Test export demo %}

![Test export demo](images/designer/tests/export.gif)

{% enddetails %}

Note that the exported file does not contain any information about the rule. The rule must be in a ruleset file
somewhere else.

If you want to use PMD's test framework to use the test file in your build, please refer to the conventions
explained in [the test framework documentation](pmd_userdocs_extending_testing.html#where-to-place-the-test-code).

