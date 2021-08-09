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

This release ships with 1 new Java rule.

*   java-errorprone
    *   [`AvoidAssert`](https://pmd.github.io/pmd-6.38.0-SNAPSHOT/pmd_rules_java_errorprone.html#avoidassert) reports usages of assert.  An if statement is a better choice since it cannot be disabled and hence reduces the need for 1 test case.
    
```xml
    <rule ref="category/java/errorprone.xml/AvoidAssert" />
```

### Fixed Issues

*   java-bestpractices
    *   [#3403](https://github.com/pmd/pmd/issues/3403): \[java] MethodNamingConventions junit5TestPattern does not detect parameterized tests
    

### API Changes

### External Contributions

*   [#3445](https://github.com/pmd/pmd/pull/3445): \[java] Fix #3403 about MethodNamingConventions and JUnit5 parameterized tests - [Cyril Sicard](https://github.com/CyrilSicard)

{% endtocmaker %}

