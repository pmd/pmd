---
title: Design
summary: The Design ruleset contains rules that flag suboptimal code implementations. Alternate approaches are suggested.
permalink: pmd_rules_java_design.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/design.xml
keywords: Design, UseUtilityClass, SimplifyBooleanReturns, SimplifyBooleanExpressions, SwitchStmtsShouldHaveDefault, AvoidDeeplyNestedIfStmts, AvoidReassigningParameters, SwitchDensity, ConstructorCallsOverridableMethod, AccessorClassGeneration, FinalFieldCouldBeStatic, CloseResource, NonStaticInitializer, DefaultLabelNotLastInSwitchStmt, NonCaseLabelInSwitchStatement, OptimizableToArrayCall, BadComparison, EqualsNull, ConfusingTernary, InstantiationToGetClass, IdempotentOperations, SimpleDateFormatNeedsLocale, ImmutableField, UseLocaleWithCaseConversions, AvoidProtectedFieldInFinalClass, AssignmentToNonFinalStatic, MissingStaticMethodInNonInstantiatableClass, AvoidSynchronizedAtMethodLevel, MissingBreakInSwitch, UseNotifyAllInsteadOfNotify, AvoidInstanceofChecksInCatchClause, AbstractClassWithoutAbstractMethod, SimplifyConditional, CompareObjectsWithEquals, PositionLiteralsFirstInComparisons, PositionLiteralsFirstInCaseInsensitiveComparisons, UnnecessaryLocalBeforeReturn, NonThreadSafeSingleton, SingleMethodSingleton, SingletonClassReturningNewInstance, UncommentedEmptyMethodBody, UncommentedEmptyConstructor, UnsynchronizedStaticDateFormatter, PreserveStackTrace, UseCollectionIsEmpty, ClassWithOnlyPrivateConstructorsShouldBeFinal, EmptyMethodInAbstractClassShouldBeAbstract, SingularField, ReturnEmptyArrayRatherThanNull, AbstractClassWithoutAnyMethod, TooFewBranchesForASwitchStatement, LogicInversion, UseVarargs, FieldDeclarationsShouldBeAtStartOfClass, GodClass, AvoidProtectedMethodInFinalClassNotExtending, ConstantsInInterface, AccessorMethodGeneration
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

## AvoidReassigningParameters

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

## AvoidSynchronizedAtMethodLevel

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

## EmptyMethodInAbstractClassShouldBeAbstract

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

## FieldDeclarationsShouldBeAtStartOfClass

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

## NonThreadSafeSingleton

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

## OptimizableToArrayCall

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

## PositionLiteralsFirstInCaseInsensitiveComparisons

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

## PositionLiteralsFirstInComparisons

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

## SwitchStmtsShouldHaveDefault

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

## UncommentedEmptyConstructor

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

## UncommentedEmptyMethodBody

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

## UnnecessaryLocalBeforeReturn

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

## UnsynchronizedStaticDateFormatter

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

## UseNotifyAllInsteadOfNotify

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

## UseVarargs

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

