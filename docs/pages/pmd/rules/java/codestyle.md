---
title: Code Style
summary: Rules which enforce a specific coding style.
permalink: pmd_rules_java_codestyle.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/category/java/codestyle.xml
keywords: Code Style, AbstractNaming, AtLeastOneConstructor, AvoidDollarSigns, AvoidFinalLocalVariable, AvoidPrefixingMethodParameters, AvoidProtectedFieldInFinalClass, AvoidProtectedMethodInFinalClassNotExtending, AvoidUsingNativeCode, BooleanGetMethodName, CallSuperInConstructor, ClassNamingConventions, CommentDefaultAccessModifier, ConfusingTernary, ControlStatementBraces, DefaultPackage, DontImportJavaLang, DuplicateImports, EmptyMethodInAbstractClassShouldBeAbstract, ExtendsObject, FieldDeclarationsShouldBeAtStartOfClass, FieldNamingConventions, ForLoopShouldBeWhileLoop, ForLoopsMustUseBraces, FormalParameterNamingConventions, GenericsNaming, IdenticalCatchBranches, IfElseStmtsMustUseBraces, IfStmtsMustUseBraces, LinguisticNaming, LocalHomeNamingConvention, LocalInterfaceSessionNamingConvention, LocalVariableCouldBeFinal, LocalVariableNamingConventions, LongVariable, MDBAndSessionBeanNamingConvention, MethodArgumentCouldBeFinal, MethodNamingConventions, MIsLeadingVariableName, NoPackage, OnlyOneReturn, PackageCase, PrematureDeclaration, RemoteInterfaceNamingConvention, RemoteSessionInterfaceNamingConvention, ShortClassName, ShortMethodName, ShortVariable, SuspiciousConstantFieldName, TooManyStaticImports, UnnecessaryAnnotationValueElement, UnnecessaryConstructor, UnnecessaryFullyQualifiedName, UnnecessaryLocalBeforeReturn, UnnecessaryModifier, UnnecessaryReturn, UselessParentheses, UselessQualifiedThis, VariableNamingConventions, WhileLoopsMustUseBraces
language: Java
---
## AbstractNaming

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.4

**Priority:** Medium (3)

