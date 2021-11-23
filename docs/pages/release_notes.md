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

*   core
    *   [#3635](https://github.com/pmd/pmd/issues/3635): \[java] Update sample projects for regression tester
*   java-performance
    *   [#3491](https://github.com/pmd/pmd/issues/3491): \[java] UselessStringValueOf: False positive when `valueOf(char [], int, int)` is used

### API Changes

### External Contributions

*   [#3612](https://github.com/pmd/pmd/pull/3612): \[java] Created fix for UselessStringValueOf false positive - [John Armgardt](https://github.com/johnra2)

{% endtocmaker %}

