


## 29-June-2026 - 7.26.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.26.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Updated PMD Designer
This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.19.3)](https://github.com/pmd/pmd-designer/releases/tag/7.19.3).

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule [`WrongTestAnnotation`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_java_errorprone.html#wrongtestannotation) detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.
* The new Java rule [`AssertEqualsArgumentOrder`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_java_errorprone.html#assertequalsargumentorder) detects assertions
  where the expected and actual arguments were swapped. This helps find assertions
  that are producing a confusing error message when they fail.
* The new Kotlin rule [`LocalVariableShadowsParameter`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_kotlin_bestpractices.html#localvariableshadowsparameter) detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.
* The new Apex rule [`InvocableClassNoArgConstructor`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_apex_errorprone.html#invocableclassnoargconstructor) detects classes that use
  `@InvocableVariable` properties, but that don't provide a no-arg constructor. Without such a constructor,
  runtime exception occur when Salesforce Flow tries to instantiate such classes.

#### Deprecated Rules
* The rule [`UseObjectForClearerAPI`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_java_design.html#useobjectforclearerapi) was deprecated. Use [`ExcessiveParameterList`](https://docs.pmd-code.org/pmd-doc-7.26.0-SNAPSHOT/pmd_rules_java_design.html#excessiveparameterlist)
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
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation
  * [#6743](https://github.com/pmd/pmd/issues/6743): \[java] CloseResource: False positive for closeable initialized with (T) null
  * [#6781](https://github.com/pmd/pmd/issues/6781): \[java] UselessPureMethodCall: False positive for Stream.forEach
* kotlin
  * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add auxClasspath language property
* kotlin-bestpractices
  * [#6732](https://github.com/pmd/pmd/issues/6732): \[kotlin] New Rule: LocalVariableShadowsParameter

### 🚨️ API Changes
* core
  * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.26.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseParser.html#"><code>AntlrBaseParser</code></a> has been deprecated in favor of
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.26.0-SNAPSHOT/net/sourceforge/pmd/lang/ast/impl/antlr4/AntlrBaseParserWithErrorHandling.html#"><code>AntlrBaseParserWithErrorHandling</code></a>, which converts ANTLR's parsing
    errors into PMD's processing errors by default.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



