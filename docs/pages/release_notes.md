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
*   java
    *   [#2042](https://github.com/pmd/pmd/issues/2042): \[java] PMD crashes with ClassFormatError: Absent Code attribute...
*   java-codestyle
    *    [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class
*   java-design
    *   [#1912](https://github.com/pmd/pmd/issues/1912): \[java] Metrics not computed correctly with annotations

### API Changes


#### Deprecated APIs

##### For removal

* pmd-core
  * All the package {% jdoc_package core::dcd %} and its subpackages. See {% jdoc core::dcd.DCD %}.
  * In {% jdoc core::lang.LanguageRegistry %}:
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguageVersion(List) %}
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguage(List) %}
    * {% jdoc core::lang.LanguageRegistry#findAllVersions() %}
    * {% jdoc core::lang.LanguageRegistry#findLanguageVersionByTerseName(String) %}
    * {% jdoc core::lang.LanguageRegistry#getInstance() %}
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

*   [#2047](https://github.com/pmd/pmd/pull/2047): \[java] Fix computation of metrics with annotations - [Andi](https://github.com/andipabst)

{% endtocmaker %}

