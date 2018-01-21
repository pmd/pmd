---
title: Using rule properties
short_title: Using rule properties
tags: [customizing, properties]
summary: "Properties are a way to parameterise the behaviour of PMD rules and your own directly from the ruleset XML, which greatly improves their reusability. This page explains how to use rule properties to make the most of your rulesets."
last_updated: December 2017 (6.0.0)
permalink: pmd_userdocs_using_properties.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

Properties make it easy to customise the behaviour of a rule directly from the xml. They come in several types, which correspond to the type of their values. For example, NPathComplexity declares a property "reportLevel", with an integer value type, and which corresponds to the threshold above which a method will be reported. If you believe that its default value of 200 is too high, you could lower it to e.g. 150 in the following way:

```xml
<rule ref="category/java/design.xml/NPathComplexity">
    <properties>
        <property name="reportLevel">150</property>
    </properties>
</rule>
```

Properties are assigned a value with a `property` element, which should mention the name of a property as an attribute. The value of the property can be specified either in the content of the element, like above, or in the `value` attribute, e.g.

```xml
<property name="reportLevel" value="150"/>
```

All property assignements must be enclosed in a `properties` element, which is itself inside a `rule` element.

{%include tip.html content="The properties of a rule are documented with the rule, e.g. [here](pmd_rules_java_design.html#npathcomplexity) for NPathComplexity. Note that **assigning a value to a property that does not exist throws an error!**" %}

Some properties take multiple values (a list), in which case you can provide them all by delimiting them with a delimiter character. It is usually a pipe ('\|'), or a comma (',') for numeric properties, e.g.
```xml
 <property name="legalCollectionTypes"
           value="java.util.ArrayList|java.util.Vector|java.util.HashMap"/>
```

These properties are referred to as **multivalued properties** in this documentation.
