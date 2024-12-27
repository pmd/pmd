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

#### New: CPD support for Rust

CPD now supports Rust, a blazingly fast and memory-efficient programming language.
It is shipped in the new module `pmd-rust`.

### 🐛 Fixed Issues
* cli
  * [#5399](https://github.com/pmd/pmd/issues/5399): \[cli] Windows: PMD fails to start with special characters in path names
  * [#5401](https://github.com/pmd/pmd/issues/5401): \[cli] Windows: Console output doesn't use unicode
* java
  * [#5096](https://github.com/pmd/pmd/issues/5096): \[java] StackOverflowError with recursively bound type variable
* java-bestpractices
  * [#4861](https://github.com/pmd/pmd/issues/4861): \[java] UnusedPrivateMethod - false positive with static methods in core JDK classes
* java-documentation
  * [#2996](https://github.com/pmd/pmd/issues/2996): \[java] CommentSize rule violation is not suppressed at method level

### 🚨 API Changes

#### Experimental API

* pmd-core: {%jdoc !!core::reporting.RuleContext#addViolationWithPosition(core::reporting.Reportable,core::lang.ast.AstInfo,core::lang.document.FileLocation,java.lang.String,java.lang.Object...) %}

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5414](https://github.com/pmd/pmd/pull/5414): Add Rust CPD - [Julia Paluch](https://github.com/juliapaluch) (@juliapaluch)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

