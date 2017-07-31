---
title: Unnecessary
summary: The Unnecessary Ruleset contains a collection of rules for unnecessary code.
permalink: pmd_rules_java_unnecessary.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/unnecessary.xml
---
## UnnecessaryConversionTemporary
**Since:** 0.1

**Priority:** Medium (3)

Avoid the use temporary objects when converting primitives to Strings. Use the static conversion methods
on the wrapper classes instead.

**Example(s):**
```
public String convert(int x) {
	String foo = new Integer(x).toString();	// this wastes an object
	
	return Integer.toString(x);				// preferred approach
}
```

## UnnecessaryFinalModifier
**Since:** 3.0

**Priority:** Medium (3)

When a class has the final modifier, all the methods are automatically final and do not need to be
tagged as such. Similarly, methods that can't be overridden (private methods, methods of anonymous classes,
methods of enum instance) do not need to be tagged either.

**Example(s):**
```
public final class Foo {
    // This final modifier is not necessary, since the class is final
    // and thus, all methods are final
    private final void foo() {
    }
}
```

## UnnecessaryModifier
**Since:** 1.02

**Priority:** Medium (3)

Fields in interfaces and annotations are automatically `public static final`, and methods are `public abstract`.
Classes, interfaces or annotations nested in an interface or annotation are automatically `public static`
(all nested interfaces and annotations are automatically static).
Nested enums are automatically `static`.
For historical reasons, modifiers which are implied by the context are accepted by the compiler, but are superfluous.

**Example(s):**
```
public @interface Annotation {
  public abstract void bar(); 		// both abstract and public are ignored by the compiler
  public static final int X = 0; 	// public, static, and final all ignored
  public static class Bar {} 		// public, static ignored
  public static interface Baz {} 	// ditto
}
public interface Foo {
  public abstract void bar(); 		// both abstract and public are ignored by the compiler
  public static final int X = 0; 	// public, static, and final all ignored
  public static class Bar {} 		// public, static ignored
  public static interface Baz {} 	// ditto
}
public class Bar {
  public static interface Baz {} // static ignored
  public static enum FoorBar { // static ignored
    FOO;
  }
}
```

## UnnecessaryReturn
**Since:** 1.3

**Priority:** Medium (3)

Avoid the use of unnecessary return statements.

**Example(s):**
```
public class Foo {
  public void bar() {
    int x = 42;
    return;
  }
}
```

## UnusedNullCheckInEquals
**Since:** 3.5

**Priority:** Medium (3)

After checking an object reference for null, you should invoke equals() on that object rather than passing it to another object's equals() method.

**Example(s):**
```
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

## UselessOperationOnImmutable
**Since:** 3.5

**Priority:** Medium (3)

An operation on an Immutable object (String, BigDecimal or BigInteger) won't change the object itself
since the result of the operation is a new object. Therefore, ignoring the operation result is an error.

**Example(s):**
```
import java.math.*;

class Test {
  void method1() {
    BigDecimal bd=new BigDecimal(10);
    bd.add(new BigDecimal(5)); 		// this will trigger the rule
  }
  void method2() {
    BigDecimal bd=new BigDecimal(10);
    bd = bd.add(new BigDecimal(5)); // this won't trigger the rule
  }
}
```

## UselessOverridingMethod
**Since:** 3.3

**Priority:** Medium (3)

The overriding method merely calls the same method defined in a superclass.

**Example(s):**
```
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

## UselessParentheses
**Since:** 5.0

**Priority:** Medium Low (4)

Useless parentheses should be removed.

**Example(s):**
```
public class Foo {

   private int _bar1;
   private Integer _bar2;

   public void setBar(int n) {
      _bar1 = Integer.valueOf((n)); // here
      _bar2 = (n); // and here
   }

}
```

## UselessQualifiedThis
**Since:** 5.4.0

**Priority:** Medium (3)

Look for qualified this usages in the same class.

**Example(s):**
```
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

