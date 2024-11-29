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
  * [#5283](https://github.com/pmd/pmd/issues/5283): \[java] AssertionError "this should be unreachable" with scala library
  * [#5293](https://github.com/pmd/pmd/issues/5293): \[java] Deadlock when executing PMD in multiple threads
  * [#5324](https://github.com/pmd/pmd/issues/5324): \[java] Issue with type inference of nested lambdas
  * [#5329](https://github.com/pmd/pmd/issues/5329): \[java] Type inference issue with unknown method ref in call chain
  * [#5338](https://github.com/pmd/pmd/issues/5338): \[java] Unresolved target type for lambdas make overload resolution fail
* java-bestpractices
  * [#4113](https://github.com/pmd/pmd/issues/4113): \[java] JUnitTestsShouldIncludeAssert - false positive with SoftAssertionsExtension
  * [#5083](https://github.com/pmd/pmd/issues/5083): \[java] UnusedPrivateMethod false positive when method reference has no target type
  * [#5097](https://github.com/pmd/pmd/issues/5097): \[java] UnusedPrivateMethod FP with raw type missing from the classpath
  * [#5318](https://github.com/pmd/pmd/issues/5318): \[java] PreserveStackTraceRule: false-positive on Pattern Matching with instanceof
* java-codestyle
  * [#5214](https://github.com/pmd/pmd/issues/5214): \[java] Wrong message for LambdaCanBeMethodReference with method of enclosing class
  * [#5263](https://github.com/pmd/pmd/issues/5263): \[java] UnnecessaryFullyQualifiedName: false-positive in an enum that uses its own static variables
  * [#5315](https://github.com/pmd/pmd/issues/5315): \[java] UnnecessaryImport false positive for on-demand imports
* java-design
  * [#4763](https://github.com/pmd/pmd/issues/4763): \[java] SimplifyBooleanReturns - wrong suggested solution
* java-errorprone
  * [#5070](https://github.com/pmd/pmd/issues/5070): \[java] ConfusingArgumentToVarargsMethod FP when types are unresolved
* java-performance
  * [#5287](https://github.com/pmd/pmd/issues/5287): \[java] TooFewBranchesForSwitch false-positive with switch using list of case constants
  * [#5314](https://github.com/pmd/pmd/issues/5314): \[java] InsufficientStringBufferDeclarationRule: Lack of handling for char type parameters
  * [#5320](https://github.com/pmd/pmd/issues/5320): \[java] UseStringBufferLength: false-negative on StringBuffer of sb.toString().equals("")

### 🚨 API Changes

#### Deprecations
* pmd-coco
  * {%jdoc coco::lang.coco.ast.CocoBaseListener %} is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * {%jdoc coco::lang.coco.ast.CocoBaseVisitor %} is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * {%jdoc coco::lang.coco.ast.CocoListener %} is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * {%jdoc coco::lang.coco.ast.CocoParser %} is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
   * {%jdoc coco::lang.coco.ast.CocoVisitor %} is deprecated for removal. This class was never intended
     to be generated. It will be removed with no replacement.
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

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5284](https://github.com/pmd/pmd/pull/5284): \[apex] Use case-insensitive input stream to avoid choking on Unicode escape sequences - [Willem A. Hajenius](https://github.com/wahajenius) (@wahajenius)
* [#5286](https://github.com/pmd/pmd/pull/5286): \[ant] Formatter: avoid reflective access to determine console encoding - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5289](https://github.com/pmd/pmd/pull/5289): \[java] TooFewBranchesForSwitch - allow list of case constants - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5296](https://github.com/pmd/pmd/pull/5296): \[xml] Have pmd-xml Lexer in line with other antlr grammars - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5300](https://github.com/pmd/pmd/pull/5300): Add rule test cases for issues fixed with PMD 7.0.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5303](https://github.com/pmd/pmd/pull/5303): \[apex] New Rule: Queueable Should Attach Finalizer - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5309](https://github.com/pmd/pmd/pull/5309): \[java] Fix #5293: Parse number of type parameters eagerly - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5310](https://github.com/pmd/pmd/pull/5310): \[java] Fix #5283 - inner class has public private modifiers - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5325](https://github.com/pmd/pmd/pull/5325): \[java] Fix inference dependency issue with nested lambdas - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5326](https://github.com/pmd/pmd/pull/5326): \[java] UseStringBufferLength - consider sb.toString().equals("") - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5328](https://github.com/pmd/pmd/pull/5328): \[html] Test for a closing tag when determining node positions - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5330](https://github.com/pmd/pmd/pull/5330): \[java] Propagate unknown type better when mref is unresolved - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5331](https://github.com/pmd/pmd/pull/5331): \[java] PreserveStackTrace - consider instance type patterns - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5332](https://github.com/pmd/pmd/pull/5332): \[java] InsufficientStringBufferDeclaration: Fix CCE for Character - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5334](https://github.com/pmd/pmd/pull/5334): \[java] UnitTestShouldIncludeAssert - consider SoftAssertionsExtension - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5335](https://github.com/pmd/pmd/pull/5335): \[kotlin] Prevent auxiliary grammars from generating lexers - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5336](https://github.com/pmd/pmd/pull/5336): \[gherkin] Remove generated gherkin code from coverage report - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5337](https://github.com/pmd/pmd/pull/5337): \[doc] Introducing PMD Guru on Gurubase.io - [Kursat Aktas](https://github.com/kursataktas) (@kursataktas)
* [#5339](https://github.com/pmd/pmd/pull/5339): \[java] Allow lambdas with unresolved target types to succeed inference - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5340](https://github.com/pmd/pmd/pull/5340): \[java] Fix #5097 - problem with unchecked conversion - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5341](https://github.com/pmd/pmd/pull/5341): \[java] Fix #5083 - UnusedPrivateMethod false positive with mref without target type but with exact method - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5342](https://github.com/pmd/pmd/pull/5342): \[julia] Ignore generated code in Julia module - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5345](https://github.com/pmd/pmd/pull/5345): \[coco] Remove generated coco files form coverage - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5346](https://github.com/pmd/pmd/pull/5346): \[typescript] Add cleanup after generating ts lexer - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5347](https://github.com/pmd/pmd/pull/5347): \[tsql] Flag generated lexer as generated - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5352](https://github.com/pmd/pmd/pull/5352): \[java] Add permitted subtypes to symbol API - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5353](https://github.com/pmd/pmd/pull/5353): \[java] Fix #5263 - UnnecessaryFullyQualifiedName FP with forward references - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5354](https://github.com/pmd/pmd/pull/5354): \[apex] Updated the docs for UnusedMethod as per discussion #5200 - [samc-gearset](https://github.com/sam-gearset) (@sam-gearset)
* [#5370](https://github.com/pmd/pmd/pull/5370): \[java] Fix #5214 - LambdaCanBeMethodReference issue with method of enclosing class - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5371](https://github.com/pmd/pmd/pull/5371): \[doc] Improve docs on adding Antlr languages - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)
* [#5372](https://github.com/pmd/pmd/pull/5372): \[java] Fix #5315 - UnusedImport FP with import on demand - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5373](https://github.com/pmd/pmd/pull/5373): \[java] Fix #4763 - wrong message for SimplifyBooleanReturns - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#5374](https://github.com/pmd/pmd/pull/5374): \[java] Fix #5070 - confusing argument to varargs method FP when types are unknown - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#5285](https://github.com/pmd/pmd/pull/5285): Bump pmd from 7.5.0 to 7.7.0
* [#5288](https://github.com/pmd/pmd/pull/5288): Bump asm from 9.7 to 9.7.1
* [#5290](https://github.com/pmd/pmd/pull/5290): Bump org.apache.maven.plugins:maven-assembly-plugin from 3.6.0 to 3.7.1
* [#5301](https://github.com/pmd/pmd/pull/5301): Bump gems and bundler
* [#5307](https://github.com/pmd/pmd/pull/5307): Bump org.apache.maven.plugins:maven-clean-plugin from 3.3.2 to 3.4.0
* [#5308](https://github.com/pmd/pmd/pull/5308): Bump webrick from 1.8.2 to 1.9.0 in /docs in the all-gems group across 1 directory
* [#5312](https://github.com/pmd/pmd/pull/5312): Bump maven-pmd-plugin from 3.24.0 to 3.26.0
* [#5316](https://github.com/pmd/pmd/pull/5316): Bump rouge from 4.4.0 to 4.5.0 in the all-gems group across 1 directory
* [#5317](https://github.com/pmd/pmd/pull/5317): Bump org.apache.commons:commons-compress from 1.26.0 to 1.27.1
* [#5348](https://github.com/pmd/pmd/pull/5348): Bump rouge from 4.5.0 to 4.5.1 in the all-gems group across 1 directory
* [#5350](https://github.com/pmd/pmd/pull/5350): Bump org.apache.commons:commons-lang3 from 3.14.0 to 3.17.0

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

