---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.3.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.3.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Tree transversal revision](#tree-transversal-revision)
    *   [Swift 4.1 Support](#swift-41-support)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Tree transversal revision

As described in [#904](https://github.com/pmd/pmd/issues/904), when searching for child nodes of the AST methods
such as `hasDescendantOfType`, `getFirstDescendantOfType` and `findDescendantsOfType` were found to behave inconsistently,
not all of them honoring find boundaries; that is, nodes that define a self-contained entity which should be considered separately
(think of lambdas, nested classes, anonymous classes, etc.). We have modified these methods to ensure all of them honor
find boundaries.

This change implies several false positives / unexpected results (ie: `ASTBlockStatement` falsely returning `true` to `isAllocation()`)
have been fixed; and lots of searches are now restricted to smaller search areas, which improves performance (depending on the project,
we have measured up to 10% improvements during Type Resolution, Symbol Table analysis, and some rule's application).

#### Swift 4.1 Support

Thanks to major contributions from [kenji21](https://github.com/kenji21) the Swift grammar has been updated to support Swift 4.1.
This is a major update, since the old grammar was quite dated, and we are sure all iOS developers will enjoy it.

Unfortunately, this change is not compatible. The grammar elements that have been removed (ie: the keywords `__FILE__`,
`__LINE__`, `__COLUMN__` and `__FUNCTION__`) are no longer supported. We don't usually introduce such drastic / breaking
changes in minor releases, however, given that the whole Swift ecosystem pushes hard towards always using the latest
versions, and that Swift needs all code and libraries to be currently compiling against the same Swift version,
we felt strongly this change was both safe and necessary to be shipped as soon as possible. We had great feedback
from the comunity during the processm but if you have a legitimate use case for older Swift versions, please let us know
[on our Issue Tracke](https://github.com/pmd/pmd/issues).

### Fixed Issues

*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
*   java-bestpracrtices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas
*   swift
    *   [#678](https://github.com/pmd/pmd/issues/678): \[swift][cpd] Exception when running for Swift 4 code (KeyPath)

### API Changes

### External Contributions

*   [#778](https://github.com/pmd/pmd/pull/778): \[swift] Support Swift 4 grammar - [kenji21](https://github.com/kenji21)
*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
