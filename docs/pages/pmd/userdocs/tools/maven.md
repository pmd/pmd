---
title: Maven PMD Plugin
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_maven.html
last_updated: March 2020
mpmd_version: 3.13.0
author: >
    Miguel Griffa <mikkey@users.sourceforge.net>,
    Romain PELISSE <belaran@gmail.com>,
    Andreas Dangel <andreas.dangel@adangel.org>
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

    mvn pmd:pmd

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

### Reference

For more information, please see the well documented PMD plugin project page here:
<http://maven.apache.org/plugins/maven-pmd-plugin/index.html>.
Also, the bug tracker for this plugin is [here](https://issues.apache.org/jira/browse/MPMD).


## Maven 1

{% include warning.html content="Apache Maven 1.x has reached its end of life, and is no longer supported. For more information, see the [announcement](http://maven.apache.org/maven-1.x-eol.html). Users are encouraged to migrate to the current version of Apache Maven." %}

This section is about the maven 1 PMD plugin.

### Running the pmd plugin

#### Generating a project report

To include the PMD report in the project reports section add the following line under
the reports element in your project.xml:

    <report>maven-pmd-plugin</report>

This will add an entry to the 'project reports' section with the PMD report.

#### Executing PMD manually

To run PMD on a Maven project without adding it as a report, simply run

    maven pmd xdoc

The PMD plugin writes the report in XML which will then be formatted into more readable HTML.

### Customization

#### Changing rulesets

To specify a set of official, built-in rulesets to be used set them in the property
<em>maven.pmd.rulesets</em>.  You can include this setting in your project.properties file.

A clean strategy for customizing which rules to use for a project is to write a ruleset file.
In this file you can define which rules to use, add custom rules, and
customizing which rules to include/exclude from official rulesets. More information on
writing a ruleset can be found [here](pmd_userdocs_making_rulesets.html).

Add to the root of your Maven project a pmd.xml file which contains the ruleset mentioned in
the previous paragraph. Add the following property to your project now:

    maven.pmd.rulesetfiles = ${basedir}/pmd.xml

### Reference

See the PMD plugin project page here:
<http://maven.apache.org/maven-1.x/plugins/pmd/>
