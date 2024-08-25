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
* apex-performance
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop not firing in triggers
* java
  * [#5167](https://github.com/pmd/pmd/pull/5167): \[java] java.lang.IllegalArgumentException: \<\?\> cannot be a wildcard bound
* java-bestpractices
  * [#3602](https://github.com/pmd/pmd/issues/3602): \[java] GuardLogStatement: False positive when compile-time constant is created from external constants
  * [#4731](https://github.com/pmd/pmd/issues/4731): \[java] GuardLogStatement documentation is unclear why getters are flagged
  * [#5145](https://github.com/pmd/pmd/issues/5145): \[java] False positive UnusedPrivateMethod
  * [#5151](https://github.com/pmd/pmd/issues/5151): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is a constant from another class
  * [#5152](https://github.com/pmd/pmd/issues/5152): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is "this"
  * [#5153](https://github.com/pmd/pmd/issues/5153): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is an array element
* plsql-bestpractices
  * [#5132](https://github.com/pmd/pmd/issues/5132): \[plsql] TomKytesDespair - exception for more complex exception handler

### 🚨 API Changes
* pmd-jsp
  * {%jdoc jsp::lang.jsp.ast.JspParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-velocity
  * {%jdoc velocity::lang.velocity.ast.VtlParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-visualforce
  * {%jdoc visualforce::lang.visualforce.ast.VfParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.

### ✨ External Contributions

{% endtocmaker %}