Abstract classes should be named 'AbstractXXX'.

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|strict|true|Also flag classes, that are named Abstract, but are not abstract.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/AbstractNaming" />
```

## AtLeastOneConstructor

**Since:** PMD 1.04

**Priority:** Medium (3)

Each non-static class should declare at least one constructor.
Classes with solely static members are ignored, refer to [UseUtilityClassRule](pmd_rules_java_design.html#useutilityclass) to detect those.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.AtLeastOneConstructorRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/AtLeastOneConstructorRule.java)

**Example(s):**

``` java
public class Foo {
   // missing constructor
  public void doSomething() { ... }
  public void doOtherThing { ... }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|ignoredAnnotations|lombok.Data \| lombok.Value \| lombok.Builder \| lombok.NoArgsConstructor \| lombok.RequiredArgsConstructor \| lombok.AllArgsConstructorAtLeastOneConstructor|Fully qualified names of the annotation types that should be ignored by this rule|yes. Delimiter is '\|'.|

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

**This rule is defined by the following XPath expression:**
``` xpath
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

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Prefixing parameters by 'in' or 'out' pollutes the name of the parameters and reduces code readability.
To indicate whether or not a parameter will be modify in a method, its better to document method
behavior with Javadoc.

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|checkParameterizedMethods|false|Check parameterized methods|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/BooleanGetMethodName" />
```

## CallSuperInConstructor

**Since:** PMD 3.0

**Priority:** Medium (3)

It is a good practice to call super() in a constructor. If super() is not called but
another constructor (such as an overloaded constructor) is called, this rule will not report it.

**This rule is defined by the following XPath expression:**
``` xpath
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

Configurable naming conventions for type declarations. This rule reports
type declarations which do not match the regex that applies to their
specific kind (e.g. enum or interface). Each regex can be configured through
properties.

By default this rule uses the standard Java naming convention (Pascal case),
and reports utility class names not ending with 'Util'.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.ClassNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/ClassNamingConventionsRule.java)

**Example(s):**

``` java
// This is Pascal case, the recommended naming convention in Java
// Note that the default values of this rule don't allow underscores 
// or accented characters in type names
public class FooBar {}

// You may want abstract classes to be named 'AbstractXXX',
// in which case you can customize the regex for abstract
// classes to 'Abstract[A-Z]\w+'
public abstract class Thing {}

// This class doesn't respect the convention, and will be flagged
public class Éléphant {}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|classPattern|\[A-Z\]\[a-zA-Z0-9\]\*|Regex which applies to concrete class names|no|
|abstractClassPattern|\[A-Z\]\[a-zA-Z0-9\]\*|Regex which applies to abstract class names|no|
|interfacePattern|\[A-Z\]\[a-zA-Z0-9\]\*|Regex which applies to interface names|no|
|enumPattern|\[A-Z\]\[a-zA-Z0-9\]\*|Regex which applies to enum names|no|
|annotationPattern|\[A-Z\]\[a-zA-Z0-9\]\*|Regex which applies to annotation names|no|
|utilityClassPattern|\[A-Z\]\[a-zA-Z0-9\]+(Utils?\|Helper)|Regex which applies to utility class names|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ClassNamingConventions" />
```

## CommentDefaultAccessModifier

**Since:** PMD 5.4.0

**Priority:** Medium (3)

To avoid mistakes if we want that a Method, Constructor, Field or Nested class have a default access modifier
we must add a comment at the beginning of it's declaration.
By default the comment must be /* default */ or /* package */, if you want another, you have to provide a regular expression.
This rule ignores by default all cases that have a @VisibleForTesting annotation. Use the
property "ignoredAnnotations" to customize the recognized annotations.

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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|regex|\\/\\\*\\s+(default\|package)\\s+\\\*\\/|Regular expression|no|
|ignoredAnnotations|com.google.common.annotations.VisibleForTesting \| android.support.annotation.VisibleForTesting|Fully qualified names of the annotation types that should be ignored by this rule|yes. Delimiter is '\|'.|

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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|ignoreElseIf|false|Ignore conditions with an else-if case|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ConfusingTernary" />
```

## ControlStatementBraces

**Since:** PMD 6.2.0

**Priority:** Medium (3)

Enforce a policy for braces on control statements. It is recommended to use braces on 'if ... else'
statements and loop statements, even if they are optional. This usually makes the code clearer, and
helps prepare the future when you need to add another statement. That said, this rule lets you control
which statements are required to have braces via properties.

From 6.2.0 on, this rule supersedes WhileLoopMustUseBraces, ForLoopMustUseBraces, IfStmtMustUseBraces,
and IfElseStmtMustUseBraces.

**This rule is defined by the following XPath expression:**
``` xpath
//WhileStatement[$checkWhileStmt and not(Statement/Block) and not($allowEmptyLoop and Statement/EmptyStatement)]
                |
                //ForStatement[$checkForStmt and not(Statement/Block) and not($allowEmptyLoop and Statement/EmptyStatement)]
                |
                //DoStatement[$checkDoWhileStmt and not(Statement/Block) and not($allowEmptyLoop and Statement/EmptyStatement)]
                |
                (: The violation is reported on the sub statement -- not the if statement :)
                //Statement[$checkIfElseStmt and parent::IfStatement and not(child::Block or child::IfStatement)
                            (: Whitelists single if statements :)
                            and ($checkSingleIfStmt
                                 (: Inside this not(...) is the definition of a "single if statement" :)
                                 or not(count(../Statement) = 1 (: No else stmt :)
                                        (: Not the last branch of an 'if ... else if' chain :)
                                        and not(parent::IfStatement[parent::Statement[parent::IfStatement]])))]
                |
                (: Reports case labels if one of their subordinate statements is not braced :)
                //SwitchLabel[$checkCaseStmt]
                             [count(following-sibling::BlockStatement except following-sibling::SwitchLabel[1]/following-sibling::BlockStatement) > 1
                              or (some $stmt (: in only the block statements until the next label :)
                                  in following-sibling::BlockStatement except following-sibling::SwitchLabel[1]/following-sibling::BlockStatement
                                  satisfies not($stmt/Statement/Block))]
```

**Example(s):**

``` java
while (true)    // not recommended
  x++;

while (true) {  // preferred approach
  x++;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|checkIfElseStmt|true|Require that 'if ... else' statements use braces|no|
|checkSingleIfStmt|true|Require that 'if' statements with a single branch use braces|no|
|checkWhileStmt|true|Require that 'while' loops use braces|no|
|checkForStmt|true|Require that 'for' loops should use braces|no|
|checkDoWhileStmt|true|Require that 'do ... while' loops use braces|no|
|checkCaseStmt|false|Require that cases of a switch have braces|no|
|allowEmptyLoop|false|Allow loops with an empty statement, e.g. 'while(true);'|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ControlStatementBraces" />
```

## DefaultPackage

**Since:** PMD 3.4

**Priority:** Medium (3)

Use explicit scoping instead of accidental usage of default package private level.
The rule allows methods and fields annotated with Guava's @VisibleForTesting.

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|ignoreInterfaceDeclarations|false|Ignore Interface Declarations that precede fields.|no|
|ignoreAnonymousClassDeclarations|true|Ignore Field Declarations, that are initialized with anonymous class declarations|no|
|ignoreEnumDeclarations|true|Ignore Enum Declarations that precede fields.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/FieldDeclarationsShouldBeAtStartOfClass" />
```

## FieldNamingConventions

**Since:** PMD 6.7.0

**Priority:** High (1)

Configurable naming conventions for field declarations. This rule reports variable declarations
which do not match the regex that applies to their specific kind ---e.g. constants (static final),
enum constant, final field. Each regex can be configured through properties.

By default this rule uses the standard Java naming convention (Camel case), and uses the ALL_UPPER
convention for constants and enum constants.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.FieldNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/FieldNamingConventionsRule.java)

**Example(s):**

``` java
class Foo {
                int myField = 1; // This is in camel case, so it's ok
                int my_Field = 1; // This contains an underscore, it's not ok by default
                                  // but you may allow it, or even require the "my_" prefix

