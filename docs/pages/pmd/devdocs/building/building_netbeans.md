---
title: Building PMD with Netbeans
tags: [devdocs]
permalink: pmd_devdocs_building_netbeans.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.10.0)
---

## Import PMD Project

This needs to be done only once.

1. Install [Apache Netbeans](https://netbeans.apache.org/index.html)
2. Select File > Open Project... and choose the already checked out PMD source folder.
3. After a while, you should see under "Projects > PMD > Modules" all the modules. You can then
   open individual projects.

## Running Unit Tests

* Right-click on an open project, e.g. "PMD Java" and select "Test". Netbeans then uses maven
  to execute the unit tests.
* You can also run individual test classes.

{%include note.html content="When executing tests, NetBeans actually calls Maven with `surefire:test` goal. " %}

## Running / Debugging PMD

* Open the project "PMD CLI" and navigate to the class `PmdCli`.
* Right-click on the class and select "Run File". This runs PMD without any commands and just shows the help text.
* In the output window at the bottom, click the double yellow arrow to open a "Run Maven" dialog and configure
  the arguments as e.g. `exec.appArgs=check --help`. Also change the classpath scope to `exec.classpathScope=test`.
* In the "Run Maven" dialog, use the "Add >" button to add the debug configuration - then you can set breakpoints
  and run in the debugger. Unfortunately, this doesn't work correctly, as Netbeans seems to start maven with
  the debugger instead of the executed java app. Therefore, instead of using `jpda.listen=maven`, we use
  `exec.vmArgs=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000`. This will stop PMD at the
  beginning to wait for the debugger. Then you can run in NetBeans "Debug > Attach Debugger...", select
  "SocketAttach" using port "8000".

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

Import the code style settings (formatter) so that it conforms with our Checkstyle config.
To do that, go to `Tool > Options` and the use the button "Import..." and the bottom.
Choose the file `netbeans/formatting.zip` from the build-tools repository.

## Running the Designer

The designer lives in a separate repository, that you'll need to fork and clone first:
[Designer repository](https://github.com/pmd/pmd-designer)

```shell
git clone git@github.com:your_user_name/pmd-designer.git
```

1. Select File > Open Project... and choose the pmd-designer source folder.
2. Navigate to class `DesignerStarter`
3. Select "Run File"
4. In the output window at the bottom, click the double yellow arrow to open a "Run Maven" dialog and configure
   the profiles: "running,with-javafx"

## Known Issues
* There is no Kotlin support for Netbeans. As some parts of PMD use Kotlin, there is no
  IDE support. See [kotlin-netbeans#122](https://github.com/JetBrains/kotlin-netbeans/issues/122).
* There is no recent checkstyle plugin for Netbeans.
* When executing tests, the test results are not always recognized by Netbeans. It sometimes says no tests
  executed although surefire did run tests.
