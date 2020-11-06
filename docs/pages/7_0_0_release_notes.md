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

#### Changed Rules

##### Java

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

### Fixed Issues

* java-bestpractices
    * [#2796](https://github.com/pmd/pmd/issue/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
* java-codestyle
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation

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

