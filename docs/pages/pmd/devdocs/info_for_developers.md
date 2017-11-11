---
title: PMD Info for Developers
short_title: Info for Developers
tags: [customizing, properties, property]
summary: Info for PMD Developers
last_updated: July 3, 2016
permalink: pmd_devdocs_info_for_developers.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>
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

The new PMD properties subsystem is intended to bring some rigor and expanded functionality to the wild world of rule properies. It defines a value type template that can be used by IDE plugins to enumerate the properties specified by individual rules and provides validation and serialization services for multi-value properties. It uses custom serialization routines to generate human-readable values that can be edited in the XML files.

The subsystem implements the following property constructors with the leading name and description arguments not shown:

1.  BooleanProperty(…, boolean defaultValue, float uiOrder)
2.  BooleanProperty(…, boolean[] defaultValues, uiOrder, maxValues) BooleanProperty(…, Boolean[] defaultValues, float uiOrder, int maxValues)
3.  IntegerProperty(…, int defaultValue, float uiOrder)
4.  IntegerProperty(…, int[]defaultValues, float uiOrder, int maxValues) IntegerProperty(…, Integer[] defaultValues, float uiOrder, int maxValues)
5.  FloatProperty
6.  FloatProperty(…, float defaultValue, float uiOrder)
7.  FloatProperty(…, float[]defaultValues, float uiOrder, int maxValues) FloatProperty(…, Float[] defaultValues, float uiOrder, int maxValues)
8.  DoubleProperty
9.  DoubleProperty(…, int defaultValue, float uiOrder)
10.  DoubleProperty (…, double[]defaultValues, float uiOrder, int maxValues) DoubleProperty (…, Double[] defaultValues, float uiOrder, int maxValues)
11.  StringProperty
12.  StringProperty(…, String defaultValue, float uiOrder)
13.  StringProperty(…, String[] defaultValues, float uiOrder, char delimiter)

The delimiter character is used in the serialized string and cannot be part of the property value strings.

1.  TypeProperty(…, Class defaultValue, float uiOrder)
2.  TypeProperty(…, Class[]defaultValues, float uiOrder)

>(PMD doesn’t currently support full type resolution at the moment)

1.  CharacterProperty(…, char defaultValue, float uiOrder)
2.  CharacterProperty(…, char[] defaultValues, float uiOrder, char delimiter)
3.  CharacterProperty(…, Character[] defaultValues, float uiOrder, char delimiter)
4.  CharacterProperty(…, String defaultValues, float uiOrder, char delimiter)

The delimiter character is used in the serialized string and cannot be part of the property value characters.

1.  EnumeratedProperty(…, Object[][] values, float uiOrder)
2.  EnumeratedProperty(…, Object[][] values, float uiOrder, int maxValues)

The 2D value array holds the label-value tuples in the order that they should be presented in the UI widget. See usage below.

## For Rule Developers

All rule properties need to be characterized via individual PropertyDescriptors so that they can be viewed and adjusted the IDE plugin users. Since the descriptors never change at runtime we only need one of each so we create them as static singletons within the rule class definition. The following rule usage example makes use of a pair of integer properties:

```java
public MyVarNameLengthRule extends AbstractRule() {

private static final PropertyDescriptor minVarNameLength =
    new IntegerProperty(
        "minVarNameLength",
        "Minimum length for variable names",
        3,
        1.0f
    );

private static final PropertyDescriptor maxVarNameLength =
    new IntegerProperty(
        "maxVarNameLength",
        "Maximum length for variable names",
        30,
        1.1f
    );

private static final Map propertyDescriptorsByName = asFixedMap(
    new PropertyDescriptor[] { minVarNameLength, maxVarNameLength }
);

public MyVarNameLengthRule() { };

protected Map propertiesByName() {
    return propertyDescriptorsByName;
};

// rule body methods...
}
```

All property descriptors must be returned via the propertiesByName() method for each rule class.

Properties can also be multivalued, that is, we can capture and define a set of them at once:

```java
private static final PropertyDescriptor booleanPrefixes =
    new StringProperty(
        "booleanPrefixes",
        "Legal prefixes to use for boolean field names",
        new String[] { "is", "has", "can" },
        1.0f,
        '|' // reserved as delimiter
    );
```

There are at least two constructors for each property type, one that limits the property to a single value and another that accepts more than one.

In addition to the regular Java types such as Boolean, Integer, Float, Character, String, and Class/Type values you can also allow your rule users to pick between complex mixed datatypes such as maps or graphs that you define at compilation time:

```java
private static final Object[][] mixedItems = new Object[][] {
    {"map",         new HashMap()},
    {"emptyArray",  new Object[0]},
    {"list",        new ArrayList()},
    {"string",      "Hello World!"},
};

private static final PropertyDescriptor sampleObjects =
    new EnumeratedProperty(
        "testEnumerations",
        "Test enumerations with complex types",
        mixedItems,
        1.0f
    );
```

Note that Java values held by the EnumeratedProperty are not written out as property values themselves, we just write out the labels they are associated with. Specifying a label in the XML file for an object that doesn’t exist will result in an IllegalArgumentException.

## XML values

Defining the property rules within the ruleset XML files is straightforward for single values:

```xml
<properties>
    <property name="maxMethodArgs" value="2"/>
</properties>
```

When specifying multiple values you will need to separate them using the delimiter held by the property descriptor, most commonly a single pipe character, `|`:

```xml
<properties>
    <property name="legalListTypes"
              value="java.util.ArrayList|java.util.Vector|java.util.HashMap"/>
</properties>
```

You can define your own datatypes by implementing a subclass of AbstractPMDProperty and implementing the serialization, and validation routines listed in the PMDProperty interface. Just ensure that you create a corresponding JUnit test in the test.net.sourceforge.pmd.properties package to go along with it.

One of the implementation goals in this system is to try and come up with property constructors sufficiently useful that we don’t need to assemble them within static blocks. A single statement should be enough to build a rule property descriptor.

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
