---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues
* apex
  * [#5138](https://github.com/pmd/pmd/issues/5138): \[apex] Various false-negatives since 7.3.0 when using triggers
    (ApexCRUDViolation, CognitiveComplexity, OperationWithLimitsInLoop)
  * [#5163](https://github.com/pmd/pmd/issues/5163): \[apex] Parser error when using toLabel in SOSL query
  * [#5182](https://github.com/pmd/pmd/issues/5182): \[apex] Parser error when using GROUPING in a SOQL query
* core
  * [#5059](https://github.com/pmd/pmd/issues/5059): \[core] xml output doesn't escape CDATA inside its own CDATA
  * [#5201](https://github.com/pmd/pmd/issues/5201): \[core] PMD sarif schema file points to nonexistent location
* java
  * [#5190](https://github.com/pmd/pmd/issues/5190): \[java] NPE in type inference
* java-errorprone
  * [#5207](https://github.com/pmd/pmd/issues/5207): \[java] CheckSkipResult: false positve for a private method `void skip(int)` in a subclass of FilterInputStream

### 🚨 API Changes

### ✨ External Contributions
* [#5208](https://github.com/pmd/pmd/pull/5208): \[doc] Added Codety to "Tools / Integrations" - [Tony](https://github.com/random1223) (@random1223)
* [#5202](https://github.com/pmd/pmd/pull/5202): \[core] Sarif format: refer to schemastore.org - [David Schach](https://github.com/dschach) (@dschach)

{% endtocmaker %}

