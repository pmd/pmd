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

#### PLSQL parsing exclusions

The PMD PLSQL parser might not parse every valid PL/SQL code without problems.
In order to still use PMD on such files, you can now mark certain lines for exclusion from
the parser. More information can be found in the [language specific documentation for PLSQL](pmd_languages_plsql.html).

### Fixed Issues

*   plsql
    *   [#195](https://github.com/pmd/pmd/issues/195): \[plsql] Ampersand '&' causes PMD processing error in sql file - Lexical error in file

### API Changes

### External Contributions

*   [#3161](https://github.com/pmd/pmd/pull/3161): \[plsql] Add support for lexical parameters in SQL*Plus scripts, allow excluding lines which the parser does not understand - [Henning von Bargen](https://github.com/hvbtup)

{% endtocmaker %}

