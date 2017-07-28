---
title: Clone Implementation
summary: The Clone Implementation ruleset contains a collection of rules that find questionable usages of the clone() method.
permalink: pmd_rules_java_clone.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/clone.xml
---
## ProperCloneImplementation
**Since:** 1.4

**Priority:** Medium High (2)

Object clone() should be implemented with super.clone().

**Example(s):**
```
class Foo{
    public Object clone(){
        return new Foo(); // This is bad
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## CloneThrowsCloneNotSupportedException
**Since:** 1.9

**Priority:** Medium (3)

The method clone() should throw a CloneNotSupportedException.

**Example(s):**
```
public class MyClass implements Cloneable{
     public Object clone() { // will cause an error
          MyClass clone = (MyClass)super.clone();
          return clone;
     }
 }
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## CloneMethodMustImplementCloneable
**Since:** 1.9

**Priority:** Medium (3)

The method clone() should only be implemented if the class implements the Cloneable interface with the exception of a final method that only throws CloneNotSupportedException.

**Example(s):**
```
public class MyClass {
 public Object clone() throws CloneNotSupportedException {
  return foo;
 }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## CloneMethodReturnTypeMustMatchClassName
**Since:** 5.4.0

**Priority:** Medium (3)

If a class implements cloneable the return type of the method clone() must be the class name. That way, the caller
of the clone method doesn't need to cast the returned clone to the correct type.

Note: This is only possible with Java 1.5 or higher.

**Example(s):**
```
public class Foo implements Cloneable {
    @Override
    protected Object clone() { // Violation, Object must be Foo
    }
}

public class Foo implements Cloneable {
    @Override
    public Foo clone() { //Ok
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## CloneMethodMustBePublic
**Since:** 5.4.0

**Priority:** Medium (3)

The java Manual says "By convention, classes that implement this interface should override
Object.clone (which is protected) with a public method."

**Example(s):**
```
public class Foo implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException { // Violation, must be public
    }
}

public class Foo implements Cloneable {
    @Override
    protected Foo clone() { // Violation, must be public
    }
}

public class Foo implements Cloneable {
    @Override
    public Object clone() // Ok
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

