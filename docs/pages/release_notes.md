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
    *   [#2543](https://github.com/pmd/pmd/issues/2543): \[java] UseCollectionIsEmpty can not detect the case this.foo.size()

### API Changes

### External Contributions
*   [#2590](https://github.com/pmd/pmd/pull/2590): Update libraries snyk is referring to as `unsafe` - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2652](https://github.com/pmd/pmd/pull/2652): \[java] UseCollectionIsEmpty can not detect the case this.foo.size() - [Mykhailo Palahuta](https://github.com/Drofff)

{% endtocmaker %}

