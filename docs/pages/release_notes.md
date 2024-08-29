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
* The new Java rule {%rule java/multithreading/AvoidSynchronizedStatement %} finds synchronization blocks that
  could cause performance issues with virtual threads due to pinning.

### 🐛 Fixed Issues
* apex-performance
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop not firing in triggers
* java-multithreading
  * [#5175](https://github.com/pmd/pmd/issues/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement
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
* [#5175](https://github.com/pmd/pmd/pull/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement - [Chas Honton](https://github.com/chonton) (@chonton)

{% endtocmaker %}

