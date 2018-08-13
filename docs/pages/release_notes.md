---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.7.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.7.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rules

*   The new Java rule [`LinguisticNaming`](pmd_rules_java_codestyle.html#linguisticnaming) (`java-codestyle`)
    detects cases, when a method name indicates it returns a boolean (such as `isSmall()`) but it doesn't.
    Besides method names, the rule also checks field and variable names. It also checks, that getters return
    something but setters won't. The rule has several properties with which it can be customized.

*   The new PL/SQL rule [`ForLoopNaming`](pmd_rules_plsql_codestyle.html#forloopnaming) (`plsql-codestyle`)
    enforces a naming convention for "for loops". Both "cursor for loops" and "index for loops" are covered.
    The rule can be customized via patterns. By default, short variable names are reported.

### Fixed Issues

*   core
    *   [#1191](https://github.com/pmd/pmd/issues/1191): \[core] Test Framework: Sort violations by line/column
    *   [#1288](https://github.com/pmd/pmd/issues/1288): \[core] No supported build listeners found with Gradle
    *   [#1300](https://github.com/pmd/pmd/issues/1300): \[core] PMD stops processing file completely, if one rule in a rule chain fails
*   java-bestpractices
    *   [#1267](https://github.com/pmd/pmd/pull/1267): \[java] MissingOverrideRule: Avoid NoClassDefFoundError with incomplete classpath
*   java-codestyle
    *   [#1255](https://github.com/pmd/pmd/issues/1255): \[java] UnnecessaryFullyQualifiedName false positive: static method on shadowed implicitly imported class
    *   [#1258](https://github.com/pmd/pmd/issues/1285): \[java] False positive "UselessParentheses" for parentheses that contain assignment
*   java-errorprone
    *   [#1078](https://github.com/pmd/pmd/issues/1078): \[java] MissingSerialVersionUID rule does not seem to catch inherited classes
*   java-performance
    *   [#1291](https://github.com/pmd/pmd/issues/1291): \[java] InvalidSlf4jMessageFormat false positive: too many arguments with string concatenation operator
    *   [#1298](https://github.com/pmd/pmd/issues/1298): \[java] RedundantFieldInitializer - NumberFormatException with Long
*   jsp
    *   [#1274](https://github.com/pmd/pmd/issues/1274): \[jsp] Support EL in tag attributes
    *   [#1276](https://github.com/pmd/pmd/issues/1276): \[jsp] add support for jspf and tag extensions
*   plsql
    *   [#681](https://github.com/pmd/pmd/issues/681): \[plsql] Parse error with Cursor For Loop

### API Changes

### External Contributions

*   [#109](https://github.com/pmd/pmd/pull/109): \[java] Add two linguistics rules under naming - [Arda Aslan](https://github.com/ardaasln)
*   [#1254](https://github.com/pmd/pmd/pull/1254): \[ci] \[GSoC] Integrating the danger and pmdtester to travis CI - [BBG](https://github.com/djydewang)
*   [#1258](https://github.com/pmd/pmd/pull/1258): \[java] Use typeof in MissingSerialVersionUID - [krichter722](https://github.com/krichter722)
*   [#1264](https://github.com/pmd/pmd/pull/1264): \[cpp] Fix NullPointerException in CPPTokenizer:99 - [Rafael Cortês](https://github.com/mrfyda)
*   [#1277](https://github.com/pmd/pmd/pull/1277): \[jsp] #1276 add support for jspf and tag extensions - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1275](https://github.com/pmd/pmd/pull/1275): \[jsp] Issue #1274 - Support EL in tag attributes - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1278](https://github.com/pmd/pmd/pull/1278): \[ci] \[GSoC] Use pmdtester 1.0.0.pre.beta3 - [BBG](https://github.com/djydewang)
*   [#1289](https://github.com/pmd/pmd/pull/1289): \[java] UselessParentheses: Fix false positive with assignments - [cobratbq](https://github.com/cobratbq)
*   [#1290](https://github.com/pmd/pmd/pull/1290): \[docs] \[GSoC] Create the documentation about pmdtester - [BBG](https://github.com/djydewang)
