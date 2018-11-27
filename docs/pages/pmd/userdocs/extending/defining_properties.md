---
title: Defining rule properties
short_title: Defining rule properties
tags: [extending, userdocs]
summary: "Learn how to define your own properties both for Java and XPath rules."
last_updated: December 2017 (6.0.0)
permalink: pmd_userdocs_extending_defining_properties.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

{% jdoc_nspace :props core::properties %}

## Defining properties

If you're a rule developer, you may want to think about what would be useful for a user of your rule to parameterise. It could be a numeric report level, a boolean flag changing the behaviour of your rule... PMD ships with many types of properties ready to use!

### Overview of properties

The basic thing you need to do as a developer is to define a **property descriptor** and declare that your rule uses it. A property descriptor defines a number of attributes for your property:
* Its *name*, with which the user will refer to your property;
* Its *description*, for documentation purposes;
* Its *default value*

Don't worry, all of these attributes can be specified in a single Java statement (or xml element for XPath rules).

Without further ado, here is the list of available (single-value) properties:

|Class name|Value type|
|----------|----------|
|IntegerProperty | int
|DoubleProperty | double
|FloatProperty | float
|LongProperty | long
|EnumeratedProperty\<*E*\>| *E*
|StringProperty|String
|BooleanProperty|boolean
|CharacterProperty|char
|FileProperty|java.io.File
|MethodProperty|java.lang.reflect.Method
|TypeProperty|java.lang.Class\<?\>
|RegexProperty|java.util.regex.Pattern

Each of these is complemented by a multivalued variant, whose name ends with "MultiProperty", and which returns a list of values, e.g.

|Class name|Value type|
|----------|----------|
|LongMultiProperty | List\<Long\>
|EnumeratedMultiProperty\<*E*\>| List\<*E*\>

Note that RegexProperty doesn't have a multivalued variant, since the delimiters could be part of a specific value.

### For Java rules

The procedure to define a property is quite straightforward:
* Create a property descriptor of the type you want, using its builder;
* Call {% jdoc !a!props::PropertySource#definePropertyDescriptor(props::PropertyDescriptor) %}` in the rule's noarg constructor.

You can then retrieve the value of the property at any time using {% jdoc !a!props::PropertySource#getProperty(props::PropertyDescriptor) %}.

#### Creating a descriptor

From version 6.0.0 on, properties can be built using specific **builders**. For example, to build a string property, you'd call
```java
StringProperty.named("myProperty")
              .desc("This is my property")
              .defaultValue("foo")
              .build();
```

This is fairly more readable than a constructor call, but keep in mind the description and the default value are not optional.

{%include note.html content="The constructors may be deprecated in a future release, so please use the builders instead." %}

For **numeric properties**, you'd add a call to `range` to define the range of acceptable values, e.g.
```java
IntegerProperty.named("myIntProperty")
               .desc("This is my property")
               .defaultValue(3)
               .range(0, 100)
               .build();
```

**Enumerated properties** are a bit less straightforward to define, though they are arguably more powerful. These properties don't have a specific value type, instead, you can choose any type of value, provided the values are from a closed set. To make that actionable, you give string labels to each of the acceptable values, and the user will provide one of those labels as a value in the XML. The property will give you back the associated value, not the label. Here's an example:
```java
static Map<String, ModeStrategy> map = new HashMap<>();

static {
  map.put("easyMode", new EasyStrategy());
  map.put("hardMode", new HardStrategy());
}

static EnumeratedProperty<ModeStrategy> modeProperty
 = EnumeratedProperty.<ModeStrategy>named("modeProperty")
                     .desc("This is my property")
                     .defaultValue(new EasyStrategy())
                     .mappings(map)
                     .type(ModeStrategy.class)
                     .build();
```

Note that you're required to fill in the type of the values too, using `type()`.

#### Example

You can see an example of properties used in a PMD rule [here](https://github.com/pmd/pmd/blob/ac2ff0f6af8d16f739584ba8d00b7ea1a6311ccc/pmd-apex/src/main/java/net/sourceforge/pmd/lang/apex/rule/complexity/AvoidDeeplyNestedIfStmtsRule.java#L17).
There are several things to notice here:
* The property descriptor is declared `static final`, which should generally be the case, as descriptors are immutable and can be shared between instances of the same rule;
* The property is declared using `definePropertyDescriptor` *in the constructor*, which ensures the property gets recognised by PMD;
* The value of the property is *not retrieved in the constructor*, but in one of the `visit` methods (typically on the highest node in the tree, since the property doesn't change).



### For XPath rules

XPath rules can also define their own properties. To do so, you must add a `property` element in the `properties` element of your rule, which **declares the `type` attribute**. This attribute conditions what type the underlying property has, and can have the following values:

| `type` attribute | Property type|
|----------|----------|
|Integer|IntegerProperty
|Double | DoubleProperty
|Float|FloatProperty
|Long| LongProperty
|String|StringProperty
|Character|CharacterProperty
|Boolean|BooleanProperty
|Class|TypeProperty
|Regex|RegexProperty

Note that enumerated properties are not available in XPath rules (yet?).

Properties defined in XPath also *must* declare the `description` attribute. Numeric properties also expect the `min` and `max` attributes. Here are a few examples to sum it up:

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

#### Multivalued properties

Multivalued properties are also allowed and their `type` attribute has the form `List[Boolean]` or `List[Character]`, with every above type allowed. These properties **require XPath 2.0** to work properly, and make use of the **sequence datatype** provided by that language. You thus need to set the `version` property to `2.0` to use them. Properties can also declare the `delimiter` attribute.



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

Notice that in the example above, `@Image = $reportedIdentifiers` doesn't test `@Image` for equality with the whole sequence `('foo', 'bar')`, it tests whether the sequence *contains* `@Image`. That is, the above rule will report all variables named `foo` or `bar`. All other XPath 2.0 [functions operating on sequences](https://www.w3.org/TR/xpath-functions/#sequence-functions) are supported.

