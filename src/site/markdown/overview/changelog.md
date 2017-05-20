# PMD Release Notes

## 20-Mai-2017 - 5.7.0

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

#### Renderers

*   Added the 'empty' renderer which will write nothing.  Does not affect other behaviors, for example the command line PMD exit status
    will still indicate whether violations were found.

### Fixed Issues

*   General
    *   [#377](https://github.com/pmd/pmd/issues/377): \[core] Use maven wrapper and upgrade to maven 3.5.0
    *   [#376](https://github.com/pmd/pmd/issues/376): \[core] Improve build time on travis
*   java
    *   [#378](https://github.com/pmd/pmd/issues/378): \[java] Parser Error for empty statements
*   java-coupling
    *   [#1427](https://sourceforge.net/p/pmd/bugs/1427/): \[java] Law of Demeter violations for the Builder pattern
*   java-design
    *   [#345](https://github.com/pmd/pmd/issues/345): \[java] FieldDeclarationsShouldBeAtStartOfClass: Add ability to ignore interfaces
    *   [#389](https://github.com/pmd/pmd/issues/389): \[java] RuleSetCompatibility - not taking rename of UnusedModifier into account
*   java-junit
    *   [#358](https://github.com/pmd/pmd/issues/358): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule
*   java-strings
    *   [#334](https://github.com/pmd/pmd/issues/334): \[java] \[doc] Add suggestion to use StringUtils#isBlank for InefficientEmptyStringCheck
*   jsp-basic
    *   [#369](https://github.com/pmd/pmd/issues/369): \[jsp] Wrong issue "JSP file should use UTF-8 encoding"

### API Changes

*   The method `net.sourceforge.pmd.util.StringUtil#htmlEncode(String)` is deprecated.
    `org.apache.commons.lang3.StringEscapeUtils#escapeHtml4(String)` should be used instead.

### External Contributions

*   [#368](https://github.com/pmd/pmd/pull/368): \[vf] Adding proper AST support for negation expressions
*   [#372](https://github.com/pmd/pmd/pull/372): \[core] Fix XSS in HTML renderer
*   [#374](https://github.com/pmd/pmd/pull/374): \[java] Add property to ignore interfaces in FieldDeclarationsShouldBeAtStartOfClassRule
*   [#381](https://github.com/pmd/pmd/pull/381): \[core] Fix broken link in the site's doc
*   [#382](https://github.com/pmd/pmd/pull/382): \[java] Added documentation details on InefficientEmptyStringCheck
*   [#383](https://github.com/pmd/pmd/pull/383): \[jsp] Fixed JspEncoding false positive
*   [#390](https://github.com/pmd/pmd/pull/390): \[java] Remove trailing whitespaces in design.xml
*   [#391](https://github.com/pmd/pmd/pull/391): \[apex] Fix documentation typo
*   [#392](https://github.com/pmd/pmd/pull/392): \[java] False positive for Law Of Demeter (Builder pattern)
*   [#395](https://github.com/pmd/pmd/pull/395): \[java] Mockito verify method is not taken into account in JUnitTestsShouldIncludeAssert rule
