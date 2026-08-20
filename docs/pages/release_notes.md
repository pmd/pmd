---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy
#### Java 27 Support
This release of PMD brings support for Java 27.

There are no new standard language features.

There is one preview language feature:
* [JEP 532: Primitive Types in Patterns, instanceof, and switch (Fifth Preview)](https://openjdk.org/jeps/532)

In order to analyze a project with PMD that uses these preview language features,
you'll need to select the new language version `27-preview`:

    pmd check --use-version java-27-preview ...

Note: Support for Java 25 preview language features have been removed. The version "25-preview"
is no longer available.

#### Updated Apex Support
The Apex language support has been bumped to version 67.0 (Summer '26). It supports the new
[Multiline String](https://help.salesforce.com/s/articleView?id=release-notes.rn_apex_multiline_string.htm&release=262&type=5) literals.

#### Kotlin type-aware analysis
Kotlin now supports type-aware analysis via the `auxClasspath` language property (see [#6677](https://github.com/pmd/pmd/issues/6677)).
Resolved type names, return types, and annotation FQNs are available through
{%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %} for use in Java-based rules.

Note: Type data is not yet accessible in XPath rules or the PMD Rule Designer. This will be added in the next version.

### 🌟️ New and Changed Rules
#### New Rules
* The new java rule {% rule java/errorprone/UnusedReturnValue %} (Java Error Prone) finds method calls whose result is not used,
  although ignoring the result of these method calls is likely a mistake.
  The rule is referenced in the quickstart.xml ruleset for Java.
* New rule {% rule java/design/ProtectedMemberInFinalClass %} (Java Design) finds protected members defined in final classes.
  Such members should use package or private visibility to clarify their intended scope.
  The rule replaces now deprecated rules {% rule java/codestyle/AvoidProtectedFieldInFinalClass %} and {% rule java/codestyle/AvoidProtectedMethodInFinalClassNotExtending %}
  and flags members that were previously not detected by either of these rules, such as nested types or constructors.

#### Renamed Rules
* The rule {%rule java/design/InstantiableUtilityClass %} (Java Design) was renamed from `UseUtilityClass` to better reflect the problem.
  The old name still works but is deprecated.

#### Changed Rules
* The rule {% rule java/documentation/CommentRequired %} (Java Documentation)
  has a new property `packageMethodCommentRequirement`. It controls whether Javadoc comments are required (or
  unwanted) for package-private methods and constructors. Previously, only `public` and `protected` methods could
  be configured (via `publicMethodCommentRequirement` and `protectedMethodCommentRequirement`). The new property
  defaults to `Ignored`, so existing rule configurations are unaffected.
  This was implemented in [#6880](https://github.com/pmd/pmd/pull/6880).
* The rule {% rule java/codestyle/BooleanGetMethodName %} (Java Codestyle) has a new property
  `includeWrappedType`. If set to true (default), the rule treats Boolean and boolean identical.
  If set to false, the rule follows the bean convention and treats Boolean like any other object.

#### Deprecated Rules
* The java rule {% rule java/errorprone/CheckSkipResult %} has been deprecated for removal
  in favor of the new rule {% rule java/errorprone/UnusedReturnValue %}.
* The java rule {% rule java/errorprone/UselessPureMethodCall %} has been deprecated for removal
  in favor of the new rule {% rule java/errorprone/UnusedReturnValue %}.

### 🐛️ Fixed Issues
* apex
    * [#6478](https://github.com/pmd/pmd/issues/6478): \[apex] Parser error when using CALENDAR_YEAR() in SOQL
    * [#6887](https://github.com/pmd/pmd/issues/6887): \[apex] ParseException on Summer '26 multiline string literals ('''...''')
* apex-bestpractices
    * [#5904](https://github.com/pmd/pmd/issues/5904): \[apex] ApexUnitTestShouldNotUseSeeAllDataTrue violation range should only be the annotation and not the entire test method
* java
    * [#5041](https://github.com/pmd/pmd/issues/5041): \[java] Parsing failed in ParseLock#doParse(): IndexOutOfBoundsException
    * [#6010](https://github.com/pmd/pmd/issues/6010): \[java] java.lang.OutOfMemoryError: Java heap space when accessing big Jar files with PMD 7
    * [#6374](https://github.com/pmd/pmd/issues/6374): \[java] Support Java 27
    * [#6768](https://github.com/pmd/pmd/issues/6768): \[java] Disambiguation IllegalStateException resolving a synthesized record accessor used as a call argument alongside an anonymous class
    * [#6932](https://github.com/pmd/pmd/issues/6932): \[java] AssertionError when outer class is parsed before inner class with conflicting visibility
* java-bestpractices
    * [#1237](https://github.com/pmd/pmd/issues/1237): \[java] AbstractClassWithoutAnyMethod: False positive for empty subclasses that inherit methods
    * [#1287](https://github.com/pmd/pmd/issues/1287): \[java] GuardLogStatement: False positive when using negative guard conditions
    * [#2033](https://github.com/pmd/pmd/issues/2033): \[jsp] NoClassAttribute: False positive for jsp:useBean
    * [#5514](https://github.com/pmd/pmd/issues/5514): \[java] ExhaustiveSwitchHasDefault: False positive for non-exhaustive switch statements
    * [#5670](https://github.com/pmd/pmd/issues/5670): \[java] ExhaustiveSwitchHasDefault: False positive with final fields not initialized in constructor
    * [#6200](https://github.com/pmd/pmd/issues/6200): \[java] UnusedAssignment: False positive about the ++ unary operator
    * [#6393](https://github.com/pmd/pmd/issues/6393): \[java] UnusedPrivateMethod: False positive with overloaded private methods called with values returned from methods of an unresolved type
    * [#6611](https://github.com/pmd/pmd/issues/6611): \[java] UnnecessaryVarargsArrayCreation: False positive when removing the array creates overload ambiguity
    * [#6965](https://github.com/pmd/pmd/issues/6965): \[java] AbstractClassWithoutAnyMethod: False Positive on derived abstract class
* java-codestyle
    * [#2974](https://github.com/pmd/pmd/issues/2974): \[java] Merge rules about protected in final class (AvoidProtectedFieldInFinalClass, AvoidProtectedMethodInFinalClassNotExtending)
    * [#5441](https://github.com/pmd/pmd/issues/5441): \[java] UseDiamondOperator: False positive with interdependent generic vars
    * [#6958](https://github.com/pmd/pmd/issues/6958): \[java] BooleanGetMethodName should have the option to treat boolean wrapper type differently
    * [#6274](https://github.com/pmd/pmd/issues/6274): \[java] UselessParentheses: False positive in ternary else expression
    * [#6651](https://github.com/pmd/pmd/issues/6651): \[java] UnnecessaryImport: False positive when Javadoc {@<!-- -->link} references an array type
    * [#6709](https://github.com/pmd/pmd/issues/6709): \[java] LambdaCanBeMethodReference: False positive with array creation containing constructor call in receiver
    * [#6737](https://github.com/pmd/pmd/issues/6737): \[java] TooManyStaticImports: @<!-- -->SuppressWarnings("PMD.TooManyStaticImports") has stopped working
    * [#6846](https://github.com/pmd/pmd/issues/6846): \[java] VariableDeclarationUsageDistance: False positive with variables grouped at the top of a block
    * [#6867](https://github.com/pmd/pmd/issues/6867): \[java] UnnecessaryFullyQualifiedName: ContextedAssertionError: This should be unreachable: unknown constant ScopeInfo: MODULE_IMPORT
* java-design
    * [#6714](https://github.com/pmd/pmd/issues/6714): \[java] Rename UseUtilityClass to InstantiableUtilityClass
    * [#6844](https://github.com/pmd/pmd/issues/6844): \[java] AvoidThrowingNewInstanceOfSameException: message inconsistent with logic
    * [#6881](https://github.com/pmd/pmd/issues/6881): \[java] CognitiveComplexity does not count switch expressions
    * [#6925](https://github.com/pmd/pmd/issues/6925): \[java] ImmutableField: False positive on picocli annotated fields
* java-documentation
    * [#6270](https://github.com/pmd/pmd/issues/6270): \[java] CommentSize: False positive with file header comments
    * [#6880](https://github.com/pmd/pmd/issues/6880): \[java] CommentRequired: add packageMethodCommentRequirement property
* java-errorprone
    * [#2840](https://github.com/pmd/pmd/issues/2840): \[java] CloseResource: False positive on mocks
    * [#3880](https://github.com/pmd/pmd/issues/3880): \[java] ReturnEmptyCollectionRatherThanNull: False negative when a null value is assigned to a local that is later returned
    * [#4623](https://github.com/pmd/pmd/issues/4623): \[java] CloseResource: False positive with resource being closed in method
    * [#6435](https://github.com/pmd/pmd/issues/6435): \[java] UnconditionalIfStatement: False negative for negated boolean constant
    * [#6547](https://github.com/pmd/pmd/issues/6547): \[java] NonSerializableClass: False negative for generic element/value types of collections and maps
    * [#6625](https://github.com/pmd/pmd/issues/6625): \[java] New rule: UnusedReturnValue
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
* miscellaneous
    * [#1995](https://github.com/pmd/pmd/issues/1995): \[core] PMD should display number of rules violated or errors found
    * [#2527](https://github.com/pmd/pmd/issues/2527): \[doc] CPD: Invalid link to String Tiling Algorithm
    * [#4952](https://github.com/pmd/pmd/issues/4952): \[doc] Improve doc around PMDConfiguration#prependAuxclasspath #setClassloader
    * [#4953](https://github.com/pmd/pmd/issues/4953): \[core] Deprecate PMDConfiguration#setClassloader and #getClassloader
    * [#6837](https://github.com/pmd/pmd/issues/6837): \[ci] chore: actions/create-github-app-token: Input 'app-id' has been deprecated with message: Use 'client-id' instead
    * [#6865](https://github.com/pmd/pmd/issues/6865): \[core] Include the running PMD version in the "Unable to find referenced rule" error
    * [#6913](https://github.com/pmd/pmd/issues/6913): \[core] RuleSetLoader#loadFromString ignores previously configured Resource/ClassLoader
    * [#6952](https://github.com/pmd/pmd/issues/6952): \[core] Ruleset references are not resolved relative to the referencing ruleset

### 🚨️ API Changes
#### Deprecations
* core
    * {%jdoc !!core::PMDConfiguration#getClassLoader() %} and {%jdoc !!core::PMDConfiguration#setClassLoader(java.lang.ClassLoader) %} are deprecated.
      Use {%jdoc core::PMDConfiguration#prependAuxClasspath(String) %} or {%jdoc core::PMDConfiguration#setAuxClasspath(String) %} to
      configure the auxClasspath for analyzing Java code.  
      Note: In order to read back the currently configured auxClasspath, use {%jdoc core::PMDConfiguration#getAuxClasspath() %} and not the
      deprecated `getClassLoader()` anymore.  
      Using ClassLoaders directly is discouraged, as it is unclear, if and when the ClassLoaders should be closed to release their resources.
      By just configuring the auxClasspath, PMD internally can deal with that.
* core
    * {%jdoc !!core::lang.JvmLanguagePropertyBundle.setClassLoader(ClassLoader) %} and
      {%jdoc !!core::lang.JvmLanguagePropertyBundle.getAnalysisClassLoader() %} are deprecated. Use the language property
      {%jdoc !!core::lang.JvmLanguagePropertyBundle#AUX_CLASSPATH %} instead via `getProperty()` and `setProperty()`. This language property
      is now set correctly when providing the auxClasspath via CLI parameter `--aux-classpath`.
    * The internal class `net.sourceforge.pmd.internal.util.ClasspathClassLoader` has been explicitly marked as deprecated.
      Using ClassLoaders directly is discouraged. Use {%jdoc !!core::PMDConfiguration#setAuxClasspath(String) %} instead.
* java
    * {%jdoc !!java::lang.java.types.TypeSystem#usingClassLoaderClasspath(java.lang.ClassLoader) %} is deprecated. Using
      ClassLoaders directly is discouraged. Use {%jdoc java::lang.java.types.TypeSystem#usingClasspath(java::lang.java.symbols.internal.asm.Classpath) %}
      instead.

#### Experimental API
* core
    * The new {%jdoc core::util.AuxClasspathLoader %} is a replacement for the deprecated `ClasspathClassLoader`.
      It deals with a typical classpath to load classes need for Java's type resolution. It has the static method
      `enableReuse(int)` which enables caching of AuxClasspathLoader instances. This is useful for unit tests
      or IDE plugins, when PMD is executed multiple times within one JVM instance. Don't forget to call
      `disableReuse()` when you're done to close all cached instances.

#### Experimental API
* kotlin
    * {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %}: Provides the initial API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

