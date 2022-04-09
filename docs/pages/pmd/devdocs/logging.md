---
title: Logging
tags: [devdocs]
permalink: pmd_devdocs_logging.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

## Logging API is slf4j

PMD uses [slf4j](https://www.slf4j.org/) as the logging API internally. Logging can be done also in
(Java-based) rules.

In order to log, get a logger. Usually the logger is stored in a static final field called `LOG`:

```java
    private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
```

## In Unit Tests

If you need log output in unit tests, make sure to have a logging implementation on the test classpath.
E.g. you can add (if it is missing) the following dependency:

```xml
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <scope>test</scope>
        </dependency>
```

To configure the logging, create the file `src/test/resources/simplelogger.properties`:

```
org.slf4j.simpleLogger.logFile=System.err
org.slf4j.simpleLogger.showDateTime=false
org.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd'T'HH:mm:ss.SSSXXX
org.slf4j.simpleLogger.showThreadName=true
org.slf4j.simpleLogger.showThreadId=false
org.slf4j.simpleLogger.showLogName=true
org.slf4j.simpleLogger.showShortLogName=false
org.slf4j.simpleLogger.levelInBrackets=false

# Default log level for all loggers
# Must be one of "trace", "debug", "info", "warn", "error" or "off"
# Will be changed by "--debug" command line option
org.slf4j.simpleLogger.defaultLogLevel=info

# configure logging detail level for a single logger.
# Must be one of "trace", "debug", "info", "warn", "error" or "off"
#org.slf4j.simpleLogger.log.net.sourceforge.pmd.PMD=debug
#org.slf4j.simpleLogger.log.com.example.rules.MyRule=debug
```

If you want to verify log output in unit tests, you can use `org.junit.contrib.java.lang.system.SystemErrRule`.
Disabling the logging in this property file will the make the tests fail of course.

## Binary distribution

The binary distribution ships with also with `slf4j-simple` as the logger implementation.
The default configuration is provided in `pmd-dist/src/main/resources/config/simplelogger.properties`.


