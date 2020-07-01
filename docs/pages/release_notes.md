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

### API Changes

#### Deprecated API

##### For removal

* {% jdoc core::lang.rule.RuleChainVisitor %} and all implementations in language modules
* {% jdoc core::lang.rule.AbstractRuleChainVisitor %}
* {% jdoc core::lang.Language#getRuleChainVisitorClass() %}
* {% jdoc core::lang.BaseLanguageModule#<init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Class,java.lang.String...) %}


### External Contributions

{% endtocmaker %}

