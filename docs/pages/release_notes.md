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
* The new Apex rule {%rule apex/bestpractices/AvoidFutureAnnotation %} finds usages of the `@Future`
  annotation. It is a legacy way to execute asynchronous Apex code. New code should implement
  the `Queueable` interface instead.

### 🐛️ Fixed Issues
* apex-bestpractices
  * [#6203](https://github.com/pmd/pmd/issues/6203): \[apex] New Rule: Avoid Future Annotation

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6204](https://github.com/pmd/pmd/pull/6204): \[apex] Add rule to limit usage of @<!-- -->Future annotation - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6217](https://github.com/pmd/pmd/pull/6217): \[doc] Add Blue Cave to known tools using PMD - [Jude Pereira](https://github.com/judepereira) (@judepereira)
* [#6277](https://github.com/pmd/pmd/pull/6277): \[doc] Add button to copy configuration snippet - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

