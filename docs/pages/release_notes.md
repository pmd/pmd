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

Note: Type data is not yet accessible in XPath rules or the PMD Rule Designer. This will be added in the next version.

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

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

