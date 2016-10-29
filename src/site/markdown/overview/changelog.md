# Changelog

## ????? - 5.4.3-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#35](https://github.com/adangel/pmd/pull/35): Javascript tokenizer now ignores comment tokens.
*   [#103](https://github.com/pmd/pmd/pull/103): \[java] \[apex] Fix for 1501: CyclomaticComplexity rule causes OOM when class reporting is disabled
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable

**Bugfixes:**

*   Java
    *   [#1501](https://sourceforge.net/p/pmd/bugs/1501/): \[java] \[apex] CyclomaticComplexity rule causes OOM when class reporting is disabled
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   General
    *   [#1499](https://sourceforge.net/p/pmd/bugs/1499/): \[core] CPD test break PMD 5.5.1 build on Windows
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1508](https://sourceforge.net/p/pmd/bugs/1508/): \[core] \[java] PMD is leaking file handles

**API Changes:**
