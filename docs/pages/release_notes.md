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

#### Full support for Scala

Thanks to [Chris Smith](https://github.com/tophersmith) PMD now fully supports Scala. Now rules for analyzing Scala
code can be developed in addition to the Copy-Pase-Detection (CPD) functionality. There are no rules yet, so
contributions are welcome.

Additionally Scala support has been upgraded from 2.12.4 to 2.13.

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

*   [#1965](https://github.com/pmd/pmd/pull/1965): \[scala] Use Scalameta for parsing - [Chris Smith](https://github.com/tophersmith)

{% endtocmaker %}

