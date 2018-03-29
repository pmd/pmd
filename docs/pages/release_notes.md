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
  * [Tree transversal revision](#tree-transversal-revision)
  * [Naming rules enhancements](#naming-rules-enhancements)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
  * [Deprecated Rules](#deprecated-rules)
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


#### Naming rules enhancements

 * [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions)
  has been enhanced to allow granular configuration of naming
  conventions for different kinds of type declarations (eg enum or abstract
  class). Each kind of declaration can use its own naming convention
  using a regex property. See the rule's documentation for more info about
  configuration and default conventions.


### Fixed Issues

*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
*   java-bestpracrtices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas

### API Changes

#### Deprecated Rules

  * The Java rule `AbstractNaming` (category `codestyle`) is deprecated
  in favour of [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions).
  See [Naming rules enhancements](#naming-rules-enhancements).




### External Contributions

*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
