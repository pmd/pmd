


## 2026-??-?? - 7.27.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.27.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [Kotlin type-aware analysis](#kotlin-type-aware-analysis)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Renamed Rules](#renamed-rules)
    * [Changed Rules](#changed-rules)
    * [Deprecated Rules](#deprecated-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
    * [Deprecations](#deprecations)
    * [Experimental API](#experimental-api)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy
#### Updated Apex Support
The Apex language support has been bumped to version 67.0 (Summer '26). It supports the new
[Multiline String](https://help.salesforce.com/s/articleView?id=release-notes.rn_apex_multiline_string.htm&release=262&type=5) literals.

#### Kotlin type-aware analysis
Kotlin now supports type-aware analysis via the `auxClasspath` language property (see [#6677](https://github.com/pmd/pmd/issues/6677)).
Resolved type names, return types, and annotation FQNs are available through
<a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.27.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinNodeTypeData.html#"><code>KotlinNodeTypeData</code></a> for use in Java-based rules.

Note: Type data is not yet accessible in XPath rules or the PMD Rule Designer. This will be added in the next version.

### 🌟️ New and Changed Rules
#### New Rules
* The new java rule [`UnusedReturnValue`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_errorprone.html#unusedreturnvalue) finds method calls whose result is not used,
  although ignoring the result of these method calls is likely a mistake.
  The rule is referenced in the quickstart.xml ruleset for Java.

#### Renamed Rules
* The rule [`InstantiableUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_design.html#instantiableutilityclass) (Java Design) was renamed from `UseUtilityClass` to better reflect the problem.
  The old name still works but is deprecated.

#### Changed Rules
* The rule [`CommentRequired`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_documentation.html#commentrequired) (Java Documentation)
  has a new property `packageMethodCommentRequirement`. It controls whether Javadoc comments are required (or
  unwanted) for package-private methods and constructors. Previously, only `public` and `protected` methods could
  be configured (via `publicMethodCommentRequirement` and `protectedMethodCommentRequirement`). The new property
  defaults to `Ignored`, so existing rule configurations are unaffected.
  This was implemented in [#6880](https://github.com/pmd/pmd/pull/6880).
* The rule [`BooleanGetMethodName`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_codestyle.html#booleangetmethodname) (Java Codestyle) has a new property
  `includeWrappedType`. If set to true (default), the rule treats Boolean and boolean identical.
  If set to false, the rule follows the bean convention and treats Boolean like any other object.

#### Deprecated Rules
* The java rule [`CheckSkipResult`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_errorprone.html#checkskipresult) has been deprecated for removal
  in favor of the new rule [`UnusedReturnValue`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_errorprone.html#unusedreturnvalue).
* The java rule [`UselessPureMethodCall`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_errorprone.html#uselesspuremethodcall) has been deprecated for removal
  in favor of the new rule [`UnusedReturnValue`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_errorprone.html#unusedreturnvalue).


### 🐛️ Fixed Issues
* apex
    * [#6478](https://github.com/pmd/pmd/issues/6478): \[apex] Parser error when using CALENDAR_YEAR() in SOQL
    * [#6887](https://github.com/pmd/pmd/issues/6887): \[apex] ParseException on Summer '26 multiline string literals ('''...''')
* apex-bestpractices
    * [#5904](https://github.com/pmd/pmd/issues/5904): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue violation range should only be the annotation and not the entire test method
* chore
    * [#6837](https://github.com/pmd/pmd/issues/6837): chore: Input 'app-id' has been deprecated with message: Use 'client-id' instead
* core
    * [#1995](https://github.com/pmd/pmd/issues/1995): \[core] PMD should display number of rules violated or errors found
    * [#4952](https://github.com/pmd/pmd/issues/4952): \[doc] Improve doc around PMDConfiguration#prependAuxclasspath #setClassloader
    * [#4953](https://github.com/pmd/pmd/issues/4953): \[core] Deprecate PMDConfiguration#setClassloader and #getClassloader
    * [#6865](https://github.com/pmd/pmd/issues/6865): \[core] Include the running PMD version in the "Unable to find referenced rule" error
    * [#6913](https://github.com/pmd/pmd/issues/6913): \[core] RuleSetLoader#loadFromString ignores previously configured Resource/ClassLoader
    * [#6952](https://github.com/pmd/pmd/issues/6952): \[core] Ruleset references are not resolved relative to the referencing ruleset
* java
    * [#5041](https://github.com/pmd/pmd/issues/5041): \[java] Parsing failed in ParseLock#doParse(): IndexOutOfBoundsException 
    * [#6768](https://github.com/pmd/pmd/issues/6768): \[java] Disambiguation IllegalStateException resolving a synthesized record accessor used as a call argument alongside an anonymous class
    * [#6932](https://github.com/pmd/pmd/issues/6932): \[java] AssertionError when outer class is parsed before inner class with conflicting visibility
* java-bestpractices
    * [#1237](https://github.com/pmd/pmd/issues/1237): \[java] AbstractClassWithoutAnyMethod - Only detecting empty classes 
    * [#1287](https://github.com/pmd/pmd/issues/1287): \[java] GuardLogStatement: False positive when using negative guard conditions
    * [#2033](https://github.com/pmd/pmd/issues/2033): \[jsp] NoClassAttribute rule is generating a false positive
    * [#5514](https://github.com/pmd/pmd/issues/5514): \[java] ExhaustiveSwitchHasDefault fails for non-exhaustive switch statements
    * [#5670](https://github.com/pmd/pmd/issues/5670): \[java] ExhaustiveSwitchHasDefault issue with final fields not initialized in constructor
    * [#6200](https://github.com/pmd/pmd/issues/6200): \[java] UnusedAssignment: False positive about the ++ unary operator
    * [#6393](https://github.com/pmd/pmd/issues/6393): \[java] UnusedPrivateMethod: False positive with overloaded private methods called with values returned from methods of an unresolved type
    * [#6611](https://github.com/pmd/pmd/issues/6611): \[java] UnnecessaryVarargsArrayCreation: false positive when removing the array creates overload ambiguity
    * [#6965](https://github.com/pmd/pmd/issues/6965): \[java] AbstractClassWithoutAnyMethod False Positive on derived abstract class
* java-codestyle
    * [#5441](https://github.com/pmd/pmd/issues/5441): \[java] UseDiamondOperator: False positive with interdependent generic vars
    * [#6958](https://github.com/pmd/pmd/issues/6958): \[java] BooleanGetMethodName should have the option to treat boolean wrapper type differently
    * [#6274](https://github.com/pmd/pmd/issues/6274): \[java] UselessParentheses: Treat parentheses around a ternary in the else-branch as clarifying, consistent with the then-branch.
    * [#6651](https://github.com/pmd/pmd/issues/6651): \[java] UnnecessaryImport false-positive when Javadoc {@link} references an array type
    * [#6709](https://github.com/pmd/pmd/issues/6709): \[java] LambdaCanBeMethodReference: False positive with array creation containing constructor call in receiver
    * [#6737](https://github.com/pmd/pmd/issues/6737): \[java] TooManyStaticImports: @<!-- -->SuppressWarnings("PMD.TooManyStaticImports") has stopped working
    * [#6846](https://github.com/pmd/pmd/issues/6846): \[java] VariableDeclarationUsageDistance: False positive with variables grouped at the top of a block
    * [#6867](https://github.com/pmd/pmd/issues/6867): \[java] UnnecessaryFullyQualifiedName: ContextedAssertionError: This should be unreachable: unknown constant ScopeInfo: MODULE_IMPORT
* java-design
    * [#6714](https://github.com/pmd/pmd/issues/6714): \[java] Rename UseUtilityClass to InstantiableUtilityClass
    * [#6844](https://github.com/pmd/pmd/issues/6844): \[java] AvoidThrowingNewInstanceOfSameException: message inconsistent with logic
    * [#6881](https://github.com/pmd/pmd/issues/6881): \[java] CognitiveComplexity does not count switch expressions
    * [#6925](https://github.com/pmd/pmd/issues/6925): \[java] ImmutableField: false positive on picocli annotated fields with default
* java-documentation
    * [#6270](https://github.com/pmd/pmd/issues/6270): \[java] CommentSize: Skip file header comments.
* java-errorprone
    * [#2840](https://github.com/pmd/pmd/issues/2840): \[java] CloseResource: False positive on mocks
    * [#3880](https://github.com/pmd/pmd/issues/3880): \[java] ReturnEmptyCollectionRatherThanNull: False negative when a null value is assigned to a local that is later returned
    * [#4623](https://github.com/pmd/pmd/issues/4623): \[java] CloseResource: False positive with resource being closed in method
    * [#6435](https://github.com/pmd/pmd/issues/6435): \[java] UnconditionalIfStatement: False negative for negated boolean constant
    * [#6547](https://github.com/pmd/pmd/issues/6547): \[java] NonSerializableClass: Report non-serializable generic element/value types of collections and maps
    * [#6695](https://github.com/pmd/pmd/issues/6695): \[java] ReturnEmptyCollectionRatherThanNull: False negative when null is returned through a local variable
    * [#6742](https://github.com/pmd/pmd/issues/6742): \[java] CloseResource: False positive when a correctly-closed resource is declared without initializer
    * [#6744](https://github.com/pmd/pmd/issues/6744): \[java] ReturnEmptyCollectionRatherThanNull: False negatives when a returned expression can evaluate to null
    * [#6826](https://github.com/pmd/pmd/issues/6826): \[java] AssertEqualsArgumentOrder: False positive for double assertEquals
    * [#6900](https://github.com/pmd/pmd/issues/6900): \[java] DoubleCheckedLocking: False negative when the outer null check is written as !(x != null)
* java-multithreading
    * [#6747](https://github.com/pmd/pmd/issues/6747): \[java] NonThreadSafeSingleton: False negative with ternary conditional operator
* kotlin
    * [#6795](https://github.com/pmd/pmd/issues/6795): \[kotlin] Add kotlin-type-mapper infrastructure
    * [#6891](https://github.com/pmd/pmd/issues/6891): \[kotlin] AnnotationFqnAnnotator: @<!-- -->TypeName not set on UnescapedAnnotation nodes

### 🚨️ API Changes

#### Deprecations
* core
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.27.0-SNAPSHOT/net/sourceforge/pmd/PMDConfiguration.html#getClassLoader()"><code>PMDConfiguration#getClassLoader</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.27.0-SNAPSHOT/net/sourceforge/pmd/PMDConfiguration.html#setClassLoader(java.lang.ClassLoader)"><code>PMDConfiguration#setClassLoader</code></a> are deprecated.
      Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.27.0-SNAPSHOT/net/sourceforge/pmd/PMDConfiguration.html#prependAuxClasspath(String)"><code>prependAuxClasspath</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.27.0-SNAPSHOT/net/sourceforge/pmd/PMDConfiguration.html#setAuxClasspath(String)"><code>setAuxClasspath</code></a> to
      configure the auxClasspath for analyzing Java code.  
      Note: In order to read back the currently configured auxClasspath, use <a href="https://docs.pmd-code.org/apidocs/pmd-core/7.27.0-SNAPSHOT/net/sourceforge/pmd/PMDConfiguration.html#getAuxClasspath()"><code>getAuxClasspath</code></a> and not the
      deprecated `getClassLoader()` anymore.  
      Using ClassLoaders directly is discouraged, as it is unclear, if and when the ClassLoaders should be closed to release their resources.
      By just configuring the auxClasspath, PMD internally can deal with that.

#### Experimental API
* kotlin
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/7.27.0-SNAPSHOT/net/sourceforge/pmd/lang/kotlin/types/KotlinNodeTypeData.html#"><code>KotlinNodeTypeData</code></a>: Provides the initial API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



