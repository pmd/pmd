---
title: Empty Code
summary: The Empty Code ruleset contains rules that find empty statements of any kind (empty method, empty block statement, empty try or catch block,...).
permalink: pmd_rules_java_empty.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/empty.xml
---
## EmptyCatchBlock
**Since:** 0.1

**Priority:** Medium (3)

Empty Catch Block finds instances where an exception is caught, but nothing is done.  
In most circumstances, this swallows an exception which should either be acted on 
or reported.

**Example(s):**
```
public void doSomething() {
  try {
    FileInputStream fis = new FileInputStream("/tmp/bugger");
  } catch (IOException ioe) {
      // not good
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|
|allowCommentedBlocks|false|Empty blocks containing comments will be skipped|
|allowExceptionNameRegex|^$|Empty blocks catching exceptions with names matching this regular expression will be skipped|

## EmptyIfStmt
**Since:** 0.1

**Priority:** Medium (3)

Empty If Statement finds instances where a condition is checked but nothing is done about it.

**Example(s):**
```
public class Foo {
 void bar(int x) {
  if (x == 0) {
   // empty!
  }
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyWhileStmt
**Since:** 0.2

**Priority:** Medium (3)

Empty While Statement finds all instances where a while statement does nothing.  
If it is a timing loop, then you should use Thread.sleep() for it; if it is
a while loop that does a lot in the exit expression, rewrite it to make it clearer.

**Example(s):**
```
void bar(int a, int b) {
	while (a == b) {
	// empty!
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyTryBlock
**Since:** 0.4

**Priority:** Medium (3)

Avoid empty try blocks - what's the point?

**Example(s):**
```
public class Foo {
 public void bar() {
  try {
  } catch (Exception e) {
    e.printStackTrace();
  }
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyFinallyBlock
**Since:** 0.4

**Priority:** Medium (3)

Empty finally blocks serve no purpose and should be removed.

**Example(s):**
```
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
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptySwitchStatements
**Since:** 1.0

**Priority:** Medium (3)

Empty switch statements serve no purpose and should be removed.

**Example(s):**
```
public void bar() {
	int x = 2;
	switch (x) {
	// once there was code here
	// but it's been commented out or something
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptySynchronizedBlock
**Since:** 1.3

**Priority:** Medium (3)

Empty synchronized blocks serve no purpose and should be removed.

**Example(s):**
```
public class Foo {
 public void bar() {
  synchronized (this) {
   // empty!
  }
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyStatementNotInLoop
**Since:** 1.5

**Priority:** Medium (3)

An empty statement (or a semicolon by itself) that is not used as the sole body of a 'for' 
or 'while' loop is probably a bug.  It could also be a double semicolon, which has no purpose
and should be removed.

**Example(s):**
```
public void doit() {
      // this is probably not what you meant to do
      ;
      // the extra semicolon here this is not necessary
      System.out.println("look at the extra semicolon");;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyInitializer
**Since:** 5.0

**Priority:** Medium (3)

Empty initializers serve no purpose and should be removed.

**Example(s):**
```
public class Foo {

   static {} // Why ?

   {} // Again, why ?

}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyStatementBlock
**Since:** 5.0

**Priority:** Medium (3)

Empty block statements serve no purpose and should be removed.

**Example(s):**
```
public class Foo {

   private int _bar;

   public void setBar(int bar) {
      { _bar = bar; } // Why not?
      {} // But remove this.
   }

}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## EmptyStaticInitializer
**Since:** 1.5

**Priority:** Medium (3)

An empty static initializer serve no purpose and should be removed.

**Example(s):**
```
public class Foo {
	static {
	// empty
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

