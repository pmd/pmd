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

#### Renamed rules

* The Java rule {% rule java/errorprone/BeanMembersShouldSerialize %} has been renamed to
  {% rule java/errorprone/NonSerializableClass %}. It has been revamped to only check for classes that are marked with
  `Serializable` and reports each field in it, that is not serializable.

  The property `prefix` has been deprecated, since in a serializable class all fields have to be
  serializable regardless of the name.

### Fixed Issues
* java-errorprone
    * [#1668](https://github.com/pmd/pmd/issues/1668): \[java] BeanMembersShouldSerialize is extremely noisy
    * [#4176](https://github.com/pmd/pmd/issues/4176): \[java] Rename BeanMembersShouldSerialize to NonSerializableClass

### API Changes

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)

{% endtocmaker %}

