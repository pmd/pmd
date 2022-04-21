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
* core
  * [#3792](https://github.com/pmd/pmd/issues/3792): \[core] Allow to filter violations in Report
  * [#3882](https://github.com/pmd/pmd/pull/3882): \[core] Fix AssertionError about exhaustive switch

### API Changes

#### Experimental APIs

* Report has two new methods which allow limited mutations of a given report:
  * {% jdoc !!core::Report#filterViolations(net.sourceforge.pmd.util.Predicate) %} creates a new report with
    some violations removed with a given predicate based filter.
  * {% jdoc !!core::Report#union(net.sourceforge.pmd.Report) %} can combine two reports into a single new Report.
* {% jdoc !!core::util.Predicate %} will be replaced in PMD7 with the standard Predicate interface from java8.

### External Contributions

{% endtocmaker %}

