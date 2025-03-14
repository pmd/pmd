---
title: ADR 3 - API evolution principles
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions_adr_3.html
sidebaractiveurl: /pmd_projectdocs_decisions.html
adr: true
# Proposed / Accepted / Deprecated / Superseded
adr_status: "Accepted"
last_updated: February 2024
---

<!-- https://github.com/joelparkerhenderson/architecture-decision-record/blob/main/templates/decision-record-template-by-michael-nygard/index.md -->

## Context

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Until PMD 7.0.0, all released public members and types were implicitly considered part
of public PMD API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

PMD is used and integrated in many different tools such as IDE plugins or build plugins. These plugins
use our public API and rely on it being stable, hence we tried to break it only infrequently.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0 and onwards. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

The actual API development and marking some part of the API as internal or add new API is an ongoing task,
that will need to be done everytime. We won't just define an API and then are done with it.
The API will change as new features want to be implemented.

This decision document aims to document principles and guidelines that are used for PMD development.

## Decision

### Semantic Versioning

PMD and all its modules are versioned together. PMD uses [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).
This means, that each PMD version consists of MAJOR.MINOR.PATCH components:

* MAJOR version is incremented for incompatible API changes
* MINOR version is incremented for added functionality in a backwards compatible way
* PATCH version is incremented for backward compatible bug fixes

Additional labels for release candidates might be used.

Incompatible API changes shouldn't be introduced lightly. See
[FAQ: If even the tiniest backward incompatible changes to the public API require a major version bump, won’t I end up at version 42.0.0 very rapidly?](https://semver.org/spec/v2.0.0.html#if-even-the-tiniest-backward-incompatible-changes-to-the-public-api-require-a-major-version-bump-wont-i-end-up-at-version-4200-very-rapidly).

### Project structure and Java base packages names

PMD is mainly developed in the Java programming language. The build tool is Maven and the PMD build consists
of several maven modules.

* All packages belonging to a given module should have a common package prefix.
* Given a package name, it should be easy to figure out to which module this package belongs. There is a 1:1 mapping
  between maven module and package. This rule helps to find the source code for any fully qualified (Java) class name.
* Two modules must not define the same packages. That means, it is not allowed that any given package spans more than
  one module. Otherwise, the mapping between module and package wouldn't be unambiguous.
* The base package for all PMD source code is `net.sourceforge.pmd`. There are many different sub packages.
* The core module `pmd-core` uses directly the base package as the only module. All other modules must use
  specific sub packages.
* Language modules use the base package `net.sourceforge.pmd.lang.<language id>`.
  E.g. `pmd-java` uses the package `net.sourceforge.pmd.lang.java`.
* All other modules use the base package `net.sourceforge.pmd.<module>`,
  E.g. `pmd-cli` uses the package `net.sourceforge.pmd.cli`.

### Criteria for public API

Public API is

* API needed to execute PMD analysis
  * Renderers
  * RuleSet XML Schema
  * Configuration
  * Ant Tasks
* API needed to implement custom rules
  * AST structure and classes of languages (incl. AST structure for XPath rules)
  * XPath functions
  * Language Symbol Table / Metrics / Type Resolution (Not the implementation)

**Not** public API is

* Anything in packages `internal` and `impl`
* Inheritance-specific members of AST related classes and interfaces. E.g. adding a member to an
  interface shouldn't be considered API breaking
* Setters in AST classes are private. They are only used in the parser.

### Separation between public API, internal and implementation

All packages are considered to be public API by default, with **two exceptions**:

* Any package that contains an `internal` segment is considered internal. E.g. `net.sourceforge.pmd.internal`.
  *Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
  may be modified in any way, or even removed, at any time without a MAJOR version change.

  The `@InternalApi` annotation will be used for types that have to live outside of
  these packages, e.g. methods of a public type that shouldn't be used outside PMD (again,
  these can be removed anytime).

* Any package that contains an `impl` segment is considered internal. E.g. `net.sourceforge.pmd.lang.impl`.
  These packages contain base classes that are needed for extending PMD (like adding a new language).
  These can change at any time without a MAJOR version change.

  In a later version, the `impl` packages could be promoted as a public API for implementing new
  languages for PMD outside the main monorepo. In that sense, e.g. the module `pmd-java` is allowed
  to depend on `impl` packages of `pmd-core`, but ideally it doesn't depend on `internal` packages of
  `pmd-core` (or any other module). However, for now, the `impl` packages are **explicitly considered
  internal** until this decision is revised.

### Deprecation and removing of old APIs

* APIs can be deprecated at any time (even in PATCH versions). Deprecated APIs are marked with the
  `@Deprecated` annotation.
* Deprecations should be listed in the release notes.
* Deprecated APIs can only be removed with a MAJOR version change.

### Experimental APIs

* New features often introduce new APIs. These new APIs can be marked with the annotation `@Experimental` at
  the class or method level.
* APIs marked with the `@Experimental` annotation are subject to change and are considered **not stable**.
  They can be modified in any way, or even removed, at any time. You should not use or rely
  on them in any production code. They are purely to allow broad testing and feedback.
* Experimental APIs can be introduced or removed with at least a MINOR version change.
  These experimental APIs should be listed in the release notes.
* Experimental APIs can be promoted to Public APIs with at least a MINOR version change.

### Guidelines for AST classes

AST classes of the individual language modules are used by custom rule implementations and are considered
Public API in general. Rules only read the AST and do not need to modify it.

In order to minimize the public API surface of AST classes, the following guidelines apply:

* Concrete AST classes should be final, to avoid custom subclasses.
* Concrete AST classes should only have a package private constructor to avoid manual instantiation.
  Only the parser of the language (which lives in the same package) should be able to create new instances
  of AST classes.
* Concrete AST classes should not have public setters. All setters should be package private, so that
  only the parser of the language can call the setters during AST construction.

Non-concrete AST classes (like base classes or common interfaces) should follow similar guidelines:
* Only package private constructor
* Only package private setters

### Summary of the annotations

* `@InternalApi` (`net.sourceforge.pmd.annotation.InternalApi`)

  This annotation is used for API members that are not publicly supported API but have to live in
  public packages (outside `internal` packages).
  Such members may be removed, renamed, moved, or otherwise broken at any time and should not be
  relied upon outside the main PMD codebase.

* `@Experimental` (`net.sourceforge.pmd.annotation.Experimental`)

  API members marked with the `@Experimental` annotation at the class or method level are subject to change.
  It is an indication that the feature is in experimental, unstable state.
  The API members can be modified in any way, or even removed, at any time, without warning.
  You should not use or rely on them in any production code. They are purely to allow broad testing and feedback.

* `@Deprecated` (`java.lang.Deprecated`)

  API members marked with the `@Deprecated` annotation at the class or method level will remain supported
  until the next major release, but it is recommended to stop using them. These members might be
  removed with the next MAJOR release.

## Status

{{ page.adr_status }} (Last updated: {{ page.last_updated }})

## Consequences

* Clearly defining the API PMD provides will help to further modularize PMD using the
  Java [Module System](https://openjdk.org/jeps/261).
* Simpler decisions when to increase MAJOR, MINOR of PATCH version.
* Refactoring of the implementation is possible without affecting public API.

## Change History

2024-02-01: Changed status to "Accepted". ([#4756](https://github.com/pmd/pmd/pull/4756))

2023-12-01: Proposed initial version.
