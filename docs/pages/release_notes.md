---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

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
