# PMD Release Notes

## ????? - 5.5.4-SNAPSHOT

The PMD team is pleased to announce PMD 5.5.4


### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)


### New and noteworthy


### Fixed Issues

*   java-design
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final

### API Changes


### External Contributions

*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields

