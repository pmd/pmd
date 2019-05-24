---
title: Configuring rules
short_title: Configuring rules
keywords: [property, properties, message, priority]
tags: [userdocs, getting_started]
summary: "Learn how to configure your rules directly from the ruleset XML."
last_updated: May 2018 (6.4.0)
permalink: pmd_userdocs_configuring_rules.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

## Message and priority overriding

You can change a rule's **message** by specifying a `message`
attribute on the rule element. This will override the previous
value and change the message the rule will print on the report.

Similarly, the **priority** of a rule can be changed via a nested
element. Using priority, you can deactivate some rules based on a
minimum priority threshold (set using the `-min` CLI option).
Priority is an integer ranging from 1 to 5, with 1 being the highest
priority.


Putting things together, the following rule reference lowers the priority
of EmptyCatchBlock to 5, such that e.g. using the `-min 4` CLI parameters
will cause the rule to be ignored.

```xml
<rule ref="category/java/errorprone.xml/EmptyCatchBlock"
      message="Empty catch blocks should be avoided" >
      <priority>5</priority>
</rule>
```


## Rule properties

Properties make it easy to customise the behaviour of a rule directly from the xml. They come in several types, which correspond to the type of their values. For example, NPathComplexity declares a property "reportLevel", with an integer value type, and which corresponds to the threshold above which a method will be reported. If you believe that its default value of 200 is too high, you could lower it to e.g. 150 in the following way:

```xml
<rule ref="category/java/design.xml/NPathComplexity">
    <properties>
        <property name="reportLevel">
              <value>150</value>
        </property>
    </properties>
</rule>
```

Properties are assigned a value with a `property` element, which should mention the name of a property as an attribute. The value of the property can be specified either in the content of the element, like above, or in the `value` attribute, e.g.

```xml
<property name="reportLevel" value="150"/>
```

All property assignments must be enclosed in a `properties` element, which is itself inside a `rule` element.

{%include tip.html content="The properties of a rule are documented with the rule, e.g. [here](pmd_rules_java_design.html#npathcomplexity) for NPathComplexity. Note that **assigning a value to a property that does not exist throws an error!**" %}

Some properties take multiple values (a list), in which case you can provide them all by delimiting them with a delimiter character. It is usually a pipe ('\|'), or a comma (',') for numeric properties, e.g.
```xml
 <property name="legalCollectionTypes"
           value="java.util.ArrayList|java.util.Vector|java.util.HashMap"/>
```

These properties are referred to as **multivalued properties** in this documentation.
