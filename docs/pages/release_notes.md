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
    *   [#848](https://github.com/pmd/pmd/issues/848): \[doc] Test failures when building pmd-doc under Windows
    *   [#872](https://github.com/pmd/pmd/issues/872): \[core] NullPointerException at FileDataSource.glomName()
*   doc
    *   [#791](https://github.com/pmd/pmd/issues/791): \[doc] Documentation site reorganisation
*   java
    *   [#825](https://github.com/pmd/pmd/issues/825): \[java] Excessive*Length ignores too much

### API Changes

#### Changes to the Node interface

The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
A default implementation is provided in `AbstractNode`, to stay compatible
with existing implementors.

The `toString` method of a Node is not changed for the time being, and still produces
the name of the XPath node. That behaviour may however change in future major releases,
e.g. to produce a more useful message for debugging.

### External Contributions

*   [#790](https://github.com/pmd/pmd/pull/790): \[java] Added some comments for JDK 9 - [Tobias Weimer](https://github.com/tweimer)
*   [#803](https://github.com/pmd/pmd/pull/803): \[doc] Added SpotBugs as successor of FindBugs - [Tobias Weimer](https://github.com/tweimer)
*   [#828](https://github.com/pmd/pmd/pull/828): \[core] Add operations to manipulate a document - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#830](https://github.com/pmd/pmd/pull/830): \[java] UseArraysAsList: Description added - [Tobias Weimer](https://github.com/tweimer)
*   [#845](https://github.com/pmd/pmd/pull/845): \[java] Fix false negative PreserveStackTrace on string concatenation - [Alberto Fernández](https://github.com/albfernandez)
*   [#868](https://github.com/pmd/pmd/pull/868): \[core] Improve XPath documentation && make small refactors - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#875](https://github.com/pmd/pmd/pull/875): \[core] Support shortnames when using filelist - [John Zhang](https://github.com/johnjiabinzhang)

