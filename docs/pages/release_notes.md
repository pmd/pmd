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

#### Improved Incremental Analysis

[Incremental Analysis](https://pmd.github.io/pmd-6.36.0/pmd_userdocs_incremental_analysis.html) has long helped
our users obtain faster analysis results, however, it's implementation tended to be too cautious in detecting
changes to the runtime and type resolution classpaths, producing more cache invalidations than were necessary.
We have now improved the heuristics to remove several bogus invalidations, and slightly sped up the cache
usage along the way.

PMD will now ignore:

*   Non class files in classpath and jar / zip files being referenced.
*   Changes to the order of file entries within a jar / zip
*   Changes to file metadata within jar / zip (ie: creation and modification time,
    significant in multi-module / composite build projects where lateral artifacts are frequently recreated)

#### New rules

*   The new Apex rule {% rule apex/performance/AvoidDebugStatements %} finds usages of `System.debug` calls.
    Debug statements contribute to longer transactions and consume Apex CPU time even when debug logs are not
    being captured.
    You can try out this rule like so:

```xml
    <rule ref="category/apex/performance.xml/AvoidDebugStatements" />
```

*   The new Apex rule {% rule "apex/errorprone/InaccessibleAuraEnabledGetter" %} checks that an `AuraEnabled`
    getter is public or global. This is necessary if it is referenced in Lightning components.
    You can try out this rule like so:

```xml
    <rule ref="category/apex/errorprone.xml/InaccessibleAuraEnabledGetter" />
```

#### Renamed rules

*   The Java rule {% rule "java/errorprone/BadComparison" %} has been renamed to
    {% rule "java/errorprone/ComparisonWithNaN" %} to better reflect what the rule actually detects.
    It now considers usages of `Double.NaN` or `Float.NaN` in more cases and fixes false negatives.

### Fixed Issues

*   apex
    *   [#3307](https://github.com/pmd/pmd/issues/3307): \[apex] Avoid debug statements since it impact performance
    *   [#3321](https://github.com/pmd/pmd/issues/3321): \[apex] New rule to detect inaccessible AuraEnabled getters (summer '21 security update)
    *   [#3332](https://github.com/pmd/pmd/issues/3332): \[apex] CognitiveComplexity - incorrect increment for "else if"
*   core
    *   [#2637](https://github.com/pmd/pmd/issues/2637): \[cpd] Error Loading stylesheet cpdhtml.xslt
    *   [#3323](https://github.com/pmd/pmd/pull/3323): \[core] Adds fullDescription and tags in SARIF report
*   java-bestpractices
    *   [#957](https://github.com/pmd/pmd/issues/957): \[java] GuardLogStatement: False positive with compile-time constant arguments
    *   [#3114](https://github.com/pmd/pmd/issues/3114): \[java] UnusedAssignment false positive when reporting unused variables
    *   [#3315](https://github.com/pmd/pmd/issues/3315): \[java] LiteralsFirstInComparisons false positive with two constants
    *   [#3341](https://github.com/pmd/pmd/issues/3341): \[java] JUnitTestsShouldIncludeAssert should support Junit 5
    *   [#3340](https://github.com/pmd/pmd/issues/3340): \[java] NullPointerException applying rule GuardLogStatement
*   java-codestyle
    *   [#3317](https://github.com/pmd/pmd/pull/3317): \[java] Update UnnecessaryImport to recognize usage of imported types in javadoc's `@exception` tag
*   java-errorprone
    *   [#2895](https://github.com/pmd/pmd/issues/2895): \[java] Improve BadComparison and rename to ComparisonWithNaN
    *   [#3284](https://github.com/pmd/pmd/issues/3284): \[java] InvalidLogMessageFormat may examine the value of a different but identically named String variable
    *   [#3304](https://github.com/pmd/pmd/issues/3304): \[java] NPE in MoreThanOneLoggerRule on a java 16 record
    *   [#3305](https://github.com/pmd/pmd/issues/3305): \[java] ConstructorCallsOverridableMethodRule IndexOutOfBoundsException on a java16 record
    *   [#3343](https://github.com/pmd/pmd/pull/3343): \[java] CloneMethodMustImplementCloneable: FN with local classes
*   java-performance
    *   [#3331](https://github.com/pmd/pmd/issues/3331): \[java] UseArraysAsList false negative with for-each loop
    *   [#3344](https://github.com/pmd/pmd/pull/3344): \[java] InefficientEmptyStringCheck FN with trim.length on method call

### API Changes

### External Contributions

*   [#3276](https://github.com/pmd/pmd/pull/3276): \[apex] Update ApexCRUDViolation and OperationWithLimitsInLoop docs - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3306](https://github.com/pmd/pmd/pull/3306): \[java] More than one logger rule test null pointer exception - [Arnaud Jeansen](https://github.com/ajeans)
*   [#3317](https://github.com/pmd/pmd/pull/3317): \[java] Update UnnecessaryImport to recognize usage of imported types in javadoc's `@exception` tag - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#3319](https://github.com/pmd/pmd/pull/3319): \[apex] New AvoidDebugStatements rule to mitigate performance impact - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3320](https://github.com/pmd/pmd/pull/3320): \[java] Fix incorrect increment for "else if" branch in Cognitive Complexity docs - [Denis Borovikov](https://github.com/borovikovd)
*   [#3322](https://github.com/pmd/pmd/pull/3322): \[apex] added rule to detect inaccessible AuraEnabled getters - [Philippe Ozil](https://github.com/pozil)
*   [#3323](https://github.com/pmd/pmd/pull/3323): \[core] Adds fullDescription and tags in SARIF report - [Clint Chester](https://github.com/Clint-Chester)
*   [#3339](https://github.com/pmd/pmd/pull/3339): \[java] JUnitTestsShouldIncludeAssert Tweak assertion definition to avoid false positive with modern JUnit5 - [Arnaud Jeansen](https://github.com/ajeans)

{% endtocmaker %}

