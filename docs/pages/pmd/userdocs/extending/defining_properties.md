---
title: Defining rule properties
short_title: Defining rule properties
tags: [extending, userdocs]
summary: "Learn how to define your own properties both for Java and XPath rules."
last_updated: December 2023 (7.0.0)
permalink: pmd_userdocs_extending_defining_properties.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PF props::PropertyFactory %}

Rule properties are a way to make your rules configurable directly from the
ruleset XML. Their usage is described on the
[Configuring Rules](pmd_userdocs_configuring_rules.html#rule-properties) page.

If you're a rule developer, you may want to think about what would be useful for
a user of your rule to parameterize. It could be a numeric report level, a boolean
flag changing the behaviour of your rule... Chances are there *is* some detail
that can be abstracted away from your implementation, and in that case, this
page can help you squeeze that sweet flexibility out of your rule.

## Overview of properties

The basic thing you need to do as a developer is to define a **property descriptor** and declare
that your rule uses it. A property descriptor defines a number of attributes for your property:
* Its *name*, with which the user will refer to your property;
* Its *description*, for documentation purposes;
* Its *default value*

All of these attributes can be specified in a single Java statement (or XML element for XPath rules).


## For Java rules

The procedure to define a property is quite straightforward:
* Create a property descriptor of the type you want, by using a
  builder from {% jdoc :PF %}
* Call {% jdoc !a!props::PropertySource#definePropertyDescriptor(props::PropertyDescriptor) %}
  in the rule's noarg constructor.

You can then retrieve the value of the property at any time using
{% jdoc !a!props::PropertySource#getProperty(props::PropertyDescriptor) %}.

Note: The base class for all rule implementations is {% jdoc core::lang.rule.AbstractRule %}, which
is a {% jdoc props::PropertySource %}. So you can directly call `definePropertyDescriptor(...)`
or `getProperty(...)` within your rule.

### Creating a descriptor

Properties can be built using type-specific **builders**, which can be obtained
from the factory methods of {% jdoc :PF %}. For example, to build a
string property, you would call
```java
PropertyFactory.stringProperty("myProperty")
               .desc("This is my property")
               .defaultValue("foo")
               .build();
```

This is fairly more readable than a constructor call, but keep in mind the description
and the default value are not optional.

For **numeric properties**, you can add constraints on the range of acceptable values, e.g.
```java
PropertyFactory.intProperty("myIntProperty")
               .desc("This is my property")
               .defaultValue(3)
               .require(positive()) // must be > 0
               .require(below(100)) // must be <= 100
               .build();
```

Predefined constraints such as `positive` and `below` are available in the class {% jdoc props::NumericConstraints %}.
A custom constraint can be implemented by implementing the interface {% jdoc props::PropertyConstraint %}.

**Enumerated properties** do not have a specific value type, instead,
you can choose any type of value, provided the values are from a closed set. To make
that actionable, you give string labels to each of the acceptable values, and the user
will provide one of those labels as a value in the XML. The property will give you back
the associated value, not the label. Here's an example:
```java
enum Mode {
    Easy, Hard
}

// Using this method, the labels are the `toString` of each enum constant.
// To customize this look at the overloads of `enumProperty`.
static PropertyDescriptor<Mode> modeProperty
 = PropertyFactory.enumProperty("modeProperty", Mode.class)
                  .desc("This is my property")
                  .defaultValue(Mode.Easy)
                  .build();
```


### Example

You can see an example of properties used in a PMD rule such as [AvoidReassigningLoopVariables](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/bestpractices/AvoidReassigningLoopVariablesRule.java#L40).
There are several things to notice here:
* The property descriptors are declared `static final`, which should generally be
the case, as descriptors are immutable and can be shared between instances of the same rule;
* The property is declared using {% jdoc props::PropertySource#definePropertyDescriptor(props::PropertyDescriptor) %}` *in the constructor*,
which ensures the property gets recognised by PMD at the time the properties
are overridden (which happens before rule execution);
* The value of the property is *not retrieved in the constructor*, but in one of
the `visit` methods (typically on the highest node in the tree, since the property
doesn't change).



## For XPath rules

XPath rules can also define their own properties. To do so, you must add a `property` element in
the `properties` element of your rule, which **declares the `type` attribute**. This attribute conditions
what type the underlying property has, and can have the following values:

| `type` attribute | XSD type   |
|------------------|------------|
| Integer          | xs:integer |
| Long             | xs:integer |
| Double           | xs:decimal |
| Boolean          | xs:boolean |
| String           | xs:string  |
| Character        | xs:string  |
| Regex            | xs:string  |

Note that enumerated properties are not available in XPath rules.

Properties defined in XPath also *must* declare the `description` attribute.
Numeric properties also expect the `min` and `max` attributes for now. Here are
a few examples to sum it up:

```xml
<property name="stringProp" type="Boolean" value="true" description="A BooleanProperty."/>
<property name="intProp" type="Integer" value="3" min="1" max="20" description="An IntegerProperty."/>
```

You can then use the property in XPath with the syntax `$propertyName`, for example:

```xml
<rule name="MyXpathRule" ...>
  <properties>
    <property name="maxStatements" type="Integer" value="10" min="1" max="40"
              description="Max number of statements per method"/>
    <property name="xpath">
      <value>
        <![CDATA[
            //MethodDeclaration/Block[count(./*) > $maxStatements]
        ]]></value>
    </property>
  </properties>
</rule>
```

### Multivalued properties

Multivalued properties are also allowed and their `type` attribute has the form
`List[Boolean]` or `List[Character]`, with every above type allowed. These properties
make use of the **sequence datatype** provided by XPath 2.0 and above.
When providing multiple values, the delimiter is a simple comma ("`,`"). The comma can be escaped
with a backslash when needed.

```xml
<rule name="MyXpathRule" ...>
  <properties>
    <property name="intProp" type="List[Integer]" value="1,2,5" description="An IntegerMultiProperty." />
    <property name="reportedIdentifiers" type="List[String]" value="foo,bar"
              description="A StringMultiProperty." />
    <property name="xpath">
        <value><![CDATA[
            //VariableId[@Image = $reportedIdentifiers]
        ]]></value>
    </property>
  </properties>
</rule>
```

Notice that in the example above, `@Name = $reportedIdentifiers` doesn't test
`@Name` for equality with the whole sequence `('foo', 'bar')`, it tests whether
the sequence *contains* `@Name`. That is, the above rule will report all variables
named `foo` or `bar`. All other XPath 2.0 [functions operating on sequences](https://www.w3.org/TR/xpath-functions/#sequence-functions)
are supported.

{%include tip.html content="You can also [define properties directly in the designer](pmd_userdocs_extending_designer_reference.html#rule-properties)" %}
