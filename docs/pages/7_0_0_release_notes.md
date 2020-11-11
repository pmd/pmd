---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FOR 7.0.0 -->
<!-- It must be used instead of release_notes.md when adding 7.0.0 changes -->
<!-- to avoid merge conflicts with master -->
<!-- It must replace release_notes.md when releasing 7.0.0 -->

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Full Antlr support

Languages backed by an Antlr grammar are now fully supported. This means, it's now possible not only to use Antlr grammars for CPD,
but we can actually build full-fledged PMD rules for them as well. Both the traditional Java visitor rules, and the simpler
XPath rules are available to users.

We expect this to enable both our dev team and external contributors to largely extend PMD usage for more languages.

#### Swift support

Given the full Antlr support, PMD now fully supports Swift. We are pleased to announce we are shipping a number of rules starting with PMD 7.

* {% rule "swift/errorprone/ForceCast" %} (`swift-errorprone`) flags all force casts, making sure you are defensively considering all types.
  Having the application crash shouldn't be an option.
* {% rule "swift/errorprone/ForceTry" %} (`swift-errorprone`) flags all force tries, making sure you are defensively handling exceptions.
  Having the application crash shouldn't be an option.
* {% rule "swift/bestpractices/ProhibitedInterfaceBuilder" %} (`swift-bestpractices`) flags any usage of interface builder. Interface builder
  files are prone to merge conflicts, and are impossible to code review, so larger teams usually try to avoid it or reduce it's usage.
* {% rule "swift/bestpractices/UnavailableFunction" %} (`swift-bestpractices`) flags any function throwing a `fatalError` not marked as
  `@available(*, unavailable)` to ensure no calls are actually performed in the codebase.

#### XPath 3.1 support

Support for XPath versions 1.0, 1.0-compatibility was removed, support for XPath 2.0 is deprecated. The default (and only) supported XPath version is now XPath 3.1. This version of the XPath language is mostly identical to XPath 2.0. Notable changes:
 * The deprecated support for sequence-valued attributes is removed. Sequence-valued properties are still supported.
 * Refer to [the Saxonica documentation](https://www.saxonica.com/html/documentation/expressions/xpath31new.html) for an introduction to new features in XPath 3.1.

#### JavaScript support

The JS specific parser options have been removed. The parser now always retains comments and uses version ES6.
The language module registers only one version (as before), now correctly with version "ES6" instead of "3".
Since there is only one version available for JavaScript there is actually no need to selected a specific version.
The default version is always ES6.

#### Changed Rules

##### Java

*   {% rule "java/codestyle/UnnecessaryFullyQualifiedName" %} has two new properties, to selectively disable reporting on
    static field and method qualifiers. The rule also has been improved to be more precise.
*   The rule {% rule "java/codestyle/UselessParentheses" %} has two new properties which control how strict
    the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
    not necessary are allowed, if they separate expressions of different precedence.
    The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
    reading and understanding the expressions.

#### Removed Rules

The following previously deprecated rules have been finally removed:

*   AbstractNaming (java-codestyle)
*   AvoidFinalLocalVariable (java-codestyle)
*   AvoidPrefixingMethodParameters (java-codestyle)
*   DataflowAnomalyAnalysis (java-errorprone)
*   ForLoopsMustUseBraces (java-codestyle)
*   IfElseStmtsMustUseBraces (java-codestyle)
*   IfStmtsMustUseBraces (java-codestyle)
*   LoggerIsNotStaticFinal (java-errorprone)
*   MIsLeadingVariableName (java-codestyle)
*   ModifiedCyclomaticComplexity (java-design)
*   StdCyclomaticComplexity (java-design)
*   SuspiciousConstantFieldName (java-codestyle)
*   UnsynchronizedStaticDateFormatter (java-multithreading)
*   VariableNamingConventions (apex-codestyle)
*   VariableNamingConventions (java-codestyle)
*   WhileLoopsMustUseBraces (java-codestyle)

#### Changed rules

##### Java

* {% rule "java/errorprone/EmptyCatchBlock" %}: `CloneNotSupportedException` and `InterruptedException` are not special-cased anymore. Rename the exception parameter to `ignored` to ignore them.


### Fixed Issues

* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342): \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#807](https://github.com/pmd/pmd/issues/807): \[java] AccessorMethodGeneration false positive with overloads
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @Rule field
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2796](https://github.com/pmd/pmd/issue/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
* java-codestyle
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
* java-errorprone
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case new BigDecimal(Expression)
    * [#2716](https://github.com/pmd/pmd/issues/2716): \[java] CompareObjectsWithEqualsRule: False positive with Enums
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ

### API Changes

* [#1648](https://github.com/pmd/pmd/pull/1702): \[apex,vf] Remove CodeClimate dependency - [Robert Sösemann](https://github.com/rsoesemann)
  Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" can no longer be overridden in rulesets.
  They were deprecated without replacement.

* The old GUI applications accessible through `run.sh designerold` and `run.sh bgastviewer` (and corresponding Batch scripts)
  have been removed from the PMD distribution. Please use the newer rule designer with `run.sh designer`.
  The corresponding classes in packages `java.net.sourceforge.pmd.util.viewer` and `java.net.sourceforge.pmd.util.designer` have
  all been removed.

* All API related to XPath support has been moved to the package {% jdoc_package core::lang.rule.xpath %}.
  This includes API that was previously dispersed over `net.sourceforge.pmd.lang`, `net.sourceforge.pmd.lang.ast.xpath`,
  `net.sourceforge.pmd.lang.rule.xpath`, `net.sourceforge.pmd.lang.rule`, and various language-specific packages 
  (which were made internal).

#### Metrics framework

* {% jdoc_old !!core::lang.metrics.MetricKeyUtil#of(java.lang.String, core::lang.metrics.Metric) %} is replaced with {% jdoc_old !!core::lang.metrics.MetricKey#of(java.lang.String, core::lang.metrics.Metric) %}
* {% jdoc_old !!core::lang.metrics.MetricsUtil#computeAggregate(core::lang.metrics.MetricKey, java.lang.Iterable, core::lang.metrics.ResultOption) %} and its overload are replaced with {% jdoc_old !!core::lang.metrics.MetricsUtil#computeStatistics(core::lang.metrics.MetricKey, java.lang.Iterable) %}, {% jdoc_old core::lang.metrics.ResultOption %} is removed

### External Contributions

*   [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga)
*   [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini)
*   [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini)
*   [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matias Fraga](https://github.com/matifraga)
*   [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca)

{% endtocmaker %}

