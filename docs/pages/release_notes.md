---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.4.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.4.0.

This is a bug fixing release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Java 10 Support](#java-10-support)
*   [Fixed Issues](#fixed-issues)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Java 10 Support

PMD is now able to understand local-variable type inference as introduced by Java 10.
Simple type resolution features are available, e.g. the type of the variable `s` is inferred
correctly as `String`:

    var s = "Java 10";


### Fixed Issues

*   java
    *   [#743](https://github.com/pmd/pmd/issues/743): \[java] Prepare for Java 10
    *   [#1077](https://github.com/pmd/pmd/issues/1077): \[java] Analyzing enum with lambda passed in constructor fails with "The enclosing scope must exist."
*   java-bestpractices
    *   [#1063](https://github.com/pmd/pmd/issues/1063): \[java] MissingOverride is triggered in illegal places
*   java-codestyle
    *   [#1064](https://github.com/pmd/pmd/issues/1064): \[java] ClassNamingConventions suggests to add Util suffix for simple exception wrappers
    *   [#1065](https://github.com/pmd/pmd/issues/1065): \[java] ClassNamingConventions shouldn't prohibit numbers in class names
    *   [#1067](https://github.com/pmd/pmd/issues/1067): \[java] [6.3.0] PrematureDeclaration false-positive

### API Changes

### External Contributions
