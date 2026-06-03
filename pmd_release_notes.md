


## 29-June-2026 - 7.26.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.26.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`WrongTestAnnotation`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_java_errorprone.html#wrongtestannotation) detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.

### 🐛️ Fixed Issues
* java-bestpractices
  * [#6692](https://github.com/pmd/pmd/issues/6692): \[java] ForLoopCanBeForeach: inconsistent detection between i += 1 and i = i + 1 update forms
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



