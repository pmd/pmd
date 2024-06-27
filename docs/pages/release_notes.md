---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### ✨ New Rules
* The new Java rule {%rule java/bestpractices/UseEnumCollections %} reports usages for `HashSet` and `HashMap`
  when the keys are of an enum type. The specialized enum collections are more space- and time-efficient.

### 🐛 Fixed Issues
* java
  * [#577](https://github.com/pmd/pmd/issues/577): \[java] New Rule: Check that Map<K,V> is an EnumMap if K is an enum value

### 🚨 API Changes

### ✨ External Contributions

{% endtocmaker %}

