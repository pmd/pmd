---
title: Strict Exceptions
summary: These rules provide some strict guidelines about throwing and catching exceptions.
permalink: pmd_rules_java_strictexception.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/strictexception.xml
---
## AvoidCatchingGenericException

**Since:** PMD 4.2.6

**Priority:** Medium (3)

Avoid catching generic exceptions such as NullPointerException, RuntimeException, Exception in try-catch block

```
//CatchStatement/FormalParameter/Type/ReferenceType/ClassOrInterfaceType[
          @Image='NullPointerException' or
          @Image='Exception' or
          @Image='RuntimeException']
```

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

## AvoidCatchingNPE

**Since:** PMD 1.8

**Priority:** Medium (3)

Code should never throw NullPointerExceptions under normal circumstances.  A catch block may hide the 
original error, causing other, more subtle problems later on.

```
//CatchStatement/FormalParameter/Type
 /ReferenceType/ClassOrInterfaceType[@Image='NullPointerException']
```

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

## AvoidCatchingThrowable

**Since:** PMD 1.2

**Priority:** Medium (3)

Catching Throwable errors is not recommended since its scope is very broad. It includes runtime issues such as 
OutOfMemoryError that should be exposed and managed separately.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.AvoidCatchingThrowableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/AvoidCatchingThrowableRule.java)

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

## AvoidLosingExceptionInformation

**Since:** PMD 4.2.6

**Priority:** Medium High (2)

Statements in a catch block that invoke accessors on the exception without using the information
only add to code size.  Either remove the invocation, or use the return result.

```
//CatchStatement/Block/BlockStatement/Statement/StatementExpression/PrimaryExpression/PrimaryPrefix/Name
[
   @Image = concat(../../../../../../../FormalParameter/VariableDeclaratorId/@Image, '.getMessage')
   or
   @Image = concat(../../../../../../../FormalParameter/VariableDeclaratorId/@Image, '.getLocalizedMessage')
   or
   @Image = concat(../../../../../../../FormalParameter/VariableDeclaratorId/@Image, '.getCause')
   or
   @Image = concat(../../../../../../../FormalParameter/VariableDeclaratorId/@Image, '.getStackTrace')
   or
   @Image = concat(../../../../../../../FormalParameter/VariableDeclaratorId/@Image, '.toString')
]
```

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

## AvoidRethrowingException

**Since:** PMD 3.8

**Priority:** Medium (3)

Catch blocks that merely rethrow a caught exception only add to code size and runtime complexity.

```
//CatchStatement[FormalParameter
 /VariableDeclaratorId/@Image = Block/BlockStatement/Statement
 /ThrowStatement/Expression/PrimaryExpression[count(PrimarySuffix)=0]/PrimaryPrefix/Name/@Image
 and count(Block/BlockStatement/Statement) =1]
```

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

## AvoidThrowingNewInstanceOfSameException

**Since:** PMD 4.2.5

**Priority:** Medium (3)

Catch blocks that merely rethrow a caught exception wrapped inside a new instance of the same type only add to
code size and runtime complexity.

```
//CatchStatement[
  count(Block/BlockStatement/Statement) = 1
  and
  FormalParameter/Type/ReferenceType/ClassOrInterfaceType/@Image = Block/BlockStatement/Statement/ThrowStatement/Expression/PrimaryExpression/PrimaryPrefix/AllocationExpression/ClassOrInterfaceType/@Image
  and
  count(Block/BlockStatement/Statement/ThrowStatement/Expression/PrimaryExpression/PrimaryPrefix/AllocationExpression/Arguments/ArgumentList/Expression) = 1
  and
  FormalParameter/VariableDeclaratorId = Block/BlockStatement/Statement/ThrowStatement/Expression/PrimaryExpression/PrimaryPrefix/AllocationExpression/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Name
  ]
```

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

## AvoidThrowingNullPointerException

**Since:** PMD 1.8

**Priority:** High (1)

Avoid throwing NullPointerExceptions. These are confusing because most people will assume that the
virtual machine threw it. Consider using an IllegalArgumentException instead; this will be
clearly seen as a programmer-initiated exception.

```
//AllocationExpression/ClassOrInterfaceType[@Image='NullPointerException']
```

**Example(s):**

```
public class Foo {
  void bar() {
    throw new NullPointerException();
  }
}
```

## AvoidThrowingRawExceptionTypes

**Since:** PMD 1.8

**Priority:** High (1)

Avoid throwing certain exception types. Rather than throw a raw RuntimeException, Throwable,
Exception, or Error, use a subclassed exception or error instead.

```
//ThrowStatement//AllocationExpression
 /ClassOrInterfaceType[
 (@Image='Throwable' and count(//ImportDeclaration/Name[ends-with(@Image,'Throwable')]) = 0)
or
 (@Image='Exception' and count(//ImportDeclaration/Name[ends-with(@Image,'Exception')]) = 0)
or
 (@Image='Error'  and count(//ImportDeclaration/Name[ends-with(@Image,'Error')]) = 0)
or
( @Image='RuntimeException'  and count(//ImportDeclaration/Name[ends-with(@Image,'RuntimeException')]) = 0)
]
```

**Example(s):**

```
public class Foo {
  public void bar() throws Exception {
    throw new Exception();
   }
}
```

## DoNotExtendJavaLangError

**Since:** PMD 4.0

**Priority:** Medium (3)

Errors are system exceptions. Do not extend them.

```
//ClassOrInterfaceDeclaration/ExtendsList/ClassOrInterfaceType
  [@Image="Error" or @Image="java.lang.Error"]
```

**Example(s):**

```
public class Foo extends Error { }
```

## DoNotThrowExceptionInFinally

**Since:** PMD 4.2

**Priority:** Medium Low (4)

Throwing exceptions within a 'finally' block is confusing since they may mask other exceptions 
or code defects.
Note: This is a PMD implementation of the Lint4j rule "A throw in a finally block"

```
//FinallyStatement[descendant::ThrowStatement]
```

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

## ExceptionAsFlowControl

**Since:** PMD 1.8

**Priority:** Medium (3)

Using Exceptions as form of flow control is not recommended as they obscure true exceptions when debugging.
Either add the necessary validation or use an alternate control structure.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.ExceptionAsFlowControlRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/ExceptionAsFlowControlRule.java)

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

## SignatureDeclareThrowsException

**Since:** PMD 1.2

**Priority:** Medium (3)

Methods that declare the generic Exception as a possible throwable are not very helpful since their
failure modes are unclear. Use a class derived from RuntimeException or a more specific checked exception.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.SignatureDeclareThrowsExceptionRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/SignatureDeclareThrowsExceptionRule.java)

**Example(s):**

```
public void foo() throws Exception {
}
```

