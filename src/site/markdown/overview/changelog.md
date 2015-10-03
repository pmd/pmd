# Changelog

## ????? - 5.3.5-SNAPSHOT

**New Supported Languages:**


**Feature Request and Improvements:**


**New/Modified/Deprecated Rules:**

*   java-design/CloseResource: New Property *closeAsDefaultTarget* which is *true* by default to stay
    backwards compatible. If this property is *true*, the rule will make sure, that `close` itself is
    always considered as a *closeTarget* - no matter whether it is configured with the *closeTargets* property
    or not.

**Pull Requests:**

*   [#71](https://github.com/pmd/pmd/pull/71): #1410 Improve description of DefaultPackage rule

**Bugfixes:**

*   java-controversial/DefaultPackage:
    *    [#1410](https://sourceforge.net/p/pmd/bugs/1410/): DefaultPackage triggers on field annotated with @VisibleForTesting
*   java-design/CloseResource:
    *    [#1387](https://sourceforge.net/p/pmd/bugs/1387/): CloseResource has false positive for ResultSet
*   java-strings/InsufficientStringBufferDeclaration:
    *    [#1409](https://sourceforge.net/p/pmd/bugs/1409/): NullPointerException in InsufficientStringBufferRule
    *    [#1413](https://sourceforge.net/p/pmd/bugs/1413/): False positive StringBuffer constructor with ?: int value
*   java-unnecessary/UselessParentheses:
    *    [#1407](https://sourceforge.net/p/pmd/bugs/1407/): UselessParentheses "&" and "+" operator precedence


**API Changes:**

