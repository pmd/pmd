---
title: IDE Plugins
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_ide_plugins.html
author: David Dixon-Peugh <dpeugh@users.sourceforge.net>, Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: February 2026 (7.22.0)
---

## IDE Integrations

### General comments

A general note - most plugins include the PMD jar files, which has the rulesets
inside it.  So even though the rulesets parameter that some plugins
use (i.e., "category/java/bestpractices.xml") looks like a filesystem reference, it's really
being used by a getResourceAsStream() call to load it out of the PMD jar files.

### Summary

Status column:
* 🟢 = Supports at least PMD 7, latest release not older than 12 months.
* 🔴 = Doesn't support PMD 7 or the latest release is older than 12 months, indicating that this plugin is not actively maintained anymore.

| IDE                                                                                              | Plugin Name / Marketplace Link                                                                                   | Status | Homepage                                                  | Source Code                                                                                         | More Info                                   |
|--------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|:-------|-----------------------------------------------------------|-----------------------------------------------------------------------------------------------------|---------------------------------------------|
| [Apache NetBeans](https://netbeans.apache.org/index.html)                                        | SQE - Software Quality Environment                                                                               | 🔴     | [sqe-team](https://sqe-team.github.io/)                   | [github: sqe-team/sqe](https://github.com/sqe-team/sqe)                                             |                                             | 
| [Apache NetBeans](https://netbeans.apache.org/index.html)                                        | EasyPmd                                                                                                          | 🔴     |                                                           | [github: giancosta86/EasyPmd](https://github.com/giancosta86/EasyPmd)                               |                                             | 
| [BlueJ](https://bluej.org/)                                                                      | pmd-bluej                                                                                                        | 🔴     |                                                           | [github: pmd/bluej](https://github.com/pmd/pmd-bluej)                                               | [↓ see below](#bluej)                       |
| [Eclipse IDE](https://eclipseide.org/)                                                           | [eclipse-pmd](https://marketplace.eclipse.org/content/eclipse-pmd)                                               | 🟢     | [eclipse-pmd](https://eclipse-pmd.acanda.ch/)             | [github: eclipse-pmd/eclipse-pmd](https://github.com/eclipse-pmd/eclipse-pmd)                       |                                             |
| [Eclipse IDE](https://eclipseide.org/)                                                           | [pmd-eclipse-plugin](https://marketplace.eclipse.org/content/pmd-eclipse-plugin)                                 | 🟢     |                                                           | [github: pmd/pmd-eclipse-plugin](https://github.com/pmd/pmd-eclipse-plugin)                         | [↓ see below](#pmd-eclipse-plugin)          |
| [Eclipse IDE](https://eclipseide.org/)                                                           | qa-eclipse-plugin                                                                                                | 🔴     |                                                           | [github: ChristianWulf/qa-eclipse-plugin](https://github.com/ChristianWulf/qa-eclipse-plugin)       |                                             |
| [Emacs](https://www.gnu.org/software/emacs/)                                                     | pmd-emacs                                                                                                        | 🔴     |                                                           | [github: pmd/pmd-emacs](https://github.com/pmd/pmd-emacs/)                                          |                                             |
| [Emacs](https://www.gnu.org/software/emacs/)                                                     | emacs-pmd                                                                                                        | 🔴     | [emacs-pmd](http://yrk.nfshost.com/projects/emacs-pmd/)   | [yrk.nfshost.com/projects/emacs-pmd/](http://yrk.nfshost.com/projects/emacs-pmd/)                   | [↓ see below](#emacs)                       |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                 | [PMD X](https://plugins.jetbrains.com/plugin/28935-pmd-x)                                                        | 🟢     |                                                           | [github: xdev-software/intellij-plugin-pmd](https://github.com/xdev-software/intellij-plugin-pmd)   |                                             |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                 | [PMD](https://plugins.jetbrains.com/plugin/1137-pmd)                                                             | 🟢     |                                                           | [github: amitdev/PMD-Intellij](https://github.com/amitdev/PMD-Intellij)                             |                                             |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                 | [QAPlug - PMD](https://plugins.jetbrains.com/plugin/4596-qaplug--pmd)                                            | 🔴     | [QA Plug – quality assurance plugin](https://qaplug.com/) |                                                                                                     | [↓ see below](#intellij-idea---qaplug)      |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)                                                 | none - external tool                                                                                             |        |                                                           |                                                                                                     | [↓ see below](#intellij-idea-external-tool) |
| [Oracle JDeveloper](https://www.oracle.com/application-development/technologies/jdeveloper.html) | pmd-jdeveloper                                                                                                   | 🔴     |                                                           | [github: pmd/pmd-jdeveloper](https://github.com/pmd/pmd-jdeveloper)                                 | [↓ see below](#jdeveloper)                  |
| [jEdit](https://www.jedit.org/)                                                                  | [PMDPlugin](https://plugins.jedit.org/plugins/?PMDPlugin)                                                        | 🔴     |                                                           | [sourceforge: jedit/PMDPlugin](https://sourceforge.net/p/jedit/PMDPlugin/ci/master/tree/)           | [↓ see below](#jedit)                       |
| [TextPad](https://www.textpad.com/home)                                                          | none - external tool                                                                                             |        |                                                           |                                                                                                     | [↓ see below](#textpad)                     |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | [Apex PMD](https://marketplace.visualstudio.com/items?itemName=chuckjonas.apex-pmd)                              | 🟢     |                                                           | [github: ChuckJonas/vscode-apex-pmd](https://github.com/ChuckJonas/vscode-apex-pmd)                 |                                             |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | [VSCode Apex PMD](https://marketplace.visualstudio.com/items?itemName=mohanChinnappan.apex-pmd-code-scanner)     | 🔴     |                                                           | [github: mohan-chinnappan-n/vscode-apex-pmd](https://github.com/mohan-chinnappan-n/vscode-apex-pmd) |                                             |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | [PMD CPD](https://marketplace.visualstudio.com/items?itemName=nwcm.pmd-cpd)                                      | 🟢     |                                                           | [github: nwcm/vscode-pmd-cpd](https://github.com/nwcm/vscode-pmd-cpd)                               |                                             |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | [PMD for Java](https://marketplace.visualstudio.com/items?itemName=cracrayol.pmd-java)                           | 🟢     |                                                           | [github: cracrayol/vscode-java-pmd](https://github.com/cracrayol/vscode-java-pmd)                   |                                             |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | [Alibaba Java Coding Guidelines](https://marketplace.visualstudio.com/items?itemName=yangbaopan.vscode-java-p3c) | 🔴     |                                                           |                                                                                                     |                                             |
| [Visual Studio Code](https://code.visualstudio.com/)                                             | vscode-pmd-daemon                                                                                                |        |                                                           | [github: hvbargen/vscode-pmd-daemon](https://github.com/hvbargen/vscode-pmd-daemon)                 |                                             |

### BlueJ

[BlueJ](https://bluej.org/) is a teaching IDE. To install the PMD extension for BlueJ, download
the [PMDExecExt.jar file](https://github.com/pmd/pmd-bluej/releases/latest)
and place it in your `bluej/lib/extensions/` directory.

### PMD Eclipse Plugin

To install the PMD plugin for Eclipse:

*   Start Eclipse and open a project
*   Select "Help"->"Software Updates"->"Find and Install"
*   Click "Next", then click "New remote site"
*   Enter "PMD" into the Name field and <https://pmd.github.io/pmd-eclipse-plugin-p2-site/> into the URL field
*   Click through the rest of the dialog boxes to install the plugin

Alternatively, you can download the latest zip file and follow the above procedures
except for using "New local site" and browsing to the downloaded zip file.

To configure PMD, select "Windows"->"Preferences", then select PMD.

To run PMD, right-click on a project node and select "PMD"->"Check code with PMD".

To run the duplicate code detector, right-click on a project node and
select "PMD"->"Find suspect cut and paste".  The report will be placed in a "reports" directory
in a file called "cpd-report.txt".

To find additional help for other features, please read included help by selecting
Help->Help Contents and browse the "How to..." section in the "PMD Plugin Documentation" book.

After installing an update, if you get an Exception such as
"java.lang.RuntimeException: Couldn't find that class xxxxx",
try deleting the ruleset.xml file in the .metadata/plugins/net.sourceforge.pmd.eclipse directory in your workspace.

To get Eclipse to not flag the @SuppressWarnings("PMD") annotation, look
under the menu headings Java -> Compiler -> Errors/Warnings -> Annotations -> Unhandled Warning Token.


### Emacs

Integration with GNU Emacs is performed through an ELisp package, pmd.el.
It supports two commands, "pmd-current-buffer" and "pmd-current-dir".
The output is captured in a compilation buffer which allows the user to "jump"
directly to the source code position associated with the PMD warnings.


### IntelliJ IDEA External Tool

You can use an integrated plugin or just use it as an IDEA "External Tool".

Here's how to set it up as an "External Tool":

*   Open IDEA and go to File->Settings
*   Click on the "External Tools" icon
*   Click on the Add button
*   Fill in the blocks
    *   Name: PMD
    *   Description: PMD, good for what ails you.
    *   Menu: Select the "Main menu", "Project views", "Editor menu", and "Search results" checkboxes.
    *   Program: `c:\pmd\bin\pmd.bat`
    *   For the next parameter you'll need to plug in the location of your PMD installation
        and the rulesets you want to use
    *   Parameters:
        `-d "$FilePath$" -f ideaj -R rulesets/java/quickstart.xml -P sourcePath="$Sourcepath$" -P classAndMethodName=$FileClass$.method -P fileName=$FileName$`

That's pretty much it. Now you can right click on a source directory and select PMD,
it'll run recursively on the source files, and the results should
be displayed in a window and hyperlinked into the correct file and line of code.  I usually
right-click on the message window title bar and unselect "autohide" so the window doesn't go
away every time I fix something in the code window.


### IntelliJ IDEA - QAPlug

QAPlug is an Intellij IDEA plugin to manage code quality.  It integrates no less than Checkstyle, FindBugs, and PMD.

The plugin is available at <https://qaplug.com/>.

Also available at the JetBrains site, [QAPlug-PMD](http://plugins.jetbrains.com/idea/plugin/4596-qaplug--pmd)
and [QAPlug](http://plugins.jetbrains.com/idea/plugin/4594-qaplug).

### JDeveloper

To install the PMD plugin for JDeveloper:

*   JDeveloper 10.1.2: Download the binary release and unzip it into your jdev/lib/ext directory
*   JDeveloper 10.1.3 upwards: Click "Help", click "Check for Updates"
    *   JDeveloper 10.1.3:
        *   Press "Add" to add a new update center
        *   Name: PMD Update Center
        *   Location: <http://pmd.sourceforge.net/center.xml>
        *   Select Update Center: PMD Update Center
    *   JDeveloper 11 upwards: Select Update Center: Open Source and Partner Extensions
    *   Press Next and select the actual PMD Plugin and install it
*   Restart JDeveloper

To run the PMD plugin for JDeveloper:

*   Open the Tools-&amp;Preferences menu
*   Click on the PMD option
*   Select a couple of rules to try
*   To run PMD, right click on either a file, folder, package, project or workspace and select PMD via
    Toolbar Icon, Context Menu or File Menu
*   Any rule violations should show up in a LogWindow tab at the bottom of the screen


### JEdit

The way I use the JEdit plugin is:

*   Dock the ErrorList by going to Utilities->Global Options->Docking and
    putting ErrorList at the bottom of the screen
*   Open the File Browser if it isn't already open
*   Double-click on a source directory
*   Select Plugins->PMD->Check directory recursively

Note that you can select individual rules by going to Utilities->Global Options->Plugin Options->PMD.
Also, you can change the plugin to prompt you for a directory to check by going to that same menu and
selecting the "Ask for Directory" checkbox.


### TextPad

**Assumptions**

*   The Java Development Kit, version 1.4.2 (versions 1.4 and higher are acceptable) is properly installed
    into your machine, and exists in `D:\java\jdk\_142\`. This means that `D:\java\jdk\_142\bin\java.exe` exists.
*   PMD version 5.0 exists in `D:\java\pmd-bin-{{pmd.site.version}}\`.
    This means that `D:\java\pmd-bin-{{pmd.site.version}}\lib\pmd-{{pmd.site.version}}.jar` (among other jar files
    in the same directory) exist.

**To integrate into TextPad**

1.  In the **Configure** menu, choose **Preferences...**. This opens the Preferences dialog
2.  In the left pane of the Preferences dialog, choose the **Tools** branch by clicking on the *word* "Tools".
3.  On the far right of the dialog, click on the **Add** button, and then select **Program...** from the drop-down.
    This opens the standard Windows Open File dialog.
4.  Type `D:\java\jdk_142\bin\java.exe` and click the **Open** button.  In the center pane of the Preferences dialog,
    an item "Java" has now been added, and is currently selected.
5.  Click the word Java, which makes the word editable. Select the entire word, and type "PMD directory". Press Return.
6.  Repeat steps three through five, but type "PMD file", instead of "PMD directory".
7.  Click **Apply**.
8.  Expand the **Tools** branch (if not already) by clicking on the '`+`' directly to its left.
9.  In the expanded list, select **PMD directory**. This changes the right side of this dialog to the "tool" form.
10. In the "tool" form, enter these parameters:
    *   **Parameters:**  `-classpath D:\java\pmd-bin-{{pmd.site.version}}\lib\pmd-{{pmd.site.version}}.jar;D:\java\pmd-bin-{{pmd.site.version}}\lib\asm-3.2.jar;D:\java\pmd-bin-{{pmd.site.version}}\lib\jaxen-1.1.1.jar net.sourceforge.pmd.PMD -d <i><b>$FileDir</b></i> -f net.sourceforge.pmd.renderers.TextPadRenderer -R E:\directory\my_pmd_ruleset.xml -debug`
    *   **Initial Folder:**  `$FileDir`
    *   **Save all documents first:**  `Checked`
    *   **Capture output:**  `Checked`
    *   **_All other checkboxes_:**  Unchecked
    *   **Regular expression to match output:**  `^\([^(]+\)(\([0-9]+\),`
    *   **Registers/File:** `1`
    *   **Registers/Line:** `2`
11. In the expanded list, select **PMD file**.
12. In the "tool" form, enter the same parameters as above, except replace '`$FileDir`' with '`$File`',
    in the Parameters textbox.
13. To save your work (truly, given a quirk of TextPad), click on **OK**, which closes the Preferences dialog.
    Restart TextPad and re-open the Preferences dialog.
14. Go back to both the "PMD directory" and "PMD file" Tools branches, and replace '`E:\directory\my_pmd_ruleset.xml`'
    with the ruleset of your choice.  For example, `basic`.
15. Go to the **Keyboard** branch in the left pane (above **Tools**), which changes the right side to
    the "keyboard configuration" form.
16. In the **Categories** list box, select **Tools**.
17. In the **Command** list box, select **PMD directory**.
18. Put your cursor into the **Press new shortcut key**, and type your desired key command.
    For example `Ctrl+Page Up`
19. Click **Assign**.
20. In the **Command** list box, select **PMD file**.
21. Put your cursor into the **Press new shortcut key**, and type your desired key command.
    For example `Ctrl+Page Down`
22. Click **Assign**.
23. Save your work again: Click on **OK**, which closes the Preferences dialog, and then restart TextPad.


**To run PMD against a single Java file**

1.  In TextPad, open any Java file.
2.  Click `Ctrl+Page Down`. This opens an empty, read-only text document (titled "Command Results").
    When PMD completes its analysis, this document will be populated with a listing of violated rules
    (or "Command completed successfully" indicating no violations).
3.  Double click any line to go to it.


**To run PMD against a directory of Java files**

1.  In TextPad, open *any* file in the *root* directory you wish to analyze. Unfortunately, you'll need to
    create a dummy file, if no file exists there.
2.  Click `Ctrl+Page Up`. This opens an empty, read-only text document (titled "Command Results").
    When PMD completes its analysis, this document will be populated with a listing of violated rules
    (or "Command completed successfully" indicating no violations).
3.  Double click any line to go to it.

<em>Because directory analysis may take a while, you may choose to cancel this operation. Do so by closing
the (blank Command Results) document, and then confirming that, "yes, I do really want to exit the tool".</em>




## Archived Integrations

These are integrations that are no longer maintained or for IDEs that no longer exist.

### CodeGuide

CodeGuide was a Java IDE by omnicore: <https://web.archive.org/web/20081210063520/http://www.omnicore.com:80/en/codeguide.htm>

### Gel

Gel was once an IDE: <https://archive.org/details/tucows_246670_Gel>

Source code for the PMD plugin is here: <https://github.com/pmd/pmd-misc/tree/main/pmd-gel/>

Here's how to set up the Gel plugin:

*   Download the pmd-gel-[version].zip file
*   Close Gel
*   Remove any old plugin versions from your gel\plugins directory
*   Unzip the new zip file into your gel\plugins directory
*   Start Gel
*   Go to Tools->Options->Plugin
*   Select the PMD plugin and click "Remove"
*   Click "Add" and select "net.sourceforge.pmd.gel.PMDPlugin"
*   Restart Gel

That's pretty much it.  Now you can open a Java project and click on Plugins->PMD and
a configuration panel will pop up.  You can pick which ruleset you want to run and
you can also pick whether you want to run PMD on the current file or on every
source file in your project.

### JBuilder

Was once a IDE by Borland (later Embarcadero): see <https://en.wikipedia.org/wiki/JBuilder> and
<https://web.archive.org/web/20090228184200/http://www.embarcadero.com/products/jbuilder/>

Source code for the plugin is here: <https://github.com/pmd/pmd-misc/tree/main/pmd-jbuilder/>

To enable this OpenTool in JBuilder:

*   Download the [latest binary release](https://sourceforge.net/projects/pmd/files/pmd-jbuilder/)
*   Unzip it into your `jbuilder/lib/ext/` directory
*   Restart JBuilder

What you can do:

*   Check a single file by bringing up the context menu from the file tab and selecting PMDCheck
*   Configure the rulesets that the PMD OpenTool will use by selecting Tools->PMD->Configure PMD
*   Check all the files in a project by bringing up the context menu for
    the project node and selecting PMD Check Project
*   Locate duplicate code by right clicking on a package and selection "Check with CPD"

When running PMD, the results will be displayed in the MessageView under a tab called PMD Results.  If you click on a
violation message within this view, you will be taken to the line in the source code where the violation was detected.

Things still to do:

*   Enable selection of individual rules within a rule set (maybe)
*   Optional insertion of @todo comments at the point of a violation
*   Possibly provide configurable ability to limit the number of violations per rule per file

### JCreator

Was once a IDE: <https://web.archive.org/web/20211018201200/http://www.jcreator.com/>

1.  Open Configure > Options
2.  Go to the Tools panel
3.  Click New > Program
4.  Browse for PMD's pmd.bat
5.  Put quotations around the path if it has spaces.
6.  Set the initial directory to PMD's \bin directory
7.  Check capture output
8.  Put '"$[PrjDir]" emacs' followed by desired rulesets in the arguments

To run PMD on a project, just pick pmd from the Tools menu.

### WebLogic Workshop 8.1.x

That's a plugin for an old version of Bea WebLogic Workshop 8.1.x (which is now available from Oracle).
The new versions are based on Eclipse and don't require this plugin anymore.

Please see [the WebLogic Workshop plugin project home page](http://pmdwlw.sf.net/) for more information.
