# PMD Release Notes

## ????? - 6.0.0-SNAPSHOT

The PMD team is pleased to announce PMD 6.0.0.

This is a major release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
    *   [Revamped Apex CPD](#Revamped_Apex_CPD)
    *   [Java Type Resolution](#Java_Type_Resolution)
    *   [Modified Rules](#Modified_Rules)
    *   [Removed Rules](#Removed_Rules)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Revamped Apex CPD

We are now using the Apex jorje lexer to tokenize Apex code for CPD. This change means:

    *   All comments are now ignored for CPD. This is consistent with how other languages such as Java and Groovy work.
    *   Tokenization honors the language specification, which improves accurancy.

CPD will therefore have less false positives and false negatives.

#### Java Type Resolution

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph)'s work on type resolution for Java continues.
For this release he has extended support for method calls.

Method shadowing and overloading are supported, as is varargs management. However, the selection of the target method upon the presence
of generics and type inference is still work in progress. Expect it in forecoming releases.

#### Modified Rules

*   The rule `UnnecessaryFinalModifier` (ruleset `java-unnecessarycode`) has been revamped to detect more cases.
    It will now flag anonymous class' methods marked as final (can't be overriden, so it's pointless), along with
    final methods overriden / defined within enum instances.

#### Removed Rules

*   The deprecated rule `UseSingleton` has been removed from the ruleset `java-design`. The rule has been renamed
    long time ago to `UseUtilityClass`.

### Fixed Issues

*   apex
    *   [#488](https://github.com/pmd/pmd/pull/488): \[apex] Use Apex lexer for CPD
*   cpp
    *   [#448](https://github.com/pmd/pmd/issues/448): \[cpp] Write custom CharStream to handle continuation characters
*   java
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
*   java-controversial
    *   [#408](https://github.com/pmd/pmd/issues/408): \[java] DFA not analyzing asserts
*   java-unnecessarycode
    *   [#412](https://github.com/pmd/pmd/issues/412): \[java] java-unnecessarycode/UnnecessaryFinalModifier missing cases

### API Changes

*   The class `net.sourceforge.pmd.lang.dfa.NodeType` has been converted to an enum.
    All node types are enum members now instead of int constants. The names for node types are retained.

### External Contributions

*   [#420](https://github.com/pmd/pmd/pull/420): \[java] Fix UR anomaly in assert statements - [Clément Fournier](https://github.com/oowekyala)
*   [#484](https://github.com/pmd/pmd/pull/484): \[core] Changed linux usage to a more unix like path - [patriksevallius](https://github.com/patriksevallius)
*   [#486](https://github.com/pmd/pmd/pull/486): \[java] Add basic method typeresolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#492](https://github.com/pmd/pmd/pull/492): \[java] Typeresolution for overloaded methods - [Bendegúz Nagy](https://github.com/WinterGrascph)

