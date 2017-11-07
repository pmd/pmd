---
title: Code Style
summary: Rules which enforce a specific coding style.
permalink: pmd_rules_java_codestyle.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/category/java/codestyle.xml
keywords: Code Style, AbstractNaming, AtLeastOneConstructor, AvoidDollarSigns, AvoidFinalLocalVariable, AvoidPrefixingMethodParameters, AvoidProtectedFieldInFinalClass, AvoidProtectedMethodInFinalClassNotExtending, AvoidUsingNativeCode, BooleanGetMethodName, CallSuperInConstructor, ClassNamingConventions, CommentDefaultAccessModifier, ConfusingTernary, DefaultPackage, DontImportJavaLang, DuplicateImports, EmptyMethodInAbstractClassShouldBeAbstract, ExtendsObject, FieldDeclarationsShouldBeAtStartOfClass, ForLoopShouldBeWhileLoop, ForLoopsMustUseBraces, GenericsNaming, IfElseStmtsMustUseBraces, IfStmtsMustUseBraces, LocalHomeNamingConvention, LocalInterfaceSessionNamingConvention, LocalVariableCouldBeFinal, LongVariable, MDBAndSessionBeanNamingConvention, MethodArgumentCouldBeFinal, MethodNamingConventions, MIsLeadingVariableName, NoPackage, OnlyOneReturn, PackageCase, PrematureDeclaration, RemoteInterfaceNamingConvention, RemoteSessionInterfaceNamingConvention, ShortClassName, ShortMethodName, ShortVariable, SuspiciousConstantFieldName, TooManyStaticImports, UnnecessaryConstructor, UnnecessaryFinalModifier, UnnecessaryFullyQualifiedName, UnnecessaryLocalBeforeReturn, UnnecessaryModifier, UnnecessaryReturn, UselessParentheses, UselessQualifiedThis, VariableNamingConventions, WhileLoopsMustUseBraces
---
## AbstractNaming

**Since:** PMD 1.4

**Priority:** Medium (3)

Abstract classes should be named 'AbstractXXX'.

```
//ClassOrInterfaceDeclaration
 [@Abstract='true' and @Interface='false']
 [not (starts-with(@Image,'Abstract'))]
|
//ClassOrInterfaceDeclaration
 [@Abstract='false']
 [$strict='true']
 [starts-with(@Image, 'Abstract')]
```

**Example(s):**

``` java
public abstract class Foo { // should be AbstractFoo
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strict|true|Also flag classes, that are named Abstract, but are not abstract.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AbstractNaming" />
```

## AtLeastOneConstructor

**Since:** PMD 1.04

**Priority:** Medium (3)

Each class should declare at least one constructor.

```
//ClassOrInterfaceDeclaration[
  not(ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration)
  and
  (@Static = 'false')
  and
  (count(./descendant::MethodDeclaration[@Static = 'true']) < 1)
]
  [@Interface='false']
```

**Example(s):**

``` java
public class Foo {
   // missing constructor
  public void doSomething() { ... }
  public void doOtherThing { ... }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AtLeastOneConstructor" />
```

## AvoidDollarSigns

**Since:** PMD 1.5

**Priority:** Medium (3)

Avoid using dollar signs in variable/method/class/interface names.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.AvoidDollarSignsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/AvoidDollarSignsRule.java)

**Example(s):**

``` java
public class Fo$o {  // not a recommended name
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AvoidDollarSigns" />
```

## AvoidFinalLocalVariable

**Since:** PMD 4.1

**Priority:** Medium (3)

Avoid using final local variables, turn them into fields.

```
//LocalVariableDeclaration[
  @Final = 'true'
  and not(../../ForStatement)
  and
  (
    (count(VariableDeclarator/VariableInitializer) = 0)
    or
    (VariableDeclarator/VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix/Literal)
  )
]
```

**Example(s):**

``` java
public class MyClass {
    public void foo() {
        final String finalLocalVariable;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AvoidFinalLocalVariable" />
```

## AvoidPrefixingMethodParameters

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Prefixing parameters by 'in' or 'out' pollutes the name of the parameters and reduces code readability.
To indicate whether or not a parameter will be modify in a method, its better to document method
behavior with Javadoc.

