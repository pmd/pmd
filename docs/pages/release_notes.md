---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### ✨ New Rules

* The new Apex rule {% rule apex/errorprone/AvoidStatefulDatabaseResult %} detects `Database.Stateful` implementations
  that store database results in instance variables. This can cause serialization issues between successive batch
  iterations.

#### Signed Releases

We now not only sign the maven artifacts, but also our binary distribution files that you can
download from [GitHub Releases](https://github.com/pmd/pmd/releases).
See the page [Signed Releases](pmd_userdocs_signed_releases.html) in our documentation for how to verify the files.

### 🐛 Fixed Issues
* apex-errorprone
  * [#5305](https://github.com/pmd/pmd/issues/5305): \[apex] New Rule: Avoid Stateful Database Results
* java
  * [#5442](https://github.com/pmd/pmd/issues/5442): \[java] StackOverflowError with recursive generic types
  * [#5493](https://github.com/pmd/pmd/issues/5493): \[java] IllegalArgumentException: <?> cannot be a wildcard bound
  * [#5505](https://github.com/pmd/pmd/issues/5505): \[java] java.lang.StackOverflowError while executing a PmdRunnable
* java-bestpractices
  * [#3359](https://github.com/pmd/pmd/issues/3359): \[java] UnusedPrivateMethod does not recognize Lombok @<!-- -->EqualsAndHashCode.Include annotation
  * [#5486](https://github.com/pmd/pmd/issues/5486): \[java] UnusedPrivateMethod detected when class is referenced in another class
  * [#5504](https://github.com/pmd/pmd/issues/5504): \[java] UnusedAssignment false-positive in for-loop with continue
* java-codestyle
  * [#4822](https://github.com/pmd/pmd/issues/4822): \[java] UnnecessaryCast false-positive for raw types
  * [#5073](https://github.com/pmd/pmd/issues/5073): \[java] UnnecessaryCast false-positive for cast in return position of lambda
  * [#5440](https://github.com/pmd/pmd/issues/5440): \[java] UnnecessaryCast reported in stream chain map() call that casts to more generic interface
  * [#5523](https://github.com/pmd/pmd/issues/5523): \[java] UnnecessaryCast false-positive for integer operations in floating-point context
  * [#5541](https://github.com/pmd/pmd/pull/5541): \[java] Fix IdenticalCatchBranch reporting branches that call different overloads
* java-design
  * [#5018](https://github.com/pmd/pmd/issues/5018): \[java] FinalFieldCouldBeStatic false-positive for access of super class field
* plsql
  * [#5522](https://github.com/pmd/pmd/issues/5522): \[plsql] Parse error for operator in TRIM function call

### 🚨 API Changes

#### Deprecations
* java
  * The method {%jdoc !ca!java::lang.java.types.TypeOps#isContextDependent(java::lang.java.types.JMethodSig) %} is deprecated for removal.
    Use {%jdoc !a!java::lang.java.types.TypeOps#isContextDependent(java::lang.java.symbols.JExecutableSymbol) %} instead which
    is more flexible.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5425](https://github.com/pmd/pmd/pull/5425): \[apex] New Rule: Avoid Stateful Database Results - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5503](https://github.com/pmd/pmd/pull/5503): \[java] AvoidSynchronizedAtMethodLevel: Fixed error in code example - [Balazs Glatz](https://github.com/gbq6) (@gbq6)
* [#5538](https://github.com/pmd/pmd/pull/5538): Add project icon for IntelliJ IDEA - [Vincent Potucek](https://github.com/punkratz312) (@punkratz312)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

