---
title: Coupling
summary: Rules which find instances of high or inappropriate coupling between objects and packages.
permalink: pmd_rules_java_coupling.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/coupling.xml
---
## CouplingBetweenObjects

**Since:** 1.04

**Priority:** Medium (3)

This rule counts the number of unique attributes, local variables, and return types within an object. 
A number higher than the specified threshold can indicate a high degree of coupling.

**Example(s):**

```
import com.Blah;
import org.Bar;
import org.Bardo;

public class Foo {
   private Blah var1;
   private Bar var2;
 
 	//followed by many imports of unique objects
   void ObjectC doWork() {
     Bardo var55;
     ObjectA var44;
     ObjectZ var93;
     return something;
   }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|threshold|20|Unique type reporting threshold|

## ExcessiveImports

**Since:** 1.04

**Priority:** Medium (3)

A high number of imports can indicate a high degree of coupling within an object. This rule 
counts the number of unique imports and reports a violation if the count is above the 
user-specified threshold.

**Example(s):**

```
import blah.blah.Baz;
import blah.blah.Bif;
// 18 others from the same package elided
public class Foo {
 public void doWork() {}
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|topscore||Top score value|
|minimum||Minimum reporting threshold|
|sigma||Sigma value|

## LawOfDemeter

**Since:** 5.0

**Priority:** Medium (3)

The Law of Demeter is a simple rule, that says "only talk to friends". It helps to reduce coupling between classes or objects. 
See also the references:
Andrew Hunt, David Thomas, and Ward Cunningham. The Pragmatic Programmer. From Journeyman to Master. Addison-Wesley Longman, Amsterdam, October 1999.;
K.J. Lieberherr and I.M. Holland. Assuring good style for object-oriented programs. Software, IEEE, 6(5):38–48, 1989.;
http://www.ccs.neu.edu/home/lieber/LoD.html;
http://en.wikipedia.org/wiki/Law_of_Demeter

**Example(s):**

```
public class Foo {
    /**
     * This example will result in two violations.
     */
    public void example(Bar b) {
        // this method call is ok, as b is a parameter of "example"
        C c = b.getC();
        
        // this method call is a violation, as we are using c, which we got from B.
        // We should ask b directly instead, e.g. "b.doItOnC();"
        c.doIt();
        
        // this is also a violation, just expressed differently as a method chain without temporary variables.
        b.getC().doIt();
        
        // a constructor call, not a method call.
        D d = new D();
        // this method call is ok, because we have create the new instance of D locally.
        d.doSomethingElse(); 
    }
}
```

## LooseCoupling

**Since:** 0.7

**Priority:** Medium (3)

The use of implementation types as object references limits your ability to use alternate
implementations in the future as requirements change. Whenever available, referencing objects 
by their interface types provides much more flexibility.

**Example(s):**

```
// sub-optimal approach
private ArrayList list = new ArrayList();

public HashSet getFoo() {
	return new HashSet();
}

	// preferred approach
private List list = new ArrayList();

public Set getFoo() {
	return new HashSet();
}
```

## LoosePackageCoupling

**Since:** 5.0

**Priority:** Medium (3)

Avoid using classes from the configured package hierarchy outside of the package hierarchy, 
except when using one of the configured allowed classes.

**Example(s):**

```
package some.package;

import some.other.package.subpackage.subsubpackage.DontUseThisClass;

public class Bar {
   DontUseThisClass boo = new DontUseThisClass();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|classes|[]|Allowed classes|
|packages|[]|Restricted packages|

