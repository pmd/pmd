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
* core
  * [#4395](https://github.com/pmd/pmd/issues/4395): \[core] Support environment variable CLASSPATH with pmd.bat under Windows
* java-errorprone
  * [#4393](https://github.com/pmd/pmd/issues/4393): \[java] MissingStaticMethodInNonInstantiatableClass false-positive for Lombok's @UtilityClass for classes with non-private fields

### API Changes

* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
  * {% jdoc go::lang.go.GoLanguageModule %}

### External Contributions
* [#4384](https://github.com/pmd/pmd/pull/4384): \[swift] Add more swift 5.x support (#unavailable mainly) - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4392](https://github.com/pmd/pmd/pull/4392): \[java] Fix #4393 MissingStaticMethodInNonInstantiatableClass: Fix false-positive for field-only class - [Dawid Ciok](https://github.com/dawiddc) (@dawiddc)

{% endtocmaker %}

