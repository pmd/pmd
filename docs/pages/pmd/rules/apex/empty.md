---
title: Empty Code
summary: The Empty Code ruleset contains rules that find empty statements of any kind (empty method, empty block statement, empty try or catch block,...).
permalink: pmd_rules_apex_empty.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/empty.xml
keywords: Empty Code, EmptyCatchBlock, EmptyIfStmt, EmptyTryOrFinallyBlock, EmptyWhileStmt, EmptyStatementBlock
---
## EmptyCatchBlock

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Empty Catch Block finds instances where an exception is caught, but nothing is done.  
In most circumstances, this swallows an exception which should either be acted on 
or reported.

```
//CatchBlockStatement[./BlockStatement[count(*) = 0]]
```

**Example(s):**

``` java
public void doSomething() {
  ...
  try {
    insert accounts;
  } catch (DmlException dmle) {
    // not good
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/empty.xml/EmptyCatchBlock" />
```

## EmptyIfStmt

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Empty If Statement finds instances where a condition is checked but nothing is done about it.

```
//IfBlockStatement
 [BlockStatement[count(*) = 0]]
```

**Example(s):**

``` java
public class Foo {
  public void bar(Integer x) {
    if (x == 0) {
      // empty!
    }
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/empty.xml/EmptyIfStmt" />
```

## EmptyStatementBlock

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Empty block statements serve no purpose and should be removed.

```
//Method/ModifierNode[@Abstract!='true' and ../BlockStatement[count(*) = 0]]
| //Method/BlockStatement//BlockStatement[count(*) = 0]
```

**Example(s):**

``` java
public class Foo {

   private int _bar;

   public void setBar(int bar) {
        // empty
   }

}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/empty.xml/EmptyStatementBlock" />
```

## EmptyTryOrFinallyBlock

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Avoid empty try or finally blocks - what's the point?

```
//TryCatchFinallyBlockStatement[./BlockStatement[count(*) = 0]]
```

**Example(s):**

``` java
public class Foo {
    public void bar() {
        try {
          // empty !
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Foo {
    public void bar() {
        try {
            int x=2;
        } finally {
            // empty!
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/empty.xml/EmptyTryOrFinallyBlock" />
```

## EmptyWhileStmt

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Empty While Statement finds all instances where a while statement does nothing.  
If it is a timing loop, then you should use Thread.sleep() for it; if it is
a while loop that does a lot in the exit expression, rewrite it to make it clearer.

```
//WhileLoopStatement[./BlockStatement[count(*) = 0]]
```

**Example(s):**

``` java
public void bar(Integer a, Integer b) {
  while (a == b) {
    // empty!
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|cc_categories|[Style]|Code Climate Categories|
|cc_remediation_points_multiplier|1|Code Climate Remediation Points multiplier|
|cc_block_highlighting|false|Code Climate Block Highlighting|

**Use this rule by referencing it:**
``` xml
<rule ref="category/apex/empty.xml/EmptyWhileStmt" />
```

