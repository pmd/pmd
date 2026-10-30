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
* [#6923](https://github.com/pmd/pmd/pull/6923): \[java] Fix #6882: New Rule: Enforce that local variables are declared at the start of their scope without initialization - [Will-6543](https://github.com/Will-6543) (@Will-6543)
* [#6996](https://github.com/pmd/pmd/pull/6996): \[java] Fix #6984: ExhaustiveSwitchHasDefault: list missing cases in the violation - [Abdullah](https://github.com/AzazelSensei) (@AzazelSensei)
* [#7048](https://github.com/pmd/pmd/pull/7048): \[java] Deprecate getImage()/@<!-- -->Image and remove usages - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7050](https://github.com/pmd/pmd/pull/7050): \[html] Fix #5313: CPD end line without trailing newline - [Eymen Onar](https://github.com/Eymenonar) (@Eymenonar)
* [#7055](https://github.com/pmd/pmd/pull/7055): \[java] Fix #7054: Capture wildcard components of intersection bounds - [benstpierre](https://github.com/benstpierre) (@benstpierre)
* [#7059](https://github.com/pmd/pmd/pull/7059): \[java] Fix #7014/#7015: InsecureCryptoIv/HardCodedCryptoKey: Detect default-initialized arrays - [YukiAoto](https://github.com/aoto-tech) (@aoto-tech)
* [#7062](https://github.com/pmd/pmd/pull/7062): \[java] Fix #7060: Fix constant classification of final variable references - [YukiAoto](https://github.com/aoto-tech) (@aoto-tech)
* [#7066](https://github.com/pmd/pmd/pull/7066): \[java] Fix #7063: CheckResultSet false positive with next() as ternary condition - [Tanbir Hossain Ramim](https://github.com/TanbirRamim) (@TanbirRamim)
* [#7067](https://github.com/pmd/pmd/pull/7067): \[java] Fix #7018: UnusedAssignment false positive for assignment in condition operand - [fudian](https://github.com/fudianchn) (@fudianchn)
* [#7073](https://github.com/pmd/pmd/pull/7073): \[core] Fix #7072: Reuse compiled violationSuppressXPath queries - [YukiAoto](https://github.com/aoto-tech) (@aoto-tech)
* [#7087](https://github.com/pmd/pmd/pull/7087): \[scala] chore: Add test for #2153 - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#7088](https://github.com/pmd/pmd/pull/7088): \[java] Fix #6903: Rewrite UseExplicitTypes as Java rule to support allowLongTypeNames - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7094](https://github.com/pmd/pmd/pull/7094): \[kotlin] Fix #7092: Add LooseCoupling rule - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#7103](https://github.com/pmd/pmd/pull/7103): \[java] UnnecessaryVarargsArrayCreation: add regression tests for #5069 - [Yuriy Tumanov](https://github.com/Eljees) (@Eljees)
* [#7104](https://github.com/pmd/pmd/pull/7104): \[java] Fix #5159: UnusedAssignment: an assert statement may not run - [Yuriy Tumanov](https://github.com/Eljees) (@Eljees)
* [#7105](https://github.com/pmd/pmd/pull/7105): \[java] Fix #4815: ExceptionAsFlowControl: stop at a lambda body - [Yuriy Tumanov](https://github.com/Eljees) (@Eljees)
* [#7109](https://github.com/pmd/pmd/pull/7109): \[java] Fix #5158: SingletonClassReturningNewInstance: a local holding the existing instance is not new - [Yuriy Tumanov](https://github.com/Eljees) (@Eljees)
* [#7110](https://github.com/pmd/pmd/pull/7110): \[groovy] Fix #7100: CPD fails on GStrings ending in an interpolated variable - [anying](https://github.com/anyingiit) (@anyingiit)
* [#7112](https://github.com/pmd/pmd/pull/7112): \[doc] Use Markdown admonitions - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7115](https://github.com/pmd/pmd/pull/7115): \[plsql] Fix #6609: Parse parenthesized arithmetic comparisons - [Burak Kalaycı](https://github.com/kalayciburak) (@kalayciburak)
* [#7118](https://github.com/pmd/pmd/pull/7118): \[java] Fix #7117: ExceptionAsFlowControl: a throw in a lambda invoked by the method it is passed to - [Yuriy Tumanov](https://github.com/Eljees) (@Eljees)
* [#7121](https://github.com/pmd/pmd/pull/7121): \[java] Fix #7120: NonThreadSafeSingleton NoSuchElementException - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7122](https://github.com/pmd/pmd/pull/7122): \[java] Remove unused ctConstIfNotNull method from ASTExpression.ConstResult - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7123](https://github.com/pmd/pmd/pull/7123): \[core] Tokens: Add asserts - start of a location is never after the end of the location - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7129](https://github.com/pmd/pmd/pull/7129): \[java] Fix #7119: SimplifiableTestAssertion: Report more cases - [Akhil-1527](https://github.com/Akhil-1527) (@Akhil-1527)
* [#7130](https://github.com/pmd/pmd/pull/7130): \[core] Include XML validation details in ruleset loading errors - [Noor Ahmadi](https://github.com/noor-ahmadi) (@noor-ahmadi)
* [#7131](https://github.com/pmd/pmd/pull/7131): \[apex] Fix #5082: Allow "Schema." in "EagerlyLoadedDescribeSObjectResult" - [Marek Madejski](https://github.com/TrangOul) (@TrangOul)
* [#7132](https://github.com/pmd/pmd/pull/7132): \[java] Prepare deprecation of asCtx in java-errorprone (part of #4814) - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7138](https://github.com/pmd/pmd/pull/7138): \[java] Reset CPD constructor detection for each file - [Noor Ahmadi](https://github.com/noor-ahmadi) (@noor-ahmadi)
* [#7139](https://github.com/pmd/pmd/pull/7139): \[core] chore: Replace most of IOUtil with commons-io - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7154](https://github.com/pmd/pmd/pull/7154): Fix line endings of mwnw.cmd - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7157](https://github.com/pmd/pmd/pull/7157): \[core] Fix #7156: Analysis cache fails with ZipException on invalid archive - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#7158](https://github.com/pmd/pmd/pull/7158): \[java] Fix #7145: void compatibility of parenthesized lambda bodies - [Noor Ahmadi](https://github.com/noor-ahmadi) (@noor-ahmadi)
* [#7160](https://github.com/pmd/pmd/pull/7160): \[java] Fix #7137: InvalidLogMessageFormat: handle unresolved exception type (incomplete auxclasspath) - [Kaustubh Abhinand](https://github.com/KaustAbhinand) (@KaustAbhinand)
* [#7161](https://github.com/pmd/pmd/pull/7161): \[java] Fix #7151: Apply the comb rule to unqualified method calls - [sary](https://github.com/saryn17) (@saryn17)
* [#7162](https://github.com/pmd/pmd/pull/7162): \[java] Fix #7150: type of numeric conditionals with byte, short or char operands - [sary](https://github.com/saryn17) (@saryn17)
* [#7163](https://github.com/pmd/pmd/pull/7163): \[java] Fix #7146, #7149: Loose invocation conversion and the most specific check for boxing - [sary](https://github.com/saryn17) (@saryn17)
* [#7164](https://github.com/pmd/pmd/pull/7164): \[java] Fix #7148: Allow boxing of lambda and method reference results in the strict phase - [sary](https://github.com/saryn17) (@saryn17)
* [#7165](https://github.com/pmd/pmd/pull/7165): \[kotlin] Suppress empty-list attribute values in tree dump tests - [Peter Paul Bakker](https://github.com/stokpop) (@stokpop)
* [#7167](https://github.com/pmd/pmd/pull/7167): \[html] Fix #7166: off-by-one error in LineNumbers.java - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7169](https://github.com/pmd/pmd/pull/7169): chore: Ensure deprecated items are annotated with @<!-- -->Deprecated - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7170](https://github.com/pmd/pmd/pull/7170): chore: Replace `java.version` with `maven.compiler.release` in pom files - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7173](https://github.com/pmd/pmd/pull/7173): \[tests] Remove flow control from tests - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#7177](https://github.com/pmd/pmd/pull/7177): \[matlab] Fix #2258: Handle literal backslashes in character vectors - [subotac](https://github.com/subotac) (@subotac)
* [#7193](https://github.com/pmd/pmd/pull/7193): \[java] Fix #6409: UnnecessaryCast false positive for anonymous class - [Yang Bai](https://github.com/ybai08) (@ybai08)
* [#7199](https://github.com/pmd/pmd/pull/7199): \[java] Fix #6546: Consolidate the Vector replacement rules - [João Gabriel Alves Junior](https://github.com/Gabrieljr42) (@Gabrieljr42)
* [#7203](https://github.com/pmd/pmd/pull/7203): \[java] Fix #4576: UseShortArrayInitializer false positive with arrays of parameterized types - [markxu](https://github.com/markjoin123) (@markjoin123)
* [#7219](https://github.com/pmd/pmd/pull/7219): \[java] Fix #7213: UnusedPrivateMethod/UnusedPrivateField with qualified MethodSource/FieldSource references - [leeshengis](https://github.com/leeshengis) (@leeshengis)
* [#7224](https://github.com/pmd/pmd/pull/7224): \[plsql] Fix #7223: Deprecate AST node ASTKEYWORD_UNRESERVED - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7230](https://github.com/pmd/pmd/pull/7230): \[core] Remove ShortFilenameUtil.java - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7232](https://github.com/pmd/pmd/pull/7232): \[core] Remove more unused code (FileFinder.java, FileExtensionFilter.java, PredicateUtil.getFileExtensionFilter()) - [Sören Glimm](https://github.com/UncleOwen) (@UncleOwen)
* [#7259](https://github.com/pmd/pmd/pull/7259): \[core] Don't follow symlinks in FileCollector - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7260](https://github.com/pmd/pmd/pull/7260): \[core] HTMLRenderers: Properly escape rule name and externalInfoUrl - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7261](https://github.com/pmd/pmd/pull/7261): \[core] Disallow external entity expansions/DTD in XML parsing - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#7262](https://github.com/pmd/pmd/pull/7262): \[core] Fix missing escaping in CSV renderers - [Andreas Dangel](https://github.com/adangel) (@adangel)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#7113](https://github.com/pmd/pmd/pull/7113): Bump PMD from 7.27.0 to 7.28.0
* [#7114](https://github.com/pmd/pmd/pull/7114): chore: Replace system-lambda with system-stubs
* [#7124](https://github.com/pmd/pmd/pull/7124): chore(deps): bump org.mockito:mockito-core from 5.23.0 to 5.24.0
* [#7125](https://github.com/pmd/pmd/pull/7125): chore(deps): bump org.apache.groovy:groovy from 5.1.2 to 5.1.3
* [#7126](https://github.com/pmd/pmd/pull/7126): chore(deps): bump ruby/setup-ruby from 1.324.0 to 1.327.0
* [#7127](https://github.com/pmd/pmd/pull/7127): chore(deps): bump org.apache.commons:commons-lang3 from 3.20.0 to 3.21.0
* [#7128](https://github.com/pmd/pmd/pull/7128): chore(deps): bump crate-ci/typos from 1.50.2 to 1.50.3
* [#7140](https://github.com/pmd/pmd/pull/7140): chore(deps): bump org.apache.maven:apache-maven from 3.9.16 to 3.10.0
* [#7141](https://github.com/pmd/pmd/pull/7141): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.2.0 to 6.3.1
* [#7142](https://github.com/pmd/pmd/pull/7142): chore(deps-dev): bump com.google.guava:guava from 33.7.1-jre to 33.7.2-jre
* [#7143](https://github.com/pmd/pmd/pull/7143): chore(deps): bump com.puppycrawl.tools:checkstyle from 14.1.0 to 14.3.0
* [#7159](https://github.com/pmd/pmd/pull/7159): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.26.2 to 0.26.3
* [#7185](https://github.com/pmd/pmd/pull/7185): chore(deps): bump ruby/setup-ruby from 1.327.0 to 1.328.0
* [#7186](https://github.com/pmd/pmd/pull/7186): chore(deps): bump crate-ci/typos from 1.50.3 to 1.51.1
* [#7187](https://github.com/pmd/pmd/pull/7187): chore(deps): bump actions/upload-artifact from 7.0.1 to 7.0.2
* [#7188](https://github.com/pmd/pmd/pull/7188): chore(deps): bump actions/download-artifact from 8.0.1 to 8.0.2
* [#7189](https://github.com/pmd/pmd/pull/7189): chore(deps): bump org.ow2.asm:asm from 9.10.1 to 9.11
* [#7190](https://github.com/pmd/pmd/pull/7190): chore(deps): bump actions/setup-java from 6.0.1 to 6.1.0
* [#7205](https://github.com/pmd/pmd/pull/7205): chore(deps): bump org.ow2.asm:asm from 9.11 to 9.11.1
* [#7206](https://github.com/pmd/pmd/pull/7206): chore(deps): bump byte.buddy.version from 1.18.14 to 1.18.15
* [#7207](https://github.com/pmd/pmd/pull/7207): chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.11.0 to 0.12.0
* [#7208](https://github.com/pmd/pmd/pull/7208): chore(deps): bump io.github.apex-dev-tools:apex-parser from 5.2.0 to 5.3.0
* [#7209](https://github.com/pmd/pmd/pull/7209): chore(deps): bump ruby/setup-ruby from 1.328.0 to 1.329.0
* [#7210](https://github.com/pmd/pmd/pull/7210): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.26.3 to 0.26.5
* [#7212](https://github.com/pmd/pmd/pull/7212): chore(deps-dev): bump org.codehaus.mojo:versions-maven-plugin from 2.22.0 to 2.23.0
* [#7211](https://github.com/pmd/pmd/pull/7211): chore(deps): bump actions/create-github-app-token from 3.2.0 to 3.3.0
* [#7222](https://github.com/pmd/pmd/pull/7222): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.3.1 to 6.3.2
* [#7247](https://github.com/pmd/pmd/pull/7247): chore(deps): bump byte.buddy.version from 1.18.15 to 1.18.16
* [#7248](https://github.com/pmd/pmd/pull/7248): chore(deps): bump ruby/setup-ruby from 1.329.0 to 1.335.0
* [#7249](https://github.com/pmd/pmd/pull/7249): chore(deps): bump surefire.version from 3.6.0 to 3.6.1
* [#7250](https://github.com/pmd/pmd/pull/7250): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.3.2 to 6.4.0
* [#7251](https://github.com/pmd/pmd/pull/7251): chore(deps): bump com.google.code.gson:gson from 2.14.0 to 2.15.0

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 91 commits
* 95 closed tickets & PRs
* Days since last release: 34

{% endtocmaker %}
