---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New: Java 23 Support
This release of PMD brings support for Java 23. There are no new standard language features,
but a couple of preview language features:

* [JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)](https://openjdk.org/jeps/455)
* [JEP 476: Module Import Declarations (Preview)](https://openjdk.org/jeps/476)
* [JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)](https://openjdk.org/jeps/477)
* [JEP 482: Flexible Constructor Bodies (Second Preview)](https://openjdk.org/jeps/482)

Note that String Templates (introduced as preview in Java 21 and 22) are not supported anymore in Java 23,
see [JDK-8329949](https://bugs.openjdk.org/browse/JDK-8329949) for details.

In order to analyze a project with PMD that uses these preview language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `23-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-23-preview ...

Note: Support for Java 21 preview language features have been removed. The version "21-preview"
are no longer available.

### 🌟 New Rules
* The new Java rule {%rule java/multithreading/AvoidSynchronizedStatement %} finds synchronization blocks that
  could cause performance issues with virtual threads due to pinning.
* The new JavaScript rule {%rule ecmascript/performance/AvoidConsoleStatements %} finds any function calls
  on the Console API (e.g. `console.log`). Using these in production code might negatively impact performance.

### 🐛 Fixed Issues
* apex-performance
  * [#5139](https://github.com/pmd/pmd/issues/5139): \[apex] OperationWithHighCostInLoop: false negative for triggers
* java
  * [#5062](https://github.com/pmd/pmd/issues/5062): \[java] Support Java 23
  * [#5167](https://github.com/pmd/pmd/issues/5167): \[java] java.lang.IllegalArgumentException: \<?\> cannot be a wildcard bound
* java-bestpractices
  * [#3602](https://github.com/pmd/pmd/issues/3602): \[java] GuardLogStatement: False positive when compile-time constant is created from external constants
  * [#4731](https://github.com/pmd/pmd/issues/4731): \[java] GuardLogStatement: Documentation is unclear why getters are flagged
  * [#5145](https://github.com/pmd/pmd/issues/5145): \[java] UnusedPrivateMethod: False positive with method calls inside lambda
  * [#5151](https://github.com/pmd/pmd/issues/5151): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is a constant from another class
  * [#5152](https://github.com/pmd/pmd/issues/5152): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is "this"
  * [#5153](https://github.com/pmd/pmd/issues/5153): \[java] GuardLogStatement: Should not need to guard parameterized log messages where the replacement arg is an array element
* java-design
  * [#5048](https://github.com/pmd/pmd/issues/5084): \[java] CognitiveComplexity: Exception when using Map.of()
  * [#5162](https://github.com/pmd/pmd/issues/5162): \[java] SingularField: False-positive when preceded by synchronized block
* java-multithreading
  * [#5175](https://github.com/pmd/pmd/issues/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement
* javascript-performance
  * [#5105](https://github.com/pmd/pmd/issues/5105): \[javascript] Prohibit any console methods
* plsql
  * [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names)
* plsql-bestpractices
  * [#5132](https://github.com/pmd/pmd/issues/5132): \[plsql] TomKytesDespair: XPathException for more complex exception handler

### 🚨 API Changes
#### Deprecations
* pmd-jsp
  * {%jdoc jsp::lang.jsp.ast.JspParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-plsql
  * {%jdoc plsql::lang.plsql.ast.PLSQLParserImpl#MergeUpdateClausePrefix() %} is deprecated. This production is
    not used anymore and will be removed. Note: The whole parser implementation class has been deprecated since 7.3.0,
    as it is supposed to be internalized.
* pmd-velocity
  * {%jdoc velocity::lang.velocity.ast.VtlParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.
* pmd-visualforce
  * {%jdoc visualforce::lang.visualforce.ast.VfParserImpl %} is deprecated now. It should have been package-private
    because this is an implementation class that should not be used directly.

#### Experimental
* pmd-java
  * Renamed `isUnnamedClass()` to {%jdoc !!java::lang.java.ast.ASTCompilationUnit#isSimpleCompilationUnit() %}
  * {%jdoc java::lang.java.ast.ASTImplicitClassDeclaration %}
  * {%jdoc !!java::lang.java.ast.ASTImportDeclaration#isModuleImport() %}
  * {%jdoc !ac!java::lang.java.ast.JavaVisitorBase#visit(java::lang.java.ast.ASTImplicitClassDeclaration,P) %}

### ✨ External Contributions
* [#5125](https://github.com/pmd/pmd/pull/5125): \[plsql] Improve merge statement (order of merge insert/update flexible, allow prefixes in column names) - [Arjen Duursma](https://github.com/duursma) (@duursma)
* [#5175](https://github.com/pmd/pmd/pull/5175): \[java] Update AvoidSynchronizedAtMethodLevel message to mention ReentrantLock, new rule AvoidSynchronizedStatement - [Chas Honton](https://github.com/chonton) (@chonton)

### 📦 Dependency updates
* [#5100](https://github.com/pmd/pmd/issues/5100): Enable Dependabot
* [#5141](https://github.com/pmd/pmd/issues/5141): Bump org.apache.maven.plugins:maven-checkstyle-plugin from 3.3.1 to 3.4.0
* [#5142](https://github.com/pmd/pmd/issues/5142): Bump org.apache.maven.plugins:maven-compiler-plugin from 3.12.1 to 3.13.0
* [#5144](https://github.com/pmd/pmd/issues/5144): Bump org.codehaus.mojo:versions-maven-plugin from 2.16.2 to 2.17.1
* [#5148](https://github.com/pmd/pmd/issues/5148): Bump org.apache.commons:commons-text from 1.11.0 to 1.12.0
* [#5149](https://github.com/pmd/pmd/issues/5149): Bump org.apache.maven.plugins:maven-site-plugin from 4.0.0-M13 to 4.0.0-M16
* [#5160](https://github.com/pmd/pmd/issues/5160): Bump org.pcollections:pcollections from 3.2.0 to 4.0.2
* [#5161](https://github.com/pmd/pmd/issues/5161): Bump danger from 9.4.3 to 9.5.0 in the all-gems group across 1 directory
* [#5164](https://github.com/pmd/pmd/issues/5164): Bump org.apache.maven.plugins:maven-dependency-plugin from 3.6.1 to 3.7.1
* [#5165](https://github.com/pmd/pmd/issues/5165): Bump the all-gems group across 1 directory with 2 updates
* [#5171](https://github.com/pmd/pmd/issues/5171): Bump net.bytebuddy:byte-buddy-agent from 1.14.12 to 1.14.19
* [#5180](https://github.com/pmd/pmd/issues/5180): Bump net.sf.saxon:Saxon-HE from 12.4 to 12.5

### 📈 Stats
* 87 commits
* 25 closed tickets & PRs
* Days since last release: 35

{% endtocmaker %}
