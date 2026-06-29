---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy
#### Swift Changes
The Swift parser now forwards syntax errors as usual processing errors. Before it just logged any errors and
tried to move on, resulting in an incomplete AST with error nodes. As part of this change, the grammar has been
slightly improved around macro declarations, generic parameters and parameter packs.  
This means that PMD might fail now on Swift files with processing errors, when it previously ran without
obvious problems. The Swift module in PMD now behaves like other modules in regard to error handling.

#### Updated PMD Designer
This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.19.3)](https://github.com/pmd/pmd-designer/releases/tag/7.19.3).

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule {% rule java/errorprone/WrongTestAnnotation %} detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.
* The new Java rule {% rule java/errorprone/AssertEqualsArgumentOrder %} detects assertions
  where the expected and actual arguments were swapped. This helps find assertions
  that are producing a confusing error message when they fail.
* The new Kotlin rule {% rule kotlin/bestpractices/LocalVariableShadowsParameter %} detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.
* The new Apex rule {% rule apex/errorprone/InvocableClassNoArgConstructor %} detects classes that use
  `@InvocableVariable` properties, but that don't provide a no-arg constructor. Without such a constructor,
  runtime exception occur when Salesforce Flow tries to instantiate such classes.

#### Deprecated Rules
* The rule {% rule java/design/UseObjectForClearerAPI %} was deprecated. Use {% rule java/design/ExcessiveParameterList %}
  instead. The old rule name still works.

### 🐛️ Fixed Issues
* apex
  * [#6806](https://github.com/pmd/pmd/issues/6806): \[apex] ANTLR runtime mismatch 4.9.1 used for code generation does not match the current runtime version 4.13.2
* apex-errorprone
  * [#6793](https://github.com/pmd/pmd/issues/6793): \[apex] New Rule: Invocable Classes require a no argument constructor
* apex-security
  * [#2955](https://github.com/pmd/pmd/issues/2955): \[apex] ApexSOQLInjection: False positive when passing local var with concatenating strings
  * [#3877](https://github.com/pmd/pmd/issues/3877): \[apex] ApexCRUDViolation: False positive with Lists of Objects with getSObjectType().getDescribe()
* core
  * [#6764](https://github.com/pmd/pmd/issues/6764): \[core] ANTLR: Report syntax errors as processing errors
* cpp
  * [#6641](https://github.com/pmd/pmd/issues/6641): \[cpp]: IndexOutOfBoundsException in CPD when a duplication is at end of file with UTF8-BOM
* cli
  * [#6741](https://github.com/pmd/pmd/issues/6741): \[cli] Designer: Fix quotes in PMD_OPENJFX_MODULE_PATH setting
* java
  * [#6812](https://github.com/pmd/pmd/issues/6812): \[java] Rename ASTMethodDeclaration#isOverridden() to isOverride()
* java-bestpractices
  * [#6692](https://github.com/pmd/pmd/issues/6692): \[java] ForLoopCanBeForeach: inconsistent detection between i += 1 and i = i + 1 update forms
  * [#6736](https://github.com/pmd/pmd/issues/6736): \[java] JUnitJupiterTestShouldBePackagePrivate: False negative when the only tests are in a @<!-- -->Nested class
  * [#6782](https://github.com/pmd/pmd/issues/6782): \[java] UseStandardCharsets: ArrayIndexOutOfBoundsException in line 81
* java-codestyle
  * [#6239](https://github.com/pmd/pmd/issues/6239): \[java] UseDiamondOperator: False positive with Guice TypeLiteral
  * [#6775](https://github.com/pmd/pmd/issues/6775): \[java] UselessParentheses: False negative when on the right-hand side of an assignment statement
* java-design
  * [#3741](https://github.com/pmd/pmd/issues/3741): \[java] Deprecate UseObjectForClearerAPI
  * [#6459](https://github.com/pmd/pmd/issues/6459): \[java] PublicMemberInNonPublicType: False positive for main(...) methods
  * [#6460](https://github.com/pmd/pmd/issues/6460): \[java] PublicMemberInNonPublicType: False negative for overridden methods
  * [#6814](https://github.com/pmd/pmd/issues/6814): \[java] AvoidDeepNestedIfStmts: count ifs properly in else branch
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation
  * [#5011](https://github.com/pmd/pmd/issues/5011): \[java] TestClassWithoutTestCases: False positive for test classes extending a class with tests (in nested classes)
  * [#6743](https://github.com/pmd/pmd/issues/6743): \[java] CloseResource: False positive for closeable initialized with (T) null
  * [#6781](https://github.com/pmd/pmd/issues/6781): \[java] UselessPureMethodCall: False positive for Stream.forEach
* java-performance
  * [#6740](https://github.com/pmd/pmd/issues/6740): \[java] OptimizableToArrayCall: False positive when new T\[0x0] is used instead of new T\[0]
* kotlin
  * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add auxClasspath language property
* kotlin-bestpractices
  * [#6732](https://github.com/pmd/pmd/issues/6732): \[kotlin] New Rule: LocalVariableShadowsParameter
* swift
  * [#6801](https://github.com/pmd/pmd/issues/6801): \[swift] Report syntax errors as processing errors

### 🚨️ API Changes
* core
  * {% jdoc core::lang.ast.impl.antlr4.AntlrBaseParser %} has been deprecated in favor of
    {% jdoc core::lang.ast.impl.antlr4.AntlrBaseParserWithErrorHandling %}, which converts ANTLR's parsing
    errors into PMD's processing errors by default.
* java
  * {% jdoc !!java::lang.java.ast.ASTMethodDeclaration#isOverridden() %} has been renamed to {% jdoc java::lang.java.ast.ASTMethodDeclaration#isOverride() %}.
    The old name has been deprecated and will remain available until PMD 8.  
    The corresponding XPath attribute `@Overridden` is deprecated as well. Use `@Override` instead.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

