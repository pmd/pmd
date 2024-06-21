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
* core
  * [#4992](https://github.com/pmd/pmd/pull/4992): \[core] CPD: Include processing errors in XML report
* apex
  * [#5053](https://github.com/pmd/pmd/issues/5053): \[apex] CPD fails to parse string literals with escaped characters
* java-bestpractices
  * [#5047](https://github.com/pmd/pmd/issues/5047): \[java] UnusedPrivateMethod FP for Generics & Overloads
* plsql
  * [#1934](https://github.com/pmd/pmd/issues/1934): \[plsql] ParseException with MERGE statement in anonymous block
  * [#2779](https://github.com/pmd/pmd/issues/2779): \[plsql] Error while parsing statement with (Oracle) DML Error Logging

### 🚨 API Changes

#### CPD Report Format XML

The CPD XML report will now also contain processing errors (if CPD is called with `--skip-lexical-errors`).

See [Report formats for CPD](pmd_userdocs_cpd_report_formats.html#xml) for an example.

### ✨ External Contributions

{% endtocmaker %}

