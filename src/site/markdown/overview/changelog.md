# Changelog

## ????? - 5.4.4-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

*   java
    *   Type Resolution performance improved by ~15%

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#126](https://github.com/pmd/pmd/pull/126): \[java] Avoid creating a new String to qualify types
*   [#127](https://github.com/pmd/pmd/pull/127): \[java] Don't look twice for the same variables
*   [#128](https://github.com/pmd/pmd/pull/128): \[java] Minor optimizations to type resolution
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#130](https://github.com/pmd/pmd/pull/130); \[core] Reduce thread contention
*   [#133](https://github.com/pmd/pmd/pull/133): \[java] UnnecessaryFullyQualifiedName can detect conflicts
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores

**Bugfixes:**

*   java-design
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct


**API Changes:**