                final int FinalField = 1; // you may configure a different convention for final fields,
                                          // e.g. here PascalCase: [A-Z][a-zA-Z0-9]*

                interface Interface {
                    double PI = 3.14; // interface "fields" use the constantPattern property
                }

                enum AnEnum {
                    ORG, NET, COM; // These use a separate property but are set to ALL_UPPER by default
                }
            }
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|publicConstantPattern|\[A-Z\]\[A-Z\_0-9\]\*|Regex which applies to public constant names|no|
|constantPattern|\[A-Z\]\[A-Z\_0-9\]\*|Regex which applies to non-public static final field names|no|
|enumConstantPattern|\[A-Z\]\[A-Z\_0-9\]\*|Regex which applies to enum constant names|no|
|finalFieldPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to final field names|no|
|staticFieldPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to static field names|no|
|defaultFieldPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to field names|no|
|exclusions|serialVersionUID|Names of fields to whitelist.|yes. Delimiter is '\|'.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/FieldNamingConventions" />
```

## ForLoopShouldBeWhileLoop

**Since:** PMD 1.02

**Priority:** Medium (3)

Some for loops can be simplified to while loops, this makes them more concise.

**This rule is defined by the following XPath expression:**
``` xpath
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

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'for' statements without using curly braces. If the code formatting or 
indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

**This rule is defined by the following XPath expression:**
``` xpath
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

## FormalParameterNamingConventions

**Since:** PMD 6.6.0

**Priority:** High (1)

Configurable naming conventions for formal parameters of methods and lambdas.
This rule reports formal parameters which do not match the regex that applies to their
specific kind (e.g. lambda parameter, or final formal parameter). Each regex can be
configured through properties.

By default this rule uses the standard Java naming convention (Camel case).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.FormalParameterNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/FormalParameterNamingConventionsRule.java)

**Example(s):**

``` java
class Foo {

                abstract void bar(int myInt); // This is Camel case, so it's ok

                void bar(int my_i) { // this will be reported

                }

