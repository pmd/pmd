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

#### ✨ New Rules

* The new Apex rule {% rule apex/design/AvoidBooleanMethodParameters %} finds methods that take a
  boolean parameter. This can make method calls difficult to understand and maintain as the method is clearly
  doing two things.

### 🐛 Fixed Issues
* apex-design
  * [#5427](https://github.com/pmd/pmd/issues/5427): \[apex] New Rule: Avoid Boolean Method Parameters

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5738](https://github.com/pmd/pmd/pull/5738): chore: Remove unused private methods in test classes - [Pankraz76](https://github.com/Pankraz76) (@Pankraz76)
* [#5821](https://github.com/pmd/pmd/pull/5821): \[apex] New Rule: Avoid boolean method parameters - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

