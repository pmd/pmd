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

*   java-bestpractices
    *   [#3613](https://github.com/pmd/pmd/issues/3613): \[java] ArrayIsStoredDirectly doesn't consider nested classes
    *   [#3614](https://github.com/pmd/pmd/issues/3614): \[java] JUnitTestsShouldIncludeAssert doesn't consider nested classes
*   java-errorprone
    *   [#3624](https://github.com/pmd/pmd/issues/3624): \[java] TestClassWithoutTestCases reports wrong classes in a file

### API Changes

### External Contributions

{% endtocmaker %}

