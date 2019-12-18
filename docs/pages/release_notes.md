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

#### Modelica support

Thanks to [Anatoly Trosinenko](https://github.com/atrosinenko) PMD supports now a new language:
[Modelica](https://modelica.org/modelicalanguage) is a language to model complex physical systems.
Both PMD and CPD are supported and there are already [3 rules available](pmd_rules_modelica.html).
The PMD Designer supports syntax highlighting for Modelica.

While the language implementation is quite complete, Modelica support is considered experimental
for now. This is to allow us to change the rule API (e.g. the AST classes) slightly and improve
the implementation based on your feedback.

#### Modified Rules

*   The Java rule {% rule "java/errorprone/AvoidLiteralsInIfCondition" %} (`java-errorprone`) has a new property
    `ignoreExpressions`. This property is set by default to `true` in order to maintain compatibility. If this
    property is set to false, then literals in more complex expressions are considered as well.

*   The Apex rule {% rule "apex/errorprone/ApexCSRF" %} (`apex-errorprone`) has been moved from category
    "Security" to "Error Prone". The Apex runtime already prevents DML statements from being executed, but only
    at runtime. So, if you try to do this, you'll get an error at runtime, hence this is error prone. See also
    the discussion on [#2064](https://github.com/pmd/pmd/issues/2064).

### Fixed Issues

*   java-bestpractices
    *   [#2149](https://github.com/pmd/pmd/issues/2149): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#2167](https://github.com/pmd/pmd/issues/2167): \[java] UnnecessaryLocalBeforeReturn false positive with variable captured by method reference
*   java-errorprone
    *   [#2140](https://github.com/pmd/pmd/issues/2140): \[java] AvoidLiteralsInIfCondition: false negative for expressions
*   java-performance
    *   [#2141](https://github.com/pmd/pmd/issues/2141): \[java] StringInstatiation: False negative with String-array access

### API Changes


#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

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
* [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
* {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
* {% jdoc java::lang.java.ast.JavaQualifiedName %}
* {% jdoc !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
* {% jdoc java::lang.java.ast.JavaQualifiableNode %}
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
  * {% jdoc !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
  * {% jdoc !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
* {% jdoc_package java::lang.java.qname %} and its contents
* {% jdoc java::lang.java.ast.MethodLikeNode %}
  * Its methods will also be removed from its implementations,
    {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
    {% jdoc java::lang.java.ast.ASTLambdaExpression %}.
* {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
  instead. This affects {% jdoc !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
  {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
  {% jdoc !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.


### External Contributions

*   [#2041](https://github.com/pmd/pmd/pull/2041): \[modelica] Initial implementation for PMD - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2051](https://github.com/pmd/pmd/pull/2051): \[doc] Update the docs on adding a new language - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2169](https://github.com/pmd/pmd/pull/2169): \[modelica] Follow-up fixes for Modelica language module - [Anatoly Trosinenko](https://github.com/atrosinenko)

{% endtocmaker %}

