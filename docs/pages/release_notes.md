---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### 🚀 New: Java 25 Support
This release of PMD brings support for Java 25.

There are the following new standard language features:
* [JEP 511: Module Import Declarations](https://openjdk.org/jeps/511)
* [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)
* [JEP 513: Flexible Constructor Bodies](https://openjdk.org/jeps/513)

And one preview language feature:
* [JEP 507: Primitive Types in Patterns, instanceof, and switch (Third Preview)](https://openjdk.org/jeps/507)

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `25-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-25-preview ...

Note: Support for Java 23 preview language features have been removed. The version "23-preview"
is no longer available.

### 🐛 Fixed Issues
* java
  * [#5478](https://github.com/pmd/pmd/issues/5478): \[java] Support Java 25

### 🚨 API Changes

#### Experimental APIs that are now considered stable
* pmd-java
  * {% jdoc !!java::lang.java.ast.ASTImportDeclaration#isModuleImport() %} is now stable API.
  * {% jdoc !!java::lang.java.ast.ASTCompilationUnit#isCompact() %} is now stable API. Note, it was previously
    called `isSimpleCompilationUnit`.
  * {% jdoc java::lang.java.ast.ASTImplicitClassDeclaration %} is now stable API.
  * {% jdoc !ac!java::lang.java.ast.JavaVisitorBase#visit(java::lang.java.ast.ASTImplicitClassDeclaration,P) %} is now
    stable API.

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

