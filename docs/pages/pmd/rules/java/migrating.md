---
title: Migration
summary: Contains rules about migrating from one JDK version to another.  Don't use these rules directly, rather, use a wrapper ruleset such as migrating_to_13.xml.
permalink: pmd_rules_java_migrating.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/migrating.xml
keywords: Migration, ReplaceVectorWithList, ReplaceHashtableWithMap, ReplaceEnumerationWithIterator, AvoidEnumAsIdentifier, AvoidAssertAsIdentifier, IntegerInstantiation, ByteInstantiation, ShortInstantiation, LongInstantiation, JUnit4TestShouldUseBeforeAnnotation, JUnit4TestShouldUseAfterAnnotation, JUnit4TestShouldUseTestAnnotation, JUnit4SuitesShouldUseSuiteAnnotation, JUnitUseExpected, ForLoopCanBeForeach
---
## AvoidAssertAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'assert' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='assert']
```

**Example(s):**

``` java
public class A {
    public class Foo {
        String assert = "foo";
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/AvoidAssertAsIdentifier" />
```

## AvoidEnumAsIdentifier

**Since:** PMD 3.4

**Priority:** Medium High (2)

Use of the term 'enum' will conflict with newer versions of Java since it is a reserved word.

```
//VariableDeclaratorId[@Image='enum']
```

**Example(s):**

``` java
public class A {
    public class Foo {
        String enum = "foo";
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/AvoidEnumAsIdentifier" />
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

``` java
public class Foo {
    private Byte i = new Byte(0); // change to Byte i = Byte.valueOf(0);
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ByteInstantiation" />
```

## ForLoopCanBeForeach

**Since:** PMD 6.0

**Priority:** Medium (3)

**Minimum Language Version:** Java 1.5

Reports loops that can be safely replaced with the foreach syntax. The rule considers loops over
            lists, arrays and iterators. A loop is safe to replace if it only uses the index variable to
            access an element of the list or array, only has one update statement, and loops through *every*
            element of the list or array left to right.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.migrating.ForLoopCanBeForeachRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/migrating/ForLoopCanBeForeachRule.java)

**Example(s):**

``` java
public class MyClass {
  void loop(List<String> l) {
    for (int i = 0; i < l.size(); i++) { // pre Java 1.5
      System.out.println(l.get(i));
    }

    for (String s : l) {        // post Java 1.5
      System.out.println(s);
    }
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ForLoopCanBeForeach" />
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

``` java
public class Foo {
    private Integer i = new Integer(0); // change to Integer i = Integer.valueOf(0);
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/IntegerInstantiation" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/JUnit4SuitesShouldUseSuiteAnnotation" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/JUnit4TestShouldUseAfterAnnotation" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/JUnit4TestShouldUseBeforeAnnotation" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/JUnit4TestShouldUseTestAnnotation" />
```

## JUnitUseExpected

**Since:** PMD 4.0

**Priority:** Medium (3)

In JUnit4, use the @Test(expected) annotation to denote tests that should throw exceptions.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.migrating.JUnitUseExpectedRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/migrating/JUnitUseExpectedRule.java)

**Example(s):**

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/JUnitUseExpected" />
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

``` java
public class Foo {
    private Long i = new Long(0); // change to Long i = Long.valueOf(0);
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/LongInstantiation" />
```

## ReplaceEnumerationWithIterator

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Enumeration usages with the newer java.util.Iterator

```
//ImplementsList/ClassOrInterfaceType[@Image='Enumeration']
```

**Example(s):**

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ReplaceEnumerationWithIterator" />
```

## ReplaceHashtableWithMap

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Hashtable usage with the newer java.util.Map if thread safety is not required.

```
//Type/ReferenceType/ClassOrInterfaceType[@Image='Hashtable']
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        Hashtable h = new Hashtable();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ReplaceHashtableWithMap" />
```

## ReplaceVectorWithList

**Since:** PMD 3.4

**Priority:** Medium (3)

Consider replacing Vector usages with the newer java.util.ArrayList if expensive thread-safe operations are not required.

```
//Type/ReferenceType/ClassOrInterfaceType[@Image='Vector']
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        Vector v = new Vector();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ReplaceVectorWithList" />
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

``` java
public class Foo {
    private Short i = new Short(0); // change to Short i = Short.valueOf(0);
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/migrating.xml/ShortInstantiation" />
```

