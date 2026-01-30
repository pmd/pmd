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

#### Build Requirement is Java 21
From now on, Java 21 or newer is required to build PMD. PMD itself still remains compatible with Java 8,
so that it still can be used in a pure Java 8 environment. This allows us to use the latest
checkstyle version during the build.

#### CPD
* The Apex module now supports [suppression](https://docs.pmd-code.org/latest/pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs. See [#6417](https://github.com/pmd/pmd/pull/6417)

### 🌟️ New and Changed Rules
#### New Rules
* The new Java rule {% rule java/design/PublicMemberInNonPublicType %} detects public members (such as methods
  or fields) within non-public types. Non-public types should not declare public members, as their effective
  visibility is limited, and using the `public` modifier can create confusion.
* The new Java rule {% rule java/errorprone/UnsupportedJdkApiUsage %} flags the use of unsupported and non-portable
  JDK APIs, including `sun.*` packages, `sun.misc.Unsafe`, and `jdk.internal.misc.Unsafe`. These APIs are unstable,
  intended for internal use, and may change or be removed. The rule complements Java compiler warnings by
  highlighting such usage during code reviews and encouraging migration to official APIs like VarHandle and
  the Foreign Function & Memory API.

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

#### Deprecated Rules
* The Java rule {% rule java/errorprone/DontImportSun %} has been deprecated. It is replaced by
  {% rule java/errorprone/UnsupportedJdkApiUsage %}.

### 🐛️ Fixed Issues
* core
  * [#6184](https://github.com/pmd/pmd/issues/6184): \[core] Consistent implementation of enum properties
* apex
  * [#6417](https://github.com/pmd/pmd/issues/6417): \[apex] Support CPD suppression with "CPD-OFF" & "CPD-ON"
* apex-codestyle
  * [#6349](https://github.com/pmd/pmd/issues/6349): \[apex] FieldDeclarationsShouldBeAtStart: False positive with properties
* cli
  * [#6290](https://github.com/pmd/pmd/issues/6290): \[cli] Improve Designer start script
* java
  * [#5871](https://github.com/pmd/pmd/issues/5871): \[java] Support Java 26
  * [#6364](https://github.com/pmd/pmd/issues/6364): \[java] Parse error with yield lambda inside switch
* java-design
  * [#6231](https://github.com/pmd/pmd/issues/6231): \[java] New Rule: PublicMemberInNonPublicType
* java-errorprone
  * [#3601](https://github.com/pmd/pmd/issues/3601): \[java] InvalidLogMessageFormat: False positive when final parameter is Supplier&lt;Throwable&gt;
  * [#5882](https://github.com/pmd/pmd/issues/5882): \[java] UnconditionalIfStatement: False negative when true/false is not literal but local variable
  * [#5923](https://github.com/pmd/pmd/issues/5923): \[java] New Rule: Catch usages of sun.misc.Unsafe or jdk.internal.misc.Unsafe
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
* [#6231](https://github.com/pmd/pmd/pull/6231): \[java] New Rule: PublicMemberInNonPublicType - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6232](https://github.com/pmd/pmd/pull/6232): \[java] New Rule: UnsupportedJdkApiUsage - [Thomas Leplus](https://github.com/thomasleplus) (@thomasleplus)
* [#6233](https://github.com/pmd/pmd/pull/6233): \[core] Fix #6184: More consistent enum properties - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6290](https://github.com/pmd/pmd/pull/6290): \[cli] Improve Designer start script - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6315](https://github.com/pmd/pmd/pull/6315): \[java] Fix #5882: UnconditionalIfStatement false-negative if true/false is not literal - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6362](https://github.com/pmd/pmd/pull/6362): chore: Fix typos - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6366](https://github.com/pmd/pmd/pull/6366): \[java] Fix #3857: InsufficientStringBufferDeclaration should consider constant Strings - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6373](https://github.com/pmd/pmd/pull/6373): \[java] Support Java 26 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6377](https://github.com/pmd/pmd/pull/6377): \[doc] chore: update last_updated - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6384](https://github.com/pmd/pmd/pull/6384): chore: helper script check-all-contributors.sh - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6386](https://github.com/pmd/pmd/pull/6386): \[core] chore: Bump minimum Java version required for building to 21 - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6387](https://github.com/pmd/pmd/pull/6387): \[ci] publish-pull-requests: download latest build result - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6389](https://github.com/pmd/pmd/pull/6389): chore: update javadoc deprecated tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6390](https://github.com/pmd/pmd/pull/6390): chore: update javadoc experimental tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6391](https://github.com/pmd/pmd/pull/6391): chore: update javadoc internal API tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6392](https://github.com/pmd/pmd/pull/6392): \[doc] ADR 3: Clarify javadoc tags - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6394](https://github.com/pmd/pmd/pull/6394): \[apex] Fix #6349: FieldDeclarationsShouldBeAtStart false positive with properties - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6407](https://github.com/pmd/pmd/pull/6407): \[java]  Fix #3601: InvalidLogMessageFormat: False positive when final parameter is Supplier<Throwable>  - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6417](https://github.com/pmd/pmd/pull/6417): \[apex] Support CPD suppression with "CPD-OFF" & "CPD-ON" - [Jade](https://github.com/goto-dev-null) (@goto-dev-null)
* [#6428](https://github.com/pmd/pmd/pull/6428): \[ci] chore: run extensive integration tests under linux only - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6429](https://github.com/pmd/pmd/pull/6429): \[doc] chore: add keywords for auxclasspath in Java documentation - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#6430](https://github.com/pmd/pmd/pull/6430): \[java] Fix #6364: Parse error with yield lambda - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6367](https://github.com/pmd/pmd/pull/6367): Bump PMD from 7.19.0 to 7.20.0
* [#6369](https://github.com/pmd/pmd/pull/6369): chore(deps): bump ruby/setup-ruby from 1.275.0 to 1.277.0
* [#6370](https://github.com/pmd/pmd/pull/6370): chore(deps): bump org.apache.groovy:groovy from 5.0.2 to 5.0.3
* [#6371](https://github.com/pmd/pmd/pull/6371): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.2 to 1.18.3
* [#6372](https://github.com/pmd/pmd/pull/6372): chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.6.2 to 3.6.3
* [#6375](https://github.com/pmd/pmd/pull/6375): chore: Bump maven from 3.9.11 to 3.9.12
* [#6378](https://github.com/pmd/pmd/pull/6378): chore(deps): bump ruby/setup-ruby from 1.277.0 to 1.279.0
* [#6379](https://github.com/pmd/pmd/pull/6379): chore(deps): bump scalameta.version from 4.14.2 to 4.14.4
* [#6380](https://github.com/pmd/pmd/pull/6380): chore(deps): bump junit.version from 6.0.1 to 6.0.2
* [#6381](https://github.com/pmd/pmd/pull/6381): chore(deps): bump org.jsoup:jsoup from 1.21.2 to 1.22.1
* [#6382](https://github.com/pmd/pmd/pull/6382): chore(deps): bump org.checkerframework:checker-qual from 3.52.1 to 3.53.0
* [#6383](https://github.com/pmd/pmd/pull/6383): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.3.0 to 13.0.0
* [#6385](https://github.com/pmd/pmd/pull/6385): chore(deps): bump uri from 1.0.3 to 1.0.4 in /docs
* [#6399](https://github.com/pmd/pmd/pull/6399): chore(deps): bump ruby/setup-ruby from 1.279.0 to 1.282.0
* [#6400](https://github.com/pmd/pmd/pull/6400): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.25.1 to 0.25.4
* [#6401](https://github.com/pmd/pmd/pull/6401): chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.9.0 to 0.10.0
* [#6403](https://github.com/pmd/pmd/pull/6403): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.2 to 4.33.4
* [#6410](https://github.com/pmd/pmd/pull/6410): chore(deps): bump ruby/setup-ruby from 1.282.0 to 1.285.0
* [#6411](https://github.com/pmd/pmd/pull/6411): chore(deps): bump actions/cache from 5.0.1 to 5.0.2
* [#6412](https://github.com/pmd/pmd/pull/6412): chore(deps): bump scalameta.version from 4.14.4 to 4.14.5
* [#6413](https://github.com/pmd/pmd/pull/6413): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.3 to 1.18.4
* [#6414](https://github.com/pmd/pmd/pull/6414): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.20.1 to 2.21.0
* [#6415](https://github.com/pmd/pmd/pull/6415): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.3 to 1.18.4
* [#6419](https://github.com/pmd/pmd/pull/6419): chore(deps-dev): bump lodash from 4.17.21 to 4.17.23
* [#6421](https://github.com/pmd/pmd/pull/6421): chore(deps): bump actions/setup-java from 5.1.0 to 5.2.0
* [#6422](https://github.com/pmd/pmd/pull/6422): chore(deps): bump actions/checkout from 6.0.1 to 6.0.2
* [#6423](https://github.com/pmd/pmd/pull/6423): chore(deps): bump scalameta.version from 4.14.5 to 4.14.6
* [#6424](https://github.com/pmd/pmd/pull/6424): chore(deps-dev): bump org.assertj:assertj-core from 3.27.6 to 3.27.7
* [#6425](https://github.com/pmd/pmd/pull/6425): chore(deps): bump org.apache.groovy:groovy from 5.0.3 to 5.0.4

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 146 commits
* 30 closed tickets & PRs
* Days since last release: 30

{% endtocmaker %}
