---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.0.1-SNAPSHOT

The PMD team is pleased to announce PMD 6.0.1-SNAPSHOT.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

### Fixed Issues

*   apex-errorprone
    *   [#792](https://github.com/pmd/pmd/issues/792): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes
*   apex-security
    *   [#788](https://github.com/pmd/pmd/issues/788): \[apex] Method chaining breaks ApexCRUDViolation
*   doc
    *   [#794](https://github.com/pmd/pmd/issues/794): \[doc] Broken documentation links for 6.0.0
*   java
    *   [#783](https://github.com/pmd/pmd/issues/783): \[java] GuardLogStatement regression
    *   [#793](https://github.com/pmd/pmd/issues/793): \[java] Parser error with private method in nested classes in interfaces
*   java-bestpractices
    *   [#800](https://github.com/pmd/pmd/issues/800): \[java] ForLoopCanBeForeach NPE when looping on `this` object
*   java-design
    *   [#785](https://github.com/pmd/pmd/issues/785): \[java] NPE in DataClass rule

### API Changes

### External Contributions

*   [#796](https://github.com/pmd/pmd/pull/796): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#799](https://github.com/pmd/pmd/pull/799): \[apex] Method chaining breaks ApexCRUDViolation - [Robert Sösemann](https://github.com/up2go-rsoesemann)
