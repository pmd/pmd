---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://pmd.github.io/pmd-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date }} - {{ site.pmd.version }}

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0]({{ baseurl }}pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

{% tocmaker is_release_notes_processor %}

### Changes since 7.0.0-rc1

This section lists the most important changes from the last release candidate.
The remaining section describe the complete release notes for 7.0.0.

Fixed Issues:
* java-codestyle
  * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
* java-errorprone
  * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo]({{ baseurl }}images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo]({{ baseurl }}images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

### 🌟 New and changed rules

#### New Rules

**Apex**
* {% rule apex/design/UnusedMethod %} finds unused methods in your code.

**Java**
* {% rule java/codestyle/UnnecessaryBoxing %} reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* {% rule kotlin/bestpractices/FunctionNameTooShort %}
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %}

**Swift**
* {% rule swift/bestpractices/ProhibitedInterfaceBuilder %}
* {% rule swift/bestpractices/UnavailableFunction %}
* {% rule swift/errorprone/ForceCast %}
* {% rule swift/errorprone/ForceTry %}

#### Changed Rules

**Java**

* {% rule "java/codestyle/UnnecessaryFullyQualifiedName" %}: the rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* {% rule "java/codestyle/UselessParentheses" %}: the rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.
* {% rule "java/bestpractices/LooseCoupling" %}: the rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* {% rule "java/errorprone/EmptyCatchBlock" %}: `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* {% rule "java/errorprone/DontImportSun" %}: `sun.misc.Signal` is not special-cased anymore.
* {% rule "java/codestyle/UseDiamondOperator" %}: the property `java7Compatibility` is removed. The rule now
  handles Java 7 properly without a property.
* {% rule "java/design/SingularField" %}: Properties `checkInnerClasses` and `disallowNotAssignment` are removed.
  The rule is now more precise and will check these cases properly.
* {% rule "java/design/UseUtilityClass" %}: The property `ignoredAnnotations` has been removed.
* {% rule "java/design/LawOfDemeter" %}: the rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* {% rule "java/documentation/CommentContent" %}: The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `fobiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties

### 💥 Compatibility and migration notes
See [Detailed Release Notes for PMD 7](pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @Rule field
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

###  ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)

### 📈 Stats
* 4416 commits
* 464 closed tickets & PRs
* Days since last release: 28

{% endtocmaker %}

