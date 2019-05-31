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

*   The Java rule {% rule "java/bestpractices/UnusedPrivateField" %} (`java-bestpractices`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rule {% rule "java/design/SingularField" %} (`java-design`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

### Fixed Issues

*   java-bestpractices
    *   [#1703](https://github.com/pmd/pmd/issues/1703): \[java] UnusedPrivateField on member annotated with lombok @Delegate

### API Changes

### External Contributions

*   [#1792](https://github.com/pmd/pmd/pull/1792): \[java] Added lombok.experimental to AbstractLombokAwareRule - [jakivey32](https://github.com/jakivey32)

{% endtocmaker %}

