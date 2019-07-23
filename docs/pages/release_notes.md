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

#### Lua support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Lua](https://www.lua.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

#### Modified Rules

*   The Java rule {% rule "java/errorprone/MissingStaticMethodInNonInstantiatableClass" %} (`java-errorprone`) has now
    the new property `annotations`.
    When one of the private constructors is annotated with one of the annotations, then the class is not considered
    non-instantiatable anymore and no violation will be reported. By default, Spring's `@Autowired` and
    Java EE's `@Inject` annotations are recognized.

### Fixed Issues

*   doc
    *   [#1896](https://github.com/pmd/pmd/issues/1896): \[doc] Error in changelog 6.16.0 due to not properly closed rule tag
    *   [#1906](https://github.com/pmd/pmd/issues/1906): \[doc] Broken link for adding own CPD languages
    *   [#1909](https://github.com/pmd/pmd/issues/1909): \[doc] Sample usage example refers to deprecated ruleset "basic.xml" instead of "quickstart.xml"
*   java-errorprone
    *   [#1832](https://github.com/pmd/pmd/issues/1832): \[java] False positives for MissingStaticMethodInNonInstantiatableClass when DI is used
*   xml
    *   [#1666](https://github.com/pmd/pmd/issues/1666): \[xml] wrong cdata rule description and examples

### API Changes

### External Contributions

*   [#1869](https://github.com/pmd/pmd/pull/1869): \[xml] fix #1666 wrong cdata rule description and examples - [Artem](https://github.com/KroArtem)
*   [#1892](https://github.com/pmd/pmd/pull/1892): \[lua] \[cpd] Added CPD support for Lua - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1908](https://github.com/pmd/pmd/pull/1908): \[doc] Update ruleset filename from deprecated basic.xml to quickstart.xml - [crunsk](https://github.com/crunsk)
*   [#1916](https://github.com/pmd/pmd/pull/1916): \[java] Exclude Autowired and Inject for MissingStaticMethodInNonInstantiatableClass - [AnthonyKot](https://github.com/AnthonyKot)

{% endtocmaker %}

