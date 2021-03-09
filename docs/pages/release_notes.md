---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

*   java
    *   [#3117](https://github.com/pmd/pmd/issues/3117): \[java] Infinite loop when parsing invalid code nested in lambdas
    *   [#3145](https://github.com/pmd/pmd/issues/3145): \[java] Parse exception when using "record" as variable name
*   java-bestpractices
    *   [#3160](https://github.com/pmd/pmd/issues/3160): \[java] MethodReturnsInternalArray does not consider static final fields and fields initialized with empty array

### API Changes

### External Contributions

{% endtocmaker %}

