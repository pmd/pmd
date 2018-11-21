---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### New Rules

*   The new Java rule {% rule "java/codestyle/UseUnderscoresInNumericLiterals" %} (`java-codestyle`)
    verifies that numeric literals over a given length (4 chars by default, but configurable) are using
    underscores every 3 digits for readability. The rule only applies to Java 7+ codebases.

#### Modified Rules

*   The Java rule {% rule "java/bestpractices/JUnitTestsShouldIncludeAssert" %} (`java-bestpractices`)
    now also detects [Soft Assertions](https://github.com/joel-costigliola/assertj-core).

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
*   java
    *   [#1460](https://github.com/pmd/pmd/issues/1460): \[java] Intermittent PMD failure : PMD processing errors while no violations reported
*   java-bestpractices
    *   [#1435](https://github.com/pmd/pmd/issues/1435): \[java] JUnitTestsShouldIncludeAssert: Support AssertJ soft assertions
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis
    *   [#1440](https://github.com/pmd/pmd/issues/1440): \[java] CommentDefaultAccessModifierRule shows incorrect message

### API Changes

#### Properties framework

The properties framework is about to get a lifting, and for that reason, the following APIs are
now deprecated until 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

* Several classes and interfaces from the properties framework are now deprecated and will be removed with 7.0.0.
  * `MethodProperty`, `FloatProperty`, `FileProperty`, `TypeProperty` and their multi-valued counterparts
    are discontinued for lack of a use-case, and will probably not be replaced with 7.0.0.
    Users of `FloatProperty` should consider using a `DoubleProperty`.
  * `EnumeratedPropertyDescriptor`, `NumericPropertyDescriptor`, `PackagedPropertyDescriptor`, and the related builders
    (in `net.sourceforge.pmd.properties.builders`) will be removed. In the future, these interfaces won't be around
    but their functionality will, under another form. The related methods `PropertyTypeId#isPropertyNumeric` and
    `PropertyTypeId#isPropertyPackaged` are also deprecated.
  * All classes of net.sourceforge.pmd.properties.modules are deprecated and will be removed. They were
    never intended as public api.
  * The classes `PropertyDescriptorField`, `PropertyDescriptorBuilderConversionWrapper`, and the methods
    `PropertyDescriptor#attributeValuesById`, `PropertyDescriptor#isDefinedExternally` and `PropertyTypeId#getFactory` are deprecated with no
    intended replacement. These were used to read and write properties to and from XML, and were never
    intended as public API.
  * The class `ValueParserConstants` and the interface `ValueParser` are deprecated with no intended replacement,
    they were not intended as public API.
  * Methods from `PropertyDescriptor`:
    * `preferredRowCount` is deprecated with no intended replacement. It was never implemented, and does not belong
      in this interface. The methods `uiOrder` and `compareTo` are deprecated for the same reason. These methods mix presentation logic
      with business logic and are not necessary for PropertyDescriptors to work. `PropertyDescriptor` will not
      extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
    * The method `PropertyDescriptor#propertyErrorFor` is deprecated and will be removed with no intended
      replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
    * `T valueFrom(String)` and `String asDelimitedString(T)` are deprecated and will be removed. These were
      used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
      XML syntax which will make them obsolete.
    * `isMultiValue` and `type` are deprecated and won't be replaced. The new XML syntax will remove the need
      for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
      Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
      which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
      new way to document these properties exhaustively will be added with 7.0.0.
    * `errorFor` is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

#### Deprecated APIs

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package `net.sourceforge.pmd.lang.ast.xpath`)
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only `Attribute` remains public API.

*   The interface `net.sourceforge.pmd.lang.java.ast.Dimensionable` has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from LocalVariableDeclaration and FieldDeclaration have also been deprecated:

    *   FieldDeclaration won't be a TypeNode come 7.0.0, so `getType` and `getTypeDefinition` are deprecated.

    *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<ASTVariableDeclaratorId>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)
*   [#1434](https://github.com/pmd/pmd/pull/1434): \[java] JUnitTestsShouldIncludeAssert: Recognize AssertJ soft assertions as valid assert statements - [Loïc Ledoyen](https://github.com/ledoyen)
*   [#1447](https://github.com/pmd/pmd/pull/1447): \[fortran] Use diamond operator in impl - [reudismam](https://github.com/reudismam)
*   [#1453](https://github.com/pmd/pmd/pull/1453): \[java] Adding the fix for #1440. Showing correct message for CommentDefaultAccessmodifier. - [Rohit Kumar](https://github.com/stationeros)
*   [#1464](https://github.com/pmd/pmd/pull/1464): \[doc] Fix XSS on documentation web page - [Maxime Robert](https://github.com/marob)

{% endtocmaker %}

{% include note.html content="The release notes of previous versions are available [here](pmd_release_notes_old.html)" %}

