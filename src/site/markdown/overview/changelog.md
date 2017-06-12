# PMD Release Notes

## ????? - 5.8.0-SNAPSHOT

The PMD team is pleased to announce PMD 5.8.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    *   [Modified Rules](#Modified_Rules)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule `UnnecessaryFinalModifier` (ruleset java-unnecessary) now also reports on private methods marked as `final`.
    Being private, such methods can't be overriden, and therefore, the final keyword is redundant.

*   The Java rule `PreserveStackTrace` (ruleset java-design) has been relaxed to support the builder pattern on thrown exception.
    This change may introduce some false positives if using the exception in non-orthodox ways for things other than setting the
    root cause of the exception. Contact us if you find any such scenarios.

*   The ruleset java-junit now properly detects JUnit5, and rules are being adapted to the changes on it's API.
    This support is, however, still incomplete. Let us know of any uses we are still missing on the [issue tracker](https://github.com/pmd/pmd/issues)

### Fixed Issues

*   General
    *   [#407](https://github.com/pmd/pmd/issues/407): \[web] Release date is not properly formatted
    *   [#429](https://github.com/pmd/pmd/issues/429): \[core] Error when running PMD from folder with space
*   java
    *   [#414](https://github.com/pmd/pmd/issues/414): \[java] Java 8 parsing problem with annotations for wildcards
    *   [#415](https://github.com/pmd/pmd/issues/415): \[java] Parsing Error when having an Annotated Inner class
    *   [#417](https://github.com/pmd/pmd/issues/417): \[java] Parsing Problem with Annotation for Array Member Types
*   java-design
    *   [#397](https://github.com/pmd/pmd/issues/397): \[java] ConstructorCallsOverridableMethodRule: false positive for method called from lambda expression
    *   [#410](https://github.com/pmd/pmd/issues/410): \[java] ImmutableField: False positive with lombok
    *   [#422](https://github.com/pmd/pmd/issues/422): \[java] PreserveStackTraceRule: false positive when using builder pattern
*   java-junit
    *   [#428](https://github.com/pmd/pmd/issues/428): \[java] PMD requires public modifier on JUnit 5 test
*   java-unnecessary
    *   [#421](https://github.com/pmd/pmd/issues/421): \[java] UnnecessaryFinalModifier final in private method

### API Changes

### External Contributions

*   [#406](https://github.com/pmd/pmd/pull/406): \[java] False positive with lambda in java-design/ConstructorCallsOverridableMethod
*   [#409](https://github.com/pmd/pmd/pull/409): \[java] Groundwork for the upcoming metrics framework
*   [#416](https://github.com/pmd/pmd/pull/416): \[java] FIXED: Java 8 parsing problem with annotations for wildcards
*   [#418](https://github.com/pmd/pmd/pull/418): \[java] Type resolution: super and this keywords
*   [#425](https://github.com/pmd/pmd/pull/425): \[java] False positive with builder pattern in java-design/PreserveStackTrace
*   [#426](https://github.com/pmd/pmd/pull/426): \[java] UnnecessaryFinalModifier final in private method

