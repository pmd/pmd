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

#### Modified rules

* The Java rule {% rule java/bestpractices/UnusedPrivateField %} has a new property `reportForAnnotations`.
  This is a list of fully qualified names of the annotation types that should be reported anyway. If an unused field
  has any of these annotations, then it is reported. If it has any other annotation, then it is still considered 
  to be used and is not reported.

### Fixed Issues
* core
    * [#4248](https://github.com/pmd/pmd/issues/4248): \[core] Can't analyze sources in zip files
* apex-security
    * [#4146](https://github.com/pmd/pmd/issues/4146): \[apex] ApexCRUDViolation: Recognize User Mode in SOQL + DML
* java-bestpractices
    * [#4166](https://github.com/pmd/pmd/issues/4166): \[java] UnusedPrivateField doesn't find annotated unused private fields anymore
* java-multithreading
    * [#4210](https://github.com/pmd/pmd/issues/4210): \[java] DoNotUseThreads report duplicate warnings

### API Changes

### External Contributions
* [#4244](https://github.com/pmd/pmd/pull/4244): \[apex] ApexCRUDViolation: user mode and system mode with test cases added - [Tarush Singh](https://github.com/Tarush-Singh35) (@Tarush-Singh35)

{% endtocmaker %}

