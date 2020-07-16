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

#### Scala cross compilation

Up until now the PMD Scala module has been compiled against scala 2.13 only by default.
However, this makes it impossible to use pmd as a library in scala projects,
that use scala 2.12, e.g. in sbt plugins. Therefore PMD now provides cross compiled pmd-scala
modules for both versions: **scala 2.12** and **scala 2.13**.

The new modules have new maven artifactIds. The old artifactId `net.sourceforge.pmd:pmd-scala:{{ site.pmd.version }}`
is still available, but is deprecated from now on. It has been demoted to be just a delegation to the new
`pmd-scala_2.13` module and will be removed eventually.

The coordinates for the new modules are:

```
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.12</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>

<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.13</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>
```

The command line version of PMD continues to use **scala 2.13**.

#### New Rules

*   The new Java Rule {% rule "java/codestyle/UnnecessaryCast" %} (`java-codestyle`)
    finds casts that are unnecessary while accessing collection elements.


#### Modified rules

*   The Java rule {% rule "java/codestyle/UseDiamondOperator" %} (`java-codestyle`) now by default
    finds unnecessary usages of type parameters, which are nested, involve wildcards and are used
    within a ternary operator. These usages are usually only unnecessary with Java8 and later, when
    the type inference in Java has been improved.
    
    In order to avoid false positives when checking Java7 only code, the rule has the new property
    `java7Compatibility`, which is disabled by default. Settings this to "true" retains
    the old rule behaviour.


### Fixed Issues

*   apex
    *   [#2610](https://github.com/pmd/pmd/pull/2610): \[apex] Support top-level enums in rules
*   apex-bestpractices
    *   [#2554](https://github.com/pmd/pmd/issues/2554): \[apex] Exception applying rule UnusedLocalVariable on trigger
*   core
    *   [#2599](https://github.com/pmd/pmd/pull/2599): \[core] Fix XPath 2.0 Rule Chain Analyzer with Unions
    *   [#2483](https://github.com/pmd/pmd/issues/2483): \[lang-test] Support cpd tests based on text comparison.
        For details see
        [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
        in the developer documentation.
*   c#
    *   [#2551](https://github.com/pmd/pmd/issues/2551): \[c#] CPD suppression with comments doesn't work
*   cpp
    *   [#1757](https://github.com/pmd/pmd/issues/1757): \[cpp] Support unicode characters
*   java
    *   [#2549](https://github.com/pmd/pmd/issues/2549): \[java] Auxclasspath in PMD CLI does not support relative file path
*   java-codestyle
    *   [#2545](https://github.com/pmd/pmd/issues/2545): \[java] UseDiamondOperator false negatives
    *   [#2573](https://github.com/pmd/pmd/pull/2573): \[java] DefaultPackage: Allow package default JUnit 5 Test methods
*   java-design
    *   [#2563](https://github.com/pmd/pmd/pull/2563): \[java] UselessOverridingMethod false negative with already public methods
    *   [#2570](https://github.com/pmd/pmd/issues/2570): \[java] NPathComplexity should mention the expected NPath complexity
*   java-errorprone
    *   [#2544](https://github.com/pmd/pmd/issues/2544): \[java] UseProperClassLoader can not detect the case with method call on intermediate variable
*   scala
    *   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13


### API Changes

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.
    
#### Deprecated APIs

*   {% jdoc !!apex::lang.apex.ast.ASTAnnotation#suppresses(core::Rule) %} (Apex)
*   {% jdoc !!core::cpd.TokenEntry#TokenEntry(java.lang.String, java.lang.String, int) %}
*   {% jdoc test::testframework.AbstractTokenizerTest %}. Use CpdTextComparisonTest in module pmd-lang-test instead.
    For details see
    [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
    in the developer documentation.

#### Internal API

*   {% jdoc apex::lang.apex.ApexParser %}
*   {% jdoc apex::lang.apex.ApexHandler %}

### External Contributions

*   [#2349](https://github.com/pmd/pmd/pull/2349): \[java] Optimize UnusedPrivateMethodRule - [shilko2013](https://github.com/shilko2013)
*   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13 - [João Ferreira](https://github.com/jtjeferreira)
*   [#2567](https://github.com/pmd/pmd/pull/2567): \[c#] Fix CPD suppression with comments doesn't work - [Lixon Lookose](https://github.com/LixonLookose)
*   [#2573](https://github.com/pmd/pmd/pull/2573): \[java] DefaultPackage: Allow package default JUnit 5 Test methods - [Craig Andrews](https://github.com/candrews)
*   [#2593](https://github.com/pmd/pmd/pull/2593): \[java] NPathComplexity should mention the expected NPath complexity - [Artem Krosheninnikov](https://github.com/KroArtem)

{% endtocmaker %}

