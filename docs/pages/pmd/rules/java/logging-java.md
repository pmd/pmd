---
title: Java Logging
summary: The Java Logging ruleset contains a collection of rules that find questionable usages of the logger.
permalink: pmd_rules_java_logging-java.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/logging-java.xml
keywords: Java Logging, MoreThanOneLogger, LoggerIsNotStaticFinal, SystemPrintln, AvoidPrintStackTrace, GuardLogStatementJavaUtil, InvalidSlf4jMessageFormat
---
## AvoidPrintStackTrace

**Since:** PMD 3.2

**Priority:** Medium (3)

Avoid printStackTrace(); use a logger call instead.

```
//PrimaryExpression
 [PrimaryPrefix/Name[contains(@Image,'printStackTrace')]]
 [PrimarySuffix[not(boolean(Arguments/ArgumentList/Expression))]]
```

**Example(s):**

``` java
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

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/AvoidPrintStackTrace" />
```

## GuardLogStatementJavaUtil

**Since:** PMD 5.1.0

**Priority:** Medium High (2)

Whenever using a log level, one should check if the loglevel is actually enabled, or
otherwise skip the associate String creation and manipulation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardLogStatementJavaUtilRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardLogStatementJavaUtilRule.java)

**Example(s):**

``` java
//...
// Add this for performance
if (log.isLoggable(Level.FINE)) {
    log.fine("log something" + " and " + "concat strings");
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/GuardLogStatementJavaUtil" />
```

## InvalidSlf4jMessageFormat

**Since:** PMD 5.5.0

**Priority:** Low (5)

Check for messages in slf4j loggers with non matching number of arguments and placeholders.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.InvalidSlf4jMessageFormatRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/InvalidSlf4jMessageFormatRule.java)

**Example(s):**

``` java
LOGGER.error("forget the arg {}");
LOGGER.error("too many args {}", "arg1", "arg2");
LOGGER.error("param {}", "arg1", new IllegalStateException("arg")); //The exception is shown separately, so is correct.
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/InvalidSlf4jMessageFormat" />
```

## LoggerIsNotStaticFinal

**Since:** PMD 2.0

**Priority:** Medium High (2)

In most cases, the Logger reference can be declared as static and final.

```
//VariableDeclarator
 [parent::FieldDeclaration]
 [../Type/ReferenceType
  /ClassOrInterfaceType[@Image='Logger']
   and
  (..[@Final='false'] or ..[@Static = 'false'] ) ]
```

**Example(s):**

``` java
public class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());                 // not recommended

    static final Logger log = Logger.getLogger(Foo.class.getName());    // preferred approach
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/LoggerIsNotStaticFinal" />
```

## MoreThanOneLogger

**Since:** PMD 2.0

**Priority:** Medium High (2)

Normally only one logger is used in each class.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.MoreThanOneLoggerRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/MoreThanOneLoggerRule.java)

**Example(s):**

``` java
public class Foo {
    Logger log = Logger.getLogger(Foo.class.getName());
    // It is very rare to see two loggers on a class, normally
    // log information is multiplexed by levels
    Logger log2= Logger.getLogger(Foo.class.getName());
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/MoreThanOneLogger" />
```

## SystemPrintln

**Since:** PMD 2.1

**Priority:** Medium High (2)

References to System.(out|err).print are usually intended for debugging purposes and can remain in
the codebase even in production code. By using a logger one can enable/disable this behaviour at
will (and by priority) and avoid clogging the Standard out log.

```
//Name[
    starts-with(@Image, 'System.out.print')
    or
    starts-with(@Image, 'System.err.print')
    ]
```

**Example(s):**

``` java
class Foo{
    Logger log = Logger.getLogger(Foo.class.getName());
    public void testA () {
        System.out.println("Entering test");
        // Better use this
        log.fine("Entering test");
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/logging-java.xml/SystemPrintln" />
```

