---
title: Clone Implementation
summary: The Clone Implementation ruleset contains a collection of rules that find questionable usages of the clone() method.
permalink: pmd_rules_java_clone.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/clone.xml
keywords: Clone Implementation, ProperCloneImplementation, CloneThrowsCloneNotSupportedException, CloneMethodMustImplementCloneable, CloneMethodReturnTypeMustMatchClassName, CloneMethodMustBePublic
---
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

## CloneMethodMustImplementCloneable

**Since:** PMD 1.9

**Priority:** Medium (3)

The method clone() should only be implemented if the class implements the Cloneable interface with the exception of a final method that only throws CloneNotSupportedException.

```
//ClassOrInterfaceDeclaration
[not(./ExtendsList/ClassOrInterfaceType[@Image='Cloneable'])]
[not(./ImplementsList/ClassOrInterfaceType[@Image='Cloneable'])]
/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
[MethodDeclaration
[MethodDeclarator[@Image
= 'clone' and count(FormalParameters/*) = 0]]
[not((../MethodDeclaration[@Final = 'true'] or ancestor::ClassOrInterfaceDeclaration[1][@Final = 'true'])
and Block[count(BlockStatement)=1]
/BlockStatement/Statement/ThrowStatement/Expression
/PrimaryExpression/PrimaryPrefix/AllocationExpression
/ClassOrInterfaceType[@Image = 'CloneNotSupportedException'])]]
```

**Example(s):**

``` java
public class MyClass {
 public Object clone() throws CloneNotSupportedException {
  return foo;
 }
}
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