```
//MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId[
        pmd:matches(@Image,'^in[A-Z].*','^out[A-Z].*','^in$','^out$')
]
```

**Example(s):**

``` java
// Not really clear
public class Foo {
  public void bar(
      int inLeftOperand,
      Result outRightOperand) {
      outRightOperand.setValue(inLeftOperand * outRightOperand.getValue());
  }
}
```

``` java
// Far more useful
public class Foo {
  /**
   *
   * @param leftOperand, (purpose), not modified by method.
   * @param rightOperand (purpose), will be modified by the method: contains the result.
   */
  public void bar(
        int leftOperand,
        Result rightOperand) {
        rightOperand.setValue(leftOperand * rightOperand.getValue());
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AvoidPrefixingMethodParameters" />
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
<rule ref="category/java/codestyle.xml/AvoidProtectedFieldInFinalClass" />
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
<rule ref="category/java/codestyle.xml/AvoidProtectedMethodInFinalClassNotExtending" />
```

## AvoidUsingNativeCode

**Since:** PMD 4.1

**Priority:** Medium High (2)

Unnecessary reliance on Java Native Interface (JNI) calls directly reduces application portability
and increases the maintenance burden.

```
//Name[starts-with(@Image,'System.loadLibrary')]
```

**Example(s):**

``` java
public class SomeJNIClass {

     public SomeJNIClass() {
         System.loadLibrary("nativelib");
     }

     static {
         System.loadLibrary("nativelib");
     }

     public void invalidCallsInMethod() throws SecurityException, NoSuchMethodException {
         System.loadLibrary("nativelib");
     }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AvoidUsingNativeCode" />
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
<rule ref="category/java/codestyle.xml/BooleanGetMethodName" />
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
<rule ref="category/java/codestyle.xml/CallSuperInConstructor" />
```

## ClassNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

Class names should always begin with an upper case character.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.ClassNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/ClassNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ClassNamingConventions" />
```

## CommentDefaultAccessModifier

**Since:** PMD 5.4.0

**Priority:** Medium (3)

To avoid mistakes if we want that a Method, Constructor, Field or Nested class have a default access modifier
we must add a comment at the beginning of it's declaration.
By default the comment must be /* default */, if you want another, you have to provide a regexp.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.CommentDefaultAccessModifierRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/CommentDefaultAccessModifierRule.java)

**Example(s):**

``` java
public class Foo {
    final String stringValue = "some string";
    String getString() {
       return stringValue;
    }

    class NestedFoo {
    }
}

// should be
public class Foo {
    /* default */ final String stringValue = "some string";
    /* default */ String getString() {
       return stringValue;
    }

