---
title: Naming
summary: The Naming Ruleset contains rules regarding preferred usage of names and identifiers.
permalink: pmd_rules_java_naming.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/naming.xml
keywords: Naming, ShortVariable, LongVariable, ShortMethodName, VariableNamingConventions, MethodNamingConventions, ClassNamingConventions, AbstractNaming, AvoidDollarSigns, MethodWithSameNameAsEnclosingClass, SuspiciousHashcodeMethodName, SuspiciousConstantFieldName, SuspiciousEqualsMethodName, AvoidFieldNameMatchingTypeName, AvoidFieldNameMatchingMethodName, NoPackage, PackageCase, MisleadingVariableName, BooleanGetMethodName, ShortClassName, GenericsNaming
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
<rule ref="rulesets/java/naming.xml/AbstractNaming" />
```

## AvoidDollarSigns

**Since:** PMD 1.5

**Priority:** Medium (3)

Avoid using dollar signs in variable/method/class/interface names.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.AvoidDollarSignsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/AvoidDollarSignsRule.java)

**Example(s):**

``` java
public class Fo$o {  // not a recommended name
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/naming.xml/AvoidDollarSigns" />
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
<rule ref="rulesets/java/naming.xml/AvoidFieldNameMatchingMethodName" />
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
<rule ref="rulesets/java/naming.xml/AvoidFieldNameMatchingTypeName" />
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
<rule ref="rulesets/java/naming.xml/BooleanGetMethodName" />
```

## ClassNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

Class names should always begin with an upper case character.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.ClassNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/ClassNamingConventionsRule.java)

**Example(s):**

``` java
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/naming.xml/ClassNamingConventions" />
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
<rule ref="rulesets/java/naming.xml/GenericsNaming" />
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
<rule ref="rulesets/java/naming.xml/LongVariable" />
```

## MethodNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.MethodNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/MethodNamingConventionsRule.java)

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
<rule ref="rulesets/java/naming.xml/MethodNamingConventions" />
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
<rule ref="rulesets/java/naming.xml/MethodWithSameNameAsEnclosingClass" />
```

## MisleadingVariableName

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
<rule ref="rulesets/java/naming.xml/MisleadingVariableName" />
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
<rule ref="rulesets/java/naming.xml/NoPackage" />
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
<rule ref="rulesets/java/naming.xml/PackageCase" />
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
<rule ref="rulesets/java/naming.xml/ShortClassName" />
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
<rule ref="rulesets/java/naming.xml/ShortMethodName" />
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
<rule ref="rulesets/java/naming.xml/ShortVariable" />
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
<rule ref="rulesets/java/naming.xml/SuspiciousConstantFieldName" />
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
<rule ref="rulesets/java/naming.xml/SuspiciousEqualsMethodName" />
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
<rule ref="rulesets/java/naming.xml/SuspiciousHashcodeMethodName" />
```

## VariableNamingConventions

**Since:** PMD 1.2

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.naming.VariableNamingConventionsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/naming/VariableNamingConventionsRule.java)

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
<rule ref="rulesets/java/naming.xml/VariableNamingConventions" />
```

