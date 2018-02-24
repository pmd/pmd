---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.1.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.1.0-SNAPSHOT.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Designer UI

The Designer now supports configuring properties for XPath based rule development.
The Designer is still under development and any feedback is welcome.

You can start the designer via `run.sh designer` or `designer.bat`.

### Fixed Issues

*   apex-errorprone
    *   [#792](https://github.com/pmd/pmd/issues/792): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes
*   apex-security
    *   [#788](https://github.com/pmd/pmd/issues/788): \[apex] Method chaining breaks ApexCRUDViolation
*   java-design
    *    [#785](https://github.com/pmd/pmd/issues/785): \[java] NPE in DataClass rule

### API Changes

### External Contributions

*   [#796](https://github.com/pmd/pmd/pull/796): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#799](https://github.com/pmd/pmd/pull/799): \[apex] Method chaining breaks ApexCRUDViolation - [Robert Sösemann](https://github.com/up2go-rsoesemann)
