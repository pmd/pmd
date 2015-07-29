# Changelog

## ????? - 5.4.0-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1344](https://sourceforge.net/p/pmd/bugs/1344/): AbstractNaming should check reverse
*   [#1361](https://sourceforge.net/p/pmd/bugs/1361/): ShortVariable and ShortMethodName configuration

**New/Modified/Deprecated Rules:**

*   New Rule: rulesets/java/design.xml/SingleMethodSingletonRule: Verifies that there is only one method called
    "getInstance". If there are more methods that return the singleton, than it can easily happen, that these
    are not the same instances - and thus no singleton.
*   New Rule: rulesets/java/design.xml/SingletonClassReturningNewInstance: Verifies that the method called
    "getInstance" returns a cached instance not always a fresh, new instance.
*   Language Java, ruleset design.xml: The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
    See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).
*   New Rule: rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion: Checks the usage of ${project.version} in
    Maven POM files. This rule can be found in the pmd-xml module.
*   New Rule: rulesets/pom/basic.xml/InvalidDependencyTypes: Verifies that only the default types (jar, war, ...)
    for dependencies are used. This rule can be found in the pmd-xml module.
*   Modified Rule: rulesets/java/naming.xml/AbstractNaming: By default, this rule flags now classes,
    that are named "Abstract" but are not abstract. This behavior can be disabled by setting
    the new property "strict" to false.
*   Modified Rule: rulesets/java/naming.xml/ShortVariable: Additional property `minimum` to configure
    the minimum required length of a variable name.
*   Modified Rule: rulesets/java/naming.xml/ShortMethodName: Additional property `minimum` to configure
    the minimum required length of a method name.
*   New Rule: rulesets/java/comments.xml/CommentDefaultAccessModifier: In order to avoid mistakes with
    forgotten access modifiers for methods, this rule ensures, that you explicitly mark the usage of the
    default access modifier by placing a comment.
*   New Rule: rulesets/java/basic.xml/SimplifiedTernary: Ternary operator with a boolean literal
    can be simplified with a boolean expression.


**Pull Requests:**

*   [#21](https://github.com/adangel/pmd/pull/21): Added PMD Rules for Singleton pattern violations.
*   [#54](https://github.com/pmd/pmd/pull/54): Add a new rulesets for Maven's POM rules
*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces
*   [#56](https://github.com/pmd/pmd/pull/56): Adding support for WSDL rules
*   [#57](https://github.com/pmd/pmd/pull/57): Add default access modifier as comment rule
*   [#58](https://github.com/pmd/pmd/pull/58): Add rule for unnecessary literal boolean in ternary operators

**Bugfixes:**

**API Changes:**
