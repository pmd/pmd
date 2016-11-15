# Changelog

## ????? - 5.5.3-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#124](https://github.com/pmd/pmd/pull/124): \[java] CPD: Properly handle enums with `-ignore-identifiers`
*   [#133](https://github.com/pmd/pmd/pull/133): \[java] UnnecessaryFullyQualifiedName can detect conflicts

**Bugfixes:**

*   apex-apexunit
    *    [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive
*   java-imports
    *    [#1546](https://sourceforge.net/p/pmd/bugs/1546/): \[java] UnnecessaryFullyQualifiedNameRule doesn't take into consideration conflict resolution
*   General
    *    [#1542](https://sourceforge.net/p/pmd/bugs/1542/): \[java] CPD throws an NPE when parsing enums with -ignore-identifiers

**API Changes:**
