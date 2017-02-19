# PMD Release Notes

## ????? - 5.5.4-SNAPSHOT

The PMD team is pleased to announce PMD 5.5.4


### Table Of Contents

*   [New and noteworthy](#New_and_noteworthy)
    *   [New Rules](#New_Rules)
    *   [Modified Rules](#Modified_Rules)
*   [Fixed Issues](#Fixed_Issues)
*   [API Changes](#API_Changes)
*   [External Contributions](#External_Contributions)


### New and noteworthy

#### New Rules

##### AccessorMethodGeneration (java-design)

When accessing a private field / method from another class, the Java compiler will generate a accessor methods
with package-private visibility. This adds overhead, and to the dex method count on Android. This situation can
be avoided by changing the visibility of the field / method from private to package-private.

For instance, it would report violations on code such as:

```
public class OuterClass {
    private int counter;
    /* package */ int id;

    public class InnerClass {
        InnerClass() {
            OuterClass.this.counter++; // wrong, accessor method will be generated
        }

        public int getOuterClassId() {
            return OuterClass.this.id; // id is package-private, no accessor method needed
        }
    }
}
```

This new rule is part of the `java-design` ruleset.

#### Modified Rules

*   The Java rule `UnusedModifier` (ruleset java-unusedcode) has been expanded to consider more redundant modifiers.
    *   Annotations marked as `abstract`.
    *   Nested annotations marked as `static`.
    *   Nested annotations within another interface or annotation marked as `public`.
    *   Classes, interfaces or annotations nested within an annotation marked as `public` or `static`.
    *   Nested enums marked as `static`.

*   The Java rule `UnnecessaryLocalBeforeReturn` (ruleset java-design) no longer requires the variable declaration
    and return statement to be on consecutive lines. Any variable that is used solely in a return statement will be
    reported.

### Fixed Issues

*   General
    *   [#234](https://github.com/pmd/pmd/issues/234): \[core] Zip file stream closes spuriously when loading rulesets
*   apex-complexity
    *   [#251](https://github.com/pmd/pmd/issues/251): \[apex] NCSS Type length is incorrect when using method chaining
*   java-basic
    *   [#232](https://github.com/pmd/pmd/issues/232): \[java] SimplifiedTernary: Incorrect ternary operation can be simplified.
*   java-design
    *   [#933](https://sourceforge.net/p/pmd/bugs/933/): \[java] UnnecessaryLocalBeforeReturn false positive for SuppressWarnings annotation
    *   [#1496](https://sourceforge.net/p/pmd/bugs/1496/): \[java] New Rule: AccesorMethodGeneration - complements accessor class rule
    *   [#216](https://github.com/pmd/pmd/issues/216): \[java] \[doc] NonThreadSafeSingleton: Be more explicit as to why double checked locking is not recommended
    *   [#219](https://github.com/pmd/pmd/issues/219): \[java] UnnecessaryLocalBeforeReturn: ClassCastException in switch case with local variable returned
    *   [#240](https://github.com/pmd/pmd/issues/240): \[java] UnnecessaryLocalBeforeReturn: Enhance by checking usages
*   java-optimizations
    *   [#215](https://github.com/pmd/pmd/issues/215): \[java] RedundantFieldInitializer report for annotation field not explicitly marked as final
*   java-unusedcode
    *   [#246](https://github.com/pmd/pmd/issues/246): \[java] UnusedModifier doesn't check annotations
    *   [#247](https://github.com/pmd/pmd/issues/247): \[java] UnusedModifier doesn't check annotations inner classes
    *   [#248](https://github.com/pmd/pmd/issues/248): \[java] UnusedModifier doesn't check static keyword on nested enum declaration

### API Changes


### External Contributions

*   [#227](https://github.com/pmd/pmd/pull/227): \[apex] Improving detection of getters
*   [#228](https://github.com/pmd/pmd/pull/228): \[apex] Excluding count from CRUD/FLS checks
*   [#229](https://github.com/pmd/pmd/pull/229): \[apex] Dynamic SOQL is safe against Integer, Boolean, Double
*   [#231](https://github.com/pmd/pmd/pull/231): \[apex] CRUD/FLS rule - add support for fields

