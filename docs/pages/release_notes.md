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

#### SARIF Format

PMD now supports the [Static Analysis Results Interchange Format (SARIF)](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=sarif)
as an additional report format. Just use the [command line parameter](pmd_userdocs_cli_reference.html#format) `-format sarif` to select it.
SARIF is an OASIS standard format for static analysis tools.
PMD creates SARIF JSON files in [SARIF version 2.1.0](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html).
An example report can be found in the documentation in [Report formats for PMD](pmd_userdocs_report_formats.html#sarif).

### Fixed Issues

*   core
    *   [#2953](https://github.com/pmd/pmd/issues/2953): \[core] Support SARIF JSON Format
    *   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings
*   java-bestpractices
    *   [#575](https://github.com/pmd/pmd/issues/575): \[java] LiteralsFirstInComparisons should consider constant fields

### API Changes

### External Contributions

*   [#2964](https://github.com/pmd/pmd/pull/2964): \[cs] Update C# grammar for additional C# 7 and C# 8 features - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2983](https://github.com/pmd/pmd/pull/2983): \[java] LiteralsFirstInComparisons should consider constant fields - [Ozan Gulle](https://github.com/ozangulle)
*   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings - [Vincent Maurin](https://github.com/vmaurin)
*   [#3073](https://github.com/pmd/pmd/pull/3073): \[core] Include SARIF renderer - [Manuel Moya Ferrer](https://github.com/mmoyaferrer)

{% endtocmaker %}

