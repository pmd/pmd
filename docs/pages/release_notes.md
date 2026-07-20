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
#### Kotlin type-aware analysis
Kotlin now supports type-aware analysis via the `auxClasspath` language property (see [#6677](https://github.com/pmd/pmd/issues/6677)).
Resolved type names, return types, and annotation FQNs are available through
{%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %} for use in Java-based rules.

#### Kotlin XPath functions and type attributes
Type data is now accessible in XPath rules via new attributes and helper functions (see [Kotlin XPath rule support](pmd_languages_kotlin.html#xpath-rule-support)):

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
#### Renamed Rules
* The rule {%rule java/design/InstantiableUtilityClass %} (Java Design) was renamed from `UseUtilityClass` to better reflect the problem.
  The old name still works but is deprecated.

### 🐛️ Fixed Issues
* chore
    * [#6837](https://github.com/pmd/pmd/issues/6837): chore: Input 'app-id' has been deprecated with message: Use 'client-id' instead
* core
    * [#1995](https://github.com/pmd/pmd/issues/1995): \[core] PMD should display number of rules violated or errors found
    * [#4952](https://github.com/pmd/pmd/issues/4952): \[doc] Improve doc around PMDConfiguration#prependAuxclasspath #setClassloader
    * [#4953](https://github.com/pmd/pmd/issues/4953): \[core] Deprecate PMDConfiguration#setClassloader and #getClassloader
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
    * [#6677](https://github.com/pmd/pmd/issues/6677): \[kotlin] Add XPath functions and type attributes for type-aware XPath rules

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

#### Experimental API
* kotlin
    * {%jdoc kotlin::lang.kotlin.types.KotlinNodeTypeData %}: Provides the initial API to access type information
      on Kotlin AST nodes. It's part of the new Kotlin type-aware analysis.
    * {%jdoc kotlin::lang.kotlin.ast.HasTypeName %}: Marker interface for Kotlin AST nodes that expose
      a `@TypeName` XPath attribute.
    * New XPath functions `pmd-kotlin:typeIs`, `pmd-kotlin:typeIsExactly`, `pmd-kotlin:hasAnnotation`,
      `pmd-kotlin:modifiers`, `pmd-kotlin:matchesSig`, `pmd-kotlin:isNullable`, `pmd-kotlin:hasUnresolvedReference`
      in package `net.sourceforge.pmd.lang.kotlin.rule.xpath.internal`.
    * New AST attribute view classes in package `net.sourceforge.pmd.lang.kotlin.ast` (e.g.
      `KtFunctionDeclarationAttributes`, `KtPropertyDeclarationAttributes`, etc.).

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

