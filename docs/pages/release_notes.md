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

#### New Rules

*   The new PLSQL rule {% rule "plsql/codestyle/CodeFormat" %} (`plsql-codestyle`) verifies that
    PLSQL code is properly formatted. It checks e.g. for correct indentation in select statements and verifies
    that each parameter is defined on a separate line.

### Fixed Issues

*   java
    *   [#1330](https://github.com/pmd/pmd/issues/1330): \[java] PMD crashes with java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/xml/ws/Service
*   java-codestyle
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public->static

### API Changes

### External Contributions

*   [#1366](https://github.com/pmd/pmd/pull/1366): \[Java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1375](https://github.com/pmd/pmd/pull/1375): \[java] Add missing null check AbstractJavaAnnotatableNode - [Will Winder](https://github.com/winder)

{% endtocmaker %}

