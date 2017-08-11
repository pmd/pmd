---
title: Basic
summary: The Basic ruleset contains a collection of good practices which should be followed.
permalink: pmd_rules_java_basic.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/basic.xml
---
## AvoidBranchingStatementAsLastInLoop

**Since:** 5.0

**Priority:** Medium High (2)

Using a branching statement as the last part of a loop may be a bug, and/or is confusing.
Ensure that the usage is not a bug, or consider using another approach.

**Example(s):**

```
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

## AvoidDecimalLiteralsInBigDecimalConstructor

**Since:** 3.4

**Priority:** Medium (3)

One might assume that the result of "new BigDecimal(0.1)" is exactly equal to 0.1, but it is actually
equal to .1000000000000000055511151231257827021181583404541015625.
This is because 0.1 cannot be represented exactly as a double (or as a binary fraction of any finite
length). Thus, the long value that is being passed in to the constructor is not exactly equal to 0.1,
appearances notwithstanding.

The (String) constructor, on the other hand, is perfectly predictable: 'new BigDecimal("0.1")' is
exactly equal to 0.1, as one would expect.  Therefore, it is generally recommended that the
(String) constructor be used in preference to this one.

**Example(s):**

```
BigDecimal bd = new BigDecimal(1.123);		// loss of precision, this would trigger the rule

BigDecimal bd = new BigDecimal("1.123");   	// preferred approach

BigDecimal bd = new BigDecimal(12);     	// preferred approach, ok for integer values
```

## AvoidMultipleUnaryOperators

**Since:** 4.2

**Priority:** Medium High (2)

The use of multiple unary operators may be problematic, and/or confusing.
Ensure that the intended usage is not a bug, or consider simplifying the expression.

**Example(s):**

```
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

## AvoidThreadGroup

**Since:** 3.6

**Priority:** Medium (3)

Avoid using java.lang.ThreadGroup; although it is intended to be used in a threaded environment
it contains methods that are not thread-safe.

**Example(s):**

```
public class Bar {
	void buz() {
		ThreadGroup tg = new ThreadGroup("My threadgroup") ;
		tg = new ThreadGroup(tg, "my thread group");
		tg = Thread.currentThread().getThreadGroup();
		tg = System.getSecurityManager().getThreadGroup();
	}
}
```

## AvoidUsingHardCodedIP

**Since:** 4.1

**Priority:** Medium (3)

Application with hard-coded IP addresses can become impossible to deploy in some cases.
Externalizing IP adresses is preferable.

**Example(s):**

```
public class Foo {
	private String ip = "127.0.0.1"; 	// not recommended
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkAddressTypes|[IPv4, IPv6, IPv4 mapped IPv6]|Check for IP address types.|
|pattern|^"[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}"$|Regular Expression|

## AvoidUsingOctalValues

**Since:** 3.9

**Priority:** Medium (3)

Integer literals should not start with zero since this denotes that the rest of literal will be
interpreted as an octal value.

**Example(s):**

```
int i = 012;	// set i with 10 not 12
int j = 010;	// set j with 8 not 10
k = i * j;		// set k with 80 not 120
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strict|false|Detect violations between 00 and 07|

## BigIntegerInstantiation

**Since:** 3.9

**Priority:** Medium (3)

Don't create instances of already existing BigInteger (BigInteger.ZERO, BigInteger.ONE) and
for Java 1.5 onwards, BigInteger.TEN and BigDecimal (BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN)

**Example(s):**

```
BigInteger bi = new BigInteger(1);		// reference BigInteger.ONE instead
BigInteger bi2 = new BigInteger("0");	// reference BigInteger.ZERO instead
BigInteger bi3 = new BigInteger(0.0);	// reference BigInteger.ZERO instead
BigInteger bi4;
bi4 = new BigInteger(0);				// reference BigInteger.ZERO instead
```

## BooleanInstantiation

**Since:** 1.2

**Priority:** Medium High (2)

Avoid instantiating Boolean objects; you can reference Boolean.TRUE, Boolean.FALSE, or call Boolean.valueOf() instead.

**Example(s):**

```
Boolean bar = new Boolean("true");		// unnecessary creation, just reference Boolean.TRUE;
Boolean buz = Boolean.valueOf(false);	// ...., just reference Boolean.FALSE;
```

## BrokenNullCheck

**Since:** 3.8

**Priority:** Medium High (2)

The null check is broken since it will throw a NullPointerException itself.
It is likely that you used || instead of && or vice versa.

**Example(s):**

```
public String bar(String string) {
  // should be &&
	if (string!=null || !string.equals(""))
		return string;
  // should be ||
	if (string==null && string.equals(""))
		return string;
}
```

## CheckResultSet

**Since:** 4.1

**Priority:** Medium (3)

Always check the return values of navigation methods (next, previous, first, last) of a ResultSet.
If the value return is 'false', it should be handled properly.

**Example(s):**

```
Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
rst.next(); 	// what if it returns false? bad form
String firstName = rst.getString(1);

Statement stat = conn.createStatement();
ResultSet rst = stat.executeQuery("SELECT name FROM person");
if (rst.next()) {	// result is properly examined and used
    String firstName = rst.getString(1);
	} else  {
		// handle missing data
}
```

## CheckSkipResult

