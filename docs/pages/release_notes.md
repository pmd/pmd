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

### 🌟 New and changed rules

#### New Rules

* The new Apex rule {%rule apex/performance/AvoidNonRestrictiveQueries %} finds SOQL and SOSL queries without a where
  or limit statement. This can quickly cause governor limit exceptions.

#### Changed rules
* {%rule apex/codestyle/ClassNamingConventions %}: Two new properties to configure different patterns
  for inner classes and interfaces: `innerClassPattern` and `innerInterfacePattern`.

#### Renamed rules
* {%rule ecmascript/errorprone/InaccurateNumericLiteral %} has been renamed from `InnaccurateNumericLiteral`.
  The old rule name still works but is deprecated.

### 🐛 Fixed Issues
* apex
  * [#5094](https://github.com/pmd/pmd/issues/5094): \[apex] "No adapter exists for type" error message printed to stdout instead of stderr
* apex-bestpractices
  * [#5095](https://github.com/pmd/pmd/issues/5095): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue false negative due to casing (regression in PMD 7)
* apex-codestyle
  * [#4800](https://github.com/pmd/pmd/issues/4800): \[apex] ClassNamingConvention: Support naming convention for *inner* classes
* apex-performance
  * [#635](https://github.com/pmd/pmd/issues/635): \[apex] New Rule: Avoid soql/sosl queries without a where clause or limit statement
* java-bestpractices
  * [#5106](https://github.com/pmd/pmd/issues/5106): \[java] AccessorClassGeneration: Node was null for default constructor
  * [#5110](https://github.com/pmd/pmd/issues/5110): \[java] UnusedPrivateMethod for method referenced by lombok.Builder.ObtainVia
  * [#5117](https://github.com/pmd/pmd/issues/5117): \[java] UnusedPrivateMethod for methods annotated with jakarta.annotation.PostConstruct or PreDestroy
* java-errorprone
  * [#1488](https://github.com/pmd/pmd/issues/1488): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with Lombok Builder on Constructor
* javascript-errorprone
  * [#2367](https://github.com/pmd/pmd/issues/2367): \[javascript] InnaccurateNumericLiteral is misspelled
  * [#4716](https://github.com/pmd/pmd/issues/4716): \[javascript] InaccurateNumericLiteral with number 259200000
* plsql
  * [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage
  * [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO
  * [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions
  * [#5133](https://github.com/pmd/pmd/issues/5133): \[plsql] AssertionError: Root of the tree should implement RootNode for a PL/SQL type declaration
* cli
  * [#5120](https://github.com/pmd/pmd/issues/5120): \[cli] Can't start designer under Windows
* core
  * [#5091](https://github.com/pmd/pmd/issues/5091): \[core] PMD CPD v7.3.0 gives deprecation warning for skipLexicalErrors even when not used

### 🚨 API Changes

* javascript
  * The old rule name `InnaccurateNumericLiteral` has been deprecated. Use the new name
    {%rule ecmascript/errorprone/InaccurateNumericLiteral %} instead.

### ✨ External Contributions
* [#5048](https://github.com/pmd/pmd/pull/5048): \[apex] Added Inner Classes to Apex Class Naming Conventions Rule - [Justin Stroud](https://github.com/justinstroudbah) (@justinstroudbah / @sgnl-labs)
* [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5107](https://github.com/pmd/pmd/pull/5107): \[doc] Update maven.md - Typo fixed for maven target - [karthikaiyasamy](https://github.com/karthikaiyasamy) (@karthikaiyasamy)
* [#5109](https://github.com/pmd/pmd/pull/5109): \[java] Exclude constructor with lombok.Builder for MissingStaticMethodInNonInstantiatableClass - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5111](https://github.com/pmd/pmd/pull/5111): \[java] Fix UnusedPrivateMethod for @<!-- -->lombok.Builder.ObtainVia - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5118](https://github.com/pmd/pmd/pull/5118): \[java] FP for UnusedPrivateMethod with Jakarta @<!-- -->PostConstruct/PreDestroy annotations - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)
* [#5121](https://github.com/pmd/pmd/pull/5121): \[plsql] Fixed issue with missing optional table alias in MERGE usage - [Arjen Duursma](https://github.com/duursma) (@duursma)

{% endtocmaker %}

