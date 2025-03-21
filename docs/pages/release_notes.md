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
  * [#5587](https://github.com/pmd/pmd/issues/5587): \[java] Thread deadlock during PMD analysis in ParseLock.getFinalStatus
* java-bestpractices
  * [#5369](https://github.com/pmd/pmd/issues/5369): \[java] UnusedPrivateMethod false positives with lombok.val
* java-codestyle
  * [#5452](https://github.com/pmd/pmd/issues/5452): \[java] Suppression comment has no effect due to finding at wrong position in case of JavaDoc comment

### 🚨 API Changes

- {% jdoc !!java::lang.java.types.JTypeVar#withUpperbound(java::types.JTypeMirror) %} is deprecated. It was previously meant to be used
  internally and not needed anymore.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

