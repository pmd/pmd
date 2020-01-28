---
title: Old Release Notes
permalink: pmd_release_notes_old.html
---

Previous versions of PMD can be downloaded here: https://github.com/pmd/pmd/releases

## 24-January-2020 - 6.21.0

The PMD team is pleased to announce PMD 6.21.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Modelica support](#modelica-support)
    * [Simple XML dump of AST](#simple-xml-dump-of-ast)
    * [Updated Apex Support](#updated-apex-support)
    * [CPD XML format](#cpd-xml-format)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [Internal API](#internal-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Modelica support

Thanks to [Anatoly Trosinenko](https://github.com/atrosinenko) PMD supports now a new language:
[Modelica](https://modelica.org/modelicalanguage) is a language to model complex physical systems.
Both PMD and CPD are supported and there are already [3 rules available](pmd_rules_modelica.html).
The PMD Designer supports syntax highlighting for Modelica.

While the language implementation is quite complete, Modelica support is considered experimental
for now. This is to allow us to change the rule API (e.g. the AST classes) slightly and improve
the implementation based on your feedback.

#### Simple XML dump of AST

We added a experimental feature to dump the AST of a source file into XML. The XML format
is of course PMD specific and language dependent. That XML file can be used to execute
(XPath) queries against without PMD. It can also be used as a textual visualization of the AST
if you don't want to use the [Designer](https://github.com/pmd/pmd-designer).

This feature is experimental and might change or even be removed in the future, if it is not
useful. A short description how to use it is available under [Creating XML dump of the AST](pmd_devdocs_experimental_ast_dump.html).

Any feedback about it, especially about your use cases, is highly appreciated.

#### Updated Apex Support

*   The Apex language support has been bumped to version 48 (Spring '20). All new language features are now properly
    parsed and processed.

#### CPD XML format

The CPD XML output format has been enhanced to also report column information for found duplications
in addition to the line information. This allows to display the exact tokens, that are considered
duplicate.

If a CPD language doesn't provide these exact information, then these additional attributes are omitted.

Each `<file>` element in the XML format now has 3 new attributes:

*   attribute `endline`
*   attribute `column` (if there is column information available)
*   attribute `endcolumn` (if there is column information available)

#### Modified Rules

*   The Java rule [`AvoidLiteralsInIfCondition`](https://pmd.github.io/pmd-6.21.0/pmd_rules_java_errorprone.html#avoidliteralsinifcondition) (`java-errorprone`) has a new property
    `ignoreExpressions`. This property is set by default to `true` in order to maintain compatibility. If this
    property is set to false, then literals in more complex expressions are considered as well.

*   The Apex rule [`ApexCSRF`](https://pmd.github.io/pmd-6.21.0/pmd_rules_apex_errorprone.html#apexcsrf) (`apex-errorprone`) has been moved from category
    "Security" to "Error Prone". The Apex runtime already prevents DML statements from being executed, but only
    at runtime. So, if you try to do this, you'll get an error at runtime, hence this is error prone. See also
    the discussion on [#2064](https://github.com/pmd/pmd/issues/2064).

*   The Java rule [`CommentRequired`](https://pmd.github.io/pmd-6.21.0/pmd_rules_java_documentation.html#commentrequired) (`java-documentation`) has a new property
    `classCommentRequirement`. This replaces the now deprecated property `headerCommentRequirement`, since
    the name was misleading. (File) header comments are not checked, but class comments are.

### Fixed Issues

*   apex
    *   [#2208](https://github.com/pmd/pmd/issues/2208): \[apex] ASTFormalComment should implement ApexNode&lt;T&gt;
*   core
    *   [#1984](https://github.com/pmd/pmd/issues/1984): \[java] Cyclomatic complexity is misreported (lack of clearing metrics cache)
    *   [#2006](https://github.com/pmd/pmd/issues/2006): \[core] PMD should warn about multiple instances of the same rule in a ruleset
    *   [#2161](https://github.com/pmd/pmd/issues/2161): \[core] ResourceLoader is deprecated and marked as internal but is exposed
    *   [#2170](https://github.com/pmd/pmd/issues/2170): \[core] DocumentFile doesn't preserve newlines
*   doc
    *   [#2214](https://github.com/pmd/pmd/issues/2214): \[doc] Link broken in pmd documentation for writing Xpath rules
*   java
    *   [#2212](https://github.com/pmd/pmd/issues/2212): \[java] JavaRuleViolation reports wrong class name
*   java-bestpractices
    *   [#2149](https://github.com/pmd/pmd/issues/2149): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#2167](https://github.com/pmd/pmd/issues/2167): \[java] UnnecessaryLocalBeforeReturn false positive with variable captured by method reference
*   java-documentation
    *   [#1683](https://github.com/pmd/pmd/issues/1683): \[java] CommentRequired property names are inconsistent
*   java-errorprone
    *   [#2140](https://github.com/pmd/pmd/issues/2140): \[java] AvoidLiteralsInIfCondition: false negative for expressions
    *   [#2196](https://github.com/pmd/pmd/issues/2196): \[java] InvalidLogMessageFormat does not detect extra parameters when no placeholders
*   java-performance
    *   [#2141](https://github.com/pmd/pmd/issues/2141): \[java] StringInstatiation: False negative with String-array access
*   plsql
    *   [#2008](https://github.com/pmd/pmd/issues/2008): \[plsql] In StringLiteral using alternative quoting mechanism single quotes cause parsing errors
    *   [#2009](https://github.com/pmd/pmd/issues/2009): \[plsql] Multiple DDL commands are skipped during parsing

### API Changes


#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* [`JavaLanguageHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaLanguageHandler.html#)
* [`JavaLanguageParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaLanguageParser.html#)
* [`JavaDataFlowHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaDataFlowHandler.html#)
* Implementations of [`RuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#) in each
  language module, eg [`JavaRuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolationFactory.html#).
  See javadoc of [`RuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#).
* Implementations of [`RuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleViolation.html#) in each language module,
  eg [`JavaRuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#). See javadoc of
  [`RuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleViolation.html#).

* [`RuleFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/rules/RuleFactory.html#)
* [`RuleBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/rules/RuleBuilder.html#)
* Constructors of [`RuleSetFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleSetFactory.html#), use factory methods from [`RulesetsFactoryUtils`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#) instead
* [`getRulesetFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRulesetFactory(net.sourceforge.pmd.PMDConfiguration,net.sourceforge.pmd.util.ResourceLoader))

* [`AbstractApexNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#)
* [`AbstractApexNodeBase`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNodeBase.html#), and the related `visit`
methods on [`ApexParserVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitor.html#) and its implementations.
 Use [`ApexNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#) instead, now considers comments too.

* [`CharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/CharStream.html#), [`JavaCharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/JavaCharStream.html#),
[`SimpleCharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/SimpleCharStream.html#): these are APIs used by our JavaCC
implementations and that will be moved/refactored for PMD 7.0.0. They should not
be used, extended or implemented directly.
* All classes generated by JavaCC, eg [`JJTJavaParserState`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/ast/JJTJavaParserState.html#).
This includes token classes, which will be replaced with a single implementation, and
subclasses of [`ParseException`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/ParseException.html#), whose usages will be replaced
by just that superclass.


##### For removal

* pmd-core
  * Many methods on the [`Node`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/Node.html#) interface
  and [`AbstractNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#) base class. See their javadoc for details.
  * [`Node#isFindBoundary`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/Node.html#isFindBoundary()) is deprecated for XPath queries.
* pmd-java
  * [`AbstractJavaParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/AbstractJavaParser.html#)
  * [`AbstractJavaHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/AbstractJavaHandler.html#)
  * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * [`ASTAnyTypeDeclaration#getKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getKind())
  * [`JavaQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.html#)
  * [`ASTCatchStatement#getBlock`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTCatchStatement.html#getBlock())
  * [`ASTCompilationUnit#declarationsAreInDefaultPackage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#declarationsAreInDefaultPackage())
  * [`JavaQualifiableNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiableNode.html#)
    * [`ASTAnyTypeDeclaration#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getQualifiedName())
    * [`ASTMethodOrConstructorDeclaration#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#getQualifiedName())
    * [`ASTLambdaExpression#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getQualifiedName())
  * [`net.sourceforge.pmd.lang.java.qname`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/qname/package-summary.html#) and its contents
  * [`MethodLikeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/MethodLikeNode.html#)
    * Its methods will also be removed from its implementations,
      [`ASTMethodOrConstructorDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#),
      [`ASTLambdaExpression`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#).
  * [`ASTAnyTypeDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getImage()) will be removed. Please use `getSimpleName()`
    instead. This affects [`ASTAnnotationTypeDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotationTypeDeclaration.html#getImage()),
    [`ASTClassOrInterfaceDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getImage()), and
    [`ASTEnumDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTEnumDeclaration.html#getImage()).
  * Several methods of [`ASTTryStatement`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTTryStatement.html#), replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * [`ASTYieldStatement`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTYieldStatement.html#) will not implement [`TypeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#)
    anymore come 7.0.0. Test the type of the expression nested within it.


### External Contributions

*   [#2041](https://github.com/pmd/pmd/pull/2041): \[modelica] Initial implementation for PMD - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2051](https://github.com/pmd/pmd/pull/2051): \[doc] Update the docs on adding a new language - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2069](https://github.com/pmd/pmd/pull/2069): \[java] CommentRequired: make property names consistent - [snuyanzin](https://github.com/snuyanzin)
*   [#2169](https://github.com/pmd/pmd/pull/2169): \[modelica] Follow-up fixes for Modelica language module - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2193](https://github.com/pmd/pmd/pull/2193): \[core] Fix odd logic in test runner - [Egor Bredikhin](https://github.com/Egor18)
*   [#2194](https://github.com/pmd/pmd/pull/2194): \[java] Fix odd logic in AvoidUsingHardCodedIPRule - [Egor Bredikhin](https://github.com/Egor18)
*   [#2195](https://github.com/pmd/pmd/pull/2195): \[modelica] Normalize invalid node ranges - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2199](https://github.com/pmd/pmd/pull/2199): \[modelica] Fix Javadoc tags - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2225](https://github.com/pmd/pmd/pull/2225): \[core] CPD: report endLine / column informations for found duplications - [Maikel Steneker](https://github.com/maikelsteneker)

## 29-November-2019 - 6.20.0

The PMD team is pleased to announce PMD 6.20.0.

This is a minor release.

### Table Of Contents

* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)

### Fixed Issues

*   apex
    *   [#2092](https://github.com/pmd/pmd/issues/2092): \[apex] ApexLexer logs visible when Apex is the selected language upon starting the designer
    *   [#2136](https://github.com/pmd/pmd/issues/2136): \[apex] Provide access to underlying query of SoqlExpression
*   core
    *   [#2002](https://github.com/pmd/pmd/issues/2002): \[doc] Issue with http://pmdapplied.com/ linking to a gambling Web site
    *   [#2062](https://github.com/pmd/pmd/issues/2062): \[core] Shortnames parameter does not work with Ant
    *   [#2090](https://github.com/pmd/pmd/issues/2090): \[ci] Release notes and draft releases
    *   [#2096](https://github.com/pmd/pmd/issues/2096): \[core] Referencing category errorprone.xml produces deprecation warnings for InvalidSlf4jMessageFormat
*   java
    *   [#1861](https://github.com/pmd/pmd/issues/1861): \[java] Be more lenient with version numbers
    *   [#2105](https://github.com/pmd/pmd/issues/2105): \[java] Wrong name for inner classes in violations
*   java-bestpractices
    *   [#2016](https://github.com/pmd/pmd/issues/2016): \[java] UnusedImports: False positive if wildcard is used and only static methods
*   java-codestyle
    *   [#1362](https://github.com/pmd/pmd/issues/1362): \[java] LinguisticNaming flags Predicates with boolean-style names
    *   [#2029](https://github.com/pmd/pmd/issues/2029): \[java] UnnecessaryFullyQualifiedName false-positive for non-static nested classes
    *   [#2098](https://github.com/pmd/pmd/issues/2098): \[java] UnnecessaryFullyQualifiedName: regression / false positive
*   java-design
    *   [#2075](https://github.com/pmd/pmd/issues/2075): \[java] ImmutableField false positive with inner class
    *   [#2125](https://github.com/pmd/pmd/issues/2125): \[java] ImmutableField: False positive when variable is updated in conditional loop
*   java-errorprone
    *   [#2102](https://github.com/pmd/pmd/issues/2102): \[java] False positive MissingStaticMethodInNonInstantiatableClass when inheritors are instantiable

### External Contributions

*   [#2088](https://github.com/pmd/pmd/pull/2088): \[java] Add more version shortcuts for older java - [Henning Schmiedehausen](https://github.com/hgschmie)
*   [#2089](https://github.com/pmd/pmd/pull/2089): \[core] Minor unrelated improvements to code - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#2091](https://github.com/pmd/pmd/pull/2091): \[core] Fix pmd warnings (IdenticalCatchCases) - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#2106](https://github.com/pmd/pmd/pull/2106): \[java] Wrong name for inner classes - [Andi Pabst](https://github.com/andipabst)
*   [#2121](https://github.com/pmd/pmd/pull/2121): \[java] Predicates treated like booleans - [Ozan Gulle](https://github.com/ozangulle)

## 31-October-2019 - 6.19.0

The PMD team is pleased to announce PMD 6.19.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Java Metrics](#java-metrics)
    * [Modified Rules](#modified-rules)
    * [Renamed Rules](#renamed-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
        * [Internal APIs](#internal-apis)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.19.0).

#### Java Metrics

*   The new metric "Class Fan Out Complexity" has been added. See
    [Java Metrics Documentation](pmd_java_metrics_index.html#class-fan-out-complexity-class_fan_out) for details.


#### Modified Rules

*   The Java rules [`InvalidLogMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidlogmessageformat) and [`MoreThanOneLogger`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#morethanonelogger)
    (`java-errorprone`) now both support [Log4j2](https://logging.apache.org/log4j/2.x/). Note that the
    rule "InvalidSlf4jMessageFormat" has been renamed to "InvalidLogMessageFormat" to reflect the fact, that it now
    supports more than slf4j.

*   The Java rule [`LawOfDemeter`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_design.html#lawofdemeter) (`java-design`) ignores now also Builders, that are
    not assigned to a local variable, but just directly used within a method call chain. The method, that creates
    the builder needs to end with "Builder", e.g. `newBuilder()` or `initBuilder()` works. This change
    fixes a couple of false positives.

*   The Java rule [`DataflowAnomalyAnalysis`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#dataflowanomalyanalysis) (`java-errorprone`) doesn't check for
    UR anomalies (undefined and then referenced) anymore. These checks were all false-positives, since actual
    UR occurrences would lead to compile errors.

*   The java rule [`DoNotUseThreads`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_multithreading.html#donotusethreads) (`java-multithreading`) has been changed
    to not report usages of `java.lang.Runnable` anymore. Just using `Runnable` does not automatically create
    a new thread. While the check for `Runnable` has been removed, the rule now additionally checks for
    usages of `Executors` and `ExecutorService`. Both create new threads, which are not managed by a J2EE
    server.

#### Renamed Rules

*   The Java rule [`InvalidSlf4jMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidslf4jmessageformat) has been renamed to
    [`InvalidLogMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidlogmessageformat) since it supports now both slf4j and log4j2
    message formats.

### Fixed Issues

*   core
    *   [#1978](https://github.com/pmd/pmd/issues/1978): \[core] PMD fails on excluding unknown rules
    *   [#2014](https://github.com/pmd/pmd/issues/2014): \[core] Making add(SourceCode sourceCode) public for alternative file systems
    *   [#2020](https://github.com/pmd/pmd/issues/2020): \[core] Wrong deprecation warnings for unused XPath attributes
    *   [#2036](https://github.com/pmd/pmd/issues/2036): \[core] Wrong include/exclude patterns are silently ignored
    *   [#2048](https://github.com/pmd/pmd/issues/2048): \[core] Enable type resolution by default for XPath rules
    *   [#2067](https://github.com/pmd/pmd/issues/2067): \[core] Build issue on Windows
    *   [#2068](https://github.com/pmd/pmd/pull/2068): \[core] Rule loader should use the same resources loader for the ruleset
    *   [#2071](https://github.com/pmd/pmd/issues/2071): \[ci] Add travis build on windows
    *   [#2072](https://github.com/pmd/pmd/issues/2072): \[test]\[core] Not enough info in "test setup error" when numbers of lines do not match
    *   [#2082](https://github.com/pmd/pmd/issues/2082): \[core] Incorrect logging of deprecated/renamed rules
*   java
    *   [#2042](https://github.com/pmd/pmd/issues/2042): \[java] PMD crashes with ClassFormatError: Absent Code attribute...
*   java-bestpractices
    *   [#1531](https://github.com/pmd/pmd/issues/1531): \[java] UnusedPrivateMethod false-positive with method result
    *   [#2025](https://github.com/pmd/pmd/issues/2025): \[java] UnusedImports when @see / @link pattern includes a FQCN
*   java-codestyle
    *   [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class
*   java-design
    *   [#1912](https://github.com/pmd/pmd/issues/1912): \[java] Metrics not computed correctly with annotations
*   java-errorprone
    *   [#336](https://github.com/pmd/pmd/issues/336): \[java] InvalidSlf4jMessageFormat applies to log4j2
    *   [#1636](https://github.com/pmd/pmd/issues/1636): \[java] Stop checking UR anomalies for DataflowAnomalyAnalysis
*   java-multithreading
    *   [#1627](https://github.com/pmd/pmd/issues/1627): \[java] DoNotUseThreads should not warn on Runnable
*   doc
    * [#2058](https://github.com/pmd/pmd/issues/2058): \[doc] CLI reference for `-norulesetcompatibility` shows a boolean default value


### API Changes

#### Deprecated APIs

##### For removal

* pmd-core
  * All the package [`net.sourceforge.pmd.dcd`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/dcd/package-summary.html#) and its subpackages. See [`DCD`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/dcd/DCD.html#).
  * In [`LanguageRegistry`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#):
    * [`commaSeparatedTerseNamesForLanguageVersion`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguageVersion(List))
    * [`commaSeparatedTerseNamesForLanguage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguage(List))
    * [`findAllVersions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findAllVersions())
    * [`findLanguageVersionByTerseName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findLanguageVersionByTerseName(String))
    * [`getInstance`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#getInstance())
  * [`RuleSet#getExcludePatterns`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getExcludePatterns()). Use the new method [`getFileExclusions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getFileExclusions()) instead.
  * [`RuleSet#getIncludePatterns`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getIncludePatterns()). Use the new method [`getFileInclusions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getFileInclusions()) instead.
  * [`Parser#canParse`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/Parser.html#canParse())
  * [`Parser#getSuppressMap`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/Parser.html#getSuppressMap())
  * [`RuleBuilder#RuleBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/rules/RuleBuilder.html#RuleBuilder(String,String,String)). Use the new constructor with the correct ResourceLoader instead.
  * [`RuleFactory#RuleFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/rules/RuleFactory.html#RuleFactory()). Use the new constructor with the correct ResourceLoader instead.
* pmd-java
  * [`CanSuppressWarnings`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/CanSuppressWarnings.html#) and its implementations
  * [`isSuppressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#isSuppressed(Node))
  * [`getDeclaringType`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#getDeclaringType(Node)).
  * [`isSupressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#isSupressed(Node,Rule))
  * [`ASTMethodDeclarator`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclarator.html#)
  * [`getMethodName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getMethodName())
  * [`getBlock`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getBlock())
  * [`getParameterCount`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#getParameterCount())
* pmd-apex
  * [`CanSuppressWarnings`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.19.0/net/sourceforge/pmd/lang/apex/ast/CanSuppressWarnings.html#) and its implementations
  * [`isSupressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.19.0/net/sourceforge/pmd/lang/apex/rule/ApexRuleViolation.html#isSupressed(Node,Rule))

##### Internal APIs

* pmd-core
  * All the package [`net.sourceforge.pmd.util`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/package-summary.html#) and its subpackages,
  except [`net.sourceforge.pmd.util.datasource`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/datasource/package-summary.html#) and [`net.sourceforge.pmd.util.database`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/database/package-summary.html#).
  * [`GridBagHelper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/cpd/GridBagHelper.html#)
  * [`ColumnDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/renderers/ColumnDescriptor.html#)



### External Contributions

*   [#2010](https://github.com/pmd/pmd/pull/2010): \[java] LawOfDemeter to support inner builder pattern - [Gregor Riegler](https://github.com/gregorriegler)
*   [#2012](https://github.com/pmd/pmd/pull/2012): \[java] Fixes 336, slf4j log4j2 support - [Mark Hall](https://github.com/markhall82)
*   [#2032](https://github.com/pmd/pmd/pull/2032): \[core] Allow adding SourceCode directly into CPD - [Nathan Braun](https://github.com/nbraun-Google)
*   [#2047](https://github.com/pmd/pmd/pull/2047): \[java] Fix computation of metrics with annotations - [Andi Pabst](https://github.com/andipabst)
*   [#2065](https://github.com/pmd/pmd/pull/2065): \[java] Stop checking UR anomalies - [Carlos Macasaet](https://github.com/l0s)
*   [#2068](https://github.com/pmd/pmd/pull/2068): \[core] Rule loader should use the same resources loader for the ruleset - [Chen Yang](https://github.com/willamette)
*   [#2070](https://github.com/pmd/pmd/pull/2070): \[core] Fix renderer tests for windows builds - [Saladoc](https://github.com/Saladoc)
*   [#2073](https://github.com/pmd/pmd/pull/2073): \[test]\[core] Add expected and actual line of numbers to message wording - [snuyanzin](https://github.com/snuyanzin)
*   [#2076](https://github.com/pmd/pmd/pull/2076): \[java] Add Metric ClassFanOutComplexity - [Andi Pabst](https://github.com/andipabst)
*   [#2078](https://github.com/pmd/pmd/pull/2078): \[java] DoNotUseThreads should not warn on Runnable #1627 - [Michael Clay](https://github.com/mclay)

## 15-September-2019 - 6.18.0

The PMD team is pleased to announce PMD 6.18.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 13 Support](#java-13-support)
    * [Full support for Scala](#full-support-for-scala)
    * [New rule designer documentation](#new-rule-designer-documentation)
    * [New rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Changes to Renderer](#changes-to-renderer)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
        * [Internal APIs](#internal-apis)
* [External Contributions](#external-contributions)

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

*   The Java rule [`AvoidMessageDigestField`](https://pmd.github.io/pmd-6.18.0/pmd_rules_java_bestpractices.html#avoidmessagedigestfield) (`java-bestpractices`) detects fields
    of the type `java.security.MessageDigest`. Using a message digest instance as a field would need to be
    synchronized, as it can easily be used by multiple threads. Without synchronization the calculated hash could
    be entirely wrong. Instead of declaring this as a field and synchronize access to use it from multiple threads,
    a new instance should be created when needed. This rule is also active when using java's quickstart ruleset.

*   The Apex rule [`DebugsShouldUseLoggingLevel`](https://pmd.github.io/pmd-6.18.0/pmd_rules_apex_bestpractices.html#debugsshoulduselogginglevel) (`apex-bestpractices`) detects
    usages of `System.debug()` method calls that are used without specifying the log level. Having the log
    level specified provides a cleaner log, and improves readability of it.

#### Modified Rules

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.18.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) now ignores by default instances
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
    *   [#1227](https://github.com/pmd/pmd/issues/1227): \[java] UnusedFormalParameter should explain checkAll better
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

*   Each renderer has now a new method [`Renderer#setUseShortNames`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/renderers/Renderer.html#setUseShortNames(List)) which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    [`AbstractRenderer#determineFileName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/renderers/AbstractRenderer.html#determineFileName(String)) should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.
    
    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

#### Deprecated APIs

##### For removal

*   The methods [`getImportedNameNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getImportedNameNode()) and
    [`getPackage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getPackage()) have been deprecated and
    will be removed with PMD 7.0.0.
*   The method [`RuleContext#setSourceCodeFilename`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFilename(String)) has been deprecated
    and will be removed. The already existing method [`RuleContext#setSourceCodeFile`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFile(File))
    should be used instead. The method [`RuleContext#getSourceCodeFilename`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#getSourceCodeFilename()) still
    exists and returns just the filename without the full path.
*   The method [`AbstractPMDProcessor#filenameFrom`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/processor/AbstractPMDProcessor.html#filenameFrom(DataSource)) has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The method [`Report#metrics`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/Report.html#metrics()) and [`Report`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/Report.html#) have
    been deprecated. They were leftovers from a previous deprecation round targeting
    [`StatisticalRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#).

##### Internal APIs

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
  * [`net.sourceforge.pmd.cache`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/cache/package-summary.html#)
* pmd-java
  * [`net.sourceforge.pmd.lang.java.typeresolution`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/package-summary.html#): Everything, including
    subpackages, except [`TypeHelper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#) and
    [`JavaTypeDefinition`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/typedefinition/JavaTypeDefinition.html#).
  * [`ASTCompilationUnit#getClassTypeResolver`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#getClassTypeResolver())

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
*   [#2015](https://github.com/pmd/pmd/pull/2015): \[java] Update doc for unused formal parameter - [Amish Shah](https://github.com/shahamish150294)

## 28-July-2019 - 6.17.0

The PMD team is pleased to announce PMD 6.17.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Lua support](#lua-support)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.17.0).
It contains a new feature to edit test cases directly within the designer. Any feedback is highly appreciated.

#### Lua support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Lua](https://www.lua.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

#### Modified Rules

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.17.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) ignores now by default
    `java.io.ByteArrayInputStream` and `java.io.CharArrayWriter`. Such streams/writers do not need to be closed.

*   The Java rule [`MissingStaticMethodInNonInstantiatableClass`](https://pmd.github.io/pmd-6.17.0/pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass) (`java-errorprone`) has now
    the new property `annotations`.
    When one of the private constructors is annotated with one of the annotations, then the class is not considered
    non-instantiatable anymore and no violation will be reported. By default, Spring's `@Autowired` and
    Java EE's `@Inject` annotations are recognized.

### Fixed Issues

*   core
    *   [#1913](https://github.com/pmd/pmd/issues/1913): \[core] "-help" CLI option ends with status code != 0
*   doc
    *   [#1896](https://github.com/pmd/pmd/issues/1896): \[doc] Error in changelog 6.16.0 due to not properly closed rule tag
    *   [#1898](https://github.com/pmd/pmd/issues/1898): \[doc] Incorrect code example for DoubleBraceInitialization in documentation on website
    *   [#1906](https://github.com/pmd/pmd/issues/1906): \[doc] Broken link for adding own CPD languages
    *   [#1909](https://github.com/pmd/pmd/issues/1909): \[doc] Sample usage example refers to deprecated ruleset "basic.xml" instead of "quickstart.xml"
*   java
    *   [#1910](https://github.com/pmd/pmd/issues/1910): \[java] ATFD calculation problem
*   java-errorprone
    *   [#1749](https://github.com/pmd/pmd/issues/1749): \[java] DD False Positive in DataflowAnomalyAnalysis
    *   [#1832](https://github.com/pmd/pmd/issues/1832): \[java] False positives for MissingStaticMethodInNonInstantiatableClass when DI is used
    *   [#1921](https://github.com/pmd/pmd/issues/1921): \[java] CloseResource false positive with ByteArrayInputStream
*   java-multithreading
    *   [#1903](https://github.com/pmd/pmd/issues/1903): \[java] UnsynchronizedStaticFormatter doesn't allow block-level synchronization when using allowMethodLevelSynchronization=true
*   plsql
    *   [#1902](https://github.com/pmd/pmd/issues/1902): \[pslql] ParseException when parsing (+)
*   xml
    *   [#1666](https://github.com/pmd/pmd/issues/1666): \[xml] wrong cdata rule description and examples

### External Contributions

*   [#1869](https://github.com/pmd/pmd/pull/1869): \[xml] fix #1666 wrong cdata rule description and examples - [Artem](https://github.com/KroArtem)
*   [#1892](https://github.com/pmd/pmd/pull/1892): \[lua] \[cpd] Added CPD support for Lua - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1905](https://github.com/pmd/pmd/pull/1905): \[java] DataflowAnomalyAnalysis Rule in right order - [YoonhoChoi96](https://github.com/YoonhoChoi96)
*   [#1908](https://github.com/pmd/pmd/pull/1908): \[doc] Update ruleset filename from deprecated basic.xml to quickstart.xml - [crunsk](https://github.com/crunsk)
*   [#1916](https://github.com/pmd/pmd/pull/1916): \[java] Exclude Autowired and Inject for MissingStaticMethodInNonInstantiatableClass - [AnthonyKot](https://github.com/AnthonyKot)
*   [#1917](https://github.com/pmd/pmd/pull/1917): \[core] Add 'no error' return option, and assign it to the cli when the help command is invoked - [Renato Oliveira](https://github.com/renatoliveira)

## 30-June-2019 - 6.16.0

The PMD team is pleased to announce PMD 6.16.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [PLSQL Grammar Updates](#plsql-grammar-updates)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [In ASTs](#in-asts)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.16.0).

#### PLSQL Grammar Updates

The grammar has been updated to support inline constraints in CREATE TABLE statements. Additionally, the
CREATE TABLE statement may now be followed by physical properties and table properties. However, these
properties are skipped over during parsing.

The CREATE VIEW statement now supports subquery views.

The EXTRACT function can now be parsed correctly. It is used to extract values from a specified
datetime field. Also date time literals are parsed now correctly.

The CASE expression can now be properly used within SELECT statements.

Table aliases are now supported when specifying columns in INSERT INTO clauses.

#### New Rules

*   The Java rule [`DoubleBraceInitialization`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#doublebraceinitialization) (`java-bestpractices`)
    detects non static initializers in anonymous classes also known as "double brace initialization".
    This can be problematic, since a new class file is generated and object holds a strong reference
    to the surrounding class.
    
    Note: This rule is also part of the Java quickstart ruleset (`rulesets/java/quickstart.xml`).

#### Modified Rules

*   The Java rule [`UnusedPrivateField`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#unusedprivatefield) (`java-bestpractices`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rule [`SingularField`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_design.html#singularfield) (`java-design`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rules [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter) and
    [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) (`java-multithreading`)
    now prefer synchronized blocks by default. They will raise a violation, if the synchronization is implemented
    on the method level. To allow the old behavior, the new property `allowMethodLevelSynchronization` can
    be enabled.

*   The Java rule [`UseUtilityClass`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_design.html#useutilityclass) (`java-design`) has a new property `ignoredAnnotations`.
    By default, classes that are annotated with Lombok's `@UtilityClass` are ignored now.

*   The Java rule [`NonStaticInitializer`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#nonstaticinitializer) (`java-errorprone`) does not report
    non static initializers in anonymous classes anymore. For this use case, there is a new rule now:
    [`DoubleBraceInitialization`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#doublebraceinitialization) (`java-bestpractices`).

*   The Java rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`) was enhanced
    in the last version 6.15.0 to check also top-level types by default. This created many new violations.
    Missing the access modifier for top-level types is not so critical, since it only decreases the visibility
    of the type.
    
    The default behaviour has been restored. If you want to enable the check for top-level types, you can
    use the new property `checkTopLevelTypes`.

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) now by default searches
    for any unclosed `java.lang.AutoCloseable` resource. This includes now the standard `java.io.*Stream` classes.
    Previously only SQL-related resources were considered by this rule. The types can still be configured
    via the `types` property. Some resources do not need to be closed (e.g. `ByteArrayOutputStream`). These
    exceptions can be configured via the new property `allowedResourceTypes`.
    In order to restore the old behaviour, just remove the type `java.lang.AutoCloseable` from the `types`
    property and keep the remaining SQL-related classes.

#### Deprecated Rules

*   The Java rule [`AvoidFinalLocalVariable`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#avoidfinallocalvariable) (`java-codestyle`) has been deprecated
    and will be removed with PMD 7.0.0. The rule is controversial and also contradicts other existing
    rules such as [`LocalVariableCouldBeFinal`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#localvariablecouldbefinal). If the goal is to avoid defining
    constants in a scope smaller than the class, then the rule [`AvoidDuplicateLiterals`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#avoidduplicateliterals)
    should be used instead.

### Fixed Issues

*   apex
    *   [#1664](https://github.com/pmd/pmd/issues/1664): \[apex] False positive ApexSharingViolationsRule, unsupported Apex feature
*   java
    *   [#1848](https://github.com/pmd/pmd/issues/1848): \[java] Local classes should preserve their modifiers
*   java-bestpractices
    *   [#1703](https://github.com/pmd/pmd/issues/1703): \[java] UnusedPrivateField on member annotated with lombok @Delegate
    *   [#1845](https://github.com/pmd/pmd/issues/1845): \[java] Regression in MethodReturnsInternalArray not handling enums
    *   [#1854](https://github.com/pmd/pmd/issues/1854): \[java] Rule to check for double brace initialisation
*   java-codestyle
    *   [#1612](https://github.com/pmd/pmd/issues/1612): \[java] Deprecate AvoidFinalLocalVariable
    *   [#1880](https://github.com/pmd/pmd/issues/1880): \[java] CommentDefaultAccessModifier should be configurable for top-level classes
*   java-design
    *   [#1094](https://github.com/pmd/pmd/issues/1094): \[java] UseUtilityClass should be LombokAware
*   java-errorprone
    *   [#1000](https://github.com/pmd/pmd/issues/1000): \[java] The rule CloseResource should deal with IO stream as default
    *   [#1853](https://github.com/pmd/pmd/issues/1853): \[java] False positive for NonStaticInitializer in anonymous class
*   java-multithreading
    *   [#1814](https://github.com/pmd/pmd/issues/1814): \[java] UnsynchronizedStaticFormatter documentation and implementation wrong
    *   [#1815](https://github.com/pmd/pmd/issues/1815): \[java] False negative in UnsynchronizedStaticFormatter
*   plsql
    *   [#1828](https://github.com/pmd/pmd/issues/1828): \[plsql] Parentheses stopped working
    *   [#1850](https://github.com/pmd/pmd/issues/1850): \[plsql] Parsing errors with INSERT using returning or records and TRIM expression
    *   [#1873](https://github.com/pmd/pmd/issues/1873): \[plsql] Expression list not working
    *   [#1878](https://github.com/pmd/pmd/issues/1878): \[pslql] ParseException when parsing USING
    *   [#1879](https://github.com/pmd/pmd/issues/1879): \[pslql] ParseException when parsing LEFT JOIN

### API Changes

#### Deprecated APIs

> Reminder: Please don't use members marked with the annotation [`InternalApi`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/annotation/InternalApi.html#), as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


##### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked [`InternalApi`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/annotation/InternalApi.html#). Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
  * In the meantime you should use interfaces like [`JavaNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.16.0/net/sourceforge/pmd/lang/java/ast/JavaNode.html#) or  [`Node`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/lang/ast/Node.html#), or the other published interfaces in this package, to refer to nodes generically.
  * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at [`net.sourceforge.pmd.lang.java.ast`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.16.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#) to find out the full list
of deprecations.





### External Contributions

*   [#1482](https://github.com/pmd/pmd/pull/1482): \[java] Explain the existence of AvoidFinalLocalVariable in it's description - [Karl-Philipp Richter](https://github.com/krichter722)
*   [#1792](https://github.com/pmd/pmd/pull/1792): \[java] Added lombok.experimental to AbstractLombokAwareRule - [jakivey32](https://github.com/jakivey32)
*   [#1808](https://github.com/pmd/pmd/pull/1808): \[plsql] Fix PL/SQL Syntax errors - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1829](https://github.com/pmd/pmd/pull/1829): \[java] Fix false negative in UnsynchronizedStaticFormatter - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1847](https://github.com/pmd/pmd/pull/1847): \[java] Regression in MethodReturnsInternalArray not handling enums - [Artem](https://github.com/KroArtem)
*   [#1863](https://github.com/pmd/pmd/pull/1863): \[plsql] Add Table InlineConstraint - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1864](https://github.com/pmd/pmd/pull/1864): \[plsql] Add support for Subquery Views - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1865](https://github.com/pmd/pmd/pull/1865): \[plsql] Add Support for Extract Expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1874](https://github.com/pmd/pmd/pull/1874): \[plsql] Add parenthesis equation support for Update - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1876](https://github.com/pmd/pmd/pull/1876): \[plsql] Datetime support for queries - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1883](https://github.com/pmd/pmd/pull/1883): \[plsql] Fix #1873 Expression list not working - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1884](https://github.com/pmd/pmd/pull/1884): \[plsql] fix #1878 Support explicit INNER word for INNER JOIN - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1885](https://github.com/pmd/pmd/pull/1885): \[plsql] Correct case expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1886](https://github.com/pmd/pmd/pull/1886): \[plsql] Support table alias for Insert Clause - [Hugo Araya Nash](https://github.com/kabroxiko)

## 26-May-2019 - 6.15.0

The PMD team is pleased to announce PMD 6.15.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Enhanced Matlab support](#enhanced-matlab-support)
    * [Enhanced C++ support](#enhanced-c++-support)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Enhanced Matlab support

Thanks to the contributions from [Maikel Steneker](https://github.com/maikelsteneker) CPD for Matlab can
now parse Matlab programs which use the question mark operator to specify access to
class members:

```
classdef Class1
properties (SetAccess = ?Class2)
```

CPD also understands now double quoted strings, which are supported since version R2017a of Matlab:

```
str = "This is a string"
```

#### Enhanced C++ support

CPD now supports digit separators in C++ (language module "cpp"). This is a C++14 feature.

Example: `auto integer_literal = 1'000'000;`

The single quotes can be used to add some structure to large numbers.

CPD also parses raw string literals now correctly (see [#1784](https://github.com/pmd/pmd/issues/1784)).

#### New Rules

*   The new Apex rule [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#fieldnamingconventions) (`apex-codestyle`) checks the naming
    conventions for field declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Apex rule [`FormalParameterNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#formalparameternamingconventions) (`apex-codestyle`) checks the
    naming conventions for formal parameters of methods. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule [`LocalVariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#localvariablenamingconventions) (`apex-codestyle`) checks the
    naming conventions for local variable declarations. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule [`PropertyNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#propertynamingconventions) (`apex-codestyle`) checks the naming
    conventions for property declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Java rule [`UseShortArrayInitializer`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#useshortarrayinitializer) (`java-codestyle`) searches for
    array initialization expressions, which can be written shorter.

#### Modified Rules

*   The Apex rule [`ClassNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#classnamingconventions) (`apex-codestyle`) can now be configured
    using various properties for the specific kind of type declarations (e.g. class, interface, enum).
    As before, this rule uses by default the standard Apex naming convention (Pascal case).

*   The Apex rule [`MethodNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#methodnamingconventions) (`apex-codestyle`) can now be configured
    using various properties to differenciate e.g. static methods and test methods.
    As before, this rule uses by default the standard Apex naming convention (Camel case).

*   The Java rule [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#fieldnamingconventions) (`java-codestyle`) now by default ignores
    the field `serialPersistentFields`. Since this is a field which needs to have this special name, no
    field naming conventions can be applied here. It is excluded the same way like `serialVersionUID` via the
    property `exclusions`.

*   The Java rule [`CommentRequired`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_documentation.html#commentrequired) (`java-documentation`) has a new property
    `serialPersistentFieldsCommentRequired` with the default value "Ignored". This means that from now
    on comments for the field `serialPersistentFields` are not required anymore. You can change the property
    to restore the old behavior.

*   The Java rule [`ProperLogger`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#properlogger) (`java-errorprone`) has two new properties
    to configure the logger class (e.g. "org.slf4j.Logger") and the logger name of the special case,
    when the logger is not static. The name of the static logger variable was already configurable.
    The new property "loggerClass" allows to use this rule for different logging frameworks.
    This rule covers all the cases of the now deprecated rule [`LoggerIsNotStaticFinal`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#loggerisnotstaticfinal).

*   The Java rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`) now reports also
    missing comments for top-level classes and annotations, that are package-private.

#### Deprecated Rules

*   The Apex rule [`VariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#variablenamingconventions) (`apex-codestyle`) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general rules
    [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#fieldnamingconventions),
    [`FormalParameterNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#formalparameternamingconventions),
    [`LocalVariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#localvariablenamingconventions), and
    [`PropertyNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#propertynamingconventions).

*   The Java rule [`LoggerIsNotStaticFinal`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#loggerisnotstaticfinal) (`java-errorprone`) has been deprecated
    and will be removed with PMD 7.0.0. The rule is replaced by [`ProperLogger`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#properlogger).

### Fixed Issues

*   apex
    *   [#1321](https://github.com/pmd/pmd/issues/1321): \[apex] Should VariableNamingConventions require properties to start with a lowercase letter?
    *   [#1783](https://github.com/pmd/pmd/issues/1783): \[apex] comments on constructor not recognized when the Class has inner class
*   cpp
    *   [#1784](https://github.com/pmd/pmd/issues/1784): \[cpp] Improve support for raw string literals
*   dart
    *   [#1809](https://github.com/pmd/pmd/issues/1809): \[dart] \[cpd] Parse error with escape sequences
*   java
    *   [#1842](https://github.com/pmd/pmd/issues/1842): \[java] Annotated module declarations cause parse error
*   java-bestpractices
    *   [#1738](https://github.com/pmd/pmd/issues/1738): \[java] MethodReturnsInternalArray does not work in inner classes
*   java-codestyle
    *   [#1495](https://github.com/pmd/pmd/issues/1495): \[java] Rule to detect overly verbose array initializiation
    *   [#1684](https://github.com/pmd/pmd/issues/1684): \[java] Properly whitelist serialPersistentFields
    *   [#1804](https://github.com/pmd/pmd/issues/1804): \[java] NPE in UnnecessaryLocalBeforeReturnRule
*   python
    *   [#1810](https://github.com/pmd/pmd/issues/1810): \[python] \[cpd] Parse error when using Python 2 backticks
*   matlab
    *   [#1830](https://github.com/pmd/pmd/issues/1830): \[matlab] \[cpd] Parse error with comments
    *   [#1793](https://github.com/pmd/pmd/issues/1793): \[java] CommentDefaultAccessModifier not working for classes

### API Changes

#### Deprecated APIs

##### For removal

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
    *   [`net.sourceforge.pmd.lang.apex.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.15.0/net/sourceforge/pmd/lang/apex/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.java.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.15.0/net/sourceforge/pmd/lang/java/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.ecmascript.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-javascript/6.15.0/net/sourceforge/pmd/lang/ecmascript/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.jsp.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-jsp/6.15.0/net/sourceforge/pmd/lang/jsp/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.plsql.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-plsql/6.15.0/net/sourceforge/pmd/lang/plsql/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.vf.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-visualforce/6.15.0/net/sourceforge/pmd/lang/vf/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.vm.ast.AbstractVmNode#dump`](https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.15.0/net/sourceforge/pmd/lang/vm/ast/AbstractVmNode.html#dump(String,boolean,Writer))
    *   [`net.sourceforge.pmd.lang.xml.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-xml/6.15.0/net/sourceforge/pmd/lang/xml/ast/DumpFacade.html#)
*   The method [`LanguageVersionHandler#getDumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.15.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDumpFacade(Writer,String,boolean)) will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of [`LanguageVersionHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.15.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#).

### External Contributions

*   [#1647](https://github.com/pmd/pmd/pull/1647): \[java] Rule to detect overly verbose array initialization - [Victor](https://github.com/IDoCodingStuffs)
*   [#1762](https://github.com/pmd/pmd/pull/1762): \[java] LoggerIsNotStaticFinal and ProperLogger - make class-name configurable - [Ivo Šmíd](https://github.com/bedla)
*   [#1798](https://github.com/pmd/pmd/pull/1798): \[java] Make CommentDefaultAccessModifier work for top-level classes - [Boris Petrov](https://github.com/boris-petrov)
*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1807](https://github.com/pmd/pmd/pull/1807): \[ci] Fix missing local branch issues when executing pmd-regression-tester - [BBG](https://github.com/djydewang)
*   [#1813](https://github.com/pmd/pmd/pull/1813): \[matlab] \[cpd] Matlab comments - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1816](https://github.com/pmd/pmd/pull/1816): \[apex] Fix ApexDoc handling with inner classes - [Jeff Hube](https://github.com/jeffhube)
*   [#1817](https://github.com/pmd/pmd/pull/1817): \[apex] Add configurable naming convention rules - [Jeff Hube](https://github.com/jeffhube)
*   [#1819](https://github.com/pmd/pmd/pull/1819): \[cpp] \[cpd] Add support for digit separators - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1820](https://github.com/pmd/pmd/pull/1820): \[cpp] \[cpd] Improve support for raw string literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1821](https://github.com/pmd/pmd/pull/1821): \[matlab] \[cpd] Matlab question mark token - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1822](https://github.com/pmd/pmd/pull/1822): \[matlab] \[cpd] Double quoted string - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1837](https://github.com/pmd/pmd/pull/1837): \[core] Minor performance improvements - [Michael Hausegger](https://github.com/TheRealHaui)
*   [#1838](https://github.com/pmd/pmd/pull/1838): \[dart] [cpd] Improved string tokenization - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1840](https://github.com/pmd/pmd/pull/1840): \[java] Whitelist serialPersistentFields - [Marcel Härle](https://github.com/marcelhaerle)

## 28-April-2019 - 6.14.0

The PMD team is pleased to announce PMD 6.14.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Dart support](#dart-support)
    * [Updated PMD Designer](#updated-pmd-designer)
* [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Dart support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Dart](https://www.dartlang.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   ignore imports / libraries
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/blob/6.14.0/CHANGELOG.md).

### Modified Rules

*   The Java rule [`AssignmentToNonFinalStatic`](https://pmd.github.io/pmd-6.14.0/pmd_rules_java_errorprone.html#assignmenttononfinalstatic) (`java-errorprone`) will now report on each
    assignment made within a constructor rather than on the field declaration. This makes it easier for developers to
    find the offending statements.

*   The Java rule [`NoPackage`](https://pmd.github.io/pmd-6.14.0/pmd_rules_java_codestyle.html#nopackage) (`java-codestyle`) will now report additionally enums
    and annotations that do not have a package declaration.

### Fixed Issues

*   all
    *   [#1515](https://github.com/pmd/pmd/issues/1515): \[core] Module pmd-lang-test is missing javadoc artifact
    *   [#1788](https://github.com/pmd/pmd/issues/1788): \[cpd] \[core] Use better `ClassLoader` for `ServiceLoader` in `LanguageFactory`
    *   [#1794](https://github.com/pmd/pmd/issues/1794): \[core] Ruleset Compatibility fails with excluding rules
*   go
    *   [#1751](https://github.com/pmd/pmd/issues/1751): \[go] Parsing errors encountered with escaped backslash
*   java
    *   [#1532](https://github.com/pmd/pmd/issues/1532): \[java] NPE with incomplete auxclasspath
    *   [#1691](https://github.com/pmd/pmd/issues/1691): \[java] Possible Data Race in JavaTypeDefinitionSimple.getGenericType
    *   [#1729](https://github.com/pmd/pmd/issues/1729): \[java] JavaRuleViolation loses information in `className` field when class has package-private access level
*   java-bestpractices
    *   [#1190](https://github.com/pmd/pmd/issues/1190): \[java] UnusedLocalVariable/UnusedPrivateField false-positive 
    *   [#1720](https://github.com/pmd/pmd/issues/1720): \[java] UnusedImports false positive for Javadoc link with array type
*   java-codestyle
    *   [#1755](https://github.com/pmd/pmd/issues/1775): \[java] False negative in UnnecessaryLocalBeforeReturn when splitting statements across multiple lines
    *   [#1782](https://github.com/pmd/pmd/issues/1782): \[java] NoPackage: False Negative for enums
*   java-design
    *   [#1760](https://github.com/pmd/pmd/issues/1760): \[java] UseObjectForClearerAPI flags private methods

### API Changes

No changes.

### External Contributions

*   [#1745](https://github.com/pmd/pmd/pull/1745): \[doc] Fixed some errors in docs - [0xflotus](https://github.com/0xflotus)
*   [#1746](https://github.com/pmd/pmd/pull/1746): \[java] Update rule to prevent UnusedImport when using JavaDoc with array type - [itaigilo](https://github.com/itaigilo)
*   [#1752](https://github.com/pmd/pmd/pull/1752): \[java] UseObjectForClearerAPI Only For Public - [Björn Kautler](https://github.com/Vampire)
*   [#1761](https://github.com/pmd/pmd/pull/1761): \[dart] \[cpd] Added CPD support for Dart - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1776](https://github.com/pmd/pmd/pull/1776): \[java] Show more detailed message when can't resolve field type - [Andrey Fomin](https://github.com/andrey-fomin)
*   [#1781](https://github.com/pmd/pmd/pull/1781): \[java] Location change in AssignmentToNonFinalStatic - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1789](https://github.com/pmd/pmd/pull/1789): \[cpd] \[core] Use current classloader instead of Thread's classloader - [Andreas Schmid](https://github.com/aaschmid)
*   [#1791](https://github.com/pmd/pmd/pull/1791): \[dart] \[cpd] Dart escaped string - [Maikel Steneker](https://github.com/maikelsteneker)

## 31-March-2019 - 6.13.0

The PMD team is pleased to announce PMD 6.13.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Call For Logo](#call-for-logo)
    * [Java 12 Support](#java-12-support)
    * [Quickstart Ruleset for Apex](#quickstart-ruleset-for-apex)
    * [PMD Designer](#pmd-designer)
    * [Improved Apex Support](#improved-apex-support)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Command Line Interface](#command-line-interface)
    * [Deprecated API](#deprecated-api)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Call For Logo

We are still searching for a new logo for PMD for the next major release.

Learn more about how to participate on [github issue 1663](https://github.com/pmd/pmd/issues/1663).

#### Java 12 Support

This release of PMD brings support for Java 12. PMD can parse the new [Switch Expressions](http://openjdk.java.net/jeps/325)
and resolve the type of such an expression.

Note: The Switch Expressions are a preview language feature of OpenJDK 12 and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the new environment
variable `PMD_JAVA_OPTS`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd ...

#### Quickstart Ruleset for Apex

PMD provides now a quickstart ruleset for Salesforce.com Apex, which you can use as a base ruleset to
get your custom ruleset started. You can reference it with `rulesets/apex/quickstart.xml`.
You are strongly encouraged to [create your own ruleset](https://pmd.github.io/pmd-6.12.0/pmd_userdocs_making_rulesets.html)
though.

The quickstart ruleset has the intention, to be useful out-of-the-box for many projects. Therefore it
references only rules, that are most likely to apply everywhere.

Any feedback would be greatly appreciated.

#### PMD Designer

The rule designer's codebase has been moved out of the main repository and
will be developed at [pmd/pmd-designer](https://github.com/pmd/pmd-designer)
from now on. The maven coordinates will stay the same for the time being.
The designer will still be shipped with PMD's binaries.

#### Improved Apex Support

*   Many AST nodes now expose more information which makes it easier to write XPath-based rules for Apex. Here are
    some examples:
    *   `Annotation[@Resolved = false()]` finds unsupported annotations.
    *   `AnnotationParameter[@Name='RestResource'][@Value='/myurl']` gives access to
        annotation parameters.
    *   `CatchBlockStatement[@ExceptionType='Exception'][@VariableName='e']` finds catch
        block for specific exception types.
    *   `Field[@Type='String']` find all String fields, `Field[string-length(@Name) < 5]`
        finds all fields with short names and `Field[@Value='a']` find alls fields, that are
        initialized with a specific value.
    *   `LiteralExpression[@String = true()]` finds all String literals. There are attributes
        for each type: `@Boolean`, `@Integer`, `@Double`, `@Long`, `@Decimal`, `@Null`.
    *   `Method[@Constructor = true()]` selects all constructors. `Method[@ReturnType = 'String']`
        selects all methods that return a String.
    *   The `ModifierNode` node has a couple of attributes to check for the existence of specific
        modifiers: `@Test`, `@TestOrTestSetup`, `@WithSharing`, `@WithoutSharing`, `@InheritedSharing`,
        `@WebService`, `@Global`, `@Override`.
    *   Many nodes now expose their type. E.g. with `Parameter[@Type='Integer']` you can find all
        method parameters of type Integer. The same attribute `Type` exists as well for:
        `NewObjectExpression`, `Property`, `VariableDeclaration`.
    *   `VariableExpression[@Image='i']` finds all variable usages of the variable "i".

#### New Rules

*   The new Java rule [`AvoidUncheckedExceptionsInSignatures`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_design.html#avoiduncheckedexceptionsinsignatures) (`java-design`) finds methods or constructors
    that declare unchecked exceptions in their `throws` clause. This forces the caller to handle the exception,
    even though it is a runtime exception.

*   The new Java rule [`DetachedTestCase`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_errorprone.html#detachedtestcase) (`java-errorprone`) searches for public
    methods in test classes, which are not annotated with `@Test`. These methods might be test cases where
    the annotation has been forgotten. Because of that those test cases are never executed.

*   The new Java rule [`WhileLoopWithLiteralBoolean`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_bestpractices.html#whileloopwithliteralboolean) (`java-bestpractices`) finds
    Do-While-Loops and While-Loops that can be simplified since they use simply `true` or `false` as their
    loop condition.

*   The new Apex rule [`ApexAssertionsShouldIncludeMessage`](https://pmd.github.io/pmd-6.13.0/pmd_rules_apex_bestpractices.html#apexassertionsshouldincludemessage) (`apex-bestpractices`)
    searches for assertions in unit tests and checks, whether they use a message argument.

*   The new Apex rule [`ApexUnitTestMethodShouldHaveIsTestAnnotation`](https://pmd.github.io/pmd-6.13.0/pmd_rules_apex_bestpractices.html#apexunittestmethodshouldhaveistestannotation) (`apex-bestpractices`)
    searches for methods in test classes, which are missing the `@IsTest` annotation.

*   The new PLSQL rule [`AvoidTabCharacter`](https://pmd.github.io/pmd-6.13.0/pmd_rules_plsql_codestyle.html#avoidtabcharacter) (`plsql-codestyle`) checks, that there are
    no tab characters ("\t") in the source file.

*   The new PLSQL rule [`LineLength`](https://pmd.github.io/pmd-6.13.0/pmd_rules_plsql_codestyle.html#linelength) (`plsql-codestyle`) helps to enforce a maximum
    line length.

### Fixed Issues

*   doc
    *   [#1721](https://github.com/pmd/pmd/issues/1721): \[doc] Documentation provides an invalid property configuration example
*   java
    *   [#1537](https://github.com/pmd/pmd/issues/1537): \[java] Java 12 support
*   java-bestpractices
    *   [#1701](https://github.com/pmd/pmd/issues/1701): \[java] UseTryWithResources does not handle multiple argument close methods
*   java-codestyle
    *   [#1527](https://github.com/pmd/pmd/issues/1527): \[java] UseUnderscoresInNumericLiterals false positive on floating point numbers
    *   [#1674](https://github.com/pmd/pmd/issues/1674): \[java] documentation of CommentDefaultAccessModifier is wrong
*   java-errorprone
    *   [#1570](https://github.com/pmd/pmd/issues/1570): \[java] AvoidDuplicateLiterals warning about deprecated separator property when not used
*   plsql
    *   [#1510](https://github.com/pmd/pmd/issues/1510): \[plsql] Support XMLTABLE functions
    *   [#1716](https://github.com/pmd/pmd/issues/1716): \[plsql] Support access to whole plsql code
    *   [#1731](https://github.com/pmd/pmd/issues/1731): \[pslql] ParseException when parsing ELSIF
    *   [#1733](https://github.com/pmd/pmd/issues/1733): \[plsql] % not supported in "TestSearch%notfound"
    *   [#1734](https://github.com/pmd/pmd/issues/1734): \[plsql] TooManyMethods false-negative
    *   [#1735](https://github.com/pmd/pmd/issues/1735): \[plsql] False-negatives for TO_DATE_TO_CHAR, TO_DATEWithoutDateFormat, TO_TIMESTAMPWithoutDateFormat

### API Changes

#### Command Line Interface

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

#### Deprecated API

*   [`CodeClimateRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.13.0/net/sourceforge/pmd/renderers/CodeClimateRule.html#) is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

### External Contributions

*   [#1694](https://github.com/pmd/pmd/pull/1694): \[apex] New rules for test method and assert statements - [triandicAnt](https://github.com/triandicAnt)
*   [#1697](https://github.com/pmd/pmd/pull/1697): \[doc] Update CPD documentation - [Matías Fraga](https://github.com/matifraga)
*   [#1704](https://github.com/pmd/pmd/pull/1704): \[java] Added AvoidUncheckedExceptionsInSignatures Rule - [Bhanu Prakash Pamidi](https://github.com/pamidi99)
*   [#1706](https://github.com/pmd/pmd/pull/1706): \[java] Add DetachedTestCase rule - [David Burström](https://github.com/davidburstromspotify)
*   [#1709](https://github.com/pmd/pmd/pull/1709): \[java] Detect while loops with literal booleans conditions - [David Burström](https://github.com/davidburstromspotify)
*   [#1717](https://github.com/pmd/pmd/pull/1717): \[java] Fix false positive in useTryWithResources when using a custom close method with multiple arguments - [Rishabh Jain](https://github.com/jainrish)
*   [#1724](https://github.com/pmd/pmd/pull/1724): \[doc] Correct property override example - [Felix W. Dekker](https://github.com/FWDekker)
*   [#1737](https://github.com/pmd/pmd/pull/1737): \[java] fix escaping of CommentDefaultAccessModifier documentation - [itaigilo](https://github.com/itaigilo)

## 24-February-2019 - 6.12.0

The PMD team is pleased to announce PMD 6.12.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Call For Logo](#call-for-logo)
    * [CPD Suppression for Antlr-based languages](#cpd-suppression-for-antlr-based-languages)
    * [PL/SQL Grammar improvements](#pl-sql-grammar-improvements)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Call For Logo

PMD’s logo was great for a long time. But now we want to take the opportunity with the next major release to change
our logo in order to use a more "politically correct" one.

Learn more about how to participate on [github issue 1663](https://github.com/pmd/pmd/issues/1663).

#### CPD Suppression for Antlr-based languages

[ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini)
keep working on bringing full Antlr support to PMD. For this release, they have implemented
token filtering in an equivalent way as we did for JavaCC languages, adding support for CPD
suppressions through `CPD-OFF` and `CPD-ON` comments for all Antlr-based languages.

This means, you can now ignore arbitrary blocks of code on:
* Go
* Kotlin
* Swift

Simply start the suppression with any comment (single or multiline) containing `CPD-OFF`,
and resume again with a comment containing `CPD-ON`.

More information is available in [the user documentation](pmd_userdocs_cpd.html#suppression).

#### PL/SQL Grammar improvements

*   In this release, many more parser bugs in our PL/SQL support have been fixed. This adds more complete
    support for UPDATE statements and subqueries and hierarchical queries in SELECT statements.
*   Support for analytic functions such as LISTAGG has been added.
*   Conditions in WHERE clauses support now REGEX_LIKE and multiset conditions.

#### New Rules

*   The new Java rule [`UseTryWithResources`](https://pmd.github.io/pmd-6.12.0/pmd_rules_java_bestpractices.html#usetrywithresources) (`java-bestpractices`) searches
    for try-blocks, that could be changed to a try-with-resources statement. This statement ensures that
    each resource is closed at the end of the statement and is available since Java 7.

#### Modified Rules

*   The Apex rule [`MethodNamingConventions`](https://pmd.github.io/pmd-6.12.0/pmd_rules_apex_codestyle.html#methodnamingconventions) (`apex-codestyle`) has a new
    property `skipTestMethodUnderscores`, which is by default disabled. The new property allows for ignoring
    all test methods, either using the `testMethod` modifier or simply annotating them `@isTest`.

### Fixed Issues

*   all
    *   [#1462](https://github.com/pmd/pmd/issues/1462): \[core] Failed build on Windows with source zip archive
    *   [#1559](https://github.com/pmd/pmd/issues/1559): \[core] CPD: Lexical error in file (no file name provided)
    *   [#1671](https://github.com/pmd/pmd/issues/1671): \[doc] Wrong escaping in suppressing warnings for nopmd-comment
    *   [#1693](https://github.com/pmd/pmd/pull/1693): \[ui] Improved error reporting for the designer
*   java-bestpractices
    *   [#808](https://github.com/pmd/pmd/issues/808): \[java] AccessorMethodGeneration false positives with compile time constants
    *   [#1405](https://github.com/pmd/pmd/issues/1405): \[java] New Rule: UseTryWithResources - Replace close and IOUtils.closeQuietly with try-with-resources
    *   [#1555](https://github.com/pmd/pmd/issues/1555): \[java] UnusedImports false positive for method parameter type in @see Javadoc
*   java-codestyle
    *   [#1543](https://github.com/pmd/pmd/issues/1543): \[java] LinguisticNaming should ignore overriden methods
    *   [#1547](https://github.com/pmd/pmd/issues/1547): \[java] AtLeastOneConstructorRule: false-positive with lombok.AllArgsConstructor
    *   [#1624](https://github.com/pmd/pmd/issues/1624): \[java] UseDiamondOperator false positive with var initializer
*   java-design
    *   [#1641](https://github.com/pmd/pmd/issues/1641): \[java] False-positive with Lombok and inner classes
*   java-errorprone
    *   [#780](https://github.com/pmd/pmd/issues/780): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors
*   java-multithreading
    *   [#1633](https://github.com/pmd/pmd/issues/1633): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat
*   java-performance
    *   [#1632](https://github.com/pmd/pmd/issues/1632): \[java] ConsecutiveLiteralAppends false positive over catch
*   plsql
    *   [#1587](https://github.com/pmd/pmd/issues/1587): \[plsql] ParseException with EXISTS
    *   [#1589](https://github.com/pmd/pmd/issues/1589): \[plsql] ParseException with subqueries in WHERE clause
    *   [#1590](https://github.com/pmd/pmd/issues/1590): \[plsql] ParseException when using hierarchical query clause
    *   [#1656](https://github.com/pmd/pmd/issues/1656): \[plsql] ParseException with analytic functions, trim and subqueries
*   designer
    *   [#1679](https://github.com/pmd/pmd/issues/1679): \[ui] No default language version selected

### API Changes

No changes.

### External Contributions

*   [#1623](https://github.com/pmd/pmd/pull/1623): \[java] Fix lombok.AllArgsConstructor support - [Bobby Wertman](https://github.com/CasualSuperman)
*   [#1625](https://github.com/pmd/pmd/pull/1625): \[java] UnusedImports false positive for method parameter type in @see Javadoc - [Shubham](https://github.com/Shubham-2k17)
*   [#1628](https://github.com/pmd/pmd/pull/1628): \[java] LinguisticNaming should ignore overriden methods - [Shubham](https://github.com/Shubham-2k17)
*   [#1634](https://github.com/pmd/pmd/pull/1634): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors - [Shubham](https://github.com/Shubham-2k17)
*   [#1635](https://github.com/pmd/pmd/pull/1635): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat - [Shubham](https://github.com/Shubham-2k17)
*   [#1637](https://github.com/pmd/pmd/pull/1637): \[java] Compile time constants initialized by literals avoided by AccessorMethodGenerationRule - [Shubham](https://github.com/Shubham-2k17)
*   [#1640](https://github.com/pmd/pmd/pull/1640): \[java] Update instead of override classHasLombokAnnotation flag - [Phokham Nonava](https://github.com/fluxroot)
*   [#1644](https://github.com/pmd/pmd/pull/1644): \[apex] Add property to allow apex test methods to contain underscores - [Tom](https://github.com/tomdaly)
*   [#1645](https://github.com/pmd/pmd/pull/1645): \[java] ConsecutiveLiteralAppends false positive - [Shubham](https://github.com/Shubham-2k17)
*   [#1646](https://github.com/pmd/pmd/pull/1646): \[java] UseDiamondOperator doesn't work with var - [Shubham](https://github.com/Shubham-2k17)
*   [#1654](https://github.com/pmd/pmd/pull/1654): \[core] Antlr token filter - [Tomi De Lucca](https://github.com/tomidelucca)
*   [#1655](https://github.com/pmd/pmd/pull/1655): \[kotlin] Kotlin tokenizer refactor - [Lucas Soncini](https://github.com/lsoncini)
*   [#1686](https://github.com/pmd/pmd/pull/1686): \[doc] Replaced wrong escaping with ">" - [Himanshu Pandey](https://github.com/hpandeycodeit)

## 27-January-2019 - 6.11.0

The PMD team is pleased to announce PMD 6.11.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [PL/SQL Grammar improvements](#pl/sql-grammar-improvements)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated Apex Support

*   The Apex language support has been bumped to version 45 (Spring '19). All new language features are now properly
    parsed and processed.
*   Many nodes now expose more informations, such as the operator for BooleanExpressions. This makes these operators
    consumable by XPath rules, e.g. `//BooleanExpression[@Operator='&&']`.

#### PL/SQL Grammar improvements

*   In this release, many parser bugs in our PL/SQL support have been fixed. This adds e.g. support for
    table collection expressions (`SELECT * FROM TABLE(expr)`).
*   Support for parsing insert statements has been added.
*   More improvements are planned for the next release of PMD.

#### New Rules

*   The new Java rule [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter) (`java-multithreading`) detects
    unsynchronized usages of static `java.text.Format` instances. This rule is a more generic replacement of the
    rule [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) which focused just on `DateFormat`.

*   The new Java rule [`ForLoopVariableCount`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_bestpractices.html#forloopvariablecount) (`java-bestpractices`) checks for
    the number of control variables in a for-loop. Having a lot of control variables makes it harder to understand
    what the loop does. The maximum allowed number of variables is by default 1 and can be configured by a
    property.

*   The new Java rule [`AvoidReassigningLoopVariables`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables) (`java-bestpractices`) searches
    for loop variables that are reassigned. Changing the loop variables additionally to the loop itself can lead to
    hard-to-find bugs.

*   The new Java rule [`UseDiamondOperator`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_codestyle.html#usediamondoperator) (`java-codestyle`) looks for constructor
    calls with explicit type parameters. Since Java 1.7, these type parameters are not necessary anymore, as they
    can be inferred now.

#### Modified Rules

*   The Java rule [`LocalVariableCouldBeFinal`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_codestyle.html#localvariablecouldbefinal) (`java-codestyle`) has a new
    property `ignoreForEachDecl`, which is by default disabled. The new property allows for ignoring
    non-final loop variables in a for-each statement.

#### Deprecated Rules

*   The Java rule [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general
    [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter).

### Fixed Issues

*   core
    *   [#1196](https://github.com/pmd/pmd/issues/1196): \[core] CPD results not consistent between runs
    *   [#1496](https://github.com/pmd/pmd/issues/1496) \[core] Refactor metrics to be dealt with generically from pmd-core
*   apex
    *   [#1542](https://github.com/pmd/pmd/pull/1542): \[apex] Include the documentation category
    *   [#1546](https://github.com/pmd/pmd/issues/1546): \[apex] PMD parsing exception for Apex classes using 'inherited sharing' keyword
    *   [#1568](https://github.com/pmd/pmd/pull/1568): \[apex] AST node attribute @Image not usable / always null in XPath rule / Designer
*   java
    *   [#1556](https://github.com/pmd/pmd/issues/1556): \[java] Default methods should not be considered abstract
    *   [#1578](https://github.com/pmd/pmd/issues/1578): \[java] Private field is detected as public inside nested classes in interfaces
*   java-bestpractices
    *   [#658](https://github.com/pmd/pmd/issues/658): \[java] OneDeclarationPerLine: False positive for loops
    *   [#1518](https://github.com/pmd/pmd/issues/1518): \[java] New rule: AvoidReassigningLoopVariable
    *   [#1519](https://github.com/pmd/pmd/issues/1519): \[java] New rule: ForLoopVariableCount
*   java-codestyle
    *   [#1513](https://github.com/pmd/pmd/issues/1513): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop
    *   [#1517](https://github.com/pmd/pmd/issues/1517): \[java] New Rule: UseDiamondOperator
*   java-errorprone
    *   [#1035](https://github.com/pmd/pmd/issues/1035): \[java] ReturnFromFinallyBlock: False positive on lambda expression in finally block
    *   [#1549](https://github.com/pmd/pmd/issues/1549): \[java] NPE in PMD 6.8.0 InvalidSlf4jMessageFormat
*   java-multithreading
    *   [#1533](https://github.com/pmd/pmd/issues/1533): \[java] New rule: UnsynchronizedStaticFormatter
*   plsql
    *   [#1507](https://github.com/pmd/pmd/issues/1507): \[plsql] Parse Exception when using '||' operator in where clause
    *   [#1508](https://github.com/pmd/pmd/issues/1508): \[plsql] Parse Exception when using SELECT COUNT(\*)
    *   [#1509](https://github.com/pmd/pmd/issues/1509): \[plsql] Parse Exception with OUTER/INNER Joins
    *   [#1511](https://github.com/pmd/pmd/issues/1511): \[plsql] Parse Exception with IS NOT NULL
    *   [#1526](https://github.com/pmd/pmd/issues/1526): \[plsql] ParseException when using TableCollectionExpression
    *   [#1583](https://github.com/pmd/pmd/issues/1583): \[plsql] Update Set Clause should allow multiple columns
    *   [#1586](https://github.com/pmd/pmd/issues/1586): \[plsql] Parse Exception when functions are used with LIKE
    *   [#1588](https://github.com/pmd/pmd/issues/1588): \[plsql] Parse Exception with function calls in WHERE clause

### API Changes

* [`StatisticalRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#) and the related helper classes and base rule classes
are deprecated for removal in 7.0.0. This includes all of [`net.sourceforge.pmd.stat`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/stat/package-summary.html#) and [`net.sourceforge.pmd.lang.rule.stat`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/lang/rule/stat/package-summary.html#),
and also [`AbstractStatisticalJavaRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.11.0/net/sourceforge/pmd/lang/java/rule/AbstractStatisticalJavaRule.html#), [`AbstractStatisticalApexRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.11.0/net/sourceforge/pmd/lang/apex/rule/AbstractStatisticalApexRule.html#) and the like.
The methods [`Report#addMetric`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/Report.html#addMetric(net.sourceforge.pmd.stat.Metric)) and [`metricAdded`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/ThreadSafeReportListener.html#metricAdded(net.sourceforge.pmd.stat.Metric))
will also be removed.
* [`setProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/properties/PropertySource.html#setProperty(net.sourceforge.pmd.properties.MultiValuePropertyDescriptor,Object[])) is deprecated,
because [`MultiValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#) is deprecated as well

### External Contributions

*   [#1503](https://github.com/pmd/pmd/pull/1503): \[java] Fix for ReturnFromFinallyBlock false-positives - [RishabhDeep Singh](https://github.com/rishabhdeepsingh)
*   [#1514](https://github.com/pmd/pmd/pull/1514): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1516](https://github.com/pmd/pmd/pull/1516): \[java] OneDeclarationPerLine: Don't report multiple variables in a for statement. - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1520](https://github.com/pmd/pmd/pull/1520): \[java] New rule: ForLoopVariableCount: check the number of control variables in a for loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1521](https://github.com/pmd/pmd/pull/1521): \[java] Upgrade to ASM7 for JDK 11 support - [Mark Pritchard](https://github.com/markpritchard)
*   [#1530](https://github.com/pmd/pmd/pull/1530): \[java] New rule: AvoidReassigningLoopVariables - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1534](https://github.com/pmd/pmd/pull/1534): \[java] This is the change regarding the usediamondoperator #1517 - [hemanshu070](https://github.com/hemanshu070)
*   [#1545](https://github.com/pmd/pmd/pull/1545): \[doc] fixing dead links + tool to check for dead links automatically - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1551](https://github.com/pmd/pmd/pull/1551): \[java] InvalidSlf4jMessageFormatRule should not throw NPE for enums - [Robbie Martinus](https://github.com/rmartinus)
*   [#1552](https://github.com/pmd/pmd/pull/1552): \[core] Upgrading Google Gson from 2.5 to 2.8.5 - [Thunderforge](https://github.com/Thunderforge)
*   [#1553](https://github.com/pmd/pmd/pull/1553): \[core] Upgrading System Rules dependency from 1.8.0 to 1.19.0 - [Thunderforge](https://github.com/Thunderforge)
*   [#1554](https://github.com/pmd/pmd/pull/1554): \[plsql] updates should allow for multiple statements - [tashiscool](https://github.com/tashiscool)
*   [#1584](https://github.com/pmd/pmd/pull/1584): \[core] Fixes 1196: inconsistencies of clones returned by different CPD executions for the same files  - [Bruno Ferreira](https://github.com/bmbferreira)

## 09-December-2018 - 6.10.0

The PMD team is pleased to announce PMD 6.10.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Kotlin support for CPD](#kotlin-support-for-cpd)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Properties framework](#properties-framework)
        * [Changes to how you define properties](#changes-to-how-you-define-properties)
        * [Architectural simplifications](#architectural-simplifications)
        * [Changes to the PropertyDescriptor interface](#changes-to-the-propertydescriptor-interface)
    * [Deprecated APIs](#deprecated-apis)
        * [For internalization](#for-internalization)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Kotlin support for CPD

Thanks to [Maikel Steneker](https://github.com/maikelsteneker), CPD now supports [Kotlin](https://kotlinlang.org/).
This means, you can use CPD to find duplicated code in your Kotlin projects.

#### New Rules

*   The new Java rule [`UseUnderscoresInNumericLiterals`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_codestyle.html#useunderscoresinnumericliterals) (`java-codestyle`)
    verifies that numeric literals over a given length (4 chars by default, but configurable) are using
    underscores every 3 digits for readability. The rule only applies to Java 7+ codebases.

#### Modified Rules

*   The Java rule [`JUnitTestsShouldIncludeAssert`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_bestpractices.html#junittestsshouldincludeassert) (`java-bestpractices`)
    now also detects [Soft Assertions](https://github.com/joel-costigliola/assertj-core).

*   The property `exceptionfile` of the rule [`AvoidDuplicateLiterals`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_errorprone.html#avoidduplicateliterals) (`java-errorprone`)
    has been deprecated and will be removed with 7.0.0. Please use `exceptionList` instead.

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1328](https://github.com/pmd/pmd/issues/1328): \[ci] Building docs for release fails
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
    *   [#1468](https://github.com/pmd/pmd/issues/1468): \[doc] Missing escaping leads to XSS
    *   [#1471](https://github.com/pmd/pmd/issues/1471): \[core] XMLRenderer: ProcessingErrors from exceptions without a message missing
    *   [#1477](https://github.com/pmd/pmd/issues/1477): \[core] Analysis cache fails with wildcard classpath entries
*   java
    *   [#1460](https://github.com/pmd/pmd/issues/1460): \[java] Intermittent PMD failure : PMD processing errors while no violations reported
*   java-bestpractices
    *   [#647](https://github.com/pmd/pmd/issues/647): \[java] JUnitTestsShouldIncludeAssertRule should support `this.exception` as well as just `exception`
    *   [#1435](https://github.com/pmd/pmd/issues/1435): \[java] JUnitTestsShouldIncludeAssert: Support AssertJ soft assertions
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis
    *   [#1440](https://github.com/pmd/pmd/issues/1440): \[java] CommentDefaultAccessModifierRule shows incorrect message
*   java-design
    *   [#1151](https://github.com/pmd/pmd/issues/1151): \[java] ImmutableField false positive with multiple constructors
    *   [#1483](https://github.com/pmd/pmd/issues/1483): \[java] Cyclo metric should count conditions of for statements correctly
*   java-errorprone
    *   [#1512](https://github.com/pmd/pmd/issues/1512): \[java] InvalidSlf4jMessageFormatRule causes NPE in lambda and static blocks
*   plsql
    *   [#1454](https://github.com/pmd/pmd/issues/1454): \[plsql] ParseException for IF/CASE statement with >=, <=, !=


### API Changes

#### Properties framework





The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

##### Changes to how you define properties


* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
construction through builders. The builder hierarchy, currently found in the package [`net.sourceforge.pmd.properties.builders`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/package-summary.html#),
is being replaced by the simpler [`PropertyBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyBuilder.html#). Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like [`IntegerProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerProperty.html#) and [`StringMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringMultiProperty.html#) will gradually
all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the [`PropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#)
interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class [`PropertyFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#) will become
from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
by a corresponding method on `PropertyFactory`:
  * [`IntegerProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerProperty.html#) is replaced by [`PropertyFactory#intProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#intProperty(java.lang.String))
    * [`IntegerMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerMultiProperty.html#) is replaced by [`PropertyFactory#intListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#intListProperty(java.lang.String))

  * [`FloatProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FloatProperty.html#) and [`DoubleProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/DoubleProperty.html#) are both replaced by [`PropertyFactory#doubleProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleProperty(java.lang.String)).
    Having a separate property for floats wasn't that useful.
    * Similarly, [`FloatMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FloatMultiProperty.html#) and [`DoubleMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/DoubleMultiProperty.html#) are replaced by [`PropertyFactory#doubleListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleListProperty(java.lang.String)).

  * [`StringProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringProperty.html#) is replaced by [`PropertyFactory#stringProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringProperty(java.lang.String))
    * [`StringMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringMultiProperty.html#) is replaced by [`PropertyFactory#stringListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringListProperty(java.lang.String))

  * [`RegexProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/RegexProperty.html#) is replaced by [`PropertyFactory#regexProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#regexProperty(java.lang.String))

  * [`EnumeratedProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#) is replaced by [`PropertyFactory#enumProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.util.Map))
    * [`EnumeratedProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#) is replaced by [`PropertyFactory#enumListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.util.Map))

  * [`BooleanProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/BooleanProperty.html#) is replaced by [`PropertyFactory#booleanProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#booleanProperty(java.lang.String))
    * Its multi-valued counterpart, [`BooleanMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/BooleanMultiProperty.html#), is not replaced, because it doesn't have a use case.

  * [`CharacterProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/CharacterProperty.html#) is replaced by [`PropertyFactory#charProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#charProperty(java.lang.String))
    * [`CharacterMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/CharacterMultiProperty.html#) is replaced by [`PropertyFactory#charListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#charListProperty(java.lang.String))

  * [`LongProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/LongProperty.html#) is replaced by [`PropertyFactory#longIntProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntProperty(java.lang.String))
    * [`LongMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/LongMultiProperty.html#) is replaced by [`PropertyFactory#longIntListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntListProperty(java.lang.String))

  * [`MethodProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/MethodProperty.html#), [`FileProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FileProperty.html#), [`TypeProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/TypeProperty.html#) and their multi-valued counterparts
    are discontinued for lack of a use-case, and have no planned replacement in 7.0.0 for now.
    <!-- TODO complete that as we proceed. -->


Here's an example:
```java
// Before 7.0.0, these are equivalent:
IntegerProperty myProperty = new IntegerProperty("score", "Top score value", 1, 100, 40, 3.0f);
IntegerProperty myProperty = IntegerProperty.named("score").desc("Top score value").range(1, 100).defaultValue(40).uiOrder(3.0f);

// They both map to the following in 7.0.0
PropertyDescriptor<Integer> myProperty = PropertyFactory.intProperty("score").desc("Top score value").require(inRange(1, 100)).defaultValue(40);
```

You're highly encouraged to migrate to using this new API as soon as possible, to ease your migration to 7.0.0.



##### Architectural simplifications

* [`EnumeratedPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedPropertyDescriptor.html#), [`NumericPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/NumericPropertyDescriptor.html#), [`PackagedPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PackagedPropertyDescriptor.html#),
and the related builders (in [`net.sourceforge.pmd.properties.builders`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/package-summary.html#)) will be removed.
These specialized interfaces allowed additional constraints to be enforced on the
value of a property, but made the property class hierarchy very large and impractical
to maintain. Their functionality will be mapped uniformly to [`PropertyConstraint`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/constraints/PropertyConstraint.html#)s,
which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
related methods [`PropertyTypeId#isPropertyNumeric`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyNumeric()) and
[`PropertyTypeId#isPropertyPackaged`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyPackaged()) are also deprecated.

* [`MultiValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#) and [`SingleValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/SingleValuePropertyDescriptor.html#)
are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
between single- and multi-valued properties. The method [`PropertyDescriptor#isMultiValue`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()) will be removed
accordingly.

##### Changes to the PropertyDescriptor interface

* [`preferredRowCount`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#preferredRowCount()) is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods [`uiOrder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#uiOrder()) and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method [`propertyErrorFor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#propertyErrorFor(net.sourceforge.pmd.Rule)) is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `[`valueFrom(String)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#valueFrom(java.lang.String)) and `String `[`asDelimitedString`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#asDelimitedString(java.lang.Object))`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* [`isMultiValue`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()) and [`type`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#type()) are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* [`errorFor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#errorFor(java.lang.Object)) is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

#### Deprecated APIs








##### For internalization

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package [`net.sourceforge.pmd.lang.ast.xpath`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/ast/xpath/package-summary.html#))
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only [`Attribute`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/ast/xpath/Attribute.html#) remains public API.

*   The classes [`PropertyDescriptorField`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptorField.html#), [`PropertyDescriptorBuilderConversionWrapper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/PropertyDescriptorBuilderConversionWrapper.html#), and the methods
    [`PropertyDescriptor#attributeValuesById`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#attributeValuesById), [`PropertyDescriptor#isDefinedExternally`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isDefinedExternally()) and [`PropertyTypeId#getFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#getFactory()).
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class [`ValueParserConstants`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/ValueParserConstants.html#) and the interface [`ValueParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/ValueParser.html#).

*   All classes from [`net.sourceforge.pmd.lang.java.metrics.impl.visitors`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/metrics/impl/visitors/package-summary.html#) are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    [`JavaParserVisitorAdapter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorAdapter.html#) should be directly subclassed.

*   [`LanguageVersionHandler#getDataFlowHandler()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowHandler()), [`LanguageVersionHandler#getDFAGraphRule()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDFAGraphRule())

*   [`VisitorStarter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/VisitorStarter.html#)

##### For removal

*   All classes from [`net.sourceforge.pmd.properties.modules`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/modules/package-summary.html#) will be removed.

*   The interface [`Dimensionable`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/Dimensionable.html#) has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from [`ASTLocalVariableDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTLocalVariableDeclaration.html#) and [`ASTFieldDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#) have
    also been deprecated:

    *   [`ASTFieldDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#) won't be a [`TypeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#) come 7.0.0, so
        [`getType`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getType()) and
        [`getTypeDefinition`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getTypeDefinition()) are deprecated.

    *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`[`ASTVariableDeclaratorId`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#)`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

    *   In [`net.sourceforge.pmd.lang.java.ast`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#): [`JavaParserDecoratedVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserDecoratedVisitor.html#), [`JavaParserControllessVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitor.html#),
        [`JavaParserControllessVisitorAdapter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitorAdapter.html#), and [`JavaParserVisitorDecorator`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorDecorator.html#) are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

    *   [`CppHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppHandler.html#)
    *   [`CppLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppLanguageModule.html#)
    *   [`CppParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppParser.html#)
    *   [`CsLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cs/6.10.0/net/sourceforge/pmd/lang/cs/CsLanguageModule.html#)
    *   [`FortranLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-fortran/6.10.0/net/sourceforge/pmd/lang/fortran/FortranLanguageModule.html#)
    *   [`GroovyLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-groovy/6.10.0/net/sourceforge/pmd/lang/groovy/GroovyLanguageModule.html#)
    *   [`MatlabHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabHandler.html#)
    *   [`MatlabLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabLanguageModule.html#)
    *   [`MatlabParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabParser.html#)
    *   [`ObjectiveCHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCHandler.html#)
    *   [`ObjectiveCLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCLanguageModule.html#)
    *   [`ObjectiveCParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCParser.html#)
    *   [`PhpLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-php/6.10.0/net/sourceforge/pmd/lang/php/PhpLanguageModule.html#)
    *   [`PythonHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonHandler.html#)
    *   [`PythonLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonLanguageModule.html#)
    *   [`PythonParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonParser.html#)
    *   [`RubyLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-ruby/6.10.0/net/sourceforge/pmd/lang/ruby/RubyLanguageModule.html#)
    *   [`ScalaLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-scala/6.10.0/net/sourceforge/pmd/lang/scala/ScalaLanguageModule.html#)
    *   [`SwiftLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-swift/6.10.0/net/sourceforge/pmd/lang/swift/SwiftLanguageModule.html#)


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
removal:
  * In [`Rule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#): [`isDfa()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isDfa()), [`isTypeResolution()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isTypeResolution()), [`isMultifile()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isMultifile()) and their
    respective setters.
  * In [`RuleSet`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#): [`usesDFA(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesDFA(net.sourceforge.pmd.lang.Language)), [`usesTypeResolution(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)), [`usesMultifile(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesMultifile(net.sourceforge.pmd.lang.Language))
  * In [`RuleSets`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#): [`usesDFA(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesDFA(net.sourceforge.pmd.lang.Language)), [`usesTypeResolution(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)), [`usesMultifile(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesMultifile(net.sourceforge.pmd.lang.Language))
  * In [`LanguageVersionHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#): [`getDataFlowFacade()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowFacade()), [`getSymbolFacade()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade()), [`getSymbolFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade(java.lang.ClassLoader)),
    [`getTypeResolutionFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getTypeResolutionFacade(java.lang.ClassLoader)), [`getQualifiedNameResolutionFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getQualifiedNameResolutionFacade(java.lang.ClassLoader))

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)
*   [#1434](https://github.com/pmd/pmd/pull/1434): \[java] JUnitTestsShouldIncludeAssert: Recognize AssertJ soft assertions as valid assert statements - [Loïc Ledoyen](https://github.com/ledoyen)
*   [#1439](https://github.com/pmd/pmd/pull/1439): \[java] Avoid FileInputStream and FileOutputStream - [reudismam](https://github.com/reudismam)
*   [#1441](https://github.com/pmd/pmd/pull/1441): \[kotlin] [cpd] Added CPD support for Kotlin - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1447](https://github.com/pmd/pmd/pull/1447): \[fortran] Use diamond operator in impl - [reudismam](https://github.com/reudismam)
*   [#1453](https://github.com/pmd/pmd/pull/1453): \[java] Adding the fix for #1440. Showing correct message for CommentDefaultAccessmodifier. - [Rohit Kumar](https://github.com/stationeros)
*   [#1457](https://github.com/pmd/pmd/pull/1457): \[java] Adding test for Issue #647 - [orimarko](https://github.com/orimarko)
*   [#1464](https://github.com/pmd/pmd/pull/1464): \[doc] Fix XSS on documentation web page - [Maxime Robert](https://github.com/marob)
*   [#1469](https://github.com/pmd/pmd/pull/1469): \[core] Configurable max loops in DAAPathFinder - [Alberto Fernández](https://github.com/albfernandez)
*   [#1494](https://github.com/pmd/pmd/pull/1494): \[java] 1151: Rephrase ImmutableField documentation in design.xml - [Robbie Martinus](https://github.com/rmartinus)
*   [#1504](https://github.com/pmd/pmd/pull/1504): \[java] NPE in InvalidSlf4jMessageFormatRule if a logger call with a variable as parameter is not inside a method or constructor - [kris-scheibe](https://github.com/kris-scheibe)

## 28-October-2018 - 6.9.0

The PMD team is pleased to announce PMD 6.9.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Improved Golang CPD Support](#improved-golang-cpd-support)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Improved Golang CPD Support

Thanks to the work of [ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini),
Golang is now backed by a proper Antlr Grammar. This means CPD is now better at detecting duplicates,
as comments are recognized as such and ignored.

#### New Rules

*   The new PLSQL rule [`CodeFormat`](https://pmd.github.io/pmd-6.9.0/pmd_rules_plsql_codestyle.html#codeformat) (`plsql-codestyle`) verifies that
    PLSQL code is properly formatted. It checks e.g. for correct indentation in select statements and verifies
    that each parameter is defined on a separate line.

### Fixed Issues

*   all
    *   [#649](https://github.com/pmd/pmd/issues/649): \[core] Exclude specific files from command line
    *   [#1272](https://github.com/pmd/pmd/issues/1272): \[core] Could not find or load main class when using symlinked run.sh
    *   [#1377](https://github.com/pmd/pmd/issues/1377): \[core] LanguageRegistry uses default class loader when invoking ServiceLocator 
    *   [#1394](https://github.com/pmd/pmd/issues/1394): \[doc] How to configure "-cache <path>"
    *   [#1412](https://github.com/pmd/pmd/issues/1412): \[doc] Broken link to adding new cpd language documentation
*   apex
    *   [#1396](https://github.com/pmd/pmd/issues/1396): \[apex] ClassCastException caused by Javadoc
*   java
    *   [#1330](https://github.com/pmd/pmd/issues/1330): \[java] PMD crashes with java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/xml/ws/Service
*   java-bestpractices
    *   [#1202](https://github.com/pmd/pmd/issues/1202): \[java] GuardLogStatement: "There is log block not surrounded by if" doesn't sound right
    *   [#1209](https://github.com/pmd/pmd/issues/1209): \[java] UnusedImports false positive for static import with package-private method usage
    *   [#1343](https://github.com/pmd/pmd/issues/1343): \[java] Update CommentDefaultAccessModifierRule to extend AbstractIgnoredAnnotationRule
    *   [#1365](https://github.com/pmd/pmd/issues/1365): \[java] JUnitTestsShouldIncludeAssert false positive
    *   [#1404](https://github.com/pmd/pmd/issues/1404): \[java] UnusedImports false positive with static ondemand import with method call
*   java-codestyle
    *   [#1199](https://github.com/pmd/pmd/issues/1199): \[java] UnnecessaryFullyQualifiedName doesn't flag same package FQCNs
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public-\>static
*   java-design
    *   [#1369](https://github.com/pmd/pmd/issues/1369): \[java] Processing error (ClassCastException) if a TYPE\_USE annotation is used on a base class in the "extends" clause
*   jsp
    *   [#1402](https://github.com/pmd/pmd/issues/1402): \[jsp] JspTokenManager has a problem about jsp scriptlet
*   documentation
    *   [#1349](https://github.com/pmd/pmd/pull/1349): \[doc] Provide some explanation for WHY duplicate code is bad, like mutations

### API Changes

*   PMD has a new CLI option `-ignorelist`. With that, you can provide a file containing a comma-delimit list of files,
    that should be excluded during analysis. The ignorelist is applied after the files have been selected
    via `-dir` or `-filelist`, which means, if the file is in both lists, then it will be ignored.
    Note: there is no corresponding option for the Ant task, since the feature is already available via
    Ant's FileSet include/exclude filters.

### External Contributions

*   [#1338](https://github.com/pmd/pmd/pull/1338): \[core] \[cpd] Generalize ANTLR tokens preparing support for ANTLR token filter - [Matías Fraga](https://github.com/matifraga) and [Tomi De Lucca](https://github.com/tomidelucca)
*   [#1361](https://github.com/pmd/pmd/pull/1361): \[doc] Update cpd.md with information about risks - [David M. Karr](https://github.com/davidmichaelkarr)
*   [#1366](https://github.com/pmd/pmd/pull/1366): \[java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1374](https://github.com/pmd/pmd/pull/1374): \[java] Simplify check for 'Test' annotation in JUnitTestsShouldIncludeAssertRule. - [Will Winder](https://github.com/winder)
*   [#1375](https://github.com/pmd/pmd/pull/1375): \[java] Add missing null check AbstractJavaAnnotatableNode - [Will Winder](https://github.com/winder)
*   [#1376](https://github.com/pmd/pmd/pull/1376): \[all] Upgrading Apache Commons IO from 2.4 to 2.6 - [Thunderforge](https://github.com/Thunderforge)
*   [#1378](https://github.com/pmd/pmd/pull/1378): \[all] Upgrading Apache Commons Lang 3 from 3.7 to 3.8.1 - [Thunderforge](https://github.com/Thunderforge)
*   [#1382](https://github.com/pmd/pmd/pull/1382): \[all] Replacing deprecated IO methods with ones that specify a charset - [Thunderforge](https://github.com/Thunderforge)
*   [#1383](https://github.com/pmd/pmd/pull/1383): \[java] Improved message for GuardLogStatement rule - [Felix Lampe](https://github.com/fblampe)
*   [#1386](https://github.com/pmd/pmd/pull/1386): \[go] \[cpd] Add CPD support for Antlr based grammar on Golang - [Matías Fraga](https://github.com/matifraga)
*   [#1398](https://github.com/pmd/pmd/pull/1398): \[all] Upgrading SLF4J from 1.7.12 to 1.7.25 - [Thunderforge](https://github.com/Thunderforge)
*   [#1400](https://github.com/pmd/pmd/pull/1400): \[java] Fix Issue 1343: Update CommentDefaultAccessModifierRule - [CrazyUnderdog](https://github.com/CrazyUnderdog)
*   [#1401](https://github.com/pmd/pmd/pull/1401): \[all] Replacing IOUtils.closeQuietly(foo) with try-with-resources statements - [Thunderforge](https://github.com/Thunderforge)
*   [#1406](https://github.com/pmd/pmd/pull/1406): \[jsp] Fix issue 1402: JspTokenManager has a problem about jsp scriptlet - [JustPRV](https://github.com/JustPRV)
*   [#1411](https://github.com/pmd/pmd/pull/1411): \[core] Add ignore file path functionality - [Jon Moroney](https://github.com/darakian)
*   [#1414](https://github.com/pmd/pmd/pull/1414): \[doc] Fix broken link. Fixes #1412 - [Johan Hammar](https://github.com/johanhammar)


## 30-September-2018 - 6.8.0

The PMD team is pleased to announce PMD 6.8.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Drawing a line between private and public API](#drawing-a-line-between-private-and-public-api)
            * [`.internal` packages and `@InternalApi` annotation](#`.internal`-packages-and-`@internalapi`-annotation)
            * [`@ReservedSubclassing`](#`@reservedsubclassing`)
            * [`@Experimental`](#`@experimental`)
            * [`@Deprecated`](#`@deprecated`)
            * [The transition](#the-transition)
    * [Quickstart Ruleset](#quickstart-ruleset)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [PLSQL](#plsql)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Drawing a line between private and public API

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

###### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

###### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

###### `@Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

###### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.

###### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.


#### Quickstart Ruleset

PMD 6.8.0 provides a first quickstart ruleset for Java, which you can use as a base ruleset to get your
custom ruleset started. You can reference it with `rulesets/java/quickstart.xml`.
You are strongly encouraged to [create your own ruleset](https://pmd.github.io/pmd-6.7.0/pmd_userdocs_making_rulesets.html)
though.

The quickstart ruleset has the intention, to be useful out-of-the-box for many projects. Therefore it
references only rules, that are most likely to apply everywhere.

Any feedback would be greatly appreciated.


#### New Rules

*   The new Apex rule [`ApexDoc`](https://pmd.github.io/pmd-6.8.0/pmd_rules_apex_documentation.html#apexdoc) (`apex-documentation`)
    enforces the inclusion of ApexDoc on classes, interfaces, properties and methods; as well as some
    sanity rules for such docs (no missing parameters, parameters' order, and return value). By default,
    method overrides and test classes are allowed to not include ApexDoc.


#### Modified Rules

*   The rule [`MissingSerialVersionUID`](https://pmd.github.io/pmd-6.8.0/pmd_rules_java_errorprone.html#missingserialversionuid) (`java-errorprone`) has been modified
    in order to recognize also missing `serialVersionUID` fields in abstract classes, if they are serializable.
    Each individual class in the inheritance chain needs an own serialVersionUID field. See also [Should an abstract class have a serialVersionUID](https://stackoverflow.com/questions/893259/should-an-abstract-class-have-a-serialversionuid).
    This change might lead to additional violations in existing code bases.


#### PLSQL

The grammar for PLSQL has been revamped in order to fully parse `SELECT INTO`, `UPDATE`, and `DELETE`
statements. Previously such statements have been simply skipped ahead, now PMD is parsing them, giving access
to the individual parts of a SELECT-statement, such as the Where-Clause. This might produce new parsing errors
where PMD previously could successfully parse PLSQL code. If this happens, please report a new [issue](https://github.com/pmd/pmd/issues/new) to get this problem fixed.


### Fixed Issues

*   apex-bestpractices
    *   [#1348](https://github.com/pmd/pmd/issues/1348): \[apex] AvoidGlobalModifierRule gives warning even when its a webservice - false positive
*   java-codestyle
    *   [#1329](https://github.com/pmd/pmd/issues/1329): \[java] FieldNamingConventions: false positive in serializable class with serialVersionUID
    *   [#1334](https://github.com/pmd/pmd/issues/1334): \[java] LinguisticNaming should support AtomicBooleans
*   java-errorprone
    *   [#1350](https://github.com/pmd/pmd/issues/1350): \[java] MissingSerialVersionUID false-positive on interfaces
    *   [#1352](https://github.com/pmd/pmd/issues/1352): \[java] MissingSerialVersionUID false-negative with abstract classes
*   java-performance
    *   [#1325](https://github.com/pmd/pmd/issues/1325): \[java] False positive in ConsecutiveLiteralAppends
*   plsql
    *   [#1279](https://github.com/pmd/pmd/pull/1279): \[plsql] Support for SELECT INTO


### API Changes

*   A couple of methods and fields in `net.sourceforge.pmd.properties.AbstractPropertySource` have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in `net.sourceforge.pmd.properties.PropertySource` have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of `net.sourceforge.pmd.lang.rule.RuleReference` has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.


### External Contributions

*   [#1309](https://github.com/pmd/pmd/pull/1309): \[core] \[CPD] Decouple Antlr Tokenizer implementation from any CPD language supported with Antlr - [Matías Fraga](https://github.com/matifraga)
*   [#1314](https://github.com/pmd/pmd/pull/1314): \[apex] Add validation of ApexDoc comments - [Jeff Hube](https://github.com/jeffhube)
*   [#1339](https://github.com/pmd/pmd/pull/1339): \[ci] Improve danger message - [BBG](https://github.com/djydewang)
*   [#1340](https://github.com/pmd/pmd/pull/1340): \[java] Derive correct classname for non-public non-classes - [kris-scheibe](https://github.com/kris-scheibe)
*   [#1357](https://github.com/pmd/pmd/pull/1357): \[doc] Improve Codacy description - [Daniel Reigada](https://github.com/DReigada)


## 02-September-2018 - 6.7.0

The PMD team is pleased to announce PMD 6.7.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Modified Rules](#modified-rules)
    *   [New Rules](#new-rules)
    *   [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule {% rule java/bestpractices/OneDeclarationPerLine %} (`java-bestpractices`) has been revamped to
    consider not only local variable declarations, but field declarations too.

#### New Rules

*   The new Java rule {% rule java/codestyle/LinguisticNaming %} (`java-codestyle`)
    detects cases, when a method name indicates it returns a boolean (such as `isSmall()`) but it doesn't.
    Besides method names, the rule also checks field and variable names. It also checks, that getters return
    something but setters won't. The rule has several properties with which it can be customized.

*   The new PL/SQL rule {% rule plsql/codestyle/ForLoopNaming %} (`plsql-codestyle`)
    enforces a naming convention for "for loops". Both "cursor for loops" and "index for loops" are covered.
    The rule can be customized via patterns. By default, short variable names are reported.

*   The new Java rule {% rule java/codestyle/FieldNamingConventions %} (`java-codestyle`)
    detects field names that don't comply to a given convention. It defaults to standard Java convention of using camelCase,
    but can be configured with ease for e.g. constants or static fields.

*   The new Apex rule {% rule apex/codestyle/OneDeclarationPerLine %} (`apex-codestyle`) enforces declaring a
    single field / variable per line; or per statement if the `strictMode` property is set.
    It's an Apex equivalent of the already existing Java rule of the same name.

#### Deprecated Rules

*   The Java rules {% rule java/codestyle/VariableNamingConventions %}, {% rule java/codestyle/MIsLeadingVariableName %},
    {% rule java/codestyle/SuspiciousConstantFieldName %}, and {% rule java/codestyle/AvoidPrefixingMethodParameters %} are 
    now deprecated, and will be removed with version 7.0.0. They are replaced by the more general
    {% rule java/codestyle/FieldNamingConventions %}, {% rule java/codestyle/FormalParameterNamingConventions %}, and
    {% rule java/codestyle/LocalVariableNamingConventions %}.

### Fixed Issues

*   core
    *   [#1191](https://github.com/pmd/pmd/issues/1191): \[core] Test Framework: Sort violations by line/column
    *   [#1283](https://github.com/pmd/pmd/issues/1283): \[core] Deprecate ReportTree
    *   [#1288](https://github.com/pmd/pmd/issues/1288): \[core] No supported build listeners found with Gradle
    *   [#1300](https://github.com/pmd/pmd/issues/1300): \[core] PMD stops processing file completely, if one rule in a rule chain fails
    *   [#1317](https://github.com/pmd/pmd/issues/1317): \[ci] Coveralls hasn't built the project since June 25th
*   java-bestpractices
    *   [#940](https://github.com/pmd/pmd/issues/940): \[java] JUnit 4 false positives for JUnit 5 tests
    *   [#1267](https://github.com/pmd/pmd/pull/1267): \[java] MissingOverrideRule: Avoid NoClassDefFoundError with incomplete classpath
    *   [#1323](https://github.com/pmd/pmd/issues/1323): \[java] AvoidUsingHardCodedIP ignores match pattern
    *   [#1327](https://github.com/pmd/pmd/pull/1327): \[java] AvoidUsingHardCodedIP false positive for ":bee"
*   java-codestyle
    *   [#1255](https://github.com/pmd/pmd/issues/1255): \[java] UnnecessaryFullyQualifiedName false positive: static method on shadowed implicitly imported class
    *   [#1258](https://github.com/pmd/pmd/issues/1285): \[java] False positive "UselessParentheses" for parentheses that contain assignment
*   java-errorprone
    *   [#1078](https://github.com/pmd/pmd/issues/1078): \[java] MissingSerialVersionUID rule does not seem to catch inherited classes
*   java-performance
    *   [#1291](https://github.com/pmd/pmd/issues/1291): \[java] InvalidSlf4jMessageFormat false positive: too many arguments with string concatenation operator
    *   [#1298](https://github.com/pmd/pmd/issues/1298): \[java] RedundantFieldInitializer - NumberFormatException with Long
*   jsp
    *   [#1274](https://github.com/pmd/pmd/issues/1274): \[jsp] Support EL in tag attributes
    *   [#1276](https://github.com/pmd/pmd/issues/1276): \[jsp] add support for jspf and tag extensions
*   plsql
    *   [#681](https://github.com/pmd/pmd/issues/681): \[plsql] Parse error with Cursor For Loop

### API Changes

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.
    
*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

### External Contributions

*   [#109](https://github.com/pmd/pmd/pull/109): \[java] Add two linguistics rules under naming - [Arda Aslan](https://github.com/ardaasln)
*   [#1254](https://github.com/pmd/pmd/pull/1254): \[ci] \[GSoC] Integrating the danger and pmdtester to travis CI - [BBG](https://github.com/djydewang)
*   [#1258](https://github.com/pmd/pmd/pull/1258): \[java] Use typeof in MissingSerialVersionUID - [krichter722](https://github.com/krichter722)
*   [#1264](https://github.com/pmd/pmd/pull/1264): \[cpp] Fix NullPointerException in CPPTokenizer:99 - [Rafael Cortês](https://github.com/mrfyda)
*   [#1277](https://github.com/pmd/pmd/pull/1277): \[jsp] #1276 add support for jspf and tag extensions - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1275](https://github.com/pmd/pmd/pull/1275): \[jsp] Issue #1274 - Support EL in tag attributes - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1278](https://github.com/pmd/pmd/pull/1278): \[ci] \[GSoC] Use pmdtester 1.0.0.pre.beta3 - [BBG](https://github.com/djydewang)
*   [#1289](https://github.com/pmd/pmd/pull/1289): \[java] UselessParentheses: Fix false positive with assignments - [cobratbq](https://github.com/cobratbq)
*   [#1290](https://github.com/pmd/pmd/pull/1290): \[docs] \[GSoC] Create the documentation about pmdtester - [BBG](https://github.com/djydewang)
*   [#1256](https://github.com/pmd/pmd/pull/1256): \[java] #940 Avoid JUnit 4 false positives for JUnit 5 tests - [Alex Shesterov](https://github.com/vovkss)
*   [#1315](https://github.com/pmd/pmd/pull/1315): \[apex] Add OneDeclarationPerStatement rule - [Jeff Hube](https://github.com/jeffhube) 


## 29-July-2018 - 6.6.0

The PMD team is pleased to announce PMD 6.6.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Java 11 Support](#java-11-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Java 11 Support

PMD is now able to parse the local-variable declaration syntax `var xxx`, that has been
extended for lambda parameters with Java 11 via
[JEP 323: Local-Variable Syntax for Lambda Parameters](http://openjdk.java.net/jeps/323).

#### New Rules

*   The new Java rule [`LocalVariableNamingConventions`](pmd_rules_java_codestyle.html#localvariablenamingconventions)
    (`java-codestyle`) detects local variable names that don't comply to a given convention. It defaults to standard
    Java convention of using camelCase, but can be configured. Special cases can be configured for final variables
    and caught exceptions' names.

*   The new Java rule [`FormalParameterNamingConventions`](pmd_rules_java_codestyle.html#formalparameternamingconventions)
    (`java-codestyle`) detects formal parameter names that don't comply to a given convention. It defaults to
    standard Java convention of using camelCase, but can be configured. Special cases can be configured for final
    parameters and lambda parameters (considering whether they are explicitly typed or not).

#### Modified Rules

*   The Java rules [`AccessorClassGeneration`](pmd_rules_java_bestpractices.html#accessorclassgeneration) and
    [`AccessorMethodGeneration`](pmd_rules_java_bestpractices.html#accessormethodgeneration) (both in category
    `java-bestpractices`) have been modified to be only valid up until Java 10. Java 11 adds support for
    [JEP 181: Nest-Based Access Control](http://openjdk.java.net/jeps/181) which avoids the generation of
    accessor classes / methods altogether.

### Fixed Issues

*   core
    *   [#1178](https://github.com/pmd/pmd/issues/1178): \[core] "Unsupported build listener" in gradle build
    *   [#1225](https://github.com/pmd/pmd/issues/1225): \[core] Error in sed expression on line 82 of run.sh while detecting installed version of Java
*   doc
    *   [#1215](https://github.com/pmd/pmd/issues/1215): \[doc] TOC links don't work?
*   java-codestyle
    *   [#1211](https://github.com/pmd/pmd/issues/1211): \[java] CommentDefaultAccessModifier false positive with nested interfaces (regression from 6.4.0)
    *   [#1216](https://github.com/pmd/pmd/issues/1216): \[java] UnnecessaryFullyQualifiedName false positive for the same name method
*   java-design
    *   [#1217](https://github.com/pmd/pmd/issues/1217): \[java] CyclomaticComplexityRule counts ?-operator twice
    *   [#1226](https://github.com/pmd/pmd/issues/1226): \[java] NPath complexity false negative due to overflow
*   plsql
    *   [#980](https://github.com/pmd/pmd/issues/980): \[plsql] ParseException for CREATE TABLE
    *   [#981](https://github.com/pmd/pmd/issues/981): \[plsql] ParseException when parsing VIEW
    *   [#1047](https://github.com/pmd/pmd/issues/1047): \[plsql] ParseException when parsing EXECUTE IMMEDIATE
*   ui
    *   [#1233](https://github.com/pmd/pmd/issues/1233): \[ui] XPath autocomplete arrows on first and last items

### API Changes

*   The `findDescendantsOfType` methods in `net.sourceforge.pmd.lang.ast.AbstractNode` no longer search for
    exact type matches, but will match subclasses, too. That means, it's now possible to look for abstract node
    types such as `AbstractJavaTypeNode` and not only for it's concrete subtypes.

### External Contributions

* [#1182](https://github.com/pmd/pmd/pull/1182): \[ui] XPath AutoComplete - [Akshat Bahety](https://github.com/akshatbahety)
* [#1231](https://github.com/pmd/pmd/pull/1231): \[doc] Minor typo fix in installation.md - [Ashish Rana](https://github.com/ashishrana160796)
* [#1250](https://github.com/pmd/pmd/pull/1250): \[ci] \[GSoC] Upload baseline of pmdtester automatically - [BBG](https://github.com/djydewang)


## 26-June-2018 - 6.5.0

The PMD team is pleased to announce PMD 6.5.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rules

*   The new Apex rule [`AvoidNonExistentAnnotations`](pmd_rules_apex_errorprone.html#avoidnonexistentannotations) (`apex-errorprone`)
    detects usages non-officially supported annotations. Apex supported non existent annotations for legacy reasons.
    In the future, use of such non-existent annotations could result in broken Apex code that will not compile.
    A full list of supported annotations can be found [here](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm)

#### Modified Rules

*   The Java rule [UnnecessaryModifier](pmd_rules_java_codestyle.html#unnecessarymodifier) (`java-codestyle`)
    now detects enum constrcutors with explicit `private` modifier. The rule now produces better error messages
    letting you know exactly which modifiers are redundant at each declaration.

### Fixed Issues
*   all
    *   [#1119](https://github.com/pmd/pmd/issues/1119): \[doc] Make the landing page of the documentation website more useful
    *   [#1168](https://github.com/pmd/pmd/issues/1168): \[core] xml renderer schema definitions (#538) break included xslt files
    *   [#1173](https://github.com/pmd/pmd/issues/1173): \[core] Some characters in CPD are not shown correctly.
    *   [#1193](https://github.com/pmd/pmd/issues/1193): \[core] Designer doesn't start with run.sh
*   ecmascript
    *   [#861](https://github.com/pmd/pmd/issues/861): \[ecmascript] InnaccurateNumericLiteral false positive with hex literals
*   java
    *   [#1074](https://github.com/pmd/pmd/issues/1074): \[java] MissingOverrideRule exception when analyzing PMD under Java 9
    *   [#1174](https://github.com/pmd/pmd/issues/1174): \[java] CommentUtil.multiLinesIn() could lead to StringIndexOutOfBoundsException
*   java-bestpractices
    *   [#651](https://github.com/pmd/pmd/issues/651): \[java] SwitchStmtsShouldHaveDefault should be aware of enum types
    *   [#869](https://github.com/pmd/pmd/issues/869): \[java] GuardLogStatement false positive on return statements and Math.log
*   java-codestyle
    *   [#667](https://github.com/pmd/pmd/issues/667): \[java] Make AtLeastOneConstructor Lombok-aware
    *   [#1154](https://github.com/pmd/pmd/pull/1154): \[java] CommentDefaultAccessModifierRule FP with nested enums
    *   [#1158](https://github.com/pmd/pmd/issues/1158): \[java] Fix IdenticalCatchBranches false positive
    *   [#1186](https://github.com/pmd/pmd/issues/1186): \[java] UnnecessaryFullyQualifiedName doesn't detect java.lang FQ names as violations
*   java-design
    *   [#1200](https://github.com/pmd/pmd/issues/1200): \[java] New default NcssCount method report level is drastically reduced from values of deprecated NcssMethodCount and NcssTypeCount
*   xml
    *   [#715](https://github.com/pmd/pmd/issues/715): \[xml] ProjectVersionAsDependencyVersion false positive

### API Changes

*   The utility class `net.sourceforge.pmd.lang.java.ast.CommentUtil` has been deprecated and will be removed
    with PMD 7.0.0. Its methods have been intended to parse javadoc tags. A more useful solution will be added
    around the AST node `FormalComment`, which contains as children `JavadocElement` nodes, which in
    turn provide access to the `JavadocTag`.
    
    All comment AST nodes (`FormalComment`, `MultiLineComment`, `SingleLineComment`) have a new method
    `getFilteredComment()` which provide access to the comment text without the leading `/*` markers.

*   The method `AbstractCommentRule.tagsIndicesIn()` has been deprecated and will be removed with
    PMD 7.0.0. It is not very useful, since it doesn't extract the information
    in a useful way. You would still need check, which tags have been found, and with which
    data they might be accompanied.

### External Contributions

*   [#836](https://github.com/pmd/pmd/pull/836): \[apex] Add a rule to prevent use of non-existent annotations - [anand13s](https://github.com/anand13s)
*   [#1159](https://github.com/pmd/pmd/pull/1159): \[ui] Allow to setup the auxclasspath in the designer - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1169](https://github.com/pmd/pmd/pull/1169): \[core] Update stylesheets with a default namespace - [Matthew Duggan](https://github.com/mduggan)
*   [#1183](https://github.com/pmd/pmd/pull/1183): \[java] fixed typos in rule remediation - [Jake Hemmerle](https://github.com/jakehemmerle)
*   [#1206](https://github.com/pmd/pmd/pull/1206): \[java] Recommend StringBuilder next to StringBuffer - [krichter722](https://github.com/krichter722)


## 29-May-2018 - 6.4.0

The PMD team is pleased to announce PMD 6.4.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Java 10 Support](#java-10-support)
    *   [XPath Type Resolution Functions](#xpath-type-resolution-functions)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
*   [Fixed Issues](#fixed-issues)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Java 10 Support

PMD is now able to understand local-variable type inference as introduced by Java 10.
Simple type resolution features are available, e.g. the type of the variable `s` is inferred
correctly as `String`:

    var s = "Java 10";

#### XPath Type Resolution Functions

For some time now PMD has supported Type Resolution, and exposed this functionality to XPath rules for the Java language
with the `typeof` function. This function however had a number of shortcomings:

*   It would take a first arg with the name to match if types couldn't be resolved. In all cases this was `@Image`
    but was still required.
*   It required 2 separate arguments for the Fully Qualified Class Name and the simple name of the class against
    which to test.
*   If only the Fully Qualified Class Name was provided, no simple name check was performed (not documented,
    but abused on some rules to "fix" some false positives).

In this release we are deprecating `typeof` in favor of a simpler `typeIs` function, which behaves exactly as the
old `typeof` when given all 3 arguments.

`typeIs` receives a single parameter, which is the fully qualified name of the class to test against.

So, calls such as:

```ruby
//ClassOrInterfaceType[typeof(@Image, 'junit.framework.TestCase', 'TestCase')]
```

can now we expressed much more concisely as:

```ruby
//ClassOrInterfaceType[typeIs('junit.framework.TestCase')]
```

With this change, we also allow to check against array types by just appending `[]` to the fully qualified class name.
These can be repeated for arrays of arrays (e.g. `byte[][]` or `java.lang.String[]`).

Additionally, we introduce the companion function `typeIsExactly`, that receives the same parameters as `typeIs`,
but checks for exact type matches, without considering the type hierarchy. That is, the test
`typeIsExactly('junit.framework.TestCase')` will match only if the context node is an instance of `TestCase`, but
not if it's an instance of a subclass of `TestCase`. Be aware then, that using that method with abstract types will
never match.

#### New Rules

*   The new Java rule [`HardCodedCryptoKey`](pmd_rules_java_security.html#hardcodedcryptokey) (`java-security`)
    detects hard coded keys used for encryption. It is recommended to store keys outside of the source code.

*   The new Java rule [`IdenticalCatchBranches`](pmd_rules_java_codestyle.html#identicalcatchbranches) (`java-codestyle`)
    finds catch blocks,
    that catch different exception but perform the same exception handling and thus can be collapsed into a
    multi-catch try statement.

#### Modified Rules

*   The Java rule [JUnit4TestShouldUseTestAnnotation](pmd_rules_java_bestpractices.html#junit4testshouldusetestannotation) (`java-bestpractices`)
    has a new parameter "testClassPattern". It is used to distinguish test classes from other classes and
    avoid false positives. By default, any class, that has "Test" in its name, is considered a test class.

*   The Java rule [CommentDefaultAccessModifier](pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`)
    allows now by default the comment "`/* package */` in addition to "`/* default */`. This behavior can
    still be adjusted by setting the property `regex`.

### Fixed Issues

*   all
    *   [#1018](https://github.com/pmd/pmd/issues/1018): \[java] Performance degradation of 250% between 6.1.0 and 6.2.0
    *   [#1145](https://github.com/pmd/pmd/issues/1145): \[core] JCommander's help text for option -min is wrong
*   java
    *   [#672](https://github.com/pmd/pmd/issues/672): \[java] Support exact type matches for type resolution from XPath
    *   [#743](https://github.com/pmd/pmd/issues/743): \[java] Prepare for Java 10
    *   [#1077](https://github.com/pmd/pmd/issues/1077): \[java] Analyzing enum with lambda passed in constructor fails with "The enclosing scope must exist."
    *   [#1115](https://github.com/pmd/pmd/issues/1115): \[java] Simplify xpath typeof syntax
    *   [#1131](https://github.com/pmd/pmd/issues/1131): \[java] java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/faces/application/FacesMessage$Severity
*   java-bestpractices
    *   [#527](https://github.com/pmd/pmd/issues/572): \[java] False Alarm of JUnit4TestShouldUseTestAnnotation on Predicates
    *   [#1063](https://github.com/pmd/pmd/issues/1063): \[java] MissingOverride is triggered in illegal places
*   java-codestyle
    *   [#720](https://github.com/pmd/pmd/issues/720): \[java] ShortVariable should whitelist lambdas
    *   [#955](https://github.com/pmd/pmd/issues/955): \[java] Detect identical catch statements
    *   [#1114](https://github.com/pmd/pmd/issues/1114): \[java] Star import overwritten by explicit import is not correctly handled
    *   [#1064](https://github.com/pmd/pmd/issues/1064): \[java] ClassNamingConventions suggests to add Util suffix for simple exception wrappers
    *   [#1065](https://github.com/pmd/pmd/issues/1065): \[java] ClassNamingConventions shouldn't prohibit numbers in class names
    *   [#1067](https://github.com/pmd/pmd/issues/1067): \[java] \[6.3.0] PrematureDeclaration false-positive
    *   [#1096](https://github.com/pmd/pmd/issues/1096): \[java] ClassNamingConventions is too ambitious on finding utility classes
*   java-design
    *   [#824](https://github.com/pmd/pmd/issues/824): \[java] UseUtilityClass false positive when extending
    *   [#1021](https://github.com/pmd/pmd/issues/1021): \[java] False positive for `DoNotExtendJavaLangError`
    *   [#1097](https://github.com/pmd/pmd/pull/1097): \[java] False negative in AvoidThrowingRawExceptionTypes
*   java-performance
    *   [#1051](https://github.com/pmd/pmd/issues/1051): \[java] ConsecutiveAppendsShouldReuse false-negative
    *   [#1098](https://github.com/pmd/pmd/pull/1098): \[java] Simplify LongInstantiation, IntegerInstantiation, ByteInstantiation, and ShortInstantiation using type resolution
    *   [#1125](https://github.com/pmd/pmd/issues/1125): \[java] Improve message of InefficientEmptyStringCheck for String.trim().isEmpty()
*   doc
    *   [#999](https://github.com/pmd/pmd/issues/999): \[doc] Add a header before the XPath expression in rules
    *   [#1082](https://github.com/pmd/pmd/issues/1082): \[doc] Multifile analysis doc is invalid
*   vf-security
    *   [#1100](https://github.com/pmd/pmd/issues/1100): \[vf] URLENCODE is ignored as valid escape method

### API Changes

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methdos in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.


### External Contributions

*   [#966](https://github.com/pmd/pmd/pull/966): \[java] Issue #955: add new rule to detect identical catch statement - [Clément Fournier](https://github.com/oowekyala) and [BBG](https://github.com/djydewang)
*   [#1046](https://github.com/pmd/pmd/pull/1046): \[java] New security rule for finding hard-coded keys used for cryptographic operations - [Sergey Gorbaty](https://github.com/sgorbaty)
*   [#1101](https://github.com/pmd/pmd/pull/1101): \[java] Fixes false positive for `DoNotExtendJavaLangError`  - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1106](https://github.com/pmd/pmd/pull/1106): \[vf] URLENCODE is ignored as valid escape method - [Robert Sösemann](https://github.com/rsoesemann)
*   [#1126](https://github.com/pmd/pmd/pull/1126): \[java] Improve implementation hint in InefficientEmptyStringCheck - [krichter722](https://github.com/krichter722)
*   [#1129](https://github.com/pmd/pmd/pull/1129): \[java] Adjust InefficientEmptyStringCheck documentation - [krichter722](https://github.com/krichter722)
*   [#1137](https://github.com/pmd/pmd/pull/1137): \[ui] Removes the need for RefreshAST - [Akshat Bahety](https://github.com/akshatbahety)



## 29-April-2018 - 6.3.0

The PMD team is pleased to announce PMD 6.3.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Tree Traversal Revision](#tree-traversal-revision)
    *   [Naming Rules Enhancements](#naming-rules-enhancements)
    *   [CPD Suppression](#cpd-suppression)
    *   [Swift 4.1 Support](#swift-41-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
    *   [Deprecated Rules](#deprecated-rules)
*   [Fixed Issues](#fixed-issues)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Tree Traversal Revision

As described in [#904](https://github.com/pmd/pmd/issues/904), when searching for child nodes of the AST methods
such as `hasDescendantOfType`, `getFirstDescendantOfType` and `findDescendantsOfType` were found to behave inconsistently,
not all of them honoring find boundaries; that is, nodes that define a self-contained entity which should be considered separately
(think of lambdas, nested classes, anonymous classes, etc.). We have modified these methods to ensure all of them honor
find boundaries.

This change implies several false positives / unexpected results
(ie: `ASTBlockStatement` falsely returning `true` to `isAllocation()`)
have been fixed; and lots of searches are now restricted to smaller search areas, which improves performance
(depending on the project, we have measured up to 10% improvements during Type Resolution, Symbol Table analysis,
and some rules' application).

#### Naming Rules Enhancements

*   [ClassNamingConventions](pmd_rules_java_codestyle.html#classnamingconventions) (`java-codestyle`)
    has been enhanced to allow granular configuration of naming
    conventions for different kinds of type declarations (eg enum or abstract
    class). Each kind of declaration can use its own naming convention
    using a regex property. See the rule's documentation for more info about
    configuration and default conventions.

*   [MethodNamingConventions](pmd_rules_java_codestyle.html#methodnamingconventions) (`java-codestyle`)
    has been enhanced in the same way.

#### CPD Suppression

Back in PMD 5.6.0 we introduced the ability to suppress CPD warnings in Java using comments, by
including `CPD-OFF` (to start ignoring code), or `CPD-ON` (to resume analysis) during CPD execution.
This has proved to be much more flexible and versatile than the old annotation-based approach,
and has since been the preferred way to suppress CPD warnings.

On this occasion, we are extending support for comment-based suppressions to many other languages:

*   C/C++
*   Ecmascript / Javascript
*   Matlab
*   Objective-C
*   PL/SQL
*   Python

So for instance, in Python we could now do:

```python
class BaseHandler(object):
    def __init__(self):
        # some unignored code

        # tell cpd to start ignoring code - CPD-OFF

        # mission critical code, manually loop unroll
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);

        # resume CPD analysis - CPD-ON

        # further code will *not* be ignored
```

Other languages are equivalent.

#### Swift 4.1 Support

Thanks to major contributions from [kenji21](https://github.com/kenji21) the Swift grammar has been updated to
support Swift 4.1. This is a major update, since the old grammar was quite dated, and we are sure all iOS
developers will enjoy it.

Unfortunately, this change is not compatible. The grammar elements that have been removed (ie: the keywords `__FILE__`,
`__LINE__`, `__COLUMN__` and `__FUNCTION__`) are no longer supported. We don't usually introduce such
drastic / breaking changes in minor releases, however, given that the whole Swift ecosystem pushes hard towards
always using the latest versions, and that Swift needs all code and libraries to be currently compiling against
the same Swift version, we felt strongly this change was both safe and necessary to be shipped as soon as possible.
We had great feedback from the community during the process but if you have a legitimate use case for older Swift
versions, please let us know [on our Issue Tracker](https://github.com/pmd/pmd/issues).

#### New Rules

*   The new Java rule [InsecureCryptoIv](pmd_rules_java_security.html#insecurecryptoiv) (`java-security`)
    detects hard coded initialization vectors used in cryptographic operations. It is recommended to use
    a randomly generated IV.

#### Modified Rules

*   The Java rule [UnnecessaryConstructor](pmd_rules_java_codestyle.html#unnecessaryconstructor) (`java-codestyle`)
    has been rewritten as a Java rule (previously it was a XPath-based rule). It supports a new property
    `ignoredAnnotations` and ignores by default empty constructors,
    that are annotated with `javax.inject.Inject`. Additionally, it detects now also unnecessary private constructors
    in enums.

*   The property `checkNativeMethods` of the Java rule [MethodNamingConventions](pmd_rules_java_codestyle.html#methodnamingconventions) (`java-codestyle`)
    is now deprecated, as it is now superseded by `nativePattern`. Support for that property will be maintained until
    7.0.0.

*   The Java rule [ControlStatementBraces](pmd_rules_java_codestyle.html#controlstatementbraces) (`java-codestyle`)
    supports a new boolean property `checkSingleIfStmt`. When unset, the rule won't report `if` statements which lack
    braces, if the statement is not part of an `if ... else if` chain. This property defaults to true.

#### Deprecated Rules

*   The Java rule [AbstractNaming](pmd_rules_java_codestyle.html#abstractnaming) (`java-codestyle`) is deprecated
    in favour of [ClassNamingConventions](pmd_rules_java_codestyle.html#classnamingconventions).
    See [Naming rules enhancements](#naming-rules-enhancements).

### Fixed Issues

*   all
    *   [#695](https://github.com/pmd/pmd/issues/695): \[core] Extend comment-based suppression to all JavaCC languages
    *   [#988](https://github.com/pmd/pmd/issues/988): \[core] FileNotFoundException for missing classes directory with analysis cache enabled
    *   [#1036](https://github.com/pmd/pmd/issues/1036): \[core] Non-XML output breaks XML-based CLI integrations
*   apex-errorprone
    *   [#776](https://github.com/pmd/pmd/issues/776): \[apex] AvoidHardcodingId false positives
*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
    *   [#1057](https://github.com/pmd/pmd/issues/1057): \[doc] Documentation of ignoredAnnotations property is misleading
*   java
    *   [#894](https://github.com/pmd/pmd/issues/894): \[java] Maven PMD plugin fails to process some files without any explanation
    *   [#899](https://github.com/pmd/pmd/issues/899): \[java] JavaTypeDefinitionSimple.toString can cause NPEs
    *   [#1020](https://github.com/pmd/pmd/issues/1020): \[java] The CyclomaticComplexity rule runs forever in 6.2.0
    *   [#1030](https://github.com/pmd/pmd/pull/1030): \[java] NoClassDefFoundError when analyzing PMD with PMD
    *   [#1061](https://github.com/pmd/pmd/issues/1061): \[java] Update ASM to handle Java 10 bytecode
*   java-bestpractices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas
    *   [#558](https://github.com/pmd/pmd/issues/558): \[java] ProperLogger Warnings for enums
    *   [#719](https://github.com/pmd/pmd/issues/719): \[java] Unused Code: Java 8 receiver parameter with an internal class
    *   [#1009](https://github.com/pmd/pmd/issues/1009): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#1003](https://github.com/pmd/pmd/issues/1003): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject)
    *   [#1023](https://github.com/pmd/pmd/issues/1023): \[java] False positive for useless parenthesis
    *   [#1004](https://github.com/pmd/pmd/issues/1004): \[java] ControlStatementBraces is missing checkIfStmt property
*   java-design
    *   [#1056](https://github.com/pmd/pmd/issues/1056): \[java] Property ignoredAnnotations does not work for SingularField and ImmutableField
*   java-errorprone
    *   [#629](https://github.com/pmd/pmd/issues/629): \[java] NullAssignment false positive
    *   [#816](https://github.com/pmd/pmd/issues/816): \[java] SingleMethodSingleton false positives with inner classes
*   java-performance
    *   [#586](https://github.com/pmd/pmd/issues/586): \[java] AvoidUsingShortType erroneously triggered on overrides of 3rd party methods
*   swift
    *   [#678](https://github.com/pmd/pmd/issues/678): \[swift]\[cpd] Exception when running for Swift 4 code (KeyPath)

### External Contributions

*   [#778](https://github.com/pmd/pmd/pull/778): \[swift] Support Swift 4 grammar - [kenji21](https://github.com/kenji21)
*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
*   [#1010](https://github.com/pmd/pmd/pull/1010): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject) - [BBG](https://github.com/djydewang)
*   [#1012](https://github.com/pmd/pmd/pull/1012): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5 - [BBG](https://github.com/djydewang)
*   [#1024](https://github.com/pmd/pmd/pull/1024): \[java] Issue 558: Properlogger for enums - [Utku Cuhadaroglu](https://github.com/utkuc)
*   [#1041](https://github.com/pmd/pmd/pull/1041): \[java] Make BasicProjectMemoizer thread safe. - [bergander](https://github.com/bergander)
*   [#1042](https://github.com/pmd/pmd/pull/1042): \[java] New security rule: report usage of hard coded IV in crypto operations - [Sergey Gorbaty](https://github.com/sgorbaty)
*   [#1044](https://github.com/pmd/pmd/pull/1044): \[java] Fix for issue #816 - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1048](https://github.com/pmd/pmd/pull/1048): \[core] Make MultiThreadProcessor more space efficient - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#1062](https://github.com/pmd/pmd/pull/1062): \[core] Update ASM to version 6.1.1 - [Austin Shalit](https://github.com/AustinShalit)


## 26-March-2018 - 6.2.0

The PMD team is pleased to announce PMD 6.2.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Ecmascript (JavaScript)](#ecmascript-javascript)
    *   [Disable Incremental Analysis](#disable-incremental-analysis)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
*   [Fixed Issues](#fixed-issues)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Ecmascript (JavaScript)

The [Rhino Library](https://github.com/mozilla/rhino) has been upgraded from version 1.7.7 to version 1.7.7.2.

Detailed changes for changed in Rhino can be found:
* [For 1.7.7.2](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1772)
* [For 1.7.7.1](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1771)

Both are bugfixing releases.

#### Disable Incremental Analysis

Some time ago, we added support for [Incremental Analysis](pmd_userdocs_incremental_analysis.html). On PMD 6.0.0, we
started to add warns when not using it, as we strongly believe it's a great improvement to our user's experience as
analysis time is greatly reduced; and in the future we plan to have it enabled by default. However, we realize some
scenarios don't benefit from it (ie: CI jobs), and having the warning logged can be noisy and cause confusion.

To this end, we have added a new flag to allow you to explicitly disable incremental analysis. On CLI, this is
the new `-no-cache` flag. On Ant, there is a `noCache` attribute for the `<pmd>` task.

On both scenarios, disabling the cache takes precedence over setting a cache location.

#### New Rules

*   The new Java rule [`MissingOverride`](pmd_rules_java_bestpractices.html#missingoverride)
    (category `bestpractices`) detects overridden and implemented methods, which are not marked with the
    `@Override` annotation. Annotating overridden methods with `@Override` ensures at compile time that
    the method really overrides one, which helps refactoring and clarifies intent.

*   The new Java rule [`UnnecessaryAnnotationValueElement`](pmd_rules_java_codestyle.html#unnecessaryannotationvalueelement)
    (category `codestyle`) detects annotations with a single element (`value`) that explicitely names it.
    That is, doing `@SuppressWarnings(value = "unchecked")` would be flagged in favor of
    `@SuppressWarnings("unchecked")`.

*   The new Java rule [`ControlStatementBraces`](pmd_rules_java_codestyle.html#controlstatementbraces)
    (category `codestyle`) enforces the presence of braces on control statements where they are optional.
    Properties allow to customize which statements are required to have braces. This rule replaces the now
    deprecated rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and
    `IfElseStmtMustUseBraces`. More than covering the use cases of those rules, this rule also supports
    `do ... while` statements and `case` labels of `switch` statements (disabled by default).

#### Modified Rules

*   The Java rule `CommentContentRule` (`java-documentation`) previously had the property `wordsAreRegex`. But this 
    property never had been implemented and is removed now.

*   The Java rule `UnusedPrivateField` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the field should be ignored. By default `@java.lang.Deprecated`
    and `@javafx.fxml.FXML` are ignored.

*   The Java rule `UnusedPrivateMethod` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default `@java.lang.Deprecated`
    is ignored.

*   The Java rule `ImmutableField` (`java-design`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default several `lombok`
    annotations are ignored

*   The Java rule `SingularField` (`java-design`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default several `lombok`
    annotations are ignored

#### Deprecated Rules

*   The Java rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and `IfElseStmtMustUseBraces`
    are deprecated. They will be replaced by the new rule `ControlStatementBraces`, in the category `codestyle`.

### Fixed Issues

*   all
    *   [#928](https://github.com/pmd/pmd/issues/928): \[core] PMD build failure on Windows
*   java-bestpracrtices
    *   [#907](https://github.com/pmd/pmd/issues/907): \[java] UnusedPrivateField false-positive with @FXML
    *   [#963](https://github.com/pmd/pmd/issues/965): \[java] ArrayIsStoredDirectly not triggered from variadic functions
*   java-codestyle
    *   [#974](https://github.com/pmd/pmd/issues/974): \[java] Merge \*StmtMustUseBraces rules
    *   [#983](https://github.com/pmd/pmd/issues/983): \[java] Detect annotations with single value element
*   java-design
    *   [#832](https://github.com/pmd/pmd/issues/832): \[java] AvoidThrowingNullPointerException documentation suggestion
    *   [#837](https://github.com/pmd/pmd/issues/837): \[java] CFGs of declared but not called lambdas are treated as parts of an enclosing method's CFG
    *   [#839](https://github.com/pmd/pmd/issues/839): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors
    *   [#968](https://github.com/pmd/pmd/issues/968): \[java] UseUtilityClassRule reports false positive with lombok NoArgsConstructor
*   documentation
    *   [#978](https://github.com/pmd/pmd/issues/978): \[core] Broken link in CONTRIBUTING.md
    *   [#992](https://github.com/pmd/pmd/issues/992): \[core] Include info about rule doc generation in "Writing Documentation" md page

### API Changes

*    A new CLI switch, `-no-cache`, disables incremental analysis and the related suggestion. This overrides the
    `-cache` option. The corresponding Ant task parameter is `noCache`.

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

### External Contributions

* [#941](https://github.com/pmd/pmd/pull/941): \[java] Use char notation to represent a character to improve performance - [reudismam](https://github.com/reudismam)
* [#943](https://github.com/pmd/pmd/pull/943): \[java] UnusedPrivateField false-positive with @FXML - [BBG](https://github.com/djydewang)
* [#951](https://github.com/pmd/pmd/pull/951): \[java] Add ignoredAnnotations property to unusedPrivateMethod rule - [BBG](https://github.com/djydewang)
* [#952](https://github.com/pmd/pmd/pull/952): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors - [BBG](https://github.com/djydewang)
* [#958](https://github.com/pmd/pmd/pull/958): \[java] Refactor how we ignore annotated elements in rules - [BBG](https://github.com/djydewang)
* [#965](https://github.com/pmd/pmd/pull/965): \[java] Make Varargs trigger ArrayIsStoredDirectly - [Stephen](https://github.com/pmd/pmd/issues/907)
* [#967](https://github.com/pmd/pmd/pull/967): \[doc] Issue 959: fixed broken link to XPath Rule Tutorial - [Andrey Mochalov](https://github.com/epidemia)
* [#969](https://github.com/pmd/pmd/pull/969): \[java] Issue 968 Add logic to handle lombok private constructors with utility classes - [Kirk Clemens](https://github.com/clem0110)
* [#970](https://github.com/pmd/pmd/pull/970): \[java] Fixed inefficient use of keySet iterator instead of entrySet iterator - [Andrey Mochalov](https://github.com/epidemia)
* [#984](https://github.com/pmd/pmd/pull/984): \[java] issue983 Add new UnnecessaryAnnotationValueElement rule - [Kirk Clemens](https://github.com/clem0110)
* [#989](https://github.com/pmd/pmd/pull/989): \[core] Update Contribute.md to close Issue #978 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#990](https://github.com/pmd/pmd/pull/990): \[java] Updated Doc on AvoidThrowingNullPointerException to close Issue #832 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#993](https://github.com/pmd/pmd/pull/993): \[core] Update writing_documentation.md to fix Issue #992 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)


## 25-February-2018 - 6.1.0

The PMD team is pleased to announce PMD 6.1.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Designer UI](#designer-ui)
*    [Fixed Issues](#fixed-issues)
*    [API Changes](#api-changes)
    *   [Changes to the Node interface](#changes-to-the-node-interface)
    *   [Changes to CPD renderers](#changes-to-cpd-renderers)
*    [External Contributions](#external-contributions)

### New and noteworthy

#### Designer UI

The Designer now supports configuring properties for XPath based rule development.
The Designer is still under development and any feedback is welcome.

You can start the designer via `run.sh designer` or `designer.bat`.

### Fixed Issues

*   all
    *   [#569](https://github.com/pmd/pmd/issues/569): \[core] XPath support requires specific toString implementations
    *   [#795](https://github.com/pmd/pmd/issues/795): \[cpd] java.lang.OutOfMemoryError
    *   [#848](https://github.com/pmd/pmd/issues/848): \[doc] Test failures when building pmd-doc under Windows
    *   [#872](https://github.com/pmd/pmd/issues/872): \[core] NullPointerException at FileDataSource.glomName()
    *   [#854](https://github.com/pmd/pmd/issues/854): \[ci] Use Java9 for building PMD
*   doc
    *   [#791](https://github.com/pmd/pmd/issues/791): \[doc] Documentation site reorganisation
    *   [#891](https://github.com/pmd/pmd/issues/891): \[doc] Apex @SuppressWarnings should use single quotes instead of double quotes
    *   [#909](https://github.com/pmd/pmd/issues/909): \[doc] Please add new PMD Eclipse Plugin to tool integration section
*   java
    *   [#825](https://github.com/pmd/pmd/issues/825): \[java] Excessive\*Length ignores too much
    *   [#888](https://github.com/pmd/pmd/issues/888): \[java] ParseException occurs with valid '<>' in Java 1.8 mode
    *   [#920](https://github.com/pmd/pmd/pull/920): \[java] Update valid identifiers in grammar
*   java-bestpractices
    *   [#784](https://github.com/pmd/pmd/issues/784): \[java] ForLoopCanBeForeach false-positive
    *   [#925](https://github.com/pmd/pmd/issues/925): \[java] UnusedImports false positive for static import
*   java-design
    *   [#855](https://github.com/pmd/pmd/issues/855): \[java] ImmutableField false-positive with lambdas
*   java-documentation
    *   [#877](https://github.com/pmd/pmd/issues/877): \[java] CommentRequired valid rule configuration causes PMD error
*   java-errorprone
    *   [#885](https://github.com/pmd/pmd/issues/885): \[java] CompareObjectsWithEqualsRule trigger by enum1 != enum2
*   java-performance
    *   [#541](https://github.com/pmd/pmd/issues/541): \[java] ConsecutiveLiteralAppends with types other than string
*   scala
    *   [#853](https://github.com/pmd/pmd/issues/853): \[scala] Upgrade scala version to support Java 9
*   xml
    *   [#739](https://github.com/pmd/pmd/issues/739): \[xml] IllegalAccessException when accessing attribute using Saxon on JRE 9


### API Changes

#### Changes to the Node interface

The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
A default implementation is provided in `AbstractNode`, to stay compatible
with existing implementors.

The `toString` method of a Node is not changed for the time being, and still produces
the name of the XPath node. That behaviour may however change in future major releases,
e.g. to produce a more useful message for debugging.

#### Changes to CPD renderers

The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface `net.sourceforge.pmd.cpd.renderer.CPDRenderer`
has been introduced to replace it. The main difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on large projects, with many duplications,
it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

`net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

### External Contributions

*   [#790](https://github.com/pmd/pmd/pull/790): \[java] Added some comments for JDK 9 - [Tobias Weimer](https://github.com/tweimer)
*   [#803](https://github.com/pmd/pmd/pull/803): \[doc] Added SpotBugs as successor of FindBugs - [Tobias Weimer](https://github.com/tweimer)
*   [#828](https://github.com/pmd/pmd/pull/828): \[core] Add operations to manipulate a document - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#830](https://github.com/pmd/pmd/pull/830): \[java] UseArraysAsList: Description added - [Tobias Weimer](https://github.com/tweimer)
*   [#845](https://github.com/pmd/pmd/pull/845): \[java] Fix false negative PreserveStackTrace on string concatenation - [Alberto Fernández](https://github.com/albfernandez)
*   [#868](https://github.com/pmd/pmd/pull/868): \[core] Improve XPath documentation && make small refactors - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#875](https://github.com/pmd/pmd/pull/875): \[core] Support shortnames when using filelist - [John Zhang](https://github.com/johnjiabinzhang)
*   [#886](https://github.com/pmd/pmd/pull/886): \[java] Fix #885 - [Matias Comercio](https://github.com/MatiasComercio)
*   [#900](https://github.com/pmd/pmd/pull/900): \[core] Use the isEmpty method instead of comparing the value of size() to 0 - [reudismam](https://github.com/reudismam)
*   [#914](https://github.com/pmd/pmd/pull/914): \[doc] Apex @SuppressWarnings documentation updated - [Akshat Bahety](https://github.com/akshatbahety)
*   [#918](https://github.com/pmd/pmd/pull/918): \[doc] Add qa-eclipse as new tool - [Akshat Bahety](https://github.com/akshatbahety)
*   [#927](https://github.com/pmd/pmd/pull/927): \[java]\[doc] Fix example of AbstractClassWithoutAnyMethod - [Kazuma Watanabe](https://github.com/wata727)


## 21-January-2018 - 6.0.1

The PMD team is pleased to announce PMD 6.0.1.

This is a bug fixing release.

### Table Of Contents

* [Additional information about the new introduced rule categories](#additional-information-about-the-new-introduced-rule-categories)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### Additional information about the new introduced rule categories

With the release of PMD 6.0.0, all rules have been sorted into one of the following eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

Please note, that not every category in every language may have a rule. There might be categories with no
rules at all, such as `category/java/security.xml`, which has currently no rules.
There are even languages, which only have rules of one category (e.g. `category/xml/errorprone.xml`).

You can find the information about available rules in the generated rule documentation, available
at <https://pmd.github.io/6.0.1/>.

In order to help migrate to the new category scheme, the new name for the old, deprecated rule names will
be logged as a warning. See [PR #865](https://github.com/pmd/pmd/pull/865). Please note, that the deprecated
rule names will keep working throughout PMD 6. You can upgrade to PMD 6 without the immediate need
to migrate your current ruleset. That backwards compatibility will be maintained until PMD 7.0.0 is released.

### Fixed Issues

*   all
    *   [#842](https://github.com/pmd/pmd/issues/842): \[core] Use correct java bootclasspath for compiling
*   apex-errorprone
    *   [#792](https://github.com/pmd/pmd/issues/792): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes
*   apex-security
    *   [#788](https://github.com/pmd/pmd/issues/788): \[apex] Method chaining breaks ApexCRUDViolation
*   doc
    *   [#782](https://github.com/pmd/pmd/issues/782): \[doc] Wrong information in the Release Notes about the Security ruleset
    *   [#794](https://github.com/pmd/pmd/issues/794): \[doc] Broken documentation links for 6.0.0
*   java
    *   [#793](https://github.com/pmd/pmd/issues/793): \[java] Parser error with private method in nested classes in interfaces
    *   [#814](https://github.com/pmd/pmd/issues/814): \[java] UnsupportedClassVersionError is failure instead of a warning
    *   [#831](https://github.com/pmd/pmd/issues/831): \[java] StackOverflow in JavaTypeDefinitionSimple.toString
*   java-bestpractices
    *   [#783](https://github.com/pmd/pmd/issues/783): \[java] GuardLogStatement regression
    *   [#800](https://github.com/pmd/pmd/issues/800): \[java] ForLoopCanBeForeach NPE when looping on `this` object
*   java-codestyle
    *   [#817](https://github.com/pmd/pmd/issues/817): \[java] UnnecessaryModifierRule crashes on valid code
*   java-design
    *   [#785](https://github.com/pmd/pmd/issues/785): \[java] NPE in DataClass rule
    *   [#812](https://github.com/pmd/pmd/issues/812): \[java] Exception applying rule DataClass
    *   [#827](https://github.com/pmd/pmd/issues/827): \[java] GodClass crashes with java.lang.NullPointerException
*   java-performance
    *   [#841](https://github.com/pmd/pmd/issues/841): \[java] InsufficientStringBufferDeclaration NumberFormatException
*   java-typeresolution
    *   [#866](https://github.com/pmd/pmd/issues/866): \[java] rulesets/java/typeresolution.xml lists non-existent rules

### API Changes

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.

### External Contributions

*   [#796](https://github.com/pmd/pmd/pull/796): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#799](https://github.com/pmd/pmd/pull/799): \[apex] Method chaining breaks ApexCRUDViolation - [Robert Sösemann](https://github.com/up2go-rsoesemann)


## 15-December-2017 - 6.0.0

The PMD team is pleased to announce PMD 6.0.0.

This is a major release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [New Rule Designer](#new-rule-designer)
    *   [Java 9 support](#java-9-support)
    *   [Revamped Apex CPD](#revamped-apex-cpd)
    *   [Java Type Resolution](#java-type-resolution)
    *   [Metrics Framework](#metrics-framework)
    *   [Error Reporting](#error-reporting)
    *   [Apex Rule Suppression](#apex-rule-suppression)
    *   [Rule Categories](#rule-categories)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
    *   [Deprecated Rules](#deprecated-rules)
    *   [Removed Rules](#removed-rules)
    *   [Java Symbol Table](#java-symbol-table)
    *   [Apex Parser Update](#apex-parser-update)
    *   [Incremental Analysis](#incremental-analysis)
    *   [Rule and Report Properties](#rule-and-report-properties)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rule Designer

Thanks to [Clément Fournier](https://github.com/oowekyala), we now have a new rule designer GUI, which
is based on JavaFX. It replaces the old designer and can be started via

*   `bin/run.sh designer` (on Unix-like platform such as Linux and Mac OS X)
*   `bin\designer.bat` (on Windows)

Note: At least Java8 is required for the designer. The old designer is still available
as `designerold` but will be removed with the next major release.

#### Java 9 support

The Java grammar has been updated to support analyzing Java 9 projects:

*   private methods in interfaces are possible
*   The underscore "\_" is considered an invalid identifier
*   Diamond operator for anonymous classes
*   The module declarations in `module-info.java` can be parsed
*   Concise try-with-resources statements are supported

Java 9 support is enabled by default. You can switch back to an older java version
via the command line, e.g. `-language java -version 1.8`.

#### Revamped Apex CPD

We are now using the Apex Jorje Lexer to tokenize Apex code for CPD. This change means:

*   All comments are now ignored for CPD. This is consistent with how other languages such as Java and Groovy work.
*   Tokenization honors the language specification, which improves accuracy.

CPD will therefore have less false positives and false negatives.

#### Java Type Resolution

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph) worked on type resolution
for Java. For this release he has extended support for method calls for both instance and static methods.

Method shadowing and overloading are supported, as are varargs. However, the selection of the target method upon
the presence of generics and type inference is still work in progress. Expect it in forecoming releases.

As for fields, the basic support was in place for release 5.8.0, but has now been expanded to support static fields.

#### Metrics Framework

As part of Google Summer of Code 2017, [Clément Fournier](https://github.com/oowekyala) is worked
on the new metrics framework for object-oriented metrics.

There are already a couple of metrics (e.g. ATFD, WMC, Cyclo, LoC) implemented. More metrics are planned.
Based on those metrics, rules like "GodClass" detection could be implemented more easily.
The following rules benefit from the metrics framework: NcssCount (java), NPathComplexity (java),
CyclomaticComplexity (both java and apex).

The Metrics framework has been abstracted and is available in `pmd-core` for other languages. With this
PMD release, the metrics framework is supported for both Java and Apex.

#### Error Reporting

A number of improvements on error reporting have taken place, meaning changes to some of the report formats.

Also of note, the xml report now provides a XML Schema definition, allowing easier parsing and validation.

##### Processing Errors

Processing errors can now provide not only the message previously included on some reports, but also a full stacktrace.
This will allow better error reports when providing feedback to the PMD team and help in debugging issues.

The report formats providing full stacktrace of errors are:

*   html
*   summaryhtml
*   textcolor
*   vbhtml
*   xml

##### Configuration Errors

For a long time reports have been notified of configuration errors on rules, but they have remained hidden.
On a push to make these more evident to users, and help them get the best results out of PMD, we have started
to include them on the reports.

So far, only reports that include processing errors are showing configuration errors. In other words, the report formats
providing configuration error reporting are:

*   csv
*   html
*   summaryhtml
*   text
*   textcolor
*   vbhtml
*   xml

As we move forward we will be able to detect and report more configuration errors (ie: incomplete `auxclasspath`)
and include them to such reports.

#### Apex Rule Suppression

Apex violations can now be suppressed very similarly to how it's done in Java, by making use of a
`@SuppressWarnings` annotation.

Supported syntax includes:

```
@SupressWarnings('PMD') // to supress all Apex rules
@SupressWarnings('all') // to supress all Apex rules
@SupressWarnings('PMD.ARuleName') // to supress only the rule named ARuleName
@SupressWarnings('PMD.ARuleName, PMD.AnotherRuleName') // to supress only the rule named ARuleName or AnotherRuleName
```

Notice this last scenario is slightly different to the Java syntax. This is due to differences in the Apex grammar for annotations.

#### Rule Categories

All built-in rules have been sorted into one of eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

These categories help you to find rules and figure out the relevance and impact for your project.

All rules have been moved accordingly, e.g. the rule "JumbledIncrementer", which was previously defined in the
ruleset "java-basic" has now been moved to the "Error Prone" category. The new rule reference to be used is
`<rule ref="category/java/errorprone.xml/JumbledIncrementer"/>`.

The old rulesets like "java-basic" are still kept for backwards-compatibility but will be removed eventually.
The rule reference documentation has been updated to reflect these changes.

#### New Rules

*   The new Java rule `NcssCount` (category `design`) replaces the three rules "NcssConstructorCount", "NcssMethodCount",
    and "NcssTypeCount". The new rule uses the metrics framework to achieve the same. It has two properties, to
    define the report level for method and class sizes separately. Constructors and methods are considered the same.

*   The new Java rule `DoNotExtendJavaLangThrowable` (category `errorprone`) is a companion for the
    `java-strictexception.xml/DoNotExtendJavaLangError`, detecting direct extensions of `java.lang.Throwable`.

*   The new Java rule `ForLoopCanBeForeach` (category `errorprone`) helps to identify those for-loops that can
    be safely refactored into for-each-loops available since java 1.5.

*   The new Java rule `AvoidFileStream` (category `performance`) helps to identify code relying on `FileInputStream` / `FileOutputStream`
    which, by using a finalizer, produces extra / unnecessary overhead to garbage collection, and should be replaced with
    `Files.newInputStream` / `Files.newOutputStream` available since java 1.7.

*   The new Java rule `DataClass` (category `design`) detects simple data-holders without behaviour. This might indicate
    that the behaviour is scattered elsewhere and the data class exposes the internal data structure,
    which breaks encapsulation.

*   The new Apex rule `AvoidDirectAccessTriggerMap` (category `errorprone`) helps to identify direct array access to triggers,
    which can produce bugs by either accessing non-existing indexes, or leaving them out. You should use for-each-loops
    instead.

*   The new Apex rule `AvoidHardcodingId` (category `errorprone`) detects hardcoded strings that look like identifiers
    and flags them. Record IDs change between environments, meaning hardcoded ids are bound to fail under a different
    setup.

*   The new Apex rule `CyclomaticComplexity` (category `design`) detects overly complex classes and methods. The
    report threshold can be configured separately for classes and methods.

*   A whole bunch of new rules has been added to Apex. They all fit into the category `errorprone`.
    The 5 rules are migrated for Apex from the equivalent Java rules and include:
    * `EmptyCatchBlock` to detect catch blocks completely ignoring exceptions.
    * `EmptyIfStmt` for if blocks with no content, that can be safely removed.
    * `EmptyTryOrFinallyBlock` for empty try / finally blocks that can be safely removed.
    * `EmptyWhileStmt` for empty while loops that can be safely removed.
    * `EmptyStatementBlock` for empty code blocks that can be safely removed.

*   The new Apex rule `AvoidSoslInLoops` (category `performance`) is the companion of the old
    `AvoidSoqlInLoops` rule, flagging SOSL (Salesforce Object Search Language) queries when within
    loops, to avoid governor issues, and hitting the database too often.

#### Modified Rules

*   The Java rule `UnnecessaryFinalModifier` (category `codestyle`, former ruleset `java-unnecessarycode`)
    has been merged into the rule `UnnecessaryModifier`. As part of this, the rule  has been revamped to detect more cases.
    It will now flag anonymous class' methods marked as final (can't be overridden, so it's pointless), along with
    final methods overridden / defined within enum instances. It will also flag `final` modifiers on try-with-resources.

*   The Java rule `UnnecessaryParentheses` (category `codestyle`, former ruleset `java-controversial`)
    has been merged into `UselessParentheses` (category `codestyle`, former ruleset `java-unnecessary`).
    The rule covers all scenarios previously covered by either rule.

*   The Java rule `UncommentedEmptyConstructor` (category `documentation`, former ruleset `java-design`)
     will now ignore empty constructors annotated with `javax.inject.Inject`.

*   The Java rule `AbstractClassWithoutAnyMethod` (category `bestpractices`, former ruleset `java-design`)
    will now ignore classes annotated with `com.google.auto.value.AutoValue`.

*   The Java rule `GodClass` (category `design', former ruleset `java-design`) has been revamped to use
    the new metrics framework.

*   The Java rule `LooseCoupling` (category `bestpractices`, former ruleset `java-coupling`) has been
    replaced by the typeresolution-based implementation.

*   The Java rule `CloneMethodMustImplementCloneable` (category `errorprone`, former ruleset `java-clone`)
    has been replaced by the typeresolution-based
    implementation and is now able to detect cases if a class implements or extends a Cloneable class/interface.

*   The Java rule `UnusedImports` (category `bestpractices`, former ruleset `java-imports`) has been
    replaced by the typeresolution-based
    implementation and is now able to detect unused on-demand imports.

*   The Java rule `SignatureDeclareThrowsException` (category `design`, former ruleset 'java-strictexception')
    has been replaced by the
    typeresolution-based implementation. It has a new property `IgnoreJUnitCompletely`, which allows all
    methods in a JUnit testcase to throw exceptions.

*   The Java rule `NPathComplexity` (category `design`, former ruleset `java-codesize`) has been revamped
    to use the new metrics framework.
    Its report threshold can be configured via the property `reportLevel`, which replaces the now
    deprecated property `minimum`.

*   The Java rule `CyclomaticComplexity` (category `design`, former ruleset `java-codesize`) has been
    revamped to use the new metrics framework.
    Its report threshold can be configured via the properties `classReportLevel` and `methodReportLevel` separately.
    The old property `reportLevel`, which configured the level for both total class and method complexity,
    is deprecated.

*   The Java rule `CommentRequired` (category `documentation`, former ruleset `java-comments`)
    has been revamped to include 2 new properties:
    *   `accessorCommentRequirement` to specify documentation requirements for getters and setters (default to `ignored`)
    *   `methodWithOverrideCommentRequirement` to specify documentation requirements for methods annotated with `@Override` (default to `ignored`)

*   The Java rule `EmptyCatchBlock` (category `errorprone`, former ruleset `java-empty`) has been changed to ignore
    exceptions named `ignore` or `expected` by default. You can still override this behaviour by setting the `allowExceptionNameRegex` property.

*   The Java rule `OptimizableToArrayCall` (category `performance`, former ruleset `design`) has been
    modified to fit for the current JVM implementations: It basically detects now the opposite and suggests to
    use `Collection.toArray(new E[0])` with a zero-sized array.
    See [Arrays of Wisdom of the Ancients](https://shipilev.net/blog/2016/arrays-wisdom-ancients/).

#### Deprecated Rules

*   The Java rules `NcssConstructorCount`, `NcssMethodCount`, and `NcssTypeCount` (ruleset `java-codesize`) have been
    deprecated. They will be replaced by the new rule `NcssCount` in the category `design`.

*   The Java rule `LooseCoupling` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name
    from category `bestpractices` instead.

*   The Java rule `CloneMethodMustImplementCloneable` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `errorprone` instead.

*   The Java rule `UnusedImports` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `bestpractices` instead.

*   The Java rule `SignatureDeclareThrowsException` in ruleset `java-typeresolution` is deprecated. Use the rule
    with the same name from category `design` instead.

*   The Java rule `EmptyStaticInitializer` in ruleset `java-empty` is deprecated. Use the rule `EmptyInitializer`
    from the category `errorprone`, which covers both static and non-static empty initializers.`

*   The Java rules `GuardDebugLogging` (ruleset `java-logging-jakarta-commons`) and `GuardLogStatementJavaUtil`
    (ruleset `java-logging-java`) have been deprecated. Use the rule `GuardLogStatement` from the
    category `bestpractices`, which covers all cases regardless of the logging framework.

#### Removed Rules

*   The deprecated Java rule `UseSingleton` has been removed from the ruleset `java-design`. The rule has been renamed
    long time ago to `UseUtilityClass` (category `design`).

#### Java Symbol Table

A [bug in symbol table](https://github.com/pmd/pmd/pull/549/commits/0958621ca884a8002012fc7738308c8dfc24b97c) prevented
the symbol table analysis to properly match primitive arrays types. The issue [affected the `java-unsedcode/UnusedPrivateMethod`](https://github.com/pmd/pmd/issues/521)
rule, but other rules may now produce improved results as consequence of this fix.

#### Apex Parser Update

The Apex parser version was bumped, from `1.0-sfdc-187` to `210-SNAPSHOT`. This update let us take full advantage
of the latest improvements from Salesforce, but introduces some breaking changes:

*   `BlockStatements` are now created for all control structures, even if no brace is used. We have therefore added
    a `hasCurlyBrace` method to differentiate between both scenarios.
*   New AST node types are available. In particular `CastExpression`, `ConstructorPreamble`, `IllegalStoreExpression`,
    `MethodBlockStatement`, `Modifier`, `MultiStatement`, `NestedExpression`, `NestedStoreExpression`,
    `NewKeyValueObjectExpression` and `StatementExecuted`
*   Some nodes have been removed. Such is the case of `TestNode`, `DottedExpression` and `NewNameValueObjectExpression`
    (replaced by `NewKeyValueObjectExpression`)

All existing rules have been updated to reflect these changes. If you have custom rules, be sure to update them.

For more info about the included Apex parser, see the new pmd module "pmd-apex-jorje", which packages and provides
the parser as a binary.

#### Incremental Analysis

The incremental analysis feature first introduced in PMD 5.6.0 has been enhanced. A few minor issues have been fixed,
and several improvements have been performed to make it more accurate.

The cache will now detect changes to the JARs referenced in the `auxclasspath` instead of simply looking at their paths
and order. This means that if you are referencing a JAR you are overwriting in some way, the incremental analysis can
now detect it and invalidate it's cache to avoid false reports.

Similarly, any changes to the execution classpath of PMD will invalidate the cache. This means that if you have custom
rules packaged in a jar, any changes to it will invalidate the cache automatically.

We have also improved logging on the analysis code, allowing better insight into how the cache is performing,
under debug / verbose builds you can even see individual hits / misses to the cache (and the reason for any miss!)

Finally, as this feature keeps maturing, we are gently pushing this forward. If not using incremental analysis,
a warning will now be produced suggesting users to adopt it for better performance.

#### Rule and Report Properties

The implementation around the properties support for rule properties and report properties has been revamped
to be fully typesafe. Along with that change, the support classes have been moved into an own
package `net.sourceforge.pmd.properties`. While there is no change necessary in the ruleset XML files,
when using/setting values for rules, there are adjustments necessary when declaring properties in Java-implemented
rules.

Rule properties can be declared both for Java based rules and XPath rules.
This is now very well documented in [Working with properties](pmd_userdocs_extending_defining_properties.html).

With PMD 6.0.0, multivalued properties are now also possible with XPath rules.

### Fixed Issues

*   all
    *   [#394](https://github.com/pmd/pmd/issues/394): \[core] PMD exclude rules are failing with IllegalArgumentException with non-default minimumPriority
    *   [#532](https://github.com/pmd/pmd/issues/532): \[core] security concerns on URL-based rulesets
    *   [#538](https://github.com/pmd/pmd/issues/538): \[core] Provide an XML Schema for XML reports
    *   [#600](https://github.com/pmd/pmd/issues/600): \[core] Nullpointer while creating cache File
    *   [#604](https://github.com/pmd/pmd/issues/604): \[core] Incremental analysis should detect changes to jars in classpath
    *   [#608](https://github.com/pmd/pmd/issues/608): \[core] Add DEBUG log when applying incremental analysis
    *   [#618](https://github.com/pmd/pmd/issues/618): \[core] Incremental Analysis doesn't close file correctly on Windows upon a cache hit
    *   [#643](https://github.com/pmd/pmd/issues/643): \[core] PMD Properties (dev-properties) breaks markup on CodeClimateRenderer
    *   [#680](https://github.com/pmd/pmd/pull/680): \[core] Isolate classloaders for runtime and auxclasspath
    *   [#762](https://github.com/pmd/pmd/issues/762): \[core] Remove method and file property from available property descriptors for XPath rules
    *   [#763](https://github.com/pmd/pmd/issues/763): \[core] Turn property descriptor util into an enum and enrich its interface
*   apex
    *   [#265](https://github.com/pmd/pmd/issues/265): \[apex] Make Rule suppression work
    *   [#488](https://github.com/pmd/pmd/pull/488): \[apex] Use Apex lexer for CPD
    *   [#489](https://github.com/pmd/pmd/pull/489): \[apex] Update Apex compiler
    *   [#500](https://github.com/pmd/pmd/issues/500): \[apex] Running through CLI shows jorje optimization messages
    *   [#605](https://github.com/pmd/pmd/issues/605): \[apex] java.lang.NoClassDefFoundError in the latest build
    *   [#637](https://github.com/pmd/pmd/issues/637): \[apex] Avoid SOSL in loops
    *   [#760](https://github.com/pmd/pmd/issues/760): \[apex] EmptyStatementBlock complains about missing rather than empty block
    *   [#766](https://github.com/pmd/pmd/issues/766): \[apex] Replace old Jorje parser with new one
    *   [#768](https://github.com/pmd/pmd/issues/768): \[apex] java.lang.NullPointerException from PMD
*   cpp
    *   [#448](https://github.com/pmd/pmd/issues/448): \[cpp] Write custom CharStream to handle continuation characters
*   java
    *   [#1454](https://sourceforge.net/p/pmd/bugs/1454/): \[java] OptimizableToArrayCall is outdated and invalid in current JVMs
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
    *   [#328](https://github.com/pmd/pmd/issues/328): \[java] java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/servlet/jsp/PageContext
    *   [#487](https://github.com/pmd/pmd/pull/487): \[java] Fix typeresolution for anonymous extending object
    *   [#496](https://github.com/pmd/pmd/issues/496): \[java] processing error on generics inherited from enclosing class
    *   [#510](https://github.com/pmd/pmd/issues/510): \[java] Typeresolution fails on a simple primary when the source is loaded from a class literal
    *   [#527](https://github.com/pmd/pmd/issues/527): \[java] Lombok getter annotation on enum is not recognized correctly
    *   [#534](https://github.com/pmd/pmd/issues/534): \[java] NPE in MethodTypeResolution for static methods
    *   [#603](https://github.com/pmd/pmd/issues/603): \[core] incremental analysis should invalidate upon Java rule plugin changes
    *   [#650](https://github.com/pmd/pmd/issues/650): \[java] ProcesingError analyzing code under 5.8.1
    *   [#732](https://github.com/pmd/pmd/issues/732): \[java] LinkageError with aux classpath
*   java-basic
    *   [#565](https://github.com/pmd/pmd/pull/565): \[java] False negative on DontCallThreadRun when extending Thread
*   java-comments
    *   [#396](https://github.com/pmd/pmd/issues/396): \[java] CommentRequired: add properties to ignore @Override method and getters / setters
    *   [#536](https://github.com/pmd/pmd/issues/536): \[java] CommentDefaultAccessModifierRule ignores constructors
*   java-controversial
    *   [#388](https://github.com/pmd/pmd/issues/388): \[java] controversial.AvoidLiteralsInIfCondition 0.0 false positive
    *   [#408](https://github.com/pmd/pmd/issues/408): \[java] DFA not analyzing asserts
    *   [#537](https://github.com/pmd/pmd/issues/537): \[java] UnnecessaryParentheses fails to detect obvious scenario
*   java-design
    *   [#357](https://github.com/pmd/pmd/issues/357): \[java] UncommentedEmptyConstructor consider annotations on Constructor
    *   [#438](https://github.com/pmd/pmd/issues/438): \[java] Relax AbstractClassWithoutAnyMethod when class is annotated by @AutoValue
    *   [#590](https://github.com/pmd/pmd/issues/590): \[java] False positive on MissingStaticMethodInNonInstantiatableClass
*    java-logging
    *   [#457](https://github.com/pmd/pmd/issues/457): \[java] Merge all log guarding rules
    *   [#721](https://github.com/pmd/pmd/issues/721): \[java] NPE in PMD 5.8.1 InvalidSlf4jMessageFormat
*   java-sunsecure
    *   [#468](https://github.com/pmd/pmd/issues/468): \[java] ArrayIsStoredDirectly false positive
*   java-unusedcode
    *   [#521](https://github.com/pmd/pmd/issues/521): \[java] UnusedPrivateMethod returns false positives with primitive data type in map argument
*   java-unnecessarycode
    *   [#412](https://github.com/pmd/pmd/issues/412): \[java] java-unnecessarycode/UnnecessaryFinalModifier missing cases
    *   [#676](https://github.com/pmd/pmd/issues/676): \[java] java-unnecessarycode/UnnecessaryFinalModifier on try-with-resources

### API Changes

*   The class `net.sourceforge.pmd.lang.dfa.NodeType` has been converted to an enum.
    All node types are enum members now instead of int constants. The names for node types are retained.

*   The *Properties API* (rule and report properties) has been revamped to be fully typesafe. This is everything
    around `net.sourceforge.pmd.properties.PropertyDescriptor`.

    Note: All classes related to properties have been moved into the package `net.sourceforge.pmd.properties`.

*   The rule classes `net.sourceforge.pmd.lang.apex.rule.apexunit.ApexUnitTestClassShouldHaveAsserts`
    and `net.sourceforge.pmd.lang.apex.rule.apexunit.ApexUnitTestShouldNotUseSeeAllDataTrue` have been
    renamed to `ApexUnitTestClassShouldHaveAssertsRule` and `ApexUnitTestShouldNotUseSeeAllDataTrueRule`,
    respectively. This is to comply with the naming convention, that each rule class should be suffixed with "Rule".

    This change has no impact on custom rulesets, since the rule names themselves didn't change.

*   The never implemented method `PMD.processFiles(PMDConfiguration, RuleSetFactory, Collection<File>, RuleContext, ProgressMonitor)` along with the interface `ProgressMonitor` has been removed.

*   The method `PMD.setupReport(RuleSets, RuleContext, String)` is gone. It was used to report dysfunctional
    rules. But PMD does this now automatically before processing the files, so there is no need for this
    method anymore.

*   All APIs deprecated in older versions are now removed. This includes:
    *    `Renderer.getPropertyDefinitions`
    *    `AbstractRenderer.defineProperty(String, String)`
    *    `AbstractRenderer.propertyDefinitions`
    *    `ReportListener`
    *    `Report.addListener(ReportListener)`
    *    `SynchronizedReportListener`
    *    `CPDConfiguration.CPDConfiguration(int, Language, String)`
    *    `CPDConfiguration.getRendererFromString(String)`
    *    `StreamUtil`
    *    `StringUtil.appendXmlEscaped(StringBuilder, String)`
    *    `StringUtil.htmlEncode(String)`


*   Several methods in `net.sourceforge.pmd.util.CollectionUtil` have been deprecated and will be removed in PMD 7.0.0. In particular:
    *    `CollectionUtil.addWithoutDuplicates(T[], T)`
    *    `CollectionUtil.addWithoutDuplicates(T[], T[])`
    *    `CollectionUtil.areSemanticEquals(T[], T[])`
    *    `CollectionUtil.areEqual(Object, Object)`
    *    `CollectionUtil.arraysAreEqual(Object, Object)`
    *    `CollectionUtil.valuesAreTransitivelyEqual(Object[], Object[])`


*   Several methods in `net.sourceforge.pmd.util.StringUtil` have been deprecated and will be removed in PMD 7.0.0. In particular:
    *    `StringUtil.startsWithAny(String, String[])`
    *    `StringUtil.isNotEmpty(String)`
    *    `StringUtil.isEmpty(String)`
    *    `StringUtil.isMissing(String)`
    *    `StringUtil.areSemanticEquals(String, String)`
    *    `StringUtil.replaceString(String, String, String)`
    *    `StringUtil.replaceString(String, char, String)`
    *    `StringUtil.substringsOf(String, char)`
    *    `StringUtil.substringsOf(String, String)`
    *    `StringUtil.asStringOn(StringBuffer, Iterator, String)`
    *    `StringUtil.asStringOn(StringBuilder, Object[], String)`
    *    `StringUtil.lpad(String, int)`

*   The class `net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition` is now abstract, and has been enhanced
    to provide several new methods.

*   The constructor of `net.sourceforge.pmd.RuleSetFactory`, which took a `ClassLoader` is deprecated.
    Please use the alternative constructor with the `net.sourceforge.pmd.util.ResourceLoader` instead.

*   The following GUI related classes have been deprecated and will be removed in PMD 7.0.0.
    The tool "bgastviewer", that could be started via the script `bgastviewer.bat` or `run.sh bgastviewer` is
    deprecated, too, and will be removed in PMD 7.0.0.
    Both the "old designer" and "bgastviewer" are replaced by the [New Rule Designer](#new-rule-designer).
    *   `net.sourceforge.pmd.util.designer.CodeEditorTextPane`
    *   `net.sourceforge.pmd.util.designer.CreateXMLRulePanel`
    *   `net.sourceforge.pmd.util.designer.Designer`
    *   `net.sourceforge.pmd.util.designer.DFAPanel`
    *   `net.sourceforge.pmd.util.designer.LineGetter`
    *   `net.sourceforge.pmd.util.viewer.Viewer`
    *   `net.sourceforge.pmd.util.viewer.gui.ActionCommands`
    *   `net.sourceforge.pmd.util.viewer.gui.ASTPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.EvaluationResultsPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.MainFrame`
    *   `net.sourceforge.pmd.util.viewer.gui.ParseExceptionHandler`
    *   `net.sourceforge.pmd.util.viewer.gui.SourceCodePanel`
    *   `net.sourceforge.pmd.util.viewer.gui.XPathPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.ASTNodePopupMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.AttributesSubMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.SimpleNodeSubMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.XPathFragmentAddingItem`
    *   `net.sourceforge.pmd.util.viewer.model.ASTModel`
    *   `net.sourceforge.pmd.util.viewer.model.AttributeToolkit`
    *   `net.sourceforge.pmd.util.viewer.model.SimpleNodeTreeNodeAdapter`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModel`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModelEvent`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModelListener`
    *   `net.sourceforge.pmd.util.viewer.util.NLS`

*   The following methods in `net.sourceforge.pmd.Rule` have been deprecated and will be removed in PMD 7.0.0.
    All methods are replaced by their bean-like counterparts
    *   `void setUsesDFA()`. Use `void setDfa(boolean)` instead.
    *   `boolean usesDFA()`. Use `boolean isDfa()` instead.
    *   `void setUsesTypeResolution()`. Use `void setTypeResolution(boolean)` instead.
    *   `boolean usesTypeResolution()`. Use `boolean isTypeResolution()` instead.
    *   `void setUsesMultifile()`. Use `void setMultifile(boolean)` instead.
    *   `boolean usesMultifile()`. Use `boolean isMultifile()` instead.
    *   `boolean usesRuleChain()`. Use `boolean isRuleChain()` instead.

### External Contributions

*   [#287](https://github.com/pmd/pmd/pull/287): \[apex] Make Rule suppression work - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#420](https://github.com/pmd/pmd/pull/420): \[java] Fix UR anomaly in assert statements - [Clément Fournier](https://github.com/oowekyala)
*   [#482](https://github.com/pmd/pmd/pull/482): \[java] Metrics testing framework + improved capabilities for metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#484](https://github.com/pmd/pmd/pull/484): \[core] Changed linux usage to a more unix like path - [patriksevallius](https://github.com/patriksevallius)
*   [#486](https://github.com/pmd/pmd/pull/486): \[java] Add basic method typeresolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#492](https://github.com/pmd/pmd/pull/492): \[java] Typeresolution for overloaded methods - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#495](https://github.com/pmd/pmd/pull/495): \[core] Custom rule reinitialization code - [Clément Fournier](https://github.com/oowekyala)
*   [#479](https://github.com/pmd/pmd/pull/479): \[core] Typesafe and immutable properties - [Clément Fournier](https://github.com/oowekyala)
*   [#499](https://github.com/pmd/pmd/pull/499): \[java] Metrics memoization tests - [Clément Fournier](https://github.com/oowekyala)
*   [#501](https://github.com/pmd/pmd/pull/501): \[java] Add support for most specific vararg method type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#502](https://github.com/pmd/pmd/pull/502): \[java] Add support for static field type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#505](https://github.com/pmd/pmd/pull/505): \[java] Followup on metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#506](https://github.com/pmd/pmd/pull/506): \[java] Add reduction rules to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#511](https://github.com/pmd/pmd/pull/511): \[core] Prepare abstraction of the metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#512](https://github.com/pmd/pmd/pull/512): \[java] Add incorporation to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#513](https://github.com/pmd/pmd/pull/513): \[java] Fix for maximally specific method selection - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#514](https://github.com/pmd/pmd/pull/514): \[java] Add static method type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#517](https://github.com/pmd/pmd/pull/517): \[doc] Metrics documentation - [Clément Fournier](https://github.com/oowekyala)
*   [#518](https://github.com/pmd/pmd/pull/518): \[core] Properties refactoring: factorized enumerated property - [Clément Fournier](https://github.com/oowekyala)
*   [#523](https://github.com/pmd/pmd/pull/523): \[java] Npath complexity metric and rule - [Clément Fournier](https://github.com/oowekyala)
*   [#524](https://github.com/pmd/pmd/pull/524): \[java] Add support for explicit type arguments with method invocation - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#525](https://github.com/pmd/pmd/pull/525): \[core] Fix line ending and not ignored files issues - [Matias Comercio](https://github.com/MatiasComercio)
*   [#528](https://github.com/pmd/pmd/pull/528): \[core] Fix typo - [Ayoub Kaanich](https://github.com/kayoub5)
*   [#529](https://github.com/pmd/pmd/pull/529): \[java] Abstracted the Java metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#530](https://github.com/pmd/pmd/pull/530): \[java] Fix issue #527: Lombok getter annotation on enum is not recognized correctly - [Clément Fournier](https://github.com/oowekyala)
*   [#533](https://github.com/pmd/pmd/pull/533): \[core] improve error message - [Dennis Kieselhorst](https://github.com/deki)
*   [#535](https://github.com/pmd/pmd/pull/535): \[apex] Fix broken Apex visitor adapter - [Clément Fournier](https://github.com/oowekyala)
*   [#542](https://github.com/pmd/pmd/pull/542): \[java] Metrics abstraction - [Clément Fournier](https://github.com/oowekyala)
*   [#545](https://github.com/pmd/pmd/pull/545): \[apex] Apex metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#548](https://github.com/pmd/pmd/pull/548): \[java] Metrics documentation - [Clément Fournier](https://github.com/oowekyala)
*   [#550](https://github.com/pmd/pmd/pull/550): \[java] Add basic resolution to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#553](https://github.com/pmd/pmd/pull/553): \[java] Refactored ParserTst into a static utility class + add getSourceFromClass - [Clément Fournier](https://github.com/oowekyala)
*   [#554](https://github.com/pmd/pmd/pull/554): \[java] Fix #537: UnnecessaryParentheses fails to detect obvious scenario - [Clément Fournier](https://github.com/oowekyala)
*   [#555](https://github.com/pmd/pmd/pull/555): \[java] Changed metrics/CyclomaticComplexityRule to use WMC when reporting classes - [Clément Fournier](https://github.com/oowekyala)
*   [#556](https://github.com/pmd/pmd/pull/556): \[java] Fix #357: UncommentedEmptyConstructor consider annotations on Constructor - [Clément Fournier](https://github.com/oowekyala)
*   [#557](https://github.com/pmd/pmd/pull/557): \[java] Fix NPath metric not counting ternaries correctly - [Clément Fournier](https://github.com/oowekyala)
*   [#563](https://github.com/pmd/pmd/pull/563): \[java] Add support for basic method type inference for strict invocation - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#566](https://github.com/pmd/pmd/pull/566): \[java] New rule in migrating ruleset: ForLoopCanBeForeach - [Clément Fournier](https://github.com/oowekyala)
*   [#567](https://github.com/pmd/pmd/pull/567): \[java] Last API change for metrics (metric options) - [Clément Fournier](https://github.com/oowekyala)
*   [#570](https://github.com/pmd/pmd/pull/570): \[java] Model lower, upper and intersection types - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#573](https://github.com/pmd/pmd/pull/573): \[java] Data class rule - [Clément Fournier](https://github.com/oowekyala)
*   [#576](https://github.com/pmd/pmd/pull/576): \[doc]\[java] Add hint for Guava users in InefficientEmptyStringCheck - [mmoehring](https://github.com/mmoehring)
*   [#578](https://github.com/pmd/pmd/pull/578): \[java] Refactored god class rule - [Clément Fournier](https://github.com/oowekyala)
*   [#579](https://github.com/pmd/pmd/pull/579): \[java] Update parsing to produce upper and lower bounds - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#580](https://github.com/pmd/pmd/pull/580): \[core] Add AbstractMetric to topple the class hierarchy of metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#581](https://github.com/pmd/pmd/pull/581): \[java] Relax AbstractClassWithoutAnyMethod when class is annotated by @AutoValue - [Niklas Baudy](https://github.com/vanniktech)
*   [#583](https://github.com/pmd/pmd/pull/583): \[java] Documentation about writing metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#585](https://github.com/pmd/pmd/pull/585): \[java] Moved NcssCountRule to codesize.xml - [Clément Fournier](https://github.com/oowekyala)
*   [#587](https://github.com/pmd/pmd/pull/587): \[core] Properties refactoring: Move static constants of ValueParser to class ValueParserConstants - [Clément Fournier](https://github.com/oowekyala)
*   [#588](https://github.com/pmd/pmd/pull/588): \[java] XPath function to compute metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#598](https://github.com/pmd/pmd/pull/598): \[java] Fix #388: controversial.AvoidLiteralsInIfCondition 0.0 false positive - [Clément Fournier](https://github.com/oowekyala)
*   [#602](https://github.com/pmd/pmd/pull/602): \[java] \[apex] Separate multifile analysis from metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#620](https://github.com/pmd/pmd/pull/620): \[core] Moved properties to n.s.pmd.properties - [Clément Fournier](https://github.com/oowekyala)
*   [#625](https://github.com/pmd/pmd/pull/625): \[apex] empty code ruleset for apex - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#632](https://github.com/pmd/pmd/pull/632): \[apex] Add AvoidDirectAccessTriggerMap rule to the style set - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#644](https://github.com/pmd/pmd/pull/644): \[core] Prevent internal dev-properties from being displayed on CodeClimate renderer - [Filipe Esperandio](https://github.com/filipesperandio)
*   [#660](https://github.com/pmd/pmd/pull/660): \[apex] avoid sosl in loops - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#661](https://github.com/pmd/pmd/pull/661): \[apex] avoid hardcoding id's - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#666](https://github.com/pmd/pmd/pull/666): \[java] Add DoNotExtendJavaLangThrowable rule - [Robert Painsi](https://github.com/robertpainsi)
*   [#668](https://github.com/pmd/pmd/pull/668): \[core] Fix javadoc warnings on pmd-core - [Clément Fournier](https://github.com/oowekyala)
*   [#669](https://github.com/pmd/pmd/pull/669): \[core] Builder pattern for properties - [Clément Fournier](https://github.com/oowekyala)
*   [#675](https://github.com/pmd/pmd/pull/675): \[java] Fix in Java grammar: Try with final resource node error - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#679](https://github.com/pmd/pmd/pull/679): \[core] Token scheme generalization - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#694](https://github.com/pmd/pmd/pull/694): \[core] Add minor fixes to root pom - [Matias Comercio](https://github.com/MatiasComercio)
*   [#696](https://github.com/pmd/pmd/pull/696): \[core] Add remove operation over nodes - [Matias Comercio](https://github.com/MatiasComercio)
*   [#711](https://github.com/pmd/pmd/pull/711): \[ui] New rule designer - [Clément Fournier](https://github.com/oowekyala)
*   [#722](https://github.com/pmd/pmd/pull/722): \[java] Move NPathComplexity from metrics to design - [Clément Fournier](https://github.com/oowekyala)
*   [#723](https://github.com/pmd/pmd/pull/723): \[core] Rule factory refactoring - [Clément Fournier](https://github.com/oowekyala)
*   [#726](https://github.com/pmd/pmd/pull/726): \[java] Fix issue #721 (NPE in InvalidSlf4jMessageFormat) - [Clément Fournier](https://github.com/oowekyala)
*   [#727](https://github.com/pmd/pmd/pull/727): \[core] Fix #725: numeric property descriptors now check their default value - [Clément Fournier](https://github.com/oowekyala)
*   [#733](https://github.com/pmd/pmd/pull/733): \[java] Some improvements to CommentRequired - [Clément Fournier](https://github.com/oowekyala)
*   [#734](https://github.com/pmd/pmd/pull/734): \[java] Move CyclomaticComplexity from metrics to design - [Clément Fournier](https://github.com/oowekyala)
*   [#736](https://github.com/pmd/pmd/pull/736): \[core] Make Saxon support multi valued XPath properties - [Clément Fournier](https://github.com/oowekyala)
*   [#737](https://github.com/pmd/pmd/pull/737): \[doc] Fix NPathComplexity documentation bad rendering - [Clément Fournier](https://github.com/oowekyala)
*   [#744](https://github.com/pmd/pmd/pull/744): \[doc] Added Apex to supported languages - [Michał Kuliński](https://github.com/coola)
*   [#746](https://github.com/pmd/pmd/pull/746): \[doc] Fix typo in incremental analysis log message - [Clément Fournier](https://github.com/oowekyala)
*   [#749](https://github.com/pmd/pmd/pull/749): \[doc] Update the documentation for properties - [Clément Fournier](https://github.com/oowekyala)
*   [#758](https://github.com/pmd/pmd/pull/758): \[core] Expose the full mapping from property type id to property extractor - [Clément Fournier](https://github.com/oowekyala)
*   [#764](https://github.com/pmd/pmd/pull/764): \[core] Prevent method and file property use in XPath rules - [Clément Fournier](https://github.com/oowekyala)
*   [#771](https://github.com/pmd/pmd/pull/771): \[apex] Fix Apex metrics framework failing on triggers, refs #768 - [Clément Fournier](https://github.com/oowekyala)
*   [#774](https://github.com/pmd/pmd/pull/774): \[java] Avoid using FileInput/Output - see JDK-8080225 - [Chas Honton](https://github.com/chonton)


## 01-July-2017 - 5.8.1

The PMD team is pleased to announce PMD 5.8.1.

This is a bug fixing release.

### Fixed Issues

*   java
    *   [#471](https://github.com/pmd/pmd/issues/471): \[java] Error while processing class when EnumMap is used in PMD 5.8.0
    *   [#477](https://github.com/pmd/pmd/issues/477): \[core] NoClassDefFoundError under 5.8
    *   [#478](https://github.com/pmd/pmd/issues/478): \[core] Processing issues dealing with anonymous classes

### API Changes

*   The `getGenericArgs()` method introduced to `TypeNode` in 5.8.0 was removed. You can access to generics' info through the `JavaTypeDefinition` object.
*   The `JavaTypeDefinitionBuilder` class introduced in 5.8.0 is not more. You can use factory methods available on `JavaTypeDefinition`

### External Contributions

*   [#472](https://github.com/pmd/pmd/pull/472): \[java] fix error with raw types, bug #471


## 24-June-2017 - 5.8.0

The PMD team is pleased to announce PMD 5.8.0.

This is a minor release.

### New and noteworthy

#### Java Type Resolution

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph) has been working on completing type resolution for Java.
His progress so far has allowed to properly resolve, in addition to previously supported statements:

 - References to `this` and `super`, even when qualified
 - References to fields, even when chained (ie: `this.myObject.aField`), and properly handling inheritance / shadowing

Lambda parameter types where these are infered rather than explicit are still not supported. Expect future releases to do so.


#### Metrics Framework

As part of Google Summer of Code 2017, [Clément Fournier](https://github.com/oowekyala) has been working on
a new metrics framework for object-oriented metrics.

The basic groundwork has been done already and with this release, including a first rule based on the
metrics framework as a proof-of-concept: The rule *CyclomaticComplexity*, currently in the temporary
ruleset *java-metrics*, uses the Cyclomatic Complexity metric to find overly complex code.
This rule will eventually replace the existing three *CyclomaticComplexity* rules that are currently
defined in the *java-codesize* ruleset (see also [issue #445](https://github.com/pmd/pmd/issues/445)).

Since this work is still in progress, the metrics API (package `net.sourceforge.pmd.lang.java.oom`)
is not finalized yet and is expected to change.


#### Modified Rules

*   The Java rule `UnnecessaryFinalModifier` (ruleset java-unnecessary) now also reports on private methods marked as `final`.
    Being private, such methods can't be overriden, and therefore, the final keyword is redundant.

*   The Java rule `PreserveStackTrace` (ruleset java-design) has been relaxed to support the builder pattern on thrown exception.
    This change may introduce some false positives if using the exception in non-orthodox ways for things other than setting the
    root cause of the exception. Contact us if you find any such scenarios.

*   The ruleset java-junit now properly detects JUnit5, and rules are being adapted to the changes on it's API.
    This support is, however, still incomplete. Let us know of any uses we are still missing on the [issue tracker](https://github.com/pmd/pmd/issues)

*   The Java rule `EmptyTryBlock` (ruleset java-empty) now allows empty blocks when using try-with-resources.

*   The Java rule `EmptyCatchBlock` (ruleset java-empty) now exposes a new property called `allowExceptionNameRegex`.
    This allow to setup a regular expression for names of exceptions you wish to ignore for this rule. For instance,
    setting it to `^(ignored|expected)$` would ignore all empty catch blocks where the catched exception is named
    either `ignored` or `expected`. The default ignores no exceptions, being backwards compatible.

#### Deprecated Rules

*   The three complexity rules `CyclomaticComplexity`, `StdCyclomaticComplexity`, `ModifiedCyclomaticComplexity` (ruleset java-codesize) have been deprecated. They will be eventually replaced
by a new CyclomaticComplexity rule based on the metrics framework. See also [issue #445](https://github.com/pmd/pmd/issues/445).

### Fixed Issues

*   General
    *   [#380](https://github.com/pmd/pmd/issues/380): \[core] NPE in RuleSet.hashCode
    *   [#407](https://github.com/pmd/pmd/issues/407): \[web] Release date is not properly formatted
    *   [#429](https://github.com/pmd/pmd/issues/429): \[core] Error when running PMD from folder with space
*   apex
    *   [#427](https://github.com/pmd/pmd/issues/427): \[apex] CPD error when parsing apex code from release 5.5.3
*   cpp
    *   [#431](https://github.com/pmd/pmd/issues/431): \[cpp] CPD gives wrong duplication blocks for CPP code
*   java
    *   [#414](https://github.com/pmd/pmd/issues/414): \[java] Java 8 parsing problem with annotations for wildcards
    *   [#415](https://github.com/pmd/pmd/issues/415): \[java] Parsing Error when having an Annotated Inner class
    *   [#417](https://github.com/pmd/pmd/issues/417): \[java] Parsing Problem with Annotation for Array Member Types
*   java-design
    *   [#397](https://github.com/pmd/pmd/issues/397): \[java] ConstructorCallsOverridableMethodRule: false positive for method called from lambda expression
    *   [#410](https://github.com/pmd/pmd/issues/410): \[java] ImmutableField: False positive with lombok
    *   [#422](https://github.com/pmd/pmd/issues/422): \[java] PreserveStackTraceRule: false positive when using builder pattern
*   java-empty
    *   [#413](https://github.com/pmd/pmd/issues/413): \[java] EmptyCatchBlock don't fail when exception is named ignore / expected
    *   [#432](https://github.com/pmd/pmd/issues/432): \[java] EmptyTryBlock: false positive for empty try-with-resource
*   java-imports:
    *   [#348](https://github.com/pmd/pmd/issues/348): \[java] imports/UnusedImport rule not considering static inner classes of imports
*   java-junit
    *   [#428](https://github.com/pmd/pmd/issues/428): \[java] PMD requires public modifier on JUnit 5 test
    *   [#465](https://github.com/pmd/pmd/issues/465): \[java] NullPointerException in JUnitTestsShouldIncludeAssertRule
*   java-logging:
    *   [#365](https://github.com/pmd/pmd/issues/365): \[java] InvalidSlf4jMessageFormat does not handle inline incrementation of arguments
*   java-strictexceptions
    *   [#350](https://github.com/pmd/pmd/issues/350): \[java] Throwing Exception in method signature is fine if the method is overriding or implementing something
*   java-typeresolution
    *   [#350](https://github.com/pmd/pmd/issues/350): \[java] Throwing Exception in method signature is fine if the method is overriding or implementing something
*   java-unnecessary
    *   [#421](https://github.com/pmd/pmd/issues/421): \[java] UnnecessaryFinalModifier final in private method
*   jsp
    *   [#311](https://github.com/pmd/pmd/issues/311): \[jsp] Parse error on HTML boolean attribute


### External Contributions

*   [#406](https://github.com/pmd/pmd/pull/406): \[java] False positive with lambda in java-design/ConstructorCallsOverridableMethod
*   [#409](https://github.com/pmd/pmd/pull/409): \[java] Groundwork for the upcoming metrics framework
*   [#416](https://github.com/pmd/pmd/pull/416): \[java] FIXED: Java 8 parsing problem with annotations for wildcards
*   [#418](https://github.com/pmd/pmd/pull/418): \[java] Type resolution: super and this keywords
*   [#423](https://github.com/pmd/pmd/pull/423): \[java] Add field access type resolution in non-generic cases
*   [#425](https://github.com/pmd/pmd/pull/425): \[java] False positive with builder pattern in java-design/PreserveStackTrace
*   [#426](https://github.com/pmd/pmd/pull/426): \[java] UnnecessaryFinalModifier final in private method
*   [#436](https://github.com/pmd/pmd/pull/436): \[java] Metrics framework tests and various improvements
*   [#440](https://github.com/pmd/pmd/pull/440): \[core] Created ruleset schema 3.0.0 (to use metrics)
*   [#443](https://github.com/pmd/pmd/pull/443): \[java] Optimize typeresolution, by skipping package and import declarations in visit(ASTName)
*   [#444](https://github.com/pmd/pmd/pull/444): \[java] [typeresolution]: add support for generic fields
*   [#451](https://github.com/pmd/pmd/pull/451): \[java] Metrics framework: first metrics + first rule


## 20-Mai-2017 - 5.7.0

The PMD team is pleased to announce PMD 5.7.0.

This is a minor release.

### New and noteworthy

#### Modified Rules

*   The rule "FieldDeclarationsShouldBeAtStartOfClass" of the java-design ruleset has a new property `ignoreInterfaceDeclarations`.
    Setting this property to `true` ignores interface declarations, that precede fields.
    Example usage:


    <rule ref="rulesets/java/design.xml/FieldDeclarationsShouldBeAtStartOfClass">
        <properties>
            <property name="ignoreInterfaceDeclarations" value="true"/>
        </properties>
    </rule>

#### Renderers

*   Added the 'empty' renderer which will write nothing.  Does not affect other behaviors, for example the command line PMD exit status
    will still indicate whether violations were found.

### Fixed Issues

*   General
    *   [#377](https://github.com/pmd/pmd/issues/377): \[core] Use maven wrapper and upgrade to maven 3.5.0
    *   [#376](https://github.com/pmd/pmd/issues/376): \[core] Improve build time on travis
*   java
    *   [#378](https://github.com/pmd/pmd/issues/378): \[java] Parser Error for empty statements
*   java-coupling
    *   [#1427](https://sourceforge.net/p/pmd/bugs/1427/): \[java] Law of Demeter violations for the Builder pattern
*   java-design
    *   [#345](https://github.com/pmd/pmd/issues/345): \[java] FieldDeclarationsShouldBeAtStartOfClass: Add ability to ignore interfaces
    *   [#389](https://github.com/pmd/pmd/issues/389): \[java] RuleSetCompatibility - not taking rename of UnusedModifier into account
*   java-junit
    *   [#358](https://github.com/pmd/pmd/issues/358): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule
*   java-strings
    *   [#334](https://github.com/pmd/pmd/issues/334): \[java] \[doc] Add suggestion to use StringUtils#isBlank for InefficientEmptyStringCheck
*   jsp-basic
    *   [#369](https://github.com/pmd/pmd/issues/369): \[jsp] Wrong issue "JSP file should use UTF-8 encoding"

### API Changes

*   The method `net.sourceforge.pmd.util.StringUtil#htmlEncode(String)` is deprecated.
    `org.apache.commons.lang3.StringEscapeUtils#escapeHtml4(String)` should be used instead.

### External Contributions

*   [#368](https://github.com/pmd/pmd/pull/368): \[vf] Adding proper AST support for negation expressions
*   [#372](https://github.com/pmd/pmd/pull/372): \[core] Fix XSS in HTML renderer
*   [#374](https://github.com/pmd/pmd/pull/374): \[java] Add property to ignore interfaces in FieldDeclarationsShouldBeAtStartOfClassRule
*   [#381](https://github.com/pmd/pmd/pull/381): \[core] Fix broken link in the site's doc
*   [#382](https://github.com/pmd/pmd/pull/382): \[java] Added documentation details on InefficientEmptyStringCheck
*   [#383](https://github.com/pmd/pmd/pull/383): \[jsp] Fixed JspEncoding false positive
*   [#390](https://github.com/pmd/pmd/pull/390): \[java] Remove trailing whitespaces in design.xml
*   [#391](https://github.com/pmd/pmd/pull/391): \[apex] Fix documentation typo
*   [#392](https://github.com/pmd/pmd/pull/392): \[java] False positive for Law Of Demeter (Builder pattern)
*   [#395](https://github.com/pmd/pmd/pull/395): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule


## 29-April-2017 - 5.6.1

The PMD team is pleased to announce PMD 5.6.1.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#363](https://github.com/pmd/pmd/issues/363): \[core] Rule documentation pages are missing
    *   [#364](https://github.com/pmd/pmd/issues/364): \[core] Stream closed exception when running through maven
    *   [#373](https://github.com/pmd/pmd/issues/373): \[core] RuleSetFactory - add more helper methods


## 22-April-2017 - 5.6.0

The PMD team is pleased to announce PMD 5.6.0.

The most significant changes are on analysis performance, support for Salesforce's Visualforce language
a whole new **Apex Security Rule Set** and the new **Braces Rule Set for Apex**.

We have added initial support for **incremental analysis**. The experimental feature allows
PMD to cache analysis results between executions to speed up the analysis for all
languages. New CLI flags and Ant options are available to configure it. Currently
*the feature is disabled by default*, but this may change as it matures.

Multithread performance has been enhanced by reducing thread-contention on a
bunch of areas. This is still an area of work, as the speedup of running
multithreaded analysis is still relatively small (4 threads produce less
than a 50% speedup). Future releases will keep improving on this area.

Once again, *Symbol Table* has been an area of great performance improvements.
This time we were able to further improve it's performance by roughly 10% on all
supported languages. In *Java* in particular, several more improvements were possible,
improving *Symbol Table* performance by a whooping 80%, that's over 15X faster
than PMD 5.5.1, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

### New and noteworthy

#### Incremental Analysis

PMD now supports incremental analysis. Analysis results can be cached and reused between runs.
This allows PMD to skip files without violations that have remained unchanged. In future releases,
we plan to extend this behavior to unchanged files with violations too.

The cache is automatically invalidated if:
 * the used PMD version changes
 * the `auxclasspath` changed and any rules require type resolution
 * the configured rule set has changed

This feature is *incubating* and is disabled by default. It's only enabled if you
specifically configure a cache file.

To configure the cache file from CLI, a new `-cache <path/to/file>` flag has been added.

For Ant, a new `cacheLocation` attribute has been added. For instance:

```xml
    <target name="pmd">
        <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask"/>
        <pmd cacheLocation="build/pmd/pmd.cache">
            <ruleset>rulesets/java/design.xml</ruleset>
            <ruleset>java-basic</ruleset>
            <formatter type="xml" toFile="c:\pmd_report.xml"/>
            <fileset dir="/usr/local/j2sdk1.4.1_01/src/">
                <include name="java/lang/*.java"/>
            </fileset>
        </pmd>
    </target>
```

#### Visualforce Support

Salesforce developers rejoice. To out growing Apex support we have added full Visualforce support.
Both CPD and PD are available. So far only a security ruleset is available (`vf-security`).

##### Visualforce Security Rule Set

###### VfUnescapeEl

The rule looks for Expression Language occurances printing unescaped values from the backend. These
could lead to XSS attacks.

###### VfCsrf

The rule looks for `<apex:page>` tags performing an action on page load, definish such `action`
through Expression Language, as doing so is vulnerable to CSRF attacks.

#### Apex Security Rule Set

A new ruleset focused on security has been added, consisting of a wide range of rules
to detect most common security problems.

##### ApexBadCrypto

The rule makes sure you are using randomly generated IVs and keys for `Crypto` calls.
Hard-wiring these values greatly compromises the security of encrypted data.

For instance, it would report violations on code such as:

```
public class without sharing Foo {
    Blob hardCodedIV = Blob.valueOf('Hardcoded IV 123');
    Blob hardCodedKey = Blob.valueOf('0000000000000000');
    Blob data = Blob.valueOf('Data to be encrypted');
    Blob encrypted = Crypto.encrypt('AES128', hardCodedKey, hardCodedIV, data);
}

```

##### ApexCRUDViolation

The rule validates you are checking for access permissions before a SOQL/SOSL/DML operation.
Since Apex runs in system mode not having proper permissions checks results in escalation of
privilege and may produce runtime errors. This check forces you to handle such scenarios.

For example, the following code is considered valid:

```
public class Foo {
    public Contact foo(String status, String ID) {
        Contact c = [SELECT Status__c FROM Contact WHERE Id=:ID];

        // Make sure we can update the database before even trying
        if (!Schema.sObjectType.Contact.fields.Name.isUpdateable()) {
            return null;
        }

        c.Status__c = status;
        update c;
        return c;
    }
}
```

##### ApexCSRF

Check to avoid making DML operations in Apex class constructor/init method. This prevents
modification of the database just by accessing a page.

For instance, the following code would be invalid:

```
public class Foo {
    public init() {
        insert data;
    }

    public Foo() {
        insert data;
    }
}
```

##### ApexDangerousMethods

Checks against calling dangerous methods.

For the time being, it reports:

* Against `FinancialForce`'s `Configuration.disableTriggerCRUDSecurity()`. Disabling CRUD security
opens the door to several attacks and requires manual validation, which is unreliable.
* Calling `System.debug` passing sensitive data as parameter, which could lead to exposure
of private data.

##### ApexInsecureEndpoint

Checks against accessing endpoints under plain **http**. You should always use
**https** for security.

##### ApexOpenRedirect

Checks against redirects to user-controlled locations. This prevents attackers from
redirecting users to phishing sites.

For instance, the following code would be reported:

```
public class without sharing Foo {
    String unsafeLocation = ApexPage.getCurrentPage().getParameters.get('url_param');
    PageReference page() {
       return new PageReference(unsafeLocation);
    }
}
```

##### ApexSharingViolations

Detect classes declared without explicit sharing mode if DML methods are used. This
forces the developer to take access restrictions into account before modifying objects.

##### ApexSOQLInjection

Detects the usage of untrusted / unescaped variables in DML queries.

For instance, it would report on:

```
public class Foo {
    public void test1(String t1) {
        Database.query('SELECT Id FROM Account' + t1);
    }
}
```

##### ApexSuggestUsingNamedCred

Detects hardcoded credentials used in requests to an endpoint.

You should refrain from hardcoding credentials:
  * They are hard to mantain by being mixed in application code
  * Particularly hard to update them when used from different classes
  * Granting a developer access to the codebase means granting knowledge
     of credentials, keeping a two-level access is not possible.
  * Using different credentials for different environments is troublesome
     and error-prone.

Instead, you should use *Named Credentials* and a callout endpoint.

For more information, you can check [this](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_callouts_named_credentials.htm)

##### ApexXSSFromEscapeFalse

Reports on calls to `addError` with disabled escaping. The message passed to `addError`
will be displayed directly to the user in the UI, making it prime ground for XSS
attacks if unescaped.

##### ApexXSSFromURLParam

Makes sure that all values obtained from URL parameters are properly escaped / sanitized
to avoid XSS attacks.

#### Apex Braces Rule Set

The Braces Rule Set has been added and serves the same purpose as the Braces Rule Set from Java:
It checks the use and placement of braces around if-statements, for-loops and so on.

##### IfStmtsMustUseBraces

Avoid using if statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

##### WhileLoopsMustUseBraces

Avoid using 'while' statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
while (true)    // not recommended
      x++;

while (true) {  // preferred approach
      x++;
}
```

##### IfElseStmtsMustUseBraces

Avoid using if..else statements without using surrounding braces. If the code formatting
or indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
// this is not recommended
if (foo)
       x = x+1;
   else
       x = x-1;

// preferred approach
if (foo) {
   x = x+1;
} else {
   x = x-1;
}
```

##### ForLoopsMustUseBraces

Avoid using 'for' statements without using surrounding braces. If the code formatting or
indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

For instance, the following code shows the different. PMD would report on the not recommended approach:

```
for (int i = 0; i < 42; i++) // not recommended
    foo();

for (int i = 0; i < 42; i++) { // preferred approach
    foo();
}
```

#### New Rules

##### AccessorMethodGeneration (java-design)

When accessing a private field / method from another class, the Java compiler will generate an accessor method
with package-private visibility. This adds overhead, and to the dex method count on Android. This situation can
be avoided by changing the visibility of the field / method from private to package-private.

For instance, it would report violations on code such as:

```
public class OuterClass {
    private int counter;
    /* package */ int id;

    public class InnerClass {
        InnerClass() {
            OuterClass.this.counter++; // wrong, accessor method will be generated
        }

        public int getOuterClassId() {
            return OuterClass.this.id; // id is package-private, no accessor method needed
        }
    }
}
```

This new rule is part of the `java-design` ruleset.

#### Modified Rules

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) now has a new property `statementOrderMatters`.
    It is enabled by default to stay backwards compatible. But if this property is set to `false`, this rule
    no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

*   The Java rule `UseLocaleWithCaseConversions` (ruleset java-design) has been modified, to detect calls
    to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
    and potentially new false positives.
    See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).

*   The Java rule `AvoidConstantsInterface` (ruleset java-design) has been removed. It is completely replaced by
    the rule `ConstantsInInterface`.

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been moved to the ruleset java-unnecessary
    and has been renamed to `UnnecessaryModifier`.
    Additionally, it has been expanded to consider more redundant modifiers:
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

*   The Java rule `JUnitTestsShouldIncludeAssert` (ruleset java-junit) now accepts usage of `@Rule` `ExpectedException`
    to set expectations on exceptions, and are considered as valid assertions.

#### CPD Suppression

It is now possible to allow CPD suppression through comments in **Java**. You tell CPD to ignore
the following code with a comment containin `CPD-OFF` and with `CPD-ON` you tell CPD to resume
analysis. The old approach via `@SuppressWarnings` annotation is still supported, but is considered
**deprecated**, since it is limited to locations where the `SuppressWarnings` annotation is allowed.
See [PR #250](https://github.com/pmd/pmd/pull/250).

For example:

```java
    public Object someMethod(int x) throws Exception {
        // some unignored code

        // tell cpd to start ignoring code - CPD-OFF

        // mission critical code, manually loop unroll
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);
        goDoSomethingAwesome(x + x / 2);

        // resume CPD analysis - CPD-ON

        // further code will *not* be ignored
    }
```

#### CPD filelist command line option

CPD now supports the command line option `--filelist`. With that, you can specify a file, which
contains the names and paths of the files, that should be analyzed. This is similar to PMD's filelist option.
You need to use this, if you have a large project with many files, and you hit the command line length limit.


### Fixed Issues

*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
    *   [#324](https://github.com/pmd/pmd/issues/324): \[core] Automated release - github release notes missing
    *   [#337](https://github.com/pmd/pmd/issues/337): \[core] Version 5.5.4 seems to hold file lock on rules JAR (affects Windows only)
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   apex-complexity
    *   [#183](https://github.com/pmd/pmd/issues/183): \[apex] NCSS Method length is incorrect when using method chaining
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   apex-security
    *   [#264](https://github.com/pmd/pmd/issues/264): \[apex] ApexXSSFromURLParamRule shouldn't enforce ESAPI usage. String.escapeHtml4 is sufficient.
    *   [#315](https://github.com/pmd/pmd/issues/315): \[apex] Documentation flaw on Apex Sharing Violations
*   java
    *   [#185](https://github.com/pmd/pmd/issues/185): \[java] CPD runs into NPE when analyzing Lucene
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#933](https://sourceforge.net/p/pmd/bugs/933/): \[java] UnnecessaryLocalBeforeReturn false positive for SuppressWarnings annotation
    *   [#1448](https://sourceforge.net/p/pmd/bugs/1448/): \[java] ImmutableField: Private field in inner class gives false positive with lambdas
    *   [#1495](https://sourceforge.net/p/pmd/bugs/1495/): \[java] UnnecessaryLocalBeforeReturn with assert
    *   [#1496](https://sourceforge.net/p/pmd/bugs/1496/): \[java] New Rule: AccesorMethodGeneration - complements accessor class rule
    *   [#1512](https://sourceforge.net/p/pmd/bugs/1512/): \[java] Combine rules AvoidConstantsInInterface and ConstantsInInterface
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
    *   [#1556](https://sourceforge.net/p/pmd/bugs/1556/): \[java] UseLocaleWithCaseConversions does not works with `ResultSet` (false negative)
    *   [#177](https://github.com/pmd/pmd/issues/177): \[java] SingularField with lambdas as final fields
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
    *   [#240](https://github.com/pmd/pmd/issues/240): \[java] UnnecessaryLocalBeforeReturn: Enhance by checking usages
    *   [#274](https://github.com/pmd/pmd/issues/274): \[java] AccessorMethodGeneration: Method inside static inner class incorrectly reported
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
    *   [#282](https://github.com/pmd/pmd/issues/282): \[java] UnnecessaryLocalBeforeReturn false positive when cloning Maps
    *   [#291](https://github.com/pmd/pmd/issues/291): \[java] Improve quality of AccessorClassGeneration
    *   [#310](https://github.com/pmd/pmd/issues/310): \[java] UnnecessaryLocalBeforeReturn enhancement is overly restrictive -- method order matters
    *   [#352](https://github.com/pmd/pmd/issues/352): \[java] AccessorClassGeneration throws ClassCastException when seeing array construction
*   java-imports
    *   [#338](https://github.com/pmd/pmd/issues/338): \[java] False positive on DontImportJavaLang when importing java.lang.ProcessBuilder
    *   [#339](https://github.com/pmd/pmd/issues/339): \[java] False positive on DontImportJavaLang when importing Java 7's java.lang.invoke.MethodHandles
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
    *   [#1555](https://sourceforge.net/p/pmd/bugs/1555/): \[java] UnnecessaryFullyQualifiedName: Really necessary fully qualified name
*   java-junit
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
    *   [#330](https://github.com/pmd/pmd/issues/330): \[java] NPE applying rule JUnitTestsShouldIncludeAssert
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings
    *   [#202](https://github.com/pmd/pmd/issues/202): \[java] \[doc] ConsecutiveAppendsShouldReuse is not really an optimization
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()
*   java-unnecessary
    *   [#199](https://github.com/pmd/pmd/issues/199): \[java] UselessParentheses: Parentheses in return statement are incorrectly reported as useless
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct
*   javascript
    *   [#201](https://github.com/pmd/pmd/issues/201): \[javascript] template strings are not correctly parsed


### API Changes

*   `net.sourceforge.pmd.RuleSetFactory` is now immutable and its behavior cannot be changed anymore.
    It provides constructors to create new adjusted instances. This allows to avoid synchronization in RuleSetFactory.
    See [PR #131](https://github.com/pmd/pmd/pull/131).
*   `net.sourceforge.pmd.RuleSet` is now immutable, too, and can only be created via `RuleSetFactory`.
    See [PR #145](https://github.com/pmd/pmd/pull/145).
*   `net.sourceforge.pmd.cli.XPathCLI` has been removed. It's functionality is fully covered by the Designer.
*   `net.sourceforge.pmd.Report` now works with `ThreadSafeReportListener`s. Both `ReportListener` and
    `SynchronizedReportListener` are deprecated in favor of `net.sourceforge.pmd.ThreadSafeReportListener`.
    Therefore, the methods `getSynchronizedListeners()` and `addSynchronizedListeners(...)` have been
    replaced by `getListeners()` and `addListeners(...)`. See [PR #193](https://github.com/pmd/pmd/pull/193).

### External Contributions

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#137](https://github.com/pmd/pmd/pull/137): \[apex] Adjusted remediation points
*   [#146](https://github.com/pmd/pmd/pull/146): \[apex] Detection of missing Apex CRUD checks for SOQL/DML operations
*   [#147](https://github.com/pmd/pmd/pull/147): \[apex] Adding XSS detection to return statements
*   [#148](https://github.com/pmd/pmd/pull/148): \[apex] Improving detection of SOQL injection
*   [#149](https://github.com/pmd/pmd/pull/149): \[apex] Whitelisting String.isEmpty and casting
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#158](https://github.com/pmd/pmd/pull/158): \[apex] Reducing FPs in SOQL with VF getter methods
*   [#160](https://github.com/pmd/pmd/pull/160): \[apex] Flagging of dangerous method call
*   [#163](https://github.com/pmd/pmd/pull/163): \[apex] Flagging of System.debug
*   [#165](https://github.com/pmd/pmd/pull/165): \[apex] Improving open redirect rule to avoid test classes/methods
*   [#167](https://github.com/pmd/pmd/pull/167): \[apex] GC and thread safety changes
*   [#169](https://github.com/pmd/pmd/pull/169): \[apex] Improving detection for DML with inline new object
*   [#170](https://github.com/pmd/pmd/pull/170): \[core] Ant Task Formatter encoding issue with XMLRenderer
*   [#172](https://github.com/pmd/pmd/pull/172): \[apex] Bug fix, detects both Apex fields and class members
*   [#175](https://github.com/pmd/pmd/pull/175): \[apex] ApexXSSFromURLParam: Adding missing casting methods
*   [#176](https://github.com/pmd/pmd/pull/176): \[apex] Bug fix for FP: open redirect for strings prefixed with / is safe
*   [#179](https://github.com/pmd/pmd/pull/179): \[apex] Legacy test class declaration support
*   [#181](https://github.com/pmd/pmd/pull/181): \[apex] Control flow based CRUD rule checking
*   [#184](https://github.com/pmd/pmd/pull/184): \[apex] Improving open redirect detection for static fields & assignment operations
*   [#189](https://github.com/pmd/pmd/pull/189): \[apex] Bug fix of SOQL concatenated vars detection
*   [#191](https://github.com/pmd/pmd/pull/191): \[apex] Detection of sharing violation when Database. methods are used
*   [#192](https://github.com/pmd/pmd/pull/192): \[apex] Dead code removal
*   [#200](https://github.com/pmd/pmd/pull/200): \[javascript] Templatestring grammar fix
*   [#204](https://github.com/pmd/pmd/pull/204): \[apex] Sharing violation SOQL detection bug fix
*   [#214](https://github.com/pmd/pmd/pull/214): \[apex] Sharing violation improving reporting of the correct node, de-duping
*   [#217](https://github.com/pmd/pmd/pull/217): \[core] Make it build on Windows
*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields
*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation
*   [#268](https://github.com/pmd/pmd/pull/268): \[apex] Support safe escaping via String method
*   [#273](https://github.com/pmd/pmd/pull/273): \[apex] Shade jackson on apex
*   [#279](https://github.com/pmd/pmd/pull/279): \[vf] New Salesforce VisualForce language support
*   [#280](https://github.com/pmd/pmd/pull/280): \[apex] Support for Aggregate Result in CRUD rules
*   [#281](https://github.com/pmd/pmd/pull/281): \[apex] Add Braces Rule Set
*   [#283](https://github.com/pmd/pmd/pull/283): \[vf] CSRF in VF controller pages
*   [#284](https://github.com/pmd/pmd/pull/284): \[vf] Adding support for parsing EL in script tags
*   [#288](https://github.com/pmd/pmd/pull/288): \[vf] Setting the tab size to 4 for VF
*   [#289](https://github.com/pmd/pmd/pull/289): \[apex] Complex SOQL Crud check bug fixes
*   [#296](https://github.com/pmd/pmd/pull/296): \[apex] Adding String.IsNotBlank to the whitelist to prevent False positives
*   [#297](https://github.com/pmd/pmd/pull/297): \[core] CPD: Adding the --filelist option from pmd to cpd
*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty()
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions
*   [#313](https://github.com/pmd/pmd/pull/313): \[vf] Apex:iFrame not being detected - bug fix
*   [#314](https://github.com/pmd/pmd/pull/314): \[vf] Bug fixes for incorrect Id detection and escaping
*   [#316](https://github.com/pmd/pmd/pull/316): \[apex] Ignoring certain rules in Batch classes, Queueable, and install scripts
*   [#317](https://github.com/pmd/pmd/pull/317): \[apex] Add support for safe ID assignment from URL param
*   [#326](https://github.com/pmd/pmd/pull/326): \[vf] Quote detection improvement and method argument detection
*   [#327](https://github.com/pmd/pmd/pull/327): \[apex] Fixed SOQL injection detection for escaped vars
*   [#331](https://github.com/pmd/pmd/pull/331): \[java] JunitTestsShouldIncludeAssertRule now handles AllocationExpression correctly
*   [#332](https://github.com/pmd/pmd/pull/332): \[java] Future-proof DontImportJavaLangRule
*   [#340](https://github.com/pmd/pmd/pull/340): \[vf] Multiple parser bug fixes
*   [#341](https://github.com/pmd/pmd/pull/341): \[vf] JSON.parse(..) and NOT(..) are safely evaluated
*   [#343](https://github.com/pmd/pmd/pull/343): \[apex] int,id,boolean,ternary operator condition are not injection in Soql
*   [#344](https://github.com/pmd/pmd/pull/344): \[apex] ApexCRUDViolationRule: Bug fix for ClassCastException
*   [#351](https://github.com/pmd/pmd/pull/351): \[vf] Fixing regression introduced by #341


## 29-April-2017 - 5.5.7

The PMD team is pleased to announce PMD 5.5.7.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#364](https://github.com/pmd/pmd/issues/364): \[core] Stream closed exception when running through maven


## 19-April-2017 - 5.5.6

The PMD team is pleased to announce PMD 5.5.6.

This is a bug fixing release.

### Fixed Issues

*   General
    *   [#324](https://github.com/pmd/pmd/issues/324): \[core] Automated release - github release notes missing
    *   [#337](https://github.com/pmd/pmd/issues/337): \[core] Version 5.5.4 seems to hold file lock on rules JAR (affects Windows only)


## 27-March-2017 - 5.5.5

The PMD team is pleased to announce PMD 5.5.5.


### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java:
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
*   java-design
    *   [#274](https://github.com/pmd/pmd/issues/274): \[java] AccessorMethodGeneration: Method inside static inner class incorrectly reported
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
    *   [#282](https://github.com/pmd/pmd/issues/282): \[java] UnnecessaryLocalBeforeReturn false positive when cloning Maps
    *   [#291](https://github.com/pmd/pmd/issues/291): \[java] Improve quality of AccessorClassGeneration
*   java-junit:
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### External Contributions

*   [#280](https://github.com/pmd/pmd/pull/280): \[apex] Support for Aggregate Result in CRUD rules
*   [#289](https://github.com/pmd/pmd/pull/289): \[apex] Complex SOQL Crud check bug fixes
*   [#296](https://github.com/pmd/pmd/pull/296): \[apex] Adding String.IsNotBlank to the whitelist to prevent False positives
*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty()
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions


## 25-Februar-2017 - 5.5.4

The PMD team is pleased to announce PMD 5.5.4



### New and noteworthy

#### New Rules

##### AccessorMethodGeneration (java-design)

When accessing a private field / method from another class, the Java compiler will generate a accessor methods
with package-private visibility. This adds overhead, and to the dex method count on Android. This situation can
be avoided by changing the visibility of the field / method from private to package-private.

For instance, it would report violations on code such as:

```
public class OuterClass {
    private int counter;
    /* package */ int id;

    public class InnerClass {
        InnerClass() {
            OuterClass.this.counter++; // wrong, accessor method will be generated
        }

        public int getOuterClassId() {
            return OuterClass.this.id; // id is package-private, no accessor method needed
        }
    }
}
```

This new rule is part of the `java-design` ruleset.

#### Modified Rules

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

### Fixed Issues

*   General
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
*   apex-complexity
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   apex-security
    *   [#264](https://github.com/pmd/pmd/issues/264): \[apex] ApexXSSFromURLParamRule shouldn't enforce ESAPI usage. String.escapeHtml4 is sufficient.
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#933](https://sourceforge.net/p/pmd/bugs/933/): \[java] UnnecessaryLocalBeforeReturn false positive for SuppressWarnings annotation
    *   [#1496](https://sourceforge.net/p/pmd/bugs/1496/): \[java] New Rule: AccesorMethodGeneration - complements accessor class rule
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
    *   [#240](https://github.com/pmd/pmd/issues/240): \[java] UnnecessaryLocalBeforeReturn: Enhance by checking usages
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive


### External Contributions

*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields
*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation
*   [#268](https://github.com/pmd/pmd/pull/268): \[apex] Support safe escaping via String method
*   [#273](https://github.com/pmd/pmd/pull/273): \[apex] Shade jackson on apex


## 28-January-2017 - 5.5.3

The PMD team is pleased to announce PMD 5.5.3

The most significant changes are on analysis performance and a whole new **Apex Security Rule Set**.

Multithread performance has been enhanced by reducing thread-contention on a
bunch of areas. This is still an area of work, as the speedup of running
multithreaded analysis is still relatively small (4 threads produce less
than a 50% speedup). Future releases will keep improving on this area.

Once again, *Symbol Table* has been an area of great performance improvements.
This time we were able to further improve it's performance by roughly 10% on all
supported languages. In *Java* in particular, several more improvements were possible,
improving *Symbol Table* performance by a whooping 30%, that's over 5X faster
than PMD 5.5.1, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

### New and noteworthy

#### Apex Security Rule Set

A new ruleset focused on security has been added, consisting of a wide range of rules
to detect most common security problems.

##### ApexBadCrypto

The rule makes sure you are using randomly generated IVs and keys for `Crypto` calls.
Hard-wiring these values greatly compromises the security of encrypted data.

For instance, it would report violations on code such as:

```
public class without sharing Foo {
    Blob hardCodedIV = Blob.valueOf('Hardcoded IV 123');
    Blob hardCodedKey = Blob.valueOf('0000000000000000');
    Blob data = Blob.valueOf('Data to be encrypted');
    Blob encrypted = Crypto.encrypt('AES128', hardCodedKey, hardCodedIV, data);
}

```

##### ApexCRUDViolation

The rule validates you are checking for access permissions before a SOQL/SOSL/DML operation.
Since Apex runs in system mode not having proper permissions checks results in escalation of
privilege and may produce runtime errors. This check forces you to handle such scenarios.

For example, the following code is considered valid:

```
public class Foo {
    public Contact foo(String status, String ID) {
        Contact c = [SELECT Status__c FROM Contact WHERE Id=:ID];

        // Make sure we can update the database before even trying
        if (!Schema.sObjectType.Contact.fields.Name.isUpdateable()) {
            return null;
        }

        c.Status__c = status;
        update c;
        return c;
    }
}
```

##### ApexCSRF

Check to avoid making DML operations in Apex class constructor/init method. This prevents
modification of the database just by accessing a page.

For instance, the following code would be invalid:

```
public class Foo {
    public init() {
        insert data;
    }

    public Foo() {
        insert data;
    }
}
```

##### ApexDangerousMethods

Checks against calling dangerous methods.

For the time being, it reports:

* Against `FinancialForce`'s `Configuration.disableTriggerCRUDSecurity()`. Disabling CRUD security
opens the door to several attacks and requires manual validation, which is unreliable.
* Calling `System.debug` passing sensitive data as parameter, which could lead to exposure
of private data.

##### ApexInsecureEndpoint

Checks against accessing endpoints under plain **http**. You should always use
**https** for security.

##### ApexOpenRedirect

Checks against redirects to user-controlled locations. This prevents attackers from
redirecting users to phishing sites.

For instance, the following code would be reported:

```
public class without sharing Foo {
    String unsafeLocation = ApexPage.getCurrentPage().getParameters.get('url_param');
    PageReference page() {
       return new PageReference(unsafeLocation);
    }
}
```

##### ApexSharingViolations

Detect classes declared without explicit sharing mode if DML methods are used. This
forces the developer to take access restrictions into account before modifying objects.

##### ApexSOQLInjection

Detects the usage of untrusted / unescaped variables in DML queries.

For instance, it would report on:

```
public class Foo {
    public void test1(String t1) {
        Database.query('SELECT Id FROM Account' + t1);
    }
}
```

##### ApexSuggestUsingNamedCred

Detects hardcoded credentials used in requests to an endpoint.

You should refrain from hardcoding credentials:
  * They are hard to mantain by being mixed in application code
  * Particularly hard to update them when used from different classes
  * Granting a developer access to the codebase means granting knowledge
     of credentials, keeping a two-level access is not possible.
  * Using different credentials for different environments is troublesome
     and error-prone.

Instead, you should use *Named Credentials* and a callout endpoint.

For more information, you can check [this](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_callouts_named_credentials.htm)

##### ApexXSSFromEscapeFalse

Reports on calls to `addError` with disabled escaping. The message passed to `addError`
will be displayed directly to the user in the UI, making it prime ground for XSS
attacks if unescaped.

##### ApexXSSFromURLParam

Makes sure that all values obtained from URL parameters are properly escaped / sanitized
to avoid XSS attacks.

#### Modified Rules

The Java rule "UseLocaleWithCaseConversions" (ruleset java-design) has been modified, to detect calls
to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
and potentially new false positives.
See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).


### Fixed Issues

*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   apex-complexity
    *   [#183](https://github.com/pmd/pmd/issues/183): \[apex] NCSS Method length is incorrect when using method chaining
*   java
    *   [#185](https://github.com/pmd/pmd/issues/185): \[java] CPD runs into NPE when analyzing Lucene
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-design
    *   [#1448](https://sourceforge.net/p/pmd/bugs/1448/): \[java] ImmutableField: Private field in inner class gives false positive with lambdas
    *   [#1495](https://sourceforge.net/p/pmd/bugs/1495/): \[java] UnnecessaryLocalBeforeReturn with assert
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
    *   [#1556](https://sourceforge.net/p/pmd/bugs/1556/): \[java] UseLocaleWithCaseConversions does not works with `ResultSet` (false negative)
    *   [#177](https://github.com/pmd/pmd/issues/177): \[java] SingularField with lambdas as final fields
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
    *   [#1555](https://sourceforge.net/p/pmd/bugs/1555/): \[java] UnnecessaryFullyQualifiedName: Really necessary fully qualified name
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
*   java-unnecessary
    *   [#199](https://github.com/pmd/pmd/issues/199): \[java] UselessParentheses: Parentheses in return statement are incorrectly reported as useless
*   java-strings
    *   [#202](https://github.com/pmd/pmd/issues/202): \[java] \[doc] ConsecutiveAppendsShouldReuse is not really an optimization
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct
*   javascript
    *   [#201](https://github.com/pmd/pmd/issues/201): \[javascript] template strings are not correctly parsed


### API Changes

*   `net.sourceforge.pmd.RuleSetFactory` is now immutable and its behavior cannot be changed anymore.
    It provides constructors to create new adjusted instances. This allows to avoid synchronization in RuleSetFactory.
    See [PR #131](https://github.com/pmd/pmd/pull/131).

### External Contributions

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#137](https://github.com/pmd/pmd/pull/137): \[apex] Adjusted remediation points
*   [#146](https://github.com/pmd/pmd/pull/146): \[apex] Detection of missing Apex CRUD checks for SOQL/DML operations
*   [#147](https://github.com/pmd/pmd/pull/147): \[apex] Adding XSS detection to return statements
*   [#148](https://github.com/pmd/pmd/pull/148): \[apex] Improving detection of SOQL injection
*   [#149](https://github.com/pmd/pmd/pull/149): \[apex] Whitelisting String.isEmpty and casting
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#158](https://github.com/pmd/pmd/pull/158): \[apex] Reducing FPs in SOQL with VF getter methods
*   [#160](https://github.com/pmd/pmd/pull/160): \[apex] Flagging of dangerous method call
*   [#163](https://github.com/pmd/pmd/pull/163): \[apex] Flagging of System.debug
*   [#165](https://github.com/pmd/pmd/pull/165): \[apex] Improving open redirect rule to avoid test classes/methods
*   [#167](https://github.com/pmd/pmd/pull/167): \[apex] GC and thread safety changes
*   [#169](https://github.com/pmd/pmd/pull/169): \[apex] Improving detection for DML with inline new object
*   [#170](https://github.com/pmd/pmd/pull/170): \[core] Ant Task Formatter encoding issue with XMLRenderer
*   [#172](https://github.com/pmd/pmd/pull/172): \[apex] Bug fix, detects both Apex fields and class members
*   [#175](https://github.com/pmd/pmd/pull/175): \[apex] ApexXSSFromURLParam: Adding missing casting methods
*   [#176](https://github.com/pmd/pmd/pull/176): \[apex] Bug fix for FP: open redirect for strings prefixed with / is safe
*   [#179](https://github.com/pmd/pmd/pull/179): \[apex] Legacy test class declaration support
*   [#181](https://github.com/pmd/pmd/pull/181): \[apex] Control flow based CRUD rule checking
*   [#184](https://github.com/pmd/pmd/pull/184): \[apex] Improving open redirect detection for static fields & assignment operations
*   [#189](https://github.com/pmd/pmd/pull/189): \[apex] Bug fix of SOQL concatenated vars detection
*   [#191](https://github.com/pmd/pmd/pull/191): \[apex] Detection of sharing violation when Database. methods are used
*   [#192](https://github.com/pmd/pmd/pull/192): \[apex] Dead code removal
*   [#200](https://github.com/pmd/pmd/pull/200): \[javascript] Templatestring grammar fix
*   [#204](https://github.com/pmd/pmd/pull/204): \[apex] Sharing violation SOQL detection bug fix
*   [#214](https://github.com/pmd/pmd/pull/214): \[apex] Sharing violation improving reporting of the correct node, de-duping


## 05-November-2016 - 5.5.2

**Summary:**

*   1 new language for CPD: Groovy
*   1 new rule: plsql-strictsyntax/MisplacedPragma
*   12 pull requests
*   17 bug fixes

**New Supported Languages:**

*   CPD now supports Groovy. See [PR#107](https://github.com/pmd/pmd/pull/107).

**Feature Requests and Improvements:**

*   plsql
    *   [#1539](https://sourceforge.net/p/pmd/bugs/1539/): \[plsql] Create new rule for strict syntax checking: MisplacedPragma

**New Rules:**

*   New Rules for plsql
    *   plsql-strictsyntax: MisplacedPragma

**Pull Requests:**

*   [#106](https://github.com/pmd/pmd/pull/106): \[java] CPD: Keep constructor names under ignoreIdentifiers
*   [#107](https://github.com/pmd/pmd/pull/107): \[groovy] Initial support for CPD Groovy
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#114](https://github.com/pmd/pmd/pull/114): \[core] Remove multihreading workaround for JRE5, as no PMD version supports running on JRE5 anymore
*   [#115](https://github.com/pmd/pmd/pull/115): \[java] Simplify lambda parsing
*   [#116](https://github.com/pmd/pmd/pull/116): \[core] \[java] Improve collection usage
*   [#117](https://github.com/pmd/pmd/pull/117): \[java] Improve symboltable performance
*   [#118](https://github.com/pmd/pmd/pull/118): \[java] Simplify VariableDeclaratorId parsing
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   apex-apexunit
    *   [#1521](https://sourceforge.net/p/pmd/bugs/1521/): \[apex] ApexUnitTestClassShouldHaveAsserts: Parsing error on APEX class: expected one element but was: <BlockStatement, BlockStatement>
*   Java
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
    *   [#1490](https://sourceforge.net/p/pmd/bugs/1490/): \[java] PMD Error while processing - NullPointerException
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/SingularField
    *   [#1494](https://sourceforge.net/p/pmd/bugs/1494/): \[java] SingularField: lombok.Data false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-logging-java
    *   [#1500](https://sourceforge.net/p/pmd/bugs/1500/) \[java] InvalidSlf4jMessageFormat: doesn't ignore exception param
    *   [#1509](https://sourceforge.net/p/pmd/bugs/1509/) \[java] InvalidSlf4jMessageFormat: NPE
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   PLSQL
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1517](https://sourceforge.net/p/pmd/bugs/1517/): \[java] CPD reports on Java constructors when using ignoreIdentifiers


## 27-July-2016 - 5.5.1

**New Rules:**

*   New rules for Salesforce.com Apex:
    *   apex-apexunit: ApexUnitTestClassShouldHaveAsserts, ApexUnitTestShouldNotUseSeeAllDataTrue

**Pull Requests:**

*   [#101](https://github.com/pmd/pmd/pull/101): \[java] Improve multithreading performance: do not lock on classloader
*   [#102](https://github.com/pmd/pmd/pull/102): \[apex] Restrict AvoidLogicInTrigger rule to max. 1 violation per file
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#104](https://github.com/pmd/pmd/pull/104): \[core] \[java] Close opened file handles
*   [apex #43](https://github.com/Up2Go/pmd/pull/43): \[apex] Basic apex unit test rules

**Bugfixes:**

*   Apex
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles


## 25-June-2016 - 5.5.0

**System requirements:**

PMD and CPD need at least a java7 runtime environment. For analyzing Salesforce.com Apex source code,
you'll need a java8 runtime environment.

**New Supported Languages:**

*   Salesforce.com Apex is now supported by PMD and CPD. See [PR#86](https://github.com/pmd/pmd/pull/86).
*   CPD now supports Perl. See [PR#82](https://github.com/pmd/pmd/pull/82).
*   CPD now supports Swift. See [PR#33](https://github.com/adangel/pmd/pull/33).

**New and modified Rules:**

*   New rules in Java:
    *   java-logging-java/InvalidSlf4jMessageFormat: Check for invalid message format in slf4j loggers.
        See [PR#73](https://github.com/pmd/pmd/pull/73).
    *   java-design/ConstantsInInterface: Avoid constants in interfaces.
        Interfaces should define types, constants are implementation details
        better placed in classes or enums. See Effective Java, item 19.
        See [PR#93](https://github.com/pmd/pmd/pull/93).

*   Modified rules in Java:
    *   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
        for *serialVersionUID* fields. By default, no comment is required for this field.
    *   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
        with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

*   New rules for Salesforce.com Apex:
    *   apex-complexity: AvoidDeeplyNestedIfStmts, ExcessiveParameterList, ExcessiveClassLength,
        NcssMethodCount, NcssTypeCount, NcssConstructorCount, StdCyclomaticComplexity,
        TooManyFields, ExcessivePublicCount
    *   apex-performance: AvoidDmlStatementsInLoops, AvoidSoqlInLoops
    *   apex-style: VariableNamingConventions, MethodNamingConventions, ClassNamingConventions,
        MethodWithSameNameAsEnclosingClass, AvoidLogicInTrigger, AvoidGlobalModifier

*   Javascript
    *   New Rule: ecmascript-unnecessary/NoElseReturn: The else block in a if-else-construct is
        unnecessary if the `if` block contains a return. Then the content of the else block can be
        put outside. See [#1486](https://sourceforge.net/p/pmd/bugs/1486/).

**Improvements and CLI changes:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).
*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): \[core] \[java] Provide backwards compatibility for PMD configuration file
*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)
*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.
*   PMD: New command line parameter: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   PMD: New command line parameter: `-filelist`- this provides an alternative way to define, which
    files should be process by PMD. With this option, you can provide the path to a single file containing a comma
    delimited list of files to analyze. If this is given, then you don't need to provide `-dir`.
    See [PR#98](https://github.com/pmd/pmd/pull/98).

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): \[cs] Added option to exclude C# using directives from CPD analysis
*   [#27](https://github.com/adangel/pmd/pull/27): \[cpp] Added support for Raw String Literals (C++11).
*   [#29)(https://github.com/adangel/pmd/pull/29): \[jsp] Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): \[core] CPD: Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): \[core] CPD: Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): \[objectivec] Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): \[swift] Added support for Swift to CPD.
*   [#34](https://github.com/adangel/pmd/pull/34): multiple code improvements: squid:S1192, squid:S1118, squid:S1066, squid:S1854, squid:S2864
*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#72](https://github.com/pmd/pmd/pull/72): \[java] \[jsp] Added capability in Java and JSP parser for tracking tokens.
*   [#73](https://github.com/pmd/pmd/pull/73): \[java] InvalidSlf4jMessageFormat: Add rule to look for invalid message format in slf4j loggers
*   [#74](https://github.com/pmd/pmd/pull/74): \[java] CommentDefaultAccessModifier: Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): \[core] RuleSetFactory Performance Enhancement
*   [#76](https://github.com/pmd/pmd/pull/76): \[java] DoNotCallGarbageCollectionExplicitly: fix formatting typos in an example
*   [#77](https://github.com/pmd/pmd/pull/77): \[java] \[plsql] Fix various typos
*   [#78](https://github.com/pmd/pmd/pull/78): \[java] MissingStaticMethodInNonInstantiatableClass: Add Builder pattern check
*   [#79](https://github.com/pmd/pmd/pull/79): \[java] UseVarargs: do not flag public static void main(String[]), ignore @Override
*   [#80](https://github.com/pmd/pmd/pull/80): \[site] Update mvn-plugin.md
*   [#82](https://github.com/pmd/pmd/pull/82): \[perl] Add Perl support to CPD.
*   [#83](https://github.com/pmd/pmd/pull/83): \[core] CodeClimateRenderer: Adds new Code Climate-compliant JSON renderer
*   [#84](https://github.com/pmd/pmd/pull/84): \[java] EmptyMethodInAbstractClassShouldBeAbstract: Change rule's description.
*   [#85](https://github.com/pmd/pmd/pull/85): \[java] UseStringBufferForStringAppends: False Positive with Ternary Operator (#1340)
*   [#86](https://github.com/pmd/pmd/pull/86): \[apex] Added language module for Salesforce.com Apex incl. rules ported from Java and new ones.
*   [#87](https://github.com/pmd/pmd/pull/87): \[core] \[apex] Customize Code Climate Json "categories" + "remediation_points" as PMD rule properties
*   [#88](https://github.com/pmd/pmd/pull/88): \[core] \[apex] Fixed typo in ruleset.xml and problems with the CodeClimate renderer
*   [#89](https://github.com/pmd/pmd/pull/89): \[core] Some code enhancements
*   [#90](https://github.com/pmd/pmd/pull/90): \[core] Refactored two test to stop using the deprecated ant class BuildFileTest
*   [#91](https://github.com/pmd/pmd/pull/91): \[core] \[java] \[jsp] \[plsql] \[test] \[vm] Small code enhancements, basically reordering variable declarations, constructors and variable modifiers
*   [#92](https://github.com/pmd/pmd/pull/92): \[core] \[apex] Improved Code Climate Renderer Output and a Bugfix for Apex StdCyclomaticComplexityRule on triggers
*   [#93](https://github.com/pmd/pmd/pull/93): \[java] ConstantsInInterface: Add ConstantsInInterface rule. Effective Java, 19
*   [#94](https://github.com/pmd/pmd/pull/94): \[core] \[apex] Added property, fixed code climate renderer output and deleted unused rulessets
*   [#95](https://github.com/pmd/pmd/pull/95): \[apex] AvoidDmlStatementsInLoops: New apex rule AvoidDmlStatementsInLoops
*   [#96](https://github.com/pmd/pmd/pull/96): \[core] CodeClimateRenderer: Clean up Code Climate renderer
*   [#97](https://github.com/pmd/pmd/pull/97): \[java] BooleanGetMethodName: Don't report bad method names on @Override
*   [#98](https://github.com/pmd/pmd/pull/98): \[core] PMD: Input filelist parameter
*   [#99](https://github.com/pmd/pmd/pull/99): \[apex] Fixed Trigger name is reported incorrectly
*   [#100](https://github.com/pmd/pmd/pull/100): \[core] CSVRenderer: escape filenames with commas in csvrenderer

**Bugfixes:**

*   java-basic
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): \[java] DoubleCheckedLocking: False positives
    *   [#1424](https://sourceforge.net/p/pmd/bugs/1424/): \[java] SimplifiedTernary: False positive with ternary operator
*   java-codesize
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): \[java] TooManyMethods: counts inner class methods
*   java-comments
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): \[java] CommentDefaultAccessModifier: triggers on field
        annotated with @VisibleForTesting
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): \[java] CommentRequired: raises violation on serialVersionUID field
*   java-controversial
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): \[java] AvoidUsingShortType: false positive when casting a variable to short
*   java-design
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): \[java] AccessorClassGenerationRule: ArrayIndexOutOfBoundsException with Annotations
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): \[java] CloseResource: false positive on Statement
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): \[java] UseNotifyAllInsteadOfNotify: false positive
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): \[java] UseUtilityClass: can't correctly check functions with multiple annotations
*   java-finalizers
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): \[java] AvoidCallingFinalize: NPE
*   java-imports
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): \[java] UnnecessaryFullyQualifiedName: false positive on clashing static imports with enums
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): \[java] UnusedImports: False Positve with javadoc @link
*   java-junit
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): \[java] JUnitAssertionsShouldIncludeMessage: is no longer compatible with TestNG
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): \[java] TestClassWithoutTestCases: false positive
*   java-migrating
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): \[java] JUnit4TestShouldUseBeforeAnnotation: False positive when TestNG is used
*   java-naming
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): \[java] SuspiciousEqualsMethodName: false positive
*   java-optimizations
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): \[java] RedundantFieldInitializer: False positive for small floats
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-sunsecure
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): \[java] ArrayIsStoredDirectly: False positive
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): \[java] MethodReturnsInternalArray: False positive
*   java-unnecessary
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): \[java] UnnecessaryFinalModifier: false positive on a @SafeVarargs method
    *   [#1422](https://sourceforge.net/p/pmd/bugs/1422/): \[java] UselessQualifiedThis: False positive with Java 8 Function
*   java-unusedcode
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): \[java] UnusedFormalParameter: should ignore overriding methods
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): \[java] UnusedLocalVariable: false positive - parenthesis
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): \[java] UnusedModifier: false positive on public modifier used with inner interface in enum
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): \[java] UnusedPrivateField: False positive when local variable hides member variable
        hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): \[core] XMLRenderer: Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): \[java] Parser Error: Cast in return expression
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): \[site] PMD: Update documentation how to compile after modularization
    *   [#1442](https://sourceforge.net/p/pmd/bugs/1442/): \[java] PDMASMClassLoader: Java 9 Jigsaw readiness
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): \[java] Parser: PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): \[xml] Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): \[core] RuleSetFactory: Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): \[java] Parser: Error with type-bound lambda
    *   [#1478](https://sourceforge.net/p/pmd/bugs/1478/): \[core] PMD CLI: Use first language as default if Java is not available
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): \[core] CPD: no problems found results in blank file instead of empty xml
    *   [#1485](https://sourceforge.net/p/pmd/bugs/1485/): \[apex] Analysis of some apex classes cause a stackoverflow error
    *   [#1488](https://sourceforge.net/p/pmd/bugs/1488/): \[apex] Windows line endings falsify the location of issues
    *   [#1491](https://sourceforge.net/p/pmd/bugs/1491/): \[core] CodeClimateRenderer: corrupt JSON output with real line breaks
    *   [#1492](https://sourceforge.net/p/pmd/bugs/1492/): \[core] PMD CLI: IncompatibleClassChangeError when running PMD


## 27-March-2017 - 5.4.6

The PMD team is pleased to announce PMD 5.4.6.

This is a bug fixing release.

### Table Of Contents

* [Fixed Issues](#Fixed_Issues)
* [External Contributions](#External_Contributions)

### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java:
    *   [#309](https://github.com/pmd/pmd/issues/309): \[java] Parse error on method reference
*   java-design:
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
*   java-junit:
    *   [#285](https://github.com/pmd/pmd/issues/285): \[java] JUnitTestsShouldIncludeAssertRule should support @Rule as well as @Test(expected = ...)
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### External Contributions

*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty()
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule
*   [#308](https://github.com/pmd/pmd/pull/308): \[java] JUnitTestsShouldIncludeAssertRule supports @Rule annotated ExpectedExceptions


## 25-Februar-2017 - 5.4.5

The PMD team is pleased to announce PMD 5.4.5

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    *   [Modified Rules](#Modified_Rules)
* [Fixed Issues](#Fixed_Issues)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

### Fixed Issues

*   general
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive


### External Contributions

*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation


## 28-January-2017 - 5.4.4

The PMD team is pleased to announce PMD 5.4.4

This is a bug fixing release. The most significant changes are on analysis performance.

Multithread performance has been enhanced by reducing thread-contention on a
bunch of areas. This is still an area of work, as the speedup of running
multithreaded analysis is still relatively small (4 threads produce less
than a 50% speedup). Future releases will keep improving on this area.

Once again, *Symbol Table* has been an area of great performance improvements.
This time we were able to further improve it's performance by roughly 10% on all
supported languages. In *Java* in particular, several more improvements were possible,
improving *Symbol Table* performance by a whooping 30%, that's over 5X faster
than PMD 5.4.2, when we first started working on it.

Java developers will also appreciate the revamp of `CloneMethodMustImplementCloneable`,
making it over 500X faster, and `PreserveStackTrace` which is now 7X faster.

### New and noteworthy

This is a bug fixing release, no major changes were introduced.

#### Modified Rules

The Java rule "UseLocaleWithCaseConversions" (ruleset java-design) has been modified, to detect calls
to `toLowerCase` and to `toUpperCase` also within method call chains. This leads to more detected cases
and potentially new false positives.
See also [bugfix #1556](https://sourceforge.net/p/pmd/bugs/1556/).


### Fixed Issues

*   java
    *   [#206](https://github.com/pmd/pmd/issues/206): \[java] Parse error on annotation fields with generics
    *   [#207](https://github.com/pmd/pmd/issues/207): \[java] Parse error on method reference with generics
    *   [#208](https://github.com/pmd/pmd/issues/208): \[java] Parse error with local class with 2 or more annotations
    *   [#213](https://github.com/pmd/pmd/issues/213): \[java] CPD: OutOfMemory when analyzing Lucene
*   java-design
    *   [#1448](https://sourceforge.net/p/pmd/bugs/1448/): \[java] ImmutableField: Private field in inner class gives false positive with lambdas
    *   [#1495](https://sourceforge.net/p/pmd/bugs/1495/): \[java] UnnecessaryLocalBeforeReturn with assert
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
    *   [#1556](https://sourceforge.net/p/pmd/bugs/1556/): \[java] UseLocaleWithCaseConversions does not works with `ResultSet` (false negative)
    *   [#177](https://github.com/pmd/pmd/issues/177): \[java] SingularField with lambdas as final fields
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
    *   [#1555](https://sourceforge.net/p/pmd/bugs/1555/): \[java] UnnecessaryFullyQualifiedName: Really necessary fully qualified name
*   java-unnecessary
    *   [#199](https://github.com/pmd/pmd/issues/199): \[java] UselessParentheses: Parentheses in return statement are incorrectly reported as useless
*   java-strings
    *   [#202](https://github.com/pmd/pmd/issues/202): \[java] \[doc] ConsecutiveAppendsShouldReuse is not really an optimization
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct
*   javascript
    *   [#201](https://github.com/pmd/pmd/issues/201): \[javascript] template strings are not correctly parsed
*   General
    *   [#1511](https://sourceforge.net/p/pmd/bugs/1511/): \[core] Inconsistent behavior of Rule.start/Rule.end


### External Contributions

*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#170](https://github.com/pmd/pmd/pull/170): \[core] Ant Task Formatter encoding issue with XMLRenderer
*   [#200](https://github.com/pmd/pmd/pull/200): \[javascript] Templatestring grammar fix


## 04-November-2016 - 5.4.3

**Summary:**

*   7 pull requests
*   16 bug fixes

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] CyclomaticComplexity rule causes OOM when class reporting is disabled
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
    *   [#1490](https://sourceforge.net/p/pmd/bugs/1490/): \[java] PMD Error while processing - NullPointerException
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/SingularField
    *   [#1494](https://sourceforge.net/p/pmd/bugs/1494/): \[java] SingularField: lombok.Data false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   PLSQL
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles


## 29-May-2016 - 5.4.2

**New Supported Languages:**

*   CPD supports now Swift (see [PR#33](https://github.com/adangel/pmd/pull/33)).

**Feature Request and Improvements:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).
*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**Modified Rules:**

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
    with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

**Pull Requests:**

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): Added support for Swift to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer
*   [#85](https://github.com/pmd/pmd/pull/85): #1340 UseStringBufferForStringAppends False Positive with Ternary Operator

**Bugfixes:**

*   java-basic/DoubleCheckedLocking:
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): False positives for DoubleCheckedLocking
*   java-basic/SimplifiedTernary:
    *   [#1424](https://sourceforge.net/p/pmd/bugs/1424/): False positive with ternary operator
*   java-codesize/TooManyMethods:
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): TooManyMethods counts inner class methods
*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): CloseResource false positive on Statement
*   java-design/UseUtilityClass:
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): UseUtilityClass can't correctly check functions with multiple annotations
*   java-imports/UnusedImports:
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): False Positve UnusedImports with javadoc @link
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   java-optimizations/UseStringBufferForStringAppends:
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): UseStringBufferForStringAppends False Positive with ternary operator
*   java-sunsecure/ArrayIsStoredDirectly:
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): False positive of MethodReturnsInternalArray
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): False positive of ArrayIsStoredDirectly
*   java-unnecessary/UnnecessaryFinalModifier:
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): UnnecessaryFinalModifier false positive on a @SafeVarargs method
*   java-unusedcode/UnusedFormalParameter:
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): UnusedFormalParameter should ignore overriding methods
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): UnusedLocalVariable - false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): false positive on public modifier used with inner interface in enum
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): Error with type-bound lambda
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): no problems found results in blank file instead of empty xml

**CLI Changes:**

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)
*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.


## 04-December-2015 - 5.4.1

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**Modified Rules:**

*   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
    for *serialVersionUID* fields. By default, no comment is required for this field.

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#74](https://github.com/pmd/pmd/pull/74): Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-comments/CommentDefaultAccessModifier
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): CommentDefaultAccessModifier triggers on field
        annotated with @VisibleForTesting
*   java-comments/CommentRequired
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): CommentRequired raises violation on serialVersionUID field
*   java-design/UseNotifyAllInsteadOfNotify
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): UseNotifyAllInsteadOfNotify gives false positive
*   java-finalizers/AvoidCallingFinalize
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): NPE in AvoidCallingFinalize
*   java-imports/UnnecessaryFullyQualifiedName
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): UnnecessaryFullyQualifiedName false positive on clashing static imports with enums
*   java-junit/JUnitAssertionsShouldIncludeMessage
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): JUnitAssertionsShouldIncludeMessage is no longer compatible with TestNG
*   java-migrating/JUnit4TestShouldUseBeforeAnnotation
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): False positive with JUnit4TestShouldUseBeforeAnnotation when TestNG is used
*   java-naming/SuspiciousEqualsMethodName
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): SuspiciousEqualsMethodName false positive
*   java-optimizations/RedundantFieldInitializer
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): RedundantFieldInitializer: False positive for small floats
*   java-unnecessary/UselessQualifiedThis
    *   [#1422](https://sourceforge.net/p/pmd/bugs/1422/): UselessQualifiedThis: False positive with Java 8 Function
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable
        hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): PMD: Update documentation how to compile after modularization


## 04-October-2015 - 5.4.0


<div style="border: 1px solid red; border-radius: 5px; border-left-width: 10px; padding: 5px 1em; background-color: lightyellow;">
<strong>Note</strong>: PMD 5.4.0 requires JDK 1.7 or above.
</div>

**Summary:**

* 9 new rules
* 4 features requests
* 18 pull requests


**Feature Request and Improvements:**

*   [#1344](https://sourceforge.net/p/pmd/bugs/1344/): AbstractNaming should check reverse
*   [#1361](https://sourceforge.net/p/pmd/bugs/1361/): ShortVariable and ShortMethodName configuration
*   [#1414](https://sourceforge.net/p/pmd/bugs/1414/): Command line parameter to disable "failOnViolation" behavior
    PMD and CPD Command Line Interfaces have a new optional parameter: `failOnViolation`. Executing PMD with the option
    `-failOnViolation false` will perform the PMD checks but won't fail the build and still exit with status 0.
    This is useful if you only want to generate the report with violations but don't want to fail your build.
*   [#1420](https://sourceforge.net/p/pmd/bugs/1420/): UnusedPrivateField: Ignore fields if using lombok

**New Rules:**

*   Java:

    *   Basic: **SimplifiedTernary** (rulesets/java/basic.xml/SimplifiedTernary)<br/>
        Ternary operator with a boolean literal can be simplified with a boolean
        expression.

    *   Clone: **CloneMethodMustBePublic** (rulesets/java/clone.xml/CloneMethodMustBePublic)<br/>
        The java manual says "By convention,
        classes that implement the `Cloneable` interface should override `Object.clone` (which is protected)
        with a public method."

    *   Clone: **CloneMethodReturnTypeMustMatchClassName** (rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName)<br/>
        If a class implements `Cloneable`
        the return type of the method `clone()` must be the class name.

    *   Comments: **CommentDefaultAccessModifier** (rulesets/java/comments.xml/CommentDefaultAccessModifier)<br/>
        In order to avoid mistakes with
        forgotten access modifiers for methods, this rule ensures, that you explicitly mark the usage of the
        default access modifier by placing a comment.

    *   Design: **SingletonClassReturningNewInstance** (rulesets/java/design.xml/SingletonClassReturningNewInstance)<br/>
        Verifies that the method called `getInstance` returns a cached instance and not always a fresh, new instance.

    *   Design: **SingleMethodRule** (rulesets/java/design.xml/SingleMethodSingletonRule)<br/>
        Verifies that there is only one method called
        `getInstance`. If there are more methods that return the singleton, then it can easily happen, that these
        are not the same instances - and thus no singleton.

    *   Unnecessary: **UselessQualifiedThis** (rulesets/java/unnecessary.xml/UselessQualifiedThis)<br/>
        Flags unnecessary qualified usages
        of this, when `this` alone would be unique. E.g. use just `this` instead of `Foo.this`.

*   Maven POM: (The rules can be found in the *pmd-xml* module)

    *   Basic: **ProjectVersionAsDependencyVersion** (rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion)<br/>
        Checks the usage of `${project.version}` in Maven POM files.

    *   Basic: **InvalidDependencyTypes** (rulesets/pom/basic.xml/InvalidDependencyTypes)<br/>
        Verifies that only the default types (jar, war, ...) for dependencies are used.

Ruleset snippet to activate the new rules:

    <rule ref="rulesets/java/basic.xml/SimplifiedTernary"/>
    <rule ref="rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName"/>
    <rule ref="rulesets/java/clone.xml/CloneMethodMustBePublic"/>
    <rule ref="rulesets/java/comments.xml/CommentDefaultAccessModifier"/>
    <rule ref="rulesets/java/design.xml/SingleMethodSingleton"/>
    <rule ref="rulesets/java/design.xml/SingletonClassReturningNewInstance"/>
    <rule ref="rulesets/java/unnecessary.xml/UselessQualifiedThis"/>

    <rule ref="rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion"/>
    <rule ref="rulesets/pom/basic.xml/InvalidDependencyTypes"/>


**Modified Rules:**

*   Java

    *   Basic: **CheckResultSet** (rulesets/java/basic.xml/CheckResultSet)<br/>
        Do not require to check the result of a navigation method, if it is returned.

    *   JUnit: **UseAssertTrueInsteadOfAssertEquals** (rulesets/java/junit.xml/UseAssertTrueInsteadOfAssertEquals)<br/>
        This rule also flags assertEquals, that use Boolean.TRUE/FALSE constants.

    *   Naming: **AbstractNaming** (rulesets/java/naming.xml/AbstractNaming)<br/>
        By default, this rule flags now classes,
        that are named "Abstract" but are not abstract. This behavior can be disabled by setting
        the new property `strict` to false.

    *   Naming: **ShortMethodName** (rulesets/java/naming.xml/ShortMethodName)<br/>
        Additional property `minimum` to configure the minimum required length of a method name.

    *   Naming: **ShortVariable** (rulesets/java/naming.xml/ShortVariable)<br/>
        Additional property `minimum` to configure the minimum required length of a variable name.

    *   UnusedCode: **UnusedPrivateField** (rulesets/java/unusedcode.xml/UnusedPrivateField)<br/>
        This rule won't trigger anymore if [Lombok](https://projectlombok.org) is in use.
        See [#1420](https://sourceforge.net/p/pmd/bugs/1420/).

**Renamed Rules:**

*   Java
    *   Design: **<del>UseSingleton</del>** - **UseUtilityClass** (rulesets/java/design.xml/UseUtilityClass)<br/>
        The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
        See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

**Removed Rules:**

*   Java
    *   Basic: The following rules of ruleset "Basic" were marked as deprecated and are removed with this release now:<br/>
        <br/>
        EmptyCatchBlock, EmptyIfStatement, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptySwitchStatements, EmptySynchronizedBlock, EmptyStatementNotInLoop, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer
        <br/><br/>
        UnnecessaryConversionTemporary, UnnecessaryReturn, UnnecessaryFinalModifier, UselessOverridingMethod, UselessOperationOnImmutable, UnusedNullCheckInEquals, UselessParentheses
        <br/><br/>
        These rules are still available in the rulesets "Empty" (rulesets/java/empty.xml) and
        "Unnecessary" (rulesets/java/unnecessary.xml) respectively.

    *   Design: The rule "UncommentedEmptyMethod" has been renamed last release to "UncommentedEmptyMethodBody". The
        old rule name reference has been removed with this release now.

    *   Controversial: The rule "BooleanInversion" has been deprecated last release
        and has been removed with this release completely.

**Pull Requests:**

*   [#21](https://github.com/adangel/pmd/pull/21): Added PMD Rules for Singleton pattern violations.
*   [#23](https://github.com/adangel/pmd/pull/23): Extended Objective-C grammar to accept Unicode characters in identifiers
*   [#54](https://github.com/pmd/pmd/pull/54): Add a new rulesets for Maven's POM rules
*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces
*   [#56](https://github.com/pmd/pmd/pull/56): Adding support for WSDL rules
*   [#57](https://github.com/pmd/pmd/pull/57): Add default access modifier as comment rule
*   [#58](https://github.com/pmd/pmd/pull/58): Add rule for unnecessary literal boolean in ternary operators
*   [#59](https://github.com/pmd/pmd/pull/59): Add check to Boxed booleans in UseAssertTrueInsteadOfAssertEquals rule
*   [#60](https://github.com/pmd/pmd/pull/60): Add UselessQualifiedThisRule
*   [#61](https://github.com/pmd/pmd/pull/61): Add CloneMethodReturnTypeMustMatchClassName rule
*   [#62](https://github.com/pmd/pmd/pull/62): Add CloneMethodMustBePublic rule
*   [#63](https://github.com/pmd/pmd/pull/63): Change CheckResultSet to allow for the result of the navigation methods to be returned
*   [#65](https://github.com/pmd/pmd/pull/65): Fix ClassCastException in UselessOverridingMethodRule.
*   [#66](https://github.com/pmd/pmd/pull/66): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#67](https://github.com/pmd/pmd/pull/67): Use Path instead of string to check file exclusions to fix windows-only bug
*   [#68](https://github.com/pmd/pmd/pull/68): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#69](https://github.com/pmd/pmd/pull/69): #1371 InsufficientStringBufferDeclaration not detected properly on StringBuffer
*   [#70](https://github.com/pmd/pmd/pull/70): Fix code example


**Bugfixes:**

*   java-unusedcode/UnusedPrivateMethod:
    *   [#1412](https://sourceforge.net/p/pmd/bugs/1412/): UnusedPrivateMethod false positive: Issue #1403 not completely solved

**API Changes:**

*   pmd requires now JDK 1.7 or above.

*   pmd-core: `net.sourceforge.pmd.lang.symboltable.Scope`:

    The method `addNameOccurrence` returns now a Set of
    NameDeclarations to which the given occurrence has been added. This is useful in case there are ambiguous declarations
    of methods.

*   pmd-core: `net.sourceforge.pmd.lang.symboltable.AbstractScope`:

    The method `findVariableHere` returns now
    a Set of NameDeclarations which match the given occurrence.  This is useful in case there are ambiguous declarations
    of methods.


## 04-November-2016 - 5.3.8

**Summary**

*   1 feature requests
*   6 pull requests
*   17 bug fixes

**Feature Requests and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): \[core] \[java] Provide backwards compatibility for PMD configuration file

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): \[javascript] Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): \[java] CloseResource: false positive on Statement
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): \[java] UnusedLocalVariable: false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): \[java] UnusedModifier: false positive on public modifier used with inner interface in enum
*   plsql
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): \[core] CPD: no problems found results in blank file instead of empty xml
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)


## 30-April-2016 - 5.3.7

**New Supported Languages:**

*   CPD supports now Swift (see [PR#33](https://github.com/adangel/pmd/pull/33)).

**Feature Request and Improvements:**

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).

**Modified Rules:**

*   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated
    with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).

**Pull Requests:**

*   [#27](https://github.com/adangel/pmd/pull/27): Added support for Raw String Literals (C++11).
*   [#29](https://github.com/adangel/pmd/pull/29): Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): Removed file filter for files that are explicitly specified on the CPD command line using the '--files' command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): Added support for Swift to CPD.
*   [#79](https://github.com/pmd/pmd/pull/79): do not flag public static void main(String[]) as UseVarargs; ignore @Override for UseVarargs
*   [#80](https://github.com/pmd/pmd/pull/80): Update mvn-plugin.md
*   [#83](https://github.com/pmd/pmd/pull/83): Adds new Code Climate-compliant JSON renderer
*   [#85](https://github.com/pmd/pmd/pull/85): #1340 UseStringBufferForStringAppends False Positive with Ternary Operator

**Bugfixes:**

*   java-basic/DoubleCheckedLocking:
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): False positives for DoubleCheckedLocking
*   java-codesize/TooManyMethods:
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): TooManyMethods counts inner class methods
*   java-controversial/AvoidUsingShortType:
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): false positive when casting a variable to short
*   java-design/AccessorClassGeneration:
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): ArrayIndexOutOfBoundsException with Annotations for AccessorClassGenerationRule
*   java-design/UseUtilityClass:
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): UseUtilityClass can't correctly check functions with multiple annotations
*   java-imports/UnusedImports:
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): False Positve UnusedImports with javadoc @link
*   java-junit/TestClassWithoutTestCases:
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): Test Class Without Test Cases gives false positive
*   java-optimizations/UseStringBufferForStringAppends:
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): UseStringBufferForStringAppends False Positive with ternary operator
*   java-sunsecure/ArrayIsStoredDirectly:
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): False positive of MethodReturnsInternalArray
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): False positive of ArrayIsStoredDirectly
*   java-unnecessary/UnnecessaryFinalModifier:
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): UnnecessaryFinalModifier false positive on a @SafeVarargs method
*   java-unusedcode/UnusedFormalParameter:
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): UnusedFormalParameter should ignore overriding methods
*   General
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): PMD doesn't handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): Error with type-bound lambda

**CLI Changes:**

*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows
    to scan files, that are not using the standard file extension. If a directory is specified, the filename filter
    is still applied and only those files with the correct file extension of the language are scanned.


## 04-December-2015 - 5.3.6

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**Modified Rules:**

*   java-comments/CommentRequired: New property `serialVersionUIDCommentRequired` which controls the comment requirements
    for *serialVersionUID* fields. By default, no comment is required for this field.

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
    *   Note: This also contains the fix from [#23](https://github.com/adangel/pmd/pull/23)
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-comments/CommentRequired
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): CommentRequired raises violation on serialVersionUID field
*   java-design/UseNotifyAllInsteadOfNotify
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): UseNotifyAllInsteadOfNotify gives false positive
*   java-finalizers/AvoidCallingFinalize
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): NPE in AvoidCallingFinalize
*   java-imports/UnnecessaryFullyQualifiedName
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): UnnecessaryFullyQualifiedName false positive on clashing static imports with enums
*   java-junit/JUnitAssertionsShouldIncludeMessage
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): JUnitAssertionsShouldIncludeMessage is no longer compatible with TestNG
*   java-migrating/JUnit4TestShouldUseBeforeAnnotation
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): False positive with JUnit4TestShouldUseBeforeAnnotation when TestNG is used
*   java-naming/SuspiciousEqualsMethodName
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): SuspiciousEqualsMethodName false positive
*   java-optimizations/RedundantFieldInitializer
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): RedundantFieldInitializer: False positive for small floats
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable hides member variable
*   General
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): PMD: Update documentation how to compile after modularization


## 04-October-2015 - 5.3.5

**Modified Rules:**

*   java-design/CloseResource: New Property *closeAsDefaultTarget* which is *true* by default to stay
    backwards compatible. If this property is *true*, the rule will make sure, that `close` itself is
    always considered as a *closeTarget* - no matter whether it is configured with the *closeTargets* property
    or not.

**Pull Requests:**

*   [#71](https://github.com/pmd/pmd/pull/71): #1410 Improve description of DefaultPackage rule

**Bugfixes:**

*   java-controversial/DefaultPackage:
    *   [#1410](https://sourceforge.net/p/pmd/bugs/1410/): DefaultPackage triggers on field annotated with @VisibleForTesting
*   java-design/CloseResource:
    *   [#1387](https://sourceforge.net/p/pmd/bugs/1387/): CloseResource has false positive for ResultSet
*   java-optimizations/RedundantFieldInitializer
    *   [#1418](https://sourceforge.net/p/pmd/bugs/1418/): RedundantFieldInitializer false positive with large long value
*   java-strings/InsufficientStringBufferDeclaration:
    *   [#1409](https://sourceforge.net/p/pmd/bugs/1409/): NullPointerException in InsufficientStringBufferRule
    *   [#1413](https://sourceforge.net/p/pmd/bugs/1413/): False positive StringBuffer constructor with ?: int value
*   java-unnecessary/UselessParentheses:
    *   [#1407](https://sourceforge.net/p/pmd/bugs/1407/): UselessParentheses "&" and "+" operator precedence


## 18-September-2015 - 5.3.4

**Bugfixes:**

*   [#1370](https://sourceforge.net/p/pmd/bugs/1370/): ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#1371](https://sourceforge.net/p/pmd/bugs/1371/): InsufficientStringBufferDeclaration not detected properly on StringBuffer
*   [#1380](https://sourceforge.net/p/pmd/bugs/1380/): InsufficientStringBufferDeclaration false positive when literal string passed to a lookup service
*   [#1384](https://sourceforge.net/p/pmd/bugs/1384/): NullPointerException in ConsecutiveLiteralAppendsRule
*   [#1388](https://sourceforge.net/p/pmd/bugs/1388/): ConstructorCallsOverridableMethodRule doesn't work with params?
*   [#1392](https://sourceforge.net/p/pmd/bugs/1392/): SimplifyStartsWith false-negative
*   [#1393](https://sourceforge.net/p/pmd/bugs/1393/): PMD hanging during DataflowAnomalyAnalysis
*   [#1394](https://sourceforge.net/p/pmd/bugs/1394/): dogfood.xml - Unable to exclude rules [UncommentedEmptyMethod]
*   [#1395](https://sourceforge.net/p/pmd/bugs/1395/): UnusedPrivateMethod false positive for array element method call
*   [#1396](https://sourceforge.net/p/pmd/bugs/1396/): PrematureDeclaration lambda false positive
*   [#1397](https://sourceforge.net/p/pmd/bugs/1397/): StringToString should ignore method references
*   [#1398](https://sourceforge.net/p/pmd/bugs/1398/): False positive for GuardLogStatementJavaUtil with Log4j
*   [#1399](https://sourceforge.net/p/pmd/bugs/1399/): False positive for VariableNamingConventions with annotation @interface
*   [#1400](https://sourceforge.net/p/pmd/bugs/1400/): False positive with JUnit4TestShouldUseBeforeAnnotation
*   [#1401](https://sourceforge.net/p/pmd/bugs/1401/): False positive for StringBuilder.append called with constructor
*   [#1402](https://sourceforge.net/p/pmd/bugs/1402/): Windows-Only: File exclusions are not case insensitive
*   [#1403](https://sourceforge.net/p/pmd/bugs/1403/): False positive UnusedPrivateMethod with JAVA8
*   [#1404](https://sourceforge.net/p/pmd/bugs/1404/): Java8 'Unnecessary use of fully qualified name' in Streams Collector
*   [#1405](https://sourceforge.net/p/pmd/bugs/1405/): UnusedPrivateMethod false positive?


## 25-July-2015 - 5.3.3

**Pull Requests:**

*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces

**Bugfixes:**

*   [#1364](https://sourceforge.net/p/pmd/bugs/1364/): FieldDeclarationsShouldBeAtStartOfClass false positive using multiple annotations
*   [#1365](https://sourceforge.net/p/pmd/bugs/1365/): Aggregated javadoc report is missing
*   [#1366](https://sourceforge.net/p/pmd/bugs/1366/): UselessParentheses false positive on multiple equality operators
*   [#1369](https://sourceforge.net/p/pmd/bugs/1369/): ConsecutiveLiteralAppends not detected properly on StringBuffer
*   [#1372](https://sourceforge.net/p/pmd/bugs/1372/): False Negative for CloseResource rule.
*   [#1375](https://sourceforge.net/p/pmd/bugs/1375/): CloseResource not detected properly
*   [#1376](https://sourceforge.net/p/pmd/bugs/1376/): CompareObjectsWithEquals fails for type annotated method parameter
*   [#1379](https://sourceforge.net/p/pmd/bugs/1379/): PMD CLI: Cannot specify multiple properties
*   [#1381](https://sourceforge.net/p/pmd/bugs/1381/): CPD Cannot use CSV/VS Renderers because they don't support encoding property


## 22-May-2015 - 5.3.2

**Bugfixes:**

*   [#1330](https://sourceforge.net/p/pmd/bugs/1330/): AvoidReassigningParameters does not work with varargs
*   [#1335](https://sourceforge.net/p/pmd/bugs/1335/): GuardLogStatementJavaUtil should not apply to SLF4J Logger
*   [#1342](https://sourceforge.net/p/pmd/bugs/1342/): UseConcurrentHashMap false positive (with documentation example)
*   [#1343](https://sourceforge.net/p/pmd/bugs/1343/): MethodNamingConventions for overrided methods
*   [#1345](https://sourceforge.net/p/pmd/bugs/1345/): UseCollectionIsEmpty throws NullPointerException
*   [#1353](https://sourceforge.net/p/pmd/bugs/1353/): False positive "Only One Return" with lambda
*   [#1354](https://sourceforge.net/p/pmd/bugs/1354/): Complex FieldDeclarationsShouldBeAtStartOfClass false positive with Spring annotations
*   [#1355](https://sourceforge.net/p/pmd/bugs/1355/): NullPointerException in a java file having a single comment line


## 20-April-2015 - 5.3.1

**New/Modified/Deprecated Rules:**

*   Language Java, ruleset design.xml: The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
    See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

**Pull Requests:**

*   [#53](https://github.com/pmd/pmd/pull/53): Fix some NullPointerExceptions

**Bugfixes:**

*   [#1332](https://sourceforge.net/p/pmd/bugs/1332/): False Positive: UnusedPrivateMethod
*   [#1333](https://sourceforge.net/p/pmd/bugs/1333/): Error while processing Java file with Lambda expressions
*   [#1337](https://sourceforge.net/p/pmd/bugs/1337/): False positive "Avoid throwing raw exception types" when exception is not thrown
*   [#1338](https://sourceforge.net/p/pmd/bugs/1338/): The pmd-java8 POM bears the wrong parent module version


## April 1, 2015 - 5.3.0

**New Supported Languages:**

* Matlab (CPD)
* Objective-C (CPD)
* Python (CPD)
* Scala (CPD)

**Feature Requests and Improvements:**

*   XML: Line numbers for XML documents are more accurate. This is a further improvement of [#1054](https://sourceforge.net/p/pmd/bugs/1054/).
*   CPD: New output format 'csv_with_linecount_per_file'
*   [#1320](https://sourceforge.net/p/pmd/bugs/1320/): Enhance SimplifyBooleanReturns checks
*   PMD exits with status `4` if any violations have been found. This behavior has been introduced to ease PMD
    integration into scripts or hooks, such as SVN hooks.

**New/Modified/Deprecated Rules:**

The following rules have been
<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #5CB85C; font-size: 75%;">enhanced</span>
:

*   Language Java, ruleset design.xml: The rule "SimplifyBooleanReturns" now also marks methods where the else case is omitted and just a return.
    See also feature [#1320](https://sourceforge.net/p/pmd/bugs/1320/).

The following rules are marked as
<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">deprecated</span>
and will be removed with the next release of PMD.

*   Language Java, ruleset basic.xml: The following rules have been *moved into the `empty.xml` ruleset*. You'll need
    to enable the "empty" ruleset explicitly from now on, if you want to have these rules executed:

    EmptyCatchBlock, EmptyIfStatement, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptySwitchStatements,
    EmptySynchronizedBlock, EmptyStatementNotInLoop, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer.

*   Language Java, ruleset basic.xml: The following rules have been *moved into the `unnecessary.xml` ruleset*. You'll need
    to enable the "unnecessary" ruleset explicitly from now on, if you want to have these rules executed:

    UnnecessaryConversionTemporary, UnnecessaryReturn, UnnecessaryFinalModifier, UselessOverridingMethod,
    UselessOperationOnImmutable, UnusedNullCheckInEquals, UselessParentheses.

*   Language Java, ruleset design.xml: The rule "UncommentedEmptyMethod" *has been renamed* to "UncommentedEmptyMethodBody".
    See also bug [#1283](https://sourceforge.net/p/pmd/bugs/1283/).

*   Language Java, ruleset controversial.xml: The rule "BooleanInversion" is deprecated and *will be removed* with
    the next release. See [#1277](https://sourceforge.net/p/pmd/bugs/1277/) for more details.

**Pull Requests:**

* [#11](https://github.com/adangel/pmd/pull/11): Added support for Python to CPD.
* [#12](https://github.com/adangel/pmd/pull/12): Added support for Matlab to CPD.
* [#13](https://github.com/adangel/pmd/pull/13): Added support for Objective-C to CPD.
* [#14](https://github.com/adangel/pmd/pull/14): Added support for Scala to CPD.
* [#15](https://github.com/adangel/pmd/pull/15): (pmd-cs) Fixed incorrect line numbers after mutiline comments and verbatim strings.
* [#16](https://github.com/adangel/pmd/pull/16): Fixed several C++ lexical / tokenize errors.
* [#17](https://github.com/adangel/pmd/pull/17): Fixed '--files' command line option of CPD, so it also works for files and not only for directories.
* [#18](https://github.com/adangel/pmd/pull/18): Created extra CSV output format `csv_with_linecount_per_file` which outputs the correct line count per file.
* [#19](https://github.com/adangel/pmd/pull/19): Fixed exit status of PMD when error occurs
* [#48](https://github.com/pmd/pmd/pull/48): Handle NoClassDefFoundError along ClassNotFoundException
* [#49](https://github.com/pmd/pmd/pull/49): Fix some false positives in UnusedPrivateField
* [#50](https://github.com/pmd/pmd/pull/50): Add missing assertions in JUnitAssertionsShouldIncludeMessage test
* [#51](https://github.com/pmd/pmd/pull/51): [JUnit] Check assertion message present in assertEquals with delta
* [#52](https://github.com/pmd/pmd/pull/52): Improves JDK8 support for default methods and static methods in interfaces

**Bugfixes:**

* [#914](https://sourceforge.net/p/pmd/bugs/914/): False +ve from UnusedImports with wildcard static imports
* [#1197](https://sourceforge.net/p/pmd/bugs/1197/): JUnit4TestShouldUseTestAnnotation for private method
* [#1277](https://sourceforge.net/p/pmd/bugs/1277/): Delete BooleanInversion as it makes no sense
* [#1283](https://sourceforge.net/p/pmd/bugs/1283/): Rename UncommentedEmptyMethod to UncommentedEmptyMethodBody
* [#1296](https://sourceforge.net/p/pmd/bugs/1296/): PMD UnusedPrivateMethod invalid detection of 'private void method(int,boolean,Integer...)'
* [#1298](https://sourceforge.net/p/pmd/bugs/1298/): Member variable int type with value 0xff000000 causes processing error
* [#1299](https://sourceforge.net/p/pmd/bugs/1299/): MethodReturnsInternalArray false positive
* [#1302](https://sourceforge.net/p/pmd/bugs/1302/): False Positive: UnusedPrivateField when accessed by inner class
* [#1303](https://sourceforge.net/p/pmd/bugs/1303/): OverrideBothEqualsAndHashcodeRule does not work on class implements resolvable interfaces
* [#1304](https://sourceforge.net/p/pmd/bugs/1304/): UseCollectionIsEmpty false positive comparing to 1
* [#1305](https://sourceforge.net/p/pmd/bugs/1305/): variable declaration inside switch causes ClassCastException
* [#1306](https://sourceforge.net/p/pmd/bugs/1306/): False positive on duplicate when using static imports
* [#1307](https://sourceforge.net/p/pmd/bugs/1307/): False positive: SingularField and lambda-expression
* [#1308](https://sourceforge.net/p/pmd/bugs/1308/): PMD runs endlessly on some generated files
* [#1312](https://sourceforge.net/p/pmd/bugs/1312/): Rule reference must not override rule name of referenced rule
* [#1313](https://sourceforge.net/p/pmd/bugs/1313/): Missing assertion message in assertEquals with delta not detected
* [#1316](https://sourceforge.net/p/pmd/bugs/1316/): Multi Rule Properties with delimiter not possible
* [#1317](https://sourceforge.net/p/pmd/bugs/1317/): RuntimeException when parsing class with multiple lambdas
* [#1319](https://sourceforge.net/p/pmd/bugs/1319/): PMD stops with NoClassDefFoundError (typeresolution)
* [#1321](https://sourceforge.net/p/pmd/bugs/1321/): CPD format XML fails with NullPointer
* [#1322](https://sourceforge.net/p/pmd/bugs/1322/): MethodReturnsInternalArray on private methods
* [#1323](https://sourceforge.net/p/pmd/bugs/1323/): False positive case of UseAssertTrueInsteadOfAssertEquals
* [#1324](https://sourceforge.net/p/pmd/bugs/1324/): MethodReturnsInternalArray false positive with clone()
* [#1325](https://sourceforge.net/p/pmd/bugs/1325/): Inner class declared within a method fails to parse (ClassCastException)
* [#1326](https://sourceforge.net/p/pmd/bugs/1326/): PMD 5.3.0-SNAPSHOT doesn't compile under Windows

**API Changes:**

*   `net.sourceforge.pmd.cpd.Match.iterator()` now returns an iterator of the new type `net.sourceforge.pmd.cpd.Mark` instead
    of TokenEntry. A `Mark` contains all the informations about each single duplication, including the TokenEntry via `Mark.getToken()`.
    This Mark is useful for reporting the correct line count for each duplication. Previously only one line count was available.
    As for some languages CPD can be instructed to ignore comments, the line count could be different in the different files
    for the same duplication.

*   pmd-test: The utility class `StreamUtil` is deprecated. Just use Apache Commons IO Utils instead.


## December 21, 2014 - 5.2.3:

**Feature Requests and Improvements:**

* [#1288](https://sourceforge.net/p/pmd/bugs/1288/): MethodNamingConventions for native should be deactivated
* [#1293](https://sourceforge.net/p/pmd/bugs/1293/): Disable VariableNamingConventions for native methods

**Modified Rules:**

* [Java / Design / UseVarargs](http://pmd.sourceforge.net/pmd-java/rules/java/design.html#UseVarargs): if `byte[]` is used as the last argument, it is ignored and no violation will be reported.
* [Java / Naming / MethodNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#MethodNamingConventions): New property `checkNativeMethods`
* [Java / Naming / VariableNamingConventions](http://pmd.sourceforge.net/pmd-java/rules/java/naming.html#VariableNamingConventions): New property `checkNativeMethodParameters`

**Pull requests:**

* [#45](https://github.com/pmd/pmd/pull/45): #1290 RuleSetReferenceId does not process HTTP(S) correctly.
* [#46](https://github.com/pmd/pmd/pull/46): Allow byte[] as no-vargars last argument
* [#47](https://github.com/pmd/pmd/pull/47): Allow byte[] data and byte data[] as no-varargs last argument

**Bugfixes:**

* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1289](https://sourceforge.net/p/pmd/bugs/1289/): CommentRequired not ignored if javadoc {@inheritDoc} anon inner classes
* [#1290](https://sourceforge.net/p/pmd/bugs/1290/): RuleSetReferenceId does not process HTTP(S) correctly.
* [#1294](https://sourceforge.net/p/pmd/bugs/1294/): False positive UnusedPrivateMethod with public inner enum from another class


## December 3, 2014 - 5.2.2:

**New Parameters for CPD:**

For the language cpp, the following new parameters are supported:

* `--no-skip-blocks`: Disables skipping of code blocks like a pre-processor. This is by default enabled.
* `--skip-blocks-pattern`: Pattern to find the blocks to skip. Start and End pattern separated by "`|`". Default value is "`#if 0|#endif`".

**Bugfixes:**

* [#1090](https://sourceforge.net/p/pmd/bugs/1090/): cpp parser exception with inline asm
* [#1128](https://sourceforge.net/p/pmd/bugs/1128/): CompareObjectsWithEquals False Positive comparing boolean (primitive) values
* [#1254](https://sourceforge.net/p/pmd/bugs/1254/): CPD run that worked in 5.1.2 fails in 5.1.3 with OOM
* [#1276](https://sourceforge.net/p/pmd/bugs/1276/): False positive in UnusedPrivateMethod with inner enum
* [#1280](https://sourceforge.net/p/pmd/bugs/1280/): False Positive in UnusedImports when import used in javadoc
* [#1281](https://sourceforge.net/p/pmd/bugs/1281/): UnusedPrivateMethod incorrectly flagged for methods nested private classes
* [#1282](https://sourceforge.net/p/pmd/bugs/1282/): False Positive with implicit String.valuesOf() (Java)
* [#1285](https://sourceforge.net/p/pmd/bugs/1285/): Prevent to modify the System environment
* [#1286](https://sourceforge.net/p/pmd/bugs/1286/): UnusedPrivateMethod returns false positives for varags and enums


## November 3, 2014 - 5.2.1:

**Bugfixes:**

* [#550](https://sourceforge.net/p/pmd/bugs/550/): False +: MissingBreakInSwitch
* [#1252](https://sourceforge.net/p/pmd/bugs/1252/): net.sourceforge.pmd.lang.ast.TokenMgrError: Lexical error in file xxx.cpp
* [#1253](https://sourceforge.net/p/pmd/bugs/1253/): Document default behaviour when CPD command line arguments "encoding" and "ignoreAnnotations" are not specified
* [#1255](https://sourceforge.net/p/pmd/bugs/1255/): UseUtilityClass false positive with Exceptions
* [#1256](https://sourceforge.net/p/pmd/bugs/1256/): PositionLiteralsFirstInComparisons false positive with Characters
* [#1258](https://sourceforge.net/p/pmd/bugs/1258/): Java 8 Lambda parse error on direct field access
* [#1259](https://sourceforge.net/p/pmd/bugs/1259/): CloseResource rule ignores conditionnals within finally blocks
* [#1261](https://sourceforge.net/p/pmd/bugs/1261/): False positive "Avoid unused private methods" with Generics
* [#1262](https://sourceforge.net/p/pmd/bugs/1262/): False positive for MissingBreakInSwitch
* [#1263](https://sourceforge.net/p/pmd/bugs/1263/): PMD reports CheckResultSet violation in completely unrelated source files.
* [#1272](https://sourceforge.net/p/pmd/bugs/1272/): varargs in methods are causing IndexOutOfBoundException when trying to process files
* [#1273](https://sourceforge.net/p/pmd/bugs/1273/): CheckResultSet false positive in try-with-resources nested in if
* [#1274](https://sourceforge.net/p/pmd/bugs/1274/): ant integration broken with pmd-5.2.0
* [#1275](https://sourceforge.net/p/pmd/bugs/1275/): False positive: UnusedModifier rule for static inner class in enum


## October 17, 2014 - 5.2.0:

**Modularization of the source code:**

The source code of pmd was undergoing a major restructuring. Each language is separated
out into its own module. This reduces the size of the artifacts significantly, if only
one language is needed. It also makes it easier, to add new languages as extensions.

Therefore, the maven coordinates needed to change. In order to just use pmd with java support, you'll need
the following two dependencies:

    <dependency>
        <groupId>net.sourceforge.pmd</groupId>
        <artifactId>pmd-core</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>net.sourceforge.pmd</groupId>
        <artifactId>pmd-java</artifactId>
        <version>5.2.0</version>
    </dependency>

The binary package still contains all languages and can be used as usual. Have a look at
[the central repository](http://search.maven.org/#search|ga|1|g%3Anet.sourceforge.pmd) for available modules.

**New Languages**

* CPD supports now [Go](https://golang.org/).

**Pull requests:**

* [#9](https://github.com/adangel/pmd/pull/9/): New rule: NoUnsanitizedJSPExpressionRule
* [#44](https://github.com/pmd/pmd/pull/44/): Add GoLang support to CPD

**New/Modified Rules:**

* JSP - Basic ruleset:
    * NoUnsanitizedJSPExpression: Using unsanitized JSP expression can lead to Cross Site Scripting (XSS) attacks


## August 31, 2014 - 5.1.3:

**Bugfixes:**

* [#1156](https://sourceforge.net/p/pmd/bugs/1156/): False failure with "Avoid unused private methods"
* [#1187](https://sourceforge.net/p/pmd/bugs/1187/): double variable with AvoidDecimalLiteralsInBigDecimalConstructor
* [#1228](https://sourceforge.net/p/pmd/bugs/1228/): UnusedPrivateMethod returns false positives
* [#1230](https://sourceforge.net/p/pmd/bugs/1230/): UseCollectionIsEmpty gets false positives
* [#1231](https://sourceforge.net/p/pmd/bugs/1231/): No Error Message on Missing Rule
* [#1233](https://sourceforge.net/p/pmd/bugs/1233/): UnusedPrivateMethod: False positive : method called on returned object.
* [#1234](https://sourceforge.net/p/pmd/bugs/1234/): Unused private methods still giving false positives in 5.1.3 snapshot
* [#1235](https://sourceforge.net/p/pmd/bugs/1235/): scope dependencies in POM file
* [#1239](https://sourceforge.net/p/pmd/bugs/1239/): StackOverflowError in AbstractTokenizer.parseString running CPD on >1MB JS file
* [#1241](https://sourceforge.net/p/pmd/bugs/1241/): False+ AvoidProtectedMethodInFinalClassNotExtending
* [#1243](https://sourceforge.net/p/pmd/bugs/1243/): Useless Parentheses False Positive
* [#1245](https://sourceforge.net/p/pmd/bugs/1245/): False Positive for Law of Demeter
* [#1246](https://sourceforge.net/p/pmd/bugs/1246/): False positive from MissingBreakInSwitch
* [#1247](https://sourceforge.net/p/pmd/bugs/1247/): Not able to recognize JDK 8 Static Method References
* [#1249](https://sourceforge.net/p/pmd/bugs/1249/): Regression: UnusedPrivateMethod from 5.0.5 to 5.1.2
* [#1250](https://sourceforge.net/p/pmd/bugs/1250/): Method attribute missing for some violations
* [#1251](https://sourceforge.net/p/pmd/bugs/1251/): UnusedPrivateMethod false positives for boxing & unboxing arguments

**Feature Requests and Improvements:**

* [#1232](https://sourceforge.net/p/pmd/bugs/1232/): Make ShortClassName configurable
* [#1244](https://sourceforge.net/p/pmd/bugs/1244/): FieldDeclarationsShouldBeAtStartOfClass and anonymous classes

**New/Modified Rules:**

* FieldDeclarationsShouldBeAtStartOfClass (ruleset java-design) has a new property called `ignoreAnonymousClassDeclarations`:
  Ignore Field Declarations, that are initialized with anonymous class declarations. This property is enabled by default.
  See [feature #1244](https://sourceforge.net/p/pmd/bugs/1244/).
* ShortClassName (ruleset java-naming) has a new property called `minimum`: Number of characters that are required
  as a minimum for a class name. By default, 5 characters are required - if the class name is shorter, a violation
  will be reported. See [feature #1232](https://sourceforge.net/p/pmd/bugs/1232/).

## July 20, 2014 - 5.1.2:

**Bugfixes:**

* Fixed [bug #1181]: unused import false positive if used as parameter in javadoc only.
* Fixed [bug #1192]: Ecmascript fails to parse this operator " ^= "
* Fixed [bug #1198]: ConfusingTernary does not ignore else if blocks even when property is set
* Fixed [bug #1200]: setRuleSets method javadoc mistype commands instead commas
* Fixed [bug #1201]: Error "Can't find resource null" when ruleset contains spaces after comma
* Fixed [bug #1202]: StackOverflowError in RuleSetReferenceId
* Fixed [bug #1205]: Parse error on lambda with if
* Fixed [bug #1206]: SummaryHTMLRenderer always shows suppressed warnings/violations
* Fixed [bug #1208]: yahtml's outputDir property does not work
* Fixed [bug #1209]: XPath 2.0 following-sibling incorrectly includes context node
* Fixed [bug #1211]: PMD is failing with NPE for rule UseIndexOfChar while analyzing Jdk 8 Lambda expression
* Fixed [bug #1214]: UseCollectionIsEmpty misses some usage
* Fixed [bug #1215]: AvoidInstantiatingObjectsInLoops matches the right side of a list iteration loop
* Fixed [bug #1216]: AtLeastOneConstructor ignores classes with *any* methods
* Fixed [bug #1218]: TooFewBranchesForASwitchStatement misprioritized
* Fixed [bug #1219]: PrimarySuffix/@Image does not work in some cases in xpath 2.0
* Fixed [bug #1223]: UnusedPrivateMethod: Java 8 method reference causing false positives
* Fixed [bug #1224]: GuardDebugLogging broken in 5.1.1 - missing additive statement check in log statement
* Fixed [bug #1226]: False Positive: UnusedPrivateMethod overloading with varargs
* Fixed [bug #1227]: GuardLogStatementJavaUtil doesn't catch log(Level.FINE, "msg" + " msg") calls

[bug #1181]: https://sourceforge.net/p/pmd/bugs/1181/
[bug #1192]: https://sourceforge.net/p/pmd/bugs/1192/
[bug #1198]: https://sourceforge.net/p/pmd/bugs/1198/
[bug #1200]: https://sourceforge.net/p/pmd/bugs/1200/
[bug #1201]: https://sourceforge.net/p/pmd/bugs/1201/
[bug #1202]: https://sourceforge.net/p/pmd/bugs/1202/
[bug #1205]: https://sourceforge.net/p/pmd/bugs/1205/
[bug #1206]: https://sourceforge.net/p/pmd/bugs/1206/
[bug #1208]: https://sourceforge.net/p/pmd/bugs/1208/
[bug #1209]: https://sourceforge.net/p/pmd/bugs/1209/
[bug #1211]: https://sourceforge.net/p/pmd/bugs/1211/
[bug #1214]: https://sourceforge.net/p/pmd/bugs/1214/
[bug #1215]: https://sourceforge.net/p/pmd/bugs/1215/
[bug #1216]: https://sourceforge.net/p/pmd/bugs/1216/
[bug #1218]: https://sourceforge.net/p/pmd/bugs/1218/
[bug #1219]: https://sourceforge.net/p/pmd/bugs/1219/
[bug #1223]: https://sourceforge.net/p/pmd/bugs/1223/
[bug #1224]: https://sourceforge.net/p/pmd/bugs/1224/
[bug #1226]: https://sourceforge.net/p/pmd/bugs/1226/
[bug #1227]: https://sourceforge.net/p/pmd/bugs/1227/

**Feature Requests and Improvements:**

* [#1203]: Make GuardLogStatementJavaUtil configurable
* [#1213]: AvoidLiteralsInIfCondition -- switch for integer comparison with 0
* [#1217]: SystemPrintln always says "System.out.print is used"
* [#1221]: OneDeclarationPerLine really checks for one declaration each statement

[#1203]: https://sourceforge.net/p/pmd/bugs/1203/
[#1213]: https://sourceforge.net/p/pmd/bugs/1213/
[#1217]: https://sourceforge.net/p/pmd/bugs/1217/
[#1221]: https://sourceforge.net/p/pmd/bugs/1221/


**Pull requests:**

* [#41](https://github.com/pmd/pmd/pull/41): Update to use asm 5.0.2
* [#42](https://github.com/pmd/pmd/pull/42): Add SLF4j Logger type to MoreThanOneLogger rule
* [#43](https://github.com/pmd/pmd/pull/43): Standard and modified cyclomatic complexity

**New Rules:**

* Java - codesize ruleset:
    * StdCyclomaticComplexity: Like CyclomaticComplexityRule, but not including boolean operators
    * ModifiedCyclomaticComplexity: Like StdCyclomaticComplexity, but switch statement plus all cases count as 1
    * Thanks to Alan Hohn


## April 27, 2014 - 5.1.1:

**Bugfixes:**

* Fixed [bug 1165]: SimplifyConditional false positive
* Fixed [bug 1166]: PLSQL XPath Rules Fail for XPath 1.0
* Fixed [bug 1167]: Error while processing PLSQL file with BOM
* Fixed [bug 1168]: Designer errors when trying to copy xml to clipboard
* Fixed [bug 1170]: false positive with switch in loop
* Fixed [bug 1171]: Specifying minimum priority from command line gives NPE
* Fixed [bug 1173]: Java 8 support: method references
* Fixed [bug 1175]: false positive for StringBuilder.append called 2 consecutive times
* Fixed [bug 1176]: ShortVariable false positive with for-each loops
* Fixed [bug 1177]: Incorrect StringBuffer warning when that class is not used
* Fixed [bug 1178]: LexicalError while parsing Java code aborts CPD run
* Fixed [bug 1180]: False Positive for ConsecutiveAppendsShouldReuse on different variable names
* Fixed [bug 1185]: UnusedModifier throws NPE when parsing enum with a nested static interface
* Fixed [bug 1188]: False positive in UnusedPrivateField
* Fixed [bug 1191]: Ecmascript fails to parse "void(0)"
* Document that PMD requires Java 1.6, see [discussion].
* [Pull request 38]: Some fixes for AbstractCommentRule
* [Pull request 39]: Fixed NPE in ConsecutiveAppendsShouldReuseRule.getVariableAppended()
* [Pull request 40]: Added support for enums in CommentRequiredRule

[bug 1165]: https://sourceforge.net/p/pmd/bugs/1165/
[bug 1166]: https://sourceforge.net/p/pmd/bugs/1166/
[bug 1167]: https://sourceforge.net/p/pmd/bugs/1167/
[bug 1168]: https://sourceforge.net/p/pmd/bugs/1168/
[bug 1170]: https://sourceforge.net/p/pmd/bugs/1170/
[bug 1171]: https://sourceforge.net/p/pmd/bugs/1171/
[bug 1173]: https://sourceforge.net/p/pmd/bugs/1173/
[bug 1175]: https://sourceforge.net/p/pmd/bugs/1175/
[bug 1176]: https://sourceforge.net/p/pmd/bugs/1176/
[bug 1177]: https://sourceforge.net/p/pmd/bugs/1177/
[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/
[bug 1180]: https://sourceforge.net/p/pmd/bugs/1180/
[bug 1185]: https://sourceforge.net/p/pmd/bugs/1185/
[bug 1188]: https://sourceforge.net/p/pmd/bugs/1188/
[bug 1191]: https://sourceforge.net/p/pmd/bugs/1191/
[discussion]: https://sourceforge.net/p/pmd/discussion/188192/thread/6e86840c/
[Pull request 38]: https://github.com/pmd/pmd/pull/38
[Pull request 39]: https://github.com/pmd/pmd/pull/39
[Pull request 40]: https://github.com/pmd/pmd/pull/40

**CPD Changes:**

- Command Line
    - Added option "--skip-lexical-errors" to skip files, which can't be tokenized
      due to invalid characters instead of aborting CPD. See also [bug 1178].
- Ant
    - New optional parameter "skipDuplicateFiles": Ignore multiple copies of files of the same name and length in
      comparison; defaults to "false".
      This was already a command line option, but now also available in in CPD's ant task.
    - New optional parameter "skipLexicalErros": Skip files which can't be tokenized due to invalid characters
      instead of aborting CPD; defaults to "false".

[bug 1178]: https://sourceforge.net/p/pmd/bugs/1178/


## February 11, 2014 - 5.1.0:

**New/Updated Languages:**

- Java 1.8 support added.
- PLSQL support added; thanks to Stuart Turton. See also http://pldoc.sourceforge.net/
- Apache Velocity support added; thanks to Andrey Utis. See also http://velocity.apache.org

**New/Updated Rulesets and Rules:**

- EcmaScript
    - Controversial ruleset, featuring AvoidWithStatement
    - UseBaseWithParseInt
- Java
    - GuardLogStatement
        - replace xpath implementation of GuardDebugLogging by GuardLogStatement (better perf)
    - CommentRequired
        - basic rule to check for existence for formal (javadoc) comments.
    - AvoidProtectedMethodInFinalClassNotExtending
        - rule to avoid protected methods in final classes that don't extend anything other than Object.
    - ConsecutiveAppendsShouldReuse
        - rule to encourage to reuse StringBuilder.append returned object for consecutive calls.
    - PositionLiteralsFirstInCaseInsensitiveComparisons
        - rule similar to PositionLiteralsFirstInComparisons, but for case insensitive comparisons (equalsIgnoreCase).
          Thanks to Larry Diamond
    - ConfusingTernary
        - new property "ignoreElseIf" to suppress this rule in case of if-else-if-else usage.
          See [feature 1161]: Confusing Ternary should skip else if statements (or have a property to do so)
    - FieldDeclarationsShouldBeAtStartOfClass
        - new property "ignoreEnumDeclarations" which is enabled by default. This relaxes the rule, so
          that enums can be declared before fields and the rule is not triggered.

[feature 1161]: http://sourceforge.net/p/pmd/bugs/1161/


**Bugfixes:**

<http://sourceforge.net/p/pmd/bugs/milestone/PMD-5.1.0/>

* Fixed [bug  881]: private final without setter is flagged
* Fixed [bug 1059]: Change rule name "Use Singleton" should be "Use Utility class"
* Fixed [bug 1106]: PMD 5.0.4 fails with NPE on parsing java enum with inner class instance creation
* Fixed [bug 1045]: //NOPMD not working (or not implemented) with ECMAscript
* Fixed [bug 1054]: XML Rules ever report a line -1 and not the line/column where the error occurs
* Fixed [bug 1115]: commentRequiredRule in pmd 5.1 is not working properly
* Fixed [bug 1120]: equalsnull false positive
* Fixed [bug 1121]: NullPointerException when invoking XPathCLI
* Fixed [bug 1123]: failure in help examples
* Fixed [bug 1124]: PMD.run() multithreading issue
* Fixed [bug 1125]: Missing Static Method In Non Instantiatable Class
* Fixed [bug 1126]: False positive with FieldDeclarationsShouldBeAtStartOfClass for static enums
* Fixed [bug 1130]: CloseResource doesn't recognize custom close method
* Fixed [bug 1131]: CloseResource should complain if code between declaration of resource and try
* Fixed [bug 1134]: UseStringBufferLength: false positives
* Fixed [bug 1135]: CheckResultSet ignores results set declared outside of try/catch
* Fixed [bug 1136]: ECMAScript: NullPointerException in getLeft() and getRight()
* Fixed [bug 1140]: public EcmascriptNode getBody(int index)
* Fixed [bug 1141]: ECMAScript: getFinallyBlock() is buggy.
* Fixed [bug 1142]: ECMAScript: getCatchClause() is buggy.
* Fixed [bug 1144]: CPD encoding argument has no effect
* Fixed [bug 1146]: UseArrayListInsteadOfVector false positive when using own Vector class
* Fixed [bug 1147]: EmptyMethodInAbstractClassShouldBeAbstract false positives
* Fixed [bug 1150]: "EmptyExpression" for valid statements!
* Fixed [bug 1154]: Call super onPause when there is no super
* Fixed [bug 1155]: maven pmd plugin does not like empty rule sets
* Fixed [bug 1159]: false positive UnusedFormalParameter readObject(ObjectInputStream) if not used
* Fixed [bug 1164]: Violations are not suppressed with @java.lang.SuppressWarnings("all")

[bug  881]: https://sourceforge.net/p/pmd/bugs/881
[bug 1059]: https://sourceforge.net/p/pmd/bugs/1059
[bug 1045]: https://sourceforge.net/p/pmd/bugs/1045
[bug 1054]: https://sourceforge.net/p/pmd/bugs/1054
[bug 1106]: https://sourceforge.net/p/pmd/bugs/1106
[bug 1115]: https://sourceforge.net/p/pmd/bugs/1115
[bug 1120]: https://sourceforge.net/p/pmd/bugs/1120
[bug 1121]: https://sourceforge.net/p/pmd/bugs/1121
[bug 1123]: https://sourceforge.net/p/pmd/bugs/1123
[bug 1124]: https://sourceforge.net/p/pmd/bugs/1124
[bug 1125]: https://sourceforge.net/p/pmd/bugs/1125
[bug 1126]: https://sourceforge.net/p/pmd/bugs/1126
[bug 1130]: https://sourceforge.net/p/pmd/bugs/1130
[bug 1131]: https://sourceforge.net/p/pmd/bugs/1131
[bug 1134]: https://sourceforge.net/p/pmd/bugs/1134
[bug 1135]: https://sourceforge.net/p/pmd/bugs/1135
[bug 1136]: https://sourceforge.net/p/pmd/bugs/1136
[bug 1140]: https://sourceforge.net/p/pmd/bugs/1140
[bug 1141]: https://sourceforge.net/p/pmd/bugs/1141
[bug 1142]: https://sourceforge.net/p/pmd/bugs/1142
[bug 1144]: https://sourceforge.net/p/pmd/bugs/1144
[bug 1146]: https://sourceforge.net/p/pmd/bugs/1146
[bug 1147]: https://sourceforge.net/p/pmd/bugs/1147
[bug 1150]: https://sourceforge.net/p/pmd/bugs/1150
[bug 1154]: https://sourceforge.net/p/pmd/bugs/1154
[bug 1155]: https://sourceforge.net/p/pmd/bugs/1155
[bug 1159]: https://sourceforge.net/p/pmd/bugs/1159
[bug 1164]: https://sourceforge.net/p/pmd/bugs/1164



**CPD Changes:**
- Command Line
    - Added non-recursive option "--non-recursive" to not scan sub-directories
    - Added option "--exclude" to exclude specific files from being scanned (thanks to Delmas for patch #272)
- CPD is now thread-safe, so that multiple instances of CPD can run concurrently without stepping
    on each other (eg: multi-module Maven projects.). Thanks to David Golpira.

**Miscellaneous:**

- Upgrade to javacc 5.0 (see patch #1109 Patch to build with Javacc 5.0)
- DBURI as DataSource possible - directly scan plsql code stored within the database

**API Changes**

- Deprecated APIs:
    - net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode: getBody(int index) deprecated, use getBody() instead
    - net.sourceforge.pmd.lang.ecmascript.ast.ASTTryStatement: isCatch() and isFinally() deprecated, use hasCatch() and hasBody() instead
- Generalize Symbol Table treatement
    - Added net.sourceforge.pmd.lang.symboltable.ScopedNode
    - Added net.sourceforge.pmd.lang.symboltable.Scope
    - Added net.sourceforge.pmd.lang.symboltable.NameDeclaration
    - Added net.sourceforge.pmd.lang.symboltable.NameOccurrence
    - Added net.sourceforge.pmd.lang.symboltable.AbstractScope
    - Added net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration


## August 11, 2013 - 5.0.5:

    Fixed bug  991: AvoidSynchronizedAtMethodLevel for static methods
    Fixed bug 1084: NPE at UselessStringValueOfRule.java:36
    Fixed bug 1091: file extension for fortran seems to be wrong in cpdgui tools
    Fixed bug 1092: Wrong Attribute "excludemarker" in Ant Task Documentation
    Fixed bug 1095: AvoidFinalLocalVariable false positive
    Fixed bug 1099: UseArraysAsList false positives
    Fixed bug 1102: False positive: shift operator parenthesis
    Fixed bug 1104: IdempotentOperation false positive
    Fixed bug 1107: PMD 5.0.4 couldn't parse call of parent outer java class method from inner class
    Fixed bug 1069: Eclipse plugin does not accept project-local config
    Fixed bug 1111: False positive: Useless parentheses
    Fixed bug 1114: CPD - Tokenizer not initialized with requested properties
    Fixed bug 1118: ClassCastException in pmd.lang.ecmascript.ast.ASTElementGet


## May 1, 2013 - 5.0.4:

    Fixed bug  254: False+ : UnusedImport with Javadoc @throws
    Fixed bug  794: False positive on PreserveStackTrace with anonymous inner
    Fixed bug 1063: False+: ArrayIsStoredDirectly
    Fixed bug 1080: net.sourceforge.pmd.cpd.CPDTest test failing
    Fixed bug 1081: Regression: CPD skipping all files when using relative paths
    Fixed bug 1082: CPD performance issue on larger projects
    Fixed bug 1085: NullPointerException by at net.sourceforge.pmd.lang.java.rule.design.GodClassRule.visit(GodClassRule.java:313)
    Fixed bug 1086: Unsupported Element and Attribute in Ant Task Example
    Fixed bug 1087: PreserveStackTrace (still) ignores initCause()
    Fixed bug 1089: When changing priority in a custom ruleset, violations reported twice


## April 5, 2013 - 5.0.3:

    Fixed bug  938: False positive on LooseCoupling for overriding methods
    Fixed bug  940: False positive on UnsynchronizedStaticDateFormatter
    Fixed bug  942: CheckResultSet False Positive and Negative
    Fixed bug  943: PreserveStackTrace false positive if a StringBuffer exists
    Fixed bug  945: PMD generates RuleSets it cannot read.
    Fixed bug  958: Intermittent NullPointerException while loading XPath node attributes
    Fixed bug  968: Issues with JUnit4 @Test annotation with expected exception (Thanks to Yiannis Paschalidis)
    Fixed bug  975: false positive in ClassCastExceptionWithToArray
    Fixed bug  976: UselessStringValueOf wrong when appending character arrays
    Fixed bug  977: MisplacedNullCheck makes false positives
    Fixed bug  984: Cyclomatic complexity should treat constructors like methods
    Fixed bug  985: Suppressed methods shouldn't affect avg CyclomaticComplexity
    Fixed bug  992: Class java.beans.Statement triggered in CloseResource rule
    Fixed bug  997: Rule NonThreadSafeSingleton gives analysis problem
    Fixed bug  999: Law of Demeter: False positives and negatives
    Fixed bug 1002: False +: FinalFieldCouldBeStatic on inner class
    Fixed bug 1005: False + for ConstructorCallsOverridableMethod - overloaded methods
    Fixed bug 1027: PMD Ant: java.lang.ClassCastException
    Fixed bug 1032: ImmutableField Rule: Private field in inner class gives false positive
    Fixed bug 1064: Exception running PrematureDeclaration
    Fixed bug 1068: CPD fails on broken symbolic links
    Fixed bug 1073: Hard coded violation messages CommentSize
    Fixed bug 1074: rule priority doesn't work on group definitions
    Fixed bug 1076: Report.treeIterator() does not return all violations
    Fixed bug 1077: Missing JavaDocs for Xref-Test Files
    Fixed bug 1078: Package statement introduces false positive UnnecessaryFullyQualifiedName violation
    Merged pull request #14: fix Nullpointer Exception when using -l jsp



## February 3, 2013 - 5.0.2:

    Fixed bug  878: False positive: UnusedFormalParameter for abstract methods
    Fixed bug  913: SignatureDeclareThrowsException is raised twice
    Fixed bug  947: CloseResource rule fails if field is marked with annotation
    Fixed bug 1004: targetjdk isn't attribute of PMD task
    Fixed bug 1007: Parse Exception with annotation
    Fixed bug 1011: CloseResource Rule ignores Constructors
    Fixed bug 1012: False positive: Useless parentheses.
    Fixed bug 1020: Parsing Error
    Fixed bug 1026: PMD doesn't handle 'value =' in SuppressWarnings annotation
    Fixed bug 1028: False-positive: Compare objects with equals for Enums
    Fixed bug 1030: CPD Java.lang.IndexOutOfBoundsException: Index:
    Fixed bug 1037: Facing a showstopper issue in PMD Report Class (report listeners)
    Fixed bug 1039: pmd-nicerhtml.xsl is packaged in wrong location
    Fixed bug 1043: node.getEndLine() always returns 0 (ECMAscript)
    Fixed bug 1044: Unknown option: -excludemarker
    Fixed bug 1046: ant task CPDTask doesn't accept ecmascript
    Fixed bug 1047: False Positive in 'for' loops for LocalVariableCouldBeFinal in 5.0.1
    Fixed bug 1048: CommentContent Rule, String Index out of range Exception
    Fixed bug 1049: Errors in "How to write a rule"
    Fixed bug 1055: Please add a colon in the ant output after line,column for Oracle JDeveloper IDE usage
    Fixed bug 1056: "Error while processing" while running on xml file with DOCTYPE reference
    Fixed bug 1060: GodClassRule >>> wrong method



## November 28, 2012 - 5.0.1:

    Fixed bug  820: False+ AvoidReassigningParameters
    Fixed bug 1008: pmd-5.0.0: ImmutableField false positive on self-inc/dec
    Fixed bug 1009: pmd-5.0.0: False + UselessParentheses
    Fixed bug 1003: newline characters stripped from CPD data in PMD 5.0.0
    Fixed bug 1001: InsufficientStringBufferDeclaration fails to parse hex
    Fixed bug  522: InefficientStringBuffering bug false +
    Fixed bug  953: String.InefficientStringBuffering false +
    Fixed bug  981: Unable to parse
    Fixed bug 1010: pmd: parsing of generic method call with super fails
    Fixed bug  996: pmd-4.2.6: MissingBreakInSwitch fails to report violation
    Fixed bug  993: Invalid NPath calculation in return statement. Thanks to Prabhjot Singh for the patch.
    Fixed bug 1023: c/c++ \ as a continuation character not supported
    Fixed bug 1033: False+ : SingularField
    Fixed bug 1025: Regression of Crash in PMDTask due to multithreading (Eclipse and Java 1.5)
    Fixed bug 1017: Type resolution very slow for big project. Thanks to Roman for the patch.
    Fixed bug 1036: Documentation: default threshold values removed from v5.0
    Fixed bug 1035: UseObjectForClearerAPI has misspelled message
    Fixed bug 1031: false DontImportJavaLang
    Fixed bug 1034: UseConcurrentHashMap flags calls to methods that return Map
    Fixed bug 1006: Problem with implementation of getPackageNameImage method
    Fixed bug 1014: AvoidLiteralsInIfCondition must NOT consider null
    Fixed bug 1013: jnlp link for CPD is wrong

    PMD Command Line Changes:
      Improved command line interface (CLI) parsing using JCommander.
      Note: this breaks compatibility, but should be easy to fix.
      With "-d" you specify nowtThe source files / source directory to be scanned.
      With "-f" you select the report format (like text, html, ...)
      With "-R" you select the rulesets to be used.
      Example: pmd -d c:\data\pmd\pmd\test-data\Unused1.java -f xml -R rulesets/java/unusedcode.xml

    Improved JSP parser to be less strict with not valid XML documents (like HTML). Thanks to Victor Bucutea.
    Fixed bgastviewer not working. Thanks to Victor Bucutea.
    Improved CPD: Support in CPD for IgnoreAnnotations and SuppressWarnings("CPD-START"). Thanks to Matthew Short.
    Fixed C# support for CPD - thanks to TIOBE Software.

    New Ecmascript rules:

        Basic ruleset: AvoidTrailingComma


## May, 1, 2012 - 5.0.0:

    Fixed bug 3515487: Inconsistent reference to ruleset file in documentation
    Fixed bug 3470274: Using Label for lines in XMLRenderer
    Fixed bug 3175710: NPE in InsufficientStringBufferDeclaration

    CPD:
    - Exit with status code 4 when CPD detects code duplication (Patch ID: 3497021)

## January 31, 2012 - 5.0-alpha:

    This version of PMD breaks API compatibility with prior versions of PMD, as well
    as RuleSet XML compatibility. Also the maven coordinates (groupId) have been changed.
    The decision to break compatibility, allows PMD
    internals and code organization to be improved to better handle additional
    languages.  This opportunity was used to remove depreciated APIs, and beat up
    any code which has thumbed its nose at the developers over the years. ;)

    The following is relatively complete list of the major changes (this may not be
    100% accurate, see actual source code when in doubt):

    Fixed bug (no number) - Fixed UseStringBufferLengthRule only worked once per class
    All StringBuffer-related rules now also catch StringBuilder-related issues in the same way

        API Change - Unification of treatment of languages within PMD core:
           Added - net.sourceforge.pmd.lang.Language (now an 'enum')
           Added - net.sourceforge.pmd.lang.LanguageVersion
           Added - net.sourceforge.pmd.lang.LanguageVersionDiscoverer
           Added - net.sourceforge.pmd.lang.LanguageVersionHandler
           Added - net.sourceforge.pmd.lang.XPathHandler
           Added - net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler
           Added - net.sourceforge.pmd.lang.xpath.Initializer
           Added - net.sourceforge.pmd.lang.ast.AbstractTokenManager
           Added - net.sourceforge.pmd.lang.ast.CharStream
           Added - net.sourceforge.pmd.lang.ast.JavaCharStream
           Added - net.sourceforge.pmd.lang.ast.SimpleCharStream
           Added - net.sourceforge.pmd.lang.ast.TokenMgrError
           Added - net.sourceforge.pmd.lang.rule.stat.StatisticalRule
           Added - net.sourceforge.pmd.lang.rule.stat.StatisticalRuleHelper
           Added - net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule
           Added - net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory
           Added - net.sourceforge.pmd.lang.rule.RuleViolationFactory
           Added - net.sourceforge.pmd.lang.java.rule.JavaRuleViolationFactory
           Added - net.sourceforge.pmd.lang.jsp.rule.JspRuleViolationFactory
           Renamed - net.sourceforge.pmd.AbstractRule to net.sourceforge.pmd.lang.rule.AbstractRule
           Renamed - net.sourceforge.pmd.AbstractJavaRule to net.sourceforge.pmd.lang.java.rule.AbstractJavaRule
           Renamed - net.sourceforge.pmd.AbstractRuleChainVisitor to net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor
           Renamed - net.sourceforge.pmd.RuleChainVisitor to net.sourceforge.pmd.lang.rule.RuleChainVisitor
           Renamed - net.sourceforge.pmd.SourceFileSelector to net.sourceforge.pmd.lang.rule.LanguageFilenameFilter
           Renamed - net.sourceforge.pmd.rule.XPathRule to net.sourceforge.pmd.lang.rule.XPathRule
           Renamed - net.sourceforge.pmd.jsp.rule.AbstractJspRule to net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule
           Renamed - net.sourceforge.pmd.ast.CompilationUnit to net.sourceforge.pmd.lang.ast.RootNode
           Renamed - net.sourceforge.pmd.ast.JavaRuleChainVisitor to net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor
           Renamed - net.sourceforge.pmd.jsp.ast.JspRuleChainVisitor to net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor
           Renamed - net.sourceforge.pmd.parser.Parser to net.sourceforge.pmd.lang.Parser
           Renamed - net.sourceforge.pmd.parser.TokenManager to net.sourceforge.pmd.lang.TokenManager
           Renamed - net.sourceforge.pmd.parser.* into net.sourceforge.pmd.lang.{Language}
           Renamed - net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler to net.sourceforge.pmd.lang.LanguageVersionHandler
           Renamed - net.sourceforge.pmd.sourcetypehandlers.VisitorStarter to net.sourceforge.pmd.lang.VisitorStarter
           Renamed - net.sourceforge.pmd.sourcetypehandlers.* into net.sourceforge.pmd.lang.{Language}
           Renamed - net.sourceforge.pmd.stat.StatisticalRule to net.sourceforge.pmd.lang.rule.StatisticalRuleHelper
           Renamed - net.sourceforge.pmd.jaxen.TypeOfFunction to net.sourceforge.pmd.lang.java.xpath.TypeOfFunction
           Renamed - net.sourceforge.pmd.jaxen.MatchesFunction to net.sourceforge.pmd.lang.xpath.MatchesFunction
           Renamed - net.sourceforge.pmd.jaxen.Attribute to net.sourceforge.pmd.lang.ast.xpath.Attribute
           Renamed - net.sourceforge.pmd.jaxen.AttributeAxisIterator to net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator
           Renamed - net.sourceforge.pmd.jaxen.DocumentNavigator to net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator
           Renamed - net.sourceforge.pmd.jaxen.NodeIterator to net.sourceforge.pmd.lang.ast.xpath.NodeIterator
           Renamed - net.sourceforge.pmd.ast.* into net.sourceforge.pmd.lang.java.ast.*
           Renamed - net.sourceforge.pmd.rules.* into net.sourceforge.pmd.lang.java.rule.* and updated to follow conventions
           Renamed - net.sourceforge.pmd.jsp.ast.* into net.sourceforge.pmd.lang.jsp.ast.*
           Renamed - net.sourceforge.pmd.jsp.rules.* into net.sourceforge.pmd.lang.jsp.ast.rule.* and updated to follow conventions
           Deleted - net.sourceforge.pmd.cpd.cppast.* into net.sourceforge.pmd.lang.cpp.ast.*
           Deleted - net.sourceforge.pmd.CommonAbstractRule
           Deleted - net.sourceforge.pmd.SourceFileConstants
           Deleted - net.sourceforge.pmd.SourceType
           Deleted - net.sourceforge.pmd.SourceTypeDiscoverer
           Deleted - net.sourceforge.pmd.SourceTypeToRuleLanguageMapper
           Deleted - net.sourceforge.pmd.TargetJDK1_3
           Deleted - net.sourceforge.pmd.TargetJDK1_4
           Deleted - net.sourceforge.pmd.TargetJDK1_5
           Deleted - net.sourceforge.pmd.TargetJDK1_6
           Deleted - net.sourceforge.pmd.TargetJDK1_7
           Deleted - net.sourceforge.pmd.TargetJDKVersion
           Deleted - net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter
           Deleted - net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker
           Deleted - net.sourceforge.pmd.ast.JavaCharStream
           Deleted - net.sourceforge.pmd.ast.CharStream
           Deleted - net.sourceforge.pmd.ast.TokenMgrError
           Deleted - net.sourceforge.pmd.jsp.ast.JspCharStream
           Deleted - net.sourceforge.pmd.jsp.ast.TokenMgrError

        API Change - Generalize RuleViolation treatment
           Renamed - net.sourceforge.pmd.IRuleViolation to net.sourceforge.pmd.RuleViolation
           Renamed - net.sourceforge.pmd.RuleViolation to net.sourceforge.pmd.lang.rule.AbstractRuleViolation
           Added - net.sourceforge.pmd.RuleViolationComparator
           Added - net.sourceforge.pmd.lang.java.rule.JavaRuleViolation
           Added - net.sourceforge.pmd.lang.jsp.rule.JspRuleViolation

        API Change - Generalize DFA treatment
           Renamed - net.sourceforge.pmd.dfa.IDataFlowNode to net.sourceforge.pmd.lang.dfa.DataFlowNode
           Renamed - net.sourceforge.pmd.dfa.DataFlowNode to net.sourceforge.pmd.lang.dfa.AbstractDataFlowNode
           Renamed - net.sourceforge.pmd.dfa.Linker to net.sourceforge.pmd.lang.dfa.Linker
           Renamed - net.sourceforge.pmd.dfa.LinkerException to net.sourceforge.pmd.lang.dfa.LinkerException
           Renamed - net.sourceforge.pmd.dfa.NodeType to net.sourceforge.pmd.lang.dfa.NodeType
           Renamed - net.sourceforge.pmd.dfa.StackObject to net.sourceforge.pmd.lang.dfa.StackObject
           Renamed - net.sourceforge.pmd.dfa.SequenceChecker to net.sourceforge.pmd.lang.dfa.SequenceChecker
           Renamed - net.sourceforge.pmd.dfa.SequenceException to net.sourceforge.pmd.lang.dfa.SequenceException
           Renamed - net.sourceforge.pmd.dfa.StartOrEndDataFlowNode to net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode
           Renamed - net.sourceforge.pmd.dfa.Structure to net.sourceforge.pmd.lang.dfa.Structure
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccess to net.sourceforge.pmd.lang.dfa.VariableAccess
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccessException to net.sourceforge.pmd.lang.dfa.VariableAccessException
           Renamed - net.sourceforge.pmd.dfa.pathfinder.* to net.sourceforge.pmd.lang.dfa.pathfinder.*
           Renamed - net.sourceforge.pmd.dfa.report.* to net.sourceforge.pmd.lang.dfa.report.*
           Renamed - net.sourceforge.pmd.dfa.DaaRuleViolation to net.sourceforge.pmd.lang.java.dfa.DaaRuleViolation
           Renamed - net.sourceforge.pmd.dfa.DataFlowFacade to net.sourceforge.pmd.lang.java.dfa.DataFlowFacade
           Renamed - net.sourceforge.pmd.dfa.StatementAndBraceFinder to net.sourceforge.pmd.lang.java.dfa.StatementAndBraceFinder
           Renamed - net.sourceforge.pmd.dfa.variableaccess.VariableAccessVisitor to net.sourceforge.pmd.lang.java.dfa.VariableAccessVisitor
           Added - net.sourceforge.pmd.lang.java.dfa.JavaDataFlowNode
           Added - net.sourceforge.pmd.lang.DataFlowHandler

       API Change - Generalize Symbol Table treatement
           Deleted - net.sourceforge.pmd.symboltable.JspSymbolFacade
           Deleted - net.sourceforge.pmd.symboltable.JspScopeAndDeclarationFinder
           Renamed - net.sourceforge.pmd.symboltable.* to net.sourceforge.pmd.lang.java.symboltable.*

       API Change - Generalize Type Resolution treatment
           Renamed - net.sourceforge.pmd.typeresolution.* to net.sourceforge.pmd.lang.java.typeresolution.*

        API Change - Generalize Property Descriptor treatment
           Renamed - net.sourceforge.pmd.properties.* to net.sourceforge.pmd.lang.rule.properties.*
           Renamed - net.sourceforge.pmd.properties.AbstractPMDProperty to net.sourceforge.pmd.lang.rule.properties.AbstractProperty
           Changed - net.sourceforge.pmd.properties.PropertyDescriptor to use Generics, and other changes
           Added - net.sourceforge.pmd.lang.rule.properties.* new types and other API changes

        API Change - Generalize AST treatment
           Added - net.sourceforge.pmd.lang.ast.Node (interface extracted from old Node/SimpleNode)
           Added - net.sourceforge.pmd.lang.ast.AbstractNode
           Added - net.sourceforge.pmd.ast.DummyJavaNode
           Added - net.sourceforge.pmd.jsp.ast.AbstractJspNode
           Added - net.sourceforge.pmd.jsp.ast.JspNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaNode to net.sourceforge.pmd.ast.AbstractJavaNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaTypeNode to net.sourceforge.pmd.ast.AbstractJavaTypeNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaAccessNode to net.sourceforge.pmd.ast.AbstractJavaAccessNode
           Renamed - net.sourceforge.pmd.ast.SimpleJavaAccessTypeNode to net.sourceforge.pmd.ast.AbstractJavaAccessTypeNode
           Deleted - net.sourceforge.pmd.ast.Node
           Deleted - net.sourceforge.pmd.ast.SimpleNode
           Deleted - net.sourceforge.pmd.ast.AccessNodeInterface
           Deleted - net.sourceforge.pmd.jsp.ast.Node
           Deleted - net.sourceforge.pmd.jsp.ast.SimpleNode

        API Change - General code reorganization/cleanup
           Renamed - net.sourceforge.pmd.AbstractDelegateRule to net.sourceforge.pmd.lang.rule.AbstractDelegateRule
           Renamed - net.sourceforge.pmd.MockRule to net.sourceforge.pmd.lang.rule.MockRule
           Renamed - net.sourceforge.pmd.RuleReference to net.sourceforge.pmd.lang.rule.RuleReference
           Renamed - net.sourceforge.pmd.ScopedLogHandlersManager to net.sourceforge.pmd.util.log.ScopedLogHandlersManager
           Renamed - net.sourceforge.pmd.util.AntLogHandler to net.sourceforge.pmd.util.log.AntLogHandler
           Renamed - net.sourceforge.pmd.util.ConsoleLogHandler to net.sourceforge.pmd.util.log.ConsoleLogHandler
           Renamed - net.sourceforge.pmd.util.PmdLogFormatter to net.sourceforge.pmd.util.log.PmdLogFormatter

       API Change - Changes to Rule/RuleSet/RuleSets
          Removed - boolean Rule.include()
          Removed - void Rule.setInclude(boolean)
          Removed - String Rule.getRulePriorityName()
          Removed - String Rule.getExample()
          Removed - Rule.LOWEST_PRIORITY
          Removed - Rule.PRIORITIES
           Removed - Properties Rule.getProperties()
           Removed - Rule.addProperties(Properties)
           Removed - boolean Rule.hasProperty(String)
           Removed - RuleSet.applies(Language,Language)
           Removed - RuleSet.getLanguage()
           Removed - RuleSet.setLanguage(Language)
           Removed - RuleSets.applies(Language,Language)
          Changed - void Rule.setPriority(int) to void Rule.setPriority(RulePriority)
          Changed - int Rule.getPriority() to void RulePriority Rule.getPriority()
           Changed - XXX Rule.getXXXProperty(String) to <T> Rule.getProperty(PropertyDescriptor<T>)
           Changed - XXX Rule.getXXXProperty(PropertyDescriptor) to <T> Rule.getProperty(PropertyDescriptor<T>)
           Changed - Rule.addProperty(String, String) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.setProperty(PropertyDescriptor, Object) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.setProperty(PropertyDescriptor, Object[]) to Rule.setProperty(PropertyDescriptor<T>, T)
           Changed - Rule.propertyValuesByDescriptor() to Rule.getPropertiesByPropertyDescriptor()
           Changed - PropertyDescriptor Rule.propertyDescriptorFor(String) to PropertyDescriptor Rule.getPropertyDescriptor(String)
           Changed - boolean RuleSet.usesDFA() to boolean RuleSet.usesDFA(Language)
           Changed - boolean RuleSet.usesTypeResolution() to boolean RuleSet.usesTypeResolution(Language)
          Added - Rule.setLanguage(Language)
          Added - Language Rule.getLanguage()
          Added - Rule.setMinimumLanguageVersion(LanguageVersion)
          Added - LanguageVersion Rule.getMinimumLanguageVersion()
          Added - Rule.setMaximumLanguageVersion(LanguageVersion)
          Added - LanguageVersion Rule.getMaximumLanguageVersion()
          Added - Rule.setDeprecated(boolean)
          Added - boolean Rule.isDeprecated()
          Added - String Rule.dysfunctionReason();
           Added - Rule.definePropertyDescriptor(PropertyDescriptor)
           Added - List<PropertyDescriptor> Rule.getPropertyDescriptors()
           Added - RuleSet.applies(Rule,LanguageVersion)

       API Change - Changes to PMD class
          Renamed - PMD.EXCLUDE_MARKER to PMD.SUPPRESS_MARKER
          Removed - PMD.processFile(InputStream, RuleSet, RuleContext)
          Removed - PMD.processFile(InputStream, String, RuleSet, RuleContext)
          Removed - PMD.processFile(Reader, RuleSet, RuleContext)
          Removed - PMD.processFile(Reader, RuleSets, RuleContext, LanguageVersion)
          Moved - PMD.getExcludeMarker() to Configuration.getSuppressMarker()
          Moved - PMD.setExcludeMarker(String) to Configuration.getSuppressMarker(String)
          Moved - PMD.getClassLoader() to Configuration.getClassLoader()
          Moved - PMD.setClassLoader(ClassLoader) to Configuration.getClassLoader(ClassLoader)
          Moved - PMD.setDefaultLanguageVersion(LanguageVersion) to Configuration.setDefaultLanguageVersion(LanguageVersion)
          Moved - PMD.setDefaultLanguageVersions(List<LanguageVersion>) to Configuration.setDefaultLanguageVersions(List<LanguageVersion>)
          Moved - PMD.createClasspathClassLoader(String) to Configuration.createClasspathClassLoader(String)

       API Change - Changes to Node interface
          Renamed - Node.findChildrenOfType(Class) as Node.findDescendantsOfType(Class)
          Renamed - Node.getFirstChildOfType(Class) as Node.getFirstDescendantOfType(Class)
          Renamed - Node.containsChildOfType(Class) as Node.hasDescendantOfType(Class)
          Renamed - Node.getAsXml() as Node.getAsDocument()
          Added - Node.findChildrenOfType(Class), non recursive version
          Added - Node.getFirstChildOfType(Class), non recursive version

       API Change - Remove deprecated APIs
          Removed - AccessNode.setXXX() methods, use AccessNode.setXXX(boolean) instead.
          Removed - PMDException.getReason()
          Removed - RuleSetFactory.createRuleSet(String,ClassLoader), use RuleSetFactory.setClassLoader(ClassLoader) and RuleSetFactory.createRuleSets(String) instead.
          Removed - net.sourceforge.pmd.cpd.FileFinder use net.sourceforge.pmd.util.FileFinder instead.

       API Change - RuleSetFactory
            Added - RuleSetFactory.setClassLoader(ClassLoader)
            Added - RuleSetFactory.createRuleSets(List<RuleSetReferenceId>)
            Added - RuleSetFactory.createRuleSet(RuleSetReferenceId)
            Added - RuleSetFactory.setClassLoader(ClassLoader)
            Added - RuleSetReferenceId class to handle parsing of RuleSet strings, see RuleSetReferenceId.parse(String)
          Renamed - RuleSetFactory.createSingleRuleSet(String) to RuleSetFactory.createRuleSet(String);
          Removed - RuleSetFactory.createRuleSets(String, ClassLoader), use RuleSetFactory.createRuleSets(String) instead.
          Removed - RuleSetFactory.createSingleRuleSet(String, ClassLoader), use RuleSetFactory.createSingleRuleSet(String) instead.
          Removed - RuleSetFactory.createRuleSet(InputStream, ClassLoader), use RuleSetFactory.createRuleSet(RuleSetReferenceId) instead.
          Removed - ExternalRuleID, use RuleSetReferenceId instead
          Removed - SimpleRuleSetNameMapper, use RuleSetReferenceId instead

       API Change - Changes to Renderer class, and Renderer implementations
            Added - Renderer.getName()
            Added - Renderer.setName(String)
            Added - Renderer.getDescription()
            Added - Renderer.setDescription(String)
            Added - Renderer.getPropertyDefinitions()
            Added - Renderer.isShowSuppressedViolations()
            Added - AbstractAccumulatingRenderer
          Removed - Renderer.render(Report)
          Removed - Renderer.render(Report, Writer)
          Renamed - Renderer.showSuppressedViolations(boolean) to Renderer.setShowSuppressedViolations(boolean)
          Renamed - PapariTextRenderer to TextColorRenderer
          Renamed - OntheFlyRenderer to AbstractIncrementingRenderer

    PMD command line changes:

       Removed -lineprefix use -property linePrefix {value} instead
       Removed -linkprefix use -property linkPrefix {value} instead
       Removed -xslt use -property xsltFilename {value} instead
       Removed -nojsp now obsolete
       Removed -targetjdk use -version {name} {version} instead
       Added -version {name} {version} to set language version to use for a given language
       Added -property {name} {value} as generic way to pass properties to Renderers
       Added -showsuppressed as a means to show suppressed rule violations (consistent with Ant task behavior)
       Renamed 'nicehtml' report to 'xslt'
       Renamed 'papari' report to 'textcolor'
       Renamed -excludemarker option to -suppressmarker
       Renamed -cpus option to -threads

    Ant changes:

       Removed - <formatter> 'linkPrefix' attribute, use <param name="linkPrefix"> instead
       Removed - <formatter> 'linePrefix' attribute, use <param name="linePrefix"> instead
       Changed - <formatter> is optional - if not specified, falls back to "text" and console output.
       Removed - <pmd> 'targetJDK' attribute to <version>lang version</version> instead
         Added - <param name="name" value="value"/> as generic way to pass properties to Renderers on <formatter>
       Renamed - <pmd> 'excludeMarker' attribute to 'suppressMarker'
       Renamed - <pmd> 'cpus' attribute to 'threads'

    Maven changes:
       The new maven coordinates are: net.sourceforge.pmd:pmd, e.g.
       <dependency>
         <groupId>net.sourceforge.pmd</groupId>
         <artifactId>pmd</artifactId>
         <version>5.0</version>
       </dependency>

    New features:

    New Language 'ecmascript' added, for writing XPathRule and Java Rules against ECMAScript/JavaScript documents (must be standalone, not embedded in HTML).  Many thanks to Rhino!
    New Language 'xml' added, for writing XPathRules against XML documents
    New Language 'xsl' added, as a derivative from XML.
    Rules can now define a 'violationSuppressRegex' property to universally suppress violations with messages matching the given regular expression
    Rules can now define a 'violationSuppressXPath' property to universally suppress violations on nodes which match the given relative XPath expression
    Rules are now directly associated with a corresponding Language, and a can also be associated with a specific Language Version range if desired.
    Rules can now be flagged with deprecated='true' in the RuleSet XML to allow the PMD Project to indicate a Rule (1) is scheduled for removal, (2) has been removed, or (3) has been renamed/moved.
    XPathRules can now query using XPath 2.0 with 'version=2.0"', or XPath 2.0 in XPath 1.0 compatibility mode using 'version="1.0 compatibility"'.  Many thanks to Saxon!
    Rules can now use property values in messages, for example ${propertyName} will expand to the value of the 'propertyName' property on the Rule.
    Rules can now use violation specific values in messages, specifically ${variableName}, ${methodName}, ${className}, ${packageName}.
    New XPath function 'getCommentOn' can be used to search for strings in comments - Thanks to Andy Throgmorton

    CPD:
    Add .hxx and .hpp as valid file extension for CPD - Thanks to Ryan Pavlik
    Add options to to the CPD command line task - Thanks to Cd-Man
    Add C# support for CPD - thanks to Florian Bauer
    Fix small bug in Rule Designer UI
    Performance enhacement when parsing Javadoc (Patch ID: 3217201), thanks to Cd-Man
    Rework the XMLRenderer to use proper XML API and strictly uses the system value for encoding (Fix bug: 1435751)

    Other changes:
    Rule property API upgrades:
      All numeric property descriptors can specify upper & lower limits
      Newly functional Method & Type descriptors allow rule developers to incorporate/watch for individual methods or types
      Better initialization error detection
      Deprecated old string-keyed property API, will leave some methods behind for XPath rules however
    '41' and '42' shortcuts for rulesets added
    The default Java version processed by PMD is now uniformly Java 1.5.
    RuleViolations in Reports now uses List internally, and RuleViolationComparator is no longer broken
    TokenManager errors now include a file name whenever possible for every AST in PMD
    Added file encoding option to CPD GUI, which already existed for the command line and Ant
    AssignmentInOperand enhanced to catch assignment in 'for' condition, as well as use of increment/decrement operators.  Customization properties added to allow assignment in if/while/for, or use of increment/decrement.
    Fix false positive on CastExpressions for UselessParentheses
    Fix false positive where StringBuffer.setLength(0) was using default constructor size of 16, instead of actual constructor size.
    Fix false negative for non-primitive types for VariableNamingConventions, also expanded scope to local and method/constructors, and enhanced customization options to choose between members/locals/parameters (all checked by default)
    Fix false negative for UseArraysAsList when the array was passed as method parameter - thanks to Andy Throgmorton
    Improve TooManyMethods rule - thanks to a patch from Riku Nykanen
    Improve DoNotCallSystemExit - thanks to a patch from Steven Christou
    Correct -benchmark reporting of Rule visits via the RuleChain
    Creating an Empty Code Ruleset and moved the following rules from Basic ruleset:
            * Empty Code Rules
            * EmptyCatchBlock
            * EmptyIfStmt
            * EmptyWhileStmt
            * EmptyTryBlock
            * EmptyFinallyBlock
            * EmptySwitchStatements
            * EmptySynchronizedBlock
            * EmptyStatementNotInLoop
            * EmptyInitializer
            * EmptyStatementBlock
            * EmptyStaticInitializer
        Basic rulesets still includes a reference to those rules.
    Creating a unnecessary Code Ruleset and moved the following rules from Basic ruleset:
            * UnnecessaryConversionTemporary
            * UnnecessaryReturn
            * UnnecessaryFinalModifier
            * UselessOverridingMethod
            * UselessOperationOnImmutable
            * UnusedNullCheckInEquals
            * UselessParentheses
        Basic rulesets still includes a reference to those rules.

    Fixed bug 2920057 - Fixed False + on CloseResource
    Fixed bug 1808110 - Fixed performance issues on PreserveStackTrace
    Fixed bug 2832322 - cpd.xml file tag path attribute should be entity-encoded
    Fixed bug 2826119 - False +: DoubleCheckedLocking warning with volatile field
    Fixed bug 2835074 - False -: DoubleCheckedLocking with reversed null check
    Fixed bug 1932242 - EmptyMethodInAbstractClassShouldBeAbstract false +
    Fixed bug 1928009 - Error using migration ruleset in PMD 4.2
    Fixed bug 1808110 - PreserveStackTrace
    Fixed bug 1988829 - Violation reported without source file name (actually a fix to ConsecutiveLiteralAppends)
    Fixed bug 1989814 - false +: ConsecutiveLiteralAppends
    Fixed bug 1977230 - false positive: UselessOverridingMethod
    Fixed bug 1998185 - BeanMembersShouldSerialize vs @SuppressWarnings("serial")
    Fixed bug 2002722 - false + in UseStringBufferForStringAppends
    Fixed bug 2056318 - False positive for AvoidInstantiatingObjectsInLoops
    Fixed bug 1977438 - False positive for UselessStringValueOf
    Fixed bug 2050064 - False + SuspiciousOctalEscape with backslash literal
    Fixed bug 1556594 - Wonky detection of NullAssignment
    Fixed bug 1481051 - false + UnusedNullCheckInEquals (and other false positives too)
    Fixed bug 1943204 - Ant task: <ruleset> path should be relative to Ant basedir
    Fixed patch 2075906 - Add toString() to the rule UnnecessaryWrapperObjectCreation
    Fixed bug 2315623 - @SuppressWarnings("PMD.UseSingleton") has no effect
    Fixed bug 2230809 - False +: ClassWithOnlyPrivateConstructorsShouldBeFinal
    Fixed bug 2338341 - ArrayIndexOutOfBoundsException in CPD (on Ruby)
    Fixed bug 2315599 - False +: UseSingleton with class containing constructor
    Fixed bug 1955852 - false positives for UnusedPrivateMethod & UnusedLocalVariable
    Fixed bug 2404700 - UseSingleton should not act on enums
    Fixed bug - JUnitTestsShouldIncludeAssert now detects Junit 4 Assert.assert...  constructs
    Fixed bug 1609038 - Xslt report generators break if path contains "java"
    Fixed bug 2142986 - UselessOverridingMethod doesn't consider annotations
    Fixed bug 2027626 - False + : AvoidFinalLocalVariable
    Fixed bug 2606609 - False "UnusedImports" positive in package-info.java
    Fixed bug 2645268 - ClassCastException in UselessOperationOnImmutable.getDeclaration
    Fixed bug 2724653 - AvoidThreadGroup reports false positives
    Fixed bug 2904832 - Type resolution not working for ASTType when using an inner class
    Fixed bug 1435751 - XML format does not support UTF-8
    Fixed bug 3303811 - Deadlink on "Similar projects" page
    Fixed bug 3017616 - Updated documentation regarding Netbeans plugin - thanks to Jesse Glick
    Fixed bug 3427563 - Deprecated class (android.util.config) - thanks to Lukas Reschke for the patch

    ruleset.dtd and ruleset_xml_schema.xsd added to jar file in rulesets directory
    bin and java14/bin scripts:
        retroweaver version was not correct in java14/bin scripts
        support for extra languages in cpd.sh
        standard unix scripts can be used with cygwin
    Upgrading UselessOperationOnImmutable to detect more use cases, especially on String and fix false positives
    AvoidDuplicateLiteralRule now has 'skipAnnotations' boolean property
    Fixed false positive in UnusedImports: javadoc comments are parsed to check @see and other tags
    Fixed parsing bug: constant fields in annotation classes
    Bug fix: NPE in MoreThanOneLogger
    UnnecessaryParentheses now checks all expressions, not just return statements
    UnusedFormalParameter now reports violations on the parameter node, not the method/constructor node
    Updates to RuleChain to honor RuleSet exclude-pattern
    Optimizations and false positive fixes in PreserveStackTrace
    @SuppressWarnings("all") disables all warnings
    SingularField now checks for multiple fields in the same declaration
    Java grammar enhanced to include AnnotationMethodDeclaration as parent node of method related children of AnnotationTypeMemberDeclaration
    JavaCC generated artifacts updated to JavaCC 4.1.
    Dependencies updates: asm updated to 3.2
    Ant requirement is now 1.7.0 or higher for compilation
        JUnit testing jar is packaged on 1.7.0+ only in ant binary distributions
        Note that the ant task still works with 1.6.0 and higher
    All comment types are now stored in ASTCompilationUnit, not just formal ones
    Fixed false negative in UselessOverridingMethod
    Fixed handling of escape characters in UseIndexOfChar and AppendCharacterWithChar
    Fixed ClassCastException on generic method in BeanMembersShouldSerialize
    Fixed ClassCastException in symbol table code
    Support for Java 1.4 runtime dropped, PMD now requires Java 5 or higher.  PMD can still process Java 1.4 source files.
    Support for Java 1.7
    Text renderer is now silent if there's no violation instead of displaying "No problems found!"
    RuleSet short names now require a language prefix, 'basic' is now 'java-basic', and 'rulesets/basic.xml' is now 'rulesets/java/basic.xml'
    The JSP RuleSets are now in the 'jsp' language, and are 'jsp-basic', 'jsp-basic-jsf', 'rulesets/jsp/basic.xml' and 'rulesets/jsp/basic-jsp.xml'
    Enhanced logging in the ClassTypeResolver to provide more detailed messaging.
    AvoidUsingHardCodedIP modified to not use InetAddress.getByName(String), instead does better pattern analysis.
    The JSP/JSF parser can now parse Unicode input.
    The JSP/JSP parser can now handle <script>...</script> tags.  The AST HtmlScript node contains the content.
    Added Ecmascript as a supported language for CPD.
    The RuleSet XML Schema namespace is now: http://pmd.sourceforge.net/ruleset/2.0.0
    The RuleSet XML Schema is located in the source at: etc/ruleset_2_0_0.xsd
    The RuleSet DTD is located in the source at: etc/ruleset_2_0_0.dtd
    Improved include/exclude pattern matching performance for ends-with type patterns.
    Modify (and hopefully fixed) CPD algorithm thanks to a patch from Juan Jesús García de Soria.
    Fixed character reference in xml report - thanks to Seko
    Enhanced SuspiciousEqualsMethodName rule - thanks to Andy Throgmorton
    Add a script to launch CPDGUI on Unix system - thanks to Tom Wheeler

    New Java rules:

        Basic ruleset: ExtendsObject,CheckSkipResult,AvoidBranchingStatementAsLastInLoop,DontCallThreadRun,DontUseFloatTypeForLoopIndices
        Controversial ruleset: AvoidLiteralsInIfCondition, AvoidPrefixingMethodParameters, OneDeclarationPerLine, UseConcurrentHashMap
        Coupling ruleset: LoosePackageCoupling,LawofDemeter
        Design ruleset: LogicInversion,UseVarargs,FieldDeclarationsShouldBeAtStartOfClass,GodClass
        Empty ruleset: EmptyInitializer,EmptyStatementBlock
        Import ruleset: UnnecessaryFullyQualifiedName
        Optimization ruleset: RedundantFieldInitializer
        Naming ruleset: ShortClassName, GenericsNaming
        StrictException ruleset: AvoidThrowingNewInstanceOfSameException, AvoidCatchingGenericException, AvoidLosingExceptionInformation
        Unnecessary ruleset: UselessParentheses
        JUnit ruleset: JUnitTestContainsTooManyAsserts, UseAssertTrueInsteadOfAssertEquals
        Logging with Jakarta Commons ruleset: GuardDebugLogging

    New Java ruleset:
        android.xml: new rules specific to the Android platform

    New JSP rules:
        Basic ruleset: NoInlineScript

    New ECMAScript rules:
        Basic ruleset: AssignmentInOperand,ConsistentReturn,InnaccurateNumericLiteral,ScopeForInVariable,UnreachableCode,EqualComparison,GlobalVariable
        Braces ruleset: ForLoopsMustUseBraces,IfStmtsMustUseBraces,IfElseStmtsMustUseBraces,WhileLoopsMustUseBraces
        Unnecessary ruleset: UnnecessaryParentheses,UnnecessaryBlock

    New XML rules:
        Basic ruleset: MistypedCDATASection


## November 4, 2011 - 4.3:

    Add support for Java 7 grammer - thanks to Dinesh Bolkensteyn and SonarSource
    Add options --ignore-literals and --ignore-identifiers to the CPD command line task, thanks to Cd-Man
    Fixed character reference in xml report - thanks to Seko
    Add C# support for CPD - thanks to Florian Bauer
    Fix small bug in Rule Designer UI
    Improve TooManyMethods rule - thanks to a patch from Riku Nykanen
    Improve DoNotCallSystemExit - thanks to a patch from Steven Christou
    Fix false negative for UseArraysAsList when the array was passed as method parameter - thanks to Andy Throgmorton
    Enhanced SuspiciousEqualsMethodName rule - thanks to Andy Throgmorton
    Add a script to launch CPDGUI on Unix system - thanks to Tom Wheeler

    New Rule:
        Basic ruleset: DontCallThreadRun - thanks to Andy Throgmorton
        Logging with Jakarta Commons ruleset: GuardDebugLogging


## September 14, 2011 - 4.2.6:

    Fixed bug 2920057 - False + : CloseRessource whith an external getter
    Fixed bug 1808110 - Fixed performance issue on PreserveStackTrace
    Fixed bug 2832322 -  cpd.xml file tag path attribute should be entity-encoded
    Fixed bug 2590258 - NPE with nicerhtml output
    Fixed bug 2317099 - False + in SimplifyCondition
    Fixed bug 2606609 - False "UnusedImports" positive in package-info.java
    Fixed bug 2645268 - ClassCastException in UselessOperationOnImmutable.getDeclaration
    Fixed bug 2724653 - AvoidThreadGroup reports false positives
    Fixed bug 2835074 - False -: DoubleCheckedLocking with reversed null check
    Fixed bug 2826119 - False +: DoubleCheckedLocking warning with volatile field
    Fixed bug 2904832 - Type resolution not working for ASTType when using an inner class

    Modify (and hopefully fixed) CPD algorithm thanks to a patch from Juan Jesús García de Soria.
    Correct -benchmark reporting of Rule visits via the RuleChain
    Fix issue with Type Resolution incorrectly handling of Classes with same name as a java.lang Class.
    The JSP/JSF parser can now parse Unicode input.
    The JSP/JSP parser can now handle <script>...</script> tags.  The AST HtmlScript node contains the content.
    Added Ecmascript as a supported language for CPD.
    Improved include/exclude pattern matching performance for ends-with type patterns.

    Dependencies updates: asm updated to 3.2

    Android ruleset: CallSuperLast rule now also checks for finish() redefinitions

    New rule:
        Android: DoNotHardCodeSDCard
        Controversial : AvoidLiteralsInIfCondition (patch 2591627), UseConcurrentHashMap
        StrictExceptions : AvoidCatchingGenericException, AvoidLosingExceptionInformation
        Naming : GenericsNaming
        JSP: NoInlineScript


## February 08, 2009 - 4.2.5:

    Enhanced logging in the ClassTypeResolver to provide more detailed messaging.
    Fixed bug 2315623 - @SuppressWarnings("PMD.UseSingleton") has no effect
    Fixed bug 2230809 - False +: ClassWithOnlyPrivateConstructorsShouldBeFinal
    Fixed bug 2338341 - ArrayIndexOutOfBoundsException in CPD (on Ruby)
    Fixed bug 2315599 - False +: UseSingleton with class containing constructor
    Fixed bug 1955852 - false positives for UnusedPrivateMethod & UnusedLocalVariable
    Fixed bug 2404700 - UseSingleton should not act on enums
    Fixed bug 2225474 - VariableNamingConventions does not work with nonprimitives
    Fixed bug 1609038 - Xslt report generators break if path contains "java"
    Fixed bug - JUnitTestsShouldIncludeAssert now detects Junit 4 Assert.assert...  constructs
    Fixed bug 2142986 - UselessOverridingMethod doesn't consider annotations
    Fixed bug 2027626 - False + : AvoidFinalLocalVariable

    New rule:
        StrictExceptions : AvoidThrowingNewInstanceOfSameException
    New ruleset:
        android.xml: new rules specific to the Android platform


## October 12, 2008 - 4.2.4:

    Fixed bug 1481051 - false + UnusedNullCheckInEquals (and other false positives too)
    Fixed bug 1943204 - Ant task: <ruleset> path should be relative to Ant basedir
    Fixed bug 2139720 - Exception in PMD Rule Designer for inline comments in source
    Fixed patch 2075906 - Add toString() to the rule UnnecessaryWrapperObjectCreation
    Fixed ClassCastException on generic method in BeanMembersShouldSerialize
    Fixed ClassCastException in symbol table code


## August 31, 2008 - 4.2.3:

    JavaCC generated artifacts updated to JavaCC 4.1d1.
    Java grammar enhanced to include AnnotationMethodDeclaration as parent node of method related children of AnnotationTypeMemberDeclaration
    Fixes for exclude-pattern
    Updates to RuleChain to honor RuleSet exclude-pattern
    Upgrading UselessOperationOnImmutable to detect more use cases, especially on String and fix false positives
    Fixed bug 1988829 - Violation reported without source file name (actually a fix to ConsecutiveLiteralAppends)
    Fixed bug 1989814 - false +: ConsecutiveLiteralAppends
    Fixed bug 1977230 - false positive: UselessOverridingMethod
    Fixed bug 1998185 - BeanMembersShouldSerialize vs @SuppressWarnings("serial")
    Fixed bug 2002722 - false + in UseStringBufferForStringAppends
    Fixed bug 2056318 - False positive for AvoidInstantiatingObjectsInLoops
    Fixed bug 1977438 - False positive for UselessStringValueOf
    Fixed bug 2050064 - False + SuspiciousOctalEscape with backslash literal
    Fixed bug 1556594 - Wonky detection of NullAssignment
    Optimizations and false positive fixes in PreserveStackTrace
    @SuppressWarnings("all") disables all warnings
    All comment types are now stored in ASTCompilationUnit, not just formal ones
    Fixed false negative in UselessOverridingMethod
    Fixed handling of escape characters in UseIndexOfChar and AppendCharacterWithChar

    New rule:
        Basic ruleset:  EmptyInitializer


## May 20, 2008 - 4.2.2:

    Fixed false positive in UnusedImports: javadoc comments are parsed to check @see and other tags
    Fixed parsing bug: constant fields in annotation classes
    Bug fix: NPE in MoreThanOneLogger
    UnnecessaryParentheses now checks all expressions, not just return statements


## April 11, 2008 - 4.2.1:

    '41' and '42' shortcuts for rulesets added
    Fixed bug 1928009 - Error using migration ruleset in PMD 4.2
    Fixed bug 1932242 - EmptyMethodInAbstractClassShouldBeAbstract false +
    Fixed bug 1808110 - PreserveStackTrace

    AvoidDuplicateLiteralRule now has 'skipAnnotations' boolean property
    ruleset.dtd and ruleset_xml_schema.xsd added to jar file in rulesets directory
    Update RuleSetWriter to handle non-Apache TRAX implementations, add an option to not use XML Namespaces
    Added file encoding option to CPD GUI, which already existed for the command line and Ant
    bin and java14/bin scripts:
        retroweaver version was not correct in java14/bin scripts
        support for extra languages in cpd.sh
        standard unix scripts can be used with cygwin


## March 25, 2008 - 4.2:

    Fixed bug 1920155 - CheckResultSet: Does not pass for loop conditionals

## March 21, 2008 - 4.2rc2:

    Fixed bug 1912831 - False + UnusedPrivateMethod with varargs
    Fixed bug 1913536 - Rule Designer does not recognize JSP(XML)
    Add -auxclasspath option for specifying Type Resolution classpath from command line and auxclasspath nested element for ant task.
    Fixed formatting problems in loggers.

    Ant task upgrade:
        Added a new attribute 'maxRuleCount' to indicate whether or not to fail the build if PMD finds that much violations.


## March 07, 2008 - 4.2rc1:

    Fixed bug 1866198 - PMD should not register global Logger
    Fixed bug 1843273 - False - on SimplifyBooleanReturns
    Fixed bug 1848888 - Fixed false positive in UseEqualsToCompareStrings
    Fixed bug 1874313 - Documentation bugs
    Fixed bug 1855409 - False + in EmptyMethodInAbstractClassShouldBeAbstract
    Fixed bug 1888967 - Updated xpath query to detect more "empty" methods.
    Fixed bug 1891399 - Check for JUnit4 test method fails
    Fixed bug 1894821 - False - for Test Class without Test Cases
    Fixed bug 1882457 - PositionLiteralsFirstInComparisons rule not working OK
    Fixed bug 1842505 - XML output incorrect for inner classes
    Fixed bug 1808158 - Constructor args could also be final
    Fixed bug 1902351 - AvoidReassigningParameters not identify parent field
    Fixed other false positives in EmptyMethodInAbstractClassShouldBeAbstract
    Fixed other issues in SimplifyBooleanReturns
    Modified AvoidReassigningParameter to also check constructor arguments for reassignement

    New rules:
        Basic ruleset: AvoidMultipleUnaryOperators
        Controversial ruleset: DoNotCallGarbageCollectionExplicitly,UseObjectForClearerAPI
        Design ruleset : ReturnEmptyArrayRatherThanNull,TooFewBranchesForASwitchStatement,AbstractClassWithoutAnyMethod
        Codesize : TooManyMethods
        StrictExceptions : DoNotThrowExceptionInFinally
        Strings : AvoidStringBufferField

    Rule upgrade:
        CyclomaticComplexity now can be configured to display only class average complexity or method complexity, or both.

    Designer upgrade:
        A new panel for symbols and a tooltips on AST node that displays line, column and access node attributes (private,
        static, abstract,...)

    1.7 added as a valid option for targetjdk.
    New elements under <ruleset>: <exclude-pattern> to match files exclude from processing, with <include-pattern> to override.
    Rules can now be written which produce violations based upon aggregate file processing (i.e. cross/multiple file violations).
    PMD Rule Designer can now shows Symbol Table contents for the selected AST node.
    PMD Rule Designer shows position info in tooltip for AST nodes and highlights matching code for selected AST node in code window.
    CPD Ant task will report to System.out when 'outputFile' not given.
    RuleSetWriter class can be used to Serialize a RuleSet to XML in a standard fashion.  Recommend PMD IDE plugins standardize their behavior.
    retroweaver updated to version 2.0.5.


## November 17, 2007 - 4.1:

    Fixed annotation bug: ClassCastException when a formal parameter had multiple annotations
    Added a Visual Studio renderer for CPD; just use "--format vs".
    Dependencies updates: asm to 3.1, retroweaver to 2.0.2, junit to 4.4
    new ant target ("regress") to test regression bugs only


## November 01, 2007 - 4.1rc1:

    New rules:
        Basic ruleset: AvoidUsingHardCodedIP,CheckResultSet
        Controversial ruleset: AvoidFinalLocalVariable,AvoidUsingShortType,AvoidUsingVolatile,AvoidUsingNativeCode,AvoidAccessibilityAlteration
        Design ruleset: ClassWithOnlyPrivateConstructorsShouldBeFinal,EmptyMethodInAbstractClassShouldBeAbstract
        Imports ruleset: TooManyStaticImports
        J2ee ruleset: DoNotCallSystemExit, StaticEJBFieldShouldBeFinal,DoNotUseThreads
        Strings ruleset: UseEqualsToCompareStrings

    Fixed bug 674394  - fixed false positive in DuplicateImports for disambiguation import
    Fixed bug 631681  - fixed false positive in UnusedPrivateField when field is accessed by outer class
    Fixed bug 985989  - fixed false negative in ConstructorCallsOverridableMethod for inner static classes
    Fixed bug 1409944 - fixed false positive in SingularField for lock objects
    Fixed bug 1472195 - fixed false positives in PositionLiteralsFirstInComparisons when the string is used as a parameter
    Fixed bug 1522517 - fixed false positive in UselessOverridingMethod for clone method
    Fixed bug 1744065 - fixed false positive in BooleanInstantiation when a custom Boolean is used
    Fixed bug 1765613 - fixed NullPointerException in CloneMethodMustImplementCloneable when checking enum
    Fixed bug 1740480 - fixed false positive in ImmutableField when the assignment is inside an 'if'
    Fixed bug 1702782 - fixed false positive in UselessOperationOnImmutable when an Immutable on which an operation is performed is compareTo'd
    Fixed bugs 1764288/1744069/1744071 - When using Type Resolution all junit test cases will notice if you're using an extended TestCase
    Fixed bug 1793215 - pmd-nicerhtml.xsl does not display line numbers
    Fixes bug 1796928 - fixed false positive in AvoidThrowingRawExceptionTypes, when a Type name is the same as a RawException.
    Fixed bug 1811506 - False - : UnusedFormalParameter (property "checkall" needs to be set)
    Fixed false negative in UnnecessaryCaseChange

    The Java 1.5 source code parser is now the default for testcode used in PMD's unit tests.
    Added TypeResolution to the XPath rule. Use typeof function to determine if a node is of a particular type
    Adding a GenericLiteralChecker, a generic rule that require a regex as property. It will log a violation if a Literal is matched by the regex. See the new rule AvoidUsingHardCodedIP for an example.
    Adding support for multiple line span String in CPD's AbstractTokenizer, this may change, for the better, CPD's Ruby parsing.
    This release adds 'nicehtml', with the plan for the next major release to make nicehtml->html, and html->oldhtml. This feature uses an XSLT transformation, default stylesheet maybe override with '-xslt filename'.
    New CPD command line feature : Using more than one directory for sources. You can now have several '--files' on the command line.
    SingularField greatly improved to generate very few false positives (none?). Moved from controversial to design. Two options added to restore old behaviour (mostly).
    Jaxen updated to 1.1.1, now Literal[@Image='""'] works in XPath expressions.


## July 20, 2007 - 4.0

    Fixed bug 1697397 - fixed false positives in ClassCastExceptionWithToArray
    Fixed bug 1728789 - removed redundant rule AvoidNonConstructorMethodsWithClassName; MethodWithSameNameAsEnclosingClass is faster and does the same thing.


## July 12, 2007 - 4.0rc2:

    New rules:
        Typeresolution ruleset: SignatureDeclareThrowsException - re-implementation using the new Type Resolution facility (old rule is still available)
    Fixed bug 1698550 - CloneMethodMustImplementCloneable now accepts a clone method that throws CloneNotSupportedException in a final class
    Fixed bug 1680568 - The new typeresolution SignatureDeclareThrowsException rule now ignores setUp and tearDown in JUnit 4 tests and tests that do not directly extend TestCase
    The new typeresolution SignatureDeclareThrowsException rule can now ignore JUnit classes completely by setting the IgnoreJUnitCompletely property
    Fixed false positive in UselessOperationOnImmutable
    PMD now defaults to using a Java 1.5 source code parser.


## June 22, 2007 - 4.0rc1:

    New rules:
        Strict exception ruleset: DoNotExtendJavaLangError
        Basic JSP ruleset: JspEncoding
        J2EE ruleset: MDBAndSessionBeanNamingConvention, RemoteSessionInterfaceNamingConvention, LocalInterfaceSessionNamingConvention, LocalHomeNamingConvention, RemoteInterfaceNamingConvention
        Optimizations ruleset: AddEmptyString
        Naming: BooleanGetMethodName
    New rulesets:
        Migrating To JUnit4: Rules that help move from JUnit 3 to JUnit 4
    Fixed bug 1670717 - 'Copy xml to clipboard' menu command now works again in the Designer
    Fixed bug 1618858 - PMD no longer raises an exception on XPath like '//ConditionalExpression//ConditionalExpression'
    Fixed bug 1626232 - Commons logging rules (ProperLogger and UseCorrectExceptionLogging) now catch more cases
    Fixed bugs 1626201 & 1633683 - BrokenNullCheck now catches more cases
    Fixed bug 1626715 - UseAssertSameInsteadOfAssertTrue now correctly checks classes which contain the null constant
    Fixed bug 1531216 - ImmutableField. NameOccurrence.isSelfAssignment now recognizes this.x++ as a self assignment
    Fixed bug 1634078 - StringToString now recognizes toString on a String Array, rather than an element.
    Fixed bug 1631646 - UselessOperationOnImmutable doesn't throw on variable.method().variable.
    Fixed bug 1627830 - UseLocaleWithCaseConversions now works with compound string operations
    Fixed bug 1613807 - DontImportJavaLang rule allows import to Thread inner classes
    Fixed bug 1637573 - The PMD Ant task no longer closes System.out if toConsole is set
    Fixed bug 1451251 - A new UnusedImports rule, using typeresolution, finds unused import on demand rules
    Fixed bug 1613793 - MissingSerialVersionUID rule now doesn't fire on abstract classes
    Fixed bug 1666646 - ImmutableField rule doesn't report against volatile variables
    Fixed bug 1693924 - Type resolution now works for implicit imports
    Fixed bug 1705716 - Annotation declarations now trigger a new scope level in the symbol table.
    Fixed bug 1743938 - False +: InsufficientStringBufferDeclaration with multiply
    Fixed bug 1657957 - UseStringBufferForStringAppends now catches self-assignments
    Applied patch 1612455 - RFE 1411022 CompareObjectsWithEquals now catches the case where comparison is against new Object
    Implemented RFE 1562230 - Added migration rule to check for instantiation of Short/Byte/Long
    Implemented RFE 1627581 - SuppressWarnings("unused") now suppresses all warnings in unusedcode.xml
    XPath rules are now chained together for an extra speedup in processing
    PMD now requires JDK 1.5 to be compiled. Java 1.4 support is provided using Retroweaver
    - PMD will still analyze code from earlier JDKs
    - to run pmd with 1.4, use the files from the java14 directory (weaved pmd jar and support files)
    TypeResolution now looks at some ASTName nodes.
    Memory footprint reduced: most renderers now use less memory by generating reports on the fly.
    Ant task now takes advantage of multithreading code and on the fly renderers
    Ant task now logs more debug info when using -verbose
    PMD command line now has -benchmark: output a benchmark report upon completion; default to System.err


## December 19, 2006 - 3.9:

    New rules:
        Basic ruleset: BigIntegerInstantiation, AvoidUsingOctalValues
        Codesize ruleset: NPathComplexity, NcssTypeCount, NcssMethodCount, NcssConstructorCount
        Design ruleset: UseCollectionIsEmpty
        Strings ruleset: StringBufferInstantiationWithChar
        Typeresolution ruleset: Loose Coupling - This is a re-implementation using the new Type Resolution facility
    Fixed bug 1610730 - MisplacedNullCheck now catches more cases
    Fixed bug 1570915 - AvoidRethrowingException no longer reports a false positive for certain nested exceptions.
    Fixed bug 1571324 - UselessStringValueOf no longer reports a false positive for additive expressions.
    Fixed bug 1573795 - PreserveStackTrace doesn't throw CastClassException on exception with 0 args
    Fixed bug 1573591 - NonThreadSafeSingleton doesn't throw NPE when using this keyword
    Fixed bug 1371753 - UnnecessaryLocalBeforeReturn is now less aggressive in its reporting.
    Fixed bug 1566547 - Annotations with an empty MemberValueArrayInitializer are now parsed properly.
    Fixed bugs 1060761 / 1433119 & RFE 1196954 - CloseResource now takes an optional parameter to identify closure methods
    Fixed bug 1579615 - OverrideBothEqualsAndHashcode no longer throws an Exception on equals methods that don't have Object as a parameter type.
    Fixed bug 1580859 - AvoidDecimalLiteralsInBigDecimalConstructor now catches more cases.
    Fixed bug 1581123 - False +: UnnecessaryWrapperObjectCreation.
    Fixed bug 1592710 - VariableNamingConventions no longer reports false positives on certain enum declarations.
    Fixed bug 1593292 - The CPD GUI now works with the 'by extension' option selected.
    Fixed bug 1560944 - CPD now skips symlinks.
    Fixed bug 1570824 - HTML reports generated on Windows no longer contain double backslashes.  This caused problems when viewing those reports with Apache.
    Fixed bug 1031966 - Re-Implemented CloneMethodMustImplementCloneable as a typeresolution rule. This rule can now detect super classes/interfaces which are cloneable
    Fixed bug 1571309 - Optional command line options may be used either before or after the mandatory arguments
    Applied patch 1551189 - SingularField false + for initialization blocks
    Applied patch 1573981 - false + in CloneMethodMustImplementCloneable
    Applied patch 1574988 - false + in OverrideBothEqualsAndHashcode
    Applied patch 1583167 - Better test code management. Internal JUnits can now be written in XML's
    Applied patch 1613674 - Support classpaths with spaces in pmd.bat
    Applied patch 1615519 - controversial/DefaultPackage XPath rule is wrong
    Applied patch 1615546 - Added option to command line to write directly to a file
    Implemented RFE 1566313 - Command Line now takes minimumpriority attribute to filter out rulesets
    PMD now requires JDK 1.4 to run
    - PMD will still analyze code from earlier JDKs
    - PMD now uses the built-in JDK 1.4 regex utils vs Jakarta ORO
    - PMD now uses the JDK javax.xml APIs rather than being hardcoded to use Xerces and Xalan
    SummaryHTML Report changes from Brent Fisher - now contains linePrefix to support source output from javadoc using "linksource"
    Fixed CSVRenderer - had flipped line and priority columns
    Fixed bug in Ant task - CSV reports were being output as text.
    Fixed false negatives in UseArraysAsList.
    Fixed several JDK 1.5 parsing bugs.
    Fixed several rules (exceptions on jdk 1.5 and jdk 1.6 source code).
    Fixed array handling in AvoidReassigningParameters and UnusedFormalParameter.
    Fixed bug in UselessOverridingMethod: false + when adding synchronization.
    Fixed false positives in LocalVariableCouldBeFinal.
    Fixed false positives in MethodArgumentCouldBeFinal.
    Modified annotation suppression to use @SuppressWarning("PMD") to suppress all warnings and @SuppressWarning("PMD.UnusedLocalVariable") to suppress a particular rule's warnings.
    Rules can now call RuleContext.getSourceType() if they need to make different checks on JDK 1.4 and 1.5 code.
    CloseResource rule now checks code without java.sql import.
    ArrayIsStoredDirectly rule now checks Constructors
    undo/redo added to text areas in Designer.
    Better 'create rule XML' panel in Designer.
    use of entrySet to iterate over Maps.
    1.6 added as a valid option for targetjdk.
    PMD now allows rules to use Type Resolution. This was referenced in patch 1257259.
    Renderers use less memory when generating reports.
    New DynamicXPathRule class to speed up XPath based rules by providing a base type for the XPath expression.
    Multithreaded processing on multi core or multi cpu systems.
    Performance Refactoring, XPath rules re-written as Java:
        AssignmentInOperand
        AvoidDollarSigns
        DontImportJavaLang
        DontImportSun
        MoreThanOneLogger
        SuspiciousHashcodeMethodName
        UselessStringValueOf


## October 4, 2006 - 3.8:

    New rules:
        Basic ruleset: BrokenNullCheck
        Strict exceptions ruleset: AvoidRethrowingException
        Optimizations ruleset: UnnecessaryWrapperObjectCreation
        Strings ruleset: UselessStringValueOf
    Fixed bug 1498910 - AssignmentInOperand no longer has a typo in the message.
    Fixed bug 1498960 - DontImportJavaLang no longer reports static imports of java.lang members.
    Fixed bug 1417106 - MissingBreakInSwitch no longer flags stmts where every case has either a return or a throw.
    Fixed bug 1412529 - UncommentedEmptyConstructor no longer flags private constructors.
    Fixed bug 1462189 - InsufficientStringBufferDeclaration now resets when it reaches setLength the same way it does at a Constructor
    Fixed bug 1497815 - InsufficientStringBufferDeclaration rule now takes the length of the constructor into account, and adds the length of the initial string to its initial length
    Fixed bug 1504842 - ExceptionSignatureDeclaration no longer flags methods starting with 'test'.
    Fixed bug 1516728 - UselessOverridingMethod no longer raises an NPE on methods that use generics.
    Fixed bug 1522054 - BooleanInstantiation now detects instantiations inside method calls.
    Fixed bug 1522056 - UseStringBufferForStringAppends now flags appends which occur in static initializers and constructors
    Fixed bug 1526530 - SingularField now finds fields which are hidden at the method or static level
    Fixed bug 1529805 - UnusedModifier no longer throws NPEs on JDK 1.5 enums.
    Fixed bug 1531593 - UnnecessaryConversionTemporary no longer reports false positives when toString() is invoked inside the call to 'new Long/Integer/etc()'.
    Fixed bug 1512871 - Improved C++ tokenizer error messages - now they include the filename.
    Fixed bug 1531152 - CloneThrowsCloneNotSupportedException now reports the proper line number.
    Fixed bug 1531236 - IdempotentOperations reports fewer false positives.
    Fixed bug 1544564 - LooseCoupling rule now checks for ArrayLists
    Fixed bug 1544565 - NonThreadSafeSingleton now finds if's with compound statements
    Fixed bug 1561784 - AbstractOptimizationRule no longer throws ClassCastExceptions on certain postfix expressions.
    Fixed a bug in AvoidProtectedFieldInFinalClass - it no longer reports false positives for protected fields in inner classes.
    Fixed a bug in the C++ grammar - the tokenizer now properly recognizes macro definitions which are followed by a multiline comment.
    Modified C++ tokenizer to use the JavaCC STATIC option; this results in about a 30% speedup in tokenizing.
    Implemented RFE 1501850 - UnusedFormalParameter now catches cases where a parameter is assigned to but not used.
    Applied patch 1481024 (implementing RFE 1490181)- NOPMD messages can now be reported with a user specified msg, e.g., //NOPMD - this is expected
    Added JSP support to the copy/paste detector.
    Placed JSF/JSP ruleset names in rulesets/jsprulesets.properties
    Added the image to the ASTEnumConstant nodes.
    Added new XSLT stylesheet for CPD XML->HTML from Max Tardiveau.
    Refactored UseIndexOfChar to extract common functionality into AbstractPoorMethodCall.
    Improved CPD GUI and Designer look/functionality; thanks to Brian Remedios for the changes!
    Rewrote the NOPMD mechanism to collect NOPMD markers as the source file is tokenized.  This eliminates an entire scan of each source file.
    Applied patch from Jason Bennett to enhance CyclomaticComplexity rule to account for conditional or/and nodes, do stmts, and catch blocks.
    Applied patch from Xavier Le Vourch to reduce false postives from CloneMethodMustImplementCloneable.
    Updated Jaxen library to beta 10.
    Performance Refactoring, XPath rules re-written as Java:
        BooleanInstantiation
        UselessOperationOnImmutable
        OverrideBothEqualsAndHashcode
        UnnecessaryReturn
        UseStringBufferForStringAppends
        SingularField
        NonThreadSafeSingleton


## June 1, 2006 - 3.7:

    New rules:
        Basic-JSP ruleset: DuplicateJspImport
        Design ruleset: PreserveStackTrace
        J2EE ruleset: UseProperClassLoader
    Implemented RFE 1462019 - Add JSPs to Ant Task
    Implemented RFE 1462020 - Add JSPs to Designer
    Fixed bug 1461426 InsufficientStringBufferDeclaration does not consider paths
    Fixed bug 1462184 False +: InsufficientStringBufferDeclaration - wrong size
    Fixed bug 1465574 - UnusedPrivateMethod no longer reports false positives when a private method is called from a method with a parameter of the same name.
    Fixed bug 1114003 - UnusedPrivateMethod no longer reports false positives when two methods have the same name and number of arguments but different types.  The fix causes PMD to miss a few valid cases, but, c'est la vie.
    Fixed bug 1472843 - UnusedPrivateMethod no longer reports false positives when a private method is only called from a method that contains a variable with the same name as that method.
    Fixed bug 1461442 - UseAssertSameInsteadOfAssertTrue now ignores comparisons to null; UseAssertNullInsteadOfAssertTrue will report those.
    Fixed bug 1474778 - UnnecessaryCaseChange no longer flags usages of toUpperCase(Locale).
    Fixed bug 1423429 - ImmutableField no longer reports false positives on variables which can be set via an anonymous inner class that is created in the constructor.
    Fixed major bug in CPD; it was not picking up files other than .java or .jsp.
    Fixed a bug in CallSuperInConstructor; it now checks inner classes/enums more carefully.
    Fixed a bug in VariableNamingConventions; it was not setting the warning message properly.
    Fixed bug in C/C++ parser; a '$' is now allowed in an identifier.  This is useful in VMS.
    Fixed a symbol table bug; PMD no longer crashes on enumeration declarations in the same scope containing the same field name
    Fixed a bug in ASTVariableDeclaratorId that triggered a ClassCastException if a annotation was used on a parameter.
    Added RuleViolation.getBeginColumn()/getEndColumn()
    Added an optional 'showSuppressed' item to the Ant task; this is false by default and toggles whether or not suppressed items are shown in the report.
    Added an IRuleViolation interface and modified various code classes (include Renderer implementations and Report) to use it.
    Modified JJTree grammar to use conditional node descriptors for various expression nodes and to use node suppression for ASTModifier nodes; this replaces a bunch of DiscardableNodeCleaner hackery.  It also fixed bug 1445026.
    Modified C/CPP grammar to only build the lexical analyzer; we're not using the parser for CPD, just the token manager.  This reduces the PMD jar file size by about 50 KB.


## March 29, 2006 - 3.6:

    New rules:
        Basic ruleset: AvoidThreadGroup
        Design ruleset: UnsynchronizedStaticDateFormatter
        Strings ruleset: InefficientEmptyStringCheck, InsufficientStringBufferDeclaration
        JUnit ruleset: SimplifyBooleanAssertion
        Basic-JSF ruleset: DontNestJsfInJstlIteration
        Basic-JSP ruleset: NoLongScripts, NoScriptlets, NoInlineStyleInformation, NoClassAttribute, NoJspForward, IframeMissingSrcAttribute, NoHtmlComments
    Fixed bug 1414985 - ConsecutiveLiteralAppends now checks for intervening references between appends.
    Fixed bug 1418424 - ConsecutiveLiteralAppends no longer flags appends in separate methods.
    Fixed bug 1416167 - AppendCharacterWithChar now catches cases involving escaped characters.
    Fixed bug 1421409 - Ant task now has setter to allow minimumPriority attribute to be used.
    Fixed bug 1416164 - InefficientStringBuffering no longer reports false positives on the three argument version of StringBuffer.append().
    Fixed bug 1415326 - JUnitTestsShouldContainAsserts no longer errors out on JDK 1.5 generics.
    Fixed bug 1415333 - CyclomaticComplexity no longer errors out on JDK 1.5 enums.
    Fixed bug 1415663 - PMD no longer fails to parse abstract classes declared in a method.
    Fixed bug 1433439 - UseIndexOfChar no longer reports false positives on case like indexOf('a' + getFoo()).
    Fixed bug 1435218 - LoggerIsNotStaticFinal no longer reports false positives for local variables.
    Fixed bug 1413745 - ArrayIsStoredDirectly no longer reports false positives for array deferences.
    Fixed bug 1435751 - Added encoding type of UTF-8 to the CPD XML file.
    Fixed bug 1441539 - ConsecutiveLiteralAppends no longer flags appends() involving method calls.
    Fixed bug 1339470 - PMD no longer fails to parse certain non-static initializers.
    Fixed bug 1425772 - PMD no longer fails with errors in ASTFieldDeclaration when parsing some JDK 1.5 code.
    Fixed bugs 1448123 and 1449175 - AvoidFieldNameMatchingTypeName, SingularField, TooManyFields, and AvoidFieldNameMatchingMethodName no longer error out on enumerations.
    Fixed bug 1444654 - migrating_to_14 and migrating_to_15 no longer refer to rule tests.
    Fixed bug 1445231 - TestClassWithoutTestCases: no longer flags abstract classes.
    Fixed bug 1445765 - PMD no longer uses huge amounts of memory.  However, you need to use RuleViolation.getBeginLine(); RuleViolation.getNode() is no more.
    Fixed bug 1447295 - UseNotifyAllInsteadOfNotify no longer flags notify() methods that have a parameter.
    Fixed bug 1455965 - MethodReturnsInternalArray no longer flags variations on 'return new Object[] {}'.
    Implemented RFE 1415487 - Added a rulesets/releases/35.xml ruleset (and similar rulesets for previous releases) contains rules new to PMD v3.5
    Wouter Zelle fixed a false positive in NonThreadSafeSingleton.
    Wouter Zelle fixed a false positive in InefficientStringBuffering.
    The CPD Ant task now supports an optional 'language' attribute.
    Removed some ill-advised casts from the parsers.
    Fixed bug in CallSuperInConstructor; it no longer flag classes without extends clauses.
    Fixed release packaging; now entire xslt/ directory contents are included.
    Added more XSLT from Dave Corley - you can use them to filter PMD reports by priority level.
    You can now access the name of a MemberValuePair node using getImage().
    PositionLiteralsFirstInComparisons was rewritten in XPath.
    Added a getVersionString method to the TargetJDKVersion interface.
    Added an option '--targetjdk' argument to the Benchmark utility.
    Applied a patch from Wouter Zelle to clean up the Ant Formatter class, fix a TextRenderer bug, and make toConsole cleaner.
    Rewrote AvoidCallingFinalize in Java; fixed bug and runs much faster, too.
    Uploaded ruleset schema to http://pmd.sf.net/ruleset_xml_schema.xsd
    UseIndexOfChar now catches cases involving lastIndexOf.
    Rules are now run in the order in which they're listed in a ruleset file.  Internally, they're now stored in a List vs a Set, and RuleSet.getRules() now returns a Collection.
    Upgraded to JUnit version 3.8.2.


## Jan 25, 2006 - 3.5:

    New rules:
     Basic ruleset: UselessOperationOnImmutable, MisplacedNullCheck, UnusedNullCheckInEquals
     Migration ruleset: IntegerInstantiation
     JUnit ruleset: UseAssertNullInsteadOfAssertTrue
     Strings ruleset: AppendCharacterWithChar, ConsecutiveLiteralAppends, UseIndexOfChar
     Design ruleset: AvoidConstantsInterface
     Optimizations ruleset: UseArraysAsList, AvoidArrayLoops
     Controversial ruleset: BooleanInversion
    Fixed bug 1371980 - InefficientStringBuffering no longer flags StringBuffer methods other than append().
    Fixed bug 1277373 - InefficientStringBuffering now catches more cases.
    Fixed bug 1376760 - InefficientStringBuffering no longer throws a NullPointerException when processing certain expressions.
    Fixed bug 1371757 - Misleading example in AvoidSynchronizedAtMethodLevel
    Fixed bug 1373510 - UseAssertSameInsteadOfAssertTrue no longer has a typo in its message, and its message is more clear.
    Fixed bug 1375290 - @SuppressWarnings annotations are now implemented correctly; they accept one blank argument to suppress all warnings.
    Fixed bug 1376756 - UselessOverridingMethod no longer throws an exception on overloaded methods.
    Fixed bug 1378358 - StringInstantiation no longer throws ClassCastExceptions on certain allocation patterns.
    Fixed bug 1371741 - UncommentedEmptyConstructor no longer flags constructors that consist of a this() or a super() invocation.
    Fixed bug 1277373 - InefficientStringBuffering no longer flags concatenations that involve a static final String.
    Fixed bug 1379701 - CompareObjectsWithEquals no longer flags comparisons of array elements.
    Fixed bug 1380969 - UnusedPrivateMethod no longer flags private static methods that are only invoked in a static context from a field declaration.
    Fixed bug 1384594 - Added a 'prefix' property for BeanMembersShouldSerializeRule
    Fixed bug 1394808 - Fewer missed hits for AppendCharacterWithChar and InefficientStringBuffering, thanks to Allan Caplan for catching these
    Fixed bug 1400754 - A NPE is no longer thrown on certain JDK 1.5 enum usages.
    Partially fixed bug 1371753 - UnnecessaryLocalBeforeReturn message now reflects the fact that that rule flags all types
    Fixed a bug in UseStringBufferLength; it no longers fails with an exception on expressions like StringBuffer.toString.equals(x)
    Fixed a bug in CPD's C/C++ parser so that it no longer fails on multi-line literals; thx to Tom Judge for the nice patch.
    CPD now recognizes '--language c' and '--language cpp' as both mapping to the C/C++ parser.
    Modified renderers to support disabling printing of suppressed warnings.  Introduced a new AbstractRenderer class that all Renderers can extends to get the current behavior - that is, suppressed violations are printed.
    Implemented RFE 1375435 - you can now embed regular expressions inside XPath rules, i.e., //ClassOrInterfaceDeclaration[matches(@Image, 'F?o')].
    Added current CLASSPATH to pmd.bat.
    UnusedFormalParameter now catches unused constructor parameters, and its warning message now reflects whether it caught a method or a constructor param.
    Rebuilt JavaCC parser with JavaCC 4.0.
    Added jakarta-oro-2.0.8.jar as a new dependency to support regular expression in XPath rules.
    Ant task now supports a 'minimumPriority' attribute; only rules with this priority or higher will be run.
    Renamed Ant task 'printToConsole' attribute to 'toConsole' and it can only be used inside a formatter element.
    Added David Corley's Javascript report, more details are here: http://tomcopeland.blogs.com/juniordeveloper/2005/12/demo_of_some_ni.html


## November 30, 2005 - 3.4:

    New rules:
     Basic ruleset: ClassCastExceptionWithToArray, AvoidDecimalLiteralsInBigDecimalConstructor
     Design ruleset: NonThreadSafeSingleton, UncommentedEmptyMethod, UncommentedEmptyConstructor
     Controversial ruleset: DefaultPackage
     Naming ruleset: MisleadingVariableName
     Migration ruleset: ReplaceVectorWithList, ReplaceHashtableWithMap, ReplaceEnumerationWithIterator, AvoidEnumAsIdentifier, AvoidAssertAsIdentifier
     Strings ruleset: UseStringBufferLength
    Fixed bug 1292745 - Removed unused source file ExceptionTypeChecking.java
    Fixed bug 1292609 - The JDK 1.3 parser now correctly handles certain 'assert' usages.  Also added a 'JDK 1.3' menu item to the Designer.
    Fixed bug 1292689 - Corrected description for UnnecessaryLocalBeforeReturn
    Fixed bug 1293157 - UnusedPrivateMethod no longer reports false positives for private methods which are only invoked from static initializers.
    Fixed bug 1293277 - Messages that used 'pluginname' had duplicated messages.
    Fixed bug 1291353 - ASTMethodDeclaration isPublic/isAbstract methods always return true.  The syntactical modifier - i.e., whether or not 'public' was used in the source code in the method declaration - is available via 'isSyntacticallyPublic' and 'isSyntacticallyAbstract'
    Fixed bug 1296544 - TooManyFields no longer checks the wrong property value.
    Fixed bug 1304739 - StringInstantiation no longer crashes on certain String constructor usages.
    Fixed bug 1306180 - AvoidConcatenatingNonLiteralsInStringBuffer no longer reports false positives on certain StringBuffer usages.
    Fixed bug 1309235 - TooManyFields no longer includes static finals towards its count.
    Fixed bug 1312720 - DefaultPackage no longer flags interface fields.
    Fixed bug 1312754 - pmd.bat now handles command line arguments better in WinXP.
    Fixed bug 1312723 - Added isSyntacticallyPublic() behavior to ASTFieldDeclaration nodes.
    Fixed bug 1313216 - Designer was not displaying 'final' attribute for ASTLocalVariableDeclaration nodes.
    Fixed bug 1314086 - Added logging-jakarta-commons as a short name for rulesets/logging-jakarta-commons.xml to SimpleRuleSetNameMapper.
    Fixed bug 1351498 - Improved UnnecessaryCaseChange warning message.
    Fixed bug 1351706 - CompareObjectsWithEquals now catches more cases.
    Fixed bug 1277373 (and 1347286) - InefficientStringBuffering now flags fewer false positives.
    Fixed bug 1363447 - MissingBreakInSwitch no longer reports false positives for switch statements where each switch label has a return statement.
    Fixed bug 1363458 - MissingStaticMethodInNonInstantiatableClass no longer reports cases where there are public static fields.
    Fixed bug 1364816 - ImmutableField no longer reports false positives for fields assigned in an anonymous inner class in a constructor.
    Implemented RFE 1311309 (and 1119854) - Suppressed RuleViolation counts are now included in the reports.
    Implemented RFE 1220371 - Rule violation suppression via annotations.  Per the JLS, @SuppressWarnings can be placed before the following nodes: TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE.
    Implemented RFE 1275547 - OverrideBothEqualsAndHashcode now skips Comparator implementations.
    Applied patch 1306999 - Renamed CloseConnection to CloseResource and added support for checking Statement and ResultSet objects.
    Applied patch 1344754 - EmptyCatchBlock now skips catch blocks that contain comments.  This is also requested in RFE 1347884.
    Renamed AvoidConcatenatingNonLiteralsInStringBuffer to InefficientStringBuffering; new name is a bit more concise.
    Modified LongVariable; now it has a property which can be used to override the minimum reporting value.
    Improved CPD XML report.
    CPD no longer skips header files when checking C/C++ code.
    Reworked CPD command line arguments; old-style arguments will still work for one more version, though.
    Lots of documentation improvements.


## September 15, 2005 - 3.3:

    New rules:
        Design: PositionLiteralsFirstInComparisons,  UnnecessaryLocalBeforeReturn
        Logging-jakarta-commons: ProperLogger
        Basic: UselessOverridingMethod
        Naming: PackageCase, NoPackage
        Strings: UnnecessaryCaseChange
    Implemented RFE 1220171 - rule definitions can now contain a link to an external URL for more information on that rule - for example, a link to the rule's web page.  Thanks to Wouter Zelle for designing and implementing this!
    Implemented RFE 1230685 - The text report now includes parsing errors even if no rule violations are reported
    Implemented RFE 787860 - UseSingleton now accounts for JUnit test suite declarations.
    Implemented RFE 1097090 - The Report object now contains the elapsed time for running PMD.  This shows up in the XML report as an elapsedTime attribute in the pmd element in the format '2m 5s' or '1h 5h 35s' or '25s' .
    Implemented RFE 1246338 - CPD now handles SCCS directories and NTFS junction points.
    Fixed bug 1226858 - JUnitAssertionsShouldIncludeMessage now checks calls to assertFalse.
    Fixed bug 1227001 - AvoidCallingFinalize no longer flags calls to finalize() within finalizers.
    Fixed bug 1229755 - Fixed typo in ArrayIsStoredDirectly description.
    Fixed bug 1229749 - Improved error message when an external rule is not found.
    Fixed bug 1224849 - JUnitTestsShouldContainAsserts no longer skips method declarations which include explicit throws clauses.
    Fixed bug 1225492 - ConstructorCallsOverridableMethod now reports the correct method name.  dvholten's examples in RFE 1235562 also helped with this a great deal.
    Fixed bug 1228589 - DoubleCheckedLocking and ExceptionSignatureDeclaration no longer throw ClassCastExceptions on method declarations that declare generic return types.
    Fixed bug 1235299 - NullAssignment no longer flags null equality comparisons in ternary expressions.
    Fixed bug 1235300 - NullAssignment no longer flags assignments to final fields.
    Fixed bug 1240201 - The UnnecessaryParentheses message is no longer restricted to return statements.
    Fixed bug 1242290 - The JDK 1.5 parser no longer chokes on nested enumerations with a constructor.
    Fixed bug 1242544 - SimplifyConditional no longer flags null checks that precede an instanceof involving an array dereference.
    Fixed bug 1242946 - ArrayIsStoredDirectly no longer reports false positives for equality expression comparisons.  As a bonus, its message now includes the variable name :-)
    Fixed bug 1232648 - MethodReturnsInternalArray no longer reports false positives on return statement expressions that involve method invocations on an internal array.
    Fixed bug 1244428 - MissingStaticMethodInNonInstantiatableClass no longer reports warnings for nested classes.  Some inner class cases will be missed, but false positives will be eliminated as well.
    Fixed bug 1244443 - EqualsNull now catches more cases.
    Fixed bug 1250949 - The JDK 1.5 parser no longer chokes on annotated parameters and annotated local variables.
    Fixed bug 1245139 - TooManyFields no longer throws a ClassCastException when processing anonymous classes.
    Fixed bug 1251256 - ImmutableField no longer skips assignments in try blocks on methods (which led to false positives).
    Fixed bug 1250949 - The JDK 1.5 parser no longer chokes on AnnotationTypeMemberDeclaration with a default value.
    Fixed bug 1245367 - ImmutableField no longer triggers on assignments in loops in constructors.
    Fixed bug 1251269 - AvoidConcatenatingNonLiteralsInStringBuffer no longer triggers on StringBuffer constructors like 'new StringBuffer(1 + getFoo());'
    Fixed bug 1244570 - AvoidConcatenatingNonLiteralsInStringBuffer no longer triggers on certain AST patterns involving local variable declarations inside Statement nodes.
    Fixed bug 695344 - StringInstantiation no longer triggers on the String(byte[]) constructor.
    Fixed bug 1114754 - UnusedPrivateMethod reports fewer false positives.
    Fixed bug 1290718 - Command line parameter documentation is now correct for targetjdk options.
    Applied patch 1228834 - XPath rules can now use properties to customize rules.  Thanks to Wouter Zelle for another great piece of work!
    Fixed a bug in RuleSetFactory that missed some override cases; thx to Wouter Zelle for the report and a fix.
    Fixed a bug in the grammar that didn't allow constructors to have type parameters, which couldn't parse some JDK 1.5 constructs.
    Fixed a bug in ImportFromSamePackage; now it catches the case where a class has an on-demand import for the same package it is in.
    Fixed a bug in CompareObjectsWithEquals; now it catches some local variable cases.
    Fixed a bug in CouplingBetweenObjects; it no longer triggers an exception (which is a bug in the symbol table layer) by calling getEnclosingClassScope() when the node in question isn't enclosed by one.
    Moved AvoidCallingFinalize from the design ruleset to the finalize ruleset and then deleted redundant ExplicitCallToFinalize rule from the finalize ruleset.
    Deleted redundant ExceptionTypeChecking rule from the strictexception ruleset; use AvoidInstanceofChecksInCatchClause in the design ruleset instead.
    Added some new XSLT scripts that create nifty HTML output; thanks to Wouter Zelle for the code.
    Improved UseCorrectExceptionLogging; thx to Wouter Zelle for the new XPath.
    Improved warning message from UnusedPrivateMethod.
    Improved EmptyIfStmt; now it catches the case where an IfStatement is followed by an EmptyStatement node.
    The Ant task now accepts the short names of rulesets (e.g., unusedcode for rulesets/unusedcode.xml).
    Removed unnecessary '.html' suffix from displayed filenames when the linkPrefix attribute is used with the HTML renderer.
    Added an optional 'description' attribute to the 'property' element in the ruleset XML files.
    Added a simplified SimpleNode.addViolation() method to reduce duplicated rule violation creation code.
    Moved from jaxen-1.0-fcs.jar/saxpath-1.0-fcs.jar to jaxen-1.1-beta-7.jar.  This yielded a 20% speed increase in the basic ruleset!


## June 21, 2005 - 3.2:

    New rules: UseCorrectExceptionLogging (logging-jakarta-commons ruleset), AvoidPrintStackTrace (logging-java ruleset), CompareObjectsWithEquals (design ruleset)
    Fixed bug 1201577 - PMD now correctly parses method declarations that return generic types.
    Fixed bug 1205709 - PMD no longer takes a long time to report certain parsing errors.
    Fixed bug 1052356 - ImmutableField no longer triggers on fields which are assigned to in a constructor's try statement.
    Fixed bug 1215854 - Package/class/method names are now filled in whenever possible, and the XML report includes all three.
    Fixed bug 1209719 - MethodArgumentCouldBeFinal no longer triggers on arguments which are modified using postfix or prefix expressions.  A bug in AvoidReassigningParameters was also fixed under the same bug id.
    Fixed bug 1188386 - MethodReturnsInternalArray no longer flags returning a local array declaration.
    Fixed bug 1172137 - PMD no longer locks up when generating a control flow graph for if statements with labelled breaks.
    Fixed bug 1221094 - JUnitTestsShouldContainAsserts no longer flags static methods.
    Fixed bug 1217028 - pmd.bat now correctly passes parameters to PMD.
    Implemented RFE 1188604 - AvoidThrowingCertainExceptionTypes has been split into AvoidThrowingRawExceptionTypes and AvoidThrowingNullPointerException.
    Implemented RFE 1188369 - UnnecessaryBooleanAssertion now checks for things like 'assertTrue(!foo)'.  These should be changed to 'assertFalse(foo)' for clarity.
    Implemented RFE 1199622 - UnusedFormalParameter now defaults to only checking private methods unless a 'checkall' property is set.
    Implemented RFE 1220314 - the symbol table now includes some rudimentary type information.
    Break and continue statement labels (if present) are placed in the image field.
    Fixed bug which caused MissingSerialVersionUID to trigger on all interfaces that implemented other interfaces.
    Modified NullAssignmentRule to catch null assignments in ternary expressions.
    Added two new node types - ASTCatchStatement and ASTFinallyStatement.
    Modified rule XML definition; it no longer includes a symboltable attribute since the symbol table layer is now run for all files analyzed.
    Harden equality of AbstractRule and RuleSet objects (needed for the Eclipse plugin features)
    Change RuleSet.getRuleByName. Now return null instead of throwing a RuntimeException when the rule is not found
    Add .project and .classpath to the module so that it can be checkout as an Eclipse project


## May 10, 2005 - 3.1:

    New rules: SimplifyStartsWith, UnnecessaryParentheses, CollapsibleIfStatements, UseAssertEqualsInsteadOfAssertTrue,  UseAssertSameInsteadOfAssertTrue, UseStringBufferForStringAppends, SimplifyConditional, SingularField
    Fixed bug 1170535 - LongVariable now report variables longer than 17 characters, not 12.
    Fixed bug 1182755 - SystemPrintln no longer overreports problems.
    Fixed bug 1188372 - AtLeastOneConstructor no longer fires on interfaces.
    Fixed bug 1190508 - UnnecessaryBooleanAssertion no longer fires on nested boolean literals.
    Fixed bug 1190461 - UnusedLocal no longer misses usages which are on the RHS of a right bit shift operator.
    Fixed bug 1188371 - AvoidInstantiatingObjectsInLoops no longer fires on instantiations in loops when the 'new' keyword is preceded by a 'return' or a 'throw'.
    Fixed bug 1190526 - TooManyFields now accepts a property setting correctly, and default lower bound is 15 vs 10.
    Fixed bug 1196238 - UnusedImports no longer reports false positives for various JDK 1.5 java.lang subpackages.
    Fixed bug 1169731 - UnusedImports no longer reports false positives on types used inside generics.  This bug also resulted in a bug in ForLoopShouldBeWhileLoop being fixed, thanks Wim!
    Fixed bug 1187325 - UnusedImports no longer reports a false positive on imports which are used inside an Annotation.
    Fixed bug 1189720 - PMD no longer fails to parse generics that use 'member selectors'.
    Fixed bug 1170109 - The Ant task now supports an optional 'targetjdk' attribute that accepts values of '1.3', '1.4', or '1.5'.
    Fixed bug 1183032 - The XMLRenderer no longer throws a SimpleDateFormat exception when run with JDK 1.3.
    Fixed bug 1097256 - The XMLRenderer now supports optional encoding of UTF8 characters using the 'net.sourceforge.pmd.supportUTF8' environment variable.
    Fixed bug 1198832 - AbstractClassWithoutAbstractMethod no longer flags classes which implement interfaces since these can partially implement the interface and thus don't need to explicitly declare abstract methods.
    Implemented RFE 1193979 - BooleanInstantiation now catches cases like Boolean.valueOf(true)
    Implemented RFE 1171095 - LabeledStatement nodes now contain the image of the label.
    Implemented RFE 1176401 - UnusedFormalParameter now flags public methods.
    Implemented RFE 994338 - The msg produced by ConstructorCallsOverridableMethod now includes the offending method name.
    Modified command line parameters; removed -jdk15 and -jdk13 parameters and added a -'targetjdk [1.3|1.4|1.5]' parameter.
    Modified CSVRenderer to include more columns.
    Optimized rules: FinalFieldCouldBeStatic (115 seconds to 7 seconds), SuspiciousConstantFieldName (48 seconds to 14 seconds), UnusedModifer (49 seconds to 4 seconds)


## March 23, 2005 - 3.0:

    New rules: MissingSerialVersionUID, UnnecessaryFinalModifier, AbstractClassDoesNotContainAbstractMethod, MissingStaticMethodInNonInstantiatableClass, AvoidSynchronizedAtMethodLevel, AvoidCallingFinalize, UseNotifyAllInsteadOfNotify, MissingBreakInSwitch, AvoidInstanceofChecksInCatchClause, AvoidFieldNameMatchingTypeName, AvoidFieldNameMatchingMethodName, AvoidNonConstructorMethodsWithClassName, TestClassWithoutTestCases, TooManyFields, CallSuperInConstructor, UnnecessaryBooleanAssertion, UseArrayListInsteadOfVector
    Implemented RFE 1058039 - PMD's command line interface now accepts abbreviated names for the standard rulesets; for example 'java net.sourceforge.pmd.PMD /my/source/code/ text basic,unusedcode' would run the rulesets/basic.xml and the rulesets/unusedcode.xml rulesets on the source in /my/source/code and produce a text report.
    Implemented RFE 1119851 - PMD's Ant task now supports an 'excludeMarker' attribute.
    Fixed bug 994400 - False +: ConstructorCallsOverridableMethodRule, thanks to ereissner for reporting it
    Fixed bug 1146116 - JUnitTestsShouldIncludeAssert no longer crashes on inner Interface
    Fixed bug 1114625 - UnusedPrivateField no longer throws an NPE on standalone postfix expressions which are prefixed with 'this'.
    Fixed bug 1114020 - The Ant task now reports a complete stack trace when run with the -verbose flag.
    Fixed bug 1117983 - MethodArgumentCouldBeFinal no longer reports false positives on try..catch blocks.
    Fixed bug 797742 - PMD now parses JDK 1.5 source code.  Note that it's not perfect yet; more testing/bug reports are welcome!
    Fixed a bug - the StatisticalRule no longer 'merges' data points when using the 'topscore' property.
    Fixed a bug - the PMD Ant task's failOnRuleViolation attribute no longer causes a BuildException in the case when no rule violations occur.
    Modified the XSLT to add a summary section.
    Added Ruby support to CPD.
    Optimized various rules and wrote a benchmarking application; results are here - http://infoether.com/~tom/pmd_timing.txt


## February 1, 2005 - 2.3:

    Fixed bug 1113927 - ExceptionAsFlowControl no longer throws NPEs on code where a throw statement exists without a try statement wrapping it.
    Fixed bug 1113981 - AvoidConcatenatingNonLiteralsInStringBuffer no longer throws NPEs on code where an append appears as a child of an ExplicitConstructorInvocation node.
    Fixed bug 1114039 - AvoidInstantiatingObjectsInLoops's message no longer contains a spelling error.
    Fixed bug 1114029 - The 'optimization' rules no longer throw NPEs at various points.
    Fixed bug 1114251 - The 'sunsecure' rules no longer throw NPEs at various points.


## January 31, 2005 - 2.2:

    New rules: LocalVariableCouldBeFinal, MethodArgumentCouldBeFinal, AvoidInstantiatingObjectsInLoops, ArrayIsStoredDirectly, MethodReturnsInternalArray, AssignmentToNonFinalStatic, AvoidConcatenatingNonLiteralsInStringBuffer
    Fixed bug 1088459 - JUnitTestsShouldContainAsserts no longer throws ClassCastException on interface, native, and abstract method declarations.
    Fixed bug 1100059 - The Ant task now generates a small empty-ish report if there are no violations.
    Implemented RFE 1086168 - PMD XML reports now contain a version and timestamp attribute in the <pmd> element.
    Implemented RFE 1031950 - The PMD Ant task now supports nested ruleset tags
    Fixed a bug in the rule override logic; it no longer requires the "class" attribute of a rule be listed in the overrides section.
    Added 'ignoreLiterals' and 'ignoreIdentifiers' boolean options to the CPD task.
    Cleaned up a good bit of the symbol table code; thanks much to Harald Gurres for the patch.
    CPD now contains a generic copy/paste checker for programs in any language


## December 15, 2004 - 2.1:

    New rules: AvoidProtectedFieldInFinalClass, SystemPrintln
    Fixed bug 1050173 - ImmutableFieldRule no longer reports false positives for static fields.
    Fixed bug 1050286 - ImmutableFieldRule no longer reports false positives for classes which have multiple constructors only a subset of which set certain fields.
    Fixed bug 1055346 - ImmutableFieldRule no longer reports false positive on preinc/predecrement/postfix expressions.
    Fixed bug 1041739 - EmptyStatementNotInLoop no longer reports false positives for nested class declarations in methods.
    Fixed bug 1039963 - CPD no longer fails to parse C++ files with multi-line macros.
    Fixed bug 1053663 - SuspiciousConstantFieldName no longer reports false positives for interface members.
    Fixed bug 1055930 - CouplingBetweenObjectsRule no longer throws a NPE on interfaces
    Fixed a possible NPE in dfa.report.ReportTree.
    Implemented RFE 1058033 - Renamed run.[sh|bat] to pmd.[sh|bat].
    Implemented RFE 1058042 - XML output is more readable now.
    Applied patch 1051956 - Rulesets that reference rules using "ref" can now override various properties.
    Applied patch 1070733 - CPD's Java checker now has an option to ignore both literals and identifiers - this can help find large duplicate code blocks, but can also result in false positives.
    YAHTMLRenderer no longer has dependence on Ant packages.
    Modified the AST to correctly include PostfixExpression nodes.  Previously a statement like "x++;" was embedded in the parent StatementExpression node.
    Moved BooleanInstantiation from the design ruleset to the basic ruleset.
    Updated Xerces libraries to v2.6.2.
    Many rule names had the word "Rule" tacked on to the end.  Various folks thought this was a bad idea, so here are the new names of those rules which were renamed:
    - basic.xml: UnnecessaryConversionTemporary, OverrideBothEqualsAndHashcode, DoubleCheckedLocking
    - braces.xml: WhileLoopsMustUseBraces, IfElseStmtsMustUseBraces, ForLoopsMustUseBraces
    - clone.xml: ProperCloneImplementation
    - codesize.xml: CyclomaticComplexity, ExcessivePublicCount
    - controversial.xml: UnnecessaryConstructor, AssignmentInOperand, DontImportSun, SuspiciousOctalEscape
    - coupling.xml: CouplingBetweenObjects, ExcessiveImports, LooseCoupling
    - design.xml: UseSingleton, SimplifyBooleanReturns, AvoidReassigningParameters, ConstructorCallsOverridableMethod, AccessorClassGeneration, CloseConnection, OptimizableToArrayCall, IdempotentOperations. ImmutableField
    - junit.xml: JUnitAssertionsShouldIncludeMessage, JUnitTestsShouldIncludeAssert
    - logging-java.xml: MoreThanOneLogger, LoggerIsNotStaticFinal
    - naming.xml: ShortMethodName, VariableNamingConventions, ClassNamingConventions, AbstractNaming
    - strictexception.xml: ExceptionAsFlowControl, AvoidCatchingNPE, AvoidThrowingCertainExceptionTypes
    Continued working on JDK 1.5 compatibility - added support for static import statements, varargs, and the new for loop syntax
    - still TODO: generics and annotations (note that autoboxing shouldn't require a grammar change)
    - Good article on features: http://java.sun.com/developer/technicalArticles/releases/j2se15/

## October 19, 2004 - 2.0:

    New rules: InstantiationToGetClass, IdempotentOperationsRule, SuspiciousEqualsMethodName, SimpleDateFormatNeedsLocale, JUnitTestsShouldContainAssertsRule, SuspiciousConstantFieldName, ImmutableFieldRule, MoreThanOneLoggerRule, LoggerIsNotStaticFinalRule, UseLocaleWithCaseConversions
    Applied patch in RFE 992576 - Enhancements to VariableNamingConventionsRule
    Implemented RFE 995910 - The HTML report can now include links to HTMLlized source code - for example, the HTML generated by JXR.
    Implemented RFE 665824 - PMD now ignores rule violations in lines containing the string 'NOPMD'.
    Fixed bug in SimplifyBooleanExpressions - now it catches more cases.
    Fixed bugs in AvoidDuplicateLiterals - now it ignores small duplicate literals, its message is more helpful, and it catches more cases.
    Fixed bug 997893 - UnusedPrivateField now detects assignments to members of private fields as a usage.
    Fixed bug 1020199 - UnusedLocalVariable no longer flags arrays as unused if an assignment is made to an array slot.
    Fixed bug 1027133 - Now ExceptionSignatureDeclaration skips certain JUnit framework methods.
    Fixed bug 1008548 - The 'favorites' ruleset no longer contains a broken reference.
    Fixed bug 1045583 - UnusedModifier now correctly handles anonymous inner classes within interface field declarations.
    Partially fixed bug 998122 - CloseConnectionRule now checks for imports of java.sql before reporting a rule violation.
    Applied patch 1001694 - Now PMD can process zip/jar files of source code.
    Applied patch 1032927 - The XML report now includes the rule priority.
    Added data flow analysis facade from Raik Schroeder.
    Added two new optional attributes to rule definitions - symboltable and dfa.  These allow the symbol table and DFA facades to be configured on a rule-by-rule basis.  Note that if your rule needs the symbol table; you'll need to add symboltable="true" to your rule definition.  FWIW, this also results in about a 5% speedup for rules that don't need either layer.
    Added a "logging" ruleset - thanks to Miguel Griffa for the code!
    Enhanced the ASTViewer - and renamed it 'Designer' - to display data flows.
    Moved development environment to Maven 1.0.
    Moved development environment to Ant 1.6.2.  This is nice because using the new JUnit task attribute "forkmode='perBatch'" cuts test runtime from 90 seconds to 7 seconds.  Sweet.
    MethodWithSameNameAsEnclosingClass now reports a more helpful line number.

## July 14, 2004 - 1.9:

    New rules: CloneMethodMustImplementCloneable, CloneThrowsCloneNotSupportedException, EqualsNull, ConfusingTernary
    Created new "clone" ruleset and moved ProperCloneImplementationRule over from the design ruleset.
    Moved LooseCoupling from design.xml to coupling.xml.
    Some minor performance optimizations - removed some unnecessary casts from the grammar, simplified some XPath rules.
    Postfix expressions (i.e., x++) are now available in the grammar.  To access them, search for StatementExpressions with an image of "++" or "--" - i.e., in XPath, //StatementExpression[@Image="++"].  This is an odd hack and hopefully will get cleared up later.
    Ant task and CLI now used BufferedInputStreams.
    Converted AtLeastOneConstructor rule from Java code to XPath.
    Implemented RFE 743460: The XML report now contains the ruleset name.
    Implemented RFE 958714: Private field and local variables that are assigned but not used are now flagged as unused.
    Fixed bug 962782 - BeanMembersShouldSerializeRule no longer reports set/is as being a violation.
    Fixed bug 977022 - UnusedModifier no longer reports false positives for modifiers of nested classes in interfaces
    Fixed bug 976643 - IfElseStmtsMustUseBracesRule no longer reports false positives for certain if..else constructs.
    Fixed bug 985961 - UseSingletonRule now fires on classes which contain static fields
    Fixed bug 977031 - FinalizeDoesNotCallSuperFinalize no longer reports a false positive when a finalizer contains a call to super.finalize in a try {} finally {} block.

## May 19, 2004 - 1.8:

    New rules: ExceptionAsFlowControlRule, BadComparisonRule, AvoidThrowingCertainExceptionTypesRule, AvoidCatchingNPERule, OptimizableToArrayCallRule
    Major grammar changes - lots of new node types added, many superfluous nodes removed from the runtime AST.  Bug 786611 - http://sourceforge.net/tracker/index.php?func=detail&aid=786611&group_id=56262&atid=479921 - explains it a bit more.
    Fixed bug 786611 - Expressions are no longer over-expanded in the AST
    Fixed bug 874284 - The AST now contains tokens for bitwise or expressions - i.e., "|"

## April 22, 2004 - 1.7:

    Moved development environment to Maven 1.0-RC2.
    Fixed bug 925840 - Messages were no longer getting variable names plugged in correctly
    Fixed bug 919308 - XMLRenderer was still messed up; 'twas missing a quotation mark.
    Fixed bug 923410 - PMD now uses the default platform character set encoding; optionally, you can pass in a character encoding to use.
    Implemented RFE 925839 - Added some more detail to the UseSingletonRule.
    Added an optional 'failuresPropertyName' attribute to the Ant task.
    Refactored away duplicate copies of XPath rule definitions in regress/, yay!
    Removed manifest from jar file; it was only there for the Main-class attribute, and it's not very useful now since PMD has several dependencies.
    Began working on JDK 1.5 compatibility - added support for EnumDeclaration nodes.

## March 15, 2004 - 1.6:

    Fixed bug 895661 - XML reports containing error elements no longer have malformed XML.
    Fixed a bug in UnconditionalIfStatement - it no longer flags things like "if (x==true)".
    Applied Steve Hawkins' improvements to CPD:
    - Various optimizations; now it runs about 4 times faster!
    - fixed "single match per file" bug
    - tweaked source code slicing
    - CSV renderer
    Added two new renderers - SummaryHTMLRenderer and PapariTextRenderer.
    Moved development environment to Ant 1.6 and JavaCC 3.2.

## February 2, 2004 - 1.5:

    New rules: DontImportSunRule, EmptyFinalizer, EmptyStaticInitializer, AvoidDollarSigns, FinalizeOnlyCallsSuperFinalize, FinalizeOverloaded, FinalizeDoesNotCallSuperFinalize, MethodWithSameNameAsEnclosingClass, ExplicitCallToFinalize, NonStaticInitializer, DefaultLabelNotLastInSwitchStmt, NonCaseLabelInSwitchStatement, SuspiciousHashcodeMethodName, EmptyStatementNotInLoop, SuspiciousOctalEscapeRule
    FinalizeShouldBeProtected moved from design.xml to finalizers.xml.
    Added isTrue() to ASTBooleanLiteral.
    Added UnaryExpression to the AST.
    Added isPackagePrivate() to AccessNode.

## January 7, 2004 - 1.4:

    New rules: AbstractNamingRule, ProperCloneImplementationRule
    Fixed bug 840926 - AvoidReassigningParametersRule no longer reports a false positive when assigning a value to an array slot when the array is passed as a parameter to a method
    Fixed bug 760520 - RuleSetFactory is less strict about whitespace in ruleset.xml files.
    Fixed bug 826805 - JumbledIncrementorRule no longer reports a false positive when a outer loop incrementor is used as an array index
    Fixed bug 845343 - AvoidDuplicateLiterals now picks up cases when a duplicate literal appears in field declarations.
    Fixed bug 853409 - VariableNamingConventionsRule no longer requires that non-static final fields be capitalized
    Fixed a bug in OverrideBothEqualsAndHashcodeRule; it no longer reports a false positive when equals() is passed the fully qualified name of Object.
    Implemented RFE 845348 - UnnecessaryReturn yields more useful line numbers now
    Added a ruleset DTD and a ruleset XML Schema.
    Added 'ExplicitExtends' and 'ExplicitImplements' attributes to UnmodifiedClassDeclaration nodes.

## October 23, 2003 - 1.3:

    Relicensed under a BSD-style license.
    Fixed bug 822245 - VariableNamingConventionsRule now handles interface fields correctly.
    Added new rules: EmptySynchronizedBlock, UnnecessaryReturn
    ASTType now has an getDimensions() method.

## October 06, 2003 - 1.2.2:

    Added new rule: CloseConnectionRule
    Fixed bug 782246 - FinalFieldCouldBeStatic no longer flags fields in interfaces.
    Fixed bug 782235 - "ant -version" now prints more details when a file errors out.
    Fixed bug 779874 - LooseCouplingRule no longer triggers on ArrayList
    Fixed bug 781393 - VariableNameDeclaration no longer throws ClassCastExpression since ASTLocalVariableDeclaration now subclasses AccessNode
    Fixed bug 797243 - CPD XML report can no longer contain ]]> (CDEnd)
    Fixed bug 690196 - PMD now handles both JDK 1.3 and 1.4 code - i.e., usage of "assert" as an identifier.
    Fixed bug 805092 - VariableNamingConventionsRule no longer flags serialVersionUID as a violation
    Fixed bug - Specifying a non-existing rule format on the command line no longer results in a ClassNotFoundException.
    XPath rules may now include pluggable parameters.  This feature is very limited.  For now.
    Tweaked CPD time display field
    Made CPD text fields uneditable
    Added more error checking to CPD GUI input
    Added "dialog cancelled" check to CPD "Save" function
    Added Boris Gruschko's AST viewer.
    Added Jeff Epstein's TextPad integration.
    ASTType now has an isArray() method.

## August 1, 2003 - 1.2.1:

    Fixed bug 781077 - line number "-1" no longer appears for nodes with siblings.

## July 30, 2003 - 1.2:

    Added new rules: VariableNamingConventionsRule, MethodNamingConventionsRule, ClassNamingConventionsRule, AvoidCatchingThrowable, ExceptionSignatureDeclaration, ExceptionTypeChecking, BooleanInstantiation
    Fixed bug 583047 - ASTName column numbers are now correct
    Fixed bug 761048 - Symbol table now creates a scope level for anonymous inner classes
    Fixed bug 763529 - AccessorClassGenerationRule no longer crashes when given a final inner class
    Fixed bug 771943 - AtLeastOneConstructorRule and UnnecessaryConstructorRule no longer reports false positives on inner classes.
    Applied patch from Chris Webster to fix another UnnecessaryConstructorRule problem.
    Added ability to accept a comma-delimited string of files and directories on the command line.
    Added a CSVRenderer.
    Added a "-shortfilenames" argument to the PMD command line interface.
    Modified grammer to provide information on whether an initializer block is static.
    ASTViewer now shows node images and modifiers
    ASTViewer now saves last edited text to ~/.pmd_astviewer
    Moved the PMD Swing UI into a separate module - pmd-swingui.
    Updated license.txt to point to new location.

## June 19, 2003 - 1.1:

    Added new rules: FinalizeShouldBeProtected, FinalFieldCouldBeStatic, BeanMembersShouldSerializeRule
    Removed "verbose" attribute from PMD and CPD Ant tasks; now they use built in logging so you can do a "ant -verbose cpd" or "ant -verbose pmd".  Thanks to Philippe T'Seyen for the code.
    Added "excludes" feature to rulesets; thanks to Gael Marziou for the suggestion.
    Removed "LinkedList" from LooseCouplingRule checks; thx to Randall Schulz for the suggestion.
    CPD now processes PHP code.
    Added VBHTMLRenderer; thanks to Vladimir Bossicard for the code.
    Added "Save" item to CPD GUI; thanks to mcclain looney for the patch.
    Fixed bug 732592 - Ant task now accepts a nested classpath element.
    Fixed bug 744915 - UseSingletonRule no longer fires on abstract classes, thanks to Pablo Casado for the bug report.
    Fixed bugs 735396 and 735399 - false positives from ConstructorCallsOverridableMethodRule
    Fixed bug 752809 - UnusedPrivateMethodRule now catches unused private static methods, thanks to Conrad Roche for the bug report.

## April 17, 2003 - 1.05:

    Added new rules: ReturnFromFinallyBlock, SimplifyBooleanExpressions
    Added a new Ant task for CPD; thanks to Andy Glover for the code.
    Added ability to specify a class name as a renderer on the command line or in the formatter "type" attribute of the Ant task.
    Brian Ewins completely rewrote CPD using a portion of the Burrows-Wheeler Transform - it's much, much, much faster now.
    Rebuilt parser with JavaCC 3.0; made several parser optimizations.
    The Ant task now accepts a <classpath> element to aid in loading custom rulesets.  Thanks to Luke Francl for the suggestion.
    Fixed several bugs in UnnecessaryConstructorRule; thanks to Adam Nemeth for the reports and fixes.
    All test-data classes have been inlined into their respective JUnit tests.

## March 21, 2003 - 1.04

    Added new rules: ConstructorCallsOverridableMethodRule, AtLeastOneConstructorRule, JUnitAssertionsShouldIncludeMessageRule, DoubleCheckedLockingRule, ExcessivePublicCountRule, AccessorClassGenerationRule
    The Ant task has been updated; if you set "verbose=true" full stacktraces are printed.  Thx to Paul Roebuck for the suggestion.
    Moved JUnit rules into their own package - "net.sourceforge.pmd.rules.junit".
    Incorporated new ResourceLoader; thanks to Dave Fuller
    Incorporated new XPath-based rule definitions; thanks to Dan Sheppard for the excellent work.
    Fixed bug 697187 - Problem with nested ifs
    Fixed bug 699287 - Grammar bug; good catch by David Whitmore

## February 11, 2003 - 1.03

    Added new rules: CyclomaticComplexityRule, AssignmentInOperandRule
    Added numbering to the HTMLRenderer; thx to Luke Francl for the code.
    Added an optional Ant task attribute 'failOnRuleViolation'.  This stops the build if any rule violations are found.
    Added an XSLT script for processing the PMD XML report; thx to Mats for the code.
    The Ant task now determines whether the formatter toFile attribute is absolute or relative and routes the report appropriately.
    Moved several rules into a new "controversial" ruleset.
    Fixed bug 672742 - grammar typo was hosing up ASTConstructorDeclaration which was hosing up UseSingletonRule
    Fixed bug 674393 - OnlyOneReturn rule no longer counts returns that are inside anonymous inner classes as being inside the containing method.  Thx to C. Lamont Gilbert for the bug report.
    Fixed bug 674420 - AvoidReassigningParametersRule no longer counts parameter field reassignment as a violation.  Thx to C. Lamont Gilbert for the bug report.
    Fixed bug 673662 - The Ant task's "failOnError" attribute works again.  Changed the semantics of this attribute, though, so it fails the build if errors occurred.  A new attribute 'failOnRuleViolation' serves the purpose of stopping the build if rule violations are found.
    Fixed bug 676340 - Symbol table now creates new scope level when it encounters a switch statement.  See the bug for code details; generally, this bug would have triggered runtime exceptions on certain blocks of code.
    Fixed bug 683465 - JavaCC parser no longer has ability to throw java.lang.Error; now it only throws java.lang.RuntimeExceptions.  Thx to Gunnlaugur Thor Briem for a good discussion on this topic.
    Fixed bug in OverrideBothEqualsAndHashcodeRule - it no longer bails out with a NullPtrException on interfaces that declare a method signature "equals(Object)".  Thx to Don Leckie for catching that.

## January 22, 2003 - 1.02:

    Added new rules: ImportFromSamePackageRule, SwitchDensityRule, NullAssignmentRule, UnusedModifierRule, ForLoopShouldBeWhileLoopRule
    Updated LooseCouplingRule to check for usage of Vector; thx to Vladimir for the good catch.
    Updated AvoidDuplicateLiteralsRule to report the line number of the first occurrence of the duplicate String.
    Modified Ant task to use a formatter element; this lets you render a report in several formats without having to rerun PMD.
    Added a new Ant task attribute - shortFilenames.
    Modified Ant task to ignore whitespace in the ruleset attribute
    Added rule priority settings.
    Added alternate row colorization to HTML renderer.
    Fixed bug 650623 - the Ant task now uses relative directories for the report file
    Fixed bug 656944 - PMD no longer prints errors to System.out, instead it just rethrows any exceptions
    Fixed bug 660069 - this was a symbol table bug; thanks to mcclain looney for the report.
    Fixed bug 668119 - OverrideBothEqualsAndHashcodeRule now checks the signature on equals(); thanks to mcclain looney for the report.

## November 07 2002 - 1.01:

    Fixed bug 633879: EmptyFinallyBlockRule now handles multiple catch blocks followed by a finally block.
    Fixed bug 633892: StringToStringRule false positive exposed problem in symbol table usage to declaration code.
    Fixed bug 617971: Statistical rules no longer produce tons of false positives due to accumulated results.
    Fixed bug 633209: OnlyOneReturn rule no longer requires the return stmt to be the last statement.
    Enhanced EmptyCatchBlockRule to flag multiple consecutive empty catch blocks.
    Renamed AvoidStringLiteralsRule to AvoidDuplicateLiteralsRule.
    Modified Ant task to truncate file paths to make the HTML output neater.

## November 04 2002 - 1.0:

    Added new rules: StringToStringRule, AvoidReassigningParametersRule, UnnecessaryConstructorRule, AvoidStringLiteralsRule
    Fixed bug 631010: AvoidDeeplyNestedIfStmtsRule works correctly with if..else stmts now
    Fixed bug 631605: OnlyOneReturn handles line spillover now.
    Moved AvoidDeeplyNestedIfStmts from the braces ruleset to the design ruleset.
    Moved several rules from the design ruleset to the codesize ruleset.
    Added a new "favorites" ruleset.

## October 04 2002 - 1.0rc3:

    Added new rules: OnlyOneReturnRule, JumbledIncrementerRule, AvoidDeeplyNestedIfStmtsRule
    PMD is now built and tested with JUnit 3.8.1 and Ant 1.5.
    Added support for IntelliJ's IDEAJ.
    Fixed bug 610018 - StringInstantiationRule now allows for String(byte[], int, int) usage.
    Fixed bug 610693 - UnusedPrivateInstanceVariable handles parameter shadowing better.
    Fixed bug 616535 - Command line interface input checking is better now.
    Fixed bug 616615 - Command line interface allows the text renderer to be used now
    Fixed a bug - the statistics rules now handle interfaces better.

## September 12 2002 - 1.0rc2:

    Added new rules: JUnitSpellingRule, JUnitStaticSuiteRule, StringInstantiationRule
    Added new rulesets - junit, strings.
    Added a printToConsole attribute to the Ant task so that you can see the report right there in the Ant output.
    Fixed bug in PMD GUI - rules are now saved correctly.
    Fixed bug 597916 - CPD line counts are accurate now.

## September 09 2002 - 1.0rc1:

    Added new rules: UnusedImportsRule, EmptySwitchStmtRule, SwitchStmtsShouldHaveDefaultRule, IfStmtsMustUseBracesRule
    Fixed bug 597813 - Rule properties are now parsed correctly
    Fixed bug 597905 - UseSingletonRule now resets its state correctly
    Moved several rules into a new ruleset - braces.
    Improved CPD by removing import statements and package statements from the token set.
    Added Metrics API to the Report.
    Updated PMD GUI.

## August 16 2002 - 0.9:

    Added new rules: LongParameterListRule, SimplifyBooleanReturnsRule
    Enhanced statistics rules to support various ways of triggering rule violations
    Added rule customization via XML parameters
    Enhanced CopyAndPasteDetector; added a GUI
    Fixed bug 592060 - UnusedPrivateInstanceVariable handles explicitly referenced statics correctly
    Fixed bug 593849 - UnusedPrivateInstanceVariable handles nested classes better

## July 30 2002 - 0.8:

    Added new rule: UnusedFormalParameterRule
    Fixed bug 588083 - ForLoopsNeedBraces rule correctly handles a variety of for statement formats
    Added prototype of the copy and paste detector

## July 25 2002 - 0.7:

    Added new rules: UnusedPrivateMethodRule, WhileLoopsMustUseBracesRule, ForLoopsMustUseBracesRule, LooseCouplingRule
    Fixed bug 583482 - EmptyCatchBlock and EmptyFinallyBlock no longer report an incorrect line number.

## July 18 2002 - 0.6:

    Added new rules: ExcessiveClassLength, ExcessiveMethodLength
    DuplicateImportsRule now reports the correct line number.
    Fixed bug 582639 - Rule violations are now reported on the proper line
    Fixed bug 582509 - Removed unneeded throws clause
    Fixed bug 583009 - Now rulesets.properties is in the jar file

## July 15 2002 - 0.5:

    Added new rules: DontImportJavaLangRule, DuplicateImportsRule
    Added new ruleset: rulesets/imports.xml
    Changed sorting of RuleViolations to group Files together.
    Changed XML Renderer to improved format.
    Created DVSL Stylesheet for the new format.
    Moved the Cougaar rules out of the PMD core.
    Fixed bug 580093 - OverrideBothEqualsAndHashcodeRule reports a more correct line number.
    Fixed bug 581853 - UnusedLocalVariableRule now handles anonymous inner classes correctly.
    Fixed bug 580278 - this was a side effect of bug 581853.
    Fixed bug 580123 - UnusedPrivateInstanceVariable now checks for instance variable usage in inner classes.

## July 10 2002 - 0.4:

    Added new rules: OverrideBothEqualsAndHashcodeRule, EmptyTryBlock, EmptyFinallyBlock
    Reports are now sorted by line number
    RuleSets can now reference rules in other RuleSets
    Fixed bug 579718 - made 'ruleset not found' error message clearer.

## July 03 2002 - 0.3:

    Added new rules: UseSingletonRule, ShortVariableRule, LongVariableRule, ShortMethodNameRule
    Moved rules into RuleSets which are defined in XML files in the ruleset directory
    Ant task:
    -Added a 'failonerror' attribute
    -Changed 'rulesettype' to 'rulesetfiles'
    -Removed 'text' report format; only 'html' and 'xml' are available now

## June 27 2002 - 0.2:

    Added new rules: IfElseStmtsMustUseBracesRule, EmptyWhileStmtRule
    Modified command line interface to accept a rule set
    Fixed bug in EmptyCatchBlockRule
    Fixed typo in UnnecessaryConversionTemporaryRule
    Moved Ant task to the net.sourceforge.pmd.ant package
    Added new HTML report format

## June 25 2002 - 0.1:

    Initial release
