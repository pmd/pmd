---
title: Basic
summary: The Basic ruleset contains a collection of good practices which should be followed.
permalink: pmd_rules_java_basic.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/basic.xml
keywords: Basic, JumbledIncrementer, ForLoopShouldBeWhileLoop, OverrideBothEqualsAndHashcode, DoubleCheckedLocking, ReturnFromFinallyBlock, UnconditionalIfStatement, BooleanInstantiation, CollapsibleIfStatements, ClassCastExceptionWithToArray, AvoidDecimalLiteralsInBigDecimalConstructor, MisplacedNullCheck, AvoidThreadGroup, BrokenNullCheck, BigIntegerInstantiation, AvoidUsingOctalValues, AvoidUsingHardCodedIP, CheckResultSet, AvoidMultipleUnaryOperators, ExtendsObject, CheckSkipResult, AvoidBranchingStatementAsLastInLoop, DontCallThreadRun, DontUseFloatTypeForLoopIndices, SimplifiedTernary
---
## AvoidBranchingStatementAsLastInLoop

**Since:** PMD 5.0

**Priority:** Medium High (2)

Using a branching statement as the last part of a loop may be a bug, and/or is confusing.
Ensure that the usage is not a bug, or consider using another approach.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidBranchingStatementAsLastInLoopRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidBranchingStatementAsLastInLoopRule.java)

**Example(s):**

``` java
// unusual use of branching statement in a loop
for (int i = 0; i < 10; i++) {
    if (i*i <= 25) {
        continue;
    }
    break;
}

  // this makes more sense...
for (int i = 0; i < 10; i++) {
    if (i*i > 25) {
        break;
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkReturnLoopTypes|[for, do, while]|Check for return statements in loop types|
|checkContinueLoopTypes|[for, do, while]|Check for continue statements in loop types|
|checkBreakLoopTypes|[for, do, while]|Check for break statements in loop types|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/AvoidBranchingStatementAsLastInLoop" />
```

## AvoidDecimalLiteralsInBigDecimalConstructor

**Since:** PMD 3.4

**Priority:** Medium (3)

One might assume that the result of "new BigDecimal(0.1)" is exactly equal to 0.1, but it is actually
equal to .1000000000000000055511151231257827021181583404541015625.
This is because 0.1 cannot be represented exactly as a double (or as a binary fraction of any finite
length). Thus, the long value that is being passed in to the constructor is not exactly equal to 0.1,
appearances notwithstanding.

The (String) constructor, on the other hand, is perfectly predictable: 'new BigDecimal("0.1")' is
exactly equal to 0.1, as one would expect.  Therefore, it is generally recommended that the
(String) constructor be used in preference to this one.

```
//AllocationExpression
[ClassOrInterfaceType[@Image="BigDecimal"]]
[Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix
    [
        Literal[(not(ends-with(@Image,'"'))) and contains(@Image,".")]
        or
        Name[ancestor::Block/BlockStatement/LocalVariableDeclaration
                [Type[PrimitiveType[@Image='double' or @Image='float']
                      or ReferenceType/ClassOrInterfaceType[@Image='Double' or @Image='Float']]]
                /VariableDeclarator/VariableDeclaratorId/@Image = @Image
            ]
        or
        Name[ancestor::MethodDeclaration/MethodDeclarator/FormalParameters/FormalParameter
                [Type[PrimitiveType[@Image='double' or @Image='float']
                      or ReferenceType/ClassOrInterfaceType[@Image='Double' or @Image='Float']]]
                /VariableDeclaratorId/@Image = @Image
            ]
    ]
]
```

**Example(s):**

``` java
BigDecimal bd = new BigDecimal(1.123);       // loss of precision, this would trigger the rule

BigDecimal bd = new BigDecimal("1.123");     // preferred approach

BigDecimal bd = new BigDecimal(12);          // preferred approach, ok for integer values
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/AvoidDecimalLiteralsInBigDecimalConstructor" />
```

