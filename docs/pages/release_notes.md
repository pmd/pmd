---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker %}

### New and noteworthy

#### Drawing a line between private and public API

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.


In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

##### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

##### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

##### `@Experimental`


APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

##### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.


##### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.

### Fixed Issues

*   java-codestyle
    *   [#1329](https://github.com/pmd/pmd/issues/1329): \[java] FieldNamingConventions: false positive in serializable class with serialVersionUID
    *   [#1334](https://github.com/pmd/pmd/issues/1334): \[java] LinguisticNaming should support AtomicBooleans
*   java-performance
    *   [#1325](https://github.com/pmd/pmd/issues/1325): \[java] False positive in ConsecutiveLiteralAppends

### API Changes

### External Contributions

*   [#1340](https://github.com/pmd/pmd/pull/1340): \[java] Derive correct classname for non-public non-classes - [kris-scheibe](https://github.com/kris-scheibe)

{% endtocmaker %}

