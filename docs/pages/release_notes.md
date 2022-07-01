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

#### Gherkin support
Thanks to the contribution from [Anne Brouwers](https://github.com/ASBrouwers) PMD now has CPD support
for the [Gherkin](https://cucumber.io/docs/gherkin/) language. It is used to defined test cases for the
[Cucumber](https://cucumber.io/) testing tool for behavior-driven development.

Being based on a proper Antlr grammar, CPD can:

* ignore comments
* honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Fixed Issues

### API Changes

### External Contributions
* [#4017](https://github.com/pmd/pmd/pull/4017): Add Gherkin support to CPD - [@ASBrouwers](https://github.com/ASBrouwers)

{% endtocmaker %}

