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
* apex
  * [#5388](https://github.com/pmd/pmd/issues/5388): \[apex] Parse error with time literal in SOQL query
* cli
  * [#5399](https://github.com/pmd/pmd/issues/5399): \[cli] Windows: PMD fails to start with special characters in path names
  * [#5401](https://github.com/pmd/pmd/issues/5401): \[cli] Windows: Console output doesn't use unicode
* java-bestpractices
  * [#4861](https://github.com/pmd/pmd/issues/4861): \[java] UnusedPrivateMethod - false positive with static methods in core JDK classes

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

