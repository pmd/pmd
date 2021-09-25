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

#### All Contributors

PMD follows the [All Contributors](https://allcontributors.org/) specification.
Contributions of any kind welcome!

See [credits](https://pmd.github.io/latest/pmd_projectdocs_credits.html) for our complete contributors list.

### Fixed Issues

*   core
    *   [#3499](https://github.com/pmd/pmd/pull/3499): \[core] Fix XPath rulechain with combined node tests
*   java-errorprone
    *   [#3493](https://github.com/pmd/pmd/pull/3493): \[java] AvoidAccessibilityAlteration: add tests and fix rule
*   javascript
    *   [#3516](https://github.com/pmd/pmd/pull/3516): \[javascript] NPE while creating rule violation when specifying explicit line numbers
*   plsql
    *   [#3487](https://github.com/pmd/pmd/issues/3487): \[plsql] Parsing exception OPEN ref_cursor_name FOR statement
    *   [#3515](https://github.com/pmd/pmd/issues/3515): \[plsql] Parsing exception SELECT...INTO on Associative Arrays Types

### API Changes

No changes.

### External Contributions

*   [#3516](https://github.com/pmd/pmd/pull/3516): \[javascript] NPE while creating rule violation when specifying explicit line numbers - [Kevin Guerra](https://github.com/kevingnet)

### Stats
* 37 commits
* 10 closed tickets & PRs
* Days since last release: 27

{% endtocmaker %}

