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
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

### Fixed Issues

*   java
    *   [#1077](https://github.com/pmd/pmd/issues/1077): \[java] Analyzing enum with lambda passed in constructor fails with "The enclosing scope must exist."
*   java-bestpractices
    *   [#1063](https://github.com/pmd/pmd/issues/1063): \[java] MissingOverride is triggered in illegal places
*   java-codestyle
    *   [#1064](https://github.com/pmd/pmd/issues/1064): \[java] ClassNamingConventions suggests to add Util suffix for simple exception wrappers
    *   [#1065](https://github.com/pmd/pmd/issues/1065): \[java] ClassNamingConventions shouldn't prohibit numbers in class names
    *   [#1067](https://github.com/pmd/pmd/issues/1067): \[java] [6.3.0] PrematureDeclaration false-positive
*   java-design
    *   [#824](https://github.com/pmd/pmd/issues/824): \[java] UseUtilityClass false positive when extending
    *   [#1097](https://github.com/pmd/pmd/pull/1097): \[java] False negative in AvoidThrowingRawExceptionTypes
*   java-performance
    *   [#1098](https://github.com/pmd/pmd/pull/1098): \[java] Simplify LongInstantiation, IntegerInstantiation, ByteInstantiation, and ShortInstantiation using type resolution

### API Changes

### External Contributions
