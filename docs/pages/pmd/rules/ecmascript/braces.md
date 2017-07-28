---
title: Braces
summary: The Braces Ruleset contains a collection of braces rules.
permalink: pmd_rules_ecmascript_braces.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/braces.xml
---
## IfStmtsMustUseBraces
**Since:** 5.0

**Priority:** Medium (3)

Avoid using if statements without using curly braces.

**Example(s):**
```
// Ok
if (foo) {
   x++;
}

// Bad
if (foo)
   x++;
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## IfElseStmtsMustUseBraces
**Since:** 5.0

**Priority:** Medium (3)

Avoid using if..else statements without using curly braces.

**Example(s):**
```
// Ok
if (foo) {
   x++;
} else {
   y++;
}

// Bad
if (foo)
   x++;
else
   y++;
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## WhileLoopsMustUseBraces
**Since:** 5.0

**Priority:** Medium (3)

Avoid using 'while' statements without using curly braces.

**Example(s):**
```
// Ok
while (true) {
   x++;
}

// Bad
while (true)
   x++;
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## ForLoopsMustUseBraces
**Since:** 5.0

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces.

**Example(s):**
```
// Ok
for (var i = 0; i < 42; i++) {
   foo();
}

// Bad
for (var i = 0; i < 42; i++)
   foo();
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

