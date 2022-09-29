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
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Missing --file arg in TreeExport CLI example
* java-codestyle
    * [#4085](https://github.com/pmd/pmd/issues/4085): \[java] UnnecessaryFullyQualifiedName false positive when nested and non-nested classes with the same name and in the same package are used together

### API Changes

### External Contributions
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Fix missing --file arg in TreeExport CLI example - [@mohan-chinnappan-n](https://github.com/mohan-chinnappan-n)
* [#4128](https://github.com/pmd/pmd/pull/4128): \[java] Fix False-positive UnnecessaryFullyQualifiedName when nested and non-nest… #4103 - [Oleg Andreych](https://github.com/OlegAndreych) (@OlegAndreych)

{% endtocmaker %}

