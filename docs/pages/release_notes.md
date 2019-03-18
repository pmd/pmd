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

#### Modified Rules

*   The Apex rule {% rule "apex/codestyle/MethodNamingConventions" %} (apex-codestyle) has a new
    property `skipTestMethodUnderscores`, which is by default disabled. The new property allows for ignoring
    all test methods, either using the `testMethod` modifier or simply annotating them `@isTest`.

### Fixed Issues

*   java-bestpractices
    *   [#808](https://github.com/pmd/pmd/issues/808): \[java] AccessorMethodGeneration false positives with compile time constants
    *   [#1555](https://github.com/pmd/pmd/issues/1555): \[java] UnusedImports false positive for method parameter type in @see Javadoc
*   java-codestyle
    *   [#1543](https://github.com/pmd/pmd/issues/1543): \[java] LinguisticNaming should ignore overriden methods
    *   [#1547](https://github.com/pmd/pmd/issues/1547): \[java] AtLeastOneConstructorRule: false-positive with lombok.AllArgsConstructor
    *   [#1624](https://github.com/pmd/pmd/issues/1624): \[java] UseDiamondOperator false positive with var initializer
*   java-design
    *   [#1641](https://github.com/pmd/pmd/issues/1641): \[java] False-positive with Lombok and inner classes
*   java-errorprone
    *   [#780](https://github.com/pmd/pmd/issues/780): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors
*   java-multithreading
    *   [#1633](https://github.com/pmd/pmd/issues/1633): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat
*   java-performance
    *   [#1632](https://github.com/pmd/pmd/issues/1632): \[java] ConsecutiveLiteralAppends false positive over catch

### API Changes

### External Contributions

*   [#1623](https://github.com/pmd/pmd/pull/1623): \[java] Fix lombok.AllArgsConstructor support - [Bobby Wertman](https://github.com/CasualSuperman)
*   [#1625](https://github.com/pmd/pmd/pull/1625): \[java] UnusedImports false positive for method parameter type in @see Javadoc - [Shubham](https://github.com/Shubham-2k17)
*   [#1628](https://github.com/pmd/pmd/pull/1628): \[java] LinguisticNaming should ignore overriden methods - [Shubham](https://github.com/Shubham-2k17)
*   [#1634](https://github.com/pmd/pmd/pull/1634): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors - [Shubham](https://github.com/Shubham-2k17)
*   [#1635](https://github.com/pmd/pmd/pull/1635): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat - [Shubham](https://github.com/Shubham-2k17)
*   [#1637](https://github.com/pmd/pmd/pull/1637): \[java] Compile time constants initialized by literals avoided by AccessorMethodGenerationRule - [Shubham](https://github.com/Shubham-2k17)
*   [#1640](https://github.com/pmd/pmd/pull/1640): \[java] Update instead of override classHasLombokAnnotation flag - [Phokham Nonava](https://github.com/fluxroot)
*   [#1644](https://github.com/pmd/pmd/pull/1644): \[apex] Add property to allow apex test methods to contain underscores - [Tom](https://github.com/tomdaly)
*   [#1645](https://github.com/pmd/pmd/pull/1645): \[java] ConsecutiveLiteralAppends false positive - [Shubham](https://github.com/Shubham-2k17)
*   [#1646](https://github.com/pmd/pmd/pull/1646): \[java] UseDiamondOperator doesn't work with var - [Shubham](https://github.com/Shubham-2k17)
*   [#1697](https://github.com/pmd/pmd/pull/1697): \[doc] Update CPD documentation - [Matías Fraga](https://github.com/matifraga)

{% endtocmaker %}

