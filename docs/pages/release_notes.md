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

### API Changes

#### Deprecated API

##### Around RuleSet parsing

* {% jdoc core::RuleSetFactory %} and {% jdoc core::RuleSetFactoryUtils %} have been deprecated in favor of {% jdoc core::RuleSetParser %}. This is easier to configure, and more maintainable than the multiple overloads of `RuleSetFactoryUtils`.
* Some static creation methods have been added to {% jdoc core::RuleSet %} for simple cases, eg {% jdoc core::RuleSet#forSingleRule(core::Rule) %}. These replace some counterparts in {% jdoc core::RuleSetFactory %}
* Since {% jdoc core::RuleSets %} is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* {% jdoc core::RuleSetReferenceId %}, {% jdoc core::RuleSetReference %}, {% jdoc core::RuleSetFactoryCompatibility %} are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

##### Around the `PMD` class

Many APIs around PMD's entry point ({% jdoc core::PMD %}) have been deprecated, including:
* The contents of the packages {% jdoc_package core::cli %}, {% jdoc_package core::processor %}
* {% jdoc core::SourceCodeProcessor %}
* The constructors of {% jdoc core::PMD %} (the class will be made a utility class)


### External Contributions

{% endtocmaker %}

