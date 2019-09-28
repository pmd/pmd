---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

*   java-codestyle
    *    [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class

### API Changes


#### Deprecated APIs

##### For removal

* pmd-core
  * All the package {% jdoc_package core::dcd %} and its subpackages. See {% jdoc core::dcd.DCD %}.
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
* pmd-apex
  * {% jdoc java::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}


### External Contributions

{% endtocmaker %}

