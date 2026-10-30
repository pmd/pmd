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
#### Security fixes
All known security vulnerabilities are described in full detail on the page
[PMD Security]({{ baseurl }}pmd_about_security.html). This release, the following
security vulnerabilities were fixed:

*   [GHSA-wj6q-xw9m-26p6](https://github.com/pmd/pmd/security/advisories/GHSA-wj6q-xw9m-26p6):
    Arbitrary file disclosure in CPD via symlinks in analyzed sources.  
    When collecting files, CPD followed symbolic links, even if their targets were outside the scanned
    directory. This allowed reading any file accessible to the user running CPD. If a CI/CD pipeline
    runs CPD on untrusted source code (e.g. a pull request from an external contributor), an attacker
    could commit two symlinks pointing to the same file outside the project. CPD detected them as
    duplicates and wrote the full content of the target file into the report. If the report was
    published, e.g. as a CI artifact, the attacker could read it.  
    With this fix, neither PMD nor CPD follows symbolic links anymore when collecting files to analyze.  
    Affected versions: PMD >= 6.54.0 and <= 7.28.0. If you run CPD on untrusted source code,
    it is recommended to upgrade PMD.  
    Reported by [Dan](https://github.com/7alen7) (@7alen7).
    Independently reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
*   [GHSA-5x5v-644m-q2qc](https://github.com/pmd/pmd/security/advisories/GHSA-5x5v-644m-q2qc):
    XSS via externalInfoUrl in HTML report formats.  
    The `html` and `summaryhtml` renderers inserted a rule's `externalInfoUrl` into generated reports
    without escaping or URL-scheme validation. With a crafted ruleset, this allowed injected markup that
    runs when the report is opened (with `--show-suppressed`), or `javascript:` links that run when clicked.
    This only affects setups where PMD loads an untrusted ruleset and the resulting HTML report is viewed
    in a browser.  
    Affected versions: PMD 7.28.0 and earlier. If you use the html or summaryhtml report formats, it is
    recommended to upgrade PMD.  
    Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
*   [GHSA-x2h8-xxj6-pf6c](https://github.com/pmd/pmd/security/advisories/GHSA-x2h8-xxj6-pf6c):
    XSS via rule name in summaryhtml report format.  
    The `summaryhtml` renderer wrote rule names into the report without HTML escaping. A ruleset with a
    crafted rule name could inject JavaScript that runs in the browser of anyone who opens the report.
    This affects setups that use `--format summaryhtml` together with a ruleset from an untrusted source,
    e.g. CI/CD pipelines that use a ruleset from a pull request by an external contributor.
    If the report is served from a web host (e.g. a CI dashboard), the script runs in that origin.
    This is a follow-up to CVE-2026-28338, whose fix did not cover the summaryhtml renderer.  
    Affected versions: PMD 7.28.0 and earlier. If you use the summaryhtml report format, it is
    recommended to upgrade PMD.  
    Reported by [Dan](https://github.com/7alen7) (@7alen7).
    Independently reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
*   [GHSA-9hm5-r2r7-hjcq](https://github.com/pmd/pmd/security/advisories/GHSA-9hm5-r2r7-hjcq):
    Local file reads through XXE in PMD rule tests.  
    **Normal usage of PMD for analyzing source code is not affected.** Only projects that develop custom
    rules and test them with PMD's rule test framework (modules `pmd-test` and `pmd-test-schema`) are affected.  
    The parser for rule-test XML files did not disable DTDs and external entities. A crafted rule-test XML
    file could therefore read a local file and place its contents into a parsed test description. Since the
    description is used as the name of the JUnit test, the file contents could appear in test results.
    This is relevant when rule-test XML files from untrusted sources are executed, e.g. in a CI/CD pipeline
    for pull requests from external contributors.  
    With this fix, DTDs are disabled. Rule-test XML files containing a `DOCTYPE` declaration are rejected.
    This was never officially supported, as PMD doesn't provide a DTD for rule-test XML files.  
    Affected versions: `pmd:pmd` >= 3.9, `net.sourceforge.pmd:pmd-test` <= 6.47.0,
    `net.sourceforge.pmd:pmd-test-schema` <= 7.28.0. If you test custom rules with PMD's rule test
    framework, it is recommended to upgrade PMD.  
    Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
*   [GHSA-wv72-2ccf-28gj](https://github.com/pmd/pmd/security/advisories/GHSA-wv72-2ccf-28gj):
    Blind SSRF through Visualforce metadata parser.  
    When resolving custom field types, the Visualforce module parsed Salesforce `*.field-meta.xml` files
    from the analyzed source tree without disabling external DTD loading. A crafted metadata file could
    therefore make PMD send a request to an attacker-chosen URL during analysis. This affects users who
    analyze Visualforce code from untrusted sources, such as pull requests.  
    Affected versions: `net.sourceforge.pmd:pmd-visualforce` >= 6.30.0, <= 7.28.0  
    Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
*   [GHSA-jm8j-h474-h54h](https://github.com/pmd/pmd/security/advisories/GHSA-jm8j-h474-h54h):
    Formula injection in CSV report format via unescaped quotes in file names.  
    The CSV renderer neither escaped embedded double quotes nor neutralized values starting with
    formula trigger characters (`=`, `+`, `-`, `@`). This let a crafted source filename, or a rule name, ruleset name
    or rule message from an attacker-controlled ruleset, inject a formula into the CSV report, which a spreadsheet
    application could evaluate when the report is opened.  
    Affected versions: `pmd:pmd` >= 1.2, `net.sourceforge.pmd:pmd-core` <= 7.28.0  
    Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).
    Independently reported by [Hama1cco](https://github.com/Hama1cco) (@Hama1cco).

### 🌟️ New and Changed Rules
#### New Rules
*   The new Java rule {% rule java/bestpractices/ReplaceJavaUtilVector %} reports usages of `java.util.Vector`,
    including subclasses such as `Stack`, and suggests modern alternatives when thread safety is not required.
    It recommends `ArrayList` for `Vector` and `Deque` with an implementation such as `ArrayDeque` for `Stack`.
    A declaration such as `Vector<String> v = new Vector<>()` is reported only once, not twice. It replaces
    the rules ReplaceVectorWithList and UseArrayListInsteadOfVector, which are now deprecated (see below).
*   The new Java rule {% rule java/codestyle/LocalVariableDeclarationShouldBeAtStartOfBlock %} enforces a code style
    where all local variables are declared together at the start of their block, without initialization.
    Optionally, the declarations can be required to be sorted by name or by type and name
    (see properties `sortBy` and `caseSensitiveSorting`).
*   The new Kotlin rule {% rule kotlin/bestpractices/LooseCoupling %} reports declarations (properties,
    parameters, function return types) using concrete collection/map implementation types (e.g. `ArrayList`,
    `HashMap`) instead of the corresponding interface (e.g. `List`, `Map`). It is powered by the
    `pmd-kotlin:typeIs()` XPath function introduced in PMD 7.28.0.

#### Changed Rules
*   Several Java rules now benefit from improved constant folding (see [#7060](https://github.com/pmd/pmd/issues/7060)),
    which now recognizes final local variables and unqualified instance fields initialized with constant
    expressions. This may change the reported violations of the following rules:
    {% rule java/bestpractices/AvoidReassigningLoopVariables %},
    {% rule java/bestpractices/LiteralsFirstInComparisons %},
    {% rule java/bestpractices/UnusedAssignment %},
    {% rule java/design/FinalFieldCouldBeStatic %},
    {% rule java/errorprone/UnconditionalIfStatement %},
    {% rule java/errorprone/UnusedNullCheckInEquals %}.
    Notably, LiteralsFirstInComparisons may report new violations when such a constant is the argument
    of a comparison.
*   The Java rule {% rule java/codestyle/UseExplicitTypes %} has a new property `allowLongTypeNames`. It allows
    using `var` when the explicit type name would be longer than a given minimum length configured with
    this property.
*   The Java rule {% rule java/performance/AddEmptyString %} no longer reports concatenation of an empty string
    with a compile-time constant variable, or with a constant expression that uses one. `javac` inlines those
    concatenations to a string literal, while `String.valueOf` and `Integer.toString` stay method calls.
    Literal conversions such as `"" + 123`, and concatenation of two strings, are still reported.

#### Deprecated Rules
*   The Java rules {% rule java/bestpractices/ReplaceVectorWithList %} and
    {% rule java/performance/UseArrayListInsteadOfVector %} are deprecated aliases of
    {% rule java/bestpractices/ReplaceJavaUtilVector %}. Update rule references to use the new name.

### 🐛️ Fixed Issues
* apex-performance
    * [#5082](https://github.com/pmd/pmd/issues/5082): \[apex] EagerlyLoadedDescribeSObjectResult: False positive with fully qualified SObjectDescribeOptions/FieldDescribeOptions
* core
    * [#6912](https://github.com/pmd/pmd/issues/6912): \[core] Include XML validation details in ruleset loading errors
    * [#7072](https://github.com/pmd/pmd/issues/7072): \[core] Reuse compiled violationSuppressXPath queries
    * [#7156](https://github.com/pmd/pmd/issues/7156): \[core] Analysis cache fails with ZipException on invalid archive on the auxclasspath
* groovy
    * [#7110](https://github.com/pmd/pmd/issues/7100): \[groovy] CPD fails on GStrings that end in an interpolated variable
* html
    * [#5313](https://github.com/pmd/pmd/issues/5313): \[html] CPD does not include the last line in the duplication chunk
    * [#7166](https://github.com/pmd/pmd/issues/7166): \[html] endcolumn of nodes is off-by-one
* java
    * [#7048](https://github.com/pmd/pmd/issues/7048): \[java] Deprecate getImage()/@<!-- -->Image and remove usages
    * [#7054](https://github.com/pmd/pmd/issues/7054): \[java] IllegalArgumentException with wildcard in the upper bound of a type variable, in a lambda or method reference
    * [#7060](https://github.com/pmd/pmd/issues/7060): \[java] getConstValue() returns null for constant expressions referencing final local variables
    * [#7133](https://github.com/pmd/pmd/issues/7133): \[java] CPD: Constructor detection state leaks between files with --ignore-identifiers
    * [#7145](https://github.com/pmd/pmd/issues/7145): \[java] Lambda with a parenthesized expression body is treated as void-compatible
    * [#7146](https://github.com/pmd/pmd/issues/7146): \[java] Overload resolution prefers int over Integer where javac reports an ambiguity
    * [#7148](https://github.com/pmd/pmd/issues/7148): \[java] Strict phase rejects lambda and method reference results that need boxing
    * [#7149](https://github.com/pmd/pmd/issues/7149): \[java] Loose invocation conversion accepts int to Long and null to int
    * [#7150](https://github.com/pmd/pmd/issues/7150): \[java] Numeric conditional with a char operand and an int constant is typed as int
    * [#7151](https://github.com/pmd/pmd/issues/7151): \[java] Unqualified method call resolves to an enclosing class method that the inner class hides
* java-bestpractices
    * [#5159](https://github.com/pmd/pmd/issues/5159): \[java] UnusedAssignment: False positive when using assert
    * [#6546](https://github.com/pmd/pmd/issues/6546): \[java] New Rule: ReplaceJavaUtilVector to consolidate ReplaceVectorWithList / UseArrayListInsteadOfVector
    * [#6984](https://github.com/pmd/pmd/issues/6984): \[java] ExhaustiveSwitchHasDefault: Improve violation messages to list missing cases
    * [#7018](https://github.com/pmd/pmd/issues/7018): \[java] UnusedAssignment: False positive for an assignment in a condition operand that may be skipped
    * [#7063](https://github.com/pmd/pmd/issues/7063): \[java] CheckResultSet: False positive for rs.next() ? x : y
    * [#7119](https://github.com/pmd/pmd/issues/7119): \[java] SimplifiableTestAssertion: False negatives for assertInstanceOf, assertNull, assertNonNull
    * [#7213](https://github.com/pmd/pmd/issues/7213): \[java] UnusedPrivateMethod / UnusedPrivateField: False positives for fully qualified @<!-- -->MethodSource / @<!-- -->FieldSource references
* java-codestyle
    * [#4576](https://github.com/pmd/pmd/issues/4576): \[java] UseShortArrayInitializer: False positive with arrays of parametrized types (generic array creation)
    * [#6409](https://github.com/pmd/pmd/issues/6409): \[java] UnnecessaryCast: False positive for anonymous class in Stream.map() with toList()
    * [#6882](https://github.com/pmd/pmd/issues/6882): \[java] New Rule: Enforce local variable declarations to be at the start of a block
    * [#6903](https://github.com/pmd/pmd/issues/6903): \[java] Enhance UseExplicitTypes to allow verbose long explicit types
* java-design
    * [#4815](https://github.com/pmd/pmd/issues/4815): \[java] ExceptionAsFlowControl: False positive on Lambda/asynchronous
    * [#7117](https://github.com/pmd/pmd/issues/7117): \[java] ExceptionAsFlowControl: False negative when the lambda is invoked by the method it is passed to
* java-errorprone
    * [#5158](https://github.com/pmd/pmd/issues/5158): \[java] SingletonClassReturningNewInstance: False positive due to type cast
    * [#7137](https://github.com/pmd/pmd/issues/7137): \[java] InvalidLogMessageFormat: False positive for unresolved exception type (incomplete auxclasspath)
* java-multithreading
    * [#7120](https://github.com/pmd/pmd/issues/7120): \[java] NonThreadSafeSingleton: throws NoSuchElementException on a record that null checks its own fields
* java-performance
    * [#6327](https://github.com/pmd/pmd/issues/6327): \[java] AddEmptyString: False positive reported for constants
* java-security
    * [#7014](https://github.com/pmd/pmd/issues/7014): \[java] InsecureCryptoIv/HardCodedCryptoKey: False negative when a hard-coded array is constructed through array element assignments
    * [#7015](https://github.com/pmd/pmd/issues/7015): \[java] InsecureCryptoIv/HardCodedCryptoKey: False negative for unchanged default-initialized arrays
* kotlin-bestpractices
    * [#7092](https://github.com/pmd/pmd/issues/7092): \[kotlin] New Rule: LooseCoupling
* matlab
    * [#2258](https://github.com/pmd/pmd/issues/2258): \[matlab] CPD: Parse error with backslashes in character vectors/string literals
* plsql
    * [#6609](https://github.com/pmd/pmd/issues/6609): \[plsql] Parse exception when comparing a parenthesized arithmetic expression in a WHERE clause
    * [#7223](https://github.com/pmd/pmd/issues/7223): \[plsql] Deprecate AST node `ASTKEYWORD_UNRESERVED`

### 🚨️ API Changes
*   pmd-java
    *   Java constant folding now recognizes final primitive and String variables initialized with constant expressions,
        including local variables and unqualified instance fields. Numeric references are converted to their declared type.
        Boxed fields and field accesses qualified by expressions (such as `this.CONSTANT`) are not compile-time constants.
        These changes affect {% jdoc !!pmd-java::lang.java.ast.ASTExpression#getConstValue() %},
        {% jdoc pmd-java::lang.java.ast.ASTExpression#isCompileTimeConstant() %}, and the XPath attribute
        `@CompileTimeConstant`; custom Java and XPath rules relying on them may report different results.
    *   As part of [Deprecate getImage()/@<!-- -->Image (#4787)](https://github.com/pmd/pmd/issues/4787), the `@Image`
        attribute and `getImage()` method of multiple AST nodes have been deprecated:
        *   {% jdoc !!pmd-java::lang.java.ast.ASTAmbiguousName#getImage() %}:
            The `@Image` attribute of `AmbiguousName` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTBreakStatement#getImage() %}:
            The `@Image` attribute of `BreakStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTConstructorDeclaration#getImage() %}:
            The `@Image` attribute of `ConstructorDeclaration` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTContinueStatement#getImage() %}:
            The `@Image` attribute of `ContinueStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTFieldAccess#getImage() %}:
            The `@Image` attribute of `FieldAccess` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTImportDeclaration#getImage() %}:
            The `@Image` attribute of `ImportDeclaration` has been deprecated. Use `@ImportedName` instead.
            Note: This method always returns `null`. Don't use it. It is only deprecated for consistency.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTLabeledStatement#getImage() %}:
            The `@Image` attribute of `LabeledStatement` has been deprecated. Use `@Label` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMemberValuePair#getImage() %}:
            The `@Image` attribute of `MemberValuePair` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMethodCall#getImage() %}:
            The `@Image` attribute of `MethodCall` has been deprecated. Use `@MethodName` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTMethodDeclaration#getImage() %}:
            The `@Image` attribute of `MethodDeclaration` has been deprecated. Use `@Name` instead.
            Note: This method always returns `null`. Don't use it. It is only deprecated for consistency.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTPackageDeclaration#getImage() %}:
            The `@Image` attribute of `PackageDeclaration` has been deprecated. Use `@Name` instead.
            Note: This method always returns `null`. Don't use it. It is only deprecated for consistency.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTTypeParameter#getImage() %}:
            The `@Image` attribute of `TypeParameter` has been deprecated. Use `@Name` instead.
        *   {% jdoc !!pmd-java::lang.java.ast.ASTVariableAccess#getImage() %}:
            The `@Image` attribute of `VariableAccess` has been deprecated. Use `@Name` instead.
*   pmd-plsql
    *   {% jdoc pmd-plsql::lang.plsql.ast.ASTKEYWORD_UNRESERVED %} has been deprecated. This AST node was generated
        by mistake and is not used. The parser production has already been deprecated since 7.3.0.

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
