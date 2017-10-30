---
title: Error Prone
summary: The Error Prone category contains rules, that detect incorrect usages, missed checks, ...  It fully contains these previous rulesets:  *   android *   clone *   empty *   finalizers *   javabeans *   unusedcode
permalink: pmd_rules_java_errorprone.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/category/java/errorprone.xml
keywords: Error Prone, AbstractClassWithoutAbstractMethod, AccessorClassGeneration, AccessorMethodGeneration, AssignmentInOperand, AssignmentToNonFinalStatic, AvoidAccessibilityAlteration, AvoidAssertAsIdentifier, AvoidBranchingStatementAsLastInLoop, AvoidCallingFinalize, AvoidCatchingNPE, AvoidCatchingThrowable, AvoidDecimalLiteralsInBigDecimalConstructor, AvoidDuplicateLiterals, AvoidEnumAsIdentifier, AvoidFieldNameMatchingMethodName, AvoidFieldNameMatchingTypeName, AvoidInstanceofChecksInCatchClause, AvoidLiteralsInIfCondition, AvoidLosingExceptionInformation, AvoidMultipleUnaryOperators, AvoidProtectedFieldInFinalClass, AvoidProtectedMethodInFinalClassNotExtending, AvoidUsingOctalValues, BadComparison, BeanMembersShouldSerialize, BooleanGetMethodName, BrokenNullCheck, CallSuperFirst, CallSuperInConstructor, CallSuperLast, CheckResultSet, CheckSkipResult, ClassCastExceptionWithToArray, CloneMethodMustBePublic, CloneMethodMustImplementCloneable, CloneMethodReturnTypeMustMatchClassName, CloneThrowsCloneNotSupportedException, CloseResource, CompareObjectsWithEquals, ConfusingTernary, ConstantsInInterface, ConstructorCallsOverridableMethod, DataflowAnomalyAnalysis, DefaultLabelNotLastInSwitchStmt, DoNotCallSystemExit, DoNotExtendJavaLangError, DoNotExtendJavaLangThrowable, DoNotHardCodeSDCard, DoNotThrowExceptionInFinally, DontImportJavaLang, DontImportSun, DontUseFloatTypeForLoopIndices, DuplicateImports, EmptyCatchBlock, EmptyFinalizer, EmptyFinallyBlock, EmptyIfStmt, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer, EmptyStatementNotInLoop, EmptySwitchStatements, EmptySynchronizedBlock, EmptyTryBlock, EmptyWhileStmt, EqualsNull, ExtendsObject, FinalizeDoesNotCallSuperFinalize, FinalizeOnlyCallsSuperFinalize, FinalizeOverloaded, FinalizeShouldBeProtected, ForLoopCanBeForeach, ForLoopShouldBeWhileLoop, IdempotentOperations, ImportFromSamePackage, InstantiationToGetClass, InvalidSlf4jMessageFormat, JumbledIncrementer, JUnitSpelling, JUnitStaticSuite, LoggerIsNotStaticFinal, MethodWithSameNameAsEnclosingClass, MisplacedNullCheck, MissingBreakInSwitch, MissingSerialVersionUID, MissingStaticMethodInNonInstantiatableClass, MoreThanOneLogger, NonCaseLabelInSwitchStatement, NonStaticInitializer, OneDeclarationPerLine, OverrideBothEqualsAndHashcode, PreserveStackTrace, ProperCloneImplementation, ProperLogger, ReturnEmptyArrayRatherThanNull, ReturnFromFinallyBlock, SimpleDateFormatNeedsLocale, SingleMethodSingleton, SingletonClassReturningNewInstance, StaticEJBFieldShouldBeFinal, StringBufferInstantiationWithChar, SuspiciousConstantFieldName, SuspiciousEqualsMethodName, SuspiciousHashcodeMethodName, SuspiciousOctalEscape, TestClassWithoutTestCases, UnconditionalIfStatement, UnnecessaryBooleanAssertion, UnnecessaryCaseChange, UnnecessaryConversionTemporary, UnnecessaryFullyQualifiedName, UnnecessaryReturn, UnusedFormalParameter, UnusedImports, UnusedLocalVariable, UnusedNullCheckInEquals, UnusedPrivateField, UnusedPrivateMethod, UseAssertEqualsInsteadOfAssertTrue, UseAssertNullInsteadOfAssertTrue, UseAssertSameInsteadOfAssertTrue, UseAssertTrueInsteadOfAssertEquals, UseCollectionIsEmpty, UseCorrectExceptionLogging, UseEqualsToCompareStrings, UselessOperationOnImmutable, UseLocaleWithCaseConversions, UseProperClassLoader
---
## AbstractClassWithoutAbstractMethod

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
<rule ref="category/java/errorprone.xml/AbstractClassWithoutAbstractMethod" />
```

## AccessorClassGeneration

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
<rule ref="category/java/errorprone.xml/AccessorClassGeneration" />
```

