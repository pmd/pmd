---
title: Error Prone
summary: 
permalink: pmd_rules_ecmascript_errorprone.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/category/ecmascript/errorprone.xml
keywords: Error Prone, EqualComparison, InnaccurateNumericLiteral
---
## EqualComparison

**Since:** PMD 5.0

**Priority:** Medium (3)

Using == in condition may lead to unexpected results, as the variables are automatically casted to be of the
same type. The === operator avoids the casting.

```
//InfixExpression[(@Image = "==" or @Image = "!=")
  and
 (child::KeywordLiteral[@Image = "true" or @Image = "false"]
 or
 child::NumberLiteral)
]
```

**Example(s):**

``` javascript
// Ok
if (someVar === true) {
  ...
}
// Ok
if (someVar !== 3) {
  ...
}
// Bad
if (someVar == true) {
  ...
}
// Bad
if (someVar != 3) {
  ...
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/ecmascript/errorprone.xml/EqualComparison" />
```

## InnaccurateNumericLiteral

**Since:** PMD 5.0

**Priority:** Medium High (2)

The numeric literal will have a different value at runtime, which can happen if you provide too much
precision in a floating point number.  This may result in numeric calculations being in error.

```
//NumberLiteral[
    @Image != @Number
    and translate(@Image, "e", "E") != @Number
    and concat(@Image, ".0") != @Number
    and @Image != substring-before(translate(@Number, ".", ""), "E")]
```

**Example(s):**

``` javascript
var a = 9; // Ok
var b = 999999999999999; // Ok
var c = 999999999999999999999; // Not good
var w = 1.12e-4; // Ok
var x = 1.12; // Ok
var y = 1.1234567890123; // Ok
var z = 1.12345678901234567; // Not good
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/ecmascript/errorprone.xml/InnaccurateNumericLiteral" />
```

