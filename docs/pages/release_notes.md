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

*   The Java rule {% rule "java/errorprone/CloseResource" %} (`java-errorprone`) now ignores by default instances
    of `java.util.stream.Stream`. These streams are `AutoCloseable`, but most streams are backed by collections,
    arrays, or generating functions, which require no special resource management. However, there are some exceptions:
    The stream returned by `Files::lines(Path)` is backed by a actual file and needs to be closed. These instances
    won't be found by default by the rule anymore.

### Fixed Issues

*   java-codestyle
    *   [#1951](https://github.com/pmd/pmd/issues/1951): \[java] UnnecessaryFullyQualifiedName rule triggered when variable name clashes with package name
*   java-errorprone
    *   [#1922](https://github.com/pmd/pmd/issues/1922): \[java] CloseResource possible false positive with Streams
    *   [#1966](https://github.com/pmd/pmd/issues/1966): \[java] CloseResource false positive if Stream is passed as method parameter
    *   [#1967](https://github.com/pmd/pmd/issues/1967): \[java] CloseResource false positive with late assignment of variable

### API Changes

#### Deprecated APIs

##### For removal

*   The methods {% jdoc java::ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.

### External Contributions

{% endtocmaker %}

