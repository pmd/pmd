---
title: Best Practices
summary: The Best Practices category contains rules...  It fully contains these previous rulesets:  *   sunsecure
permalink: pmd_rules_java_bestpractices.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/category/java/bestpractices.xml
keywords: Best Practices, ArrayIsStoredDirectly, AvoidPrintStackTrace, AvoidReassigningParameters, AvoidStringBufferField, AvoidUsingHardCodedIP, GuardDebugLogging, GuardLogStatement, GuardLogStatementJavaUtil, JUnit4SuitesShouldUseSuiteAnnotation, JUnit4TestShouldUseAfterAnnotation, JUnit4TestShouldUseBeforeAnnotation, JUnit4TestShouldUseTestAnnotation, JUnitAssertionsShouldIncludeMessage, JUnitTestContainsTooManyAsserts, JUnitTestsShouldIncludeAssert, JUnitUseExpected, LooseCoupling, MethodReturnsInternalArray, NullAssignment, PositionLiteralsFirstInCaseInsensitiveComparisons, PositionLiteralsFirstInComparisons, ReplaceEnumerationWithIterator, ReplaceHashtableWithMap, ReplaceVectorWithList, SwitchStmtsShouldHaveDefault, SystemPrintln, UseVarargs
---
## ArrayIsStoredDirectly

**Since:** PMD 2.2

**Priority:** Medium (3)

Constructors and methods receiving arrays should clone objects and store the copy.
This prevents future changes from the user from affecting the original array.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.sunsecure.ArrayIsStoredDirectlyRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/sunsecure/ArrayIsStoredDirectlyRule.java)

**Example(s):**

``` java
public class Foo {
    private String [] x;
        public void foo (String [] param) {
        // Don't do this, make a copy of the array at least
        this.x=param;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/ArrayIsStoredDirectly" />
```

## AvoidPrintStackTrace

**Since:** PMD 3.2

**Priority:** Medium (3)

Avoid printStackTrace(); use a logger call instead.

```
//PrimaryExpression
 [PrimaryPrefix/Name[contains(@Image,'printStackTrace')]]
 [PrimarySuffix[not(boolean(Arguments/ArgumentList/Expression))]]
```

**Example(s):**

``` java
class Foo {
    void bar() {
        try {
            // do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/AvoidPrintStackTrace" />
```

## AvoidReassigningParameters

**Since:** PMD 1.0

**Priority:** Medium High (2)

Reassigning values to incoming parameters is not recommended.  Use temporary local variables instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.design.AvoidReassigningParametersRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/design/AvoidReassigningParametersRule.java)

**Example(s):**

``` java
public class Foo {
  private void foo(String bar) {
    bar = "something else";
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/AvoidReassigningParameters" />
```

## AvoidStringBufferField

**Since:** PMD 4.2

**Priority:** Medium (3)

StringBuffers/StringBuilders can grow considerably, and so may become a source of memory leaks
if held within objects with long lifetimes.

```
//FieldDeclaration/Type/ReferenceType/ClassOrInterfaceType[@Image = 'StringBuffer' or @Image = 'StringBuilder']
```

**Example(s):**

``` java
public class Foo {
    private StringBuffer buffer;    // potential memory leak as an instance variable;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/AvoidStringBufferField" />
```

## AvoidUsingHardCodedIP

**Since:** PMD 4.1

**Priority:** Medium (3)

Application with hard-coded IP addresses can become impossible to deploy in some cases.
Externalizing IP adresses is preferable.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidUsingHardCodedIPRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidUsingHardCodedIPRule.java)

**Example(s):**

``` java
public class Foo {
    private String ip = "127.0.0.1";     // not recommended
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkAddressTypes|[IPv4, IPv6, IPv4 mapped IPv6]|Check for IP address types.|
|pattern|^"[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}"$|Regular Expression|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/AvoidUsingHardCodedIP" />
```

## GuardDebugLogging

**Since:** PMD 4.3

**Priority:** Medium (3)

When log messages are composed by concatenating strings, the whole section should be guarded
by a isDebugEnabled() check to avoid performance and memory issues.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardDebugLoggingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardDebugLoggingRule.java)

**Example(s):**

``` java
public class Test {
    private static final Log __log = LogFactory.getLog(Test.class);
    public void test() {
        // okay:
        __log.debug("log something");

        // okay:
        __log.debug("log something with exception", e);

        // bad:
        __log.debug("log something" + " and " + "concat strings");

        // bad:
        __log.debug("log something" + " and " + "concat strings", e);

        // good:
        if (__log.isDebugEnabled()) {
        __log.debug("bla" + "",e );
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/GuardDebugLogging" />
```

## GuardLogStatement

**Since:** PMD 5.1.0

