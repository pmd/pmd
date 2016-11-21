# Changelog

## ????? - 5.6.0-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

*   java
    *   Type Resolution performance improved by ~15%

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
*   [#126](https://github.com/pmd/pmd/pull/126): \[java] Avoid creating a new String to qualify types
*   [#135](https://github.com/pmd/pmd/pull/135): \[apex] New ruleset for Apex security


**Bugfixes:**

*   General
    *   [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers
*   apex-apexunit
    *   [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   XML
    *   [#1518](https://sourceforge.net/p/pmd/bugs/1518/): \[xml] Error while processing xml file with ".webapp" in the file or directory name


**API Changes:**
