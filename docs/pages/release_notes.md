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

#### Modified Rules

*   The Java rules {% rule "java/errorprone/InvalidSlf4jMessageFormat" %} and {% rule "java/errorprone/MoreThanOneLogger" %}
    (`java-errorprone`) now both support [Log4j2](https://logging.apache.org/log4j/2.x/).

*   The Java rule {% rule "java/design/LawOfDemeter" %} (`java-design`) ignores now also Builders, that are
    not assigned to a local variable, but just directly used within a method call chain. The method, that creates
    the builder needs to end with "Builder", e.g. `newBuilder()` or `initBuilder()` works. This change
    fixes a couple of false positives.

*   The Java rule {% rule "java/errorprone/DataflowAnomalyAnalysis" %} (`java-errorprone`) doesn't check for
    UR anomalies (undefined and then referenced) anymore. These checks were all false-positives, since actual
    UR occurrences would lead to compile errors.

### Fixed Issues

*   core
    *   [#2014](https://github.com/pmd/pmd/issues/2014): \[core] Making add(SourceCode sourceCode) public for alternative file systems
    *   [#2036](https://github.com/pmd/pmd/issues/2036): \[core] Wrong include/exclude patterns are silently ignored
*   java
    *   [#2042](https://github.com/pmd/pmd/issues/2042): \[java] PMD crashes with ClassFormatError: Absent Code attribute...
*   java-bestpractices
    *   [#2025](https://github.com/pmd/pmd/issues/2025): \[java] UnusedImports when @see / @link pattern includes a FQCN
*   java-codestyle
    *   [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class
*   java-design
    *   [#1912](https://github.com/pmd/pmd/issues/1912): \[java] Metrics not computed correctly with annotations
*   java-errorprone
    *   [#336](https://github.com/pmd/pmd/issues/336): \[java] InvalidSlf4jMessageFormat applies to log4j2
    *   [#1636](https://github.com/pmd/pmd/issues/1636): \[java] Stop checking UR anomalies for DataflowAnomalyAnalysis
*   doc
    * [#2058](https://github.com/pmd/pmd/issues/2058): \[doc] CLI reference for `-norulesetcompatibility` shows a boolean default value


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
  * {% jdoc !!core::lang.Parser#canParse() %}
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclarator %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getMethodName() %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getBlock() %}
  * {% jdoc java::lang.java.ast.ASTConstructorDeclaration#getParameterCount() %}
* pmd-apex
  * {% jdoc apex::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc apex::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}


### External Contributions

*   [#2010](https://github.com/pmd/pmd/pull/2010): \[java] LawOfDemeter to support inner builder pattern - [Gregor Riegler](https://github.com/gregorriegler)
*   [#2012](https://github.com/pmd/pmd/pull/2012): \[java] Fixes 336, slf4j log4j2 support - [Mark Hall](https://github.com/markhall82)
*   [#2032](https://github.com/pmd/pmd/pull/2032): \[core] Allow adding SourceCode directly into CPD - [Nathan Braun](https://github.com/nbraun-Google)
*   [#2047](https://github.com/pmd/pmd/pull/2047): \[java] Fix computation of metrics with annotations - [Andi](https://github.com/andipabst)
*   [#2065](https://github.com/pmd/pmd/pull/2065): \[java] Stop checking UR anomalies - [Carlos Macasaet](https://github.com/l0s)

{% endtocmaker %}

