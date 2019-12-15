---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

*   java-bestpractices
    *   [#2149](https://github.com/pmd/pmd/issues/2149): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-performance
    *   [#2141](https://github.com/pmd/pmd/issues/2141): \[java] StringInstatiation: False negative with String-array access

### API Changes


#### Deprecated APIs

##### Internal API

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

##### For removal

* {% jdoc java::lang.java.AbstractJavaParser %}
* {% jdoc java::lang.java.AbstractJavaHandler %}
* {% jdoc java::lang.java.ast.ASTAnyTypeDeclaration.TypeKind %}
* {% jdoc java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
* {% jdoc java::lang.java.ast.JavaQualifiedName %}
* {% jdoc java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
* {% jdoc java::lang.java.ast.JavaQualifiableNode %}
  * {% jdoc java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
  * {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
  * {% jdoc java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
* {% jdoc_package java::lang.java.qname %} and its contents
* {% jdoc java::lang.java.ast.ASTMethodLikeNode %}
  * Its methods will also be removed from its implementations, {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %}, {% jdoc java::lang.java.ast.ASTLambdaExpression %}.


### External Contributions

{% endtocmaker %}

