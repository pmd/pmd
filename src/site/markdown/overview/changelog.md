# PMD Release Notes

## ????? - 5.5.5-SNAPSHOT

The PMD team is pleased to announce PMD 5.5.5.


### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

### Fixed Issues

*   general:
    *   [#305](https://github.com/pmd/pmd/issues/305): \[core] PMD not executing under git bash
*   java-design
    *   [#274](https://github.com/pmd/pmd/issues/274): \[java] AccessorMethodGeneration: Method inside static inner class incorrectly reported
    *   [#275](https://github.com/pmd/pmd/issues/275): \[java] FinalFieldCouldBeStatic: Constant in @interface incorrectly reported as "could be made static"
    *   [#282](https://github.com/pmd/pmd/issues/282): \[java] UnnecessaryLocalBeforeReturn false positive when cloning Maps
    *   [#291](https://github.com/pmd/pmd/issues/291): \[java] Improve quality of AccessorClassGeneration
*   java-optimizations:
    *   [#222](https://github.com/pmd/pmd/issues/222): \[java] UseStringBufferForStringAppends: False Positive with ternary operator
*   java-strings:
    *   [#290](https://github.com/pmd/pmd/issues/290): \[java] InefficientEmptyStringCheck misses String.trim().isEmpty()

### API Changes

### External Contributions

*   [#280](https://github.com/pmd/pmd/pull/280): \[apex] Support for Aggregate Result in CRUD rules
*   [#289](https://github.com/pmd/pmd/pull/289): \[apex] Complex SOQL Crud check bug fixes
*   [#296](https://github.com/pmd/pmd/pull/296): \[apex] Adding String.IsNotBlank to the whitelist to prevent False positives
*   [#303](https://github.com/pmd/pmd/pull/303): \[java] InefficientEmptyStringCheckRule now reports String.trim().isEmpty() 
*   [#307](https://github.com/pmd/pmd/pull/307): \[java] Fix false positive with UseStringBufferForStringAppendsRule

