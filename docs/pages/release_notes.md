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

### 🚀 New: Java 24 Support
This release of PMD brings support for Java 24. There are no new standard language features,
but a couple of preview language features:

* [JEP 488: Primitive Types in Patterns, instanceof, and switch (Second Preview)](https://openjdk.org/jeps/488)
* [JEP 492: Flexible Constructor Bodies (Third Preview)](https://openjdk.org/jeps/492)
* [JEP 494: Module Import Declarations (Second Preview)](https://openjdk.org/jeps/494)
* [JEP 495: Simple Source Files and Instance Main Methods (Fourth Preview)](https://openjdk.org/jeps/495)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `24-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-24-preview ...

Note: Support for Java 22 preview language features have been removed. The version "22-preview"
are no longer available.

### 🐛 Fixed Issues
* java
  * [#5154](https://github.com/pmd/pmd/issues/5154): \[java] Support Java 24

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

