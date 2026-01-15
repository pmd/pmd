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

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule {% rule java/errorprone/UnsupportedJdkApiUsage %} flags the use of unsupported and non-portable
  JDK APIs, including `sun.*` packages, `sun.misc.Unsafe`, and `jdk.internal.misc.Unsafe`. These APIs are unstable,
  intended for internal use, and may change or be removed. The rule complements Java compiler warnings by
  highlighting such usage during code reviews and encouraging migration to official APIs like VarHandle and
  the Foreign Function & Memory API.
#### Deprecated Rules
* The Java rule {% rule java/errorprone/DontImportSun %} has been deprecated. It is replaced by
  {% rule java/errorprone/UnsupportedJdkApiUsage %}.

### 🐛️ Fixed Issues
* java
  * [#5923](https://github.com/pmd/pmd/issues/5923): \[java] New rule: Catch usages of sun.misc.Unsafe or jdk.internal.misc.Unsafe

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

