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
* java-bestpractices
  * [#5514](https://github.com/pmd/pmd/issues/5514): \[java] ExhaustiveSwitchHasDefault fails for non-exhaustive switch statements
  * [#5670](https://github.com/pmd/pmd/issues/5670): \[java] ExhaustiveSwitchHasDefault issue with final fields not initialized in constructor
* java-codestyle
  * [#6709](https://github.com/pmd/pmd/issues/6709): \[java] LambdaCanBeMethodReference: False positive with array creation containing constructor call in receiver
* java-design
  * [#6844](https://github.com/pmd/pmd/issues/6844): \[java] AvoidThrowingNewInstanceOfSameException: message inconsistent with logic
  * [#6881](https://github.com/pmd/pmd/issues/6881): \[java] CognitiveComplexity does not count switch expressions
* java-errorprone
  * [#6826](https://github.com/pmd/pmd/issues/6826): \[java] AssertEqualsArgumentOrder: False positive for double assertEquals

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