## AccessorMethodGeneration

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
<rule ref="category/java/errorprone.xml/AccessorMethodGeneration" />
```

## AssignmentInOperand

**Since:** PMD 1.03

**Priority:** Medium (3)

Avoid assignments in operands; this can make code more complicated and harder to read.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.controversial.AssignmentInOperandRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/controversial/AssignmentInOperandRule.java)

**Example(s):**

``` java
public void bar() {
    int x = 2;
    if ((x = getX()) == 3) {
      System.out.println("3!");
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|allowIncrementDecrement|false|Allow increment or decrement operators within the conditional expression of an if, for, or while statement|
|allowWhile|false|Allow assignment within the conditional expression of a while statement|
|allowFor|false|Allow assignment within the conditional expression of a for statement|
|allowIf|false|Allow assignment within the conditional expression of an if statement|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AssignmentInOperand" />
```

## AssignmentToNonFinalStatic

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
<rule ref="category/java/errorprone.xml/AssignmentToNonFinalStatic" />
```

## AvoidAccessibilityAlteration

**Since:** PMD 4.1

**Priority:** Medium (3)

Methods such as getDeclaredConstructors(), getDeclaredConstructor(Class[]) and setAccessible(),
as the interface PrivilegedAction, allows for the runtime alteration of variable, class, or
method visibility, even if they are private. This violates the principle of encapsulation.

```
//PrimaryExpression[
(
(PrimarySuffix[
        ends-with(@Image,'getDeclaredConstructors')
                or
        ends-with(@Image,'getDeclaredConstructor')
                or
        ends-with(@Image,'setAccessible')
        ])
or
(PrimaryPrefix/Name[
        ends-with(@Image,'getDeclaredConstructor')
        or
        ends-with(@Image,'getDeclaredConstructors')
        or
        starts-with(@Image,'AccessibleObject.setAccessible')
        ])
)
and
(//ImportDeclaration/Name[
        contains(@Image,'java.security.PrivilegedAction')])
]
```

**Example(s):**

``` java
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;

public class Violation {
  public void invalidCallsInMethod() throws SecurityException, NoSuchMethodException {
    // Possible call to forbidden getDeclaredConstructors
    Class[] arrayOfClass = new Class[1];
    this.getClass().getDeclaredConstructors();
    this.getClass().getDeclaredConstructor(arrayOfClass);
    Class clazz = this.getClass();
    clazz.getDeclaredConstructor(arrayOfClass);
    clazz.getDeclaredConstructors();
      // Possible call to forbidden setAccessible
    clazz.getMethod("", arrayOfClass).setAccessible(false);
    AccessibleObject.setAccessible(null, false);
    Method.setAccessible(null, false);
    Method[] methodsArray = clazz.getMethods();
    int nbMethod;
    for ( nbMethod = 0; nbMethod < methodsArray.length; nbMethod++ ) {
      methodsArray[nbMethod].setAccessible(false);
    }

      // Possible call to forbidden PrivilegedAction
    PrivilegedAction priv = (PrivilegedAction) new Object(); priv.run();
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidAccessibilityAlteration" />
```

## AvoidAssertAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'assert' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='assert']
```

**Example(s):**

``` java
public class A {
    public class Foo {
        String assert = "foo";
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidAssertAsIdentifier" />
```

## AvoidBranchingStatementAsLastInLoop

**Since:** PMD 5.0

**Priority:** Medium High (2)

Using a branching statement as the last part of a loop may be a bug, and/or is confusing.
Ensure that the usage is not a bug, or consider using another approach.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidBranchingStatementAsLastInLoopRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidBranchingStatementAsLastInLoopRule.java)

**Example(s):**

``` java
// unusual use of branching statement in a loop
for (int i = 0; i < 10; i++) {
    if (i*i <= 25) {
        continue;
    }
    break;
}

// this makes more sense...
for (int i = 0; i < 10; i++) {
    if (i*i > 25) {
        break;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkReturnLoopTypes|[for, do, while]|Check for return statements in loop types|
|checkContinueLoopTypes|[for, do, while]|Check for continue statements in loop types|
|checkBreakLoopTypes|[for, do, while]|Check for break statements in loop types|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidBranchingStatementAsLastInLoop" />
```

## AvoidCallingFinalize

**Since:** PMD 3.0

**Priority:** Medium (3)

The method Object.finalize() is called by the garbage collector on an object when garbage collection determines
that there are no more references to the object. It should not be invoked by application logic.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.finalizers.AvoidCallingFinalizeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/finalizers/AvoidCallingFinalizeRule.java)

**Example(s):**

``` java
void foo() {
    Bar b = new Bar();
    b.finalize();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidCallingFinalize" />
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
<rule ref="category/java/errorprone.xml/AvoidCatchingNPE" />
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
<rule ref="category/java/errorprone.xml/AvoidCatchingThrowable" />
```

## AvoidDecimalLiteralsInBigDecimalConstructor

**Since:** PMD 3.4

**Priority:** Medium (3)

One might assume that the result of "new BigDecimal(0.1)" is exactly equal to 0.1, but it is actually
equal to .1000000000000000055511151231257827021181583404541015625.
This is because 0.1 cannot be represented exactly as a double (or as a binary fraction of any finite
length). Thus, the long value that is being passed in to the constructor is not exactly equal to 0.1,
appearances notwithstanding.

The (String) constructor, on the other hand, is perfectly predictable: 'new BigDecimal("0.1")' is
exactly equal to 0.1, as one would expect.  Therefore, it is generally recommended that the
(String) constructor be used in preference to this one.

```
//AllocationExpression
[ClassOrInterfaceType[@Image="BigDecimal"]]
[Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix
    [
        Literal[(not(ends-with(@Image,'"'))) and contains(@Image,".")]
        or
        Name[ancestor::Block/BlockStatement/LocalVariableDeclaration
                [Type[PrimitiveType[@Image='double' or @Image='float']
                      or ReferenceType/ClassOrInterfaceType[@Image='Double' or @Image='Float']]]
                /VariableDeclarator/VariableDeclaratorId/@Image = @Image
            ]
        or
        Name[ancestor::MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter
                [Type[PrimitiveType[@Image='double' or @Image='float']
                      or ReferenceType/ClassOrInterfaceType[@Image='Double' or @Image='Float']]]
                /VariableDeclaratorId/@Image = @Image
            ]
    ]
]
```

**Example(s):**

``` java
BigDecimal bd = new BigDecimal(1.123);       // loss of precision, this would trigger the rule

BigDecimal bd = new BigDecimal("1.123");     // preferred approach

BigDecimal bd = new BigDecimal(12);          // preferred approach, ok for integer values
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidDecimalLiteralsInBigDecimalConstructor" />
```

## AvoidDuplicateLiterals

**Since:** PMD 1.0

**Priority:** Medium (3)

Code containing duplicate String literals can usually be improved by declaring the String as a constant field.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strings.AvoidDuplicateLiteralsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strings/AvoidDuplicateLiteralsRule.java)

**Example(s):**

``` java
private void bar() {
     buz("Howdy");
     buz("Howdy");
     buz("Howdy");
     buz("Howdy");
}
private void buz(String x) {}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|exceptionfile||File containing strings to skip (one string per line), only used if ignore list is not set|
|separator|,|Ignore list separator|
|exceptionList||Strings to ignore|
|maxDuplicateLiterals|4|Max duplicate literals|
|minimumLength|3|Minimum string length to check|
|skipAnnotations|false|Skip literals within annotations|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidDuplicateLiterals" />
```

## AvoidEnumAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'enum' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='enum']
```

**Example(s):**

``` java
public class A {
    public class Foo {
        String enum = "foo";
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidEnumAsIdentifier" />
```

## AvoidFieldNameMatchingMethodName

**Since:** PMD 3.0

**Priority:** Medium (3)

It can be confusing to have a field name with the same name as a method. While this is permitted, 
having information (field) and actions (method) is not clear naming. Developers versed in 
Smalltalk often prefer this approach as the methods denote accessor methods.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.AvoidFieldNameMatchingMethodNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/AvoidFieldNameMatchingMethodNameRule.java)

**Example(s):**

``` java
public class Foo {
    Object bar;
    // bar is data or an action or both?
    void bar() {
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidFieldNameMatchingMethodName" />
```

## AvoidFieldNameMatchingTypeName

**Since:** PMD 3.0

**Priority:** Medium (3)

It is somewhat confusing to have a field name matching the declaring class name.
This probably means that type and/or field names should be chosen more carefully.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.AvoidFieldNameMatchingTypeNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/AvoidFieldNameMatchingTypeNameRule.java)

**Example(s):**

``` java
public class Foo extends Bar {
    int foo;    // There is probably a better name that can be used
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidFieldNameMatchingTypeName" />
```

## AvoidInstanceofChecksInCatchClause

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
<rule ref="category/java/errorprone.xml/AvoidInstanceofChecksInCatchClause" />
```

## AvoidLiteralsInIfCondition

**Since:** PMD 4.2.6

**Priority:** Medium (3)

Avoid using hard-coded literals in conditional statements. By declaring them as static variables
or private members with descriptive names maintainability is enhanced. By default, the literals "-1" and "0" are ignored.
More exceptions can be defined with the property "ignoreMagicNumbers".

```
//IfStatement/Expression/*/PrimaryExpression/PrimaryPrefix/Literal
[not(NullLiteral)]
[not(BooleanLiteral)]
[empty(index-of(tokenize($ignoreMagicNumbers, '\s*,\s*'), @Image))]
```

**Example(s):**

``` java
private static final int MAX_NUMBER_OF_REQUESTS = 10;

public void checkRequests() {

    if (i == 10) {                        // magic number, buried in a method
      doSomething();
    }

    if (i == MAX_NUMBER_OF_REQUESTS) {    // preferred approach
      doSomething();
    }

    if (aString.indexOf('.') != -1) {}     // magic number -1, by default ignored
    if (aString.indexOf('.') >= 0) { }     // alternative approach

    if (aDouble > 0.0) {}                  // magic number 0.0
    if (aDouble >= Double.MIN_VALUE) {}    // preferred approach
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|ignoreMagicNumbers|-1,0|Comma-separated list of magic numbers, that should be ignored|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidLiteralsInIfCondition" />
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
<rule ref="category/java/errorprone.xml/AvoidLosingExceptionInformation" />
```

## AvoidMultipleUnaryOperators

**Since:** PMD 4.2

**Priority:** Medium High (2)

The use of multiple unary operators may be problematic, and/or confusing.
Ensure that the intended usage is not a bug, or consider simplifying the expression.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidMultipleUnaryOperatorsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidMultipleUnaryOperatorsRule.java)

**Example(s):**

``` java
// These are typo bugs, or at best needlessly complex and confusing:
int i = - -1;
int j = + - +1;
int z = ~~2;
boolean b = !!true;
boolean c = !!!true;

// These are better:
int i = 1;
int j = -1;
int z = 2;
boolean b = true;
boolean c = false;

// And these just make your brain hurt:
int i = ~-2;
int j = -~7;
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidMultipleUnaryOperators" />
```

## AvoidProtectedFieldInFinalClass

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
<rule ref="category/java/errorprone.xml/AvoidProtectedFieldInFinalClass" />
```

## AvoidProtectedMethodInFinalClassNotExtending

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
<rule ref="category/java/errorprone.xml/AvoidProtectedMethodInFinalClassNotExtending" />
```

## AvoidUsingOctalValues

**Since:** PMD 3.9

**Priority:** Medium (3)

Integer literals should not start with zero since this denotes that the rest of literal will be
interpreted as an octal value.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidUsingOctalValuesRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidUsingOctalValuesRule.java)

**Example(s):**

``` java
int i = 012;    // set i with 10 not 12
int j = 010;    // set j with 8 not 10
k = i * j;      // set k with 80 not 120
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strict|false|Detect violations between 00 and 07|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/AvoidUsingOctalValues" />
```

## BadComparison

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
<rule ref="category/java/errorprone.xml/BadComparison" />
```

## BeanMembersShouldSerialize

**Since:** PMD 1.1

**Priority:** Medium (3)

If a class is a bean, or is referenced by a bean directly or indirectly it needs to be serializable. 
Member variables need to be marked as transient, static, or have accessor methods in the class. Marking 
variables as transient is the safest and easiest modification. Accessor methods should follow the Java 
naming conventions, i.e. for a variable named foo, getFoo() and setFoo() accessor methods should be provided.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.javabeans.BeanMembersShouldSerializeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/javabeans/BeanMembersShouldSerializeRule.java)

**Example(s):**

``` java
private transient int someFoo;  // good, it's transient
private static int otherFoo;    // also OK
private int moreFoo;            // OK, has proper accessors, see below
private int badFoo;             // bad, should be marked transient

private void setMoreFoo(int moreFoo){
      this.moreFoo = moreFoo;
}

private int getMoreFoo(){
      return this.moreFoo;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|prefix||A variable prefix to skip, i.e., m_|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/BeanMembersShouldSerialize" />
```

## BooleanGetMethodName

**Since:** PMD 4.0

**Priority:** Medium Low (4)

Methods that return boolean results should be named as predicate statements to denote this.
I.e, 'isReady()', 'hasValues()', 'canCommit()', 'willFail()', etc.   Avoid the use of the 'get'
prefix for these methods.

```
//MethodDeclaration[
MethodDeclarator[count(FormalParameters/FormalParameter) = 0 or $checkParameterizedMethods = 'true']
                [starts-with(@Image, 'get')]
and
ResultType/Type/PrimitiveType[@Image = 'boolean']
and not(../Annotation//Name[@Image = 'Override'])
]
```

**Example(s):**

``` java
public boolean getFoo();            // bad
public boolean isFoo();             // ok
public boolean getFoo(boolean bar); // ok, unless checkParameterizedMethods=true
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkParameterizedMethods|false|Check parameterized methods|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/BooleanGetMethodName" />
```

## BrokenNullCheck

**Since:** PMD 3.8

**Priority:** Medium High (2)

The null check is broken since it will throw a NullPointerException itself.
It is likely that you used || instead of && or vice versa.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.BrokenNullCheckRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/BrokenNullCheckRule.java)

**Example(s):**

``` java
public String bar(String string) {
  // should be &&
    if (string!=null || !string.equals(""))
        return string;
  // should be ||
    if (string==null && string.equals(""))
        return string;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/BrokenNullCheck" />
```

## CallSuperFirst

**Since:** PMD 4.2.5

**Priority:** Medium (3)

Super should be called at the start of the method

```
//MethodDeclaration[MethodDeclarator[
  @Image='onCreate' or
  @Image='onConfigurationChanged' or
  @Image='onPostCreate' or
  @Image='onPostResume' or
  @Image='onRestart' or
  @Image='onRestoreInstanceState' or
  @Image='onResume' or
  @Image='onStart'
  ]]
    /Block[not(
      (BlockStatement[1]/Statement/StatementExpression/PrimaryExpression[./PrimaryPrefix[@SuperModifier='true']]/PrimarySuffix[@Image= ancestor::MethodDeclaration/MethodDeclarator/@Image]))]
[ancestor::ClassOrInterfaceDeclaration[ExtendsList/ClassOrInterfaceType[
  typeof(@Image, 'android.app.Activity', 'Activity') or
  typeof(@Image, 'android.app.Application', 'Application') or
  typeof(@Image, 'android.app.Service', 'Service')
]]]
```

**Example(s):**

``` java
public class DummyActivity extends Activity {
    public void onCreate(Bundle bundle) {
        // missing call to super.onCreate(bundle)
        foo();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CallSuperFirst" />
```

## CallSuperInConstructor

**Since:** PMD 3.0

**Priority:** Medium (3)

It is a good practice to call super() in a constructor. If super() is not called but
another constructor (such as an overloaded constructor) is called, this rule will not report it.

```
//ClassOrInterfaceDeclaration[ count (ExtendsList/*) > 0 ]
/ClassOrInterfaceBody
 /ClassOrInterfaceBodyDeclaration
 /ConstructorDeclaration[ count (.//ExplicitConstructorInvocation)=0 ]
```

**Example(s):**

``` java
public class Foo extends Bar{
  public Foo() {
   // call the constructor of Bar
   super();
  }
 public Foo(int code) {
  // do something with code
   this();
   // no problem with this
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CallSuperInConstructor" />
```

## CallSuperLast

**Since:** PMD 4.2.5

**Priority:** Medium (3)

Super should be called at the end of the method

```
//MethodDeclaration[MethodDeclarator[
  @Image='finish' or
  @Image='onDestroy' or
  @Image='onPause' or
  @Image='onSaveInstanceState' or
  @Image='onStop' or
  @Image='onTerminate'
  ]]
   /Block/BlockStatement[last()]
    [not(Statement/StatementExpression/PrimaryExpression[./PrimaryPrefix[@SuperModifier='true']]/PrimarySuffix[@Image= ancestor::MethodDeclaration/MethodDeclarator/@Image])]
[ancestor::ClassOrInterfaceDeclaration[ExtendsList/ClassOrInterfaceType[
  typeof(@Image, 'android.app.Activity', 'Activity') or
  typeof(@Image, 'android.app.Application', 'Application') or
  typeof(@Image, 'android.app.Service', 'Service')
]]]
```

**Example(s):**

``` java
public class DummyActivity extends Activity {
    public void onPause() {
        foo();
        // missing call to super.onPause()
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CallSuperLast" />
```

## CheckResultSet

**Since:** PMD 4.1

**Priority:** Medium (3)

Always check the return values of navigation methods (next, previous, first, last) of a ResultSet.
If the value return is 'false', it should be handled properly.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.CheckResultSetRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/CheckResultSetRule.java)

**Example(s):**

``` java
Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
rst.next();     // what if it returns false? bad form
String firstName = rst.getString(1);

Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
if (rst.next()) {    // result is properly examined and used
    String firstName = rst.getString(1);
    } else  {
        // handle missing data
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CheckResultSet" />
```

## CheckSkipResult

**Since:** PMD 5.0

**Priority:** Medium (3)

The skip() method may skip a smaller number of bytes than requested. Check the returned value to find out if it was the case or not.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.CheckSkipResultRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/CheckSkipResultRule.java)

**Example(s):**

``` java
public class Foo {

   private FileInputStream _s = new FileInputStream("file");

   public void skip(int n) throws IOException {
      _s.skip(n); // You are not sure that exactly n bytes are skipped
   }

   public void skipExactly(int n) throws IOException {
      while (n != 0) {
         long skipped = _s.skip(n);
         if (skipped == 0)
            throw new EOFException();
         n -= skipped;
      }
   }
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CheckSkipResult" />
```

## ClassCastExceptionWithToArray

**Since:** PMD 3.4

**Priority:** Medium (3)

When deriving an array of a specific class from your Collection, one should provide an array of
the same class as the parameter of the toArray() method. Doing otherwise you will will result
in a ClassCastException.

```
//CastExpression[Type/ReferenceType/ClassOrInterfaceType[@Image !=
"Object"]]/PrimaryExpression
[
 PrimaryPrefix/Name[ends-with(@Image, '.toArray')]
 and
 PrimarySuffix/Arguments[count(*) = 0]
and
count(PrimarySuffix) = 1
]
```

**Example(s):**

``` java
Collection c = new ArrayList();
Integer obj = new Integer(1);
c.add(obj);

    // this would trigger the rule (and throw a ClassCastException if executed)
Integer[] a = (Integer [])c.toArray();

   // this is fine and will not trigger the rule
Integer[] b = (Integer [])c.toArray(new Integer[c.size()]);
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ClassCastExceptionWithToArray" />
```

## CloneMethodMustBePublic

**Since:** PMD 5.4.0

**Priority:** Medium (3)

The java Manual says "By convention, classes that implement this interface should override
Object.clone (which is protected) with a public method."

```
//MethodDeclaration[@Public='false']
  [MethodDeclarator/@Image = 'clone']
  [MethodDeclarator/FormalParameters/@ParameterCount = 0]
```

**Example(s):**

``` java
public class Foo implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException { // Violation, must be public
    }
}

public class Foo implements Cloneable {
    @Override
    protected Foo clone() { // Violation, must be public
    }
}

public class Foo implements Cloneable {
    @Override
    public Object clone() // Ok
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CloneMethodMustBePublic" />
```

## CloneMethodMustImplementCloneable

**Since:** PMD 1.9

**Priority:** Medium (3)

The method clone() should only be implemented if the class implements the Cloneable interface with the exception of
a final method that only throws CloneNotSupportedException.

The rule can also detect, if the class implements or extends a Cloneable class.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.clone.CloneMethodMustImplementCloneableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/clone/CloneMethodMustImplementCloneableRule.java)

**Example(s):**

``` java
public class MyClass {
 public Object clone() throws CloneNotSupportedException {
  return foo;
 }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CloneMethodMustImplementCloneable" />
```

## CloneMethodReturnTypeMustMatchClassName

**Since:** PMD 5.4.0

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.5

If a class implements cloneable the return type of the method clone() must be the class name. That way, the caller
of the clone method doesn't need to cast the returned clone to the correct type.

Note: This is only possible with Java 1.5 or higher.

```
//MethodDeclaration
[
MethodDeclarator/@Image = 'clone'
and MethodDeclarator/FormalParameters/@ParameterCount = 0
and not (ResultType//ClassOrInterfaceType/@Image = ancestor::ClassOrInterfaceDeclaration[1]/@Image)
]
```

**Example(s):**

``` java
public class Foo implements Cloneable {
    @Override
    protected Object clone() { // Violation, Object must be Foo
    }
}

public class Foo implements Cloneable {
    @Override
    public Foo clone() { //Ok
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CloneMethodReturnTypeMustMatchClassName" />
```

## CloneThrowsCloneNotSupportedException

**Since:** PMD 1.9

**Priority:** Medium (3)

The method clone() should throw a CloneNotSupportedException.

```
//MethodDeclaration
[
MethodDeclarator/@Image = 'clone'
and count(MethodDeclarator/FormalParameters/*) = 0
and count(NameList/Name[contains
(@Image,'CloneNotSupportedException')]) = 0
]
[
../../../../ClassOrInterfaceDeclaration[@Final = 'false']
]
```

**Example(s):**

``` java
public class MyClass implements Cloneable{
    public Object clone() { // will cause an error
         MyClass clone = (MyClass)super.clone();
         return clone;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/CloneThrowsCloneNotSupportedException" />
```

## CloseResource

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
<rule ref="category/java/errorprone.xml/CloseResource" />
```

## CompareObjectsWithEquals

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
<rule ref="category/java/errorprone.xml/CompareObjectsWithEquals" />
```

## ConfusingTernary

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
<rule ref="category/java/errorprone.xml/ConfusingTernary" />
```

## ConstantsInInterface

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
<rule ref="category/java/errorprone.xml/ConstantsInInterface" />
```

## ConstructorCallsOverridableMethod

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
<rule ref="category/java/errorprone.xml/ConstructorCallsOverridableMethod" />
```

## DataflowAnomalyAnalysis

**Since:** PMD 3.9

**Priority:** Low (5)

The dataflow analysis tracks local definitions, undefinitions and references to variables on different paths on the data flow.
From those informations there can be found various problems.

1. UR - Anomaly: There is a reference to a variable that was not defined before. This is a bug and leads to an error.
2. DU - Anomaly: A recently defined variable is undefined. These anomalies may appear in normal source text.
3. DD - Anomaly: A recently defined variable is redefined. This is ominous but don't have to be a bug.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.controversial.DataflowAnomalyAnalysisRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/controversial/DataflowAnomalyAnalysisRule.java)

**Example(s):**

``` java
public void foo() {
  int buz = 5;
  buz = 6; // redefinition of buz -> dd-anomaly
  foo(buz);
  buz = 2;
} // buz is undefined when leaving scope -> du-anomaly
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maxViolations|100|Maximum number of anomalies per class|
|maxPaths|1000|Maximum number of checked paths per method. A lower value will increase the performance of the rule but may decrease anomalies found.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DataflowAnomalyAnalysis" />
```

## DefaultLabelNotLastInSwitchStmt

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
<rule ref="category/java/errorprone.xml/DefaultLabelNotLastInSwitchStmt" />
```

## DoNotCallSystemExit

**Since:** PMD 4.1

**Priority:** Medium (3)

Web applications should not call System.exit(), since only the web container or the
application server should stop the JVM. This rule also checks for the equivalent call Runtime.getRuntime().exit().

```
//Name[
    starts-with(@Image,'System.exit')
    or
    (starts-with(@Image,'Runtime.getRuntime') and ../../PrimarySuffix[ends-with(@Image,'exit')])
]
```

**Example(s):**

``` java
public void bar() {
    System.exit(0);                 // never call this when running in an application server!
}
public void foo() {
    Runtime.getRuntime().exit(0);   // never stop the JVM manually, the container will do this.
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DoNotCallSystemExit" />
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
<rule ref="category/java/errorprone.xml/DoNotExtendJavaLangError" />
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
<rule ref="category/java/errorprone.xml/DoNotExtendJavaLangThrowable" />
```

## DoNotHardCodeSDCard

**Since:** PMD 4.2.6

**Priority:** Medium (3)

Use Environment.getExternalStorageDirectory() instead of "/sdcard"

```
//Literal[starts-with(@Image,'"/sdcard')]
```

**Example(s):**

``` java
public class MyActivity extends Activity {
    protected void foo() {
        String storageLocation = "/sdcard/mypackage";   // hard-coded, poor approach

       storageLocation = Environment.getExternalStorageDirectory() + "/mypackage"; // preferred approach
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DoNotHardCodeSDCard" />
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
<rule ref="category/java/errorprone.xml/DoNotThrowExceptionInFinally" />
```

## DontImportJavaLang

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Avoid importing anything from the package 'java.lang'.  These classes are automatically imported (JLS 7.5.3).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.DontImportJavaLangRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/DontImportJavaLangRule.java)

