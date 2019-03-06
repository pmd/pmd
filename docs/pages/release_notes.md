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

*   The new Java rule {% rule "java/design/AvoidUncheckedExceptionsInSignatures" %} finds methods or constructors
    that declare unchecked exceptions in their `throws` clause. This forces the caller to handle the exception,
    even though it is a runtime exception.

### Fixed Issues
*   java-design
    *   [#1692](https://github.com/pmd/pmd/issues/1692): \[java] Add rule to avoid declaration of throwing unchecked exception

### API Changes

### External Contributions
*   [#1704](https://github.com/pmd/pmd/pull/1704): \[java] Added AvoidUncheckedExceptionsInSignatures Rule - [Bhanu Prakash Pamidi](https://github.com/pamidi99)

{% endtocmaker %}

