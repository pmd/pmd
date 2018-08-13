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
    *   [Drawing a line between private and public API](#drawing-a-line-between-private-and-public-api)
    *   [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Drawing a line between private and public API

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.


In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

##### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

##### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

##### `@Beta`

APIs marked with the `@Beta` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. If your code is a library
itself (i.e. it is used on the CLASSPATH of users outside your own control), you should not
use beta APIs, unless you repackage them (e.g. using ProGuard, shading, etc).

##### `@Experimental`


APIs marked with the `@Experimental` annotation at the class or method level will almost certainly
change. They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

##### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.


##### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.


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
*   java-bestpractices
    *   [#1267](https://github.com/pmd/pmd/pull/1267): \[java] MissingOverrideRule: Avoid NoClassDefFoundError with incomplete classpath
*   java-codestyle
    *   [#1255](https://github.com/pmd/pmd/issues/1255): \[java] UnnecessaryFullyQualifiedName false positive: static method on shadowed implicitly imported class
    *   [#1258](https://github.com/pmd/pmd/issues/1285): \[java] False positive "UselessParentheses" for parentheses that contain assignment
*   java-errorprone
    *   [#1078](https://github.com/pmd/pmd/issues/1078): \[java] MissingSerialVersionUID rule does not seem to catch inherited classes
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
