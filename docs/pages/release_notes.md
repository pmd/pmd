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

### API Changes

* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
  * {% jdoc go::lang.go.GoLanguageModule %}

### External Contributions

{% endtocmaker %}

