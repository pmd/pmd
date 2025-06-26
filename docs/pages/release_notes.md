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

### 🚨 API Changes

#### Rule Test Schema
The rule test schema has been extended to support verifying suppressed violations.
See [Testing your rules]({{ baseurl }}pmd_userdocs_extending_testing.html) for more information.

Also note, the schema [rule-tests.xsd](https://github.com/pmd/pmd/blob/main/pmd-test-schema/src/main/resources/net/sourceforge/pmd/test/schema/rule-tests_1_1_0.xsd)
is now only in the module "pmd-test-schema". It has been removed from the old location from module "pmd-test".

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5806](https://github.com/pmd/pmd/pull/5806): \[test] Verify suppressed violations in rule tests - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

