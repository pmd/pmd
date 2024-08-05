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

#### New: Java 23 Support

This release of PMD brings support for Java 23. There are no new standard language features,
but a couple of preview language features:

* [JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)](https://openjdk.org/jeps/455)
* [JEP 476: Module Import Declarations (Preview)](https://openjdk.org/jeps/476)
* [JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)](https://openjdk.org/jeps/477)
* [JEP 482: Flexible Constructor Bodies (Second Preview)](https://openjdk.org/jeps/482)

Note that String Templates (introduced as preview in Java 21 and 22) are not supported anymore in Java 23,
see [JDK-8329949](https://bugs.openjdk.org/browse/JDK-8329949) for details.

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `23-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-23-preview ...

Note: Support for Java 21 preview language features have been removed. The version "21-preview"
are no longer available.

### 🐛 Fixed Issues
* apex-performance
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop not firing in triggers
* java
  * [#5062](https://github.com/pmd/pmd/issues/5062): \[java] Support Java 23
* plsql-bestpractices
  * [#5132](https://github.com/pmd/pmd/issues/5132): \[plsql] TomKytesDespair - exception for more complex exception handler

### 🚨 API Changes
#### Deprecations
* pmd-jsp
  * {%jdoc jsp::lang.jsp.ast.JspParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-velocity
  * {%jdoc velocity::lang.velocity.ast.VtlParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-visualforce
  * {%jdoc visualforce::lang.visualforce.ast.VfParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.

#### Experimental
* pmd-java
  * Renamed `isUnnamedClass()` to {%jdoc !!java::lang.java.ast.ASTCompilationUnit#isSimpleCompilationUnit() %}
  * {%jdoc java::lang.java.ast.ASTImplicitClassDeclaration %}
  * {%jdoc !!java::lang.java.ast.ASTImportDeclaration#isModule() %}
  * {%jdoc !ac!java::lang.java.ast.JavaVisitorBase#visit(java::lang.java.ast.ASTImplicitClassDeclaration,P) %}

### ✨ External Contributions

{% endtocmaker %}

