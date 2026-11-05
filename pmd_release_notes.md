


## 27-November-2026 - 7.30.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.30.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Experimental API](#experimental-api)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🐛️ Fixed Issues
* kotlin
    * [#7168](https://github.com/pmd/pmd/pull/7168): \[kotlin] Add @<!-- -->TypeName to VariableDeclaration, fix destructuring type leak

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



