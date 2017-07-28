---
title: Unnecessary
summary: The Unnecessary Ruleset contains a collection of rules for unnecessary code.
permalink: pmd_rules_ecmascript_unnecessary.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/unnecessary.xml
---
## UnnecessaryParentheses
**Since:** 5.0

**Priority:** Medium Low (4)

Unnecessary parentheses should be removed.

**Example(s):**
```
var x = 1; // Ok
var y = (1 + 1); // Ok
var z = ((1 + 1)); // Bad
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## UnnecessaryBlock
**Since:** 5.0

**Priority:** Medium (3)

An unnecessary Block is present.  Such Blocks are often used in other languages to
    introduce a new variable scope.  Blocks do not behave like this in ECMAScipt, and using them can
    be misleading.  Considering removing this unnecessary Block.

**Example(s):**
```
if (foo) {
   // Ok
}
if (bar) {
   {
      // Bad
   }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## NoElseReturn
**Since:** 5.5.0

**Priority:** Medium (3)

The else block in a if-else-construct is unnecessary if the `if` block contains a return.
    Then the content of the else block can be put outside.
    
    See also: http://eslint.org/docs/rules/no-else-return

**Example(s):**
```
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

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

