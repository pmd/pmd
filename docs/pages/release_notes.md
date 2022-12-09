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

* The Java rules {% rule java/design/ExcessiveClassLength %} and {% rule java/design/ExcessiveMethodLength %}
  have been deprecated. The rule {% rule java/design/NcssCount %} can be used instead.
  The deprecated rules will be removed with PMD 7.0.0.

### Fixed Issues

* java-design
    * [#2127](https://github.com/pmd/pmd/issues/2127): \[java] Deprecate rules ExcessiveClassLength and ExcessiveMethodLength

### API Changes

### External Contributions

{% endtocmaker %}

