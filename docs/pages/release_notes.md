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

#### Javascript module now requires at least Java 8

The latest version of [Rhino](https://github.com/mozilla/rhino), the implementation of JavaScript we use
for parsing JavaScript code, requires at least Java 8. Therefore we decided to upgrade the pmd-javascript
module to Java 8 as well. This means, that from now on, a Java 8 or later runtime is required in order
to analyze JavaScript code. Note, that PMD core still stays the at Java 7.

### Fixed Issues

*   pmd-javascript
    *   [#699](https://github.com/pmd/pmd/issues/699): \[javascript] Update Rhino library to 1.7.13
    *   [#2081](https://github.com/pmd/pmd/issues/2081): \[javascript] Failing with OutOfMemoryError parsing a Javascript file

### API Changes

### External Contributions

{% endtocmaker %}

