---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

*   apex
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors

### API Changes

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)

{% endtocmaker %}

