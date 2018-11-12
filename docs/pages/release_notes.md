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

### API Changes

* The implementation of the adapters for the XPath engines Saxon and Jaxen (package `net.sourceforge.pmd.lang.ast.xpath`)
  are now deprecated. They'll be moved to an internal package come 7.0.0. Only `Attribute` remains public API.

### External Contributions

{% endtocmaker %}

