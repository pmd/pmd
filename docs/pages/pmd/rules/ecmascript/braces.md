---
title: Braces
summary: The Braces Ruleset contains a collection of braces rules.
permalink: pmd_rules_ecmascript_braces.html
folder: pmd/rules/ecmascript
sidebaractiveurl: /pmd_rules_ecmascript.html
editmepath: ../pmd-javascript/src/main/resources/rulesets/ecmascript/braces.xml
---
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

