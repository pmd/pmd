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

*   pmd-core
    * [#1939](https://github.com/pmd/pmd/issues/1939): \[core] XPath expressions return handling


### API Changes

#### Deprecated API

- {% jdoc java::ast.ASTPackageDeclaration#getPackageNameImage() %}, {% jdoc java::ast.ASTTypeParameter#getParameterName() %} and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`, the attribute is `@Name`

### External Contributions

{% endtocmaker %}
