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
* The new Apex rule {% rule apex/bestpractices/QueueableWithoutFinalizer %} detects when the Queueable interface
  is used but a Finalizer is not attached. Without attaching a Finalizer, there is no way of designing error
  recovery actions should the Queueable action fail.

### 🐛 Fixed Issues
* ant
  * [#1860](https://github.com/pmd/pmd/issues/1860): \[ant] Reflective access warnings on java > 9 and java < 17
* apex
  * [#5302](https://github.com/pmd/pmd/issues/5302): \[apex] New Rule: Queueable Should Attach Finalizer
  * [#5333](https://github.com/pmd/pmd/issues/5333): \[apex] Token recognition errors for string containing unicode escape sequence
* html
  * [#5322](https://github.com/pmd/pmd/issues/5322): \[html] CPD throws exception on when HTML file is missing closing tag
* java
  * [#5293](https://github.com/pmd/pmd/issues/5293): \[java] Deadlock when executing PMD in multiple threads
  * [#5324](https://github.com/pmd/pmd/issues/5324): \[java] Issue with type inference of nested lambdas
  * [#5329](https://github.com/pmd/pmd/issues/5329): \[java] Type inference issue with unknown method ref in call chain
* java-bestpractices
  * [#5083](https://github.com/pmd/pmd/issues/5083): \[java] UnusedPrivateMethod false positive when method reference has no target type
  * [#5097](https://github.com/pmd/pmd/issues/5097): \[java] UnusedPrivateMethod FP with raw type missing from the classpath
  * [#5318](https://github.com/pmd/pmd/issues/5318): \[java] PreserveStackTraceRule: false-positive on Pattern Matching with instanceof
* java-codestyle
  * [#5263](https://github.com/pmd/pmd/issues/5263): \[java] UnnecessaryFullyQualifiedName: false-positive in an enum that uses its own static variables
* java-performance
  * [#5287](https://github.com/pmd/pmd/issues/5287): \[java] TooFewBranchesForSwitch false-positive with switch using list of case constants
  * [#5314](https://github.com/pmd/pmd/issues/5314): \[java] InsufficientStringBufferDeclarationRule: Lack of handling for char type parameters

### 🚨 API Changes

#### Deprecations
* pmd-gherkin
  * {%jdoc gherkin::lang.gherkin.ast.GherkinBaseListener %} is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * {%jdoc gherkin::lang.gherkin.ast.GherkinBaseVisitor %} is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * {%jdoc gherkin::lang.gherkin.ast.GherkinListener %} is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * {%jdoc gherkin::lang.gherkin.ast.GherkinParser %} is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
  * {%jdoc gherkin::lang.gherkin.ast.GherkinVisitor %} is deprecated for removal. This class was never intended
    to be generated. It will be removed with no replacement.
* pmd-julia
  * {%jdoc julia::lang.julia.ast.JuliaBaseListener %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * {%jdoc julia::lang.julia.ast.JuliaBaseVisitor %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * {%jdoc julia::lang.julia.ast.JuliaListener %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * {%jdoc julia::lang.julia.ast.JuliaParser %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
  * {%jdoc julia::lang.julia.ast.JuliaVisitor %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
* pmd-kotlin
  * {%jdoc kotlin::lang.kotlin.ast.UnicodeClasses %} is deprecated for removal. This class was never intended to
    be generated. It will be removed with no replacement.
* pmd-xml
  * {%jdoc xml::lang.xml.antlr4.XMLLexer %} is deprecated for removal. Use {%jdoc !!xml::lang.xml.ast.XMLLexer %}
    instead (note different package `ast` instead of `antlr4`).

### ✨ External Contributions
* [#5284](https://github.com/pmd/pmd/pull/5284): \[apex] Use case-insensitive input stream to avoid choking on Unicode escape sequences - [Willem A. Hajenius](https://github.com/wahajenius) (@wahajenius)
* [#5303](https://github.com/pmd/pmd/pull/5303): \[apex] New Rule: Queueable Should Attach Finalizer - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5354](https://github.com/pmd/pmd/pull/5354): \[apex] Updated the docs for UnusedMethod as per discussion #5200 - [samc-gearset](https://github.com/sam-gearset) (@sam-gearset)

{% endtocmaker %}