    /* default */ class NestedFoo {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|regex||Regular expression|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/CommentDefaultAccessModifier" />
```

## ConfusingTernary

**Since:** PMD 1.9

**Priority:** Medium (3)

Avoid negation within an "if" expression with an "else" clause.  For example, rephrase:
`if (x != y) diff(); else same();` as: `if (x == y) same(); else diff();`.

Most "if (x != y)" cases without an "else" are often return cases, so consistent use of this
rule makes the code easier to read.  Also, this resolves trivial ordering problems, such
as "does the error case go first?" or "does the common case go first?".

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.ConfusingTernaryRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/ConfusingTernaryRule.java)

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
<rule ref="category/java/codestyle.xml/ConfusingTernary" />
```

## DefaultPackage

**Since:** PMD 3.4

**Priority:** Medium (3)

Use explicit scoping instead of accidental usage of default package private level.
The rule allows methods and fields annotated with Guava's @VisibleForTesting.

```
//ClassOrInterfaceDeclaration[@Interface='false']
/ClassOrInterfaceBody
/ClassOrInterfaceBodyDeclaration
[not(Annotation//Name[ends-with(@Image, 'VisibleForTesting')])]
[
FieldDeclaration[@PackagePrivate='true']
or MethodDeclaration[@PackagePrivate='true']
]
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/DefaultPackage" />
```

## DontImportJavaLang

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Avoid importing anything from the package 'java.lang'.  These classes are automatically imported (JLS 7.5.3).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.DontImportJavaLangRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/DontImportJavaLangRule.java)

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
<rule ref="category/java/codestyle.xml/DontImportJavaLang" />
```

## DuplicateImports

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Duplicate or overlapping import statements should be avoided.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.DuplicateImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/DuplicateImportsRule.java)

**Example(s):**

``` java
import java.lang.String;
import java.lang.*;
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/DuplicateImports" />
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/EmptyMethodInAbstractClassShouldBeAbstract" />
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
<rule ref="category/java/codestyle.xml/ExtendsObject" />
```

## FieldDeclarationsShouldBeAtStartOfClass

**Since:** PMD 5.0

**Priority:** Medium (3)

Fields should be declared at the top of the class, before any method declarations, constructors, initializers or inner classes.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.FieldDeclarationsShouldBeAtStartOfClassRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/FieldDeclarationsShouldBeAtStartOfClassRule.java)

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
<rule ref="category/java/codestyle.xml/FieldDeclarationsShouldBeAtStartOfClass" />
```

## ForLoopShouldBeWhileLoop

**Since:** PMD 1.02

**Priority:** Medium (3)

Some for loops can be simplified to while loops, this makes them more concise.

```
//ForStatement
  [not(LocalVariableDeclaration)]
  [not(ForInit)]
  [not(ForUpdate)]
  [Expression]
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
<rule ref="category/java/codestyle.xml/ForLoopShouldBeWhileLoop" />
```

## ForLoopsMustUseBraces

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces. If the code formatting or 
indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

```
//ForStatement[not(Statement/Block)]
```

**Example(s):**

``` java
for (int i = 0; i < 42; i++)
   foo();
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ForLoopsMustUseBraces" />
```

## GenericsNaming

**Since:** PMD 4.2.6

**Priority:** Medium Low (4)

Names for references to generic values should be limited to a single uppercase letter.

```
//TypeDeclaration/ClassOrInterfaceDeclaration/TypeParameters/TypeParameter[
  string-length(@Image) > 1 
  or
  string:upper-case(@Image) != @Image
]
```

**Example(s):**

``` java
public interface GenericDao<E extends BaseModel, K extends Serializable> extends BaseDao {
    // This is ok...
}

public interface GenericDao<E extends BaseModel, K extends Serializable> {
    // Also this
}

public interface GenericDao<e extends BaseModel, K extends Serializable> {
    // 'e' should be an 'E'
}

public interface GenericDao<EF extends BaseModel, K extends Serializable> {
   // 'EF' is not ok.
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/GenericsNaming" />
```

## IfElseStmtsMustUseBraces

**Since:** PMD 0.2

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting 
or indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

```
//Statement
 [parent::IfStatement[@Else='true']]
 [not(child::Block)]
 [not(child::IfStatement)]
```

**Example(s):**

``` java
// this is OK
if (foo) x++;

   // but this is not
if (foo)
       x = x+1;
   else
       x = x-1;
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/IfElseStmtsMustUseBraces" />
```

## IfStmtsMustUseBraces

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//IfStatement[count(*) < 3][not(Statement/Block)]
```

**Example(s):**

``` java
if (foo)    // not recommended
    x++;

if (foo) {  // preferred approach
    x++;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/IfStmtsMustUseBraces" />
```

## LocalHomeNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The Local Home interface of a Session EJB should be suffixed by 'LocalHome'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalHome')])
    )
    and
    not
    (
        ends-with(@Image,'LocalHome')
    )
]
```

**Example(s):**

``` java
public interface MyBeautifulLocalHome extends javax.ejb.EJBLocalHome {} // proper name

public interface MissingProperSuffix extends javax.ejb.EJBLocalHome {}  // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LocalHomeNamingConvention" />
```

## LocalInterfaceSessionNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The Local Interface of a Session EJB should be suffixed by 'Local'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalObject')])
    )
    and
    not
    (
        ends-with(@Image,'Local')
    )
]
```

**Example(s):**

``` java
public interface MyLocal extends javax.ejb.EJBLocalObject {}                // proper name

