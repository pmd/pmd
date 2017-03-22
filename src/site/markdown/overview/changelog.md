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

*   java-design:
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### API Changes

### External Contributions

*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty() 
