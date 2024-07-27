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

