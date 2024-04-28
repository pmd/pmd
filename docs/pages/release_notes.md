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

Several rules for unit testing have been renamed to better reflect their actual scope. Lots of them were called after JUnit / JUnit 4, even when they applied to JUnit 5 and / or TestNG.

* `java/bestpractices/JUnit4TestShouldUseAfterAnnotation` has been renamed to {% rule java/bestpractices/JUnitTestShouldUseAfterAnnotation %}

* `java/bestpractices/JUnit4TestShouldUseBeforeAnnotation` has been renamed to {% rule java/bestpractices/JUnitTestShouldUseBeforeAnnotation %}

* `java/bestpractices/JUnit4TestShouldUseTestAnnotation` has been renamed to {% rule java/bestpractices/UnitTestShouldUseTestAnnotation %}

* `java/bestpractices/JUnitAssertionsShouldIncludeMessage` has been renamed to {% rule java/bestpractices/UnitTestAssertionsShouldIncludeMessage %}

* `java/bestpractices/JUnitTestContainsTooManyAsserts` has been renamed to {% rule java/bestpractices/UnitTestContainsTooManyAsserts %}

* `java/bestpractices/JUnitTestsShouldIncludeAssert` has been renamed to {% rule java/bestpractices/UnitTestsShouldIncludeAssert %}

### 🐛 Fixed Issues

* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

