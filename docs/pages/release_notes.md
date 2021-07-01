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

#### Java 17 Support

This release of PMD brings support for Java 17. PMD supports [JEP 409: Sealed Classes](https://openjdk.java.net/jeps/409)
which has been promoted to be a standard language feature of Java 17.

PMD also supports [JEP 406: Pattern Matching for switch (Preview)](https://openjdk.java.net/jeps/406) as a preview
language feature. In order to analyze a project with PMD that uses these language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version `17-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 17-preview ...

Note: Support for Java 15 preview language features have been removed. The version "15-preview" is no longer available.

### Fixed Issues

### API Changes

#### Experimental APIs

*   The AST types and APIs around Sealed Classes are not experimental anymore:
    *   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isNonSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getPermittedSubclasses() %}
    *   {% jdoc java::lang.java.ast.ASTPermitsList %}

### External Contributions

{% endtocmaker %}

