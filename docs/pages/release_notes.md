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

### 🐛️ Fixed Issues
* chore
  * [#6837](https://github.com/pmd/pmd/issues/6837): chore: Input 'app-id' has been deprecated with message: Use 'client-id' instead
* core
  * [#1995](https://github.com/pmd/pmd/issues/1995): \[core] PMD should display number of rules violated or errors found
* java
  * [#6010](https://github.com/pmd/pmd/issues/6010): \[java] java.lang.OutOfMemoryError: Java heap space when accessing big Jar files with PMD 7
* java-codestyle
  * [#6709](https://github.com/pmd/pmd/issues/6709): \[java] LambdaCanBeMethodReference: False positive with array creation containing constructor call in receiver
* java-errorprone
  * [#6826](https://github.com/pmd/pmd/issues/6826): \[java] AssertEqualsArgumentOrder: False positive for double assertEquals

### 🚨️ API Changes

#### Deprecations
*   core
  * {%jdoc !!core::lang.JvmLanguagePropertyBundle.setClassLoader(ClassLoader) %} and
    {%jdoc !!core::lang.JvmLanguagePropertyBundle.getAnalysisClassLoader() %} are deprecated. Use the language property
    {%jdoc !!core::lang.JvmLanguagePropertyBundle#AUX_CLASSPATH %} instead via `getProperty()` and `setProperty()`. This language property
    is now set correctly when providing the auxClasspath via CLI parameter `--aux-classpath`.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

