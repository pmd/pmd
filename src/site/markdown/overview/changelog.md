# Changelog

## ????? - 5.3.8-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**New/Modified/Deprecated Rules:**

**Pull Requests:**

**Bugfixes:**

*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): UnusedLocalVariable - false positive - parenthesis
*   General
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): no problems found results in blank file instead of empty xml

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)