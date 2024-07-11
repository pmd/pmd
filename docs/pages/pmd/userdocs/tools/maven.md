---
title: Maven PMD Plugin
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_maven.html
last_updated: June 2024 (7.3.0)
mpmd_version: 3.23.0
author: >
    Miguel Griffa <mikkey@users.sourceforge.net>,
    Romain PELISSE <belaran@gmail.com>,
    Andreas Dangel <andreas.dangel@pmd-code.org>
---

## Maven 2 and 3

### Running the pmd plugin

#### Choosing the plugin version

When adding the maven-pmd-plugin to your pom.xml, you need to select a version. To figure out the
latest available version, have a look at the official [maven-pmd-plugin documentation](https://maven.apache.org/plugins/maven-pmd-plugin/).

As of {{ page.last_updated }}, the current plugin version is **{{ page.mpmd_version }}**.

The version of the plugin should be specified in `<build><pluginManagement/></build>` and if using the project
report additionally in `<reporting><plugins/></reporting>` elements. Here's an example for the pluginManagement
section:

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>{{ page.mpmd_version }}</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

When defining the version in the pluginManagment section, then it doesn't need to be specified in the normal plugins
section. However, it should additionally be specified in the reporting section.

More information, see [Guide to Configuring Plugin-ins](https://maven.apache.org/guides/mini/guide-configuring-plugins.html).

#### Generating a project report

To include the PMD report in the project reports section add the following lines under
the reports element in your pom.xml:

``` xml
<project>
    ...
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>{{ page.mpmd_version }}</version>
            </plugin>
        </plugins>
    </reporting>
    ...
</project>
```

This will add an entry to the 'project reports' section with the PMD report when you build the maven site.


#### Executing PMD manually

To run PMD on a Maven project without adding it as a report, simply run

    mvn compile pmd:pmd

The PMD plugin writes the report in XML which will then be formatted into more readable HTML.


#### Integrated into the build process

You can also run PMD automatically when building your project. You even let the build fail, if
PMD finds some violations. Therefore the `check` goal is used:

``` xml
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-pmd-plugin</artifactId>
        <version>{{ page.mpmd_version }}</version> <!-- or use version from pluginManagement -->
        <configuration>
            <!-- failOnViolation is actually true by default, but can be disabled -->
            <failOnViolation>true</failOnViolation>
            <!-- printFailingErrors is pretty useful -->
            <printFailingErrors>true</printFailingErrors>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  ...
</project>
```

This will run PMD automatically during the `verify` phase of the build. You can additionally run CPD, if
you add `cpd-check` as a goal.


### Customization

#### Changing rulesets

To specify a ruleset, simply edit the previous configuration:


``` xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-pmd-plugin</artifactId>
            <version>{{ page.mpmd_version }}</version>
            <configuration>
                <rulesets>
                    <ruleset>/rulesets/java/quickstart.xml</ruleset>
                    <ruleset>d:\rulesets\my-ruleset.xml</ruleset>
                    <ruleset>http://localhost/design.xml</ruleset>
                </rulesets>
            </configuration>
        </plugin>
    </plugins>
</reporting>
```

The value of the 'ruleset' element can either be a relative address, an absolute address or even an url.

A clean strategy for customizing which rules to use for a project is to write a ruleset file.
In this file you can define which rules to use, add custom rules, and
customizing which rules to include/exclude from official rulesets. More information on
writing a ruleset can be found [here](pmd_userdocs_making_rulesets.html).
Note that if you include other rulesets in your own rulesets, you have to be sure that the plugin
will be able to resolve those other ruleset references.

#### Enabling Incremental Analysis

When using the Maven PMD plugin 3.8 or later along with PMD 5.6.0 or later, you can enable incremental analysis to
speed up PMD's execution while retaining the quality of the analysis. You can additionally customize where the cache is stored::

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>{{ page.mpmd_version }}</version> <!-- or use version from pluginManagement -->
    <configuration>
        <!-- enable incremental analysis -->
        <analysisCache>true</analysisCache>
        <!-- analysisCacheLocation: optional - points to the following location by default -->
        <analysisCacheLocation>${project.build.directory}/pmd/pmd.cache</analysisCacheLocation>
    </configuration>
</plugin>
```

#### Other configurations

The Maven PMD plugin allows you to configure CPD, targetJDK, and the use of XRef to link
the report to html source files, and the file encoding:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>{{ page.mpmd_version }}</version> <!-- or use version from pluginManagement -->
    <configuration>
        <linkXRef>true</linkXRef>
        <sourceEncoding>ISO-8859-1</sourceEncoding>
        <minimumTokens>30</minimumTokens>
        <targetJdk>1.4</targetJdk>
    </configuration>
</plugin>
```

#### Upgrading the PMD version at runtime

The Maven PMD plugin comes with a specific PMD version, which is documented on the
[plugin project page](https://maven.apache.org/plugins/maven-pmd-plugin/index.html).

Given that the newer PMD version is compatible, you can override the PMD version, that the
Maven plugin will use and benefit from the latest bugfixes and enhancements:

``` xml
<project>
    <properties>
        <pmdVersion>...choose your version...</pmdVersion>
    </properties>
...
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-pmd-plugin</artifactId>
                    <version>{{ page.mpmd_version }}</version>
                    <dependencies>
                        <dependency>
                            <groupId>net.sourceforge.pmd</groupId>
                            <artifactId>pmd-core</artifactId>
                            <version>${pmdVersion}</version>
                        </dependency>
                        <dependency>
                            <groupId>net.sourceforge.pmd</groupId>
                            <artifactId>pmd-java</artifactId>
                            <version>${pmdVersion}</version>
                        </dependency>
                        <dependency>
                            <groupId>net.sourceforge.pmd</groupId>
                            <artifactId>pmd-javascript</artifactId>
                            <version>${pmdVersion}</version>
                        </dependency>
                        <dependency>
                            <groupId>net.sourceforge.pmd</groupId>
                            <artifactId>pmd-jsp</artifactId>
                            <version>${pmdVersion}</version>
                        </dependency>
                    </dependencies>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
...
</project>
```

#### Using PMD 7 with maven-pmd-plugin

Since version 3.22.0 ([MPMD-379](https://issues.apache.org/jira/browse/MPMD-379)), maven-pmd-plugin uses
by default now PMD 7.0.0 and no extra configuration is required.

The specific PMD version used by maven-pmd-plugin might change. The exact version is documented on the
[plugin project page](https://maven.apache.org/plugins/maven-pmd-plugin/index.html).

In order to use newer versions of PMD 7, you can simply follow the guide
[Upgrading PMD at Runtime](https://maven.apache.org/plugins/maven-pmd-plugin/examples/upgrading-PMD-at-runtime.html).

Note: If you upgrade from Maven PMD Plugin before 3.22.0 you are most likely updating from PMD 6 to PMD 7.
This upgrade is a major version change. If you use the default ruleset from Maven PMD Plugin, then everything should
just work. But if you use a custom ruleset, you most likely need to review your ruleset and migrate it to PMD 7.
Rules might have been renamed or replaced. See [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html)
and [Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html).

As PMD 7 revamped the Java module, if you have custom rules, you need to migrate these rules.
See the use case [I'm using custom rules](pmd_userdocs_migrating_to_pmd7.html#im-using-custom-rules)
in the Migration Guide.

### Reference

For more information, please see the well documented PMD plugin project page here:
<http://maven.apache.org/plugins/maven-pmd-plugin/index.html>.
Also, the bug tracker for this plugin is [MPMD](https://issues.apache.org/jira/browse/MPMD).
