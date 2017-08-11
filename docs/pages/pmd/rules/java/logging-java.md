---
title: Java Logging
summary: The Java Logging ruleset contains a collection of rules that find questionable usages of the logger.
permalink: pmd_rules_java_logging-java.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/logging-java.xml
---
## AvoidPrintStackTrace

**Since:** 3.2

**Priority:** Medium (3)

Avoid printStackTrace(); use a logger call instead.

**Example(s):**

```
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

## GuardLogStatementJavaUtil

**Since:** 5.1.0

**Priority:** Medium High (2)

Whenever using a log level, one should check if the loglevel is actually enabled, or
otherwise skip the associate String creation and manipulation.

**Example(s):**

```
// Add this for performance
	if (log.isLoggable(Level.FINE)) { ...
 	    log.fine("log something" + " and " + "concat strings");
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

## InvalidSlf4jMessageFormat

**Since:** 5.5.0

**Priority:** Low (5)

Check for messages in slf4j loggers with non matching number of arguments and placeholders.

**Example(s):**

```
LOGGER.error("forget the arg {}");
LOGGER.error("too many args {}", "arg1", "arg2");
LOGGER.error("param {}", "arg1", new IllegalStateException("arg")); //The exception is shown separately, so is correct.
```

## LoggerIsNotStaticFinal

**Since:** 2.0

**Priority:** Medium High (2)

In most cases, the Logger reference can be declared as static and final.

**Example(s):**

```
public class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());					// not recommended

    static final Logger log = Logger.getLogger(Foo.class.getName());	// preferred approach
}
```

## MoreThanOneLogger

**Since:** 2.0

**Priority:** Medium High (2)

Normally only one logger is used in each class.

**Example(s):**

```
public class Foo {
    Logger log = Logger.getLogger(Foo.class.getName());
    // It is very rare to see two loggers on a class, normally
    // log information is multiplexed by levels
    Logger log2= Logger.getLogger(Foo.class.getName());
}
```

## SystemPrintln

**Since:** 2.1

**Priority:** Medium High (2)

References to System.(out|err).print are usually intended for debugging purposes and can remain in
the codebase even in production code. By using a logger one can enable/disable this behaviour at
will (and by priority) and avoid clogging the Standard out log.

**Example(s):**

```
class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());
    public void testA () {
        System.out.println("Entering test");
        // Better use this
        log.fine("Entering test");
    }
}
```

