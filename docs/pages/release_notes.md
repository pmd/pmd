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
* java-bestpractices
    * [#3455](https://github.com/pmd/pmd/issues/3455): \[java] WhileLoopWithLiteralBoolean - false negative with complex expressions
* java-performance
    * [#3625](https://github.com/pmd/pmd/issues/3625): \[java] AddEmptyString - false negative with empty var

### API Changes

### External Contributions
* [#3984](https://github.com/pmd/pmd/pull/3984): \[java] Fix AddEmptyString false-negative issue - [@LiGaOg](https://github.com/LiGaOg)
* [#3988](https://github.com/pmd/pmd/pull/3988): \[java] Modify WhileLoopWithLiteralBoolean to meet the missing case #3455 - [@VoidxHoshi](https://github.com/VoidxHoshi)
* [#4017](https://github.com/pmd/pmd/pull/4017): Add Gherkin support to CPD - [@ASBrouwers](https://github.com/ASBrouwers)

{% endtocmaker %}

