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

#### Lua now supports additionally Luau

This release of PMD adds support for [Luau](https://github.com/Roblox/luau), a gradually typed language derived
from Lua. This means, that the Lua language in PMD can now parse both Lua and Luau.

### Fixed Issues
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Missing --file arg in TreeExport CLI example
* [#3859](https://github.com/pmd/pmd/pull/3859): \[java] CommentDefaultAccessModifier is triggered in JUnit5 test class

### API Changes

#### CPD CLI

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

### External Contributions
* [#4066](https://github.com/pmd/pmd/pull/4066): \[lua] Add support for Luau syntax and skipping literal sequences in CPD - [@matthargett](https://github.com/matthargett)
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Fix missing --file arg in TreeExport CLI example - [@mohan-chinnappan-n](https://github.com/mohan-chinnappan-n)
* [#4131](https://github.com/pmd/pmd/pull/4131): \[doc] TooFewBranchesForASwitchStatement - Use "if-else" instead of "if-then" - [@Suvashri](https://github.com/Suvashri)
* [#3859](https://github.com/pmd/pmd/pull/3859): \[java] CommentDefaultAccessModifier is triggered in JUnit5 test class  [@lfalcantar](https://github.com/lfalcantar)

{% endtocmaker %}

