---
title: Design
summary: The Design category contains rules that flag suboptimal code implementations. Alternate approaches are suggested.  It fully contains these previous rulesets:  *   codesize
permalink: pmd_rules_java_design.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/design.xml
keywords: Design, AbstractClassWithoutAnyMethod, AvoidCatchingGenericException, AvoidDeeplyNestedIfStmts, AvoidRethrowingException, AvoidThrowingNewInstanceOfSameException, AvoidThrowingNullPointerException, AvoidThrowingRawExceptionTypes, ClassWithOnlyPrivateConstructorsShouldBeFinal, CollapsibleIfStatements, CouplingBetweenObjects, CyclomaticComplexity, DataClass, ExceptionAsFlowControl, ExcessiveClassLength, ExcessiveImports, ExcessiveMethodLength, ExcessiveParameterList, ExcessivePublicCount, FinalFieldCouldBeStatic, GodClass, ImmutableField, LawOfDemeter, LogicInversion, LoosePackageCoupling, ModifiedCyclomaticComplexity, NcssConstructorCount, NcssCount, NcssMethodCount, NcssTypeCount, NPathComplexity, SignatureDeclareThrowsException, SimplifiedTernary, SimplifyBooleanAssertion, SimplifyBooleanExpressions, SimplifyBooleanReturns, SimplifyConditional, SingularField, StdCyclomaticComplexity, SwitchDensity, TooFewBranchesForASwitchStatement, TooManyFields, TooManyMethods, UselessOverridingMethod, UseObjectForClearerAPI, UseUtilityClass, AbstractClassWithoutAbstractMethod, AccessorClassGeneration, AccessorMethodGeneration, AssignmentToNonFinalStatic, AvoidInstanceofChecksInCatchClause, AvoidProtectedFieldInFinalClass, AvoidProtectedMethodInFinalClassNotExtending, BadComparison, CloseResource, CompareObjectsWithEquals, ConfusingTernary, ConstantsInInterface, ConstructorCallsOverridableMethod, DefaultLabelNotLastInSwitchStmt, EqualsNull, IdempotentOperations, InstantiationToGetClass, MissingBreakInSwitch, MissingStaticMethodInNonInstantiatableClass, NonCaseLabelInSwitchStatement, NonStaticInitializer, PreserveStackTrace, ReturnEmptyArrayRatherThanNull, SimpleDateFormatNeedsLocale, SingleMethodSingleton, SingletonClassReturningNewInstance, UseCollectionIsEmpty, UseLocaleWithCaseConversions, EmptyMethodInAbstractClassShouldBeAbstract, FieldDeclarationsShouldBeAtStartOfClass, UnnecessaryLocalBeforeReturn, AvoidSynchronizedAtMethodLevel, NonThreadSafeSingleton, UnsynchronizedStaticDateFormatter, UseNotifyAllInsteadOfNotify, AvoidReassigningParameters, PositionLiteralsFirstInCaseInsensitiveComparisons, PositionLiteralsFirstInComparisons, SwitchStmtsShouldHaveDefault, UseVarargs, OptimizableToArrayCall, UncommentedEmptyConstructor, UncommentedEmptyMethodBody
---
## AbstractClassWithoutAbstractMethod

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AbstractClassWithoutAbstractMethod](pmd_rules_java_errorprone.html#abstractclasswithoutabstractmethod)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

The abstract class does not contain any abstract methods. An abstract class suggests
an incomplete implementation, which is to be completed by subclasses implementing the
abstract methods. If the class is intended to be used as a base class only (not to be instantiated
directly) a protected constructor can be provided prevent direct instantiation.

```
//ClassOrInterfaceDeclaration
 [@Abstract='true'
  and count( .//MethodDeclaration[@Abstract='true'] )=0 ]
  [count(ImplementsList)=0]
  [count(.//ExtendsList)=0]
```

**Example(s):**

``` java
public abstract class Foo {
  void int method1() { ... }
  void int method2() { ... }
  // consider using abstract methods or removing
  // the abstract modifier and adding protected constructors
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AbstractClassWithoutAbstractMethod" />
```

## AbstractClassWithoutAnyMethod

**Since:** PMD 4.2

**Priority:** High (1)

If an abstract class does not provides any methods, it may be acting as a simple data container
that is not meant to be instantiated. In this case, it is probably better to use a private or
protected constructor in order to prevent instantiation than make the class misleadingly abstract.

```
//ClassOrInterfaceDeclaration
    [@Abstract = 'true']
    [count(//MethodDeclaration) + count(//ConstructorDeclaration) = 0]
    [not(../Annotation/MarkerAnnotation/Name[typeof(@Image, 'com.google.auto.value.AutoValue', 'AutoValue')])]
```

**Example(s):**

``` java
public class abstract Example {
    String field;
    int otherField;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AbstractClassWithoutAnyMethod" />
```

## AccessorClassGeneration

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AccessorClassGeneration](pmd_rules_java_errorprone.html#accessorclassgeneration)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.04

**Priority:** Medium (3)

Instantiation by way of private constructors from outside of the constructor's class often causes the
generation of an accessor. A factory method, or non-privatization of the constructor can eliminate this
situation. The generated class file is actually an interface.  It gives the accessing class the ability
to invoke a new hidden package scope constructor that takes the interface as a supplementary parameter.
This turns a private constructor effectively into one with package scope, and is challenging to discern.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AccessorClassGenerationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AccessorClassGenerationRule.java)

**Example(s):**

``` java
public class Outer {
 void method(){
  Inner ic = new Inner();//Causes generation of accessor class
 }
 public class Inner {
  private Inner(){}
 }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AccessorClassGeneration" />
```

## AccessorMethodGeneration

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AccessorMethodGeneration](pmd_rules_java_errorprone.html#accessormethodgeneration)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.5.4

**Priority:** Medium (3)

When accessing a private field / method from another class, the Java compiler will generate a accessor methods
with package-private visibility. This adds overhead, and to the dex method count on Android. This situation can
be avoided by changing the visibility of the field / method from private to package-private.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AccessorMethodGenerationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AccessorMethodGenerationRule.java)

**Example(s):**

``` java
public class OuterClass {
    private int counter;
    /* package */ int id;

    public class InnerClass {
        InnerClass() {
            OuterClass.this.counter++; // wrong accessor method will be generated
        }

        public int getOuterClassId() {
            return OuterClass.this.id; // id is package-private, no accessor method needed
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AccessorMethodGeneration" />
```

## AssignmentToNonFinalStatic

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AssignmentToNonFinalStatic](pmd_rules_java_errorprone.html#assignmenttononfinalstatic)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.2

**Priority:** Medium (3)

Identifies a possible unsafe usage of a static field.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AssignmentToNonFinalStaticRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AssignmentToNonFinalStaticRule.java)

**Example(s):**

