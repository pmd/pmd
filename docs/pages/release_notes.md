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

### API Changes

*   The `findDescendantsOfType` methods in `net.sourceforge.pmd.lang.ast.AbstractNode` no longer search for exact type matches, but will
    match subclasses too. That means, it's now possible to look for abstract node types such as `AbstractJavaTypeNode` and not only for it's concrete subtypes.

### External Contributions

