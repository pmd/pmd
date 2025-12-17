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

#### CPD performance improvements

CPD has been refactored internally to be more efficient. It now uses less
memory (as much as 10 times less on some benchmarks), and can process source
files in parallel for much faster results. CPD now supports the `--threads`
(`-t`) option. The default is `1C`, meaning 1 thread per core.

Note: if you have written you own CpdLexer implementations:
- Make sure they are thread-safe, as they can now be used in a threaded context.
- You are advised to use the new overloads of {% jdoc core::cpd.TokenFactory#recordToken(java.lang.String,int,int) %}.
  These are more memory-efficient as the node coordinates require only two ints
  to be saved instead of four.

### 🐛️ Fixed Issues

- java-bestpractices
  - [#6257](https://github.com/pmd/pmd/issues/6257): \[java] UnusedLocalVariable: False positive with instanceof pattern guard

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

