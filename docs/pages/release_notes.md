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

#### Java 15 Support

This release of PMD brings support for Java 15. PMD can parse [Text Blocks](https://openjdk.java.net/jeps/378)
which have been promoted to be a standard language feature of Java.

PMD also supports [Pattern Matching for instanceof](https://openjdk.java.net/jeps/375),
[Records](https://openjdk.java.net/jeps/384), and [Sealed Classes](https://openjdk.java.net/jeps/360).

Note: The Pattern Matching for instanceof, Records, and Sealed Classes are all preview language features of OpenJDK 15
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS` and select the new language version `15-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 15-preview ...

Note: Support for Java 13 preview language features have been removed. The version "13-preview" is no longer available.

#### Changes in how tab characters are handled

In the past, tab characters in source files has been handled differently in different languages by PMD.
For instance in Java, tab characters had a width of 8 columns, while C# used only 1 column. Visualforce instead
used 4 columns.

This has been unified now so that tab characters are consistently now always 1 column wide.

This however might be a **incompatible** change, if you're using the properties "BeginColumn" or "EndColumn"
additionally to "BeginLine" and "EndLine" of a Token/AST node in order to highlight
where a rule violation occurred in the source file. If you have logic there that deals with tab characters,
you most likely can remove this logic now, since tab characters are now just "normal" characters
in terms of string processing.

See also [[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently #2656](https://github.com/pmd/pmd/pull/2656).

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.27.0).

#### New Rules

*   The new Java rule {% rule "java/bestpractices/AvoidReassigningCatchVariables" %} (`java-bestpractices`) finds
    cases where the variable of the caught exception is reassigned. This practice is surprising and prevents
    further evolution of the code like multi-catch.

#### Modified Rules

*   The Java rule {% rule "java/errorprone/CloseResource" %} (`java-errorprone`) has a new property
    `closeNotInFinally`. With this property set to `true` the rule will also find calls to close a
    resource, which are not in a finally-block of a try-statement. If a resource is not closed within a
    finally block, it might not be closed at all in case of exceptions.
    
    As this new detection would yield many new violations, it is disabled by default. It might be
    enabled in a later version of PMD.

#### Deprecated Rules

*   The Java rule [`DataFlowAnomalyAnalysis`](https://pmd.github.io/latest/pmd_rules_java_errorprone.html#dataflowanomalyanalysis) (`java-errorprone`)
    is deprecated in favour of {% rule "java/bestpractices/UnusedAssignment" %} (`java-bestpractices`),
    which was introduced in PMD 6.26.0.

### Fixed Issues

*   core
    *   [#724](https://github.com/pmd/pmd/issues/724): \[core] Avoid parsing rulesets multiple times
    *   [#1962](https://github.com/pmd/pmd/issues/1962): \[core] Simplify Report API
    *   [#2653](https://github.com/pmd/pmd/issues/2653): \[lang-test] Upgrade kotlintest to Kotest
    *   [#2656](https://github.com/pmd/pmd/pull/2656): \[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently
    *   [#2690](https://github.com/pmd/pmd/pull/2690): \[core] Fix java7 compatibility
*   java
    *   [#2646](https://github.com/pmd/pmd/issues/2646): \[java] Support JDK 15
*   java-bestpractices
    *   [#2471](https://github.com/pmd/pmd/issues/2471): \[java] New Rule: AvoidReassigningCatchVariables
    *   [#2663](https://github.com/pmd/pmd/issues/2663): \[java] NoClassDefFoundError on upgrade from 6.25.0 to 6.26.0
    *   [#2668](https://github.com/pmd/pmd/issues/2668): \[java] UnusedAssignment false positives
    *   [#2673](https://github.com/pmd/pmd/issues/2673): \[java] UnusedPrivateField and SingularField false positive with lombok annotation EqualsAndHashCode
    *   [#2684](https://github.com/pmd/pmd/issues/2684): \[java] UnusedAssignment FP in try/catch
    *   [#2686](https://github.com/pmd/pmd/issues/2686): \[java] UnusedAssignment must not flag abstract method parameters in interfaces and abstract classes
*   java-design
    *   [#2108](https://github.com/pmd/pmd/issues/2108): \[java] \[doc] ImmutableField rule: Description should clarify shallow immutability
    *   [#2461](https://github.com/pmd/pmd/issues/2461): \[java] ExcessiveParameterListRule must ignore a private constructor
*   java-errorprone
    *   [#2264](https://github.com/pmd/pmd/issues/2264): \[java] SuspiciousEqualsMethodName: Improve description about error-prone overloading of equals()
    *   [#2410](https://github.com/pmd/pmd/issues/2410): \[java] ProperCloneImplementation not valid for final class
    *   [#2431](https://github.com/pmd/pmd/issues/2431): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message
    *   [#2439](https://github.com/pmd/pmd/issues/2439): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t)
    *   [#2470](https://github.com/pmd/pmd/issues/2470): \[java] CloseResource false positive when resource included in return value
    *   [#2531](https://github.com/pmd/pmd/issues/2531): \[java] UnnecessaryCaseChange can not detect the case like: foo.equals(bar.toLowerCase())
    *   [#2647](https://github.com/pmd/pmd/issues/2647): \[java] Deprecate rule DataFlowAnomalyAnalysis
*   java-performance
    *   [#1868](https://github.com/pmd/pmd/issues/1868): \[java] false-positive for SimplifyStartsWith if string is empty
    *   [#2441](https://github.com/pmd/pmd/issues/2441): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';`
    *   [#2530](https://github.com/pmd/pmd/issues/2530): \[java] StringToString can not detect the case: getStringMethod().toString()

### API Changes

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

#### Deprecated API

##### For removal

*   {% jdoc !!core::Rule#getParserOptions() %}
*   {% jdoc !!core::lang.Parser#getParserOptions() %}
*   {% jdoc core::lang.AbstractParser %}
*   {% jdoc !!core::RuleContext#removeAttribute(java.lang.String) %}
*   {% jdoc !!core::RuleContext#getAttribute(java.lang.String) %}
*   {% jdoc !!core::RuleContext#setAttribute(java.lang.String, java.lang.Object) %}
*   {% jdoc apex::lang.apex.ApexParserOptions %}
*   {% jdoc !!java::lang.java.ast.ASTThrowStatement#getFirstClassOrInterfaceTypeImage() %}
*   {% jdoc javascript::lang.ecmascript.EcmascriptParserOptions %}
*   {% jdoc javascript::lang.ecmascript.rule.EcmascriptXPathRule %}
*   {% jdoc xml::lang.xml.XmlParserOptions %}
*   {% jdoc xml::lang.xml.rule.XmlXPathRule %}
*   Properties of {% jdoc xml::lang.xml.rule.AbstractXmlRule %}

*   {% jdoc !!core::Report.ReadableDuration %}
*   Many methods of {% jdoc !!core::Report %}. They are replaced by accessors
  that produce a List. For example, {% jdoc !a!core::Report#iterator() %} 
  (and implementing Iterable) and {% jdoc !a!core::Report#isEmpty() %} are both
  replaced by {% jdoc !a!core::Report#getViolations() %}.

*   The dataflow codebase is deprecated for removal in PMD 7. This
    includes all code in the following packages, and their subpackages:
    *   {% jdoc_package plsql::lang.plsql.dfa %}
    *   {% jdoc_package java::lang.java.dfa %}
    *   {% jdoc_package core::lang.dfa %}
    *   and the class {% jdoc plsql::lang.plsql.PLSQLDataFlowHandler %}

*   {% jdoc visualforce::lang.vf.VfSimpleCharStream %}

*   {% jdoc jsp::lang.jsp.ast.ASTJspDeclarations %}
*   {% jdoc jsp::lang.jsp.ast.ASTJspDocument %}
*   {% jdoc !!scala::lang.scala.ast.ScalaParserVisitorAdapter#zero() %}
*   {% jdoc !!scala::lang.scala.ast.ScalaParserVisitorAdapter#combine(Object, Object) %}
*   {% jdoc apex::lang.apex.ast.ApexParserVisitorReducedAdapter %}
*   {% jdoc java::lang.java.ast.JavaParserVisitorReducedAdapter %}

* {% jdoc java::lang.java.typeresolution.TypeHelper %} is deprecated in
 favor of {% jdoc java::lang.java.types.TypeTestUtil %}, which has the
same functionality, but a slightly changed API.
* Many of the classes in {% jdoc_package java::lang.java.symboltable %}
are deprecated as internal API.


### External Contributions

*   [#2656](https://github.com/pmd/pmd/pull/2656): \[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2659](https://github.com/pmd/pmd/pull/2659): \[java] StringToString can not detect the case: getStringMethod().toString() - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2662](https://github.com/pmd/pmd/pull/2662): \[java] UnnecessaryCaseChange can not detect the case like: foo.equals(bar.toLowerCase()) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2671](https://github.com/pmd/pmd/pull/2671): \[java] CloseResource false positive when resource included in return value - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2674](https://github.com/pmd/pmd/pull/2674): \[java] add lombok.EqualsAndHashCode in AbstractLombokAwareRule - [berkam](https://github.com/berkam)
*   [#2677](https://github.com/pmd/pmd/pull/2677): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';` - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2678](https://github.com/pmd/pmd/pull/2678): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2679](https://github.com/pmd/pmd/pull/2679): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2682](https://github.com/pmd/pmd/pull/2682): \[java] New Rule: AvoidReassigningCatchVariables - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2697](https://github.com/pmd/pmd/pull/2697): \[java] ExcessiveParameterListRule must ignore a private constructor - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2699](https://github.com/pmd/pmd/pull/2699): \[java] ProperCloneImplementation not valid for final class - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2700](https://github.com/pmd/pmd/pull/2700): \[java] Fix OnlyOneReturn code example - [Jan-Lukas Else](https://github.com/jlelse)
*   [#2722](https://github.com/pmd/pmd/pull/2722): \[doc] \[java] ImmutableField: extend description, fixes #2108 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2723](https://github.com/pmd/pmd/pull/2723): \[doc] \[java] SimplifyStartsWith: update description and example, fixes #1868 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2724](https://github.com/pmd/pmd/pull/2724): \[doc] [java] SuspiciousEqualsMethodName: update description, fixes #2264 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2725](https://github.com/pmd/pmd/pull/2725): Cleanup: change valueOf to parse when we need primitive return value. - [XenoAmess](https://github.com/XenoAmess)
*   [#2726](https://github.com/pmd/pmd/pull/2726): Cleanup: replace StringBuffer with StringBuilder - [XenoAmess](https://github.com/XenoAmess)
*   [#2727](https://github.com/pmd/pmd/pull/2727): Cleanup: replace indexOf() < 0 with contains - [XenoAmess](https://github.com/XenoAmess)
*   [#2728](https://github.com/pmd/pmd/pull/2728): Cleanup: javadoc issues - [XenoAmess](https://github.com/XenoAmess)
*   [#2729](https://github.com/pmd/pmd/pull/2729): Cleanup: use print instead of printf if no format exists - [XenoAmess](https://github.com/XenoAmess)
*   [#2730](https://github.com/pmd/pmd/pull/2730): Cleanup: StringBuilder issues - [XenoAmess](https://github.com/XenoAmess)
*   [#2731](https://github.com/pmd/pmd/pull/2731): Cleanup: avoid compiling Patterns repeatedly - [XenoAmess](https://github.com/XenoAmess)
*   [#2732](https://github.com/pmd/pmd/pull/2732): Cleanup: use StandardCharsets instead of Charset.forName - [XenoAmess](https://github.com/XenoAmess) 
*   [#2733](https://github.com/pmd/pmd/pull/2733): Cleanup: Collection::addAll issues  - [XenoAmess](https://github.com/XenoAmess) 
*   [#2734](https://github.com/pmd/pmd/pull/2734): Cleanup: use try with resources - [XenoAmess](https://github.com/XenoAmess)
*   [#2744](https://github.com/pmd/pmd/pull/2744): Cleanup: fix typos - [XenoAmess](https://github.com/XenoAmess)


{% endtocmaker %}

