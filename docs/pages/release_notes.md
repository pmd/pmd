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

#### Renamed Rules
Several rules for unit testing have been renamed to better reflect their actual scope. Lots of them were called
after JUnit / JUnit 4, even when they applied to JUnit 5 and / or TestNG.

* {% rule java/bestpractices/UnitTestShouldUseAfterAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseAfterAnnotation`.
* {% rule java/bestpractices/UnitTestShouldUseBeforeAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseBeforeAnnotation`.
* {% rule java/bestpractices/UnitTestShouldUseTestAnnotation %} (Java Best Practices) has been renamed from `JUnit4TestShouldUseTestAnnotation`.
* {% rule java/bestpractices/UnitTestAssertionsShouldIncludeMessage %} (Java Best Practices) has been renamed from `JUnitAssertionsShouldIncludeMessage`.
* {% rule java/bestpractices/UnitTestContainsTooManyAsserts %} (Java Best Practices) has been renamed from `JUnitTestContainsTooManyAsserts`.
* {% rule java/bestpractices/UnitTestsShouldIncludeAssert %} (Java Best Practices) has been renamed from `JUnitTestsShouldIncludeAssert`.

The old rule names still work but are deprecated.

### 🐛 Fixed Issues
* java
  * [#4532](https://github.com/pmd/pmd/issues/4532): \[java] Rule misnomer for JUnit* rules
* java-bestpractices
  * [#4278](https://github.com/pmd/pmd/issues/4278): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource and default factory method name
  * [#4975](https://github.com/pmd/pmd/issues/4975): \[java] UnusedPrivateMethod false positive when using @MethodSource on a @Nested test

### 🚨 API Changes

### ✨ Merged pull requests
* [#4965](https://github.com/pmd/pmd/pull/4965): \[java] Rename JUnit rules with overly restrictive names - [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

{% endtocmaker %}

