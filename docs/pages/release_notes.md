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

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties

### API Changes

#### Deprecated APIs

* Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
  composable visitors, used in the metrics framework, but they didn't prove cost-effective.
  * In `net.sourceforge.pmd.lang.java.ast`: JavaParserDecoratedVisitor, JavaParserControllessVisitor,
    JavaParserControllessVisitorAdapter, and JavaParserVisitorDecorator are deprecated with no intended replacement
  * In `net.sourceforge.pmd.lang.java.metrics.impl`:
    * CycloAssertAwareDecorator, CycloBaseVisitor, and CycloPathAwareDecorator are deprecated and are replaced with CycloVisitor
    * NcssBaseVisitor and NcssCountImportsDecorator are deprecated and are replaced with NcssVisitor


### External Contributions

*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)

{% endtocmaker %}

