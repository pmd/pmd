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
*   The new java rule {% rule java/errorprone/UnusedReturnValue %} (Java Error Prone) finds method calls whose result is not used,
    although ignoring the result of these method calls is likely a mistake.  
    The rule is referenced in the quickstart.xml ruleset for Java.
*   New rule {% rule java/design/ProtectedMemberInFinalClass %} (Java Design) finds protected members defined in final classes.
    Such members should use package or private visibility to clarify their intended scope.
    The rule replaces now deprecated rules {% rule java/codestyle/AvoidProtectedFieldInFinalClass %} and {% rule java/codestyle/AvoidProtectedMethodInFinalClassNotExtending %}
    and flags members that were previously not detected by either of these rules, such as nested types or constructors.  
    The rule is referenced in the quickstart.xml ruleset for Java.

#### Renamed Rules
*   The rule {%rule java/design/InstantiableUtilityClass %} (Java Design) was renamed from `UseUtilityClass` to better reflect the problem.
    The old name still works but is deprecated.

#### Changed Rules
*   The rule {% rule java/documentation/CommentRequired %} (Java Documentation)
    has a new property `packageMethodCommentRequirement`. It controls whether Javadoc comments are required (or
    unwanted) for package-private methods and constructors. Previously, only `public` and `protected` methods could
    be configured (via `publicMethodCommentRequirement` and `protectedMethodCommentRequirement`). The new property
    defaults to `Ignored`, so existing rule configurations are unaffected.
    This was implemented in [#6880](https://github.com/pmd/pmd/pull/6880).
*   The rule {% rule java/codestyle/BooleanGetMethodName %} (Java Codestyle) has a new property
    `includeWrappedType`. If set to true (default), the rule treats Boolean and boolean identical.
    If set to false, the rule follows the bean convention and treats Boolean like any other object.

#### Deprecated Rules
*   The java rule {% rule java/errorprone/CheckSkipResult %} has been deprecated for removal
    in favor of the new rule {% rule java/errorprone/UnusedReturnValue %}.
*   The java rule {% rule java/errorprone/UselessPureMethodCall %} has been deprecated for removal
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
    * [#6943](https://github.com/pmd/pmd/issues/6943): \[java] UnnecessaryCast: False positives related to generics
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
    * [#6537](https://github.com/pmd/pmd/issues/6537): \[java] StaticEJBFieldShouldBeFinal: False Negative when using @Stateless etc.
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
* kotlin
    * {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %}: Provides the initial API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6795](https://github.com/pmd/pmd/pull/6795): \[kotlin] Add kotlin-type-mapper infrastructure - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6811](https://github.com/pmd/pmd/pull/6811): \[java] Fix #4623: CloseResource: False positive with resource being closed in method - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6822](https://github.com/pmd/pmd/pull/6822): \[java] Fix #5670, #5514: ExhaustiveSwitchHasDefault when default is necessary - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6823](https://github.com/pmd/pmd/pull/6823): \[cli] Print PMD analysis summary - [DragonFSKY](https://github.com/DragonFSKY) (@DragonFSKY)
* [#6825](https://github.com/pmd/pmd/pull/6825): \[doc] Update "Merging pull requests" - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6827](https://github.com/pmd/pmd/pull/6827): \[java] Fix #6826: AssertEqualsArgumentOrder false positive for double/float delta - [Dan Halperin](https://github.com/dhalperi) (@dhalperi)
* [#6828](https://github.com/pmd/pmd/pull/6828): \[java] Fix #6709: Fix false positive: LambdaCanBeMethodReference should not flag lambda… - [Subhadeep](https://github.com/dweep-js) (@dweep-js)
* [#6829](https://github.com/pmd/pmd/pull/6829): \[core] test: cover pmd analysis configuration - [amir](https://github.com/amirdeljouyi) (@amirdeljouyi)
* [#6838](https://github.com/pmd/pmd/pull/6838): \[java] Follow-up on #6809: Add tests - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6840](https://github.com/pmd/pmd/pull/6840): chore: Fix #6837: Use client id for create-github-app-token - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6841](https://github.com/pmd/pmd/pull/6841): \[core] refactor: AnalysisCache based on Path - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6842](https://github.com/pmd/pmd/pull/6842): \[java] #4730: Add a test for FinalFieldCouldBeStatic that shows that #4730 was already fixed - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6843](https://github.com/pmd/pmd/pull/6843): \[java] Fix #6714: Rename UseUtilityClass to InstantiableUtilityClass - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6845](https://github.com/pmd/pmd/pull/6845): \[core] Fix #4953: Deprecate PMDConfiguration#getClassLoader - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6858](https://github.com/pmd/pmd/pull/6858): \[java] Fix AvoidThrowingNewInstanceOfSameException false positive - [Subhadeep](https://github.com/dweep-js) (@dweep-js)
* [#6859](https://github.com/pmd/pmd/pull/6859): \[java] Fix #6010: Add replacement for ClasspathClassloader - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6860](https://github.com/pmd/pmd/pull/6860): \[java] Fix #6846: VariableDeclarationUsageDistance: False positive with variables grouped at the top of a block - [Gamja-rani](https://github.com/onetuks) (@onetuks)
* [#6863](https://github.com/pmd/pmd/pull/6863): \[java] chore: Add test for ReportStatsListener - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6866](https://github.com/pmd/pmd/pull/6866): \[java] Fix #5041: IndexOutOfBoundsException for type annotations on inner class method signatures - [Niklas Keller](https://github.com/kelunik) (@kelunik)
* [#6868](https://github.com/pmd/pmd/pull/6868): \[java] Fix #6867: Handle module imports in UnnecessaryFullyQualifiedName - [DragonFSKY](https://github.com/DragonFSKY) (@DragonFSKY)
* [#6870](https://github.com/pmd/pmd/pull/6870): \[core] Fix #6865: Improve missing rule reference error - [DragonFSKY](https://github.com/DragonFSKY) (@DragonFSKY)
* [#6871](https://github.com/pmd/pmd/pull/6871): \[java] Fix #6768: Resolve record component types before inference - [DragonFSKY](https://github.com/DragonFSKY) (@DragonFSKY)
* [#6880](https://github.com/pmd/pmd/pull/6880): \[java] CommentRequired: add packageMethodCommentRequirement property - [legacynode](https://github.com/legacynode) (@legacynode)
* [#6883](https://github.com/pmd/pmd/pull/6883): \[java] CognitiveComplexity: count switch expressions - [Kurath](https://github.com/KurathSec) (@KurathSec)
* [#6884](https://github.com/pmd/pmd/pull/6884): \[java] Fix #6651: UnnecessaryImport: False positive for array-typed Javadoc {@<!-- -->link} parameters - [hexonal](https://github.com/hexonal) (@hexonal)
* [#6886](https://github.com/pmd/pmd/pull/6886): \[doc] chore: release_notes - Use 4 space indentation - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6892](https://github.com/pmd/pmd/pull/6892): \[kotlin] Fix #6891: Use KtModifiers container in AnnotationFqnAnnotator - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#6894](https://github.com/pmd/pmd/pull/6894): \[java] Support Java 27 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6902](https://github.com/pmd/pmd/pull/6902): \[java] Fix #6737: Use next annotatable sibling for supressing top level - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6905](https://github.com/pmd/pmd/pull/6905): Fix Regression-Tester config: Replace Schedul-o-matic-9000 with declarative-lookup-rollup-summaries - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6906](https://github.com/pmd/pmd/pull/6906): \[jsp] Fix #2033: NoClassAttribute for jsp:useBean - [Columbus Labs](https://github.com/ColumbusLabs) (@ColumbusLabs)
* [#6916](https://github.com/pmd/pmd/pull/6916): \[java] Fix #6625: New rule: UnusedReturnValue - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6917](https://github.com/pmd/pmd/pull/6917): \[java] Fix #6900: Modifies isNullCheck to accept negated expressions - [Will-6543](https://github.com/Will-6543) (@Will-6543)
* [#6918](https://github.com/pmd/pmd/pull/6918): \[java] Fix #6742: CloseResource false positive for a wrapped resource assigned without an initializer - [Eljees](https://github.com/Eljees) (@Eljees)
* [#6919](https://github.com/pmd/pmd/pull/6919): \[java] Prepare deprecation of asCtx in java-bestpractices (part of #4814) - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6920](https://github.com/pmd/pmd/pull/6920): \[java] Fix #1287: GuardLogStatement false positive with a guard clause - [Eljees](https://github.com/Eljees) (@Eljees)
* [#6921](https://github.com/pmd/pmd/pull/6921): \[core] Fix #6913: Use the configured class loader in RuleSetLoader#loadFromString - [renechoi](https://github.com/renechoi) (@renechoi)
* [#6933](https://github.com/pmd/pmd/pull/6933): \[java] Fix #6932: Handle conflicting inner class visibility modifiers - [Scrates1](https://github.com/Scrates1) (@Scrates1)
* [#6934](https://github.com/pmd/pmd/pull/6934): \[java] Fix #5441: Resolve interdependent inference variables simultaneously - [Sebastian Lövdahl](https://github.com/slovdahl) (@slovdahl)
* [#6936](https://github.com/pmd/pmd/pull/6936): \[java] Fix #6925: ImmutableField: false positive on picocli @<!-- -->Option/@<!-- -->Parameters fields - [dev_Hakaze](https://github.com/arimu1) (@arimu1)
* [#6938](https://github.com/pmd/pmd/pull/6938): \[java] Fix #6747: NonThreadSafeSingleton flags ternary lazy init - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6939](https://github.com/pmd/pmd/pull/6939): \[java] Fix #6435: UnconditionalIfStatement flags arbitrarily negated boolean literals - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6941](https://github.com/pmd/pmd/pull/6941): \[java] Fix #6744: ReturnEmptyCollectionRatherThanNull: Analyze possible null return values - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6942](https://github.com/pmd/pmd/pull/6942): \[java] Fix #6547: NonSerializableClass checks collection/map generic element types - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6944](https://github.com/pmd/pmd/pull/6944): chore: Remove unnecessary casts - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6945](https://github.com/pmd/pmd/pull/6945): \[java] Fix #6537: StaticEJBFieldShouldBeFinal detects @<!-- -->Stateless/@<!-- -->Stateful/@<!-- -->Singleton/@<!-- -->MessageDriven EJB classes - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6947](https://github.com/pmd/pmd/pull/6947): \[java] Fix #6393: UnusedPrivateMethod FP on overloaded methods when overload resolution fails - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6948](https://github.com/pmd/pmd/pull/6948): \[java] Fix #6270: CommentSize skips file header comments - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6949](https://github.com/pmd/pmd/pull/6949): \[java] Fix #6611: UnnecessaryVarargsArrayCreation ignores overload ambiguity - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6950](https://github.com/pmd/pmd/pull/6950): \[java] Fix #6274: UselessParentheses treats ternary else-branch parentheses as clarifying - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#6953](https://github.com/pmd/pmd/pull/6953): \[core] Fix #6952: Resolve ruleset references relative to the referencing ruleset - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6954](https://github.com/pmd/pmd/pull/6954): \[java] Fix #2840: CloseResource: allow Mockito mocks by default - [Eljees](https://github.com/Eljees) (@Eljees)
* [#6955](https://github.com/pmd/pmd/pull/6955): \[apex] Fix ApexUnitTestShouldNotUseSeeAllDataTrue violation location - [Taran](https://github.com/tarann26) (@tarann26)
* [#6957](https://github.com/pmd/pmd/pull/6957): \[java] Fix dataflow state for conditional initializers - [subotac](https://github.com/subotac) (@subotac)
* [#6959](https://github.com/pmd/pmd/pull/6959): \[chore] Add oout to allowed list of typos - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6962](https://github.com/pmd/pmd/pull/6962): \[java] Fix #6958: Add configurable Boolean handling to BooleanGetMethodName - [Harshit Sinha](https://github.com/harshitsinha11) (@harshitsinha11)
* [#6964](https://github.com/pmd/pmd/pull/6964): \[java] New rule: ProtectedMemberInFinalClass - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6966](https://github.com/pmd/pmd/pull/6966): \[java] AssertEqualsArgumentOrder: False negative for assertEquals with delta - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6968](https://github.com/pmd/pmd/pull/6968): \[java] Fix #6967: Make violation message of UnusedReturnValue consistent by removing type parameters - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6972](https://github.com/pmd/pmd/pull/6972): \[java] Fix #6965: AbstractClassWithoutAnyMethod false positive on derived abstract class - [Abdullah](https://github.com/AzazelSensei) (@AzazelSensei)
* [#6973](https://github.com/pmd/pmd/pull/6973): chore: Enforce bytecode version JDK 8 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6974](https://github.com/pmd/pmd/pull/6974): \[java] Introduce JPackageSymbol - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6976](https://github.com/pmd/pmd/pull/6976): chore: Fix AssertEqualsArgumentOrder - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6978](https://github.com/pmd/pmd/pull/6978): \[java] UnusedReturnValue: Fix description - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#6981](https://github.com/pmd/pmd/pull/6981): \[doc] Fix #2527: restore CPD tiling article link - [Abdullah](https://github.com/AzazelSensei) (@AzazelSensei)
* [#6982](https://github.com/pmd/pmd/pull/6982): \[java] Fix #6943: UnnecessaryCast: false positives related to generics - [dev_Hakaze](https://github.com/arimu1) (@arimu1)
* [#6989](https://github.com/pmd/pmd/pull/6989): \[doc] Update release notes for 7.27.0 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6990](https://github.com/pmd/pmd/pull/6990): \[java] Move ProtectedMemberInFinalClass to design category - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6824](https://github.com/pmd/pmd/pull/6824): Bump PMD from 7.25.0 to 7.26.0
* [#6830](https://github.com/pmd/pmd/pull/6830): chore(deps): bump actions/setup-java from 5.3.0 to 5.4.0
* [#6831](https://github.com/pmd/pmd/pull/6831): chore(deps): bump junit.version from 6.1.0 to 6.1.1
* [#6832](https://github.com/pmd/pmd/pull/6832): chore(deps): bump actions/cache from 6.0.0 to 6.1.0
* [#6833](https://github.com/pmd/pmd/pull/6833): chore(deps): bump actions/cache/restore from 6.0.0 to 6.1.0
* [#6834](https://github.com/pmd/pmd/pull/6834): chore(deps): bump ruby/setup-ruby from 1.314.0 to 1.315.0
* [#6835](https://github.com/pmd/pmd/pull/6835): chore(deps): bump crate-ci/typos from 1.47.2 to 1.48.0
* [#6836](https://github.com/pmd/pmd/pull/6836): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.6.0 to 13.7.0
* [#6847](https://github.com/pmd/pmd/pull/6847): chore(deps): bump marocchino/sticky-pull-request-comment from 3.0.4 to 3.0.5
* [#6848](https://github.com/pmd/pmd/pull/6848): chore(deps): bump actions/setup-java from 5.4.0 to 5.5.0
* [#6849](https://github.com/pmd/pmd/pull/6849): chore(deps): bump ruby/setup-ruby from 1.315.0 to 1.316.0
* [#6850](https://github.com/pmd/pmd/pull/6850): chore(deps): bump scalameta.version from 4.17.0 to 4.17.1
* [#6851](https://github.com/pmd/pmd/pull/6851): chore(deps): bump io.github.apex-dev-tools:vf-parser from 2.0.0-beta.1 to 2.0.0
* [#6852](https://github.com/pmd/pmd/pull/6852): chore(deps-dev): bump log4j.version from 2.26.0 to 2.26.1
* [#6854](https://github.com/pmd/pmd/pull/6854): chore(deps): bump org.apache.groovy:groovy from 5.0.6 to 5.0.7
* [#6855](https://github.com/pmd/pmd/pull/6855): chore(deps-dev): bump byte.buddy.version from 1.18.10 to 1.18.11
* [#6857](https://github.com/pmd/pmd/pull/6857): chore(deps): bump org.checkerframework:checker-qual from 4.2.0 to 4.2.1
* [#6862](https://github.com/pmd/pmd/pull/6862): \[apex] Fix #6478: Bump apex-parser from 5.0.0 to 5.1.0
* [#6872](https://github.com/pmd/pmd/pull/6872): chore: Bump maven from 3.9.14 to 3.9.16
* [#6873](https://github.com/pmd/pmd/pull/6873): chore(deps): bump net.sf.saxon:Saxon-HE from 12.9 to 12.10
* [#6874](https://github.com/pmd/pmd/pull/6874): chore(deps): bump ruby/setup-ruby from 1.316.0 to 1.318.0
* [#6875](https://github.com/pmd/pmd/pull/6875): chore(deps): bump scalameta.version from 4.17.1 to 4.17.2
* [#6876](https://github.com/pmd/pmd/pull/6876): chore(deps): bump junit.version from 6.1.1 to 6.1.2
* [#6878](https://github.com/pmd/pmd/pull/6878): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.7.0 to 13.8.0
* [#6890](https://github.com/pmd/pmd/pull/6890): \[apex]\[visualforce] Update apex-ls from 6.0.2 to 6.1.0
* [#6897](https://github.com/pmd/pmd/pull/6897): chore(deps): bump ruby/setup-ruby from 1.318.0 to 1.320.0
* [#6898](https://github.com/pmd/pmd/pull/6898): chore(deps): bump actions/checkout from 7.0.0 to 7.0.1
* [#6899](https://github.com/pmd/pmd/pull/6899): chore(deps): bump actions/setup-java from 5.5.0 to 5.6.0
* [#6907](https://github.com/pmd/pmd/pull/6907): chore(deps): bump ruby/setup-ruby from 1.320.0 to 1.321.0
* [#6908](https://github.com/pmd/pmd/pull/6908): chore(deps): bump scalameta.version from 4.17.2 to 4.17.3
* [#6909](https://github.com/pmd/pmd/pull/6909): chore(deps): bump csv from 3.3.5 to 3.3.6 in /docs
* [#6910](https://github.com/pmd/pmd/pull/6910): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.8.0 to 13.10.0
* [#6911](https://github.com/pmd/pmd/pull/6911): chore(deps): bump org.apache.maven.plugins:maven-jar-plugin from 3.5.0 to 3.5.1
* [#6927](https://github.com/pmd/pmd/pull/6927): chore(deps): bump org.jsoup:jsoup from 1.22.2 to 1.23.1
* [#6928](https://github.com/pmd/pmd/pull/6928): chore(deps): bump org.cyclonedx:cyclonedx-maven-plugin from 2.9.2 to 2.9.3
* [#6929](https://github.com/pmd/pmd/pull/6929): chore(deps): bump actions/setup-java from 5.6.0 to 5.7.0
* [#6930](https://github.com/pmd/pmd/pull/6930): chore(deps): bump org.apache.groovy:groovy from 5.0.7 to 5.0.8
* [#6931](https://github.com/pmd/pmd/pull/6931): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.1.0 to 6.2.0
* [#6969](https://github.com/pmd/pmd/pull/6969): chore(deps): bump junit.version from 6.1.2 to 6.1.3
* [#6970](https://github.com/pmd/pmd/pull/6970): chore(deps): bump crate-ci/typos from 1.48.0 to 1.49.0
* [#6971](https://github.com/pmd/pmd/pull/6971): chore(deps): bump org.checkerframework:checker-qual from 4.2.1 to 4.2.2
* [#6979](https://github.com/pmd/pmd/pull/6979): chore(deps): bump build-tools from 39 to 40
* [#6980](https://github.com/pmd/pmd/pull/6980): chore(deps): bump json from 2.19.2 to 2.19.9 in /docs
* [#6985](https://github.com/pmd/pmd/pull/6985): chore(deps-dev): bump com.google.guava:guava from 33.6.0-jre to 33.7.1-jre
* [#6986](https://github.com/pmd/pmd/pull/6986): chore(deps): bump org.apache.groovy:groovy from 5.0.8 to 5.1.0
* [#6997](https://github.com/pmd/pmd/pull/6997): chore(deps): bump com.google.protobuf:protobuf-java from 4.35.1 to 4.36.0
* [#6998](https://github.com/pmd/pmd/pull/6998): chore(deps-dev): bump byte.buddy.version from 1.18.11 to 1.18.12
* [#6999](https://github.com/pmd/pmd/pull/6999): chore(deps): bump com.puppycrawl.tools:checkstyle from 13.10.0 to 14.0.0
* [#7001](https://github.com/pmd/pmd/pull/7001): chore(deps): bump io.github.apex-dev-tools:apex-parser from 5.1.0 to 5.2.0

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 120 commits
* 124 closed tickets & PRs
* Days since last release: 60

{% endtocmaker %}
