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

#### Deprecated rules

*   java-codestyle
    *   {% rule java/codestyle/DuplicateImports %}: use the rule {% rule java/bestpractices/UnusedImports %} instead, since it now reports duplicate imports

*   java-errorprone
    *   {% rule java/errorprone/ImportFromSamePackage %}: use the rule {% rule java/bestpractices/UnusedImports %} instead, since it now reports imports from the same package


### Fixed Issues

### API Changes

### External Contributions

{% endtocmaker %}

