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

#### Luau Support

This release of PMD brings support for [Luau](https://github.com/Roblox/luau), a gradually typed language derived from Lua.

### Fixed Issues
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Missing --file arg in TreeExport CLI example

### API Changes

#### CPD CLI

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

### External Contributions
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Fix missing --file arg in TreeExport CLI example - [@mohan-chinnappan-n](https://github.com/mohan-chinnappan-n)
* [#4066](https://github.com/pmd/pmd/pull/4066): \[lua] Add support for Luau syntax and skipping literal sequences in CPD - [@matthargett](https://github.com/matthargett)

{% endtocmaker %}

