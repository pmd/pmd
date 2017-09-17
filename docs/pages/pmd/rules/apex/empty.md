---
title: Empty Code
summary: The Empty Code ruleset contains rules that find empty statements of any kind (empty method, empty block statement, empty try or catch block,...).
permalink: pmd_rules_apex_empty.html
folder: pmd/rules/apex
sidebaractiveurl: /pmd_rules_apex.html
editmepath: ../pmd-apex/src/main/resources/rulesets/apex/empty.xml
keywords: Empty Code, EmptyCatchBlock, EmptyIfStmt, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptyStaticInitializer
---
## EmptyCatchBlock

**Since:** PMD 5.8.2

**Priority:** Medium (3)

Empty Catch Block finds instances where an exception is caught, but nothing is done.  
In most circumstances, this swallows an exception which should either be acted on 
or reported.

```
//CatchStatement
 [count(Block/BlockStatement) = 0 and ($allowCommentedBlocks != 'true' or Block/@containsComment = 'false')]
 [FormalParameter/Type/ReferenceType
   /ClassOrInterfaceType[@Image != 'InterruptedException' and @Image != 'CloneNotSupportedException']
 ]
 [FormalParameter/VariableDeclaratorId[not(matches(@Image, $allowExceptionNameRegex))]]
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
|allowCommentedBlocks|false|Empty blocks containing comments will be skipped|
|allowExceptionNameRegex|^$|Empty blocks catching exceptions with names matching this regular expression will be skipped|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyCatchBlock" />
```

## EmptyFinallyBlock

**Since:** PMD 5.8.2

**Priority:** Medium (3)

Empty finally blocks serve no purpose and should be removed.

```
//FinallyStatement[count(Block/BlockStatement) = 0]
```

**Example(s):**

``` java
public class Foo {
  public void bar() {
    try {
      Integer x=2;
    } finally {
      // empty!
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyFinallyBlock" />
```

## EmptyIfStmt

**Since:** PMD 5.8.2

**Priority:** Medium (3)

Empty If Statement finds instances where a condition is checked but nothing is done about it.

```
//IfStatement/Statement
 [EmptyStatement or Block[count(*) = 0]]
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

## EmptyStaticInitializer

**Since:** PMD 5.8.2

**Priority:** Medium (3)

An empty static initializer serve no purpose and should be removed.

```
//Initializer[@Static='true']/Block[count(*)=0]
```

**Example(s):**

``` java
public class Foo {
    static {
        // empty
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyStaticInitializer" />
```

## EmptyTryBlock

**Since:** PMD 5.8.2

**Priority:** Medium (3)

Avoid empty try blocks - what's the point?

```
//TryStatement[not(ResourceSpecification)]/Block[1][count(*) = 0]
```

**Example(s):**

``` java
public class Foo {
  public void bar() {
    try {
      // this has no use
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/apex/empty.xml/EmptyTryBlock" />
```

## EmptyWhileStmt

**Since:** PMD 5.8.2

**Priority:** Medium (3)

Empty While Statement finds all instances where a while statement does nothing.  
If it is a timing loop, then you should use Thread.sleep() for it; if it is
a while loop that does a lot in the exit expression, rewrite it to make it clearer.

```
//WhileStatement/Statement[./Block[count(*) = 0]  or ./EmptyStatement]
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

