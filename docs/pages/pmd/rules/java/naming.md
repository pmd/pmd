---
title: Naming
summary: The Naming Ruleset contains rules regarding preferred usage of names and identifiers.
permalink: pmd_rules_java_naming.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/naming.xml
---
## AbstractNaming

**Since:** 1.4

**Priority:** Medium (3)

Abstract classes should be named 'AbstractXXX'.

**Example(s):**

```
public abstract class Foo { // should be AbstractFoo
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|strict|true|Also flag classes, that are named Abstract, but are not abstract.|

## AvoidDollarSigns

**Since:** 1.5

**Priority:** Medium (3)

Avoid using dollar signs in variable/method/class/interface names.

**Example(s):**

```
public class Fo$o {  // not a recommended name
}
```

## AvoidFieldNameMatchingMethodName

**Since:** 3.0

**Priority:** Medium (3)

It can be confusing to have a field name with the same name as a method. While this is permitted, 
having information (field) and actions (method) is not clear naming. Developers versed in 
Smalltalk often prefer this approach as the methods denote accessor methods.

**Example(s):**

```
public class Foo {
	Object bar;
	// bar is data or an action or both?
	void bar() {
	}
}
```

## AvoidFieldNameMatchingTypeName

**Since:** 3.0

**Priority:** Medium (3)

It is somewhat confusing to have a field name matching the declaring class name.
This probably means that type and/or field names should be chosen more carefully.

**Example(s):**

```
public class Foo extends Bar {
	int foo;	// There is probably a better name that can be used
}
```

## BooleanGetMethodName

**Since:** 4.0

**Priority:** Medium Low (4)

Methods that return boolean results should be named as predicate statements to denote this.
I.e, 'isReady()', 'hasValues()', 'canCommit()', 'willFail()', etc.   Avoid the use of the 'get'
prefix for these methods.

**Example(s):**

```
public boolean getFoo(); 	// bad
public boolean isFoo(); 	// ok
public boolean getFoo(boolean bar); // ok, unless checkParameterizedMethods=true
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkParameterizedMethods|false|Check parameterized methods|

## ClassNamingConventions

**Since:** 1.2

**Priority:** High (1)

Class names should always begin with an upper case character.

**Example(s):**

```
public class Foo {}
```

## GenericsNaming

**Since:** 4.2.6

**Priority:** Medium Low (4)

Names for references to generic values should be limited to a single uppercase letter.

**Example(s):**

```
public interface GenericDao<E extends BaseModel, K extends Serializable> extends BaseDao {
   // This is ok...
}

public interface GenericDao<E extends BaseModel, K extends Serializable> {
   // Also this
}

public interface GenericDao<e extends BaseModel, K extends Serializable> {
   // 'e' should be an 'E'
}

public interface GenericDao<EF extends BaseModel, K extends Serializable> {
   // 'EF' is not ok.
}
```

## LongVariable

**Since:** 0.3

**Priority:** Medium (3)

Fields, formal arguments, or local variable names that are too long can make the code difficult to follow.

**Example(s):**

