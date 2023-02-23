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

#### PMD 7 Development

This release is the last planned release of PMD 6. The first version 6.0.0 was released in December 2017.
Over the course of more than 5 years we published almost every month a new minor version of PMD 6
with new features and improvements.

Already in November 2018 we started in parallel the development of the next major version 7.0.0,
and we are now in the process of finalizing the scope of the major version. We want to release a couple of
release candidates before publishing the final version 7.0.0.

We plan to release 7.0.0-rc1 soon. You can see the progress in [PMD 7 Tracking Issue #3898](https://github.com/pmd/pmd/issues/3898).

#### T-SQL support
Thanks to the contribution from [Paul Guyot](https://github.com/pguyot) PMD now has CPD support
for T-SQL (Transact-SQL).

Being based on a proper Antlr grammar, CPD can:

* ignore comments
* honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Fixed Issues
* java-errorprone
  * [#4393](https://github.com/pmd/pmd/issues/4393): \[java] MissingStaticMethodInNonInstantiatableClass false-positive for Lombok's @UtilityClass for classes with non-private fields

### API Changes

* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
  * {% jdoc go::lang.go.GoLanguageModule %}

### External Contributions
* [#4384](https://github.com/pmd/pmd/pull/4384): \[swift] Add more swift 5.x support (#unavailable mainly) - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4390](https://github.com/pmd/pmd/pull/4390): Add support for T-SQL using Antlr4 lexer - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4392](https://github.com/pmd/pmd/pull/4392): \[java] Fix #4393 MissingStaticMethodInNonInstantiatableClass: Fix false-positive for field-only class - [Dawid Ciok](https://github.com/dawiddc) (@dawiddc)

{% endtocmaker %}

