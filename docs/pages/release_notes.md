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

*   apex
    *   [#3462](https://github.com/pmd/pmd/issues/3462): \[apex] SOQL performed in a for-each loop doesn't trigger ApexCRUDViolationRule
    *   [#3484](https://github.com/pmd/pmd/issues/3484): \[apex] ApexCRUDViolationRule maintains state across files
*   core
    *   [#3446](https://github.com/pmd/pmd/issues/3446): \[core] Allow XPath rules to access the current file name
*   java-bestpractices
    *   [#3403](https://github.com/pmd/pmd/issues/3403): \[java] MethodNamingConventions junit5TestPattern does not detect parameterized tests

### API Changes

### External Contributions

*   [#3445](https://github.com/pmd/pmd/pull/3445): \[java] Fix #3403 about MethodNamingConventions and JUnit5 parameterized tests - [Cyril Sicard](https://github.com/CyrilSicard)
*   [#3470](https://github.com/pmd/pmd/pull/3470): \[apex] Fix ApexCRUDViolationRule - add super call - [Josh Feingold](https://github.com/jfeingold35)

{% endtocmaker %}