``` java
public class StaticField {
   static int x;
   public FinalFields(int y) {
    x = y; // unsafe
   }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AssignmentToNonFinalStatic" />
```

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
<rule ref="rulesets/java/design.xml/AvoidCatchingGenericException" />
```

## AvoidDeeplyNestedIfStmts

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid creating deeply nested if-then statements since they are harder to read and error-prone to maintain.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AvoidDeeplyNestedIfStmtsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AvoidDeeplyNestedIfStmtsRule.java)

**Example(s):**

``` java
public class Foo {
  public void bar(int x, int y, int z) {
    if (x>y) {
      if (y>z) {
        if (z==x) {
         // !! too deep
        }
      }
    }
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|problemDepth|3|The if statement depth reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidDeeplyNestedIfStmts" />
```

## AvoidInstanceofChecksInCatchClause

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AvoidInstanceofChecksInCatchClause](pmd_rules_java_errorprone.html#avoidinstanceofchecksincatchclause)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

Each caught exception type should be handled in its own catch clause.

```
//CatchStatement/FormalParameter
 /following-sibling::Block//InstanceOfExpression/PrimaryExpression/PrimaryPrefix
  /Name[
   @Image = ./ancestor::Block/preceding-sibling::FormalParameter
    /VariableDeclaratorId/@Image
  ]
```

**Example(s):**

``` java
try { // Avoid this
    // do something
} catch (Exception ee) {
    if (ee instanceof IOException) {
        cleanup();
    }
}

try {  // Prefer this:
    // do something
} catch (IOException ee) {
    cleanup();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidInstanceofChecksInCatchClause" />
```

## AvoidProtectedFieldInFinalClass

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AvoidProtectedFieldInFinalClass](pmd_rules_java_errorprone.html#avoidprotectedfieldinfinalclass)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.1

**Priority:** Medium (3)

Do not use protected fields in final classes since they cannot be subclassed.
Clarify your intent by using private or package access modifiers instead.

```
//ClassOrInterfaceDeclaration[@Final='true']
/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
/FieldDeclaration[@Protected='true']
```

**Example(s):**

``` java
public final class Bar {
  private int x;
  protected int y;  // bar cannot be subclassed, so is y really private or package visible?
  Bar() {}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidProtectedFieldInFinalClass" />
```

## AvoidProtectedMethodInFinalClassNotExtending

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AvoidProtectedMethodInFinalClassNotExtending](pmd_rules_java_errorprone.html#avoidprotectedmethodinfinalclassnotextending)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.1

**Priority:** Medium (3)

Do not use protected methods in most final classes since they cannot be subclassed. This should
only be allowed in final classes that extend other classes with protected methods (whose
visibility cannot be reduced). Clarify your intent by using private or package access modifiers instead.

```
//ClassOrInterfaceDeclaration[@Final='true' and not(ExtendsList)]
/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
/MethodDeclaration[@Protected='true'][MethodDeclarator/@Image != 'finalize']
```

**Example(s):**

``` java
public final class Foo {
  private int bar() {}
  protected int baz() {} // Foo cannot be subclassed, and doesn't extend anything, so is baz() really private or package visible?
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidProtectedMethodInFinalClassNotExtending" />
```

## AvoidReassigningParameters

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AvoidReassigningParameters](pmd_rules_java_bestpractices.html#avoidreassigningparameters)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.0

**Priority:** Medium High (2)

Reassigning values to incoming parameters is not recommended.  Use temporary local variables instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AvoidReassigningParametersRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AvoidReassigningParametersRule.java)

**Example(s):**

``` java
public class Foo {
  private void foo(String bar) {
    bar = "something else";
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidReassigningParameters" />
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
<rule ref="rulesets/java/design.xml/AvoidRethrowingException" />
```

## AvoidSynchronizedAtMethodLevel

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [AvoidSynchronizedAtMethodLevel](pmd_rules_java_multithreading.html#avoidsynchronizedatmethodlevel)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

Method-level synchronization can cause problems when new code is added to the method.
Block-level synchronization helps to ensure that only the code that needs synchronization
gets it.

```
//MethodDeclaration[@Synchronized='true']
```

**Example(s):**

``` java
public class Foo {
  // Try to avoid this:
  synchronized void foo() {
  }
  // Prefer this:
  void bar() {
    synchronized(this) {
    }
  }

  // Try to avoid this for static methods:
  static synchronized void fooStatic() {
  }

  // Prefer this:
  static void barStatic() {
    synchronized(Foo.class) {
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/AvoidSynchronizedAtMethodLevel" />
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
<rule ref="rulesets/java/design.xml/AvoidThrowingNewInstanceOfSameException" />
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
<rule ref="rulesets/java/design.xml/AvoidThrowingNullPointerException" />
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
<rule ref="rulesets/java/design.xml/AvoidThrowingRawExceptionTypes" />
```

## BadComparison

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [BadComparison](pmd_rules_java_errorprone.html#badcomparison)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.8

**Priority:** Medium (3)

Avoid equality comparisons with Double.NaN. Due to the implicit lack of representation
precision when comparing floating point numbers these are likely to cause logic errors.

```
//EqualityExpression[@Image='==']
 /PrimaryExpression/PrimaryPrefix
 /Name[@Image='Double.NaN' or @Image='Float.NaN']
```

**Example(s):**

``` java
boolean x = (y == Double.NaN);
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/BadComparison" />
```

## ClassWithOnlyPrivateConstructorsShouldBeFinal

**Since:** PMD 4.1

**Priority:** High (1)

A class with only private constructors should be final, unless the private constructor
is invoked by a inner class.

```
TypeDeclaration[count(../TypeDeclaration) = 1]/ClassOrInterfaceDeclaration
[@Final = 'false']
[count(./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration[@Private = 'true']) >= 1 ]
[count(./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration[(@Public = 'true') or (@Protected = 'true') or (@PackagePrivate = 'true')]) = 0 ]
[not(.//ClassOrInterfaceDeclaration)]
```

**Example(s):**

``` java
public class Foo {  //Should be final
    private Foo() { }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ClassWithOnlyPrivateConstructorsShouldBeFinal" />
```

## CloseResource

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [CloseResource](pmd_rules_java_errorprone.html#closeresource)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.2.2

**Priority:** Medium (3)

Ensure that resources (like Connection, Statement, and ResultSet objects) are always closed after use.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.CloseResourceRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/CloseResourceRule.java)

**Example(s):**

``` java
public class Bar {
  public void foo() {
    Connection c = pool.getConnection();
    try {
      // do stuff
    } catch (SQLException ex) {
     // handle exception
    } finally {
      // oops, should close the connection using 'close'!
      // c.close();
    }
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|closeAsDefaultTarget|true|Consider 'close' as a target by default|
|types|[java.sql.Connection, java.sql.Statement, java.sql.ResultSet]|Affected types|
|closeTargets|[]|Methods which may close this resource|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/CloseResource" />
```

## CollapsibleIfStatements

**Since:** PMD 3.1

**Priority:** Medium (3)

Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.

```
//IfStatement[@Else='false']/Statement
 /IfStatement[@Else='false']
 |
//IfStatement[@Else='false']/Statement
 /Block[count(BlockStatement)=1]/BlockStatement
  /Statement/IfStatement[@Else='false']
```

**Example(s):**

``` java
void bar() {
    if (x) {            // original implementation
        if (y) {
            // do stuff
        }
    }
}

void bar() {
    if (x && y) {        // optimized implementation
        // do stuff
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/CollapsibleIfStatements" />
```

## CompareObjectsWithEquals

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [CompareObjectsWithEquals](pmd_rules_java_errorprone.html#compareobjectswithequals)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.2

**Priority:** Medium (3)

Use equals() to compare object references; avoid comparing them with ==.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.CompareObjectsWithEqualsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/CompareObjectsWithEqualsRule.java)

**Example(s):**

``` java
class Foo {
  boolean bar(String a, String b) {
    return a == b;
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/CompareObjectsWithEquals" />
```

## ConfusingTernary

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [ConfusingTernary](pmd_rules_java_errorprone.html#confusingternary)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.9

**Priority:** Medium (3)

Avoid negation within an "if" expression with an "else" clause.  For example, rephrase:
`if (x != y) diff(); else same();` as: `if (x == y) same(); else diff();`.

Most "if (x != y)" cases without an "else" are often return cases, so consistent use of this
rule makes the code easier to read.  Also, this resolves trivial ordering problems, such
as "does the error case go first?" or "does the common case go first?".

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.ConfusingTernaryRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/ConfusingTernaryRule.java)

**Example(s):**

``` java
boolean bar(int x, int y) {
    return (x != y) ? diff : same;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreElseIf|false|Ignore conditions with an else-if case|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ConfusingTernary" />
```

## ConstantsInInterface

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [ConstantsInInterface](pmd_rules_java_errorprone.html#constantsininterface)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.5

**Priority:** Medium (3)

Avoid constants in interfaces. Interfaces should define types, constants are implementation details
better placed in classes or enums. See Effective Java, item 19.

```
//ClassOrInterfaceDeclaration[@Interface='true'][$ignoreIfHasMethods='false' or not(.//MethodDeclaration)]//FieldDeclaration
```

**Example(s):**

``` java
public interface ConstantInterface {
    public static final int CONST1 = 1; // violation, no fields allowed in interface!
    static final int CONST2 = 1;        // violation, no fields allowed in interface!
    final int CONST3 = 1;               // violation, no fields allowed in interface!
    int CONST4 = 1;                     // violation, no fields allowed in interface!
}

// with ignoreIfHasMethods = false
public interface AnotherConstantInterface {
    public static final int CONST1 = 1; // violation, no fields allowed in interface!

    int anyMethod();
}

// with ignoreIfHasMethods = true
public interface YetAnotherConstantInterface {
    public static final int CONST1 = 1; // no violation

    int anyMethod();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreIfHasMethods|true|Whether to ignore constants in interfaces if the interface defines any methods|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ConstantsInInterface" />
```

## ConstructorCallsOverridableMethod

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [ConstructorCallsOverridableMethod](pmd_rules_java_errorprone.html#constructorcallsoverridablemethod)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.04

**Priority:** High (1)

Calling overridable methods during construction poses a risk of invoking methods on an incompletely
constructed object and can be difficult to debug.
It may leave the sub-class unable to construct its superclass or forced to replicate the construction
process completely within itself, losing the ability to call super().  If the default constructor
contains a call to an overridable method, the subclass may be completely uninstantiable.   Note that
this includes method calls throughout the control flow graph - i.e., if a constructor Foo() calls a
private method bar() that calls a public method buz(), this denotes a problem.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.ConstructorCallsOverridableMethodRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/ConstructorCallsOverridableMethodRule.java)

**Example(s):**

``` java
public class SeniorClass {
  public SeniorClass(){
      toString(); //may throw NullPointerException if overridden
  }
  public String toString(){
    return "IAmSeniorClass";
  }
}
public class JuniorClass extends SeniorClass {
  private String name;
  public JuniorClass(){
    super(); //Automatic call leads to NullPointerException
    name = "JuniorClass";
  }
  public String toString(){
    return name.toUpperCase();
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ConstructorCallsOverridableMethod" />
```

## CouplingBetweenObjects

**Since:** PMD 1.04

**Priority:** Medium (3)

This rule counts the number of unique attributes, local variables, and return types within an object. 
A number higher than the specified threshold can indicate a high degree of coupling.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.coupling.CouplingBetweenObjectsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/coupling/CouplingBetweenObjectsRule.java)

**Example(s):**

``` java
import com.Blah;
import org.Bar;
import org.Bardo;

public class Foo {
    private Blah var1;
    private Bar var2;

    //followed by many imports of unique objects
    void ObjectC doWork() {
        Bardo var55;
        ObjectA var44;
        ObjectZ var93;
        return something;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|threshold|20|Unique type reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/CouplingBetweenObjects" />
```

## CyclomaticComplexity

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.03

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.CyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/CyclomaticComplexityRule.java)

**Example(s):**

``` java
public class Foo {      // This has a Cyclomatic Complexity = 12
1   public void example()  {
2       if (a == b)  {
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
            switch (z) {
9               case 1:
                    fiddle();
                    break;
10              case 2:
                    fiddle();
                    break;
11              case 3:
                    fiddle();
                    break;
12              default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/CyclomaticComplexity" />
```

## DataClass

**Since:** PMD 6.0

**Priority:** Medium (3)

Data Classes are simple data holders, which reveal most of their state, and
without complex functionality. The lack of functionality may indicate that
their behaviour is defined elsewhere, which is a sign of poor data-behaviour
proximity. By directly exposing their internals, Data Classes break encapsulation,
and therefore reduce the system's maintainability and understandability. Moreover,
classes tend to strongly rely on their data representation, which makes for a brittle
design.

Refactoring a Data Class should focus on restoring a good data-behaviour proximity. In
most cases, that means moving the operations defined on the data back into the class.
In some other cases it may make sense to remove entirely the class and move the data
into the former client classes.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.metrics.rule.DataClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/metrics/rule/DataClassRule.java)

**Example(s):**

``` java
public class DataClass {

  public int bar = 0;
  public int na = 0;
  private int bee = 0;

  public void setBee(int n) {
    bee = n;
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/DataClass" />
```

## DefaultLabelNotLastInSwitchStmt

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [DefaultLabelNotLastInSwitchStmt](pmd_rules_java_errorprone.html#defaultlabelnotlastinswitchstmt)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.5

**Priority:** Medium (3)

By convention, the default label should be the last label in a switch statement.

```
//SwitchStatement
 [not(SwitchLabel[position() = last()][@Default='true'])]
 [SwitchLabel[@Default='true']]
```

**Example(s):**

``` java
public class Foo {
  void bar(int a) {
   switch (a) {
    case 1:  // do something
       break;
    default:  // the default case should be last, by convention
       break;
    case 2:
       break;
   }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/DefaultLabelNotLastInSwitchStmt" />
```

## EmptyMethodInAbstractClassShouldBeAbstract

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [EmptyMethodInAbstractClassShouldBeAbstract](pmd_rules_java_codestyle.html#emptymethodinabstractclassshouldbeabstract)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 4.1

**Priority:** High (1)

Empty or auto-generated methods in an abstract class should be tagged as abstract. This helps to remove their inapproprate
usage by developers who should be implementing their own versions in the concrete subclasses.

```
//ClassOrInterfaceDeclaration[@Abstract = 'true']
    /ClassOrInterfaceBody
    /ClassOrInterfaceBodyDeclaration
    /MethodDeclaration[@Abstract = 'false' and @Native = 'false']
    [
        ( boolean(./Block[count(./BlockStatement) =  1]/BlockStatement/Statement/ReturnStatement/Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral) = 'true' )
        or
        ( boolean(./Block[count(./BlockStatement) =  1]/BlockStatement/Statement/ReturnStatement/Expression/PrimaryExpression/PrimaryPrefix/Literal[@Image = '0']) = 'true' )
        or
        ( boolean(./Block[count(./BlockStatement) =  1]/BlockStatement/Statement/ReturnStatement/Expression/PrimaryExpression/PrimaryPrefix/Literal[string-length(@Image) = 2]) = 'true' )
        or
        (./Block[count(./BlockStatement) =  1]/BlockStatement/Statement/EmptyStatement)
        or
        ( count (./Block/*) = 0 )
    ]
```

**Example(s):**

``` java
public abstract class ShouldBeAbstract {
    public Object couldBeAbstract() {
        // Should be abstract method ?
        return null;
    }

    public void couldBeAbstract() {
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/EmptyMethodInAbstractClassShouldBeAbstract" />
```

## EqualsNull

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [EqualsNull](pmd_rules_java_errorprone.html#equalsnull)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.9

**Priority:** High (1)

Tests for null should not use the equals() method. The '==' operator should be used instead.

```
//PrimaryExpression
  [
    PrimaryPrefix[Name[ends-with(@Image, 'equals')]]
      [following-sibling::node()/Arguments/ArgumentList[count(Expression)=1]
          /Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]

    or

    PrimarySuffix[ends-with(@Image, 'equals')]
      [following-sibling::node()/Arguments/ArgumentList[count(Expression)=1]
          /Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]

  ]
```

**Example(s):**

``` java
String x = "foo";

if (x.equals(null)) {   // bad form
    doSomething();
}

if (x == null) {        // preferred
    doSomething();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/EqualsNull" />
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
<rule ref="rulesets/java/design.xml/ExceptionAsFlowControl" />
```

## ExcessiveClassLength

**Since:** PMD 0.6

**Priority:** Medium (3)

Excessive class file lengths are usually indications that the class may be burdened with excessive 
responsibilities that could be provided by external classes or functions. In breaking these methods
apart the code becomes more manageable and ripe for reuse.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.ExcessiveClassLengthRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/ExcessiveClassLengthRule.java)

**Example(s):**

``` java
public class Foo {
    public void bar1() {
        // 1000 lines of code
    }
    public void bar2() {
        // 1000 lines of code
    }
    public void bar3() {
        // 1000 lines of code
    }

    public void barN() {
        // 1000 lines of code
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ExcessiveClassLength" />
```

## ExcessiveImports

**Since:** PMD 1.04

**Priority:** Medium (3)

A high number of imports can indicate a high degree of coupling within an object. This rule 
counts the number of unique imports and reports a violation if the count is above the 
user-specified threshold.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.coupling.ExcessiveImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/coupling/ExcessiveImportsRule.java)

**Example(s):**

``` java
import blah.blah.Baz;
import blah.blah.Bif;
// 18 others from the same package elided
public class Foo {
    public void doWork() {}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ExcessiveImports" />
```

## ExcessiveMethodLength

**Since:** PMD 0.6

**Priority:** Medium (3)

When methods are excessively long this usually indicates that the method is doing more than its
name/signature might suggest. They also become challenging for others to digest since excessive 
scrolling causes readers to lose focus.
Try to reduce the method length by creating helper methods and removing any copy/pasted code.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.ExcessiveMethodLengthRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/ExcessiveMethodLengthRule.java)

**Example(s):**

``` java
public void doSomething() {
    System.out.println("Hello world!");
    System.out.println("Hello world!");
    // 98 copies omitted for brevity.
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ExcessiveMethodLength" />
```

## ExcessiveParameterList

**Since:** PMD 0.9

**Priority:** Medium (3)

Methods with numerous parameters are a challenge to maintain, especially if most of them share the
same datatype. These situations usually denote the need for new objects to wrap the numerous parameters.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.ExcessiveParameterListRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/ExcessiveParameterListRule.java)

**Example(s):**

``` java
public void addPerson(      // too many arguments liable to be mixed up
    int birthYear, int birthMonth, int birthDate, int height, int weight, int ssn) {

    . . . .
}
 
public void addPerson(      // preferred approach
    Date birthdate, BodyMeasurements measurements, int ssn) {

    . . . .
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ExcessiveParameterList" />
```

## ExcessivePublicCount

**Since:** PMD 1.04

**Priority:** Medium (3)

Classes with large numbers of public methods and attributes require disproportionate testing efforts
since combinational side effects grow rapidly and increase risk. Refactoring these classes into
smaller ones not only increases testability and reliability but also allows new variations to be
developed easily.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.ExcessivePublicCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/ExcessivePublicCountRule.java)

**Example(s):**

``` java
public class Foo {
    public String value;
    public Bar something;
    public Variable var;
    // [... more more public attributes ...]

    public void doWork() {}
    public void doMoreWork() {}
    public void doWorkAgain() {}
    // [... more more public methods ...]
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ExcessivePublicCount" />
```

## FieldDeclarationsShouldBeAtStartOfClass

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [FieldDeclarationsShouldBeAtStartOfClass](pmd_rules_java_codestyle.html#fielddeclarationsshouldbeatstartofclass)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.0

**Priority:** Medium (3)

Fields should be declared at the top of the class, before any method declarations, constructors, initializers or inner classes.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.FieldDeclarationsShouldBeAtStartOfClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/FieldDeclarationsShouldBeAtStartOfClassRule.java)

**Example(s):**

``` java
public class HelloWorldBean {

  // Field declared before methods / inner classes - OK
  private String _thing;

  public String getMessage() {
    return "Hello World!";
  }

  // Field declared after methods / inner classes - avoid this
  private String _fieldInWrongLocation;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreInterfaceDeclarations|false|Ignore Interface Declarations that precede fields.|
|ignoreAnonymousClassDeclarations|true|Ignore Field Declarations, that are initialized with anonymous class declarations|
|ignoreEnumDeclarations|true|Ignore Enum Declarations that precede fields.|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/FieldDeclarationsShouldBeAtStartOfClass" />
```

## FinalFieldCouldBeStatic

**Since:** PMD 1.1

**Priority:** Medium (3)

If a final field is assigned to a compile-time constant, it could be made static, thus saving overhead
in each object at runtime.

```
//FieldDeclaration
 [@Final='true' and @Static='false']
   /VariableDeclarator/VariableInitializer/Expression
    /PrimaryExpression[not(PrimarySuffix)]/PrimaryPrefix/Literal
```

**Example(s):**

``` java
public class Foo {
  public final int BAR = 42; // this could be static and save some space
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/FinalFieldCouldBeStatic" />
```

## GodClass

**Since:** PMD 5.0

**Priority:** Medium (3)

The God Class rule detects the God Class design flaw using metrics. God classes do too many things,
are very big and overly complex. They should be split apart to be more object-oriented.
The rule uses the detection strategy described in "Object-Oriented Metrics in Practice".
The violations are reported against the entire class.

See also the references:

Michele Lanza and Radu Marinescu. Object-Oriented Metrics in Practice:
Using Software Metrics to Characterize, Evaluate, and Improve the Design
of Object-Oriented Systems. Springer, Berlin, 1 edition, October 2006. Page 80.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.GodClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/GodClassRule.java)

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/GodClass" />
```

## IdempotentOperations

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [IdempotentOperations](pmd_rules_java_errorprone.html#idempotentoperations)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.0

**Priority:** Medium (3)

Avoid idempotent operations - they have no effect.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.IdempotentOperationsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/IdempotentOperationsRule.java)

**Example(s):**

``` java
public class Foo {
 public void bar() {
  int x = 2;
  x = x;
 }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/IdempotentOperations" />
```

## ImmutableField

**Since:** PMD 2.0

**Priority:** Medium (3)

Identifies private fields whose values never change once they are initialized either in the declaration
of the field or by a constructor.  This helps in converting existing classes to becoming immutable ones.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.ImmutableFieldRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/ImmutableFieldRule.java)

**Example(s):**

``` java
public class Foo {
  private int x; // could be final
  public Foo() {
      x = 7;
  }
  public void foo() {
     int a = x + 2;
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ImmutableField" />
```

## InstantiationToGetClass

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [InstantiationToGetClass](pmd_rules_java_errorprone.html#instantiationtogetclass)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.0

**Priority:** Medium Low (4)

Avoid instantiating an object just to call getClass() on it; use the .class public member instead.

```
//PrimarySuffix
 [@Image='getClass']
 [parent::PrimaryExpression
  [PrimaryPrefix/AllocationExpression]
  [count(PrimarySuffix) = 2]
 ]
```

**Example(s):**

``` java
// replace this
Class c = new String().getClass();

// with this:
Class c = String.class;
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/InstantiationToGetClass" />
```

## LawOfDemeter

**Since:** PMD 5.0

**Priority:** Medium (3)

The Law of Demeter is a simple rule, that says "only talk to friends". It helps to reduce coupling between classes
or objects.

See also the references:

*   Andrew Hunt, David Thomas, and Ward Cunningham. The Pragmatic Programmer. From Journeyman to Master. Addison-Wesley Longman, Amsterdam, October 1999.;
*   K.J. Lieberherr and I.M. Holland. Assuring good style for object-oriented programs. Software, IEEE, 6(5):38–48, 1989.;
*   <http://www.ccs.neu.edu/home/lieber/LoD.html>
*   <http://en.wikipedia.org/wiki/Law_of_Demeter>

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.coupling.LawOfDemeterRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/coupling/LawOfDemeterRule.java)

**Example(s):**

``` java
public class Foo {
    /**
     * This example will result in two violations.
     */
    public void example(Bar b) {
        // this method call is ok, as b is a parameter of "example"
        C c = b.getC();

        // this method call is a violation, as we are using c, which we got from B.
        // We should ask b directly instead, e.g. "b.doItOnC();"
        c.doIt();

        // this is also a violation, just expressed differently as a method chain without temporary variables.
        b.getC().doIt();

        // a constructor call, not a method call.
        D d = new D();
        // this method call is ok, because we have create the new instance of D locally.
        d.doSomethingElse(); 
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/LawOfDemeter" />
```

## LogicInversion

**Since:** PMD 5.0

**Priority:** Medium (3)

Use opposite operator instead of negating the whole expression with a logic complement operator.

```
//UnaryExpressionNotPlusMinus[@Image='!']/PrimaryExpression/PrimaryPrefix/Expression[EqualityExpression or RelationalExpression]
```

**Example(s):**

``` java
public boolean bar(int a, int b) {

    if (!(a == b)) { // use !=
         return false;
     }

    if (!(a < b)) { // use >=
         return false;
    }

    return true;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/LogicInversion" />
```

## LoosePackageCoupling

**Since:** PMD 5.0

**Priority:** Medium (3)

Avoid using classes from the configured package hierarchy outside of the package hierarchy, 
except when using one of the configured allowed classes.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.coupling.LoosePackageCouplingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/coupling/LoosePackageCouplingRule.java)

**Example(s):**

``` java
package some.package;

import some.other.package.subpackage.subsubpackage.DontUseThisClass;

public class Bar {
    DontUseThisClass boo = new DontUseThisClass();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|classes|[]|Allowed classes|
|packages|[]|Restricted packages|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/LoosePackageCoupling" />
```

## MissingBreakInSwitch

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [MissingBreakInSwitch](pmd_rules_java_errorprone.html#missingbreakinswitch)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

Switch statements without break or return statements for each case option
may indicate problematic behaviour. Empty cases are ignored as these indicate an intentional fall-through.

```
//SwitchStatement
[(count(.//BreakStatement)
 + count(BlockStatement//Statement/ReturnStatement)
 + count(BlockStatement//Statement/ContinueStatement)
 + count(BlockStatement//Statement/ThrowStatement)
 + count(BlockStatement//Statement/IfStatement[@Else='true' and Statement[2][ReturnStatement|ContinueStatement|ThrowStatement]]/Statement[1][ReturnStatement|ContinueStatement|ThrowStatement])
 + count(SwitchLabel[name(following-sibling::node()) = 'SwitchLabel'])
 + count(SwitchLabel[count(following-sibling::node()) = 0])
  < count (SwitchLabel))]
```

**Example(s):**

``` java
public void bar(int status) {
    switch(status) {
      case CANCELLED:
        doCancelled();
        // break; hm, should this be commented out?
      case NEW:
        doNew();
        // is this really a fall-through?
      case REMOVED:
        doRemoved();
        // what happens if you add another case after this one?
      case OTHER: // empty case - this is interpreted as an intentional fall-through
      case ERROR:
        doErrorHandling();
        break;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/MissingBreakInSwitch" />
```

## MissingStaticMethodInNonInstantiatableClass

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [MissingStaticMethodInNonInstantiatableClass](pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

A class that has private constructors and does not have any static methods or fields cannot be used.

```
//ClassOrInterfaceDeclaration[@Nested='false']
[
  (
    ./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration
    and
    count(./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration) = count(./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration[@Private='true'])
  )
  and
  not(.//MethodDeclaration[@Static='true'])
  and
  not(.//FieldDeclaration[@Private='false'][@Static='true'])
  and
  not(.//ClassOrInterfaceDeclaration[@Nested='true']
           [@Public='true']
           [@Static='true']
           [not(./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration) or ./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration[@Public='true']]
           [./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/MethodDeclaration
                [@Public='true']
                [./ResultType/Type/ReferenceType/ClassOrInterfaceType
                    [@Image = //ClassOrInterfaceDeclaration[@Nested='false']/@Image]
                ]
            ]
        )
]
```

**Example(s):**

``` java
// This class is unusable, since it cannot be
// instantiated (private constructor),
// and no static method can be called.

public class Foo {
  private Foo() {}
  void foo() {}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/MissingStaticMethodInNonInstantiatableClass" />
```

## ModifiedCyclomaticComplexity

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.1.2

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity. Modified complexity treats switch statements as a single
decision point.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.ModifiedCyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/ModifiedCyclomaticComplexityRule.java)

**Example(s):**

``` java
public class Foo {    // This has a Cyclomatic Complexity = 9
1   public void example()  {
2       if (a == b)  {
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
9           switch (z) {
                case 1:
                    fiddle();
                    break;
                case 2:
                    fiddle();
                    break;
                case 3:
                    fiddle();
                    break;
                default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ModifiedCyclomaticComplexity" />
```

## NcssConstructorCount

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given constructor. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.NcssConstructorCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/NcssConstructorCountRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    public Foo() {
        super();





        //this constructor only has 1 NCSS lines
        super.foo();
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NcssConstructorCount" />
```

## NcssCount

**Since:** PMD 6.0

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) metric to determine the number of lines
of code in a class, method or constructor. NCSS ignores comments, blank lines, and only counts actual
statements. For more details on the calculation, see the documentation of
the [NCSS metric](/pmd_java_metrics_index.html#non-commenting-source-statements-ncss).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.NcssCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/NcssCountRule.java)

**Example(s):**

``` java
import java.util.Collections;       // +0
import java.io.IOException;         // +0

class Foo {                         // +1, total Ncss = 12

  public void bigMethod()           // +1
      throws IOException {
    int x = 0, y = 2;               // +1
    boolean a = false, b = true;    // +1

    if (a || b) {                   // +1
      try {                         // +1
        do {                        // +1
          x += 2;                   // +1
        } while (x < 12);

        System.exit(0);             // +1
      } catch (IOException ioe) {   // +1
        throw new PatheticFailException(ioe); // +1
      }
    } else {
      assert false;                 // +1
    }
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ncssOptions|[]|Choose options for the calculation of Ncss|
|methodReportLevel|12|Metric reporting threshold for methods|
|classReportLevel|250|Metric reporting threshold for classes|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NcssCount" />
```

## NcssMethodCount

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given method. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.NcssMethodCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/NcssMethodCountRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    public int methd() {
        super.methd();






        //this method only has 1 NCSS lines
        return 1;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NcssMethodCount" />
```

## NcssTypeCount

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given type. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.NcssTypeCountRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/NcssTypeCountRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    public Foo() {
        //this class only has 6 NCSS lines
        super();





        super.foo();
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NcssTypeCount" />
```

## NonCaseLabelInSwitchStatement

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [NonCaseLabelInSwitchStatement](pmd_rules_java_errorprone.html#noncaselabelinswitchstatement)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.5

**Priority:** Medium (3)

A non-case label (e.g. a named break/continue label) was present in a switch statement.
This legal, but confusing. It is easy to mix up the case labels and the non-case labels.

```
//SwitchStatement//BlockStatement/Statement/LabeledStatement
```

**Example(s):**

``` java
public class Foo {
  void bar(int a) {
   switch (a) {
     case 1:
       // do something
       break;
     mylabel: // this is legal, but confusing!
       break;
     default:
       break;
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NonCaseLabelInSwitchStatement" />
```

## NonStaticInitializer

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [NonStaticInitializer](pmd_rules_java_errorprone.html#nonstaticinitializer)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.5

**Priority:** Medium (3)

A non-static initializer block will be called any time a constructor is invoked (just prior to
invoking the constructor).  While this is a valid language construct, it is rarely used and is
confusing.

```
//Initializer[@Static='false']
```

**Example(s):**

``` java
public class MyClass {
  // this block gets run before any call to a constructor
  {
    System.out.println("I am about to construct myself");
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NonStaticInitializer" />
```

## NonThreadSafeSingleton

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [NonThreadSafeSingleton](pmd_rules_java_multithreading.html#nonthreadsafesingleton)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.4

**Priority:** Medium (3)

Non-thread safe singletons can result in bad state changes. Eliminate
static singletons if possible by instantiating the object directly. Static
singletons are usually not needed as only a single instance exists anyway.
Other possible fixes are to synchronize the entire method or to use an
[initialize-on-demand holder class](https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom).

Refrain from using the double-checked locking pattern. The Java Memory Model doesn't
guarantee it to work unless the variable is declared as `volatile`, adding an uneeded
performance penalty. [Reference](http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html)

See Effective Java, item 48.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.NonThreadSafeSingletonRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/NonThreadSafeSingletonRule.java)

**Example(s):**

``` java
private static Foo foo = null;

//multiple simultaneous callers may see partially initialized objects
public static Foo getFoo() {
    if (foo==null) {
        foo = new Foo();
    }
    return foo;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkNonStaticFields|false|Check for non-static fields.  Do not set this to true and checkNonStaticMethods to false.|
|checkNonStaticMethods|true|Check for non-static methods.  Do not set this to false and checkNonStaticFields to true.|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NonThreadSafeSingleton" />
```

## NPathComplexity

**Since:** PMD 3.9

**Priority:** Medium (3)

The NPath complexity of a method is the number of acyclic execution paths through that method.
A threshold of 200 is generally considered the point where measures should be taken to reduce 
complexity and increase readability.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.NPathComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/NPathComplexityRule.java)

**Example(s):**

``` java
void bar() {    // this is something more complex than it needs to be,
    if (y) {   // it should be broken down into smaller methods or functions
        for (j = 0; j < m; j++) {
            if (j > r) {
                doSomething();
                while (f < 5 ) {
                    anotherThing();
                    f -= 27;
                }
            } else {
                tryThis();
            }
        }
    }
    if ( r - n > 45) {
       while (doMagic()) {
          findRabbits();
       }
    }
    try {
        doSomethingDangerous();
    } catch (Exception ex) {
        makeAmends();
    } finally {
        dontDoItAgain();
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/NPathComplexity" />
```

## OptimizableToArrayCall

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [OptimizableToArrayCall](pmd_rules_java_performance.html#optimizabletoarraycall)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.8

**Priority:** Medium (3)

Calls to a collection's toArray() method should specify target arrays sized to match the size of the
collection. Initial arrays that are too small are discarded in favour of new ones that have to be created
that are the proper size.

```
//PrimaryExpression
[PrimaryPrefix/Name[ends-with(@Image, 'toArray')]]
[
PrimarySuffix/Arguments/ArgumentList/Expression
 /PrimaryExpression/PrimaryPrefix/AllocationExpression
 /ArrayDimsAndInits/Expression/PrimaryExpression/PrimaryPrefix/Literal[@Image='0']
]
```

**Example(s):**

``` java
List foos = getFoos();

    // inefficient, the array will be discarded
Foo[] fooArray = foos.toArray(new Foo[0]);

    // much better; this one sizes the destination array,
    // avoiding of a new one via reflection
Foo[] fooArray = foos.toArray(new Foo[foos.size()]);
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/OptimizableToArrayCall" />
```

## PositionLiteralsFirstInCaseInsensitiveComparisons

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [PositionLiteralsFirstInCaseInsensitiveComparisons](pmd_rules_java_bestpractices.html#positionliteralsfirstincaseinsensitivecomparisons)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.1

**Priority:** Medium (3)

Position literals first in comparisons, if the second argument is null then NullPointerExceptions
can be avoided, they will just return false.

```
//PrimaryExpression[
        PrimaryPrefix[Name
                [
    (ends-with(@Image, '.equalsIgnoreCase'))
                ]
        ]
        [
                   (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal)
    and
    ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
        ]
]
[not(ancestor::Expression/ConditionalAndExpression//EqualityExpression[@Image='!=']//NullLiteral)]
[not(ancestor::Expression/ConditionalOrExpression//EqualityExpression[@Image='==']//NullLiteral)]
```

**Example(s):**

``` java
class Foo {
  boolean bar(String x) {
    return x.equalsIgnoreCase("2"); // should be "2".equalsIgnoreCase(x)
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/PositionLiteralsFirstInCaseInsensitiveComparisons" />
```

## PositionLiteralsFirstInComparisons

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [PositionLiteralsFirstInComparisons](pmd_rules_java_bestpractices.html#positionliteralsfirstincomparisons)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.3

**Priority:** Medium (3)

Position literals first in comparisons, if the second argument is null then NullPointerExceptions
can be avoided, they will just return false.

```
//PrimaryExpression[
    PrimaryPrefix[Name[(ends-with(@Image, '.equals'))]]
        [
            (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral='true'])
            and
            ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
        ]
]
[not(ancestor::Expression/ConditionalAndExpression//EqualityExpression[@Image='!=']//NullLiteral)]
[not(ancestor::Expression/ConditionalOrExpression//EqualityExpression[@Image='==']//NullLiteral)]
```

**Example(s):**

``` java
class Foo {
  boolean bar(String x) {
    return x.equals("2"); // should be "2".equals(x)
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/PositionLiteralsFirstInComparisons" />
```

## PreserveStackTrace

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [PreserveStackTrace](pmd_rules_java_errorprone.html#preservestacktrace)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.7

**Priority:** Medium (3)

Throwing a new exception from a catch block without passing the original exception into the
new exception will cause the original stack trace to be lost making it difficult to debug
effectively.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.PreserveStackTraceRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/PreserveStackTraceRule.java)

**Example(s):**

``` java
public class Foo {
    void good() {
        try{
            Integer.parseInt("a");
        } catch (Exception e) {
            throw new Exception(e); // first possibility to create exception chain
        }
        try {
            Integer.parseInt("a");
        } catch (Exception e) {
            throw (IllegalStateException)new IllegalStateException().initCause(e); // second possibility to create exception chain.
        }
    }
    void bad() {
        try{
            Integer.parseInt("a");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/PreserveStackTrace" />
```

## ReturnEmptyArrayRatherThanNull

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [ReturnEmptyArrayRatherThanNull](pmd_rules_java_errorprone.html#returnemptyarrayratherthannull)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 4.2

**Priority:** High (1)

For any method that returns an array, it is a better to return an empty array rather than a
null reference. This removes the need for null checking all results and avoids inadvertent
NullPointerExceptions.

```
//MethodDeclaration
[
(./ResultType/Type[@Array='true'])
and
(./Block/BlockStatement/Statement/ReturnStatement/Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral)
]
```

**Example(s):**

``` java
public class Example {
    // Not a good idea...
    public int[] badBehavior() {
        // ...
        return null;
    }

    // Good behavior
    public String[] bonnePratique() {
        //...
        return new String[0];
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/ReturnEmptyArrayRatherThanNull" />
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
<rule ref="rulesets/java/design.xml/SignatureDeclareThrowsException" />
```

## SimpleDateFormatNeedsLocale

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [SimpleDateFormatNeedsLocale](pmd_rules_java_errorprone.html#simpledateformatneedslocale)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.0

**Priority:** Medium (3)

Be sure to specify a Locale when creating SimpleDateFormat instances to ensure that locale-appropriate
formatting is used.

```
//AllocationExpression
 [ClassOrInterfaceType[@Image='SimpleDateFormat']]
 [Arguments[@ArgumentCount=1]]
```

**Example(s):**

``` java
public class Foo {
  // Should specify Locale.US (or whatever)
  private SimpleDateFormat sdf = new SimpleDateFormat("pattern");
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimpleDateFormatNeedsLocale" />
```

## SimplifiedTernary

**Since:** PMD 5.4.0

**Priority:** Medium (3)

Look for ternary operators with the form `condition ? literalBoolean : foo`
or `condition ? foo : literalBoolean`.

These expressions can be simplified respectively to
`condition || foo`  when the literalBoolean is true
`!condition && foo` when the literalBoolean is false
or
`!condition || foo` when the literalBoolean is true
`condition && foo`  when the literalBoolean is false

```
//ConditionalExpression[@Ternary='true'][not(PrimaryExpression/*/Literal) and (Expression/PrimaryExpression/*/Literal/BooleanLiteral)]
|
//ConditionalExpression[@Ternary='true'][not(Expression/PrimaryExpression/*/Literal) and (PrimaryExpression/*/Literal/BooleanLiteral)]
```

**Example(s):**

``` java
public class Foo {
    public boolean test() {
        return condition ? true : something(); // can be as simple as return condition || something();
    }

    public void test2() {
        final boolean value = condition ? false : something(); // can be as simple as value = !condition && something();
    }

    public boolean test3() {
        return condition ? something() : true; // can be as simple as return !condition || something();
    }

    public void test4() {
        final boolean otherValue = condition ? something() : false; // can be as simple as condition && something();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimplifiedTernary" />
```

## SimplifyBooleanAssertion

**Since:** PMD 3.6

**Priority:** Medium (3)

Avoid negation in an assertTrue or assertFalse test.

For example, rephrase:

    assertTrue(!expr);

as:

    assertFalse(expr);

```
//StatementExpression
[
.//Name[@Image='assertTrue' or  @Image='assertFalse']
and
PrimaryExpression/PrimarySuffix/Arguments/ArgumentList
 /Expression/UnaryExpressionNotPlusMinus[@Image='!']
/PrimaryExpression/PrimaryPrefix
]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
public class SimpleTest extends TestCase {
    public void testX() {
        assertTrue("not empty", !r.isEmpty());  // replace with assertFalse("not empty", r.isEmpty())
        assertFalse(!r.isEmpty());              // replace with assertTrue(r.isEmpty())
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimplifyBooleanAssertion" />
```

## SimplifyBooleanExpressions

**Since:** PMD 1.05

**Priority:** Medium (3)

Avoid unnecessary comparisons in boolean expressions, they serve no purpose and impacts readability.

```
//EqualityExpression/PrimaryExpression
 /PrimaryPrefix/Literal/BooleanLiteral
```

**Example(s):**

``` java
public class Bar {
  // can be simplified to
  // bar = isFoo();
  private boolean bar = (isFoo() == true);

  public isFoo() { return false;}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimplifyBooleanExpressions" />
```

## SimplifyBooleanReturns

**Since:** PMD 0.9

**Priority:** Medium (3)

Avoid unnecessary if-then-else statements when returning a boolean. The result of
the conditional test can be returned instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.SimplifyBooleanReturnsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SimplifyBooleanReturnsRule.java)

**Example(s):**

``` java
public boolean isBarEqualTo(int x) {
    if (bar == x) {      // this bit of code...
        return true;
    } else {
        return false;
    }
}

public boolean isBarEqualTo(int x) {
    return bar == x;    // can be replaced with this
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimplifyBooleanReturns" />
```

## SimplifyConditional

**Since:** PMD 3.1

**Priority:** Medium (3)

No need to check for null before an instanceof; the instanceof keyword returns false when given a null argument.

```
//Expression
 [ConditionalOrExpression
 [EqualityExpression[@Image='==']
  //NullLiteral
  and
  UnaryExpressionNotPlusMinus
   [@Image='!']//InstanceOfExpression[PrimaryExpression
     //Name/@Image = ancestor::ConditionalOrExpression/EqualityExpression
      /PrimaryExpression/PrimaryPrefix/Name/@Image]
  and
  (count(UnaryExpressionNotPlusMinus) + 1 = count(*))
 ]
or
ConditionalAndExpression
 [EqualityExpression[@Image='!=']//NullLiteral
 and
InstanceOfExpression
 [PrimaryExpression[count(PrimarySuffix[@ArrayDereference='true'])=0]
  //Name[not(contains(@Image,'.'))]/@Image = ancestor::ConditionalAndExpression
   /EqualityExpression/PrimaryExpression/PrimaryPrefix/Name/@Image]
 and
(count(InstanceOfExpression) + 1 = count(*))
 ]
]
```

**Example(s):**

``` java
class Foo {
  void bar(Object x) {
    if (x != null && x instanceof Bar) {
      // just drop the "x != null" check
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SimplifyConditional" />
```

## SingleMethodSingleton

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [SingleMethodSingleton](pmd_rules_java_errorprone.html#singlemethodsingleton)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.4

**Priority:** Medium High (2)

Some classes contain overloaded getInstance. The problem with overloaded getInstance methods
is that the instance created using the overloaded method is not cached and so,
for each call and new objects will be created for every invocation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.SingleMethodSingletonRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SingleMethodSingletonRule.java)

**Example(s):**

``` java
public class Singleton {

    private static Singleton singleton = new Singleton( );

    private Singleton(){ }

    public static Singleton getInstance( ) {
        return singleton;
    }

    public static Singleton getInstance(Object obj){
        Singleton singleton = (Singleton) obj;
        return singleton;           //violation
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SingleMethodSingleton" />
```

## SingletonClassReturningNewInstance

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [SingletonClassReturningNewInstance](pmd_rules_java_errorprone.html#singletonclassreturningnewinstance)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.4

**Priority:** Medium High (2)

Some classes contain overloaded getInstance. The problem with overloaded getInstance methods
is that the instance created using the overloaded method is not cached and so,
for each call and new objects will be created for every invocation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.SingletonClassReturningNewInstanceRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SingletonClassReturningNewInstanceRule.java)

**Example(s):**

``` java
class Singleton {
    private static Singleton instance = null;
    public static Singleton getInstance() {
        synchronized(Singleton.class) {
            return new Singleton();
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SingletonClassReturningNewInstance" />
```

## SingularField

**Since:** PMD 3.1

**Priority:** Medium (3)

Fields whose scopes are limited to just single methods do not rely on the containing
object to provide them to other methods. They may be better implemented as local variables
within those methods.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.SingularFieldRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SingularFieldRule.java)

**Example(s):**

``` java
public class Foo {
    private int x;  // no reason to exist at the Foo instance level
    public void foo(int y) {
     x = y + 5;
     return x;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|disallowNotAssignment|false|Disallow violations where the first usage is not an assignment|
|checkInnerClasses|false|Check inner classes|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SingularField" />
```

## StdCyclomaticComplexity

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.1.2

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.StdCyclomaticComplexityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/StdCyclomaticComplexityRule.java)

**Example(s):**

``` java
public class Foo {    // This has a Cyclomatic Complexity = 12
1   public void example()  {
2       if (a == b || (c == d && e == f))  { // Only one
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
            switch (z) {
9               case 1:
                    fiddle();
                    break;
10              case 2:
                    fiddle();
                    break;
11              case 3:
                    fiddle();
                    break;
12              default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/StdCyclomaticComplexity" />
```

## SwitchDensity

**Since:** PMD 1.02

**Priority:** Medium (3)

A high ratio of statements to labels in a switch statement implies that the switch statement
is overloaded.  Consider moving the statements into new methods or creating subclasses based
on the switch variable.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.SwitchDensityRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/SwitchDensityRule.java)

**Example(s):**

``` java
public class Foo {
  public void bar(int x) {
    switch (x) {
      case 1: {
        // lots of statements
        break;
      } case 2: {
        // lots of statements
        break;
      }
    }
  }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SwitchDensity" />
```

## SwitchStmtsShouldHaveDefault

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [SwitchStmtsShouldHaveDefault](pmd_rules_java_bestpractices.html#switchstmtsshouldhavedefault)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.0

**Priority:** Medium (3)

All switch statements should include a default option to catch any unspecified values.

```
//SwitchStatement[not(SwitchLabel[@Default='true'])]
```

**Example(s):**

``` java
public void bar() {
    int x = 2;
    switch (x) {
      case 1: int j = 6;
      case 2: int j = 8;
          // missing default: here
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/SwitchStmtsShouldHaveDefault" />
```

## TooFewBranchesForASwitchStatement

**Since:** PMD 4.2

**Priority:** Medium (3)

Switch statements are intended to be used to support complex branching behaviour. Using a switch for only a few
cases is ill-advised, since switches are not as easy to understand as if-then statements. In these cases use the
if-then statement to increase code readability.

```
//SwitchStatement[
    (count(.//SwitchLabel) < $minimumNumberCaseForASwitch)
]
```

**Example(s):**

``` java
// With a minimumNumberCaseForASwitch of 3
public class Foo {
    public void bar() {
        switch (condition) {
            case ONE:
                instruction;
                break;
            default:
                break; // not enough for a 'switch' stmt, a simple 'if' stmt would have been more appropriate
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimumNumberCaseForASwitch|3|Minimum number of branches for a switch|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/TooFewBranchesForASwitchStatement" />
```

## TooManyFields

**Since:** PMD 3.0

**Priority:** Medium (3)

Classes that have too many fields can become unwieldy and could be redesigned to have fewer fields,
possibly through grouping related fields in new objects.  For example, a class with individual 
city/state/zip fields could park them within a single Address field.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codesize.TooManyFieldsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codesize/TooManyFieldsRule.java)

**Example(s):**

``` java
public class Person {   // too many separate fields
   int birthYear;
   int birthMonth;
   int birthDate;
   float height;
   float weight;
}

public class Person {   // this is more manageable
   Date birthDate;
   BodyMeasurements measurements;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maxfields|15|Max allowable fields|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/TooManyFields" />
```

## TooManyMethods

**Since:** PMD 4.2

**Priority:** Medium (3)

A class with too many methods is probably a good suspect for refactoring, in order to reduce its
complexity and find a way to have more fine grained objects.

```
//ClassOrInterfaceDeclaration/ClassOrInterfaceBody
     [
      count(./ClassOrInterfaceBodyDeclaration/MethodDeclaration/MethodDeclarator[
         not (
                starts-with(@Image,'get')
                or
                starts-with(@Image,'set')
                or
                starts-with(@Image,'is')
            )
      ]) > $maxmethods
   ]
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maxmethods|10|The method count reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/TooManyMethods" />
```

## UncommentedEmptyConstructor

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UncommentedEmptyConstructor](pmd_rules_java_documentation.html#uncommentedemptyconstructor)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.4

**Priority:** Medium (3)

Uncommented Empty Constructor finds instances where a constructor does not
contain statements, but there is no comment. By explicitly commenting empty
constructors it is easier to distinguish between intentional (commented)
and unintentional empty constructors.

```
//ConstructorDeclaration[@Private='false']
                        [count(BlockStatement) = 0 and ($ignoreExplicitConstructorInvocation = 'true' or not(ExplicitConstructorInvocation)) and @containsComment = 'false']
                        [not(../Annotation/MarkerAnnotation/Name[typeof(@Image, 'javax.inject.Inject', 'Inject')])]
```

**Example(s):**

``` java
public Foo() {
  // This constructor is intentionally empty. Nothing special is needed here.
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreExplicitConstructorInvocation|false|Ignore explicit constructor invocation when deciding whether constructor is empty or not|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UncommentedEmptyConstructor" />
```

## UncommentedEmptyMethodBody

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UncommentedEmptyMethodBody](pmd_rules_java_documentation.html#uncommentedemptymethodbody)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.4

**Priority:** Medium (3)

Uncommented Empty Method Body finds instances where a method body does not contain
statements, but there is no comment. By explicitly commenting empty method bodies
it is easier to distinguish between intentional (commented) and unintentional
empty methods.

```
//MethodDeclaration/Block[count(BlockStatement) = 0 and @containsComment = 'false']
```

**Example(s):**

``` java
public void doSomething() {
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UncommentedEmptyMethodBody" />
```

## UnnecessaryLocalBeforeReturn

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UnnecessaryLocalBeforeReturn](pmd_rules_java_codestyle.html#unnecessarylocalbeforereturn)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.3

**Priority:** Medium (3)

Avoid the creation of unnecessary local variables

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.UnnecessaryLocalBeforeReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/UnnecessaryLocalBeforeReturnRule.java)

**Example(s):**

``` java
public class Foo {
   public int foo() {
     int x = doSomething();
     return x;  // instead, just 'return doSomething();'
   }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|statementOrderMatters|true|If set to false this rule no longer requires the variable declaration and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be reported.|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UnnecessaryLocalBeforeReturn" />
```

## UnsynchronizedStaticDateFormatter

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UnsynchronizedStaticDateFormatter](pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.6

**Priority:** Medium (3)

SimpleDateFormat instances are not synchronized. Sun recommends using separate format instances
for each thread. If multiple threads must access a static formatter, the formatter must be
synchronized either on method or block level.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.UnsynchronizedStaticDateFormatterRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/UnsynchronizedStaticDateFormatterRule.java)

**Example(s):**

``` java
public class Foo {
    private static final SimpleDateFormat sdf = new SimpleDateFormat();
    void bar() {
        sdf.format(); // poor, no thread-safety
    }
    synchronized void foo() {
        sdf.format(); // preferred
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UnsynchronizedStaticDateFormatter" />
```

## UseCollectionIsEmpty

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UseCollectionIsEmpty](pmd_rules_java_errorprone.html#usecollectionisempty)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.9

**Priority:** Medium (3)

The isEmpty() method on java.util.Collection is provided to determine if a collection has any elements.
Comparing the value of size() to 0 does not convey intent as well as the isEmpty() method.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.UseCollectionIsEmptyRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/UseCollectionIsEmptyRule.java)

**Example(s):**

``` java
public class Foo {
    void good() {
        List foo = getList();
        if (foo.isEmpty()) {
            // blah
        }
    }

    void bad() {
        List foo = getList();
        if (foo.size() == 0) {
            // blah
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseCollectionIsEmpty" />
```

## UselessOverridingMethod

**Since:** PMD 3.3

**Priority:** Medium (3)

The overriding method merely calls the same method defined in a superclass.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UselessOverridingMethodRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UselessOverridingMethodRule.java)

**Example(s):**

``` java
public void foo(String bar) {
    super.foo(bar);      // why bother overriding?
}

public String foo() {
    return super.foo();  // why bother overriding?
}

@Id
public Long getId() {
    return super.getId();  // OK if 'ignoreAnnotations' is false, which is the default behavior
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreAnnotations|false|Ignore annotations|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UselessOverridingMethod" />
```

## UseLocaleWithCaseConversions

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UseLocaleWithCaseConversions](pmd_rules_java_errorprone.html#uselocalewithcaseconversions)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.0

**Priority:** Medium (3)

When doing String.toLowerCase()/toUpperCase() conversions, use Locales to avoids problems with languages that
have unusual conventions, i.e. Turkish.

```
//PrimaryExpression
[
PrimaryPrefix
[Name[ends-with(@Image, 'toLowerCase') or ends-with(@Image, 'toUpperCase')]]
[following-sibling::PrimarySuffix[position() = 1]/Arguments[@ArgumentCount=0]]

or

PrimarySuffix
[ends-with(@Image, 'toLowerCase') or ends-with(@Image, 'toUpperCase')]
[following-sibling::PrimarySuffix[position() = 1]/Arguments[@ArgumentCount=0]]
]
[not(PrimaryPrefix/Name[ends-with(@Image, 'toHexString')])]
```

**Example(s):**

``` java
class Foo {
    // BAD
    if (x.toLowerCase().equals("list")) { }

    /*
     * This will not match "LIST" when in Turkish locale
     * The above could be
     * if (x.toLowerCase(Locale.US).equals("list")) { }
     * or simply
     * if (x.equalsIgnoreCase("list")) { }
     */
    // GOOD
    String z = a.toLowerCase(Locale.EN);
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseLocaleWithCaseConversions" />
```

## UseNotifyAllInsteadOfNotify

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UseNotifyAllInsteadOfNotify](pmd_rules_java_multithreading.html#usenotifyallinsteadofnotify)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.0

**Priority:** Medium (3)

Thread.notify() awakens a thread monitoring the object. If more than one thread is monitoring, then only
one is chosen.  The thread chosen is arbitrary; thus its usually safer to call notifyAll() instead.

```
//StatementExpression/PrimaryExpression
[PrimarySuffix/Arguments[@ArgumentCount = '0']]
[
    PrimaryPrefix[
        ./Name[@Image='notify' or ends-with(@Image,'.notify')]
        or ../PrimarySuffix/@Image='notify'
        or (./AllocationExpression and ../PrimarySuffix[@Image='notify'])
    ]
]
```

**Example(s):**

``` java
void bar() {
    x.notify();
    // If many threads are monitoring x, only one (and you won't know which) will be notified.
    // use instead:
    x.notifyAll();
  }
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseNotifyAllInsteadOfNotify" />
```

## UseObjectForClearerAPI

**Since:** PMD 4.2.6

**Priority:** Medium (3)

When you write a public method, you should be thinking in terms of an API. If your method is public, it means other class
will use it, therefore, you want (or need) to offer a comprehensive and evolutive API. If you pass a lot of information
as a simple series of Strings, you may think of using an Object to represent all those information. You'll get a simpler
API (such as doWork(Workload workload), rather than a tedious series of Strings) and more importantly, if you need at some
point to pass extra data, you'll be able to do so by simply modifying or extending Workload without any modification to
your API.

```
//MethodDeclaration[@Public]/MethodDeclarator/FormalParameters[
     count(FormalParameter/Type/ReferenceType/ClassOrInterfaceType[@Image = 'String']) > 3
]
```

**Example(s):**

``` java
public class MyClass {
    public void connect(String username,
        String pssd,
        String databaseName,
        String databaseAdress)
        // Instead of those parameters object
        // would ensure a cleaner API and permit
        // to add extra data transparently (no code change):
        // void connect(UserData data);
    {

    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseObjectForClearerAPI" />
```

## UseUtilityClass

**Since:** PMD 0.3

**Priority:** Medium (3)

For classes that only have static methods, consider making them utility classes.
Note that this doesn't apply to abstract classes, since their subclasses may
well include non-static methods.  Also, if you want this class to be a utility class,
remember to add a private constructor to prevent instantiation.
(Note, that this use was known before PMD 5.1.0 as UseSingleton).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.UseUtilityClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/UseUtilityClassRule.java)

**Example(s):**

``` java
public class MaybeAUtility {
  public static void foo() {}
  public static void bar() {}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseUtilityClass" />
```

## UseVarargs

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

The rule has been moved to another ruleset. Use instead: [UseVarargs](pmd_rules_java_bestpractices.html#usevarargs)

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.0

**Priority:** Medium Low (4)

**Minimum Language Version:** Java 1.5

Java 5 introduced the varargs parameter declaration for methods and constructors.  This syntactic
sugar provides flexibility for users of these methods and constructors, allowing them to avoid
having to deal with the creation of an array.

```
//FormalParameters/FormalParameter
    [position()=last()]
    [@Array='true']
    [@Varargs='false']
    [not (./Type/ReferenceType[@Array='true'][PrimitiveType[@Image='byte']])]
    [not (./Type/ReferenceType[ClassOrInterfaceType[@Image='Byte']])]
    [not (./Type/PrimitiveType[@Image='byte'])]
    [not (ancestor::MethodDeclaration/preceding-sibling::Annotation/*/Name[@Image='Override'])]
    [not(
        ancestor::MethodDeclaration
            [@Public='true' and @Static='true']
            [child::ResultType[@Void='true']] and
        ancestor::MethodDeclarator[@Image='main'] and
        ..[@ParameterCount='1'] and
        ./Type/ReferenceType[ClassOrInterfaceType[@Image='String']]
    )]
```

**Example(s):**

``` java
public class Foo {
    public void foo(String s, Object[] args) {
        // Do something here...
    }

    public void bar(String s, Object... args) {
        // Ahh, varargs tastes much better...
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/design.xml/UseVarargs" />
```

