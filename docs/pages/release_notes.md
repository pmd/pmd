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

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/ControlFlowInTest %}
    reports control flow in test methods that can obscure which assertions
    are executed. It checks conditionals, switch statements and expressions,
    explicit throw statements, try statements with catch clauses,
    and plain try-finally statements.
    Loops are allowed by default and can be reported by setting
    `allowLoops` to `false`.

#### Deprecated Rules
*   The Java rule {% rule java/performance/AddEmptyString %} is deprecated without replacement
    and will be removed with PMD 8.0.0. Its performance claim is not valid on modern JDKs:
    constant string concatenations are evaluated at compile time, and replacing concatenation
    with `toString()` or `String.valueOf()` does not generally improve performance.
    The rule is excluded when referencing the whole Performance category, but explicit rule
    references continue to work during the deprecation period.

### 🐛️ Fixed Issues
* apex
    * [#7182](https://github.com/pmd/pmd/issues/7182): \[apex] Expose the referenced type name on ClassRefExpression for XPath rules
* apex-design
    * [#4863](https://github.com/pmd/pmd/issues/4863): \[apex] CyclomaticComplexity: False negative with switch statements
* core
    * [#5760](https://github.com/pmd/pmd/issues/5760): \[core] SarifRenderer does not respect relativizeRoots
* java
    * [#7144](https://github.com/pmd/pmd/issues/7144): \[java] Lambda block body that cannot complete normally is not treated as value-compatible
    * [#7240](https://github.com/pmd/pmd/issues/7240): \[java] "Not a Java binary name" when importing a class with non-ASCII (e.g. Japanese) letters in its name; the whole file is skipped 
    * [#7313](https://github.com/pmd/pmd/issues/7313): \[java] Casts to boolean and boolean equality are not folded as constant expressions
* java-bestpractices
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] New rule: ControlFlowInTest
    * [#7216](https://github.com/pmd/pmd/issues/7216): \[java] GuardLogStatement: False positive on SLF4J parameterised calls whose arguments are wrapped with logstash value(...)
* java-codestyle
    * [#6731](https://github.com/pmd/pmd/issues/6731): \[java] UnnecessaryFullyQualifiedName: False positive for lombok.Builder annotation
    * [#7215](https://github.com/pmd/pmd/issues/7215): \[java] UnnecessaryImport: False positive for on-demand imports with incomplete aux-classpath
* java-design
    * [#7277](https://github.com/pmd/pmd/issues/7277): \[java] AvoidRethrowingException: False negative if rethrow is wrapped in a block, ExceptionAsFlowControl false positive instead
    * [#7279](https://github.com/pmd/pmd/issues/7279): \[java] InternalApiUsage: False positive when static field is used in constructor
* java-errorprone
    * [#4531](https://github.com/pmd/pmd/issues/4531): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with multiple top-level classes
    * [#7275](https://github.com/pmd/pmd/issues/7275): \[java] AvoidBranchingStatementAsLastInLoop: False negative when the statement is in a bare block
* java-performance
    * [#6327](https://github.com/pmd/pmd/issues/6327): \[java] AddEmptyString: Deprecate rule because performance claim is not valid anymore
    * [#7327](https://github.com/pmd/pmd/issues/7327): \[java] AvoidArrayLoops: a copy loop whose body has no braces is not reported
* java-security
    * [#7329](https://github.com/pmd/pmd/issues/7329): \[java] InsecureCryptoIv / HardCodedCryptoKey: hard-coded field passed as `this.field` not reported
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak
    * [#7171](https://github.com/pmd/pmd/issues/7171): \[kotlin] Support the ..< range operator (Kotlin 1.9)
* misc
    * [#6655](https://github.com/pmd/pmd/issues/6655): Deprecate ANTLR generated lexer implementation classes

### 🚨️ API Changes and Deprecations
#### Experimental API
* kotlin
    * {% jdoc kotlin::lang.kotlin.ast.KtVariableDeclarationAttributes %} now implements
      {% jdoc kotlin::lang.kotlin.ast.HasTypeName %}, so `VariableDeclaration` nodes have a `@TypeName` attribute.
      This covers property and for-loop variables, lambda parameters, and each component of a
      destructuring declaration (`val (a, b) = ...`, `for ((a, b) in ...)`, `{ (a, b) -> ... }`).
      The XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly` and `pmd-kotlin:isNullable`
      support `VariableDeclaration` nodes as well.
    * A destructuring `PropertyDeclaration` (e.g. `val (a, b) = Pair(1, "x")`) no longer has a `@TypeName`.
      Before, it wrongly got the type of the first component. To find destructured variables
      by type, query the component nodes instead: `//VariableDeclaration[pmd-kotlin:typeIs('kotlin.Int')]`.

#### Deprecations
* coco
    * {% jdoc coco::lang.coco.ast.CocoLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* css
    * {% jdoc css::lang.css.ast.CssLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* csharp
    * {% jdoc cs::lang.cs.ast.CSharpLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* dart
    * {% jdoc dart::lang.dart.ast.DartLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* gherkin
    * {% jdoc gherkin::lang.gherkin.ast.GherkinLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* go
    * {% jdoc go::lang.go.ast.GolangLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* javascript
    * {% jdoc javascript::lang.typescript.ast.TypeScriptLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* julia
    * {% jdoc julia::lang.julia.ast.JuliaLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* kotlin
    * {% jdoc kotlin::lang.kotlin.ast.KotlinBaseListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc kotlin::lang.kotlin.ast.KotlinLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc kotlin::lang.kotlin.ast.KotlinListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* lua
    * {% jdoc lua::lang.lua.ast.LuaLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* rust
    * {% jdoc rust::lang.rust.ast.RustLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* swift
    * {% jdoc swift::lang.swift.ast.SwiftBaseListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc swift::lang.swift.ast.SwiftLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * {% jdoc swift::lang.swift.ast.SwiftListener %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* tsql
    * {% jdoc tsql::lang.tsql.ast.TSqlLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* xml
    * {% jdoc xml::lang.xml.ast.XMLLexer %} has been deprecated. The class is an implementation detail and should have never been part of the public API.
* The generated `KotlinParser` and `SwiftParser` classes and their nested AST node classes are
  intentionally not deprecated yet. These classes still contain the public AST node types,
  which have no replacement API yet. They will be deprecated once we have decided how to handle
  the inner classes.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

