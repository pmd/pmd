---
title: J2EE
summary: Rules specific to the use of J2EE implementations.
permalink: pmd_rules_java_j2ee.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/j2ee.xml
keywords: J2EE, UseProperClassLoader, MDBAndSessionBeanNamingConvention, RemoteSessionInterfaceNamingConvention, LocalInterfaceSessionNamingConvention, LocalHomeNamingConvention, RemoteInterfaceNamingConvention, DoNotCallSystemExit, StaticEJBFieldShouldBeFinal, DoNotUseThreads
---
## DoNotCallSystemExit

**Since:** PMD 4.1

**Priority:** Medium (3)

Web applications should not call System.exit(), since only the web container or the
application server should stop the JVM. This rule also checks for the equivalent call Runtime.getRuntime().exit().

```
//Name[
    starts-with(@Image,'System.exit')
    or
    (starts-with(@Image,'Runtime.getRuntime') and ../../PrimarySuffix[ends-with(@Image,'exit')])
]
```

**Example(s):**

``` java
public void bar() {
    System.exit(0);                 // never call this when running in an application server!
}
public void foo() {
    Runtime.getRuntime().exit(0);   // never stop the JVM manually, the container will do this.
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/DoNotCallSystemExit" />
```

## DoNotUseThreads

**Since:** PMD 4.1

**Priority:** Medium (3)

The J2EE specification explicitly forbids the use of threads.

```
//ClassOrInterfaceType[@Image = 'Thread' or @Image = 'Runnable']
```

**Example(s):**

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/DoNotUseThreads" />
```

## LocalHomeNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The Local Home interface of a Session EJB should be suffixed by 'LocalHome'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalHome')])
    )
    and
    not
    (
        ends-with(@Image,'LocalHome')
    )
]
```

**Example(s):**

``` java
public interface MyBeautifulLocalHome extends javax.ejb.EJBLocalHome {} // proper name

public interface MissingProperSuffix extends javax.ejb.EJBLocalHome {}  // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/LocalHomeNamingConvention" />
```

## LocalInterfaceSessionNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The Local Interface of a Session EJB should be suffixed by 'Local'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalObject')])
    )
    and
    not
    (
        ends-with(@Image,'Local')
    )
]
```

**Example(s):**

``` java
public interface MyLocal extends javax.ejb.EJBLocalObject {}                // proper name

public interface MissingProperSuffix extends javax.ejb.EJBLocalObject {}    // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/LocalInterfaceSessionNamingConvention" />
```

## MDBAndSessionBeanNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

The EJB Specification states that any MessageDrivenBean or SessionBean should be suffixed by 'Bean'.

```
//TypeDeclaration/ClassOrInterfaceDeclaration
[
    (
        (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'SessionBean')])
        or
        (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'MessageDrivenBean')])
    )
    and
    not
    (
        ends-with(@Image,'Bean')
    )
]
```

**Example(s):**

``` java
public class SomeBean implements SessionBean{}                  // proper name

public class MissingTheProperSuffix implements SessionBean {}   // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/MDBAndSessionBeanNamingConvention" />
```

## RemoteInterfaceNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

Remote Interface of a Session EJB should not have a suffix.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBObject')])
    )
    and
    (
        ends-with(@Image,'Session')
        or
        ends-with(@Image,'EJB')
        or
        ends-with(@Image,'Bean')
    )
]
```

**Example(s):**

``` java
/* Poor Session suffix */
public interface BadSuffixSession extends javax.ejb.EJBObject {}

/* Poor EJB suffix */
public interface BadSuffixEJB extends javax.ejb.EJBObject {}

/* Poor Bean suffix */
public interface BadSuffixBean extends javax.ejb.EJBObject {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/RemoteInterfaceNamingConvention" />
```

## RemoteSessionInterfaceNamingConvention

**Since:** PMD 4.0

**Priority:** Medium Low (4)

A Remote Home interface type of a Session EJB should be suffixed by 'Home'.

```
//ClassOrInterfaceDeclaration
[
    (
        (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBHome')])
    )
    and
    not
    (
        ends-with(@Image,'Home')
    )
]
```

**Example(s):**

``` java
public interface MyBeautifulHome extends javax.ejb.EJBHome {}       // proper name

public interface MissingProperSuffix extends javax.ejb.EJBHome {}   // non-standard name
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/RemoteSessionInterfaceNamingConvention" />
```

## StaticEJBFieldShouldBeFinal

**Since:** PMD 4.1

**Priority:** Medium (3)

According to the J2EE specification, an EJB should not have any static fields
with write access. However, static read-only fields are allowed. This ensures proper
behavior especially when instances are distributed by the container on several JREs.

```
//ClassOrInterfaceDeclaration[
    (
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'SessionBean')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBHome')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalObject')])
    or
    (./ImplementsList/ClassOrInterfaceType[ends-with(@Image,'EJBLocalHome')])
    or
    (./ExtendsList/ClassOrInterfaceType[ends-with(@Image,'EJBObject')])
    )
    and
    (./ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration[
         (./FieldDeclaration[@Static = 'true'])
         and
         (./FieldDeclaration[@Final = 'false'])
    ])
]
```

**Example(s):**

``` java
public class SomeEJB extends EJBObject implements EJBLocalHome {

    private static int CountA;          // poor, field can be edited

    private static final int CountB;    // preferred, read-only access
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/StaticEJBFieldShouldBeFinal" />
```

## UseProperClassLoader

**Since:** PMD 3.7

**Priority:** Medium (3)

In J2EE, the getClassLoader() method might not work as expected. Use 
Thread.currentThread().getContextClassLoader() instead.

```
//PrimarySuffix[@Image='getClassLoader']
```

**Example(s):**

``` java
public class Foo {
    ClassLoader cl = Bar.class.getClassLoader();
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/j2ee.xml/UseProperClassLoader" />
```

