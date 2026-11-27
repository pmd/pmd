


## 28-December-2026 - 7.31.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.31.0-SNAPSHOT.

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
*   The new Java rule [`MarkdownJavadocBeforeJava23`](https://docs.pmd-code.org/pmd-doc-7.31.0-SNAPSHOT/pmd_rules_java_documentation.html#markdownjavadocbeforejava23)
    reports comments starting with `///`, so they can be reviewed before
    switching to Java 23. The rule stays off for Java 23 and later.

### 🐛️ Fixed Issues
* java
    * [#7392](https://github.com/pmd/pmd/issues/7392): \[java\] PMD 7.29.0 fails to resolve a switch pattern variable passed to an anonymous class constructor
* java-bestpractices
    * [#7308](https://github.com/pmd/pmd/issues/7308): \[java\] MethodReturnsInternalArray: document that private methods are not checked 
* java-design
    * [#7328](https://github.com/pmd/pmd/issues/7328): \[java] CollapsibleIfStatements: not reported when a bare block separates the two if statements
* java-documentation
    * [#7057](https://github.com/pmd/pmd/issues/7057): \[java\] New rule: MarkdownJavadocBeforeJava23

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



