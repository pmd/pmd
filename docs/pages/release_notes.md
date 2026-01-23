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

#### Changed Rules
The following rules have been changed to use a consistent implementation of enum based
rule properties:
* The property `checkAddressTypes` of rule {%rule java/bestpractices/AvoidUsingHardCodedIP %} has changed:
  * Instead of `IPv4` use `ipv4`
  * Instead of `IPv6` use `ipv6`
  * Instead of `IPv4 mapped IPv6` use `ipv4MappedIpv6`
  * The old values still work, but you'll see a deprecation warning.
* The property `nullCheckBranch` of rule {%rule java/codestyle/ConfusingTernary %} has changed:
  * Instead of `Any` use `any`
  * Instead of `Then` use `then`
  * Instead of `Else` use `else`
  * The old values still work, but you'll see a deprecation warning.
* The property `typeAnnotations` of rule {%rule java/codestyle/ModifierOrder %} has changed:
  * Instead of `ontype` use `onType`
  * Instead of `ondecl` use `onDecl`
  * The old values still work, but you'll see a deprecation warning.
* The values of the properties of rule {%rule java/documentation/CommentRequired %} have changed:
  * Instead of `Required` use `required`
  * Instead of `Ignored` use `ignored`
  * Instead of `Unwanted` use `unwanted`
  * The old values still work, but you'll see a deprecation warning.

### 🐛️ Fixed Issues
* core
  * [#6184](https://github.com/pmd/pmd/issues/6184): \[core] Consistent implementation of enum properties
* apex-codestyle
  * [#6349](https://github.com/pmd/pmd/issues/6349): \[apex] FieldDeclarationsShouldBeAtStart: False positive with properties
* java-errorprone
  * [#5882](https://github.com/pmd/pmd/issues/5882): \[java] UnconditionalIfStatement: False negative when true/false is not literal but local variable
* java-performance
  * [#3857](https://github.com/pmd/pmd/issues/3857): \[java] InsufficientStringBufferDeclaration: False negatives with String constants

### 🚨️ API Changes

#### Deprecations
* core
  * {%jdoc !!core::lang.metrics.MetricOption#valueName() %}: When metrics are used for (rule) properties,
    then the conventional enum mapping (from SCREAMING_SNAKE_CASE to camelCase) will be used for the enum values.
    See {%jdoc core::properties.PropertyFactory#conventionalEnumListProperty(java.lang.String, java.lang.Class) %}.
  * In {%jdoc core::properties.PropertyFactory %}:
    * {%jdoc !a!core::properties.PropertyFactory#enumProperty(java.lang.String, java.util.Map) %}. Use
      {%jdoc core::properties.PropertyFactory#conventionalEnumProperty(java.lang.String, java.lang.Class) %} instead.
    * {%jdoc !a!core::properties.PropertyFactory#enumProperty(java.lang.String, java.lang.Class) %}. Use
      {%jdoc core::properties.PropertyFactory#conventionalEnumProperty(java.lang.String, java.lang.Class) %} instead.
    * {%jdoc !a!core::properties.PropertyFactory#enumProperty(java.lang.String, java.lang.Class, java.util.function.Function) %}. Use
      {%jdoc core::properties.PropertyFactory#conventionalEnumProperty(java.lang.String, java.lang.Class) %} instead.
    * {%jdoc !a!core::properties.PropertyFactory#enumListProperty(java.lang.String, java.util.Map) %}. Use
      {%jdoc core::properties.PropertyFactory#conventionalEnumListProperty(java.lang.String, java.lang.Class) %} instead.
    * {%jdoc !a!core::properties.PropertyFactory#enumListProperty(java.lang.String, java.lang.Class, java.util.function.Function) %}. Use
      {%jdoc core::properties.PropertyFactory#conventionalEnumListProperty(java.lang.String, java.lang.Class) %} instead.
* java
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_FOR %}. This constant should
    have never been public.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_DO %}. This constant should
    have never been public.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_WHILE %}. This constant should
    have never been public.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_BREAK_LOOP_TYPES %}. This property
    descriptor should have been private. It won't be used anymore. Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptor(java.lang.String) %}
    on the rule to retrieve the property descriptor.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_CONTINUE_LOOP_TYPES %}. This property
    descriptor should have been private. It won't be used anymore. Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptor(java.lang.String) %}
    on the rule to retrieve the property descriptor.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#CHECK_RETURN_LOOP_TYPES %}. This property
    descriptor should have been private. It won't be used anymore. Use {%jdoc core::properties.AbstractPropertySource#getPropertyDescriptor(java.lang.String) %}
    on the rule to retrieve the property descriptor.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#check(core::properties.PropertyDescriptor, core::lang.ast.Node, java.lang.Object) %}.
    This method should have been private and will be internalized.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#hasPropertyValue(core::properties.PropertyDescriptor, java.lang.String) %}.
    This method should have been private and will be internalized.
  * {%jdoc !c!java::lang.java.rule.errorprone.AvoidBranchingStatementAsLastInLoopRule#checksNothing() %}.
    This method should have been private and will be internalized.
  * {%jdoc !!java::lang.java.metrics.JavaMetrics.ClassFanOutOption#valueName() %},
    {%jdoc !!java::lang.java.metrics.JavaMetrics.CycloOption#valueName() %},
    {%jdoc !!java::lang.java.metrics.JavaMetrics.NcssOption#valueName() %}
* lang-test
  * {%jdoc !c!lang-test::lang.test.AbstractMetricTestRule#optionMappings() %}. No extra mapping is required anymore.
    The {%jdoc core::lang.metrics.MetricOption %} enum values are used. See 
    {%jdoc !a!lang-test::lang.test.AbstractMetricTestRule#AbstractMetricTestRule(core::lang.metrics.Metric, java.lang.Class) %}
    to provide the enum at construction time.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

