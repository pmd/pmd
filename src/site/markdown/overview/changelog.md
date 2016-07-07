# Changelog

## ????? - 5.3.8-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled

**Bugfixes:**

*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): CloseResource false positive on Statement
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): UnusedLocalVariable - false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): false positive on public modifier used with inner interface in enum
*   General
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): no problems found results in blank file instead of empty xml
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)