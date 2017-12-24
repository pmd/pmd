---
title:  PMD Making Rulesets
tags: [customizing, rulesets]
summary: Making Custom Rulesets for PMD
last_updated: November 2017
permalink: pmd_userdocs_making_rulesets.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>
---

# How to make a new rule set

Say you want to pick specific rules from various rule sets and customize them. You can do this by making your own rule set.

## Create a new ruleset.xml file

Use one of the current rulesets as an example. Copy and paste it into your new file, delete all the old rules from it, and change the name and description. Like this:

```xml
<?xml version="1.0"?>
<ruleset name="Custom ruleset"
        xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">
    <description>
        This ruleset checks my code for bad stuff
    </description>
</ruleset>
````

## Add some rule references to it

After you add these references it’ll look something like this:

```xml
<?xml version="1.0"?>
<ruleset name="Custom ruleset"
		xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">

	<description>
		This ruleset checks my code for bad stuff
	</description>

	<!-- Here's some rules we'll specify one at a time -->
	<rule ref="category/java/bestpractices.xml/UnusedLocalVariable"/>
	<rule ref="category/java/bestpractices.xml/UnusedPrivateField"/>
	<rule ref="category/java/codestyle.xml/DuplicateImports"/>
	<rule ref="category/java/errorprone.xml/UnnecessaryConversionTemporary"/>

	<!-- We want to customize this rule a bit, change the message and raise the priority  -->
	<rule ref="category/java/errorprone.xml/EmptyCatchBlock"
			message="Must handle exceptions">
		<priority>2</priority>
	</rule>

	<!-- Now we'll customize a rule's property value -->
	<rule ref="category/java/design.xml/CyclomaticComplexity">
		<properties>
			<property name="reportLevel" value="5"/>
		</properties>
	</rule>

	<!-- We want everything from category Code Style except WhileLoopsMustUseBraces -->
	<rule ref="category/java/codestyle.xml">
		<exclude name="WhileLoopsMustUseBraces"/>
	</rule>
</ruleset>
```

>Notice that you can customize individual referenced rules. Everything but the class of the rule can be overridden in your custom ruleset.

## Excluding rules from a ruleset

You can also make a custom ruleset by referencing a complete category and exclude certain rules, like this:

```xml
<?xml version="1.0"?>
<ruleset name="myruleset"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">
  <description>All codestyle rules, but with just the braces rules I like</description>
  <rule ref="category/java/codestyle.xml">
    <exclude name="WhileLoopsMustUseBraces"/>
    <exclude name="IfElseStmtsMustUseBraces"/>
  </rule>
</ruleset>
```

## Excluding files from a ruleset

You can also exclude certain files from being processed by a ruleset using exclude patterns, with an optional overriding include pattern. A file will be excluded from processing when there is a matching exclude pattern, but no matching include pattern. Path separators in the source file path are normalized to be the ‘/’ character, so the same ruleset can be used on multiple platforms transparently. Additionally, this exclude/include technique works regardless of how PMD is used (e.g. command line, IDE, Ant), making it easier to keep application of your PMD rules consistent throughout your environment. Here is an example:

```xml
<?xml version="1.0"?>
<ruleset name="myruleset"
		xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">
	<description>My ruleset</description>
	<exclude-pattern>.*/some/package/.*</exclude-pattern>
	<exclude-pattern>.*/some/other/package/FunkyClassNamePrefix.*</exclude-pattern>
	<include-pattern>.*/some/package/ButNotThisClass.*</include-pattern>
	<rule>...
</ruleset>
```

## Reference it in your Ant task

You can specify the full path to your custom ruleset name alongside of the built-in PMD rulesets - like this:

```xml
<pmd rulesetfiles="/home/tom/data/pmd/pmd/rulesets/java/design.xml,rulesets/java/unusedcode.xml">
	<formatter type="xml" toFile="foo.xml"/>
	<fileset dir="/home/tom/data/pmd/pmd/src">
		<include name="**/*.java"/>
	</fileset>
</pmd>
```

## To see it in your IDE

You'll need to point the IDE plugin to the location of your custom ruleset.

## Send us feedback

If you have suggestions on clarifying this document, please post them to [the forum](http://sourceforge.net/p/pmd/discussion/188192). Thanks!

Finally, for many more details on building custom rulesets, pick up [PMD Applied](http://pmdapplied.com/)!
