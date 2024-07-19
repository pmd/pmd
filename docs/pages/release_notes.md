---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* java-bestpractices
  * [#5117](https://github.com/pmd/pmd/issues/5117): \[java] UnusedPrivateMethod for methods annotated with jakarta.annotation.PostConstruct or PreDestroy
* plsql
  * [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage
  * [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO
  * [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions

### 🚨 API Changes

### ✨ External Contributions
* [#5086](https://github.com/pmd/pmd/pull/5086): \[plsql] Fixed issue with missing optional table alias in MERGE usage - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5087](https://github.com/pmd/pmd/pull/5087): \[plsql] Add support for SQL_MACRO - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5088](https://github.com/pmd/pmd/pull/5088): \[plsql] Add support for 'DEFAULT' clause on the arguments of some oracle functions - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5107](https://github.com/pmd/pmd/pull/5107): \[doc] Update maven.md - Typo fixed for maven target - [karthikaiyasamy](https://github.com/karthikaiyasamy) (@karthikaiyasamy)
* [#5118](https://github.com/pmd/pmd/pull/5118): \[java] FP for UnusedPrivateMethod with Jakarta @<!-- -->PostConstruct/PreDestroy annotations - [Krzysztof Debski](https://github.com/kdebski85) (@kdebski85)

{% endtocmaker %}

