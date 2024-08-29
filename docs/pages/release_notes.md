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
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop: false negative for triggers
* java
  * [#5167](https://github.com/pmd/pmd/issues/5167): \[java] java.lang.IllegalArgumentException: \<?\> cannot be a wildcard bound
* java-bestpractices
  * [#3602](https://github.com/pmd/pmd/issues/3602): \[java] GuardLogStatement: False positive when compile-time constant is created from external constants
  * [#4731](https://github.com/pmd/pmd/issues/4731): \[java] GuardLogStatement: Documentation is unclear why getters are flagged
  * [#5145](https://github.com/pmd/pmd/issues/5145): \[java] UnusedPrivateMethod: False positive with method calls inside lambda
  * [#5151](https://github.com/pmd/pmd/issues/5151): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is a constant from another class
  * [#5152](https://github.com/pmd/pmd/issues/5152): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is "this"
  * [#5153](https://github.com/pmd/pmd/issues/5153): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is an array element
* java-design
  * [#5162](https://github.com/pmd/pmd/issues/5162): \[java] SingularField: False-positive when preceded by synchronized block
* java-multithreading
  * [#5175](https://github.com/pmd/pmd/issues/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement
* plsql
  * [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names)
* plsql-bestpractices
  * [#5132](https://github.com/pmd/pmd/issues/5132): \[plsql] TomKytesDespair: XPathException for more complex exception handler

### 🚨 API Changes
* pmd-jsp
  * {%jdoc jsp::lang.jsp.ast.JspParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-plsql
  * {%jdoc plsql::lang.plsql.ast.PLSQLParserImpl#MergeUpdateClausePrefix() %} is deprecated. This production is
    not used anymore and will be removed. Note: The whole parser implementation class has been deprecated since 7.3.0,
    as it is supposed to be internalized.
* pmd-velocity
  * {%jdoc velocity::lang.velocity.ast.VtlParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-visualforce
  * {%jdoc visualforce::lang.visualforce.ast.VfParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.

### ✨ External Contributions
* [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names) - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5175](https://github.com/pmd/pmd/pull/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement - [Chas Honton](https://github.com/chonton) (@chonton)

{% endtocmaker %}

