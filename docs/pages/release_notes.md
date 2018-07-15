---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.6.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.6.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

### Fixed Issues

*   doc
    *   [#1215](https://github.com/pmd/pmd/issues/1215): \[doc] TOC links don't work?
*   java-codestyle
    *   [#1211](https://github.com/pmd/pmd/issues/1211): \[java] CommentDefaultAccessModifier false positive with nested interfaces (regression from 6.4.0)
    *   [#1216](https://github.com/pmd/pmd/issues/1216): \[java] UnnecessaryFullyQualifiedName false positive for the same name method
*   java-design
    *   [#1217](https://github.com/pmd/pmd/issues/1217): \[java] CyclomaticComplexityRule counts ?-operator twice
*   plsql
    *   [#980](https://github.com/pmd/pmd/issues/980): \[plsql] ParseException for CREATE TABLE
    *   [#981](https://github.com/pmd/pmd/issues/981): \[plsql] ParseException when parsing VIEW
    *   [#1047](https://github.com/pmd/pmd/issues/1047): \[plsql] ParseException when parsing EXECUTE IMMEDIATE
*   ui
    *   [#1233](https://github.com/pmd/pmd/issues/1233): \[ui] XPath autocomplete arrows on first and last items

### API Changes

*   The `findDescendantsOfType` methods in `net.sourceforge.pmd.lang.ast.AbstractNode` no longer search for exact type matches, but will
    match subclasses too. That means, it's now possible to look for abstract node types such as `AbstractJavaTypeNode` and not only for it's concrete subtypes.

### External Contributions

* [#1182](https://github.com/pmd/pmd/pull/1182): \[ui] XPath AutoComplete - [Akshat Bahety](https://github.com/akshatbahety)
* [#1231](https://github.com/pmd/pmd/pull/1231): \[doc] Minor typo fix in installation.md - [Ashish Rana](https://github.com/ashishrana160796)
