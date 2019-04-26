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

#### Dart support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Dart](https://www.dartlang.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   ignore imports / libraries
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Modified Rules

*   The Java rule {% rule "java/errorprone/AssignmentToNonFinalStatic" %} (`java-errorprone`) will now report on each
    assignment made within a constructor rather than on the field declaration. This makes it easier for developers to
    find the offending statements.

*   The Java rule {% rule "java/codestyle/NoPackage" %} (`java-codestyle`) will now report additionally enums
    and annotations that do not have a package declaration.

### Fixed Issues

*   go
    *   [#1751](https://github.com/pmd/pmd/issues/1751): \[go] Parsing errors encountered with escaped backslash
*   java
    *   [#1729](https://github.com/pmd/pmd/issues/1729): \[java] JavaRuleViolation loses information in `className` field when class has package-private access level
*   java-bestpractices
    *   [#1720](https://github.com/pmd/pmd/issues/1720): \[java] UnusedImports false positive for Javadoc link with array type
*   java-codestyle
    *   [#1782](https://github.com/pmd/pmd/issues/1782): \[java] NoPackage: False Negative for enums
*   java-design
    *   [#1760](https://github.com/pmd/pmd/issues/1760): \[java] UseObjectForClearerAPI flags private methods

### API Changes

### External Contributions

*   [#1745](https://github.com/pmd/pmd/pull/1745): \[doc] Fixed some errors in docs - [0xflotus](https://github.com/0xflotus)
*   [#1746](https://github.com/pmd/pmd/pull/1746): \[java] Update rule to prevent UnusedImport when using JavaDoc with array type - [itaigilo](https://github.com/itaigilo)
*   [#1752](https://github.com/pmd/pmd/pull/1752): \[java] UseObjectForClearerAPI Only For Public - [Björn Kautler](https://github.com/Vampire)
*   [#1761](https://github.com/pmd/pmd/pull/1761): \[dart] \[cpd] Added CPD support for Dart - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1776](https://github.com/pmd/pmd/pull/1776): \[java] Show more detailed message when can't resolve field type - [Andrey Fomin](https://github.com/andrey-fomin)
*   [#1781](https://github.com/pmd/pmd/pull/1781): \[java] Location change in AssignmentToNonFinalStatic - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1791](https://github.com/pmd/pmd/pull/1791): \[dart] \[cpd] Dart escaped string - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}

