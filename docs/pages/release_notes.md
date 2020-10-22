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
*   java-errorprone
    *   [#2764](https://github.com/pmd/pmd/issues/2764): \[java] CloseResourceRule does not recognize multiple assignment done to resource

### API Changes

### External Contributions
*   [#2811](https://github.com/pmd/pmd/pull/2811): \[java] CloseResource - Fix #2764: False-negative when re-assigning variable - [Andi Pabst](https://github.com/andipabst)

{% endtocmaker %}