                void lambdas() {

                    // lambdas parameters can be configured separately
                    Consumer<String> lambda1 = s_str -> { };

                    // lambda parameters with an explicit type can be configured separately
                    Consumer<String> lambda1 = (String str) -> { };

                }

            }
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|methodParameterPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to formal parameter names|no|
|finalMethodParameterPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to final formal parameter names|no|
|lambdaParameterPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to inferred-type lambda parameter names|no|
|explicitLambdaParameterPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to explicitly-typed lambda parameter names|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/FormalParameterNamingConventions" />
```

## GenericsNaming

**Since:** PMD 4.2.6

**Priority:** Medium Low (4)

Names for references to generic values should be limited to a single uppercase letter.

**This rule is defined by the following XPath expression:**
``` xpath
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

## IdenticalCatchBranches

**Since:** PMD 6.4.0

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.7

Identical `catch` branches use up vertical space and increase the complexity of code without
adding functionality. It's better style to collapse identical branches into a single multi-catch
branch.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.IdenticalCatchBranchesRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/IdenticalCatchBranchesRule.java)

**Example(s):**

``` java
try {
    // do something
} catch (IllegalArgumentException e) {
    throw e;
} catch (IllegalStateException e) { // Can be collapsed into the previous block
    throw e;
}

try {
    // do something
} catch (IllegalArgumentException | IllegalStateException e) { // This is better
    throw e;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/IdenticalCatchBranches" />
```

## IfElseStmtsMustUseBraces

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 0.2

**Priority:** Medium (3)

Avoid using if..else statements without using surrounding braces. If the code formatting 
or indentation is lost then it becomes difficult to separate the code being controlled 
from the rest.

**This rule is defined by the following XPath expression:**
``` xpath
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

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid using if statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

**This rule is defined by the following XPath expression:**
``` xpath
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

## LinguisticNaming

**Since:** PMD 6.7.0

**Priority:** Medium (3)

This rule finds Linguistic Naming Antipatterns. It checks for fields, that are named, as if they should
be boolean but have a different type. It also checks for methods, that according to their name, should
return a boolean, but don't. Further, it checks, that getters return something and setters won't.
Finally, it checks that methods, that start with "to" - so called transform methods - actually return
something, since according to their name, they should convert or transform one object into another.
There is additionally an option, to check for methods that contain "To" in their name - which are
also transform methods. However, this is disabled by default, since this detection is prone to
false positives.

For more information, see [Linguistic Antipatterns - What They Are and How
Developers Perceive Them](https://doi.org/10.1007/s10664-014-9350-8).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.LinguisticNamingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/LinguisticNamingRule.java)

**Example(s):**

``` java
public class LinguisticNaming {
    int isValid;    // the field name indicates a boolean, but it is an int.
    boolean isTrue; // correct type of the field

    void myMethod() {
        int hasMoneyLocal;      // the local variable name indicates a boolean, but it is an int.
        boolean hasSalaryLocal; // correct naming and type
    }

    // the name of the method indicates, it is a boolean, but the method returns an int.
    int isValid() {
        return 1;
    }
    // correct naming and return type
    boolean isSmall() {
        return true;
    }

    // the name indicates, this is a setter, but it returns something
    int setName() {
        return 1;
    }

    // the name indicates, this is a getter, but it doesn't return anything
    void getName() {
        // nothing to return?
    }

    // the name indicates, it transforms an object and should return the result
    void toDataType() {
        // nothing to return?
    }
    // the name indicates, it transforms an object and should return the result
    void grapeToWine() {
        // nothing to return?
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|booleanFieldPrefixes|is \| has \| can \| have \| will \| should|The prefixes of fields and variables that indicate boolean.|yes. Delimiter is '\|'.|
|checkVariables|true|Check local variable names and types for inconsistent naming.|no|
|checkFields|true|Check field names and types for inconsistent naming.|no|
|transformMethodNames|to \| as|The prefixes and infixes that indicate a transform method.|yes. Delimiter is '\|'.|
|booleanMethodPrefixes|is \| has \| can \| have \| will \| should|The prefixes of methods that return boolean.|yes. Delimiter is '\|'.|
|checkPrefixedTransformMethods|true|Check return type of methods whose names start with the configured prefix (see transformMethodNames property).|no|
|checkTransformMethods|false|Check return type of methods which contain the configured infix in their name (see transformMethodNames property).|no|
|checkSetters|true|Check return type of setters.|no|
|checkGetters|true|Check return type of getters.|no|
|checkBooleanMethod|true|Check method names and types for inconsistent naming.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LinguisticNaming" />
```

## LocalHomeNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The Local Home interface of a Session EJB should be suffixed by 'LocalHome'.

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

## LocalVariableNamingConventions

**Since:** PMD 6.6.0

**Priority:** High (1)

Configurable naming conventions for local variable declarations and other locally-scoped
variables. This rule reports variable declarations which do not match the regex that applies to their
specific kind (e.g. final variable, or catch-clause parameter). Each regex can be configured through
properties.

By default this rule uses the standard Java naming convention (Camel case).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.LocalVariableNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/LocalVariableNamingConventionsRule.java)

