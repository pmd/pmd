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

#### New rules

*   The new Java rule {% rule "java/bestpractices/UseStandardCharsets" %} finds usages of `Charset.forName`,
    where `StandardCharsets` can be used instead.

    This rule is also part of the Quickstart Ruleset (`rulesets/java/quickstart.xml`) for Java.

*   The new Java rule {% rule "java/codestyle/UnnecessaryImport" %} replaces the rules
    {% rule "java/bestpractices/UnusedImports" %}, {% rule "java/codestyle/DuplicateImports" %},
    {% rule "java/errorprone/ImportFromSamePackage" %}, and {% rule "java/codestyle/DontImportJavaLang" %}.

    This rule is also part of the Quickstart Ruleset (`rulesets/java/quickstart.xml`) for Java.

#### Modified rules

*   The Apex rule {% rule "apex/security/ApexCRUDViolation" %} does not ignore getters anymore and also flags
    SOQL/SOSL/DML operations without access permission checks in getters. This will produce false positives now for
    VF getter methods, but we can't reliably detect, whether a getter is a VF getter or not. In such cases,
    the violation should be [suppressed](pmd_userdocs_suppressing_warnings.html).

#### Deprecated rules

*   java-bestpractices
    *   {% rule java/bestpractices/UnusedImports %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead

*   java-codestyle
    *   {% rule java/codestyle/DuplicateImports %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead
    *   {% rule java/codestyle/DontImportJavaLang %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead

*   java-errorprone
    *   {% rule java/errorprone/ImportFromSamePackage %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead

### Fixed Issues

*   apex-performance
    *   [#3198](https://github.com/pmd/pmd/pull/3198): \[apex] OperationWithLimitsInLoopRule: Support more limit consuming static method invocations
*   apex-security
    *   [#3202](https://github.com/pmd/pmd/issues/3202): \[apex] ApexCRUDViolationRule fails to report CRUD violation on COUNT() queries
    *   [#3210](https://github.com/pmd/pmd/issues/3210): \[apex] ApexCRUDViolationRule false-negative on non-VF getter
*   java-bestpractices
    *   [#3190](https://github.com/pmd/pmd/issues/3190): \[java] Use StandardCharsets instead of Charset.forName
    *   [#3224](https://github.com/pmd/pmd/issues/3224): \[java] UnusedAssignment crashes with nested records
*   java-codestyle
    *   [#3128](https://github.com/pmd/pmd/issues/3128): \[java] New rule UnnecessaryImport, deprecate DuplicateImports, ImportFromSamePackage, UnusedImports
*   java-errorprone
    *   [#2757](https://github.com/pmd/pmd/issues/2757): \[java] CloseResource: support Lombok's @Cleanup annotation
    *   [#3169](https://github.com/pmd/pmd/issues/3169): \[java] CheckSkipResult: NPE when using pattern bindings

### API Changes

### External Contributions

*   [#3193](https://github.com/pmd/pmd/pull/3193): \[java] New rule: UseStandardCharsets - [Andrea Aime](https://github.com/aaime)
*   [#3198](https://github.com/pmd/pmd/pull/3198): \[apex] OperationWithLimitsInLoopRule: Support more limit consuming static method invocations - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3211](https://github.com/pmd/pmd/pull/3211): \[apex] ApexCRUDViolationRule: Do not assume method is VF getter to avoid CRUD checks - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3234](https://github.com/pmd/pmd/pull/3234): \[apex] ApexCRUDViolation: COUNT is indeed CRUD checkable since it exposes data (false-negative) - [Jonathan Wiesel](https://github.com/jonathanwiesel)

{% endtocmaker %}

