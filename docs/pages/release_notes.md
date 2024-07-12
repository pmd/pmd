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

#### ✨ New Rules

* The new Apex rule {% apex/performance/AvoidNonRestrictiveQueries %} finds SOQL and SOSL queries without a where
  or limit statement. This can quickly cause governor limit exceptions.

### 🐛 Fixed Issues
* apex
  * [#635](https://github.com/pmd/pmd/issues/635): \[apex] New Rule: Avoid soql/sosl queries without a where clause or limit statement
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

{% endtocmaker %}

