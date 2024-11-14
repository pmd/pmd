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
* apex
  * [#5333](https://github.com/pmd/pmd/issues/5333): \[apex] Token recognition errors for string containing unicode escape sequence
* java
  * [#5293](https://github.com/pmd/pmd/issues/5293): \[java] Deadlock when executing PMD in multiple threads
  * [#5324](https://github.com/pmd/pmd/issues/5324): \[java] Issue with type inference of nested lambdas
  * [#5329](https://github.com/pmd/pmd/issues/5329): \[java] Type inference issue with unknown method ref in call chain
* java-bestpractices
  * [#4113](https://github.com/pmd/pmd/issues/4113): \[java] JUnitTestsShouldIncludeAssert - false positive with SoftAssertionsExtension

### 🚨 API Changes

#### Deprecations
* pmd-xml
  * {%jdoc xml::lang.xml.antlr4.XMLLexer %} is deprecated for removal. Use {%jdoc !!xml::lang.xml.ast.XMLLexer %}
    instead (note different package `ast` instead of `antlr4`).

### ✨ External Contributions
* [#5284](https://github.com/pmd/pmd/pull/5284): \[apex] Use case-insensitive input stream to avoid choking on Unicode escape sequences - [Willem A. Hajenius](https://github.com/wahajenius) (@wahajenius)

{% endtocmaker %}

