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

#### New rules

- The new Java rule {% rule "java/codestyle/LambdaCanBeMethodReference" %} reports lambda expressions that can be replaced with a method reference. Please read the documentation of the rule for more info. 

### 🐛 Fixed Issues
* java-codestyle
  * [#4881](https://github.com/pmd/pmd/issues/4881): \[java] ClassNamingConventions: interfaces are identified as abstract classes (regression in 7.0.0)
* java-design
  * [#4873](https://github.com/pmd/pmd/issues/4873): \[java] AvoidCatchingGenericException: Can no longer suppress on the exception itself
* java-performance
  * [#3845](https://github.com/pmd/pmd/issues/3845): \[java] InsufficientStringBufferDeclaration should consider literal expression
  * [#4874](https://github.com/pmd/pmd/issues/4874): \[java] StringInstantiation: False-positive when using `new String(charArray)`
  * [#4886](https://github.com/pmd/pmd/issues/4886): \[java] BigIntegerInstantiation: False Positive with Java 17 and BigDecimal.TWO

### 🚨 API Changes

#### Deprecated API

- {% jdoc java::lang.java.ast.ASTLambdaExpression#getBlockBody() %} and {% jdoc java::lang.java.ast.ASTLambdaExpression#getExpressionBody() %}

### ✨ External Contributions

{% endtocmaker %}

