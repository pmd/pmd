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

*   java
    *   [#1729](https://github.com/pmd/pmd/issues/1729): \[java] JavaRuleViolation loses information in `className` field when class has package-private access level
*   java-bestpractices
    *   [#1720](https://github.com/pmd/pmd/issues/1720): \[java] UnusedImports false positive for Javadoc link with array type
*   java-design
    *   [#1760](https://github.com/pmd/pmd/issues/1760): \[java] UseObjectForClearerAPI flags private methods

### API Changes

### External Contributions

*   [#1745](https://github.com/pmd/pmd/pull/1745): \[doc] Fixed some errors in docs - [0xflotus](https://github.com/0xflotus)
*   [#1746](https://github.com/pmd/pmd/pull/1746): \[java] Update rule to prevent UnusedImport when using JavaDoc with array type - [itaigilo](https://github.com/itaigilo)
*   [#1752](https://github.com/pmd/pmd/pull/1752): \[java] UseObjectForClearerAPI Only For Public - [Björn Kautler](https://github.com/Vampire)

{% endtocmaker %}

