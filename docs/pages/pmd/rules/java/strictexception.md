---
title: Strict Exceptions
summary: These rules provide some strict guidelines about throwing and catching exceptions.
permalink: pmd_rules_java_strictexception.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/strictexception.xml
keywords: Strict Exceptions, AvoidCatchingThrowable, SignatureDeclareThrowsException, ExceptionAsFlowControl, AvoidCatchingNPE, AvoidThrowingRawExceptionTypes, AvoidThrowingNullPointerException, AvoidRethrowingException, DoNotExtendJavaLangError, DoNotExtendJavaLangThrowable, DoNotThrowExceptionInFinally, AvoidThrowingNewInstanceOfSameException, AvoidCatchingGenericException, AvoidLosingExceptionInformation
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidCatchingGenericException" />
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

``` java
public class Foo {
    void bar() {
        try {
            // do something
        } catch (NullPointerException npe) {
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidCatchingNPE" />
```

## AvoidCatchingThrowable

**Since:** PMD 1.2

**Priority:** Medium (3)

Catching Throwable errors is not recommended since its scope is very broad. It includes runtime issues such as 
OutOfMemoryError that should be exposed and managed separately.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.AvoidCatchingThrowableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/AvoidCatchingThrowableRule.java)

**Example(s):**

``` java
public void bar() {
    try {
        // do something
    } catch (Throwable th) {  // should not catch Throwable
        th.printStackTrace();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidCatchingThrowable" />
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

``` java
public void bar() {
    try {
        // do something
    } catch (SomeException se) {
        se.getMessage();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidLosingExceptionInformation" />
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

``` java
public void bar() {
    try {
        // do something
    }  catch (SomeException se) {
       throw se;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidRethrowingException" />
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

``` java
public void bar() {
    try {
        // do something
    } catch (SomeException se) {
        // harmless comment
        throw new SomeException(se);
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidThrowingNewInstanceOfSameException" />
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

``` java
public class Foo {
    void bar() {
        throw new NullPointerException();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidThrowingNullPointerException" />
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

``` java
public class Foo {
    public void bar() throws Exception {
        throw new Exception();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/AvoidThrowingRawExceptionTypes" />
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

``` java
public class Foo extends Error { }
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/DoNotExtendJavaLangError" />
```

## DoNotExtendJavaLangThrowable

**Since:** PMD 6.0

**Priority:** Medium (3)

Extend Exception or RuntimeException instead of Throwable.

```
//ClassOrInterfaceDeclaration/ExtendsList/ClassOrInterfaceType
  [@Image="Throwable" or @Image="java.lang.Throwable"]
```

**Example(s):**

``` java
public class Foo extends Throwable { }
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/DoNotExtendJavaLangThrowable" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/DoNotThrowExceptionInFinally" />
```

## ExceptionAsFlowControl

**Since:** PMD 1.8

**Priority:** Medium (3)

Using Exceptions as form of flow control is not recommended as they obscure true exceptions when debugging.
Either add the necessary validation or use an alternate control structure.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.ExceptionAsFlowControlRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/ExceptionAsFlowControlRule.java)

**Example(s):**

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/ExceptionAsFlowControl" />
```

## SignatureDeclareThrowsException

**Since:** PMD 1.2

**Priority:** Medium (3)

A method/constructor shouldn't explicitly throw the generic java.lang.Exception, since it
is unclear which exceptions that can be thrown from the methods. It might be
difficult to document and understand such vague interfaces. Use either a class
derived from RuntimeException or a checked exception.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strictexception.SignatureDeclareThrowsExceptionRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strictexception/SignatureDeclareThrowsExceptionRule.java)

**Example(s):**

``` java
public void foo() throws Exception {
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|IgnoreJUnitCompletely|false|Allow all methods in a JUnit testcase to throw Exceptions|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/strictexception.xml/SignatureDeclareThrowsException" />
```

