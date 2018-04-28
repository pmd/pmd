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
    *   [Tree Traversal Revision](#tree-traversal-revision)
    *   [Naming Rules Enhancements](#naming-rules-enhancements)
    *   [CPD Suppression](#cpd-suppression)
    *   [Swift 4.1 Support](#swift-41-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    *   [Deprecated Rules](#deprecated-rules)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Tree Traversal Revision

As described in [#904](https://github.com/pmd/pmd/issues/904), when searching for child nodes of the AST methods
such as `hasDescendantOfType`, `getFirstDescendantOfType` and `findDescendantsOfType` were found to behave inconsistently,
not all of them honoring find boundaries; that is, nodes that define a self-contained entity which should be considered separately
(think of lambdas, nested classes, anonymous classes, etc.). We have modified these methods to ensure all of them honor
find boundaries.

This change implies several false positives / unexpected results (ie: `ASTBlockStatement` falsely returning `true` to `isAllocation()`)
have been fixed; and lots of searches are now restricted to smaller search areas, which improves performance (depending on the project,
we have measured up to 10% improvements during Type Resolution, Symbol Table analysis, and some rules' application).

#### Naming Rules Enhancements

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

#### Swift 4.1 Support

Thanks to major contributions from [kenji21](https://github.com/kenji21) the Swift grammar has been updated to support Swift 4.1.
This is a major update, since the old grammar was quite dated, and we are sure all iOS developers will enjoy it.

Unfortunately, this change is not compatible. The grammar elements that have been removed (ie: the keywords `__FILE__`,
`__LINE__`, `__COLUMN__` and `__FUNCTION__`) are no longer supported. We don't usually introduce such drastic / breaking
changes in minor releases, however, given that the whole Swift ecosystem pushes hard towards always using the latest
versions, and that Swift needs all code and libraries to be currently compiling against the same Swift version,
we felt strongly this change was both safe and necessary to be shipped as soon as possible. We had great feedback
from the comunity during the processm but if you have a legitimate use case for older Swift versions, please let us know
[on our Issue Tracke](https://github.com/pmd/pmd/issues).

#### New Rules

*   The new Java rule [`InsecureCryptoIv`](pmd_rules_java_security.html#insecurecryptoiv) (`java-security`)
    detects hard coded initialization vectors used in cryptographic operations. It is recommended to use
    a randomly generated IV.

#### Modified Rules

*   The Java rule [`UnnecessaryConstructor`](pmd_rules_java_codestyle.html#unnecessaryconstructor) (`java-codestyle`)
    has been rewritten as a Java rule (previously it was a XPath-based rule). It supports a new property
    `ignoredAnnotations` and ignores by default empty constructors,
    that are annotated with `javax.inject.Inject`. Additionally, it detects now also unnecessary private constructors
    in enums.

### Fixed Issues

*   all
    *   [#695](https://github.com/pmd/pmd/issues/695): \[core] Extend comment-based suppression to all JavaCC languages
    *   [#988](https://github.com/pmd/pmd/issues/988): \[core] FileNotFoundException for missing classes directory with analysis cache enabled
    *   [#1036](https://github.com/pmd/pmd/issues/1036): \[core] Non-XML output breaks XML-based CLI integrations
*   apex-errorprone
    *   [#776](https://github.com/pmd/pmd/issues/776): \[apex] AvoidHardcodingId false positives
*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
*   java
    *   [#894](https://github.com/pmd/pmd/issues/894): \[java] Maven PMD plugin fails to process some files without any explanation
    *   [#899](https://github.com/pmd/pmd/issues/899): \[java] JavaTypeDefinitionSimple.toString can cause NPEs
    *   [#1020](https://github.com/pmd/pmd/issues/1020): \[java] The CyclomaticComplexity rule runs forever in 6.2.0
    *   [#1030](https://github.com/pmd/pmd/pull/1030): \[java] NoClassDefFoundError when analyzing PMD with PMD
    *   [#1061](https://github.com/pmd/pmd/issues/1061): \[java] Update ASM to handle Java 10 bytecode
*   java-bestpractices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas
    *   [#558](https://github.com/pmd/pmd/issues/558): \[java] ProperLogger Warnings for enums
    *   [#719](https://github.com/pmd/pmd/issues/719): \[java] Unused Code: Java 8 receiver parameter with an internal class
    *   [#1009](https://github.com/pmd/pmd/issues/1009): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#1003](https://github.com/pmd/pmd/issues/1003): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject)
    *   [#1023](https://github.com/pmd/pmd/issues/1023): \[java] False positive for useless parenthesis
*   java-design
    *   [#1056](https://github.com/pmd/pmd/issues/1056): \[java] Property ignoredAnnotations does not work for SingularField and ImmutableField
*   java-errorprone
    *   [#629](https://github.com/pmd/pmd/issues/629): \[java] NullAssignment false positive
    *   [#816](https://github.com/pmd/pmd/issues/816): \[java] SingleMethodSingleton false positives with inner classes
*   java-performance
    *   [#586](https://github.com/pmd/pmd/issues/586): \[java] AvoidUsingShortType erroneously triggered on overrides of 3rd party methods
*   swift
    *   [#678](https://github.com/pmd/pmd/issues/678): \[swift][cpd] Exception when running for Swift 4 code (KeyPath)

### API Changes

#### Deprecated Rules

  * The Java rule `AbstractNaming` (category `codestyle`) is deprecated
  in favour of [`ClassNamingConventions`](pmd_rules_java_codestyle.html#classnamingconventions).
  See [Naming rules enhancements](#naming-rules-enhancements).

### External Contributions

*   [#778](https://github.com/pmd/pmd/pull/778): \[swift] Support Swift 4 grammar - [kenji21](https://github.com/kenji21)
*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
*   [#1010](https://github.com/pmd/pmd/pull/1010): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject) - [BBG](https://github.com/djydewang)
*   [#1012](https://github.com/pmd/pmd/pull/1012): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5 - [BBG](https://github.com/djydewang)
*   [#1024](https://github.com/pmd/pmd/pull/1024): \[java] Issue 558: Properlogger for enums - [Utku Cuhadaroglu](https://github.com/utkuc)
*   [#1041](https://github.com/pmd/pmd/pull/1041): \[java] Make BasicProjectMemoizer thread safe. - [bergander](https://github.com/bergander)
*   [#1042](https://github.com/pmd/pmd/pull/1042): \[java] New security rule: report usage of hard coded IV in crypto operations - [Sergey Gorbaty](https://github.com/sgorbaty)
*   [#1044](https://github.com/pmd/pmd/pull/1044): \[java] Fix for issue #816 - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1048](https://github.com/pmd/pmd/pull/1048): \[core] Make MultiThreadProcessor more space efficient - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#1062](https://github.com/pmd/pmd/pull/1062): \[core] Update ASM to version 6.1.1 - [Austin Shalit](https://github.com/AustinShalit)

