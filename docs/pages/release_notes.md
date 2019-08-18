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

#### Modified Rules

*   The Java rule {% rule "java/errorprone/CloseResource" %} (`java-errorprone`) now ignores by default instances
    of `java.util.stream.Stream`. These streams are `AutoCloseable`, but most streams are backed by collections,
    arrays, or generating functions, which require no special resource management. However, there are some exceptions:
    The stream returned by `Files::lines(Path)` is backed by a actual file and needs to be closed. These instances
    won't be found by default by the rule anymore.

### Fixed Issues

*   all
    *   [#1983](https://github.com/pmd/pmd/pull/1983): \[core] Avoid crashes with analysis cache when classpath references non-existing directories
*   java-bestpractices
    *   [#1862](https://github.com/pmd/pmd/issues/1862): \[java] New rule for MessageDigest.getInstance
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

*   [#1970](https://github.com/pmd/pmd/pull/1970): \[java] DoubleBraceInitialization: Fix example - [Tobias Weimer](https://github.com/tweimer)
*   [#1971](https://github.com/pmd/pmd/pull/1971): \[java] 1862 - Message Digest should not be used as class field - [AnthonyKot](https://github.com/AnthonyKot)

{% endtocmaker %}

