# Changelog

## ????? - 5.5.3-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

*   java
    *   Type Resolution performance improved by ~15%

**New/Modified/Deprecated Rules:**

*   apex
    *   New Security ruleset including:
        *   ApexBadCrypto
        *   ApexCRUDViolation
        *   ApexCSRF
        *   ApexDangerousMethods
        *   ApexInsecureEndpoint
        *   ApexOpenRedirect
        *   ApexSharingViolations
        *   ApexSOQLInjection
        *   ApexXSSFromEscapeFalse
        *   ApexXSSFromURLParam

**Pull Requests:**

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#124](https://github.com/pmd/pmd/pull/124): \[java] CPD: Properly handle enums with `-ignore-identifiers`
*   [#126](https://github.com/pmd/pmd/pull/126): \[java] Avoid creating a new String to qualify types
*   [#127](https://github.com/pmd/pmd/pull/127): \[java] Don't look twice for the same variables
*   [#128](https://github.com/pmd/pmd/pull/128): \[java] Minor optimizations to type resolution
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#130](https://github.com/pmd/pmd/pull/130); \[core] Reduce thread contention
*   [#133](https://github.com/pmd/pmd/pull/133): \[java] UnnecessaryFullyQualifiedName can detect conflicts
*   [#134](https://github.com/pmd/pmd/pull/134): \[java] Symbol table can now handle inner classes
*   [#135](https://github.com/pmd/pmd/pull/135): \[apex] New ruleset for Apex security
*   [#137](https://github.com/pmd/pmd/pull/137): \[apex] Adjusted remediation points
*   [#138](https://github.com/pmd/pmd/pull/138): \[java] Make ClasspathClassLoader parallel capable
*   [#140](https://github.com/pmd/pmd/pull/140): \[java] Make CloneMethodMustImplementCloneable over 500x faster
*   [#141](https://github.com/pmd/pmd/pull/141): \[java] Speedup PreserveStackTraceRule by over 7X
*   [#146](https://github.com/pmd/pmd/pull/146): \[apex] Detection of missing Apex CRUD checks for SOQL/DML operations
*   [#147](https://github.com/pmd/pmd/pull/147): \[apex] Adding XSS detection to return statements
*   [#148](https://github.com/pmd/pmd/pull/148): \[apex] Improving detection of SOQL injection
*   [#149](https://github.com/pmd/pmd/pull/149): \[apex] Whitelisting String.isEmpty and casting
*   [#152](https://github.com/pmd/pmd/pull/152): \[java] fixes #1552 continue does not require break
*   [#154](https://github.com/pmd/pmd/pull/154): \[java] Fix #1547: UnusedImports: Adjust regex to support underscores
*   [#158](https://github.com/pmd/pmd/pull/158): \[apex] Reducing FPs in SOQL with VF getter methods
*   [#160](https://github.com/pmd/pmd/pull/160): \[apex] Flagging of dangerous method call

**Bugfixes:**

*   General
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   Java
    *   [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-design
    *   [#1552](https://sourceforge.net/p/pmd/bugs/1552/): \[java] MissingBreakInSwitch - False positive for continue
*   java-imports
    *   [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
    *   [#1547](https://sourceforge.net/p/pmd/bugs/1547/): \[java] UnusedImportRule - False Positive for only usage in Javadoc - {@link ClassName#CONSTANT}
*   java-logging-java
    *   [#1541](https://sourceforge.net/p/pmd/bugs/1541/): \[java] InvalidSlf4jMessageFormat: False positive with placeholder and exception
    *   [#1551](https://sourceforge.net/p/pmd/bugs/1551/): \[java] InvalidSlf4jMessageFormat: fails with NPE
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct


**API Changes:**
