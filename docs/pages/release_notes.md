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

### 🌟 New and changed rules
#### New Rules

* The new JavaScript rule {%rule ecmascript/performance/AvoidConsoleStatements %} finds usages of `console.log` and
  similar function calls. Using these in production code might negatively impact performance.

### 🐛 Fixed Issues
* javascript-performance
  * [#5105](https://github.com/pmd/pmd/issues/5105): \[javascript] Prohibit any console methods

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

