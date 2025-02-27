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

* The new Apex rule {% rule apex/errorprone/AvoidStatefulDatabaseResult %} detects `Database.Stateful` implementations
  that store database results in instance variables. This can cause serialization issues between successive batch
  iterations.

### 🐛 Fixed Issues
* apex-errorprone
  * [#5305](https://github.com/pmd/pmd/issues/5305): \[apex] New Rule: Avoid Stateful Database Results

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5425](https://github.com/pmd/pmd/pull/5425): \[apex] New Rule: Avoid Stateful Database Results - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

