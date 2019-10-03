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

### Fixed Issues

*   core
    *   [#2036](https://github.com/pmd/pmd/issues/2036): \[core] Wrong include/exclude patterns are silently ignored
*   java-codestyle
    *    [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class

### API Changes


#### Deprecated APIs

##### For removal

* core
  * {% jdoc !!core::RuleSet#getExcludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileExclusions() %} instead.
  * {% jdoc !!core::RuleSet#getIncludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileInclusions() %} instead.
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
* pmd-apex
  * {% jdoc java::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}


### External Contributions

{% endtocmaker %}

