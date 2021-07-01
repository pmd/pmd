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

#### Experimental APIs

*   The AST types and APIs around Sealed Classes are not experimental anymore:
    *   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isNonSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getPermittedSubclasses() %}
    *   {% jdoc java::lang.java.ast.ASTPermitsList %}

### External Contributions

{% endtocmaker %}

