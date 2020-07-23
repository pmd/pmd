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

*   apex-bestpractices
    *   [#2626](https://github.com/pmd/pmd/issues/2626): \[apex] UnusedLocalVariable - false positive on case insensitivity allowed in Apex
*   java-bestpractices
    *   [#2569](https://github.com/pmd/pmd/issues/2569): \[java] LiteralsFirstInComparisons: False negative for methods returning Strings

### API Changes

### External Contributions
*   [#2590](https://github.com/pmd/pmd/pull/2590): Update libraries snyk is referring to as `unsafe` - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2651](https://github.com/pmd/pmd/pull/2651): \[java] False negative: LiteralsFirstInComparisons for methods... (2569) - [Mykhailo Palahuta](https://github.com/Drofff)

{% endtocmaker %}