public interface MissingProperSuffix extends javax.ejb.EJBLocalObject {}    // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LocalInterfaceSessionNamingConvention" />
```

## LocalVariableCouldBeFinal

**Since:** PMD 2.2

**Priority:** Medium (3)

A local variable assigned only once can be declared final.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.LocalVariableCouldBeFinalRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/LocalVariableCouldBeFinalRule.java)

**Example(s):**

``` java
public class Bar {
    public void foo () {
    String txtA = "a";          // if txtA will not be assigned again it is better to do this:
    final String txtB = "b";
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LocalVariableCouldBeFinal" />
```

## LongVariable

**Since:** PMD 0.3

**Priority:** Medium (3)

Fields, formal arguments, or local variable names that are too long can make the code difficult to follow.

```
//VariableDeclaratorId[string-length(@Image) > $minimum]
```

**Example(s):**

``` java
public class Something {
    int reallyLongIntName = -3;             // VIOLATION - Field
    public static void main( String argumentsList[] ) { // VIOLATION - Formal
        int otherReallyLongName = -5;       // VIOLATION - Local
        for (int interestingIntIndex = 0;   // VIOLATION - For
             interestingIntIndex < 10;
             interestingIntIndex ++ ) {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|17|The variable length reporting threshold|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LongVariable" />
```

## MDBAndSessionBeanNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The EJB Specification states that any MessageDrivenBean or SessionBean should be suffixed by 'Bean'.

```
//TypeDeclaration/ClassOrInterfaceDeclaration
[
    (
        (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'SessionBean')])
        or
        (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'MessageDrivenBean')])
    )
    and
    not
    (
        ends-with(@Image,'Bean')
    )
]
```

**Example(s):**

``` java
public class SomeBean implements SessionBean{}                  // proper name

public class MissingTheProperSuffix implements SessionBean {}   // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/MDBAndSessionBeanNamingConvention" />
```

## MethodArgumentCouldBeFinal

**Since:** PMD 2.2

**Priority:** Medium (3)

A method argument that is never re-assigned within the method can be declared final.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.MethodArgumentCouldBeFinalRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/MethodArgumentCouldBeFinalRule.java)

**Example(s):**

``` java
public void foo1 (String param) {       // do stuff with param never assigning it

}

public void foo2 (final String param) { // better, do stuff with param never assigning it

}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/MethodArgumentCouldBeFinal" />
```

## MethodNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.MethodNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/MethodNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {
    public void fooStuff() {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkNativeMethods|true|Check native methods|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/MethodNamingConventions" />
```

## MIsLeadingVariableName

**Since:** PMD 3.4

**Priority:** Medium (3)

Detects when a non-field has a name starting with 'm_'.  This usually denotes a field and could be confusing.

```
//VariableDeclaratorId
[starts-with(@Image, 'm_')]
[not (../../../FieldDeclaration)]
```

**Example(s):**

``` java
public class Foo {
    private int m_foo; // OK
    public void bar(String m_baz) { // Bad
      int m_boz = 42; // Bad
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/MIsLeadingVariableName" />
```

## NoPackage

**Since:** PMD 3.3

**Priority:** Medium (3)

Detects when a class or interface does not have a package definition.

```
//ClassOrInterfaceDeclaration[count(preceding::PackageDeclaration) = 0]
```

**Example(s):**

``` java
// no package declaration
public class ClassInDefaultPackage {
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/NoPackage" />
```

## OnlyOneReturn

**Since:** PMD 1.0

**Priority:** Medium (3)

A method should have only one exit point, and that should be the last statement in the method.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.OnlyOneReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/OnlyOneReturnRule.java)

**Example(s):**

``` java
public class OneReturnOnly1 {
  public void foo(int x) {
    if (x > 0) {
      return "hey";   // first exit
    }
    return "hi";    // second exit
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/OnlyOneReturn" />
```

## PackageCase

**Since:** PMD 3.3

**Priority:** Medium (3)

Detects when a package definition contains uppercase characters.

```
//PackageDeclaration/Name[lower-case(@Image)!=@Image]
```

**Example(s):**

``` java
package com.MyCompany;  // should be lowercase name

public class SomeClass {
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/PackageCase" />
```

## PrematureDeclaration

**Since:** PMD 5.0

**Priority:** Medium (3)

