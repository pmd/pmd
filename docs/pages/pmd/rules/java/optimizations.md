---
title: Optimization
summary: These rules deal with different optimizations that generally apply to best practices.
permalink: pmd_rules_java_optimizations.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/optimizations.xml
---
## AddEmptyString

**Since:** PMD 4.0

**Priority:** Medium (3)

The conversion of literals to strings by concatenating them with empty strings is inefficient.
It is much better to use one of the type-specific toString() methods instead.

```
//AdditiveExpression/PrimaryExpression/PrimaryPrefix/Literal[@Image='""']
```

**Example(s):**

```
String s = "" + 123; 				// inefficient 
String t = Integer.toString(456); 	// preferred approach
```

## AvoidArrayLoops

**Since:** PMD 3.5

**Priority:** Medium (3)

Instead of manually copying data between two arrays, use the efficient System.arraycopy method instead.

```
//Statement[(ForStatement or WhileStatement) and
count(*//AssignmentOperator[@Image = '='])=1
and
*/Statement
[
./Block/BlockStatement/Statement/StatementExpression/PrimaryExpression
/PrimaryPrefix/Name/../../PrimarySuffix/Expression
[(PrimaryExpression or AdditiveExpression) and count
(.//PrimaryPrefix/Name)=1]//PrimaryPrefix/Name/@Image
and
./Block/BlockStatement/Statement/StatementExpression/Expression/PrimaryExpression
/PrimaryPrefix/Name/../../PrimarySuffix[count
(..//PrimarySuffix)=1]/Expression[(PrimaryExpression
or AdditiveExpression) and count(.//PrimaryPrefix/Name)=1]
//PrimaryPrefix/Name/@Image
]]
```

**Example(s):**

```
public class Test {
  public void bar() {
    int[] a = new int[10];
    int[] b = new int[10];
    for (int i=0;i<10;i++) {
      b[i]=a[i];
    }
  }
}
     // this will trigger the rule
     for (int i=0;i<10;i++) {
       b[i]=a[c[i]];
     }

  }
}
```

## AvoidInstantiatingObjectsInLoops

**Since:** PMD 2.2

**Priority:** Medium (3)

New objects created within loops should be checked to see if they can created outside them and reused.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.AvoidInstantiatingObjectsInLoopsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/AvoidInstantiatingObjectsInLoopsRule.java)

**Example(s):**

```
public class Something {
	public static void main( String as[] ) {  
		for (int i = 0; i < 10; i++) {
		    Foo f = new Foo(); // Avoid this whenever you can it's really expensive
		}
	}
}
```

## LocalVariableCouldBeFinal

**Since:** PMD 2.2

**Priority:** Medium (3)

A local variable assigned only once can be declared final.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.LocalVariableCouldBeFinalRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/LocalVariableCouldBeFinalRule.java)

**Example(s):**

```
public class Bar {
	public void foo () {
		String txtA = "a"; 		// if txtA will not be assigned again it is better to do this:
		final String txtB = "b";
	}
}
```

## MethodArgumentCouldBeFinal

**Since:** PMD 2.2

**Priority:** Medium (3)

A method argument that is never re-assigned within the method can be declared final.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.MethodArgumentCouldBeFinalRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/MethodArgumentCouldBeFinalRule.java)

**Example(s):**

```
public void foo1 (String param) {	// do stuff with param never assigning it
  
}

public void foo2 (final String param) {	// better, do stuff with param never assigning it
  
}
```

## PrematureDeclaration

**Since:** PMD 5.0

**Priority:** Medium (3)

Checks for variables that are defined before they might be used. A reference is deemed to be premature if it is created right before a block of code that doesn't use it that also has the ability to return or throw an exception.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.PrematureDeclarationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/PrematureDeclarationRule.java)

**Example(s):**

```
public int getLength(String[] strings) {
  
  int length = 0;	// declared prematurely

  if (strings == null || strings.length == 0) return 0;
  
  for (String str : strings) {
    length += str.length();
    }

  return length;
}
```

## RedundantFieldInitializer

**Since:** PMD 5.0

**Priority:** Medium (3)

Java will initialize fields with known default values so any explicit initialization of those same defaults
is redundant and results in a larger class file (approximately three additional bytecode instructions per field).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.RedundantFieldInitializerRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/RedundantFieldInitializerRule.java)

**Example(s):**

```
public class C {
	boolean b	= false;	// examples of redundant initializers
	byte by		= 0;
	short s		= 0;
	char c		= 0;
	int i		= 0;
	long l		= 0;
	
	float f		= .0f;    // all possible float literals
	doable d	= 0d;     // all possible double literals
	Object o	= null;
	
	MyClass mca[] = null;
	int i1 = 0, ia1[] = null;
	
	class Nested {
		boolean b = false;
	}
}
```

## SimplifyStartsWith

**Since:** PMD 3.1

**Priority:** Medium (3)

