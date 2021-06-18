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

*   The new Apex rule {% rule apex/performance/AvoidDebugStatements %} finds usages of `System.debug` calls.
    Debug statements contribute to longer transactions and consume Apex CPU time even when debug logs are not
    being captured.
    You can try out this rule like so:
```xml
    <rule ref="category/apex/performance.xml/AvoidDebugStatements" />
```

### Fixed Issues

*   apex
    *   [#3307](https://github.com/pmd/pmd/issues/3307): \[apex] Avoid debug statements since it impact performance

### API Changes

### External Contributions

*   [#3319](https://github.com/pmd/pmd/pull/3319): \[apex] New AvoidDebugStatements rule to mitigate performance impact - [Jonathan Wiesel](https://github.com/jonathanwiesel)

{% endtocmaker %}

