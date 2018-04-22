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
    *   [CPD Suppression](#cpd-suppression)
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

#### CPD Suppression

Back in PMD 5.6.0 we introduced the ability to suppress CPD warnings in Java using comments, by
including `CPD-OFF` (to start ignoring code), or `CPD-ON` (to resume analysis) during CPD execution.
This has proved to be much more flexible and versatile than the old annotation-based approach,
and has since been the preferred way to suppress CPD warnings.

On this ocassion, we are extending support for comment-based suppressions to many other languages:

*   C/C++
*   Ecmascript / Javascript
*   Matlab
*   Objective-C
*   PL/SQL
*   Python

So for instance, in Python we could now do:

```python
class BaseHandler(object):
    def __init__(self):
        # some unignored code

        # tell cpd to start ignoring code - CPD-OFF

        # mission critical code, manually loop unroll
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);

        # resume CPD analysis - CPD-ON

        # further code will *not* be ignored
```

Other languages are equivalent.

#### Modified Rules

*   The Java rule `UnnecessaryConstructor` (`java-codestyle`) has been rewritten as a Java rule (previously it was
    a XPath-based rule). It supports a new property `ignoredAnnotations` and ignores by default empty constructors,
    that are annotated with `javax.inject.Inject`. Additionally, it detects now also unnecessary private constructors
    in enums.


### Fixed Issues

*   all
    *   [#988](https://github.com/pmd/pmd/issues/988): \[core] FileNotFoundException for missing classes directory with analysis cache enabled
*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
*   java
    *   [#894](https://github.com/pmd/pmd/issues/894): \[java] Maven PMD plugin fails to process some files without any explanation
    *   [#899](https://github.com/pmd/pmd/issues/899): \[java] JavaTypeDefinitionSimple.toString can cause NPEs
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

