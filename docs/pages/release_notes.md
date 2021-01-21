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

*   The new Visualforce rule {% rule "vf/security/VfHtmlStyleTagXss" %} checks for potential XSS problems
    when using `<style>` tags on Visualforce pages.

### Fixed Issues

### API Changes

### External Contributions

*   [#3005](https://github.com/pmd/pmd/pull/3005): \[vf] \[New Rule] Handle XSS violations that can occur within Html Style tags - [rmohan20](https://github.com/rmohan20)

{% endtocmaker %}

