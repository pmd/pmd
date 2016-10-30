# Changelog

## ????? - 5.5.2-SNAPSHOT

**New Supported Languages:**

*   CPD now supports Groovy. See [PR#107](https://github.com/pmd/pmd/pull/107).

**Feature Request and Improvements:**

**New/Modified/Deprecated Rules:**

**Pull Requests:**

*   [#106](https://github.com/pmd/pmd/pull/106): \[java] CPD: Keep constructor names under ignoreIdentifiers
*   [#107](https://github.com/pmd/pmd/pull/107): \[groovy] Initial support for CPD Groovy
*   [#110](https://github.com/pmd/pmd/pull/110): \[java] Fix parser error (issue 1530)
*   [#111](https://github.com/pmd/pmd/pull/111): \[java] Fix BooleanInstantiationRule for Java 8
*   [#112](https://github.com/pmd/pmd/pull/112): \[java] Fix ClassCastException on CloneMethodMustImplementCloneable
*   [#113](https://github.com/pmd/pmd/pull/113): \[java] Fix ClassCastException on SignatureDeclareThrowsException
*   [#114](https://github.com/pmd/pmd/pull/114): \[core] Remove multihreading workaround for JRE5, as no PMD version supports running on JRE5 anymore
*   [#115](https://github.com/pmd/pmd/pull/115): \[java] Simplify lambda parsing
*   [#116](https://github.com/pmd/pmd/pull/116): \[core] \[java] Improve collection usage
*   [#117](https://github.com/pmd/pmd/pull/117): \[java] Improve symboltable performance
*   [#118](https://github.com/pmd/pmd/pull/118): \[java] Simplify VariableDeclaratorId parsing

**Bugfixes:**

*   Java
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-logging-java
    *   [#1500](https://sourceforge.net/p/pmd/bugs/1500/) \[java] InvalidSlf4jMessageFormat: doesn't ignore exception param
    *   [#1509](https://sourceforge.net/p/pmd/bugs/1509/) \[java] InvalidSlf4jMessageFormat: NPE
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   General
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1517](https://sourceforge.net/p/pmd/bugs/1517/): \[java] CPD reports on Java constructors when using ignoreIdentifiers


**API Changes:**