**Since:** 5.0

**Priority:** Medium (3)

The skip() method may skip a smaller number of bytes than requested. Check the returned value to find out if it was the case or not.

**Example(s):**

```
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

## ClassCastExceptionWithToArray

**Since:** 3.4

**Priority:** Medium (3)

When deriving an array of a specific class from your Collection, one should provide an array of
the same class as the parameter of the toArray() method. Doing otherwise you will will result
in a ClassCastException.

**Example(s):**

```
Collection c = new ArrayList();
Integer obj = new Integer(1);
c.add(obj);

    // this would trigger the rule (and throw a ClassCastException if executed)
Integer[] a = (Integer [])c.toArray();

   // this is fine and will not trigger the rule
Integer[] b = (Integer [])c.toArray(new Integer[c.size()]);
```

## CollapsibleIfStatements

**Since:** 3.1

**Priority:** Medium (3)

Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.

**Example(s):**

```
void bar() {
	if (x) {			// original implementation
		if (y) {
			// do stuff
		}
	}
}

void bar() {
	if (x && y) {		// optimized implementation
		// do stuff
	}
}
```

## DontCallThreadRun

**Since:** 4.3

**Priority:** Medium Low (4)

Explicitly calling Thread.run() method will execute in the caller's thread of control.  Instead, call Thread.start() for the intended behavior.

**Example(s):**

```
Thread t = new Thread();
t.run();            // use t.start() instead
new Thread().run(); // same violation
```

## DontUseFloatTypeForLoopIndices

**Since:** 4.3

**Priority:** Medium (3)

Don't use floating point for loop indices. If you must use floating point, use double
unless you're certain that float provides enough precision and you have a compelling
performance need (space or time).

**Example(s):**

```
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

## DoubleCheckedLocking

**Since:** 1.04

**Priority:** High (1)

Partially created objects can be returned by the Double Checked Locking pattern when used in Java.
An optimizing JRE may assign a reference to the baz variable before it calls the constructor of the object the
reference points to.

Note: With Java 5, you can make Double checked locking work, if you declare the variable to be `volatile`.

For more details refer to: http://www.javaworld.com/javaworld/jw-02-2001/jw-0209-double.html
or http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html

**Example(s):**

```
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

## ExtendsObject

**Since:** 5.0

**Priority:** Medium Low (4)

No need to explicitly extend Object.

**Example(s):**

```
public class Foo extends Object { 	// not required
}
```

## ForLoopShouldBeWhileLoop

**Since:** 1.02

**Priority:** Medium (3)

Some for loops can be simplified to while loops, this makes them more concise.

**Example(s):**

```
public class Foo {
	void bar() {
		for (;true;) true; // No Init or Update part, may as well be: while (true)
	}
}
```

## JumbledIncrementer

**Since:** 1.0

**Priority:** Medium (3)

Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.

**Example(s):**

```
public class JumbledIncrementerRule1 {
	public void foo() {
		for (int i = 0; i < 10; i++) {			// only references 'i'
			for (int k = 0; k < 20; i++) {		// references both 'i' and 'k'
				System.out.println("Hello");
			}
		}
	}
}
```

## MisplacedNullCheck

**Since:** 3.5

**Priority:** Medium (3)

The null check here is misplaced. If the variable is null a NullPointerException will be thrown.
Either the check is useless (the variable will never be "null") or it is incorrect.

**Example(s):**

```
public class Foo {
	void bar() {
		if (a.equals(baz) && a != null) {}
		}
}
```

```
public class Foo {
	void bar() {
		if (a.equals(baz) || a == null) {}
	}
}
```

## OverrideBothEqualsAndHashcode

**Since:** 0.4

**Priority:** Medium (3)

Override both public boolean Object.equals(Object other), and public int Object.hashCode(), or override neither.  Even if you are inheriting a hashCode() from a parent class, consider implementing hashCode and explicitly delegating to your superclass.

**Example(s):**

```
public class Bar {		// poor, missing a hashcode() method
	public boolean equals(Object o) {
      // do some comparison
	}
}

public class Baz {		// poor, missing an equals() method
	public int hashCode() {
      // return some hash value
	}
}

public class Foo {		// perfect, both methods provided
	public boolean equals(Object other) {
      // do some comparison
	}
	public int hashCode() {
      // return some hash value
	}
}
```

## ReturnFromFinallyBlock

**Since:** 1.05

**Priority:** Medium (3)

Avoid returning from a finally block, this can discard exceptions.

**Example(s):**

```
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

## SimplifiedTernary

**Since:** 5.4.0

**Priority:** Medium (3)

Look for ternary operators with the form `condition ? literalBoolean : foo`
or `condition ? foo : literalBoolean`.

These expressions can be simplified respectively to
`condition || foo`  when the literalBoolean is true
`!condition && foo` when the literalBoolean is false
or
`!condition || foo` when the literalBoolean is true
`condition && foo`  when the literalBoolean is false

**Example(s):**

```
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

## UnconditionalIfStatement

**Since:** 1.5

**Priority:** Medium (3)

Do not use "if" statements whose conditionals are always true or always false.

**Example(s):**

```
public class Foo {
	public void close() {
		if (true) {		// fixed conditional, not recommended
			// ...
		}
	}
}
```

