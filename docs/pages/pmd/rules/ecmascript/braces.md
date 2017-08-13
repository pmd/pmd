---
title: Braces
summary: The Braces Ruleset contains a collection of braces rules.
permalink: pmd_rules_ecmascript_braces.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/braces.xml
---
## ForLoopsMustUseBraces

**Since:** PMD 5.0

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces.

```
//ForLoop[not(child::Scope)]
|
	//ForInLoop[not(child::Scope)]
```

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

## IfElseStmtsMustUseBraces

**Since:** PMD 5.0

**Priority:** Medium (3)

Avoid using if..else statements without using curly braces.

```
//ExpressionStatement[parent::IfStatement[@Else = "true"]]
   [not(child::Scope)]
   [not(child::IfStatement)]
```

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

## IfStmtsMustUseBraces

**Since:** PMD 5.0

**Priority:** Medium (3)

Avoid using if statements without using curly braces.

```
//IfStatement[@Else = "false" and not(child::Scope)]
```

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

## WhileLoopsMustUseBraces

**Since:** PMD 5.0

**Priority:** Medium (3)

Avoid using 'while' statements without using curly braces.

```
//WhileLoop[not(child::Scope)]
```

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

