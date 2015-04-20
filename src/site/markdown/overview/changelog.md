# Changelog

## 20-April-2015 - 5.3.1

**New/Modified/Deprecated Rules:**

*   Language Java, ruleset design.xml: The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
    See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

**Pull Requests:**

*   [#53](https://github.com/pmd/pmd/pull/53): Fix some NullPointerExceptions

**Bugfixes:**

*   [#1332](https://sourceforge.net/p/pmd/bugs/1332/): False Positive: UnusedPrivateMethod
*   [#1333](https://sourceforge.net/p/pmd/bugs/1333/): Error while processing Java file with Lambda expressions
*   [#1337](https://sourceforge.net/p/pmd/bugs/1337/): False positive "Avoid throwing raw exception types" when exception is not thrown
*   [#1338](https://sourceforge.net/p/pmd/bugs/1338/): The pmd-java8 POM bears the wrong parent module version
