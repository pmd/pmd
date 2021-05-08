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

#### Modified rules

*   The Java rule {% rule "java/errorprone/CompareObjectsWithEquals" %} has now a new property
    `typesThatCompareByReference`. With that property, you can configure types, that should be whitelisted
    for comparison by reference. By default, `java.lang.Enum` and `java.lang.Class` are allowed, but
    you could add custom types here.
    Additionally comparisons against constants are allowed now. This makes the rule less noisy when two constants
    are compared. Constants are identified by looking for an all-caps identifier.

### Fixed Issues

*   doc
    *   [#3230](https://github.com/pmd/pmd/issues/3230): \[doc] Remove "Edit me" button for language index pages
*   java-bestpractices
    *   [#3236](https://github.com/pmd/pmd/issues/3236): \[java] LiteralsFirstInComparisons should consider constant fields (cont'd)
*   java-codestyle
    *   [#2655](https://github.com/pmd/pmd/issues/2655): \[java] UnnecessaryImport false positive for on-demand imports
    *   [#3262](https://github.com/pmd/pmd/pull/3262): \[java] FieldDeclarationsShouldBeAtStartOfClass: false negative with anon classes
    *   [#3265](https://github.com/pmd/pmd/pull/3265): \[java] MethodArgumentCouldBeFinal: false negatives with interfaces and inner classes
    *   [#3266](https://github.com/pmd/pmd/pull/3266): \[java] LocalVariableCouldBeFinal: false negatives with interfaces, anon classes
*   java-errorprone
    *   [#3110](https://github.com/pmd/pmd/issues/3110): \[java] Enhance CompareObjectsWithEquals with list of exceptions
    *   [#3205](https://github.com/pmd/pmd/issues/3205): \[java] Make CompareObjectWithEquals allow comparing against constants

### API Changes

### External Contributions

{% endtocmaker %}

