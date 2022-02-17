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

### Fixed Issues

*   core
    *   [#3768](https://github.com/pmd/pmd/issues/3768): \[core] SARIF formatter reports multiple locations when it should report multiple results
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
{% jdoc core::Rule#apply(java.util.List, core::RuleContext) %}. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

### External Contributions

*   [#3767](https://github.com/pmd/pmd/pull/3767): \[core] Update GUI.java - [Vyom Yadav](https://github.com/Vyom-Yadav)

{% endtocmaker %}

