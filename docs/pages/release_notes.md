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

#### New rules

*   The new Apex rule {% rule apex/performance/EagerlyLoadedDescribeSObjectResult %} finds
    `DescribeSObjectResult`s which could have been loaded eagerly via `SObjectType.getDescribe()`.

```xml
    <rule ref="category/apex/performance.xml/EagerlyLoadedDescribeSObjectResult" />
```

#### Modified rules

*   The Apex rule {% rule apex/bestpractices/ApexUnitTestClassShouldHaveAsserts %} has a new property
    `additionalAssertMethodPattern`. When specified the pattern is evaluated against each invoked
    method name to determine whether it represents a test assertion in addition to the standard names.

### Fixed Issues

*   apex
    *   [#1089](https://github.com/pmd/pmd/issues/1089): \[apex] Test asserts in other methods not detected
    *   [#3532](https://github.com/pmd/pmd/issues/3532): \[apex] Promote usage of consistent getDescribe() info
*   java-errorprone
    *   [#3560](https://github.com/pmd/pmd/issues/3560): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda
*   java-performance
    *   [#2364](https://github.com/pmd/pmd/issues/2364): \[java] AddEmptyString false positive in annotation value

### API Changes

### External Contributions

*   [#3538](https://github.com/pmd/pmd/pull/3538): \[apex] New rule EagerlyLoadedDescribeSObjectResult - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3549](https://github.com/pmd/pmd/pull/3549): \[java] Ignore AddEmptyString rule in annotations - [Stanislav Myachenkov](https://github.com/smyachenkov)
*   [#3561](https://github.com/pmd/pmd/pull/3561): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda - [Nicolas Filotto](https://github.com/essobedo)
*   [#3565](https://github.com/pmd/pmd/pull/3565): \[doc] Fix resource leak due to Files.walk - [lujiefsi](https://github.com/lujiefsi)
*   [#3571](https://github.com/pmd/pmd/pull/3571): \[apex] Fix for #1089 - Added new configuration property additionalAssertMethodPattern to ApexUnitTestClassShouldHaveAssertsRule - [Scott Wells](https://github.com/SCWells72)

{% endtocmaker %}

