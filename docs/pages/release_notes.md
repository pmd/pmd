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
    *   [Tree transversal revision](#tree-transversal-revision)
    *   [Naming rules enhancements](#naming-rules-enhancements)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    *   [Deprecated Rules](#deprecated-rules)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Tree transversal revision

As described in [#904](https://github.com/pmd/pmd/issues/904), when searching for child nodes of the AST methods
such as `hasDescendantOfType`, `getFirstDescendantOfType` and `findDescendantsOfType` were found to behave inconsistently,
not all of them honoring find boundaries; that is, nodes that define a self-contained entity which should be considered separately
(think of lambdas, nested classes, anonymous classes, etc.). We have modified these methods to ensure all of them honor
find boundaries.

This change implies several false positives / unexpected results (ie: `ASTBlockStatement` falsely returning `true` to `isAllocation()`)
have been fixed; and lots of searches are now restricted to smaller search areas, which improves performance (depending on the project,
we have measured up to 10% improvements during Type Resolution, Symbol Table analysis, and some rule's application).

#### Naming rules enhancements

 *   [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions)
     has been enhanced to allow granular configuration of naming
     conventions for different kinds of type declarations (eg enum or abstract
     class). Each kind of declaration can use its own naming convention
     using a regex property. See the rule's documentation for more info about
     configuration and default conventions.

 *  [`MethodNamingConventions`](pmd_rules_java_codestyle.html#methodnamingconventions)
    has been enhanced in the same way.

#### Modified Rules

*   The Java rule `UnnecessaryConstructor` (`java-codestyle`) has been rewritten as a Java rule (previously it was
    a XPath-based rule). It supports a new property `ignoredAnnotations` and ignores by default empty constructors,
    that are annotated with `javax.inject.Inject`. Additionally, it detects now also unnecessary private constructors
    in enums.

*   The property `checkNativeMethods` of the Java rule [`MethodNamingConventions`](pmd_rules_java_codestyle.html#methodnamingconventions)
    is now deprecated, as it is now superseded by `nativePattern`. Support for that property will be maintained until
    7.0.0.


### Fixed Issues

*   all
    *   [#988](https://github.com/pmd/pmd/issues/988): \[core] FileNotFoundException for missing classes directory with analysis cache enabled
*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
*   java
    *   [#894](https://github.com/pmd/pmd/issues/894): \[java] Maven PMD plugin fails to process some files without any explanation
    *   [#899](https://github.com/pmd/pmd/issues/899): \[java] JavaTypeDefinitionSimple.toString can cause NPEs
    *   [#1030](https://github.com/pmd/pmd/pull/1030): \[java] NoClassDefFoundError when analyzing PMD with PMD
*   java-bestpractices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas
    *   [#558](https://github.com/pmd/pmd/issues/558): \[java] ProperLogger Warnings for enums
    *   [#719](https://github.com/pmd/pmd/issues/719): \[java] Unused Code: Java 8 receiver parameter with an internal class
    *   [#1009](https://github.com/pmd/pmd/issues/1009): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#1003](https://github.com/pmd/pmd/issues/1003): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject)
    *   [#1023](https://github.com/pmd/pmd/issues/1023): \[java] False positive for useless parenthesis
*   java-performance
    *   [#586](https://github.com/pmd/pmd/issues/586): \[java] AvoidUsingShortType erroneously triggered on overrides of 3rd party methods

### API Changes

#### Deprecated Rules

  * The Java rule `AbstractNaming` (category `codestyle`) is deprecated
  in favour of [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions).
  See [Naming rules enhancements](#naming-rules-enhancements).

### External Contributions

*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
*   [#1010](https://github.com/pmd/pmd/pull/1010): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject) - [BBG](https://github.com/djydewang)
*   [#1012](https://github.com/pmd/pmd/pull/1012): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5 - [BBG](https://github.com/djydewang)
*   [#1024](https://github.com/pmd/pmd/pull/1024): \[java]Issue 558: Properlogger for enums - [Utku Cuhadaroglu](https://github.com/utkuc)

