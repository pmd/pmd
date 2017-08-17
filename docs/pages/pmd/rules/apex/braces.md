---
title: Braces
summary: The Braces ruleset contains rules regarding the use and placement of braces.
permalink: pmd_rules_apex_braces.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/braces.xml
keywords: Braces, IfStmtsMustUseBraces, WhileLoopsMustUseBraces, IfElseStmtsMustUseBraces, ForLoopsMustUseBraces
---
## ForLoopsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using 'for' statements without using surrounding braces. If the code formatting or
indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

```
//ForLoopStatement/BlockStatement[@CurlyBrace='false']
|
//ForEachStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
for (int i = 0; i < 42; i++) // not recommended
    foo();

for (int i = 0; i < 42; i++) { // preferred approach
    foo();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

## IfElseStmtsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting
or indentation is lost then it becomes difficult to separate the code being controlled
from the rest.

```
//IfBlockStatement/BlockStatement[@CurlyBrace='false'][count(child::*) > 0]
|
//IfElseBlockStatement/BlockStatement[@CurlyBrace='false'][count(child::*) > 0]
```

**Example(s):**

``` java
// this is OK
if (foo) x++;

// but this is not
if (foo)
    x = x+1;
else
    x = x-1;
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

## IfStmtsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//IfBlockStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

## WhileLoopsMustUseBraces

**Since:** PMD 5.6.0

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//WhileLoopStatement/BlockStatement[@CurlyBrace='false']
```

**Example(s):**

``` java
while (true)    // not recommended
    x++;

while (true) {  // preferred approach
    x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

