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

#### Modified rules

*   The Java rule {% rule java/bestpractices/UnusedPrivateField %} has a new property `ignoredFieldNames`.
    The default ignores serialization-specific fields (eg `serialVersionUID`).
    The property can be used to ignore more fields based on their name.
    Note that the rule used to ignore fields named `IDENT`, but doesn't anymore (add this value to the property to restore the old behaviour).

### Fixed Issues
* core
  * [#3881](https://github.com/pmd/pmd/issues/3881): \[core] SARIF renderer depends on platform default encoding
  * [#3882](https://github.com/pmd/pmd/pull/3882): \[core] Fix AssertionError about exhaustive switch
  * [#3884](https://github.com/pmd/pmd/issues/3884): \[core] XML report via ant task contains XML header twice
  * [#3896](https://github.com/pmd/pmd/pull/3896): \[core] Fix ast-dump CLI when reading from stdin
* doc
  * [#2505](https://github.com/pmd/pmd/issues/2505): \[doc] Improve side bar to show release date
* java
  * [#3068](https://github.com/pmd/pmd/issues/3068): \[java] Some tests should not depend on real rules
  * [#3889](https://github.com/pmd/pmd/pull/3889): \[java] Catch LinkageError in UselessOverridingMethodRule
  * [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable
* plsql
  * [#3706](https://github.com/pmd/pmd/issues/3706): \[plsql] Parsing exception CURSOR statement with parenthesis groupings

### API Changes

### External Contributions
* [#3883](https://github.com/pmd/pmd/pull/3883): \[doc] Improve side bar by Adding Release Date - [@jasonqiu98](https://github.com/jasonqiu98)
* [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable - [@laoseth](https://github.com/laoseth)
* [#3928](https://github.com/pmd/pmd/pull/3928): \[plsql] Fix plsql parsing error in parenthesis groups - [@LiGaOg](https://github.com/LiGaOg)

{% endtocmaker %}

