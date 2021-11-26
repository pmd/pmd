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

*   core
    *   [#3424](https://github.com/pmd/pmd/issues/3424): \[core] Migrate CLI to using GNU-style long options
    *   [#3425](https://github.com/pmd/pmd/issues/3425): \[core] Add a `--version` CLI option
    *   [#3635](https://github.com/pmd/pmd/issues/3635): \[ci] Update sample projects for regression tester
*   java-bestpractices
    *   [#3613](https://github.com/pmd/pmd/issues/3613): \[java] ArrayIsStoredDirectly doesn't consider nested classes
    *   [#3614](https://github.com/pmd/pmd/issues/3614): \[java] JUnitTestsShouldIncludeAssert doesn't consider nested classes
*   java-errorprone
    *   [#3624](https://github.com/pmd/pmd/issues/3624): \[java] TestClassWithoutTestCases reports wrong classes in a file
*   java-performance
    *   [#3491](https://github.com/pmd/pmd/issues/3491): \[java] UselessStringValueOf: False positive when `valueOf(char [], int, int)` is used

### API Changes

#### Command Line Interface

The command line options for PMD and CPD now use GNU-syle long options format. E.g. instead of `-rulesets` the
preferred usage is now `--rulesets`. Alternatively one can still use the short option `-R`.
Some options also have been renamed to a more consistent casing pattern at the same time
(`--fail-on-violation` instead of `-failOnViolation`).
The old single-dash options are still supported but are deprecated and will be removed with PMD 7.
This change makes the command line interface more consistent within PMD and also less surprising
compared to other cli tools.

The changes in detail for PMD:

|old option                     |new option|
|-------------------------------|----------|
| `-rulesets`                   | `--rulesets` (or `-R`) |
| `-uri`                        | `--uri` |
| `-dir`                        | `--dir` (or `-d`) |
| `-filelist`                   | `--file-list` |
| `-ignorelist`                 | `--ignore-list` |
| `-format`                     | `--format` (or `-f`) |
| `-debug`                      | `--debug` |
| `-verbose`                    | `--verbose` |
| `-help`                       | `--help` |
| `-encoding`                   | `--encoding` |
| `-threads`                    | `--threads` |
| `-benchmark`                  | `--benchmark` |
| `-stress`                     | `--stress` |
| `-shortnames`                 | `--short-names` |
| `-showsuppressed`             | `--show-suppressed` |
| `-suppressmarker`             | `--suppress-marker` |
| `-minimumpriority`            | `--minimum-priority` |
| `-property`                   | `--property` |
| `-reportfile`                 | `--report-file` |
| `-force-language`             | `--force-language` |
| `-auxclasspath`               | `--aux-classpath` |
| `-failOnViolation`            | `--fail-on-violation` |
| `--failOnViolation`           | `--fail-on-violation` |
| `-norulesetcompatibility`     | `--no-ruleset-compatibility` |
| `-cache`                      | `--cache` |
| `-no-cache`                   | `--no-cache` |

The changes in detail for CPD:

|old option             |new option|
|-----------------------|----------|
| `--failOnViolation`   | `--fail-on-violation` |
| `-failOnViolation`    | `--fail-on-violation` |
| `--filelist`          | `--file-list` |

### External Contributions

*   [#3600](https://github.com/pmd/pmd/pull/3600): \[core] Implement GNU-style long options and '--version' - [Yang](https://github.com/duanyang25)
*   [#3612](https://github.com/pmd/pmd/pull/3612): \[java] Created fix for UselessStringValueOf false positive - [John Armgardt](https://github.com/johnra2)

{% endtocmaker %}

