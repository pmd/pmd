---
title: Braces
summary: The Braces ruleset contains rules regarding the use and placement of braces.
permalink: pmd_rules_java_braces.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/braces.xml
keywords: Braces, IfStmtsMustUseBraces, WhileLoopsMustUseBraces, IfElseStmtsMustUseBraces, ForLoopsMustUseBraces
---
## ForLoopsMustUseBraces

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces. If the code formatting or 
indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

```
//ForStatement[not(Statement/Block)]
```

**Example(s):**

``` java
for (int i = 0; i < 42; i++)
   foo();
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/braces.xml/ForLoopsMustUseBraces" />
```

## IfElseStmtsMustUseBraces

**Since:** PMD 0.2

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting 
or indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

```
//Statement
 [parent::IfStatement[@Else='true']]
 [not(child::Block)]
 [not(child::IfStatement)]
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/braces.xml/IfElseStmtsMustUseBraces" />
```

## IfStmtsMustUseBraces

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//IfStatement[count(*) < 3][not(Statement/Block)]
```

**Example(s):**

``` java
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/braces.xml/IfStmtsMustUseBraces" />
```

## WhileLoopsMustUseBraces

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//WhileStatement[not(Statement/Block)]
```

**Example(s):**

``` java
while (true)    // not recommended
      x++;
      
while (true) {  // preferred approach
      x++;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/braces.xml/WhileLoopsMustUseBraces" />
```

