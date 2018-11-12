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

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis

### API Changes

* The implementation of the adapters for the XPath engines Saxon and Jaxen (package `net.sourceforge.pmd.lang.ast.xpath`)
  are now deprecated. They'll be moved to an internal package come 7.0.0. Only `Attribute` remains public API.

* Several classes and interfaces from the properties framework are now deprecated and will be removed with 7.0.0.
  * MethodProperty, FloatProperty, FileProperty, TypeProperty and their multi-valued counterparts
    are discontinued for lack of a use-case, and will probably not be replaced with 7.0.0.
    Users of FloatProperty should consider using a DoubleProperty.
  * EnumeratedPropertyDescriptor, NumericPropertyDescriptor, PackagedPropertyDescriptor, and the related builders
    (in net.sourceforge.pmd.properties.builders) will be removed. In the future, these interfaces won't be around
    but their functionality will, under another form. The related methods `PropertyTypeId#isPropertyNumeric` and
    `PropertyTypeId#isPropertyPackaged` are also deprecated.
  * All classes of net.sourceforge.pmd.properties.modules are deprecated and will be removed. They were
    never intended as public api.
  * The classes PropertyDescriptorField, PropertyDescriptorBuilderConversionWrapper, and the methods
    `PropertyDescriptor#attributeValuesById`, `PropertyDescriptor#isDefinedExternally` and `PropertyTypeId#getFactory` are deprecated with no
    intended replacement. These were used to read and write properties to and from XML, and were never
    intended as public API.
  * The class ValueParserConstants is deprecated with no intended replacement, it was not intended as
    public API.
  * The method `PropertyDescriptor#preferredRowCount` is deprecated with no intended replacement. It was
    never implemented, and does not belong in this interface.

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)

{% endtocmaker %}

{% include note.html content="The release notes of previous versions are available [here](pmd_release_notes_old.html)" %}

