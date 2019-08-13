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

#### Java 13 Support

This release of PMD brings support for Java 13. PMD can parse [Switch Expressions](http://openjdk.java.net/jeps/354)
with the new `yield` statement and resolve the type of such an expression.

PMD also parses [Text Blocks](http://openjdk.java.net/jeps/355) as String literals.

Note: The Switch Expressions and Text Blocks are a preview language feature of OpenJDK 13
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd ...

Note: Support for the extended break statement introduced in Java 12 as a preview language feature
has been removed.

#### New rule designer documentation

The documentation for the rule designer is now available on the main PMD documentation page:
[Rule Designer Reference](pmd_userdocs_extending_designer_reference.html). Check it out to learn
about the usage and features of the rule designer.

### Fixed Issues

*   java
    *   [#1930](https://github.com/pmd/pmd/issues/1930): \[java] Add Java 13 support
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

