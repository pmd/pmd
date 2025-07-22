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

#### New: CPD support for CSS
CPD now supports CSS (Cascading Style Sheets), a language for describing the rendering of structured
documents (such as HTML) on screen, on paper etc.  
It is shipped with the new module `pmd-css`.

#### ✨ New Rules
* Two new rules have been added to Java's Error Prone category: {%rule java/errorprone/ReplaceJavaUtilCalendar %}
  and {%rule java/errorprone/ReplaceJavaUtilDate %}. These rules help to migrate away from old Java APIs around
  `java.util.Calendar` and `java.util.Date`. It is recommended to use the modern `java.time` API instead, which
  is available since Java 8.

### 🐛 Fixed Issues
* core
  * [#4328](https://github.com/pmd/pmd/issues/4328): \[ci] Improve Github Actions Workflows
  * [#5597](https://github.com/pmd/pmd/issues/5597): \[core] POM Incompatibility with Maven 4
* java
  * [#5344](https://github.com/pmd/pmd/issues/5344): \[java] IllegalArgumentException: Invalid type reference for method or ctor type annotation: 16
  * [#5478](https://github.com/pmd/pmd/issues/5478): \[java] Support Java 25
* java-codestyle
  * [#5892](https://github.com/pmd/pmd/issues/5892): \[java] ShortVariable false positive for java 22 unnamed variable `_`
* java-design
  * [#5858](https://github.com/pmd/pmd/issues/5858): \[java] FinalFieldCouldBeStatic false positive for array initializers
* java-errorprone
  * [#2862](https://github.com/pmd/pmd/issues/2862): \[java] New Rules: Avoid java.util.Date and Calendar classes

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
* [#5733](https://github.com/pmd/pmd/pull/5733): \[css] Add new CPD language - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#5859](https://github.com/pmd/pmd/pull/5859): Fix #5858: \[java] Fix false positive in FinalFieldCouldBeStatic for array initializers - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#5876](https://github.com/pmd/pmd/pull/5876): chore: license header cleanup - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5883](https://github.com/pmd/pmd/pull/5883): Fix #2862: \[java] Add rules discouraging the use of java.util.Calendar and java.util.Date - [UncleOwen](https://github.com/UncleOwen) (@UncleOwen)
* [#5893](https://github.com/pmd/pmd/pull/5893): chore: Fix Mockito javaagent warning for Java 21+ - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5894](https://github.com/pmd/pmd/pull/5894): chore: Fix JUnit warning about invalid test factory - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5895](https://github.com/pmd/pmd/pull/5895): Fix #5597: Move dogfood profile to separate settings.xml - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5899](https://github.com/pmd/pmd/pull/5899): Fix #5344: \[java] Just log invalid annotation target type - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5909](https://github.com/pmd/pmd/pull/5909): \[ci] Create a pre-release for snapshot builds - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5911](https://github.com/pmd/pmd/pull/5911): \[doc] Reference CPD Capable Languages in CPD CLI docu - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5914](https://github.com/pmd/pmd/pull/5914): Fix #5892: \[java] ShortVariable FP for java 22 Unnamed Variable - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

