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
variable `PMD_JAVA_OPTS` and select the new language version `13-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 13-preview ...

Note: Support for the extended break statement introduced in Java 12 as a preview language feature
will be removed with the next PMD version 6.19.0.

#### Full support for Scala

Thanks to [Chris Smith](https://github.com/tophersmith) PMD now fully supports Scala. Now rules for analyzing Scala
code can be developed in addition to the Copy-Paste-Detection (CPD) functionality. There are no rules yet, so
contributions are welcome.

Additionally Scala support has been upgraded from 2.12.4 to 2.13.

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

*   The Apex rule {% rule "apex/bestpractices/DebugsShouldUseLoggingLevel" %} (`apex-bestpractices`) detects
    usages of `System.debug()` method calls that are used without specifying the log level. Having the log
    level specified provides a cleaner log, and improves readability of it.

#### Modified Rules

*   The Java rule {% rule "java/errorprone/CloseResource" %} (`java-errorprone`) now ignores by default instances
    of `java.util.stream.Stream`. These streams are `AutoCloseable`, but most streams are backed by collections,
    arrays, or generating functions, which require no special resource management. However, there are some exceptions:
    The stream returned by `Files::lines(Path)` is backed by a actual file and needs to be closed. These instances
    won't be found by default by the rule anymore.

### Fixed Issues

*   all
    *   [#1465](https://github.com/pmd/pmd/issues/1465): \[core] Stylesheet pmd-report.xslt fails to display filepath if 'java' in path
    *   [#1923](https://github.com/pmd/pmd/issues/1923): \[core] Incremental analysis does not work with shortnames
    *   [#1983](https://github.com/pmd/pmd/pull/1983): \[core] Avoid crashes with analysis cache when classpath references non-existing directories
    *   [#1990](https://github.com/pmd/pmd/pull/1990): \[core] Incremental analysis mixes XPath rule violations
*   apex
    *   [#1901](https://github.com/pmd/pmd/issues/1901): \[apex] Expose super type name of UserClass
    *   [#1942](https://github.com/pmd/pmd/issues/1942): \[apex] Add best practice rule for debug statements in Apex
*   java
    *   [#1930](https://github.com/pmd/pmd/issues/1930): \[java] Add Java 13 support
*   java-bestpractices
    *   [#1862](https://github.com/pmd/pmd/issues/1862): \[java] New rule for MessageDigest.getInstance
    *   [#1952](https://github.com/pmd/pmd/issues/1952): \[java] UnusedPrivateField not triggering if @Value annotation present
*   java-codestyle
    *   [#1951](https://github.com/pmd/pmd/issues/1951): \[java] UnnecessaryFullyQualifiedName rule triggered when variable name clashes with package name
*   java-errorprone
    *   [#1922](https://github.com/pmd/pmd/issues/1922): \[java] CloseResource possible false positive with Streams
    *   [#1966](https://github.com/pmd/pmd/issues/1966): \[java] CloseResource false positive if Stream is passed as method parameter
    *   [#1967](https://github.com/pmd/pmd/issues/1967): \[java] CloseResource false positive with late assignment of variable
*   plsql
    *   [#1933](https://github.com/pmd/pmd/issues/1933): \[plsql] ParseException with cursor declared in anonymous block
    *   [#1935](https://github.com/pmd/pmd/issues/1935): \[plsql] ParseException with SELECT INTO record defined as global variable
    *   [#1936](https://github.com/pmd/pmd/issues/1936): \[plslq] ParseException with cursor inside procedure declaration
    *   [#1946](https://github.com/pmd/pmd/issues/1946): \[plsql] ParseException with using TRIM inside IF statements condition
    *   [#1947](https://github.com/pmd/pmd/issues/1947): \[plsql] ParseError - SELECT with FOR UPDATE OF
    *   [#1948](https://github.com/pmd/pmd/issues/1948): \[plsql] ParseException with INSERT INTO using package global variables
    *   [#1950](https://github.com/pmd/pmd/issues/1950): \[plsql] ParseException with UPDATE and package record variable
    *   [#1953](https://github.com/pmd/pmd/issues/1953): \[plsql] ParseException with WITH in CURSOR

### API Changes

#### Changes to Renderer

*   Each renderer has now a new method {% jdoc !!core::renderers.Renderer#setUseShortNames(List) %} which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    {% jdoc !!core::renderers.AbstractRenderer#determineFileName(String) %} should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.
    
    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

#### Deprecated APIs

##### For removal

*   The methods {% jdoc java::lang.java.ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::lang.java.ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.
*   The method {% jdoc !!core::RuleContext#setSourceCodeFilename(String) %} has been deprecated
    and will be removed. The already existing method {% jdoc !!core::RuleContext#setSourceCodeFile(File) %}
    should be used instead. The method {% jdoc !!core::RuleContext#getSourceCodeFilename() %} still
    exists and returns just the filename without the full path.
*   The method {% jdoc !!core::processor.AbstractPMDProcessor#filenameFrom(DataSource) %} has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The method {% jdoc !!core::Report#metrics() %} and {% jdoc core::Report::hasMetrics() %} have
    been deprecated. They were leftovers from a previous deprecation round targeting
    {% jdoc core::lang.rule.stat.StatisticalRule %}.

##### Internal APIs

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
  * {% jdoc_package core::cache %}
* pmd-java
  * {% jdoc_package java::lang.java.typeresolution %}: Everything, including
    subpackages, except {% jdoc java::lang.java.typeresolution.TypeHelper %} and
    {% jdoc java::lang.java.typeresolution.typedefinition.JavaTypeDefinition %}.
  * {% jdoc !c!java::lang.java.ast.ASTCompilationUnit#getClassTypeResolver() %}

### External Contributions

*   [#1943](https://github.com/pmd/pmd/pull/1943): \[apex] Adds "debug should use logging level" best practice rule for Apex - [Renato Oliveira](https://github.com/renatoliveira)
*   [#1965](https://github.com/pmd/pmd/pull/1965): \[scala] Use Scalameta for parsing - [Chris Smith](https://github.com/tophersmith)
*   [#1970](https://github.com/pmd/pmd/pull/1970): \[java] DoubleBraceInitialization: Fix example - [Tobias Weimer](https://github.com/tweimer)
*   [#1971](https://github.com/pmd/pmd/pull/1971): \[java] 1862 - Message Digest should not be used as class field - [AnthonyKot](https://github.com/AnthonyKot)
*   [#1972](https://github.com/pmd/pmd/pull/1972): \[plsql] ParseError - SELECT with FOR UPDATE OF - [Piotr Szymanski](https://github.com/szyman23)
*   [#1974](https://github.com/pmd/pmd/pull/1974): \[plsql] Fixes for referencing record type variables - [Piotr Szymanski](https://github.com/szyman23)
*   [#1975](https://github.com/pmd/pmd/pull/1975): \[plsql] TRIM function with record type variables - [Piotr Szymanski](https://github.com/szyman23)
*   [#1976](https://github.com/pmd/pmd/pull/1976): \[plsql] Fix for mistaking / for MultiplicativeExpression - [Piotr Szymanski](https://github.com/szyman23)
*   [#1977](https://github.com/pmd/pmd/pull/1977): \[plsql] fix for skipping sql starting with WITH - [Piotr Szymanski](https://github.com/szyman23)
*   [#1986](https://github.com/pmd/pmd/pull/1986): \[plsql] Fix for cursors in anonymous blocks - [Piotr Szymanski](https://github.com/szyman23)
*   [#1994](https://github.com/pmd/pmd/pull/1994): \[core] Resolve pmd-report failure when java folder in filepath - [Amish Shah](https://github.com/shahamish150294)

{% endtocmaker %}

