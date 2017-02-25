# PMD Release Notes

## 25-Februar-2017 - 5.4.5

The PMD team is pleased to announce PMD 5.4.5

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    *   [Modified Rules](#Modified_Rules)
* [Fixed Issues](#Fixed_Issues)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

### Fixed Issues

*   general
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
    *   [#256](https://github.com/pmd/pmd/issues/256): \[core] shortnames option is broken with relative paths
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-coupling
    *   [#270](https://github.com/pmd/pmd/issues/270): \[java] LoD false positive
*   java-design
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration
    *   [#257](https://github.com/pmd/pmd/issues/257): \[java] UnusedLocalVariable false positive


### External Contributions

*   [#266](https://github.com/pmd/pmd/pull/266): \[java] corrected invalid reporting of LoD violation
