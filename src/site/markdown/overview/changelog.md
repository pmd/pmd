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

### Fixed Issues

*   General
    *   [#407](https://github.com/pmd/pmd/issues/407): \[web] Release date is not properly formatted
*   java-design
    *   [#397](https://github.com/pmd/pmd/issues/397): \[java] ConstructorCallsOverridableMethodRule: false positive for method called from lambda expression
*   java-unnecessary
    *   [#421](https://github.com/pmd/pmd/issues/421): \[java] UnnecessaryFinalModifier final in private method

### API Changes

### External Contributions

*   [#406](https://github.com/pmd/pmd/pull/406): \[java] False positive with lambda in java-design/ConstructorCallsOverridableMethod
*   [#426](https://github.com/pmd/pmd/pull/426): \[java] UnnecessaryFinalModifier final in private method