**Example(s):**

``` java
import java.lang.String;    // this is unnecessary

public class Foo {}

// --- in another source code file...

import java.lang.*;         // this is bad

public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DontImportJavaLang" />
```

## DontImportSun

**Since:** PMD 1.5

**Priority:** Medium Low (4)

Avoid importing anything from the 'sun.*' packages.  These packages are not portable and are likely to change.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.controversial.DontImportSunRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/controversial/DontImportSunRule.java)

**Example(s):**

``` java
import sun.misc.foo;
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DontImportSun" />
```

## DontUseFloatTypeForLoopIndices

**Since:** PMD 4.3

**Priority:** Medium (3)

Don't use floating point for loop indices. If you must use floating point, use double
unless you're certain that float provides enough precision and you have a compelling
performance need (space or time).

```
//ForStatement/ForInit/LocalVariableDeclaration
/Type/PrimitiveType[@Image="float"]
```

**Example(s):**

``` java
public class Count {
  public static void main(String[] args) {
    final int START = 2000000000;
    int count = 0;
    for (float f = START; f < START + 50; f++)
      count++;
      //Prints 0 because (float) START == (float) (START + 50).
      System.out.println(count);
      //The termination test misbehaves due to floating point granularity.
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DontUseFloatTypeForLoopIndices" />
```

