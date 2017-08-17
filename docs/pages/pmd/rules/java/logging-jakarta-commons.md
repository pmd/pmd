---
title: Jakarta Commons Logging
summary: The Jakarta Commons Logging ruleset contains a collection of rules that find questionable usages of that framework.
permalink: pmd_rules_java_logging-jakarta-commons.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/logging-jakarta-commons.xml
keywords: Jakarta Commons Logging, UseCorrectExceptionLogging, ProperLogger, GuardDebugLogging, GuardLogStatement
---
## GuardDebugLogging

**Since:** PMD 4.3

**Priority:** Medium (3)

When log messages are composed by concatenating strings, the whole section should be guarded
by a isDebugEnabled() check to avoid performance and memory issues.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardDebugLoggingRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardDebugLoggingRule.java)

**Example(s):**

``` java
public class Test {
    private static final Log __log = LogFactory.getLog(Test.class);
    public void test() {
        // okay:
        __log.debug("log something");

        // okay:
        __log.debug("log something with exception", e);

        // bad:
        __log.debug("log something" + " and " + "concat strings");

        // bad:
        __log.debug("log something" + " and " + "concat strings", e);

        // good:
        if (__log.isDebugEnabled()) {
        __log.debug("bla" + "",e );
        }
    }
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

## GuardLogStatement

**Since:** PMD 5.1.0

**Priority:** Medium High (2)

Whenever using a log level, one should check if the loglevel is actually enabled, or
otherwise skip the associate String creation and manipulation.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.logging.GuardLogStatementRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/logging/GuardLogStatementRule.java)

**Example(s):**

``` java
// Add this for performance
    if (log.isDebugEnabled() { ...
        log.debug("log something" + " and " + "concat strings");
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|guardsMethods|[]|method use to guard the log statement|
|logLevels|[]|LogLevels to guard|

## ProperLogger

**Since:** PMD 3.3

**Priority:** Medium (3)

A logger should normally be defined private static final and be associated with the correct class.
Private final Log log; is also allowed for rare cases where loggers need to be passed around,
with the restriction that the logger needs to be passed into the constructor.

```
//ClassOrInterfaceBodyDeclaration[FieldDeclaration//ClassOrInterfaceType[@Image='Log']
 and
 not(FieldDeclaration[@Final='true'][@Static='true'][@Private='true'][.//VariableDeclaratorId[@Image=$staticLoggerName]]
 //ArgumentList//ClassOrInterfaceType/@Image = ancestor::ClassOrInterfaceDeclaration/@Image)
 and
 not(FieldDeclaration[@Final='true'][@Private='true'][.//VariableDeclaratorId[@Image='log']]
 [count(.//VariableInitializer)=0]
 [ancestor::ClassOrInterfaceBody//StatementExpression[.//PrimaryExpression/descendant::*[@Image='log']][count(.//AllocationExpression)=0]]
 )]
```

**Example(s):**

``` java
public class Foo {

    private static final Log LOG = LogFactory.getLog(Foo.class);    // proper way

    protected Log LOG = LogFactory.getLog(Testclass.class);         // wrong approach
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|staticLoggerName|LOG|Name of the static Logger variable|

## UseCorrectExceptionLogging

**Since:** PMD 3.2

**Priority:** Medium (3)

To make sure the full stacktrace is printed out, use the logging statement with two arguments: a String and a Throwable.

```
//CatchStatement/Block/BlockStatement/Statement/StatementExpression
/PrimaryExpression[PrimaryPrefix/Name[starts-with(@Image,
concat(ancestor::ClassOrInterfaceDeclaration/ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/FieldDeclaration
[Type//ClassOrInterfaceType[@Image='Log']]
/VariableDeclarator/VariableDeclaratorId/@Image, '.'))]]
[PrimarySuffix/Arguments[@ArgumentCount='1']]
[PrimarySuffix/Arguments//Name/@Image = ancestor::CatchStatement/FormalParameter/VariableDeclaratorId/@Image]
```

**Example(s):**

``` java
public class Main {
    private static final Log _LOG = LogFactory.getLog( Main.class );
    void bar() {
        try {
        } catch( Exception e ) {
            _LOG.error( e ); //Wrong!
        } catch( OtherException oe ) {
            _LOG.error( oe.getMessage(), oe ); //Correct
        }
    }
}
```

