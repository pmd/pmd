---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Modified rules

* The rule {% rule java/codestyle/ClassNamingConventions %} has a new property `testClassPattern`, which is applied
  to test classes. By default, test classes should end with the suffix "Test". Test classes are classes, that
  either inherit from JUnit 3 TestCase or have at least one method annotated with the Test annotations from
  JUnit4/5 or TestNG.

### Fixed Issues
* java-codestyle
    * [#2867](https://github.com/pmd/pmd/issues/2867): \[java] Separate pattern for test classes in ClassNamingConventions rule for Java

### API Changes

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)

{% endtocmaker %}