Since it passes in a literal of length 1, calls to (string).startsWith can be rewritten using (string).charAt(0)
at the expense of some readability.

```
//PrimaryExpression
 [PrimaryPrefix/Name
  [ends-with(@Image, '.startsWith')] or PrimarySuffix[@Image='startsWith']]
 [PrimarySuffix/Arguments/ArgumentList
  /Expression/PrimaryExpression/PrimaryPrefix
  /Literal
   [string-length(@Image)=3]
   [starts-with(@Image, '"')]
   [ends-with(@Image, '"')]
 ]
```

**Example(s):**

```
public class Foo {

	boolean checkIt(String x) {
		return x.startsWith("a");	// suboptimal
	}
  
	boolean fasterCheckIt(String x) {
		return x.charAt(0) == 'a';	//	faster approach
	}
}
```

## UnnecessaryWrapperObjectCreation

**Since:** PMD 3.8

**Priority:** Medium (3)

Most wrapper classes provide static conversion methods that avoid the need to create intermediate objects
just to create the primitive forms. Using these avoids the cost of creating objects that also need to be 
garbage-collected later.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.UnnecessaryWrapperObjectCreationRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/UnnecessaryWrapperObjectCreationRule.java)

**Example(s):**

```
public int convert(String s) {
  int i, i2;

  i = Integer.valueOf(s).intValue(); // this wastes an object
  i = Integer.parseInt(s); 			 // this is better

  i2 = Integer.valueOf(i).intValue(); // this wastes an object
  i2 = i; // this is better

  String s3 = Integer.valueOf(i2).toString(); // this wastes an object
  s3 = Integer.toString(i2); 		// this is better

  return i2;
}
```

## UseArrayListInsteadOfVector

**Since:** PMD 3.0

**Priority:** Medium (3)

ArrayList is a much better Collection implementation than Vector if thread-safe operation is not required.

```
//CompilationUnit[count(ImportDeclaration) = 0 or count(ImportDeclaration/Name[@Image='java.util.Vector']) > 0]
  //AllocationExpression/ClassOrInterfaceType
    [@Image='Vector' or @Image='java.util.Vector']
```

**Example(s):**

```
public class SimpleTest extends TestCase {
	public void testX() {
		Collection c1 = new Vector();		
		Collection c2 = new ArrayList();	// achieves the same with much better performance
	}
}
```

## UseArraysAsList

**Since:** PMD 3.5

**Priority:** Medium (3)

The java.util.Arrays class has a "asList" method that should be used when you want to create a new List from
an array of objects. It is faster than executing a loop to copy all the elements of the array one by one.

```
//Statement[
    (ForStatement) and (ForStatement//VariableInitializer//Literal[@IntLiteral='true' and @Image='0']) and (count(.//IfStatement)=0)
   ]
   //StatementExpression[
    PrimaryExpression/PrimaryPrefix/Name[
     substring-before(@Image,'.add') = ancestor::MethodDeclaration//LocalVariableDeclaration[
      ./Type//ClassOrInterfaceType[
       @Image = 'Collection' or 
       @Image = 'List' or @Image='ArrayList'
      ]
     ]
     /VariableDeclarator/VariableDeclaratorId[
      count(..//AllocationExpression/ClassOrInterfaceType[
       @Image="ArrayList"
      ]
      )=1
     ]/@Image
    ]
   and
   PrimaryExpression/PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Name
   [
     @Image = ancestor::MethodDeclaration//LocalVariableDeclaration[@Array="true"]/VariableDeclarator/VariableDeclaratorId/@Image
     or
     @Image = ancestor::MethodDeclaration//FormalParameter/VariableDeclaratorId/@Image
   ]
   /../..[count(.//PrimarySuffix)
   =1]/PrimarySuffix/Expression/PrimaryExpression/PrimaryPrefix
   /Name
   ]
```

**Example(s):**

```
public class Test {
  public void foo(Integer[] ints) {
    // could just use Arrays.asList(ints)
     List l= new ArrayList(10);
     for (int i=0; i< 100; i++) {
       l.add(ints[i]);
     }
     for (int i=0; i< 100; i++) {
       l.add(a[i].toString()); // won't trigger the rule
     }
  }
}
```

## UseStringBufferForStringAppends

**Since:** PMD 3.1

**Priority:** Medium (3)

The use of the '+=' operator for appending strings causes the JVM to create and use an internal StringBuffer.
If a non-trivial number of these concatenations are being used then the explicit use of a StringBuilder or 
threadsafe StringBuffer is recommended to avoid this.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.optimizations.UseStringBufferForStringAppendsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/optimizations/UseStringBufferForStringAppendsRule.java)

**Example(s):**

```
public class Foo {
  void bar() {
    String a;
    a = "foo";
    a += " bar";
   // better would be:
   // StringBuilder a = new StringBuilder("foo");
   // a.append(" bar);
  }
}
```

