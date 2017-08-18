---
title: PMD Info for Developers
short_title: Info for Developers
tags: [customizing]
summary: Info for PMD Developers
last_updated: July 3, 2016
permalink: pmd_devdocs_info_for_developers.html
author: Hooper Bloob <hooperbloob@users.sourceforge.net>, Romain Pelisse <rpelisse@users.sourceforge.net>
---

# Information for PMD Developers

Some piece of information handy to PMD developers. Clearly not everything, but some useful bits :)

## PMD Properties

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

Properties can also be multi-valued, that is, we can capture and define a set of them at once:

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

*   All multi-value properties make use of a character to delimit the values in their serialized form so you will need to ensure that you prevent the user from entering values containing it. Retrieve the delimiter via the multiValueDelimiter() method.

You can use the errorFor(value) method to validate the values entered by the user or check the values held by the rule configuration file. It returns null or an error message as appropriate. It would be best to flag and disable rules that have invalid property values.

Use the defaultValue() method to reset the rule properties to their default value.

The two serialization methods, valueFrom() and asDelimitedString(), are to be used to retrive and store property values respectively.

Widgets should be ordered vertically according to the values returned by the uiOrder() method with lower-valued properties appearing above the ones with higher values. The order of the property descriptors returned from the rule cannot be guaranteed to be the same as the presentation order. If the two or more widgets share the same integer value then you can use the fractional portions to place their widgets in a horizontal sequence (if possible).

For types that can have null values, such as strings, then use the isRequired() method to flag any possible missing values.

If a property field is multi-valued then the maximum number of values it can hold is set to largest possible int value unless set explicitly in a rule property constructor.

## ToDo items

1.  Expand this note with further examples
2.  Internationalize error strings in the code
3.  Provide for additional datatypes such as Date
4.  Figure out the best way to add the rowCount value to the StringProperty constructor
