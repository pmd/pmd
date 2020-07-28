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

#### Deprecated API

- Many methods of {% jdoc !!core::Report %}. They are replaced by accessors
that produce a List. For example, {% jdoc !a!core::Report#iterator() %} 
(and implementing Iterable) and {% jdoc !a!core::Report#isEmpty() %} are both
replaced by {% jdoc !a!core::Report#getViolations() %}.

### External Contributions

{% endtocmaker %}

