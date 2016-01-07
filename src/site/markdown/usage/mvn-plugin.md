<!--
    <author email="belaran@gmail.com">Romain PELISSE</author>
-->

# Maven 2 plugin

## Running the pmd plugin

### report

To include the mvn report in the project reports section add the following lines under
the reports element in your pom.xml:


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

This will add an entry to the 'project reports' section with the PMD report.


### manual

To run PMD on a Maven project without adding it as a report, simply run

    mvn pmd:pmd

The PMD plugin writes the report in XML which will then be formatted into more readable HTML.

## Customization

### Changing rulesets

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

The value of the 'ruleset' value can either be a relative address, an absolute address or even an url.

A clean strategy for customizing which rules to use for a project is to write a ruleset file.
In this file you can define which rules to use, add custom rules, and
customizing which rules to include/exclude from official rulesets. More information on
writing a ruleset can be found [here](../customizing/howtomakearuleset.html).
Note that if you include other rulesets in your own rulesets, you have to be sure that the plugin
will be able to resolve those other ruleset references.

### Other configurations

The Maven 2 PMD plugin allows you to configure CPD, targetJDK, and the use of XRef to link
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

## Reference

For more data, please see the well documented PMD plugin project page here:
<http://maven.apache.org/plugins/maven-pmd-plugin/index.html>
