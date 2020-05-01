---
title: Making rulesets
keywords: [rulesets, reference, rule, exclude, include, pattern, filter]
tags: [getting_started, userdocs]
summary: Making Custom Rulesets for PMD
last_updated: November 2017
summary: "A ruleset is an XML configuration file, which describes a collection of rules to be executed
          in a PMD run. PMD includes built-in rulesets to run quick analyses with a default configuration, but
          users are encouraged to make their own rulesets from the start, because they allow for so much
          configurability. This page walk you through the creation of a ruleset and the multiple configuration
          features offered by rulesets."
last_updated: May 2018 (6.4.0)
permalink: pmd_userdocs_making_rulesets.html
author: Tom Copeland <tomcopeland@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

## Creating a ruleset

The first step is to create a new empty ruleset. You can use the following template:

```xml
<?xml version="1.0"?>

<ruleset name="Custom Rules"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

    <description>
        My custom rules
    </description>


    <!-- Your rules will come here -->

</ruleset>
```

### Referencing a single rule

<!-- TODO this could be better explained, eg first explain how a ruleset reference works, then rule reference, then go on showing single rule & bulk addition, then include/exclude patterns -->

To use the built-in rules PMD provides, you need to add some *references* to them. Here's a
basic rule reference:

```xml
<rule ref="category/java/errorprone.xml/EmptyCatchBlock" />
```

Adding that element into the `ruleset` element adds the rule [EmptyCatchBlock](pmd_rules_java_errorprone.html#emptycatchblock)
to your ruleset. This is a Java rule, so it will be executed on every Java file PMD encounters in
its search space.

How to read the `ref` attribute?

* `category/java/errorprone.xml` is a reference to the Java category `errorprone`. Since PMD 6.0.0,
  all PMD built-in rules are sorted in one of eight categories, which are consistent across languages:

  1.  **Best Practices**: These are rules which enforce generally accepted best practices.<br/>
  2.  **Code Style**: These rules enforce a specific coding style.<br/>
  3.  **Design**: Rules that help you discover design issues.<br/>
  4.  **Documentation**: These rules are related to code documentation.<br/>
  5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.<br/>
  6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.<br/>
  7.  **Performance**: Rules that flag suboptimal code.<br/>
  8.  **Security**: Rules that flag potential security flaws."

{% include tip.html content="You can discover the available rules by language and category [from this page](tag_rule_references.html)" %}


* `EmptyCatchBlock` is simply the name of the rule. If there were no rule with that name within the specified
  category, then PMD would fail before starting the analysis.

#### [Configuring individual rules](pmd_userdocs_configuring_rules.html)

How you can configure individual rules is described on [Configuring Rules](pmd_userdocs_configuring_rules.html).

### Bulk-adding rules

You can also reference rules in bulk by referencing a complete category or ruleset, possibly excluding certain rules, like in the following:

```xml
<rule ref="category/java/codestyle.xml">
    <exclude name="WhileLoopsMustUseBraces"/>
    <exclude name="IfElseStmtsMustUseBraces"/>
</rule>
```

Here, the `ref` attribute references a whole category. You can also use a file system path or classpath relative path. In any case, the path must address an accessible ruleset XML file.

{% include note.html content="Path separators in the source file path are normalized to be the `/` character within PMD, so the same ruleset can be used on multiple platforms transparently." %}

{% include note.html content="Referencing a complete category or ruleset means, you'll also get automatically any
changes for this ruleset. If new rules are added, then these are automatically activated for you. If rules
are deprecated, then these rules are automatically deactivated. This might or
not might be, what you want. This can happen, if a new version of PMD provides a new rule and or deprecates
existing rules. If you want to have
complete control over the rules, that you are using, then it is recommended to add each rule separately via
a single rule reference." %}

### Filtering the processed files

You can exclude some files from being processed by a ruleset using **exclude patterns**, with an optional overridding **include pattern**. A file will be excluded from processing *when there is a matching exclude pattern, but no matching include pattern*. This exclude/include technique works regardless of how PMD is used (e.g. command line, IDE, Ant), making it easier to keep application of your PMD rules consistent throughout your environment. Here is an example:

```xml
<?xml version="1.0"?>
<ruleset name="myruleset"
		xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
    <description>My ruleset</description>

    <exclude-pattern>.*/some/package/.*</exclude-pattern>
    <exclude-pattern>.*/some/other/package/FunkyClassNamePrefix.*</exclude-pattern>
    <include-pattern>.*/some/package/ButNotThisClass.*</include-pattern>

    <!-- Rules here ... -->

</ruleset>
```
