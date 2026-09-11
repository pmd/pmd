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

#### Kotlin XPath functions and type attributes
Type data is now accessible in XPath rules via new attributes and helper functions (see [Kotlin XPath rule support]({{ baseurl }}pmd_languages_kotlin.html#xpath-rule-support)):

* **Attributes**: `@TypeName`, `@ReturnTypeName`, `@AnnotationFqNames`, `@Modifiers`, `@Identifier`
  are exposed on declaration nodes (property, function, class, parameter, catch, for-loop, delegation specifier,
  annotation nodes).
* **`pmd-kotlin:typeIs(typeName)`**: matches if the node's type is `typeName` or a subtype.
* **`pmd-kotlin:typeIsExactly(typeName)`**: matches the exact declared type only (no subtypes).
* **`pmd-kotlin:hasAnnotation(name)`**: matches if the node has an annotation with the given simple or FQN.
* **`pmd-kotlin:modifiers()`**: returns the modifier keywords of a declaration as a sequence.
* **`pmd-kotlin:isNullable()`**: returns `true` if the node's declared type is nullable (has `?`).
* **`pmd-kotlin:hasUnresolvedReference()`**: returns `true` if the node contains an unresolved reference.
* **`pmd-kotlin:matchesSig(signature)`**: matches call sites by method signature pattern (supports wildcards).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/OnDemandImport %} reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new java rule  {% rule java/errorprone/LongLiteralEndingWithLowercaseL %} finds long literals ending with l.
    That helps to avoid confusion between numbers ending with 1 and l. Capital L should be used to define long literals.
*   The new java rule  {% rule java/bestpractices/TypeNameMismatch %} finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule {% rule java/codestyle/CStyleArrayDeclaration %} finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
#### Changed Rules
*   The property `checkNonStaticMethods` of the rule {% rule java/multithreading/NonThreadSafeSingleton %} is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath]({{ baseurl }}pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```

### 🐛️ Fixed Issues
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] False positive in UnusedAssignment when assignment is in conditional statement
* java-codestyle
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
* java-design
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
* java-errorprone
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable fires inconsistently between inline `throw new` and throw-via-local forms
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate is suppressed by using pattern variable
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6747](https://github.com/pmd/pmd/issues/6747): \[java] NonThreadSafeSingleton: False negative with ternary conditional operator
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])
* kotlin
    * [#6795](https://github.com/pmd/pmd/issues/6795): \[kotlin] Add kotlin-type-mapper infrastructure
    * [#6891](https://github.com/pmd/pmd/issues/6891): \[kotlin] AnnotationFqnAnnotator: @<!-- -->TypeName not set on UnescapedAnnotation nodes
    * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add XPath functions and type attributes for type-aware XPath rules
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
    * `KotlinNodeTypeData.getTypeName(KotlinNode)` and `KotlinNodeTypeData.getReturnTypeName(KotlinNode)`
      have been renamed to {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData#getType(KotlinNode) %}
      and {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData#getReturnType(KotlinNode) %}, and
      now return {%jdoc kotlin::lang.kotlin.types.KotlinTypeName %} instead of `String`.
      The corresponding {%jdoc kotlin::lang.kotlin.types.InternalApiBridge %} setters
      `setTypeName`/`setReturnTypeName` were renamed to `setType`/`setReturnType` the same way.
      This is a breaking change to this experimental API.
    * {%jdoc kotlin::lang.kotlin.ast.HasTypeName %}: Marker interface for Kotlin AST nodes that expose
      a `@TypeName` XPath attribute.
    * New XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly`, `pmd-kotlin:hasAnnotation`,
      `pmd-kotlin:modifiers`, `pmd-kotlin:matchesSig`, `pmd-kotlin:isNullable`, `pmd-kotlin:hasUnresolvedReference`
      in package `net.sourceforge.pmd.lang.kotlin.rule.xpath.internal`.
    * New AST attribute view classes in package `net.sourceforge.pmd.lang.kotlin.ast`:
      {%jdoc kotlin::lang.kotlin.ast.KtCatchBlockAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtDelegationSpecifierAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtForStatementAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtFunctionValueParameterAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtKotlinFileAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtPropertyDeclarationAttributes %},
      {%jdoc kotlin::lang.kotlin.ast.KtSingleAnnotationAttributes %}, and
      {%jdoc kotlin::lang.kotlin.ast.KtUnescapedAnnotationAttributes %}.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
