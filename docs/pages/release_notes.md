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
*   The new Java rule {% rule java/codestyle/LocalVariableDeclarationShouldBeAtStartOfBlock %} enforces a code style
    where all local variables are declared together at the start of their block, without initialization.
    Optionally, the declarations can be required to be sorted by name or by type and name
    (see properties `sortBy` and `caseSensitiveSorting`).
*   The new Kotlin rule {% rule kotlin/bestpractices/LooseCoupling %} reports declarations (properties,
    parameters, function return types) using concrete collection/map implementation types (e.g. `ArrayList`,
    `HashMap`) instead of the corresponding interface (e.g. `List`, `Map`). It is powered by the
    `pmd-kotlin:typeIs()` XPath function introduced in the previous release.

#### Changed Rules
*   The new Java rule {% rule java/bestpractices/ControlFlowInTest %}
    reports control flow in test methods that can obscure which assertions
    are executed. It checks conditionals, switch statements and expressions,
    explicit throw statements, and try statements with catch clauses.
    Loops are allowed by default and can be reported by setting
    `allowLoops` to `false`.
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
*   The Java rule {% rule java/codestyle/UseExplicitTypes %} has a new property `allowLongTypeNames`. It allows
    to use `var` when the explicit type name would be longer than a given minimum length configured with 
    this property.

