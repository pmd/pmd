---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.0.0-SNAPSHOT

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
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rule Designer

Thanks to [Clément Fournier](https://github.com/oowekyala), we now have a new rule designer GUI, which
is based on JavaFX. It replaces the old designer and can be started via

*   `bin/run.sh designer` (on Unix-like platform such as Linux and Mac OS X)
*   `bin\\designer.bat` (on Windows)

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

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph)'s work on type resolution for Java continues.
For this release he has extended support for method calls for both instance and static methods.

Method shadowing and overloading are supported, as are varargs. However, the selection of the target method upon the presence
of generics and type inference is still work in progress. Expect it in forecoming releases.

As for fields, the basic support was in place for release 5.8.0, but has now been expanded to support static fields.

#### Metrics Framework

As part of Google Summer of Code 2017, [Clément Fournier](https://github.com/oowekyala) is continuing his work
on the new metrics framework for object-oriented metrics.

There are already a couple of metrics (e.g. ATFD, WMC, Cyclo, LoC) implemented. More metrics are planned.
Based on those metrics, rules like "GodClass" detection can be implemented more easily.

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

*   The new Apex rule `AvoidDirectAccessTriggerMap` (category `errorprone`) helps to identify direct array access to triggers,
    which can produce bugs by either accessing non-existing indexes, or leaving them out. You should use for-each-loops
    instead.

*   The new Apex rule `AvoidHardcodingId` (category `errorprone`) detects hardcoded strings that look like identifiers
    and flags them. Record IDs change between environments, meaning hardcoded ids are bound to fail under a different
    setup.

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

#### Removed Rules

*   The deprecated Java rule `UseSingleton` has been removed from the ruleset `java-design`. The rule has been renamed
    long time ago to `UseUtilityClass` (category `design`).

#### Java Symbol Table

A [bug in symbol table](https://github.com/pmd/pmd/pull/549/commits/0958621ca884a8002012fc7738308c8dfc24b97c) prevented
the symbol table analysis to properly match primitive arrays types. The issue [affected the `java-unsedcode/UnusedPrivateMethod`](https://github.com/pmd/pmd/issues/521)
rule, but other rules may now produce improved results as consequence of this fix.

#### Apex Parser Update

The Apex parser version was bumped, from `1.0-sfdc-187` to `1.0-sfdc-224`. This update let us take full advantage
of the latest improvements from Salesforce, but introduces some breaking changes:

*   `BlockStatements` are now created for all control structures, even if no brace is used. We have therefore added
    a `hasCurlyBrace` method to differentiate between both scenarios.
*   New AST node types are available. In particular `CastExpression`, `ConstructorPreamble`, `IllegalStoreExpression`,
    `MethodBlockStatement`, `Modifier`, `MultiStatement`, `NestedExpression`, `NestedStoreExpression`,
    `NewKeyValueObjectExpression` and `StatementExecuted`
*   Some nodes have been removed. Such is the case of `TestNode`, `DottedExpression` and `NewNameValueObjectExpression`
    (replaced by `NewKeyValueObjectExpression`)

All existing rules have been updated to reflect these changes. If you have custom rules, be sure to update them.

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
*   apex
    *   [#265](https://github.com/pmd/pmd/issues/265): \[apex] Make Rule suppression work
    *   [#488](https://github.com/pmd/pmd/pull/488): \[apex] Use Apex lexer for CPD
    *   [#489](https://github.com/pmd/pmd/pull/489): \[apex] Update Apex compiler
    *   [#500](https://github.com/pmd/pmd/issues/500): \[apex] Running through CLI shows jorje optimization messages
    *   [#605](https://github.com/pmd/pmd/issues/605): \[apex] java.lang.NoClassDefFoundError in the latest build
    *   [#637](https://github.com/pmd/pmd/issues/637): \[apex] Avoid SOSL in loops
*   cpp
    *   [#448](https://github.com/pmd/pmd/issues/448): \[cpp] Write custom CharStream to handle continuation characters
*   java
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
    *   [#328](https://github.com/pmd/pmd/issues/328): \[java] java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/servlet/jsp/PageContext
    *   [#487](https://github.com/pmd/pmd/pull/487): \[java] Fix typeresolution for anonymous extending object
    *   [#496](https://github.com/pmd/pmd/issues/496): \[java] processing error on generics inherited from enclosing class
    *   [#510](https://github.com/pmd/pmd/issues/510): \[java] Typeresolution fails on a simple primary when the source is loaded from a class literal
    *   [#527](https://github.com/pmd/pmd/issues/527): \[java] Lombok getter annotation on enum is not recognized correctly
    *   [#534](https://github.com/pmd/pmd/issues/534): \[java] NPE in MethodTypeResolution for static methods
    *   [#603](https://github.com/pmd/pmd/issues/603): \[core] incremental analysis should invalidate upon Java rule plugin changes
    *   [#650](https://github.com/pmd/pmd/issues/650): \[java] ProcesingError analyzing code under 5.8.1
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

*   The properties API (rule and report properties) have been revamped to be fully typesafe. This is everything
    around `net.sourceforge.pmd.PropertyDescriptor`.

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
*   [#737](https://github.com/pmd/pmd/pull/737): \[doc] Fix NPathComplexity documentation bad rendering - [Clément Fournier](https://github.com/oowekyala)
*   [#744](https://github.com/pmd/pmd/pull/744): \[doc] Added Apex to supported languages - [Michał Kuliński](https://github.com/coola)
*   [#746](https://github.com/pmd/pmd/pull/746): \[doc] Fix typo in incremental analysis log message - [Clément Fournier](https://github.com/oowekyala)

