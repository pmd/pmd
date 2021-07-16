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
    *   [#3201](https://github.com/pmd/pmd/issues/3201): \[apex] ApexCRUDViolation doesn't report Database class DMLs, inline no-arg object instantiations and inline list initialization
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors
*   java-errorprone
    *   [#3382](https://github.com/pmd/pmd/pull/3382): \[java] New rule ReturnEmptyCollectionRatherThanNull

### API Changes

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3373](https://github.com/pmd/pmd/pull/3373): \[apex] Add ApexCRUDViolation support for database class, inline no-arg object construction DML and inline list initialization DML - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)
*   [#3388](https://github.com/pmd/pmd/pull/3388): \[doc] Add Code Inspector in the list of tools - [Julien Delange](https://github.com/juli1)

{% endtocmaker %}

