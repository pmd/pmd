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
* java
  * [#5505](https://github.com/pmd/pmd/issues/5505): \[java] java.lang.StackOverflowError while executing a PmdRunnable
  * [#5442](https://github.com/pmd/pmd/issues/5442): \[java] StackOverflowError with recursive generic types
* java-bestpractices
  * [#5504](https://github.com/pmd/pmd/issues/5504): \[java] UnusedAssignment false-positive in for-loop with continue
* java-codestyle
  * [#5523](https://github.com/pmd/pmd/issues/5523): \[java] UnnecessaryCast false-positive for integer operations in floating-point context

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5503](https://github.com/pmd/pmd/pull/5503): \[java] AvoidSynchronizedAtMethodLevel: Fixed error in code example - [Balazs Glatz](https://github.com/gbq6) (@gbq6)
* [#5538](https://github.com/pmd/pmd/pull/5538): Add project icon for IntelliJ IDEA - [Vincent Potucek](https://github.com/punkratz312) (@punkratz312)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

