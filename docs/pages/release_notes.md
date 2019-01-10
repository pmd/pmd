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

#### New Rules

*   The new Java rule {% rule "java/multithreading/UnsynchronizedStaticFormatter" %} (`java-multithreading`) detects
    unsynchronized usages of static `java.text.Format` instances. This rule is a more generic replacement of the
    rule {% rule "java/multithreading/UnsynchronizedStaticDateFormatter" %} which focused just on `DateFormat`.

*   The new Java rule {% rule "java/bestpractices/ForLoopVariableCount" %} (`java-bestpractices`) checks for
    the number of control variables in a for-loop. Having a lot of control variables makes it harder to understand
    what the loop does. The maximum allowed number of variables is by default 1 and can be configured by a
    property.

*   The new Java rule {% rule "java/bestpractices/AvoidReassigningLoopVariables" %} (`java-bestpractices`) searches
    for loop variables that are reassigned. Changing the loop variables additionally to the loop itself can lead to
    hard-to-find bugs.

*   The new Java rule {% rule "java/codestyle/UseDiamondOperator" %} (`java-codestyle`) looks for constructor
    calls with explicit type parameters. Since Java 1.7, these type parameters are not necessary anymore, as they
    can be inferred now.

#### Modified Rules

*   The Java rule {% rule "java/codestyle/LocalVariableCouldBeFinal" %} (`java-codestyle`) has a new
    property `ignoreForEachDecl`, which is by default disabled. The new property allows for ignoring
    non-final loop variables in a for-each statement.

#### Deprecated Rules

*   The Java rule {% rule "java/multithreading/UnsynchronizedStaticDateFormatter" %} has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general
    {% rule "java/multithreading/UnsynchronizedStaticFormatter" %}.

### Fixed Issues

*   apex
    *   [#1542](https://github.com/pmd/pmd/pull/1542): \[apex] Include the documentation category
*   java
    * [#1556](https://github.com/pmd/pmd/issues/1556): \[java] Default methods should not be considered abstract
*   java-bestpractices
    *   [#658](https://github.com/pmd/pmd/issues/658): \[java] OneDeclarationPerLine: False positive for loops
    *   [#1518](https://github.com/pmd/pmd/issues/1518): \[java] New rule: AvoidReassigningLoopVariable
    *   [#1519](https://github.com/pmd/pmd/issues/1519): \[java] New rule: ForLoopVariableCount
*   java-codestyle
    *   [#1513](https://github.com/pmd/pmd/issues/1513): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop
    *   [#1517](https://github.com/pmd/pmd/issues/1517): \[java] New Rule: UseDiamondOperator
*   java-errorprone
    *   [#1035](https://github.com/pmd/pmd/issues/1035): \[java] ReturnFromFinallyBlock: False positive on lambda expression in finally block
    *   [#1549](https://github.com/pmd/pmd/issues/1549): \[java] NPE in PMD 6.8.0 InvalidSlf4jMessageFormat
*   java-multithreading
    *   [#1533](https://github.com/pmd/pmd/issues/1533): \[java] New rule: UnsynchronizedStaticFormatter
*   plsql
    *   [#1507](https://github.com/pmd/pmd/issues/1507): \[plsql] Parse Exception when using '||' operator in where clause
    *   [#1508](https://github.com/pmd/pmd/issues/1508): \[plsql] Parse Exception when using SELECT COUNT(\*)
    *   [#1509](https://github.com/pmd/pmd/issues/1509): \[plsql] Parse Exception with OUTER/INNER Joins
    *   [#1511](https://github.com/pmd/pmd/issues/1511): \[plsql] Parse Exception with IS NOT NULL

### API Changes

### External Contributions

*   [#1503](https://github.com/pmd/pmd/pull/1503): \[java] Fix for ReturnFromFinallyBlock false-positives - [RishabhDeep Singh](https://github.com/rishabhdeepsingh)
*   [#1514](https://github.com/pmd/pmd/pull/1514): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1516](https://github.com/pmd/pmd/pull/1516): \[java] OneDeclarationPerLine: Don't report multiple variables in a for statement. - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1520](https://github.com/pmd/pmd/pull/1520): \[java] New rule: ForLoopVariableCount: check the number of control variables in a for loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1521](https://github.com/pmd/pmd/pull/1521): \[java] Upgrade to ASM7 for JDK 11 support - [Mark Pritchard](https://github.com/markpritchard)
*   [#1530](https://github.com/pmd/pmd/pull/1530): \[java] New rule: AvoidReassigningLoopVariables - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1534](https://github.com/pmd/pmd/pull/1534): \[java] This is the change regarding the usediamondoperator #1517 - [hemanshu070](https://github.com/hemanshu070)
*   [#1545](https://github.com/pmd/pmd/pull/1545): \[doc] fixing dead links + tool to check for dead links automatically - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1551](https://github.com/pmd/pmd/pull/1551): \[java] InvalidSlf4jMessageFormatRule should not throw NPE for enums - [Robbie Martinus](https://github.com/rmartinus)

{% endtocmaker %}

