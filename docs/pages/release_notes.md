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

*   apex
    *   [#3201](https://github.com/pmd/pmd/issues/3201): \[apex] ApexCRUDViolation doesn't report Database class DMLs, inline no-arg object instantiations and inline list initialization
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors

### API Changes

#### Experimental APIs

*   The AST types and APIs around Sealed Classes are not experimental anymore:
    *   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isNonSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getPermittedSubclasses() %}
    *   {% jdoc java::lang.java.ast.ASTPermitsList %}

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3373](https://github.com/pmd/pmd/pull/3373): \[apex] Add ApexCRUDViolation support for database class, inline no-arg object construction DML and inline list initialization DML - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)
*   [#3388](https://github.com/pmd/pmd/pull/3388): \[doc] Add Code Inspector in the list of tools - [Julien Delange](https://github.com/juli1)

{% endtocmaker %}

