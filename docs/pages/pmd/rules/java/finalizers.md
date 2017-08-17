---
title: Finalizer
summary: These rules deal with different problems that can occur with finalizers.
permalink: pmd_rules_java_finalizers.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/finalizers.xml
keywords: Finalizer, EmptyFinalizer, FinalizeOnlyCallsSuperFinalize, FinalizeOverloaded, FinalizeDoesNotCallSuperFinalize, FinalizeShouldBeProtected, AvoidCallingFinalize
---
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

