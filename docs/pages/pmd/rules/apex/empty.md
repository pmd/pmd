---
title: Empty Code
summary: The Empty Code ruleset contains rules that find empty statements of any kind (empty method, empty block statement, empty try or catch block,...).
permalink: pmd_rules_apex_empty.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/empty.xml
keywords: Empty Code, EmptyCatchBlock, EmptyIfStmt, EmptyWhileStmt, EmptyTryOrFinallyBlock
---
## EmptyCatchBlock

**Since:** PMD 6.0.0

**Priority:** Medium (3)

Empty Catch Block finds instances where an exception is caught, but nothing is done.  
In most circumstances, this swallows an exception which should either be acted on 
or reported.

```
//CatchBlockStatement
[./BlockStatement[count(*) = 0]]
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyCatchBlock" />
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
  void bar(Integer x) {
   if (x == 0) {
     // empty!
   }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyIfStmt" />
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
        } catch (Exception e) {
            system.debug(e):
        } finally {
            // empty!
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyTryOrFinallyBlock" />
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
public class Foo {
  public void bar(Integer a, Integer b) {
    while (a == b) {
      // empty!
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyWhileStmt" />
```

