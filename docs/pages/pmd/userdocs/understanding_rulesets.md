---
title: Understanding Rulesets
tags: [essentials, userdocs]
permalink: pmd_userdocs_understanding_rulesets.html
summary: "Learn how to specify which rules you want to run, and how, by creating your own rulesets."
last_updated: May 2018 (6.4.0)
---


## What's a ruleset?

A *ruleset* is a configuration file, which describes a collection of PMD *rules* to be executed
in a PMD run. PMD includes built-in rulesets to run quick analyses with a default configuration, but
users are encouraged to make their own rulesets from the start, because they allow for so much
configurability.

Rulesets are written in XML. The following sections walk you through the creation of a ruleset.


## Creating a custom ruleset

The first step is to create a new empty ruleset. You can use the following template:

``` xml
<?xml version="1.0"?>

<ruleset name="Custom Rules"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">

    <description>
        My custom rules
    </description>


    <!-- Your rules will come here -->

</ruleset>
```

### Referencing rules

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

### Configuring individual rules

Main documentation page: [Configuring rules](pmd_userdocs_configuring_rules.html)
