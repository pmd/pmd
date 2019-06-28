---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### PLSQL Grammar Updates

The grammar has been updated to support inline constraints in CREATE TABLE statements. Additionally, the
CREATE TABLE statement may now be followed by physical properties and table properties. However, these
properties are skipped over during parsing.

The CREATE VIEW statement now supports subquery views.

The EXTRACT function can now be parsed correctly. It is used to extract values from a specified
datetime field. Also date time literals are parsed now correctly.

The CASE expression can now be properly used within SELECT statements.

#### New Rules

*   The Java rule {% rule "java/bestpractices/DoubleBraceInitialization" %} (`java-bestpractices`)
    detects non static initializers in anonymous classes also known as "double brace initialization".
    This can be problematic, since a new class file is generated and object holds a strong reference
    to the surrounding class.

Table aliases are now supported when specifying columns in INSERT INTO clauses.

#### Modified Rules

*   The Java rule {% rule "java/bestpractices/UnusedPrivateField" %} (`java-bestpractices`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rule {% rule "java/design/SingularField" %} (`java-design`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rules {% rule "java/multithreading/UnsynchronizedStaticFormatter" %} and
    {% rule "java/multithreading/UnsynchronizedStaticDateFormatter" %} (`java-multithreading`)
    now prefer synchronized blocks by default. They will raise a violation, if the synchronization is implemented
    on the method level. To allow the old behavior, the new property `allowMethodLevelSynchronization` can
    be enabled.

*   The Java rule {% rule "java/design/UseUtilityClass" %} (`java-design`) has a new property `ignoredAnnotations`.
    By default, classes that are annotated with Lombok's `@UtilityClass` are ignored now.

*   The Java rule {% rule "java/errorprone/NonStaticInitializer" %} (`java-errorprone`) does not report
    non static initializers in anonymous classes anymore. For this use case, there is a new rule now:
    {% rule "java/bestpractices/DoubleBraceInitialization" %} (`java-bestpractices`).

### Fixed Issues

*   apex
    *   [#1664](https://github.com/pmd/pmd/issues/1664): \[apex] False positive ApexSharingViolationsRule, unsupported Apex feature
*   java
    *   [#1848](https://github.com/pmd/pmd/issues/1848): \[java] Local classes should preserve their modifiers
*   java-bestpractices
    *   [#1703](https://github.com/pmd/pmd/issues/1703): \[java] UnusedPrivateField on member annotated with lombok @Delegate
    *   [#1845](https://github.com/pmd/pmd/issues/1845): \[java] Regression in MethodReturnsInternalArray not handling enums
    *   [#1854](https://github.com/pmd/pmd/issues/1854): \[java] Rule to check for double brace initialisation
*   java-design
    *   [#1094](https://github.com/pmd/pmd/issues/1094): \[java] UseUtilityClass should be LombokAware
*   java-errorprone
    *   [#1853](https://github.com/pmd/pmd/issues/1853): \[java] False positive for NonStaticInitializer in anonymous class
*   java-multithreading
    *   [#1814](https://github.com/pmd/pmd/issues/1814): \[java] UnsynchronizedStaticFormatter documentation and implementation wrong
    *   [#1815](https://github.com/pmd/pmd/issues/1815): \[java] False negative in UnsynchronizedStaticFormatter
*   plsql
    *   [#1828](https://github.com/pmd/pmd/issues/1828): \[plsql] Parentheses stopped working
    *   [#1850](https://github.com/pmd/pmd/issues/1850): \[plsql] Parsing errors with INSERT using returning or records and TRIM expression
    *   [#1873](https://github.com/pmd/pmd/issues/1873): \[plsql] Expression list not working
    *   [#1878](https://github.com/pmd/pmd/issues/1878): \[pslql] ParseException when parsing USING

### API Changes

#### Deprecated APIs

> Reminder: Please don't use members marked with the annotation {% jdoc core::annotation.InternalApi %}, as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


##### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
  * In the meantime you should use interfaces like {% jdoc java::lang.java.ast.JavaNode %} or  {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package, to refer to nodes generically.
  * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at {% jdoc_package java::lang.java.ast %} to find out the full list
of deprecations.





### External Contributions

*   [#1792](https://github.com/pmd/pmd/pull/1792): \[java] Added lombok.experimental to AbstractLombokAwareRule - [jakivey32](https://github.com/jakivey32)
*   [#1808](https://github.com/pmd/pmd/pull/1808): \[plsql] Fix PL/SQL Syntax errors - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1829](https://github.com/pmd/pmd/pull/1829): \[java] Fix false negative in UnsynchronizedStaticFormatter - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1847](https://github.com/pmd/pmd/pull/1847): \[java] Regression in MethodReturnsInternalArray not handling enums - [Artem](https://github.com/KroArtem)
*   [#1863](https://github.com/pmd/pmd/pull/1863): \[plsql] Add Table InlineConstraint - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1864](https://github.com/pmd/pmd/pull/1864): \[plsql] Add support for Subquery Views - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1865](https://github.com/pmd/pmd/pull/1865): \[plsql] Add Support for Extract Expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1874](https://github.com/pmd/pmd/pull/1874): \[plsql] Add parenthesis equation support for Update - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1876](https://github.com/pmd/pmd/pull/1876): \[plsql] Datetime support for queries - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1883](https://github.com/pmd/pmd/pull/1883): \[plsql] Fix #1873 Expression list not working - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1884](https://github.com/pmd/pmd/pull/1884): \[plsql] fix #1878 Support explicit INNER word for INNER JOIN - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1885](https://github.com/pmd/pmd/pull/1885): \[plsql] Correct case expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1886](https://github.com/pmd/pmd/pull/1886): \[plsql] Support table alias for Insert Clause - [Hugo Araya Nash](https://github.com/kabroxiko)

{% endtocmaker %}

