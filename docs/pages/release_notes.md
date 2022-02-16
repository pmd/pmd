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

*   misc
    *   [#3759](https://github.com/pmd/pmd/issues/3759): \[lang-test] Upgrade dokka maven plugin to 1.4.32

### API Changes

#### Deprecated API

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.
- {% jdoc core::Report %}: construction methods like addViolation or createReport
- {% jdoc core::RuleContext %}: all constructors, getters and setters. A new set
of stable methods, matching those in PMD 7, was added to replace the `addViolation`
overloads of {% jdoc core::lang.rule.AbstractRule %}. In PMD 7, `RuleContext` will
be the API to report violations, and it can already be used as such in PMD 6.
- {% jdoc core::RuleSet %}: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`

#### Changed API

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

{% jdoc core::RuleContext %} now requires setting the current rule before calling
{% jdoc core::lang.rule.Rule#apply(java.util.List, core::RuleContext)}. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

### External Contributions

*   [#3767](https://github.com/pmd/pmd/pull/3767): \[core] Update GUI.java - [Vyom Yadav](https://github.com/Vyom-Yadav)

{% endtocmaker %}

