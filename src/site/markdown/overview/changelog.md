# PMD Release Notes

## ????? - 6.0.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.0.0.

This is a major release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    * [Removed Rules](#Removed_Rules)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Removed Rules

*   The deprecated rule `UseSingleton` has been removed from the ruleset `java-design`. The rule has been renamed
    long time ago to `UseUtilityClass`.

### Fixed Issues

*   java
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
*   java-controversial
    *   [#408](https://github.com/pmd/pmd/issues/408): \[java] DFA not analyzing asserts

### API Changes

*   The class `net.sourceforge.pmd.lang.dfa.NodeType` has been converted to an enum.
    All node types are enum members now instead of int constants. The names for node types are retained.

### External Contributions

*   [#420](https://github.com/pmd/pmd/pull/420): \[java] Fix UR anomaly in assert statements - [Clément Fournier](https://github.com/oowekyala)
*   [#484](https://github.com/pmd/pmd/pull/484): \[core] Changed linux usage to a more unix like path - [patriksevallius](https://github.com/patriksevallius)

