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

#### New rule designer documentation

The documentation for the rule designer is now available on the main PMD documentation page:
[Rule Designer Reference](pmd_userdocs_extending_designer_reference.html). Check it out to learn
about the usage and features of the rule designer.

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

*   [#1976](https://github.com/pmd/pmd/pull/1976): \[plsql] Fix for mistaking / for MultiplicativeExpression - [Piotr Szymanski](https://github.com/szyman23)

{% endtocmaker %}

