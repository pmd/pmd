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

#### Java 14 Support

This release of PMD brings support for Java 14. PMD can parse [Switch Expressions](https://openjdk.java.net/jeps/361),
which have been promoted to be a standard language feature of Java.

PMD also parses [Text Blocks](https://openjdk.java.net/jeps/368) as String literals, which is still a preview
language feature in Java 14.

The new [Pattern Matching for instanceof](https://openjdk.java.net/jeps/305) can be used as well as
[Records](https://openjdk.java.net/jeps/359).

Note: The Text Blocks, Pattern Matching for instanceof and Records are all preview language features of OpenJDK 14
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS` and select the new language version `14-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 14-preview ...

Note: Support for the extended break statement introduced in Java 12 as a preview language feature
has been removed from PMD with this version. The version "12-preview" is no longer available.


#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.21.0).

#### Apex Suppressions

In addition to suppressing violation with the `@SuppressWarnings` annotation, Apex now also supports
the suppressions with a `NOPMD` comment. See [Suppressing warnings](pmd_userdocs_suppressing_warnings.html).

#### Improved CPD support for C#

The C# tokenizer is now based on an antlr grammar instead of a manual written tokenizer. This
should give more accurate results and especially fixes the problems with the using statement syntax
(see [#2139](https://github.com/pmd/pmd/issues/2139)).

#### New Rules

*   The Rule {% rule "apex/design/CognitiveComplexity" %} (`apex-design`) finds methods and classes
    that are highly complex and therefore difficult to read and more costly to maintain. In contrast
    to cyclomatic complexity, this rule uses "Cognitive Complexity", which is a measure of how
    difficult it is for humans to read and understand a method.

*   The Rule {% rule "apex/errorprone/TestMethodsMustBeInTestClasses" %} (`apex-errorprone`) finds test methods
    that are not residing in a test class. The test methods should be moved to a proper test class.
    Support for tests inside functional classes was removed in Spring-13 (API Version 27.0), making classes
    that violate this rule fail compile-time. This rule is however useful when dealing with legacy code.

### Fixed Issues

*   apex
    *   [#1087](https://github.com/pmd/pmd/issues/1087): \[apex] Support suppression via //NOPMD
    *   [#2306](https://github.com/pmd/pmd/issues/2306): \[apex] Switch statements are not parsed/supported
*   apex-design
    *   [#2162](https://github.com/pmd/pmd/issues/2162): \[apex] Cognitive Complexity rule
*   apex-errorprone
    *   [#639](https://github.com/pmd/pmd/issues/639): \[apex] Test methods should not be in classes other than test classes
*   cs
    *   [#2139](https://github.com/pmd/pmd/issues/2139): \[cs] CPD doesn't understand alternate using statement syntax with C# 8.0
*   doc
    *   [#2274](https://github.com/pmd/pmd/issues/2274): \[doc] Java API documentation for PMD
*   java
    *   [#2159](https://github.com/pmd/pmd/issues/2159): \[java] Prepare for JDK 14
    *   [#2268](https://github.com/pmd/pmd/issues/2268): \[java] Improve TypeHelper resilience
*   java-bestpractices
    *   [#2277](https://github.com/pmd/pmd/issues/2277): \[java] FP in UnusedImports for ambiguous static on-demand imports
*   java-design
    *   [#911](https://github.com/pmd/pmd/issues/911): \[java] UselessOverridingMethod false positive when elevating access modifier
*   java-errorprone
    *   [#2242](https://github.com/pmd/pmd/issues/2242): \[java] False-positive MisplacedNullCheck reported
    *   [#2250](https://github.com/pmd/pmd/issues/2250): \[java] InvalidLogMessageFormat flags logging calls using a slf4j-Marker
    *   [#2255](https://github.com/pmd/pmd/issues/2255): \[java] InvalidLogMessageFormat false-positive for a lambda argument
*   java-performance
    *   [#2275](https://github.com/pmd/pmd/issues/2275): \[java] AppendCharacterWithChar flags literals in an expression
*   plsql
    *   [#2327](https://github.com/pmd/pmd/pull/2327): \[plsql] Parsing of WHERE CURRENT OF
    *   [#2328](https://github.com/pmd/pmd/issues/2328): \[plsql] Support XMLROOT
    *   [#2331](https://github.com/pmd/pmd/pull/2331): \[plsql] Fix in Comment statement
    *   [#2332](https://github.com/pmd/pmd/pull/2332): \[plsql] Fixed Execute Immediate statement parsing
    *   [#2340](https://github.com/pmd/pmd/pull/2340): \[plsql] Fixed parsing / as divide or execute

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

* {% jdoc core::rules.RuleFactory %}
* {% jdoc core::rules.RuleBuilder %}
* Constructors of {% jdoc core::RuleSetFactory %}, use factory methods from {% jdoc core::RulesetsFactoryUtils %} instead
* {% jdoc core::RulesetsFactoryUtils#getRulesetFactory(core::PMDConfiguration, core::util.ResourceLoader) %}

* {% jdoc apex::lang.apex.ast.AbstractApexNode %}
* {% jdoc apex::lang.apex.ast.AbstractApexNodeBase %}, and the related `visit`
methods on {% jdoc apex::lang.apex.ast.ApexParserVisitor %} and its implementations.
 Use {% jdoc apex::lang.apex.ast.ApexNode %} instead, now considers comments too.

##### For removal

* pmd-core
  * {% jdoc core::lang.dfa.DFAGraphRule %} and its implementations
  * {% jdoc core::lang.dfa.DFAGraphMethod %}
  * Many methods on the {% jdoc core::lang.ast.Node %} interface
  and {% jdoc core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
  * Many APIs of {% jdoc_package core::lang.metrics %}, though most of them were internal and
  probably not used directly outside of PMD. Use {% jdoc core::lang.metrics.MetricsUtil %} as
  a replacement for the language-specific façades too.
  * {% jdoc core::lang.ast.QualifiableNode %}, {% jdoc core::lang.ast.QualifiedName %}
* pmd-java
  * {% jdoc java::lang.java.AbstractJavaParser %}
  * {% jdoc java::lang.java.AbstractJavaHandler %}
  * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
  * {% jdoc java::lang.java.ast.JavaQualifiedName %}
  * {% jdoc !!java::lang.java.ast.ASTCatchStatement#getBlock() %}
  * {% jdoc !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
  * {% jdoc java::lang.java.ast.JavaQualifiableNode %}
    * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
  * {% jdoc_package java::lang.java.qname %} and its contents
  * {% jdoc java::lang.java.ast.MethodLikeNode %}
    * Its methods will also be removed from its implementations,
      {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
      {% jdoc java::lang.java.ast.ASTLambdaExpression %}.
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
    instead. This affects {% jdoc !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
    {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
    {% jdoc !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.
  * Several methods of {% jdoc java::lang.java.ast.ASTTryStatement %}, replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * {% jdoc java::lang.java.ast.ASTYieldStatement %} will not implement {% jdoc java::lang.java.ast.TypeNode %}
    anymore come 7.0.0. Test the type of the expression nested within it.
  * {% jdoc java::lang.java.metrics.JavaMetrics %}, {% jdoc java::lang.java.metrics.JavaMetricsComputer %}
  * {% jdoc !!java::lang.java.ast.ASTArguments#getArgumentCount() %}.
    Use {% jdoc java::lang.java.ast.ASTArguments#size() %} instead.
  * {% jdoc !!java::lang.java.ast.ASTFormalParameters#getParameterCount() %}.
    Use {% jdoc java::lang.java.ast.ASTFormalParameters#size() %} instead.
* pmd-apex
  * {% jdoc apex::lang.apex.metrics.ApexMetrics %}, {% jdoc apex::lang.apex.metrics.ApexMetricsComputer %}

#### PLSQL AST

The production and node `ASTCursorBody` was unnecessary, not used and has been removed. Cursors have been already
parsed as `ASTCursorSpecification`.

### External Contributions

*   [#2251](https://github.com/pmd/pmd/pull/2251): \[java] FP for InvalidLogMessageFormat when using slf4j-Markers - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2253](https://github.com/pmd/pmd/pull/2253): \[modelica] Remove duplicated dependencies - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#2256](https://github.com/pmd/pmd/pull/2256): \[doc] Corrected XML attributes in release notes - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2276](https://github.com/pmd/pmd/pull/2276): \[java] AppendCharacterWithCharRule ignore literals in expressions - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2278](https://github.com/pmd/pmd/pull/2278): \[java] fix UnusedImports rule for ambiguous static on-demand imports - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2279](https://github.com/pmd/pmd/pull/2279): \[apex] Add support for suppressing violations using the // NOPMD comment - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2280](https://github.com/pmd/pmd/pull/2280): \[cs] CPD: Replace C# tokenizer by an Antlr-based one - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2297](https://github.com/pmd/pmd/pull/2297): \[apex] Cognitive complexity metrics - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2317](https://github.com/pmd/pmd/pull/2317): \[apex] New Rule - Test Methods Must Be In Test Classes - [Brian Nørremark](https://github.com/noerremark)
*   [#2321](https://github.com/pmd/pmd/pull/2321): \[apex] Support switch statements correctly in Cognitive Complexity - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2326](https://github.com/pmd/pmd/pull/2326): \[plsql] Added XML functions to parser: extract(xml), xml_root and fixed xml_forest - [Piotr Szymanski](https://github.com/szyman23)
*   [#2327](https://github.com/pmd/pmd/pull/2327): \[plsql] Parsing of WHERE CURRENT OF added - [Piotr Szymanski](https://github.com/szyman23)
*   [#2331](https://github.com/pmd/pmd/pull/2331): \[plsql] Fix in Comment statement - [Piotr Szymanski](https://github.com/szyman23)
*   [#2332](https://github.com/pmd/pmd/pull/2332): \[plsql] Fixed Execute Immediate statement parsing - [Piotr Szymanski](https://github.com/szyman23)
*   [#2338](https://github.com/pmd/pmd/pull/2338): \[cs] CPD: fixes in filtering of using directives - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2339](https://github.com/pmd/pmd/pull/2339): \[cs] CPD: Fixed CPD --ignore-usings option - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2340](https://github.com/pmd/pmd/pull/2340): \[plsql] fix for parsing / as divide or execute - [Piotr Szymanski](https://github.com/szyman23)

{% endtocmaker %}

