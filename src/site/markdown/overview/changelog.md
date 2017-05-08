# PMD Release Notes

## ????? - 5.7.0-SNAPSHOT

The PMD team is pleased to announce PMD 5.7.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#New_and_noteworthy)
* [Fixed Issues](#Fixed_Issues)
* [API Changes](#API_Changes)
* [External Contributions](#External_Contributions)

### New and noteworthy

#### Modified Rules

*   The rule "FieldDeclarationsShouldBeAtStartOfClass" of the java-design ruleset has a new property `ignoreInterfaceDeclarations`.
    Setting this property to `true` ignores interface declarations, that precede fields.
    Example usage:


    <rule ref="rulesets/java/design.xml/FieldDeclarationsShouldBeAtStartOfClass">
        <properties>
            <property name="ignoreInterfaceDeclarations" value="true"/>
        </properties>
    </rule>

#### Removed Rules

*   The deprecated rule "UseSingleton" has been removed from the ruleset "java-design". The rule has been renamed
    long time ago to "UseUtilityClass".


### Fixed Issues

*   General
    *   [#377](https://github.com/pmd/pmd/issues/377): \[core] Use maven wrapper and upgrade to maven 3.5.0
*   java
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
*   java-design
    *   [#345](https://github.com/pmd/pmd/issues/345): \[java] FieldDeclarationsShouldBeAtStartOfClass: Add ability to ignore interfaces
    *   [#389](https://github.com/pmd/pmd/issues/389): \[java] RuleSetCompatibility - not taking rename of UnusedModifier into account

### API Changes

*   The method `net.sourceforge.pmd.util.StringUtil#htmlEncode(String)` is deprecated.
    `org.apache.commons.lang3.StringEscapeUtils#escapeHtml4(String)` should be used instead.

### External Contributions

*   [#368](https://github.com/pmd/pmd/pull/368): \[vf] Adding proper AST support for negation expressions
*   [#372](https://github.com/pmd/pmd/pull/372): \[core] Fix XSS in HTML renderer
*   [#374](https://github.com/pmd/pmd/pull/374): \[java] Add property to ignore interfaces in FieldDeclarationsShouldBeAtStartOfClassRule
*   [#381](https://github.com/pmd/pmd/pull/381): \[core] Fix broken link in the site's doc
