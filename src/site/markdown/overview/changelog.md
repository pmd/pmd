# Changelog

## ????? - 5.4.0-SNAPSHOT

**New Supported Languages:**

**Feature Request and Improvements:**

*   [#1344](https://sourceforge.net/p/pmd/bugs/1344/): AbstractNaming should check reverse
*   [#1361](https://sourceforge.net/p/pmd/bugs/1361/): ShortVariable and ShortMethodName configuration

**New Rules:**

*   New Rule: rulesets/java/design.xml/SingleMethodSingletonRule: Verifies that there is only one method called
    "getInstance". If there are more methods that return the singleton, than it can easily happen, that these
    are not the same instances - and thus no singleton.
*   New Rule: rulesets/java/design.xml/SingletonClassReturningNewInstance: Verifies that the method called
    "getInstance" returns a cached instance not always a fresh, new instance.
*   New Rule: rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion: Checks the usage of ${project.version} in
    Maven POM files. This rule can be found in the pmd-xml module.
*   New Rule: rulesets/pom/basic.xml/InvalidDependencyTypes: Verifies that only the default types (jar, war, ...)
    for dependencies are used. This rule can be found in the pmd-xml module.
*   New Rule: rulesets/java/comments.xml/CommentDefaultAccessModifier: In order to avoid mistakes with
    forgotten access modifiers for methods, this rule ensures, that you explicitly mark the usage of the
    default access modifier by placing a comment.
*   New Rule: rulesets/java/basic.xml/SimplifiedTernary: Ternary operator with a boolean literal
    can be simplified with a boolean expression.
*   New Rule: rulesets/java/unnecessary.xml/UselessQualifiedThis: Flags unnecessary qualified usages
    of this, when `this` alone would be unique. E.g. use just `this` instead of `Foo.this`.
*   New Rule: rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName: If a class implements cloneable
    the return type of the method clone() must be the class name.
*   New Rule: rulesets/java/clone.xml/CloneMethodMustBePublic: The java manual says "By convention,
    classes that implement the cloneable interface should override Object.clone (which is protected)
    with a public method."

**Modified/Deprecated Rules:**

*   Renamed Rule: Language Java, ruleset design.xml: The rule "UseSingleton" *has been renamed* to "UseUtilityClass".
    See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).
*   Modified Rule: rulesets/java/naming.xml/AbstractNaming: By default, this rule flags now classes,
    that are named "Abstract" but are not abstract. This behavior can be disabled by setting
    the new property "strict" to false.
*   Modified Rule: rulesets/java/naming.xml/ShortVariable: Additional property `minimum` to configure
    the minimum required length of a variable name.
*   Modified Rule: rulesets/java/naming.xml/ShortMethodName: Additional property `minimum` to configure
    the minimum required length of a method name.
*   Modified Rule: rulesets/java/junit.xml/UseAssertTrueInsteadOfAssertEquals: This rule also flags
    assertEquals, that use Boolean.TRUE/FALSE constants.
*   Modified Rule: rulesets/java/basic.xml/CheckResultSet: Do not require to check the result of a navigation
    method, if it is returned.


**Pull Requests:**

*   [#21](https://github.com/adangel/pmd/pull/21): Added PMD Rules for Singleton pattern violations.
*   [#54](https://github.com/pmd/pmd/pull/54): Add a new rulesets for Maven's POM rules
*   [#55](https://github.com/pmd/pmd/pull/55): Fix run.sh for paths with spaces
*   [#56](https://github.com/pmd/pmd/pull/56): Adding support for WSDL rules
*   [#57](https://github.com/pmd/pmd/pull/57): Add default access modifier as comment rule
*   [#58](https://github.com/pmd/pmd/pull/58): Add rule for unnecessary literal boolean in ternary operators
*   [#59](https://github.com/pmd/pmd/pull/59): Add check to Boxed booleans in UseAssertTrueInsteadOfAssertEquals rule
*   [#60](https://github.com/pmd/pmd/pull/60): Add UselessQualifiedThisRule
*   [#61](https://github.com/pmd/pmd/pull/61): Add CloneMethodReturnTypeMustMatchClassName rule
*   [#62](https://github.com/pmd/pmd/pull/62): Add CloneMethodMustBePublic rule
*   [#63](https://github.com/pmd/pmd/pull/63): Change CheckResultSet to allow for the result of the navigation methods to be returned
*   [#65](https://github.com/pmd/pmd/pull/65): Fix ClassCastException in UselessOverridingMethodRule.
*   [#66](https://github.com/pmd/pmd/pull/66): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#67](https://github.com/pmd/pmd/pull/67): Use Path instead of string to check file exclusions to fix windows-only bug
*   [#68](https://github.com/pmd/pmd/pull/68): #1370 ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#69](https://github.com/pmd/pmd/pull/69): #1371 InsufficientStringBufferDeclaration not detected properly on StringBuffer

**Bugfixes:**

*   [#1370](https://sourceforge.net/p/pmd/bugs/1370/): ConsecutiveAppendsShouldReuse not detected properly on StringBuffer
*   [#1371](https://sourceforge.net/p/pmd/bugs/1371/): InsufficientStringBufferDeclaration not detected properly on StringBuffer
*   [#1384](https://sourceforge.net/p/pmd/bugs/1384/): NullPointerException in ConsecutiveLiteralAppendsRule
*   [#1402](https://sourceforge.net/p/pmd/bugs/1402/): Windows-Only: File exclusions are not case insensitive

**API Changes:**
