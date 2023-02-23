---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Java 20 Support

This release of PMD brings support for Java 20. There are no new standard language features.

PMD supports [JEP 433: Pattern Matching for switch (Fourth Preview)](https://openjdk.org/jeps/433) and
[JEP 432: Record Patterns (Second Preview)](https://openjdk.org/jeps/432) as preview language features.

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `20-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd --use-version java-20-preview ...

#### T-SQL support
Thanks to the contribution from [Paul Guyot](https://github.com/pguyot) PMD now has CPD support
for T-SQL (Transact-SQL).

Being based on a proper Antlr grammar, CPD can:

* ignore comments
* honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Fixed Issues
* core
  * [#4395](https://github.com/pmd/pmd/issues/4395): \[core] Support environment variable CLASSPATH with pmd.bat under Windows
* java
  * [#4333](https://github.com/pmd/pmd/issues/4333): \[java] Support JDK 20
* java-errorprone
  * [#4393](https://github.com/pmd/pmd/issues/4393): \[java] MissingStaticMethodInNonInstantiatableClass false-positive for Lombok's @UtilityClass for classes with non-private fields

### API Changes

#### Java
* Support for Java 18 preview language features have been removed. The version "18-preview" is no longer available.
* The experimental class `net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern` has been removed.

#### Go
* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
  * {% jdoc go::lang.go.GoLanguageModule %}

### External Contributions
* [#4384](https://github.com/pmd/pmd/pull/4384): \[swift] Add more swift 5.x support (#unavailable mainly) - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4390](https://github.com/pmd/pmd/pull/4390): Add support for T-SQL using Antlr4 lexer - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4392](https://github.com/pmd/pmd/pull/4392): \[java] Fix #4393 MissingStaticMethodInNonInstantiatableClass: Fix false-positive for field-only class - [Dawid Ciok](https://github.com/dawiddc) (@dawiddc)

{% endtocmaker %}

