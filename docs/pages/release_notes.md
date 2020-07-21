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
*   java-design
    *   [#2580](https://github.com/pmd/pmd/issues/2580): \[java] AvoidThrowingNullPointerException marks all NullPointerException objects as wrong, whether or not thrown

### API Changes

### External Contributions
*   [#2641](https://github.com/pmd/pmd/pull/2641): \[java] AvoidThrowingNullPointerException marks all NullPointerException… - [Mykhailo Palahuta](https://github.com/Drofff)

{% endtocmaker %}

