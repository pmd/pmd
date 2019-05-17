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

#### Enhanced Matlab support

Thanks to the contributions from [Maikel Steneker](https://github.com/maikelsteneker) CPD for Matlab can
now parse Matlab programs which use the question mark operator to specify access to
class members:

```
classdef Class1
properties (SetAccess = ?Class2)
```

CPD also understands now double quoted strings, which are supported since version R2017a of Matlab:

```
str = "This is a string"
```

#### Enhanced C++ support

CPD now supports digit separators in C++ (language module "cpp"). This is a C++14 feature.

Example: `auto integer_literal = 1'000'000;`

The single quotes can be used to add some structure to large numbers.

CPD also parses raw string literals now correctly (see [#1784](https://github.com/pmd/pmd/issues/1784)).

### Fixed Issues

*   apex
    *   [#1783](https://github.com/pmd/pmd/issues/1783): \[apex] comments on constructor not recognized when the Class has inner class
*   cpp
    *   [#1784](https://github.com/pmd/pmd/issues/1784): \[cpp] Improve support for raw string literals
*   dart
    *   [#1809](https://github.com/pmd/pmd/issues/1809): \[dart] \[cpd] Parse error with escape sequences
*   java-bestpractices
    *   [#1738](https://github.com/pmd/pmd/issues/1738): \[java] MethodReturnsInternalArray does not work in inner classes
*   java-codestyle
    *   [#1804](https://github.com/pmd/pmd/issues/1804): \[java] NPE in UnnecessaryLocalBeforeReturnRule
*   python
    *   [#1810](https://github.com/pmd/pmd/issues/1810): \[python] \[cpd] Parse error when using Python 2 backticks
*   matlab
    *   [#1830](https://github.com/pmd/pmd/issues/1830): \[matlab] \[cpd] Parse error with comments

### API Changes

### External Contributions

*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1807](https://github.com/pmd/pmd/pull/1807): \[ci] Fix missing local branch issues when executing pmd-regression-tester - [BBG](https://github.com/djydewang)
*   [#1813](https://github.com/pmd/pmd/pull/1813): \[matlab] \[cpd] Matlab comments - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1816](https://github.com/pmd/pmd/pull/1816): \[apex] Fix ApexDoc handling with inner classes - [Jeff Hube](https://github.com/jeffhube)
*   [#1819](https://github.com/pmd/pmd/pull/1819): \[cpp] \[cpd] Add support for digit separators - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1820](https://github.com/pmd/pmd/pull/1820): \[cpp] \[cpd] Improve support for raw string literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1821](https://github.com/pmd/pmd/pull/1821): \[matlab] \[cpd] Matlab question mark token - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1822](https://github.com/pmd/pmd/pull/1822): \[matlab] \[cpd] Double quoted string - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}

