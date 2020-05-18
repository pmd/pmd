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

#### CPD now supports XML as well

Thanks to [Fernando Cosso](https://github.com/xnYi9wRezm) CPD can now find duplicates in XML files as well.
This is useful to find duplicated sections in XML files.

#### New Rules

*   The new Java Rule {% rule "java/bestpractices/LiteralsFirstInComparisons" %} (`java-bestpractices`)
    find String literals, that are used in comparisons and are not positioned first. Using the String literal
    as the receiver of e.g. `equals` helps to avoid NullPointerExceptions.
    
    This rule is replacing the two old rules {% rule "java/bestpractices/PositionLiteralsFirstInComparisons" %}
    and {% rule "java/bestpractices/PositionLiteralsFirstInCaseInsensitiveComparisons" %} and extends the check
    for the methods `compareTo`, `compareToIgnoreCase` and `contentEquals` in addition to `equals` and
    `equalsIgnoreCase`.
    
    Note: This rule also replaces the two mentioned rules in Java's quickstart ruleset.

### Fixed Issues

*   apex-bestpractices
    *   [#2468](https://github.com/pmd/pmd/issues/2468): \[apex] Unused Local Variable fails on blocks
*   core
    *   [#2484](https://github.com/pmd/pmd/issues/2484): \[core] Update maven-enforcer-plugin to require Java 118
*   java
    *   [#2472](https://github.com/pmd/pmd/issues/2472): \[java] JavaCharStream throws an Error on invalid escape
*   java-bestpractices
    *   [#2288](https://github.com/pmd/pmd/issues/2288): \[java] JUnitTestsShouldIncludeAssert: Add support for Hamcrest MatcherAssert.assertThat
*   java-errorprone
    *   [#2477](https://github.com/pmd/pmd/issues/2477): \[java] JUnitSpelling false-positive for JUnit5/4 tests
*   swift
    *   [#2473](https://github.com/pmd/pmd/issues/2473): \[swift] Swift 5 (up to 5.2) support for CPD

### API Changes

#### Deprecated APIs

*   {% jdoc !ca!core::lang.BaseLanguageModule#addVersion(String, LanguageVersionHandler, boolean) %}
*   Some members of {% jdoc core::lang.ast.TokenMgrError %}, in particular, a new constructor is available
    that should be preferred to the old ones
*   {% jdoc core::lang.antlr.AntlrTokenManager.ANTLRSyntaxError %}

#### Experimental APIs

**Note:** Experimental APIs are identified with the annotation {% jdoc core::annotation.Experimental %},
see its javadoc for details

* The experimental methods in {% jdoc !ca!core::lang.BaseLanguageModule %} have been replaced by a
definitive API.

### External Contributions

*   [#2446](https://github.com/pmd/pmd/pull/2446): \[core] Update maven-compiler-plugin to 3.8.1 - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2448](https://github.com/pmd/pmd/pull/2448): \[java] Operator Wrap check - [Harsh Kukreja](https://github.com/harsh-kukreja)
*   [#2449](https://github.com/pmd/pmd/pull/2449): \[plsql] Additional info in SqlStatement, FormalParameter and FetchStatement - [Grzegorz Sudolski](https://github.com/zgrzyt93)
*   [#2452](https://github.com/pmd/pmd/pull/2452): \[doc] Fix "Making Rulesets" doc sample code indentation - [Artur Dryomov](https://github.com/arturdryomov)
*   [#2457](https://github.com/pmd/pmd/pull/2457): \[xml] Adding XML to CPD supported languages - [Fernando Cosso](https://github.com/xnYi9wRezm)
*   [#2469](https://github.com/pmd/pmd/pull/2469): \[apex] fix false positive unused variable if only a method is called - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2475](https://github.com/pmd/pmd/pull/2475): \[swift] Swift 4.2-5.2 support - [kenji21](https://github.com/kenji21)
*   [#2478](https://github.com/pmd/pmd/pull/2478): \[java] New rule: LiteralsFirstInComparisons - [John-Teng](https://github.com/John-Teng)
*   [#2479](https://github.com/pmd/pmd/pull/2479): \[java] False positive with Hamcrest's assertThat - [andreoss](https://github.com/andreoss)
*   [#2481](https://github.com/pmd/pmd/pull/2481): \[java] Fix JUnitSpellingRule false positive - [Artem Krosheninnikov](https://github.com/KroArtem)

{% endtocmaker %}

