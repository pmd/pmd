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

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc vm::lang.vm.VmTokenManager %}
*   {% jdoc java::lang.java.JavaTokenManager %}
*   {% jdoc python::lang.python.PythonTokenManager %}

### External Contributions

{% endtocmaker %}

