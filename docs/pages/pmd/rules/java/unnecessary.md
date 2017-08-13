---
title: Unnecessary
summary: The Unnecessary Ruleset contains a collection of rules for unnecessary code.
permalink: pmd_rules_java_unnecessary.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/unnecessary.xml
---
## UnnecessaryConversionTemporary

**Since:** PMD 0.1

**Priority:** Medium (3)

Avoid the use temporary objects when converting primitives to Strings. Use the static conversion methods
on the wrapper classes instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UnnecessaryConversionTemporaryRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UnnecessaryConversionTemporaryRule.java)

**Example(s):**

```
public String convert(int x) {
	String foo = new Integer(x).toString();	// this wastes an object
	
	return Integer.toString(x);				// preferred approach
}
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

```
public final class Foo {
    // This final modifier is not necessary, since the class is final
    // and thus, all methods are final
    private final void foo() {
    }
}
```

## UnnecessaryModifier

**Since:** PMD 1.02

**Priority:** Medium (3)

Fields in interfaces and annotations are automatically `public static final`, and methods are `public abstract`.
Classes, interfaces or annotations nested in an interface or annotation are automatically `public static`
(all nested interfaces and annotations are automatically static).
Nested enums are automatically `static`.
For historical reasons, modifiers which are implied by the context are accepted by the compiler, but are superfluous.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UnnecessaryModifierRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UnnecessaryModifierRule.java)

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

**Since:** PMD 1.3

**Priority:** Medium (3)

Avoid the use of unnecessary return statements.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UnnecessaryReturnRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UnnecessaryReturnRule.java)

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

**Since:** PMD 3.5

**Priority:** Medium (3)

An operation on an Immutable object (String, BigDecimal or BigInteger) won't change the object itself
since the result of the operation is a new object. Therefore, ignoring the operation result is an error.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UselessOperationOnImmutableRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UselessOperationOnImmutableRule.java)

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

**Since:** PMD 3.3

**Priority:** Medium (3)

The overriding method merely calls the same method defined in a superclass.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.unnecessary.UselessOverridingMethodRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/unnecessary/UselessOverridingMethodRule.java)

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

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Useless parentheses should be removed.

```
//Expression/PrimaryExpression/PrimaryPrefix/Expression
[count(*)=1][count(./CastExpression)=0][count(./ConditionalExpression[@Ternary='true'])=0]
[not(./AdditiveExpression[//Literal[@StringLiteral='true']])]
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
    count(./CastExpression)=0 and
    count(./ConditionalExpression[@Ternary='true'])=0 and
    count(./EqualityExpression/MultiplicativeExpression)=0]
|
//Expression/ConditionalExpression/PrimaryExpression/PrimaryPrefix/Expression[
    count(*)=1 and
    count(./CastExpression)=0 and
    count(./EqualityExpression)=0]
|
//Expression/AdditiveExpression[not(./PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral='true'])]/PrimaryExpression[1]/PrimaryPrefix/Expression[
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
    count(./CastExpression)=0 and
    count(./AndExpression)=0 and
    count(./InclusiveOrExpression)=0 and
    count(./ExclusiveOrExpression)=0 and
    count(./ConditionalExpression)=0 and
    count(./ConditionalAndExpression)=0 and
    count(./ConditionalOrExpression)=0 and
    count(./EqualityExpression)=0]
```

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

