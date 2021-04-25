---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


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
    {% deleted_rule "java/bestpractices/UnusedImports" %}, {% deleted_rule "java/codestyle/DuplicateImports" %},
    {% deleted_rule "java/errorprone/ImportFromSamePackage" deleted %}, and {% deleted_rule "java/codestyle/DontImportJavaLang" %}.

    This rule is also part of the Quickstart Ruleset (`rulesets/java/quickstart.xml`) for Java.

#### Deprecated rules

*   java-bestpractices
    *   {% deleted_rule java/bestpractices/UnusedImports %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead

*   java-codestyle
    *   {% deleted_rule java/codestyle/DuplicateImports %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead
    *   {% deleted_rule java/codestyle/DontImportJavaLang %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead

*   java-errorprone
    *   {% deleted_rule java/errorprone/ImportFromSamePackage %}: use the rule {% rule "java/codestyle/UnnecessaryImport" %} instead


### Fixed Issues

*   apex-performance
    *   [#3198](https://github.com/pmd/pmd/pull/3198): \[apex] OperationWithLimitsInLoopRule: Support more limit consuming static method invocations
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

{% endtocmaker %}

