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

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties

### API Changes

#### Deprecated APIs

* The interface `net.sourceforge.pmd.lang.java.ast.Dimensionable` has been deprecated.
  It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

* Several methods from LocalVariableDeclaration and FieldDeclaration have also been
  deprecated:
  * FieldDeclaration won't be a TypeNode come 7.0.0, so `getType` and `getTypeDefinition` are deprecated
  * The method `getVariableName` on those two nodes will be removed too

  All these are deprecated because those nodes may declare several variables at once, possibly
  with different types (and obviously with different names). They both implement `Iterator<ASTVariableDeclaratorId>`
  though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910)



### External Contributions

*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)

{% endtocmaker %}

