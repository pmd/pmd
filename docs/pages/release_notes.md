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

#### Java 20 Support

This release of PMD brings support for Java 20. There are no new standard language features.

PMD supports [JEP 433: Pattern Matching for switch (Fourth Preview)](https://openjdk.org/jeps/433) and
[JEP 432: Record Patterns (Second Preview)](https://openjdk.org/jeps/432) as preview language features.

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `20-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd --use-version java-20-preview ...

### Fixed Issues
* java
    * [#4333](https://github.com/pmd/pmd/issues/4333): \[java] Support JDK 20

### API Changes

#### Java
* Support for Java 18 preview language features have been removed. The version "18-preview" is no longer available.
* The experimental class `net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern` has been removed.

### External Contributions

{% endtocmaker %}

