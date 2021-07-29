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

This release ships with 2 new Java rules.

*   {% rule java/bestpractices/SimplifiableTestAssertion %} suggests rewriting
    some test assertions to be more readable.

```xml
    <rule ref="category/java/bestpractices.xml/SimplifiableTestAssertion" />
```

   The rule is part of the quickstart.xml ruleset.

*   {% rule java/errorprone/ReturnEmptyCollectionRatherThanNull %} suggests returning empty collections / arrays
    instead of null.

```xml
    <rule ref="category/java/errorprone.xml/ReturnEmptyCollectionRatherThanNull" />
```

   The rule is part of the quickstart.xml ruleset.

#### Renamed rules

*   The Java rule {% rule java/errorprone/MissingBreakInSwitch %} has been renamed to
    {% rule java/errorprone/ImplicitSwitchFallThrough %} (category error prone) to better reflect the rule's
    purpose: The rule finds implicit fall-through cases in switch statements, which are most
    likely unexpected. The old rule name described only one way how to avoid a fall-through,
    namely using `break` but `continue`, `throw` and `return` avoid a fall-through
    as well. This enables us to improve this rule in the future.

#### Deprecated rules

The following Java rules are deprecated and removed from the quickstart ruleset,
 as the new rule {% rule java/bestpractices/SimplifiableTestAssertion %} merges
 their functionality:
* {% rule java/bestpractices/UseAssertEqualsInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertNullInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertSameInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertTrueInsteadOfAssertEquals %}
* {% rule java/design.xml/SimplifyBooleanAssertion %}

The rule {% rule java/errorprone/ReturnEmptyArrayRatherThanNull %} is deprecated and removed from
the quickstart ruleset, as the new rule {% rule java/errorprone/ReturnEmptyCollectionRatherThanNull %}
supersedes it.


### Fixed Issues

*   apex
    *   [#3201](https://github.com/pmd/pmd/issues/3201): \[apex] ApexCRUDViolation doesn't report Database class DMLs, inline no-arg object instantiations and inline list initialization
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors
*   java-bestpractices
    *   [#2908](https://github.com/pmd/pmd/issues/2908): \[java] Merge Junit assertion simplification rules
*   java-errorprone
    *   [#3361](https://github.com/pmd/pmd/issues/3361): \[java] Rename rule MissingBreakInSwitch to ImplicitSwitchFallThrough
    *   [#3382](https://github.com/pmd/pmd/pull/3382): \[java] New rule ReturnEmptyCollectionRatherThanNull

### API Changes

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3373](https://github.com/pmd/pmd/pull/3373): \[apex] Add ApexCRUDViolation support for database class, inline no-arg object construction DML and inline list initialization DML - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)
*   [#3388](https://github.com/pmd/pmd/pull/3388): \[doc] Add Code Inspector in the list of tools - [Julien Delange](https://github.com/juli1)

{% endtocmaker %}

