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

#### Modified Rules

*   The Java rule {% rule "java/errorprone/AvoidLiteralsInIfCondition" %} (`java-errorprone`) has a new property
    `ignoreExpressions`. This property is set by default to `true` in order to maintain compatibility. If this
    property is set to false, then literals in more complex expressions are considered as well.

*   The Apex rule {% rule "apex/errorprone/ApexCSRF" %} (`apex-errorprone`) has been moved from category
    "Security" to "Error Prone". The Apex runtime already prevents DML statements from being executed, but only
    at runtime. So, if you try to do this, you'll get an error at runtime, hence this is error prone. See also
    the discussion on [#2064](https://github.com/pmd/pmd/issues/2064).

*   The Java rule {% rule "java/documentation/CommentRequired" %} (`java-documentation`) has a new property
    `classCommentRequirement`. This replaces the now deprecated property `headerCommentRequirement`, since
    the name was misleading. (File) header comments are not checked, but class comments are.

### Fixed Issues

*   apex
    *   [#2208](https://github.com/pmd/pmd/issues/2208): \[apex] ASTFormalComment should implement ApexNode&lt;T&gt;
*   core
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
  * Many methods on the {% jdoc core::lang.ast.Node %} interface
  and {% jdoc core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
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


### External Contributions

*   [#2041](https://github.com/pmd/pmd/pull/2041): \[modelica] Initial implementation for PMD - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2051](https://github.com/pmd/pmd/pull/2051): \[doc] Update the docs on adding a new language - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2069](https://github.com/pmd/pmd/pull/2069): \[java] CommentRequired: make property names consistent - [snuyanzin](https://github.com/snuyanzin)
*   [#2169](https://github.com/pmd/pmd/pull/2169): \[modelica] Follow-up fixes for Modelica language module - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2193](https://github.com/pmd/pmd/pull/2193): \[core] Fix odd logic in test runner - [Egor Bredikhin](https://github.com/Egor18)
*   [#2194](https://github.com/pmd/pmd/pull/2194): \[java] Fix odd logic in AvoidUsingHardCodedIPRule - [Egor Bredikhin](https://github.com/Egor18)
*   [#2195](https://github.com/pmd/pmd/pull/2195): \[modelica] Normalize invalid node ranges - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2199](https://github.com/pmd/pmd/pull/2199): \[modelica] Fix Javadoc tags - [Anatoly Trosinenko](https://github.com/atrosinenko)

{% endtocmaker %}

