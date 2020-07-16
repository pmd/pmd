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
*   java-design
    *   [#2174](https://github.com/pmd/pmd/issues/2174): \[java] LawOfDemeter: False positive with 'this' pointer
    *   [#2189](https://github.com/pmd/pmd/issues/2189): \[java] LawOfDemeter: False positive when casting to derived class

### API Changes

### External Contributions
*   [#2560](https://github.com/pmd/pmd/pull/2560): \[java] Fix false positives of LawOfDemeter: this and cast expressions - [xioayuge](https://github.com/xioayuge)

{% endtocmaker %}

