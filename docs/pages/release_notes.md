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

*   The new Apex rule {% rule "apex/errorprone/OverrideBothEqualsAndHashcode" %} brings the well known Java rule
    to Apex. In Apex the same principle applies: `equals` and `hashCode` should always be overridden
    together to ensure collection classes such as Maps and Sets work as expected.

#### Deprecated rules

*   java-performance
    *   {% rule "java/performance/AvoidUsingShortType" %}: arithmetic on shorts is not significantly
        slower than on ints, whereas using shorts may provide significant memory savings in arrays.
    *   {% rule "java/performance/SimplifyStartsWith" %}: the suggested code transformation has an
        insignificant performance impact, and decreases readability.

### Fixed Issues

*   core
    *   [#2970](https://github.com/pmd/pmd/issues/2970): \[core] PMD 6.30.0 release is not reproducible
    *   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings
*   java-bestpractices
    *   [#575](https://github.com/pmd/pmd/issues/575): \[java] LiteralsFirstInComparisons should consider constant fields
*   java-codestyle
    *   [#2960](https://github.com/pmd/pmd/issues/2960): \[java] Thread issue in MethodNamingConventionsRule
*   java-performance
    *   [#2296](https://github.com/pmd/pmd/issues/2296): \[java] Deprecate rule AvoidUsingShortType
    *   [#2740](https://github.com/pmd/pmd/issues/2740): \[java] Deprecate rule SimplifyStartsWith

### API Changes

### External Contributions

*   [#2666](https://github.com/pmd/pmd/pull/2666): \[swift] Manage swift5 string literals - [kenji21](https://github.com/kenji21)
*   [#2959](https://github.com/pmd/pmd/pull/2959): \[apex] New Rule: override equals and hashcode rule - [recdevs](https://github.com/recdevs)
*   [#2964](https://github.com/pmd/pmd/pull/2964): \[cs] Update C# grammar for additional C# 7 and C# 8 features - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2965](https://github.com/pmd/pmd/pull/2965): \[cs] Improvements for ignore sequences of literals functionality - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2983](https://github.com/pmd/pmd/pull/2983): \[java] LiteralsFirstInComparisons should consider constant fields - [Ozan Gulle](https://github.com/ozangulle)
*   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings - [Vincent Maurin](https://github.com/vmaurin)

{% endtocmaker %}

