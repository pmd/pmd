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

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.21.0).

### Fixed Issues

*   java
    *   [#2268](https://github.com/pmd/pmd/issues/2268): \[java] Improve TypeHelper resilience
*   java-errorprone
    *   [#2250](https://github.com/pmd/pmd/issues/2250): \[java] InvalidLogMessageFormat flags logging calls using a slf4j-Marker
*   java-performance
    *   [#2275](https://github.com/pmd/pmd/issues/2275): \[java] AppendCharacterWithChar flags literals in an expression

### API Changes

#### Deprecated API

* {% jdoc core::lang.dfa.DFAGraphRule %} and its implementations
* {% jdoc core::lang.dfa.DFAGraphMethod %}

### External Contributions

*   [#2251](https://github.com/pmd/pmd/pull/2251): \[java] FP for InvalidLogMessageFormat when using slf4j-Markers - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2253](https://github.com/pmd/pmd/pull/2253): \[modelica] Remove duplicated dependencies - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#2256](https://github.com/pmd/pmd/pull/2256): \[doc] Corrected XML attributes in release notes - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2276](https://github.com/pmd/pmd/pull/2276): \[java] AppendCharacterWithCharRule ignore literals in expressions - [Kris Scheibe](https://github.com/kris-scheibe)

{% endtocmaker %}