Checks for variables that are defined before they might be used. A reference is deemed to be premature if it is created right before a block of code that doesn't use it that also has the ability to return or throw an exception.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.PrematureDeclarationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/PrematureDeclarationRule.java)

**Example(s):**

``` java
public int getLength(String[] strings) {

    int length = 0; // declared prematurely

    if (strings == null || strings.length == 0) return 0;

    for (String str : strings) {
        length += str.length();
    }

    return length;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/PrematureDeclaration" />
```

## RemoteInterfaceNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

Remote Interface of a Session EJB should not have a suffix.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBObject')])
    )
    and
    (
        ends-with(@Image,'Session')
        or
        ends-with(@Image,'EJB')
        or
        ends-with(@Image,'Bean')
    )
]
```

**Example(s):**

``` java
/* Poor Session suffix */
public interface BadSuffixSession extends javax.ejb.EJBObject {}

/* Poor EJB suffix */
public interface BadSuffixEJB extends javax.ejb.EJBObject {}

/* Poor Bean suffix */
public interface BadSuffixBean extends javax.ejb.EJBObject {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/RemoteInterfaceNamingConvention" />
```

## RemoteSessionInterfaceNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

A Remote Home interface type of a Session EJB should be suffixed by 'Home'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBHome')])
    )
    and
    not
    (
        ends-with(@Image,'Home')
    )
]
```

**Example(s):**

``` java
public interface MyBeautifulHome extends javax.ejb.EJBHome {}       // proper name

public interface MissingProperSuffix extends javax.ejb.EJBHome {}   // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/RemoteSessionInterfaceNamingConvention" />
```

## ShortClassName

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Short Classnames with fewer than e.g. five characters are not recommended.

```
//ClassOrInterfaceDeclaration[string-length(@Image) < $minimum]
```

**Example(s):**

``` java
public class Foo {
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|5|Number of characters that are required as a minimum for a class name.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortClassName" />
```

## ShortMethodName

**Since:** PMD 0.3

**Priority:** Medium (3)

Method names that are very short are not helpful to the reader.

```
//MethodDeclarator[string-length(@Image) < $minimum]
```

**Example(s):**

``` java
public class ShortMethod {
    public void a( int i ) { // Violation
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|3|Number of characters that are required as a minimum for a method name.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortMethodName" />
```

## ShortVariable

**Since:** PMD 0.3

**Priority:** Medium (3)

Fields, local variables, or parameter names that are very short are not helpful to the reader.

```
//VariableDeclaratorId[string-length(@Image) < $minimum]
 [not(ancestor::ForInit)]
 [not(../../VariableDeclarator and ../../../LocalVariableDeclaration and ../../../../ForStatement)]
 [not((ancestor::FormalParameter) and (ancestor::TryStatement))]
```

**Example(s):**

``` java
public class Something {
    private int q = 15;                         // field - too short
    public static void main( String as[] ) {    // formal arg - too short
        int r = 20 + q;                         // local var - too short
        for (int i = 0; i < 10; i++) {          // not a violation (inside 'for' loop)
            r += q;
        }
        for (Integer i : numbers) {             // not a violation (inside 'for-each' loop)
            r += q;
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|3|Number of characters that are required as a minimum for a variable name.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortVariable" />
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
<rule ref="category/java/codestyle.xml/SuspiciousConstantFieldName" />
```

## TooManyStaticImports

**Since:** PMD 4.1

**Priority:** Medium (3)

If you overuse the static import feature, it can make your program unreadable and 
unmaintainable, polluting its namespace with all the static members you import. 
Readers of your code (including you, a few months after you wrote it) will not know 
which class a static member comes from (Sun 1.5 Language Guide).

```
.[count(ImportDeclaration[@Static = 'true']) > $maximumStaticImports]
```

**Example(s):**

``` java
import static Lennon;
import static Ringo;
import static George;
import static Paul;
import static Yoko; // Too much !
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maximumStaticImports|4|All static imports can be disallowed by setting this to 0|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/TooManyStaticImports" />
```

## UnnecessaryConstructor

**Since:** PMD 1.0

**Priority:** Medium (3)

This rule detects when a constructor is not necessary; i.e., when there is only one constructor,
it's public, has an empty body, and takes no arguments.

```
//ClassOrInterfaceBody[count(ClassOrInterfaceBodyDeclaration/ConstructorDeclaration)=1]
/ClassOrInterfaceBodyDeclaration/ConstructorDeclaration
[@Public='true']
[not(FormalParameters/*)]
[not(BlockStatement)]
[not(NameList)]
[count(ExplicitConstructorInvocation/Arguments/ArgumentList/Expression)=0]
```

**Example(s):**

``` java
public class Foo {
  public Foo() {}
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UnnecessaryConstructor" />
```

## UnnecessaryFinalModifier

**Since:** PMD 3.0

**Priority:** Medium (3)

When a class has the final modifier, all the methods are automatically final and do not need to be
tagged as such. Similarly, methods that can't be overridden (private methods, methods of anonymous classes,
methods of enum instance) do not need to be tagged either.

