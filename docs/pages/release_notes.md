---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

*   core
    *   [#724](https://github.com/pmd/pmd/issues/724): \[core] Avoid parsing rulesets multiple times
    *   [#2653](https://github.com/pmd/pmd/issues/2653): \[lang-test] Upgrade kotlintest to Kotest
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

*   Many methods of {% jdoc !!core::Report %}. They are replaced by accessors
that produce a List. For example, {% jdoc !a!core::Report#iterator() %} 
(and implementing Iterable) and {% jdoc !a!core::Report#isEmpty() %} are both
replaced by {% jdoc !a!core::Report#getViolations() %}.
*   {% jdoc !!core::Report.ReadableDuration %}

### External Contributions

*   [#2677](https://github.com/pmd/pmd/pull/2677): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';` - [Mykhailo Palahuta](https://github.com/Drofff)


{% endtocmaker %}

