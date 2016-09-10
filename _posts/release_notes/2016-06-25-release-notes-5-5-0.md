---
title: Release Notes 5.5.0
tags: [release_notes]
keywords: release notes, announcements, what's new, new features
last_updated: June 25, 2016
summary: "Version 5.5.0 of the PMD Open Source Project, released June 25, 2016."
sidebar: mydoc_sidebar
permalink: 2016-06-25-release-notes-5-5-0.html
folder: mydoc
---

## System requirements

{% include note.html content="PMD and CPD need at least a java7 runtime environment. For analyzing Salesforce.com Apex source code, you’ll need a java8 runtime environment." %}

## New Supported Languages

*   Salesforce.com Apex is now supported by PMD and CPD. See [PR#86](https://github.com/pmd/pmd/pull/86).
*   CPD now supports Perl. See [PR#82](https://github.com/pmd/pmd/pull/82).
*   CPD now supports Swift. See [PR#33](https://github.com/adangel/pmd/pull/33).

## New and Modified Rules

*   New rules in Java:

    *   java-logging-java/InvalidSlf4jMessageFormat: Check for invalid message format in slf4j loggers. See [PR#73](https://github.com/pmd/pmd/pull/73).
    *   java-design/ConstantsInInterface: Avoid constants in interfaces. Interfaces should define types, constants are implementation details better placed in classes or enums. See Effective Java, item 19\. See [PR#93](https://github.com/pmd/pmd/pull/93).
*   Modified rules in Java:

    *   java-comments/CommentRequired: New property <tt>serialVersionUIDCommentRequired</tt> which controls the comment requirements for _serialVersionUID_ fields. By default, no comment is required for this field.
    *   java-design/UseVargs: public static void main method is ignored now and so are methods, that are annotated with Override. See [PR#79](https://github.com/pmd/pmd/pull/79).
*   New rules for Salesforce.com Apex:

    *   apex-complexity: AvoidDeeplyNestedIfStmts, ExcessiveParameterList, ExcessiveClassLength, NcssMethodCount, NcssTypeCount, NcssConstructorCount, StdCyclomaticComplexity, TooManyFields, ExcessivePublicCount
    *   apex-performance: AvoidDmlStatementsInLoops, AvoidSoqlInLoops
    *   apex-style: VariableNamingConventions, MethodNamingConventions, ClassNamingConventions, MethodWithSameNameAsEnclosingClass, AvoidLogicInTrigger, AvoidGlobalModifier
*   Javascript

    *   New Rule: ecmascript-unnecessary/NoElseReturn: The else block in a if-else-construct is unnecessary if the <tt>if</tt> block contains a return. Then the content of the else block can be put outside. See [#1486](https://sourceforge.net/p/pmd/bugs/1486/).

## Improvements and CLI Changes

*   A JSON-renderer for PMD which is compatible with CodeClimate. See [PR#83](https://github.com/pmd/pmd/pull/83).
*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): [core] [java] Provide backwards compatibility for PMD configuration file
*   CPD: If a complete filename is specified, the language dependent filename filter is not applied. This allows to scan files, that are not using the standard file extension. If a directory is specified, the filename filter is still applied and only those files with the correct file extension of the language are scanned.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)
*   CPD: New command line parameter <tt>--ignore-usings</tt>: Ignore using directives in C# when comparing text.
*   PMD: New command line parameter: <tt>-norulesetcompatibility</tt> - this disables the ruleset factory compatibility filter and fails, if e.g. an old rule name is used in the ruleset. See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/). This option is also available for the ant task: <tt><noRuleSetCompatibility>true</noRuleSetCompatibility></tt>.
*   PMD: New command line parameter: <tt>-filelist</tt>- this provides an alternative way to define, which files should be process by PMD. With this option, you can provide the path to a single file containing a comma delimited list of files to analyze. If this is given, then you don’t need to provide <tt>-dir</tt>. See [PR#98](https://github.com/pmd/pmd/pull/98).

## Pull Requests

*   [#25](https://github.com/adangel/pmd/pull/25): [cs] Added option to exclude C# using directives from CPD analysis
*   [#27](https://github.com/adangel/pmd/pull/27): [cpp] Added support for Raw String Literals (C++11).
*   [#29)(https://github.com/adangel/pmd/pull/29): [jsp] Added support for files with UTF-8 BOM to JSP tokenizer.
*   [#30](https://github.com/adangel/pmd/pull/30): [core] CPD: Removed file filter for files that are explicitly specified on the CPD command line using the ‘–files’ command line option.
*   [#31](https://github.com/adangel/pmd/pull/31): [core] CPD: Added file encoding detection to CPD.
*   [#32](https://github.com/adangel/pmd/pull/32): [objectivec] Extended Objective-C grammar to accept UTF-8 escapes (\uXXXX) in string literals.
*   [#33](https://github.com/adangel/pmd/pull/33): [swift] Added support for Swift to CPD.
*   [#34](https://github.com/adangel/pmd/pull/34): multiple code improvements: squid:S1192, squid:S1118, squid:S1066, squid:S1854, squid:S2864
*   [#35](https://github.com/adangel/pmd/pull/35): [javascript] Javascript tokenizer now ignores comment tokens.
*   [#72](https://github.com/pmd/pmd/pull/72): [java] [jsp] Added capability in Java and JSP parser for tracking tokens.
*   [#73](https://github.com/pmd/pmd/pull/73): [java] InvalidSlf4jMessageFormat: Add rule to look for invalid message format in slf4j loggers
*   [#74](https://github.com/pmd/pmd/pull/74): [java] CommentDefaultAccessModifier: Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): [core] RuleSetFactory Performance Enhancement
*   [#76](https://github.com/pmd/pmd/pull/76): [java] DoNotCallGarbageCollectionExplicitly: fix formatting typos in an example
*   [#77](https://github.com/pmd/pmd/pull/77): [java] [plsql] Fix various typos
*   [#78](https://github.com/pmd/pmd/pull/78): [java] MissingStaticMethodInNonInstantiatableClass: Add Builder pattern check
*   [#79](https://github.com/pmd/pmd/pull/79): [java] UseVarargs: do not flag public static void main(String[]), ignore @Override
*   [#80](https://github.com/pmd/pmd/pull/80): [site] Update mvn-plugin.md
*   [#82](https://github.com/pmd/pmd/pull/82): [perl] Add Perl support to CPD.
*   [#83](https://github.com/pmd/pmd/pull/83): [core] CodeClimateRenderer: Adds new Code Climate-compliant JSON renderer
*   [#84](https://github.com/pmd/pmd/pull/84): [java] EmptyMethodInAbstractClassShouldBeAbstract: Change rule’s description.
*   [#85](https://github.com/pmd/pmd/pull/85): [java] UseStringBufferForStringAppends: False Positive with Ternary Operator (#1340)
*   [#86](https://github.com/pmd/pmd/pull/86): [apex] Added language module for Salesforce.com Apex incl. rules ported from Java and new ones.
*   [#87](https://github.com/pmd/pmd/pull/87): [core] [apex] Customize Code Climate Json “categories” + “remediation_points” as PMD rule properties
*   [#88](https://github.com/pmd/pmd/pull/88): [core] [apex] Fixed typo in ruleset.xml and problems with the CodeClimate renderer
*   [#89](https://github.com/pmd/pmd/pull/89): [core] Some code enhancements
*   [#90](https://github.com/pmd/pmd/pull/90): [core] Refactored two test to stop using the deprecated ant class BuildFileTest
*   [#91](https://github.com/pmd/pmd/pull/91): [core] [java] [jsp] [plsql] [test] [vm] Small code enhancements, basically reordering variable declarations, constructors and variable modifiers
*   [#92](https://github.com/pmd/pmd/pull/92): [core] [apex] Improved Code Climate Renderer Output and a Bugfix for Apex StdCyclomaticComplexityRule on triggers
*   [#93](https://github.com/pmd/pmd/pull/93): [java] ConstantsInInterface: Add ConstantsInInterface rule. Effective Java, 19
*   [#94](https://github.com/pmd/pmd/pull/94): [core] [apex] Added property, fixed code climate renderer output and deleted unused rulessets
*   [#95](https://github.com/pmd/pmd/pull/95): [apex] AvoidDmlStatementsInLoops: New apex rule AvoidDmlStatementsInLoops
*   [#96](https://github.com/pmd/pmd/pull/96): [core] CodeClimateRenderer: Clean up Code Climate renderer
*   [#97](https://github.com/pmd/pmd/pull/97): [java] BooleanGetMethodName: Don’t report bad method names on @Override
*   [#98](https://github.com/pmd/pmd/pull/98): [core] PMD: Input filelist parameter
*   [#99](https://github.com/pmd/pmd/pull/99): [apex] Fixed Trigger name is reported incorrectly
*   [#100](https://github.com/pmd/pmd/pull/100): [core] CSVRenderer: escape filenames with commas in csvrenderer

## Bugfixes

*   java-basic
    *   [#1471](https://sourceforge.net/p/pmd/bugs/1471/): [java] DoubleCheckedLocking: False positives
    *   [#1424](https://sourceforge.net/p/pmd/bugs/1424/): [java] SimplifiedTernary: False positive with ternary operator
*   java-codesize
    *   [#1457](https://sourceforge.net/p/pmd/bugs/1457/): [java] TooManyMethods: counts inner class methods
*   java-comments
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): [java] CommentDefaultAccessModifier: triggers on field annotated with @VisibleForTesting
    *   [#1434](https://sourceforge.net/p/pmd/bugs/1434/): [java] CommentRequired: raises violation on serialVersionUID field
*   java-controversial
    *   [#1449](https://sourceforge.net/p/pmd/bugs/1449/): [java] AvoidUsingShortType: false positive when casting a variable to short
*   java-design
    *   [#1452](https://sourceforge.net/p/pmd/bugs/1452/): [java] AccessorClassGenerationRule: ArrayIndexOutOfBoundsException with Annotations
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): [java] CloseResource: false positive on Statement
    *   [#1438](https://sourceforge.net/p/pmd/bugs/1438/): [java] UseNotifyAllInsteadOfNotify: false positive
    *   [#1467](https://sourceforge.net/p/pmd/bugs/1467/): [java] UseUtilityClass: can’t correctly check functions with multiple annotations
*   java-finalizers
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): [java] AvoidCallingFinalize: NPE
*   java-imports
    *   [#1436](https://sourceforge.net/p/pmd/bugs/1436/): [java] UnnecessaryFullyQualifiedName: false positive on clashing static imports with enums
    *   [#1465](https://sourceforge.net/p/pmd/bugs/1465/): [java] UnusedImports: False Positve with javadoc @link
*   java-junit
    *   [#1373](https://sourceforge.net/p/pmd/bugs/1373/): [java] JUnitAssertionsShouldIncludeMessage: is no longer compatible with TestNG
    *   [#1453](https://sourceforge.net/p/pmd/bugs/1453/): [java] TestClassWithoutTestCases: false positive
*   java-migrating
    *   [#1446](https://sourceforge.net/p/pmd/bugs/1446/): [java] JUnit4TestShouldUseBeforeAnnotation: False positive when TestNG is used
*   java-naming
    *   [#1431](https://sourceforge.net/p/pmd/bugs/1431/): [java] SuspiciousEqualsMethodName: false positive
*   java-optimizations
    *   [#1443](https://sourceforge.net/p/pmd/bugs/1443/): [java] RedundantFieldInitializer: False positive for small floats
    *   [#1340](https://sourceforge.net/p/pmd/bugs/1340/): [java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-sunsecure
    *   [#1476](https://sourceforge.net/p/pmd/bugs/1476/): [java] ArrayIsStoredDirectly: False positive
    *   [#1475](https://sourceforge.net/p/pmd/bugs/1475/): [java] MethodReturnsInternalArray: False positive
*   java-unnecessary
    *   [#1464](https://sourceforge.net/p/pmd/bugs/1464/): [java] UnnecessaryFinalModifier: false positive on a @SafeVarargs method
    *   [#1422](https://sourceforge.net/p/pmd/bugs/1422/): [java] UselessQualifiedThis: False positive with Java 8 Function
*   java-unusedcode
    *   [#1456](https://sourceforge.net/p/pmd/bugs/1456/): [java] UnusedFormalParameter: should ignore overriding methods
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): [java] UnusedLocalVariable: false positive - parenthesis
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): [java] UnusedModifier: false positive on public modifier used with inner interface in enum
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): [java] UnusedPrivateField: False positive when local variable hides member variable hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): [core] XMLRenderer: Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): [java] Parser Error: Cast in return expression
    *   [#1441](https://sourceforge.net/p/pmd/bugs/1441/): [site] PMD: Update documentation how to compile after modularization
    *   [#1442](https://sourceforge.net/p/pmd/bugs/1442/): [java] PDMASMClassLoader: Java 9 Jigsaw readiness
    *   [#1455](https://sourceforge.net/p/pmd/bugs/1455/): [java] Parser: PMD doesn’t handle Java 8 explicit receiver parameters
    *   [#1458](https://sourceforge.net/p/pmd/bugs/1458/): [xml] Performance degradation scanning large XML files with XPath custom rules
    *   [#1461](https://sourceforge.net/p/pmd/bugs/1461/): [core] RuleSetFactory: Possible threading issue due to PR#75
    *   [#1470](https://sourceforge.net/p/pmd/bugs/1470/): [java] Parser: Error with type-bound lambda
    *   [#1478](https://sourceforge.net/p/pmd/bugs/1478/): [core] PMD CLI: Use first language as default if Java is not available
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): [core] CPD: no problems found results in blank file instead of empty xml
    *   [#1485](https://sourceforge.net/p/pmd/bugs/1485/): [apex] Analysis of some apex classes cause a stackoverflow error
    *   [#1488](https://sourceforge.net/p/pmd/bugs/1488/): [apex] Windows line endings falsify the location of issues
    *   [#1491](https://sourceforge.net/p/pmd/bugs/1491/): [core] CodeClimateRenderer: corrupt JSON output with real line breaks
    *   [#1492](https://sourceforge.net/p/pmd/bugs/1492/): [core] PMD CLI: IncompatibleClassChangeError when running PMD

{% include links.html %}
