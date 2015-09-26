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


**Bugfixes:**

*   java-controversial/DefaultPackage:
    [#1410](https://sourceforge.net/p/pmd/bugs/1410/): DefaultPackage triggers on field annotated with @VisibleForTesting
*   java-design/CloseResource:
    [#1387](https://sourceforge.net/p/pmd/bugs/1387/): CloseResource has false positive for ResultSet
*   java-strings/InsufficientStringBufferDeclaration:
    [#1409](https://sourceforge.net/p/pmd/bugs/1409/): NullPointerException in InsufficientStringBufferRule

**API Changes:**

