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
* ant
  * [#1860](https://github.com/pmd/pmd/issues/1860): \[ant] Reflective access warnings on java > 9 and java < 17

### 🚨 API Changes

#### Deprecations
* pmd-xml
  * {%jdoc xml::lang.xml.antlr4.XMLLexer %} is deprecated for removal. Use {%jdoc !!xml::lang.xml.ast.XMLLexer %}
    instead (note different package `ast` instead of `antlr4`).

### ✨ External Contributions

{% endtocmaker %}

