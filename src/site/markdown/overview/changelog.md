# PMD Release Notes

## ????? - 5.4.6-SNAPSHOT

The PMD team is pleased to announce PMD 5.4.6.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java-design:
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### API Changes

### External Contributions

*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty() 
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule

