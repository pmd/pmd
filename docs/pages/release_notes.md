---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.0.1-SNAPSHOT

The PMD team is pleased to announce PMD 6.0.1-SNAPSHOT.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Additional information about the new introduced rule categories

With the release of PMD 6.0.0, all rules have been sorted into one of the following eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

Please note, that not every category in every language may have a rule. There might be categories with no
rules at all, such as `category/java/security.xml`, which has currently no rules.
There are even languages, which only have rules of one category (e.g. `category/xml/errorprone.xml`).

You can find the information about available rules in the generated rule documentation, available
at <https://pmd.github.io/latest/>.

### Fixed Issues

*   apex-errorprone
    *   [#792](https://github.com/pmd/pmd/issues/792): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes
*   apex-security
    *   [#788](https://github.com/pmd/pmd/issues/788): \[apex] Method chaining breaks ApexCRUDViolation
*   doc
    *   [#782](https://github.com/pmd/pmd/issues/782): \[doc] Wrong information in the Release Notes about the Security ruleset
    *   [#794](https://github.com/pmd/pmd/issues/794): \[doc] Broken documentation links for 6.0.0
*   java
    *   [#783](https://github.com/pmd/pmd/issues/783): \[java] GuardLogStatement regression
    *   [#793](https://github.com/pmd/pmd/issues/793): \[java] Parser error with private method in nested classes in interfaces
    *   [#812](https://github.com/pmd/pmd/issues/812): \[java] Exception applying rule DataClass
*   java-design
    *   [#785](https://github.com/pmd/pmd/issues/785): \[java] NPE in DataClass rule

### API Changes

### External Contributions

*   [#796](https://github.com/pmd/pmd/pull/796): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#799](https://github.com/pmd/pmd/pull/799): \[apex] Method chaining breaks ApexCRUDViolation - [Robert Sösemann](https://github.com/up2go-rsoesemann)