```
//ClassOrInterfaceDeclaration[@Final='true' and @Interface='false']
    /ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
        [count(./Annotation/MarkerAnnotation/Name[@Image='SafeVarargs' or @Image='java.lang.SafeVarargs']) = 0]
    /MethodDeclaration[@Final='true']
| //MethodDeclaration[@Final='true' and @Private='true']
| //EnumConstant/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/MethodDeclaration[@Final='true']
| //AllocationExpression/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/MethodDeclaration[@Final='true']
```

**Example(s):**

``` java
public final class Foo {
    // This final modifier is not necessary, since the class is final
    // and thus, all methods are final
    private final void foo() {
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UnnecessaryFinalModifier" />
```

## UnnecessaryFullyQualifiedName

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Import statements allow the use of non-fully qualified names.  The use of a fully qualified name
which is covered by an import statement is redundant.  Consider using the non-fully qualified name.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryFullyQualifiedNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryFullyQualifiedNameRule.java)

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
<rule ref="category/java/codestyle.xml/UnnecessaryFullyQualifiedName" />
```

## UnnecessaryLocalBeforeReturn

**Since:** PMD 3.3

**Priority:** Medium (3)

Avoid the creation of unnecessary local variables

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryLocalBeforeReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryLocalBeforeReturnRule.java)

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
<rule ref="category/java/codestyle.xml/UnnecessaryLocalBeforeReturn" />
```

## UnnecessaryModifier

**Since:** PMD 1.02

**Priority:** Medium (3)

Fields in interfaces and annotations are automatically `public static final`, and methods are `public abstract`.
Classes, interfaces or annotations nested in an interface or annotation are automatically `public static`
(all nested interfaces and annotations are automatically static).
Nested enums are automatically `static`.
For historical reasons, modifiers which are implied by the context are accepted by the compiler, but are superfluous.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryModifierRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryModifierRule.java)

**Example(s):**

``` java
public @interface Annotation {
    public abstract void bar();     // both abstract and public are ignored by the compiler
    public static final int X = 0;  // public, static, and final all ignored
    public static class Bar {}      // public, static ignored
    public static interface Baz {}  // ditto
}
public interface Foo {
    public abstract void bar();     // both abstract and public are ignored by the compiler
    public static final int X = 0;  // public, static, and final all ignored
    public static class Bar {}      // public, static ignored
    public static interface Baz {}  // ditto
}
public class Bar {
    public static interface Baz {}  // static ignored
    public static enum FoorBar {    // static ignored
        FOO;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UnnecessaryModifier" />
```

## UnnecessaryReturn

**Since:** PMD 1.3

**Priority:** Medium (3)

