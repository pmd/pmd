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

*   java-bestpractices
    *   [#1365](https://github.com/pmd/pmd/issues/1365): \[java] JUnitTestsShouldIncludeAssert false positive
*   java-codestyle
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public->static

### API Changes

### External Contributions

*   [#1366](https://github.com/pmd/pmd/pull/1366): \[Java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1374](https://github.com/pmd/pmd/pull/1374): \[java] Simplify check for 'Test' annotation in JUnitTestsShouldIncludeAssertRule. - [Will Winder](https://github.com/winder)

{% endtocmaker %}

