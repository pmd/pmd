---
title: Finalizer
summary: These rules deal with different problems that can occur with finalizers.
permalink: pmd_rules_java_finalizers.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/finalizers.xml
---
## AvoidCallingFinalize
**Since:** 3.0

**Priority:** Medium (3)

The method Object.finalize() is called by the garbage collector on an object when garbage collection determines
that there are no more references to the object. It should not be invoked by application logic.

**Example(s):**
```
void foo() {
	Bar b = new Bar();
	b.finalize();
}
```

## EmptyFinalizer
**Since:** 1.5

**Priority:** Medium (3)

Empty finalize methods serve no purpose and should be removed.

**Example(s):**
```
public class Foo {
   protected void finalize() {}
}
```

## FinalizeDoesNotCallSuperFinalize
**Since:** 1.5

**Priority:** Medium (3)

If the finalize() is implemented, its last action should be to call super.finalize.

**Example(s):**
```
protected void finalize() {
	something();
	// neglected to call super.finalize()
}
```

## FinalizeOnlyCallsSuperFinalize
**Since:** 1.5

**Priority:** Medium (3)

If the finalize() is implemented, it should do something besides just calling super.finalize().

**Example(s):**
```
protected void finalize() {
	super.finalize();
}
```

## FinalizeOverloaded
**Since:** 1.5

**Priority:** Medium (3)

Methods named finalize() should not have parameters.  It is confusing and most likely an attempt to
overload Object.finalize(). It will not be called by the VM.

**Example(s):**
```
public class Foo {
   // this is confusing and probably a bug
   protected void finalize(int a) {
   }
}
```

## FinalizeShouldBeProtected
**Since:** 1.1

**Priority:** Medium (3)

When overriding the finalize(), the new method should be set as protected.  If made public, 
other classes may invoke it at inappropriate times.

**Example(s):**
```
public void finalize() {
	// do something
}
```

