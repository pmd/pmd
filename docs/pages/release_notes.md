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

*   The Java rule {% rule "java/codestyle/UseDiamondOperator" %} (`java-codestyle`) now by default
    finds unnecessary usages of type parameters, which are nested, involve wildcards and are used
    within a ternary operator. These usages are usually only unnecessary with Java8 and later, when
    the type inference in Java has been improved.
    
    In order to avoid false positives when checking Java7 only code, the rule has the new property
    `java7Compatibility`, which is disabled by default. Settings this to "true" retains
    the old rule behaviour.

### Fixed Issues

*   java-codestyle
    *   [#2545](https://github.com/pmd/pmd/issues/2545): \[java] UseDiamondOperator false negatives

### API Changes

### External Contributions

{% endtocmaker %}

