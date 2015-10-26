# Changelog

## ????? - 5.4.1-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   CPD: New command line parameter `--ignore-usings`: Ignore using directives in C# when comparing text.

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#25](https://github.com/adangel/pmd/pull/25): Added option to exclude C# using directives from CPD analysis
*   [#72](https://github.com/pmd/pmd/pull/72): Added capability in Java and JSP parser for tracking tokens.
*   [#74](https://github.com/pmd/pmd/pull/74): Fix rendering CommentDefaultAccessModifier description as code
*   [#75](https://github.com/pmd/pmd/pull/75): RuleSetFactory Performance Enhancement

**Bugfixes:**

*   java-comments/CommentDefaultAccessModifier
    *   [#1430](https://sourceforge.net/p/pmd/bugs/1430/): CommentDefaultAccessModifier triggers on field
        annotated with @VisibleForTesting
*   java-unusedcode/UnusedPrivateField
    *   [#1428](https://sourceforge.net/p/pmd/bugs/1428/): False positive in UnusedPrivateField when local variable
        hides member variable
*   General
    *   [#1425](https://sourceforge.net/p/pmd/bugs/1425/): Invalid XML Characters in Output
    *   [#1429](https://sourceforge.net/p/pmd/bugs/1429/): Java - Parse Error: Cast in return expression

**API Changes:**