**Example(s):**

``` java
class Foo {
                void bar() {
                    int localVariable = 1; // This is in camel case, so it's ok
                    int local_variable = 1; // This will be reported unless you change the regex

                    final int i_var = 1; // final local variables can be configured separately

                    try {
                        foo();
                    } catch (IllegalArgumentException e_illegal) {
                        // exception block parameters can be configured separately
                    }

                }
            }
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|localVarPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to non-final local variable names|no|
|finalVarPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to final local variable names|no|
|catchParameterPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to exception block parameter names|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LocalVariableNamingConventions" />
```

## LongVariable

**Since:** PMD 0.3

**Priority:** Medium (3)

Fields, formal arguments, or local variable names that are too long can make the code difficult to follow.

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|minimum|17|The variable length reporting threshold|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/LongVariable" />
```

## MDBAndSessionBeanNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The EJB Specification states that any MessageDrivenBean or SessionBean should be suffixed by 'Bean'.

**This rule is defined by the following XPath expression:**
``` xpath
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

Configurable naming conventions for method declarations. This rule reports
method declarations which do not match the regex that applies to their
specific kind (e.g. JUnit test or native method). Each regex can be
configured through properties.

By default this rule uses the standard Java naming convention (Camel case).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.MethodNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/MethodNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {
    public void fooStuff() {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|checkNativeMethods|true|<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span>  Check native methods|no|
|methodPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to instance method names|no|
|staticPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to static method names|no|
|nativePattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to native method names|no|
|junit3TestPattern|test\[A-Z0-9\]\[a-zA-Z0-9\]\*|Regex which applies to JUnit 3 test method names|no|
|junit4TestPattern|\[a-z\]\[a-zA-Z0-9\]\*|Regex which applies to JUnit 4 test method names|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/MethodNamingConventions" />
```

## MIsLeadingVariableName

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 3.4

**Priority:** Medium (3)

Detects when a non-field has a name starting with 'm_'.  This usually denotes a field and could be confusing.

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
//ClassOrInterfaceDeclaration[string-length(@Image) < $minimum]
```

**Example(s):**

``` java
public class Foo {
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|minimum|5|Number of characters that are required as a minimum for a class name.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortClassName" />
```

## ShortMethodName

**Since:** PMD 0.3

**Priority:** Medium (3)

Method names that are very short are not helpful to the reader.

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|minimum|3|Number of characters that are required as a minimum for a method name.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortMethodName" />
```

## ShortVariable

**Since:** PMD 0.3

**Priority:** Medium (3)

Fields, local variables, or parameter names that are very short are not helpful to the reader.

**This rule is defined by the following XPath expression:**
``` xpath
//VariableDeclaratorId[string-length(@Image) < $minimum]
 (: ForStatement :)
 [not(../../..[self::ForInit])]
 (: Foreach statement :)
 [not(../../..[self::ForStatement])]
 (: Catch statement parameter :)
 [not(../..[self::CatchStatement])]
 (: Lambda expression parameter :)
 [not(parent::LambdaExpression or ../../..[self::LambdaExpression])]
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|minimum|3|Number of characters that are required as a minimum for a variable name.|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/ShortVariable" />
```

## SuspiciousConstantFieldName

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 2.0

**Priority:** Medium (3)

Field names using all uppercase characters - Sun's Java naming conventions indicating constants - should
be declared as final.

**This rule is defined by the following XPath expression:**
``` xpath
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

**This rule is defined by the following XPath expression:**
``` xpath
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|maximumStaticImports|4|All static imports can be disallowed by setting this to 0|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/TooManyStaticImports" />
```

## UnnecessaryAnnotationValueElement

**Since:** PMD 6.2.0

**Priority:** Medium (3)

Avoid the use of value in annotations when it's the only element.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryAnnotationValueElementRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryAnnotationValueElementRule.java)

**Example(s):**

``` java
@TestClassAnnotation(value = "TEST")
public class Foo {

    @TestMemberAnnotation(value = "TEST")
    private String y;

    @TestMethodAnnotation(value = "TEST")
    public void bar() {
        int x = 42;
        return;
    }
}

// should be

@TestClassAnnotation("TEST")
public class Foo {

    @TestMemberAnnotation("TEST")
    private String y;

    @TestMethodAnnotation("TEST")
    public void bar() {
        int x = 42;
        return;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UnnecessaryAnnotationValueElement" />
```

## UnnecessaryConstructor

**Since:** PMD 1.0

**Priority:** Medium (3)

This rule detects when a constructor is not necessary; i.e., when there is only one constructor and the
constructor is identical to the default constructor. The default constructor should has same access
modifier as the declaring class. In an enum type, the default constructor is implicitly private.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryConstructorRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/codestyle/UnnecessaryConstructorRule.java)

**Example(s):**

``` java
public class Foo {
  public Foo() {}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|ignoredAnnotations|javax.inject.Inject|Fully qualified names of the annotation types that should be ignored by this rule|yes. Delimiter is '\|'.|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/UnnecessaryConstructor" />
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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|statementOrderMatters|true|If set to false this rule no longer requires the variable declaration and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be reported.|no|

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

**This rule is defined by the following XPath expression:**
``` xpath
//Expression[not(parent::PrimaryPrefix)]/PrimaryExpression[count(*)>1]
  /PrimaryPrefix/Expression
    [not(./CastExpression)]
    [not(./ConditionalExpression)]
    [not(./AdditiveExpression)]
    [not(./AssignmentOperator)]
|
//Expression[not(parent::PrimaryPrefix)]/PrimaryExpression[count(*)=1]
  /PrimaryPrefix/Expression
|
//Expression/ConditionalAndExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    count(./CastExpression)=0 and
    count(./EqualityExpression/MultiplicativeExpression)=0 and
    count(./ConditionalExpression)=0 and
    count(./ConditionalOrExpression)=0]