## AvoidMultipleUnaryOperators

**Since:** PMD 4.2

**Priority:** Medium High (2)

The use of multiple unary operators may be problematic, and/or confusing.
Ensure that the intended usage is not a bug, or consider simplifying the expression.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidMultipleUnaryOperatorsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidMultipleUnaryOperatorsRule.java)

**Example(s):**

``` java
// These are typo bugs, or at best needlessly complex and confusing:
int i = - -1;
int j = + - +1;
int z = ~~2;
boolean b = !!true;
boolean c = !!!true;

// These are better:
int i = 1;
int j = -1;
int z = 2;
boolean b = true;
boolean c = false;

// And these just make your brain hurt:
int i = ~-2;
int j = -~7;
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/AvoidMultipleUnaryOperators" />
```

## AvoidThreadGroup

**Since:** PMD 3.6

**Priority:** Medium (3)

Avoid using java.lang.ThreadGroup; although it is intended to be used in a threaded environment
it contains methods that are not thread-safe.

```
//AllocationExpression/ClassOrInterfaceType[pmd-java:typeof(@Image, 'java.lang.ThreadGroup')]|
//PrimarySuffix[contains(@Image, 'getThreadGroup')]
```

**Example(s):**

``` java
public class Bar {
    void buz() {
        ThreadGroup tg = new ThreadGroup("My threadgroup");
        tg = new ThreadGroup(tg, "my thread group");
        tg = Thread.currentThread().getThreadGroup();
        tg = System.getSecurityManager().getThreadGroup();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/AvoidThreadGroup" />
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
<rule ref="rulesets/java/basic.xml/AvoidUsingHardCodedIP" />
```

## AvoidUsingOctalValues

**Since:** PMD 3.9

**Priority:** Medium (3)

Integer literals should not start with zero since this denotes that the rest of literal will be
interpreted as an octal value.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.AvoidUsingOctalValuesRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/AvoidUsingOctalValuesRule.java)

**Example(s):**

``` java
int i = 012;    // set i with 10 not 12
int j = 010;    // set j with 8 not 10
k = i * j;      // set k with 80 not 120
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strict|false|Detect violations between 00 and 07|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/AvoidUsingOctalValues" />
```

## BigIntegerInstantiation

**Since:** PMD 3.9

**Priority:** Medium (3)

Don't create instances of already existing BigInteger (BigInteger.ZERO, BigInteger.ONE) and
for Java 1.5 onwards, BigInteger.TEN and BigDecimal (BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN)

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.BigIntegerInstantiationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/BigIntegerInstantiationRule.java)

**Example(s):**

``` java
BigInteger bi = new BigInteger(1);       // reference BigInteger.ONE instead
BigInteger bi2 = new BigInteger("0");    // reference BigInteger.ZERO instead
BigInteger bi3 = new BigInteger(0.0);    // reference BigInteger.ZERO instead
BigInteger bi4;
bi4 = new BigInteger(0);                 // reference BigInteger.ZERO instead
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/BigIntegerInstantiation" />
```

## BooleanInstantiation

**Since:** PMD 1.2

**Priority:** Medium High (2)

Avoid instantiating Boolean objects; you can reference Boolean.TRUE, Boolean.FALSE, or call Boolean.valueOf() instead.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.BooleanInstantiationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/BooleanInstantiationRule.java)

**Example(s):**

``` java
Boolean bar = new Boolean("true");        // unnecessary creation, just reference Boolean.TRUE;
Boolean buz = Boolean.valueOf(false);    // ...., just reference Boolean.FALSE;
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/BooleanInstantiation" />
```

## BrokenNullCheck

**Since:** PMD 3.8

**Priority:** Medium High (2)

The null check is broken since it will throw a NullPointerException itself.
It is likely that you used || instead of && or vice versa.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.BrokenNullCheckRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/BrokenNullCheckRule.java)

**Example(s):**

``` java
public String bar(String string) {
  // should be &&
    if (string!=null || !string.equals(""))
        return string;
  // should be ||
    if (string==null && string.equals(""))
        return string;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/BrokenNullCheck" />
```

## CheckResultSet

**Since:** PMD 4.1

**Priority:** Medium (3)

Always check the return values of navigation methods (next, previous, first, last) of a ResultSet.
If the value return is 'false', it should be handled properly.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.CheckResultSetRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/CheckResultSetRule.java)

**Example(s):**

``` java
Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
rst.next();     // what if it returns false? bad form
String firstName = rst.getString(1);

Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
if (rst.next()) {    // result is properly examined and used
    String firstName = rst.getString(1);
    } else  {
        // handle missing data
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/CheckResultSet" />
```

## CheckSkipResult

**Since:** PMD 5.0

**Priority:** Medium (3)

The skip() method may skip a smaller number of bytes than requested. Check the returned value to find out if it was the case or not.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.CheckSkipResultRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/CheckSkipResultRule.java)

**Example(s):**

``` java
public class Foo {

   private FileInputStream _s = new FileInputStream("file");

   public void skip(int n) throws IOException {
      _s.skip(n); // You are not sure that exactly n bytes are skipped
   }

   public void skipExactly(int n) throws IOException {
      while (n != 0) {
         long skipped = _s.skip(n);
         if (skipped == 0)
            throw new EOFException();
         n -= skipped;
      }
   }
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/CheckSkipResult" />
```

## ClassCastExceptionWithToArray

**Since:** PMD 3.4

**Priority:** Medium (3)

When deriving an array of a specific class from your Collection, one should provide an array of
the same class as the parameter of the toArray() method. Doing otherwise you will will result
in a ClassCastException.

```
//CastExpression[Type/ReferenceType/ClassOrInterfaceType[@Image !=
"Object"]]/PrimaryExpression
[
 PrimaryPrefix/Name[ends-with(@Image, '.toArray')]
 and
 PrimarySuffix/Arguments[count(*) = 0]
and
count(PrimarySuffix) = 1
]
```

**Example(s):**

``` java
Collection c = new ArrayList();
Integer obj = new Integer(1);
c.add(obj);

    // this would trigger the rule (and throw a ClassCastException if executed)
Integer[] a = (Integer [])c.toArray();

   // this is fine and will not trigger the rule
Integer[] b = (Integer [])c.toArray(new Integer[c.size()]);
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/ClassCastExceptionWithToArray" />
```

## CollapsibleIfStatements

**Since:** PMD 3.1

**Priority:** Medium (3)

Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.

```
//IfStatement[@Else='false']/Statement
 /IfStatement[@Else='false']
 |
//IfStatement[@Else='false']/Statement
 /Block[count(BlockStatement)=1]/BlockStatement
  /Statement/IfStatement[@Else='false']
```

**Example(s):**

``` java
void bar() {
    if (x) {            // original implementation
        if (y) {
            // do stuff
        }
    }
}

void bar() {
    if (x && y) {        // optimized implementation
        // do stuff
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/CollapsibleIfStatements" />
```

## DontCallThreadRun

**Since:** PMD 4.3

**Priority:** Medium Low (4)

Explicitly calling Thread.run() method will execute in the caller's thread of control.  Instead, call Thread.start() for the intended behavior.

```
//StatementExpression/PrimaryExpression
[
    PrimaryPrefix
    [
        ./Name[ends-with(@Image, '.run') or @Image = 'run']
        and substring-before(Name/@Image, '.') =//VariableDeclarator/VariableDeclaratorId/@Image
            [../../../Type/ReferenceType/ClassOrInterfaceType[typeof(@Image, 'java.lang.Thread', 'Thread')]]
        or (./AllocationExpression/ClassOrInterfaceType[typeof(@Image, 'java.lang.Thread', 'Thread')]
        and ../PrimarySuffix[@Image = 'run'])
    ]
]
```

