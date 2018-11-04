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

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties

### API Changes

* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer` (and corresponding Batch scripts)
  have been removed from the PMD distribution. Please use the newer rule designer with `run.sh designer`.
  The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and `java.net.sourceforge.pmd.util.designer` have
  all been removed.


### External Contributions

*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)

{% endtocmaker %}

