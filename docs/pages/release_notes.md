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

*   java
    *   [#1330](https://github.com/pmd/pmd/issues/1330): \[java] PMD crashes with java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/xml/ws/Service
*   java-bestpractices
    *   [#1365](https://github.com/pmd/pmd/issues/1365): \[java] JUnitTestsShouldIncludeAssert false positive
*   java-codestyle
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public-\>static

### API Changes

### External Contributions

*   [#1366](https://github.com/pmd/pmd/pull/1366): \[Java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1374](https://github.com/pmd/pmd/pull/1374): \[java] Simplify check for 'Test' annotation in JUnitTestsShouldIncludeAssertRule. - [Will Winder](https://github.com/winder)
*   [#1375](https://github.com/pmd/pmd/pull/1375): \[java] Add missing null check AbstractJavaAnnotatableNode - [Will Winder](https://github.com/winder)
*   [#1376](https://github.com/pmd/pmd/pull/1376): \[core] Upgrading Apache Commons IO from 2.4 to 2.6 - [Thunderforge](https://github.com/Thunderforge)
*   [#1382](https://github.com/pmd/pmd/pull/1382): \[all] Replacing deprecated IO methods with ones that specify a charset - [Thunderforge](https://github.com/Thunderforge)

{% endtocmaker %}