**Example(s):**

``` java
Thread t = new Thread();
t.run();            // use t.start() instead
new Thread().run(); // same violation
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/DontCallThreadRun" />
```

## DontUseFloatTypeForLoopIndices

**Since:** PMD 4.3

**Priority:** Medium (3)

Don't use floating point for loop indices. If you must use floating point, use double
unless you're certain that float provides enough precision and you have a compelling
performance need (space or time).

```
//ForStatement/ForInit/LocalVariableDeclaration
/Type/PrimitiveType[@Image="float"]
```

**Example(s):**

``` java
public class Count {
  public static void main(String[] args) {
    final int START = 2000000000;
    int count = 0;
    for (float f = START; f < START + 50; f++)
      count++;
      //Prints 0 because (float) START == (float) (START + 50).
      System.out.println(count);
      //The termination test misbehaves due to floating point granularity.
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/DontUseFloatTypeForLoopIndices" />
```

## DoubleCheckedLocking

**Since:** PMD 1.04

**Priority:** High (1)

Partially created objects can be returned by the Double Checked Locking pattern when used in Java.
An optimizing JRE may assign a reference to the baz variable before it calls the constructor of the object the
reference points to.

Note: With Java 5, you can make Double checked locking work, if you declare the variable to be `volatile`.

For more details refer to: <http://www.javaworld.com/javaworld/jw-02-2001/jw-0209-double.html>
or <http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html>

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.DoubleCheckedLockingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/DoubleCheckedLockingRule.java)

**Example(s):**

``` java
public class Foo {
    /*volatile */ Object baz = null; // fix for Java5 and later: volatile
    Object bar() {
        if (baz == null) { // baz may be non-null yet not fully created
            synchronized(this) {
                if (baz == null) {
                    baz = new Object();
                }
              }
        }
        return baz;
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/DoubleCheckedLocking" />
```

## ExtendsObject

**Since:** PMD 5.0

**Priority:** Medium Low (4)

No need to explicitly extend Object.

```
//ExtendsList/ClassOrInterfaceType[@Image='Object' or @Image='java.lang.Object']
```

**Example(s):**

``` java
public class Foo extends Object {     // not required
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/ExtendsObject" />
```

## ForLoopShouldBeWhileLoop

**Since:** PMD 1.02

**Priority:** Medium (3)

Some for loops can be simplified to while loops, this makes them more concise.

```
//ForStatement
  [not(LocalVariableDeclaration)]
  [not(ForInit)]
  [not(ForUpdate)]
  [Expression]
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        for (;true;) true; // No Init or Update part, may as well be: while (true)
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/ForLoopShouldBeWhileLoop" />
```

## JumbledIncrementer

**Since:** PMD 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

```
//ForStatement
  [
    ForUpdate/StatementExpressionList/StatementExpression/PostfixExpression/PrimaryExpression/PrimaryPrefix/Name/@Image
    =
    ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image
  ]
```

**Example(s):**

``` java
public class JumbledIncrementerRule1 {
    public void foo() {
        for (int i = 0; i < 10; i++) {          // only references 'i'
            for (int k = 0; k < 20; i++) {      // references both 'i' and 'k'
                System.out.println("Hello");
            }
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/JumbledIncrementer" />
```

## MisplacedNullCheck

**Since:** PMD 3.5

**Priority:** Medium (3)

The null check here is misplaced. If the variable is null a NullPointerException will be thrown.
Either the check is useless (the variable will never be "null") or it is incorrect.

