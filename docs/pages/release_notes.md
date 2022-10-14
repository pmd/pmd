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
* doc
    * [#4144](https://github.com/pmd/pmd/pull/4144) \[doc] Update docs to reflect supported languages
* java-documentation
    * [#4141](https://github.com/pmd/pmd/issues/4141): \[java] UncommentedEmptyConstructor FP when constructor annotated with @<!-- -->Autowired
* java-performance
    * [#1167](https://github.com/pmd/pmd/issues/1167): \[java] AvoidArrayLoops false positive on double assignment
    * [#2692](https://github.com/pmd/pmd/issues/2692): \[java] \[doc] AvoidArrayLoops flags copy assignment in same array as sub-optimal
    * [#3847](https://github.com/pmd/pmd/issues/3847): \[java] AvoidArrayLoops should consider final variables

### API Changes

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)

{% endtocmaker %}