```
public class Something {
	int reallyLongIntName = -3;  			// VIOLATION - Field
	public static void main( String argumentsList[] ) { // VIOLATION - Formal
		int otherReallyLongName = -5; 		// VIOLATION - Local
		for (int interestingIntIndex = 0;	// VIOLATION - For
             interestingIntIndex < 10;
             interestingIntIndex ++ ) {
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|17|The variable length reporting threshold|

## MethodNamingConventions

**Since:** 1.2

**Priority:** High (1)

Method names should always begin with a lower case character, and should not contain underscores.

**Example(s):**

```
public class Foo {
	public void fooStuff() {
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|checkNativeMethods|true|Check native methods|

## MethodWithSameNameAsEnclosingClass

**Since:** 1.5

**Priority:** Medium (3)

Non-constructor methods should not have the same name as the enclosing class.

**Example(s):**

```
public class MyClass {

	public MyClass() {}			// this is OK because it is a constructor
	
	public void MyClass() {}	// this is bad because it is a method
}
```

## MisleadingVariableName

**Since:** 3.4

**Priority:** Medium (3)

Detects when a non-field has a name starting with 'm_'.  This usually denotes a field and could be confusing.

**Example(s):**

```
public class Foo {
    private int m_foo; // OK
    public void bar(String m_baz) {  // Bad
      int m_boz = 42; // Bad
    }
}
```

## NoPackage

**Since:** 3.3

**Priority:** Medium (3)

Detects when a class or interface does not have a package definition.

**Example(s):**

```
// no package declaration
public class ClassInDefaultPackage {
}
```

## PackageCase

**Since:** 3.3

**Priority:** Medium (3)

Detects when a package definition contains uppercase characters.

**Example(s):**

```
package com.MyCompany;  // should be lowercase name

public class SomeClass {
}
```

## ShortClassName

**Since:** 5.0

**Priority:** Medium Low (4)

Short Classnames with fewer than e.g. five characters are not recommended.

**Example(s):**

```
public class Foo {
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|5|Number of characters that are required as a minimum for a class name.|

## ShortMethodName

**Since:** 0.3

**Priority:** Medium (3)

Method names that are very short are not helpful to the reader.

**Example(s):**

```
public class ShortMethod {
	public void a( int i ) { // Violation
	}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|3|Number of characters that are required as a minimum for a method name.|

## ShortVariable

**Since:** 0.3

**Priority:** Medium (3)

Fields, local variables, or parameter names that are very short are not helpful to the reader.

**Example(s):**

```
public class Something {
    private int q = 15;                         // field - too short
    public static void main( String as[] ) {    // formal arg - too short
        int r = 20 + q;                         // local var - too short
        for (int i = 0; i < 10; i++) {          // not a violation (inside 'for' loop)
            r += q;
        }
        for (Integer i : numbers) {             // not a violation (inside 'for-each' loop)
            r += q;
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|minimum|3|Number of characters that are required as a minimum for a variable name.|

## SuspiciousConstantFieldName

**Since:** 2.0

**Priority:** Medium (3)

Field names using all uppercase characters - Sun's Java naming conventions indicating constants - should
be declared as final.

**Example(s):**

```
public class Foo {
 // this is bad, since someone could accidentally
 // do PI = 2.71828; which is actually e
 // final double PI = 3.16; is ok
  double PI = 3.16;
}
```

## SuspiciousEqualsMethodName

**Since:** 2.0

**Priority:** Medium High (2)

The method name and parameter number are suspiciously close to equals(Object), which can denote an
intention to override the equals(Object) method.

**Example(s):**

```
public class Foo {
   public int equals(Object o) {
     // oops, this probably was supposed to be boolean equals
   }
   public boolean equals(String s) {
     // oops, this probably was supposed to be equals(Object)
   }
   public boolean equals(Object o1, Object o2) {
     // oops, this probably was supposed to be equals(Object)
   }
}
```

## SuspiciousHashcodeMethodName

**Since:** 1.5

**Priority:** Medium (3)

The method name and return type are suspiciously close to hashCode(), which may denote an intention
to override the hashCode() method.

**Example(s):**

```
public class Foo {
	public int hashcode() {	// oops, this probably was supposed to be 'hashCode'
	
	}
}
```

## VariableNamingConventions

**Since:** 1.2

**Priority:** High (1)

A variable naming conventions rule - customize this to your liking.  Currently, it
checks for final variables that should be fully capitalized and non-final variables
that should not include underscores.

**Example(s):**

```
public class Foo {
   public static final int MY_NUM = 0;
   public String myTest = "";
   DataModule dmTest = new DataModule();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|parameterSuffix|[]|Method parameter variable suffixes|
|parameterPrefix|[]|Method parameter variable prefixes|
|localSuffix|[]|Local variable suffixes|
|localPrefix|[]|Local variable prefixes|
|memberSuffix|[]|Member variable suffixes|
|memberPrefix|[]|Member variable prefixes|
|staticSuffix|[]|Static variable suffixes|
|checkParameters|true|Check constructor and method parameter variables|
|checkNativeMethodParameters|true|Check method parameter of native methods|
|staticPrefix|[]|Static variable prefixes|
|checkLocals|true|Check local variables|
|checkMembers|true|Check member variables|

