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

#### New Rules

*   The new Apex rule {% rule "apex/performance/OperationWithLimitsInLoop" %} (`apex-performance`)
    finds operations in loops that may hit governor limits such as DML operations, SOQL
    queries and more. The rule replaces the three rules "AvoidDmlStatementsInLoops", "AvoidSoqlInLoops",
    and "AvoidSoslInLoops".

#### Deprecated Rules

*   The Apex rules {% rule "apex/performance/AvoidDmlStatementsInLoops" %},
    {% rule "apex/performance/AvoidSoqlInLoops" %} and {% rule "apex/performance/AvoidSoslInLoops" %}
    (`apex-performance`) are deprecated in favour of the new rule
    {% rule "apex/performance/OperationWithLimitsInLoop" %}. The deprecated rules will be removed
    with PMD 7.0.0.

### Fixed Issues

*   apex-performance
    *   [#1713](https://github.com/pmd/pmd/issues/1713): \[apex] Mark Database DML statements in For Loop

### API Changes

### External Contributions

*   [#2809](https://github.com/pmd/pmd/pull/2809): \[java] Move test config from file to test class - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2810](https://github.com/pmd/pmd/pull/2810): \[core] Move method "renderTempFile" to XMLRendererTest - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2816](https://github.com/pmd/pmd/pull/2816): \[apex] Detect 'Database' method invocations inside loops - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)

{% endtocmaker %}

