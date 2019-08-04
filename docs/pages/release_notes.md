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

*   java-codestyle
    *   [#1951](https://github.com/pmd/pmd/issues/1951): \[java] UnnecessaryFullyQualifiedName rule triggered when variable name clashes with package name

### API Changes

#### Deprecated APIs

##### For removal

*   The methods {% jdoc java::ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.

### External Contributions

{% endtocmaker %}

