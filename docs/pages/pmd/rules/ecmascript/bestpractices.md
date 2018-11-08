---
title: Best Practices
summary: Rules which enforce generally accepted best practices.
permalink: pmd_rules_ecmascript_bestpractices.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/category/ecmascript/bestpractices.xml
keywords: Best Practices, AvoidWithStatement, ConsistentReturn, GlobalVariable, ScopeForInVariable, UseBaseWithParseInt
language: Ecmascript
---
## AvoidWithStatement

**Since:** PMD 5.0.1

**Priority:** High (1)

Avoid using with - it's bad news

**This rule is defined by the following XPath expression:**
``` xpath
//WithStatement
```

**Example(s):**

``` javascript
with (object) {
    property = 3; // Might be on object, might be on window: who knows.
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/ecmascript/bestpractices.xml/AvoidWithStatement" />
```

## ConsistentReturn

**Since:** PMD 5.0

**Priority:** Medium High (2)

ECMAScript does provide for return types on functions, and therefore there is no solid rule as to their usage.
However, when a function does use returns they should all have a value, or all with no value.  Mixed return
usage is likely a bug, or at best poor style.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.ecmascript.rule.bestpractices.ConsistentReturnRule](https://github.com/pmd/pmd/blob/master/pmd-javascript/src/main/java/net/sourceforge/pmd/lang/ecmascript/rule/bestpractices/ConsistentReturnRule.java)

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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|rhinoLanguageVersion|VERSION\_DEFAULT|Specifies the Rhino Language Version to use for parsing.  Defaults to Rhino default.|no|
|recordingLocalJsDocComments|true|Specifies that JsDoc comments are produced in the AST.|no|
|recordingComments|true|Specifies that comments are produced in the AST.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/ecmascript/bestpractices.xml/ConsistentReturn" />
```

## GlobalVariable

**Since:** PMD 5.0

**Priority:** High (1)

This rule helps to avoid using accidently global variables by simply missing the "var" declaration.
Global variables can lead to side-effects that are hard to debug.

**This rule is defined by the following XPath expression:**
``` xpath
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/ecmascript/bestpractices.xml/GlobalVariable" />
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/ecmascript/bestpractices.xml/ScopeForInVariable" />
```

## UseBaseWithParseInt

**Since:** PMD 5.0.1

**Priority:** High (1)

This rule checks for usages of parseInt. While the second parameter is optional and usually defaults
to 10 (base/radix is 10 for a decimal number), different implementations may behave differently.
It also improves readability, if the base is given.

See also: [parseInt()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt)

**This rule is defined by the following XPath expression:**
``` xpath
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/ecmascript/bestpractices.xml/UseBaseWithParseInt" />
```

