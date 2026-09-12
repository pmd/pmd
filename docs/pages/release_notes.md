---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/OnDemandImport %} reports on-demand imports, also known as wildcard imports.
    By default, static imports from JUnit and TestNG are allowed. The allowed static and type import packages
    can be configured with `allowStaticImportsFrom` and `allowTypeImportsFrom`.
*   The new java rule  {% rule java/errorprone/LongLiteralEndingWithLowercaseL %} finds long literals ending with l.
    That helps to avoid confusion between numbers ending with 1 and l. Capital L should be used to define long literals.
*   The new java rule  {% rule java/bestpractices/TypeNameMismatch %} finds types that are not defined in a .java file
    with the same name. Enforcing a match between source file name and type name makes it easier to
    find source code for given type.
*   The new Java rule {% rule java/codestyle/CStyleArrayDeclaration %} finds C-style declarations of arrays (e.g. `int numbers[]`).
    That helps you use Java-style declarations (e.g. `int[] numbers`) consistently throughout the codebase.
#### Changed Rules
*   The Java rule {% rule java/design/FinalFieldCouldBeStatic %} no longer reports casts or conditional expressions
    whose constant classification previously depended on a boxed static final field. Direct static field references
    are still reported.
*   The Java rule {% rule java/errorprone/UnconditionalIfStatement %} now excludes final local boolean constants,
    consistently with its existing exclusion of named compile-time constants used for conditional compilation.
*   The Java rule {% rule java/errorprone/UnusedNullCheckInEquals %} now recognizes final local String constants
    and unqualified instance String constants as non-null receivers, avoiding unnecessary reports.
*   The Java rule {% rule java/bestpractices/AvoidReassigningLoopVariables %}, with `forReassign=skip`, now accepts
    a final local constant equal to one as the increment of a conditional skip.
*   The Java rule {% rule java/bestpractices/UnusedAssignment %} now recognizes final local boolean constants
    when analyzing short-circuit conditions, avoiding false positives caused by assignments that cannot execute.
*   The Java rule {% rule java/bestpractices/LiteralsFirstInComparisons %} now recognizes final local String constants
    and unqualified references to non-static final String constants. This may add violations when such a constant
    is the argument of a comparison, or remove them when it is already the receiver.
*   The property `checkNonStaticMethods` of the rule {% rule java/multithreading/NonThreadSafeSingleton %} is now
    deprecated and no longer has any effect. Its implementation did the opposite of what the documentation described.
    The rule now always reports both static and non-static methods; previously it reported only static methods
    by default.  
    This may result in additional violations being reported.  
    If you want to suppress violations for non-static methods, you can use
    [suppression via XPath]({{ baseurl }}pmd_userdocs_suppressing_warnings.html#the-property-violationsuppressxpath), e.g.
    ```xml
    <property name="violationSuppressXPath" value=".[ancestor-or-self::MethodDeclaration[1][@Static = false()]]" />
    ```

### 🐛️ Fixed Issues
* html
    * [#6135](https://github.com/pmd/pmd/issues/6135): \[html] HtmlCpdLexer giving IndexOutOfBoundsException when script contains unescaped closing tag
* java
    * [#6926](https://github.com/pmd/pmd/issues/6926): \[java] IllegalArgumentException (Mismatched list sizes) with inconsistent unresolved generic arity
    * [#7060](https://github.com/pmd/pmd/issues/7060): \[java] getConstValue() returns null for constant expressions referencing final local variables
* java-bestpractices
    * [#5940](https://github.com/pmd/pmd/issues/5940): \[java] False positive in UnusedAssignment when assignment is in conditional statement
* java-codestyle
    * [#5732](https://github.com/pmd/pmd/issues/5732): \[java] UnnecessaryCast false positive with package private methods
* java-design
    * [#6513](https://github.com/pmd/pmd/issues/6513): \[java] SimplifyConditional: False negative when null check and instanceof are separated by other && conditions
    * [#6694](https://github.com/pmd/pmd/issues/6694): \[java] SimplifyBooleanReturns triggers inconsistently depending on redundant parentheses in return expression
* java-documentation
    * [#6450](https://github.com/pmd/pmd/issues/6450): \[java] DanglingJavadoc: False positive on /// comments for Java < 23
* java-errorprone
    * [#6693](https://github.com/pmd/pmd/issues/6693): \[java] CloneMethodMustImplementCloneable fires inconsistently between inline `throw new` and throw-via-local forms
    * [#7009](https://github.com/pmd/pmd/issues/7009): \[java] ReplaceJavaUtilDate is suppressed by using pattern variable
* java-multithreading
    * [#6297](https://github.com/pmd/pmd/issues/6297): \[java] AvoidUsingVolatile: Update documentation
    * [#6780](https://github.com/pmd/pmd/issues/6780): \[java] NonThreadSafeSingleton: False negative with property checkNonStaticMethods
* java-security
    * [#7007](https://github.com/pmd/pmd/issues/7007): \[java] HardCodedCryptoKey: False negative when a hard-coded key is constructed via new String(char[])

### 🚨️ API Changes

*   Java constant folding now recognizes final primitive and String variables initialized with constant expressions,
    including local variables and unqualified instance fields. Numeric references are converted to their declared type.
    Boxed fields and field accesses qualified by expressions (such as `this.CONSTANT`) are not compile-time constants.
    These changes affect `ASTExpression.getConstValue()`, `isCompileTimeConstant()`, and the XPath attribute
    `@CompileTimeConstant`; custom Java and XPath rules relying on them may report different results.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
