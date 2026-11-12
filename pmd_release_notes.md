


## 27-November-2026 - 7.30.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.30.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes and Deprecations](#api-changes-and-deprecations)
    * [Experimental API](#experimental-api)
    * [Deprecations](#deprecations)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule [`ControlFlowInTest`](https://docs.pmd-code.org/pmd-doc-7.30.0-SNAPSHOT/pmd_rules_java_bestpractices.html#controlflowintest)
    reports control flow in test methods that can obscure which assertions
    are executed. It checks conditionals, switch statements and expressions,
    explicit throw statements, try statements with catch clauses,
    and plain try-finally statements.
    Loops are allowed by default and can be reported by setting
    `allowLoops` to `false`.

#### Deprecated Rules
*   The Java rule [`AddEmptyString`](https://docs.pmd-code.org/pmd-doc-7.30.0-SNAPSHOT/pmd_rules_java_performance.html#addemptystring) is deprecated without replacement
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
* java-bestpractices
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] New rule: ControlFlowInTest
    * [#7216](https://github.com/pmd/pmd/issues/7216): \[java] GuardLogStatement: False positive on SLF4J parameterised calls whose arguments are wrapped with logstash value(...)
* java-design
    * [#7277](https://github.com/pmd/pmd/issues/7277): \[java] AvoidRethrowingException: False negative if rethrow is wrapped in a block, ExceptionAsFlowControl false positive instead
    * [#7279](https://github.com/pmd/pmd/issues/7279): \[java] InternalApiUsage: False positive when static field is used in constructor
* java-errorprone
    * [#4531](https://github.com/pmd/pmd/issues/4531): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with multiple top-level classes
    * [#7275](https://github.com/pmd/pmd/issues/7275): \[java] AvoidBranchingStatementAsLastInLoop: False negative when the statement is in a bare block
* java-performance
    * [#6327](https://github.com/pmd/pmd/issues/6327): \[java] AddEmptyString: Deprecate rule because performance claim is not valid anymore
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak
    * [#7171](https://github.com/pmd/pmd/issues/7171): \[kotlin] Support the ..< range operator (Kotlin 1.9)
* misc
    * [#6655](https://github.com/pmd/pmd/issues/6655): Deprecate ANTLR generated lexer implementation classes

### 🚨️ API Changes and Deprecations
#### Experimental API
* kotlin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KtVariableDeclarationAttributes.html#"><code>KtVariableDeclarationAttributes</code></a> now implements
      <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/HasTypeName.html#"><code>HasTypeName</code></a>, so `VariableDeclaration` nodes have a `@TypeName` attribute.
      This covers property and for-loop variables, lambda parameters, and each component of a
      destructuring declaration (`val (a, b) = ...`, `for ((a, b) in ...)`, `{ (a, b) -> ... }`).
      The XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly` and `pmd-kotlin:isNullable`
      support `VariableDeclaration` nodes as well.
    * A destructuring `PropertyDeclaration` (e.g. `val (a, b) = Pair(1, "x")`) no longer has a `@TypeName`.
      Before, it wrongly got the type of the first component. To find destructured variables
      by type, query the component nodes instead: `//VariableDeclaration[pmd-kotlin:typeIs('kotlin.Int')]`.

#### Deprecations
* coco
    * <a href="https://docs.pmd-code.org/apidocs/pmd-coco/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/coco/ast/CocoLexer.html#"><code>CocoLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* css
    * <a href="https://docs.pmd-code.org/apidocs/pmd-css/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/css/ast/CssLexer.html#"><code>CssLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* csharp
    * <a href="https://docs.pmd-code.org/apidocs/pmd-cs/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/cs/ast/CSharpLexer.html#"><code>CSharpLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* dart
    * <a href="https://docs.pmd-code.org/apidocs/pmd-dart/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/dart/ast/DartLexer.html#"><code>DartLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* gherkin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-gherkin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/gherkin/ast/GherkinLexer.html#"><code>GherkinLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* go
    * <a href="https://docs.pmd-code.org/apidocs/pmd-go/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/go/ast/GolangLexer.html#"><code>GolangLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* javascript
    * <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/typescript/ast/TypeScriptLexer.html#"><code>TypeScriptLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* julia
    * <a href="https://docs.pmd-code.org/apidocs/pmd-julia/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/julia/ast/JuliaLexer.html#"><code>JuliaLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* kotlin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KotlinBaseListener.html#"><code>KotlinBaseListener</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KotlinLexer.html#"><code>KotlinLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/ast/KotlinListener.html#"><code>KotlinListener</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* lua
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lua/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/lua/ast/LuaLexer.html#"><code>LuaLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* rust
    * <a href="https://docs.pmd-code.org/apidocs/pmd-rust/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/rust/ast/RustLexer.html#"><code>RustLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* swift
    * <a href="https://docs.pmd-code.org/apidocs/pmd-swift/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/swift/ast/SwiftBaseListener.html#"><code>SwiftBaseListener</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-swift/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/swift/ast/SwiftLexer.html#"><code>SwiftLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-swift/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/swift/ast/SwiftListener.html#"><code>SwiftListener</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* tsql
    * <a href="https://docs.pmd-code.org/apidocs/pmd-tsql/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/tsql/ast/TSqlLexer.html#"><code>TSqlLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
* xml
    * <a href="https://docs.pmd-code.org/apidocs/pmd-xml/7.30.0-SNAPSHOT/net/sourceforge/pmd/lang/xml/ast/XMLLexer.html#"><code>XMLLexer</code></a> has been deprecated. The class is an implementation detail and should have never been part of the public API.
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



