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

*   The new Java rule {% rule "java/bestpractices/AvoidReassigningCatchVariables" %} (`java-bestpractices`) finds
    cases where the variable of the caught exception is reassigned. This practice is surprising and prevents
    further evolution of the code like multi-catch.

### Fixed Issues

*   core
    *   [#724](https://github.com/pmd/pmd/issues/724): \[core] Avoid parsing rulesets multiple times
    *   [#2653](https://github.com/pmd/pmd/issues/2653): \[lang-test] Upgrade kotlintest to Kotest
    *   [#2690](https://github.com/pmd/pmd/pull/2690): \[core] Fix java7 compatibility
*   java-bestpractices
    *   [#2471](https://github.com/pmd/pmd/issues/2471): \[java] New Rule: AvoidReassigningCatchVariables
    *   [#2668](https://github.com/pmd/pmd/issues/2668): \[java] UnusedAssignment false positives
*   java-errorprone
    *   [#2431](https://github.com/pmd/pmd/issues/2431): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message
    *   [#2439](https://github.com/pmd/pmd/issues/2439): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t)
*   java-performance
    *   [#2441](https://github.com/pmd/pmd/issues/2441): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';`

### API Changes

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

#### Deprecated API

##### For removal

*   {% jdoc !!pmd-java::lang.java.ast.ASTThrowStatement#getFirstClassOrInterfaceTypeImage() %}

### External Contributions

*   [#2677](https://github.com/pmd/pmd/pull/2677): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';` - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2678](https://github.com/pmd/pmd/pull/2678): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2679](https://github.com/pmd/pmd/pull/2679): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2682](https://github.com/pmd/pmd/pull/2682): \[java] New Rule: AvoidReassigningCatchVariables - [Mykhailo Palahuta](https://github.com/Drofff)


{% endtocmaker %}

