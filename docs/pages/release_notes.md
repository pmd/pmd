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

* pmd-java
   * [#2708](https://github.com/pmd/pmd/pull/2708): \[java] False positive FinalFieldCouldBeStatic when using lombok Builder.Default
    * [#2756](https://github.com/pmd/pmd/issues/2756): \[java] TypeTestUtil fails with NPE for anonymous class


### API Changes

### External Contributions

* [#2747](https://github.com/pmd/pmd/pull/2747): \[java] Don't trigger FinalFieldCouldBeStatic when field is annotated with lombok @Builder.Default - [Ollie Abbey](https://github.com/ollieabbey)

{% endtocmaker %}

