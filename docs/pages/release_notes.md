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
    * [#2080](https://github.com/pmd/pmd/issues/2080): \[java] StringToString rule false-positive with field access
    * [#3437](https://github.com/pmd/pmd/issues/3437): \[java] StringToString doesn't trigger on Bar.class.getSimpleName().toString()
    * [#3681](https://github.com/pmd/pmd/issues/3681): \[java] StringToString doesn't trigger on string literals
    * [#3977](https://github.com/pmd/pmd/issues/3977): \[java] StringToString false-positive with local method name confusion

### API Changes

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)

{% endtocmaker %}

