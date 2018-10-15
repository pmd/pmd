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

#### Improved Golang CPD Support

Thanks to the work of [ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini),
Golang is now backed by a proper Antlr Grammar. This means CPD is now better at detecting duplicates,
as comments are recognized as such and ignored.

### Fixed Issues

*   all
    *   [#1272](https://github.com/pmd/pmd/issues/1272): \[core] Could not find or load main class when using symlinked run.sh
*   java
    *   [#1330](https://github.com/pmd/pmd/issues/1330): \[java] PMD crashes with java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/xml/ws/Service
*   java-bestpractices
    *   [#1202](https://github.com/pmd/pmd/issues/1202): \[java] GuardLogStatement: "There is log block not surrounded by if" doesn't sound right
    *   [#1365](https://github.com/pmd/pmd/issues/1365): \[java] JUnitTestsShouldIncludeAssert false positive
*   java-codestyle
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public-\>static

### API Changes

### External Contributions

*   [#1338](https://github.com/pmd/pmd/pull/1338): \[core] [cpd] Generalize ANTLR tokens preparing support for ANTLR token filter - [Matías Fraga](https://github.com/matifraga) and [Tomi De Lucca](https://github.com/tomidelucca)
*   [#1366](https://github.com/pmd/pmd/pull/1366): \[java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1374](https://github.com/pmd/pmd/pull/1374): \[java] Simplify check for 'Test' annotation in JUnitTestsShouldIncludeAssertRule. - [Will Winder](https://github.com/winder)
*   [#1375](https://github.com/pmd/pmd/pull/1375): \[java] Add missing null check AbstractJavaAnnotatableNode - [Will Winder](https://github.com/winder)
*   [#1376](https://github.com/pmd/pmd/pull/1376): \[core] Upgrading Apache Commons IO from 2.4 to 2.6 - [Thunderforge](https://github.com/Thunderforge)
*   [#1378](https://github.com/pmd/pmd/pull/1378): \[core] Upgrading Apache Commons Lang 3 from 3.7 to 3.8.1 - [Thunderforge](https://github.com/Thunderforge)
*   [#1383](https://github.com/pmd/pmd/pull/1383): \[java] Improved message for GuardLogStatement rule - [Felix Lampe](https://github.com/fblampe)
*   [#1386](https://github.com/pmd/pmd/pull/1386): \[go] [cpd] Add CPD support for Antlr based grammar on Golang - [Matías Fraga](https://github.com/matifraga)

{% endtocmaker %}