**Priority:** Medium High (2)

Whenever using a log level, one should check if the loglevel is actually enabled, or
otherwise skip the associate String creation and manipulation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardLogStatementRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardLogStatementRule.java)

**Example(s):**

``` java
// Add this for performance
    if (log.isDebugEnabled() { ...
        log.debug("log something" + " and " + "concat strings");
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/GuardLogStatement" />
```

## GuardLogStatementJavaUtil

**Since:** PMD 5.1.0

**Priority:** Medium High (2)

Whenever using a log level, one should check if the loglevel is actually enabled, or
otherwise skip the associate String creation and manipulation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardLogStatementJavaUtilRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardLogStatementJavaUtilRule.java)

**Example(s):**

``` java
//...
// Add this for performance
if (log.isLoggable(Level.FINE)) {
    log.fine("log something" + " and " + "concat strings");
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/GuardLogStatementJavaUtil" />
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
<rule ref="category/java/bestpractices.xml/JUnit4SuitesShouldUseSuiteAnnotation" />
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
<rule ref="category/java/bestpractices.xml/JUnit4TestShouldUseAfterAnnotation" />
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
<rule ref="category/java/bestpractices.xml/JUnit4TestShouldUseBeforeAnnotation" />
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
<rule ref="category/java/bestpractices.xml/JUnit4TestShouldUseTestAnnotation" />
```

## JUnitAssertionsShouldIncludeMessage

**Since:** PMD 1.04

**Priority:** Medium (3)

JUnit assertions should include an informative message - i.e., use the three-argument version of 
assertEquals(), not the two-argument version.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.JUnitAssertionsShouldIncludeMessageRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/JUnitAssertionsShouldIncludeMessageRule.java)

**Example(s):**

``` java
public class Foo extends TestCase {
    public void testSomething() {
        assertEquals("foo", "bar");
        // Use the form:
        // assertEquals("Foo does not equals bar", "foo", "bar");
        // instead
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/JUnitAssertionsShouldIncludeMessage" />
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

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/JUnitTestContainsTooManyAsserts" />
```

## JUnitTestsShouldIncludeAssert

**Since:** PMD 2.0

**Priority:** Medium (3)

JUnit tests should include at least one assertion.  This makes the tests more robust, and using assert 
with messages provide the developer a clearer idea of what the test does.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.junit.JUnitTestsShouldIncludeAssertRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/junit/JUnitTestsShouldIncludeAssertRule.java)

**Example(s):**

``` java
public class Foo extends TestCase {
   public void testSomething() {
      Bar b = findBar();
   // This is better than having a NullPointerException
   // assertNotNull("bar not found", b);
   b.work();
   }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/JUnitTestsShouldIncludeAssert" />
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
<rule ref="category/java/bestpractices.xml/JUnitUseExpected" />
```

## LooseCoupling

**Since:** PMD 0.7

**Priority:** Medium (3)

The use of implementation types (i.e., HashSet) as object references limits your ability to use alternate
implementations in the future as requirements change. Whenever available, referencing objects
by their interface types (i.e, Set) provides much more flexibility.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.coupling.LooseCouplingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/coupling/LooseCouplingRule.java)

**Example(s):**

``` java
import java.util.ArrayList;
import java.util.HashSet;

public class Bar {
    // sub-optimal approach
    private ArrayList<SomeType> list = new ArrayList<>();

    public HashSet<SomeType> getFoo() {
        return new HashSet<SomeType>();
    }

    // preferred approach
    private List<SomeType> list = new ArrayList<>();

    public Set<SomeType> getFoo() {
        return new HashSet<SomeType>();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/LooseCoupling" />
```

## MethodReturnsInternalArray

**Since:** PMD 2.2

**Priority:** Medium (3)

Exposing internal arrays to the caller violates object encapsulation since elements can be 
removed or replaced outside of the object that owns it. It is safer to return a copy of the array.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.sunsecure.MethodReturnsInternalArrayRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/sunsecure/MethodReturnsInternalArrayRule.java)

**Example(s):**

``` java
public class SecureSystem {
    UserData [] ud;
    public UserData [] getUserData() {
        // Don't return directly the internal array, return a copy
        return ud;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/MethodReturnsInternalArray" />
```

## NullAssignment

**Since:** PMD 1.02

**Priority:** Medium (3)

Assigning a "null" to a variable (outside of its declaration) is usually bad form.  Sometimes, this type
of assignment is an indication that the programmer doesn't completely understand what is going on in the code.

NOTE: This sort of assignment may used in some cases to dereference objects and encourage garbage collection.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.controversial.NullAssignmentRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/controversial/NullAssignmentRule.java)

**Example(s):**

