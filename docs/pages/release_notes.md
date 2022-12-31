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

#### Modified rules

* The Java rule {% rule java/bestpractices/UnusedPrivateField %} has a new property `reportForAnnotations`.
  This is a list of fully qualified names of the annotation types that should be reported anyway. If an unused field
  has any of these annotations, then it is reported. If it has any other annotation, then it is still considered 
  to be used and is not reported.

#### Deprecated rules

* The Java rules {% rule java/design/ExcessiveClassLength %} and {% rule java/design/ExcessiveMethodLength %}
  have been deprecated. The rule {% rule java/design/NcssCount %} can be used instead.
  The deprecated rules will be removed with PMD 7.0.0.

* The Java rule {% rule java/errorprone/EmptyStatementNotInLoop %} is deprecated.
  Use the rule {% rule java/codestyle/UnnecessarySemicolon %} instead.
  Note: Actually it was announced to be deprecated since 6.46.0 but the rule was not marked as deprecated yet.
  This has been done now.

### Fixed Issues
* core
    * [#4248](https://github.com/pmd/pmd/issues/4248): \[core] Can't analyze sources in zip files
* apex-security
    * [#4146](https://github.com/pmd/pmd/issues/4146): \[apex] ApexCRUDViolation: Recognize User Mode in SOQL + DML
* java-bestpractices
    * [#4166](https://github.com/pmd/pmd/issues/4166): \[java] UnusedPrivateField doesn't find annotated unused private fields anymore
    * [#4250](https://github.com/pmd/pmd/issues/4250): \[java] WhileLoopWithLiteralBoolean - false negative with complex expressions still occurs in PMD 6.52.0
* java-design
    * [#2127](https://github.com/pmd/pmd/issues/2127): \[java] Deprecate rules ExcessiveClassLength and ExcessiveMethodLength
* java-errorprone
    * [#4164](https://github.com/pmd/pmd/issues/4164): \[java]\[doc] AvoidAssertAsIdentifier and AvoidEnumAsIdentifier - clarify use case
* java-multithreading
    * [#4210](https://github.com/pmd/pmd/issues/4210): \[java] DoNotUseThreads report duplicate warnings

### API Changes

#### Deprecated APIs

##### For removal

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* {% jdoc java::lang.java.rule.design.ExcessiveLengthRule %} (Java)

### External Contributions
* [#4244](https://github.com/pmd/pmd/pull/4244): \[apex] ApexCRUDViolation: user mode and system mode with test cases added - [Tarush Singh](https://github.com/Tarush-Singh35) (@Tarush-Singh35)

{% endtocmaker %}
