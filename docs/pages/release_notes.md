---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

### 🐛 Fixed Issues

### 🚨 API Changes

#### Removed Experimental API
* pmd-java
  * `net.sourceforge.pmd.lang.java.ast.ASTTemplate`, `net.sourceforge.pmd.lang.java.ast.ASTTemplateExpression`,
    `net.sourceforge.pmd.lang.java.ast.ASTTemplateFragment`: These nodes were introduced with Java 21 and 22
    Preview to support String Templates. However, the String Template preview feature was not finalized
    and has been removed from Java for now. We now cleaned up the PMD implementation of it.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

