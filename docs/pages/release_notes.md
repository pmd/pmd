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
* The new Java rule {% rule java/errorprone/WrongTestAnnotation %} detects when test annotations from the wrong
  testing framework (JUnit 4, JUnit Jupiter, or TestNG) are used in your code, preventing tests from being silently
  skipped due to framework mismatches. This helps avoid the silent failure where tests compile but don't execute
  because the test runner doesn't recognize the annotation.
* The new Kotlin rule {% rule kotlin/bestpractices/LocalVariableShadowsParameter %} detects local variable
  declarations that use the same name as a parameter of the enclosing function. This shadows the parameter
  and may lead to confusion about which value is used.

### 🐛️ Fixed Issues
* apex-security
  * [#3877](https://github.com/pmd/pmd/issues/3877): \[apex] ApexCRUDViolation: False positive with Lists of Objects with getSObjectType().getDescribe()
* cpp
  * [#6641](https://github.com/pmd/pmd/issues/6641): \[cpp]: IndexOutOfBoundsException in CPD when a duplication is at end of file with UTF8-BOM
* java-bestpractices
  * [#6692](https://github.com/pmd/pmd/issues/6692): \[java] ForLoopCanBeForeach: inconsistent detection between i += 1 and i = i + 1 update forms
* java-codestyle
  * [#6239](https://github.com/pmd/pmd/issues/6239): \[java] UseDiamondOperator: False positive with Guice TypeLiteral
* java-errorprone
  * [#2846](https://github.com/pmd/pmd/issues/2846): \[java] New Rule: WrongTestAnnotation
  * [#6743](https://github.com/pmd/pmd/issues/6743): \[java] CloseResource: False positive for closeable initialized with (T) null
* kotlin-bestpractices
  * [#6732](https://github.com/pmd/pmd/issues/6732): \[kotlin] New Rule: LocalVariableShadowsParameter

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

