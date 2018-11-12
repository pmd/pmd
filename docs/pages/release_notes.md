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

### Fixed Issues

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing

### API Changes

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
  * The classes `PropertyDescriptorField`, `PropertyDescriptorBuilderConversionWrapper`, and the methods
    `PropertyDescriptor#attributeValuesById`, `PropertyDescriptor#isDefinedExternally` and `PropertyTypeId#getFactory` are deprecated with no
    intended replacement. These were used to read and write properties to and from XML, and were never
    intended as public API.
  * The class ValueParserConstants is deprecated with no intended replacement, it was not intended as
    public API.
  * The method `PropertyDescriptor#preferredRowCount` is deprecated with no intended replacement. It was
    never implemented, and does not belong in this interface.

### External Contributions

{% endtocmaker %}

