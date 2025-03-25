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

* The new Java rule {% rule java/bestpractices/ImplicitFunctionalInterface %} reports functional interfaces that were
  not explicitly declared as such with the annotation `@FunctionalInterface`. If an interface is accidentally a functional
  interface, then it should bear a `@SuppressWarnings("PMD.ImplicitFunctionalInterface")`
  annotation to make this clear.

### 🐛 Fixed Issues
* apex
  * [#5567](https://github.com/pmd/pmd/issues/5567): \[apex] Provide type information for CastExpression
* java
  * [#5587](https://github.com/pmd/pmd/issues/5587): \[java] Thread deadlock during PMD analysis in ParseLock.getFinalStatus
* java-bestpractices
  * [#2849](https://github.com/pmd/pmd/issues/2849): \[java] New Rule: ImplicitFunctionalInterface
  * [#5369](https://github.com/pmd/pmd/issues/5369): \[java] UnusedPrivateMethod false positives with lombok.val
* plsql
  * [#5521](https://github.com/pmd/pmd/issues/5521): \[plsql] Long parse time and eventually parse error with XMLAGG order by clause

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

