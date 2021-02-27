---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Java 16 Support

This release of PMD brings support for Java 16. PMD supports [JEP 394: Pattern Matching for instanceof](https://openjdk.java.net/jeps/394) and [JEP 395: Records](https://openjdk.java.net/jeps/395). Both have been promoted
to be a standard language feature of Java 16.

PMD also supports [JEP 397: Sealed Classes (Second Preview)](https://openjdk.java.net/jeps/397) as a preview
language feature. In order to analyze a project with PMD that uses these language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version `16-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 16-preview ...

Note: Support for Java 14 preview language features have been removed. The version "14-preview" is no longer available.

#### Modified Rules

*   The Apex rule {% rule "apex/documentation/ApexDoc" %} has two new properties: `reportPrivate` and
    `reportProtected`. Previously the rule only considered public and global classes, methods, and
    properties. With these properties, you can verify the existence of ApexDoc comments for private
    and protected methods as well. By default, these properties are disabled to preserve backwards
    compatible behavior.

### Fixed Issues

*   apex-documentation
    *   [#3075](https://github.com/pmd/pmd/issues/3075): \[apex] ApexDoc should support private access modifier
*   java
    *   [#3101](https://github.com/pmd/pmd/issues/3101): \[java] NullPointerException when running PMD under JRE 11
*   java-bestpractices
    *   [#3132](https://github.com/pmd/pmd/issues/3132): \[java] UnusedImports with static imports on subclasses
*   java-errorprone
    *   [#2716](https://github.com/pmd/pmd/issues/2716): \[java] CompareObjectsWithEqualsRule: False positive with Enums
    *   [#3089](https://github.com/pmd/pmd/issues/3089): \[java] CloseResource rule throws exception on spaces in property types
    *   [#3133](https://github.com/pmd/pmd/issues/3133): \[java] InvalidLogMessageFormat FP with StringFormattedMessage and ParameterizedMessage
*   plsql
    *   [#3106](https://github.com/pmd/pmd/issues/3106): \[plsql] ParseException while parsing EXECUTE IMMEDIATE 'drop database link ' \|\| linkname;

### API Changes

#### Experimental APIs

*   The experimental class `ASTTypeTestPattern` has been renamed to {% jdoc java::lang.java.ast.ASTTypePattern %}
    in order to align the naming to the JLS.
*   The experimental class `ASTRecordConstructorDeclaration` has been renamed to {% jdoc java::lang.java.ast.ASTCompactConstructorDeclaration %}
    in order to align the naming to the JLS.
*   The AST types and APIs around Pattern Matching and Records are not experimental anymore:
    *   {% jdoc !!java::lang.java.ast.ASTVariableDeclaratorId#isPatternBinding() %}
    *   {% jdoc java::lang.java.ast.ASTPattern %}
    *   {% jdoc java::lang.java.ast.ASTTypePattern %}
    *   {% jdoc java::lang.java.ast.ASTRecordDeclaration %}
    *   {% jdoc java::lang.java.ast.ASTRecordComponentList %}
    *   {% jdoc java::lang.java.ast.ASTRecordComponent %}
    *   {% jdoc java::lang.java.ast.ASTRecordBody %}
    *   {% jdoc java::lang.java.ast.ASTCompactConstructorDeclaration %}

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The protected or public member of the Java rule {% jdoc java::lang.java.rule.bestpractices.AvoidUsingHardCodedIPRule %}
    are deprecated and considered to be internal API. They will be removed with PMD 7.

### External Contributions

*   [#3098](https://github.com/pmd/pmd/pull/3098): \[apex] ApexDoc optionally report private and protected - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3107](https://github.com/pmd/pmd/pull/3107): \[plsql] Fix ParseException for EXECUTE IMMEDIATE str1\|\|str2; - [hvbtup](https://github.com/hvbtup)
*   [#3125](https://github.com/pmd/pmd/pull/3125): \[doc] Fix sample code indentation in documentation - [Artur Dryomov](https://github.com/arturdryomov)

{% endtocmaker %}

