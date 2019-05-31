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

#### Modified Rules

*   The Java rule {% rule "java/design/UseUtilityClass" %} (`java-design`) has a new property `ignoredAnnotations`.
    By default, classes that are annotated with Lombok's `@UtilityClass` are ignored now.

### Fixed Issues

*   java-design
    *   [#1094](https://github.com/pmd/pmd/issues/1094): \[java] UseUtilityClass should be LombokAware

### API Changes

### External Contributions

{% endtocmaker %}

