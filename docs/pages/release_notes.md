---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.1.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.1.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

### Fixed Issues

*   all
    *   [#569](https://github.com/pmd/pmd/issues/569): \[core] XPath support requires specific toString implementations
    *   [#795](https://github.com/pmd/pmd/issues/795): \[cpd] java.lang.OutOfMemoryError
    *   [#848](https://github.com/pmd/pmd/issues/848): \[doc] Test failures when building pmd-doc under Windows
    *   [#872](https://github.com/pmd/pmd/issues/872): \[core] NullPointerException at FileDataSource.glomName()
    *   [#854](https://github.com/pmd/pmd/issues/854): \[ci] Use Java9 for building PMD
*   doc
    *   [#791](https://github.com/pmd/pmd/issues/791): \[doc] Documentation site reorganisation
    *   [#891](https://github.com/pmd/pmd/issues/891): \[doc] Apex @SuppressWarnings should use single quotes instead of double quotes
    *   [#909](https://github.com/pmd/pmd/issues/909): \[doc] Please add new PMD Eclipse Plugin to tool integration section
*   java
    *   [#825](https://github.com/pmd/pmd/issues/825): \[java] Excessive\*Length ignores too much
    *   [#888](https://github.com/pmd/pmd/issues/888): \[java] ParseException occurs with valid '<>' in Java 1.8 mode
    *   [#920](https://github.com/pmd/pmd/pull/920): \[java] Update valid identifiers in grammar
*   java-bestpractices
    *   [#784](https://github.com/pmd/pmd/issues/784): \[java] ForLoopCanBeForeach false-positive
*   java-design
    *   [#855](https://github.com/pmd/pmd/issues/855): \[java] ImmutableField false-positive with lambdas
*   java-documentation
    *   [#877](https://github.com/pmd/pmd/issues/877): \[java] CommentRequired valid rule configuration causes PMD error
*   java-errorprone
    *   [#885](https://github.com/pmd/pmd/issues/885): \[java] CompareObjectsWithEqualsRule trigger by enum1 != enum2
*   java-performance
    *   [#541](https://github.com/pmd/pmd/issues/541): \[java] ConsecutiveLiteralAppends with types other than string
*   scala
    *   [#853](https://github.com/pmd/pmd/issues/853): \[scala] Upgrade scala version to support Java 9
*   xml
    *   [#739](https://github.com/pmd/pmd/issues/739): \[xml] IllegalAccessException when accessing attribute using Saxon on JRE 9


### API Changes

#### Changes to the Node interface

The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
A default implementation is provided in `AbstractNode`, to stay compatible
with existing implementors.

The `toString` method of a Node is not changed for the time being, and still produces
the name of the XPath node. That behaviour may however change in future major releases,
e.g. to produce a more useful message for debugging.

#### Changes to CPD renderers

The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface `net.sourceforge.pmd.cpd.renderer.CPDRenderer`
has been introduced to replace it. The main difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on large projects, with many duplications,
it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

`net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

### External Contributions

*   [#790](https://github.com/pmd/pmd/pull/790): \[java] Added some comments for JDK 9 - [Tobias Weimer](https://github.com/tweimer)
*   [#803](https://github.com/pmd/pmd/pull/803): \[doc] Added SpotBugs as successor of FindBugs - [Tobias Weimer](https://github.com/tweimer)
*   [#828](https://github.com/pmd/pmd/pull/828): \[core] Add operations to manipulate a document - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#830](https://github.com/pmd/pmd/pull/830): \[java] UseArraysAsList: Description added - [Tobias Weimer](https://github.com/tweimer)
*   [#845](https://github.com/pmd/pmd/pull/845): \[java] Fix false negative PreserveStackTrace on string concatenation - [Alberto Fernández](https://github.com/albfernandez)
*   [#868](https://github.com/pmd/pmd/pull/868): \[core] Improve XPath documentation && make small refactors - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#875](https://github.com/pmd/pmd/pull/875): \[core] Support shortnames when using filelist - [John Zhang](https://github.com/johnjiabinzhang)
*   [#886](https://github.com/pmd/pmd/pull/886): \[java] Fix #885 - [Matias Comercio](https://github.com/MatiasComercio)
*   [#900](https://github.com/pmd/pmd/pull/900): \[core] Use the isEmpty method instead of comparing the value of size() to 0 - [reudismam](https://github.com/reudismam)
*   [#914](https://github.com/pmd/pmd/pull/914): \[doc] Apex @SuppressWarnings documentation updated - [Akshat Bahety](https://github.com/akshatbahety)
*   [#918](https://github.com/pmd/pmd/pull/918): \[doc] Add qa-eclipse as new tool - [Akshat Bahety](https://github.com/akshatbahety)

