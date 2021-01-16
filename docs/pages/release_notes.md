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

*   core
    *   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings
*   java-bestpractices
    *   [#575](https://github.com/pmd/pmd/issues/575): \[java] LiteralsFirstInComparisons should consider constant fields

### API Changes

### External Contributions

*   [#2964](https://github.com/pmd/pmd/pull/2964): \[cs] Update C# grammar for additional C# 7 and C# 8 features - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2983](https://github.com/pmd/pmd/pull/2983): \[java] LiteralsFirstInComparisons should consider constant fields - [Ozan Gulle](https://github.com/ozangulle)
*   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings - [Vincent Maurin](https://github.com/vmaurin)

{% endtocmaker %}

