---
title: J2EE
summary: Rules specific to the use of J2EE implementations.
permalink: pmd_rules_java_j2ee.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/j2ee.xml
---
## UseProperClassLoader
**Since:** 3.7

**Priority:** Medium (3)

In J2EE, the getClassLoader() method might not work as expected. Use 
Thread.currentThread().getContextClassLoader() instead.

**Example(s):**
```
public class Foo {
 ClassLoader cl = Bar.class.getClassLoader();
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## MDBAndSessionBeanNamingConvention
**Since:** 4.0

**Priority:** Medium Low (4)

The EJB Specification states that any MessageDrivenBean or SessionBean should be suffixed by 'Bean'.

**Example(s):**
```
public class SomeBean implements SessionBean{}					// proper name

public class MissingTheProperSuffix implements SessionBean {}  	// non-standard name
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## RemoteSessionInterfaceNamingConvention
**Since:** 4.0

**Priority:** Medium Low (4)

A Remote Home interface type of a Session EJB should be suffixed by 'Home'.

**Example(s):**
```
public interface MyBeautifulHome extends javax.ejb.EJBHome {}		// proper name

public interface MissingProperSuffix extends javax.ejb.EJBHome {}	// non-standard name
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## LocalInterfaceSessionNamingConvention
**Since:** 4.0

**Priority:** Medium Low (4)

The Local Interface of a Session EJB should be suffixed by 'Local'.

**Example(s):**
```
public interface MyLocal extends javax.ejb.EJBLocalObject {}				// proper name

 public interface MissingProperSuffix extends javax.ejb.EJBLocalObject {}	// non-standard name
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## LocalHomeNamingConvention
**Since:** 4.0

**Priority:** Medium Low (4)

The Local Home interface of a Session EJB should be suffixed by 'LocalHome'.

**Example(s):**
```
public interface MyBeautifulLocalHome extends javax.ejb.EJBLocalHome {}// proper name

 public interface MissingProperSuffix extends javax.ejb.EJBLocalHome {}	// non-standard name
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## RemoteInterfaceNamingConvention
**Since:** 4.0

**Priority:** Medium Low (4)

Remote Interface of a Session EJB should not have a suffix.

**Example(s):**
```
/* Poor Session suffix */
 public interface BadSuffixSession extends javax.ejb.EJBObject {}

 /* Poor EJB suffix */
 public interface BadSuffixEJB extends javax.ejb.EJBObject {}

 /* Poor Bean suffix */
 public interface BadSuffixBean extends javax.ejb.EJBObject {}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## DoNotCallSystemExit
**Since:** 4.1

**Priority:** Medium (3)

Web applications should not call System.exit(), since only the web container or the
application server should stop the JVM. This rule also checks for the equivalent call Runtime.getRuntime().exit().

**Example(s):**
```
public void bar() {
    System.exit(0);                 // never call this when running in an application server!
}
public void foo() {
    Runtime.getRuntime().exit(0);   // never stop the JVM manually, the container will do this.
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## StaticEJBFieldShouldBeFinal
**Since:** 4.1

**Priority:** Medium (3)

According to the J2EE specification, an EJB should not have any static fields
with write access. However, static read-only fields are allowed. This ensures proper
behavior especially when instances are distributed by the container on several JREs.

**Example(s):**
```
public class SomeEJB extends EJBObject implements EJBLocalHome {

	private static int CountA;			// poor, field can be edited

	private static final int CountB;	// preferred, read-only access
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|violationSuppressRegex||Suppress violations with messages matching a regular expression|
|violationSuppressXPath||Suppress violations on nodes which match a given relative XPath expression.|
|version|1.0|XPath specification version|
|xpath||XPath expression|

## DoNotUseThreads
**Since:** 4.1

**Priority:** Medium (3)

The J2EE specification explicitly forbids the use of threads.

**Example(s):**
```
// This is not allowed
public class UsingThread extends Thread {

 }
	// Neither this,
public class OtherThread implements Runnable {
	// Nor this ...
	public void methode() {
			Runnable thread = new Thread(); thread.run();
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

