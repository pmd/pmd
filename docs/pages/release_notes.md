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
* doc
  * [#6396](https://github.com/pmd/pmd/pull/6396): \[doc] Mention test-pmd-tool as alternative for testing

### 🚨️ API Changes

#### Deprecations
* core
  * {%jdoc core::renderers.CodeClimateIssue %}: This class is an implementation detail of
    {%jdoc core::renderers.CodeClimateRenderer %}. It will be internalized in a future release.
* visualforce
  * {%jdoc visualforce::lang.visualforce.DataType %}. The enum constants have been renamed to follow Java naming
    conventions. The old enum constants are deprecated and should no longer be used.  
    The method {%jdoc !!visualforce::lang.visualforce.DataType#fromString(java.lang.String) %} will return the new
    enum constants.  
    Use {%jdoc !!visualforce::lang.visualforce.DataType#fieldTypeNameOf() %} to get the original field type name.

#### Deprecated API
* The methods {% jdoc core::PMDConfiguration#getClassLoader() %} and
 {% jdoc core::PMDConfiguration#setClassLoader(java.lang.ClassLoader) %}
 have been deprecated for removal. Use the new method {% jdoc core::PMDConfiguration#setAnalysisClasspath(core::util.PmdClasspathConfig) %}
 instead. It is still possible to use a custom ClassLoader to find resources
 during PMD analysis, however, a custom ClassLoader will not be closed by PMD.
 The new API makes that clearer.
  - A similar change has been done to {% jdoc core::lang.JvmLanguagePropertyBundle#getAnalysisClassLoader() %} and
  its setter.
* The methods {% jdoc java::lang.java.types.TypeSystem#usingClasspath(net.sourceforge.pmd.lang.java.symbols.internal.asm.Classpath) %}
  and {% jdoc java::lang.java.types.TypeSystem#usingClassLoaderClasspath(java.lang.ClassLoader) %} have been deprecated.
  See Javadoc for the replacement.


### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

