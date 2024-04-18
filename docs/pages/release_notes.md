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
* core
  * [#494](https://github.com/pmd/pmd/issues/494): \[core] Adopt JApiCmp to enforce control over API changes
* cli
  * [#4791](https://github.com/pmd/pmd/issues/4791): \[cli] Could not find or load main class
  * [#4913](https://github.com/pmd/pmd/issues/4913): \[cli] cpd-gui closes immediately
* doc
  * [#4901](https://github.com/pmd/pmd/issues/4901): \[doc] Improve documentation on usage of violationSuppressXPath
* apex
  * [#4418](https://github.com/pmd/pmd/issues/4418): \[apex] ASTAnnotation.getImage() does not return value as written in the class
* apex-errorprone
  * [#3953](https://github.com/pmd/pmd/issues/3953): \[apex] EmptyCatchBlock false positive with formal (doc) comments
* java
  * [#4899](https://github.com/pmd/pmd/issues/4899): \[java] Parsing failed in ParseLock#doParse() java.io.IOException: Stream closed
  * [#4902](https://github.com/pmd/pmd/issues/4902): \[java] "Bad intersection, unrelated class types" for Constable\[] and Enum\[]
  * [#4947](https://github.com/pmd/pmd/issues/4947): \[java] Broken TextBlock parser
* java-bestpractices
  * [#1084](https://github.com/pmd/pmd/issues/1084): \[java] Allow JUnitTestsShouldIncludeAssert to configure verification methods
  * [#4435](https://github.com/pmd/pmd/issues/4435): \[java] \[7.0-rc1] UnusedAssignment for used field
  * [#4569](https://github.com/pmd/pmd/issues/4569): \[java] ForLoopCanBeForeach reports on loop `for (int i = 0; i < list.size(); i += 2)`
  * [#4618](https://github.com/pmd/pmd/issues/4618): \[java] UnusedAssignment false positive with conditional assignments of fields
* java-codestyle
  * [#4602](https://github.com/pmd/pmd/issues/4602): \[java] UnnecessaryImport: false positives with static imports
  * [#4785](https://github.com/pmd/pmd/issues/4785): \[java] False Positive: PMD Incorrectly report violation for UnnecessaryImport
  * [#4779](https://github.com/pmd/pmd/issues/4779): \[java] Examples in documentation of MethodArgumentCanBeFinal do not trigger the rule
  * [#4881](https://github.com/pmd/pmd/issues/4881): \[java] ClassNamingConventions: interfaces are identified as abstract classes (regression in 7.0.0)
* java-design
  * [#3694](https://github.com/pmd/pmd/issues/3694): \[java] SingularField ignores static variables
  * [#4873](https://github.com/pmd/pmd/issues/4873): \[java] AvoidCatchingGenericException: Can no longer suppress on the exception itself
* java-errorprone
  * [#2056](https://github.com/pmd/pmd/issues/2056): \[java] CloseResource false-positive with URLClassLoader in cast expression
  * [#4928](https://github.com/pmd/pmd/issues/4928): \[java] EmptyCatchBlock false negative when allowCommentedBlocks=true
* java-performance
  * [#3845](https://github.com/pmd/pmd/issues/3845): \[java] InsufficientStringBufferDeclaration should consider literal expression
  * [#4874](https://github.com/pmd/pmd/issues/4874): \[java] StringInstantiation: False-positive when using `new String(charArray)`
  * [#4886](https://github.com/pmd/pmd/issues/4886): \[java] BigIntegerInstantiation: False Positive with Java 17 and BigDecimal.TWO
* pom-errorprone
  * [#4388](https://github.com/pmd/pmd/issues/4388): \[pom] InvalidDependencyTypes doesn't consider dependencies at all
* misc
  * [#4967](https://github.com/pmd/pmd/pull/4967): Fix reproducible build issues with 7.0.0

### 🚨 API Changes

#### Deprecated methods

* {%jdoc java::lang.java.rule.design.SingularFieldRule#mayBeSingular(java::lang.java.ast.ModifierOwner) %} has been deprecated for
  removal. The method is only useful for the rule itself and shouldn't be used otherwise.

### ✨ External Contributions
* [#4864](https://github.com/pmd/pmd/pull/4864): Fix #1084 \[Java] add extra assert method names to Junit rules - [Erwan Moutymbo](https://github.com/emouty) (@emouty)
* [#4894](https://github.com/pmd/pmd/pull/4894): Fix #4791 Error caused by space in JDK path - [Scrates1](https://github.com/Scrates1) (@Scrates1)

{% endtocmaker %}

