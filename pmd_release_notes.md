


## 12-September-2025 - 7.17.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.17.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀 New and noteworthy](#new-and-noteworthy)
    * [✨ New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛 Fixed Issues](#fixed-issues)
* [🚨 API Changes](#api-changes)
* [✨ Merged pull requests](#merged-pull-requests)
* [📦 Dependency updates](#dependency-updates)
* [📈 Stats](#stats)

### 🚀 New and noteworthy

#### ✨ New Rules
* The new java rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#typeparameternamingconventions) replaces the now deprecated rule
  GenericsNaming. The new rule is configurable and checks for naming conventions of type parameters in
  generic types and methods. It can be configured via a regular expression.  
  By default, this rule uses the standard Java naming convention (single uppercase letter).  
  The rule is referenced in the quickstart.xml ruleset for Java.

#### Deprecated Rules
* The java rule [`GenericsNaming`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#genericsnaming) has been deprecated for removal in favor
  of the new rule [`TypeParameterNamingConventions`](https://docs.pmd-code.org/pmd-doc-7.17.0-SNAPSHOT/pmd_rules_java_codestyle.html#typeparameternamingconventions).

### 🐛 Fixed Issues
* java
  * [#5874](https://github.com/pmd/pmd/issues/5874): \[java] Update java regression tests with Java 25 language features
* java-codestyle
  * [#972](https://github.com/pmd/pmd/issues/972): \[java] Improve naming conventions rules

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5922](https://github.com/pmd/pmd/pull/5922): Fix #972: \[java] Add a new rule TypeParameterNamingConventions - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5932](https://github.com/pmd/pmd/pull/5932): \[ci] Reuse GitHub Pre-Releases - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->



