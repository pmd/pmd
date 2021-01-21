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
    *   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings
*   java-bestpractices
    *   [#575](https://github.com/pmd/pmd/issues/575): \[java] LiteralsFirstInComparisons should consider constant fields
*   java-errorprone
    *   [#2976](https://github.com/pmd/pmd/issues/2976): \[java] CompareObjectsWithEquals: FP with array.length
    *   [#2977](https://github.com/pmd/pmd/issues/2977): \[java] 6.30.0 introduces new false positive in CloseResource rule?
    *   [#2979](https://github.com/pmd/pmd/issues/2979): \[java] UseEqualsToCompareStrings: FP with "var" variables
    *   [#3004](https://github.com/pmd/pmd/issues/3004): \[java] UseEqualsToCompareStrings false positive with PMD 6.30.0

### API Changes

### External Contributions

*   [#2964](https://github.com/pmd/pmd/pull/2964): \[cs] Update C# grammar for additional C# 7 and C# 8 features - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2983](https://github.com/pmd/pmd/pull/2983): \[java] LiteralsFirstInComparisons should consider constant fields - [Ozan Gulle](https://github.com/ozangulle)
*   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings - [Vincent Maurin](https://github.com/vmaurin)

{% endtocmaker %}

