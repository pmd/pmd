---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.6.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.6.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Java 11 Support](#java-11-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Java 11 Support

PMD is now able to parse the local-variable declaration syntax `var xxx`, that has been
extended for lambda parameters with Java 11 via
[JEP 323: Local-Variable Syntax for Lambda Parameters](http://openjdk.java.net/jeps/323).

#### New Rules

*   The new Java rule [`LocalVariableNamingConventions`](pmd_rules_java_codestyle.html#localvariablenamingconventions)
    (`java-codestyle`) detects local variable names that don't comply to a given convention. It defaults to standard
    Java convention of using camelCase, but can be configured. Special cases can be configured for final variables
    and caught exceptions' names.

*   The new Java rule [`FormalParameterNamingConventions`](pmd_rules_java_codestyle.html#formalparameternamingconventions)
    (`java-codestyle`) detects formal parameter names that don't comply to a given convention. It defaults to
    standard Java convention of using camelCase, but can be configured. Special cases can be configured for final
    parameters and lambda parameters (considering whether they are explicitly typed or not).

#### Modified Rules

*   The Java rules [`AccessorClassGeneration'](pmd_rules_java_bestpracices.html#accessorclassgeneration) and
    [`AccessorMethodGeneration`](pmd_rules_java_bestpracices.html#accessormethodgeneration) (both in category
    `java-bestpractices`) have been modified to be only valid up until Java 10. Java 11 adds support for
    [JEP 181: Nest-Based Access Control](http://openjdk.java.net/jeps/181) which avoids the generation of
    accessor classes / methods altogether.

### Fixed Issues

*   doc
    *   [#1215](https://github.com/pmd/pmd/issues/1215): \[doc] TOC links don't work?
*   java-codestyle
    *   [#1211](https://github.com/pmd/pmd/issues/1211): \[java] CommentDefaultAccessModifier false positive with nested interfaces (regression from 6.4.0)
    *   [#1216](https://github.com/pmd/pmd/issues/1216): \[java] UnnecessaryFullyQualifiedName false positive for the same name method
*   java-design
    *   [#1217](https://github.com/pmd/pmd/issues/1217): \[java] CyclomaticComplexityRule counts ?-operator twice
*   plsql
    *   [#980](https://github.com/pmd/pmd/issues/980): \[plsql] ParseException for CREATE TABLE
    *   [#981](https://github.com/pmd/pmd/issues/981): \[plsql] ParseException when parsing VIEW
    *   [#1047](https://github.com/pmd/pmd/issues/1047): \[plsql] ParseException when parsing EXECUTE IMMEDIATE
*   ui
    *   [#1233](https://github.com/pmd/pmd/issues/1233): \[ui] XPath autocomplete arrows on first and last items

### API Changes

*   The `findDescendantsOfType` methods in `net.sourceforge.pmd.lang.ast.AbstractNode` no longer search for
    exact type matches, but will match subclasses, too. That means, it's now possible to look for abstract node
    types such as `AbstractJavaTypeNode` and not only for it's concrete subtypes.

### External Contributions

* [#1182](https://github.com/pmd/pmd/pull/1182): \[ui] XPath AutoComplete - [Akshat Bahety](https://github.com/akshatbahety)
* [#1231](https://github.com/pmd/pmd/pull/1231): \[doc] Minor typo fix in installation.md - [Ashish Rana](https://github.com/ashishrana160796)
* [#1250](https://github.com/pmd/pmd/pull/1250): \[ci] \[GSoC] Upload baseline of pmdtester automatically - [BBG](https://github.com/djydewang)
