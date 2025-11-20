


## 28-November-2025 - 7.19.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.19.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
* The new Apex rule [`AvoidFutureAnnotation`](https://docs.pmd-code.org/pmd-doc-7.19.0-SNAPSHOT/pmd_rules_apex_bestpractices.html#avoidfutureannotation) finds usages of the `@Future`
  annotation. It is a legacy way to execute asynchronous Apex code. New code should implement
  the `Queueable` interface instead.

### 🐛️ Fixed Issues
* apex-bestpractices
  * [#6203](https://github.com/pmd/pmd/issues/6203): \[apex] New Rule: Avoid Future Annotation

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6204](https://github.com/pmd/pmd/pull/6204): \[apex] Add rule to limit usage of @<!-- -->Future annotation - [Mitch Spano](https://github.com/mitchspano) (@mitchspano)
* [#6217](https://github.com/pmd/pmd/pull/6217): \[doc] Add Blue Cave to known tools using PMD - [Jude Pereira](https://github.com/judepereira) (@judepereira)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->