```
//Expression
    /*[self::ConditionalOrExpression or self::ConditionalAndExpression]
    /descendant::PrimaryExpression/PrimaryPrefix
    /Name[starts-with(@Image,
        concat(ancestor::PrimaryExpression/following-sibling::EqualityExpression
            [./PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]
            /PrimaryExpression/PrimaryPrefix
            /Name[count(../../PrimarySuffix)=0]/@Image,".")
        )
     ]
     [count(ancestor::ConditionalAndExpression/EqualityExpression
            [@Image='!=']
            [./PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]
            [starts-with(following-sibling::*/PrimaryExpression/PrimaryPrefix/Name/@Image,
                concat(./PrimaryExpression/PrimaryPrefix/Name/@Image, '.'))]
      ) = 0
     ]
```

**Example(s):**

``` java
public class Foo {
    void bar() {
        if (a.equals(baz) && a != null) {}
        }
}
```

``` java
public class Foo {
    void bar() {
        if (a.equals(baz) || a == null) {}
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/MisplacedNullCheck" />
```

## OverrideBothEqualsAndHashcode

**Since:** PMD 0.4

**Priority:** Medium (3)

Override both public boolean Object.equals(Object other), and public int Object.hashCode(), or override neither.  Even if you are inheriting a hashCode() from a parent class, consider implementing hashCode and explicitly delegating to your superclass.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.basic.OverrideBothEqualsAndHashcodeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/basic/OverrideBothEqualsAndHashcodeRule.java)

**Example(s):**

``` java
public class Bar {        // poor, missing a hashcode() method
    public boolean equals(Object o) {
      // do some comparison
    }
}

public class Baz {        // poor, missing an equals() method
    public int hashCode() {
      // return some hash value
    }
}

public class Foo {        // perfect, both methods provided
    public boolean equals(Object other) {
      // do some comparison
    }
    public int hashCode() {
      // return some hash value
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/OverrideBothEqualsAndHashcode" />
```

## ReturnFromFinallyBlock

**Since:** PMD 1.05

**Priority:** Medium (3)

Avoid returning from a finally block, this can discard exceptions.

```
//FinallyStatement//ReturnStatement
```

**Example(s):**

``` java
public class Bar {
    public String foo() {
        try {
            throw new Exception( "My Exception" );
        } catch (Exception e) {
            throw e;
        } finally {
            return "A. O. K."; // return not recommended here
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/ReturnFromFinallyBlock" />
```

## SimplifiedTernary

**Since:** PMD 5.4.0

**Priority:** Medium (3)

Look for ternary operators with the form `condition ? literalBoolean : foo`
or `condition ? foo : literalBoolean`.

These expressions can be simplified respectively to
`condition || foo`  when the literalBoolean is true
`!condition && foo` when the literalBoolean is false
or
`!condition || foo` when the literalBoolean is true
`condition && foo`  when the literalBoolean is false

```
//ConditionalExpression[@Ternary='true'][not(PrimaryExpression/*/Literal) and (Expression/PrimaryExpression/*/Literal/BooleanLiteral)]
|
//ConditionalExpression[@Ternary='true'][not(Expression/PrimaryExpression/*/Literal) and (PrimaryExpression/*/Literal/BooleanLiteral)]
```

**Example(s):**

``` java
public class Foo {
    public boolean test() {
        return condition ? true : something(); // can be as simple as return condition || something();
    }

    public void test2() {
        final boolean value = condition ? false : something(); // can be as simple as value = !condition && something();
    }

    public boolean test3() {
        return condition ? something() : true; // can be as simple as return !condition || something();
    }

    public void test4() {
        final boolean otherValue = condition ? something() : false; // can be as simple as condition && something();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/SimplifiedTernary" />
```

## UnconditionalIfStatement

**Since:** PMD 1.5

**Priority:** Medium (3)

Do not use "if" statements whose conditionals are always true or always false.

```
//IfStatement/Expression
 [count(PrimaryExpression)=1]
 /PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral
```

**Example(s):**

``` java
public class Foo {
    public void close() {
        if (true) {        // fixed conditional, not recommended
            // ...
        }
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/basic.xml/UnconditionalIfStatement" />
```

