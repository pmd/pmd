---
title: Release Notes 5.4.0
tags: [release_notes]
keywords: release notes, announcements, what's new, new features
last_updated: December 4, 2015
summary: "Version 5.4.0 of the PMD Open Source Project, released October 4, 2015."
sidebar: pmd_sidebar
permalink: 2015-10-04-release-notes-5-4-0.html
---

## Note
    PMD 5.4.0 requires JDK 1.7 or above.


**Summary:**

*   9 new rules
*   4 features requests
*   18 pull requests

## Feature Request and Improvements

*   [#1344](https://sourceforge.net/p/pmd/bugs/1344/): AbstractNaming should check reverse
*   [#1361](https://sourceforge.net/p/pmd/bugs/1361/): ShortVariable and ShortMethodName configuration
*   [#1414](https://sourceforge.net/p/pmd/bugs/1414/): Command line parameter to disable “failOnViolation” behavior PMD and CPD Command Line Interfaces have a new optional parameter: <tt>failOnViolation</tt>. Executing PMD with the option <tt>-failOnViolation false</tt> will perform the PMD checks but won’t fail the build and still exit with status 0\. This is useful if you only want to generate the report with violations but don’t want to fail your build.
*   [#1420](https://sourceforge.net/p/pmd/bugs/1420/): UnusedPrivateField: Ignore fields if using lombok

## New Rules

*   Java:

    *   Basic: **SimplifiedTernary** (rulesets/java/basic.xml/SimplifiedTernary)  
        Ternary operator with a boolean literal can be simplified with a boolean expression.
    *   Clone: **CloneMethodMustBePublic** (rulesets/java/clone.xml/CloneMethodMustBePublic)  
        The java manual says “By convention, classes that implement the <tt>Cloneable</tt> interface should override <tt>Object.clone</tt> (which is protected) with a public method.”
    *   Clone: **CloneMethodReturnTypeMustMatchClassName** (rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName)  
        If a class implements <tt>Cloneable</tt> the return type of the method <tt>clone()</tt> must be the class name.
    *   Comments: **CommentDefaultAccessModifier** (rulesets/java/comments.xml/CommentDefaultAccessModifier)  
        In order to avoid mistakes with forgotten access modifiers for methods, this rule ensures, that you explicitly mark the usage of the default access modifier by placing a comment.
    *   Design: **SingletonClassReturningNewInstance** (rulesets/java/design.xml/SingletonClassReturningNewInstance)  
        Verifies that the method called <tt>getInstance</tt> returns a cached instance and not always a fresh, new instance.
    *   Design: **SingleMethodRule** (rulesets/java/design.xml/SingleMethodSingletonRule)  
        Verifies that there is only one method called <tt>getInstance</tt>. If there are more methods that return the singleton, then it can easily happen, that these are not the same instances - and thus no singleton.
    *   Unnecessary: **UselessQualifiedThis** (rulesets/java/unnecessary.xml/UselessQualifiedThis)  
        Flags unnecessary qualified usages of this, when <tt>this</tt> alone would be unique. E.g. use just <tt>this</tt> instead of <tt>Foo.this</tt>.
*   Maven POM: (The rules can be found in the _pmd-xml_ module)

    *   Basic: **ProjectVersionAsDependencyVersion** (rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion)  
        Checks the usage of <tt>${project.version}</tt> in Maven POM files.
    *   Basic: **InvalidDependencyTypes** (rulesets/pom/basic.xml/InvalidDependencyTypes)  
        Verifies that only the default types (jar, war, …) for dependencies are used.

		Ruleset snippet to activate the new rules:

			<pre class="prettyprint linenums"><rule ref="rulesets/java/basic.xml/SimplifiedTernary"/>
			<rule ref="rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName"/>
			<rule ref="rulesets/java/clone.xml/CloneMethodMustBePublic"/>
			<rule ref="rulesets/java/comments.xml/CommentDefaultAccessModifier"/>
			<rule ref="rulesets/java/design.xml/SingleMethodSingleton"/>
			<rule ref="rulesets/java/design.xml/SingletonClassReturningNewInstance"/>
			<rule ref="rulesets/java/unnecessary.xml/UselessQualifiedThis"/>
			<rule ref="rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion"/>
			<rule ref="rulesets/pom/basic.xml/InvalidDependencyTypes"/>

## Modified Rules

*   Java

    *   Basic: **CheckResultSet** (rulesets/java/basic.xml/CheckResultSet)  
        Do not require to check the result of a navigation method, if it is returned.
    *   JUnit: **UseAssertTrueInsteadOfAssertEquals** (rulesets/java/junit.xml/UseAssertTrueInsteadOfAssertEquals)  
        This rule also flags assertEquals, that use Boolean.TRUE/FALSE constants.
    *   Naming: **AbstractNaming** (rulesets/java/naming.xml/AbstractNaming)  
        By default, this rule flags now classes, that are named “Abstract” but are not abstract. This behavior can be disabled by setting the new property <tt>strict</tt> to false.
    *   Naming: **ShortMethodName** (rulesets/java/naming.xml/ShortMethodName)  
        Additional property <tt>minimum</tt> to configure the minimum required length of a method name.
    *   Naming: **ShortVariable** (rulesets/java/naming.xml/ShortVariable)  
        Additional property <tt>minimum</tt> to configure the minimum required length of a variable name.
    *   UnusedCode: **UnusedPrivateField** (rulesets/java/unusedcode.xml/UnusedPrivateField)  
        This rule won’t trigger anymore if [Lombok](https://projectlombok.org) is in use. See [#1420](https://sourceforge.net/p/pmd/bugs/1420/).

Renamed Rules

*   Java
    *   Design: **<s>UseSingleton</s>** - **UseUtilityClass** (rulesets/java/design.xml/UseUtilityClass)  
        The rule “UseSingleton” _has been renamed_ to “UseUtilityClass”. See also bugs [#1059](https://sourceforge.net/p/pmd/bugs/1059) and [#1339](https://sourceforge.net/p/pmd/bugs/1339/).

## Removed Rules

*   Java

    *   Basic: The following rules of ruleset “Basic” were marked as deprecated and are removed with this release now:  

        EmptyCatchBlock, EmptyIfStatement, EmptyWhileStmt, EmptyTryBlock, EmptyFinallyBlock, EmptySwitchStatements, EmptySynchronizedBlock, EmptyStatementNotInLoop, EmptyInitializer, EmptyStatementBlock, EmptyStaticInitializer  

        UnnecessaryConversionTemporary, UnnecessaryReturn, UnnecessaryFinalModifier, UselessOverridingMethod, UselessOperationOnImmutable, UnusedNullCheckInEquals, UselessParentheses  

        These rules are still available in the rulesets “Empty” (rulesets/java/empty.xml) and “Unnecessary” (rulesets/java/unnecessary.xml) respectively.
    *   Design: The rule “UncommentedEmptyMethod” has been renamed last release to “UncommentedEmptyMethodBody”. The old rule name reference has been removed with this release now.
    *   Controversial: The rule “BooleanInversion” has been deprecated last release and has been removed with this release completely.

## Pull Requests

*   [#21](https://github.com/adangel/pmd/pull/21): Added PMD Rules for Singleton pattern violations.
*   [#23](https://github.com/adangel/pmd/pull/23): Extended Objective-C grammar to accept Unicode characters in identifiers
*   [#54](https://github.com/pmd/pmd/pull/54): Add a new rulesets for Maven’s POM rules
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
*   [#70](https://github.com/pmd/pmd/pull/70): Fix code example

## Bugfixes

*   java-unusedcode/UnusedPrivateMethod:
    *   [#1412](https://sourceforge.net/p/pmd/bugs/1412/): UnusedPrivateMethod false positive: Issue #1403 not completely solved

## API Changes

*   pmd requires now JDK 1.7 or above.

*   pmd-core: <tt>net.sourceforge.pmd.lang.symboltable.Scope</tt>:

    The method <tt>addNameOccurrence</tt> returns now a Set of NameDeclarations to which the given occurrence has been added. This is useful in case there are ambiguous declarations of methods.

*   pmd-core: <tt>net.sourceforge.pmd.lang.symboltable.AbstractScope</tt>:

    The method <tt>findVariableHere</tt> returns now a Set of NameDeclarations which match the given occurrence. This is useful in case there are ambiguous declarations of methods.

{% include links.html %}
