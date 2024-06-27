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

### 🐛 Fixed Issues
* pmd-java
  * [#5050](https://github.com/pmd/pmd/issues/5050): \[java] Problems with pattern variables in switch branches

### 🚨 API Changes

#### Deprecated for removal

* pmd-java
  * {%jdoc java::lang.java.ast.ASTRecordPattern#getVarId() %} This method was added here by mistake. Record
    patterns don't declare a pattern variable for the whole pattern, but rather for individual record
    components, which can be accessed via {%jdoc java::lang.java.ast.ASTRecordPattern#getComponentPatterns() %}.

### ✨ External Contributions

{% endtocmaker %}

