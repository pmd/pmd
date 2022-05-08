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

#### CLI improvements

The PMD CLI now allows repeating the `--dir` (`-d`) and `--rulesets` (`-R`) options,
 as well as providing several space-separated arguments to either of them. For instance:
```shell
pmd -d src/main/java src/test/java -R rset1.xml -R rset2.xml
```
This also allows globs to be used on the CLI if your shell supports shell expansion.
For instance, the above can be written
```shell
pmd -d src/*/java -R rset*.xml
```
Please use theses new forms instead of using comma-separated list as argument to these options.

### Fixed Issues

* cli
  * [#1445](https://github.com/pmd/pmd/issues/1445): \[core] Allow CLI to take globs as parameters
* javascript
  * [#3948](https://github.com/pmd/pmd/issues/3948): \[js] Invalid operator error for method property in object literal

### API Changes

#### Deprecated API

- {% jdoc core::PMDConfiguration#getInputPaths() %} and
{% jdoc core::PMDConfiguration#setInputPaths(java.lang.String) %} are now deprecated.
A new set of methods have been added, which use lists and do not rely on comma splitting.

### External Contributions

{% endtocmaker %}

