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

#### Lua now supports additionally Luau

This release of PMD adds support for [Luau](https://github.com/Roblox/luau), a gradually typed language derived
from Lua. This means, that the Lua language in PMD can now parse both Lua and Luau.

#### Modified rules

*    The Java rule {% rule java/codestyle/CommentDefaultAccessModifier %} now by default ignores JUnit5 annotated
     methods. This behavior can be customized using the property `ignoredAnnotations`.

### Fixed Issues
* cli
    * [#4118](https://github.com/pmd/pmd/issues/4118): \[cli] run.sh designer reports "integer expression expected"
* core
    * [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Missing --file arg in TreeExport CLI example
* doc
    * [#4109](https://github.com/pmd/pmd/pull/4109): \[doc] Add page for 3rd party rulesets
    * [#4124](https://github.com/pmd/pmd/pull/4124): \[doc] Fix typos in Java rule docs
* java-codestyle
    * [#3859](https://github.com/pmd/pmd/issues/3859): \[java] CommentDefaultAccessModifier is triggered in JUnit5 test class
    * [#4085](https://github.com/pmd/pmd/issues/4085): \[java] UnnecessaryFullyQualifiedName false positive when nested and non-nested classes with the same name and in the same package are used together
* java-design
    * [#4090](https://github.com/pmd/pmd/issues/4090): \[java] FinalFieldCouldBeStatic false positive with non-static synchronized block (regression in 6.48, worked with 6.47)
* scala
    * [#4138](https://github.com/pmd/pmd/pull/4138): \[scala] Upgrade scala-library to 2.12.7 / 2.13.9 and scalameta to 4.6.0

### API Changes

#### CPD CLI

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

### Financial Contributions

Many thanks to our sponsors:

* [Oliver Siegmar](https://github.com/osiegmar) (@osiegmar)

### External Contributions
* [#4066](https://github.com/pmd/pmd/pull/4066): \[lua] Add support for Luau syntax and skipping literal sequences in CPD - [Matt Hargett](https://github.com/matthargett) (@matthargett)
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Fix missing --file arg in TreeExport CLI example - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4124](https://github.com/pmd/pmd/pull/4124): \[doc] Fix typos in Java rule docs - [Piotrek Żygieło](https://github.com/pzygielo) (@pzygielo)
* [#4128](https://github.com/pmd/pmd/pull/4128): \[java] Fix False-positive UnnecessaryFullyQualifiedName when nested and non-nest… #4103 - [Oleg Andreych](https://github.com/OlegAndreych) (@OlegAndreych)
* [#4131](https://github.com/pmd/pmd/pull/4131): \[doc] TooFewBranchesForASwitchStatement - Use "if-else" instead of "if-then" - [Suvashri](https://github.com/Suvashri) (@Suvashri)
* [#4137](https://github.com/pmd/pmd/pull/4137): \[java] Fixes 3859: Exclude junit5 test methods from the commentDefaultAccessModifierRule - [Luis Alcantar](https://github.com/lfalcantar) (@lfalcantar)

{% endtocmaker %}

