# Changelog

## ????? - 5.3.8-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**New/Modified/Deprecated Rules:**

**Pull Requests:**

**Bugfixes:**

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.