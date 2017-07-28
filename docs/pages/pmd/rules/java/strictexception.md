---
title: Strict Exceptions
summary: These rules provide some strict guidelines about throwing and catching exceptions.
permalink: pmd_rules_java_strictexception.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/strictexception.xml
---
## AvoidCatchingThrowable
**Since:** 1.2

**Priority:** Medium (3)

Catching Throwable errors is not recommended since its scope is very broad. It includes runtime issues such as 
OutOfMemoryError that should be exposed and managed separately.

**Example(s):**
```
public void bar() {
	try {
     // do something
    } catch (Throwable th) {  // should not catch Throwable
		th.printStackTrace();
    }
  }
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## SignatureDeclareThrowsException
**Since:** 1.2

**Priority:** Medium (3)

Methods that declare the generic Exception as a possible throwable are not very helpful since their
failure modes are unclear. Use a class derived from RuntimeException or a more specific checked exception.

**Example(s):**
```
public void foo() throws Exception {
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## ExceptionAsFlowControl
**Since:** 1.8

**Priority:** Medium (3)

Using Exceptions as form of flow control is not recommended as they obscure true exceptions when debugging.
Either add the necessary validation or use an alternate control structure.

**Example(s):**
```
public void bar() {
    try {
      try {
      } catch (Exception e) {
        throw new WrapperException(e);
       // this is essentially a GOTO to the WrapperException catch block
       }
     } catch (WrapperException e) {
     // do some more stuff
    }
  }
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|

## AvoidCatchingNPE
**Since:** 1.8

**Priority:** Medium (3)

Code should never throw NullPointerExceptions under normal circumstances.  A catch block may hide the 
original error, causing other, more subtle problems later on.

**Example(s):**
```
public class Foo {
  void bar() {
    try {
      // do something
      }  catch (NullPointerException npe) {
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

## AvoidThrowingRawExceptionTypes
**Since:** 1.8

**Priority:** High (1)

Avoid throwing certain exception types. Rather than throw a raw RuntimeException, Throwable,
Exception, or Error, use a subclassed exception or error instead.

**Example(s):**
```
public class Foo {
  public void bar() throws Exception {
    throw new Exception();
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

## AvoidThrowingNullPointerException
**Since:** 1.8

**Priority:** High (1)

Avoid throwing NullPointerExceptions. These are confusing because most people will assume that the
virtual machine threw it. Consider using an IllegalArgumentException instead; this will be
clearly seen as a programmer-initiated exception.

**Example(s):**
```
public class Foo {
  void bar() {
    throw new NullPointerException();
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

## AvoidRethrowingException
**Since:** 3.8

**Priority:** Medium (3)

Catch blocks that merely rethrow a caught exception only add to code size and runtime complexity.

**Example(s):**
```
public void bar() {
    try {
    // do something
    }  catch (SomeException se) {
       throw se;
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

## DoNotExtendJavaLangError
**Since:** 4.0

**Priority:** Medium (3)

Errors are system exceptions. Do not extend them.

**Example(s):**
```
public class Foo extends Error { }
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## DoNotThrowExceptionInFinally
**Since:** 4.2

**Priority:** Medium Low (4)

Throwing exceptions within a 'finally' block is confusing since they may mask other exceptions 
or code defects.
Note: This is a PMD implementation of the Lint4j rule "A throw in a finally block"

**Example(s):**
```
public class Foo {
	public void bar() {
		try {
			// Here do some stuff
		} catch( Exception e) {
			// Handling the issue
		} finally {
			// is this really a good idea ?
			throw new Exception();
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

## AvoidThrowingNewInstanceOfSameException
**Since:** 4.2.5

**Priority:** Medium (3)

Catch blocks that merely rethrow a caught exception wrapped inside a new instance of the same type only add to
code size and runtime complexity.

**Example(s):**
```
public void bar() {
      try {
       // do something
      }  catch (SomeException se) {
         // harmless comment      
           throw new SomeException(se);
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

## AvoidCatchingGenericException
**Since:** 4.2.6

**Priority:** Medium (3)

Avoid catching generic exceptions such as NullPointerException, RuntimeException, Exception in try-catch block

**Example(s):**
```
package com.igate.primitive;
    
public class PrimitiveType {
    
  public void downCastPrimitiveType() {
    try {
      System.out.println(" i [" + i + "]");
    } catch(Exception e) {
      e.printStackTrace();
    } catch(RuntimeException e) {
      e.printStackTrace();
    } catch(NullPointerException e) {
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

## AvoidLosingExceptionInformation
**Since:** 4.2.6

**Priority:** Medium High (2)

Statements in a catch block that invoke accessors on the exception without using the information
only add to code size.  Either remove the invocation, or use the return result.

**Example(s):**
```
public void bar() {
	try {
		// do something
	} catch (SomeException se) {
		se.getMessage();
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

