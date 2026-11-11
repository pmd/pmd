


## 27-November-2026 - 7.30.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.30.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Experimental API](#experimental-api)
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
* java
    * [#7144](https://github.com/pmd/pmd/issues/7144): \[java] Lambda block body that cannot complete normally is not treated as value-compatible
    * [#7240](https://github.com/pmd/pmd/issues/7240): \[java] "Not a Java binary name" when importing a class with non-ASCII (e.g. Japanese) letters in its name; the whole file is skipped 
* java-bestpractices
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] New rule: ControlFlowInTest
    * [#7216](https://github.com/pmd/pmd/issues/7216): \[java] GuardLogStatement: false positive on SLF4J parameterised calls whose arguments are wrapped with logstash value(...)
* java-design
    * [#7279](https://github.com/pmd/pmd/issues/7279): \[java] InternalApiUsage: false positive when static filed is used in constructor
    * [#7277](https://github.com/pmd/pmd/issues/7277): \[java] AvoidRethrowingException: false negative if rethrow is wrapped in a block, ExceptionAsFlowControl false positive instead
* java-errorprone
    * [#4531](https://github.com/pmd/pmd/issues/4531): \[java] MissingStaticMethodInNonInstantiatableClass: False positive with multiple top-level classes
    * [#7275](https://github.com/pmd/pmd/issues/7275): \[java] AvoidBranchingStatementAsLastInLoop: False negative when the statement is in a bare block
* java-performance
    * [#6327](https://github.com/pmd/pmd/issues/6327): \[java] AddEmptyString: Deprecate rule because performance claim is not valid anymore
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak
    * [#7171](https://github.com/pmd/pmd/issues/7171): \[kotlin] Support the ..< range operator (Kotlin 1.9)

### 🚨️ API Changes
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

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



