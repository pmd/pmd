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
    *   [#3117](https://github.com/pmd/pmd/issues/3117): \[java] Infinite loop when parsing invalid code nested in lambdas
    *   [#3145](https://github.com/pmd/pmd/issues/3145): \[java] Parse exception when using "record" as variable name
*   java-bestpractices
    *   [#3144](https://github.com/pmd/pmd/issues/3144): \[java] GuardLogStatement can have more detailed example
    *   [#3155](https://github.com/pmd/pmd/pull/3155): \[java] GuardLogStatement: False negative with unguarded method call
    *   [#3160](https://github.com/pmd/pmd/issues/3160): \[java] MethodReturnsInternalArray does not consider static final fields and fields initialized with empty array
*   java-errorprone
    *   [#3146](https://github.com/pmd/pmd/issues/3146): \[java] InvalidLogMessageFormat detection failing when String.format used
*   java-performance
    *   [#2427](https://github.com/pmd/pmd/issues/2427): \[java] ConsecutiveLiteralAppend false-positive with builder inside lambda
    *   [#3152](https://github.com/pmd/pmd/issues/3152): \[java] ConsecutiveLiteralAppends and InsufficientStringBufferDeclaration: FP with switch expressions

### API Changes

### External Contributions

*   [#3167](https://github.com/pmd/pmd/pull/3167): \[java] Minor typo in quickstart ruleset - [Austin Tice](https://github.com/AustinTice)

{% endtocmaker %}

