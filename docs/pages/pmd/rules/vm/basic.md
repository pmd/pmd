---
title: Basic Velocity
summary: The Basic Velocity ruleset contains basic rules for Apache Velocity pages.
permalink: pmd_rules_vm_basic.html
folder: pmd/rules/vm
sidebaractiveurl: /pmd_rules_vm.html
editmepath: ../pmd-vm/src/main/resources/rulesets/vm/basic.xml
---
## AvoidDeeplyNestedIfStmts

**Since:** 5.1

**Priority:** Medium (3)

Avoid creating deeply nested if-then statements since they are harder to read and error-prone to maintain.

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|problemDepth|3|The if statement depth reporting threshold|

## AvoidReassigningParameters

**Since:** 5.1

**Priority:** Medium High (2)

Reassigning values to incoming parameters is not recommended.  Use temporary local variables instead.

## CollapsibleIfStatements

**Since:** 5.1

**Priority:** Medium (3)

Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.

## EmptyForeachStmt

**Since:** 5.1

**Priority:** Medium High (2)

Empty foreach statements should be deleted.

## EmptyIfStmt

**Since:** 5.1

**Priority:** Medium High (2)

Empty if statements should be deleted.

## ExcessiveTemplateLength

**Since:** 5.1

**Priority:** Medium (3)

The template is too long. It should be broken up into smaller pieces.

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## NoInlineJavaScript

**Since:** 5.1

**Priority:** Medium High (2)

Avoid inline JavaScript. Import .js files instead.

## NoInlineStyles

**Since:** 5.1

**Priority:** Medium High (2)

Avoid inline styles. Use css classes instead.

## UnusedMacroParameter

**Since:** 5.1

**Priority:** Medium High (2)

Avoid unused macro parameters. They should be deleted.

