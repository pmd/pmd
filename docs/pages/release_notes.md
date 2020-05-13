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

#### CPD now supports XML as well

Thanks to [Fernando Cosso](https://github.com/xnYi9wRezm) CPD can now find duplicates in XML files as well.
This is useful to find duplicated sections in XML files.

### Fixed Issues

*   apex-bestpractices
    *   [#2468](https://github.com/pmd/pmd/issues/2468): \[apex] Unused Local Variable fails on blocks

### API Changes

### External Contributions

*   [#2446](https://github.com/pmd/pmd/pull/2446): \[core] Update maven-compiler-plugin to 3.8.1 - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2448](https://github.com/pmd/pmd/pull/2448): \[java] Operator Wrap check - [Harsh Kukreja](https://github.com/harsh-kukreja)
*   [#2449](https://github.com/pmd/pmd/pull/2449): \[plsql] Additional info in SqlStatement, FormalParameter and FetchStatement - [Grzegorz Sudolski](https://github.com/zgrzyt93)
*   [#2452](https://github.com/pmd/pmd/pull/2452): \[doc] Fix "Making Rulesets" doc sample code indentation - [Artur Dryomov](https://github.com/arturdryomov)
*   [#2457](https://github.com/pmd/pmd/pull/2457): \[xml] Adding XML to CPD supported languages - [Fernando Cosso](https://github.com/xnYi9wRezm)
*   [#2469](https://github.com/pmd/pmd/pull/2469): \[apex] fix false positive unused variable if only a method is called - [Gwilym Kuiper](https://github.com/gwilymatgearset)

{% endtocmaker %}

