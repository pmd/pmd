


## 25-September-2026 - 7.28.0-SNAPSHOT

The PMD team is pleased to announce PMD 7.28.0-SNAPSHOT.

This is a minor release.

### Table Of Contents

* [🚀️ New and noteworthy](#new-and-noteworthy)
* [🌟️ New and Changed Rules](#new-and-changed-rules)
    * [New Rules](#new-rules)
    * [Changed Rules](#changed-rules)
* [🐛️ Fixed Issues](#fixed-issues)
* [🚨️ API Changes](#api-changes)
* [✨️ Merged pull requests](#merged-pull-requests)
* [📦️ Dependency updates](#dependency-updates)
* [📈️ Stats](#stats)

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule [`OnDemandImport`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_bestpractices.html#ondemandimport) reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new java rule  [`LongLiteralEndingWithLowercaseL`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_errorprone.html#longliteralendingwithlowercasel) finds long literals ending with l.
    That helps to avoid confusion between numbers ending with 1 and l. Capital L should be used to define long literals.
*   The new java rule  [`TypeNameMismatch`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_bestpractices.html#typenamemismatch) finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule [`CStyleArrayDeclaration`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_codestyle.html#cstylearraydeclaration) finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
#### Changed Rules
*   The property `checkNonStaticMethods` of the rule [`NonThreadSafeSingleton`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_multithreading.html#nonthreadsafesingleton) is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```
*   The property `statementOrderMatters` of the rule [`VariableCanBeInlined`](https://docs.pmd-code.org/pmd-doc-7.28.0-SNAPSHOT/pmd_rules_java_codestyle.html#variablecanbeinlined) is now deprecated.
    Setting it to false only risks false negatives, therefore, the property will be removed in PMD 8.0.0.

### 🐛️ Fixed Issues
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] False positive in UnusedAssignment when assignment is in conditional statement
* java-codestyle
    * [#3124](https://github.com/pmd/pmd/issues/3124): \[java] UnnecessaryLocalBeforeReturn/VariableCanBeInlined - remove property statementOrderMatters
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
* java-design
    * [#6513](https://github.com/pmd/pmd/issues/6513): \[java] SimplifyConditional: False negative when null check and instanceof are separated by other && conditions
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
* java-documentation
    * [#6450](https://github.com/pmd/pmd/issues/6450): \[java] DanglingJavadoc: False positive on /// comments for Java < 23
* java-errorprone
    * [#1050](https://github.com/pmd/pmd/issues/1050): \[java] NullAssignment: False positive inside if statement for first assignment
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable fires inconsistently between inline `throw new` and throw-via-local forms
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate is suppressed by using pattern variable
    * [#7068](https://github.com/pmd/pmd/issues/7068): \[java] UnusedReturnValue reports calls made on Mockito.verify(mock)
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])
    * [#7008](https://github.com/pmd/pmd/issues/7008): \[java] HardCodedCryptoKey: False positive when a default value of System.getProperty() is treated as a hard-coded key

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->


