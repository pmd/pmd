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

#### All Contributors

PMD follows the [All Contributors](https://allcontributors.org/) specification.
Contributions of any kind welcome!

See [credits](https://pmd.github.io/latest/pmd_projectdocs_credits.html) for our complete contributors list.

### Fixed Issues

*   core
    *   [#3499](https://github.com/pmd/pmd/pull/3499): \[core] Fix XPath rulechain with combined node tests
*   java-errorprone
    *   [#3493](https://github.com/pmd/pmd/pull/3493): \[java] AvoidAccessibilityAlteration: add tests and fix rule
*   plsql
    *   [#3487](https://github.com/pmd/pmd/issues/3487): \[plsql] Parsing exception OPEN ref_cursor_name FOR statement

### API Changes

### External Contributions

{% endtocmaker %}

