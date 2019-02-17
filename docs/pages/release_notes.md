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

### CPD Suppression for Antlr-based languages

[ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini)
keep working on bringing full Antlr support to PMD. For this release, they have implemented
token filtering in an equivalent way as we did for JavaCC languages, adding support for CPD
suppressions through `CPD-OFF` and `CPD-ON` comments for all Antlr-based languages.

This means, you can now ignore arbitrary blocks of code on:
* Go
* Kotlin
* Swift

Simply start the suppression with any comment (single or multiline) containing `CPD-OFF`,
and resume again with a comment containing `CPD-ON`.

More information is available in [the user documentation](pmd_userdocs_cpd.html#suppression).

#### New Rules

*   The new Java rule {% rule "java/bestpractices/UseTryWithResources" %) (`java-bestpractices`) searches
    for try-blocks, that could be changed to a try-with-resources statement. This statement ensures that
    each resource is closed at the end of the statement and is available since Java 7.

#### Modified Rules

*   The Apex rule {% rule "apex/codestyle/MethodNamingConventions" %} (apex-codestyle) has a new
    property `skipTestMethodUnderscores`, which is by default disabled. The new property allows for ignoring
    all test methods, either using the `testMethod` modifier or simply annotating them `@isTest`.

### Fixed Issues

*   all
    *   [#1559](https://github.com/pmd/pmd/issues/1559): \[core] CPD: Lexical error in file (no file name provided)
*   java-bestpractices
    *   [#808](https://github.com/pmd/pmd/issues/808): \[java] AccessorMethodGeneration false positives with compile time constants
    *   [#1405](https://github.com/pmd/pmd/issues/1405): \[java] New Rule: UseTryWithResources - Replace close and IOUtils.closeQuietly with try-with-resources
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
*   [#1654](https://github.com/pmd/pmd/pull/1654): \[core] Antlr token filter - [Tomi De Lucca](https://github.com/tomidelucca)

{% endtocmaker %}

