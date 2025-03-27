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
* core
  * [#5593](https://github.com/pmd/pmd/issues/5593): \[core] Make renderers output files in deterministic order even when multithreaded
* apex
  * [#5567](https://github.com/pmd/pmd/issues/5567): \[apex] Provide type information for CastExpression
* java
  * [#5587](https://github.com/pmd/pmd/issues/5587): \[java] Thread deadlock during PMD analysis in ParseLock.getFinalStatus
* java-bestpractices
  * [#2849](https://github.com/pmd/pmd/issues/2849): \[java] New Rule: ImplicitFunctionalInterface
  * [#5369](https://github.com/pmd/pmd/issues/5369): \[java] UnusedPrivateMethod false positives with lombok.val
  * [#5590](https://github.com/pmd/pmd/issues/5590): \[java] LiteralsFirstInComparisonsRule not applied on constant
  * [#5592](https://github.com/pmd/pmd/issues/5592): \[java] UnusedAssignment false positive in record compact constructor
* plsql
  * [#5521](https://github.com/pmd/pmd/issues/5521): \[plsql] Long parse time and eventually parse error with XMLAGG order by clause

### 🚨 API Changes
#### Deprecations
* java
  * The method {%jdoc java::lang.java.ast.AbstractJavaExpr#buildConstValue() %} is deprecated for removal. It should
    have been package-private from the start. In order to get the (compile time) const value of an expression, use
    {%jdoc java::lang.java.ast.ASTExpression#getConstValue() %} or {%jdoc java::lang.java.ast.ASTExpression#getConstFoldingResult() %}
    instead.
  * For the same reason, the following methods are also deprecated for removal:
    {%jdoc java::lang.java.ast.ASTNumericLiteral#buildConstValue() %} and {%jdoc java::lang.java.ast.ASTStringLiteral#buildConstValue() %}.

- {% jdoc !!java::lang.java.types.JTypeVar#withUpperbound(java::types.JTypeMirror) %} is deprecated. It was previously meant to be used
  internally and not needed anymore.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

