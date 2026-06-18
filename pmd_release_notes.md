


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
* The new Kotlin rule [`LocalVariableShadowsParameter`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_kotlin_bestpractices.html#localvariableshadowsparameter) detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.

### 🐛️ Fixed Issues
* apex-security
  * [#2955](https://github.com/pmd/pmd/issues/2955): \[apex] ApexSOQLInjection: False positive when passing local var with concatenating strings
  * [#3877](https://github.com/pmd/pmd/issues/3877): \[apex] ApexCRUDViolation: False positive with Lists of Objects with getSObjectType().getDescribe()
* cpp
  * [#6641](https://github.com/pmd/pmd/issues/6641): \[cpp]: IndexOutOfBoundsException in CPD when a duplication is at end of file with UTF8-BOM
* java-bestpractices
  * [#6692](https://github.com/pmd/pmd/issues/6692): \[java] ForLoopCanBeForeach: inconsistent detection between i += 1 and i = i + 1 update forms
  * [#6782](https://github.com/pmd/pmd/issues/6782): \[java] UseStandardCharsets: ArrayIndexOutOfBoundsException in line 81
* java-codestyle
  * [#6239](https://github.com/pmd/pmd/issues/6239): \[java] UseDiamondOperator: False positive with Guice TypeLiteral
  * [#6775](https://github.com/pmd/pmd/issues/6775): \[java] UselessParentheses: False negative when on the right-hand side of an assignment statement
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation
  * [#6743](https://github.com/pmd/pmd/issues/6743): \[java] CloseResource: False positive for closeable initialized with (T) null
* kotlin-bestpractices
  * [#6732](https://github.com/pmd/pmd/issues/6732): \[kotlin] New Rule: LocalVariableShadowsParameter

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



