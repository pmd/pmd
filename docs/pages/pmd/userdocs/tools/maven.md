---
title: Maven PMD Plugin
tags: [userdocs, tools]
permalink: /pmd_userdocs_tools_maven.html
last_updated: August 2017
author: >
    Miguel Griffa <mikkey@users.sourceforge.net>,
    Romain PELISSE <belaran@gmail.com>,
    Andreas Dangel <andreas.dangel@adangel.org>
---

## Maven 2 and 3

### Running the pmd plugin

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
        <configuration>
            <failOnViolation>true</failOnViolation> <!-- this is actually true by default, but can be disabled -->
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


    <reporting>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <configuration>
                    <rulesets>
                        <ruleset>/rulesets/java/braces.xml</ruleset>
                        <ruleset>/rulesets/java/naming.xml</ruleset>
                        <ruleset>d:\rulesets\strings.xml</ruleset>
                        <ruleset>http://localhost/design.xml</ruleset>
                    </rulesets>
                </configuration>
            </plugin>
        </plugins>
    </reporting>

The value of the 'ruleset' element can either be a relative address, an absolute address or even an url.

A clean strategy for customizing which rules to use for a project is to write a ruleset file.
In this file you can define which rules to use, add custom rules, and
customizing which rules to include/exclude from official rulesets. More information on
writing a ruleset can be found [here](pmd_userdocs_understanding_rulesets.html).
Note that if you include other rulesets in your own rulesets, you have to be sure that the plugin
will be able to resolve those other ruleset references.

#### Enabling Incremental Analysis

When using the Maven PMD plugin 3.8 or later along with PMD 5.6.0 or later, you can enable incremental analysis to
speed up PMD's execution while retaining the quality of the analysis. You can additionally customize where the cache is stored::

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-pmd-plugin</artifactId>
        <configuration>
            <analysisCache>true</analysisCache> <!-- enable incremental analysis -->
            <analysisCacheLocation>${project.build.directory}/pmd/pmd.cache</analysisCacheLocation> <!-- Optional: points to this location by default -->
        </configuration>
    </plugin>

#### Other configurations

The Maven PMD plugin allows you to configure CPD, targetJDK, and the use of XRef to link
the report to html source files, and the file encoding:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-pmd-plugin</artifactId>
        <configuration>
            <linkXRef>true</linkXRef>
            <sourceEncoding>ISO-8859-1</sourceEncoding>
            <minimumTokens>30</minimumTokens>
            <targetJdk>1.4</targetJdk>
        </configuration>
    </plugin>

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
                    <version>3.8</version>
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
Also, the bug tracker for this plugin is [here](http://jira.codehaus.org/browse/MPMD).


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
writing a ruleset can be found [here](/pmd_userdocs_understanding_rulesets.html).

Add to the root of your Maven project a pmd.xml file which contains the ruleset mentioned in
the previous paragraph. Add the following property to your project now:

    maven.pmd.rulesetfiles = ${basedir}/pmd.xml

### Reference

See the PMD plugin project page here:
<http://maven.apache.org/maven-1.x/plugins/pmd/>