## DuplicateImports

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Duplicate or overlapping import statements should be avoided.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.DuplicateImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/DuplicateImportsRule.java)

**Example(s):**

``` java
import java.lang.String;
import java.lang.*;
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/DuplicateImports" />
```

## EmptyCatchBlock

**Since:** PMD 0.1

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
|allowCommentedBlocks|false|Empty blocks containing comments will be skipped|
|allowExceptionNameRegex|^$|Empty blocks catching exceptions with names matching this regular expression will be skipped|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyCatchBlock" />
```

## EmptyFinalizer

**Since:** PMD 1.5

**Priority:** Medium (3)

Empty finalize methods serve no purpose and should be removed.

```
//MethodDeclaration[MethodDeclarator[@Image='finalize'][not(FormalParameters/*)]]
  /Block[count(*)=0]
```

**Example(s):**

``` java
public class Foo {
   protected void finalize() {}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyFinalizer" />
```

## EmptyFinallyBlock

**Since:** PMD 0.4

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
            int x=2;
        } finally {
            // empty!
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyFinallyBlock" />
```

## EmptyIfStmt

**Since:** PMD 0.1

**Priority:** Medium (3)

Empty If Statement finds instances where a condition is checked but nothing is done about it.

```
//IfStatement/Statement
 [EmptyStatement or Block[count(*) = 0]]
```

**Example(s):**

``` java
public class Foo {
 void bar(int x) {
  if (x == 0) {
   // empty!
  }
 }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyIfStmt" />
```

## EmptyInitializer

**Since:** PMD 5.0

**Priority:** Medium (3)

Empty initializers serve no purpose and should be removed.

```
//Initializer/Block[count(*)=0]
```

**Example(s):**

``` java
public class Foo {

   static {} // Why ?

   {} // Again, why ?

}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyInitializer" />
```

## EmptyStatementBlock

**Since:** PMD 5.0

**Priority:** Medium (3)

Empty block statements serve no purpose and should be removed.

```
//BlockStatement/Statement/Block[count(*) = 0]
```

**Example(s):**

``` java
public class Foo {

   private int _bar;

   public void setBar(int bar) {
      { _bar = bar; } // Why not?
      {} // But remove this.
   }

}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyStatementBlock" />
```

## EmptyStatementNotInLoop

**Since:** PMD 1.5

**Priority:** Medium (3)

An empty statement (or a semicolon by itself) that is not used as the sole body of a 'for' 
or 'while' loop is probably a bug.  It could also be a double semicolon, which has no purpose
and should be removed.

```
//EmptyStatement
 [not(
       ../../../ForStatement
       or ../../../WhileStatement
       or ../../../BlockStatement/ClassOrInterfaceDeclaration
       or ../../../../../../ForStatement/Statement[1]
        /Block[1]/BlockStatement[1]/Statement/EmptyStatement
       or ../../../../../../WhileStatement/Statement[1]
        /Block[1]/BlockStatement[1]/Statement/EmptyStatement)
 ]
```

**Example(s):**

``` java
public void doit() {
      // this is probably not what you meant to do
      ;
      // the extra semicolon here this is not necessary
      System.out.println("look at the extra semicolon");;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyStatementNotInLoop" />
```

## EmptyStaticInitializer

**Since:** PMD 1.5

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
<rule ref="category/java/errorprone.xml/EmptyStaticInitializer" />
```

## EmptySwitchStatements

**Since:** PMD 1.0

**Priority:** Medium (3)

Empty switch statements serve no purpose and should be removed.

```
//SwitchStatement[count(*) = 1]
```

**Example(s):**

``` java
public void bar() {
    int x = 2;
    switch (x) {
        // once there was code here
        // but it's been commented out or something
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptySwitchStatements" />
```

## EmptySynchronizedBlock

**Since:** PMD 1.3

**Priority:** Medium (3)

Empty synchronized blocks serve no purpose and should be removed.

```
//SynchronizedStatement/Block[1][count(*) = 0]
```

**Example(s):**

``` java
public class Foo {
    public void bar() {
        synchronized (this) {
            // empty!
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptySynchronizedBlock" />
```

## EmptyTryBlock

**Since:** PMD 0.4

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyTryBlock" />
```

## EmptyWhileStmt

**Since:** PMD 0.2

**Priority:** Medium (3)

Empty While Statement finds all instances where a while statement does nothing.  
If it is a timing loop, then you should use Thread.sleep() for it; if it is
a while loop that does a lot in the exit expression, rewrite it to make it clearer.

```
//WhileStatement/Statement[./Block[count(*) = 0]  or ./EmptyStatement]
```

**Example(s):**

``` java
void bar(int a, int b) {
    while (a == b) {
        // empty!
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/EmptyWhileStmt" />
```

## EqualsNull

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
<rule ref="category/java/errorprone.xml/EqualsNull" />
```

## ExtendsObject

**Since:** PMD 5.0

**Priority:** Medium Low (4)

No need to explicitly extend Object.

```
//ExtendsList/ClassOrInterfaceType[@Image='Object' or @Image='java.lang.Object']
```

**Example(s):**

``` java
public class Foo extends Object {     // not required
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ExtendsObject" />
```

## FinalizeDoesNotCallSuperFinalize

**Since:** PMD 1.5

**Priority:** Medium (3)

If the finalize() is implemented, its last action should be to call super.finalize.

```
//MethodDeclaration[MethodDeclarator[@Image='finalize'][not(FormalParameters/*)]]
   /Block
      /BlockStatement[last()]
      [not(Statement/StatementExpression/PrimaryExpression
            [./PrimaryPrefix[@SuperModifier='true']]
            [./PrimarySuffix[@Image='finalize']]
          )
      ]
      [not(Statement/TryStatement/FinallyStatement
       /Block/BlockStatement/Statement/StatementExpression/PrimaryExpression
            [./PrimaryPrefix[@SuperModifier='true']]
            [./PrimarySuffix[@Image='finalize']]
          )
      ]
```

**Example(s):**

``` java
protected void finalize() {
    something();
    // neglected to call super.finalize()
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/FinalizeDoesNotCallSuperFinalize" />
```

## FinalizeOnlyCallsSuperFinalize

**Since:** PMD 1.5

**Priority:** Medium (3)

If the finalize() is implemented, it should do something besides just calling super.finalize().

```
//MethodDeclaration[MethodDeclarator[@Image="finalize"][not(FormalParameters/*)]]
   /Block[count(BlockStatement)=1]
     /BlockStatement[
       Statement/StatementExpression/PrimaryExpression
       [./PrimaryPrefix[@SuperModifier='true']]
       [./PrimarySuffix[@Image='finalize']]
     ]
```

**Example(s):**

``` java
protected void finalize() {
    super.finalize();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/FinalizeOnlyCallsSuperFinalize" />
```

## FinalizeOverloaded

**Since:** PMD 1.5

**Priority:** Medium (3)

Methods named finalize() should not have parameters.  It is confusing and most likely an attempt to
overload Object.finalize(). It will not be called by the VM.

```
//MethodDeclaration
 /MethodDeclarator[@Image='finalize'][FormalParameters[count(*)>0]]
```

**Example(s):**

``` java
public class Foo {
    // this is confusing and probably a bug
    protected void finalize(int a) {
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/FinalizeOverloaded" />
```

## FinalizeShouldBeProtected

**Since:** PMD 1.1

**Priority:** Medium (3)

When overriding the finalize(), the new method should be set as protected.  If made public, 
other classes may invoke it at inappropriate times.

```
//MethodDeclaration[@Protected="false"]
  /MethodDeclarator[@Image="finalize"]
  [not(FormalParameters/*)]
```

**Example(s):**

``` java
public void finalize() {
    // do something
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/FinalizeShouldBeProtected" />
```

## ForLoopCanBeForeach

**Since:** PMD 6.0

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.5

Reports loops that can be safely replaced with the foreach syntax. The rule considers loops over
lists, arrays and iterators. A loop is safe to replace if it only uses the index variable to
access an element of the list or array, only has one update statement, and loops through *every*
element of the list or array left to right.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.migrating.ForLoopCanBeForeachRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/migrating/ForLoopCanBeForeachRule.java)

**Example(s):**

``` java
public class MyClass {
  void loop(List<String> l) {
    for (int i = 0; i < l.size(); i++) { // pre Java 1.5
      System.out.println(l.get(i));
    }

    for (String s : l) {        // post Java 1.5
      System.out.println(s);
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ForLoopCanBeForeach" />
```

## ForLoopShouldBeWhileLoop

**Since:** PMD 1.02

**Priority:** Medium (3)

Some for loops can be simplified to while loops, this makes them more concise.

```
//ForStatement
  [count(*) > 1]
  [not(LocalVariableDeclaration)]
  [not(ForInit)]
  [not(ForUpdate)]
  [not(Type and Expression and Statement)]
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        for (;true;) true; // No Init or Update part, may as well be: while (true)
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ForLoopShouldBeWhileLoop" />
```

## IdempotentOperations

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
<rule ref="category/java/errorprone.xml/IdempotentOperations" />
```

## ImportFromSamePackage

**Since:** PMD 1.02

**Priority:** Medium (3)

There is no need to import a type that lives in the same package.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.ImportFromSamePackageRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/ImportFromSamePackageRule.java)

**Example(s):**

``` java
package foo;

import foo.Buz;     // no need for this
import foo.*;       // or this

public class Bar{}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ImportFromSamePackage" />
```

## InstantiationToGetClass

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
<rule ref="category/java/errorprone.xml/InstantiationToGetClass" />
```

## InvalidSlf4jMessageFormat

**Since:** PMD 5.5.0

**Priority:** Low (5)

Check for messages in slf4j loggers with non matching number of arguments and placeholders.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.InvalidSlf4jMessageFormatRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/InvalidSlf4jMessageFormatRule.java)

**Example(s):**

``` java
LOGGER.error("forget the arg {}");
LOGGER.error("too many args {}", "arg1", "arg2");
LOGGER.error("param {}", "arg1", new IllegalStateException("arg")); //The exception is shown separately, so is correct.
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/InvalidSlf4jMessageFormat" />
```

## JumbledIncrementer

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

```
//ForStatement
  [
    ForUpdate/StatementExpressionList/StatementExpression/PostfixExpression/PrimaryExpression/PrimaryPrefix/Name/@Image
    =
    ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image
  ]
```

**Example(s):**

``` java
public class JumbledIncrementerRule1 {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/JumbledIncrementer" />
```

## JUnitSpelling

**Since:** PMD 1.0

**Priority:** Medium (3)

Some JUnit framework methods are easy to misspell.

```
//MethodDeclarator[(not(@Image = 'setUp')
 and translate(@Image, 'SETuP', 'setUp') = 'setUp')
 or (not(@Image = 'tearDown')
 and translate(@Image, 'TEARdOWN', 'tearDown') = 'tearDown')]
 [FormalParameters[count(*) = 0]]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
import junit.framework.*;

public class Foo extends TestCase {
    public void setup() {}    // oops, should be setUp
    public void TearDown() {} // oops, should be tearDown
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/JUnitSpelling" />
```

## JUnitStaticSuite

**Since:** PMD 1.0

**Priority:** Medium (3)

The suite() method in a JUnit test needs to be both public and static.

```
//MethodDeclaration[not(@Static='true') or not(@Public='true')]
[MethodDeclarator/@Image='suite']
[MethodDeclarator/FormalParameters/@ParameterCount=0]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
import junit.framework.*;

public class Foo extends TestCase {
    public void suite() {}         // oops, should be static
    private static void suite() {} // oops, should be public
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/JUnitStaticSuite" />
```

## LoggerIsNotStaticFinal

**Since:** PMD 2.0

**Priority:** Medium High (2)

In most cases, the Logger reference can be declared as static and final.

```
//VariableDeclarator
 [parent::FieldDeclaration]
 [../Type/ReferenceType
  /ClassOrInterfaceType[@Image='Logger']
   and
  (..[@Final='false'] or ..[@Static = 'false'] ) ]
```

**Example(s):**

``` java
public class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());                 // not recommended

    static final Logger log = Logger.getLogger(Foo.class.getName());    // preferred approach
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/LoggerIsNotStaticFinal" />
```

## MethodWithSameNameAsEnclosingClass

**Since:** PMD 1.5

**Priority:** Medium (3)

Non-constructor methods should not have the same name as the enclosing class.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.MethodWithSameNameAsEnclosingClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/MethodWithSameNameAsEnclosingClassRule.java)

**Example(s):**

``` java
public class MyClass {

    public MyClass() {}         // this is OK because it is a constructor

    public void MyClass() {}    // this is bad because it is a method
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/MethodWithSameNameAsEnclosingClass" />
```

## MisplacedNullCheck

**Since:** PMD 3.5

**Priority:** Medium (3)

The null check here is misplaced. If the variable is null a NullPointerException will be thrown.
Either the check is useless (the variable will never be "null") or it is incorrect.

```
//Expression
    /*[self::ConditionalOrExpression or self::ConditionalAndExpression]
    /descendant::PrimaryExpression/PrimaryPrefix
    /Name[starts-with(@Image,
        concat(ancestor::PrimaryExpression/following-sibling::EqualityExpression
            [./PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]
            /PrimaryExpression/PrimaryPrefix
            /Name[count(../../PrimarySuffix)=0]/@Image,".")
        )
     ]
     [count(ancestor::ConditionalAndExpression/EqualityExpression
            [@Image='!=']
            [./PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]
            [starts-with(following-sibling::*/PrimaryExpression/PrimaryPrefix/Name/@Image,
                concat(./PrimaryExpression/PrimaryPrefix/Name/@Image, '.'))]
      ) = 0
     ]
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        if (a.equals(baz) && a != null) {}
        }
}
```

``` java
public class Foo {
    void bar() {
        if (a.equals(baz) || a == null) {}
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/MisplacedNullCheck" />
```

## MissingBreakInSwitch

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
<rule ref="category/java/errorprone.xml/MissingBreakInSwitch" />
```

## MissingSerialVersionUID

**Since:** PMD 3.0

**Priority:** Medium (3)

Serializable classes should provide a serialVersionUID field.

```
//ClassOrInterfaceDeclaration
 [
  count(ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
   /FieldDeclaration/VariableDeclarator/VariableDeclaratorId[@Image='serialVersionUID']) = 0
and
  count(ImplementsList
   [ClassOrInterfaceType/@Image='Serializable'
   or ClassOrInterfaceType/@Image='java.io.Serializable']) =1
and
   @Abstract = 'false'
]
```

**Example(s):**

``` java
public class Foo implements java.io.Serializable {
    String name;
    // Define serialization id to avoid serialization related bugs
    // i.e., public static final long serialVersionUID = 4328743;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/MissingSerialVersionUID" />
```

## MissingStaticMethodInNonInstantiatableClass

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
<rule ref="category/java/errorprone.xml/MissingStaticMethodInNonInstantiatableClass" />
```

## MoreThanOneLogger

**Since:** PMD 2.0

**Priority:** Medium High (2)

Normally only one logger is used in each class.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.MoreThanOneLoggerRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/MoreThanOneLoggerRule.java)

**Example(s):**

``` java
public class Foo {
    Logger log = Logger.getLogger(Foo.class.getName());
    // It is very rare to see two loggers on a class, normally
    // log information is multiplexed by levels
    Logger log2= Logger.getLogger(Foo.class.getName());
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/MoreThanOneLogger" />
```

## NonCaseLabelInSwitchStatement

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
<rule ref="category/java/errorprone.xml/NonCaseLabelInSwitchStatement" />
```

## NonStaticInitializer

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
<rule ref="category/java/errorprone.xml/NonStaticInitializer" />
```

## OneDeclarationPerLine

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Java allows the use of several variables declaration of the same type on one line. However, it
can lead to quite messy code. This rule looks for several declarations on the same line.

```
//LocalVariableDeclaration
   [count(VariableDeclarator) > 1]
   [$strictMode or count(distinct-values(VariableDeclarator/@BeginLine)) != count(VariableDeclarator)]
```

**Example(s):**

``` java
String name;            // separate declarations
String lastname;

String name, lastname;  // combined declaration, a violation

String name,
       lastname;        // combined declaration on multiple lines, no violation by default.
                        // Set property strictMode to true to mark this as violation.
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strictMode|false|If true, mark combined declaration even if the declarations are on separate lines.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/OneDeclarationPerLine" />
```

## OverrideBothEqualsAndHashcode

**Since:** PMD 0.4

**Priority:** Medium (3)

Override both public boolean Object.equals(Object other), and public int Object.hashCode(), or override neither.  Even if you are inheriting a hashCode() from a parent class, consider implementing hashCode and explicitly delegating to your superclass.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.OverrideBothEqualsAndHashcodeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/OverrideBothEqualsAndHashcodeRule.java)

**Example(s):**

``` java
public class Bar {        // poor, missing a hashcode() method
    public boolean equals(Object o) {
      // do some comparison
    }
}

public class Baz {        // poor, missing an equals() method
    public int hashCode() {
      // return some hash value
    }
}

public class Foo {        // perfect, both methods provided
    public boolean equals(Object other) {
      // do some comparison
    }
    public int hashCode() {
      // return some hash value
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/OverrideBothEqualsAndHashcode" />
```

## PreserveStackTrace

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
<rule ref="category/java/errorprone.xml/PreserveStackTrace" />
```

## ProperCloneImplementation

**Since:** PMD 1.4

**Priority:** Medium High (2)

Object clone() should be implemented with super.clone().

```
//MethodDeclarator
[@Image = 'clone']
[count(FormalParameters/*) = 0]
[count(../Block//*[
    (self::AllocationExpression) and
    (./ClassOrInterfaceType/@Image = ancestor::
ClassOrInterfaceDeclaration[1]/@Image)
  ])> 0
]
```

**Example(s):**

``` java
class Foo{
    public Object clone(){
        return new Foo(); // This is bad
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ProperCloneImplementation" />
```

## ProperLogger

**Since:** PMD 3.3

**Priority:** Medium (3)

A logger should normally be defined private static final and be associated with the correct class.
Private final Log log; is also allowed for rare cases where loggers need to be passed around,
with the restriction that the logger needs to be passed into the constructor.

```
//ClassOrInterfaceBodyDeclaration[FieldDeclaration//ClassOrInterfaceType[@Image='Log']
 and
 not(FieldDeclaration[@Final='true'][@Static='true'][@Private='true'][.//VariableDeclaratorId[@Image=$staticLoggerName]]
 //ArgumentList//ClassOrInterfaceType/@Image = ancestor::ClassOrInterfaceDeclaration/@Image)
 and
 not(FieldDeclaration[@Final='true'][@Private='true'][.//VariableDeclaratorId[@Image='log']]
 [count(.//VariableInitializer)=0]
 [ancestor::ClassOrInterfaceBody//StatementExpression[.//PrimaryExpression/descendant::*[@Image='log']][count(.//AllocationExpression)=0]]
 )]
```

**Example(s):**

``` java
public class Foo {

    private static final Log LOG = LogFactory.getLog(Foo.class);    // proper way

    protected Log LOG = LogFactory.getLog(Testclass.class);         // wrong approach
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|staticLoggerName|LOG|Name of the static Logger variable|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ProperLogger" />
```

## ReturnEmptyArrayRatherThanNull

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
<rule ref="category/java/errorprone.xml/ReturnEmptyArrayRatherThanNull" />
```

## ReturnFromFinallyBlock

**Since:** PMD 1.05

**Priority:** Medium (3)

Avoid returning from a finally block, this can discard exceptions.

```
//FinallyStatement//ReturnStatement
```

**Example(s):**

``` java
public class Bar {
    public String foo() {
        try {
            throw new Exception( "My Exception" );
        } catch (Exception e) {
            throw e;
        } finally {
            return "A. O. K."; // return not recommended here
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/ReturnFromFinallyBlock" />
```

## SimpleDateFormatNeedsLocale

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
<rule ref="category/java/errorprone.xml/SimpleDateFormatNeedsLocale" />
```

## SingleMethodSingleton

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
<rule ref="category/java/errorprone.xml/SingleMethodSingleton" />
```

## SingletonClassReturningNewInstance

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
<rule ref="category/java/errorprone.xml/SingletonClassReturningNewInstance" />
```

## StaticEJBFieldShouldBeFinal

**Since:** PMD 4.1

**Priority:** Medium (3)

According to the J2EE specification, an EJB should not have any static fields
with write access. However, static read-only fields are allowed. This ensures proper
behavior especially when instances are distributed by the container on several JREs.

```
//ClassOrInterfaceDeclaration[
    (
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'SessionBean')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBHome')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalObject')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalHome')])
    or
    (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBObject')])
    )
    and
    (./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration[
         (./FieldDeclaration[@Static = 'true'])
         and
         (./FieldDeclaration[@Final = 'false'])
    ])
]
```

**Example(s):**

``` java
public class SomeEJB extends EJBObject implements EJBLocalHome {

    private static int CountA;          // poor, field can be edited

    private static final int CountB;    // preferred, read-only access
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/StaticEJBFieldShouldBeFinal" />
```

## StringBufferInstantiationWithChar

**Since:** PMD 3.9

**Priority:** Medium Low (4)

Individual character values provided as initialization arguments will be converted into integers.
This can lead to internal buffer sizes that are larger than expected. Some examples:

```
new StringBuffer()      //  16
new StringBuffer(6)     //  6
new StringBuffer("hello world")  // 11 + 16 = 27
new StringBuffer('A')   //  chr(A) = 65
new StringBuffer("A")   //  1 + 16 = 17 

new StringBuilder()     //  16
new StringBuilder(6)    //  6
new StringBuilder("hello world")  // 11 + 16 = 27
new StringBuilder('C')   //  chr(C) = 67
new StringBuilder("A")   //  1 + 16 = 17
```

```
//AllocationExpression/ClassOrInterfaceType
[@Image='StringBuffer' or @Image='StringBuilder']
/../Arguments/ArgumentList/Expression/PrimaryExpression
/PrimaryPrefix/
Literal
  [starts-with(@Image, "'")]
  [ends-with(@Image, "'")]
```

**Example(s):**

``` java
// misleading instantiation, these buffers
// are actually sized to 99 characters long
StringBuffer  sb1 = new StringBuffer('c');
StringBuilder sb2 = new StringBuilder('c');

// in these forms, just single characters are allocated
StringBuffer  sb3 = new StringBuffer("c");
StringBuilder sb4 = new StringBuilder("c");
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/StringBufferInstantiationWithChar" />
```

## SuspiciousConstantFieldName

**Since:** PMD 2.0

**Priority:** Medium (3)

Field names using all uppercase characters - Sun's Java naming conventions indicating constants - should
be declared as final.

```
//ClassOrInterfaceDeclaration[@Interface='false']
 /ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/FieldDeclaration
  [@Final='false']
  [VariableDeclarator/VariableDeclaratorId[upper-case(@Image)=@Image]]
```

**Example(s):**

``` java
public class Foo {
 // this is bad, since someone could accidentally
 // do PI = 2.71828; which is actually e
 // final double PI = 3.16; is ok
  double PI = 3.16;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/SuspiciousConstantFieldName" />
```

## SuspiciousEqualsMethodName

**Since:** PMD 2.0

**Priority:** Medium High (2)

The method name and parameter number are suspiciously close to equals(Object), which can denote an
intention to override the equals(Object) method.

```
//MethodDeclarator[@Image = 'equals']
[   
    (count(FormalParameters/*) = 1
    and not (FormalParameters/FormalParameter/Type/ReferenceType/ClassOrInterfaceType
        [@Image = 'Object' or @Image = 'java.lang.Object'])
    or not (../ResultType/Type/PrimitiveType[@Image = 'boolean'])
    )  or  (
    count(FormalParameters/*) = 2
    and ../ResultType/Type/PrimitiveType[@Image = 'boolean']
    and FormalParameters//ClassOrInterfaceType[@Image = 'Object' or @Image = 'java.lang.Object']
    and not(../../Annotation/MarkerAnnotation/Name[@Image='Override'])
    )
]
| //MethodDeclarator[@Image = 'equal']
[
    count(FormalParameters/*) = 1
    and FormalParameters/FormalParameter/Type/ReferenceType/ClassOrInterfaceType
        [@Image = 'Object' or @Image = 'java.lang.Object']
]
```

**Example(s):**

``` java
public class Foo {
   public int equals(Object o) {
     // oops, this probably was supposed to be boolean equals
   }
   public boolean equals(String s) {
     // oops, this probably was supposed to be equals(Object)
   }
   public boolean equals(Object o1, Object o2) {
     // oops, this probably was supposed to be equals(Object)
   }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/SuspiciousEqualsMethodName" />
```

## SuspiciousHashcodeMethodName

**Since:** PMD 1.5

**Priority:** Medium (3)

The method name and return type are suspiciously close to hashCode(), which may denote an intention
to override the hashCode() method.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.SuspiciousHashcodeMethodNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/SuspiciousHashcodeMethodNameRule.java)

**Example(s):**

``` java
public class Foo {
    public int hashcode() { // oops, this probably was supposed to be 'hashCode'
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/SuspiciousHashcodeMethodName" />
```

## SuspiciousOctalEscape

**Since:** PMD 1.5

**Priority:** Medium (3)

A suspicious octal escape sequence was found inside a String literal.
The Java language specification (section 3.10.6) says an octal
escape sequence inside a literal String shall consist of a backslash
followed by:

    OctalDigit | OctalDigit OctalDigit | ZeroToThree OctalDigit OctalDigit

Any octal escape sequence followed by non-octal digits can be confusing,
e.g. "\038" is interpreted as the octal escape sequence "\03" followed by
the literal character "8".

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.controversial.SuspiciousOctalEscapeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/controversial/SuspiciousOctalEscapeRule.java)

**Example(s):**

``` java
public void foo() {
  // interpreted as octal 12, followed by character '8'
  System.out.println("suspicious: \128");
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/SuspiciousOctalEscape" />
```

## TestClassWithoutTestCases

**Since:** PMD 3.0

**Priority:** Medium (3)

Test classes end with the suffix Test. Having a non-test class with that name is not a good practice, 
since most people will assume it is a test case. Test classes have test methods named testXXX.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.TestClassWithoutTestCasesRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/TestClassWithoutTestCasesRule.java)

**Example(s):**

``` java
//Consider changing the name of the class if it is not a test
//Consider adding test methods if it is a test
public class CarTest {
   public static void main(String[] args) {
    // do something
   }
   // code
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/TestClassWithoutTestCases" />
```

## UnconditionalIfStatement

**Since:** PMD 1.5

**Priority:** Medium (3)

Do not use "if" statements whose conditionals are always true or always false.

```
//IfStatement/Expression
 [count(PrimaryExpression)=1]
 /PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral
```

**Example(s):**

``` java
public class Foo {
    public void close() {
        if (true) {        // fixed conditional, not recommended
            // ...
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnconditionalIfStatement" />
```

## UnnecessaryBooleanAssertion

**Since:** PMD 3.0

**Priority:** Medium (3)

A JUnit test assertion with a boolean literal is unnecessary since it always will evaluate to the same thing.
Consider using flow control (in case of assertTrue(false) or similar) or simply removing
statements like assertTrue(true) and assertFalse(false).  If you just want a test to halt after finding
an error, use the fail() method and provide an indication message of why it did.

```
//StatementExpression
[
PrimaryExpression/PrimaryPrefix/Name[@Image='assertTrue' or  @Image='assertFalse']
and
PrimaryExpression/PrimarySuffix/Arguments/ArgumentList/Expression
[PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral
or
UnaryExpressionNotPlusMinus[@Image='!']
/PrimaryExpression/PrimaryPrefix[Literal/BooleanLiteral or Name[count(../../*)=1]]]
]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
public class SimpleTest extends TestCase {
    public void testX() {
        assertTrue(true);       // serves no real purpose
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnnecessaryBooleanAssertion" />
```

## UnnecessaryCaseChange

**Since:** PMD 3.3

**Priority:** Medium (3)

Using equalsIgnoreCase() is faster than using toUpperCase/toLowerCase().equals()

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.strings.UnnecessaryCaseChangeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/strings/UnnecessaryCaseChangeRule.java)

**Example(s):**

``` java
boolean answer1 = buz.toUpperCase().equals("baz");              // should be buz.equalsIgnoreCase("baz")

boolean answer2 = buz.toUpperCase().equalsIgnoreCase("baz");    // another unnecessary toUpperCase()
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnnecessaryCaseChange" />
```

## UnnecessaryConversionTemporary

**Since:** PMD 0.1

**Priority:** Medium (3)

Avoid the use temporary objects when converting primitives to Strings. Use the static conversion methods
on the wrapper classes instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UnnecessaryConversionTemporaryRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UnnecessaryConversionTemporaryRule.java)

**Example(s):**

``` java
public String convert(int x) {
    String foo = new Integer(x).toString(); // this wastes an object

    return Integer.toString(x);             // preferred approach
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnnecessaryConversionTemporary" />
```

## UnnecessaryFullyQualifiedName

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Import statements allow the use of non-fully qualified names.  The use of a fully qualified name
which is covered by an import statement is redundant.  Consider using the non-fully qualified name.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.UnnecessaryFullyQualifiedNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/UnnecessaryFullyQualifiedNameRule.java)

**Example(s):**

``` java
import java.util.List;

public class Foo {
    private java.util.List list1;   // Unnecessary FQN
    private List list2;             // More appropriate given import of 'java.util.List'
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnnecessaryFullyQualifiedName" />
```

## UnnecessaryReturn

**Since:** PMD 1.3

**Priority:** Medium (3)

Avoid the use of unnecessary return statements.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UnnecessaryReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UnnecessaryReturnRule.java)

**Example(s):**

``` java
public class Foo {
    public void bar() {
        int x = 42;
        return;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnnecessaryReturn" />
```

## UnusedFormalParameter

**Since:** PMD 0.8

**Priority:** Medium (3)

Avoid passing parameters to methods or constructors without actually referencing them in the method body.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedFormalParameterRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedFormalParameterRule.java)

**Example(s):**

``` java
public class Foo {
    private void bar(String howdy) {
        // howdy is not used
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkAll|false|Check all methods, including non-private ones|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedFormalParameter" />
```

## UnusedImports

**Since:** PMD 1.0

**Priority:** Medium Low (4)

Avoid unused import statements to prevent unwanted dependencies.
This rule will also find unused on demand imports, i.e. import com.foo.*.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.UnusedImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/UnusedImportsRule.java)

**Example(s):**

``` java
import java.io.File;  // not referenced or required
import java.util.*;   // not referenced or required

public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedImports" />
```

## UnusedLocalVariable

**Since:** PMD 0.1

**Priority:** Medium (3)

Detects when a local variable is declared and/or assigned, but not used.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedLocalVariableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedLocalVariableRule.java)

**Example(s):**

``` java
public class Foo {
    public void doSomething() {
        int i = 5; // Unused
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedLocalVariable" />
```

## UnusedNullCheckInEquals

**Since:** PMD 3.5

**Priority:** Medium (3)

After checking an object reference for null, you should invoke equals() on that object rather than passing it to another object's equals() method.

```
(//PrimaryPrefix[ends-with(Name/@Image, '.equals') and Name/@Image != 'Arrays.equals'] | //PrimarySuffix[@Image='equals' and not(../PrimaryPrefix/Literal)])
 /following-sibling::PrimarySuffix/Arguments/ArgumentList/Expression
 /PrimaryExpression[count(PrimarySuffix)=0]/PrimaryPrefix
 /Name[@Image = ./../../../../../../../../../../Expression/ConditionalAndExpression
 /EqualityExpression[@Image="!=" and count(./preceding-sibling::*)=0 and
 ./PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]
  /PrimaryExpression/PrimaryPrefix/Name/@Image]
```

**Example(s):**

``` java
public class Test {

    public String method1() { return "ok";}
    public String method2() { return null;}

    public void method(String a) {
        String b;
        // I don't know it method1() can be "null"
        // but I know "a" is not null..
        // I'd better write a.equals(method1())

        if (a!=null && method1().equals(a)) { // will trigger the rule
            //whatever
        }

        if (method1().equals(a) && a != null) { // won't trigger the rule
            //whatever
        }

        if (a!=null && method1().equals(b)) { // won't trigger the rule
            //whatever
        }

        if (a!=null && "LITERAL".equals(a)) { // won't trigger the rule
            //whatever
        }

        if (a!=null && !a.equals("go")) { // won't trigger the rule
            a=method2();
            if (method1().equals(a)) {
                //whatever
            }
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedNullCheckInEquals" />
```

## UnusedPrivateField

**Since:** PMD 0.1

**Priority:** Medium (3)

Detects when a private field is declared and/or assigned a value, but not used.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedPrivateFieldRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedPrivateFieldRule.java)

**Example(s):**

``` java
public class Something {
    private static int FOO = 2; // Unused
    private int i = 5; // Unused
    private int j = 6;
    public int addOne() {
        return j++;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedPrivateField" />
```

## UnusedPrivateMethod

**Since:** PMD 0.7

**Priority:** Medium (3)

Unused Private Method detects when a private method is declared but is unused.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unusedcode.UnusedPrivateMethodRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unusedcode/UnusedPrivateMethodRule.java)

**Example(s):**

``` java
public class Something {
    private void foo() {} // unused
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UnusedPrivateMethod" />
```

## UseAssertEqualsInsteadOfAssertTrue

**Since:** PMD 3.1

**Priority:** Medium (3)

This rule detects JUnit assertions in object equality. These assertions should be made by more specific methods, like assertEquals.

```
//PrimaryExpression[
    PrimaryPrefix/Name[@Image = 'assertTrue']
][
    PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Name
    [ends-with(@Image, '.equals')]
]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
public class FooTest extends TestCase {
    void testCode() {
        Object a, b;
        assertTrue(a.equals(b));                    // bad usage
        assertEquals(?a should equals b?, a, b);    // good usage
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseAssertEqualsInsteadOfAssertTrue" />
```

## UseAssertNullInsteadOfAssertTrue

**Since:** PMD 3.5

**Priority:** Medium (3)

This rule detects JUnit assertions in object references equality. These assertions should be made by 
more specific methods, like assertNull, assertNotNull.

```
//PrimaryExpression[
 PrimaryPrefix/Name[@Image = 'assertTrue' or @Image = 'assertFalse']
][
 PrimarySuffix/Arguments/ArgumentList[
  Expression/EqualityExpression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral
 ]
]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
public class FooTest extends TestCase {
    void testCode() {
        Object a = doSomething();
        assertTrue(a==null);    // bad usage
        assertNull(a);          // good usage
        assertTrue(a != null);  // bad usage
        assertNotNull(a);       // good usage
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseAssertNullInsteadOfAssertTrue" />
```

## UseAssertSameInsteadOfAssertTrue

**Since:** PMD 3.1

**Priority:** Medium (3)

This rule detects JUnit assertions in object references equality. These assertions should be made 
by more specific methods, like assertSame, assertNotSame.

```
//PrimaryExpression[
    PrimaryPrefix/Name
     [@Image = 'assertTrue' or @Image = 'assertFalse']
]
[PrimarySuffix/Arguments
 /ArgumentList/Expression
 /EqualityExpression[count(.//NullLiteral) = 0]]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

``` java
public class FooTest extends TestCase {
    void testCode() {
        Object a, b;
        assertTrue(a == b); // bad usage
        assertSame(a, b);   // good usage
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseAssertSameInsteadOfAssertTrue" />
```

## UseAssertTrueInsteadOfAssertEquals

**Since:** PMD 5.0

**Priority:** Medium (3)

When asserting a value is the same as a literal or Boxed boolean, use assertTrue/assertFalse, instead of assertEquals.

```
//PrimaryExpression[PrimaryPrefix/Name[@Image = 'assertEquals']]
[
  PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral
  or
  PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix
  /Name[(@Image = 'Boolean.TRUE' or @Image = 'Boolean.FALSE')]
]
```

**Example(s):**

``` java
public class MyTestCase extends TestCase {
    public void testMyCase() {
        boolean myVar = true;
        // Ok
        assertTrue("myVar is true", myVar);
        // Bad
        assertEquals("myVar is true", true, myVar);
        // Bad
        assertEquals("myVar is false", false, myVar);
        // Bad
        assertEquals("myVar is true", Boolean.TRUE, myVar);
        // Bad
        assertEquals("myVar is false", Boolean.FALSE, myVar);
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseAssertTrueInsteadOfAssertEquals" />
```

## UseCollectionIsEmpty

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
<rule ref="category/java/errorprone.xml/UseCollectionIsEmpty" />
```

## UseCorrectExceptionLogging

**Since:** PMD 3.2

**Priority:** Medium (3)

To make sure the full stacktrace is printed out, use the logging statement with two arguments: a String and a Throwable.

```
//CatchStatement/Block/BlockStatement/Statement/StatementExpression
/PrimaryExpression[PrimaryPrefix/Name[starts-with(@Image,
concat(ancestor::ClassOrInterfaceDeclaration/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/FieldDeclaration
[Type//ClassOrInterfaceType[@Image='Log']]
/VariableDeclarator/VariableDeclaratorId/@Image, '.'))]]
[PrimarySuffix/Arguments[@ArgumentCount='1']]
[PrimarySuffix/Arguments//Name/@Image = ancestor::CatchStatement/FormalParameter/VariableDeclaratorId/@Image]
```

**Example(s):**

``` java
public class Main {
    private static final Log _LOG = LogFactory.getLog( Main.class );
    void bar() {
        try {
        } catch( Exception e ) {
            _LOG.error( e ); //Wrong!
        } catch( OtherException oe ) {
            _LOG.error( oe.getMessage(), oe ); //Correct
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseCorrectExceptionLogging" />
```

## UseEqualsToCompareStrings

**Since:** PMD 4.1

**Priority:** Medium (3)

Using '==' or '!=' to compare strings only works if intern version is used on both sides.
Use the equals() method instead.

```
//EqualityExpression/PrimaryExpression
[(PrimaryPrefix/Literal
   [starts-with(@Image, '"')]
   [ends-with(@Image, '"')]
and count(PrimarySuffix) = 0)]
```

**Example(s):**

``` java
public boolean test(String s) {
    if (s == "one") return true;        // unreliable
    if ("two".equals(s)) return true;   // better
    return false;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseEqualsToCompareStrings" />
```

## UselessOperationOnImmutable

**Since:** PMD 3.5

**Priority:** Medium (3)

An operation on an Immutable object (String, BigDecimal or BigInteger) won't change the object itself
since the result of the operation is a new object. Therefore, ignoring the operation result is an error.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UselessOperationOnImmutableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UselessOperationOnImmutableRule.java)

**Example(s):**

``` java
import java.math.*;

class Test {
    void method1() {
        BigDecimal bd=new BigDecimal(10);
        bd.add(new BigDecimal(5));      // this will trigger the rule
    }
    void method2() {
        BigDecimal bd=new BigDecimal(10);
        bd = bd.add(new BigDecimal(5)); // this won't trigger the rule
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UselessOperationOnImmutable" />
```

## UseLocaleWithCaseConversions

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
<rule ref="category/java/errorprone.xml/UseLocaleWithCaseConversions" />
```

## UseProperClassLoader

**Since:** PMD 3.7

**Priority:** Medium (3)

In J2EE, the getClassLoader() method might not work as expected. Use 
Thread.currentThread().getContextClassLoader() instead.

```
//PrimarySuffix[@Image='getClassLoader']
```

**Example(s):**

``` java
public class Foo {
    ClassLoader cl = Bar.class.getClassLoader();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/errorprone.xml/UseProperClassLoader" />
```

