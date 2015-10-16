# Changelog

## ????? - 5.5.0-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

**New/Modified/Deprecated Rules:**

*   Java
    *   Logging Java: **InvalidSlf4jMessageFormat** (rulesets/java/logging-java.xml/InvalidSlf4jMessageFormat)<br/>
        Check for invalid message format in slf4j loggers.

**Pull Requests:**

*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#73](https://github.com/pmd/pmd/pull/73): Add rule to look for invalid message format in slf4j loggers
*   [#74](https://github.com/pmd/pmd/pull/74): Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement
*   [#76](https://github.com/pmd/pmd/pull/76): fix formatting typos in an example of the DoNotCallGarbageCollectionExplicitly rule

**Bugfixes:**

*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression

**API Changes:**
