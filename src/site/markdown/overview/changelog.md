# Changelog

## 27-July-2016 - 5.5.1

**Pull Requests:**

*   [#101](https://github.com/pmd/pmd/pull/101): \[java] Improve multithreading performance: do not lock on classloader
*   [#102](https://github.com/pmd/pmd/pull/102): \[apex] Restrict AvoidLogicInTrigger rule to max. 1 violation per file
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#104](https://github.com/pmd/pmd/pull/104): \[core] \[java] Close opened file handles
*   [apex #43](https://github.com/Up2Go/pmd/pull/43): \[apex] Basic apex unit test rules

**Bugfixes:**

*   Apex
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles
