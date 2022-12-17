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

* The Java rule {% rule java/errorprone/EmptyStatementNotInLoop %} is deprecated.
  Use the rule {% rule java/codestyle/UnnecessarySemicolon %} instead.
  Note: Actually it was announced to be deprecated since 6.46.0 but the rule was not marked as deprecated yet.
  This has been done now.

### Fixed Issues

* java-design
    * [#2127](https://github.com/pmd/pmd/issues/2127): \[java] Deprecate rules ExcessiveClassLength and ExcessiveMethodLength

### API Changes

#### Deprecated APIs

##### For removal

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* {% jdoc java::lang.java.rule.design.ExcessiveLengthRule %} (Java)

### External Contributions

{% endtocmaker %}

