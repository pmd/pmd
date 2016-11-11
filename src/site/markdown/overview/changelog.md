# Changelog

## ????? - 5.5.3-SNAPSHOT

**New Supported Languages:**

**Feature Requests and Improvements:**

*   Core
    *     [#1538](https://sourceforge.net/p/pmd/bugs/1538/): \[core] Incremental analysis - All PMD analysis can now run incrementally using a local file cache. This can greatly reduce the analysis time when running from CLI or tools such as Ant, Maven or Gradle. New CLI and tasks `cache` argument i exposed.

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#123](https://github.com/pmd/pmd/pull/123): \[apex] Changing method names to lowercase so casing doesn't matter
*   [#125](https://github.com/pmd/pmd/pull/125): \[core] Incremental analysis

**Bugfixes:**

*    apex-apexunit
     *    [#1543](https://sourceforge.net/p/pmd/bugs/1543/): \[apex] ApexUnitTestClassShouldHaveAsserts assumes APEX is case sensitive

**API Changes:**
