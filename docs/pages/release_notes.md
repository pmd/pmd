---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### Modified rules

*   The Java rule {% rule "java/errorprone/CompareObjectsWithEquals" %} has now a new property
    `typesThatCompareByReference`. With that property, you can configure types, that should be whitelisted
    for comparison by reference. By default, `java.lang.Enum` and `java.lang.Class` are allowed, but
    you could add custom types here.
    Additionally comparisons against constants are allowed now. This makes the rule less noisy when two constants
    are compared. Constants are identified by looking for an all-caps identifier.

#### Deprecated rules

*   The Java rule {% rule "java/errorprone/CloneThrowsCloneNotSupportedException" %} has been deprecated without
    replacement.
    
    The rule has no real value as `CloneNotSupportedException` is a
    checked exception and therefore you need to deal with it while implementing the `clone()` method. You either
    need to declare the exception or catch it. If you catch it, then subclasses can't throw it themselves explicitly.
    However, `Object.clone()` will still throw this exception if the `Cloneable` interface is not implemented.
    
    Note, this rule has also been removed from the Quickstart Ruleset (`rulesets/java/quickstart.xml`).

### Fixed Issues

*   apex
    *   [#3183](https://github.com/pmd/pmd/issues/3183): \[apex] ApexUnitTestMethodShouldHaveIsTestAnnotation false positive with helper method
    *   [#3243](https://github.com/pmd/pmd/pull/3243): \[apex] Correct findBoundary when traversing AST
*   core
    *   [#2639](https://github.com/pmd/pmd/issues/2639): \[core] PMD CLI output file is not created if directory or directories in path don't exist
*   doc
    *   [#3230](https://github.com/pmd/pmd/issues/3230): \[doc] Remove "Edit me" button for language index pages
*   dist
    *   [#2466](https://github.com/pmd/pmd/issues/2466): \[dist] Distribution archive doesn't include all batch scripts
*   java
    *   [#3269](https://github.com/pmd/pmd/pull/3269): \[java] Fix NPE in MethodTypeResolution
*   java-bestpractices
    *   [#1175](https://github.com/pmd/pmd/issues/1175): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource
    *   [#2219](https://github.com/pmd/pmd/issues/2219): \[java] Document Reasons to Avoid Reassigning Parameters
    *   [#2737](https://github.com/pmd/pmd/issues/2737): \[java] Fix misleading rule message on rule SwitchStmtsShouldHaveDefault with non-exhaustive enum switch
    *   [#3236](https://github.com/pmd/pmd/issues/3236): \[java] LiteralsFirstInComparisons should consider constant fields (cont'd)
    *   [#3254](https://github.com/pmd/pmd/issues/3254): \[java] AvoidReassigningParameters reports violations on wrong line numbers
*   java-codestyle
    *   [#2655](https://github.com/pmd/pmd/issues/2655): \[java] UnnecessaryImport false positive for on-demand imports
    *   [#3262](https://github.com/pmd/pmd/pull/3262): \[java] FieldDeclarationsShouldBeAtStartOfClass: false negative with anon classes
    *   [#3265](https://github.com/pmd/pmd/pull/3265): \[java] MethodArgumentCouldBeFinal: false negatives with interfaces and inner classes
    *   [#3266](https://github.com/pmd/pmd/pull/3266): \[java] LocalVariableCouldBeFinal: false negatives with interfaces, anon classes
    *   [#3274](https://github.com/pmd/pmd/pull/3274): \[java] OnlyOneReturn: false negative with anonymous class
    *   [#3275](https://github.com/pmd/pmd/pull/3275): \[java] UnnecessaryLocalBeforeReturn: false negatives with lambda and anon class
*   java-design
    *   [#2780](https://github.com/pmd/pmd/issues/2780): \[java] DataClass example from documentation results in false-negative
*   java-errorprone
    *   [#3110](https://github.com/pmd/pmd/issues/3110): \[java] Enhance CompareObjectsWithEquals with list of exceptions
    *   [#3112](https://github.com/pmd/pmd/issues/3112): \[java] Deprecate rule CloneThrowsCloneNotSupportedException
    *   [#3205](https://github.com/pmd/pmd/issues/3205): \[java] Make CompareObjectWithEquals allow comparing against constants
    *   [#3248](https://github.com/pmd/pmd/issues/3248): \[java] Documentation is wrong for SingletonClassReturningNewInstance rule
    *   [#3249](https://github.com/pmd/pmd/pull/3249): \[java] AvoidFieldNameMatchingTypeName: False negative with interfaces
    *   [#3268](https://github.com/pmd/pmd/pull/3268): \[java] ConstructorCallsOverridableMethod: IndexOutOfBoundsException with annotations

### API Changes

### External Contributions
*   [#3272](https://github.com/pmd/pmd/pull/3272): \[apex] correction for ApexUnitTestMethodShouldHaveIsTestAnnotation false positives - [William Brockhus](https://github.com/YodaDaCoda)

{% endtocmaker %}

