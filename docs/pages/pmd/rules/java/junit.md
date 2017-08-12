---
title: JUnit
summary: These rules deal with different problems that can occur with JUnit tests.
permalink: pmd_rules_java_junit.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/junit.xml
---
## JUnitAssertionsShouldIncludeMessage

**Since:** PMD 1.04

**Priority:** Medium (3)

JUnit assertions should include an informative message - i.e., use the three-argument version of 
assertEquals(), not the two-argument version.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.JUnitAssertionsShouldIncludeMessageRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/JUnitAssertionsShouldIncludeMessageRule.java)

**Example(s):**

```
public class Foo extends TestCase {
 public void testSomething() {
  assertEquals("foo", "bar");
  // Use the form:
  // assertEquals("Foo does not equals bar", "foo", "bar");
  // instead
 }
}
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

```
import junit.framework.*;

public class Foo extends TestCase {
   public void setup() {}    // oops, should be setUp
   public void TearDown() {} // oops, should be tearDown
}
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

```
import junit.framework.*;

public class Foo extends TestCase {
   public void suite() {}         // oops, should be static
   private static void suite() {} // oops, should be public
}
```

## JUnitTestContainsTooManyAsserts

**Since:** PMD 5.0

**Priority:** Medium (3)

JUnit tests should not contain too many asserts.  Many asserts are indicative of a complex test, for which 
it is harder to verify correctness.  Consider breaking the test scenario into multiple, shorter test scenarios.  
Customize the maximum number of assertions used by this Rule to suit your needs.

```
//MethodDeclarator[(@Image[fn:matches(.,'^test')] or ../../Annotation/MarkerAnnotation/Name[@Image='Test']) and count(..//PrimaryPrefix/Name[@Image[fn:matches(.,'^assert')]]) > $maximumAsserts]
```

**Example(s):**

```
public class MyTestCase extends TestCase {
	// Ok
	public void testMyCaseWithOneAssert() {
		boolean myVar = false;		
		assertFalse("should be false", myVar);
	}

	// Bad, too many asserts (assuming max=1)
	public void testMyCaseWithMoreAsserts() {
		boolean myVar = false;		
		assertFalse("myVar should be false", myVar);
		assertEquals("should equals false", false, myVar);
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maximumAsserts|1|Maximum number of Asserts in a test method|

## JUnitTestsShouldIncludeAssert

**Since:** PMD 2.0

**Priority:** Medium (3)

JUnit tests should include at least one assertion.  This makes the tests more robust, and using assert 
with messages provide the developer a clearer idea of what the test does.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.JUnitTestsShouldIncludeAssertRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/JUnitTestsShouldIncludeAssertRule.java)

**Example(s):**

```
public class Foo extends TestCase {
   public void testSomething() {
      Bar b = findBar();
   // This is better than having a NullPointerException
   // assertNotNull("bar not found", b);
   b.work();
   }
}
```

## SimplifyBooleanAssertion

**Since:** PMD 3.6

**Priority:** Medium (3)

Avoid negation in an assertTrue or assertFalse test.

For example, rephrase:

   assertTrue(!expr);
   
as:

   assertFalse(expr);

```
//StatementExpression
[
.//Name[@Image='assertTrue' or  @Image='assertFalse']
and
PrimaryExpression/PrimarySuffix/Arguments/ArgumentList
 /Expression/UnaryExpressionNotPlusMinus[@Image='!']
/PrimaryExpression/PrimaryPrefix
]
[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType[pmd-java:typeof(@Image, 'junit.framework.TestCase','TestCase')] or //MarkerAnnotation/Name[pmd-java:typeof(@Image, 'org.junit.Test', 'Test')]]]
```

**Example(s):**

```
public class SimpleTest extends TestCase {
   public void testX() {
     assertTrue("not empty", !r.isEmpty()); // replace with assertFalse("not empty", r.isEmpty())
     assertFalse(!r.isEmpty()); // replace with assertTrue(r.isEmpty())
   }
}
```

## TestClassWithoutTestCases

**Since:** PMD 3.0

**Priority:** Medium (3)

Test classes end with the suffix Test. Having a non-test class with that name is not a good practice, 
since most people will assume it is a test case. Test classes have test methods named testXXX.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.TestClassWithoutTestCasesRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/TestClassWithoutTestCasesRule.java)

**Example(s):**

```
//Consider changing the name of the class if it is not a test
//Consider adding test methods if it is a test
public class CarTest {
   public static void main(String[] args) {
    // do something
   }
   // code
}
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

```
public class SimpleTest extends TestCase {
	public void testX() {
		assertTrue(true);		 // serves no real purpose
	}
}
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

```
public class FooTest extends TestCase {
	void testCode() {
		Object a, b;
		assertTrue(a.equals(b)); 					// bad usage
		assertEquals(?a should equals b?, a, b);	// good usage
	}
}
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

```
public class FooTest extends TestCase {
  void testCode() {
   Object a = doSomething();
   assertTrue(a==null); // bad usage
   assertNull(a);  // good usage
   assertTrue(a != null); // bad usage
   assertNotNull(a);  // good usage
  }
 }
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

```
public class FooTest extends TestCase {
 void testCode() {
  Object a, b;
  assertTrue(a == b); // bad usage
  assertSame(a, b);  // good usage
 }
}
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

```
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

