# Changelog

## 05-November-2016 - 5.5.2

**Summary:**

*   1 new language for CPD: Groovy
*   1 new rule: plsql-strictsyntax/MisplacedPragma
*   12 pull requests
*   17 bug fixes

**New Supported Languages:**

*   CPD now supports Groovy. See [PR#107](https://github.com/pmd/pmd/pull/107).

**Feature Requests and Improvements:**

*   plsql
    *   [#1539](https://sourceforge.net/p/pmd/bugs/1539/): \[plsql] Create new rule for strict syntax checking: MisplacedPragma

**New Rules:**

*   New Rules for plsql
    *   plsql-strictsyntax: MisplacedPragma

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
*   [#119](https://github.com/pmd/pmd/pull/119): \[plsql] Fix PMD issue 1531- endless loop followed by OOM while parsing (PL)SQL

**Bugfixes:**

*   apex-apexunit
    *   [#1521](https://sourceforge.net/p/pmd/bugs/1521/): \[apex] ApexUnitTestClassShouldHaveAsserts: Parsing error on APEX class: expected one element but was: <BlockStatement, BlockStatement>
*   Java
    *   [#1530](https://sourceforge.net/p/pmd/bugs/1530/): \[java] Parser exception on Java code
    *   [#1490](https://sourceforge.net/p/pmd/bugs/1490/): \[java] PMD Error while processing - NullPointerException
*   java-basic/BooleanInstantiation
    *   [#1533](https://sourceforge.net/p/pmd/bugs/1533/): \[java] BooleanInstantiation: ClassCastException with Annotation
*   java-comments
    *   [#1522](https://sourceforge.net/p/pmd/bugs/1522/): \[java] CommentRequired: false positive
*   java-design/SingularField
    *   [#1494](https://sourceforge.net/p/pmd/bugs/1494/): \[java] SingularField: lombok.Data false positive
*   java-imports/UnusedImports
    *   [#1529](https://sourceforge.net/p/pmd/bugs/1529/): \[java] UnusedImports: The created rule violation has no class name
*   java-logging-java
    *   [#1500](https://sourceforge.net/p/pmd/bugs/1500/) \[java] InvalidSlf4jMessageFormat: doesn't ignore exception param
    *   [#1509](https://sourceforge.net/p/pmd/bugs/1509/) \[java] InvalidSlf4jMessageFormat: NPE
*   java-typeresolution/CloneMethodMustImplementCloneable
    *   [#1532](https://sourceforge.net/p/pmd/bugs/1532/): \[java] CloneMethodMustImplementCloneable: Implemented Interface extends Cloneable
    *   [#1534](https://sourceforge.net/p/pmd/bugs/1534/): \[java] CloneMethodMustImplementCloneable: ClassCastException with Annotation (java8)
*   java-typeresolution/SignatureDeclareThrowsException
    *   [#1535](https://sourceforge.net/p/pmd/bugs/1535/): \[java] SignatureDeclareThrowsException: ClassCastException with Annotation
*   PLSQL
    *   [#1520](https://sourceforge.net/p/pmd/bugs/1520/): \[plsql] Missing PL/SQL language constructs in parser: Is Of Type, Using
    *   [#1527](https://sourceforge.net/p/pmd/bugs/1527/): \[plsql] PRAGMA AUTONOMOUS_TRANSACTION gives processing errors
    *   [#1531](https://sourceforge.net/p/pmd/bugs/1531/): \[plsql] OOM/Endless loop while parsing (PL)SQL
*   General
    *   [#1506](https://sourceforge.net/p/pmd/bugs/1506/): \[core] When runing any RuleTst, start/end methods not called
    *   [#1517](https://sourceforge.net/p/pmd/bugs/1517/): \[java] CPD reports on Java constructors when using ignoreIdentifiers
