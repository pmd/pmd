# PMD Release Notes

## ????? - 5.8.1-SNAPSHOT

The PMD team is pleased to announce PMD 5.8.1.

This is a bug fixing release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

### Fixed Issues

*   java
    *   [#471](https://github.com/pmd/pmd/issues/471): \[java] Error while processing class when EnumMap is used in PMD 5.8.0
    *   [#477](https://github.com/pmd/pmd/issues/477): \[core] NoClassDefFoundError under 5.8
    *   [#478](https://github.com/pmd/pmd/issues/478): \[core] Processing issues dealing with anonymous classes

### API Changes

*   The `getGenericArgs()` method introduced to `TypeNode` in 5.8.0 was removed. You can access to generics' info through the `JavaTypeDefinition` object.
*   The `JavaTypeDefinitionBuilder` class introduced in 5.8.0 is not more. You can use factory methods available on `JavaTypeDefinition`

### External Contributions

*   [#472](https://github.com/pmd/pmd/pull/472): \[java] fix error with raw types, bug #471
