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

*   doc
    *   [#3230](https://github.com/pmd/pmd/issues/3230): \[doc] Remove "Edit me" button for language index pages
*   java-codestyle
    *   [#2655](https://github.com/pmd/pmd/issues/2655): \[java] UnnecessaryImport false positive for on-demand imports
    *   [#3262](https://github.com/pmd/pmd/pull/3262): \[java] FieldDeclarationsShouldBeAtStartOfClass: false negative with anon classes
    *   [#3265](https://github.com/pmd/pmd/pull/3265): \[java] MethodArgumentCouldBeFinal: false negatives with interfaces and inner classes
    *   [#3266](https://github.com/pmd/pmd/pull/3266): \[java] LocalVariableCouldBeFinal: false negatives with interfaces, anon classes
*   java-errorprone
    *   [#3268](https://github.com/pmd/pmd/pull/3268): \[java] ConstructorCallsOverridableMethod: IndexOutOfBoundsException with annotations

### API Changes

### External Contributions

{% endtocmaker %}

