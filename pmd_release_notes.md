


## 2026-??-?? - 7.27.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.27.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [Kotlin type-aware analysis](#kotlin-type-aware-analysis)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [Renamed Rules](#renamed-rules)
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
#### Renamed Rules
* The rule [`InstantiableUtilityClass`](https://docs.pmd-code.org/pmd-doc-7.27.0-SNAPSHOT/pmd_rules_java_design.html#instantiableutilityclass) (Java Design) was renamed from `UseUtilityClass` to better reflect the problem.
  The old name still works but is deprecated.

### 🐛️ Fixed Issues
* apex
    * [#6478](https://github.com/pmd/pmd/issues/6478): \[apex] Parser error when using CALENDAR_YEAR() in SOQL
    * [#6887](https://github.com/pmd/pmd/issues/6887): \[apex] ParseException on Summer '26 multiline string literals ('''...''')
* chore
    * [#6837](https://github.com/pmd/pmd/issues/6837): chore: Input 'app-id' has been deprecated with message: Use 'client-id' instead
* core
    * [#1995](https://github.com/pmd/pmd/issues/1995): \[core] PMD should display number of rules violated or errors found
    * [#4952](https://github.com/pmd/pmd/issues/4952): \[doc] Improve doc around PMDConfiguration#prependAuxclasspath #setClassloader
    * [#4953](https://github.com/pmd/pmd/issues/4953): \[core] Deprecate PMDConfiguration#setClassloader and #getClassloader
    * [#6865](https://github.com/pmd/pmd/issues/6865): \[core] Include the running PMD version in the "Unable to find referenced rule" error
* java
    * [#5041](https://github.com/pmd/pmd/issues/5041): \[java] Parsing failed in ParseLock#doParse(): IndexOutOfBoundsException 
    * [#6768](https://github.com/pmd/pmd/issues/6768): \[java] Disambiguation IllegalStateException resolving a synthesized record accessor used as a call argument alongside an anonymous class
* java-bestpractices
    * [#5514](https://github.com/pmd/pmd/issues/5514): \[java] ExhaustiveSwitchHasDefault fails for non-exhaustive switch statements
    * [#5670](https://github.com/pmd/pmd/issues/5670): \[java] ExhaustiveSwitchHasDefault issue with final fields not initialized in constructor
* java-codestyle
    * [#6709](https://github.com/pmd/pmd/issues/6709): \[java] LambdaCanBeMethodReference: False positive with array creation containing constructor call in receiver
* java-design
    * [#6714](https://github.com/pmd/pmd/issues/6714): \[java] Rename UseUtilityClass to InstantiableUtilityClass
    * [#6844](https://github.com/pmd/pmd/issues/6844): \[java] AvoidThrowingNewInstanceOfSameException: message inconsistent with logic
* java-errorprone
    * [#6826](https://github.com/pmd/pmd/issues/6826): \[java] AssertEqualsArgumentOrder: False positive for double assertEquals
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



