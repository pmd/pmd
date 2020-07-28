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


#### Deprecated Rules

- The Java rule [`DataflowAnomalyAnalysis`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_errorprone.html#dataflowanomalyanalysis)
is deprecated in favour of [`UnusedAssignment`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_bestpractices.html#unusedassignment),
which was introduced in PMD 6.26.0.

### Fixed Issues

### API Changes

#### Deprecated API

- The dataflow codebase is deprecated for removal in PMD 7. This
 includes all code in the following packages, and their subpackages:
   - {% jdoc_package plsql::lang.plsql.dfa %}
   - {% jdoc_package java::lang.java.dfa %}
   - {% jdoc_package core::lang.dfa %}

   and the class {% jdoc plsql::lang.plsql.PLSQLDataFlowHandler %}


### External Contributions

{% endtocmaker %}

