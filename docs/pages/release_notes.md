---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.2.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.2.0.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Ecmascript (JavaScript)

The [Rhino Library](https://github.com/mozilla/rhino) has been upgraded from version 1.7.7 to version 1.7.7.2.

Detailed changes for changed in Rhino can be found:
* [For 1.7.7.2](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1772)
* [For 1.7.7.1](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1771)

Both are bugfixing releases.

### Fixed Issues

*   all
    *   [#928](https://github.com/pmd/pmd/issues/928): \[core] PMD build failure on Windows

*   java
    *   [#907](https://github.com/pmd/pmd/issues/907): \[java] UnusedPrivateField false-positive with @FXML

### API Changes

### External Contributions

* [#941](https://github.com/pmd/pmd/pull/941): \[java] Use char notation to represent a character to improve performance - [reudismam](https://github.com/reudismam)
* [#943](https://github.com/pmd/pmd/pull/943): \[java] UnusedPrivateField false-positive with @FXML - [BBG](https://github.com/djydewang)
* [#943](https://github.com/pmd/pmd/pull/967): \[doc] Issue 959: fixed broken link to XPath Rule Tutorial - [Andrey Mochalov](https://github.com/epidemia)
