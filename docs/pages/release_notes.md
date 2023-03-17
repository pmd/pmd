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
* core
  * [#4420](https://github.com/pmd/pmd/pull/4420): \[core] Remove PMD.EOL
* java-bestpractices
  * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
* java-documentation
  * [#4416](https://github.com/pmd/pmd/pull/4416): \[java] Fix reported line number in CommentContentRule

### API Changes

### External Contributions
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)

{% endtocmaker %}

