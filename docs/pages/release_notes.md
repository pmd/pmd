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

#### Improved Incremental Analysis

[Incremental Analysis](https://pmd.github.io/pmd-6.36.0/pmd_userdocs_incremental_analysis.html) has long helped our users obtain faster analysis results,
however, it's implementation tended to be too cautious in detecting changes to the runtime and type resolution classpaths, producing more cache invalidations than were necessary.
We have now improved the heuristics to remove several bogus invalidations, and slightly sped up the cache usage along the way.

PMD will now ignore:

*   Non class files in classpath and jar / zip files being referenced.
*   Changes to the order of file entries within a jar / zip
*   Changes to file metadata within jar / zip (ie: creation and modification time, significant in multi-mmodule / composite build projects where lateral artifacts are frequently recreated)

### Fixed Issues

### API Changes

### External Contributions

{% endtocmaker %}

