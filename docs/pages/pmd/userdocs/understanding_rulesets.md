---
title: Understanding Rulesets
permalink: pmd_userdocs_understanding_rulesets.html
summary: Rulesets are collections of rules
last_updated: September 2017
---

There are two major use cases:

1.  When defining a new rule, the rule needs to be defined in a ruleset. PMD's built-in rules
    are defined in specific rulesets from which the rule reference documentation is generated,
    see [Java Rules](pmd_rules_java.html) for an example.

    Similar rules are grouped together into the same ruleset, like the [Java Braces Ruleset](pmd_rules_java_braces.html)
    which contains rules that all deal with missing braces.

2.  When executing PMD you need to tell, which rules should be executed. You could directly point to the
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
[Empty Catch Blocks](pmd_rules_java_empty.html#emptycatchblock). Then you'd add the following
rule reference inside the `ruleset` elements:

```xml
    <rule ref="rulesets/java/empty.xml/EmptyCatchBlock" />
```

## Adjusting rule properties

If you want to be less strict with empty catch blocks, you could define that an exception variable name
of `ignored` will not raise a violation. Therefore you would reference the rule **and** define
the appropriate property value:

```xml
    <rule ref="rulesets/java/empty.xml/EmptyCatchBlock">
        <properties>
            <property name="allowExceptionNameRegex">
                <value>^ignored$</value>
            </property>
        </properties>
    </rule>
```


{% include note.html content="More information about rulesets can be found on [Making Rulesets](pmd_devdocs_making_rulesets.html)." %}
