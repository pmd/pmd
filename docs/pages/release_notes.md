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

*   The Java rule {% rule "java/errorprone/ProperLogger" %} (`java-errorprone`) has two new properties
    to configure the logger class (e.g. "org.slf4j.Logger") and the logger name of the special case,
    when the logger is not static. The name of the static logger variable was already configurable.
    The new property "loggerClass" allows to use this rule for different logging frameworks.
    This rule covers all the cases of the now deprecated rule {% rule "java/errorprone/LoggerIsNotStaticFinal" %}.

#### Deprecated Rules

*   The Java rule {%rule "java/errorprone/LoggerIsNotStaticFinal" %} (`java-errorprone`) has been deprecated
    and will be removed with PMD 7. The rule is replaced by {% rule "java/errorprone/ProperLogger" %}.

### Fixed Issues

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

*   [#1762](https://github.com/pmd/pmd/pull/1762): \[java] LoggerIsNotStaticFinal and ProperLogger - make class-name configurable - [Ivo Šmíd](https://github.com/bedla)
*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)

{% endtocmaker %}

