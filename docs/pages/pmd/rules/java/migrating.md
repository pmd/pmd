---
title: Migration
summary: Contains rules about migrating from one JDK version to another.  Don't use these rules directly, rather, use a wrapper ruleset such as migrating_to_13.xml.
permalink: pmd_rules_java_migrating.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/migrating.xml
---
## AvoidAssertAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'assert' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='assert']
```

**Example(s):**

```
public class A {
	public  class foo {
		String assert = "foo";
	}
}
```

## AvoidEnumAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'enum' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='enum']
```

**Example(s):**

```
public class A {
	public  class foo {
		String enum = "foo";
	}
}
```

## ByteInstantiation

**Since:** PMD 4.0

**Priority:** Medium High (2)

Calling new Byte() causes memory allocation that can be avoided by the static Byte.valueOf().
It makes use of an internal cache that recycles earlier instances making it more memory efficient.

```
//PrimaryPrefix/AllocationExpression
[not (ArrayDimsAndInits)
and (ClassOrInterfaceType/@Image='Byte'
or ClassOrInterfaceType/@Image='java.lang.Byte')]
```

**Example(s):**

```
public class Foo {
	private Byte i = new Byte(0); // change to Byte i =	Byte.valueOf(0);
}
```

## IntegerInstantiation

**Since:** PMD 3.5

**Priority:** Medium High (2)

Calling new Integer() causes memory allocation that can be avoided by the static Integer.valueOf().
It makes use of an internal cache that recycles earlier instances making it more memory efficient.

```
//PrimaryPrefix
 /AllocationExpression
  [not (ArrayDimsAndInits)
   and (ClassOrInterfaceType/@Image='Integer'
    or ClassOrInterfaceType/@Image='java.lang.Integer')]
```

**Example(s):**

```
public class Foo {
	private Integer i = new Integer(0); // change to Integer i = Integer.valueOf(0);
}
```

## JUnit4SuitesShouldUseSuiteAnnotation

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit 3, test suites are indicated by the suite() method. In JUnit 4, suites are indicated
through the @RunWith(Suite.class) annotation.

```
//ClassOrInterfaceBodyDeclaration[MethodDeclaration/MethodDeclarator[@Image='suite']]
[MethodDeclaration/ResultType/Type/ReferenceType/ClassOrInterfaceType[@Image='Test' or @Image = 'junit.framework.Test']]
[not(MethodDeclaration/Block//ClassOrInterfaceType[@Image='JUnit4TestAdapter'])]
```

**Example(s):**

```
public class BadExample extends TestCase{

    public static Test suite(){
    	return new Suite();
    }
}

@RunWith(Suite.class)
@SuiteClasses( { TestOne.class, TestTwo.class })
public class GoodTest {
}
```

## JUnit4TestShouldUseAfterAnnotation

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit 3, the tearDown method was used to clean up all data entities required in running tests. 
JUnit 4 skips the tearDown method and executes all methods annotated with @After after running each test

```
//CompilationUnit[not(ImportDeclaration/Name[starts-with(@Image, "org.testng")])]
//ClassOrInterfaceBodyDeclaration[MethodDeclaration/MethodDeclarator[@Image='tearDown']]
[count(Annotation//Name[@Image='After'])=0]
```

**Example(s):**

```
public class MyTest {
    public void tearDown() {
        bad();
    }
}
public class MyTest2 {
    @After public void tearDown() {
        good();
    }
}
```

## JUnit4TestShouldUseBeforeAnnotation

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit 3, the setUp method was used to set up all data entities required in running tests. 
JUnit 4 skips the setUp method and executes all methods annotated with @Before before all tests

```
//CompilationUnit[not(ImportDeclaration/Name[starts-with(@Image, "org.testng")])]
//ClassOrInterfaceBodyDeclaration[MethodDeclaration/MethodDeclarator[@Image='setUp']]
[count(Annotation//Name[@Image='Before'])=0]
```

**Example(s):**

```
public class MyTest {
    public void setUp() {
        bad();
    }
}
public class MyTest2 {
    @Before public void setUp() {
        good();
    }
}
```

## JUnit4TestShouldUseTestAnnotation

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit 3, the framework executed all methods which started with the word test as a unit test. 
In JUnit 4, only methods annotated with the @Test annotation are executed.

```
//ClassOrInterfaceBodyDeclaration[MethodDeclaration[@Public='true']/MethodDeclarator[starts-with(@Image,'test')]]
[count(Annotation//Name[@Image='Test'])=0]
```

**Example(s):**

```
public class MyTest {
    public void testBad() {
        doSomething();
    }

	@Test
    public void testGood() {
        doSomething();
    }
}
```

## JUnitUseExpected

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit4, use the @Test(expected) annotation to denote tests that should throw exceptions.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.migrating.JUnitUseExpectedRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/migrating/JUnitUseExpectedRule.java)

**Example(s):**

```
public class MyTest {
	@Test
    public void testBad() {
        try {
            doSomething();
            fail("should have thrown an exception");
        } catch (Exception e) {
        }
    }

	@Test(expected=Exception.class)
    public void testGood() {
        doSomething();
    }
}
```

## LongInstantiation

**Since:** PMD 4.0

**Priority:** Medium High (2)

Calling new Long() causes memory allocation that can be avoided by the static Long.valueOf().
It makes use of an internal cache that recycles earlier instances making it more memory efficient.

```
//PrimaryPrefix
/AllocationExpression
[not (ArrayDimsAndInits)
and (ClassOrInterfaceType/@Image='Long'
or ClassOrInterfaceType/@Image='java.lang.Long')]
```

**Example(s):**

```
public class Foo {
	private Long i = new Long(0); // change to Long i = Long.valueOf(0);
}
```

## ReplaceEnumerationWithIterator

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Enumeration usages with the newer java.util.Iterator

```
//ImplementsList/ClassOrInterfaceType[@Image='Enumeration']
```

**Example(s):**

```
public class Foo implements Enumeration {
    private int x = 42;
    public boolean hasMoreElements() {
        return true;
    }
    public Object nextElement() {
        return String.valueOf(i++);
    }
}
```

## ReplaceHashtableWithMap

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Hashtable usage with the newer java.util.Map if thread safety is not required.

```
//Type/ReferenceType/ClassOrInterfaceType[@Image='Hashtable']
```

**Example(s):**

```
public class Foo {
	void bar() {
		Hashtable h = new Hashtable();
	}
}
```

## ReplaceVectorWithList

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Vector usages with the newer java.util.ArrayList if expensive thread-safe operations are not required.

```
//Type/ReferenceType/ClassOrInterfaceType[@Image='Vector']
```

**Example(s):**

```
public class Foo {
 void bar() {
    Vector v = new Vector();
 }
}
```

## ShortInstantiation

**Since:** PMD 4.0

**Priority:** Medium High (2)

Calling new Short() causes memory allocation that can be avoided by the static Short.valueOf().
It makes use of an internal cache that recycles earlier instances making it more memory efficient.

```
//PrimaryPrefix
/AllocationExpression
[not (ArrayDimsAndInits)
and (ClassOrInterfaceType/@Image='Short'
or ClassOrInterfaceType/@Image='java.lang.Short')]
```

**Example(s):**

```
public class Foo {
	private Short i = new Short(0); // change to Short i = Short.valueOf(0);
}
```

