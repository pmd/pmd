---
title: Building PMD with VS Code
tags: [devdocs]
permalink: pmd_devdocs_building_vscode.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.10.0)
---

{%include warning.html content="It is not recommend to use VS Code for developing. See Known Issues below."%}

## Import PMD Project

This needs to be done only once.

1. Install [Visual Studio Code](https://code.visualstudio.com/)
2. Select File > Open Folder... and choose the already checked out PMD source folder.
3. Install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
4. After a while, you should see in the Explorer under "Java Projects" all the modules. This is the "Java Project Explorer".

## Running Unit Tests

* Navigate to a test class and select "Run Test".

{%capture notetext%}
You'll get the notification "Build failed". You can try to ignore the error and continue, but you might end
up with "Unresolved compilation problems". See below under "Known Issues" for more information.

PMD currently is still built for Java 8 and the VS Code plugin chooses a Java 8 runtime. For building however,
we use Java 11 and also the tests require some Java 11 features. You might need to override the java version
manually: Ctlr+Shift+P and enter "configure java runtime". There you can override the java version for
each project/module.
{%endcapture%}
{%include note.html content=notetext %}

## Running / Debugging PMD

* Navigate to the class `PmdCli`.
* Click on the "Run" link just above the `main`-method.
* To customize the options, open the "Run and Debug" window (Ctlr+Shift+D) and click the link "create a launch.json
  file". Add the following configuration:
  ```json
        {
            "type": "java",
            "name": "PmdCli with args",
            "request": "launch",
            "mainClass": "net.sourceforge.pmd.cli.PmdCli",
            "projectName": "pmd-cli",
            "args": "check --help",
            "classPaths": ["$Test"]
        },
  ```
* Select the run configuration "PmdCli with args" in the dropdown and use File > Start Debugging or
  File > Run Without Debugging.

{%capture notetext %}
If you want to run/debug other PMD modules than PMD Java, then you need to add additional dependencies to
PMD CLI as scope test, e.g.

```xml
        <dependency>
            <groupId>net.sourceforge.pmd</groupId>
            <artifactId>pmd-apex</artifactId>
            <version>${project.version}</version>
            <scope>test</scope>
        </dependency>
```
{%endcapture%}
{%include note.html content=notetext%}


## Formatter configuration

Select File > Preferences > Settings and search for "java format settings". Then enter the full path to
`eclipse/pmd-eclipse-code-formatter.xml` from the "build-tools" repository.

## Running the Designer

The designer lives in a separate repository, that you'll need to fork and clone first:
[Designer repository](https://github.com/pmd/pmd-designer)

```shell
git clone git@github.com:your_user_name/pmd-designer.git
```

1. Select File > Add Folder to Workspace... and choose the pmd-designer source folder.
2. Wait until the project has been successfully imported.
3. Navigate to class `DesignerStarter` and run it.

## Known Issues
* Some Java files in PMD cannot be compiled with VS Code. The Java plugin for VS Code is based on the
  Eclipse Java Plugin which uses an own compiler (ejc). This compiler
  has some subtle differences when dealing with type inference and generics and sometimes cannot compile PMD source code
  correctly. PMD source is valid, it compiles with openjdk, but just not with ecj.

  You'll get the notification "Build failed". You can try to ignore the error and continue, but you might end
  up with "Unresolved compilation problems".
* There is no Kotlin support. As some parts of PMD use Kotlin, there is no
  IDE support.
* There is no recent checkstyle plugin for Netbeans.
