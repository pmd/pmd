---
title: Ecmascript Rules
permalink: pmd_rules_ecmascript.html
folder: pmd/rules
---
List of rulesets and rules contained in each ruleset.

*   [Basic Ecmascript](pmd_rules_ecmascript_basic.html): Rules concerning basic ECMAScript guidelines.
*   [Braces](pmd_rules_ecmascript_braces.html): The Braces Ruleset contains a collection of braces rules.
*   [Controversial Ecmascript](pmd_rules_ecmascript_controversial.html): The Controversial ruleset contains rules that, for whatever reason, are considered controversial. They are held here to allow people to include them as they see fit within their custom rulesets.
*   [Unnecessary](pmd_rules_ecmascript_unnecessary.html): The Unnecessary Ruleset contains a collection of rules for unnecessary code.

## Basic Ecmascript
*   [AssignmentInOperand](pmd_rules_ecmascript_basic.html#assignmentinoperand): Avoid assignments in operands; this can make code more complicated and harder to read.  This is s...
*   [AvoidTrailingComma](pmd_rules_ecmascript_basic.html#avoidtrailingcomma): This rule helps improve code portability due to differences in browser treatment of trailing comm...
*   [ConsistentReturn](pmd_rules_ecmascript_basic.html#consistentreturn): ECMAScript does provide for return types on functions, and therefore there is no solid rule as to...
*   [EqualComparison](pmd_rules_ecmascript_basic.html#equalcomparison): Using == in condition may lead to unexpected results, as the variables are automatically casted t...
*   [GlobalVariable](pmd_rules_ecmascript_basic.html#globalvariable): This rule helps to avoid using accidently global variables by simply missing the "var" declaratio...
*   [InnaccurateNumericLiteral](pmd_rules_ecmascript_basic.html#innaccuratenumericliteral): The numeric literal will have at different value at runtime, which can happen if you provide too ...
*   [ScopeForInVariable](pmd_rules_ecmascript_basic.html#scopeforinvariable): A for-in loop in which the variable name is not explicitly scoped to the enclosing scope with the...
*   [UnreachableCode](pmd_rules_ecmascript_basic.html#unreachablecode): A 'return', 'break', 'continue', or 'throw' statement should be the last in a block. Statements a...
*   [UseBaseWithParseInt](pmd_rules_ecmascript_basic.html#usebasewithparseint): This rule checks for usages of parseInt. While the second parameter is optional and usually defau...

## Braces
*   [ForLoopsMustUseBraces](pmd_rules_ecmascript_braces.html#forloopsmustusebraces): Avoid using 'for' statements without using curly braces.
*   [IfElseStmtsMustUseBraces](pmd_rules_ecmascript_braces.html#ifelsestmtsmustusebraces): Avoid using if..else statements without using curly braces.
*   [IfStmtsMustUseBraces](pmd_rules_ecmascript_braces.html#ifstmtsmustusebraces): Avoid using if statements without using curly braces.
*   [WhileLoopsMustUseBraces](pmd_rules_ecmascript_braces.html#whileloopsmustusebraces): Avoid using 'while' statements without using curly braces.

## Controversial Ecmascript
*   [AvoidWithStatement](pmd_rules_ecmascript_controversial.html#avoidwithstatement): Avoid using with - it's bad news

## Unnecessary
*   [NoElseReturn](pmd_rules_ecmascript_unnecessary.html#noelsereturn): The else block in a if-else-construct is unnecessary if the 'if' block contains a return.Then the...
*   [UnnecessaryBlock](pmd_rules_ecmascript_unnecessary.html#unnecessaryblock): An unnecessary Block is present.  Such Blocks are often used in other languages tointroduce a new...
*   [UnnecessaryParentheses](pmd_rules_ecmascript_unnecessary.html#unnecessaryparentheses): Unnecessary parentheses should be removed.

