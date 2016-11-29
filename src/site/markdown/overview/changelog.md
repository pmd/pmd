# Changelog

## ????? - 5.6.0-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

*   java
    *   Type Resolution performance improved by ~15%
*   Core
    *   [#1538](https://sourceforge.net/p/pmd/bugs/1538/): \[core] Incremental analysis - All PMD analysis can now run
        incrementally using a local file cache. This can greatly reduce the analysis time when running from CLI or tools
        such as Ant, Maven or Gradle. New CLI and tasks `cache` argument i exposed.

**New/Modified/Deprecated Rules:**

*   apex
    *   New Security ruleset including:
        *   ApexBadCrypto
        *   ApexCSRF
        *   ApexInsecureEndpoint
        *   ApexOpenRedirect
        *   ApexSharingViolations
        *   ApexSOQLInjection
        *   ApexXSSFromEscapeFalse
        *   ApexXSSFromURLParam

**Pull Requests:**

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#124](https://github.com/pmd/pmd/pull/124): \[java] CPD: Properly handle enums with `-ignore-identifiers`
*   [#125](https://github.com/pmd/pmd/pull/125): \[core] Incremental analysis
*   [#126](https://github.com/pmd/pmd/pull/126): \[java] Avoid creating a new String to qualify types
*   [#127](https://github.com/pmd/pmd/pull/127): \[java] Don't look twice for the same variables
*   [#128](https://github.com/pmd/pmd/pull/128): \[java] Minor optimizations to type resolution
*   [#129](https://github.com/pmd/pmd/pull/129): \[plsql] Added correct parse of IS [NOT] NULL and multiline DML
*   [#130](https://github.com/pmd/pmd/pull/130); \[core] Reduce thread contention
*   [#131](https://github.com/pmd/pmd/pull/131): \[core] Make RuleSetFactory immutable
*   [#133](https://github.com/pmd/pmd/pull/133): \[java] UnnecessaryFullyQualifiedName can detect conflicts
*   [#134](https://github.com/pmd/pmd/pull/134): \[java] Symbol table can now handle inner classes
*   [#135](https://github.com/pmd/pmd/pull/135): \[apex] New ruleset for Apex security
*   [#137](https://github.com/pmd/pmd/pull/137): \[apex] Adjusted remediation points

**Bugfixes:**

*   General
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   Java
    *    [#1545](https://sourceforge.net/p/pmd/bugs/1545/): \[java] Symbol Table fails to resolve inner classes
*   java-imports
    *    [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name
*   psql
    *   [#1549](https://sourceforge.net/p/pmd/bugs/1549/): \[plsql] Parse error for IS [NOT] NULL construct


**API Changes:**

*   `net.sourceforge.pmd.RuleSetFactory` is now immutable and its behavior cannot be changed anymore.
    It provides constructors to create new adjusted instances. This allows to avoid synchronization in RuleSetFactory.
    See [PR #131](https://github.com/pmd/pmd/pull/131).
