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
* java-bestpractices
    * [#6188](https://github.com/pmd/pmd/issues/6188): \[java] UnitTestShouldIncludeAssert false positive when TestNG @<!-- -->Test.expectedException present
* java-errorprone
    * [#6072](https://github.com/pmd/pmd/issues/6072): \[java] OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes
    * [#6092](https://github.com/pmd/pmd/issues/6092): \[java] AssignmentInOperand false positive in 7.17.0 for case blocks in switch statements
    * [#6096](https://github.com/pmd/pmd/issues/6096): \[java] OverrideBothEqualsAndHashCodeOnComparable on class with lombok.EqualsAndHashCode annotation
    * [#6199](https://github.com/pmd/pmd/issues/6199): \[java] AssignmentInOperand: description of property allowIncrementDecrement is unclear

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6081](https://github.com/pmd/pmd/pull/6081): \[java] Fix #6072: OverrideBothEqualsAndHashCodeOnComparable should not be required for record classes - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#6201](https://github.com/pmd/pmd/pull/6201): \[java] Fix #6199: AssignmentInOperandRule: Update description of allowIncrementDecrement property - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6202](https://github.com/pmd/pmd/pull/6202): \[java] Fix #6188: UnitTestsShouldIncludeAssert - FP when TestNG @<!-- -->Test.expectedException is present - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6204](https://github.com/pmd/pmd/pull/6204): \[apex] Add rule to limit usage of @<!-- -->Future annotation - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6217](https://github.com/pmd/pmd/pull/6217): \[doc] Add Blue Cave to known tools using PMD - [Jude Pereira](https://github.com/judepereira) (@judepereira)
* [#6238](https://github.com/pmd/pmd/pull/6238): \[java] Fix #6096: Detect Lombok generated equals/hashCode in Comparable - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6251](https://github.com/pmd/pmd/pull/6251): \[java] Fix #6092: AssignmentInOperand false positive in 7.17.0 for case statements - [Marcel](https://github.com/mrclmh) (@mrclmh)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

