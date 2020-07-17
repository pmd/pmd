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
*   java-errorprone
    *   [#2578](https://github.com/pmd/pmd/issues/2578): \[java] AvoidCallingFinalize detects some false positives

### API Changes

### External Contributions
*   [#2590](https://github.com/pmd/pmd/pull/2590): Update libraries snyk is referring to as `unsafe` - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2643](https://github.com/pmd/pmd/pull/2643): \[java] AvoidCallingFinalize detects some false positives (2578) - [Mykhailo Palahuta](https://github.com/Drofff)

{% endtocmaker %}

