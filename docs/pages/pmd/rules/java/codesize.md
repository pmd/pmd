---
title: Code Size
summary: The Code Size ruleset contains rules that find problems related to code size or complexity.
permalink: pmd_rules_java_codesize.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/codesize.xml
---
## NPathComplexity
**Since:** 3.9

**Priority:** Medium (3)

The NPath complexity of a method is the number of acyclic execution paths through that method.
A threshold of 200 is generally considered the point where measures should be taken to reduce 
complexity and increase readability.

**Example(s):**
```
void bar() {	// this is something more complex than it needs to be,
	if (y) {	// it should be broken down into smaller methods or functions
		for (j = 0; j < m; j++) {
			if (j > r) {
				doSomething();
				while (f < 5 ) {
					anotherThing();
					f -= 27;
					}
				} else {
					tryThis();
				}
			}
		}
		if ( r - n > 45) {
		   while (doMagic()) {
		      findRabbits();
		   }
		}
		try {
			doSomethingDangerous();
		} catch (Exception ex) {
			makeAmends();
			} finally {
				dontDoItAgain();
				}
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## ExcessiveMethodLength
**Since:** 0.6

**Priority:** Medium (3)

When methods are excessively long this usually indicates that the method is doing more than its
name/signature might suggest. They also become challenging for others to digest since excessive 
scrolling causes readers to lose focus.
Try to reduce the method length by creating helper methods and removing any copy/pasted code.

**Example(s):**
```
public void doSomething() {
	System.out.println("Hello world!");
	System.out.println("Hello world!");
		// 98 copies omitted for brevity.
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## ExcessiveParameterList
**Since:** 0.9

**Priority:** Medium (3)

Methods with numerous parameters are a challenge to maintain, especially if most of them share the
same datatype. These situations usually denote the need for new objects to wrap the numerous parameters.

**Example(s):**
```
public void addPerson(		// too many arguments liable to be mixed up
	int birthYear, int birthMonth, int birthDate, int height, int weight, int ssn) {

	. . . .
}
 
public void addPerson(		// preferred approach
	Date birthdate, BodyMeasurements measurements, int ssn) {

	. . . .
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## ExcessiveClassLength
**Since:** 0.6

**Priority:** Medium (3)

Excessive class file lengths are usually indications that the class may be burdened with excessive 
responsibilities that could be provided by external classes or functions. In breaking these methods
apart the code becomes more managable and ripe for reuse.

**Example(s):**
```
public class Foo {
	public void bar1() {
    // 1000 lines of code
	}
	public void bar2() {
    // 1000 lines of code
	}
    public void bar3() {
    // 1000 lines of code
	}
	
	
    public void barN() {
    // 1000 lines of code
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## CyclomaticComplexity
**Since:** 1.03

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity.

**Example(s):**
```
public class Foo {		// This has a Cyclomatic Complexity = 12
1   public void example()  {
2       if (a == b)  {
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
            switch (z) {
9               case 1:
                    fiddle();
                    break;
10              case 2:
                    fiddle();
                    break;
11              case 3:
                    fiddle();
                    break;
12              default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

## StdCyclomaticComplexity
**Since:** 5.1.2

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity.

**Example(s):**
```
public class Foo {    // This has a Cyclomatic Complexity = 12
1   public void example()  {
2       if (a == b || (c == d && e == f))  { // Only one
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
            switch (z) {
9               case 1:
                    fiddle();
                    break;
10              case 2:
                    fiddle();
                    break;
11              case 3:
                    fiddle();
                    break;
12              default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

## ModifiedCyclomaticComplexity
**Since:** 5.1.2

**Priority:** Medium (3)

Complexity directly affects maintenance costs is determined by the number of decision points in a method 
plus one for the method entry.  The decision points include 'if', 'while', 'for', and 'case labels' calls.  
Generally, numbers ranging from 1-4 denote low complexity, 5-7 denote moderate complexity, 8-10 denote
high complexity, and 11+ is very high complexity. Modified complexity treats switch statements as a single
decision point.

**Example(s):**
```
public class Foo {    // This has a Cyclomatic Complexity = 9
1   public void example()  {
2       if (a == b)  {
3           if (a1 == b1) {
                fiddle();
4           } else if a2 == b2) {
                fiddle();
            }  else {
                fiddle();
            }
5       } else if (c == d) {
6           while (c == d) {
                fiddle();
            }
7        } else if (e == f) {
8           for (int n = 0; n < h; n++) {
                fiddle();
            }
        } else{
9           switch (z) {
                case 1:
                    fiddle();
                    break;
                case 2:
                    fiddle();
                    break;
                case 3:
                    fiddle();
                    break;
                default:
                    fiddle();
                    break;
            }
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|showMethodsComplexity|true|Add method average violations to the report|
|showClassesComplexity|true|Add class average violations to the report|
|reportLevel|10|Cyclomatic Complexity reporting threshold|

## ExcessivePublicCount
**Since:** 1.04

**Priority:** Medium (3)

Classes with large numbers of public methods and attributes require disproportionate testing efforts
since combinational side effects grow rapidly and increase risk. Refactoring these classes into
smaller ones not only increases testability and reliability but also allows new variations to be
developed easily.

**Example(s):**
```
public class Foo {
	public String value;
	public Bar something;
	public Variable var;
 // [... more more public attributes ...]
 
	public void doWork() {}
	public void doMoreWork() {}
	public void doWorkAgain() {}
 // [... more more public methods ...]
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## TooManyFields
**Since:** 3.0

**Priority:** Medium (3)

Classes that have too many fields can become unwieldy and could be redesigned to have fewer fields,
possibly through grouping related fields in new objects.  For example, a class with individual 
city/state/zip fields could park them within a single Address field.

**Example(s):**
```
public class Person {	// too many separate fields
   int birthYear;
   int birthMonth;
   int birthDate;
   float height;
   float weight;
}

public class Person {	// this is more manageable
   Date birthDate;
   BodyMeasurements measurements;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|maxfields|15|Max allowable fields|

## NcssMethodCount
**Since:** 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given method. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**Example(s):**
```
public class Foo extends Bar {
 public int methd() {
     super.methd();





 //this method only has 1 NCSS lines
      return 1;
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## NcssTypeCount
**Since:** 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given type. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**Example(s):**
```
public class Foo extends Bar {
 public Foo() {
 //this class only has 6 NCSS lines
     super();





      super.foo();
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## NcssConstructorCount
**Since:** 3.9

**Priority:** Medium (3)

This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines
of code for a given constructor. NCSS ignores comments, and counts actual statements. Using this algorithm,
lines of code that are split are counted as one.

**Example(s):**
```
public class Foo extends Bar {
 public Foo() {
     super();





 //this constructor only has 1 NCSS lines
      super.foo();
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## TooManyMethods
**Since:** 4.2

**Priority:** Medium (3)

A class with too many methods is probably a good suspect for refactoring, in order to reduce its complexity and find a way to
have more fine grained objects.

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|
|maxmethods|10|The method count reporting threshold|

