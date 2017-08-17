---
title: Basic Ecmascript
summary: Rules concerning basic ECMAScript guidelines.
permalink: pmd_rules_ecmascript_basic.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/basic.xml
keywords: Basic Ecmascript, AssignmentInOperand, UnreachableCode, InnaccurateNumericLiteral, ConsistentReturn, ScopeForInVariable, EqualComparison, GlobalVariable, AvoidTrailingComma, UseBaseWithParseInt
---
## AssignmentInOperand

**Since:** PMD 5.0

**Priority:** Medium High (2)

Avoid assignments in operands; this can make code more complicated and harder to read.  This is sometime
indicative of the bug where the assignment operator '=' was used instead of the equality operator '=='.

```
//IfStatement[$allowIf = "false"]/child::node()[1]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
|
    //WhileLoop[$allowWhile = "false"]/child::node()[1]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
|
    //DoLoop[$allowWhile = "false"]/child::node()[2]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
|
    //ForLoop[$allowFor = "false"]/child::node()[2]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
|
   //ConditionalExpression[$allowTernary = "false"]/child::node()[1]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
|
   //ConditionalExpression[$allowTernaryResults = "false"]/child::node()[position() = 2 or position() = 3]/descendant-or-self::node()[self::Assignment or self::UnaryExpression[$allowIncrementDecrement = "false" and (@Image = "--" or @Image = "++")]]
```

**Example(s):**

``` javascript
var x = 2;
// Bad
if ((x = getX()) == 3) {
    alert('3!');
}

function getX() {
    return 3;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|allowIf|false|Allow assignment within the conditional expression of an if statement|
|allowFor|false|Allow assignment within the conditional expression of a for statement|
|allowWhile|false|Allow assignment within the conditional expression of a while statement|
|allowTernary|false|Allow assignment within the conditional expression of a ternary operator|
|allowTernaryResults|false|Allow assignment within the result expressions of a ternary operator|
|allowIncrementDecrement|false|Allow increment or decrement operators within the conditional expression of an if, for, or while statement|

## AvoidTrailingComma

**Since:** PMD 5.1

**Priority:** High (1)

This rule helps improve code portability due to differences in browser treatment of trailing commas in object or array literals.

```
//ObjectLiteral[$allowObjectLiteral = "false" and @TrailingComma = 'true']
|
//ArrayLiteral[$allowArrayLiteral = "false" and @TrailingComma = 'true']
```

**Example(s):**

``` javascript
function(arg) {
    var obj1 = { a : 1 };   // Ok
    var arr1 = [ 1, 2 ];    // Ok

    var obj2 = { a : 1, };  // Syntax error in some browsers!
    var arr2 = [ 1, 2, ];   // Length 2 or 3 depending on the browser!
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|allowObjectLiteral|false|Allow a trailing comma within an object literal|
|allowArrayLiteral|false|Allow a trailing comma within an array literal|

## ConsistentReturn

**Since:** PMD 5.0

**Priority:** Medium High (2)

ECMAScript does provide for return types on functions, and therefore there is no solid rule as to their usage.
However, when a function does use returns they should all have a value, or all with no value.  Mixed return
usage is likely a bug, or at best poor style.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.ecmascript.rule.basic.ConsistentReturnRule](https://github.com/pmd/pmd/blob/master/pmd-javascript/src/main/java/net/sourceforge/pmd/lang/ecmascript/rule/basic/ConsistentReturnRule.java)

**Example(s):**

``` javascript
// Ok
function foo() {
    if (condition1) {
        return true;
    }
    return false;
}

// Bad
function bar() {
    if (condition1) {
        return;
    }
    return false;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|rhinoLanguageVersion|VERSION_DEFAULT|Specifies the Rhino Language Version to use for parsing.  Defaults to Rhino default.|
|recordingLocalJsDocComments|true|Specifies that JsDoc comments are produced in the AST.|
|recordingComments|true|Specifies that comments are produced in the AST.|

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

## GlobalVariable

**Since:** PMD 5.0

**Priority:** High (1)

This rule helps to avoid using accidently global variables by simply missing the "var" declaration.
Global variables can lead to side-effects that are hard to debug.

```
//Assignment[Name/@GlobalName = 'true']
```

**Example(s):**

``` javascript
function(arg) {
    notDeclaredVariable = 1;    // this will create a global variable and trigger the rule

    var someVar = 1;            // this is a local variable, that's ok

    window.otherGlobal = 2;     // this will not trigger the rule, although it is a global variable.
}
```

## InnaccurateNumericLiteral

**Since:** PMD 5.0

**Priority:** Medium High (2)

The numeric literal will have at different value at runtime, which can happen if you provide too much
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

## ScopeForInVariable

**Since:** PMD 5.0

**Priority:** High (1)

A for-in loop in which the variable name is not explicitly scoped to the enclosing scope with the 'var' keyword can
refer to a variable in an enclosing scope outside the nearest enclosing scope.  This will overwrite the
existing value of the variable in the outer scope when the body of the for-in is evaluated.  When the for-in loop
has finished, the variable will contain the last value used in the for-in, and the original value from before
the for-in loop will be gone.  Since the for-in variable name is most likely intended to be a temporary name, it
is better to explicitly scope the variable name to the nearest enclosing scope with 'var'.

```
//ForInLoop[not(child::VariableDeclaration)]/Name[1]
```

**Example(s):**

``` javascript
// Ok
function foo() {
    var p = 'clean';
    function() {
        var obj = { dirty: 'dirty' };
        for (var p in obj) { // Use 'var' here.
            obj[p] = obj[p];
        }
        return x;
    }();

    // 'p' still has value of 'clean'.
}
// Bad
function bar() {
    var p = 'clean';
    function() {
        var obj = { dirty: 'dirty' };
        for (p in obj) { // Oh no, missing 'var' here!
            obj[p] = obj[p];
        }
        return x;
    }();

    // 'p' is trashed and has value of 'dirty'!
}
```

## UnreachableCode

**Since:** PMD 5.0

**Priority:** High (1)

A 'return', 'break', 'continue', or 'throw' statement should be the last in a block. Statements after these
will never execute.  This is a bug, or extremely poor style.

```
//ReturnStatement[following-sibling::node()]
|
    //ContinueStatement[following-sibling::node()]
|
    //BreakStatement[following-sibling::node()]
|
    //ThrowStatement[following-sibling::node()]
```

**Example(s):**

``` javascript
// Ok
function foo() {
   return 1;
}
// Bad
function bar() {
   var x = 1;
   return x;
   x = 2;
}
```

## UseBaseWithParseInt

**Since:** PMD 5.0.1

**Priority:** High (1)

This rule checks for usages of parseInt. While the second parameter is optional and usually defaults
to 10 (base/radix is 10 for a decimal number), different implementations may behave differently.
It also improves readability, if the base is given.

See also: [parseInt()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt)

```
//FunctionCall/Name[
     @Image = 'parseInt'
     and
     count(../*) < 3
]
```

**Example(s):**

``` javascript
parseInt("010");    // unclear, could be interpreted as 10 or 7 (with a base of 7)

parseInt("10", 10); // good
```

