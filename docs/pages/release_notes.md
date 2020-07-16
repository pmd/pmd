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

*   The new Java rule {% rule "java/bestpractices/UnusedAssignment" %} (`java-bestpractices`) finds assignments
    to variables, that are never used and are useless. The new rule is supposed to entirely replace
    {% rule "java/errorprone/DataflowAnomalyAnalysis" %}.

### Fixed Issues

### API Changes

### External Contributions

{% endtocmaker %}

