# Changelog

## ????? - 5.3.8-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1360](https://sourceforge.net/p/pmd/bugs/1360/): Provide backwards compatibility for PMD configuration file

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/CloseResource
    *   [#1479](https://sourceforge.net/p/pmd/bugs/1479/): CloseResource false positive on Statement
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   java-unusedcode/UnusedLocalVariable
    *   [#1484](https://sourceforge.net/p/pmd/bugs/1484/): UnusedLocalVariable - false positive - parenthesis
*   java-unusedcode/UnusedModifier
    *   [#1480](https://sourceforge.net/p/pmd/bugs/1480/): false positive on public modifier used with inner interface in enum
*   PLSQL
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1481](https://sourceforge.net/p/pmd/bugs/1481/): no problems found results in blank file instead of empty xml
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles

**API Changes:**

*   New command line parameter for PMD: `-norulesetcompatibility` - this disables the ruleset factory
    compatibility filter and fails, if e.g. an old rule name is used in the ruleset.
    See also [#1360](https://sourceforge.net/p/pmd/bugs/1360/).
    This option is also available for the ant task: `<noRuleSetCompatibility>true</noRuleSetCompatibility>`.
*   CPD: If no problems found, an empty report will be output instead of nothing. See also [#1481](https://sourceforge.net/p/pmd/bugs/1481/)