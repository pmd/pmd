---
title: Working with properties
short_title: Working with properties
tags: [customizing, properties, property]
summary: "Properties are a way to parameterise the behaviour of PMD rules and your own directly from the ruleset XML, which greatly improves their reusability. This page explains how to use existing properties, and how to define your own both for Java and XPath rules."
last_updated: November 2017 (6.0.0)
permalink: pmd_devdocs_working_with_properties.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>, Clément Fournier <clement.fournier76@gmail.com>
---

# Working with properties

## What are properties? How do I use them?

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

{%include tip.html content="The properties of a rule are documented with the rule, e.g. [here](pmd_rules_java_design.html#npathcomplexity) for NPathComplexity. Note that assigning a value to a property that does not exist throws an error!" %}

Some properties take multiple values (a list), in which case you can provide them all by delimiting them with a delimiter character. It is usually a pipe ('\|'), or a comma (',') for numeric properties, e.g.
```xml
 <property name="legalListTypes"
           value="java.util.ArrayList|java.util.Vector|java.util.HashMap"/>
```

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
|MethodProperty|java.reflect.Method
|TypeProperty|java.reflect.Class\<?\>

Each of these is complemented by a multivalued variant, whose name ends with "MultiProperty", and which returns a list of values, e.g.

|Class name|Value type|
|----------|----------|
|LongMultiProperty | List\<Long\>
|EnumeratedMultiProperty\<*E*\>| List\<*E*\>


### For Java rules

The procedure to define a property is quite straightforward:
* Create a property descriptor of the type you want, using its builder;
* Call `definePropertyDescriptor(<your descriptor>)` in the rule's noarg constructor.

You can then retrieve the value of the property at any time using `getProperty(<your descriptor>)`.

#### Creating a descriptor

From version 6.0.0 on, properties can be built using specific builders. For example, to build a string property, you'd call
```java
StringProperty.named("myProperty")
              .description("This is my property")
              .defaultValue("foo")
              .build();
```

This is fairly more readable than a constructor call, but keep in mind the description and the default value are not optional.

For numeric properties, you'd add a call to `range` to define the range of acceptable values, e.g.
```java
IntegerProperty.named("myIntProperty")
               .description("This is my property")
               .defaultValue(3)
               .range(0, 100)
               .build();
```

Enumerated properties are a bit less straightforward to define, though they are arguably more powerful. These properties don't have a specific value type, instead, you can choose any type of value, provided the values are from a closed set. To make that actionable, you give string labels to each of the acceptable values, and the user will provide one of those labels as a value. The property will give you back the correct value, not just the label. Here's an example:
```java
static Map<String, ModeStrategy> map = new HashMap<>();

static {
  map.put("easyMode", new EasyStrategy());
  map.put("hardMode", new HardStrategy());
}

static EnumeratedProperty<ModeStrategy> modeProperty
 = EnumeratedProperty.<ModeStrategy>named("modeProperty")
                     .description("This is my property")
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
|File|FileProperty
|Class|TypeProperty
|Method|MethodProperty

Note that enumerated properties are not available in XPath rules (yet).

Properties defined in XPath also *must* declare the `description` attribute. Numeric properties also expect the `min` and `max` attributes. Here are a few examples to sum it up:

```xml
<property name="stringProp" type="Boolean" value="true" description="A BooleanProperty."/>
<property name="intProp" type="Integer" value="3" min="1" max="20" description="An IntegerProperty."/>
```

You can then use the property in XPath with the syntax `$propertyName`.

#### Multivalued properties


Multivalued properties are also allowed and their `type` attribute has the form `List[Boolean]` or `List[Character]`, with every above type allowed (except `File`). These properties **require XPath 2.0** to work properly, and make use of the **sequence datatype** provided by that language. You thus need to set the `version` property to `2.0` to use them. Properties can also declare the `delimiter` attribute.



```xml
<rule name="MyXpathRule" ...>
  <properties>
    <property name="version" value="2.0" />
    <property name="i" type="List[Integer]" value="1,2,5" description="An IntegerMultiProperty." />
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


<!-- Below is part of the old article -->
## For IDE Plugin Developers

In order to assemble an effective UI to manage the rule properties the following setup sequence is suggested:

*   Determine whether the value is open-ended or enumerated. For example, can the user type in their own values or should they pick them from a list? Invoke the choices() method that may return a 2D array of label-value pairs you can use to populate your list widget. If the method returns null then jump to step #2.

    You may need to maintain a mapping of the label-value pairs to translate between them depending on the capabilities of your list widget. The first pair in the array represents the default property value.

*   For open-ended values, determine the cardinality of the property via the maxValueCount() method. If it returns a value of one then use the type() method to determine the type-specific entry field could use next. I.e. a checkbox for booleans, text field for strings, spin buttons for ints, etc.

*   If the property supports more than one value then you may opt to use a single text field and parse the entries after the user leaves or hits the return key or you can create a read-only widget and add/remove values via a popup dialog.

*   All multi-value properties make use of a character to delimit the values in their serialized form so you will need to ensure that you prevent the user from entering values containing it. Retrieve the delimiter via the multivaluedelimiter() method.

You can use the errorFor(value) method to validate the values entered by the user or check the values held by the rule configuration file. It returns null or an error message as appropriate. It would be best to flag and disable rules that have invalid property values.

Use the defaultValue() method to reset the rule properties to their default value.

The two serialization methods, valueFrom() and asDelimitedString(), are to be used to retrive and store property values respectively.

Widgets should be ordered vertically according to the values returned by the uiOrder() method with lower-valued properties appearing above the ones with higher values. The order of the property descriptors returned from the rule cannot be guaranteed to be the same as the presentation order. If the two or more widgets share the same integer value then you can use the fractional portions to place their widgets in a horizontal sequence (if possible).

For types that can have null values, such as strings, then use the isRequired() method to flag any possible missing values.

If a property field is multivalued then the maximum number of values it can hold is set to largest possible int value unless set explicitly in a rule property constructor.

## ToDo items

1.  Expand this note with further examples
2.  Internationalize error strings in the code
3.  Provide for additional datatypes such as Date
4.  Figure out the best way to add the rowCount value to the StringProperty constructor
