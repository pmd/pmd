---
title: ADR 3 - API evolution principles
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions_adr_3.html
sidebaractiveurl: /pmd_projectdocs_decisions.html
adr: true
# Proposed / Accepted / Deprecated / Superseded
adr_status: "Proposed"
last_updated: November 2023
---

<!-- https://github.com/joelparkerhenderson/architecture-decision-record/blob/main/templates/decision-record-template-by-michael-nygard/index.md -->

# Context

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.


PMD is used and integrated by many different tools such as IDE plugins or build plugins.
Having a public stable API helps to make this
integration possible without much effort. But freezing the API will prevent further development.

In order to balance the two needs - stable API vs. space for new development - we need to
document principles on API evolution.

The actual API development and marking some part of the API as internal or add new API is an ongoing task,
that will need to be done everytime. We won't just define an API and then are done with it.
The API will change as new features want to be implemented.

This decision document aims to document

- what the criteria is for a public API
    - clear-cut between public APIs and internal
    - which packages to use, e.g. `internal` for internal "API"
    - when to use package `impl`
- how to deprecate and remove old APIs
    - how long to support a given API
    - with respect to semantic version
- when to define a new API as experimental
    - when to promote an experimental API as stable
- annotations to use, see also <https://docs.pmd-code.org/latest/pmd_release_notes_pmd7.html#new-api-support-guidelines>
- guidelines for AST classes (package private ctor, final, etc.)

Clearly defining the API PMD provides, makes sense and will help, if we at some time in the future want to
modularize PMD using java9. That would prevent API leaking then at compile time already...

# Decision

## Java packages names and structure
* PMD is mainly developed in the Java programming language. It consists of several modules.
The classes belonging to one module should be in the same package. From the package name, it should be clear to
which module this package belongs (there is a 1:1 mapping). This rule helps to find the source code for any
fully qualified (Java) class name.
* The base package for all PMD source code is `net.sourceforge.pmd`.
* Language modules, such as `pmd-java` will use the package `net.sourceforge.pmd.lang.java`, or in general:
  `net.sourceforge.pmd.lang.<language id>`.
* The core module `pmd-core` will use the base package as an exception.
* All other modules should use a subpackage of the base package, e.g. `pmd-cli` uses the package `net.sourceforge.pmd.cli`,
  or in general: `net.sourceforge.pmd.<module>`

Sub-package `impl`

Sub-package `internal`


## Criteria for public API



Not public API:

    Inheritance-specific members of AST related classes & interfaces. eg adding a member to an interface shouldn’t be considered api breaking
    Setters only used in the parser
    Exceptions… (I’ve seen JaxenException in the Node interface…)
    XPath, AttributeAxisIterator
    RuleSetFactory

public API

    AST structure
    Language Symbol Table / Metrics / Type Resolution …(Not the implementation!)
    Implementing custom Rules (AbstractRule, concrete Properties, …)
    Renderers
    Executing PMD, Ant Task, Configuration
    RuleSet XML

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

## Deprecation and removing of old APIs
Use @Deprecated during the transition (from the point we announce decide which APIs will be non-public until 7.0.0).
Update the 7.0.0 branch as we go along

## Experimental APIs

New Features, maybe @Incubating or @Experimental at first

## Guidelines for AST classes
maybe, concrete AST node classes should have a package-private constructor, and be final.
They're not meant to be instantiated by hand, but that could break some tests and rules

## Summary of the annotations

* `@InternalApi`
Full name: `net.sourceforge.pmd.annotation.InternalApi`

* `@ReservedSubclassing`
Full name: `net.sourceforge.pmd.annotation.ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

* `@Experimental`
Full name: `net.sourceforge.pmd.annotation.Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
on them in any production code. They are purely to allow broad testing and feedback.

* `@Deprecated`
Full name: `java.lang.Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release, but it is recommended to stop using them.

* `@DeprecatedUntil700`
Full name: `net.sourceforge.pmd.annotation.DeprecatedUntil700`



# Status

{{ page.adr_status }} (Last updated: {{ page.last_updated }})

# Consequences

What becomes easier or more difficult to do because of this change?

# Change History

YYYY-MM-DD: Add xyz.

YYYY-MM-DD: Proposed initial version.
