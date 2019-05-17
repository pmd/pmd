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

#### New Rules

*   The new Apex rule {% rule "apex/codestyle/FieldNamingConventions" %} (`apex-codestyle`) checks the naming
    conventions for field declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/FormalParameterNamingConventions" %} (`apex-codestyle`) checks the
    naming conventions for formal parameters of methods. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/LocalVariableNamingConventions" %} (`apex-codestyle`) checks the
    naming conventions for local variable declarations. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule {% rule "apex/codestyle/PropertyNamingConventions" %} (`apex-codestyle`) checks the naming
    conventions for property declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

#### Modified Rules

*   The Apex rule {% rule "apex/codestyle/ClassNamingConventions" %} (`apex-codestyle`) can now be configured
    using various properties for the specific kind of type declarations (e.g. class, interface, enum).
    As before, this rule uses by default the standard Apex naming convention (Pascal case).

*   The Apex rule {% rule "apex/codestyle/MethodNamingConventions" %} (`apex-codestyle`) can now be configured
    using various properties to differenciate e.g. static methods and test methods.
    As before, this rule uses by default the standard Apex naming convention (Camel case).

#### Deprecated Rules

*   The Apex rule {% rule "apex/codestyle/VariableNamingConventions" %} (`apex-codestyle`) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general rules
    {% rule "apex/codestyle/FieldNamingConventions" %},
    {% rule "apex/codestyle/FormalParameterNamingConventions" %},
    {% rule "apex/codestyle/LocalVariableNamingConventions" %}, and
    {% rule "apex/codestyle/PropertyNamingConventions" %}.

### Fixed Issues

*   apex
    *   [#1321](https://github.com/pmd/pmd/issues/1321): \[apex] Should VariableNamingConventions require properties to start with a lowercase letter?
*   dart
    *   [#1809](https://github.com/pmd/pmd/issues/1809): \[dart] \[cpd] Parse error with escape sequences
*   java-bestpractices
    *   [#1738](https://github.com/pmd/pmd/issues/1738): \[java] MethodReturnsInternalArray does not work in inner classes
*   java-codestyle
    *   [#1804](https://github.com/pmd/pmd/issues/1804): \[java] NPE in UnnecessaryLocalBeforeReturnRule
*   python
    *   [#1810](https://github.com/pmd/pmd/issues/1810): \[python] \[cpd] Parse error when using Python 2 backticks

### API Changes

### External Contributions

*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1817](https://github.com/pmd/pmd/pull/1817): \[apex] Add configurable naming convention rules - [Jeff Hube](https://github.com/jeffhube)

{% endtocmaker %}

