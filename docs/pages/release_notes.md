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
* apex-security
  * [#5788](https://github.com/pmd/pmd/issues/5788): \[apex] ApexCRUDViolation unable to detect insecure SOQL if it is a direct input argument
* doc
  * [#5790](https://github.com/pmd/pmd/issues/5790): \[doc] Website rule reference pages are returning 404
* java-bestpractices
  * [#5793](https://github.com/pmd/pmd/issues/5793): \[java] NonExhaustiveSwitch fails on exhaustive switch with sealed class
* java-codestyle
  * [#1639](https://github.com/pmd/pmd/issues/1639): \[java] UnnecessaryImport false positive for multiline @<!-- -->link Javadoc
  * [#2304](https://github.com/pmd/pmd/issues/2304): \[java] UnnecessaryImport false positive for on-demand imports in JavaDoc
  * [#5832](https://github.com/pmd/pmd/issues/5832): \[java] UnnecessaryImport false positive for multiline @<!-- -->see Javadoc

### 🚨 API Changes

#### Rule Test Schema
The rule test schema has been extended to support verifying suppressed violations.
See [Testing your rules]({{ baseurl }}pmd_userdocs_extending_testing.html) for more information.

Also note, the schema [rule-tests.xsd](https://github.com/pmd/pmd/blob/main/pmd-test-schema/src/main/resources/net/sourceforge/pmd/test/schema/rule-tests_1_1_0.xsd)
is now only in the module "pmd-test-schema". It has been removed from the old location from module "pmd-test".

### 💵 Financial Contributions

Many thanks to our sponsors:

* [Cybozu](https://github.com/cybozu) (@cybozu)

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5738](https://github.com/pmd/pmd/pull/5738): chore: Remove unused private methods in test classes - [Pankraz76](https://github.com/Pankraz76) (@Pankraz76)
* [#5791](https://github.com/pmd/pmd/pull/5791): \[doc] Add a simple check whether generate rule doc pages exist - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5806](https://github.com/pmd/pmd/pull/5806): \[test] Verify suppressed violations in rule tests - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5818](https://github.com/pmd/pmd/pull/5818): Fix #2304: \[java] UnnecessaryImport FP for on-demand imports in JavaDoc - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#5821](https://github.com/pmd/pmd/pull/5821): \[apex] New Rule: Avoid boolean method parameters - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5851](https://github.com/pmd/pmd/pull/5851): chore: \[java] ReplaceHashtableWithMap: Fix name of test - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