### 🐛️ Fixed Issues
* apex-performance
    * [#5082](https://github.com/pmd/pmd/issues/5082): \[apex] EagerlyLoadedDescribeSObjectResult: false positive with fully qualified SObjectDescribeOptions/FieldDescribeOptions
* core
    * [#6912](https://github.com/pmd/pmd/issues/6912): \[core] Include XML validation details in ruleset loading errors
    * [#7072](https://github.com/pmd/pmd/issues/7072): \[core] Reuse compiled violationSuppressXPath queries
    * [#7156](https://github.com/pmd/pmd/issues/7156): \[core] Analysis cache fails with ZipException on invalid archive on the auxclasspath
* groovy
    * [#7110](https://github.com/pmd/pmd/issues/7110): \[groovy] Fix #7100: CPD fails on GStrings ending in an interpolated variable
* html
    * [#5313](https://github.com/pmd/pmd/issues/5313): \[html] CPD gets end line wrong for HTML
    * [#7166](https://github.com/pmd/pmd/issues/7166): \[html] endcolumn of nodes is off-by-one
* java
    * [#7048](https://github.com/pmd/pmd/issues/7048): \[java] Deprecate getImage()/@<!-- -->Image and remove usages
    * [#7054](https://github.com/pmd/pmd/issues/7054): \[java] IllegalArgumentException with wildcard in the upper bound of a type variable, in a lambda or method reference
    * [#7060](https://github.com/pmd/pmd/issues/7060): \[java] getConstValue() returns null for constant expressions referencing final local variables
    * [#7133](https://github.com/pmd/pmd/issues/7133): \[java] CPD: Constructor detection state leaks between files with --ignore-identifiers
    * [#7145](https://github.com/pmd/pmd/issues/7145): \[java] Lambda with a parenthesized expression body is treated as void-compatible
    * [#7150](https://github.com/pmd/pmd/issues/7150): \[java] Numeric conditional with a char operand and an int constant is typed as int
    * [#7151](https://github.com/pmd/pmd/issues/7151): \[java] Unqualified method call resolves to an enclosing class method that the inner class hides
* java-bestpractices
    * [#5159](https://github.com/pmd/pmd/issues/5159): \[java] UnusedAssignment: false positive when using assert
    * [#6984](https://github.com/pmd/pmd/issues/6984): \[java] ExhaustiveSwitchHasDefault: Improve violation message to list missing cases
    * [#7018](https://github.com/pmd/pmd/issues/7018): \[java] UnusedAssignment: false positive for an assignment in a condition operand that may be skipped
    * [#7063](https://github.com/pmd/pmd/issues/7063): \[java] ResultSet.next() ? x : y fires CheckResultSet
    * [#7116](https://github.com/pmd/pmd/issues/7116): \[java] new rule: ControlFlowInTest
    * [#7119](https://github.com/pmd/pmd/issues/7119): \[java] SimplifiableTestAssertion: Various false negatives
* java-codestyle
    * [#6882](https://github.com/pmd/pmd/issues/6882): \[java] New Rule: Enforce local variable declarations to be at the start of a block
    * [#6903](https://github.com/pmd/pmd/issues/6903): \[java] Enhance UseExplicitTypes to allow verbose long explicit types
* java-design
    * [#4815](https://github.com/pmd/pmd/issues/4815): \[java] ExceptionAsFlowControl false-positive on Lambda/asynchronous (7.0.0-rc4)
    * [#7117](https://github.com/pmd/pmd/issues/7117): \[java] ExceptionAsFlowControl: false negative when the lambda is invoked by the method it is passed to
* java-errorprone
    * [#5158](https://github.com/pmd/pmd/issues/5158): \[java] SingletonClassReturningNewInstance: false positive due to type cast
    * [#7137](https://github.com/pmd/pmd/issues/7137): \[java] InvalidLogMessageFormat: false positive for unresolved exception type (incomplete auxclasspath)
* java-multithreading
    * [#7120](https://github.com/pmd/pmd/issues/7120): \[java] NonThreadSafeSingleton: throws NoSuchElementException on a record that null checks its own fields
* java-security
    * [#7014](https://github.com/pmd/pmd/issues/7014): \[java] InsecureCryptoIv/HardCodedCryptoKey: False negative when a hard-coded array is constructed through array element assignments
    * [#7015](https://github.com/pmd/pmd/issues/7015): \[java] InsecureCryptoIv/HardCodedCryptoKey: False negative for unchanged default-initialized arrays
* kotlin-bestpractices
    * [#7092](https://github.com/pmd/pmd/issues/7092): \[kotlin] New Rule: LooseCoupling
* plsql
    * [#6609](https://github.com/pmd/pmd/issues/6609): \[plsql] Parse exception when comparing a parenthesized arithmetic expression in a WHERE clause
    * [#7223](https://github.com/pmd/pmd/issues/7223): \[plsql] Deprecate AST node `ASTKEYWORD_UNRESERVED`

### 🚨️ API Changes
*   pmd-java
    *   Java constant folding now recognizes final primitive and String variables initialized with constant expressions,
        including local variables and unqualified instance fields. Numeric references are converted to their declared type.
        Boxed fields and field accesses qualified by expressions (such as `this.CONSTANT`) are not compile-time constants.
        These changes affect `ASTExpression.getConstValue()`, `isCompileTimeConstant()`, and the XPath attribute
        `@CompileTimeConstant`; custom Java and XPath rules relying on them may report different results.
    *   As part of [Deprecate getImage()/@Image (#4787)](https://github.com/pmd/pmd/issues/4787), the `@Image`
        attribute and `getImage()` method of multiple AST nodes have been deprecated:
        *   {% jdoc !!pmd-java::lang.java.ast.ASTConstructorDeclaration#getImage() %}:
            The `@Image` attribute of `ConstructorDeclaration` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTAmbiguousName#getImage() %}:
            The `@Image` attribute of `AmbiguousName` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTBreakStatement#getImage() %}:
            The `@Image` attribute of `BreakStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTContinueStatement#getImage() %}:
            The `@Image` attribute of `ContinueStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTFieldAccess#getImage() %}:
            The `@Image` attribute of `FieldAccess` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTImportDeclaration#getImage() %}:
            The `@Image` attribute of `ImportDeclaration` has been deprecated. Use `@ImportedName` instead.
            Note: This method always returns `null`. Don't use it. The deprecation is just a technicality.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTLabeledStatement#getImage() %}:
            The `@Image` attribute of `LabeledStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMemberValuePair#getImage() %}:
            The `@Image` attribute of `MemberValuePair` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMethodCall#getImage() %}:
            The `@Image` attribute of `MethodCall` has been deprecated. Use `@MethodName` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMethodDeclaration#getImage() %}:
            The `@Image` attribute of `MethodDeclaration` has been deprecated. Use `@Name` instead.
            Note: This method always returns `null`. Don't use it. The deprecation is just a technicality.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTPackageDeclaration#getImage() %}:
            The `@Image` attribute of `PackageDeclaration` has been deprecated. Use `@Name` instead.
            Note: This method always returns `null`. Don't use it. The deprecation is just a technicality.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTTypeParameter#getImage() %}:
            The `@Image` attribute of `TypeParameter` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTVariableAccess#getImage() %}:
            The `@Image` attribute of `VariableAccess` has been deprecated. Use `@Name` instead.
*   pmd-plsql
    *   {% jdoc pmd-plsql::lang.plsql.ast.ASTKEYWORD_UNRESERVED %} has been deprecated. This AST node was generated
        by mistake and is not used. The parser production has been deprecated already since 7.3.0.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
