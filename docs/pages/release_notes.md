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

*   {% rule java/bestpractices/SimplifiableTestAssertion %} suggests rewriting
    some test assertions to be more readable.
```xml
    <rule ref="category/java/bestpractices.xml/SimplifiableTestAssertion" />
```

   The rule is part of the quickstart.xml ruleset.

#### Deprecated rules

The following Java rules are deprecated and removed from the quickstart ruleset,
 as the new rule {% rule java/bestpractices/SimplifiableTestAssertion %} merges
 their functionality:
* {% rule java/bestpractices/UseAssertEqualsInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertNullInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertSameInsteadOfAssertTrue %}
* {% rule java/bestpractices/UseAssertTrueInsteadOfAssertEquals %}
* {% rule java/design.xml/SimplifyBooleanAssertion %}

### Fixed Issues

*   apex
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors
*   java-bestpractices
    *   [#2908](https://github.com/pmd/pmd/issues/2908): \[java] Merge Junit assertion simplification rules

### API Changes

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)

{% endtocmaker %}

