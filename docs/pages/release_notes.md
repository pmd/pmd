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

#### New rules

*   The Java rule {% rule "java/bestpractices/AvoidMessageDigestField" %} (`java-bestpractices`) detects fields
    of the type `java.security.MessageDigest`. Using a message digest instance as a field would need to be
    synchronized, as it can easily be used by multiple threads. Without synchronization the calculated hash could
    be entirely wrong. Instead of declaring this as a field and synchronize access to use it from multiple threads,
    a new instance should be created when needed. This rule is also active when using java's quickstart ruleset.

### Fixed Issues

*   java-bestpractices
    *   [#1862](https://github.com/pmd/pmd/issues/1862): \[java] New rule for MessageDigest.getInstance
*   java-codestyle
    *   [#1951](https://github.com/pmd/pmd/issues/1951): \[java] UnnecessaryFullyQualifiedName rule triggered when variable name clashes with package name

### API Changes

#### Deprecated APIs

##### For removal

*   The methods {% jdoc java::ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.

### External Contributions

*   [#1971](https://github.com/pmd/pmd/pull/1971): \[java] 1862 - Message Digest should not be used as class field - [AnthonyKot](https://github.com/AnthonyKot)

{% endtocmaker %}

