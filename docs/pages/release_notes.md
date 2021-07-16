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

*   {% rule java/errorprone/ReturnEmptyCollectionRatherThanNull %} suggests returning empty collections / arrays
    instead of null.

```xml
    <rule ref="category/java/errorprone.xml/ReturnEmptyCollectionRatherThanNull" />
```

   The rule is part of the quickstart.xml ruleset.

#### Deprecated rules

The rule {% rule java/errorprone.xml/ReturnEmptyArrayRatherThanNull %} is deprecated and removed from
the quickstart ruleset, as the new rule {% rule java/errorprone.xml/ReturnEmptyCollectionRatherThanNull %}
supersedes it.

### Fixed Issues

*   apex
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   java-errorprone
    *   [#3382](https://github.com/pmd/pmd/pull/3382): \[java] New rule ReturnEmptyCollectionRatherThanNull

### API Changes

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)

{% endtocmaker %}

