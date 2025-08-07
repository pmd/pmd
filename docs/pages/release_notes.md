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
* The new apex rule {% rule apex/codestyle/AnnotationsNamingConventions %} enforces that annotations
  are used consistently in PascalCase.  
  The rule is referenced in the quickstart.xml ruleset for Apex.
* The new java rule {% rule java/codestyle/TypeParameterNamingConventions %} replaces the now deprecated rule
  GenericsNaming. The new rule is configurable and checks for naming conventions of type parameters in
  generic types and methods. It can be configured via a regular expression.  
  By default, this rule uses the standard Java naming convention (single uppercase letter).  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule {% rule java/errorprone/OverrideBothEqualsAndHashCodeOnComparable %} finds missing
  `hashCode()` and/or `equals()` methods on types that implement `Comparable`. This is important if
  instances of these classes are used in collections. Failing to do so can lead to unexpected behavior in sets
  which then do not conform to the `Set` interface. While the `Set` interface relies on
  `equals()` to determine object equality, sorted sets like `TreeSet` use
  `compareTo()` instead. The same issue can arise when such objects are used
  as keys in sorted maps.  
  This rule is very similar to {% rule java/errorprone/OverrideBothEqualsAndHashcode %} which has always been
  skipping `Comparable` and only reports if one of the two methods is missing. The new rule will also report,
  if both methods (hashCode and equals) are missing.  
  The rule is referenced in the quickstart.xml ruleset for Java.
* The new java rule {% rule java/errorprone/UselessPureMethodCall %} finds method calls of pure methods
  whose result is not used. Ignoring the result of such method calls is likely as mistake as pure
  methods are side effect free.  
  The rule is referenced in the quickstart.xml ruleset for Java.

#### Deprecated Rules
* The java rule {% rule java/codestyle/GenericsNaming %} has been deprecated for removal in favor
  of the new rule {% rule java/codestyle/TypeParameterNamingConventions %}.
* The java rule {% rule java/errorprone/AvoidLosingExceptionInformation %} has been deprecated for removal
  in favor of the new rule {% rule java/errorprone/UselessPureMethodCall %}.
* The java rule {% rule java/errorprone/UselessOperationOnImmutable %} has been deprecated for removal
  in favor of the new rule {% rule java/errorprone/UselessPureMethodCall %}.

### 🐛 Fixed Issues
* apex-codestyle
  * [#5650](https://github.com/pmd/pmd/issues/5650): \[apex] New Rule: AnnotationsNamingConventions
* java
  * [#5874](https://github.com/pmd/pmd/issues/5874): \[java] Update java regression tests with Java 25 language features
* java-codestyle
  * [#972](https://github.com/pmd/pmd/issues/972): \[java] Improve naming conventions rules
* java-errorprone
  * [#5837](https://github.com/pmd/pmd/issues/5837): \[java] New Rule OverrideBothEqualsAndHashCodeOnComparable
  * [#5881](https://github.com/pmd/pmd/issues/5881): \[java] AvoidLosingExceptionInformation does not trigger when inside if-else
  * [#5915](https://github.com/pmd/pmd/issues/5915): \[java] AssignmentInOperand not raised when inside do-while loop

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5822](https://github.com/pmd/pmd/pull/5822): Fix #5650: \[apex] New Rule: AnnotationsNamingConventions - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#5856](https://github.com/pmd/pmd/pull/5856): Fix #5837: \[java] New Rule OverrideBothEqualsAndHashCodeOnComparable - [Vincent Potucek](https://github.com/Pankraz76) (@Pankraz76)
* [#5907](https://github.com/pmd/pmd/pull/5907): \[java] New rule: UselessPureMethodCall - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5922](https://github.com/pmd/pmd/pull/5922): Fix #972: \[java] Add a new rule TypeParameterNamingConventions - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5924](https://github.com/pmd/pmd/pull/5924): Fix #5915: \[java] Fix AssignmentInOperandRule to also work an do-while loops and switch statements - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5932](https://github.com/pmd/pmd/pull/5932): \[ci] Reuse GitHub Pre-Releases - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5965](https://github.com/pmd/pmd/pull/5965): Fix #5881: AvoidLosingException - Consider nested method calls - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

