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
* core
  * [#3881](https://github.com/pmd/pmd/issues/3881): \[core] SARIF renderer depends on platform default encoding
  * [#3882](https://github.com/pmd/pmd/pull/3882): \[core] Fix AssertionError about exhaustive switch
  * [#3884](https://github.com/pmd/pmd/issues/3884): \[core] XML report via ant task contains XML header twice
* doc
  * [#2505](https://github.com/pmd/pmd/issues/2505): \[doc] Improve side bar to show release date
* java
  * [#1474](https://github.com/pmd/pmd/issues/1474): \[java] ArrayIsStoredDirectly false positive with method call
  * [#3879](https://github.com/pmd/pmd/issues/3879) \[java] ArrayIsStoredDirectly reports duplicated violation
  * [#3889](https://github.com/pmd/pmd/pull/3889): \[java] Catch LinkageError in UselessOverridingMethodRule
* plsql
  * [#3706](https://github.com/pmd/pmd/issues/3706): \[plsql] Parsing exception CURSOR statement with parenthesis groupings

### API Changes

### External Contributions
* [#3883](https://github.com/pmd/pmd/pull/3883): \[doc] Improve side bar by Adding Release Date - [@jasonqiu98](https://github.com/jasonqiu98)
* [#3928](https://github.com/pmd/pmd/pull/3928): \[plsql] Fix plsql parsing error in parenthesis groups - [@LiGaOg](https://github.com/LiGaOg)

{% endtocmaker %}

