


## 28-December-2025 - 7.20.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.20.0-SNAPSHOT.

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
* core
  * [#6330](https://github.com/pmd/pmd/issues/6330): \[core] "Unable to create ValueRepresentation" when using @<!-- -->LiteralText (XPath)
* java-bestpractices
  * [#6028](https://github.com/pmd/pmd/issues/6028): \[java] UnusedPrivateMethod: False positive with raw type for generic method
  * [#6257](https://github.com/pmd/pmd/issues/6257): \[java] UnusedLocalVariable: False positive with instanceof pattern guard
  * [#6291](https://github.com/pmd/pmd/issues/6291): \[java] EnumComparison: False positive for any object when object.equals(null)
* java-codestyle
  * [#5043](https://github.com/pmd/pmd/issues/5043): \[java] LambdaCanBeMethodReference: False positive on overloaded methods
  * [#6237](https://github.com/pmd/pmd/issues/6237): \[java] UnnecessaryCast: ContextedRuntimeException when parsing switch expression with lambdas
  * [#6279](https://github.com/pmd/pmd/issues/6279): \[java] EmptyMethodInAbstractClassShouldBeAbstract: False positive for final empty methods
* java-errorprone
  * [#6276](https://github.com/pmd/pmd/issues/6276): \[java] NullAssignment: False positive when assigning null to a final field in a constructor
  * [#6343](https://github.com/pmd/pmd/issues/6343): \[java] MissingStaticMethodInNonInstantiatableClass: False negative when method in nested class returns null
* maintenance
  * [#6230](https://github.com/pmd/pmd/issues/6230): \[core] Single module snapshot build fails

### 🚨️ API Changes

#### Experimental API
* pmd-java: <a href="https://docs.pmd-code.org/apidocs/pmd-java/7.20.0-SNAPSHOT/net/sourceforge/pmd/lang/java/types/OverloadSelectionResult.html#hadSeveralApplicableOverloads()"><code>OverloadSelectionResult#hadSeveralApplicableOverloads</code></a>

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



