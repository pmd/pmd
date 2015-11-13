# Changelog

## ????? - 5.3.6-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
    *   Note: This also contains the fix from [#23](https://github.com/adangel/pmd/pull/23)
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-finalizers/AvoidCallingFinalize
    *   [#1440](https://sourceforge.net/p/pmd/bugs/1440/): NPE in AvoidCallingFinalize
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable hides member variable
*   General
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output

**API Changes:**