Avoid the use of unnecessary return statements.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryReturnRule.java)

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
<rule ref="category/java/codestyle.xml/UnnecessaryReturn" />
```

## UselessParentheses

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Useless parentheses should be removed.

```
//Expression[not(parent::PrimaryPrefix)]/PrimaryExpression[count(*)>1]
  /PrimaryPrefix/Expression
    [not(./CastExpression)]
    [not(./ConditionalExpression[@Ternary='true'])]
    [not(./AdditiveExpression[//Literal[@StringLiteral='true']])]
|
//Expression[not(parent::PrimaryPrefix)]/PrimaryExpression[count(*)=1]
  /PrimaryPrefix/Expression
|
//Expression/ConditionalAndExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    count(./CastExpression)=0 and
    count(./EqualityExpression/MultiplicativeExpression)=0 and
    count(./ConditionalExpression[@Ternary='true'])=0 and
    count(./ConditionalOrExpression)=0]
|
//Expression/ConditionalOrExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    not(./CastExpression) and
    not(./ConditionalExpression[@Ternary='true']) and
    not(./EqualityExpression/MultiplicativeExpression)]
|
//Expression/ConditionalExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    not(./CastExpression) and
    not(./EqualityExpression)]
|
//Expression/AdditiveExpression[not(./PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral='true'])]
  /PrimaryExpression[1]/PrimaryPrefix/Expression[
    count(*)=1 and
    not(./CastExpression) and
    not(./AdditiveExpression[@Image = '-']) and
    not(./ShiftExpression) and
    not(./RelationalExpression) and
    not(./InstanceOfExpression) and
    not(./EqualityExpression) and
    not(./AndExpression) and
    not(./ExclusiveOrExpression) and
    not(./InclusiveOrExpression) and
    not(./ConditionalAndExpression) and
    not(./ConditionalOrExpression) and
    not(./ConditionalExpression)]
|
//Expression/EqualityExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    not(./CastExpression) and
    not(./AndExpression) and
    not(./InclusiveOrExpression) and
    not(./ExclusiveOrExpression) and
    not(./ConditionalExpression) and
    not(./ConditionalAndExpression) and
    not(./ConditionalOrExpression) and
    not(./EqualityExpression)]
```

**Example(s):**

``` java
public class Foo {

    private int _bar1;
    private Integer _bar2;

    public void setBar(int n) {
        _bar1 = Integer.valueOf((n)); // here
        _bar2 = (n); // and here
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UselessParentheses" />
```

## UselessQualifiedThis

**Since:** PMD 5.4.0

**Priority:** Medium (3)

Look for qualified this usages in the same class.

```
//PrimaryExpression
[PrimaryPrefix/Name[@Image]]
[PrimarySuffix[@Arguments='false']]
[not(PrimarySuffix/MemberSelector)]
[ancestor::ClassOrInterfaceBodyDeclaration[1][@AnonymousInnerClass='false']]
/PrimaryPrefix/Name[@Image = ancestor::ClassOrInterfaceDeclaration[1]/@Image]
```

**Example(s):**

``` java
public class Foo {
    final Foo otherFoo = Foo.this;  // use "this" directly

    public void doSomething() {
         final Foo anotherFoo = Foo.this;  // use "this" directly
    }

    private ActionListener returnListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSomethingWithQualifiedThis(Foo.this);  // This is fine
            }
        };
    }

    private class Foo3 {
        final Foo myFoo = Foo.this;  // This is fine
    }

    private class Foo2 {
        final Foo2 myFoo2 = Foo2.this;  // Use "this" direclty
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UselessQualifiedThis" />
```

## VariableNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.VariableNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/VariableNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {
    public static final int MY_NUM = 0;
    public String myTest = "";
    DataModule dmTest = new DataModule();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|parameterSuffix|[]|Method parameter variable suffixes|
|parameterPrefix|[]|Method parameter variable prefixes|
|localSuffix|[]|Local variable suffixes|
|localPrefix|[]|Local variable prefixes|
|memberSuffix|[]|Member variable suffixes|
|memberPrefix|[]|Member variable prefixes|
|staticSuffix|[]|Static variable suffixes|
|checkParameters|true|Check constructor and method parameter variables|
|checkNativeMethodParameters|true|Check method parameter of native methods|
|staticPrefix|[]|Static variable prefixes|
|checkLocals|true|Check local variables|
|checkMembers|true|Check member variables|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/VariableNamingConventions" />
```

## WhileLoopsMustUseBraces

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

```
//WhileStatement[not(Statement/Block)]
```

**Example(s):**

``` java
while (true)    // not recommended
      x++;
      
while (true) {  // preferred approach
      x++;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/WhileLoopsMustUseBraces" />
```

