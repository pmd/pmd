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
* java-bestpractices
  * [#5369](https://github.com/pmd/pmd/issues/5369): \[java] UnusedPrivateMethod false positives with lombok.val
  * [#5590](https://github.com/pmd/pmd/issues/5590): \[java] LiteralsFirstInComparisonsRule not applied on constant

### 🚨 API Changes
#### Deprecations
* java
  * The method {%jdoc java::lang.java.ast.AbstractJavaExpr#buildConstValue() %} is deprecated for removal. It should
    have been package-private from the start. In order to get the (compile time) const value of an expression, use
    {%jdoc java::lang.java.ast.ASTExpression#getConstValue() %} or {%jdoc java::lang.java.ast.ASTExpression#getConstFoldingResult() %}
    instead.
  * For the same reason, the following methods are also deprecated for removal:
    {%jdoc java::lang.java.ast.ASTNumericLiteral#buildConstValue() %} and {%jdoc java::lang.java.ast.ASTStringLiteral#buildConstValue() %}.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

