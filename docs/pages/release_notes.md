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

### Fixed Issues
* apex-security
    * [#4146](https://github.com/pmd/pmd/issues/4146): \[apex] ApexCRUDViolation: Recognize User Mode in SOQL + DML
* java
    * [#4266](https://github.com/pmd/pmd/issues/4266): \[java] PMD fails to process a record with lambda in compact constructor

### API Changes

### External Contributions
* [#4244](https://github.com/pmd/pmd/pull/4244): \[apex] ApexCRUDViolation: user mode and system mode with test cases added - [Tarush Singh](https://github.com/Tarush-Singh35) (@Tarush-Singh35)
* [#4274](https://github.com/pmd/pmd/pull/4274): \[java] Fix finding lambda scope in record compact constructor - [kdebski85](https://github.com/kdebski85) (@kdebski85)

{% endtocmaker %}

