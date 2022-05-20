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

* java-design
  * [#3874](https://github.com/pmd/pmd/issues/3874): \[java] ImmutableField reports fields annotated with @Autowired (Spring) and @Mock (Mockito)
* javascript
  * [#3948](https://github.com/pmd/pmd/issues/3948): \[js] Invalid operator error for method property in object literal

### API Changes

### External Contributions

* [#3964](https://github.com/pmd/pmd/pull/3964): \[java] Fix #3874 - ImmutableField: fix mockito/spring false positives - [@lukelukes](https://github.com/lukelukes)

{% endtocmaker %}

