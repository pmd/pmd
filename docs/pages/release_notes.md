---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🌟 Rule Changes

* {%rule java/bestpractices/JUnitTestsShouldIncludeAssert %} and {% rule java/bestpractices/JUnitTestContainsTooManyAsserts %}
  have a new property named `extraAssertMethodNames`. With this property, you can configure which additional static
  methods should be considered as valid verification methods. This allows to use custom mocking or assertion libraries.

### 🐛 Fixed Issues
* cli
  * [#4791](https://github.com/pmd/pmd/issues/4791): \[cli] Could not find or load main class
* apex-errorprone
  * [#3953](https://github.com/pmd/pmd/issues/3953): \[apex] EmptyCatchBlock false positive with formal (doc) comments
* java-bestpractices
  * [#1084](https://github.com/pmd/pmd/issues/1084): \[java] Allow JUnitTestsShouldIncludeAssert to configure verification methods
  * [#4435](https://github.com/pmd/pmd/issues/4435): \[java] \[7.0-rc1] UnusedAssignment for used field
  * [#4569](https://github.com/pmd/pmd/issues/4569): \[java] ForLoopCanBeForeach reports on loop `for (int i = 0; i < list.size(); i += 2)`
  * [#4618](https://github.com/pmd/pmd/issues/4618): \[java] UnusedAssignment false positive with conditional assignments of fields
* java-codestyle
  * [#4881](https://github.com/pmd/pmd/issues/4881): \[java] ClassNamingConventions: interfaces are identified as abstract classes (regression in 7.0.0)
* java-design
  * [#3694](https://github.com/pmd/pmd/issues/3694): \[java] SingularField ignores static variables
  * [#4873](https://github.com/pmd/pmd/issues/4873): \[java] AvoidCatchingGenericException: Can no longer suppress on the exception itself
* java-errorprone
  * [#2056](https://github.com/pmd/pmd/issues/2056): \[java] CloseResource false-positive with URLClassLoader in cast expression
* java-performance
  * [#3845](https://github.com/pmd/pmd/issues/3845): \[java] InsufficientStringBufferDeclaration should consider literal expression
  * [#4874](https://github.com/pmd/pmd/issues/4874): \[java] StringInstantiation: False-positive when using `new String(charArray)`
  * [#4886](https://github.com/pmd/pmd/issues/4886): \[java] BigIntegerInstantiation: False Positive with Java 17 and BigDecimal.TWO
* pom-errorprone
  * [#4388](https://github.com/pmd/pmd/issues/4388): \[pom] InvalidDependencyTypes doesn't consider dependencies at all

### 🚨 API Changes

### ✨ External Contributions
* [#4864](https://github.com/pmd/pmd/pull/4864): Fix #1084 \[Java] add extra assert method names to Junit rules - [Erwan Moutymbo](https://github.com/emouty) (@emouty)
* [#4894](https://github.com/pmd/pmd/pull/4894): Fix #4791 Error caused by space in JDK path - [Scrates1](https://github.com/Scrates1) (@Scrates1)

{% endtocmaker %}

