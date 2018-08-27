---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

# {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Modified Rules

*   The Java rule {% rule java/bestpractices/OneDeclarationPerLine %} (`java-bestpractices`) has been revamped to
    consider not only local variable declarations, but field declarations too.

#### New Rules

*   The new Java rule {% rule java/codestyle/LinguisticNaming %} (`java-codestyle`)
    detects cases, when a method name indicates it returns a boolean (such as `isSmall()`) but it doesn't.
    Besides method names, the rule also checks field and variable names. It also checks, that getters return
    something but setters won't. The rule has several properties with which it can be customized.

*   The new PL/SQL rule {% rule plsql/codestyle/ForLoopNaming %} (`plsql-codestyle`)
    enforces a naming convention for "for loops". Both "cursor for loops" and "index for loops" are covered.
    The rule can be customized via patterns. By default, short variable names are reported.

*   The new Java rule {% rule java/codestyle/FieldNamingConventions %} (`java-codestyle`)
    detects field names that don't comply to a given convention. It defaults to standard Java convention of using camelCase,
    but can be configured with ease for e.g. constants or static fields.

*   The new Apex rule {% rule apex/codestyle/OneDeclarationPerLine %} (`apex-codestyle`) enforces declaring a
    single field / variable per line; or per statement if the `strictMode` property is set.
    It's an Apex equivalent of the already existing Java rule of the same name.

### Fixed Issues

*   core
    *   [#1191](https://github.com/pmd/pmd/issues/1191): \[core] Test Framework: Sort violations by line/column
    *   [#1283](https://github.com/pmd/pmd/issues/1283): \[core] Deprecate ReportTree
    *   [#1288](https://github.com/pmd/pmd/issues/1288): \[core] No supported build listeners found with Gradle
    *   [#1300](https://github.com/pmd/pmd/issues/1300): \[core] PMD stops processing file completely, if one rule in a rule chain fails
*   java-bestpractices
    *   [#940](https://github.com/pmd/pmd/issues/940): \[java] JUnit 4 false positives for JUnit 5 tests
    *   [#1267](https://github.com/pmd/pmd/pull/1267): \[java] MissingOverrideRule: Avoid NoClassDefFoundError with incomplete classpath
*   java-codestyle
    *   [#1255](https://github.com/pmd/pmd/issues/1255): \[java] UnnecessaryFullyQualifiedName false positive: static method on shadowed implicitly imported class
    *   [#1258](https://github.com/pmd/pmd/issues/1285): \[java] False positive "UselessParentheses" for parentheses that contain assignment
*   java-errorprone
    *   [#1078](https://github.com/pmd/pmd/issues/1078): \[java] MissingSerialVersionUID rule does not seem to catch inherited classes
*   java-performance
    *   [#1298](https://github.com/pmd/pmd/issues/1298): \[java] RedundantFieldInitializer - NumberFormatException with Long
*   jsp
    *   [#1274](https://github.com/pmd/pmd/issues/1274): \[jsp] Support EL in tag attributes
    *   [#1276](https://github.com/pmd/pmd/issues/1276): \[jsp] add support for jspf and tag extensions
*   plsql
    *   [#681](https://github.com/pmd/pmd/issues/681): \[plsql] Parse error with Cursor For Loop

### API Changes

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

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
*   [#1256](https://github.com/pmd/pmd/pull/1256): \[java] #940 Avoid JUnit 4 false positives for JUnit 5 tests - [Alex Shesterov](https://github.com/vovkss)
*   [#1315](https://github.com/pmd/pmd/pull/1315): \[apex] Add OneDeclarationPerStatement rule - [Jeff Hube](https://github.com/jeffhube) 

{% endtocmaker %}
