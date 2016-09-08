---
title: Release Notes 5.3.7
tags: [release_notes]
keywords: release notes, announcements, what's new, new features
last_updated: April 30, 2015
summary: "Version 5.3.7 of the PMD Open Source Project, released April 30, 2015."
sidebar: mydoc_sidebar
permalink: 2015-03-30-release-notes-5-3-7.html
folder: mydoc
---

## New Supported Languages

*   CPD supports now Swift (see [PR#33](https://github.com/adangel/pmd/pull/33)).

## Feature Request and Improvements

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).

## Modified Rules

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

## Pull Requests

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the ‘–files’ command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): Added support for Swift to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer
*   [#85](https://github.com/pmd/pmd/pull/85): #1340 UseStringBufferForStringAppends False Positive with Ternary Operator

## Bugfixes

*   java-basic/DoubleCheckedLocking:
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): False positives for DoubleCheckedLocking
*   java-codesize/TooManyMethods:
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): TooManyMethods counts inner class methods
*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-design/UseUtilityClass:
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): UseUtilityClass can’t correctly check functions with multiple annotations
*   java-imports/UnusedImports:
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): False Positve UnusedImports with javadoc @link
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   java-optimizations/UseStringBufferForStringAppends:
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): UseStringBufferForStringAppends False Positive with ternary operator
*   java-sunsecure/ArrayIsStoredDirectly:
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): False positive of MethodReturnsInternalArray
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): False positive of ArrayIsStoredDirectly
*   java-unnecessary/UnnecessaryFinalModifier:
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): UnnecessaryFinalModifier false positive on a @SafeVarargs method
*   java-unusedcode/UnusedFormalParameter:
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): UnusedFormalParameter should ignore overriding methods
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn’t handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): Error with type-bound lambda

## CLI Changes

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows to scan files, that are not using the standard file extension. If a directory is specified, the filename filter is still applied and only those files with the correct file extension of the language are scanned.

{% include links.html %}
