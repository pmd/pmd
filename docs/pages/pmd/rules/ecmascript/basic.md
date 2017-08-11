---
title: Basic Ecmascript
summary: Rules concerning basic ECMAScript guidelines.
permalink: pmd_rules_ecmascript_basic.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/basic.xml
---
## AssignmentInOperand

**Since:** 5.0

**Priority:** Medium High (2)

Avoid assignments in operands; this can make code more complicated and harder to read.  This is sometime
indicative of the bug where the assignment operator '=' was used instead of the equality operator '=='.

**Example(s):**

```
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

**Since:** 5.1

**Priority:** High (1)

This rule helps improve code portability due to differences in browser treatment of trailing commas in object or array literals.

**Example(s):**

```
function(arg) {
    var obj1 = { a : 1 }; // Ok
    var arr1 = [ 1, 2 ]; // Ok

    var obj2 = { a : 1, }; // Syntax error in some browsers!
    var arr2 = [ 1, 2, ]; // Length 2 or 3 depending on the browser!
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|allowObjectLiteral|false|Allow a trailing comma within an object literal|
|allowArrayLiteral|false|Allow a trailing comma within an array literal|

## ConsistentReturn

**Since:** 5.0

**Priority:** Medium High (2)

ECMAScript does provide for return types on functions, and therefore there is no solid rule as to their usage.
However, when a function does use returns they should all have a value, or all with no value.  Mixed return
usage is likely a bug, or at best poor style.

**Example(s):**

```
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

**Since:** 5.0

**Priority:** Medium (3)

Using == in condition may lead to unexpected results, as the variables are automatically casted to be of the
      same type. The === operator avoids the casting.

**Example(s):**

```
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

**Since:** 5.0

**Priority:** High (1)

This rule helps to avoid using accidently global variables by simply missing the "var" declaration.
Global variables can lead to side-effects that are hard to debug.

**Example(s):**

```
function(arg) {
    notDeclaredVariable = 1; // this will create a global variable and trigger the rule

    var someVar = 1; // this is a local variable, that's ok

    window.otherGlobal = 2; // this will not trigger the rule, although it is a global variable.
}
```

## InnaccurateNumericLiteral

**Since:** 5.0

**Priority:** Medium High (2)

The numeric literal will have at different value at runtime, which can happen if you provide too much
precision in a floating point number.  This may result in numeric calculations being in error.

**Example(s):**

```
var a = 9; // Ok
var b = 999999999999999; // Ok
var c = 999999999999999999999; // Not good
var w = 1.12e-4; // Ok
var x = 1.12; // Ok
var y = 1.1234567890123; // Ok
var z = 1.12345678901234567; // Not good
```

## ScopeForInVariable

**Since:** 5.0

**Priority:** High (1)

A for-in loop in which the variable name is not explicitly scoped to the enclosing scope with the 'var' keyword can
refer to a variable in an enclosing scope outside the nearest enclosing scope.  This will overwrite the
existing value of the variable in the outer scope when the body of the for-in is evaluated.  When the for-in loop
has finished, the variable will contain the last value used in the for-in, and the original value from before
the for-in loop will be gone.  Since the for-in variable name is most likely intended to be a temporary name, it
is better to explicitly scope the variable name to the nearest enclosing scope with 'var'.

**Example(s):**

```
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

**Since:** 5.0

**Priority:** High (1)

A 'return', 'break', 'continue', or 'throw' statement should be the last in a block. Statements after these
will never execute.  This is a bug, or extremely poor style.

**Example(s):**

```
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

**Since:** 5.0.1

**Priority:** High (1)

TODO

**Example(s):**

```
parseInt("10",base);
```

