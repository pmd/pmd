<!--
    <author email="mikkey@users.sourceforge.net">Miguel Griffa</author>
-->

# Maven 1 PMD plugin

This page is about the maven 1 PMD plugin. The maven 2 PMD plugin page is available
[here](mvn-plugin.html).

## Running the pmd plugin

### report

To include the Maven report in the project reports section add the following line under
the reports element in your project.xml:

    <report>maven-pmd-plugin</report>

This will add an entry to the 'project reports' section with the PMD report.

### manual

To run PMD on a Maven project without adding it as a report, simply run

    maven pmd xdoc

The PMD plugin writes the report in XML which will then be formatted into more readable HTML.

## Customization

### Changing rulesets

To specify a set of official, built-in rulesets to be used set them in the property
<em>maven.pmd.rulesets</em>.  You can include this setting in your project.properties file.

A clean strategy for customizing which rules to use for a project is to write a ruleset file.
In this file you can define which rules to use, add custom rules, and
customizing which rules to include/exclude from official rulesets. More information on
writing a ruleset can be found [here](../customizing/howtomakearuleset.html).

Add to the root of your Maven project a pmd.xml file which contains the ruleset mentioned in
the previous paragraph. Add the following property to your project now:

    maven.pmd.rulesetfiles = ${basedir}/pmd.xml

## Reference

See the PMD plugin project page here:
<http://maven.apache.org/maven-1.x/plugins/pmd/>
