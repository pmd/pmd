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

#### Modified rules

*   The Java rule {% rule "java/bestpractices/ArrayIsStoredDirectly" %} (`java-bestpractices`) now ignores
    by default private methods and constructors. You can restore the old behavior by setting the new property
    `allowPrivate` to "false".

### Fixed Issues

*   apex-bestpractices
    *   [#2626](https://github.com/pmd/pmd/issues/2626): \[apex] UnusedLocalVariable - false positive on case insensitivity allowed in Apex
*   java-bestpractices
    *   [#2622](https://github.com/pmd/pmd/issues/2622): \[java] ArrayIsStoredDirectly false positive with private constructor/methods

### API Changes

### External Contributions

{% endtocmaker %}

