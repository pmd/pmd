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

*   The Java rule {% rule "java/errorprone/AvoidLiteralsInIfCondition" %} (`java-errorprone`) has a new property
    `ignoreExpressions`. This property is set by default to `true` in order to maintain compatibility. If this
    property is set to false, then literals in more complex expressions are considered as well.

### Fixed Issues

*   java-bestpractices
    *   [#2149](https://github.com/pmd/pmd/issues/2149): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-errorprone
    *   [#2140](https://github.com/pmd/pmd/issues/2140): \[java] AvoidLiteralsInIfCondition: false negative for expressions
*   java-performance
    *   [#2141](https://github.com/pmd/pmd/issues/2141): \[java] StringInstatiation: False negative with String-array access

### API Changes


#### Deprecated APIs

##### Internal API

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

##### For removal

* {% jdoc java::lang.java.AbstractJavaParser %}
* {% jdoc java::lang.java.AbstractJavaHandler %}


### External Contributions

{% endtocmaker %}

