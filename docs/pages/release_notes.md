---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* java-bestpractices
  * [#4435](https://github.com/pmd/pmd/issues/4435): \[java] \[7.0-rc1] UnusedAssignment for used field
* java-codestyle
  * [#4881](https://github.com/pmd/pmd/issues/4881): \[java] ClassNamingConventions: interfaces are identified as abstract classes (regression in 7.0.0)
* java-design
  * [#4873](https://github.com/pmd/pmd/issues/4873): \[java] AvoidCatchingGenericException: Can no longer suppress on the exception itself
* java-performance
  * [#4874](https://github.com/pmd/pmd/issues/4874): \[java] StringInstantiation: False-positive when using `new String(charArray)`

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

