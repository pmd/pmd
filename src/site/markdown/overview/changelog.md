# Changelog

## ????? - 5.5.2-SNAPSHOT

**New Supported Languages:**

*   CPD now supports Groovy. See [PR#107](https://github.com/pmd/pmd/pull/107).

**Feature Request and Improvements:**

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#106](https://github.com/pmd/pmd/pull/106): \[java] CPD: Keep constructor names under ignoreIdentifiers
*   [#107](https://github.com/pmd/pmd/pull/107): \[groovy] Initial support for CPD Groovy

**Bugfixes:**

*   java-logging-java
    *   [#1500](https://sourceforge.net/p/pmd/bugs/1500/) \[java] InvalidSlf4jMessageFormat: doesn't ignore exception param
    *   [#1509](https://sourceforge.net/p/pmd/bugs/1509/) \[java] InvalidSlf4jMessageFormat: NPE
*   General
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1517](https://sourceforge.net/p/pmd/bugs/1517/): \[java] CPD reports on Java constructors when using ignoreIdentifiers


**API Changes:**
