---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### 💥 pmd-compat6 removed (breaking)

The already deprecated PMD 6 compatibility module (pmd-compat6) has been removed. It was intended to be used with
older versions of the maven-pmd-plugin, but since maven-pmd-plugin 3.22.0, PMD 7 is supported directly and this
module is not needed anymore.

If you currently use this dependency (`net.sourceforge.pmd:pmd-compat6`), remove it and upgrade maven-pmd-plugin
to the latest version (3.23.0 or newer).

See also [Maven PMD Plugin]({{ baseurl }}pmd_userdocs_tools_maven.html).

### 🐛 Fixed Issues
* apex
  * [#5053](https://github.com/pmd/pmd/issues/5053): \[apex] CPD fails to parse string literals with escaped characters
* java-bestpractices
  * [#5047](https://github.com/pmd/pmd/issues/5047): \[java] UnusedPrivateMethod FP for Generics & Overloads
* plsql
  * [#1934](https://github.com/pmd/pmd/issues/1934): \[plsql] ParseException with MERGE statement in anonymous block
  * [#2779](https://github.com/pmd/pmd/issues/2779): \[plsql] Error while parsing statement with (Oracle) DML Error Logging

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

