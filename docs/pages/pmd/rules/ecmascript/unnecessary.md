---
title: Unnecessary
summary: The Unnecessary Ruleset contains a collection of rules for unnecessary code.
permalink: pmd_rules_ecmascript_unnecessary.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/unnecessary.xml
keywords: Unnecessary, UnnecessaryParentheses, UnnecessaryBlock, NoElseReturn
---
## NoElseReturn

**Since:** PMD 5.5.0

**Priority:** Medium (3)

The else block in a if-else-construct is unnecessary if the `if` block contains a return.
Then the content of the else block can be put outside.

See also: <http://eslint.org/docs/rules/no-else-return>

```
//IfStatement[@Else="true"][Scope[1]/ReturnStatement]
```

**Example(s):**

``` javascript
// Bad:
if (x) {
    return y;
} else {
    return z;
}

// Good:
if (x) {
    return y;
}
return z;
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/ecmascript/unnecessary.xml/NoElseReturn" />
```

## UnnecessaryBlock

**Since:** PMD 5.0

**Priority:** Medium (3)

An unnecessary Block is present.  Such Blocks are often used in other languages to
introduce a new variable scope.  Blocks do not behave like this in ECMAScipt, and using them can
be misleading.  Considering removing this unnecessary Block.

```
//Block[not(parent::FunctionNode or parent::IfStatement or parent::ForLoop or parent::ForInLoop
    or parent::WhileLoop or parent::DoLoop or parent::TryStatement or parent::CatchClause)]
|
//Scope[not(parent::FunctionNode or parent::IfStatement or parent::ForLoop or parent::ForInLoop
    or parent::WhileLoop or parent::DoLoop or parent::TryStatement or parent::CatchClause)]
```

**Example(s):**

``` javascript
if (foo) {
    // Ok
}
if (bar) {
    {
        // Bad
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/ecmascript/unnecessary.xml/UnnecessaryBlock" />
```

## UnnecessaryParentheses

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Unnecessary parentheses should be removed.

```
//ParenthesizedExpression/ParenthesizedExpression
```

**Example(s):**

``` javascript
var x = 1; // Ok
var y = (1 + 1); // Ok
var z = ((1 + 1)); // Bad
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/ecmascript/unnecessary.xml/UnnecessaryParentheses" />
```

