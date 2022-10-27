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
    * [#4091](https://github.com/pmd/pmd/issues/4091): \[java] AvoidArrayLoops false negative with do-while loops

### API Changes

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4147](https://github.com/pmd/pmd/pull/4147): \[java] Added support for Do-While for AvoidArrayLoops - [Yasar Shaikh](https://github.com/yasarshaikh) (@yasarshaikh)

{% endtocmaker %}

