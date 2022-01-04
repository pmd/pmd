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

*   java-bestpractices
    *   [#3209](https://github.com/pmd/pmd/issues/3209): \[java] UnusedPrivateMethod false positive with static method and cast expression
    *   [#3468](https://github.com/pmd/pmd/issues/3468): \[java] UnusedPrivateMethod false positive when outer class calls private static method on inner class
*   java-performance
    *   [#3492](https://github.com/pmd/pmd/issues/3492): \[java] UselessStringValueOf: False positive when there is no initial String to append to

### API Changes

### External Contributions

*   [#3631](https://github.com/pmd/pmd/pull/3631): \[java] Fixed False positive for UselessStringValueOf when there is no initial String to append to - [John Armgardt](https://github.com/johnra2)
*   [#3683](https://github.com/pmd/pmd/pull/3683): \[java] Fixed 3468 UnusedPrivateMethod false positive when outer class calls private static method on inner class - [John Armgardt](https://github.com/johnra2)
*   [#3688](https://github.com/pmd/pmd/pull/3688): \[java] Bump log4j to 2.16.0 - [Sergey Nuyanzin](https://github.com/snuyanzin)

{% endtocmaker %}