|
//Expression/ConditionalOrExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    not(./CastExpression) and
    not(./ConditionalExpression) and
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

Reports qualified this usages in the same class.

**This rule is defined by the following XPath expression:**
``` xpath
//PrimaryExpression
[PrimaryPrefix/Name[@Image]]
[PrimarySuffix[@Arguments='false' and @ArrayDereference = 'false']]
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

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

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

|Name|Default Value|Description|Multivalued|
|----|-------------|-----------|-----------|
|parameterSuffix||Method parameter variable suffixes|yes. Delimiter is ','.|
|parameterPrefix||Method parameter variable prefixes|yes. Delimiter is ','.|
|localSuffix||Local variable suffixes|yes. Delimiter is ','.|
|localPrefix||Local variable prefixes|yes. Delimiter is ','.|
|memberSuffix||Member variable suffixes|yes. Delimiter is ','.|
|memberPrefix||Member variable prefixes|yes. Delimiter is ','.|
|staticSuffix||Static variable suffixes|yes. Delimiter is ','.|
|checkParameters|true|Check constructor and method parameter variables|no|
|checkNativeMethodParameters|true|Check method parameter of native methods|no|
|staticPrefix||Static variable prefixes|yes. Delimiter is ','.|
|checkLocals|true|Check local variables|no|
|checkMembers|true|Check member variables|no|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/codestyle.xml/VariableNamingConventions" />
```

## WhileLoopsMustUseBraces

<span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated</span> 

**Since:** PMD 0.7

**Priority:** Medium (3)

Avoid using 'while' statements without using braces to surround the code block. If the code 
formatting or indentation is lost then it becomes difficult to separate the code being
controlled from the rest.

**This rule is defined by the following XPath expression:**
``` xpath
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