``` java
public void bar() {
  Object x = null; // this is OK
  x = new Object();
     // big, complex piece of code here
  x = null; // this is not required
     // big, complex piece of code here
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/NullAssignment" />
```

## PositionLiteralsFirstInCaseInsensitiveComparisons

**Since:** PMD 5.1

**Priority:** Medium (3)

Position literals first in comparisons, if the second argument is null then NullPointerExceptions
can be avoided, they will just return false.

```
//PrimaryExpression[
        PrimaryPrefix[Name
                [
    (ends-with(@Image, '.equalsIgnoreCase'))
                ]
        ]
        [
                   (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal)
    and
    ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
        ]
]
[not(ancestor::Expression/ConditionalAndExpression//EqualityExpression[@Image='!=']//NullLiteral)]
[not(ancestor::Expression/ConditionalOrExpression//EqualityExpression[@Image='==']//NullLiteral)]
```

**Example(s):**

``` java
class Foo {
  boolean bar(String x) {
    return x.equalsIgnoreCase("2"); // should be "2".equalsIgnoreCase(x)
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/PositionLiteralsFirstInCaseInsensitiveComparisons" />
```

## PositionLiteralsFirstInComparisons

**Since:** PMD 3.3

**Priority:** Medium (3)

Position literals first in comparisons, if the second argument is null then NullPointerExceptions
can be avoided, they will just return false.

```
//PrimaryExpression[
    PrimaryPrefix[Name[(ends-with(@Image, '.equals'))]]
        [
            (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral='true'])
            and
            ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
        ]
]
[not(ancestor::Expression/ConditionalAndExpression//EqualityExpression[@Image='!=']//NullLiteral)]
[not(ancestor::Expression/ConditionalOrExpression//EqualityExpression[@Image='==']//NullLiteral)]
```

**Example(s):**

``` java
class Foo {
  boolean bar(String x) {
    return x.equals("2"); // should be "2".equals(x)
  }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/PositionLiteralsFirstInComparisons" />
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
<rule ref="category/java/bestpractices.xml/ReplaceEnumerationWithIterator" />
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
<rule ref="category/java/bestpractices.xml/ReplaceHashtableWithMap" />
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
<rule ref="category/java/bestpractices.xml/ReplaceVectorWithList" />
```

## SwitchStmtsShouldHaveDefault

**Since:** PMD 1.0

**Priority:** Medium (3)

All switch statements should include a default option to catch any unspecified values.

```
//SwitchStatement[not(SwitchLabel[@Default='true'])]
```

**Example(s):**

``` java
public void bar() {
    int x = 2;
    switch (x) {
      case 1: int j = 6;
      case 2: int j = 8;
          // missing default: here
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/SwitchStmtsShouldHaveDefault" />
```

## SystemPrintln

**Since:** PMD 2.1

**Priority:** Medium High (2)

References to System.(out|err).print are usually intended for debugging purposes and can remain in
the codebase even in production code. By using a logger one can enable/disable this behaviour at
will (and by priority) and avoid clogging the Standard out log.

```
//Name[
    starts-with(@Image, 'System.out.print')
    or
    starts-with(@Image, 'System.err.print')
    ]
```

**Example(s):**

``` java
class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());
    public void testA () {
        System.out.println("Entering test");
        // Better use this
        log.fine("Entering test");
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/SystemPrintln" />
```

## UseVarargs

**Since:** PMD 5.0

**Priority:** Medium Low (4)

**Minimum Language Version:** Java 1.5

Java 5 introduced the varargs parameter declaration for methods and constructors.  This syntactic
sugar provides flexibility for users of these methods and constructors, allowing them to avoid
having to deal with the creation of an array.

```
//FormalParameters/FormalParameter
    [position()=last()]
    [@Array='true']
    [@Varargs='false']
    [not (./Type/ReferenceType[@Array='true'][PrimitiveType[@Image='byte']])]
    [not (./Type/ReferenceType[ClassOrInterfaceType[@Image='Byte']])]
    [not (./Type/PrimitiveType[@Image='byte'])]
    [not (ancestor::MethodDeclaration/preceding-sibling::Annotation/*/Name[@Image='Override'])]
    [not(
        ancestor::MethodDeclaration
            [@Public='true' and @Static='true']
            [child::ResultType[@Void='true']] and
        ancestor::MethodDeclarator[@Image='main'] and
        ..[@ParameterCount='1'] and
        ./Type/ReferenceType[ClassOrInterfaceType[@Image='String']]
    )]
```

**Example(s):**

``` java
public class Foo {
    public void foo(String s, Object[] args) {
        // Do something here...
    }

    public void bar(String s, Object... args) {
        // Ahh, varargs tastes much better...
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="category/java/bestpractices.xml/UseVarargs" />
```

