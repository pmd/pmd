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

*   plsql
    *   [#3106](https://github.com/pmd/pmd/issues/3106): \[plsql] ParseException while parsing EXECUTE IMMEDIATE 'drop database link ' || linkname;

### API Changes

### External Contributions

*   [#3107](https://github.com/pmd/pmd/pull/3107): \[plsql] Fix ParseException for EXECUTE IMMEDIATE str1||str2; - [hvbtup](https://github.com/hvbtup)

{% endtocmaker %}

