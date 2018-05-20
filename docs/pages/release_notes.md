---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.4.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.4.0.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule [JUnit4TestShouldUseTestAnnotation](pmd_rules_java_bestpractices.html#junit4testshouldusetestannotation) (`java-bestpractices`)
    has a new parameter "testClassPattern". It is used to distinguish test classes from other classes and
    avoid false positives. By default, any class, that has "Test" in its name, is considered a test class.

### Fixed Issues

*   java
    *   [#1077](https://github.com/pmd/pmd/issues/1077): \[java] Analyzing enum with lambda passed in constructor fails with "The enclosing scope must exist."
*   java-bestpractices
    *   [#527](https://github.com/pmd/pmd/issues/572): \[java] False Alarm of JUnit4TestShouldUseTestAnnotation on Predicates
    *   [#1063](https://github.com/pmd/pmd/issues/1063): \[java] MissingOverride is triggered in illegal places
*   java-codestyle
    *   [#1064](https://github.com/pmd/pmd/issues/1064): \[java] ClassNamingConventions suggests to add Util suffix for simple exception wrappers
    *   [#1065](https://github.com/pmd/pmd/issues/1065): \[java] ClassNamingConventions shouldn't prohibit numbers in class names
    *   [#1067](https://github.com/pmd/pmd/issues/1067): \[java] [6.3.0] PrematureDeclaration false-positive
*   java-design
    *   [#824](https://github.com/pmd/pmd/issues/824): \[java] UseUtilityClass false positive when extending
    *   [#1021](https://github.com/pmd/pmd/issues/1021): \[java] False positive for `DoNotExtendJavaLangError`
    *   [#1097](https://github.com/pmd/pmd/pull/1097): \[java] False negative in AvoidThrowingRawExceptionTypes
*   java-performance
    *   [#1051](https://github.com/pmd/pmd/issues/1051): \[java] ConsecutiveAppendsShouldReuse false-negative
    *   [#1098](https://github.com/pmd/pmd/pull/1098): \[java] Simplify LongInstantiation, IntegerInstantiation, ByteInstantiation, and ShortInstantiation using type resolution
*   doc
    *   [#999](https://github.com/pmd/pmd/issues/999): \[doc] Add a header before the XPath expression in rules
    *   [#1082](https://github.com/pmd/pmd/issues/1082): \[doc] Multifile analysis doc is invalid
*   vf-security
    *   [#1100](https://github.com/pmd/pmd/issues/1100): \[vf] URLENCODE is ignored as valid escape method

### API Changes

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.



### External Contributions

*   [#1101](https://github.com/pmd/pmd/pull/1101): \[java] Fixes false positive for `DoNotExtendJavaLangError`  - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1106](https://github.com/pmd/pmd/pull/1106): \[vf] URLENCODE is ignored as valid escape method - [Robert Sösemann](https://github.com/rsoesemann)
