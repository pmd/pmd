---
title: Understanding Rulesets
tags: [rulesets]
permalink: pmd_userdocs_understanding_rulesets.html
summary: Rules belong to categories and rulesets are custom collections of rules
last_updated: November 2017
---

## Rule Categories

Since PMD 6.0.0, all provided built-in rules are sorted into one of eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

These categories help you to find rules and figure out the relevance and impact for your project.

You can find the available rules under "Rule Reference" in the menu. Each languages has its own rule
index, e.g. [Java Rules](pmd_rules_java.html) or [JavaScript Rules](pmd_rules_ecmascript.html).

{% include note.html content="Not all supported languages provide rules in all categories yet. " %}


## Rulesets

There are two major use cases:

1.  When defining a new rule, the rule needs to be defined in a ruleset. PMD's built-in rules
    are defined in special rulesets which form the eight categories mentioned above.
    From these rulesets the rule reference documentation is generated,
    see [Java Rules](pmd_rules_java.html) for an example.

    Similar rules are grouped together into the same category, like [Java Best Practices](pmd_rules_java_bestpractices.html)
    which contains rules which enforce generally accepted best practices. Each category uses its own
    ruleset file.

2.  When executing PMD, you need to tell, which rules should be executed. You could directly point to the
    built-in rulesets, but then you might be overwhelmed by the found violations. As described
    in [Best Practices](pmd_userdocs_best_practices.html), it's better to define an own custom ruleset.

    With an own custom ruleset, you can:

    *   Select the rules, that should be executed
    *   Adjust the rule properties to exactly meet your needs

## Create a custom ruleset

You start by creating a new XML file with the following contents:

``` xml
<?xml version="1.0"?>

<ruleset name="Custom Rules"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd">
    <description>
Custom rules
    </description>

</ruleset>
```

Now start to add rule by **referencing** them. Let's say, you want to start with finding
[Empty Catch Blocks](pmd_rules_java_errorprone.html#emptycatchblock). Then you'd add the following
rule reference inside the `ruleset` elements:

```xml
    <rule ref="category/java/errorprone.xml/EmptyCatchBlock" />
```

## Adjusting rule properties

If you want to be less strict with empty catch blocks, you could define that an exception variable name
of `ignored` will not raise a violation. Therefore you would reference the rule **and** define
the appropriate property value:

```xml
    <rule ref="category/java/errorprone.xml/EmptyCatchBlock">
        <properties>
            <property name="allowExceptionNameRegex">
                <value>^ignored$</value>
            </property>
        </properties>
    </rule>
```


{% include note.html content="More information about rulesets can be found on [Making Rulesets](pmd_userdocs_making_rulesets.html)." %}
