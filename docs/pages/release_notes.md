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
  * [Naming rules enhancements](#naming-rules-enhancements)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
  * [Deprecated Rules](#deprecated-rules)
* [External Contributions](#external-contributions)

### New and noteworthy


#### Naming rules enhancements

 * [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions)
  has been enhanced to allow granular configuration of naming
  conventions for different kinds of type declarations (eg enum or abstract
  class). Each kind of declaration can use its own naming convention
  using a regex property. See the rule's documentation for more info about
  configuration and default conventions.


### Fixed Issues

### API Changes

#### Deprecated Rules

  * The Java rule `AbstractNaming` (category `codestyle`) is deprecated
  in favour of [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions).
  See [Naming rules enhancements](#naming-rules-enhancements).




### External Contributions
