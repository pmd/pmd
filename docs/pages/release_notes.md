---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## ????? - 6.5.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.5.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

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
*   java-bestpractices
    *   [#651](https://github.com/pmd/pmd/issues/651): \[java] SwitchStmtsShouldHaveDefault should be aware of enum types
    *   [#869](https://github.com/pmd/pmd/issues/869): \[java] GuardLogStatement false positive on return statements and Math.log
*   java-codestyle
    *   [#667](https://github.com/pmd/pmd/issues/667): \[java] Make AtLeastOneConstructor Lombok-aware
    *   [#1154](https://github.com/pmd/pmd/pull/1154): \[java] CommentDefaultAccessModifierRule FP with nested enums
    *   [#1158](https://github.com/pmd/pmd/issues/1158): \[java] Fix IdenticalCatchBranches false positive
    *   [#1186](https://github.com/pmd/pmd/issues/1186): \[java] UnnecessaryFullyQualifiedName doesn't detect java.lang FQ names as violations
*   xml
    *   [#715](https://github.com/pmd/pmd/issues/715): \[xml] ProjectVersionAsDependencyVersion false positive

### API Changes

### External Contributions

*   [#1159](https://github.com/pmd/pmd/pull/1159): \[ui] Allow to setup the auxclasspath in the designer - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1169](https://github.com/pmd/pmd/pull/1169): \[core] Update stylesheets with a default namespace - [Matthew Duggan](https://github.com/mduggan)
*   [#1183](https://github.com/pmd/pmd/pull/1183): \[java] fixed typos in rule remediation - [Jake Hemmerle](https://github.com/jakehemmerle)
