---
title: PMD 7.0.0 development
permalink: pmd_next_major_development.html
keywords: changelog, release notes, deprecation, api changes
---

We're excited to bring you the next major version of PMD!
Here is a summary of what is planned for PMD 7.

To give us feedback or to suggest a new feature, drop us a line on [Gitter](https://gitter.im/pmd/pmd)!

## Summary

### New Logo

We decided it's time to have a modernized logo and get rid of the gun. This allows to include
the logo in anywhere without offense.

The current tasks are listed here: [Integrate new PMD logo #1931](https://github.com/pmd/pmd/issues/1931)

The new logo is available from the [Logo Project Page](pmd_projectdocs_logo.html).

### API

The API of PMD has been growing over the years and needs to be cleaned up. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development. This however entails some incompatibilities and
deprecations, see also the sections [New API support guidelines](#new-api-support-guidelines) and
[Planned API removals](#planned-api-removals] below.

### Full Antlr Support

PMD 6 only supports JavaCC based grammars, but with [Antlr](https://www.antlr.org/) parsers
can be generated as well. PMD 7 adds full support for grammars written in Antlr, which allows
to leverage existing grammars.

The current tasks are listed here: [Support for ANTLR based grammars with Swift as an example language #2499](https://github.com/pmd/pmd/issues/2499)

### Documentation

We have quite some ideas how we want to improve the documentation. The goal is, that the documentation is
up to date and nearly complete. One big task is, how the built-in rules are presented, so that users
can easier see, what exactly is available and decide, which rules are useful for the project at hand.

The current tasks are listed here: [Documentations improvements tracker #1139](https://github.com/pmd/pmd/issues/1139)

### XPath

PMD 6 supports XPath 1.0 via the Jaxen library. This library is old and unmaintained creating some problems
(one of which is duplicated classes in the package `org.w3c.dom` which is a Java API actually).
Therefore XPath 1.0 support will be dropped and we upgrade our XPath 2.0 implementation with Saxon moving
on to Saxon HE. This will eventually add support in PMD for XPath 3.1.

The current tasks are listed here: [XPath Improvements for PMD 7 #2523](https://github.com/pmd/pmd/issues/2523)

### Java

Like the main PMD API, the Java AST has been growing over time and the grammar doesn't support
all edge cases (e.g. annotation are not supported everywhere). The goal is to simplify the AST by reducing
unnecessary nodes and abstractions and fix the parsing issues.
This helps in the end to provide a better type resolution implementation, but changing the AST is a breaking
API change.

Some first results of the Java AST changes are for now documented in the Wiki:
[Java clean changes](https://github.com/pmd/pmd/wiki/Java_clean_changes).

{% jdoc_nspace :jast java::lang.java.ast %}

* {% jdoc jast::ASTType %} and {% jdoc jast::ASTReferenceType %} have been turned into
interfaces, implemented by {% jdoc jast::ASTPrimitiveType %}, {% jdoc jast::ASTClassOrInterfaceType %},
and the new node {% jdoc jast::ASTArrayType %}. This reduces the depth of the relevant
subtrees, and allows to explore them more easily and consistently.

* {% jdoc jast::ASTClassOrInterfaceType %} appears to be left recursive now.
TODO document that when we're done discussing the semantic rewrite phase.

* **Migrating**:
  * There is currently no way to match abstract types (or interfaces) with XPath, so `Type`
  and `ReferenceType` name tests won't match anything anymore.
  * `Type/ReferenceType/ClassOrInterfaceType` -> `ClassOrInterfaceType`
  * `Type/PrimitiveType` -> `PrimitiveType`.
  * `Type/ReferenceType[@ArrayDepth>1]/ClassOrInterfaceType` -> `ArrayType/ClassOrInterfaceType`.
  * `Type/ReferenceType/PrimitiveType` -> `ArrayType/PrimitiveType`.
  * Note that in most cases you should check the type of a variable with e.g.
  `VariableDeclaratorId[pmd-java:typeIs("java.lang.String[]")]` because it
  considers the additional dimensions on declarations like `String foo[];`.
  The Java equivalent is `TypeHelper.isA(id, String[].class);`


#### Expression grammar changes

* {% jdoc jast::ASTExpression %} and {% jdoc jast::ASTPrimaryExpression %} have
been turned into interfaces. These added no information to the AST and increased
its depth unnecessarily. All expressions implement the first interface. Both of
those nodes can no more be found in ASTs.

* **Migrating**:
  * Basically, `Expression/X` or `Expression/PrimaryExpression/X`, just becomes `X`
  * There is currently no way to match abstract or interface types with XPath, so `Expression` or `PrimaryExpression`  name tests won't match anything anymore. However, the axis step *[@Expression=true()] matches any expression.

* {% jdoc jast::ASTLiteral %} has been turned into an interface. The fact that {% jdoc jast::ASTNullLiteral %}
and {% jdoc jast::ASTBooleanLiteral %} were nested within it but other literals types were all directly represented
by it was inconsistent, and ultimately that level of nesting was unnecessary.
  * {% jdoc jast::ASTNumericLiteral %}, {% jdoc jast::ASTCharLiteral %}, {% jdoc jast::ASTStringLiteral %},
  and {% jdoc jast::ASTClassLiteral %} are new nodes that implement that interface.
  * ASTLiteral implements {% jdoc jast::ASTPrimaryExpression %}

* **Migrating**:
  * Remove all `/Literal/` segments from your XPath expressions
  * If you tested several types of literals, you can e.g. do it like `/*[self::StringLiteral or self::CharLiteral]/`
  * As is usual, use the designer to explore the new AST structure

* The nodes {% jdoc_old jast::ASTPrimaryPrefix %} and {% jdoc_old jast::ASTPrimarySuffix %} are removed from the grammar.
Subtrees for primary expressions appear to be left-recursive now. For example,

```java
new Foo().bar.foo(1)
```
used to be parsed as
```
Expression
+ PrimaryExpression
  + PrimaryPrefix
    + AllocationExpression
      + ClassOrInterfaceType[@Image="Foo"]
  + PrimarySuffix
    + Arguments
  + PrimarySuffix
    + Name[@Image="bar.foo"]
  + PrimarySuffix
    + Arguments
      + ArgumentsList
        + Expression
          + PrimaryExpression
            + Literal[@ValueAsInt=1]
```
It's now parsed as
```
MethodCall[@MethodName="foo"]
+ FieldAccess[@FieldName="bar"]
  + ConstructorCall
    + ClassOrInterfaceType[@TypeImage="Foo"]
    + ArgumentsList
+ ArgumentsList
  + NumericLiteral[@ValueAsInt=1]
```
Instead of being flat, the subexpressions are now nested within one another.
The nesting follows the naturally recursive structure of expressions:

```java
new Foo().bar.foo(1)
---------            ConstructorCall
-------------        FieldAccess
-------------------- MethodCall
```
This makes the AST more regular and easier to navigate. Each node contains 
the other nodes that are relevant to it (e.g. arguments) instead of them 
being spread out over several siblings. The API of all nodes has been 
enriched with high-level accessors to query the AST in a semantic way,
without bothering with the placement details.

The amount of changes in the grammar that this change entails is enormous, 
but hopefully firing up the designer to inspect the new structure should 
give you the information you need quickly.

TODO write a summary of changes in the javadoc of the package, will be more
accessible.

Note: this doesn't affect binary expressions like {% jdoc jast::ASTAdditiveExpression %}.
E.g. `a+b+c` is not parsed as
```
AdditiveExpression
+ AdditiveExpression
  + (a)
  + (b)
+ (c)  
``` 
It's still
```
AdditiveExpression
+ (a)
+ (b)
+ (c)  
``` 
which is easier to navigate, especially from XPath.

### Miscellaneous

There are also some small improvements, refactoring and internal tasks that are planned for PMD 7.

The current tasks are listed here: [PMD 7 Miscellaneous Tasks #2524](https://github.com/pmd/pmd/issues/2524)

## New API support guidelines

### What's new

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

#### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

#### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

#### `@Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

#### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.

### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.

## Planned API removals

### List of currently deprecated APIs

{% include warning.html content="This list is not exhaustive. The ultimate reference is whether
an API is tagged as `@Deprecated` or not in the latest minor release. During the development of 7.0.0,
we may decide to remove some APIs that were not tagged as deprecated, though we'll try to avoid it." %}

#### 6.54.0

##### PMD CLI

* PMD now supports a new `--relativize-paths-with` flag (or short `-z`), which replaces `--short-names`.
  It serves the same purpose: Shortening the pathnames in the reports. However, with the new flag it's possible
  to explicitly define one or more pathnames that should be used as the base when creating relative paths.
  The old flag `--short-names` is deprecated.

##### Deprecated APIs

###### For removal

* {% jdoc !!apex::lang.apex.ast.ApexRootNode#getApexVersion() %} has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.
* {% jdoc !!core::cpd.CPDConfiguration#setEncoding(java.lang.String) %} and
  {% jdoc !!core::cpd.CPDConfiguration#getEncoding() %}. Use the methods
  {% jdoc core::AbstractConfiguration#getSourceEncoding() %} and
  {% jdoc core::AbstractConfiguration#setSourceEncoding(java.lang.String) %} instead. Both are available
  for `CPDConfiguration` which extends `AbstractConfiguration`.
* {% jdoc test::cli.BaseCLITest %} and {% jdoc test::cli.BaseCPDCLITest %} have been deprecated for removal without
  replacement. CLI tests should be done in pmd-core only (and in PMD7 in pmd-cli). Individual language modules
  shouldn't need to test the CLI integration logic again. Instead, the individual language modules should test their
  functionality as unit tests.
* {% jdoc core::cpd.CPDConfiguration.LanguageConverter %}

* {% jdoc !!core::lang.document.FileCollector#addZipFile(java.nio.file.Path) %} has been deprecated. It is replaced
  by {% jdoc !!core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} which directly adds the
  content of the zip file for analysis.

* {% jdoc !!core::PMDConfiguration#setReportShortNames(boolean) %} and
  {% jdoc !!core::PMDConfiguration#isReportShortNames() %} have been deprecated for removal.
  Use {% jdoc !!core::PMDConfiguration#addRelativizeRoot(java.nio.file.Path) %} instead.

###### Internal APIs

* {% jdoc core::renderers.CSVWriter %}
* Some fields in {% jdoc test::ant.AbstractAntTestHelper %}

###### Experimental APIs

* CPDReport has a new method which limited mutation of a given report:
    * {%jdoc core::cpd.CPDReport#filterMatches(net.sourceforge.pmd.util.Predicate) %} creates a new CPD report
      with some matches removed with a given predicate based filter.

#### 6.53.0

##### Deprecated APIs

###### For removal

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* {% jdoc java::lang.java.rule.design.ExcessiveLengthRule %} (Java)

#### 6.52.0

##### PMD CLI

* PMD now supports a new `--use-version` flag, which receives a language-version pair (such as `java-8` or `apex-54`).
  This supersedes the usage of `-language` / `-l` and `-version` / `-v`, allowing for multiple versions to be set in a single run.
  PMD 7 will completely remove support for `-language` and `-version` in favor of this new flag.

* Support for `-V` is being deprecated in favor of `--verbose` in preparation for PMD 7.
  In PMD 7, `-v` will enable verbose mode and `-V` will show the PMD version for consistency with most Unix/Linux tools.

* Support for `-min` is being deprecated in favor of `--minimum-priority` for consistency with most Unix/Linux tools, where `-min` would be equivalent to `-m -i -n`.

##### CPD CLI

* CPD now supports using `-d` or `--dir` as an alias to `--files`, in favor of consistency with PMD.
  PMD 7 will remove support for `--files` in favor of these new flags.

##### Linux run.sh parameters

* Using `run.sh cpdgui` will now warn about it being deprecated. Use `run.sh cpd-gui` instead.

* The old designer (`run.sh designerold`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer`.

* The old visual AST viewer (`run.sh bgastviewer`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer` for a visual tool, or use `run.sh ast-dump` for a text-based alternative.

##### Deprecated API

* The following core APIs have been marked as deprecated for removal in PMD 7:
    - {% jdoc core::PMD %} and {% jdoc core::PMD.StatusCode %} - PMD 7 will ship with a revamped CLI split from pmd-core. To programmatically launch analysis you can use {% jdoc core::PmdAnalysis %}.
    - {% jdoc !!core::PMDConfiguration#getAllInputPaths() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getInputPathList() %}
    - {% jdoc !!core::PMDConfiguration#setInputPaths(List) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputPathList(List) %}
    - {% jdoc !!core::PMDConfiguration#addInputPath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#addInputPath(Path) %}
    - {% jdoc !!core::PMDConfiguration#getInputFilePath() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getInputFile() %}
    - {% jdoc !!core::PMDConfiguration#getIgnoreFilePath() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getIgnoreFile() %}
    - {% jdoc !!core::PMDConfiguration#setInputFilePath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputFilePath(Path) %}
    - {% jdoc !!core::PMDConfiguration#setIgnoreFilePath(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setIgnoreFilePath(Path) %}
    - {% jdoc !!core::PMDConfiguration#getInputUri() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getUri() %}
    - {% jdoc !!core::PMDConfiguration#setInputUri(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setInputUri(URI) %}
    - {% jdoc !!core::PMDConfiguration#getReportFile() %} - It is now superseded by {% jdoc !!core::PMDConfiguration#getReportFilePath() %}
    - {% jdoc !!core::PMDConfiguration#setReportFile(String) %} - It is now superseded by {% jdoc !!core::PMDConfiguration#setReportFile(Path) %}
    - {% jdoc !!core::PMDConfiguration#isStressTest() %} and {% jdoc !!core::PMDConfiguration#setStressTest(boolean) %} - Will be removed with no replacement.
    - {% jdoc !!core::PMDConfiguration#isBenchmark() %} and {% jdoc !!core::PMDConfiguration#setBenchmark(boolean) %} - Will be removed with no replacement, the CLI will still support it.
    - {% jdoc core::cpd.CPD %} and {% jdoc core::cpd.CPD.StatusCode %} - PMD 7 will ship with a revamped CLI split from pmd-core. An alterative to programatically launch CPD analysis will be added in due time.

* In order to reduce the dependency on Apex Jorje classes, the method {% jdoc !!visualforce::lang.vf.DataType#fromBasicType(apex.jorje.semantic.symbol.type.BasicType) %}
  has been deprecated. The equivalent method {% jdoc visualforce::lang.vf.DataType#fromTypeName(java.lang.String) %} should be used instead.

#### 6.51.0

No changes.

#### 6.50.0

##### CPD CLI

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

#### 6.49.0

##### Deprecated API

* In order to reduce the dependency on Apex Jorje classes, the following methods have been deprecated.
  These methods all leaked internal Jorje enums. These enums have been replaced now by enums the
  PMD's AST package.
    * {% jdoc !!apex::lang.apex.ast.ASTAssignmentExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTBinaryExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTBooleanExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTPostfixExpression#getOperator() %}
    * {% jdoc !!apex::lang.apex.ast.ASTPrefixExpression#getOperator() %}

  All these classes have now a new `getOp()` method. Existing code should be refactored to use this method instead.
  It returns the new enums, like {% jdoc apex::lang.apex.ast.AssignmentOperator %}, and avoids
  the dependency to Jorje.

#### 6.48.0

##### CPD CLI

* CPD has a new CLI option `--debug`. This option has the same behavior as in PMD. It enables more verbose
  logging output.

##### Rule Test Framework

* The module "pmd-test", which contains support classes to write rule tests, now **requires Java 8**. If you depend on
  this module for testing your own custom rules, you'll need to make sure to use at least Java 8.
* The new module "pmd-test-schema" contains now the XSD schema and the code to parse the rule test XML files. The
  schema has been extracted in order to easily share it with other tools like the Rule Designer or IDE plugins.
* Test schema changes:
    * The attribute `isRegressionTest` of `test-code` is deprecated. The new
      attribute `disabled` should be used instead for defining whether a rule test should be skipped or not.
    * The attributes `reinitializeRule` and `useAuxClasspath` of `test-code` are deprecated and assumed true.
      They will not be replaced.
    * The new attribute `focused` of `test-code` allows disabling all tests except the focused one temporarily.
* More information about the rule test framework can be found in the documentation:
  [Testing your rules](pmd_userdocs_extending_testing.html)

##### Deprecated API

* The experimental Java AST class {% jdoc java::lang.java.ast.ASTGuardedPattern %} has been deprecated and
  will be removed. It was introduced for Java 17 and Java 18 Preview as part of pattern matching for switch,
  but it is no longer supported with Java 19 Preview.
* The interface {% jdoc core::cpd.renderer.CPDRenderer %} is deprecated. For custom CPD renderers
  the new interface {% jdoc core::cpd.renderer.CPDReportRenderer %} should be used.
* The class {% jdoc test::testframework.TestDescriptor %} is deprecated, replaced with {% jdoc test-schema::test.schema.RuleTestDescriptor %}.
* Many methods of {% jdoc test::testframework.RuleTst %} have been deprecated as internal API.

##### Experimental APIs

* To support the Java preview language features "Pattern Matching for Switch" and "Record Patterns", the following
  AST nodes have been introduced as experimental:
    * {% jdoc java::lang.java.ast.ASTSwitchGuard %}
    * {% jdoc java::lang.java.ast.ASTRecordPattern %}
    * {% jdoc java::lang.java.ast.ASTComponentPatternList %}

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {%jdoc !!core::cpd.CPDConfiguration#setRenderer(net.sourceforge.pmd.cpd.Renderer) %}
* {%jdoc !!core::cpd.CPDConfiguration#setCPDRenderer(net.sourceforge.pmd.cpd.renderer.CPDRenderer) %}
* {%jdoc !!core::cpd.CPDConfiguration#getRenderer() %}
* {%jdoc !!core::cpd.CPDConfiguration#getCPDRenderer() %}
* {%jdoc !!core::cpd.CPDConfiguration#getRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc !!core::cpd.CPDConfiguration#getCPDRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc core::cpd.renderer.CPDRendererAdapter %}

#### 6.47.0

No changes.

#### 6.46.0

##### Deprecated ruleset references

Ruleset references with the following formats are now deprecated and will produce a warning
when used on the CLI or in a ruleset XML file:
- `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
- the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

Use the explicit forms of these references to be compatible with PMD 7.

##### Deprecated API

- {% jdoc core::RuleSetReferenceId#toString() %} is now deprecated. The format of this
  method will remain the same until PMD 7. The deprecation is intended to steer users
  away from relying on this format, as it may be changed in PMD 7.
- {% jdoc core::PMDConfiguration#getInputPaths() %} and
  {% jdoc core::PMDConfiguration#setInputPaths(java.lang.String) %} are now deprecated.
  A new set of methods have been added, which use lists and do not rely on comma splitting.

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- {% jdoc core::cpd.CPDCommandLineInterface %} has been internalized. In order to execute CPD either
  {% jdoc !!core::cpd.CPD#run(java.lang.String...) %} or {% jdoc !!core::cpd.CPD#main(java.lang.String[]) %}
  should be used.
- Several members of {% jdoc test::cli.BaseCPDCLITest %} have been deprecated with replacements.
- The methods {% jdoc !!core::ant.Formatter#start(java.lang.String) %},
  {% jdoc !!core::ant.Formatter#end(net.sourceforge.pmd.Report) %}, {% jdoc !!core::ant.Formatter#getRenderer() %},
  and {% jdoc !!core::ant.Formatter#isNoOutputSupplied() %} have been internalized.

#### 6.45.0

##### Experimental APIs

* Report has two new methods which allow limited mutations of a given report:
    * {% jdoc !!core::Report#filterViolations(net.sourceforge.pmd.util.Predicate) %} creates a new report with
      some violations removed with a given predicate based filter.
    * {% jdoc !!core::Report#union(net.sourceforge.pmd.Report) %} can combine two reports into a single new Report.
* {% jdoc !!core::util.Predicate %} will be replaced in PMD7 with the standard Predicate interface from java8.
* The module `pmd-html` is entirely experimental right now. Anything in the package
  `net.sourceforge.pmd.lang.html` should be used cautiously.

#### 6.44.0

##### Deprecated API

* Several members of {% jdoc core::PMD %} have been newly deprecated, including:
  - `PMD#EOL`: use `System#lineSeparator()`
  - `PMD#SUPPRESS_MARKER`: use {% jdoc core::PMDConfiguration#DEFAULT_SUPPRESS_MARKER %}
  - `PMD#processFiles`: use the new programmatic API
  - `PMD#getApplicableFiles`: is internal
* {% jdoc !!core::PMDConfiguration#prependClasspath(java.lang.String) %} is deprecated
  in favour of {% jdoc core::PMDConfiguration#prependAuxClasspath(java.lang.String) %}.
* {% jdoc !!core::PMDConfiguration#setRuleSets(java.lang.String) %} and
  {% jdoc core::PMDConfiguration#getRuleSets() %} are deprecated. Use instead
  {% jdoc core::PMDConfiguration#setRuleSets(java.util.List) %},
  {% jdoc core::PMDConfiguration#addRuleSet(java.lang.String) %},
  and {% jdoc core::PMDConfiguration#getRuleSetPaths() %}.
* Several members of {% jdoc test::cli.BaseCLITest %} have been deprecated with replacements.
* Several members of {% jdoc core::cli.PMDCommandLineInterface %} have been explicitly deprecated.
  The whole class however was deprecated long ago already with 6.30.0. It is internal API and should
  not be used.

* In modelica, the rule classes {% jdoc modelica::lang.modelica.rule.AmbiguousResolutionRule %}
  and {% jdoc modelica::lang.modelica.rule.ConnectUsingNonConnector %} have been deprecated,
  since they didn't comply to the usual rule class naming conventions yet.
  The replacements are in the subpackage `bestpractices`.

##### Experimental APIs

*   Together with the new programmatic API the interface
    {% jdoc core::lang.document.TextFile %} has been added as *experimental*. It intends
    to replace {% jdoc core::util.datasource.DataSource %} and {% jdoc core::cpd.SourceCode %} in the long term.
    
    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as {% jdoc core::lang.document.FileCollector %}
    decouples you from this. A file collector is available through {% jdoc !!core::PmdAnalysis#files() %}.

#### 6.43.0

##### Deprecated API

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.
- {% jdoc core::Report %}: the constructor and other construction methods like addViolation or createReport
- {% jdoc core::RuleContext %}: all constructors, getters and setters. A new set
of stable methods, matching those in PMD 7, was added to replace the `addViolation`
overloads of {% jdoc core::lang.rule.AbstractRule %}. In PMD 7, `RuleContext` will
be the API to report violations, and it can already be used as such in PMD 6.
- The field {% jdoc core::PMD#configuration %} is unused and will be removed.

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- {% jdoc core::RuleSet %}: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`
- {% jdoc !!core::renderers.AbstractAccumulatingRenderer#renderFileReport(Report) %} is internal API
  and should not be overridden in own renderers.

##### Changed API

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

{% jdoc core::RuleContext %} now requires setting the current rule before calling
{% jdoc core::Rule#apply(java.util.List, core::RuleContext) %}. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

#### 6.42.0

No changes.

#### 6.41.0

##### Command Line Interface

The command line options for PMD and CPD now use GNU-syle long options format. E.g. instead of `-rulesets` the
preferred usage is now `--rulesets`. Alternatively one can still use the short option `-R`.
Some options also have been renamed to a more consistent casing pattern at the same time
(`--fail-on-violation` instead of `-failOnViolation`).
The old single-dash options are still supported but are deprecated and will be removed with PMD 7.
This change makes the command line interface more consistent within PMD and also less surprising
compared to other cli tools.

The changes in detail for PMD:

|old option                     |new option|
|-------------------------------|----------|
| `-rulesets`                   | `--rulesets` (or `-R`) |
| `-uri`                        | `--uri` |
| `-dir`                        | `--dir` (or `-d`) |
| `-filelist`                   | `--file-list` |
| `-ignorelist`                 | `--ignore-list` |
| `-format`                     | `--format` (or `-f`) |
| `-debug`                      | `--debug` |
| `-verbose`                    | `--verbose` |
| `-help`                       | `--help` |
| `-encoding`                   | `--encoding` |
| `-threads`                    | `--threads` |
| `-benchmark`                  | `--benchmark` |
| `-stress`                     | `--stress` |
| `-shortnames`                 | `--short-names` |
| `-showsuppressed`             | `--show-suppressed` |
| `-suppressmarker`             | `--suppress-marker` |
| `-minimumpriority`            | `--minimum-priority` |
| `-property`                   | `--property` |
| `-reportfile`                 | `--report-file` |
| `-force-language`             | `--force-language` |
| `-auxclasspath`               | `--aux-classpath` |
| `-failOnViolation`            | `--fail-on-violation` |
| `--failOnViolation`           | `--fail-on-violation` |
| `-norulesetcompatibility`     | `--no-ruleset-compatibility` |
| `-cache`                      | `--cache` |
| `-no-cache`                   | `--no-cache` |

The changes in detail for CPD:

|old option             |new option|
|-----------------------|----------|
| `--failOnViolation`   | `--fail-on-violation` |
| `-failOnViolation`    | `--fail-on-violation` |
| `--filelist`          | `--file-list` |

#### 6.40.0

##### Experimental APIs

*   The interface {% jdoc apex::lang.apex.ast.ASTCommentContainer %} has been added to the Apex AST.
    It provides a way to check whether a node contains at least one comment. Currently this is only implemented for
    {% jdoc apex::lang.apex.ast.ASTCatchBlockStatement %} and used by the rule
    {% rule apex/errorprone/EmptyCatchBlock %}.
    This information is also available via XPath attribute `@ContainsComment`.

#### 6.39.0

No changes.

#### 6.38.0

No changes.

#### 6.37.0

##### PMD CLI

*   PMD has a new CLI option `-force-language`. With that a language can be forced to be used for all input files,
    irrespective of filenames. When using this option, the automatic language selection by extension is disabled
    and all files are tried to be parsed with the given language. Parsing errors are ignored and unparsable files
    are skipped.
    
    This option allows to use the xml language for files, that don't use xml as extension.
    See also the examples on [PMD CLI reference](pmd_userdocs_cli_reference.html#analyze-other-xml-formats).

##### Experimental APIs

*   The AST types and APIs around Sealed Classes are not experimental anymore:
    *   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#isNonSealed() %},
        {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getPermittedSubclasses() %}
    *   {% jdoc java::lang.java.ast.ASTPermitsList %}

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The inner class {% jdoc !!core::cpd.TokenEntry.State %} is considered to be internal API.
    It will probably be moved away with PMD 7.

#### 6.36.0

No changes.

#### 6.35.0

##### Deprecated API

*   {% jdoc !!core::PMD#doPMD(net.sourceforge.pmd.PMDConfiguration) %} is deprecated.
    Use {% jdoc !!core::PMD#runPMD(net.sourceforge.pmd.PMDConfiguration) %} instead.
*   {% jdoc !!core::PMD#run(java.lang.String[]) %} is deprecated.
    Use {% jdoc !!core::PMD#runPMD(java.lang.String...) %} instead.
*   {% jdoc core::ThreadSafeReportListener %} and the methods to use them in {% jdoc core::Report %}
    ({% jdoc core::Report#addListener(net.sourceforge.pmd.ThreadSafeReportListener) %},
    {% jdoc core::Report#getListeners() %}, {% jdoc core::Report#addListeners(java.util.List) %})
    are deprecated. This functionality will be replaced by another TBD mechanism in PMD 7.

#### 6.34.0

No changes.

#### 6.33.0

No changes.

#### 6.32.0

##### Experimental APIs

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

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The protected or public member of the Java rule {% jdoc java::lang.java.rule.bestpractices.AvoidUsingHardCodedIPRule %}
    are deprecated and considered to be internal API. They will be removed with PMD 7.

#### 6.31.0

##### Deprecated API

*   {% jdoc xml::lang.xml.rule.AbstractDomXmlRule %}
*   {% jdoc xml::lang.wsdl.rule.AbstractWsdlRule %}
*   A few methods of {% jdoc xml::lang.xml.rule.AbstractXmlRule %}

##### Experimental APIs

*   The method {% jdoc !!core::lang.ast.GenericToken#getKind() %} has been added as experimental. This
    unifies the token interface for both JavaCC and Antlr. The already existing method
    {% jdoc !!core::cpd.token.AntlrToken#getKind() %} is therefore experimental as well. The
    returned constant depends on the actual language and might change whenever the grammar
    of the language is changed.

#### 6.30.0

##### Deprecated API

###### Around RuleSet parsing

* {% jdoc core::RuleSetFactory %} and {% jdoc core::RulesetsFactoryUtils %} have been deprecated in favor of {% jdoc core::RuleSetLoader %}. This is easier to configure, and more maintainable than the multiple overloads of `RulesetsFactoryUtils`.
* Some static creation methods have been added to {% jdoc core::RuleSet %} for simple cases, eg {% jdoc core::RuleSet#forSingleRule(core::Rule) %}. These replace some counterparts in {% jdoc core::RuleSetFactory %}
* Since {% jdoc core::RuleSets %} is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* {% jdoc core::RuleSetReferenceId %}, {% jdoc core::RuleSetReference %}, {% jdoc core::RuleSetFactoryCompatibility %} are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

###### Around the `PMD` class

Many classes around PMD's entry point ({% jdoc core::PMD %}) have been deprecated as internal, including:
* The contents of the packages {% jdoc_package core::cli %}, {% jdoc_package core::processor %}
* {% jdoc core::SourceCodeProcessor %}
* The constructors of {% jdoc core::PMD %} (the class will be made a utility class)

###### Miscellaneous

*   {% jdoc !!java::lang.java.ast.ASTPackageDeclaration#getPackageNameImage() %},
    {% jdoc !!java::lang.java.ast.ASTTypeParameter#getParameterName() %}
    and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`,
    the attribute is `@Name`.
*   {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceBody#isAnonymousInnerClass() %},
    and {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceBody#isEnumChild() %},
    refs [#905](https://github.com/pmd/pmd/issues/905)

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc !!javascript::lang.ecmascript.Ecmascript3Handler %}
*   {% jdoc !!javascript::lang.ecmascript.Ecmascript3Parser %}
*   {% jdoc !!javascript::lang.ecmascript.ast.EcmascriptParser#parserOptions %}
*   {% jdoc !!javascript::lang.ecmascript.ast.EcmascriptParser#getSuppressMap() %}
*   {% jdoc !!core::lang.rule.ParametricRuleViolation %}
*   {% jdoc !!core::lang.ParserOptions#suppressMarker %}
*   {% jdoc !!modelica::lang.modelica.rule.ModelicaRuleViolationFactory %}

#### 6.29.0

No changes.

#### 6.28.0

##### Deprecated API

###### For removal

* {% jdoc !!core::RuleViolationComparator %}. Use {% jdoc !!core::RuleViolation#DEFAULT_COMPARATOR %} instead.
* {% jdoc !!core::cpd.AbstractTokenizer %}. Use {% jdoc !!core::cpd.AnyTokenizer %} instead.
* {% jdoc !!fortran::cpd.FortranTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!fortran::cpd.FortranLanguage#getTokenizer() %} anyway.
* {% jdoc !!perl::cpd.PerlTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!perl::cpd.PerlLanguage#getTokenizer() %} anyway.
* {% jdoc !!ruby::cpd.RubyTokenizer %}. Was replaced by an {% jdoc core::cpd.AnyTokenizer %}. Use {% jdoc !!ruby::cpd.RubyLanguage#getTokenizer() %} anyway.
* {% jdoc !!core::lang.rule.RuleReference#getOverriddenLanguage() %} and
  {% jdoc !!core::lang.rule.RuleReference#setLanguage(net.sourceforge.pmd.lang.Language) %}
* Antlr4 generated lexers:
    * {% jdoc !!cs::lang.cs.antlr4.CSharpLexer %} will be moved to package `net.sourceforge.pmd.lang.cs.ast` with PMD 7.
    * {% jdoc !!dart::lang.dart.antlr4.Dart2Lexer %} will be renamed to `DartLexer` and moved to package 
      `net.sourceforge.pmd.lang.dart.ast` with PMD 7. All other classes in the old package will be removed.
    * {% jdoc !!go::lang.go.antlr4.GolangLexer %} will be moved to package
      `net.sourceforge.pmd.lang.go.ast` with PMD 7. All other classes in the old package will be removed.
    * {% jdoc !!kotlin::lang.kotlin.antlr4.Kotlin %} will be renamed to `KotlinLexer` and moved to package 
      `net.sourceforge.pmd.lang.kotlin.ast` with PMD 7.
    * {% jdoc !!lua::lang.lua.antlr4.LuaLexer %} will be moved to package
      `net.sourceforge.pmd.lang.lua.ast` with PMD 7. All other classes in the old package will be removed.

#### 6.27.0

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

##### Deprecated API

###### For removal

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

#### 6.26.0

##### Deprecated API

###### For removal

* {% jdoc core::lang.rule.RuleChainVisitor %} and all implementations in language modules
* {% jdoc core::lang.rule.AbstractRuleChainVisitor %}
* {% jdoc !!core::lang.Language#getRuleChainVisitorClass() %}
* {% jdoc !!core::lang.BaseLanguageModule#<init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Class,java.lang.String...) %}
* {% jdoc core::lang.rule.ImportWrapper %}

#### 6.25.0

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.

*   Rule implementation classes are internal API and should not be used by clients directly.
    The rules should only be referenced via their entry in the corresponding category ruleset
    (e.g. `<rule ref="category/java/bestpractices.xml/AbstractClassWithoutAbstractMethod" />`).
    
    While we definitely won't move or rename the rule classes in PMD 6.x, we might consider changes
    in PMD 7.0.0 and onwards.

##### Deprecated APIs

###### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc java::lang.java.rule.AbstractIgnoredAnnotationRule %} (Java)
*   {% jdoc java::lang.java.rule.AbstractInefficientZeroCheck %} (Java)
*   {% jdoc java::lang.java.rule.AbstractJUnitRule %} (Java)
*   {% jdoc java::lang.java.rule.AbstractJavaMetricsRule %} (Java)
*   {% jdoc java::lang.java.rule.AbstractLombokAwareRule %} (Java)
*   {% jdoc java::lang.java.rule.AbstractPoorMethodCall %} (Java)
*   {% jdoc java::lang.java.rule.bestpractices.AbstractSunSecureRule %} (Java)
*   {% jdoc java::lang.java.rule.design.AbstractNcssCountRule %} (Java)
*   {% jdoc java::lang.java.rule.documentation.AbstractCommentRule %} (Java)
*   {% jdoc java::lang.java.rule.performance.AbstractOptimizationRule %} (Java)
*   {% jdoc java::lang.java.rule.regex.RegexHelper %} (Java)
*   {% jdoc apex::lang.apex.rule.AbstractApexUnitTestRule %} (Apex)
*   {% jdoc apex::lang.apex.rule.design.AbstractNcssCountRule %} (Apex)
*   {% jdoc plsql::lang.plsql.rule.design.AbstractNcssCountRule %} (PLSQL)
*   {% jdoc apex::lang.apex.ApexParser %}
*   {% jdoc apex::lang.apex.ApexHandler %}
*   {% jdoc core::RuleChain %}
*   {% jdoc core::RuleSets %}
*   {% jdoc !!core::RulesetsFactoryUtils#getRuleSets(java.lang.String, net.sourceforge.pmd.RuleSetFactory) %}

###### For removal

*   {% jdoc !!core::cpd.TokenEntry#TokenEntry(java.lang.String, java.lang.String, int) %}
*   {% jdoc test::testframework.AbstractTokenizerTest %}. Use CpdTextComparisonTest in module pmd-lang-test instead.
    For details see
    [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
    in the developer documentation.
*   {% jdoc !!apex::lang.apex.ast.ASTAnnotation#suppresses(core::Rule) %} (Apex)
*   {% jdoc apex::lang.apex.rule.ApexXPathRule %} (Apex)
*   {% jdoc java::lang.java.rule.SymbolTableTestRule %} (Java)
*   {% jdoc !!java::lang.java.rule.performance.InefficientStringBufferingRule#isInStringBufferOperation(net.sourceforge.pmd.lang.ast.Node, int, java.lang.String) %}

#### 6.24.0

##### Deprecated APIs

*   {% jdoc !ca!core::lang.BaseLanguageModule#addVersion(String, LanguageVersionHandler, boolean) %}
*   Some members of {% jdoc core::lang.ast.TokenMgrError %}, in particular, a new constructor is available
    that should be preferred to the old ones
*   {% jdoc core::lang.antlr.AntlrTokenManager.ANTLRSyntaxError %}

##### Experimental APIs

**Note:** Experimental APIs are identified with the annotation {% jdoc core::annotation.Experimental %},
see its javadoc for details

* The experimental methods in {% jdoc !ca!core::lang.BaseLanguageModule %} have been replaced by a
definitive API.

#### 6.23.0

##### Deprecated APIs

###### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc core::lang.rule.xpath.AbstractXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.JaxenXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.SaxonXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.XPathRuleQuery %}

###### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated in the **Apex**, **Javascript**, **PL/SQL**, **Scala** and **Visualforce** ASTs:

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc visualforce::lang.vf.ast.VfNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The implementation classes of {% jdoc core::lang.Parser %} (eg {% jdoc visualforce::lang.vf.VfParser %}) are deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.
*   The implementation classes of {% jdoc core::lang.TokenManager %} (eg {% jdoc visualforce::lang.vf.VfTokenManager %}) are deprecated and should not be used outside of our implementation.
    **This also affects CPD-only modules**.

These deprecations are added to the following language modules in this release.
Please look at the package documentation to find out the full list of deprecations.
* Apex: **{% jdoc_package apex::lang.apex.ast %}**
* Javascript: **{% jdoc_package javascript::lang.ecmascript.ast %}**
* PL/SQL: **{% jdoc_package plsql::lang.plsql.ast %}**
* Scala: **{% jdoc_package scala::lang.scala.ast %}**
* Visualforce: **{% jdoc_package visualforce::lang.vf.ast %}**

These deprecations have already been rolled out in a previous version for the
following languages:
* Java: {% jdoc_package java::lang.java.ast %}
* Java Server Pages: {% jdoc_package jsp::lang.jsp.ast %}
* Velocity Template Language: {% jdoc_package vm::lang.vm.ast %}

Outside of these packages, these changes also concern the following TokenManager
implementations, and their corresponding Parser if it exists (in the same package):

*   {% jdoc cpp::lang.cpp.CppTokenManager %}
*   {% jdoc java::lang.java.JavaTokenManager %}
*   {% jdoc javascript::lang.ecmascript5.Ecmascript5TokenManager %}
*   {% jdoc jsp::lang.jsp.JspTokenManager %}
*   {% jdoc matlab::lang.matlab.MatlabTokenManager %}
*   {% jdoc modelica::lang.modelica.ModelicaTokenManager %}
*   {% jdoc objectivec::lang.objectivec.ObjectiveCTokenManager %}
*   {% jdoc plsql::lang.plsql.PLSQLTokenManager %}
*   {% jdoc python::lang.python.PythonTokenManager %}
*   {% jdoc visualforce::lang.vf.VfTokenManager %}
*   {% jdoc vm::lang.vm.VmTokenManager %}


In the **Java AST** the following attributes are deprecated and will issue a warning when used in XPath rules:

*   {% jdoc !!java::lang.java.ast.ASTAdditiveExpression#getImage() %} - use `getOperator()` instead
*   {% jdoc !!java::lang.java.ast.ASTVariableDeclaratorId#getImage() %} - use `getName()` instead
*   {% jdoc !!java::lang.java.ast.ASTVariableDeclaratorId#getVariableName() %} - use `getName()` instead

###### For removal

*   {% jdoc !!core::lang.Parser#getTokenManager(java.lang.String,java.io.Reader) %}
*   {% jdoc !!core::lang.TokenManager#setFileName(java.lang.String) %}
*   {% jdoc !!core::lang.ast.AbstractTokenManager#setFileName(java.lang.String) %}
*   {% jdoc !!core::lang.ast.AbstractTokenManager#getFileName(java.lang.String) %}
*   {% jdoc !!core::cpd.token.AntlrToken#getType() %} - use `getKind()` instead.
*   {% jdoc core::lang.rule.ImmutableLanguage %}
*   {% jdoc core::lang.rule.MockRule %}
*   {% jdoc !!core::lang.ast.Node#getFirstParentOfAnyType(java.lang.Class[]) %}
*   {% jdoc !!core::lang.ast.Node#getAsDocument() %}
*   {% jdoc !!core::lang.ast.AbstractNode#hasDescendantOfAnyType(java.lang.Class[]) %}
*   {% jdoc !!java::lang.java.ast.ASTRecordDeclaration#getComponentList() %}
*   Multiple fields, constructors and methods in {% jdoc core::lang.rule.XPathRule %}. See javadoc for details.

#### 6.22.0

##### Deprecated APIs

###### Internal API

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

###### For removal

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

###### In ASTs (JSP)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the JSP AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc jsp::lang.jsp.ast.JspNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The class {% jdoc jsp::lang.jsp.JspParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at {% jdoc_package jsp::lang.jsp.ast %} to find out the full list of deprecations.

###### In ASTs (Velocity)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the VM AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc vm::lang.vm.ast.VmNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The package {% jdoc_package vm::lang.vm.directive %} as well as the classes
    {% jdoc vm::lang.vm.util.DirectiveMapper %} and {% jdoc vm::lang.vm.util.LogUtil %} are deprecated
    for removal. They were only used internally during parsing.
*   The class {% jdoc vm::lang.vm.VmParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at {% jdoc_package vm::lang.vm.ast %} to find out the full list of deprecations.

##### PLSQL AST

The production and node `ASTCursorBody` was unnecessary, not used and has been removed. Cursors have been already
parsed as `ASTCursorSpecification`.

#### 6.21.0

##### Deprecated APIs

###### Internal API

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

* {% jdoc core::lang.ast.CharStream %}, {% jdoc core::lang.ast.JavaCharStream %},
{% jdoc core::lang.ast.SimpleCharStream %}: these are APIs used by our JavaCC
implementations and that will be moved/refactored for PMD 7.0.0. They should not
be used, extended or implemented directly.
* All classes generated by JavaCC, eg {% jdoc java::lang.java.ast.JJTJavaParserState %}.
This includes token classes, which will be replaced with a single implementation, and
subclasses of {% jdoc core::lang.ast.ParseException %}, whose usages will be replaced
by just that superclass.

###### For removal

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

#### 6.20.0

No changes.

#### 6.19.0

##### Deprecated APIs

###### For removal

* pmd-core
  * All the package {% jdoc_package core::dcd %} and its subpackages. See {% jdoc core::dcd.DCD %}.
  * In {% jdoc core::lang.LanguageRegistry %}:
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguageVersion(List) %}
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguage(List) %}
    * {% jdoc core::lang.LanguageRegistry#findAllVersions() %}
    * {% jdoc core::lang.LanguageRegistry#findLanguageVersionByTerseName(String) %}
    * {% jdoc core::lang.LanguageRegistry#getInstance() %}
  * {% jdoc !!core::RuleSet#getExcludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileExclusions() %} instead.
  * {% jdoc !!core::RuleSet#getIncludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileInclusions() %} instead.
  * {% jdoc !!core::lang.Parser#canParse() %}
  * {% jdoc !!core::lang.Parser#getSuppressMap() %}
  * {% jdoc !!core::rules.RuleBuilder#RuleBuilder(String,String,String) %}. Use the new constructor with the correct ResourceLoader instead.
  * {% jdoc !!core::rules.RuleFactory#RuleFactory() %}. Use the new constructor with the correct ResourceLoader instead.
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.AbstractJavaRule#getDeclaringType(Node) %}.
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclarator %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getMethodName() %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getBlock() %}
  * {% jdoc java::lang.java.ast.ASTConstructorDeclaration#getParameterCount() %}
* pmd-apex
  * {% jdoc apex::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc apex::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}

###### Internal APIs

* pmd-core
  * All the package {% jdoc_package core::util %} and its subpackages,
  except {% jdoc_package core::util.datasource %} and {% jdoc_package core::util.database %}.
  * {% jdoc core::cpd.GridBagHelper %}
  * {% jdoc core::renderers.ColumnDescriptor %}


#### 6.18.0

##### Changes to Renderer

*   Each renderer has now a new method {% jdoc !!core::renderers.Renderer#setUseShortNames(List) %} which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    {% jdoc !!core::renderers.AbstractRenderer#determineFileName(String) %} should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.
    
    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

##### Deprecated APIs

###### For removal

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

###### Internal APIs

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
  * {% jdoc_package core::cache %}
* pmd-java
  * {% jdoc_package java::lang.java.typeresolution %}: Everything, including
    subpackages, except {% jdoc java::lang.java.typeresolution.TypeHelper %} and
    {% jdoc java::lang.java.typeresolution.typedefinition.JavaTypeDefinition %}.
  * {% jdoc !c!java::lang.java.ast.ASTCompilationUnit#getClassTypeResolver() %}


#### 6.17.0

No changes.

#### 6.16.0

##### Deprecated APIs

> Reminder: Please don't use members marked with the annotation {% jdoc core::annotation.InternalApi %}, as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


###### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
  * In the meantime you should use interfaces like {% jdoc java::lang.java.ast.JavaNode %} or  {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package, to refer to nodes generically.
  * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at {% jdoc_package java::lang.java.ast %} to find out the full list
of deprecations.


#### 6.15.0

##### Deprecated APIs

###### For removal

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
    *   {% jdoc !q!apex::lang.apex.ast.DumpFacade %}
    *   {% jdoc !q!java::lang.java.ast.DumpFacade %}
    *   {% jdoc !q!javascript::lang.ecmascript.ast.DumpFacade %}
    *   {% jdoc !q!jsp::lang.jsp.ast.DumpFacade %}
    *   {% jdoc !q!plsql::lang.plsql.ast.DumpFacade %}
    *   {% jdoc !q!visualforce::lang.vf.ast.DumpFacade %}
    *   {% jdoc !q!vm::lang.vm.ast.AbstractVmNode#dump(String, boolean, Writer) %}
    *   {% jdoc !q!xml::lang.xml.ast.DumpFacade %}
*   The method {% jdoc !c!core::lang.LanguageVersionHandler#getDumpFacade(Writer, String, boolean) %} will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of {% jdoc core::lang.LanguageVersionHandler %}.

#### 6.14.0

No changes.

#### 6.13.0

##### Command Line Interface

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

##### Deprecated API

*   {% jdoc core::renderers.CodeClimateRule %} is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

#### 6.12.0

No changes.

#### 6.11.0

* {% jdoc core::lang.rule.stat.StatisticalRule %} and the related helper classes and base rule classes
are deprecated for removal in 7.0.0. This includes all of {% jdoc_package core::stat %} and {% jdoc_package core::lang.rule.stat %},
and also {% jdoc java::lang.java.rule.AbstractStatisticalJavaRule %}, {% jdoc apex::lang.apex.rule.AbstractStatisticalApexRule %} and the like.
The methods {% jdoc !c!core::Report#addMetric(core::stat.Metric) %} and {% jdoc core::ThreadSafeReportListener#metricAdded(core::stat.Metric) %}
will also be removed.
* {% jdoc core::properties.PropertySource#setProperty(core::properties.MultiValuePropertyDescriptor, Object[]) %} is deprecated,
because {% jdoc core::properties.MultiValuePropertyDescriptor %} is deprecated as well

#### 6.10.0

##### Properties framework

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PDr props::PropertyDescriptor %}
{% jdoc_nspace :PF props::PropertyFactory %}

The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

###### Changes to how you define properties

* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
construction through builders. The builder hierarchy, currently found in the package {% jdoc_package props::builders %},
is being replaced by the simpler {% jdoc props::PropertyBuilder %}. Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like {% jdoc props::IntegerProperty %} and {% jdoc props::StringMultiProperty %} will gradually
all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the {% jdoc props::PropertyDescriptor %}
interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class {% jdoc :PF %} will become
from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
by a corresponding method on `PropertyFactory`:
  * {% jdoc props::IntegerProperty %} is replaced by {% jdoc !c!:PF#intProperty(java.lang.String) %}
    * {% jdoc props::IntegerMultiProperty %} is replaced by {% jdoc !c!:PF#intListProperty(java.lang.String) %}

  * {% jdoc props::FloatProperty %} and {% jdoc props::DoubleProperty %} are both replaced by {% jdoc !c!:PF#doubleProperty(java.lang.String) %}.
    Having a separate property for floats wasn't that useful.
    * Similarly, {% jdoc props::FloatMultiProperty %} and {% jdoc props::DoubleMultiProperty %} are replaced by {% jdoc !c!:PF#doubleListProperty(java.lang.String) %}.

  * {% jdoc props::StringProperty %} is replaced by {% jdoc !c!:PF#stringProperty(java.lang.String) %}
    * {% jdoc props::StringMultiProperty %} is replaced by {% jdoc !c!:PF#stringListProperty(java.lang.String) %}

  * {% jdoc props::RegexProperty %} is replaced by {% jdoc !c!:PF#regexProperty(java.lang.String) %}

  * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumProperty(java.lang.String,java.util.Map) %}
    * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumListProperty(java.lang.String,java.util.Map) %}

  * {% jdoc props::BooleanProperty %} is replaced by {% jdoc !c!:PF#booleanProperty(java.lang.String) %}
    * Its multi-valued counterpart, {% jdoc props::BooleanMultiProperty %}, is not replaced, because it doesn't have a use case.

  * {% jdoc props::CharacterProperty %} is replaced by {% jdoc !c!:PF#charProperty(java.lang.String) %}
    * {% jdoc props::CharacterMultiProperty %} is replaced by {% jdoc !c!:PF#charListProperty(java.lang.String) %}

  * {% jdoc props::LongProperty %} is replaced by {% jdoc !c!:PF#longIntProperty(java.lang.String) %}
    * {% jdoc props::LongMultiProperty %} is replaced by {% jdoc !c!:PF#longIntListProperty(java.lang.String) %}

  * {% jdoc props::MethodProperty %}, {% jdoc props::FileProperty %}, {% jdoc props::TypeProperty %} and their multi-valued counterparts
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



###### Architectural simplifications

* {% jdoc props::EnumeratedPropertyDescriptor %}, {% jdoc props::NumericPropertyDescriptor %}, {% jdoc props::PackagedPropertyDescriptor %},
and the related builders (in {% jdoc_package props::builders %}) will be removed.
These specialized interfaces allowed additional constraints to be enforced on the
value of a property, but made the property class hierarchy very large and impractical
to maintain. Their functionality will be mapped uniformly to {% jdoc props::constraints.PropertyConstraint %}s,
which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
related methods {% jdoc !c!props::PropertyTypeId#isPropertyNumeric() %} and
{% jdoc !c!props::PropertyTypeId#isPropertyPackaged() %} are also deprecated.

* {% jdoc props::MultiValuePropertyDescriptor %} and {% jdoc props::SingleValuePropertyDescriptor %}
are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
between single- and multi-valued properties. The method {% jdoc !c!:PDr#isMultiValue() %} will be removed
accordingly.

###### Changes to the PropertyDescriptor interface

* {% jdoc :PDr#preferredRowCount() %} is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods {% jdoc :PDr#uiOrder() %} and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method {% jdoc :PDr#propertyErrorFor(core::Rule) %} is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `{% jdoc !a!:PDr#valueFrom(java.lang.String) %} and `String `{% jdoc :PDr#asDelimitedString(java.lang.Object) %}`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* {% jdoc :PDr#isMultiValue() %} and {% jdoc :PDr#type() %} are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* {% jdoc :PDr#errorFor(java.lang.Object) %} is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

##### Deprecated APIs

{% jdoc_nspace :xpath core::lang.ast.xpath %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :rule core::Rule %}
{% jdoc_nspace :lvh core::lang.LanguageVersionHandler %}
{% jdoc_nspace :rset core::RuleSet %}
{% jdoc_nspace :rsets core::RuleSets %}

###### For internalization

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package {% jdoc_package :xpath %})
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only {% jdoc xpath::Attribute %} remains public API.

*   The classes {% jdoc props::PropertyDescriptorField %}, {% jdoc props::builders.PropertyDescriptorBuilderConversionWrapper %}, and the methods
    {% jdoc !c!:PDr#attributeValuesById %}, {% jdoc !c!:PDr#isDefinedExternally() %} and {% jdoc !c!props::PropertyTypeId#getFactory() %}.
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class {% jdoc props::ValueParserConstants %} and the interface {% jdoc props::ValueParser %}.

*   All classes from {% jdoc_package java::lang.java.metrics.impl.visitors %} are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    {% jdoc jast::JavaParserVisitorAdapter %} should be directly subclassed.

*   {% jdoc !ac!:lvh#getDataFlowHandler() %}, {% jdoc !ac!:lvh#getDFAGraphRule() %}

*   {% jdoc core::lang.VisitorStarter %}

###### For removal

*   All classes from {% jdoc_package props::modules %} will be removed.

*   The interface {% jdoc jast::Dimensionable %} has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from {% jdoc jast::ASTLocalVariableDeclaration %} and {% jdoc jast::ASTFieldDeclaration %} have
    also been deprecated:

    *   {% jdoc jast::ASTFieldDeclaration %} won't be a {% jdoc jast::TypeNode %} come 7.0.0, so
        {% jdoc jast::ASTFieldDeclaration#getType() %} and
        {% jdoc jast::ASTFieldDeclaration#getTypeDefinition() %} are deprecated.

    *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`{% jdoc jast::ASTVariableDeclaratorId %}`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

    *   In {% jdoc_package :jast %}: {% jdoc jast::JavaParserDecoratedVisitor %}, {% jdoc jast::JavaParserControllessVisitor %},
        {% jdoc jast::JavaParserControllessVisitorAdapter %}, and {% jdoc jast::JavaParserVisitorDecorator %} are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

    *   {% jdoc cpp::lang.cpp.CppHandler %}
    *   {% jdoc cpp::lang.cpp.CppLanguageModule %}
    *   {% jdoc cpp::lang.cpp.CppParser %}
    *   {% jdoc cs::lang.cs.CsLanguageModule %}
    *   {% jdoc fortran::lang.fortran.FortranLanguageModule %}
    *   {% jdoc groovy::lang.groovy.GroovyLanguageModule %}
    *   {% jdoc matlab::lang.matlab.MatlabHandler %}
    *   {% jdoc matlab::lang.matlab.MatlabLanguageModule %}
    *   {% jdoc matlab::lang.matlab.MatlabParser %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCHandler %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCLanguageModule %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCParser %}
    *   {% jdoc php::lang.php.PhpLanguageModule %}
    *   {% jdoc python::lang.python.PythonHandler %}
    *   {% jdoc python::lang.python.PythonLanguageModule %}
    *   {% jdoc python::lang.python.PythonParser %}
    *   {% jdoc ruby::lang.ruby.RubyLanguageModule %}
    *   {% jdoc scala::lang.scala.ScalaLanguageModule %}
    *   {% jdoc swift::lang.swift.SwiftLanguageModule %}


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
removal:
  * In {% jdoc :rule %}: {% jdoc !a!:rule#isDfa() %}, {% jdoc !a!:rule#isTypeResolution() %}, {% jdoc !a!:rule#isMultifile() %} and their
    respective setters.
  * In {% jdoc :rset %}: {% jdoc !a!:rset#usesDFA(core::lang.Language) %}, {% jdoc !a!:rset#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rset#usesMultifile(core::lang.Language) %}
  * In {% jdoc :rsets %}: {% jdoc !a!:rsets#usesDFA(core::lang.Language) %}, {% jdoc !a!:rsets#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rsets#usesMultifile(core::lang.Language) %}
  * In {% jdoc :lvh %}: {% jdoc !a!:lvh#getDataFlowFacade() %}, {% jdoc !a!:lvh#getSymbolFacade() %}, {% jdoc !a!:lvh#getSymbolFacade(java.lang.ClassLoader) %},
    {% jdoc !a!:lvh#getTypeResolutionFacade(java.lang.ClassLoader) %}, {% jdoc !a!:lvh#getQualifiedNameResolutionFacade(java.lang.ClassLoader) %}

#### 6.9.0

No changes.

#### 6.8.0

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

#### 6.7.0

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

#### 6.5.0

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

#### 6.4.0

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methods in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.

#### 6.2.0

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

#### 6.1.0

* The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
  * The default implementation provided in  `AbstractNode`, will
  be removed with 7.0.0
  * With 7.0.0, the `Node.toString` method will not necessarily provide its XPath node
  name anymore.

* The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface
`net.sourceforge.pmd.cpd.renderer.CPDRenderer` has been introduced to replace it. The main
difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on
large projects, with many duplications, it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

  `net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

#### 6.0.1

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.

### List of currently deprecated rules

These rules will be removed with PMD 7.0.0.

* Since 6.0.0: The Java rules `NcssConstructorCount` (design), `NcssMethodCount` (design),
  and `NcssTypeCount` (design) have been deprecated. They will be replaced by the new rule
  {% rule java/design/NcssCount %}.

* Since 6.0.0: The Java rule `LooseCoupling` in ruleset `java-typeresolution` is deprecated. Use the rule with the
  same name from category `bestpractices` instead: {% rule java/bestpractices/LooseCoupling %}.

* Since 6.0.0: The Java rule `CloneMethodMustImplementCloneable` in ruleset `java-typeresolution` is deprecated.
  Use the rule with the same name from category `errorprone` instead:
  {% rule java/errorprone/CloneMethodMustImplementCloneable %}.

* Since 6.0.0: The Java rule `UnusedImports` in ruleset `java-typeresolution` is deprecated. Use the rule with
  the same name from category `bestpractices` instead: `UnusedImports` (bestpractices).

* Since 6.0.0: The Java rule `SignatureDeclareThrowsException` in ruleset `java-typeresolution` is deprecated.
  Use the rule with the same name from category `design` instead:
  {% rule java/design/SignatureDeclareThrowsException %}.

* Since 6.0.0: The Java rule `EmptyStaticInitializer` in ruleset `java-empty` is deprecated.
  Use the rule {% rule java/errorprone/EmptyInitializer %} instead, which covers both static and non-static
  empty initializers.

* Since 6.0.0: The Java rules `GuardDebugLogging` (ruleset `java-logging-jakarta-commons`) and
  `GuardLogStatementJavaUtil` (ruleset `java-logging-java`) have been deprecated. Use the rule
  {% rule java/bestpractices/GuardLogStatement %} instead, which covers all cases regardless of the logging framework.

* Since 6.2.0: The Java rules `WhileLoopsMustUseBraces` (codestyle),
  `ForLoopsMustUseBraces` (codestyle), `IfStmtsMustUseBraces` (codestyle), and
  `IfElseStmtsMustUseBraces` (codestyle) are deprecated. They will be replaced by the new rule
  {% rule java/codestyle/ControlStatementBraces %}.

* Since 6.3.0: The Java rule `AbstractNaming` (codestyle) is deprecated
  in favour of {% rule java/codestyle/ClassNamingConventions %}.

* Since 6.7.0: The Java rules `VariableNamingConventions` (codestyle),
  `MIsLeadingVariableName` (codestyle), `SuspiciousConstantFieldName` (codestyle), and
  `AvoidPrefixingMethodParameters` (codestyle) are now deprecated. They are replaced by the more general
  {% rule java/codestyle/FieldNamingConventions %}, {% rule java/codestyle/FormalParameterNamingConventions %}, and
  {% rule java/codestyle/LocalVariableNamingConventions %}.

* Since 6.11.0: The Java rule `UnsynchronizedStaticDateFormatter` (multithreading) has been deprecated.
  The rule is replaced by the more general {% rule java/multithreading/UnsynchronizedStaticFormatter %}.

* Since 6.15.0: The Apex rule `VariableNamingConventions` (codestyle) has been deprecated. The rule is
  replaced by the more general rules {% rule apex/codestyle/FieldNamingConventions %},
  {% rule apex/codestyle/FormalParameterNamingConventions %}, {% rule apex/codestyle/LocalVariableNamingConventions %},
  and {% rule apex/codestyle/PropertyNamingConventions %}.

* Since 6.15.0: The Java rule `LoggerIsNotStaticFinal` (errorprone) has been deprecated.
  The rule is replaced by {% rule java/errorprone/ProperLogger %}.

* Since 6.16.0: The Java rule `AvoidFinalLocalVariable` (codestyle) has been deprecated.
  The rule is controversial and also contradicts other existing rules such as
  {% rule java/codestyle/LocalVariableCouldBeFinal %}. If the goal is to avoid defining
  constants in a scope smaller than the class, then the rule {% rule java/errorprone/AvoidDuplicateLiterals %}
  should be used instead.

* Since 6.19.0: The Java rule `InvalidSlf4jMessageFormat` (errorprone) has been renamed to
  {% rule java/errorprone/InvalidLogMessageFormat %}.

* Since 6.24.0: The two Java rules `PositionLiteralsFirstInComparisons` (bestpractices)
  and `PositionLiteralsFirstInCaseInsensitiveComparisons` (bestpractices)
  have been deprecated in favor of the new rule {% rule java/bestpractices/LiteralsFirstInComparisons %}.

* Since 6.27.0: The Java rule `DataflowAnomalyAnalysis` (errorprone)
  is deprecated in favour of {% rule java/bestpractices/UnusedAssignment %},
  which was introduced in PMD 6.26.0.

* Since 6.29.0: The Apex rules {% rule apex/performance/AvoidDmlStatementsInLoops %},
  {% rule apex/performance/AvoidSoqlInLoops %}, and {% rule apex/performance/AvoidSoslInLoops %} are deprecated
  in favor of the new rule {% rule apex/performance/OperationWithLimitsInLoop %}.

* Since 6.29.0: The Java rule `DoNotCallSystemExit` (errorprone) has been renamed to
  {% rule/java/errorprone/DoNotTerminateVM %}.

* Since 6.31:0: The Java rule `AvoidUsingShortType` (performance) is deprecated
  for removal without replacement.

* Since 6.31.0: The Java rule `SimplifyStartsWith` (performance) is deprecated
  for removal without replacement.

* Since 6.34.0: The Java rules `UnusedImports` (bestpractices), `DuplicateImports` (codestyle),
  `DontImportJavaLang` (codestyle), and `ImportFromSamePackage` (errorprone) are
  deprecated. These rules are replaced by {% rule java/codestyle/UnnecessaryImport %}.

* Since 6.35.0: The Java rule `DefaultPackage` (codestyle) has been deprecated in favor of
  {% rule java/codestyle/CommentDefaultAccessModifier %}.

* Since 6.35.0: The Java rule `CloneThrowsCloneNotSupportedException` (errorprone) has been
  deprecated without replacement.

* Since 6.36.0: The Java rule `BadComparison` (errorprone) has been renamed to
  {% rule java/errorprone/ComparisonWithNaN %}.

* Since 6.37.0: The following Java rules are deprecated and removed from the quickstart ruleset,
  as the new rule {% rule java/bestpractices/SimplifiableTestAssertion %} merges
  their functionality:
  * `UseAssertEqualsInsteadOfAssertTrue` (bestpractices)
  * `UseAssertNullInsteadOfAssertTrue` (bestpractices)
  * `UseAssertSameInsteadOfAssertTrue` (bestpractices)
  * `UseAssertTrueInsteadOfAssertEquals` (bestpractices)
  * `SimplifyBooleanAssertion` (design)

* Since 6.37.0: The Java rule `ReturnEmptyArrayRatherThanNull` (errorprone) is deprecated and removed from
  the quickstart ruleset, as the new rule {% rule java/errorprone/ReturnEmptyCollectionRatherThanNull %}
  supersedes it.

* Since 6.37.0: The following Java rules are deprecated and removed from the quickstart ruleset,
  as the new rule {% rule java/bestpractices/PrimitiveWrapperInstantiation %} merges
  their functionality:
  * `BooleanInstantiation` (performance)
  * `ByteInstantiation` (performance)
  * `IntegerInstantiation` (performance)
  * `LongInstantiation` (performance)
  * `ShortInstantiation` (performance)

* Since 6.37.0: The Java rule `UnnecessaryWrapperObjectCreation` (performance) is deprecated
  with no planned replacement before PMD 7. In its current state, the rule is not useful
  as it finds only contrived cases of creating a primitive wrapper and unboxing it explicitly
  in the same expression. In PMD 7 this and more cases will be covered by a
  new rule {% rule java/codestyle/UnnecessaryBoxing %}.

* Since 6.37.0: The Java rule `MissingBreakInSwitch` (errorprone) has been renamed to
  {% rule java/errorprone/ImplicitSwitchFallThrough %}.

* Since 6.46.0: The following Java rules are deprecated and removed from the quickstart ruleset, as the new rule
  {% rule java/codestyle/EmptyControlStatement %} merges their functionality:
    * {% rule java/errorprone/EmptyFinallyBlock %}
    * {% rule java/errorprone/EmptyIfStmt %}
    * {% rule java/errorprone/EmptyInitializer %}
    * {% rule java/errorprone/EmptyStatementBlock %}
    * {% rule java/errorprone/EmptySwitchStatements %}
    * {% rule java/errorprone/EmptySynchronizedBlock %}
    * {% rule java/errorprone/EmptyTryBlock %}
    * {% rule java/errorprone/EmptyWhileStmt %}

* Since 6.52.0: The Java rule {% rule java/errorprone/BeanMembersShouldSerialize %} has been renamed to
  {% rule java/errorprone/NonSerializableClass %}.

* Since 6.53.0: The Java rules {% rule java/design/ExcessiveClassLength %} and
  {% rule java/design/ExcessiveMethodLength %} have been deprecated. The rule
  {% rule java/design/NcssCount %} can be used instead.

* Since 6.53.0: The Java rule {% rule java/errorprone/EmptyStatementNotInLoop %} is deprecated.
  Use the rule {% rule java/codestyle/UnnecessarySemicolon %} instead.
