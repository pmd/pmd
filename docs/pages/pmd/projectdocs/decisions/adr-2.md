---
title: ADR 2 - Policy on the use of Kotlin for development
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions_adr_2.html
sidebaractiveurl: /pmd_projectdocs_decisions.html
adr: true
# Proposed / Accepted / Deprecated / Superseded
adr_status: "Accepted"
last_updated: September 2022
---

## Context

We currently use Kotlin only for unit tests at some places (e.g. pmd-lang-test module provides a couple of base
test classes). We were cautious to expand Kotlin because of poor development support outside JetBrain's
IntelliJ IDEA. E.g. the [Kotlin Plugin for Eclipse](https://marketplace.eclipse.org/content/kotlin-plugin-eclipse)
doesn't work properly as described in the reviews.

For VS Code there is a [Kotlin Plugin](https://marketplace.visualstudio.com/items?itemName=mathiasfrohlich.Kotlin)
with basic features. Online IDEs like gitpod.io and GitHub Codespaces are often based on VS Code.

Using Kotlin means, that we accept, that PMD can only be developed with IntelliJ IDEA. This feels like a vendor lock-in.

Also, bringing in a mix of languages might make maintenance a bit harder and make it harder for new contributors.
However - PMD is a tool that deals with many, many languages anyway, so this is maybe not a real argument.

Nevertheless, extending the usage of Kotlin within PMD can also increase contributions.

## Decision

We are generally open to the idea to increase usage of Kotlin within PMD. In order to gain experience
and to keep it within bounds and therefore maintainable we came up with the following rules:

* The module `pmd-core` should stay in plain Java. This helps in keeping binary compatibility when changing sources.
  `pmd-core` contains the main APIs for all language modules. We currently release all modules at the same time,
  so this is not a real problem for now. But that might change in the future: Because only few language modules have
  actual changes per release, it doesn't really make sense to release everything as long as the modules stay
  compatible. But that's another story.
* For (unit) testing, Kotlin can be used in `pmd-core` and in the language modules. The test frameworks can also
  use Kotlin (`pmd-test` doesn't yet, `pmd-lang-test` does already).
* Additionally: from now on, we allow to have the individual language modules be implemented in different languages
  when it makes sense. So, a language module might decide to use plain Java (like now) or also Kotlin.
* When mixing languages (e.g. Java + Kotlin), we need to care that the modules can still be used with plain Java.
  E.g. when writing custom rules: `pmd-java` provides a couple of APIs for rules (like symbol table, type resolution)
  and we should not force the users to use Kotlin (at least not for language modules which already exist and
  for which users might have written custom rules in Java already).
* It is also possible to write the entire language module in Kotlin only. Then the rules would be written in Kotlin
  as well. And the possible problems when mixing languages are gone. But that applies only for new language modules.
* When refactoring an existing language module from Java only to introduce Kotlin, care needs to be taken to
  not make incompatible changes. If compatibility (binary or source) can't be maintained, then that would be a
  major version change.

## Status

{{ page.adr_status }} (Last updated: {{ page.last_updated }})

## Consequences

Allowing more Kotlin in PMD can attract new contributions. It might make it easier to develop small DSLs.
In the future we might also consider to use other languages than Kotlin, e.g. for `pmd-scala` Scala might make sense.

On the other side, other IDEs than IntelliJ IDEA will have a difficult time to deal with PMD's source code
when Kotlin is used. Eclipse can't be used practically anymore.

Maintaining a polyglot code base with multiple languages is likely to be more challenging.

## Change History

2022-09-30: Changed status to "Accepted". ([#4072](https://github.com/pmd/pmd/pull/4072))

2022-07-28: Proposed initial version.
