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

- {% jdoc !!core::Rule#getParserOptions() %}
- {% jdoc !!core::lang.Parser#getParserOptions() %}
- {% jdoc !!core::lang.AbstractParser %}
- {% jdoc apex::lang.apex.ApexParserOptions %}
- {% jdoc xml::lang.xml.XmlParserOptions %}
- {% jdoc xml::lang.xml.rule.XmlXpathRule %}
- Properties of {% jdoc xml::lang.xml.rule.AbstractXmlRule %}
- {% jdoc javascript::lang.ecmascript.EcmascriptParserOptions %}

### External Contributions

{% endtocmaker %}

