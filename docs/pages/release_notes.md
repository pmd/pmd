---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### New Rules
* The new Apex rule {% rule apex/bestpractices/ApexUnitTestClassShouldHaveRunAs %} ensures that unit tests
  use [System.runAs()](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_testing_tools_runas.htm)
  at least once. This makes the tests more robust, and independent from the user running it.

```xml
<rule ref="category/apex/bestpractices.xml/ApexUnitTestClassShouldHaveRunAs"/>
```

The rule is part of the quickstart.xml ruleset.

#### Modified Rules

* The Java rule {% rule java/errorprone/TestClassWithoutTestCases %} has a new property `testClassPattern`. This is
  used to detect empty test classes by name. Previously this rule could only detect empty JUnit3 test cases
  properly. To switch back to the old behavior, this property can be set to an empty value which disables the
  test class detection by pattern.

### Fixed Issues
* apex
    * [#4149](https://github.com/pmd/pmd/issues/4149): \[apex] New rule: ApexUnitTestClassShouldHaveRunAs
* doc
    * [#4144](https://github.com/pmd/pmd/pull/4144): \[doc] Update docs to reflect supported languages
    * [#4163](https://github.com/pmd/pmd/issues/4163): \[doc] Broken links on page "Architecture Decisions"
* java-bestpractices
    * [#4140](https://github.com/pmd/pmd/issues/4140): \[java] \[doc] AccessorClassGeneration violations hidden with Java 11
* java-codestyle
    * [#4139](https://github.com/pmd/pmd/issues/4139): \[java] UnnecessaryFullyQualifiedName FP when the same simple class name exists in the current package
* java-documentation
    * [#4141](https://github.com/pmd/pmd/issues/4141): \[java] UncommentedEmptyConstructor FP when constructor annotated with @<!-- -->Autowired
* java-performance
    * [#1167](https://github.com/pmd/pmd/issues/1167): \[java] AvoidArrayLoops false positive on double assignment
    * [#2080](https://github.com/pmd/pmd/issues/2080): \[java] StringToString rule false-positive with field access
    * [#2692](https://github.com/pmd/pmd/issues/2692): \[java] \[doc] AvoidArrayLoops flags copy assignment in same array as sub-optimal
    * [#3437](https://github.com/pmd/pmd/issues/3437): \[java] StringToString doesn't trigger on Bar.class.getSimpleName().toString()
    * [#3681](https://github.com/pmd/pmd/issues/3681): \[java] StringToString doesn't trigger on string literals
    * [#3847](https://github.com/pmd/pmd/issues/3847): \[java] AvoidArrayLoops should consider final variables
    * [#3977](https://github.com/pmd/pmd/issues/3977): \[java] StringToString false-positive with local method name confusion
    * [#4091](https://github.com/pmd/pmd/issues/4091): \[java] AvoidArrayLoops false negative with do-while loops
    * [#4148](https://github.com/pmd/pmd/issues/4148): \[java] UseArrayListInsteadOfVector ignores Vector when other classes are imported
* java-errorprone
    * [#929](https://github.com/pmd/pmd/issues/929): \[java] Inconsistent results with TestClassWithoutTestCases
    * [#2636](https://github.com/pmd/pmd/issues/2636): \[java] TestClassWithoutTestCases false positive with JUnit5 ParameterizedTest
* javascript
    * [#4165](https://github.com/pmd/pmd/issues/4165): \[javascript] InaccurateNumericLiteral underscore separator notation false positive

### API Changes

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4147](https://github.com/pmd/pmd/pull/4147): \[java] Added support for Do-While for AvoidArrayLoops - [Yasar Shaikh](https://github.com/yasarshaikh) (@yasarshaikh)
* [#4150](https://github.com/pmd/pmd/pull/4150): \[apex] New rule ApexUnitTestClassShouldHaveRunAs #4149 - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)

{% endtocmaker %}

