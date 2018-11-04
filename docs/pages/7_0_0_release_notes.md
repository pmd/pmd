---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FOR 7.0.0 -->
<!-- It must be used instead of release_notes.md when adding 7.0.0 changes -->
<!-- to avoid merge conflicts with master -->
<!-- It must replace release_notes.md when releasing 7.0.0 -->

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

### API Changes


* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer` (and corresponding Batch scripts)
  have been removed from the PMD distribution. Please use the newer rule designer with `run.sh designer`.
  The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and `java.net.sourceforge.pmd.util.designer` have
  all been removed.


### External Contributions

{% endtocmaker %}

