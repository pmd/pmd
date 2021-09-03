---
title: Defining rule properties
short_title: Defining rule properties
tags: [extending, userdocs]
summary: "Learn how to define your own properties both for Java and XPath rules."
last_updated: February 2020 (6.22.0)
permalink: pmd_userdocs_extending_defining_properties.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PF props::PropertyFactory %}

Rule properties are a way to make your rules configurable directly from the
ruleset XML. Their usage is described on the [Configuring Rules](pmd_userdocs_configuring_rules.html#rule-properties) page.

If you're a rule developer, you may want to think about what would be useful for
a user of your rule to parameterise. It could be a numeric report level, a boolean
flag changing the behaviour of your rule... Chances are there *is* some detail
that can be abstracted away from your implementation, and in that case, this
page can help you squeeze that sweet flexibility out of your rule.

## Overview of properties

The basic thing you need to do as a developer is to define a **property descriptor** and declare that your rule uses it. A property descriptor defines a number of attributes for your property:
* Its *name*, with which the user will refer to your property;
* Its *description*, for documentation purposes;
* Its *default value*

Don't worry, all of these attributes can be specified in a single Java statement (or xml element for XPath rules).


## For Java rules

The procedure to define a property is quite straightforward:
* Create a property descriptor of the type you want, by using a
builder from {% jdoc :PF %}
* Call {% jdoc !a!props::PropertySource#definePropertyDescriptor(props::PropertyDescriptor) %}` in the rule's noarg constructor.

You can then retrieve the value of the property at any time using {% jdoc !a!props::PropertySource#getProperty(props::PropertyDescriptor) %}.

### Creating a descriptor

Properties can be built using type-specific **builders**, which can be obtained
from the factory methods of {% jdoc :PF %}. For example, to build a
string property, you'd call
```java
PropertyFactory.stringProperty("myProperty")
               .desc("This is my property")
               .defaultValue("foo")
               .build();
```

This is fairly more readable than a constructor call, but keep in mind the description and the default value are not optional.

{%include note.html
content='As of version 6.10.0, all property concrete classes are deprecated for
removal in 7.0.0. See the <a href="pmd_next_major_development.html#properties-framework">detailed list of planned removals</a> for
information about how to migrate.' %}


For **numeric properties**, you can add constraints on the range of acceptable values, e.g.
```java
PropertyFactory.intProperty("myIntProperty")
               .desc("This is my property")
               .defaultValue(3)
               .require(positive())
               .range(0, 100)
               .build();
```

The {% jdoc props::constraints.NumericConstraints#positive() %} method is part of
the {% jdoc props::constraints.NumericConstraints %} class, which provides some
other constraints. The constraint mechanism will be completely unlocked with 7.0.0,
since we'll be migrating our API to Java 8.

**Enumerated properties** are a bit less straightforward to define, though they are
arguably more powerful. These properties don't have a specific value type, instead,
you can choose any type of value, provided the values are from a closed set. To make
that actionable, you give string labels to each of the acceptable values, and the user
will provide one of those labels as a value in the XML. The property will give you back
the associated value, not the label. Here's an example:
```java
static Map<String, ModeStrategy> map = new HashMap<>();

static {
  map.put("easyMode", new EasyStrategy());
  map.put("hardMode", new HardStrategy());
}

static PropertyDescriptor<ModeStrategy> modeProperty
 = PropertyFactory.enumProperty("modeProperty", map)
                  .desc("This is my property")
                  .defaultValue(new EasyStrategy())
                  .build();
```

### Example

You can see an example of properties used in a PMD rule [here](https://github.com/pmd/pmd/blob/d06b01785a712e61d33f366520f37c2473f5bd1a/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SingularFieldRule.java#L43-L52).
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

XPath rules can also define their own properties. To do so, you must add a `property` element in the `properties` element of your rule, which **declares the `type` attribute**. This attribute conditions what type the underlying property has, and can have the following values:

| `type` attribute | XSD type
|----------|----------|
|Integer  | xs:integer
|Long     | xs:integer
|Double   | xs:decimal
|Boolean  | xs:boolean
|String   | xs:string
|Character| xs:string
|Regex    | xs:string

{% include note.html
           content="In XPath 1.0 mode, all values are actually represented as
                    string values, which is mostly fine as there is no type
                    checking. This is a problem when [migrating from XPath 1.0
                    to 2.0](pmd_userdocs_extending_writing_xpath_rules.html#migrating-from-10-to-20) though" %}


Note that enumerated properties are not available in XPath rules (yet?).

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
    <![CDATA[
      //MethodDeclaration/Block[count(//BlockStatement) > $maxStatements]
    ]]></property>
  </properties>
</rule>
```

### Multivalued properties

Multivalued properties are also allowed and their `type` attribute has the form
`List[Boolean]` or `List[Character]`, with every above type allowed. These
properties **require XPath 2.0** to work properly, and make use of the
**sequence datatype** provided by that language. You thus need to set the
`version` property to `2.0` to use them. Properties can also declare the
`delimiter` attribute.



```xml
<rule name="MyXpathRule" ...>
  <properties>
    <property name="version" value="2.0" />
    <property name="intProp" type="List[Integer]" value="1,2,5" description="An IntegerMultiProperty." />
    <property name="reportedIdentifiers" type="List[String]" value="foo$bar" delimiter="$"
              description="A StringMultiProperty." />
    <property name="xpath">
    <![CDATA[
      //VariableDeclaratorId[@Image = $reportedIdentifiers]
    ]]></property>
  </properties>
</rule>
```

Notice that in the example above, `@Image = $reportedIdentifiers` doesn't test
`@Image` for equality with the whole sequence `('foo', 'bar')`, it tests whether
the sequence *contains* `@Image`. That is, the above rule will report all variables
named `foo` or `bar`. All other XPath 2.0 [functions operating on sequences](https://www.w3.org/TR/xpath-functions/#sequence-functions)
are supported.

{%include tip.html content="You can also [define properties directly in the designer](pmd_userdocs_extending_designer_reference.html#rule-properties)" %}
