---
title: VM Rules
permalink: pmd_rules_vm.html
folder: pmd/rules
---
List of rulesets and rules contained in each ruleset.

*   [Best Practices](pmd_rules_vm_bestpractices.html): Rules which enforce generally accepted best practices.
*   [Design](pmd_rules_vm_design.html): Rules that help you discover design issues.
*   [Errorprone](pmd_rules_vm_errorprone.html): Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.

## Best Practices
*   [AvoidReassigningParameters](pmd_rules_vm_bestpractices.html#avoidreassigningparameters): Reassigning values to incoming parameters is not recommended.  Use temporary local variables inst...
*   [UnusedMacroParameter](pmd_rules_vm_bestpractices.html#unusedmacroparameter): Avoid unused macro parameters. They should be deleted.

## Design
*   [AvoidDeeplyNestedIfStmts](pmd_rules_vm_design.html#avoiddeeplynestedifstmts): Avoid creating deeply nested if-then statements since they are harder to read and error-prone to ...
*   [CollapsibleIfStatements](pmd_rules_vm_design.html#collapsibleifstatements): Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with...
*   [ExcessiveTemplateLength](pmd_rules_vm_design.html#excessivetemplatelength): The template is too long. It should be broken up into smaller pieces.
*   [NoInlineJavaScript](pmd_rules_vm_design.html#noinlinejavascript): Avoid inline JavaScript. Import .js files instead.
*   [NoInlineStyles](pmd_rules_vm_design.html#noinlinestyles): Avoid inline styles. Use css classes instead.

## Errorprone
*   [EmptyForeachStmt](pmd_rules_vm_errorprone.html#emptyforeachstmt): Empty foreach statements should be deleted.
*   [EmptyIfStmt](pmd_rules_vm_errorprone.html#emptyifstmt): Empty if statements should be deleted.

