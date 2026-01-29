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

#### 🚀️ New: Java 26 Support
This release of PMD brings support for Java 26.

There are no new standard language features.

There is one preview language feature:
* [JEP 530: Primitive Types in Patterns, instanceof, and switch (Fourth Preview)](https://openjdk.org/jeps/530)

In order to analyze a project with PMD that uses these preview language features,
you'll need to select the new language version `26-preview`:

    pmd check --use-version java-26-preview ...

Note: Support for Java 24 preview language features have been removed. The version "24-preview"
is no longer available.

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

#### Build Requirement is Java 21
From now on, Java 21 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

### 🐛️ Fixed Issues
* core
  * [#6184](https://github.com/pmd/pmd/issues/6184): \[core] Consistent implementation of enum properties
* apex-codestyle
  * [#6349](https://github.com/pmd/pmd/issues/6349): \[apex] FieldDeclarationsShouldBeAtStart: False positive with properties
* cli
  * [#6290](https://github.com/pmd/pmd/issues/6290): \[cli] Improve Designer start script
* java
  * [#5871](https://github.com/pmd/pmd/issues/5871): \[java] Support Java 26
* java-errorprone
  * [#5882](https://github.com/pmd/pmd/issues/5882): \[java] UnconditionalIfStatement: False negative when true/false is not literal but local variable
* java-performance
  * [#3857](https://github.com/pmd/pmd/issues/3857): \[java] InsufficientStringBufferDeclaration: False negatives with String constants

### 🚨️ API Changes
{% jdoc_nspace :coreast core::lang.ast %}


### New reporting API

New APIs have been introduced to report violations in rules written in Java.
They  use a 2-stage builder pattern to simplify the API and extend its
functionality. Example usages:
```java
ctx.at(node).report(); // report with default message
ctx.at(node).reportWithArgs("arg", 2); // report with default message and format arguments
ctx.at(node).reportWithMessage("message"); // report with non-default message
ctx.at(node).reportWithMessage("message", "arg", 2); // report with non-default message and format arguments
```
Use those new forms instead of the deprecated equivalent forms:
```java
ctx.addViolation(node);
ctx.addViolation(node, "arg", 2);
ctx.addViolationWithMessage(node, "message");
ctx.addViolationWithMessage(node, "message", "arg", 2);
```

The new API allows more flexibility about where violations are reported.
For instance, you can easily report on a specific token within a node:
```java
ctx.at(node.atToken(node.getFirstToken())).report();
```
The previous way to do this required using complex overloads.

See {% jdoc core::reporting.RuleContext#at(core::reporting.Reportable) %} for more information.

### New Experimental APIs

* core
    * {% jdoc coreast::Node#atLocation(core::lang.document.FileLocation) %}
    * {% jdoc coreast::Node#atToken(coreast::GenericToken) %}
    * {% jdoc core::lang.rule.impl.CannotBeSuppressed %}

### Removed Experimental APIs

* core
    * {% jdoc_old core::reporting.RuleContext#addViolationWithPosition(coreast::Node,coreast::impl.javacc.JavaccToken,java.lang.String,java.lang.Object...) %} (introduced in 7.17.0)
    * {% jdoc_old core::reporting.RuleContext#addViolationWithPosition(coreast::reporting.Reportable,coreast::AstInfo,core::lang.document.FileLocation,java.lang.String,java.lang.Object...) %} (introduced in 7.9.0)
    * {% jdoc_old core::reporting.RuleContext#addViolationNoSuppress(coreast::reporting.Reportable,coreast::AstInfo,java.lang.String,java.lang.Object...) %} (introduced in 7.14.0)


### Deprecated APIs

* core
    * {% jdoc core::reporting.RuleContext#addViolationWithPosition(coreast::Node,int,int,java.lang.String,java.lang.Object...) %}

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

