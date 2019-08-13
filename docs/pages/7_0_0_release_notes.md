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

#### Swift support

PMD now fully supports Swift, not only for CPD, but we are pleased to announce we are shipping a number of rules starting with PMD 7.

* {% rule "swift/errorprone/ForceCast" %} (`swift-errorprone`) flags all force casts, making sure you are defensively considering all types.
  Having the application crash shouldn't be an option.
* {% rule "swift/errorprone/ForceTry" %} (`swift-errorprone`) flags all force tries, making sure you are defensively handling exceptions.
  Having the application crash shouldn't be an option.
* {% rule "swift/bestpractices/ProhibitedInterfaceBuilder" %} (`swift-bestpractices`) flags any usage of interface builder. Interface builder
  files are prone to merge conflicts, and are impossible to code review, so larger teams usually try to avoid it or reduce it's usage.

### Fixed Issues

### API Changes

* [#1648](https://github.com/pmd/pmd/pull/1702): \[apex,vf] Remove CodeClimate dependency - [Robert Sösemann](https://github.com/rsoesemann)
  Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" can no longer be overridden in rulesets. 
  They were deprecated without replacement. 

* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer` (and corresponding Batch scripts)
  have been removed from the PMD distribution. Please use the newer rule designer with `run.sh designer`.
  The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and `java.net.sourceforge.pmd.util.designer` have
  all been removed.

### External Contributions

*   [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga)
*   [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini)
*   [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini)
*   [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matias Fraga](https://github.com/matifraga)

{% endtocmaker %}

