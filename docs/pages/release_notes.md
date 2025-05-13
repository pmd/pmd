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

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* java-performance
  * [#5711](https://github.com/pmd/pmd/issues/5711): [java] UseArraysAsList false positive with Sets

* core
    * [#5700](https://github.com/pmd/pmd/pull/5700): \[core] Don't accidentally catch unexpected runtime exceptions in CpdAnalysis

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

