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


### Fixed Issues

*   apex
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

{% endtocmaker %}

