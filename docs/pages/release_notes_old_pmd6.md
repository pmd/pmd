---
title: Old Release Notes PMD 6.x
permalink: pmd_release_notes_old_pmd6.html
---

Previous versions of PMD can be downloaded here: [Releases - pmd/pmd (GitHub)](https://github.com/pmd/pmd/releases)

## 25-February-2023 - 6.55.0

The PMD team is pleased to announce PMD 6.55.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [PMD 7 Development](#pmd-7-development)
    * [Java 20 Support](#java-20-support)
    * [T-SQL support](#t-sql-support)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Go](#go)
    * [Java](#java)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### PMD 7 Development
This release is the last planned release of PMD 6. The first version 6.0.0 was released in December 2017.
Over the course of more than 5 years we published almost every month a new minor version of PMD 6
with new features and improvements.

Already in November 2018 we started in parallel the development of the next major version 7.0.0,
and we are now in the process of finalizing the scope of the major version. We want to release a couple of
release candidates before publishing the final version 7.0.0.

We plan to release 7.0.0-rc1 soon. You can see the progress in [PMD 7 Tracking Issue #3898](https://github.com/pmd/pmd/issues/3898).

#### Java 20 Support
This release of PMD brings support for Java 20. There are no new standard language features.

PMD supports [JEP 433: Pattern Matching for switch (Fourth Preview)](https://openjdk.org/jeps/433) and
[JEP 432: Record Patterns (Second Preview)](https://openjdk.org/jeps/432) as preview language features.

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `20-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd --use-version java-20-preview ...

#### T-SQL support

Thanks to the contribution from [Paul Guyot](https://github.com/pguyot) PMD now has CPD support
for T-SQL (Transact-SQL).

Being based on a proper Antlr grammar, CPD can:

* ignore comments
* honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Fixed Issues
* core
    * [#4395](https://github.com/pmd/pmd/issues/4395): \[core] Support environment variable CLASSPATH with pmd.bat under Windows
* java
    * [#4333](https://github.com/pmd/pmd/issues/4333): \[java] Support JDK 20
* java-errorprone
    * [#4393](https://github.com/pmd/pmd/issues/4393): \[java] MissingStaticMethodInNonInstantiatableClass false-positive for Lombok's @UtilityClass for classes with non-private fields

### API Changes

#### Go
* The LanguageModule of Go, that only supports CPD execution, has been deprecated. This language
  is not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
  not affected by this change. The following class has been deprecated and will be removed with PMD 7.0.0:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-go/6.55.0/net/sourceforge/pmd/lang/go/GoLanguageModule.html#"><code>GoLanguageModule</code></a>

#### Java
* Support for Java 18 preview language features have been removed. The version "18-preview" is no longer available.
* The experimental class `net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern` has been removed.

### External Contributions
* [#4384](https://github.com/pmd/pmd/pull/4384): \[swift] Add more swift 5.x support (#unavailable mainly) - [Richard B.](https://github.com/kenji21) (@kenji21)
* [#4390](https://github.com/pmd/pmd/pull/4390): Add support for T-SQL using Antlr4 lexer - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4392](https://github.com/pmd/pmd/pull/4392): \[java] Fix #4393 MissingStaticMethodInNonInstantiatableClass: Fix false-positive for field-only class - [Dawid Ciok](https://github.com/dawiddc) (@dawiddc)

### Stats
* 40 commits
* 11 closed tickets & PRs
* Days since last release: 28

## 28-January-2023 - 6.54.0

The PMD team is pleased to announce PMD 6.54.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [New report format html-report-v2.xslt](#new-report-format-html-report-v2.xslt)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [PMD CLI](#pmd-cli)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
        * [Internal APIs](#internal-apis)
        * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### New report format html-report-v2.xslt

Thanks to @mohan-chinnappan-n a new PMD report format has been added which features a data table
with charting functions. It uses an XSLT stylesheet to convert PMD's XML format into HTML.

See [the example report](report-examples/html-report-v2.html).

### Fixed Issues
* apex-bestpractices
    * [#2669](https://github.com/pmd/pmd/issues/2669): \[apex] UnusedLocalVariable false positive in dynamic SOQL
* core
    * [#4026](https://github.com/pmd/pmd/issues/4026): \[cli] Filenames printed as absolute paths in the report despite parameter `--short-names`
    * [#4279](https://github.com/pmd/pmd/issues/4279): \[core] Can not set ruleset property value to empty
    * [#4329](https://github.com/pmd/pmd/pull/4329): \[core] Refactor usage of snakeyaml
    * [#4340](https://github.com/pmd/pmd/issues/4340): \[core] Allow to filter found matches in CPDReport
* java
    * [#4364](https://github.com/pmd/pmd/issues/4364): \[java] Parsing error with textblock containing quote followed by two backslashes
* testing
    * [#4236](https://github.com/pmd/pmd/issues/4236): \[test] kotest logs look broken

### API Changes

#### PMD CLI

* PMD now supports a new `--relativize-paths-with` flag (or short `-z`), which replaces `--short-names`.
  It serves the same purpose: Shortening the pathnames in the reports. However, with the new flag it's possible
  to explicitly define one or more pathnames that should be used as the base when creating relative paths.
  The old flag `--short-names` is deprecated.

#### Deprecated APIs

##### For removal

* <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.54.0/net/sourceforge/pmd/lang/apex/ast/ApexRootNode.html#getApexVersion()"><code>ApexRootNode#getApexVersion</code></a> has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setEncoding(java.lang.String)"><code>CPDConfiguration#setEncoding</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getEncoding()"><code>CPDConfiguration#getEncoding</code></a>. Use the methods
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/AbstractConfiguration.html#getSourceEncoding()"><code>getSourceEncoding</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/AbstractConfiguration.html#setSourceEncoding(java.lang.String)"><code>setSourceEncoding</code></a> instead. Both are available
  for `CPDConfiguration` which extends `AbstractConfiguration`.
* <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.54.0/net/sourceforge/pmd/cli/BaseCLITest.html#"><code>BaseCLITest</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.54.0/net/sourceforge/pmd/cli/BaseCPDCLITest.html#"><code>BaseCPDCLITest</code></a> have been deprecated for removal without
  replacement. CLI tests should be done in pmd-core only (and in PMD7 in pmd-cli). Individual language modules
  shouldn't need to test the CLI integration logic again. Instead, the individual language modules should test their
  functionality as unit tests.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/cpd/CPDConfiguration.LanguageConverter.html#"><code>CPDConfiguration.LanguageConverter</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFile(java.nio.file.Path)"><code>FileCollector#addZipFile</code></a> has been deprecated. It is replaced
  by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/lang/document/FileCollector.html#addZipFileWithContent(java.nio.file.Path)"><code>FileCollector#addZipFileWithContent</code></a> which directly adds the
  content of the zip file for analysis.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/PMDConfiguration.html#setReportShortNames(boolean)"><code>PMDConfiguration#setReportShortNames</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/PMDConfiguration.html#isReportShortNames()"><code>PMDConfiguration#isReportShortNames</code></a> have been deprecated for removal.
  Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/PMDConfiguration.html#addRelativizeRoot(java.nio.file.Path)"><code>PMDConfiguration#addRelativizeRoot</code></a> instead.

##### Internal APIs

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/renderers/CSVWriter.html#"><code>CSVWriter</code></a>
* Some fields in <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.54.0/net/sourceforge/pmd/ant/AbstractAntTestHelper.html#"><code>AbstractAntTestHelper</code></a>

##### Experimental APIs

* CPDReport has a new method which limited mutation of a given report:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.54.0/net/sourceforge/pmd/cpd/CPDReport.html#filterMatches(net.sourceforge.pmd.util.Predicate)"><code>filterMatches</code></a> creates a new CPD report
      with some matches removed with a given predicate based filter.

### External Contributions
* [#4110](https://github.com/pmd/pmd/pull/4110): \[apex] Feature/unused variable bind false positive with dynamic SOQL - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4125](https://github.com/pmd/pmd/pull/4125): \[core] New report format html-report-v2.xslt to provide html with datatable and chart features - [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) - (@mohan-chinnappan-n)
* [#4280](https://github.com/pmd/pmd/pull/4280): \[apex] Deprecate ApexRootNode.getApexVersion - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4285](https://github.com/pmd/pmd/pull/4285): \[java] CommentDefaultAccessModifier - add co.elastic.clients.util.VisibleForTesting as default suppressed annotation - [Matthew Luckam](https://github.com/mluckam) (@mluckam)

### Stats
* 107 commits
* 19 closed tickets & PRs
* Days since last release: 27

## 31-December-2022 - 6.53.0

The PMD team is pleased to announce PMD 6.53.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Modified rules](#modified-rules)
    * [Deprecated rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Modified rules

* The Java rule [`UnusedPrivateField`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_bestpractices.html#unusedprivatefield) has a new property `reportForAnnotations`.
  This is a list of fully qualified names of the annotation types that should be reported anyway. If an unused field
  has any of these annotations, then it is reported. If it has any other annotation, then it is still considered
  to be used and is not reported.

#### Deprecated rules

* The Java rules [`ExcessiveClassLength`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_design.html#excessiveclasslength) and [`ExcessiveMethodLength`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_design.html#excessivemethodlength)
  have been deprecated. The rule [`NcssCount`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_design.html#ncsscount) can be used instead.
  The deprecated rules will be removed with PMD 7.0.0.

* The Java rule [`EmptyStatementNotInLoop`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_errorprone.html#emptystatementnotinloop) is deprecated.
  Use the rule [`UnnecessarySemicolon`](https://pmd.github.io/pmd-6.53.0/pmd_rules_java_codestyle.html#unnecessarysemicolon) instead.
  Note: Actually it was announced to be deprecated since 6.46.0 but the rule was not marked as deprecated yet.
  This has been done now.

### Fixed Issues
* core
    * [#4248](https://github.com/pmd/pmd/issues/4248): \[core] Can't analyze sources in zip files
* apex-security
    * [#4146](https://github.com/pmd/pmd/issues/4146): \[apex] ApexCRUDViolation: Recognize User Mode in SOQL + DML
* java
    * [#4266](https://github.com/pmd/pmd/issues/4266): \[java] PMD fails to process a record with lambda in compact constructor
* java-bestpractices
    * [#4166](https://github.com/pmd/pmd/issues/4166): \[java] UnusedPrivateField doesn't find annotated unused private fields anymore
    * [#4250](https://github.com/pmd/pmd/issues/4250): \[java] WhileLoopWithLiteralBoolean - false negative with complex expressions still occurs in PMD 6.52.0
* java-design
    * [#2127](https://github.com/pmd/pmd/issues/2127): \[java] Deprecate rules ExcessiveClassLength and ExcessiveMethodLength
* java-errorprone
    * [#4164](https://github.com/pmd/pmd/issues/4164): \[java]\[doc] AvoidAssertAsIdentifier and AvoidEnumAsIdentifier - clarify use case
* java-multithreading
    * [#4210](https://github.com/pmd/pmd/issues/4210): \[java] DoNotUseThreads report duplicate warnings

### API Changes

#### Deprecated APIs

##### For removal

These classes / APIs have been deprecated and will be removed with PMD 7.0.0.

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.53.0/net/sourceforge/pmd/lang/java/rule/design/ExcessiveLengthRule.html#"><code>ExcessiveLengthRule</code></a> (Java)

### External Contributions
* [#4244](https://github.com/pmd/pmd/pull/4244): \[apex] ApexCRUDViolation: user mode and system mode with test cases added - [Tarush Singh](https://github.com/Tarush-Singh35) (@Tarush-Singh35)
* [#4274](https://github.com/pmd/pmd/pull/4274): \[java] Fix finding lambda scope in record compact constructor - [kdebski85](https://github.com/kdebski85) (@kdebski85)

### Stats
* 43 commits
* 17 closed tickets & PRs
* Days since last release: 35

## 26-November-2022 - 6.52.0

The PMD team is pleased to announce PMD 6.52.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [New rules](#new-rules)
    * [Renamed rules](#renamed-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [PMD CLI](#pmd-cli)
    * [CPD CLI](#cpd-cli)
    * [Linux run.sh parameters](#linux-run.sh-parameters)
    * [Deprecated API](#deprecated-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### New rules

* The new Java rule [`InvalidJavaBean`](https://pmd.github.io/pmd-6.52.0/pmd_rules_java_design.html#invalidjavabean) identifies beans, that don't follow the [JavaBeans API specification](https://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/),
  like beans with missing getters or setters.

```xml
<rule ref="category/java/design.xml/InvalidJavaBean"/>
```

#### Renamed rules

* The Java rule [`BeanMembersShouldSerialize`](https://pmd.github.io/pmd-6.52.0/pmd_rules_java_errorprone.html#beanmembersshouldserialize) has been renamed to
  [`NonSerializableClass`](https://pmd.github.io/pmd-6.52.0/pmd_rules_java_errorprone.html#nonserializableclass). It has been revamped to only check for classes that are marked with
  `Serializable` and reports each field in it, that is not serializable.

  The property `prefix` has been deprecated, since in a serializable class all fields have to be
  serializable regardless of the name.

#### Modified rules

* The rule [`ClassNamingConventions`](https://pmd.github.io/pmd-6.52.0/pmd_rules_java_codestyle.html#classnamingconventions) has a new property `testClassPattern`, which is applied
  to test classes. By default, test classes should end with the suffix "Test". Test classes are top-level classes, that
  either inherit from JUnit 3 TestCase or have at least one method annotated with the Test annotations from
  JUnit4/5 or TestNG.

* The property `ignoredAnnotations` of rule [`ImmutableField`](https://pmd.github.io/pmd-6.52.0/pmd_rules_java_design.html#immutablefield) has been deprecated  and doesn't
  have any effect anymore.
  Since PMD 6.47.0, the rule only considers fields, that are initialized once and never changed. If the field is just
  declared but never explicitly initialized, it won't be reported. That's the typical case when a framework sets
  the field value by reflection. Therefore, the property is not needed anymore. If there is a special case where
  this rule misidentifies fields as immutable, then the rule should be suppressed for these fields explicitly.

### Fixed Issues
* cli
    * [#4215](https://github.com/pmd/pmd/discussions/4215): NullPointerException when trying to open designer
* doc
    * [#4207](https://github.com/pmd/pmd/pull/4207): \[doc] List all languages in rule doc
* java
    * [#3643](https://github.com/pmd/pmd/issues/3643): \[java] More parser edge cases
    * [#4152](https://github.com/pmd/pmd/issues/4152): \[java] Parse error on array type annotations
* java-codestyle
    * [#2867](https://github.com/pmd/pmd/issues/2867): \[java] Separate pattern for test classes in ClassNamingConventions rule for Java
    * [#4201](https://github.com/pmd/pmd/issues/4201): \[java] CommentDefaultAccessModifier should consider lombok's @<!-- -->Value
* java-design
    * [#4175](https://github.com/pmd/pmd/issues/4175): \[java] ImmutableField - deprecate property `ignoredAnnotations`
    * [#4177](https://github.com/pmd/pmd/issues/4177): \[java] New Rule InvalidJavaBean
    * [#4188](https://github.com/pmd/pmd/issues/4188): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal false positive with Lombok's @<!-- -->NoArgsConstructor
    * [#4189](https://github.com/pmd/pmd/issues/4189): \[java] AbstractClassWithoutAnyMethod should consider lombok's @<!-- -->AllArgsConstructor
    * [#4200](https://github.com/pmd/pmd/issues/4200): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal should consider lombok's @<!-- -->Value
* java-errorprone
    * [#1668](https://github.com/pmd/pmd/issues/1668): \[java] BeanMembersShouldSerialize is extremely noisy
    * [#4172](https://github.com/pmd/pmd/issues/4172): \[java] InvalidLogMessageFormat false positive on externally formatted strings
    * [#4174](https://github.com/pmd/pmd/issues/4174): \[java] MissingStaticMethodInNonInstantiatableClass does not consider nested builder class
    * [#4176](https://github.com/pmd/pmd/issues/4176): \[java] Rename BeanMembersShouldSerialize to NonSerializableClass
    * [#4185](https://github.com/pmd/pmd/issues/4185): \[java] InvalidLogMessageFormat rule produces a NPE
    * [#4224](https://github.com/pmd/pmd/issues/4224): \[java] MissingStaticMethodInNonInstantiatableClass should consider Lombok's @<!-- -->UtilityClass
    * [#4225](https://github.com/pmd/pmd/issues/4225): \[java] MissingStaticMethodInNonInstantiatableClass should consider Lombok's @<!-- -->NoArgsConstructor
* java-performance
    * [#4183](https://github.com/pmd/pmd/issues/4183): \[java] AvoidArrayLoops regression: from false negative to false positive with final variables

### API Changes

#### PMD CLI

* PMD now supports a new `--use-version` flag, which receives a language-version pair (such as `java-8` or `apex-54`).
  This supersedes the usage of `-language` / `-l` and `-version` / `-v`, allowing for multiple versions to be set in a single run.
  PMD 7 will completely remove support for `-language` and `-version` in favor of this new flag.

* Support for `-V` is being deprecated in favor of `--verbose` in preparation for PMD 7.
  In PMD 7, `-v` will enable verbose mode and `-V` will show the PMD version for consistency with most Unix/Linux tools.

* Support for `-min` is being deprecated in favor of `--minimum-priority` for consistency with most Unix/Linux tools, where `-min` would be equivalent to `-m -i -n`.

#### CPD CLI

* CPD now supports using `-d` or `--dir` as an alias to `--files`, in favor of consistency with PMD.
  PMD 7 will remove support for `--files` in favor of these new flags.

#### Linux run.sh parameters

* Using `run.sh cpdgui` will now warn about it being deprecated. Use `run.sh cpd-gui` instead.

* The old designer (`run.sh designerold`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer`.

* The old visual AST viewer (`run.sh bgastviewer`) is completely deprecated and will be removed in PMD 7. Switch to the new JavaFX designer: `run.sh designer` for a visual tool, or use `run.sh ast-dump` for a text-based alternative.

#### Deprecated API

* The following core APIs have been marked as deprecated for removal in PMD 7:
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMD.StatusCode.html#"><code>PMD.StatusCode</code></a> - PMD 7 will ship with a revamped CLI split from pmd-core. To programmatically launch analysis you can use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PmdAnalysis.html#"><code>PmdAnalysis</code></a>.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getAllInputPaths()"><code>PMDConfiguration#getAllInputPaths</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getInputPathList()"><code>PMDConfiguration#getInputPathList</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPaths(List)"><code>PMDConfiguration#setInputPaths</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPathList(List)"><code>PMDConfiguration#setInputPathList</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#addInputPath(String)"><code>PMDConfiguration#addInputPath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#addInputPath(Path)"><code>PMDConfiguration#addInputPath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getInputFilePath()"><code>PMDConfiguration#getInputFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getInputFile()"><code>PMDConfiguration#getInputFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getIgnoreFilePath()"><code>PMDConfiguration#getIgnoreFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getIgnoreFile()"><code>PMDConfiguration#getIgnoreFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputFilePath(String)"><code>PMDConfiguration#setInputFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputFilePath(Path)"><code>PMDConfiguration#setInputFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setIgnoreFilePath(String)"><code>PMDConfiguration#setIgnoreFilePath</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setIgnoreFilePath(Path)"><code>PMDConfiguration#setIgnoreFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getInputUri()"><code>PMDConfiguration#getInputUri</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getUri()"><code>PMDConfiguration#getUri</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputUri(String)"><code>PMDConfiguration#setInputUri</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setInputUri(URI)"><code>PMDConfiguration#setInputUri</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getReportFile()"><code>PMDConfiguration#getReportFile</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#getReportFilePath()"><code>PMDConfiguration#getReportFilePath</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setReportFile(String)"><code>PMDConfiguration#setReportFile</code></a> - It is now superseded by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setReportFile(Path)"><code>PMDConfiguration#setReportFile</code></a>
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#isStressTest()"><code>PMDConfiguration#isStressTest</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setStressTest(boolean)"><code>PMDConfiguration#setStressTest</code></a> - Will be removed with no replacement.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#isBenchmark()"><code>PMDConfiguration#isBenchmark</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/PMDConfiguration.html#setBenchmark(boolean)"><code>PMDConfiguration#setBenchmark</code></a> - Will be removed with no replacement, the CLI will still support it.
    - <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/cpd/CPD.html#"><code>CPD</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.52.0/net/sourceforge/pmd/cpd/CPD.StatusCode.html#"><code>CPD.StatusCode</code></a> - PMD 7 will ship with a revamped CLI split from pmd-core. An alterative to programatically launch CPD analysis will be added in due time.

* In order to reduce the dependency on Apex Jorje classes, the method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.52.0/net/sourceforge/pmd/lang/vf/DataType.html#fromBasicType(apex.jorje.semantic.symbol.type.BasicType)"><code>DataType#fromBasicType</code></a>
  has been deprecated. The equivalent method <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.52.0/net/sourceforge/pmd/lang/vf/DataType.html#fromTypeName(java.lang.String)"><code>fromTypeName</code></a> should be used instead.

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)
* [#4198](https://github.com/pmd/pmd/pull/4198): \[doc] Add supported CPD languages - [Jeroen van Wilgenburg](https://github.com/jvwilge) (@jvwilge)
* [#4202](https://github.com/pmd/pmd/pull/4202): \[java] Fix #4200 and #4201: ClassWithOnlyPrivateConstructorsShouldBeFinal, CommentDefaultAccessModifier: Exclude lombok @<!-- -->Value annotation - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4205](https://github.com/pmd/pmd/pull/4205): \[doc] Clarify Scala support (no built-in rules) - [Eldrick Wega](https://github.com/Eldrick19) (@Eldrick19)
* [#4226](https://github.com/pmd/pmd/pull/4226): \[visualforce] Replace uses of Jorje types in pmd-visualforce - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4227](https://github.com/pmd/pmd/pull/4227): \[java] Fix #4225 MissingStaticMethodInNonInstantiatableClass: Exclude lombok's @<!-- -->NoArgsConstructor annotation - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4228](https://github.com/pmd/pmd/pull/4228): \[java] Fix #4224 MissingStaticMethodInNonInstantiatableClass: Exclude lombok's UtilityClass - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4232](https://github.com/pmd/pmd/pull/4232): \[doc] Fixing typos - [Andreas Deininger](https://github.com/deining) (@deining)

### Stats
* 96 commits
* 40 closed tickets & PRs
* Days since last release: 28

## 29-October-2022 - 6.51.0

The PMD team is pleased to announce PMD 6.51.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### New Rules
* The new Apex rule [`ApexUnitTestClassShouldHaveRunAs`](https://pmd.github.io/pmd-6.51.0/pmd_rules_apex_bestpractices.html#apexunittestclassshouldhaverunas) ensures that unit tests
  use [System.runAs()](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_testing_tools_runas.htm)
  at least once. This makes the tests more robust, and independent from the user running it.

```xml
<rule ref="category/apex/bestpractices.xml/ApexUnitTestClassShouldHaveRunAs"/>
```

The rule is part of the quickstart.xml ruleset.

#### Modified Rules

* The Java rule [`TestClassWithoutTestCases`](https://pmd.github.io/pmd-6.51.0/pmd_rules_java_errorprone.html#testclasswithouttestcases) has a new property `testClassPattern`. This is
  used to detect empty test classes by name. Previously this rule could only detect empty JUnit3 test cases
  properly. To switch back to the old behavior, this property can be set to an empty value which disables the
  test class detection by pattern.

### Fixed Issues
* apex
    * [#4149](https://github.com/pmd/pmd/issues/4149): \[apex] New rule: ApexUnitTestClassShouldHaveRunAs
* doc
    * [#4144](https://github.com/pmd/pmd/pull/4144): \[doc] Update docs to reflect supported languages
    * [#4163](https://github.com/pmd/pmd/issues/4163): \[doc] Broken links on page "Architecture Decisions"
* java-bestpractices
    * [#4140](https://github.com/pmd/pmd/issues/4140): \[java] \[doc] AccessorClassGeneration violations hidden with Java 11
* java-codestyle
    * [#4139](https://github.com/pmd/pmd/issues/4139): \[java] UnnecessaryFullyQualifiedName FP when the same simple class name exists in the current package
* java-documentation
    * [#4141](https://github.com/pmd/pmd/issues/4141): \[java] UncommentedEmptyConstructor FP when constructor annotated with @<!-- -->Autowired
* java-performance
    * [#1167](https://github.com/pmd/pmd/issues/1167): \[java] AvoidArrayLoops false positive on double assignment
    * [#2080](https://github.com/pmd/pmd/issues/2080): \[java] StringToString rule false-positive with field access
    * [#2692](https://github.com/pmd/pmd/issues/2692): \[java] \[doc] AvoidArrayLoops flags copy assignment in same array as sub-optimal
    * [#3437](https://github.com/pmd/pmd/issues/3437): \[java] StringToString doesn't trigger on Bar.class.getSimpleName().toString()
    * [#3681](https://github.com/pmd/pmd/issues/3681): \[java] StringToString doesn't trigger on string literals
    * [#3847](https://github.com/pmd/pmd/issues/3847): \[java] AvoidArrayLoops should consider final variables
    * [#3977](https://github.com/pmd/pmd/issues/3977): \[java] StringToString false-positive with local method name confusion
    * [#4091](https://github.com/pmd/pmd/issues/4091): \[java] AvoidArrayLoops false negative with do-while loops
    * [#4148](https://github.com/pmd/pmd/issues/4148): \[java] UseArrayListInsteadOfVector ignores Vector when other classes are imported
* java-errorprone
    * [#929](https://github.com/pmd/pmd/issues/929): \[java] Inconsistent results with TestClassWithoutTestCases
    * [#2636](https://github.com/pmd/pmd/issues/2636): \[java] TestClassWithoutTestCases false positive with JUnit5 ParameterizedTest
* javascript
    * [#4165](https://github.com/pmd/pmd/issues/4165): \[javascript] InaccurateNumericLiteral underscore separator notation false positive

### API Changes

No changes.

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4147](https://github.com/pmd/pmd/pull/4147): \[java] Added support for Do-While for AvoidArrayLoops - [Yasar Shaikh](https://github.com/yasarshaikh) (@yasarshaikh)
* [#4150](https://github.com/pmd/pmd/pull/4150): \[apex] New rule ApexUnitTestClassShouldHaveRunAs #4149 - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)

### Stats
* 63 commits
* 28 closed tickets & PRs
* Days since last release: 28


## 30-September-2022 - 6.50.0

The PMD team is pleased to announce PMD 6.50.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Lua now supports additionally Luau](#lua-now-supports-additionally-luau)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [CPD CLI](#cpd-cli)
* [Financial Contributions](#financial-contributions)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Lua now supports additionally Luau

This release of PMD adds support for [Luau](https://github.com/Roblox/luau), a gradually typed language derived
from Lua. This means, that the Lua language in PMD can now parse both Lua and Luau.

#### Modified rules

* The Java rule [`UnusedPrivateField`](https://pmd.github.io/pmd-6.50.0/pmd_rules_java_bestpractices.html#unusedprivatefield) now ignores private fields, if the fields are
  annotated with any annotation or the enclosing class has any annotation. Annotations often enable a
  framework (such as dependency injection, mocking or e.g. Lombok) which use the fields by reflection or other
  means. This usage can't be detected by static code analysis. Previously these frameworks where explicitly allowed
  by listing their annotations in the property "ignoredAnnotations", but that turned out to be prone of false
  positive for any not explicitly considered framework. That's why the property "ignoredAnnotations" has been
  deprecated for this rule.
* The Java rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.50.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) now by default ignores JUnit5 annotated
  methods. This behavior can be customized using the property `ignoredAnnotations`.

### Fixed Issues
* cli
    * [#4118](https://github.com/pmd/pmd/issues/4118): \[cli] run.sh designer reports "integer expression expected"
* core
    * [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Missing --file arg in TreeExport CLI example
* doc
    * [#4072](https://github.com/pmd/pmd/pull/4072): \[doc] Add architecture decision records
    * [#4109](https://github.com/pmd/pmd/pull/4109): \[doc] Add page for 3rd party rulesets
    * [#4124](https://github.com/pmd/pmd/pull/4124): \[doc] Fix typos in Java rule docs
* java
    * [#3431](https://github.com/pmd/pmd/issues/3431): \[java] Add sample java project to regression-tester which uses new language constructs
* java-bestpractices
    * [#4033](https://github.com/pmd/pmd/issues/4033): \[java] UnusedPrivateField - false positive with Lombok @ToString.Include
    * [#4037](https://github.com/pmd/pmd/issues/4037): \[java] UnusedPrivateField - false positive with Spring @SpyBean
* java-codestyle
    * [#3859](https://github.com/pmd/pmd/issues/3859): \[java] CommentDefaultAccessModifier is triggered in JUnit5 test class
    * [#4085](https://github.com/pmd/pmd/issues/4085): \[java] UnnecessaryFullyQualifiedName false positive when nested and non-nested classes with the same name and in the same package are used together
    * [#4133](https://github.com/pmd/pmd/issues/4133): \[java] UnnecessaryFullyQualifiedName - FP for inner class pkg.ClassA.Foo implementing pkg.Foo
* java-design
    * [#4090](https://github.com/pmd/pmd/issues/4090): \[java] FinalFieldCouldBeStatic false positive with non-static synchronized block (regression in 6.48, worked with 6.47)
* java-errorprone
    * [#1718](https://github.com/pmd/pmd/issues/1718): \[java] ConstructorCallsOverridableMethod false positive when calling super method
    * [#2348](https://github.com/pmd/pmd/issues/2348): \[java] ConstructorCallsOverridableMethod occurs when unused overloaded method is defined
    * [#4099](https://github.com/pmd/pmd/issues/4099): \[java] ConstructorCallsOverridableMethod should consider method calls with var access
* scala
    * [#4138](https://github.com/pmd/pmd/pull/4138): \[scala] Upgrade scala-library to 2.12.7 / 2.13.9 and scalameta to 4.6.0

### API Changes

#### CPD CLI

* CPD now supports the `--ignore-literal-sequences` argument when analyzing Lua code.

### Financial Contributions

Many thanks to our sponsors:

* [Oliver Siegmar](https://github.com/osiegmar) (@osiegmar)

### External Contributions
* [#4066](https://github.com/pmd/pmd/pull/4066): \[lua] Add support for Luau syntax and skipping literal sequences in CPD - [Matt Hargett](https://github.com/matthargett) (@matthargett)
* [#4100](https://github.com/pmd/pmd/pull/4100): \[java] Update UnusedPrivateFieldRule - ignore any annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4116](https://github.com/pmd/pmd/pull/4116): \[core] Fix missing --file arg in TreeExport CLI example - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4124](https://github.com/pmd/pmd/pull/4124): \[doc] Fix typos in Java rule docs - [Piotrek Żygieło](https://github.com/pzygielo) (@pzygielo)
* [#4128](https://github.com/pmd/pmd/pull/4128): \[java] Fix False-positive UnnecessaryFullyQualifiedName when nested and non-nest… #4103 - [Oleg Andreych](https://github.com/OlegAndreych) (@OlegAndreych)
* [#4130](https://github.com/pmd/pmd/pull/4130): \[ci] GitHub Workflows security hardening - [Alex](https://github.com/sashashura) (@sashashura)
* [#4131](https://github.com/pmd/pmd/pull/4131): \[doc] TooFewBranchesForASwitchStatement - Use "if-else" instead of "if-then" - [Suvashri](https://github.com/Suvashri) (@Suvashri)
* [#4137](https://github.com/pmd/pmd/pull/4137): \[java] Fixes 3859: Exclude junit5 test methods from the commentDefaultAccessModifierRule - [Luis Alcantar](https://github.com/lfalcantar) (@lfalcantar)

### Stats
* 100 commits
* 26 closed tickets & PRs
* Days since last release: 29

## 31-August-2022 - 6.49.0

The PMD team is pleased to announce PMD 6.49.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.49.0).

### Fixed Issues

* apex
    * [#4096](https://github.com/pmd/pmd/issues/4096): \[apex] ApexAssertionsShouldIncludeMessage and ApexUnitTestClassShouldHaveAsserts: support new Assert class (introduced with Apex v56.0)
* core
    * [#3970](https://github.com/pmd/pmd/issues/3970): \[core] FileCollector.addFile ignores language parameter
* java-codestyle
    * [#4082](https://github.com/pmd/pmd/issues/4082): \[java] UnnecessaryImport false positive for on-demand imports of nested classes

### API Changes

#### Deprecated API

* In order to reduce the dependency on Apex Jorje classes, the following methods have been deprecated.
  These methods all leaked internal Jorje enums. These enums have been replaced now by enums the
  PMD's AST package.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/ASTAssignmentExpression.html#getOperator()"><code>ASTAssignmentExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/ASTBinaryExpression.html#getOperator()"><code>ASTBinaryExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/ASTBooleanExpression.html#getOperator()"><code>ASTBooleanExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/ASTPostfixExpression.html#getOperator()"><code>ASTPostfixExpression#getOperator</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/ASTPrefixExpression.html#getOperator()"><code>ASTPrefixExpression#getOperator</code></a>

  All these classes have now a new `getOp()` method. Existing code should be refactored to use this method instead.
  It returns the new enums, like <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.49.0/net/sourceforge/pmd/lang/apex/ast/AssignmentOperator.html#"><code>AssignmentOperator</code></a>, and avoids
  the dependency to Jorje.

### External Contributions

* [#4081](https://github.com/pmd/pmd/pull/4081): \[apex] Remove Jorje leaks outside `ast` package - [@eklimo](https://github.com/eklimo)
* [#4083](https://github.com/pmd/pmd/pull/4083): \[java] UnnecessaryImport false positive for on-demand imports of nested classes (fix for #4082) - [@abyss638](https://github.com/abyss638)
* [#4092](https://github.com/pmd/pmd/pull/4092): \[apex] Implement ApexQualifiableNode for ASTUserEnum - [@aaronhurst-google](https://github.com/aaronhurst-google)
* [#4095](https://github.com/pmd/pmd/pull/4095): \[core] CPD: Added begin and end token to XML reports - [@pacvz](https://github.com/pacvz)
* [#4097](https://github.com/pmd/pmd/pull/4097): \[apex] ApexUnitTestClassShouldHaveAssertsRule: Support new Assert class (Apex v56.0) - [@tprouvot](https://github.com/tprouvot)
* [#4104](https://github.com/pmd/pmd/pull/4104): \[doc] Add MegaLinter in the list of integrations - [@nvuillam](https://github.com/nvuillam)

### Stats
* 49 commits
* 10 closed tickets & PRs
* Days since last release: 32

## 30-July-2022 - 6.48.0

The PMD team is pleased to announce PMD 6.48.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 19 Support](#java-19-support)
    * [Gherkin support](#gherkin-support)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [CPD CLI](#cpd-cli)
    * [Rule Test Framework](#rule-test-framework)
    * [Deprecated API](#deprecated-api)
    * [Experimental APIs](#experimental-apis)
    * [Internal API](#internal-api)
* [Financial Contributions](#financial-contributions)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Java 19 Support

This release of PMD brings support for Java 19. There are no new standard language features.

PMD supports [JEP 427: Pattern Matching for switch (Third Preview)](https://openjdk.org/jeps/427) and
[JEP 405: Record Patterns (Preview)](https://openjdk.org/jeps/405) as preview language features.

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `19-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 19-preview ...

Note: Support for Java 17 preview language features have been removed. The version "17-preview" is no longer available.

#### Gherkin support
Thanks to the contribution from [Anne Brouwers](https://github.com/ASBrouwers) PMD now has CPD support
for the [Gherkin](https://cucumber.io/docs/gherkin/) language. It is used to defined test cases for the
[Cucumber](https://cucumber.io/) testing tool for behavior-driven development.

Being based on a proper Antlr grammar, CPD can:

* ignore comments
* honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

### Fixed Issues
* apex
    * [#4056](https://github.com/pmd/pmd/pull/4056): \[apex] ApexSOQLInjection: Add support count query
* core
    * [#3796](https://github.com/pmd/pmd/issues/3796): \[core] CPD should also provide a `--debug` flag
    * [#4021](https://github.com/pmd/pmd/pull/4021): \[core] CPD: Add total number of tokens to XML reports
    * [#4031](https://github.com/pmd/pmd/issues/4031): \[core] If report is written to stdout, stdout should not be closed
    * [#4051](https://github.com/pmd/pmd/issues/4051): \[doc] Additional rulesets are not listed in documentation
    * [#4053](https://github.com/pmd/pmd/pull/4053): \[core] Allow building PMD under Java 18+
* java
    * [#4015](https://github.com/pmd/pmd/issues/4015): \[java] Support JDK 19
* java-bestpractices
    * [#3455](https://github.com/pmd/pmd/issues/3455): \[java] WhileLoopWithLiteralBoolean - false negative with complex expressions
* java-design
    * [#3729](https://github.com/pmd/pmd/issues/3729): \[java] TooManyMethods ignores "real" methods which are named like getters or setters
    * [#3949](https://github.com/pmd/pmd/issues/3949): \[java] FinalFieldCouldBeStatic - false negative with unnecessary parenthesis
* java-performance
    * [#3625](https://github.com/pmd/pmd/issues/3625): \[java] AddEmptyString - false negative with empty var
* lua
    * [#4061](https://github.com/pmd/pmd/pull/4061): \[lua] Fix several related Lua parsing issues found when using CPD
* test
    * [#3302](https://github.com/pmd/pmd/pull/3302): \[test] Improve xml test schema
    * [#3758](https://github.com/pmd/pmd/issues/3758): \[test] Move pmd-test to java 8
    * [#3976](https://github.com/pmd/pmd/pull/3976): \[test] Extract xml schema module

### API Changes

#### CPD CLI

* CPD has a new CLI option `--debug`. This option has the same behavior as in PMD. It enables more verbose
  logging output.

#### Rule Test Framework

* The module "pmd-test", which contains support classes to write rule tests, now **requires Java 8**. If you depend on
  this module for testing your own custom rules, you'll need to make sure to use at least Java 8.
* The new module "pmd-test-schema" contains now the XSD schema and the code to parse the rule test XML files. The
  schema has been extracted in order to easily share it with other tools like the Rule Designer or IDE plugins.
* Test schema changes:
    * The attribute `isRegressionTest` of `test-code` is deprecated. The new
      attribute `disabled` should be used instead for defining whether a rule test should be skipped or not.
    * The attributes `reinitializeRule` and `useAuxClasspath` of `test-code` are deprecated and assumed true.
      They will not be replaced.
    * The new attribute `focused` of `test-code` allows disabling all tests except the focused one temporarily.
* More information about the rule test framework can be found in the documentation:
  [Testing your rules](pmd_userdocs_extending_testing.html)

#### Deprecated API

* The experimental Java AST class <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.48.0/net/sourceforge/pmd/lang/java/ast/ASTGuardedPattern.html#"><code>ASTGuardedPattern</code></a> has been deprecated and
  will be removed. It was introduced for Java 17 and Java 18 Preview as part of pattern matching for switch,
  but it is no longer supported with Java 19 Preview.
* The interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/renderer/CPDRenderer.html#"><code>CPDRenderer</code></a> is deprecated. For custom CPD renderers
  the new interface <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/renderer/CPDReportRenderer.html#"><code>CPDReportRenderer</code></a> should be used.
* The class <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.48.0/net/sourceforge/pmd/testframework/TestDescriptor.html#"><code>TestDescriptor</code></a> is deprecated, replaced with <a href="https://docs.pmd-code.org/apidocs/pmd-test-schema/6.48.0/net/sourceforge/pmd/test/schema/RuleTestDescriptor.html#"><code>RuleTestDescriptor</code></a>.
* Many methods of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.48.0/net/sourceforge/pmd/testframework/RuleTst.html#"><code>RuleTst</code></a> have been deprecated as internal API.

#### Experimental APIs

* To support the Java preview language features "Pattern Matching for Switch" and "Record Patterns", the following
  AST nodes have been introduced as experimental:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.48.0/net/sourceforge/pmd/lang/java/ast/ASTSwitchGuard.html#"><code>ASTSwitchGuard</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.48.0/net/sourceforge/pmd/lang/java/ast/ASTRecordPattern.html#"><code>ASTRecordPattern</code></a>
    * <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.48.0/net/sourceforge/pmd/lang/java/ast/ASTComponentPatternList.html#"><code>ASTComponentPatternList</code></a>

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setRenderer(net.sourceforge.pmd.cpd.Renderer)"><code>CPDConfiguration#setRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#setCPDRenderer(net.sourceforge.pmd.cpd.renderer.CPDRenderer)"><code>CPDConfiguration#setCPDRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getRenderer()"><code>CPDConfiguration#getRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getCPDRenderer()"><code>CPDConfiguration#getCPDRenderer</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getRendererFromString(java.lang.String,java.lang.String)"><code>CPDConfiguration#getRendererFromString</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/CPDConfiguration.html#getCPDRendererFromString(java.lang.String,java.lang.String)"><code>CPDConfiguration#getCPDRendererFromString</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.48.0/net/sourceforge/pmd/cpd/renderer/CPDRendererAdapter.html#"><code>CPDRendererAdapter</code></a>

### Financial Contributions

Many thanks to our sponsors:

* [Matt Hargett](https://github.com/matthargett) (@matthargett)

### External Contributions
* [#3984](https://github.com/pmd/pmd/pull/3984): \[java] Fix AddEmptyString false-negative issue - [@LiGaOg](https://github.com/LiGaOg)
* [#3988](https://github.com/pmd/pmd/pull/3988): \[java] Modify WhileLoopWithLiteralBoolean to meet the missing case #3455 - [@VoidxHoshi](https://github.com/VoidxHoshi)
* [#3992](https://github.com/pmd/pmd/pull/3992): \[java] FinalFieldCouldBeStatic - fix false negative with unnecessary parenthesis - [@dalizi007](https://github.com/dalizi007)
* [#3994](https://github.com/pmd/pmd/pull/3994): \[java] TooManyMethods - improve getter/setter detection (#3729) - [@341816041](https://github.com/341816041)
* [#4017](https://github.com/pmd/pmd/pull/4017): Add Gherkin support to CPD - [@ASBrouwers](https://github.com/ASBrouwers)
* [#4021](https://github.com/pmd/pmd/pull/4021): \[core] CPD: Add total number of tokens to XML reports - [@maikelsteneker](https://github.com/maikelsteneker)
* [#4056](https://github.com/pmd/pmd/pull/4056): \[apex] ApexSOQLInjection: Add support count query - [@gwilymatgearset](https://github.com/gwilymatgearset)
* [#4061](https://github.com/pmd/pmd/pull/4061): \[lua] Fix several related Lua parsing issues found when using CPD - [@matthargett](https://github.com/matthargett)

### Stats
* 102 commits
* 26 closed tickets & PRs
* Days since last release: 35

## 25-June-2022 - 6.47.0

The PMD team is pleased to announce PMD 6.47.0.

This is a minor release.

### Table Of Contents

* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### Fixed Issues
* core
    * [#3999](https://github.com/pmd/pmd/issues/3999): \[cli] All files are analyzed despite parameter `--file-list`
    * [#4009](https://github.com/pmd/pmd/issues/4009): \[core] Cannot build PMD with Temurin 17
* java-bestpractices
    * [#3824](https://github.com/pmd/pmd/issues/3824): \[java] UnusedPrivateField: Do not flag fields annotated with @<!-- -->Version
    * [#3825](https://github.com/pmd/pmd/issues/3825): \[java] UnusedPrivateField: Do not flag fields annotated with @<!-- -->Id or @<!-- -->EmbeddedId
* java-design
    * [#3823](https://github.com/pmd/pmd/issues/3823): \[java] ImmutableField: Do not flag fields in @<!-- -->Entity
    * [#3981](https://github.com/pmd/pmd/issues/3981): \[java] ImmutableField reports fields annotated with @<!-- -->Value (Spring)
    * [#3998](https://github.com/pmd/pmd/issues/3998): \[java] ImmutableField reports fields annotated with @<!-- -->Captor (Mockito)
    * [#4004](https://github.com/pmd/pmd/issues/4004): \[java] ImmutableField reports fields annotated with @<!-- -->GwtMock (GwtMockito) and @<!-- -->Spy (Mockito)
    * [#4008](https://github.com/pmd/pmd/issues/4008): \[java] ImmutableField not reporting fields that are only initialized in the declaration
    * [#4011](https://github.com/pmd/pmd/issues/4011): \[java] ImmutableField: Do not flag fields annotated with @<!-- -->Inject
    * [#4020](https://github.com/pmd/pmd/issues/4020): \[java] ImmutableField reports fields annotated with @<!-- -->FindBy and @<!-- -->FindBys (Selenium)
* java-errorprone
    * [#3936](https://github.com/pmd/pmd/issues/3936): \[java] AvoidFieldNameMatchingMethodName should consider enum class
    * [#3937](https://github.com/pmd/pmd/issues/3937): \[java] AvoidDuplicateLiterals - uncompilable test cases

### API Changes

No changes.

### External Contributions
* [#3985](https://github.com/pmd/pmd/pull/3985): \[java] Fix false negative problem about Enum in AvoidFieldNameMatchingMethodName #3936 - [@Scrsloota](https://github.com/Scrsloota)
* [#3993](https://github.com/pmd/pmd/pull/3993): \[java] AvoidDuplicateLiterals - Add the method "buz" definition to test cases - [@dalizi007](https://github.com/dalizi007)
* [#4002](https://github.com/pmd/pmd/pull/4002): \[java] ImmutableField - Ignore fields annotated with @<!-- -->Value (Spring) or @<!-- -->Captor (Mockito) - [@jjlharrison](https://github.com/jjlharrison)
* [#4003](https://github.com/pmd/pmd/pull/4003): \[java] UnusedPrivateField - Ignore fields annotated with @<!-- -->Id/@<!-- -->EmbeddedId/@<!-- -->Version (JPA) or @<!-- -->Mock/@<!-- -->Spy/@<!-- -->MockBean (Mockito/Spring) - [@jjlharrison](https://github.com/jjlharrison)
* [#4006](https://github.com/pmd/pmd/pull/4006): \[doc] Fix eclipse plugin update site URL - [@shiomiyan](https://github.com/shiomiyan)
* [#4010](https://github.com/pmd/pmd/pull/4010): \[core] Bump kotlin to version 1.7.0 - [@maikelsteneker](https://github.com/maikelsteneker)

### Stats
* 45 commits
* 23 closed tickets & PRs
* Days since last release: 27

## 28-May-2022 - 6.46.0

The PMD team is pleased to announce PMD 6.46.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [CLI improvements](#cli-improvements)
    * [C# Improvements](#c#-improvements)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated ruleset references](#deprecated-ruleset-references)
    * [Deprecated API](#deprecated-api)
    * [Internal API](#internal-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### CLI improvements

The PMD CLI now allows repeating the `--dir` (`-d`) and `--rulesets` (`-R`) options,
as well as providing several space-separated arguments to either of them. For instance:
```shell
pmd -d src/main/java src/test/java -R rset1.xml -R rset2.xml
```
This also allows globs to be used on the CLI if your shell supports shell expansion.
For instance, the above can be written
```shell
pmd -d src/*/java -R rset*.xml
```
Please use theses new forms instead of using comma-separated lists as argument to these options.

#### C# Improvements

When executing CPD on C# sources, the option `--ignore-annotations` is now supported as well.
It ignores C# attributes when detecting duplicated code. This option can also be enabled via
the CPD GUI. See [#3974](https://github.com/pmd/pmd/pull/3974) for details.

#### New Rules

This release ships with 2 new Java rules.

* [`EmptyControlStatement`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_codestyle.html#emptycontrolstatement) reports many instances of empty things, e.g. control statements whose
  body is empty, as well as empty initializers.

  EmptyControlStatement also works for empty `for` and `do` loops, while there were previously
  no corresponding rules.

  This new rule replaces the rules EmptyFinallyBlock, EmptyIfStmt, EmptyInitializer, EmptyStatementBlock,
  EmptySwitchStatements, EmptySynchronizedBlock, EmptyTryBlock, and EmptyWhileStmt.

```xml
<rule ref="category/java/codestyle.xml/EmptyControlStatement"/>
```

The rule is part of the quickstart.xml ruleset.

* [`UnnecessarySemicolon`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_codestyle.html#unnecessarysemicolon) reports semicolons that are unnecessary  (so called "empty statements"
  and "empty declarations").

  This new rule replaces the rule EmptyStatementNotInLoop.

```xml
<rule ref="category/java/codestyle.xml/UnnecessarySemicolon"/>
```

The rule is part of the quickstart.xml ruleset.

#### Deprecated Rules

* The following Java rules are deprecated and removed from the quickstart ruleset, as the new rule
  [`EmptyControlStatement`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_codestyle.html#emptycontrolstatement) merges their functionality:
    * [`EmptyFinallyBlock`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptyfinallyblock)
    * [`EmptyIfStmt`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptyifstmt)
    * [`EmptyInitializer`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptyinitializer)
    * [`EmptyStatementBlock`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptystatementblock)
    * [`EmptySwitchStatements`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptyswitchstatements)
    * [`EmptySynchronizedBlock`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptysynchronizedblock)
    * [`EmptyTryBlock`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptytryblock)
    * [`EmptyWhileStmt`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptywhilestmt)
* The Java rule [`EmptyStatementNotInLoop`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_errorprone.html#emptystatementnotinloop) is deprecated and removed from the quickstart
  ruleset. Use the new rule [`UnnecessarySemicolon`](https://pmd.github.io/pmd-6.46.0/pmd_rules_java_codestyle.html#unnecessarysemicolon) instead.

### Fixed Issues

* cli
    * [#1445](https://github.com/pmd/pmd/issues/1445): \[core] Allow CLI to take globs as parameters
* core
    * [#2352](https://github.com/pmd/pmd/issues/2352): \[core] Deprecate \<lang\>-\<ruleset\> hyphen notation for ruleset references
    * [#3787](https://github.com/pmd/pmd/issues/3787): \[core] Internalize some methods in Ant Formatter
    * [#3835](https://github.com/pmd/pmd/issues/3835): \[core] Deprecate system properties of CPDCommandLineInterface
    * [#3942](https://github.com/pmd/pmd/issues/3942): \[core] common-io path traversal vulnerability (CVE-2021-29425)
* cs (c#)
    * [#3974](https://github.com/pmd/pmd/pull/3974): \[cs] Add option to ignore C# attributes (annotations)
* go
    * [#2752](https://github.com/pmd/pmd/issues/2752): \[go] Error parsing unicode values
* html
    * [#3955](https://github.com/pmd/pmd/pull/3955): \[html] Improvements for handling text and comment nodes
    * [#3978](https://github.com/pmd/pmd/pull/3978): \[html] Add additional file extensions htm, xhtml, xht, shtml
* java
    * [#3423](https://github.com/pmd/pmd/issues/3423): \[java] Error processing identifiers with Unicode
* java-bestpractices
    * [#3954](https://github.com/pmd/pmd/issues/3954): \[java] NPE in UseCollectionIsEmptyRule when .size() is called in a record
* java-design
    * [#3874](https://github.com/pmd/pmd/issues/3874): \[java] ImmutableField reports fields annotated with @Autowired (Spring) and @Mock (Mockito)
* java-errorprone
    * [#3096](https://github.com/pmd/pmd/issues/3096): \[java] EmptyStatementNotInLoop FP in 6.30.0 with IfStatement
* java-performance
    * [#3379](https://github.com/pmd/pmd/issues/3379): \[java] UseArraysAsList must ignore primitive arrays
    * [#3965](https://github.com/pmd/pmd/issues/3965): \[java] UseArraysAsList false positive with non-trivial loops
* javascript
    * [#2605](https://github.com/pmd/pmd/issues/2605): \[js] Support unicode characters
    * [#3948](https://github.com/pmd/pmd/issues/3948): \[js] Invalid operator error for method property in object literal
* python
    * [#2604](https://github.com/pmd/pmd/issues/2604): \[python] Support unicode identifiers

### API Changes

#### Deprecated ruleset references

Ruleset references with the following formats are now deprecated and will produce a warning
when used on the CLI or in a ruleset XML file:
- `<lang-name>-<ruleset-name>`, eg `java-basic`, which resolves to `rulesets/java/basic.xml`
- the internal release number, eg `600`, which resolves to `rulesets/releases/600.xml`

Use the explicit forms of these references to be compatible with PMD 7.

#### Deprecated API

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/RuleSetReferenceId.html#toString()"><code>toString</code></a> is now deprecated. The format of this
  method will remain the same until PMD 7. The deprecation is intended to steer users
  away from relying on this format, as it may be changed in PMD 7.
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/PMDConfiguration.html#getInputPaths()"><code>getInputPaths</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/PMDConfiguration.html#setInputPaths(java.lang.String)"><code>setInputPaths</code></a> are now deprecated.
  A new set of methods have been added, which use lists and do not rely on comma splitting.

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/cpd/CPDCommandLineInterface.html#"><code>CPDCommandLineInterface</code></a> has been internalized. In order to execute CPD either
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/cpd/CPD.html#run(java.lang.String...)"><code>CPD#run</code></a> or <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/cpd/CPD.html#main(java.lang.String[])"><code>CPD#main</code></a>
  should be used.
- Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.46.0/net/sourceforge/pmd/cli/BaseCPDCLITest.html#"><code>BaseCPDCLITest</code></a> have been deprecated with replacements.
- The methods <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/ant/Formatter.html#start(java.lang.String)"><code>Formatter#start</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/ant/Formatter.html#end(net.sourceforge.pmd.Report)"><code>Formatter#end</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/ant/Formatter.html#getRenderer()"><code>Formatter#getRenderer</code></a>,
  and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.46.0/net/sourceforge/pmd/ant/Formatter.html#isNoOutputSupplied()"><code>Formatter#isNoOutputSupplied</code></a> have been internalized.

### External Contributions

* [#3961](https://github.com/pmd/pmd/pull/3961): \[java] Fix #3954 - NPE in UseCollectionIsEmptyRule with record - [@flyhard](https://github.com/flyhard)
* [#3964](https://github.com/pmd/pmd/pull/3964): \[java] Fix #3874 - ImmutableField: fix mockito/spring false positives - [@lukelukes](https://github.com/lukelukes)
* [#3974](https://github.com/pmd/pmd/pull/3974): \[cs] Add option to ignore C# attributes (annotations) - [@maikelsteneker](https://github.com/maikelsteneker)

### Stats
* 92 commits
* 30 closed tickets & PRs
* Days since last release: 28

## 30-April-2022 - 6.45.0

The PMD team is pleased to announce PMD 6.45.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [PMD User Survey](#pmd-user-survey)
    * [Support for HTML](#support-for-html)
    * [New rules](#new-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### PMD User Survey

Help shape the future of PMD by telling us how you use it.

Our little survey is still open in case you didn't participate yet.
Please participate in our survey at <https://forms.gle/4d8r1a1RDzfixHDc7>.

Thank you!

#### Support for HTML

This version of PMD ships a new language module to support analyzing of HTML.
Support for HTML is experimental and might change without notice.
The language implementation is not complete yet and the AST doesn't look
well for text nodes and comment nodes and might be changed in the future.
You can write your own rules, but we don't guarantee that the rules work with
the next (minor) version of PMD without adjustments.

Please give us feedback about how practical this new language is in
[discussions](https://github.com/pmd/pmd/discussions). Please report
missing features or bugs as new [issues](https://github.com/pmd/pmd/issues).

#### New rules

* The HTML rule [`AvoidInlineStyles`](https://pmd.github.io/pmd-6.45.0/pmd_rules_html_bestpractices.html#avoidinlinestyles) finds elements which use a style attribute.
  In order to help maintaining a webpage it is considered good practice to separate content and styles. Instead
  of inline styles one should use CSS files and classes.

```xml
    <rule ref="category/html/bestpractices.xml/AvoidInlineStyles" />
```

* The HTML rule [`UnnecessaryTypeAttribute`](https://pmd.github.io/pmd-6.45.0/pmd_rules_html_bestpractices.html#unnecessarytypeattribute) finds "link" and "script" elements which
  still have a "type" attribute. This is not necessary anymore since modern browsers automatically use CSS and
  JavaScript.

```xml
      <rule ref="category/html/bestpractices.xml/UnnecessaryTypeAttribute" />
```

* The HTML rule [`UseAltAttributeForImages`](https://pmd.github.io/pmd-6.45.0/pmd_rules_html_bestpractices.html#usealtattributeforimages) finds "img" elements without an "alt"
  attribute. An alternate text should always be provided in order to help screen readers.

```xml
      <rule ref="category/html/bestpractices.xml/UseAltAttributeForImages" />
```

#### Modified rules

*   The Java rule [`UnusedPrivateField`](https://pmd.github.io/pmd-6.45.0/pmd_rules_java_bestpractices.html#unusedprivatefield) has a new property `ignoredFieldNames`.
    The default ignores serialization-specific fields (eg `serialVersionUID`).
    The property can be used to ignore more fields based on their name.
    Note that the rule used to ignore fields named `IDENT`, but doesn't anymore (add this value to the property to restore the old behaviour).

### Fixed Issues
* core
    * [#3792](https://github.com/pmd/pmd/issues/3792): \[core] Allow to filter violations in Report
    * [#3881](https://github.com/pmd/pmd/issues/3881): \[core] SARIF renderer depends on platform default encoding
    * [#3882](https://github.com/pmd/pmd/pull/3882): \[core] Fix AssertionError about exhaustive switch
    * [#3884](https://github.com/pmd/pmd/issues/3884): \[core] XML report via ant task contains XML header twice
    * [#3896](https://github.com/pmd/pmd/pull/3896): \[core] Fix ast-dump CLI when reading from stdin
* doc
    * [#2505](https://github.com/pmd/pmd/issues/2505): \[doc] Improve side bar to show release date
* java
    * [#3068](https://github.com/pmd/pmd/issues/3068): \[java] Some tests should not depend on real rules
    * [#3889](https://github.com/pmd/pmd/pull/3889): \[java] Catch LinkageError in UselessOverridingMethodRule
* java-bestpractices
    * [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable
    * [#1185](https://github.com/pmd/pmd/issues/1185): \[java] ArrayIsStoredDirectly false positive with field access
    * [#1474](https://github.com/pmd/pmd/issues/1474): \[java] ArrayIsStoredDirectly false positive with method call
    * [#3879](https://github.com/pmd/pmd/issues/3879) \[java] ArrayIsStoredDirectly reports duplicated violation
    * [#3929](https://github.com/pmd/pmd/issues/3929): \[java] ArrayIsStoredDirectly should report the assignment rather than formal parameter
* java-design
    * [#3603](https://github.com/pmd/pmd/issues/3603): \[java] SimplifiedTernary: no violation for 'condition ? true : false' case
* java-performance
    * [#3867](https://github.com/pmd/pmd/issues/3867): \[java] UseArraysAsList with method call
* plsql
    * [#3687](https://github.com/pmd/pmd/issues/3687): \[plsql] Parsing exception EXECUTE IMMEDIATE l_sql BULK COLLECT INTO statement
    * [#3706](https://github.com/pmd/pmd/issues/3706): \[plsql] Parsing exception CURSOR statement with parenthesis groupings

### API Changes

#### Experimental APIs

* Report has two new methods which allow limited mutations of a given report:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.45.0/net/sourceforge/pmd/Report.html#filterViolations(net.sourceforge.pmd.util.Predicate)"><code>Report#filterViolations</code></a> creates a new report with
      some violations removed with a given predicate based filter.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.45.0/net/sourceforge/pmd/Report.html#union(net.sourceforge.pmd.Report)"><code>Report#union</code></a> can combine two reports into a single new Report.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.45.0/net/sourceforge/pmd/util/Predicate.html#"><code>net.sourceforge.pmd.util.Predicate</code></a> will be replaced in PMD7 with the standard Predicate interface from java8.
* The module `pmd-html` is entirely experimental right now. Anything in the package
  `net.sourceforge.pmd.lang.html` should be used cautiously.

### External Contributions
* [#3883](https://github.com/pmd/pmd/pull/3883): \[doc] Improve side bar by Adding Release Date - [@jasonqiu98](https://github.com/jasonqiu98)
* [#3910](https://github.com/pmd/pmd/pull/3910): \[java] UnusedPrivateField - Allow the ignored fieldnames to be configurable - [@laoseth](https://github.com/laoseth)
* [#3928](https://github.com/pmd/pmd/pull/3928): \[plsql] Fix plsql parsing error in parenthesis groups - [@LiGaOg](https://github.com/LiGaOg)
* [#3935](https://github.com/pmd/pmd/pull/3935): \[plsql] Fix parser exception in EXECUTE IMMEDIATE BULK COLLECT #3687 - [@Scrsloota](https://github.com/Scrsloota)
* [#3938](https://github.com/pmd/pmd/pull/3938): \[java] Modify SimplifiedTernary to meet the missing case #3603 - [@VoidxHoshi](https://github.com/VoidxHoshi)
* [#3943](https://github.com/pmd/pmd/pull/3943): chore: Set permissions for GitHub actions - [@naveensrinivasan](https://github.com/naveensrinivasan)

### Stats
* 97 commits
* 31 closed tickets & PRs
* Days since last release: 33

## 27-March-2022 - 6.44.0

The PMD team is pleased to announce PMD 6.44.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [PMD User Survey](#pmd-user-survey)
    * [Java 18 Support](#java-18-support)
    * [Better XML XPath support](#better-xml-xpath-support)
    * [New XPath functions](#new-xpath-functions)
    * [New programmatic API](#new-programmatic-api)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
    * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### PMD User Survey

Help shape the future of PMD by telling us how you use it.

Please participate in our survey at <https://forms.gle/4d8r1a1RDzfixHDc7>.

Thank you!

#### Java 18 Support

This release of PMD brings support for Java 18. There are no new standard language features.

PMD also supports [JEP 420: Pattern Matching for switch (Second Preview)](https://openjdk.java.net/jeps/420) as a
preview language feature. In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `18-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 18-preview ...

Note: Support for Java 16 preview language features have been removed. The version "16-preview" is no longer available.

#### Better XML XPath support

The new rule class <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.44.0/net/sourceforge/pmd/lang/xml/rule/DomXPathRule.html#"><code>DomXPathRule</code></a> is intended to replace
usage of the `XPathRule` for XML rules. This rule executes the XPath query in a different
way, which sticks to the XPath specification. This means the expression is interpreted
the same way in PMD as in all other XPath development tools that stick to the standard.
You can for instance test the expression in an online XPath editor.

Prefer using this class to define XPath rules: replace the value of the `class`
attribute with `net.sourceforge.pmd.lang.xml.rule.DomXPathRule` like so:
```xml
<rule name="MyXPathRule"
      language="xml"
      message="A message"
      class="net.sourceforge.pmd.lang.xml.rule.DomXPathRule">

      <properties>
        <property name="xpath">
            <value><![CDATA[
            /a/b/c[@attr = "5"]
            ]]></value>
        </property>
        <!-- Note: the property "version" is ignored, remove it. The query is XPath 2. -->
      </properties>
</rule>
```

The rule is more powerful than `XPathRule`, as it can now handle XML namespaces,
comments and processing instructions. Please refer to the Javadoc of <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.44.0/net/sourceforge/pmd/lang/xml/rule/DomXPathRule.html#"><code>DomXPathRule</code></a>
for information about the differences with `XPathRule` and examples.

`XPathRule` is still perfectly supported for all other languages, including Apex and Java.

#### New XPath functions

The new XPath functions `pmd:startLine`, `pmd:endLine`, `pmd:startColumn`,
and `pmd:endColumn` are now available in XPath rules for all languages. They
replace the node attributes `@BeginLine`, `@EndLine` and such. These attributes
will be deprecated in a future release.

Please refer to [the documentation](https://pmd.github.io/latest/pmd_userdocs_extending_writing_xpath_rules.html#pmd-extension-functions) of these functions for more information, including usage samples.

Note that the function `pmd:endColumn` returns an exclusive index, while the
attribute `@EndColumn` is inclusive. This is for forward compatibility with PMD 7,
which uses exclusive end indices.

#### New programmatic API

This release introduces a new programmatic API to replace the inflexible <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> class.
Programmatic execution of PMD should now be done with a <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#"><code>PMDConfiguration</code></a>
and a <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PmdAnalysis.html#"><code>PmdAnalysis</code></a>, for instance:

```java
PMDConfiguration config = new PMDConfiguration();
config.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
config.setInputPaths("src/main/java");
config.prependAuxClasspath("target/classes");
config.setMinimumPriority(RulePriority.HIGH);
config.addRuleSet("rulesets/java/quickstart.xml");
config.setReportFormat("xml");
config.setReportFile("target/pmd-report.xml");

try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
    // note: don't use `config` once a PmdAnalysis has been created.
    // optional: add more rulesets
    pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
    // optional: add more files
    pmd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
    // optional: add more renderers
    pmd.addRenderer(renderer);

    // or just call PMD
    pmd.performAnalysis();
}
```

The `PMD` class still supports methods related to CLI execution: `runPmd` and `main`.
All other members are now deprecated for removal.
The CLI itself remains compatible, if you run PMD via command-line, no action is required on your part.

### Fixed Issues

*   apex
    *   [#3817](https://github.com/pmd/pmd/pull/3817): \[apex] Add designer bindings to display main attributes
*   apex-performance
    *   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe()
*   core
    *   [#2693](https://github.com/pmd/pmd/issues/2693): \[ci] Add integration tests with real open-source projects
    *   [#3299](https://github.com/pmd/pmd/issues/3299): \[core] Deprecate system properties of PMDCommandLineInterface
*   java
    *   [#3809](https://github.com/pmd/pmd/issues/3809): \[java] Support JDK 18
*   doc
    *   [#2504](https://github.com/pmd/pmd/issues/2504): \[doc] Improve "Edit me on github" button
    *   [#3812](https://github.com/pmd/pmd/issues/3812): \[doc] Documentation website table of contents broken on pages with many subheadings
*   java-design
    *   [#3850](https://github.com/pmd/pmd/issues/3850): \[java] ImmutableField - false negative when field assigned in constructor conditionally
    *   [#3851](https://github.com/pmd/pmd/issues/3851): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - false negative when a compilation unit contains two class declarations
*   xml
    *   [#2766](https://github.com/pmd/pmd/issues/2766): \[xml] XMLNS prefix is not pre-declared in xpath query
    *   [#3863](https://github.com/pmd/pmd/issues/3863): \[xml] Make XPath rules work exactly as in the XPath spec

### API Changes

#### Deprecated API

* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> have been newly deprecated, including:
    - `PMD#EOL`: use `System#lineSeparator()`
    - `PMD#SUPPRESS_MARKER`: use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#DEFAULT_SUPPRESS_MARKER"><code>DEFAULT_SUPPRESS_MARKER</code></a>
    - `PMD#processFiles`: use the [new programmatic API](#new-programmatic-api)
    - `PMD#getApplicableFiles`: is internal
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#prependClasspath(java.lang.String)"><code>PMDConfiguration#prependClasspath</code></a> is deprecated
  in favour of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#prependAuxClasspath(java.lang.String)"><code>prependAuxClasspath</code></a>.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#setRuleSets(java.lang.String)"><code>PMDConfiguration#setRuleSets</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#getRuleSets()"><code>getRuleSets</code></a> are deprecated. Use instead
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#setRuleSets(java.util.List)"><code>setRuleSets</code></a>,
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#addRuleSet(java.lang.String)"><code>addRuleSet</code></a>,
  and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PMDConfiguration.html#getRuleSetPaths()"><code>getRuleSetPaths</code></a>.
* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.44.0/net/sourceforge/pmd/cli/BaseCLITest.html#"><code>BaseCLITest</code></a> have been deprecated with replacements.
* Several members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/cli/PMDCommandLineInterface.html#"><code>PMDCommandLineInterface</code></a> have been explicitly deprecated.
  The whole class however was deprecated long ago already with 6.30.0. It is internal API and should
  not be used.

* In modelica, the rule classes <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.44.0/net/sourceforge/pmd/lang/modelica/rule/AmbiguousResolutionRule.html#"><code>AmbiguousResolutionRule</code></a>
  and <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.44.0/net/sourceforge/pmd/lang/modelica/rule/ConnectUsingNonConnector.html#"><code>ConnectUsingNonConnector</code></a> have been deprecated,
  since they didn't comply to the usual rule class naming conventions yet.
  The replacements are in the subpackage `bestpractices`.

#### Experimental APIs

*   Together with the [new programmatic API](#new-programmatic-api) the interface
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/lang/document/TextFile.html#"><code>TextFile</code></a> has been added as *experimental*. It intends
    to replace <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/util/datasource/DataSource.html#"><code>DataSource</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/cpd/SourceCode.html#"><code>SourceCode</code></a> in the long term.

    This interface will change in PMD 7 to support read/write operations
    and other things. You don't need to use it in PMD 6, as <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/lang/document/FileCollector.html#"><code>FileCollector</code></a>
    decouples you from this. A file collector is available through <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.44.0/net/sourceforge/pmd/PmdAnalysis.html#files()"><code>PmdAnalysis#files</code></a>.

### External Contributions

*   [#3773](https://github.com/pmd/pmd/pull/3773): \[apex] EagerlyLoadedDescribeSObjectResult false positives with SObjectField.getDescribe() - [@filiprafalowicz](https://github.com/filiprafalowicz)
*   [#3811](https://github.com/pmd/pmd/pull/3811): \[doc] Improve "Edit me on github" button - [@btjiong](https://github.com/btjiong)
*   [#3836](https://github.com/pmd/pmd/pull/3836): \[doc] Make TOC scrollable when too many subheadings - [@JerritEic](https://github.com/JerritEic)

### Stats
* 124 commits
* 23 closed tickets & PRs
* Days since last release: 29

## 26-February-2022 - 6.43.0

The PMD team is pleased to announce PMD 6.43.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
    * [Internal API](#internal-api)
    * [Changed API](#changed-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

### Fixed Issues

*   core
    *   [#3427](https://github.com/pmd/pmd/issues/3427): \[core] Stop printing CLI usage text when exiting due to invalid parameters
    *   [#3768](https://github.com/pmd/pmd/issues/3768): \[core] SARIF formatter reports multiple locations when it should report multiple results
*   doc
    *   [#2502](https://github.com/pmd/pmd/issues/2502): \[doc] Add floating table-of-contents (toc) on the right
    *   [#3807](https://github.com/pmd/pmd/pull/3807): \[doc] Document Ant Task parameter `threads`
*   java
    *   [#3698](https://github.com/pmd/pmd/issues/3697): \[java] Parsing error with try-with-resources and qualified resource
*   java-bestpractices
    *   [#3605](https://github.com/pmd/pmd/issues/3605): \[java] SwitchStmtsShouldHaveDefault triggered when default case is present
*   java-codestyle
    *   [#278](https://github.com/pmd/pmd/issues/278): \[java] ConfusingTernary should treat `!= null` as positive condition
*   java-performance
    *   [#3374](https://github.com/pmd/pmd/issues/3374): \[java] UseStringBufferForStringAppends: Wrong example in documentation
*   misc
    *   [#3759](https://github.com/pmd/pmd/issues/3759): \[lang-test] Upgrade dokka maven plugin to 1.4.32
*   plsql
    *   [#3746](https://github.com/pmd/pmd/issues/3746): \[plsql] Parsing exception "Less than or equal to/Greater than or equal to" operators in DML statements

### API Changes

#### Deprecated API

Some API deprecations were performed in core PMD classes, to improve compatibility with PMD 7.
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/Report.html#"><code>Report</code></a>: the constructor and other construction methods like addViolation or createReport
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/RuleContext.html#"><code>RuleContext</code></a>: all constructors, getters and setters. A new set
  of stable methods, matching those in PMD 7, was added to replace the `addViolation`
  overloads of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/lang/rule/AbstractRule.html#"><code>AbstractRule</code></a>. In PMD 7, `RuleContext` will
  be the API to report violations, and it can already be used as such in PMD 6.
- The field <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/PMD.html#configuration"><code>configuration</code></a> is unused and will be removed.

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/RuleSet.html#"><code>RuleSet</code></a>: methods that serve to apply rules, including `apply`, `start`, `end`, `removeDysfunctionalRules`
- <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/renderers/AbstractAccumulatingRenderer.html#renderFileReport(Report)"><code>AbstractAccumulatingRenderer#renderFileReport</code></a> is internal API
  and should not be overridden in own renderers.

#### Changed API

It is now forbidden to report a violation:
- With a `null` node
- With a `null` message
- With a `null` set of format arguments (prefer a zero-length array)

Note that the message is set from the XML rule declaration, so this is only relevant
if you instantiate rules manually.

<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/RuleContext.html#"><code>RuleContext</code></a> now requires setting the current rule before calling
<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.43.0/net/sourceforge/pmd/Rule.html#apply(java.util.List,net.sourceforge.pmd.RuleContext)"><code>apply</code></a>. This is
done automatically by `RuleSet#apply` and such. Creating and configuring a
`RuleContext` manually is strongly advised against, as the lifecycle of `RuleContext`
will change drastically in PMD 7.

### External Contributions

*   [#3767](https://github.com/pmd/pmd/pull/3767): \[core] Update GUI.java - [Vyom Yadav](https://github.com/Vyom-Yadav)
*   [#3804](https://github.com/pmd/pmd/pull/3804): \[doc] Add floating table of contents (issue #2502) - [JerritEic](https://github.com/JerritEic)

### Stats
* 49 commits
* 22 closed tickets & PRs
* Days since last release: 27

## 29-January-2022 - 6.42.0

The PMD team is pleased to announce PMD 6.42.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Javascript: Rhino updated to latest version 1.7.14](#javascript:-rhino-updated-to-latest-version-1.7.14)
    * [New rules](#new-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Javascript: Rhino updated to latest version 1.7.14

[Rhino](https://github.com/mozilla/rhino), the implementation of JavaScript we use
for parsing JavaScript code, has been updated to the latest version 1.7.14.
Now language features like template strings can be parsed. However Rhino does
not support all features of the latest EcmaScript standard.

#### New rules

*   The new Java rule [`FinalParameterInAbstractMethod`](https://pmd.github.io/pmd-6.42.0/pmd_rules_java_codestyle.html#finalparameterinabstractmethod) detects parameters that are
    declared as final in interfaces or abstract methods. Declaring the parameters as final is useless
    because the implementation may choose to not respect it.

```xml
    <rule ref="category/java/codestyle.xml/FinalParameterInAbstractMethod" />
```

The rule is part of the quickstart.xml ruleset.

#### Modified rules

*   The Apex rule [`ApexDoc`](https://pmd.github.io/pmd-6.42.0/pmd_rules_apex_documentation.html#apexdoc) has a new property `reportProperty`.
    If set to `false` (default is `true` if unspecified) doesn't report missing ApexDoc comments on properties.
    It allows you to enforce ApexDoc comments for classes and methods without requiring them for properties.

### Fixed Issues

*   core
    *   [#3328](https://github.com/pmd/pmd/issues/3328): \[core] designer.bat errors when JAVAFX_HOME contains spaces
*   java
    *   [#3698](https://github.com/pmd/pmd/issues/3698): \[java] Error resolving Symbol Table
*   java-bestpractices
    *   [#3209](https://github.com/pmd/pmd/issues/3209): \[java] UnusedPrivateMethod false positive with static method and cast expression
    *   [#3468](https://github.com/pmd/pmd/issues/3468): \[java] UnusedPrivateMethod false positive when outer class calls private static method on inner class
*   java-design
    *   [#3679](https://github.com/pmd/pmd/issues/3679): \[java] Make FinalFieldCouldBeStatic detect constant variable
*   java-errorprone
    *   [#3644](https://github.com/pmd/pmd/issues/3644): \[java] InvalidLogMessageFormat: false positives with logstash structured logging
    *   [#3686](https://github.com/pmd/pmd/issues/3686): \[java] ReturnEmptyCollectionRatherThanNull - false negative with conditioned returns
    *   [#3701](https://github.com/pmd/pmd/issues/3701): \[java] MissingStaticMethodInNonInstantiatableClass false positive with method inner classes
    *   [#3721](https://github.com/pmd/pmd/issues/3721): \[java] ReturnEmptyCollectionRatherThanNull - false positive with stream and lambda
*   java-performance
    *   [#3492](https://github.com/pmd/pmd/issues/3492): \[java] UselessStringValueOf: False positive when there is no initial String to append to
    *   [#3639](https://github.com/pmd/pmd/issues/3639): \[java] UseStringBufferLength: false negative with empty string variable
    *   [#3712](https://github.com/pmd/pmd/issues/3712): \[java] InsufficientStringBufferDeclaration false positive with StringBuilder.setLength(0)
*   javascript
    *   [#3703](https://github.com/pmd/pmd/issues/3703): \[javascript] Error - no Node adapter class registered for XmlPropRef

### API Changes

No changes.

### External Contributions

*   [#3631](https://github.com/pmd/pmd/pull/3631): \[java] Fixed False positive for UselessStringValueOf when there is no initial String to append to - [John Armgardt](https://github.com/johnra2)
*   [#3683](https://github.com/pmd/pmd/pull/3683): \[java] Fixed 3468 UnusedPrivateMethod false positive when outer class calls private static method on inner class - [John Armgardt](https://github.com/johnra2)
*   [#3688](https://github.com/pmd/pmd/pull/3688): \[java] Bump log4j to 2.16.0 - [Sergey Nuyanzin](https://github.com/snuyanzin)
*   [#3693](https://github.com/pmd/pmd/pull/3693): \[apex] ApexDoc: Add reportProperty property - [Steve Babula](https://github.com/babula)
*   [#3704](https://github.com/pmd/pmd/pull/3704): \[java] Fix for #3686 - Fix ReturnEmptyCollectionRatherThanNull - [Oleksii Dykov](https://github.com/dykov)
*   [#3713](https://github.com/pmd/pmd/pull/3713): \[java] Enhance UnnecessaryModifier to support records - [Vincent Galloy](https://github.com/vgalloy)
*   [#3719](https://github.com/pmd/pmd/pull/3719): \[java] Upgrade log4j to 2.17.1 - [Daniel Paul Searles](https://github.com/squaresurf)
*   [#3720](https://github.com/pmd/pmd/pull/3720): \[java] New rule: FinalParameterInAbstractMethod - [Vincent Galloy](https://github.com/vgalloy)
*   [#3724](https://github.com/pmd/pmd/pull/3724): \[java] Fix for #3679 - fix FinalFieldCouldBeStatic - [Oleksii Dykov](https://github.com/dykov)
*   [#3727](https://github.com/pmd/pmd/pull/3727): \[java] #3724 - fix FinalFieldCouldBeStatic: triggers only if the referenced name is static - [Oleksii Dykov](https://github.com/dykov)
*   [#3742](https://github.com/pmd/pmd/pull/3742): \[java] Fix #3701 - fix MissingStaticMethodInNonInstantiatableClass for method local classes - [Oleksii Dykov](https://github.com/dykov)
*   [#3744](https://github.com/pmd/pmd/pull/3744): \[core] Updated SaxonXPathRuleQueryTest.java - [Vyom Yadav](https://github.com/Vyom-Yadav)
*   [#3745](https://github.com/pmd/pmd/pull/3745): \[java] Fix #3712: InsufficientStringBufferDeclaration setLength false positive - [Daniel Gredler](https://github.com/gredler)
*   [#3747](https://github.com/pmd/pmd/pull/3747): \[visualforce] Updated DataType.java - [Vyom Yadav](https://github.com/Vyom-Yadav)

### Stats
* 88 commits
* 35 closed tickets & PRs
* Days since last release: 62

## 27-November-2021 - 6.41.0

The PMD team is pleased to announce PMD 6.41.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [GitHub Action for PMD](#github-action-for-pmd)
    * [Last release in 2021](#last-release-in-2021)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Command Line Interface](#command-line-interface)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### GitHub Action for PMD

PMD now has its own official GitHub Action: [GitHub Action for PMD](https://github.com/marketplace/actions/pmd).
It can execute PMD with your own ruleset against your project. It creates a [SARIF](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html)
report which is uploaded as a build artifact. Furthermore the build can be failed based on the number of violations.

Feedback and pull requests are welcome at <https://github.com/pmd/pmd-github-action>.

#### Last release in 2021

This minor release will be the last one in 2021. The next release is scheduled to be end of January 2022.

### Fixed Issues

*   core
    *   [#2954](https://github.com/pmd/pmd/issues/2954): Create GitHub Action for PMD
    *   [#3424](https://github.com/pmd/pmd/issues/3424): \[core] Migrate CLI to using GNU-style long options
    *   [#3425](https://github.com/pmd/pmd/issues/3425): \[core] Add a `--version` CLI option
    *   [#3593](https://github.com/pmd/pmd/issues/3593): \[core] Ant task fails with Java17
    *   [#3635](https://github.com/pmd/pmd/issues/3635): \[ci] Update sample projects for regression tester
*   java-bestpractices
    *   [#3595](https://github.com/pmd/pmd/issues/3595): \[java] PrimitiveWrapperInstantiation: no violation on 'new Boolean(val)'
    *   [#3613](https://github.com/pmd/pmd/issues/3613): \[java] ArrayIsStoredDirectly doesn't consider nested classes
    *   [#3614](https://github.com/pmd/pmd/issues/3614): \[java] JUnitTestsShouldIncludeAssert doesn't consider nested classes
    *   [#3618](https://github.com/pmd/pmd/issues/3618): \[java] UnusedFormalParameter doesn't consider anonymous classes
    *   [#3630](https://github.com/pmd/pmd/issues/3630): \[java] MethodReturnsInternalArray doesn't consider anonymous classes
*   java-design
    *   [#3620](https://github.com/pmd/pmd/issues/3620): \[java] SingularField doesn't consider anonymous classes defined in non-private fields
*   java-errorprone
    *   [#3624](https://github.com/pmd/pmd/issues/3624): \[java] TestClassWithoutTestCases reports wrong classes in a file
*   java-performance
    *   [#3491](https://github.com/pmd/pmd/issues/3491): \[java] UselessStringValueOf: False positive when `valueOf(char [], int, int)` is used

### API Changes

#### Command Line Interface

The command line options for PMD and CPD now use GNU-style long options format. E.g. instead of `-rulesets` the
preferred usage is now `--rulesets`. Alternatively one can still use the short option `-R`.
Some options also have been renamed to a more consistent casing pattern at the same time
(`--fail-on-violation` instead of `-failOnViolation`).
The old single-dash options are still supported but are deprecated and will be removed with PMD 7.
This change makes the command line interface more consistent within PMD and also less surprising
compared to other cli tools.

The changes in detail for PMD:

|old option                     |new option|
|-------------------------------|----------|
| `-rulesets`                   | `--rulesets` (or `-R`) |
| `-uri`                        | `--uri` |
| `-dir`                        | `--dir` (or `-d`) |
| `-filelist`                   | `--file-list` |
| `-ignorelist`                 | `--ignore-list` |
| `-format`                     | `--format` (or `-f`) |
| `-debug`                      | `--debug` |
| `-verbose`                    | `--verbose` |
| `-help`                       | `--help` |
| `-encoding`                   | `--encoding` |
| `-threads`                    | `--threads` |
| `-benchmark`                  | `--benchmark` |
| `-stress`                     | `--stress` |
| `-shortnames`                 | `--short-names` |
| `-showsuppressed`             | `--show-suppressed` |
| `-suppressmarker`             | `--suppress-marker` |
| `-minimumpriority`            | `--minimum-priority` |
| `-property`                   | `--property` |
| `-reportfile`                 | `--report-file` |
| `-force-language`             | `--force-language` |
| `-auxclasspath`               | `--aux-classpath` |
| `-failOnViolation`            | `--fail-on-violation` |
| `--failOnViolation`           | `--fail-on-violation` |
| `-norulesetcompatibility`     | `--no-ruleset-compatibility` |
| `-cache`                      | `--cache` |
| `-no-cache`                   | `--no-cache` |

The changes in detail for CPD:

|old option             |new option|
|-----------------------|----------|
| `--failOnViolation`   | `--fail-on-violation` |
| `-failOnViolation`    | `--fail-on-violation` |
| `--filelist`          | `--file-list` |

### External Contributions

*   [#3600](https://github.com/pmd/pmd/pull/3600): \[core] Implement GNU-style long options and '--version' - [Yang](https://github.com/duanyang25)
*   [#3612](https://github.com/pmd/pmd/pull/3612): \[java] Created fix for UselessStringValueOf false positive - [John Armgardt](https://github.com/johnra2)
*   [#3648](https://github.com/pmd/pmd/pull/3648): \[doc] Rename Code Inspector to Codiga - [Julien Delange](https://github.com/juli1)

### Stats
* 80 commits
* 23 closed tickets & PRs
* Days since last release: 28

## 30-October-2021 - 6.40.0

The PMD team is pleased to announce PMD 6.40.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [New rules](#new-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Updated Apex Support

*   The Apex language support has been bumped to version 54.0 (Spring '22).

#### New rules

*   The new Apex rule [`EagerlyLoadedDescribeSObjectResult`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_performance.html#eagerlyloadeddescribesobjectresult) finds
    `DescribeSObjectResult`s which could have been loaded eagerly via `SObjectType.getDescribe()`.

```xml
    <rule ref="category/apex/performance.xml/EagerlyLoadedDescribeSObjectResult" />
```

#### Modified rules

*   The Apex rule [`ApexUnitTestClassShouldHaveAsserts`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_bestpractices.html#apexunittestclassshouldhaveasserts) has a new property
    `additionalAssertMethodPattern`. When specified the pattern is evaluated against each invoked
    method name to determine whether it represents a test assertion in addition to the standard names.

*   The Apex rule [`ApexDoc`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_documentation.html#apexdoc) has a new property `reportMissingDescription`.
    If set to `false` (default is `true` if unspecified) doesn't report an issue if the `@description`
    tag is missing. This is consistent with the ApexDoc dialect supported by derivatives such as
    [SfApexDoc](https://gitlab.com/StevenWCox/sfapexdoc) and also with analogous documentation tools for
    other languages, e.g., JavaDoc, ESDoc/JSDoc, etc.

*   The Apex rule [`ApexCRUDViolation`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_security.html#apexcrudviolation) has a couple of new properties:
    These allow specification of regular-expression-based patterns for additional methods that should
    be considered valid for pre-CRUD authorization beyond those offered by the system Apex checks and
    ESAPI, e.g., [`sirono-common`'s `AuthorizationUtil` class](https://github.com/SCWells72/sirono-common#authorization-utilities).
    Two new properties have been added per-CRUD operation, one to specify the naming pattern for a method
    that authorizes that operation and another to specify the argument passed to that method that contains
    the `SObjectType` instance of the type being authorized. Here is an example of these new properties:

    ```xml
    <rule ref="category/apex/security.xml/ApexCRUDViolation" message="...">
      <priority>3</priority>
      <properties>
        <property name="createAuthMethodPattern" value="AuthorizationUtil\.(is|assert)(Createable|Upsertable)"/>
        <!--
         There's one of these properties for each operation, and the default value is 0 so this is technically
         superfluous, but it's included it here for example purposes.
         -->
        <property name="createAuthMethodTypeParamIndex" value="0"/>
        <property name="readAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Accessible"/>
        <property name="updateAuthMethodPattern" value="AuthorizationUtil\.(is|assert)(Updateable|Upsertable)"/>
        <property name="deleteAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Deletable"/>
        <property name="undeleteAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Undeletable"/>
        <property name="mergeAuthMethodPattern" value="AuthorizationUtil\.(is|assert)Mergeable"/>
      </properties>
    </rule>
    ```

*   The Apex rule [`EmptyStatementBlock`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_errorprone.html#emptystatementblock) has two new properties:

    Setting `reportEmptyPrivateNoArgConstructor` to `false` ignores empty private no-arg constructors
    that are commonly used in singleton pattern implementations and utility classes in support of
    prescribed best practices.

    Setting `reportEmptyVirtualMethod` to `false` ignores empty virtual methods that are commonly used in
    abstract base classes as default no-op implementations when derived classes typically only override a
    subset of virtual methods.

    By default, both properties are `true` to not change the default behaviour of this rule.

*   The Apex rule [`EmptyCatchBlock`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_errorprone.html#emptycatchblock) has two new properties modeled after the analogous Java rule:

    The `allowCommentedBlocks` property, when set to `true` (defaults to `false`), ignores empty blocks containing comments, e.g.:

    ```apex
    try {
        doSomethingThatThrowsAnExpectedException();
        System.assert(false, 'Expected to catch an exception.');
    } catch (Exception e) {
        // Expected
    }
    ```

    The `allowExceptionNameRegex` property is a regular expression for exception variable names for which empty catch blocks should be ignored by this rule. For example, using the default property value of `^(ignored|expected)$`, the following empty catch blocks will not be reported:

    ```apex
    try {
        doSomethingThatThrowsAnExpectedException();
        System.assert(false, 'Expected to catch an exception.');
    } catch (IllegalStateException ignored) {
    } catch (NumberFormatException expected) {
    }
    ```

*   The Apex rule [`OneDeclarationPerLine`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_codestyle.html#onedeclarationperline) has a new property `reportInForLoopInitializer`:
    If set to `false` (default is `true` if unspecified) doesn't report an issue for multiple declarations in
    a `for` loop's initializer section. This is support the common idiom of one declaration for the loop variable
    and another for the loop bounds condition, e.g.,

    ```apex
    for (Integer i = 0, numIterations = computeNumIterations(); i < numIterations; i++) {
    }
    ```

*   The Java rule [`ClassNamingConventions`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_java_codestyle.html#classnamingconventions) uses a different default value of the
    property `utilityClassPattern`: This rule was detecting utility classes by default since PMD 6.3.0
    and enforcing the naming convention that utility classes has to be suffixed with Util or Helper or Constants.
    However this turned out to be not so useful as a default configuration, as there is no standard
    naming convention for utility classes.

    With PMD 6.40.0, the default value of this property has been changed to `[A-Z][a-zA-Z0-9]*`
    (Pascal case), effectively disabling the special handling of utility classes. This is the same default
    pattern used for concrete classes.

    This means, that the feature to enforce a naming convention for utility classes is now a opt-in
    feature and can be enabled on demand.

    To use the old behaviour, the property needs to be configured as follows:

    ```xml
    <rule ref="category/java/codestyle.xml/ClassNamingConventions">
        <properties>
            <property name="utilityClassPattern" value="[A-Z][a-zA-Z0-9]+(Utils?|Helper|Constants)" />
        </properties>
    </rule>
    ```


### Fixed Issues

*   apex
    *   [#1089](https://github.com/pmd/pmd/issues/1089): \[apex] ApexUnitTestClassShouldHaveAsserts: Test asserts in other methods not detected
    *   [#1090](https://github.com/pmd/pmd/issues/1090): \[apex] ApexCRUDViolation: checks not detected if done in another method
    *   [#3532](https://github.com/pmd/pmd/issues/3532): \[apex] Promote usage of consistent getDescribe() info
    *   [#3566](https://github.com/pmd/pmd/issues/3566): \[apex] ApexDoc rule should not require "@description"
    *   [#3568](https://github.com/pmd/pmd/issues/3568): \[apex] EmptyStatementBlock: should provide options to ignore empty private constructors and empty virtual methods
    *   [#3569](https://github.com/pmd/pmd/issues/3569): \[apex] EmptyCatchBlock: should provide an option to ignore empty catch blocks in test methods
    *   [#3570](https://github.com/pmd/pmd/issues/3570): \[apex] OneDeclarationPerLine: should provide an option to ignore multiple declarations in a for loop initializer
    *   [#3576](https://github.com/pmd/pmd/issues/3576): \[apex] ApexCRUDViolation should provide an option to specify additional patterns for methods that encapsulate authorization checks
    *   [#3579](https://github.com/pmd/pmd/issues/3579): \[apex] ApexCRUDViolation: false negative with undelete
*   java-bestpractices
    *   [#3542](https://github.com/pmd/pmd/issues/3542): \[java] MissingOverride: False negative for enum method
*   java-codestyle
    *   [#1595](https://github.com/pmd/pmd/issues/1595): \[java] Discuss default for utility classes in ClassNamingConventions
    *   [#3563](https://github.com/pmd/pmd/issues/3563): \[java] The ClassNamingConventionsRule false-positive's on the class name "Constants"
*   java-errorprone
    *   [#3560](https://github.com/pmd/pmd/issues/3560): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda
*   java-performance
    *   [#2364](https://github.com/pmd/pmd/issues/2364): \[java] AddEmptyString false positive in annotation value
*   java-security
    *   [#3368](https://github.com/pmd/pmd/issues/3368): \[java] HardcodedCryptoKey false negative with variable assignments

### API Changes

#### Experimental APIs

*   The interface <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.41.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/ast/ASTCommentContainer.html#"><code>ASTCommentContainer</code></a> has been added to the Apex AST.
    It provides a way to check whether a node contains at least one comment. Currently this is only implemented for
    <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.41.0-SNAPSHOT/net/sourceforge/pmd/lang/apex/ast/ASTCatchBlockStatement.html#"><code>ASTCatchBlockStatement</code></a> and used by the rule
    [`EmptyCatchBlock`](https://pmd.github.io/pmd-6.41.0-SNAPSHOT/pmd_rules_apex_errorprone.html#emptycatchblock).
    This information is also available via XPath attribute `@ContainsComment`.

### External Contributions

*   [#3538](https://github.com/pmd/pmd/pull/3538): \[apex] New rule EagerlyLoadedDescribeSObjectResult - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3549](https://github.com/pmd/pmd/pull/3549): \[java] Ignore AddEmptyString rule in annotations - [Stanislav Myachenkov](https://github.com/smyachenkov)
*   [#3561](https://github.com/pmd/pmd/pull/3561): \[java] InvalidLogMessageFormat: False positive with message and exception in a block inside a lambda - [Nicolas Filotto](https://github.com/essobedo)
*   [#3565](https://github.com/pmd/pmd/pull/3565): \[doc] Fix resource leak due to Files.walk - [lujiefsi](https://github.com/lujiefsi)
*   [#3571](https://github.com/pmd/pmd/pull/3571): \[apex] Fix for #1089 - Added new configuration property additionalAssertMethodPattern to ApexUnitTestClassShouldHaveAssertsRule - [Scott Wells](https://github.com/SCWells72)
*   [#3572](https://github.com/pmd/pmd/pull/3572): \[apex] Fix for #3566 - Added new configuration property reportMissingDescription to ApexDocRule - [Scott Wells](https://github.com/SCWells72)
*   [#3573](https://github.com/pmd/pmd/pull/3573): \[apex] Fix for #3568 - Added new configuration properties reportEmptyPrivateNoArgConstructor and reportEmptyVirtualMethod to EmptyStatementBlock - [Scott Wells](https://github.com/SCWells72)
*   [#3574](https://github.com/pmd/pmd/pull/3574): \[apex] Fix for #3569 - Added new configuration properties allowCommentedBlocks and allowExceptionNameRegex to EmptyCatchBlock - [Scott Wells](https://github.com/SCWells72)
*   [#3575](https://github.com/pmd/pmd/pull/3575): \[apex] Fix for #3570 - Added new configuration property reportInForLoopInitializer to OneDeclarationPerLine - [Scott Wells](https://github.com/SCWells72)
*   [#3577](https://github.com/pmd/pmd/pull/3577): \[apex] Fix for #3576 - Added new configuration properties \*AuthMethodPattern and \*AuthMethodTypeParamIndex to ApexCRUDViolation rule - [Scott Wells](https://github.com/SCWells72)
*   [#3578](https://github.com/pmd/pmd/pull/3578): \[apex] ApexCRUDViolation: Documentation changes for #3576 - [Scott Wells](https://github.com/SCWells72)
*   [#3580](https://github.com/pmd/pmd/pull/3580): \[doc] Release notes updates for the changes in issue #3569 - [Scott Wells](https://github.com/SCWells72)
*   [#3581](https://github.com/pmd/pmd/pull/3581): \[apex] #3569 - Requested changes for code review feedback - [Scott Wells](https://github.com/SCWells72)

### Stats
* 72 commits
* 37 closed tickets & PRs
* Days since last release: 34

## 25-September-2021 - 6.39.0

The PMD team is pleased to announce PMD 6.39.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [All Contributors](#all-contributors)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### All Contributors

PMD follows the [All Contributors](https://allcontributors.org/) specification.
Contributions of any kind welcome!

See [credits](https://pmd.github.io/latest/pmd_projectdocs_credits.html) for our complete contributors list.

### Fixed Issues

*   core
    *   [#3499](https://github.com/pmd/pmd/pull/3499): \[core] Fix XPath rulechain with combined node tests
*   java-errorprone
    *   [#3493](https://github.com/pmd/pmd/pull/3493): \[java] AvoidAccessibilityAlteration: add tests and fix rule
*   javascript
    *   [#3516](https://github.com/pmd/pmd/pull/3516): \[javascript] NPE while creating rule violation when specifying explicit line numbers
*   plsql
    *   [#3487](https://github.com/pmd/pmd/issues/3487): \[plsql] Parsing exception OPEN ref_cursor_name FOR statement
    *   [#3515](https://github.com/pmd/pmd/issues/3515): \[plsql] Parsing exception SELECT...INTO on Associative Arrays Types

### API Changes

No changes.

### External Contributions

*   [#3516](https://github.com/pmd/pmd/pull/3516): \[javascript] NPE while creating rule violation when specifying explicit line numbers - [Kevin Guerra](https://github.com/kevingnet)

### Stats
* 37 commits
* 10 closed tickets & PRs
* Days since last release: 27

## 28-August-2021 - 6.38.0

The PMD team is pleased to announce PMD 6.38.0.

This is a minor release.

### Table Of Contents

* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### Fixed Issues

*   apex
    *   [#3462](https://github.com/pmd/pmd/issues/3462): \[apex] SOQL performed in a for-each loop doesn't trigger ApexCRUDViolationRule
    *   [#3484](https://github.com/pmd/pmd/issues/3484): \[apex] ApexCRUDViolationRule maintains state across files
*   core
    *   [#3446](https://github.com/pmd/pmd/issues/3446): \[core] Allow XPath rules to access the current file name
*   java-bestpractices
    *   [#3403](https://github.com/pmd/pmd/issues/3403): \[java] MethodNamingConventions junit5TestPattern does not detect parameterized tests

### External Contributions

*   [#3445](https://github.com/pmd/pmd/pull/3445): \[java] Fix #3403 about MethodNamingConventions and JUnit5 parameterized tests - [Cyril Sicard](https://github.com/CyrilSicard)
*   [#3470](https://github.com/pmd/pmd/pull/3470): \[apex] Fix ApexCRUDViolationRule - add super call - [Josh Feingold](https://github.com/jfeingold35)

### Stats
* 32 commits
* 8 closed tickets & PRs
* Days since last release: 27

## 31-July-2021 - 6.37.0

The PMD team is pleased to announce PMD 6.37.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 17 Support](#java-17-support)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New rules](#new-rules)
    * [Renamed rules](#renamed-rules)
    * [Deprecated rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [PMD CLI](#pmd-cli)
    * [Experimental APIs](#experimental-apis)
    * [Internal API](#internal-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Java 17 Support

This release of PMD brings support for Java 17. PMD supports [JEP 409: Sealed Classes](https://openjdk.java.net/jeps/409)
which has been promoted to be a standard language feature of Java 17.

PMD also supports [JEP 406: Pattern Matching for switch (Preview)](https://openjdk.java.net/jeps/406) as a preview
language feature. In order to analyze a project with PMD that uses these language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version `17-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 17-preview ...

Note: Support for Java 15 preview language features have been removed. The version "15-preview" is no longer available.

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.37.0).

#### New rules

This release ships with 3 new Java rules.

*   [`PrimitiveWrapperInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation) reports usages of primitive wrapper
    constructors. They are deprecated since Java 9 and should not be used.

```xml
    <rule ref="category/java/bestpractices.xml/PrimitiveWrapperInstantiation" />
```

The rule is part of the quickstart.xml ruleset.

*   [`SimplifiableTestAssertion`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion) suggests rewriting
    some test assertions to be more readable.

```xml
    <rule ref="category/java/bestpractices.xml/SimplifiableTestAssertion" />
```

The rule is part of the quickstart.xml ruleset.

*   [`ReturnEmptyCollectionRatherThanNull`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_errorprone.html#returnemptycollectionratherthannull) suggests returning empty collections / arrays
    instead of null.

```xml
    <rule ref="category/java/errorprone.xml/ReturnEmptyCollectionRatherThanNull" />
```

The rule is part of the quickstart.xml ruleset.

#### Renamed rules

*   The Java rule [`MissingBreakInSwitch`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_errorprone.html#missingbreakinswitch) has been renamed to
    [`ImplicitSwitchFallThrough`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_errorprone.html#implicitswitchfallthrough) (category error prone) to better reflect the rule's
    purpose: The rule finds implicit fall-through cases in switch statements, which are most
    likely unexpected. The old rule name described only one way how to avoid a fall-through,
    namely using `break` but `continue`, `throw` and `return` avoid a fall-through
    as well. This enables us to improve this rule in the future.

#### Deprecated rules

*   The following Java rules are deprecated and removed from the quickstart ruleset,
    as the new rule [`SimplifiableTestAssertion`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#simplifiabletestassertion) merges
    their functionality:
    * [`UseAssertEqualsInsteadOfAssertTrue`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#useassertequalsinsteadofasserttrue)
    * [`UseAssertNullInsteadOfAssertTrue`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#useassertnullinsteadofasserttrue)
    * [`UseAssertSameInsteadOfAssertTrue`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#useassertsameinsteadofasserttrue)
    * [`UseAssertTrueInsteadOfAssertEquals`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#useasserttrueinsteadofassertequals)
    * [`SimplifyBooleanAssertion`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_design.html#simplifybooleanassertion)

*   The Java rule [`ReturnEmptyArrayRatherThanNull`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_errorprone.html#returnemptyarrayratherthannull) is deprecated and removed from
    the quickstart ruleset, as the new rule [`ReturnEmptyCollectionRatherThanNull`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_errorprone.html#returnemptycollectionratherthannull)
    supersedes it.

*   The following Java rules are deprecated and removed from the quickstart ruleset,
    as the new rule [`PrimitiveWrapperInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#primitivewrapperinstantiation) merges
    their functionality:
    * [`BooleanInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#booleaninstantiation)
    * [`ByteInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#byteinstantiation)
    * [`IntegerInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#integerinstantiation)
    * [`LongInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#longinstantiation)
    * [`ShortInstantiation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#shortinstantiation)

*   The Java rule [`UnnecessaryWrapperObjectCreation`](https://pmd.github.io/pmd-6.37.0/pmd_rules_java_performance.html#unnecessarywrapperobjectcreation) is deprecated
    with no planned replacement before PMD 7. In it's current state, the rule is not useful
    as it finds only contrived cases of creating a primitive wrapper and unboxing it explicitly
    in the same expression. In PMD 7 this and more cases will be covered by a
    new rule `UnnecessaryBoxing`.

### Fixed Issues

*   apex
    *   [#3201](https://github.com/pmd/pmd/issues/3201): \[apex] ApexCRUDViolation doesn't report Database class DMLs, inline no-arg object instantiations and inline list initialization
    *   [#3329](https://github.com/pmd/pmd/issues/3329): \[apex] ApexCRUDViolation doesn't report SOQL for loops
*   core
    *   [#1603](https://github.com/pmd/pmd/issues/1603): \[core] Language version comparison
    *   [#2133](https://github.com/pmd/pmd/issues/2133): \[xml] Allow to check Salesforce XML Metadata using XPath rules
    *   [#3377](https://github.com/pmd/pmd/issues/3377): \[core] NPE when specifying report file in current directory in PMD CLI
    *   [#3387](https://github.com/pmd/pmd/issues/3387): \[core] CPD should avoid unnecessary copies when running with --skip-lexical-errors
*   java-bestpractices
    *   [#2908](https://github.com/pmd/pmd/issues/2908): \[java] Merge Junit assertion simplification rules
    *   [#3235](https://github.com/pmd/pmd/issues/3235): \[java] UseTryWithResources false positive when closeable is provided as a method argument or class field
*   java-errorprone
    *   [#3361](https://github.com/pmd/pmd/issues/3361): \[java] Rename rule MissingBreakInSwitch to ImplicitSwitchFallThrough
    *   [#3382](https://github.com/pmd/pmd/pull/3382): \[java] New rule ReturnEmptyCollectionRatherThanNull
*   java-performance
    *   [#3420](https://github.com/pmd/pmd/issues/3420): \[java] NPE in `InefficientStringBuffering` with Records

### API Changes

#### PMD CLI

*   PMD has a new CLI option `-force-language`. With that a language can be forced to be used for all input files,
    irrespective of filenames. When using this option, the automatic language selection by extension is disabled
    and all files are tried to be parsed with the given language. Parsing errors are ignored and unparsable files
    are skipped.

    This option allows to use the xml language for files, that don't use xml as extension.
    See also the examples on [PMD CLI reference](pmd_userdocs_cli_reference.html#analyze-other-xml-formats).

#### Experimental APIs

*   The AST types and APIs around Sealed Classes are not experimental anymore:
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.37.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#isSealed()"><code>ASTClassOrInterfaceDeclaration#isSealed</code></a>,
        <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.37.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#isNonSealed()"><code>ASTClassOrInterfaceDeclaration#isNonSealed</code></a>,
        <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.37.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getPermittedSubclasses()"><code>ASTClassOrInterfaceDeclaration#getPermittedSubclasses</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.37.0/net/sourceforge/pmd/lang/java/ast/ASTPermitsList.html#"><code>ASTPermitsList</code></a>

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The inner class <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.37.0/net/sourceforge/pmd/cpd/TokenEntry.State.html#"><code>net.sourceforge.pmd.cpd.TokenEntry.State</code></a> is considered to be internal API.
    It will probably be moved away with PMD 7.

### External Contributions

*   [#3367](https://github.com/pmd/pmd/pull/3367): \[apex] Check SOQL CRUD on for loops - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3373](https://github.com/pmd/pmd/pull/3373): \[apex] Add ApexCRUDViolation support for database class, inline no-arg object construction DML and inline list initialization DML - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3385](https://github.com/pmd/pmd/pull/3385): \[core] CPD: Optimize --skip-lexical-errors option - [Woongsik Choi](https://github.com/woongsikchoi)
*   [#3388](https://github.com/pmd/pmd/pull/3388): \[doc] Add Code Inspector in the list of tools - [Julien Delange](https://github.com/juli1)
*   [#3417](https://github.com/pmd/pmd/pull/3417): \[core] Support forcing a specific language from the command-line - [Aidan Harding](https://github.com/aidan-harding)

### Stats
* 82 commits
* 29 closed tickets & PRs
* Days since last release: 35

## 26-June-2021 - 6.36.0

The PMD team is pleased to announce PMD 6.36.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Improved Incremental Analysis](#improved-incremental-analysis)
    * [New rules](#new-rules)
    * [Renamed rules](#renamed-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Improved Incremental Analysis

[Incremental Analysis](https://pmd.github.io/pmd-6.36.0/pmd_userdocs_incremental_analysis.html) has long helped
our users obtain faster analysis results, however, its implementation tended to be too cautious in detecting
changes to the runtime and type resolution classpaths, producing more cache invalidations than necessary.
We have now improved the heuristics to remove several bogus invalidations, and slightly sped up the cache
usage along the way.

PMD will now ignore:

*   Non class files in classpath and jar / zip files being referenced.
*   Changes to the order of file entries within a jar / zip
*   Changes to file metadata within jar / zip (ie: creation and modification time,
    significant in multi-module / composite build projects where lateral artifacts are frequently recreated)

#### New rules

*   The new Apex rule [`AvoidDebugStatements`](https://pmd.github.io/pmd-6.36.0/pmd_rules_apex_performance.html#avoiddebugstatements) finds usages of `System.debug` calls.
    Debug statements contribute to longer transactions and consume Apex CPU time even when debug logs are not
    being captured.
    You can try out this rule like so:

```xml
    <rule ref="category/apex/performance.xml/AvoidDebugStatements" />
```

*   The new Apex rule [`InaccessibleAuraEnabledGetter`](https://pmd.github.io/pmd-6.36.0/pmd_rules_apex_errorprone.html#inaccessibleauraenabledgetter) checks that an `AuraEnabled`
    getter is public or global. This is necessary if it is referenced in Lightning components.
    You can try out this rule like so:

```xml
    <rule ref="category/apex/errorprone.xml/InaccessibleAuraEnabledGetter" />
```

#### Renamed rules

*   The Java rule [`BadComparison`](https://pmd.github.io/pmd-6.36.0/pmd_rules_java_errorprone.html#badcomparison) has been renamed to
    [`ComparisonWithNaN`](https://pmd.github.io/pmd-6.36.0/pmd_rules_java_errorprone.html#comparisonwithnan) to better reflect what the rule actually detects.
    It now considers usages of `Double.NaN` or `Float.NaN` in more cases and fixes false negatives.

### Fixed Issues

*   apex
    *   [#3307](https://github.com/pmd/pmd/issues/3307): \[apex] Avoid debug statements since it impact performance
    *   [#3321](https://github.com/pmd/pmd/issues/3321): \[apex] New rule to detect inaccessible AuraEnabled getters (summer '21 security update)
    *   [#3332](https://github.com/pmd/pmd/issues/3332): \[apex] CognitiveComplexity - incorrect increment for "else if"
*   core
    *   [#2637](https://github.com/pmd/pmd/issues/2637): \[cpd] Error Loading stylesheet cpdhtml.xslt
    *   [#3323](https://github.com/pmd/pmd/pull/3323): \[core] Adds fullDescription and tags in SARIF report
*   java-bestpractices
    *   [#957](https://github.com/pmd/pmd/issues/957): \[java] GuardLogStatement: False positive with compile-time constant arguments
    *   [#3076](https://github.com/pmd/pmd/pull/3076): \[java] UnusedAssignment reports unused variable when used in increment expr
    *   [#3114](https://github.com/pmd/pmd/issues/3114): \[java] UnusedAssignment false positive when reporting unused variables
    *   [#3315](https://github.com/pmd/pmd/issues/3315): \[java] LiteralsFirstInComparisons false positive with two constants
    *   [#3341](https://github.com/pmd/pmd/issues/3341): \[java] JUnitTestsShouldIncludeAssert should support Junit 5
    *   [#3340](https://github.com/pmd/pmd/issues/3340): \[java] NullPointerException applying rule GuardLogStatement
*   java-codestyle
    *   [#3317](https://github.com/pmd/pmd/pull/3317): \[java] Update UnnecessaryImport to recognize usage of imported types in javadoc's `@exception` tag
*   java-errorprone
    *   [#2895](https://github.com/pmd/pmd/issues/2895): \[java] Improve BadComparison and rename to ComparisonWithNaN
    *   [#3284](https://github.com/pmd/pmd/issues/3284): \[java] InvalidLogMessageFormat may examine the value of a different but identically named String variable
    *   [#3304](https://github.com/pmd/pmd/issues/3304): \[java] NPE in MoreThanOneLoggerRule on a java 16 record
    *   [#3305](https://github.com/pmd/pmd/issues/3305): \[java] ConstructorCallsOverridableMethodRule IndexOutOfBoundsException on a java16 record
    *   [#3343](https://github.com/pmd/pmd/pull/3343): \[java] CloneMethodMustImplementCloneable: FN with local classes
*   java-performance
    *   [#3331](https://github.com/pmd/pmd/issues/3331): \[java] UseArraysAsList false negative with for-each loop
    *   [#3344](https://github.com/pmd/pmd/pull/3344): \[java] InefficientEmptyStringCheck FN with trim.length on method call

### API Changes

No changes.

### External Contributions

*   [#3276](https://github.com/pmd/pmd/pull/3276): \[apex] Update ApexCRUDViolation and OperationWithLimitsInLoop docs - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3306](https://github.com/pmd/pmd/pull/3306): \[java] More than one logger rule test null pointer exception - [Arnaud Jeansen](https://github.com/ajeans)
*   [#3317](https://github.com/pmd/pmd/pull/3317): \[java] Update UnnecessaryImport to recognize usage of imported types in javadoc's `@exception` tag - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#3319](https://github.com/pmd/pmd/pull/3319): \[apex] New AvoidDebugStatements rule to mitigate performance impact - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3320](https://github.com/pmd/pmd/pull/3320): \[java] Fix incorrect increment for "else if" branch in Cognitive Complexity docs - [Denis Borovikov](https://github.com/borovikovd)
*   [#3322](https://github.com/pmd/pmd/pull/3322): \[apex] added rule to detect inaccessible AuraEnabled getters - [Philippe Ozil](https://github.com/pozil)
*   [#3323](https://github.com/pmd/pmd/pull/3323): \[core] Adds fullDescription and tags in SARIF report - [Clint Chester](https://github.com/Clint-Chester)
*   [#3339](https://github.com/pmd/pmd/pull/3339): \[java] JUnitTestsShouldIncludeAssert Tweak assertion definition to avoid false positive with modern JUnit5 - [Arnaud Jeansen](https://github.com/ajeans)

### Stats
* 81 commits
* 36 closed tickets & PRs
* Days since last release: 28

## 29-May-2021 - 6.35.0

The PMD team is pleased to announce PMD 6.35.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Javascript module now requires at least Java 8](#javascript-module-now-requires-at-least-java-8)
    * [New rules](#new-rules)
    * [Modified rules](#modified-rules)
    * [Deprecated rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Javascript module now requires at least Java 8

The latest version of [Rhino](https://github.com/mozilla/rhino), the implementation of JavaScript we use
for parsing JavaScript code, requires at least Java 8. Therefore we decided to upgrade the pmd-javascript
module to Java 8 as well. This means that from now on, a Java 8 or later runtime is required in order
to analyze JavaScript code. Note that PMD core still only requires Java 7.

#### New rules

This release ships with 3 new Java rules.

*   [`JUnit5TestShouldBePackagePrivate`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_bestpractices.html#junit5testshouldbepackageprivate)
    enforces the convention that JUnit 5 tests should have minimal visibility.
    You can try out this rule like so:
```xml
    <rule ref="category/java/bestpractices.xml/JUnit5TestShouldBePackagePrivate" />
```

*   [`CognitiveComplexity`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_design.html#cognitivecomplexity) uses the cognitive complexity
    metric to find overly complex code. This metric improves on the similar cyclomatic complexity
    in several ways, for instance, it incentivizes using clearly readable shorthands and idioms.
    See the rule documentation for more details. You can try out this rule like so:
```xml
    <rule ref="category/java/design.xml/CognitiveComplexity" />
```

*   [`MutableStaticState`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_design.html#mutablestaticstate) finds non-private static fields
    that are not final. These fields break encapsulation since these fields can be modified from anywhere
    within the program. You can try out this rule like so:
```xml
    <rule ref="category/java/design.xml/MutableStaticState" />
```

#### Modified rules

*   The Java rule [`CompareObjectsWithEquals`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_errorprone.html#compareobjectswithequals) has now a new property
    `typesThatCompareByReference`. With that property, you can configure types, that should be whitelisted
    for comparison by reference. By default, `java.lang.Enum` and `java.lang.Class` are allowed, but
    you could add custom types here.
    Additionally comparisons against constants are allowed now. This makes the rule less noisy when two constants
    are compared. Constants are identified by looking for an all-caps identifier.

#### Deprecated rules

*   The java rule [`DefaultPackage`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_codestyle.html#defaultpackage) has been deprecated in favor of
    [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier).

    The rule "DefaultPackage" assumes that any usage of package-access is accidental,
    and by doing so, prohibits using a really fundamental and useful feature of the language.

    To satisfy the rule, you have to make the member public even if it doesn't need to, or make it protected,
    which muddies your intent even more if you don't intend the class to be extended, and may be at odds with
    other rules like [`AvoidProtectedFieldInFinalClass`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_codestyle.html#avoidprotectedfieldinfinalclass).

    The rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) should be used instead.
    It flags the same thing, but has an escape hatch.

*   The Java rule [`CloneThrowsCloneNotSupportedException`](https://pmd.github.io/pmd-6.35.0/pmd_rules_java_errorprone.html#clonethrowsclonenotsupportedexception) has been deprecated without
    replacement.

    The rule has no real value as `CloneNotSupportedException` is a
    checked exception and therefore you need to deal with it while implementing the `clone()` method. You either
    need to declare the exception or catch it. If you catch it, then subclasses can't throw it themselves explicitly.
    However, `Object.clone()` will still throw this exception if the `Cloneable` interface is not implemented.

    Note, this rule has also been removed from the Quickstart Ruleset (`rulesets/java/quickstart.xml`).

### Fixed Issues

*   apex
    *   [#3183](https://github.com/pmd/pmd/issues/3183): \[apex] ApexUnitTestMethodShouldHaveIsTestAnnotation false positive with helper method
    *   [#3243](https://github.com/pmd/pmd/pull/3243): \[apex] Correct findBoundary when traversing AST
*   core
    *   [#2639](https://github.com/pmd/pmd/issues/2639): \[core] PMD CLI output file is not created if directory or directories in path don't exist
    *   [#3196](https://github.com/pmd/pmd/issues/3196): \[core] Deprecate ThreadSafeReportListener
*   doc
    *   [#3230](https://github.com/pmd/pmd/issues/3230): \[doc] Remove "Edit me" button for language index pages
*   dist
    *   [#2466](https://github.com/pmd/pmd/issues/2466): \[dist] Distribution archive doesn't include all batch scripts
*   java
    *   [#3269](https://github.com/pmd/pmd/pull/3269): \[java] Fix NPE in MethodTypeResolution
*   java-bestpractices
    *   [#1175](https://github.com/pmd/pmd/issues/1175): \[java] UnusedPrivateMethod FP with Junit 5 @MethodSource
    *   [#2219](https://github.com/pmd/pmd/issues/2219): \[java] Document Reasons to Avoid Reassigning Parameters
    *   [#2737](https://github.com/pmd/pmd/issues/2737): \[java] Fix misleading rule message on rule SwitchStmtsShouldHaveDefault with non-exhaustive enum switch
    *   [#3236](https://github.com/pmd/pmd/issues/3236): \[java] LiteralsFirstInComparisons should consider constant fields (cont'd)
    *   [#3239](https://github.com/pmd/pmd/issues/3239): \[java] PMD could enforce non-public methods for Junit5 / Jupiter test methods
    *   [#3254](https://github.com/pmd/pmd/issues/3254): \[java] AvoidReassigningParameters reports violations on wrong line numbers
*   java-codestyle
    *   [#2655](https://github.com/pmd/pmd/issues/2655): \[java] UnnecessaryImport false positive for on-demand imports
    *   [#3206](https://github.com/pmd/pmd/issues/3206): \[java] Deprecate rule DefaultPackage
    *   [#3262](https://github.com/pmd/pmd/pull/3262): \[java] FieldDeclarationsShouldBeAtStartOfClass: false negative with anon classes
    *   [#3265](https://github.com/pmd/pmd/pull/3265): \[java] MethodArgumentCouldBeFinal: false negatives with interfaces and inner classes
    *   [#3266](https://github.com/pmd/pmd/pull/3266): \[java] LocalVariableCouldBeFinal: false negatives with interfaces, anon classes
    *   [#3274](https://github.com/pmd/pmd/pull/3274): \[java] OnlyOneReturn: false negative with anonymous class
    *   [#3275](https://github.com/pmd/pmd/pull/3275): \[java] UnnecessaryLocalBeforeReturn: false negatives with lambda and anon class
*   java-design
    *   [#2780](https://github.com/pmd/pmd/issues/2780): \[java] DataClass example from documentation results in false-negative
    *   [#2987](https://github.com/pmd/pmd/issues/2987): \[java] New Rule: Public and protected static fields must be final
    *   [#2329](https://github.com/pmd/pmd/issues/2329): \[java] Cognitive complexity rule for Java
*   java-errorprone
    *   [#3110](https://github.com/pmd/pmd/issues/3110): \[java] Enhance CompareObjectsWithEquals with list of exceptions
    *   [#3112](https://github.com/pmd/pmd/issues/3112): \[java] Deprecate rule CloneThrowsCloneNotSupportedException
    *   [#3205](https://github.com/pmd/pmd/issues/3205): \[java] Make CompareObjectWithEquals allow comparing against constants
    *   [#3248](https://github.com/pmd/pmd/issues/3248): \[java] Documentation is wrong for SingletonClassReturningNewInstance rule
    *   [#3249](https://github.com/pmd/pmd/pull/3249): \[java] AvoidFieldNameMatchingTypeName: False negative with interfaces
    *   [#3268](https://github.com/pmd/pmd/pull/3268): \[java] ConstructorCallsOverridableMethod: IndexOutOfBoundsException with annotations
*   java-performance
    *   [#1438](https://github.com/pmd/pmd/issues/1438): \[java] InsufficientStringBufferDeclaration false positive for initial calculated StringBuilder size
*   javascript
    *   [#699](https://github.com/pmd/pmd/issues/699): \[javascript] Update Rhino library to 1.7.13
    *   [#2081](https://github.com/pmd/pmd/issues/2081): \[javascript] Failing with OutOfMemoryError parsing a Javascript file

### API Changes

#### Deprecated API

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/PMD.html#doPMD(net.sourceforge.pmd.PMDConfiguration)"><code>PMD#doPMD</code></a> is deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/PMD.html#runPMD(net.sourceforge.pmd.PMDConfiguration)"><code>PMD#runPMD</code></a> instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/PMD.html#run(java.lang.String[])"><code>PMD#run</code></a> is deprecated.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/PMD.html#runPMD(java.lang.String...)"><code>PMD#runPMD</code></a> instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/ThreadSafeReportListener.html#"><code>ThreadSafeReportListener</code></a> and the methods to use them in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/Report.html#"><code>Report</code></a>
    (<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/Report.html#addListener(net.sourceforge.pmd.ThreadSafeReportListener)"><code>addListener</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/Report.html#getListeners()"><code>getListeners</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.35.0/net/sourceforge/pmd/Report.html#addListeners(java.util.List)"><code>addListeners</code></a>)
    are deprecated. This functionality will be replaced by another TBD mechanism in PMD 7.

### External Contributions
*   [#3272](https://github.com/pmd/pmd/pull/3272): \[apex] correction for ApexUnitTestMethodShouldHaveIsTestAnnotation false positives - [William Brockhus](https://github.com/YodaDaCoda)
*   [#3246](https://github.com/pmd/pmd/pull/3246): \[java] New Rule: MutableStaticState - [Vsevolod Zholobov](https://github.com/vszholobov)
*   [#3247](https://github.com/pmd/pmd/pull/3247): \[java] New rule: JUnit5TestShouldBePackagePrivate - [Arnaud Jeansen](https://github.com/ajeans)
*   [#3293](https://github.com/pmd/pmd/pull/3293): \[java] Cognitive Complexity Metric - [Denis Borovikov](https://github.com/borovikovd)
*   [pmd.github.io#12](https://github.com/pmd/pmd.github.io/pull/12): Update quickstart.html - [Igor Lyadov](https://github.com/devigo)

### Stats
* 143 commits
* 53 closed tickets & PRs
* Days since last release: 34

## 24-April-2021 - 6.34.0

The PMD team is pleased to announce PMD 6.34.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [New rules](#new-rules)
    * [Modified rules](#modified-rules)
    * [Deprecated rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### New rules

*   The new Java rule [`UseStandardCharsets`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_bestpractices.html#usestandardcharsets) finds usages of `Charset.forName`,
    where `StandardCharsets` can be used instead.

    This rule is also part of the Quickstart Ruleset (`rulesets/java/quickstart.xml`) for Java.

*   The new Java rule [`UnnecessaryImport`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#unnecessaryimport) replaces the rules
    [`UnusedImports`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_bestpractices.html#unusedimports), [`DuplicateImports`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#duplicateimports),
    [`ImportFromSamePackage`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_errorprone.html#importfromsamepackage), and [`DontImportJavaLang`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#dontimportjavalang).

    This rule is also part of the Quickstart Ruleset (`rulesets/java/quickstart.xml`) for Java.

#### Modified rules

*   The Apex rule [`ApexCRUDViolation`](https://pmd.github.io/pmd-6.34.0/pmd_rules_apex_security.html#apexcrudviolation) does not ignore getters anymore and also flags
    SOQL/SOSL/DML operations without access permission checks in getters. This will produce false positives now for
    VF getter methods, but we can't reliably detect, whether a getter is a VF getter or not. In such cases,
    the violation should be [suppressed](pmd_userdocs_suppressing_warnings.html).

#### Deprecated rules

*   java-bestpractices
    *   [`UnusedImports`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_bestpractices.html#unusedimports): use the rule [`UnnecessaryImport`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#unnecessaryimport) instead

*   java-codestyle
    *   [`DuplicateImports`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#duplicateimports): use the rule [`UnnecessaryImport`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#unnecessaryimport) instead
    *   [`DontImportJavaLang`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#dontimportjavalang): use the rule [`UnnecessaryImport`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#unnecessaryimport) instead

*   java-errorprone
    *   [`ImportFromSamePackage`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_errorprone.html#importfromsamepackage): use the rule [`UnnecessaryImport`](https://pmd.github.io/pmd-6.34.0/pmd_rules_java_codestyle.html#unnecessaryimport) instead

### Fixed Issues

*   apex-performance
    *   [#3198](https://github.com/pmd/pmd/pull/3198): \[apex] OperationWithLimitsInLoopRule: Support more limit consuming static method invocations
*   apex-security
    *   [#3202](https://github.com/pmd/pmd/issues/3202): \[apex] ApexCRUDViolationRule fails to report CRUD violation on COUNT() queries
    *   [#3210](https://github.com/pmd/pmd/issues/3210): \[apex] ApexCRUDViolationRule false-negative on non-VF getter
*   java-bestpractices
    *   [#3190](https://github.com/pmd/pmd/issues/3190): \[java] Use StandardCharsets instead of Charset.forName
    *   [#3224](https://github.com/pmd/pmd/issues/3224): \[java] UnusedAssignment crashes with nested records
*   java-codestyle
    *   [#3128](https://github.com/pmd/pmd/issues/3128): \[java] New rule UnnecessaryImport, deprecate DuplicateImports, ImportFromSamePackage, UnusedImports
*   java-errorprone
    *   [#2757](https://github.com/pmd/pmd/issues/2757): \[java] CloseResource: support Lombok's @Cleanup annotation
    *   [#3169](https://github.com/pmd/pmd/issues/3169): \[java] CheckSkipResult: NPE when using pattern bindings

### API Changes

No changes.

### External Contributions

*   [#3193](https://github.com/pmd/pmd/pull/3193): \[java] New rule: UseStandardCharsets - [Andrea Aime](https://github.com/aaime)
*   [#3198](https://github.com/pmd/pmd/pull/3198): \[apex] OperationWithLimitsInLoopRule: Support more limit consuming static method invocations - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3211](https://github.com/pmd/pmd/pull/3211): \[apex] ApexCRUDViolationRule: Do not assume method is VF getter to avoid CRUD checks - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3234](https://github.com/pmd/pmd/pull/3234): \[apex] ApexCRUDViolation: COUNT is indeed CRUD checkable since it exposes data (false-negative) - [Jonathan Wiesel](https://github.com/jonathanwiesel)

### Stats
* 74 commits
* 18 closed tickets & PRs
* Days since last release: 27

## 27-March-2021 - 6.33.0

The PMD team is pleased to announce PMD 6.33.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [PLSQL parsing exclusions](#plsql-parsing-exclusions)
* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### PLSQL parsing exclusions

The PMD PLSQL parser might not parse every valid PL/SQL code without problems.
In order to still use PMD on such files, you can now mark certain lines for exclusion from
the parser. More information can be found in the [language specific documentation for PLSQL](pmd_languages_plsql.html).

### Fixed Issues

*   apex-design
    *   [#3142](https://github.com/pmd/pmd/issues/3142): \[apex] ExcessiveClassLength multiple warning on the same class
*   java
    *   [#3117](https://github.com/pmd/pmd/issues/3117): \[java] Infinite loop when parsing invalid code nested in lambdas
    *   [#3145](https://github.com/pmd/pmd/issues/3145): \[java] Parse exception when using "record" as variable name
*   java-bestpractices
    *   [#3118](https://github.com/pmd/pmd/issues/3118): \[java] UnusedPrivateMethod false positive when passing in lombok.val as argument
    *   [#3144](https://github.com/pmd/pmd/issues/3144): \[java] GuardLogStatement can have more detailed example
    *   [#3155](https://github.com/pmd/pmd/pull/3155): \[java] GuardLogStatement: False negative with unguarded method call
    *   [#3160](https://github.com/pmd/pmd/issues/3160): \[java] MethodReturnsInternalArray does not consider static final fields and fields initialized with empty array
*   java-errorprone
    *   [#2977](https://github.com/pmd/pmd/issues/2977): \[java] CloseResource: false positive with reassignment detection
    *   [#3146](https://github.com/pmd/pmd/issues/3146): \[java] InvalidLogMessageFormat detection failing when String.format used
    *   [#3148](https://github.com/pmd/pmd/issues/3148): \[java] CloseResource false positive with Objects.nonNull
    *   [#3165](https://github.com/pmd/pmd/issues/3165): \[java] InvalidLogMessageFormat detection failing when String.format used in a variable
*   java-performance
    *   [#2427](https://github.com/pmd/pmd/issues/2427): \[java] ConsecutiveLiteralAppend false-positive with builder inside lambda
    *   [#3152](https://github.com/pmd/pmd/issues/3152): \[java] ConsecutiveLiteralAppends and InsufficientStringBufferDeclaration: FP with switch expressions
*   plsql
    *   [#195](https://github.com/pmd/pmd/issues/195): \[plsql] Ampersand '&' causes PMD processing error in sql file - Lexical error in file

### External Contributions

*   [#3161](https://github.com/pmd/pmd/pull/3161): \[plsql] Add support for lexical parameters in SQL*Plus scripts, allow excluding lines which the parser does not understand - [Henning von Bargen](https://github.com/hvbtup)
*   [#3167](https://github.com/pmd/pmd/pull/3167): \[java] Minor typo in quickstart ruleset - [Austin Tice](https://github.com/AustinTice)

### Stats
* 49 commits
* 27 closed tickets & PRs
* Days since last release: 28

## 27-February-2021 - 6.32.0

The PMD team is pleased to announce PMD 6.32.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 16 Support](#java-16-support)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Experimental APIs](#experimental-apis)
    * [Internal API](#internal-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Java 16 Support

This release of PMD brings support for Java 16. PMD supports [JEP 394: Pattern Matching for instanceof](https://openjdk.java.net/jeps/394) and [JEP 395: Records](https://openjdk.java.net/jeps/395). Both have been promoted
to be a standard language feature of Java 16.

PMD also supports [JEP 397: Sealed Classes (Second Preview)](https://openjdk.java.net/jeps/397) as a preview
language feature. In order to analyze a project with PMD that uses these language features, you'll need to enable
it via the environment variable `PMD_JAVA_OPTS` and select the new language version `16-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 16-preview ...

Note: Support for Java 14 preview language features have been removed. The version "14-preview" is no longer available.

#### Modified Rules

*   The Apex rule [`ApexDoc`](https://pmd.github.io/pmd-6.32.0/pmd_rules_apex_documentation.html#apexdoc) has two new properties: `reportPrivate` and
    `reportProtected`. Previously the rule only considered public and global classes, methods, and
    properties. With these properties, you can verify the existence of ApexDoc comments for private
    and protected methods as well. By default, these properties are disabled to preserve backwards
    compatible behavior.

### Fixed Issues

*   apex-documentation
    *   [#3075](https://github.com/pmd/pmd/issues/3075): \[apex] ApexDoc should support private access modifier
*   java
    *   [#3101](https://github.com/pmd/pmd/issues/3101): \[java] NullPointerException when running PMD under JRE 11
*   java-bestpractices
    *   [#3132](https://github.com/pmd/pmd/issues/3132): \[java] UnusedImports with static imports on subclasses
*   java-errorprone
    *   [#2716](https://github.com/pmd/pmd/issues/2716): \[java] CompareObjectsWithEqualsRule: False positive with Enums
    *   [#3089](https://github.com/pmd/pmd/issues/3089): \[java] CloseResource rule throws exception on spaces in property types
    *   [#3133](https://github.com/pmd/pmd/issues/3133): \[java] InvalidLogMessageFormat FP with StringFormattedMessage and ParameterizedMessage
*   plsql
    *   [#3106](https://github.com/pmd/pmd/issues/3106): \[plsql] ParseException while parsing EXECUTE IMMEDIATE 'drop database link ' \|\| linkname;

### API Changes

#### Experimental APIs

*   The experimental class `ASTTypeTestPattern` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTTypePattern.html#"><code>ASTTypePattern</code></a>
    in order to align the naming to the JLS.
*   The experimental class `ASTRecordConstructorDeclaration` has been renamed to <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#"><code>ASTCompactConstructorDeclaration</code></a>
    in order to align the naming to the JLS.
*   The AST types and APIs around Pattern Matching and Records are not experimental anymore:
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#isPatternBinding()"><code>ASTVariableDeclaratorId#isPatternBinding</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTPattern.html#"><code>ASTPattern</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTTypePattern.html#"><code>ASTTypePattern</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTRecordDeclaration.html#"><code>ASTRecordDeclaration</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTRecordComponentList.html#"><code>ASTRecordComponentList</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTRecordComponent.html#"><code>ASTRecordComponent</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTRecordBody.html#"><code>ASTRecordBody</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/ast/ASTCompactConstructorDeclaration.html#"><code>ASTCompactConstructorDeclaration</code></a>

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   The protected or public member of the Java rule <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.32.0/net/sourceforge/pmd/lang/java/rule/bestpractices/AvoidUsingHardCodedIPRule.html#"><code>AvoidUsingHardCodedIPRule</code></a>
    are deprecated and considered to be internal API. They will be removed with PMD 7.

### External Contributions

*   [#3098](https://github.com/pmd/pmd/pull/3098): \[apex] ApexDoc optionally report private and protected - [Jonathan Wiesel](https://github.com/jonathanwiesel)
*   [#3107](https://github.com/pmd/pmd/pull/3107): \[plsql] Fix ParseException for EXECUTE IMMEDIATE str1\|\|str2; - [hvbtup](https://github.com/hvbtup)
*   [#3125](https://github.com/pmd/pmd/pull/3125): \[doc] Fix sample code indentation in documentation - [Artur Dryomov](https://github.com/arturdryomov)

### Stats
* 43 commits
* 21 closed tickets & PRs
* Days since last release: 27

## 30-January-2021 - 6.31.0

The PMD team is pleased to announce PMD 6.31.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [SARIF Format](#sarif-format)
    * [CPD](#cpd)
    * [New Rules](#new-rules)
    * [Deprecated rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
    * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### SARIF Format

PMD now supports the [Static Analysis Results Interchange Format (SARIF)](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=sarif)
as an additional report format. Just use the [command line parameter](pmd_userdocs_cli_reference.html#format) `-format sarif` to select it.
SARIF is an OASIS standard format for static analysis tools.
PMD creates SARIF JSON files in [SARIF version 2.1.0](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html).
An example report can be found in the documentation in [Report formats for PMD](pmd_userdocs_report_formats.html#sarif).

#### CPD

*   The C++ module now supports the new option [`--ignore-literal-sequences`](https://pmd.github.io/latest/pmd_userdocs_cpd.html#-ignore-literal-sequences),
    which can be used to avoid detection of some uninteresting clones. This options has been
    introduced with PMD 6.30.0 for C# and is now available for C++ as well. See [#2963](https://github.com/pmd/pmd/pull/2963).

#### New Rules

*   The new Apex rule [`OverrideBothEqualsAndHashcode`](https://pmd.github.io/pmd-6.31.0/pmd_rules_apex_errorprone.html#overridebothequalsandhashcode) brings the well known Java rule
    to Apex. In Apex the same principle applies: `equals` and `hashCode` should always be overridden
    together to ensure collection classes such as Maps and Sets work as expected.

*   The new Visualforce rule [`VfHtmlStyleTagXss`](https://pmd.github.io/pmd-6.31.0/pmd_rules_vf_security.html#vfhtmlstyletagxss) checks for potential XSS problems
    when using `<style>` tags on Visualforce pages.

#### Deprecated rules

*   java-performance
    *   [`AvoidUsingShortType`](https://pmd.github.io/pmd-6.31.0/pmd_rules_java_performance.html#avoidusingshorttype): arithmetic on shorts is not significantly
        slower than on ints, whereas using shorts may provide significant memory savings in arrays.
    *   [`SimplifyStartsWith`](https://pmd.github.io/pmd-6.31.0/pmd_rules_java_performance.html#simplifystartswith): the suggested code transformation has an
        insignificant performance impact, and decreases readability.

### Fixed Issues

*   core
    *   [#2953](https://github.com/pmd/pmd/issues/2953): \[core] Support SARIF JSON Format
    *   [#2970](https://github.com/pmd/pmd/issues/2970): \[core] PMD 6.30.0 release is not reproducible
    *   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings
*   java-bestpractices
    *   [#575](https://github.com/pmd/pmd/issues/575): \[java] LiteralsFirstInComparisons should consider constant fields
    *   [#2454](https://github.com/pmd/pmd/issues/2454): \[java] UnusedPrivateMethod violation for disabled class in 6.23.0
    *   [#2833](https://github.com/pmd/pmd/issues/2833): \[java] NPE in UseCollectionIsEmptyRule with enums
    *   [#2876](https://github.com/pmd/pmd/issues/2876): \[java] UnusedPrivateField cannot override ignored annotations property
    *   [#2957](https://github.com/pmd/pmd/issues/2957): \[java] Ignore unused declarations that have special name
*   java-codestyle
    *   [#2960](https://github.com/pmd/pmd/issues/2960): \[java] Thread issue in MethodNamingConventionsRule
*   java-design
    *   [#3006](https://github.com/pmd/pmd/issues/3006): \[java] NPE in SingularFieldRule with concise resource syntax
*   java-errorprone
    *   [#2976](https://github.com/pmd/pmd/issues/2976): \[java] CompareObjectsWithEquals: FP with array.length
    *   [#2977](https://github.com/pmd/pmd/issues/2977): \[java] 6.30.0 introduces new false positive in CloseResource rule?
    *   [#2979](https://github.com/pmd/pmd/issues/2979): \[java] UseEqualsToCompareStrings: FP with "var" variables
    *   [#3004](https://github.com/pmd/pmd/issues/3004): \[java] UseEqualsToCompareStrings false positive with PMD 6.30.0
    *   [#3062](https://github.com/pmd/pmd/issues/3062): \[java] CloseResource FP with reassigned stream
*   java-performance
    *   [#2296](https://github.com/pmd/pmd/issues/2296): \[java] Deprecate rule AvoidUsingShortType
    *   [#2740](https://github.com/pmd/pmd/issues/2740): \[java] Deprecate rule SimplifyStartsWith
    *   [#3088](https://github.com/pmd/pmd/issues/3088): \[java] AvoidInstantiatingObjectsInLoops - false positive with Collections
*   vf-security
    *   [#3081](https://github.com/pmd/pmd/issues/3081): \[vf] VfUnescapeEl: Inherently un-XSS-able built-in functions trigger false positives

### API Changes

#### Deprecated API

*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.31.0/net/sourceforge/pmd/lang/xml/rule/AbstractDomXmlRule.html#"><code>AbstractDomXmlRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.31.0/net/sourceforge/pmd/lang/wsdl/rule/AbstractWsdlRule.html#"><code>AbstractWsdlRule</code></a>
*   A few methods of <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.31.0/net/sourceforge/pmd/lang/xml/rule/AbstractXmlRule.html#"><code>AbstractXmlRule</code></a>

#### Experimental APIs

*   The method <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.31.0/net/sourceforge/pmd/lang/ast/GenericToken.html#getKind()"><code>GenericToken#getKind</code></a> has been added as experimental. This
    unifies the token interface for both JavaCC and Antlr. The already existing method
    <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.31.0/net/sourceforge/pmd/cpd/token/AntlrToken.html#getKind()"><code>AntlrToken#getKind</code></a> is therefore experimental as well. The
    returned constant depends on the actual language and might change whenever the grammar
    of the language is changed.

### External Contributions

*   [#2666](https://github.com/pmd/pmd/pull/2666): \[swift] Manage swift5 string literals - [kenji21](https://github.com/kenji21)
*   [#2959](https://github.com/pmd/pmd/pull/2959): \[apex] New Rule: override equals and hashcode rule - [recdevs](https://github.com/recdevs)
*   [#2963](https://github.com/pmd/pmd/pull/2963): \[cpp] Add option to ignore sequences of literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2964](https://github.com/pmd/pmd/pull/2964): \[cs] Update C# grammar for additional C# 7 and C# 8 features - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2965](https://github.com/pmd/pmd/pull/2965): \[cs] Improvements for ignore sequences of literals functionality - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2968](https://github.com/pmd/pmd/pull/2968): \[java] NPE in UseCollectionIsEmptyRule with enums - [foxmason](https://github.com/foxmason)
*   [#2983](https://github.com/pmd/pmd/pull/2983): \[java] LiteralsFirstInComparisons should consider constant fields - [Ozan Gulle](https://github.com/ozangulle)
*   [#2994](https://github.com/pmd/pmd/pull/2994): \[core] Fix code climate severity strings - [Vincent Maurin](https://github.com/vmaurin)
*   [#3005](https://github.com/pmd/pmd/pull/3005): \[vf] \[New Rule] Handle XSS violations that can occur within Html Style tags - [rmohan20](https://github.com/rmohan20)
*   [#3073](https://github.com/pmd/pmd/pull/3073): \[core] Include SARIF renderer - [Manuel Moya Ferrer](https://github.com/mmoyaferrer)
*   [#3084](https://github.com/pmd/pmd/pull/3084): \[vf] VfUnescapeEl false-positive with builtin functions - [Josh Feingold](https://github.com/jfeingold35)

### Stats
* 116 commits
* 40 closed tickets & PRs
* Days since last release: 49

## 12-December-2020 - 6.30.0

The PMD team is pleased to announce PMD 6.30.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
  * [CPD](#cpd)
  * [Type information for VisualForce](#type-information-for-visualforce)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
        * [Around RuleSet parsing](#around-ruleset-parsing)
        * [Around the `PMD` class](#around-the-`pmd`-class)
        * [Miscellaneous](#miscellaneous)
    * [Internal API](#internal-api)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

##### CPD

* The C# module now supports the new option [`--ignore-literal-sequences`](https://pmd.github.io/latest/pmd_userdocs_cpd.html#-ignore-literal-sequences), which can be used to avoid detection of some uninteresting clones. Support for other languages may be added in the future. See [#2945](https://github.com/pmd/pmd/pull/2945)

* The Scala module now supports [suppression](https://pmd.github.io/latest/pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs. See [#2929](https://github.com/pmd/pmd/pull/2929)


##### Type information for VisualForce

The Visualforce AST now can resolve the data type of Visualforce expressions that reference Apex Controller properties and Custom Object fields. This feature improves the precision of existing rules, like [`VfUnescapeEl`](https://pmd.github.io/pmd-6.30.0/pmd_rules_vf_security.html#vfunescapeel).

This can be configured using two environment variables:
* `PMD_VF_APEXDIRECTORIES`: Comma separated list of directories for Apex classes. Absolute or relative to the Visualforce directory. Default is `../classes`. Specifying an empty string will disable data type resolution for Apex Controller properties.
* `PMD_VF_OBJECTSDIRECTORIES`: Comma separated list of directories for Custom Objects. Absolute or relative to the Visualforce directory. Default is `../objects`. Specifying an empty string will disable data type resolution for Custom Object fields.

This feature is experimental, in particular, expect changes to the way the configuration is specified. We'll probably extend the CLI instead of relying on environment variables in a future version.

Thanks to Jeff Bartolotta and Roopa Mohan for contributing this!

### Fixed Issues

*   core
    * [#1939](https://github.com/pmd/pmd/issues/1939): \[core] XPath expressions return handling
    * [#1961](https://github.com/pmd/pmd/issues/1961): \[core] Text renderer should include name of violated rule
    * [#2874](https://github.com/pmd/pmd/pull/2874): \[core] Fix XMLRenderer with UTF-16
*   cs
    * [#2938](https://github.com/pmd/pmd/pull/2938): \[cs] CPD: ignoring using directives could not be disabled
*   java
    * [#2911](https://github.com/pmd/pmd/issues/2911): \[java] `ClassTypeResolver#searchNodeNameForClass` leaks memory
    * [#2934](https://github.com/pmd/pmd/pull/2934): \[java] CompareObjectsWithEquals / UseEqualsToCompareStrings - False negatives with fields
    * [#2940](https://github.com/pmd/pmd/pull/2940): \[java] Catch additional TypeNotPresentExceptions / LinkageErrors
*   scala
    * [#2480](https://github.com/pmd/pmd/issues/2480): \[scala] Support CPD suppressions


### API Changes

#### Deprecated API


##### Around RuleSet parsing

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a> and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#"><code>RulesetsFactoryUtils</code></a> have been deprecated in favor of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetLoader.html#"><code>RuleSetLoader</code></a>. This is easier to configure, and more maintainable than the multiple overloads of `RulesetsFactoryUtils`.
* Some static creation methods have been added to <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSet.html#"><code>RuleSet</code></a> for simple cases, eg <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSet.html#forSingleRule(net.sourceforge.pmd.Rule)"><code>forSingleRule</code></a>. These replace some counterparts in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a>
* Since <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSets.html#"><code>RuleSets</code></a> is also deprecated, many APIs that require a RuleSets instance now are deprecated, and have a counterpart that expects a `List<RuleSet>`.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetReferenceId.html#"><code>RuleSetReferenceId</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetReference.html#"><code>RuleSetReference</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/RuleSetFactoryCompatibility.html#"><code>RuleSetFactoryCompatibility</code></a> are deprecated. They are most likely not relevant outside of the implementation of pmd-core.

##### Around the `PMD` class

Many classes around PMD's entry point (<a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a>) have been deprecated as internal, including:
* The contents of the packages <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/cli/package-summary.html#"><code>net.sourceforge.pmd.cli</code></a>, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/processor/package-summary.html#"><code>net.sourceforge.pmd.processor</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/SourceCodeProcessor.html#"><code>SourceCodeProcessor</code></a>
* The constructors of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/PMD.html#"><code>PMD</code></a> (the class will be made a utility class)

##### Miscellaneous

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.30.0/net/sourceforge/pmd/lang/java/ast/ASTPackageDeclaration.html#getPackageNameImage()"><code>ASTPackageDeclaration#getPackageNameImage</code></a>,
    <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.30.0/net/sourceforge/pmd/lang/java/ast/ASTTypeParameter.html#getParameterName()"><code>ASTTypeParameter#getParameterName</code></a>
    and the corresponding XPath attributes. In both cases they're replaced with a new method `getName`,
    the attribute is `@Name`.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.30.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceBody.html#isAnonymousInnerClass()"><code>ASTClassOrInterfaceBody#isAnonymousInnerClass</code></a>,
    and <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.30.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceBody.html#isEnumChild()"><code>ASTClassOrInterfaceBody#isEnumChild</code></a>,
    refs [#905](https://github.com/pmd/pmd/issues/905)

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.30.0/net/sourceforge/pmd/lang/ecmascript/Ecmascript3Handler.html#"><code>net.sourceforge.pmd.lang.ecmascript.Ecmascript3Handler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.30.0/net/sourceforge/pmd/lang/ecmascript/Ecmascript3Parser.html#"><code>net.sourceforge.pmd.lang.ecmascript.Ecmascript3Parser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.30.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptParser.html#parserOptions"><code>EcmascriptParser#parserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.30.0/net/sourceforge/pmd/lang/ecmascript/ast/EcmascriptParser.html#getSuppressMap()"><code>EcmascriptParser#getSuppressMap</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/lang/rule/ParametricRuleViolation.html#"><code>net.sourceforge.pmd.lang.rule.ParametricRuleViolation</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.30.0/net/sourceforge/pmd/lang/ParserOptions.html#suppressMarker"><code>ParserOptions#suppressMarker</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.30.0/net/sourceforge/pmd/lang/modelica/rule/ModelicaRuleViolationFactory.html#"><code>net.sourceforge.pmd.lang.modelica.rule.ModelicaRuleViolationFactory</code></a>


### External Contributions

*   [#2864](https://github.com/pmd/pmd/pull/2864): [vf] Provide expression type information to Visualforce rules to avoid false positives - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)
*   [#2914](https://github.com/pmd/pmd/pull/2914): \[core] Include rule name in text renderer - [Gunther Schrijvers](https://github.com/GuntherSchrijvers)
*   [#2925](https://github.com/pmd/pmd/pull/2925): Cleanup: Correct annotation array initializer indents from checkstyle #8083 - [Abhishek Kumar](https://github.com/Abhishek-kumar09)
*   [#2929](https://github.com/pmd/pmd/pull/2929): \[scala] Add support for CPD-ON and CPD-OFF special comments - [Andy Robinson](https://github.com/andyrobinson)
*   [#2936](https://github.com/pmd/pmd/pull/2936): \[java] (doc) Fix typo: "an accessor" not "a" - [Igor Moreno](https://github.com/igormoreno)
*   [#2938](https://github.com/pmd/pmd/pull/2938): \[cs] CPD: fix issue where ignoring using directives could not be disabled - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2945](https://github.com/pmd/pmd/pull/2945): \[cs] Add option to ignore sequences of literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2962](https://github.com/pmd/pmd/pull/2962): \[cpp] Add support for C++ 14 binary literals - [Maikel Steneker](https://github.com/maikelsteneker)

### Stats
* 190 commits
* 25 closed tickets & PRs
* Days since last release: 49

## 24-October-2020 - 6.29.0

The PMD team is pleased to announce PMD 6.29.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [New Rules](#new-rules)
    * [Renamed Rules](#renamed-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Updated Apex Support

*   The Apex language support has been bumped to version 50 (Winter '21). All new language features are now properly
    parsed and processed. Especially the [Safe Navigation Operator](https://releasenotes.docs.salesforce.com/en-us/winter21/release-notes/rn_apex_SafeNavigationOperator.htm) is now supported.
    See also [Salesforce Winter '21 Release Notes](https://releasenotes.docs.salesforce.com/en-us/winter21/release-notes/rn_apex.htm)

#### New Rules

*   The new Apex rule [`OperationWithLimitsInLoop`](https://pmd.github.io/pmd-6.29.0/pmd_rules_apex_performance.html#operationwithlimitsinloop) (`apex-performance`)
    finds operations in loops that may hit governor limits such as DML operations, SOQL
    queries and more. The rule replaces the three rules "AvoidDmlStatementsInLoops", "AvoidSoqlInLoops",
    and "AvoidSoslInLoops".

#### Renamed Rules

*   The Java rule [`DoNotCallSystemExit`](https://pmd.github.io/pmd-6.29.0/pmd_rules_java_errorprone.html#donotcallsystemexit) has been renamed to
    [`DoNotTerminateVM`](https://pmd.github.io/pmd-6.29.0/pmd_rules_java_errorprone.html#donotterminatevm), since it checks for all the following calls:
    `System.exit(int)`, `Runtime.exit(int)`, `Runtime.halt(int)`. All these calls terminate
    the Java VM, which is bad, if the VM runs an application server which many independent applications.

#### Deprecated Rules

*   The Apex rules [`AvoidDmlStatementsInLoops`](https://pmd.github.io/pmd-6.29.0/pmd_rules_apex_performance.html#avoiddmlstatementsinloops),
    [`AvoidSoqlInLoops`](https://pmd.github.io/pmd-6.29.0/pmd_rules_apex_performance.html#avoidsoqlinloops) and [`AvoidSoslInLoops`](https://pmd.github.io/pmd-6.29.0/pmd_rules_apex_performance.html#avoidsoslinloops)
    (`apex-performance`) are deprecated in favour of the new rule
    [`OperationWithLimitsInLoop`](https://pmd.github.io/pmd-6.29.0/pmd_rules_apex_performance.html#operationwithlimitsinloop). The deprecated rules will be removed
    with PMD 7.0.0.

### Fixed Issues

*   apex
    *   [#2839](https://github.com/pmd/pmd/issues/2839): \[apex] Apex classes with safe navigation operator from Winter 21 (50.0) are skipped
*   apex-performance
    *   [#1713](https://github.com/pmd/pmd/issues/1713): \[apex] Mark Database DML statements in For Loop
*   core
    *   [#2831](https://github.com/pmd/pmd/pull/2831): \[core] Fix XMLRenderer newlines when running under IBM Java
*   java-errorprone
    *   [#2157](https://github.com/pmd/pmd/issues/2157): \[java] Improve DoNotCallSystemExit: permit call in main(), flag System.halt
    *   [#2764](https://github.com/pmd/pmd/issues/2764): \[java] CloseResourceRule does not recognize multiple assignment done to resource
*   miscellaneous
    *   [#2823](https://github.com/pmd/pmd/issues/2823): \[doc] Renamed/Moved rules are missing in documentation
*   vf (Salesforce VisualForce)
    *   [#2765](https://github.com/pmd/pmd/issues/2765): \[vf] Attributes with dot cause a VfParseException

### External Contributions

*   [#2803](https://github.com/pmd/pmd/pull/2803): \[java] Improve DoNotCallSystemExit (Fixes #2157) - [Vitaly Polonetsky](https://github.com/mvitaly)
*   [#2809](https://github.com/pmd/pmd/pull/2809): \[java] Move test config from file to test class - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2810](https://github.com/pmd/pmd/pull/2810): \[core] Move method "renderTempFile" to XMLRendererTest - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2811](https://github.com/pmd/pmd/pull/2811): \[java] CloseResource - Fix #2764: False-negative when re-assigning variable - [Andi Pabst](https://github.com/andipabst)
*   [#2813](https://github.com/pmd/pmd/pull/2813): \[core] Use JUnit's TemporaryFolder rule - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2816](https://github.com/pmd/pmd/pull/2816): \[apex] Detect 'Database' method invocations inside loops - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)
*   [#2829](https://github.com/pmd/pmd/pull/2829): \[doc] Small correction in pmd\_report\_formats.md - [Gustavo Krieger](https://github.com/gustavopcassol)
*   [#2834](https://github.com/pmd/pmd/pull/2834): \[vf] Allow attributes with dot in Visualforce - [rmohan20](https://github.com/rmohan20)
*   [#2842](https://github.com/pmd/pmd/pull/2842): \[core] Bump antlr4 from 4.7 to 4.7.2 - [Adrien Lecharpentier](https://github.com/alecharp)
*   [#2865](https://github.com/pmd/pmd/pull/2865): \[java] (doc) Update ExcessiveImports example code for clarity - [Gustavo Krieger](https://github.com/gustavopcassol)
*   [#2866](https://github.com/pmd/pmd/pull/2866): \[java] (doc) Fix example for CouplingBetweenObjects - [Gustavo Krieger](https://github.com/gustavopcassol)

### Stats
* 50 commits
* 23 closed tickets & PRs
* Days since last release: 27

## 26-September-2020 - 6.28.0

The PMD team is pleased to announce PMD 6.28.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [CPD's AnyTokenizer has been improved](#cpd's-anytokenizer-has-been-improved)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### CPD's AnyTokenizer has been improved

The AnyTokenizer is used for languages, that don't have an own lexer/grammar based tokenizer.
AnyTokenizer now handles string literals and end-of-line comments. Fortran, Perl and Ruby have
been updated to use AnyTokenizer instead of their old custom tokenizer based on AbstractTokenizer.
See [#2758](https://github.com/pmd/pmd/pull/2758) for details.

AbstractTokenizer and the custom tokenizers of Fortran, Perl and Ruby are deprecated now.

### Fixed Issues

* cpd
    * [#2758](https://github.com/pmd/pmd/pull/2758): \[cpd] Improve AnyTokenizer
    * [#2760](https://github.com/pmd/pmd/issues/2760): \[cpd] AnyTokenizer doesn't count columns correctly

* apex-security
    * [#2774](https://github.com/pmd/pmd/issues/2774): \[apex] ApexSharingViolations does not correlate sharing settings with class that contains data access

* java
    * [#2738](https://github.com/pmd/pmd/issues/2738): \[java] Custom rule with @ExhaustiveEnumSwitch throws NPE
    * [#2755](https://github.com/pmd/pmd/issues/2755): \[java] \[6.27.0] Exception applying rule CloseResource on file ... java.lang.NullPointerException
    * [#2756](https://github.com/pmd/pmd/issues/2756): \[java] TypeTestUtil fails with NPE for anonymous class
    * [#2767](https://github.com/pmd/pmd/issues/2767): \[java] IndexOutOfBoundsException when parsing an initializer BlockStatement
    * [#2783](https://github.com/pmd/pmd/issues/2783): \[java] Error while parsing with lambda of custom interface
* java-bestpractices
    * [#2759](https://github.com/pmd/pmd/issues/2759): \[java] False positive in UnusedAssignment
* java-design
    * [#2708](https://github.com/pmd/pmd/issues/2708): \[java] False positive FinalFieldCouldBeStatic when using lombok Builder.Default


### API Changes

#### Deprecated API

##### For removal

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/RuleViolationComparator.html#"><code>net.sourceforge.pmd.RuleViolationComparator</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/RuleViolation.html#DEFAULT_COMPARATOR"><code>RuleViolation#DEFAULT_COMPARATOR</code></a> instead.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/cpd/AbstractTokenizer.html#"><code>net.sourceforge.pmd.cpd.AbstractTokenizer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>net.sourceforge.pmd.cpd.AnyTokenizer</code></a> instead.
* <a href="https://docs.pmd-code.org/apidocs/pmd-fortran/6.28.0/net/sourceforge/pmd/cpd/FortranTokenizer.html#"><code>net.sourceforge.pmd.cpd.FortranTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>AnyTokenizer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-fortran/6.28.0/net/sourceforge/pmd/cpd/FortranLanguage.html#getTokenizer()"><code>FortranLanguage#getTokenizer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-perl/6.28.0/net/sourceforge/pmd/cpd/PerlTokenizer.html#"><code>net.sourceforge.pmd.cpd.PerlTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>AnyTokenizer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-perl/6.28.0/net/sourceforge/pmd/cpd/PerlLanguage.html#getTokenizer()"><code>PerlLanguage#getTokenizer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-ruby/6.28.0/net/sourceforge/pmd/cpd/RubyTokenizer.html#"><code>net.sourceforge.pmd.cpd.RubyTokenizer</code></a>. Was replaced by an <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/cpd/AnyTokenizer.html#"><code>AnyTokenizer</code></a>. Use <a href="https://docs.pmd-code.org/apidocs/pmd-ruby/6.28.0/net/sourceforge/pmd/cpd/RubyLanguage.html#getTokenizer()"><code>RubyLanguage#getTokenizer</code></a> anyway.
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/lang/rule/RuleReference.html#getOverriddenLanguage()"><code>RuleReference#getOverriddenLanguage</code></a> and
  <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.28.0/net/sourceforge/pmd/lang/rule/RuleReference.html#setLanguage(net.sourceforge.pmd.lang.Language)"><code>RuleReference#setLanguage</code></a>
* Antlr4 generated lexers:
    * <a href="https://docs.pmd-code.org/apidocs/pmd-cs/6.28.0/net/sourceforge/pmd/lang/cs/antlr4/CSharpLexer.html#"><code>net.sourceforge.pmd.lang.cs.antlr4.CSharpLexer</code></a> will be moved to package `net.sourceforge.pmd.lang.cs.ast` with PMD 7.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-dart/6.28.0/net/sourceforge/pmd/lang/dart/antlr4/Dart2Lexer.html#"><code>net.sourceforge.pmd.lang.dart.antlr4.Dart2Lexer</code></a> will be renamed to `DartLexer` and moved to package
      `net.sourceforge.pmd.lang.dart.ast` with PMD 7. All other classes in the old package will be removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-go/6.28.0/net/sourceforge/pmd/lang/go/antlr4/GolangLexer.html#"><code>net.sourceforge.pmd.lang.go.antlr4.GolangLexer</code></a> will be moved to package
      `net.sourceforge.pmd.lang.go.ast` with PMD 7. All other classes in the old package will be removed.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-kotlin/6.28.0/net/sourceforge/pmd/lang/kotlin/antlr4/Kotlin.html#"><code>net.sourceforge.pmd.lang.kotlin.antlr4.Kotlin</code></a> will be renamed to `KotlinLexer` and moved to package
      `net.sourceforge.pmd.lang.kotlin.ast` with PMD 7.
    * <a href="https://docs.pmd-code.org/apidocs/pmd-lua/6.28.0/net/sourceforge/pmd/lang/lua/antlr4/LuaLexer.html#"><code>net.sourceforge.pmd.lang.lua.antlr4.LuaLexer</code></a> will be moved to package
      `net.sourceforge.pmd.lang.lua.ast` with PMD 7. All other classes in the old package will be removed.


### External Contributions

* [#2735](https://github.com/pmd/pmd/pull/2735): \[ci] Add github actions for a fast view of pr succeed/not - [XenoAmess](https://github.com/XenoAmess)
* [#2747](https://github.com/pmd/pmd/pull/2747): \[java] Don't trigger FinalFieldCouldBeStatic when field is annotated with lombok @Builder.Default - [Ollie Abbey](https://github.com/ollieabbey)
* [#2773](https://github.com/pmd/pmd/pull/2773): \[java] issue-2738: Adding null check to avoid npe when switch case is default - [Nimit Patel](https://github.com/nimit-patel)
* [#2789](https://github.com/pmd/pmd/pull/2789): Add badge for reproducible build - [Dan Rollo](https://github.com/bhamail)
* [#2791](https://github.com/pmd/pmd/pull/2791): \[apex] Analyze inner classes for sharing violations - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)

### Stats
* 58 commits
* 24 closed tickets & PRs
* Days since last release: 25

## 31-August-2020 - 6.27.0

The PMD team is pleased to announce PMD 6.27.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 15 Support](#java-15-support)
    * [Changes in how tab characters are handled](#changes-in-how-tab-characters-are-handled)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Java 15 Support

This release of PMD brings support for Java 15. PMD can parse [Text Blocks](https://openjdk.java.net/jeps/378)
which have been promoted to be a standard language feature of Java.

PMD also supports [Pattern Matching for instanceof](https://openjdk.java.net/jeps/375),
[Records](https://openjdk.java.net/jeps/384), and [Sealed Classes](https://openjdk.java.net/jeps/360).

Note: The Pattern Matching for instanceof, Records, and Sealed Classes are all preview language features of OpenJDK 15
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS` and select the new language version `15-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 15-preview ...

Note: Support for Java 13 preview language features have been removed. The version "13-preview" is no longer available.

#### Changes in how tab characters are handled

In the past, tab characters in source files has been handled differently in different languages by PMD.
For instance in Java, tab characters had a width of 8 columns, while C# used only 1 column. Visualforce instead
used 4 columns.

This has been unified now so that tab characters are consistently now always 1 column wide.

This however might be a **incompatible** change, if you're using the properties "BeginColumn" or "EndColumn"
additionally to "BeginLine" and "EndLine" of a Token/AST node in order to highlight
where a rule violation occurred in the source file. If you have logic there that deals with tab characters,
you most likely can remove this logic now, since tab characters are now just "normal" characters
in terms of string processing.

See also [[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently #2656](https://github.com/pmd/pmd/pull/2656).

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.27.0).

#### New Rules

*   The new Java rule [`AvoidReassigningCatchVariables`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_bestpractices.html#avoidreassigningcatchvariables) (`java-bestpractices`) finds
    cases where the variable of the caught exception is reassigned. This practice is surprising and prevents
    further evolution of the code like multi-catch.

#### Modified Rules

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) has a new property
    `closeNotInFinally`. With this property set to `true` the rule will also find calls to close a
    resource, which are not in a finally-block of a try-statement. If a resource is not closed within a
    finally block, it might not be closed at all in case of exceptions.

    As this new detection would yield many new violations, it is disabled by default. It might be
    enabled in a later version of PMD.

#### Deprecated Rules

*   The Java rule [`DataflowAnomalyAnalysis`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_errorprone.html#dataflowanomalyanalysis) (`java-errorprone`)
    is deprecated in favour of [`UnusedAssignment`](https://pmd.github.io/pmd-6.27.0/pmd_rules_java_bestpractices.html#unusedassignment) (`java-bestpractices`),
    which was introduced in PMD 6.26.0.

### Fixed Issues

*   core
    *   [#724](https://github.com/pmd/pmd/issues/724): \[core] Avoid parsing rulesets multiple times
    *   [#1962](https://github.com/pmd/pmd/issues/1962): \[core] Simplify Report API
    *   [#2653](https://github.com/pmd/pmd/issues/2653): \[lang-test] Upgrade kotlintest to Kotest
    *   [#2656](https://github.com/pmd/pmd/pull/2656): \[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently
    *   [#2690](https://github.com/pmd/pmd/pull/2690): \[core] Fix java7 compatibility
*   java
    *   [#2646](https://github.com/pmd/pmd/issues/2646): \[java] Support JDK 15
*   java-bestpractices
    *   [#2471](https://github.com/pmd/pmd/issues/2471): \[java] New Rule: AvoidReassigningCatchVariables
    *   [#2663](https://github.com/pmd/pmd/issues/2663): \[java] NoClassDefFoundError on upgrade from 6.25.0 to 6.26.0
    *   [#2668](https://github.com/pmd/pmd/issues/2668): \[java] UnusedAssignment false positives
    *   [#2673](https://github.com/pmd/pmd/issues/2673): \[java] UnusedPrivateField and SingularField false positive with lombok annotation EqualsAndHashCode
    *   [#2684](https://github.com/pmd/pmd/issues/2684): \[java] UnusedAssignment FP in try/catch
    *   [#2686](https://github.com/pmd/pmd/issues/2686): \[java] UnusedAssignment must not flag abstract method parameters in interfaces and abstract classes
*   java-design
    *   [#2108](https://github.com/pmd/pmd/issues/2108): \[java] \[doc] ImmutableField rule: Description should clarify shallow immutability
    *   [#2461](https://github.com/pmd/pmd/issues/2461): \[java] ExcessiveParameterListRule must ignore a private constructor
*   java-errorprone
    *   [#2264](https://github.com/pmd/pmd/issues/2264): \[java] SuspiciousEqualsMethodName: Improve description about error-prone overloading of equals()
    *   [#2410](https://github.com/pmd/pmd/issues/2410): \[java] ProperCloneImplementation not valid for final class
    *   [#2431](https://github.com/pmd/pmd/issues/2431): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message
    *   [#2439](https://github.com/pmd/pmd/issues/2439): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t)
    *   [#2470](https://github.com/pmd/pmd/issues/2470): \[java] CloseResource false positive when resource included in return value
    *   [#2531](https://github.com/pmd/pmd/issues/2531): \[java] UnnecessaryCaseChange can not detect the case like: foo.equals(bar.toLowerCase())
    *   [#2647](https://github.com/pmd/pmd/issues/2647): \[java] Deprecate rule DataFlowAnomalyAnalysis
*   java-performance
    *   [#1868](https://github.com/pmd/pmd/issues/1868): \[java] false-positive for SimplifyStartsWith if string is empty
    *   [#2441](https://github.com/pmd/pmd/issues/2441): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';`
    *   [#2530](https://github.com/pmd/pmd/issues/2530): \[java] StringToString can not detect the case: getStringMethod().toString()
*   dart
    *   [#2750](https://github.com/pmd/pmd/pull/2750): \[dart] \[cpd] Cpd Dart escaped dollar


### API Changes

*   XML rule definition in rulesets: In PMD 7, the `language` attribute will be required on all `rule`
    elements that declare a new rule. Some base rule classes set the language implicitly in their
    constructor, and so this is not required in all cases for the rule to work. But this
    behavior will be discontinued in PMD 7, so missing `language` attributes are now
    reported as a forward compatibility warning.

#### Deprecated API

##### For removal

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Rule.html#getParserOptions()"><code>Rule#getParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/lang/Parser.html#getParserOptions()"><code>Parser#getParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/lang/AbstractParser.html#"><code>AbstractParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/RuleContext.html#removeAttribute(java.lang.String)"><code>RuleContext#removeAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/RuleContext.html#getAttribute(java.lang.String)"><code>RuleContext#getAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/RuleContext.html#setAttribute(java.lang.String,java.lang.Object)"><code>RuleContext#setAttribute</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.27.0/net/sourceforge/pmd/lang/apex/ApexParserOptions.html#"><code>ApexParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/ast/ASTThrowStatement.html#getFirstClassOrInterfaceTypeImage()"><code>ASTThrowStatement#getFirstClassOrInterfaceTypeImage</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.27.0/net/sourceforge/pmd/lang/ecmascript/EcmascriptParserOptions.html#"><code>EcmascriptParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.27.0/net/sourceforge/pmd/lang/ecmascript/rule/EcmascriptXPathRule.html#"><code>EcmascriptXPathRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.27.0/net/sourceforge/pmd/lang/xml/XmlParserOptions.html#"><code>XmlParserOptions</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.27.0/net/sourceforge/pmd/lang/xml/rule/XmlXPathRule.html#"><code>XmlXPathRule</code></a>
*   Properties of <a href="https://docs.pmd-code.org/apidocs/pmd-xml/6.27.0/net/sourceforge/pmd/lang/xml/rule/AbstractXmlRule.html#"><code>AbstractXmlRule</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Report.ReadableDuration.html#"><code>net.sourceforge.pmd.Report.ReadableDuration</code></a>
*   Many methods of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Report.html#"><code>net.sourceforge.pmd.Report</code></a>. They are replaced by accessors
    that produce a List. For example, <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Report.html#iterator()"><code>iterator()</code></a>
    (and implementing Iterable) and <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Report.html#isEmpty()"><code>isEmpty()</code></a> are both
    replaced by <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/Report.html#getViolations()"><code>getViolations()</code></a>.

*   The dataflow codebase is deprecated for removal in PMD 7. This
    includes all code in the following packages, and their subpackages:
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.27.0/net/sourceforge/pmd/lang/plsql/dfa/package-summary.html#"><code>net.sourceforge.pmd.lang.plsql.dfa</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/dfa/package-summary.html#"><code>net.sourceforge.pmd.lang.java.dfa</code></a>
    *   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.27.0/net/sourceforge/pmd/lang/dfa/package-summary.html#"><code>net.sourceforge.pmd.lang.dfa</code></a>
    *   and the class <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.27.0/net/sourceforge/pmd/lang/plsql/PLSQLDataFlowHandler.html#"><code>PLSQLDataFlowHandler</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.27.0/net/sourceforge/pmd/lang/vf/VfSimpleCharStream.html#"><code>VfSimpleCharStream</code></a>

*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.27.0/net/sourceforge/pmd/lang/jsp/ast/ASTJspDeclarations.html#"><code>ASTJspDeclarations</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.27.0/net/sourceforge/pmd/lang/jsp/ast/ASTJspDocument.html#"><code>ASTJspDocument</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/6.27.0/net/sourceforge/pmd/lang/scala/ast/ScalaParserVisitorAdapter.html#zero()"><code>ScalaParserVisitorAdapter#zero</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-scala_2.13/6.27.0/net/sourceforge/pmd/lang/scala/ast/ScalaParserVisitorAdapter.html#combine(Object,Object)"><code>ScalaParserVisitorAdapter#combine</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.27.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitorReducedAdapter.html#"><code>ApexParserVisitorReducedAdapter</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorReducedAdapter.html#"><code>JavaParserVisitorReducedAdapter</code></a>

* <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#"><code>TypeHelper</code></a> is deprecated in
  favor of <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/types/TypeTestUtil.html#"><code>TypeTestUtil</code></a>, which has the
  same functionality, but a slightly changed API.
* Many of the classes in <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.27.0/net/sourceforge/pmd/lang/java/symboltable/package-summary.html#"><code>net.sourceforge.pmd.lang.java.symboltable</code></a>
  are deprecated as internal API.


### External Contributions

*   [#2656](https://github.com/pmd/pmd/pull/2656): \[all] Ensure PMD/CPD uses tab width of 1 for tabs consistently - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2659](https://github.com/pmd/pmd/pull/2659): \[java] StringToString can not detect the case: getStringMethod().toString() - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2662](https://github.com/pmd/pmd/pull/2662): \[java] UnnecessaryCaseChange can not detect the case like: foo.equals(bar.toLowerCase()) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2671](https://github.com/pmd/pmd/pull/2671): \[java] CloseResource false positive when resource included in return value - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2674](https://github.com/pmd/pmd/pull/2674): \[java] add lombok.EqualsAndHashCode in AbstractLombokAwareRule - [berkam](https://github.com/berkam)
*   [#2677](https://github.com/pmd/pmd/pull/2677): \[java] RedundantFieldInitializer can not detect a special case for char initialize: `char foo = '\0';` - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2678](https://github.com/pmd/pmd/pull/2678): \[java] AvoidCatchingThrowable can not detect the case: catch (java.lang.Throwable t) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2679](https://github.com/pmd/pmd/pull/2679): \[java] InvalidLogMessageFormatRule throws IndexOutOfBoundsException when only logging exception message - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2682](https://github.com/pmd/pmd/pull/2682): \[java] New Rule: AvoidReassigningCatchVariables - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2697](https://github.com/pmd/pmd/pull/2697): \[java] ExcessiveParameterListRule must ignore a private constructor - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2699](https://github.com/pmd/pmd/pull/2699): \[java] ProperCloneImplementation not valid for final class - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2700](https://github.com/pmd/pmd/pull/2700): \[java] Fix OnlyOneReturn code example - [Jan-Lukas Else](https://github.com/jlelse)
*   [#2722](https://github.com/pmd/pmd/pull/2722): \[doc] \[java] ImmutableField: extend description, fixes #2108 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2723](https://github.com/pmd/pmd/pull/2723): \[doc] \[java] SimplifyStartsWith: update description and example, fixes #1868 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2724](https://github.com/pmd/pmd/pull/2724): \[doc] [java] SuspiciousEqualsMethodName: update description, fixes #2264 - [Mateusz Stefanski](https://github.com/mateusz-stefanski)
*   [#2725](https://github.com/pmd/pmd/pull/2725): Cleanup: change valueOf to parse when we need primitive return value. - [XenoAmess](https://github.com/XenoAmess)
*   [#2726](https://github.com/pmd/pmd/pull/2726): Cleanup: replace StringBuffer with StringBuilder - [XenoAmess](https://github.com/XenoAmess)
*   [#2727](https://github.com/pmd/pmd/pull/2727): Cleanup: replace indexOf() < 0 with contains - [XenoAmess](https://github.com/XenoAmess)
*   [#2728](https://github.com/pmd/pmd/pull/2728): Cleanup: javadoc issues - [XenoAmess](https://github.com/XenoAmess)
*   [#2729](https://github.com/pmd/pmd/pull/2729): Cleanup: use print instead of printf if no format exists - [XenoAmess](https://github.com/XenoAmess)
*   [#2730](https://github.com/pmd/pmd/pull/2730): Cleanup: StringBuilder issues - [XenoAmess](https://github.com/XenoAmess)
*   [#2731](https://github.com/pmd/pmd/pull/2731): Cleanup: avoid compiling Patterns repeatedly - [XenoAmess](https://github.com/XenoAmess)
*   [#2732](https://github.com/pmd/pmd/pull/2732): Cleanup: use StandardCharsets instead of Charset.forName - [XenoAmess](https://github.com/XenoAmess)
*   [#2733](https://github.com/pmd/pmd/pull/2733): Cleanup: Collection::addAll issues  - [XenoAmess](https://github.com/XenoAmess)
*   [#2734](https://github.com/pmd/pmd/pull/2734): Cleanup: use try with resources - [XenoAmess](https://github.com/XenoAmess)
*   [#2744](https://github.com/pmd/pmd/pull/2744): Cleanup: fix typos - [XenoAmess](https://github.com/XenoAmess)
*   [#2745](https://github.com/pmd/pmd/pull/2745): \[core] Fix a NPE in buildUsageText - [XenoAmess](https://github.com/XenoAmess)
*   [#2749](https://github.com/pmd/pmd/pull/2749): \[dart] \[cpd] Improvements for Dart interpolated strings - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2750](https://github.com/pmd/pmd/pull/2750): \[dart] \[cpd] Cpd Dart escaped dollar - [Maikel Steneker](https://github.com/maikelsteneker)


### Stats
* 189 commits
* 68 closed tickets & PRs
* Days since last release: 37

## 25-July-2020 - 6.26.0

The PMD team is pleased to announce PMD 6.26.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [New Rules](#new-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated API](#deprecated-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### New Rules

*   The new Java rule [`UnusedAssignment`](https://pmd.github.io/pmd-6.26.0/pmd_rules_java_bestpractices.html#unusedassignment) (`java-bestpractices`) finds assignments
    to variables, that are never used and are useless. The new rule is supposed to entirely replace
    [`DataflowAnomalyAnalysis`](https://pmd.github.io/pmd-6.26.0/pmd_rules_java_errorprone.html#dataflowanomalyanalysis).

#### Modified rules

*   The Java rule [`ArrayIsStoredDirectly`](https://pmd.github.io/pmd-6.26.0/pmd_rules_java_bestpractices.html#arrayisstoreddirectly) (`java-bestpractices`) now ignores
    by default private methods and constructors. You can restore the old behavior by setting the new property
    `allowPrivate` to "false".

### Fixed Issues

*   apex
    *   [#2610](https://github.com/pmd/pmd/pull/2610): \[apex] Support top-level enums in rules
*   apex-bestpractices
    *   [#2626](https://github.com/pmd/pmd/issues/2626): \[apex] UnusedLocalVariable - false positive on case insensitivity allowed in Apex
*   apex-performance
    *   [#2598](https://github.com/pmd/pmd/issues/2598): \[apex] AvoidSoqlInLoops false positive for SOQL with in For-Loop
*   apex-security
    *   [#2620](https://github.com/pmd/pmd/issues/2620): \[visualforce] False positive on VfUnescapeEl with new Message Channel feature
*   core
    *   [#710](https://github.com/pmd/pmd/issues/710): \[core] Review used dependencies
    *   [#2594](https://github.com/pmd/pmd/issues/2594): \[core] Update exec-maven-plugin and align it in all project
    *   [#2615](https://github.com/pmd/pmd/issues/2615): \[core] PMD/CPD produces invalid XML (insufficient escaping/wrong encoding)
*   java-bestpractices
    *   [#2543](https://github.com/pmd/pmd/issues/2543): \[java] UseCollectionIsEmpty can not detect the case this.foo.size()
    *   [#2569](https://github.com/pmd/pmd/issues/2569): \[java] LiteralsFirstInComparisons: False negative for methods returning Strings
    *   [#2622](https://github.com/pmd/pmd/issues/2622): \[java] ArrayIsStoredDirectly false positive with private constructor/methods
*   java-codestyle
    *   [#2546](https://github.com/pmd/pmd/issues/2546): \[java] DuplicateImports reported for the same import... and import static...
*   java-design
    *   [#2174](https://github.com/pmd/pmd/issues/2174): \[java] LawOfDemeter: False positive with 'this' pointer
    *   [#2181](https://github.com/pmd/pmd/issues/2181): \[java] LawOfDemeter: False positive with indexed array access
    *   [#2189](https://github.com/pmd/pmd/issues/2189): \[java] LawOfDemeter: False positive when casting to derived class
    *   [#2580](https://github.com/pmd/pmd/issues/2580): \[java] AvoidThrowingNullPointerException marks all NullPointerException objects as wrong, whether or not thrown
    *   [#2625](https://github.com/pmd/pmd/issues/2625): \[java] NPathComplexity can't handle switch expressions
*   java-errorprone
    *   [#2578](https://github.com/pmd/pmd/issues/2578): \[java] AvoidCallingFinalize detects some false positives
    *   [#2634](https://github.com/pmd/pmd/issues/2634): \[java] NullPointerException in rule ProperCloneImplementation
*   java-performance
    *   [#1736](https://github.com/pmd/pmd/issues/1736): \[java] UseStringBufferForStringAppends: False positive if only one concatenation
    *   [#2207](https://github.com/pmd/pmd/issues/2207): \[java] AvoidInstantiatingObjectsInLoops: False positive - should not flag objects when assigned to lists/arrays

### API Changes

#### Deprecated API

##### For removal

* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.26.0/net/sourceforge/pmd/lang/rule/RuleChainVisitor.html#"><code>RuleChainVisitor</code></a> and all implementations in language modules
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.26.0/net/sourceforge/pmd/lang/rule/AbstractRuleChainVisitor.html#"><code>AbstractRuleChainVisitor</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.26.0/net/sourceforge/pmd/lang/Language.html#getRuleChainVisitorClass()"><code>Language#getRuleChainVisitorClass</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.26.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#<init>(java.lang.String,java.lang.String,java.lang.String,java.lang.Class,java.lang.String...)"><code>BaseLanguageModule#&lt;init&gt;</code></a>
* <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.26.0/net/sourceforge/pmd/lang/rule/ImportWrapper.html#"><code>ImportWrapper</code></a>


### External Contributions
*   [#2558](https://github.com/pmd/pmd/pull/2558): \[java] Fix issue #1736 and issue #2207 - [Young Chan](https://github.com/YYoungC)
*   [#2560](https://github.com/pmd/pmd/pull/2560): \[java] Fix false positives of LawOfDemeter: this and cast expressions - [xioayuge](https://github.com/xioayuge)
*   [#2590](https://github.com/pmd/pmd/pull/2590): Update libraries snyk is referring to as `unsafe` - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2597](https://github.com/pmd/pmd/pull/2597): \[dependencies] Fix issue #2594, update exec-maven-plugin everywhere - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2621](https://github.com/pmd/pmd/pull/2621): \[visualforce] add new safe resource for VfUnescapeEl - [Peter Chittum](https://github.com/pchittum)
*   [#2640](https://github.com/pmd/pmd/pull/2640): \[java] NullPointerException in rule ProperCloneImplementation - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2641](https://github.com/pmd/pmd/pull/2641): \[java] AvoidThrowingNullPointerException marks all NullPointerException… - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2643](https://github.com/pmd/pmd/pull/2643): \[java] AvoidCallingFinalize detects some false positives (2578) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2651](https://github.com/pmd/pmd/pull/2651): \[java] False negative: LiteralsFirstInComparisons for methods... (2569) - [Mykhailo Palahuta](https://github.com/Drofff)
*   [#2652](https://github.com/pmd/pmd/pull/2652): \[java] UseCollectionIsEmpty can not detect the case this.foo.size() - [Mykhailo Palahuta](https://github.com/Drofff)

### Stats
* 156 commits
* 43 closed tickets & PRs
* Days since last release: 28

## 27-June-2020 - 6.25.0

The PMD team is pleased to announce PMD 6.25.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Scala cross compilation](#scala-cross-compilation)
    * [New Rules](#new-rules)
    * [Modified rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [Internal API](#internal-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### Scala cross compilation

Up until now the PMD Scala module has been compiled against scala 2.13 only by default.
However, this makes it impossible to use pmd as a library in scala projects,
that use scala 2.12, e.g. in sbt plugins. Therefore PMD now provides cross compiled pmd-scala
modules for both versions: **scala 2.12** and **scala 2.13**.

The new modules have new maven artifactIds. The old artifactId `net.sourceforge.pmd:pmd-scala:6.25.0`
is still available, but is deprecated from now on. It has been demoted to be just a delegation to the new
`pmd-scala_2.13` module and will be removed eventually.

The coordinates for the new modules are:

```
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.12</artifactId>
    <version>6.25.0</version>
</dependency>

<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.13</artifactId>
    <version>6.25.0</version>
</dependency>
```

The command line version of PMD continues to use **scala 2.13**.

#### New Rules

*   The new Java Rule [`UnnecessaryCast`](https://pmd.github.io/pmd-6.25.0/pmd_rules_java_codestyle.html#unnecessarycast) (`java-codestyle`)
    finds casts that are unnecessary while accessing collection elements.

*   The new Java Rule [`AvoidCalendarDateCreation`](https://pmd.github.io/pmd-6.25.0/pmd_rules_java_performance.html#avoidcalendardatecreation) (`java-performance`)
    finds usages of `java.util.Calendar` whose purpose is just to get the current date. This
    can be done in a more lightweight way.

*   The new Java Rule [`UseIOStreamsWithApacheCommonsFileItem`](https://pmd.github.io/pmd-6.25.0/pmd_rules_java_performance.html#useiostreamswithapachecommonsfileitem) (`java-performance`)
    finds usage of `FileItem.get()` and `FileItem.getString()`. These two methods are problematic since
    they load the whole uploaded file into memory.

#### Modified rules

*   The Java rule [`UseDiamondOperator`](https://pmd.github.io/pmd-6.25.0/pmd_rules_java_codestyle.html#usediamondoperator) (`java-codestyle`) now by default
    finds unnecessary usages of type parameters, which are nested, involve wildcards and are used
    within a ternary operator. These usages are usually only unnecessary with Java8 and later, when
    the type inference in Java has been improved.

    In order to avoid false positives when checking Java7 only code, the rule has the new property
    `java7Compatibility`, which is disabled by default. Settings this to "true" retains
    the old rule behaviour.

### Fixed Issues

*   apex-bestpractices
    *   [#2554](https://github.com/pmd/pmd/issues/2554): \[apex] Exception applying rule UnusedLocalVariable on trigger
*   core
    *   [#971](https://github.com/pmd/pmd/issues/971): \[apex]\[plsql]\[java] Deprecate overly specific base rule classes
    *   [#2451](https://github.com/pmd/pmd/issues/2451): \[core] Deprecate support for List attributes with XPath 2.0
    *   [#2599](https://github.com/pmd/pmd/pull/2599): \[core] Fix XPath 2.0 Rule Chain Analyzer with Unions
    *   [#2483](https://github.com/pmd/pmd/issues/2483): \[lang-test] Support cpd tests based on text comparison.
        For details see
        [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
        in the developer documentation.
*   c#
    *   [#2551](https://github.com/pmd/pmd/issues/2551): \[c#] CPD suppression with comments doesn't work
*   cpp
    *   [#1757](https://github.com/pmd/pmd/issues/1757): \[cpp] Support unicode characters
*   java
    *   [#2549](https://github.com/pmd/pmd/issues/2549): \[java] Auxclasspath in PMD CLI does not support relative file path
*   java-codestyle
    *   [#2545](https://github.com/pmd/pmd/issues/2545): \[java] UseDiamondOperator false negatives
    *   [#2573](https://github.com/pmd/pmd/pull/2573): \[java] DefaultPackage: Allow package default JUnit 5 Test methods
*   java-design
    *   [#2563](https://github.com/pmd/pmd/pull/2563): \[java] UselessOverridingMethod false negative with already public methods
    *   [#2570](https://github.com/pmd/pmd/issues/2570): \[java] NPathComplexity should mention the expected NPath complexity
*   java-errorprone
    *   [#2544](https://github.com/pmd/pmd/issues/2544): \[java] UseProperClassLoader can not detect the case with method call on intermediate variable
*   java-performance
    *   [#2591](https://github.com/pmd/pmd/pull/2591): \[java] InefficientStringBuffering/AppendCharacterWithChar: Fix false negatives with concats in appends
    *   [#2600](https://github.com/pmd/pmd/pull/2600): \[java] UseStringBufferForStringAppends: fix false negative with fields
*   scala
    *   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13


### API Changes

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.

*   Rule implementation classes are internal API and should not be used by clients directly.
    The rules should only be referenced via their entry in the corresponding category ruleset
    (e.g. `<rule ref="category/java/bestpractices.xml/AbstractClassWithoutAbstractMethod" />`).

    While we definitely won't move or rename the rule classes in PMD 6.x, we might consider changes
    in PMD 7.0.0 and onwards.

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractIgnoredAnnotationRule.html#"><code>AbstractIgnoredAnnotationRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractInefficientZeroCheck.html#"><code>AbstractInefficientZeroCheck</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractJUnitRule.html#"><code>AbstractJUnitRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaMetricsRule.html#"><code>AbstractJavaMetricsRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractLombokAwareRule.html#"><code>AbstractLombokAwareRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/AbstractPoorMethodCall.html#"><code>AbstractPoorMethodCall</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/bestpractices/AbstractSunSecureRule.html#"><code>AbstractSunSecureRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/documentation/AbstractCommentRule.html#"><code>AbstractCommentRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/performance/AbstractOptimizationRule.html#"><code>AbstractOptimizationRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/regex/RegexHelper.html#"><code>RegexHelper</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/rule/AbstractApexUnitTestRule.html#"><code>AbstractApexUnitTestRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.25.0/net/sourceforge/pmd/lang/plsql/rule/design/AbstractNcssCountRule.html#"><code>AbstractNcssCountRule</code></a> (PLSQL)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/ApexParser.html#"><code>ApexParser</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/ApexHandler.html#"><code>ApexHandler</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.25.0/net/sourceforge/pmd/RuleChain.html#"><code>RuleChain</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.25.0/net/sourceforge/pmd/RuleSets.html#"><code>RuleSets</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.25.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRuleSets(java.lang.String,net.sourceforge.pmd.RuleSetFactory)"><code>RulesetsFactoryUtils#getRuleSets</code></a>

##### For removal

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.25.0/net/sourceforge/pmd/cpd/TokenEntry.html#TokenEntry(java.lang.String,java.lang.String,int)"><code>TokenEntry#TokenEntry</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-test/6.25.0/net/sourceforge/pmd/testframework/AbstractTokenizerTest.html#"><code>AbstractTokenizerTest</code></a>. Use CpdTextComparisonTest in module pmd-lang-test instead.
    For details see
    [Testing your implementation](pmd_devdocs_major_adding_new_cpd_language.html#testing-your-implementation)
    in the developer documentation.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/ast/ASTAnnotation.html#suppresses(net.sourceforge.pmd.Rule)"><code>ASTAnnotation#suppresses</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.25.0/net/sourceforge/pmd/lang/apex/rule/ApexXPathRule.html#"><code>ApexXPathRule</code></a> (Apex)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/SymbolTableTestRule.html#"><code>SymbolTableTestRule</code></a> (Java)
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.25.0/net/sourceforge/pmd/lang/java/rule/performance/InefficientStringBufferingRule.html#isInStringBufferOperation(net.sourceforge.pmd.lang.ast.Node,int,java.lang.String)"><code>InefficientStringBufferingRule#isInStringBufferOperation</code></a>

### External Contributions

*   [#1932](https://github.com/pmd/pmd/pull/1932): \[java] Added 4 performance rules originating from PMD-jPinpoint-rules - [Jeroen Borgers](https://github.com/jborgers)
*   [#2349](https://github.com/pmd/pmd/pull/2349): \[java] Optimize UnusedPrivateMethodRule - [shilko2013](https://github.com/shilko2013)
*   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13 - [João Ferreira](https://github.com/jtjeferreira)
*   [#2567](https://github.com/pmd/pmd/pull/2567): \[c#] Fix CPD suppression with comments doesn't work - [Lixon Lookose](https://github.com/LixonLookose)
*   [#2573](https://github.com/pmd/pmd/pull/2573): \[java] DefaultPackage: Allow package default JUnit 5 Test methods - [Craig Andrews](https://github.com/candrews)
*   [#2593](https://github.com/pmd/pmd/pull/2593): \[java] NPathComplexity should mention the expected NPath complexity - [Artem Krosheninnikov](https://github.com/KroArtem)

### Stats
* 135 commits
* 31 closed tickets & PRs
* Days since last release: 33

## 24-May-2020 - 6.24.0

The PMD team is pleased to announce PMD 6.24.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [CPD now supports XML as well](#cpd-now-supports-xml-as-well)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [New Rules](#new-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
    * [Experimental APIs](#experimental-apis)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### CPD now supports XML as well

Thanks to [Fernando Cosso](https://github.com/xnYi9wRezm) CPD can now find duplicates in XML files as well.
This is useful to find duplicated sections in XML files.

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.24.0).

#### New Rules

*   The new Java Rule [`LiteralsFirstInComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#literalsfirstincomparisons) (`java-bestpractices`)
    find String literals, that are used in comparisons and are not positioned first. Using the String literal
    as the receiver of e.g. `equals` helps to avoid NullPointerExceptions.

    This rule is replacing the two old rules [`PositionLiteralsFirstInComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#positionliteralsfirstincomparisons)
    and [`PositionLiteralsFirstInCaseInsensitiveComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#positionliteralsfirstincaseinsensitivecomparisons) and extends the check
    for the methods `compareTo`, `compareToIgnoreCase` and `contentEquals` in addition to `equals` and
    `equalsIgnoreCase`.

    Note: This rule also replaces the two mentioned rules in Java's quickstart ruleset.

#### Deprecated Rules

*   The two Java rules [`PositionLiteralsFirstInComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#positionliteralsfirstincomparisons)
    and [`PositionLiteralsFirstInCaseInsensitiveComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#positionliteralsfirstincaseinsensitivecomparisons) have been deprecated
    in favor of the new rule [`LiteralsFirstInComparisons`](https://pmd.github.io/pmd-6.24.0/pmd_rules_java_bestpractices.html#literalsfirstincomparisons).

### Fixed Issues

*   apex-bestpractices
    *   [#2468](https://github.com/pmd/pmd/issues/2468): \[apex] Unused Local Variable fails on blocks
*   core
    *   [#2444](https://github.com/pmd/pmd/pull/2444): \[core] Support reproducible builds
    *   [#2484](https://github.com/pmd/pmd/issues/2484): \[core] Update maven-enforcer-plugin to require Java 118
*   c#
    *   [#2495](https://github.com/pmd/pmd/pull/2495): \[c#] Support for interpolated verbatim strings
*   java
    *   [#2472](https://github.com/pmd/pmd/issues/2472): \[java] JavaCharStream throws an Error on invalid escape
*   java-bestpractices
    *   [#2145](https://github.com/pmd/pmd/issues/2145): \[java] Deprecate rules PositionLiteralsFirstIn(CaseInsensitive)Comparisons in favor of LiteralsFirstInComparisons
    *   [#2288](https://github.com/pmd/pmd/issues/2288): \[java] JUnitTestsShouldIncludeAssert: Add support for Hamcrest MatcherAssert.assertThat
    *   [#2437](https://github.com/pmd/pmd/issues/2437): \[java] AvoidPrintStackTrace can't detect the case e.getCause().printStackTrace()
*   java-codestyle
    *   [#2476](https://github.com/pmd/pmd/pull/2476): \[java] MethodNamingConventions - Add support for JUnit 5 method naming
*   java-errorprone
    *   [#2477](https://github.com/pmd/pmd/issues/2477): \[java] JUnitSpelling false-positive for JUnit5/4 tests
*   swift
    *   [#2473](https://github.com/pmd/pmd/issues/2473): \[swift] Swift 5 (up to 5.2) support for CPD

### API Changes

#### Deprecated APIs

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.24.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#addVersion(String,LanguageVersionHandler,boolean)"><code>BaseLanguageModule#addVersion(String, LanguageVersionHandler, boolean)</code></a>
*   Some members of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.24.0/net/sourceforge/pmd/lang/ast/TokenMgrError.html#"><code>TokenMgrError</code></a>, in particular, a new constructor is available
    that should be preferred to the old ones
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.24.0/net/sourceforge/pmd/lang/antlr/AntlrTokenManager.ANTLRSyntaxError.html#"><code>AntlrTokenManager.ANTLRSyntaxError</code></a>

#### Experimental APIs

**Note:** Experimental APIs are identified with the annotation <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.24.0/net/sourceforge/pmd/annotation/Experimental.html#"><code>Experimental</code></a>,
see its javadoc for details

* The experimental methods in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.24.0/net/sourceforge/pmd/lang/BaseLanguageModule.html#"><code>BaseLanguageModule</code></a> have been replaced by a
  definitive API.

### External Contributions

*   [#2446](https://github.com/pmd/pmd/pull/2446): \[core] Update maven-compiler-plugin to 3.8.1 - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2448](https://github.com/pmd/pmd/pull/2448): \[java] Operator Wrap check - [Harsh Kukreja](https://github.com/harsh-kukreja)
*   [#2449](https://github.com/pmd/pmd/pull/2449): \[plsql] Additional info in SqlStatement, FormalParameter and FetchStatement - [Grzegorz Sudolski](https://github.com/zgrzyt93)
*   [#2452](https://github.com/pmd/pmd/pull/2452): \[doc] Fix "Making Rulesets" doc sample code indentation - [Artur Dryomov](https://github.com/arturdryomov)
*   [#2457](https://github.com/pmd/pmd/pull/2457): \[xml] Adding XML to CPD supported languages - [Fernando Cosso](https://github.com/xnYi9wRezm)
*   [#2465](https://github.com/pmd/pmd/pull/2465): \[dependencies] Upgrade hamcrest, mockito and JUnit - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2469](https://github.com/pmd/pmd/pull/2469): \[apex] fix false positive unused variable if only a method is called - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2475](https://github.com/pmd/pmd/pull/2475): \[swift] Swift 4.2-5.2 support - [kenji21](https://github.com/kenji21)
*   [#2476](https://github.com/pmd/pmd/pull/2476): \[java] MethodNamingConventions - Add support for JUnit 5 method naming - [Bruno Ritz](https://github.com/birdflier)
*   [#2478](https://github.com/pmd/pmd/pull/2478): \[java] New rule: LiteralsFirstInComparisons - [John-Teng](https://github.com/John-Teng)
*   [#2479](https://github.com/pmd/pmd/pull/2479): \[java] False positive with Hamcrest's assertThat - [andreoss](https://github.com/andreoss)
*   [#2481](https://github.com/pmd/pmd/pull/2481): \[java] Fix JUnitSpellingRule false positive - [Artem Krosheninnikov](https://github.com/KroArtem)
*   [#2493](https://github.com/pmd/pmd/pull/2493): \[java] Deprecate redundant String Comparison rules - [John-Teng](https://github.com/John-Teng)
*   [#2495](https://github.com/pmd/pmd/pull/2495): \[c#] Support for interpolated verbatim strings - [Maikel Steneker](https://github.com/maikelsteneker)

### Stats
* 114 commits
* 29 closed tickets & PRs
* Days since last release: 30

## 24-April-2020 - 6.23.0

The PMD team is pleased to announce PMD 6.23.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [PMD adopts Contributor Code of Conduct](#pmd-adopts-contributor-code-of-conduct)
    * [Performance improvements for XPath 2.0 rules](#performance-improvements-for-xpath-2.0-rules)
    * [Javascript improvements for ES6](#javascript-improvements-for-es6)
    * [New JSON renderer](#new-json-renderer)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [Internal API](#internal-api)
        * [In ASTs](#in-asts)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)
* [Stats](#stats)

### New and noteworthy

#### PMD adopts Contributor Code of Conduct

To facilitate healthy and constructive community behavior PMD adopts
[Contributor Convenant](https://www.contributor-covenant.org/) as its code of
conduct.

Please note that this project is released with a Contributor Code of Conduct.
By participating in this project you agree to abide by its terms.

You can find the code of conduct in the file [code_of_conduct.md](https://github.com/pmd/pmd/blob/main/code_of_conduct.md)
in our repository.

#### Performance improvements for XPath 2.0 rules

XPath rules written with XPath 2.0 now support conversion to a rulechain rule, which
improves their performance. The rulechain is a mechanism that allows several rules
to be executed in a single tree traversal. Conversion to the rulechain is possible if
your XPath expression looks like `//someNode/... | //someOtherNode/...  | ...`, that
is, a union of one or more path expressions that start with `//`. Instead of traversing
the whole tree once per path expression (and per rule), a single traversal executes all
rules in your ruleset as needed.

This conversion is performed automatically and cannot be disabled. *The conversion should
not change the result of your rules*, if it does, please report a bug at https://github.com/pmd/pmd/issues

Note that XPath 1.0 support, the default XPath version, is deprecated since PMD 6.22.0.
**We highly recommend that you upgrade your rules to XPath 2.0**. Please refer to the [migration guide](https://pmd.github.io/latest/pmd_userdocs_extending_writing_xpath_rules.html#migrating-from-10-to-20).

#### Javascript improvements for ES6

PMD uses the [Rhino](https://github.com/mozilla/rhino) library to parse Javascript.
The default version has been set to `ES6`, so that some ECMAScript 2015 features are
supported. E.g. `let` statements and `for-of` loops are now parsed. However Rhino does
not support all features.

#### New JSON renderer

PMD now supports a JSON renderer (use it with `-f json` on the CLI).
See [the documentation and example](https://pmd.github.io/latest/pmd_userdocs_report_formats.html#json)

#### New Rules

*   The new Apex rule [`FieldDeclarationsShouldBeAtStart`](https://pmd.github.io/pmd-6.23.0/pmd_rules_apex_codestyle.html#fielddeclarationsshouldbeatstart) (`apex-codestyle`)
    helps to ensure that field declarations are always at the beginning of a class.

*   The new Apex rule [`UnusedLocalVariable`](https://pmd.github.io/pmd-6.23.0/pmd_rules_apex_bestpractices.html#unusedlocalvariable) (`apex-bestpractices`) detects unused
    local variables.

### Fixed Issues

*   apex-design
    *   [#2358](https://github.com/pmd/pmd/issues/2358): \[apex] Invalid Apex in Cognitive Complexity tests
*   apex-security
    *   [#2210](https://github.com/pmd/pmd/issues/2210): \[apex] ApexCRUDViolation: Support WITH SECURITY_ENFORCED
    *   [#2399](https://github.com/pmd/pmd/issues/2399): \[apex] ApexCRUDViolation: false positive with security enforced with line break
*   core
    *   [#1286](https://github.com/pmd/pmd/issues/1286): \[core] Export Supporting JSON Format
    *   [#2019](https://github.com/pmd/pmd/issues/2019): \[core] Insufficient deprecation warnings for XPath attributes
    *   [#2357](https://github.com/pmd/pmd/issues/2357): Add code of conduct: Contributor Covenant
    *   [#2426](https://github.com/pmd/pmd/issues/2426): \[core] CodeClimate renderer links are dead
    *   [#2432](https://github.com/pmd/pmd/pull/2432): \[core] Close ZIP data sources even if a runtime exception or error is thrown
*   doc
    *   [#2355](https://github.com/pmd/pmd/issues/2355): \[doc] Improve documentation about incremental analysis
    *   [#2356](https://github.com/pmd/pmd/issues/2356): \[doc] Add missing doc about pmd.github.io
    *   [#2412](https://github.com/pmd/pmd/issues/2412): \[core] HTMLRenderer doesn't render links to source files
    *   [#2413](https://github.com/pmd/pmd/issues/2413): \[doc] Improve documentation about the available renderers (PMD/CPD)
*   java
    *   [#2378](https://github.com/pmd/pmd/issues/2378): \[java] AbstractJUnitRule has bad performance on large code bases
*   java-bestpractices
    *   [#2398](https://github.com/pmd/pmd/issues/2398): \[java] AbstractClassWithoutAbstractMethod false negative with inner abstract classes
*   java-codestyle
    *   [#1164](https://github.com/pmd/pmd/issues/1164): \[java] ClassNamingConventions suggests to add Util for class containing only static constants
    *   [#1723](https://github.com/pmd/pmd/issues/1723): \[java] UseDiamondOperator false-positive inside lambda
*   java-design
    *   [#2390](https://github.com/pmd/pmd/issues/2390): \[java] AbstractClassWithoutAnyMethod: missing violation for nested classes
*   java-errorprone
    *   [#2402](https://github.com/pmd/pmd/issues/2402): \[java] CloseResource possible false positive with Primitive Streams
*   java-multithreading
    *   [#2313](https://github.com/pmd/pmd/issues/2313): \[java] Documentation for DoNotUseThreads is outdated
*   javascript
    *   [#1235](https://github.com/pmd/pmd/issues/1235): \[javascript] Use of let results in an Empty Statement in the AST
    *   [#2379](https://github.com/pmd/pmd/issues/2379): \[javascript] Support for-of loop
*   javascript-errorprone
    *   [#384](https://github.com/pmd/pmd/issues/384): \[javascript] Trailing commas not detected on French default locale

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/xpath/AbstractXPathRuleQuery.html#"><code>AbstractXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQuery.html#"><code>JaxenXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/xpath/SaxonXPathRuleQuery.html#"><code>SaxonXPathRuleQuery</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/xpath/XPathRuleQuery.html#"><code>XPathRuleQuery</code></a>

##### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated in the **Apex**, **Javascript**, **PL/SQL**, **Scala** and **Visualforce** ASTs:

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.23.0/net/sourceforge/pmd/lang/vf/ast/VfNode.html#"><code>VfNode</code></a> or
        <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The implementation classes of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/Parser.html#"><code>Parser</code></a> (eg <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.23.0/net/sourceforge/pmd/lang/vf/VfParser.html#"><code>VfParser</code></a>) are deprecated and should not be used directly.
    Use <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser(ParserOptions)"><code>LanguageVersionHandler#getParser</code></a> instead.
*   The implementation classes of <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/TokenManager.html#"><code>TokenManager</code></a> (eg <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.23.0/net/sourceforge/pmd/lang/vf/VfTokenManager.html#"><code>VfTokenManager</code></a>) are deprecated and should not be used outside of our implementation.
    **This also affects CPD-only modules**.

These deprecations are added to the following language modules in this release.
Please look at the package documentation to find out the full list of deprecations.
* Apex: **<a href="https://docs.pmd-code.org/apidocs/pmd-apex/6.23.0/net/sourceforge/pmd/lang/apex/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.apex.ast</code></a>**
* Javascript: **<a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.23.0/net/sourceforge/pmd/lang/ecmascript/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.ecmascript.ast</code></a>**
* PL/SQL: **<a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.23.0/net/sourceforge/pmd/lang/plsql/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.plsql.ast</code></a>**
* Scala: **<a href="https://docs.pmd-code.org/apidocs/pmd-scala/6.23.0/net/sourceforge/pmd/lang/scala/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.scala.ast</code></a>**
* Visualforce: **<a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.23.0/net/sourceforge/pmd/lang/vf/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.vf.ast</code></a>**

These deprecations have already been rolled out in a previous version for the
following languages:
* Java: <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.java.ast</code></a>
* Java Server Pages: <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.23.0/net/sourceforge/pmd/lang/jsp/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.jsp.ast</code></a>
* Velocity Template Language: <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.23.0/net/sourceforge/pmd/lang/vm/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.vm.ast</code></a>

Outside of these packages, these changes also concern the following TokenManager
implementations, and their corresponding Parser if it exists (in the same package):

*   <a href="https://docs.pmd-code.org/apidocs/pmd-cpp/6.23.0/net/sourceforge/pmd/lang/cpp/CppTokenManager.html#"><code>CppTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/JavaTokenManager.html#"><code>JavaTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-javascript/6.23.0/net/sourceforge/pmd/lang/ecmascript5/Ecmascript5TokenManager.html#"><code>Ecmascript5TokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-jsp/6.23.0/net/sourceforge/pmd/lang/jsp/JspTokenManager.html#"><code>JspTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-matlab/6.23.0/net/sourceforge/pmd/lang/matlab/MatlabTokenManager.html#"><code>MatlabTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-modelica/6.23.0/net/sourceforge/pmd/lang/modelica/ModelicaTokenManager.html#"><code>ModelicaTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-objectivec/6.23.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCTokenManager.html#"><code>ObjectiveCTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-plsql/6.23.0/net/sourceforge/pmd/lang/plsql/PLSQLTokenManager.html#"><code>PLSQLTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-python/6.23.0/net/sourceforge/pmd/lang/python/PythonTokenManager.html#"><code>PythonTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-visualforce/6.23.0/net/sourceforge/pmd/lang/vf/VfTokenManager.html#"><code>VfTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-vm/6.23.0/net/sourceforge/pmd/lang/vm/VmTokenManager.html#"><code>VmTokenManager</code></a>


In the **Java AST** the following attributes are deprecated and will issue a warning when used in XPath rules:

*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/ast/ASTAdditiveExpression.html#getImage()"><code>ASTAdditiveExpression#getImage</code></a> - use `getOperator()` instead
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#getImage()"><code>ASTVariableDeclaratorId#getImage</code></a> - use `getName()` instead
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#getVariableName()"><code>ASTVariableDeclaratorId#getVariableName</code></a> - use `getName()` instead

##### For removal

*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/Parser.html#getTokenManager(java.lang.String,java.io.Reader)"><code>Parser#getTokenManager</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/TokenManager.html#setFileName(java.lang.String)"><code>TokenManager#setFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/AbstractTokenManager.html#setFileName(java.lang.String)"><code>AbstractTokenManager#setFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/AbstractTokenManager.html#getFileName(java.lang.String)"><code>AbstractTokenManager#getFileName</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/cpd/token/AntlrToken.html#getType()"><code>AntlrToken#getType</code></a> - use `getKind()` instead.
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/ImmutableLanguage.html#"><code>ImmutableLanguage</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/MockRule.html#"><code>MockRule</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/Node.html#getFirstParentOfAnyType(java.lang.Class[])"><code>Node#getFirstParentOfAnyType</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/Node.html#getAsDocument()"><code>Node#getAsDocument</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#hasDescendantOfAnyType(java.lang.Class[])"><code>AbstractNode#hasDescendantOfAnyType</code></a>
*   <a href="https://docs.pmd-code.org/apidocs/pmd-java/6.23.0/net/sourceforge/pmd/lang/java/ast/ASTRecordDeclaration.html#getComponentList()"><code>ASTRecordDeclaration#getComponentList</code></a>
*   Multiple fields, constructors and methods in <a href="https://docs.pmd-code.org/apidocs/pmd-core/6.23.0/net/sourceforge/pmd/lang/rule/XPathRule.html#"><code>XPathRule</code></a>. See javadoc for details.

### External Contributions

*   [#2312](https://github.com/pmd/pmd/pull/2312): \[apex] Update ApexCRUDViolation Rule - [Joshua S Arquilevich](https://github.com/jarquile)
*   [#2314](https://github.com/pmd/pmd/pull/2314): \[doc] maven integration - Add version to plugin - [Pham Hai Trung](https://github.com/gpbp)
*   [#2353](https://github.com/pmd/pmd/pull/2353): \[plsql] xmlforest with optional AS - [Piotr Szymanski](https://github.com/szyman23)
*   [#2383](https://github.com/pmd/pmd/pull/2383): \[apex] Fix invalid apex in documentation - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2395](https://github.com/pmd/pmd/pull/2395): \[apex] New Rule: Unused local variables - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2396](https://github.com/pmd/pmd/pull/2396): \[apex] New rule: field declarations should be at start - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2397](https://github.com/pmd/pmd/pull/2397): \[apex] fixed WITH SECURITY_ENFORCED regex to recognise line break characters - [Kieran Black](https://github.com/kieranlblack)
*   [#2401](https://github.com/pmd/pmd/pull/2401): \[doc] Update DoNotUseThreads rule documentation - [Saikat Sengupta](https://github.com/s4ik4t)
*   [#2403](https://github.com/pmd/pmd/pull/2403): \[java] #2402 fix false-positives on Primitive Streams - [Bernd Farka](https://github.com/BerndFarkaDyna)
*   [#2409](https://github.com/pmd/pmd/pull/2409): \[java] ClassNamingConventions suggests to add Util for class containing only static constants, fixes #1164 - [Binu R J](https://github.com/binu-r)
*   [#2411](https://github.com/pmd/pmd/pull/2411): \[java] Fix UseAssertEqualsInsteadOfAssertTrue Example - [Moritz Scheve](https://github.com/Blightbuster)
*   [#2423](https://github.com/pmd/pmd/pull/2423): \[core] Fix Checkstyle OperatorWrap in AbstractTokenizer - [Harsh Kukreja](https://github.com/harsh-kukreja)
*   [#2432](https://github.com/pmd/pmd/pull/2432): \[core] Close ZIP data sources even if a runtime exception or error is thrown - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)

### Stats
* 237 commits
* 64 closed tickets & PRs
* Days since last release: 42

## 12-March-2020 - 6.22.0

The PMD team is pleased to announce PMD 6.22.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 14 Support](#java-14-support)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Apex Suppressions](#apex-suppressions)
    * [Improved CPD support for C#](#improved-cpd-support-for-c#)
    * [XPath Rules](#xpath-rules)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [Internal API](#internal-api)
        * [For removal](#for-removal)
        * [In ASTs (JSP)](#in-asts-(jsp))
        * [In ASTs (Velocity)](#in-asts-(velocity))
    * [PLSQL AST](#plsql-ast)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Java 14 Support

This release of PMD brings support for Java 14. PMD can parse [Switch Expressions](https://openjdk.java.net/jeps/361),
which have been promoted to be a standard language feature of Java.

PMD also parses [Text Blocks](https://openjdk.java.net/jeps/368) as String literals, which is still a preview
language feature in Java 14.

The new [Pattern Matching for instanceof](https://openjdk.java.net/jeps/305) can be used as well as
[Records](https://openjdk.java.net/jeps/359).

Note: The Text Blocks, Pattern Matching for instanceof and Records are all preview language features of OpenJDK 14
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS` and select the new language version `14-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 14-preview ...

Note: Support for the extended break statement introduced in Java 12 as a preview language feature
has been removed from PMD with this version. The version "12-preview" is no longer available.


#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.21.0).

#### Apex Suppressions

In addition to suppressing violation with the `@SuppressWarnings` annotation, Apex now also supports
the suppressions with a `NOPMD` comment. See [Suppressing warnings](pmd_userdocs_suppressing_warnings.html).

#### Improved CPD support for C#

The C# tokenizer is now based on an antlr grammar instead of a manual written tokenizer. This
should give more accurate results and especially fixes the problems with the using statement syntax
(see [#2139](https://github.com/pmd/pmd/issues/2139)).

#### XPath Rules

See the new documentation about [Writing XPath Rules](pmd_userdocs_extending_writing_xpath_rules.html).

*Note:* As of PMD version 6.22.0, XPath versions 1.0 and the 1.0 compatibility mode are **deprecated**.
XPath 2.0 is superior in many ways, for example for its support for type checking, sequence values,
or quantified expressions. For a detailed but approachable review of the features of XPath 2.0 and above,
see the [Saxon documentation](https://www.saxonica.com/documentation/index.html#!expressions).

#### New Rules

*   The Rule [`CognitiveComplexity`](https://pmd.github.io/pmd-6.22.0/pmd_rules_apex_design.html#cognitivecomplexity) (`apex-design`) finds methods and classes
    that are highly complex and therefore difficult to read and more costly to maintain. In contrast
    to cyclomatic complexity, this rule uses "Cognitive Complexity", which is a measure of how
    difficult it is for humans to read and understand a method.

*   The Rule [`TestMethodsMustBeInTestClasses`](https://pmd.github.io/pmd-6.22.0/pmd_rules_apex_errorprone.html#testmethodsmustbeintestclasses) (`apex-errorprone`) finds test methods
    that are not residing in a test class. The test methods should be moved to a proper test class.
    Support for tests inside functional classes was removed in Spring-13 (API Version 27.0), making classes
    that violate this rule fail compile-time. This rule is however useful when dealing with legacy code.

### Fixed Issues

*   apex
    *   [#1087](https://github.com/pmd/pmd/issues/1087): \[apex] Support suppression via //NOPMD
    *   [#2306](https://github.com/pmd/pmd/issues/2306): \[apex] Switch statements are not parsed/supported
*   apex-design
    *   [#2162](https://github.com/pmd/pmd/issues/2162): \[apex] Cognitive Complexity rule
*   apex-errorprone
    *   [#639](https://github.com/pmd/pmd/issues/639): \[apex] Test methods should not be in classes other than test classes
*   cs
    *   [#2139](https://github.com/pmd/pmd/issues/2139): \[cs] CPD doesn't understand alternate using statement syntax with C# 8.0
*   doc
    *   [#2274](https://github.com/pmd/pmd/issues/2274): \[doc] Java API documentation for PMD
*   java
    *   [#2159](https://github.com/pmd/pmd/issues/2159): \[java] Prepare for JDK 14
    *   [#2268](https://github.com/pmd/pmd/issues/2268): \[java] Improve TypeHelper resilience
*   java-bestpractices
    *   [#2277](https://github.com/pmd/pmd/issues/2277): \[java] FP in UnusedImports for ambiguous static on-demand imports
*   java-design
    *   [#911](https://github.com/pmd/pmd/issues/911): \[java] UselessOverridingMethod false positive when elevating access modifier
*   java-errorprone
    *   [#2242](https://github.com/pmd/pmd/issues/2242): \[java] False-positive MisplacedNullCheck reported
    *   [#2250](https://github.com/pmd/pmd/issues/2250): \[java] InvalidLogMessageFormat flags logging calls using a slf4j-Marker
    *   [#2255](https://github.com/pmd/pmd/issues/2255): \[java] InvalidLogMessageFormat false-positive for a lambda argument
*   java-performance
    *   [#2275](https://github.com/pmd/pmd/issues/2275): \[java] AppendCharacterWithChar flags literals in an expression
*   plsql
    *   [#2325](https://github.com/pmd/pmd/issues/2325): \[plsql] NullPointerException while running parsing test for CREATE TRIGGER
    *   [#2327](https://github.com/pmd/pmd/pull/2327): \[plsql] Parsing of WHERE CURRENT OF
    *   [#2328](https://github.com/pmd/pmd/issues/2328): \[plsql] Support XMLROOT
    *   [#2331](https://github.com/pmd/pmd/pull/2331): \[plsql] Fix in Comment statement
    *   [#2332](https://github.com/pmd/pmd/pull/2332): \[plsql] Fixed Execute Immediate statement parsing
    *   [#2340](https://github.com/pmd/pmd/pull/2340): \[plsql] Fixed parsing / as divide or execute

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/JavaLanguageHandler.html#"><code>JavaLanguageHandler</code></a>
* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/JavaLanguageParser.html#"><code>JavaLanguageParser</code></a>
* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/JavaDataFlowHandler.html#"><code>JavaDataFlowHandler</code></a>
* Implementations of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a> in each
  language module, eg <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolationFactory.html#"><code>JavaRuleViolationFactory</code></a>.
  See javadoc of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#"><code>RuleViolationFactory</code></a>.
* Implementations of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a> in each language module,
  eg <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#"><code>JavaRuleViolation</code></a>. See javadoc of
  <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/RuleViolation.html#"><code>RuleViolation</code></a>.

* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/rules/RuleFactory.html#"><code>RuleFactory</code></a>
* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/rules/RuleBuilder.html#"><code>RuleBuilder</code></a>
* Constructors of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/RuleSetFactory.html#"><code>RuleSetFactory</code></a>, use factory methods from <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#"><code>RulesetsFactoryUtils</code></a> instead
* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRulesetFactory(net.sourceforge.pmd.PMDConfiguration,net.sourceforge.pmd.util.ResourceLoader)"><code>getRulesetFactory</code></a>

* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#"><code>AbstractApexNode</code></a>
* <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNodeBase.html#"><code>AbstractApexNodeBase</code></a>, and the related `visit`
  methods on <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitor.html#"><code>ApexParserVisitor</code></a> and its implementations.
  Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#"><code>ApexNode</code></a> instead, now considers comments too.

##### For removal

* pmd-core
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/dfa/DFAGraphRule.html#"><code>DFAGraphRule</code></a> and its implementations
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/dfa/DFAGraphMethod.html#"><code>DFAGraphMethod</code></a>
    * Many methods on the <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a> interface
      and <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#"><code>AbstractNode</code></a> base class. See their javadoc for details.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/Node.html#isFindBoundary()"><code>Node#isFindBoundary</code></a> is deprecated for XPath queries.
    * Many APIs of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/metrics/package-summary.html#"><code>net.sourceforge.pmd.lang.metrics</code></a>, though most of them were internal and
      probably not used directly outside of PMD. Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/metrics/MetricsUtil.html#"><code>MetricsUtil</code></a> as
      a replacement for the language-specific façades too.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/QualifiableNode.html#"><code>QualifiableNode</code></a>, <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/QualifiedName.html#"><code>QualifiedName</code></a>
* pmd-java
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/AbstractJavaParser.html#"><code>AbstractJavaParser</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/AbstractJavaHandler.html#"><code>AbstractJavaHandler</code></a>
    * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getKind()"><code>ASTAnyTypeDeclaration#getKind</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.html#"><code>JavaQualifiedName</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTCatchStatement.html#getBlock()"><code>ASTCatchStatement#getBlock</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#declarationsAreInDefaultPackage()"><code>ASTCompilationUnit#declarationsAreInDefaultPackage</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiableNode.html#"><code>JavaQualifiableNode</code></a>
        * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getQualifiedName()"><code>ASTAnyTypeDeclaration#getQualifiedName</code></a>
        * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#getQualifiedName()"><code>ASTMethodOrConstructorDeclaration#getQualifiedName</code></a>
        * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getQualifiedName()"><code>ASTLambdaExpression#getQualifiedName</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/qname/package-summary.html#"><code>net.sourceforge.pmd.lang.java.qname</code></a> and its contents
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/MethodLikeNode.html#"><code>MethodLikeNode</code></a>
        * Its methods will also be removed from its implementations,
          <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#"><code>ASTMethodOrConstructorDeclaration</code></a>,
          <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#"><code>ASTLambdaExpression</code></a>.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getImage()"><code>ASTAnyTypeDeclaration#getImage</code></a> will be removed. Please use `getSimpleName()`
      instead. This affects <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotationTypeDeclaration.html#getImage()"><code>ASTAnnotationTypeDeclaration#getImage</code></a>,
      <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getImage()"><code>ASTClassOrInterfaceDeclaration#getImage</code></a>, and
      <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTEnumDeclaration.html#getImage()"><code>ASTEnumDeclaration#getImage</code></a>.
    * Several methods of <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTTryStatement.html#"><code>ASTTryStatement</code></a>, replacements with other names
      have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
    * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
      following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTYieldStatement.html#"><code>ASTYieldStatement</code></a> will not implement <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#"><code>TypeNode</code></a>
      anymore come 7.0.0. Test the type of the expression nested within it.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#"><code>JavaMetrics</code></a>, <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/metrics/JavaMetricsComputer.html#"><code>JavaMetricsComputer</code></a>
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTArguments.html#getArgumentCount()"><code>ASTArguments#getArgumentCount</code></a>.
      Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTArguments.html#size()"><code>size</code></a> instead.
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTFormalParameters.html#getParameterCount()"><code>ASTFormalParameters#getParameterCount</code></a>.
      Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.22.0/net/sourceforge/pmd/lang/java/ast/ASTFormalParameters.html#size()"><code>size</code></a> instead.
* pmd-apex
    * <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/metrics/ApexMetrics.html#"><code>ApexMetrics</code></a>, <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.22.0/net/sourceforge/pmd/lang/apex/metrics/ApexMetricsComputer.html#"><code>ApexMetricsComputer</code></a>

##### In ASTs (JSP)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the JSP AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-jsp/6.22.0/net/sourceforge/pmd/lang/jsp/ast/JspNode.html#"><code>JspNode</code></a> or
        <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The class <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-jsp/6.22.0/net/sourceforge/pmd/lang/jsp/JspParser.html#"><code>JspParser</code></a> is deprecated and should not be used directly.
    Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser(ParserOptions)"><code>LanguageVersionHandler#getParser</code></a> instead.

Please look at <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-jsp/6.22.0/net/sourceforge/pmd/lang/jsp/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.jsp.ast</code></a> to find out the full list of deprecations.

##### In ASTs (Velocity)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the VM AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/annotation/InternalApi.html#"><code>InternalApi</code></a>. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/ast/VmNode.html#"><code>VmNode</code></a> or
        <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/ast/Node.html#"><code>Node</code></a>, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The package <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/directive/package-summary.html#"><code>net.sourceforge.pmd.lang.vm.directive</code></a> as well as the classes
    <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/util/DirectiveMapper.html#"><code>DirectiveMapper</code></a> and <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/util/LogUtil.html#"><code>LogUtil</code></a> are deprecated
    for removal. They were only used internally during parsing.
*   The class <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/VmParser.html#"><code>VmParser</code></a> is deprecated and should not be used directly.
    Use <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.22.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getParser(ParserOptions)"><code>LanguageVersionHandler#getParser</code></a> instead.

Please look at <a href="https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.22.0/net/sourceforge/pmd/lang/vm/ast/package-summary.html#"><code>net.sourceforge.pmd.lang.vm.ast</code></a> to find out the full list of deprecations.

#### PLSQL AST

The production and node `ASTCursorBody` was unnecessary, not used and has been removed. Cursors have been already
parsed as `ASTCursorSpecification`.

### External Contributions

*   [#2251](https://github.com/pmd/pmd/pull/2251): \[java] FP for InvalidLogMessageFormat when using slf4j-Markers - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2253](https://github.com/pmd/pmd/pull/2253): \[modelica] Remove duplicated dependencies - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#2256](https://github.com/pmd/pmd/pull/2256): \[doc] Corrected XML attributes in release notes - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2276](https://github.com/pmd/pmd/pull/2276): \[java] AppendCharacterWithCharRule ignore literals in expressions - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2278](https://github.com/pmd/pmd/pull/2278): \[java] fix UnusedImports rule for ambiguous static on-demand imports - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#2279](https://github.com/pmd/pmd/pull/2279): \[apex] Add support for suppressing violations using the // NOPMD comment - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2280](https://github.com/pmd/pmd/pull/2280): \[cs] CPD: Replace C# tokenizer by an Antlr-based one - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2297](https://github.com/pmd/pmd/pull/2297): \[apex] Cognitive complexity metrics - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2317](https://github.com/pmd/pmd/pull/2317): \[apex] New Rule - Test Methods Must Be In Test Classes - [Brian Nørremark](https://github.com/noerremark)
*   [#2321](https://github.com/pmd/pmd/pull/2321): \[apex] Support switch statements correctly in Cognitive Complexity - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2326](https://github.com/pmd/pmd/pull/2326): \[plsql] Added XML functions to parser: extract(xml), xml_root and fixed xml_forest - [Piotr Szymanski](https://github.com/szyman23)
*   [#2327](https://github.com/pmd/pmd/pull/2327): \[plsql] Parsing of WHERE CURRENT OF added - [Piotr Szymanski](https://github.com/szyman23)
*   [#2331](https://github.com/pmd/pmd/pull/2331): \[plsql] Fix in Comment statement - [Piotr Szymanski](https://github.com/szyman23)
*   [#2332](https://github.com/pmd/pmd/pull/2332): \[plsql] Fixed Execute Immediate statement parsing - [Piotr Szymanski](https://github.com/szyman23)
*   [#2338](https://github.com/pmd/pmd/pull/2338): \[cs] CPD: fixes in filtering of using directives - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2339](https://github.com/pmd/pmd/pull/2339): \[cs] CPD: Fixed CPD --ignore-usings option - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#2340](https://github.com/pmd/pmd/pull/2340): \[plsql] fix for parsing / as divide or execute - [Piotr Szymanski](https://github.com/szyman23)
*   [#2342](https://github.com/pmd/pmd/pull/2342): \[xml] Update property used in example - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#2344](https://github.com/pmd/pmd/pull/2344): \[doc] Update ruleset examples for ant - [Piotrek Żygieło](https://github.com/pzygielo)
*   [#2343](https://github.com/pmd/pmd/pull/2343): \[ci] Disable checking for snapshots in jcenter - [Piotrek Żygieło](https://github.com/pzygielo)

## 24-January-2020 - 6.21.0

The PMD team is pleased to announce PMD 6.21.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Modelica support](#modelica-support)
    * [Simple XML dump of AST](#simple-xml-dump-of-ast)
    * [Updated Apex Support](#updated-apex-support)
    * [CPD XML format](#cpd-xml-format)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [Internal API](#internal-api)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Modelica support

Thanks to [Anatoly Trosinenko](https://github.com/atrosinenko) PMD supports now a new language:
[Modelica](https://modelica.org/modelicalanguage) is a language to model complex physical systems.
Both PMD and CPD are supported and there are already [3 rules available](pmd_rules_modelica.html).
The PMD Designer supports syntax highlighting for Modelica.

While the language implementation is quite complete, Modelica support is considered experimental
for now. This is to allow us to change the rule API (e.g. the AST classes) slightly and improve
the implementation based on your feedback.

#### Simple XML dump of AST

We added a experimental feature to dump the AST of a source file into XML. The XML format
is of course PMD specific and language dependent. That XML file can be used to execute
(XPath) queries against without PMD. It can also be used as a textual visualization of the AST
if you don't want to use the [Designer](https://github.com/pmd/pmd-designer).

This feature is experimental and might change or even be removed in the future, if it is not
useful. A short description how to use it is available under [Creating XML dump of the AST](pmd_userdocs_extending_ast_dump.html).

Any feedback about it, especially about your use cases, is highly appreciated.

#### Updated Apex Support

*   The Apex language support has been bumped to version 48 (Spring '20). All new language features are now properly
    parsed and processed.

#### CPD XML format

The CPD XML output format has been enhanced to also report column information for found duplications
in addition to the line information. This allows to display the exact tokens, that are considered
duplicate.

If a CPD language doesn't provide these exact information, then these additional attributes are omitted.

Each `<file>` element in the XML format now has 3 new attributes:

*   attribute `endline`
*   attribute `column` (if there is column information available)
*   attribute `endcolumn` (if there is column information available)

#### Modified Rules

*   The Java rule [`AvoidLiteralsInIfCondition`](https://pmd.github.io/pmd-6.21.0/pmd_rules_java_errorprone.html#avoidliteralsinifcondition) (`java-errorprone`) has a new property
    `ignoreExpressions`. This property is set by default to `true` in order to maintain compatibility. If this
    property is set to false, then literals in more complex expressions are considered as well.

*   The Apex rule [`ApexCSRF`](https://pmd.github.io/pmd-6.21.0/pmd_rules_apex_errorprone.html#apexcsrf) (`apex-errorprone`) has been moved from category
    "Security" to "Error Prone". The Apex runtime already prevents DML statements from being executed, but only
    at runtime. So, if you try to do this, you'll get an error at runtime, hence this is error prone. See also
    the discussion on [#2064](https://github.com/pmd/pmd/issues/2064).

*   The Java rule [`CommentRequired`](https://pmd.github.io/pmd-6.21.0/pmd_rules_java_documentation.html#commentrequired) (`java-documentation`) has a new property
    `classCommentRequirement`. This replaces the now deprecated property `headerCommentRequirement`, since
    the name was misleading. (File) header comments are not checked, but class comments are.

### Fixed Issues

*   apex
    *   [#2208](https://github.com/pmd/pmd/issues/2208): \[apex] ASTFormalComment should implement ApexNode&lt;T&gt;
*   core
    *   [#1984](https://github.com/pmd/pmd/issues/1984): \[java] Cyclomatic complexity is misreported (lack of clearing metrics cache)
    *   [#2006](https://github.com/pmd/pmd/issues/2006): \[core] PMD should warn about multiple instances of the same rule in a ruleset
    *   [#2161](https://github.com/pmd/pmd/issues/2161): \[core] ResourceLoader is deprecated and marked as internal but is exposed
    *   [#2170](https://github.com/pmd/pmd/issues/2170): \[core] DocumentFile doesn't preserve newlines
*   doc
    *   [#2214](https://github.com/pmd/pmd/issues/2214): \[doc] Link broken in pmd documentation for writing Xpath rules
*   java
    *   [#2212](https://github.com/pmd/pmd/issues/2212): \[java] JavaRuleViolation reports wrong class name
*   java-bestpractices
    *   [#2149](https://github.com/pmd/pmd/issues/2149): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#2167](https://github.com/pmd/pmd/issues/2167): \[java] UnnecessaryLocalBeforeReturn false positive with variable captured by method reference
*   java-documentation
    *   [#1683](https://github.com/pmd/pmd/issues/1683): \[java] CommentRequired property names are inconsistent
*   java-errorprone
    *   [#2140](https://github.com/pmd/pmd/issues/2140): \[java] AvoidLiteralsInIfCondition: false negative for expressions
    *   [#2196](https://github.com/pmd/pmd/issues/2196): \[java] InvalidLogMessageFormat does not detect extra parameters when no placeholders
*   java-performance
    *   [#2141](https://github.com/pmd/pmd/issues/2141): \[java] StringInstatiation: False negative with String-array access
*   plsql
    *   [#2008](https://github.com/pmd/pmd/issues/2008): \[plsql] In StringLiteral using alternative quoting mechanism single quotes cause parsing errors
    *   [#2009](https://github.com/pmd/pmd/issues/2009): \[plsql] Multiple DDL commands are skipped during parsing

### API Changes


#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* [`JavaLanguageHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaLanguageHandler.html#)
* [`JavaLanguageParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaLanguageParser.html#)
* [`JavaDataFlowHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/JavaDataFlowHandler.html#)
* Implementations of [`RuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#) in each
  language module, eg [`JavaRuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolationFactory.html#).
  See javadoc of [`RuleViolationFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/rule/RuleViolationFactory.html#).
* Implementations of [`RuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleViolation.html#) in each language module,
  eg [`JavaRuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#). See javadoc of
  [`RuleViolation`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleViolation.html#).

* [`RuleFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/rules/RuleFactory.html#)
* [`RuleBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/rules/RuleBuilder.html#)
* Constructors of [`RuleSetFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RuleSetFactory.html#), use factory methods from [`RulesetsFactoryUtils`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#) instead
* [`getRulesetFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/RulesetsFactoryUtils.html#getRulesetFactory(net.sourceforge.pmd.PMDConfiguration,net.sourceforge.pmd.util.ResourceLoader))

* [`AbstractApexNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNode.html#)
* [`AbstractApexNodeBase`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/AbstractApexNodeBase.html#), and the related `visit`
  methods on [`ApexParserVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/ApexParserVisitor.html#) and its implementations.
  Use [`ApexNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.21.0/net/sourceforge/pmd/lang/apex/ast/ApexNode.html#) instead, now considers comments too.

* [`CharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/CharStream.html#), [`JavaCharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/JavaCharStream.html#),
  [`SimpleCharStream`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/SimpleCharStream.html#): these are APIs used by our JavaCC
  implementations and that will be moved/refactored for PMD 7.0.0. They should not
  be used, extended or implemented directly.
* All classes generated by JavaCC, eg [`JJTJavaParserState`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/JJTJavaParserState.html#).
  This includes token classes, which will be replaced with a single implementation, and
  subclasses of [`ParseException`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/ParseException.html#), whose usages will be replaced
  by just that superclass.


##### For removal

* pmd-core
    * Many methods on the [`Node`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/Node.html#) interface
      and [`AbstractNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/AbstractNode.html#) base class. See their javadoc for details.
    * [`Node#isFindBoundary`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.21.0/net/sourceforge/pmd/lang/ast/Node.html#isFindBoundary()) is deprecated for XPath queries.
* pmd-java
    * [`AbstractJavaParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/AbstractJavaParser.html#)
    * [`AbstractJavaHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/AbstractJavaHandler.html#)
    * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
    * [`ASTAnyTypeDeclaration#getKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getKind())
    * [`JavaQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiedName.html#)
    * [`ASTCatchStatement#getBlock`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTCatchStatement.html#getBlock())
    * [`ASTCompilationUnit#declarationsAreInDefaultPackage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#declarationsAreInDefaultPackage())
    * [`JavaQualifiableNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/JavaQualifiableNode.html#)
        * [`ASTAnyTypeDeclaration#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getQualifiedName())
        * [`ASTMethodOrConstructorDeclaration#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#getQualifiedName())
        * [`ASTLambdaExpression#getQualifiedName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#getQualifiedName())
    * [`net.sourceforge.pmd.lang.java.qname`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/qname/package-summary.html#) and its contents
    * [`MethodLikeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/MethodLikeNode.html#)
        * Its methods will also be removed from its implementations,
          [`ASTMethodOrConstructorDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTMethodOrConstructorDeclaration.html#),
          [`ASTLambdaExpression`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTLambdaExpression.html#).
    * [`ASTAnyTypeDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.html#getImage()) will be removed. Please use `getSimpleName()`
      instead. This affects [`ASTAnnotationTypeDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnnotationTypeDeclaration.html#getImage()),
      [`ASTClassOrInterfaceDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTClassOrInterfaceDeclaration.html#getImage()), and
      [`ASTEnumDeclaration#getImage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTEnumDeclaration.html#getImage()).
    * Several methods of [`ASTTryStatement`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTTryStatement.html#), replacements with other names
      have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
    * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
      following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
    * [`ASTYieldStatement`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTYieldStatement.html#) will not implement [`TypeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#)
      anymore come 7.0.0. Test the type of the expression nested within it.


### External Contributions

*   [#2041](https://github.com/pmd/pmd/pull/2041): \[modelica] Initial implementation for PMD - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2051](https://github.com/pmd/pmd/pull/2051): \[doc] Update the docs on adding a new language - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2069](https://github.com/pmd/pmd/pull/2069): \[java] CommentRequired: make property names consistent - [snuyanzin](https://github.com/snuyanzin)
*   [#2169](https://github.com/pmd/pmd/pull/2169): \[modelica] Follow-up fixes for Modelica language module - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2193](https://github.com/pmd/pmd/pull/2193): \[core] Fix odd logic in test runner - [Egor Bredikhin](https://github.com/Egor18)
*   [#2194](https://github.com/pmd/pmd/pull/2194): \[java] Fix odd logic in AvoidUsingHardCodedIPRule - [Egor Bredikhin](https://github.com/Egor18)
*   [#2195](https://github.com/pmd/pmd/pull/2195): \[modelica] Normalize invalid node ranges - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2199](https://github.com/pmd/pmd/pull/2199): \[modelica] Fix Javadoc tags - [Anatoly Trosinenko](https://github.com/atrosinenko)
*   [#2225](https://github.com/pmd/pmd/pull/2225): \[core] CPD: report endLine / column informations for found duplications - [Maikel Steneker](https://github.com/maikelsteneker)

## 29-November-2019 - 6.20.0

The PMD team is pleased to announce PMD 6.20.0.

This is a minor release.

### Table Of Contents

* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)

### Fixed Issues

*   apex
    *   [#2092](https://github.com/pmd/pmd/issues/2092): \[apex] ApexLexer logs visible when Apex is the selected language upon starting the designer
    *   [#2136](https://github.com/pmd/pmd/issues/2136): \[apex] Provide access to underlying query of SoqlExpression
*   core
    *   [#2002](https://github.com/pmd/pmd/issues/2002): \[doc] Issue with http://pmdapplied.com/ linking to a gambling Web site
    *   [#2062](https://github.com/pmd/pmd/issues/2062): \[core] Shortnames parameter does not work with Ant
    *   [#2090](https://github.com/pmd/pmd/issues/2090): \[ci] Release notes and draft releases
    *   [#2096](https://github.com/pmd/pmd/issues/2096): \[core] Referencing category errorprone.xml produces deprecation warnings for InvalidSlf4jMessageFormat
*   java
    *   [#1861](https://github.com/pmd/pmd/issues/1861): \[java] Be more lenient with version numbers
    *   [#2105](https://github.com/pmd/pmd/issues/2105): \[java] Wrong name for inner classes in violations
*   java-bestpractices
    *   [#2016](https://github.com/pmd/pmd/issues/2016): \[java] UnusedImports: False positive if wildcard is used and only static methods
*   java-codestyle
    *   [#1362](https://github.com/pmd/pmd/issues/1362): \[java] LinguisticNaming flags Predicates with boolean-style names
    *   [#2029](https://github.com/pmd/pmd/issues/2029): \[java] UnnecessaryFullyQualifiedName false-positive for non-static nested classes
    *   [#2098](https://github.com/pmd/pmd/issues/2098): \[java] UnnecessaryFullyQualifiedName: regression / false positive
*   java-design
    *   [#2075](https://github.com/pmd/pmd/issues/2075): \[java] ImmutableField false positive with inner class
    *   [#2125](https://github.com/pmd/pmd/issues/2125): \[java] ImmutableField: False positive when variable is updated in conditional loop
*   java-errorprone
    *   [#2102](https://github.com/pmd/pmd/issues/2102): \[java] False positive MissingStaticMethodInNonInstantiatableClass when inheritors are instantiable

### External Contributions

*   [#2088](https://github.com/pmd/pmd/pull/2088): \[java] Add more version shortcuts for older java - [Henning Schmiedehausen](https://github.com/hgschmie)
*   [#2089](https://github.com/pmd/pmd/pull/2089): \[core] Minor unrelated improvements to code - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#2091](https://github.com/pmd/pmd/pull/2091): \[core] Fix pmd warnings (IdenticalCatchCases) - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#2106](https://github.com/pmd/pmd/pull/2106): \[java] Wrong name for inner classes - [Andi Pabst](https://github.com/andipabst)
*   [#2121](https://github.com/pmd/pmd/pull/2121): \[java] Predicates treated like booleans - [Ozan Gulle](https://github.com/ozangulle)

## 31-October-2019 - 6.19.0

The PMD team is pleased to announce PMD 6.19.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Java Metrics](#java-metrics)
    * [Modified Rules](#modified-rules)
    * [Renamed Rules](#renamed-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
        * [Internal APIs](#internal-apis)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.19.0).

#### Java Metrics

*   The new metric "Class Fan Out Complexity" has been added. See
    [Java Metrics Documentation](https://pmd.github.io/pmd-6.19.0/pmd_java_metrics_index.html#class-fan-out-complexity-class_fan_out) for details.


#### Modified Rules

*   The Java rules [`InvalidLogMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidlogmessageformat) and [`MoreThanOneLogger`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#morethanonelogger)
    (`java-errorprone`) now both support [Log4j2](https://logging.apache.org/log4j/2.x/). Note that the
    rule "InvalidSlf4jMessageFormat" has been renamed to "InvalidLogMessageFormat" to reflect the fact, that it now
    supports more than slf4j.

*   The Java rule [`LawOfDemeter`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_design.html#lawofdemeter) (`java-design`) ignores now also Builders, that are
    not assigned to a local variable, but just directly used within a method call chain. The method, that creates
    the builder needs to end with "Builder", e.g. `newBuilder()` or `initBuilder()` works. This change
    fixes a couple of false positives.

*   The Java rule [`DataflowAnomalyAnalysis`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#dataflowanomalyanalysis) (`java-errorprone`) doesn't check for
    UR anomalies (undefined and then referenced) anymore. These checks were all false-positives, since actual
    UR occurrences would lead to compile errors.

*   The java rule [`DoNotUseThreads`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_multithreading.html#donotusethreads) (`java-multithreading`) has been changed
    to not report usages of `java.lang.Runnable` anymore. Just using `Runnable` does not automatically create
    a new thread. While the check for `Runnable` has been removed, the rule now additionally checks for
    usages of `Executors` and `ExecutorService`. Both create new threads, which are not managed by a J2EE
    server.

#### Renamed Rules

*   The Java rule [`InvalidSlf4jMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidslf4jmessageformat) has been renamed to
    [`InvalidLogMessageFormat`](https://pmd.github.io/pmd-6.19.0/pmd_rules_java_errorprone.html#invalidlogmessageformat) since it supports now both slf4j and log4j2
    message formats.

### Fixed Issues

*   core
    *   [#1978](https://github.com/pmd/pmd/issues/1978): \[core] PMD fails on excluding unknown rules
    *   [#2014](https://github.com/pmd/pmd/issues/2014): \[core] Making add(SourceCode sourceCode) public for alternative file systems
    *   [#2020](https://github.com/pmd/pmd/issues/2020): \[core] Wrong deprecation warnings for unused XPath attributes
    *   [#2036](https://github.com/pmd/pmd/issues/2036): \[core] Wrong include/exclude patterns are silently ignored
    *   [#2048](https://github.com/pmd/pmd/issues/2048): \[core] Enable type resolution by default for XPath rules
    *   [#2067](https://github.com/pmd/pmd/issues/2067): \[core] Build issue on Windows
    *   [#2068](https://github.com/pmd/pmd/pull/2068): \[core] Rule loader should use the same resources loader for the ruleset
    *   [#2071](https://github.com/pmd/pmd/issues/2071): \[ci] Add travis build on windows
    *   [#2072](https://github.com/pmd/pmd/issues/2072): \[test]\[core] Not enough info in "test setup error" when numbers of lines do not match
    *   [#2082](https://github.com/pmd/pmd/issues/2082): \[core] Incorrect logging of deprecated/renamed rules
*   java
    *   [#2042](https://github.com/pmd/pmd/issues/2042): \[java] PMD crashes with ClassFormatError: Absent Code attribute...
*   java-bestpractices
    *   [#1531](https://github.com/pmd/pmd/issues/1531): \[java] UnusedPrivateMethod false-positive with method result
    *   [#2025](https://github.com/pmd/pmd/issues/2025): \[java] UnusedImports when @see / @link pattern includes a FQCN
*   java-codestyle
    *   [#2017](https://github.com/pmd/pmd/issues/2017): \[java] UnnecessaryFullyQualifiedName triggered for inner class
*   java-design
    *   [#1912](https://github.com/pmd/pmd/issues/1912): \[java] Metrics not computed correctly with annotations
*   java-errorprone
    *   [#336](https://github.com/pmd/pmd/issues/336): \[java] InvalidSlf4jMessageFormat applies to log4j2
    *   [#1636](https://github.com/pmd/pmd/issues/1636): \[java] Stop checking UR anomalies for DataflowAnomalyAnalysis
*   java-multithreading
    *   [#1627](https://github.com/pmd/pmd/issues/1627): \[java] DoNotUseThreads should not warn on Runnable
*   doc
    * [#2058](https://github.com/pmd/pmd/issues/2058): \[doc] CLI reference for `-norulesetcompatibility` shows a boolean default value


### API Changes

#### Deprecated APIs

##### For removal

* pmd-core
    * All the package [`net.sourceforge.pmd.dcd`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/dcd/package-summary.html#) and its subpackages. See [`DCD`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/dcd/DCD.html#).
    * In [`LanguageRegistry`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#):
        * [`commaSeparatedTerseNamesForLanguageVersion`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguageVersion(List))
        * [`commaSeparatedTerseNamesForLanguage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#commaSeparatedTerseNamesForLanguage(List))
        * [`findAllVersions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findAllVersions())
        * [`findLanguageVersionByTerseName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#findLanguageVersionByTerseName(String))
        * [`getInstance`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/LanguageRegistry.html#getInstance())
    * [`RuleSet#getExcludePatterns`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getExcludePatterns()). Use the new method [`getFileExclusions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getFileExclusions()) instead.
    * [`RuleSet#getIncludePatterns`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getIncludePatterns()). Use the new method [`getFileInclusions`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/RuleSet.html#getFileInclusions()) instead.
    * [`Parser#canParse`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/Parser.html#canParse())
    * [`Parser#getSuppressMap`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/lang/Parser.html#getSuppressMap())
    * [`RuleBuilder#RuleBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/rules/RuleBuilder.html#RuleBuilder(String,String,String)). Use the new constructor with the correct ResourceLoader instead.
    * [`RuleFactory#RuleFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/rules/RuleFactory.html#RuleFactory()). Use the new constructor with the correct ResourceLoader instead.
* pmd-java
    * [`CanSuppressWarnings`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/CanSuppressWarnings.html#) and its implementations
    * [`isSuppressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#isSuppressed(Node))
    * [`getDeclaringType`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/AbstractJavaRule.html#getDeclaringType(Node)).
    * [`isSupressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/rule/JavaRuleViolation.html#isSupressed(Node,Rule))
    * [`ASTMethodDeclarator`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclarator.html#)
    * [`getMethodName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getMethodName())
    * [`getBlock`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTMethodDeclaration.html#getBlock())
    * [`getParameterCount`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.19.0/net/sourceforge/pmd/lang/java/ast/ASTConstructorDeclaration.html#getParameterCount())
* pmd-apex
    * [`CanSuppressWarnings`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.19.0/net/sourceforge/pmd/lang/apex/ast/CanSuppressWarnings.html#) and its implementations
    * [`isSupressed`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.19.0/net/sourceforge/pmd/lang/apex/rule/ApexRuleViolation.html#isSupressed(Node,Rule))

##### Internal APIs

* pmd-core
    * All the package [`net.sourceforge.pmd.util`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/package-summary.html#) and its subpackages,
      except [`net.sourceforge.pmd.util.datasource`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/datasource/package-summary.html#) and [`net.sourceforge.pmd.util.database`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/util/database/package-summary.html#).
    * [`GridBagHelper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/cpd/GridBagHelper.html#)
    * [`ColumnDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.19.0/net/sourceforge/pmd/renderers/ColumnDescriptor.html#)



### External Contributions

*   [#2010](https://github.com/pmd/pmd/pull/2010): \[java] LawOfDemeter to support inner builder pattern - [Gregor Riegler](https://github.com/gregorriegler)
*   [#2012](https://github.com/pmd/pmd/pull/2012): \[java] Fixes 336, slf4j log4j2 support - [Mark Hall](https://github.com/markhall82)
*   [#2032](https://github.com/pmd/pmd/pull/2032): \[core] Allow adding SourceCode directly into CPD - [Nathan Braun](https://github.com/nbraun-Google)
*   [#2047](https://github.com/pmd/pmd/pull/2047): \[java] Fix computation of metrics with annotations - [Andi Pabst](https://github.com/andipabst)
*   [#2065](https://github.com/pmd/pmd/pull/2065): \[java] Stop checking UR anomalies - [Carlos Macasaet](https://github.com/l0s)
*   [#2068](https://github.com/pmd/pmd/pull/2068): \[core] Rule loader should use the same resources loader for the ruleset - [Chen Yang](https://github.com/willamette)
*   [#2070](https://github.com/pmd/pmd/pull/2070): \[core] Fix renderer tests for windows builds - [Saladoc](https://github.com/Saladoc)
*   [#2073](https://github.com/pmd/pmd/pull/2073): \[test]\[core] Add expected and actual line of numbers to message wording - [snuyanzin](https://github.com/snuyanzin)
*   [#2076](https://github.com/pmd/pmd/pull/2076): \[java] Add Metric ClassFanOutComplexity - [Andi Pabst](https://github.com/andipabst)
*   [#2078](https://github.com/pmd/pmd/pull/2078): \[java] DoNotUseThreads should not warn on Runnable #1627 - [Michael Clay](https://github.com/mclay)

## 15-September-2019 - 6.18.0

The PMD team is pleased to announce PMD 6.18.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Java 13 Support](#java-13-support)
    * [Full support for Scala](#full-support-for-scala)
    * [New rule designer documentation](#new-rule-designer-documentation)
    * [New rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Changes to Renderer](#changes-to-renderer)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
        * [Internal APIs](#internal-apis)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Java 13 Support

This release of PMD brings support for Java 13. PMD can parse [Switch Expressions](http://openjdk.java.net/jeps/354)
with the new `yield` statement and resolve the type of such an expression.

PMD also parses [Text Blocks](http://openjdk.java.net/jeps/355) as String literals.

Note: The Switch Expressions and Text Blocks are a preview language feature of OpenJDK 13
and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the environment
variable `PMD_JAVA_OPTS` and select the new language version `13-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd -language java -version 13-preview ...

Note: Support for the extended break statement introduced in Java 12 as a preview language feature
will be removed with the next PMD version 6.19.0.

#### Full support for Scala

Thanks to [Chris Smith](https://github.com/tophersmith) PMD now fully supports Scala. Now rules for analyzing Scala
code can be developed in addition to the Copy-Paste-Detection (CPD) functionality. There are no rules yet, so
contributions are welcome.

Additionally Scala support has been upgraded from 2.12.4 to 2.13.

#### New rule designer documentation

The documentation for the rule designer is now available on the main PMD documentation page:
[Rule Designer Reference](pmd_userdocs_extending_designer_reference.html). Check it out to learn
about the usage and features of the rule designer.

#### New rules

*   The Java rule [`AvoidMessageDigestField`](https://pmd.github.io/pmd-6.18.0/pmd_rules_java_bestpractices.html#avoidmessagedigestfield) (`java-bestpractices`) detects fields
    of the type `java.security.MessageDigest`. Using a message digest instance as a field would need to be
    synchronized, as it can easily be used by multiple threads. Without synchronization the calculated hash could
    be entirely wrong. Instead of declaring this as a field and synchronize access to use it from multiple threads,
    a new instance should be created when needed. This rule is also active when using java's quickstart ruleset.

*   The Apex rule [`DebugsShouldUseLoggingLevel`](https://pmd.github.io/pmd-6.18.0/pmd_rules_apex_bestpractices.html#debugsshoulduselogginglevel) (`apex-bestpractices`) detects
    usages of `System.debug()` method calls that are used without specifying the log level. Having the log
    level specified provides a cleaner log, and improves readability of it.

#### Modified Rules

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.18.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) now ignores by default instances
    of `java.util.stream.Stream`. These streams are `AutoCloseable`, but most streams are backed by collections,
    arrays, or generating functions, which require no special resource management. However, there are some exceptions:
    The stream returned by `Files::lines(Path)` is backed by a actual file and needs to be closed. These instances
    won't be found by default by the rule anymore.

### Fixed Issues

*   all
    *   [#1465](https://github.com/pmd/pmd/issues/1465): \[core] Stylesheet pmd-report.xslt fails to display filepath if 'java' in path
    *   [#1923](https://github.com/pmd/pmd/issues/1923): \[core] Incremental analysis does not work with shortnames
    *   [#1983](https://github.com/pmd/pmd/pull/1983): \[core] Avoid crashes with analysis cache when classpath references non-existing directories
    *   [#1990](https://github.com/pmd/pmd/pull/1990): \[core] Incremental analysis mixes XPath rule violations
*   apex
    *   [#1901](https://github.com/pmd/pmd/issues/1901): \[apex] Expose super type name of UserClass
    *   [#1942](https://github.com/pmd/pmd/issues/1942): \[apex] Add best practice rule for debug statements in Apex
*   java
    *   [#1930](https://github.com/pmd/pmd/issues/1930): \[java] Add Java 13 support
*   java-bestpractices
    *   [#1227](https://github.com/pmd/pmd/issues/1227): \[java] UnusedFormalParameter should explain checkAll better
    *   [#1862](https://github.com/pmd/pmd/issues/1862): \[java] New rule for MessageDigest.getInstance
    *   [#1952](https://github.com/pmd/pmd/issues/1952): \[java] UnusedPrivateField not triggering if @Value annotation present
*   java-codestyle
    *   [#1951](https://github.com/pmd/pmd/issues/1951): \[java] UnnecessaryFullyQualifiedName rule triggered when variable name clashes with package name
*   java-errorprone
    *   [#1922](https://github.com/pmd/pmd/issues/1922): \[java] CloseResource possible false positive with Streams
    *   [#1966](https://github.com/pmd/pmd/issues/1966): \[java] CloseResource false positive if Stream is passed as method parameter
    *   [#1967](https://github.com/pmd/pmd/issues/1967): \[java] CloseResource false positive with late assignment of variable
*   plsql
    *   [#1933](https://github.com/pmd/pmd/issues/1933): \[plsql] ParseException with cursor declared in anonymous block
    *   [#1935](https://github.com/pmd/pmd/issues/1935): \[plsql] ParseException with SELECT INTO record defined as global variable
    *   [#1936](https://github.com/pmd/pmd/issues/1936): \[plslq] ParseException with cursor inside procedure declaration
    *   [#1946](https://github.com/pmd/pmd/issues/1946): \[plsql] ParseException with using TRIM inside IF statements condition
    *   [#1947](https://github.com/pmd/pmd/issues/1947): \[plsql] ParseError - SELECT with FOR UPDATE OF
    *   [#1948](https://github.com/pmd/pmd/issues/1948): \[plsql] ParseException with INSERT INTO using package global variables
    *   [#1950](https://github.com/pmd/pmd/issues/1950): \[plsql] ParseException with UPDATE and package record variable
    *   [#1953](https://github.com/pmd/pmd/issues/1953): \[plsql] ParseException with WITH in CURSOR

### API Changes

#### Changes to Renderer

*   Each renderer has now a new method [`Renderer#setUseShortNames`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/renderers/Renderer.html#setUseShortNames(List)) which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    [`AbstractRenderer#determineFileName`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/renderers/AbstractRenderer.html#determineFileName(String)) should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.

    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

#### Deprecated APIs

##### For removal

*   The methods [`getImportedNameNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getImportedNameNode()) and
    [`getPackage`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTImportDeclaration.html#getPackage()) have been deprecated and
    will be removed with PMD 7.0.0.
*   The method [`RuleContext#setSourceCodeFilename`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFilename(String)) has been deprecated
    and will be removed. The already existing method [`RuleContext#setSourceCodeFile`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#setSourceCodeFile(File))
    should be used instead. The method [`RuleContext#getSourceCodeFilename`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/RuleContext.html#getSourceCodeFilename()) still
    exists and returns just the filename without the full path.
*   The method [`AbstractPMDProcessor#filenameFrom`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/processor/AbstractPMDProcessor.html#filenameFrom(DataSource)) has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The method [`Report#metrics`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/Report.html#metrics()) and [`Report`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/Report.html#) have
    been deprecated. They were leftovers from a previous deprecation round targeting
    [`StatisticalRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#).

##### Internal APIs

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
    * [`net.sourceforge.pmd.cache`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.18.0/net/sourceforge/pmd/cache/package-summary.html#)
* pmd-java
    * [`net.sourceforge.pmd.lang.java.typeresolution`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/package-summary.html#): Everything, including
      subpackages, except [`TypeHelper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/TypeHelper.html#) and
      [`JavaTypeDefinition`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/typeresolution/typedefinition/JavaTypeDefinition.html#).
    * [`ASTCompilationUnit#getClassTypeResolver`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.18.0/net/sourceforge/pmd/lang/java/ast/ASTCompilationUnit.html#getClassTypeResolver())

### External Contributions

*   [#1943](https://github.com/pmd/pmd/pull/1943): \[apex] Adds "debug should use logging level" best practice rule for Apex - [Renato Oliveira](https://github.com/renatoliveira)
*   [#1965](https://github.com/pmd/pmd/pull/1965): \[scala] Use Scalameta for parsing - [Chris Smith](https://github.com/tophersmith)
*   [#1970](https://github.com/pmd/pmd/pull/1970): \[java] DoubleBraceInitialization: Fix example - [Tobias Weimer](https://github.com/tweimer)
*   [#1971](https://github.com/pmd/pmd/pull/1971): \[java] 1862 - Message Digest should not be used as class field - [AnthonyKot](https://github.com/AnthonyKot)
*   [#1972](https://github.com/pmd/pmd/pull/1972): \[plsql] ParseError - SELECT with FOR UPDATE OF - [Piotr Szymanski](https://github.com/szyman23)
*   [#1974](https://github.com/pmd/pmd/pull/1974): \[plsql] Fixes for referencing record type variables - [Piotr Szymanski](https://github.com/szyman23)
*   [#1975](https://github.com/pmd/pmd/pull/1975): \[plsql] TRIM function with record type variables - [Piotr Szymanski](https://github.com/szyman23)
*   [#1976](https://github.com/pmd/pmd/pull/1976): \[plsql] Fix for mistaking / for MultiplicativeExpression - [Piotr Szymanski](https://github.com/szyman23)
*   [#1977](https://github.com/pmd/pmd/pull/1977): \[plsql] fix for skipping sql starting with WITH - [Piotr Szymanski](https://github.com/szyman23)
*   [#1986](https://github.com/pmd/pmd/pull/1986): \[plsql] Fix for cursors in anonymous blocks - [Piotr Szymanski](https://github.com/szyman23)
*   [#1994](https://github.com/pmd/pmd/pull/1994): \[core] Resolve pmd-report failure when java folder in filepath - [Amish Shah](https://github.com/shahamish150294)
*   [#2015](https://github.com/pmd/pmd/pull/2015): \[java] Update doc for unused formal parameter - [Amish Shah](https://github.com/shahamish150294)

## 28-July-2019 - 6.17.0

The PMD team is pleased to announce PMD 6.17.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [Lua support](#lua-support)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.17.0).
It contains a new feature to edit test cases directly within the designer. Any feedback is highly appreciated.

#### Lua support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Lua](https://www.lua.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

#### Modified Rules

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.17.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) ignores now by default
    `java.io.ByteArrayInputStream` and `java.io.CharArrayWriter`. Such streams/writers do not need to be closed.

*   The Java rule [`MissingStaticMethodInNonInstantiatableClass`](https://pmd.github.io/pmd-6.17.0/pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass) (`java-errorprone`) has now
    the new property `annotations`.
    When one of the private constructors is annotated with one of the annotations, then the class is not considered
    non-instantiatable anymore and no violation will be reported. By default, Spring's `@Autowired` and
    Java EE's `@Inject` annotations are recognized.

### Fixed Issues

*   core
    *   [#1913](https://github.com/pmd/pmd/issues/1913): \[core] "-help" CLI option ends with status code != 0
*   doc
    *   [#1896](https://github.com/pmd/pmd/issues/1896): \[doc] Error in changelog 6.16.0 due to not properly closed rule tag
    *   [#1898](https://github.com/pmd/pmd/issues/1898): \[doc] Incorrect code example for DoubleBraceInitialization in documentation on website
    *   [#1906](https://github.com/pmd/pmd/issues/1906): \[doc] Broken link for adding own CPD languages
    *   [#1909](https://github.com/pmd/pmd/issues/1909): \[doc] Sample usage example refers to deprecated ruleset "basic.xml" instead of "quickstart.xml"
*   java
    *   [#1910](https://github.com/pmd/pmd/issues/1910): \[java] ATFD calculation problem
*   java-errorprone
    *   [#1749](https://github.com/pmd/pmd/issues/1749): \[java] DD False Positive in DataflowAnomalyAnalysis
    *   [#1832](https://github.com/pmd/pmd/issues/1832): \[java] False positives for MissingStaticMethodInNonInstantiatableClass when DI is used
    *   [#1921](https://github.com/pmd/pmd/issues/1921): \[java] CloseResource false positive with ByteArrayInputStream
*   java-multithreading
    *   [#1903](https://github.com/pmd/pmd/issues/1903): \[java] UnsynchronizedStaticFormatter doesn't allow block-level synchronization when using allowMethodLevelSynchronization=true
*   plsql
    *   [#1902](https://github.com/pmd/pmd/issues/1902): \[pslql] ParseException when parsing (+)
*   xml
    *   [#1666](https://github.com/pmd/pmd/issues/1666): \[xml] wrong cdata rule description and examples

### External Contributions

*   [#1869](https://github.com/pmd/pmd/pull/1869): \[xml] fix #1666 wrong cdata rule description and examples - [Artem](https://github.com/KroArtem)
*   [#1892](https://github.com/pmd/pmd/pull/1892): \[lua] \[cpd] Added CPD support for Lua - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1905](https://github.com/pmd/pmd/pull/1905): \[java] DataflowAnomalyAnalysis Rule in right order - [YoonhoChoi96](https://github.com/YoonhoChoi96)
*   [#1908](https://github.com/pmd/pmd/pull/1908): \[doc] Update ruleset filename from deprecated basic.xml to quickstart.xml - [crunsk](https://github.com/crunsk)
*   [#1916](https://github.com/pmd/pmd/pull/1916): \[java] Exclude Autowired and Inject for MissingStaticMethodInNonInstantiatableClass - [AnthonyKot](https://github.com/AnthonyKot)
*   [#1917](https://github.com/pmd/pmd/pull/1917): \[core] Add 'no error' return option, and assign it to the cli when the help command is invoked - [Renato Oliveira](https://github.com/renatoliveira)

## 30-June-2019 - 6.16.0

The PMD team is pleased to announce PMD 6.16.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated PMD Designer](#updated-pmd-designer)
    * [PLSQL Grammar Updates](#plsql-grammar-updates)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [In ASTs](#in-asts)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/6.16.0).

#### PLSQL Grammar Updates

The grammar has been updated to support inline constraints in CREATE TABLE statements. Additionally, the
CREATE TABLE statement may now be followed by physical properties and table properties. However, these
properties are skipped over during parsing.

The CREATE VIEW statement now supports subquery views.

The EXTRACT function can now be parsed correctly. It is used to extract values from a specified
datetime field. Also date time literals are parsed now correctly.

The CASE expression can now be properly used within SELECT statements.

Table aliases are now supported when specifying columns in INSERT INTO clauses.

#### New Rules

*   The Java rule [`DoubleBraceInitialization`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#doublebraceinitialization) (`java-bestpractices`)
    detects non static initializers in anonymous classes also known as "double brace initialization".
    This can be problematic, since a new class file is generated and object holds a strong reference
    to the surrounding class.

    Note: This rule is also part of the Java quickstart ruleset (`rulesets/java/quickstart.xml`).

#### Modified Rules

*   The Java rule [`UnusedPrivateField`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#unusedprivatefield) (`java-bestpractices`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rule [`SingularField`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_design.html#singularfield) (`java-design`) now ignores by
    default fields, that are annotated with the Lombok experimental annotation `@Delegate`. This can be
    customized with the property `ignoredAnnotations`.

*   The Java rules [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter) and
    [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) (`java-multithreading`)
    now prefer synchronized blocks by default. They will raise a violation, if the synchronization is implemented
    on the method level. To allow the old behavior, the new property `allowMethodLevelSynchronization` can
    be enabled.

*   The Java rule [`UseUtilityClass`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_design.html#useutilityclass) (`java-design`) has a new property `ignoredAnnotations`.
    By default, classes that are annotated with Lombok's `@UtilityClass` are ignored now.

*   The Java rule [`NonStaticInitializer`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#nonstaticinitializer) (`java-errorprone`) does not report
    non static initializers in anonymous classes anymore. For this use case, there is a new rule now:
    [`DoubleBraceInitialization`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_bestpractices.html#doublebraceinitialization) (`java-bestpractices`).

*   The Java rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`) was enhanced
    in the last version 6.15.0 to check also top-level types by default. This created many new violations.
    Missing the access modifier for top-level types is not so critical, since it only decreases the visibility
    of the type.

    The default behaviour has been restored. If you want to enable the check for top-level types, you can
    use the new property `checkTopLevelTypes`.

*   The Java rule [`CloseResource`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#closeresource) (`java-errorprone`) now by default searches
    for any unclosed `java.lang.AutoCloseable` resource. This includes now the standard `java.io.*Stream` classes.
    Previously only SQL-related resources were considered by this rule. The types can still be configured
    via the `types` property. Some resources do not need to be closed (e.g. `ByteArrayOutputStream`). These
    exceptions can be configured via the new property `allowedResourceTypes`.
    In order to restore the old behaviour, just remove the type `java.lang.AutoCloseable` from the `types`
    property and keep the remaining SQL-related classes.

#### Deprecated Rules

*   The Java rule [`AvoidFinalLocalVariable`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#avoidfinallocalvariable) (`java-codestyle`) has been deprecated
    and will be removed with PMD 7.0.0. The rule is controversial and also contradicts other existing
    rules such as [`LocalVariableCouldBeFinal`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_codestyle.html#localvariablecouldbefinal). If the goal is to avoid defining
    constants in a scope smaller than the class, then the rule [`AvoidDuplicateLiterals`](https://pmd.github.io/pmd-6.16.0/pmd_rules_java_errorprone.html#avoidduplicateliterals)
    should be used instead.

### Fixed Issues

*   apex
    *   [#1664](https://github.com/pmd/pmd/issues/1664): \[apex] False positive ApexSharingViolationsRule, unsupported Apex feature
*   java
    *   [#1848](https://github.com/pmd/pmd/issues/1848): \[java] Local classes should preserve their modifiers
*   java-bestpractices
    *   [#1703](https://github.com/pmd/pmd/issues/1703): \[java] UnusedPrivateField on member annotated with lombok @Delegate
    *   [#1845](https://github.com/pmd/pmd/issues/1845): \[java] Regression in MethodReturnsInternalArray not handling enums
    *   [#1854](https://github.com/pmd/pmd/issues/1854): \[java] Rule to check for double brace initialisation
*   java-codestyle
    *   [#1612](https://github.com/pmd/pmd/issues/1612): \[java] Deprecate AvoidFinalLocalVariable
    *   [#1880](https://github.com/pmd/pmd/issues/1880): \[java] CommentDefaultAccessModifier should be configurable for top-level classes
*   java-design
    *   [#1094](https://github.com/pmd/pmd/issues/1094): \[java] UseUtilityClass should be LombokAware
*   java-errorprone
    *   [#1000](https://github.com/pmd/pmd/issues/1000): \[java] The rule CloseResource should deal with IO stream as default
    *   [#1853](https://github.com/pmd/pmd/issues/1853): \[java] False positive for NonStaticInitializer in anonymous class
*   java-multithreading
    *   [#1814](https://github.com/pmd/pmd/issues/1814): \[java] UnsynchronizedStaticFormatter documentation and implementation wrong
    *   [#1815](https://github.com/pmd/pmd/issues/1815): \[java] False negative in UnsynchronizedStaticFormatter
*   plsql
    *   [#1828](https://github.com/pmd/pmd/issues/1828): \[plsql] Parentheses stopped working
    *   [#1850](https://github.com/pmd/pmd/issues/1850): \[plsql] Parsing errors with INSERT using returning or records and TRIM expression
    *   [#1873](https://github.com/pmd/pmd/issues/1873): \[plsql] Expression list not working
    *   [#1878](https://github.com/pmd/pmd/issues/1878): \[pslql] ParseException when parsing USING
    *   [#1879](https://github.com/pmd/pmd/issues/1879): \[pslql] ParseException when parsing LEFT JOIN

### API Changes

#### Deprecated APIs

> Reminder: Please don't use members marked with the annotation [`InternalApi`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/annotation/InternalApi.html#), as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


##### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked [`InternalApi`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/annotation/InternalApi.html#). Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
    * In the meantime you should use interfaces like [`JavaNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.16.0/net/sourceforge/pmd/lang/java/ast/JavaNode.html#) or  [`Node`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.16.0/net/sourceforge/pmd/lang/ast/Node.html#), or the other published interfaces in this package, to refer to nodes generically.
    * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at [`net.sourceforge.pmd.lang.java.ast`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.16.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#) to find out the full list
of deprecations.





### External Contributions

*   [#1482](https://github.com/pmd/pmd/pull/1482): \[java] Explain the existence of AvoidFinalLocalVariable in it's description - [Karl-Philipp Richter](https://github.com/krichter722)
*   [#1792](https://github.com/pmd/pmd/pull/1792): \[java] Added lombok.experimental to AbstractLombokAwareRule - [jakivey32](https://github.com/jakivey32)
*   [#1808](https://github.com/pmd/pmd/pull/1808): \[plsql] Fix PL/SQL Syntax errors - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1829](https://github.com/pmd/pmd/pull/1829): \[java] Fix false negative in UnsynchronizedStaticFormatter - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1847](https://github.com/pmd/pmd/pull/1847): \[java] Regression in MethodReturnsInternalArray not handling enums - [Artem](https://github.com/KroArtem)
*   [#1863](https://github.com/pmd/pmd/pull/1863): \[plsql] Add Table InlineConstraint - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1864](https://github.com/pmd/pmd/pull/1864): \[plsql] Add support for Subquery Views - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1865](https://github.com/pmd/pmd/pull/1865): \[plsql] Add Support for Extract Expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1874](https://github.com/pmd/pmd/pull/1874): \[plsql] Add parenthesis equation support for Update - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1876](https://github.com/pmd/pmd/pull/1876): \[plsql] Datetime support for queries - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1883](https://github.com/pmd/pmd/pull/1883): \[plsql] Fix #1873 Expression list not working - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1884](https://github.com/pmd/pmd/pull/1884): \[plsql] fix #1878 Support explicit INNER word for INNER JOIN - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1885](https://github.com/pmd/pmd/pull/1885): \[plsql] Correct case expression - [Hugo Araya Nash](https://github.com/kabroxiko)
*   [#1886](https://github.com/pmd/pmd/pull/1886): \[plsql] Support table alias for Insert Clause - [Hugo Araya Nash](https://github.com/kabroxiko)

## 26-May-2019 - 6.15.0

The PMD team is pleased to announce PMD 6.15.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Enhanced Matlab support](#enhanced-matlab-support)
    * [Enhanced C++ support](#enhanced-c++-support)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Deprecated APIs](#deprecated-apis)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Enhanced Matlab support

Thanks to the contributions from [Maikel Steneker](https://github.com/maikelsteneker) CPD for Matlab can
now parse Matlab programs which use the question mark operator to specify access to
class members:

```
classdef Class1
properties (SetAccess = ?Class2)
```

CPD also understands now double quoted strings, which are supported since version R2017a of Matlab:

```
str = "This is a string"
```

#### Enhanced C++ support

CPD now supports digit separators in C++ (language module "cpp"). This is a C++14 feature.

Example: `auto integer_literal = 1'000'000;`

The single quotes can be used to add some structure to large numbers.

CPD also parses raw string literals now correctly (see [#1784](https://github.com/pmd/pmd/issues/1784)).

#### New Rules

*   The new Apex rule [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#fieldnamingconventions) (`apex-codestyle`) checks the naming
    conventions for field declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Apex rule [`FormalParameterNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#formalparameternamingconventions) (`apex-codestyle`) checks the
    naming conventions for formal parameters of methods. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule [`LocalVariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#localvariablenamingconventions) (`apex-codestyle`) checks the
    naming conventions for local variable declarations. By default this rule uses the standard Apex naming
    convention (Camel case), but it can be configured through properties.

*   The new Apex rule [`PropertyNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#propertynamingconventions) (`apex-codestyle`) checks the naming
    conventions for property declarations. By default this rule uses the standard Apex naming convention (Camel case),
    but it can be configured through properties.

*   The new Java rule [`UseShortArrayInitializer`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#useshortarrayinitializer) (`java-codestyle`) searches for
    array initialization expressions, which can be written shorter.

#### Modified Rules

*   The Apex rule [`ClassNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#classnamingconventions) (`apex-codestyle`) can now be configured
    using various properties for the specific kind of type declarations (e.g. class, interface, enum).
    As before, this rule uses by default the standard Apex naming convention (Pascal case).

*   The Apex rule [`MethodNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#methodnamingconventions) (`apex-codestyle`) can now be configured
    using various properties to differenciate e.g. static methods and test methods.
    As before, this rule uses by default the standard Apex naming convention (Camel case).

*   The Java rule [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#fieldnamingconventions) (`java-codestyle`) now by default ignores
    the field `serialPersistentFields`. Since this is a field which needs to have this special name, no
    field naming conventions can be applied here. It is excluded the same way like `serialVersionUID` via the
    property `exclusions`.

*   The Java rule [`CommentRequired`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_documentation.html#commentrequired) (`java-documentation`) has a new property
    `serialPersistentFieldsCommentRequired` with the default value "Ignored". This means that from now
    on comments for the field `serialPersistentFields` are not required anymore. You can change the property
    to restore the old behavior.

*   The Java rule [`ProperLogger`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#properlogger) (`java-errorprone`) has two new properties
    to configure the logger class (e.g. "org.slf4j.Logger") and the logger name of the special case,
    when the logger is not static. The name of the static logger variable was already configurable.
    The new property "loggerClass" allows to use this rule for different logging frameworks.
    This rule covers all the cases of the now deprecated rule [`LoggerIsNotStaticFinal`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#loggerisnotstaticfinal).

*   The Java rule [`CommentDefaultAccessModifier`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`) now reports also
    missing comments for top-level classes and annotations, that are package-private.

#### Deprecated Rules

*   The Apex rule [`VariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#variablenamingconventions) (`apex-codestyle`) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general rules
    [`FieldNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#fieldnamingconventions),
    [`FormalParameterNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#formalparameternamingconventions),
    [`LocalVariableNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#localvariablenamingconventions), and
    [`PropertyNamingConventions`](https://pmd.github.io/pmd-6.15.0/pmd_rules_apex_codestyle.html#propertynamingconventions).

*   The Java rule [`LoggerIsNotStaticFinal`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#loggerisnotstaticfinal) (`java-errorprone`) has been deprecated
    and will be removed with PMD 7.0.0. The rule is replaced by [`ProperLogger`](https://pmd.github.io/pmd-6.15.0/pmd_rules_java_errorprone.html#properlogger).

### Fixed Issues

*   apex
    *   [#1321](https://github.com/pmd/pmd/issues/1321): \[apex] Should VariableNamingConventions require properties to start with a lowercase letter?
    *   [#1783](https://github.com/pmd/pmd/issues/1783): \[apex] comments on constructor not recognized when the Class has inner class
*   cpp
    *   [#1784](https://github.com/pmd/pmd/issues/1784): \[cpp] Improve support for raw string literals
*   dart
    *   [#1809](https://github.com/pmd/pmd/issues/1809): \[dart] \[cpd] Parse error with escape sequences
*   java
    *   [#1842](https://github.com/pmd/pmd/issues/1842): \[java] Annotated module declarations cause parse error
*   java-bestpractices
    *   [#1738](https://github.com/pmd/pmd/issues/1738): \[java] MethodReturnsInternalArray does not work in inner classes
*   java-codestyle
    *   [#1495](https://github.com/pmd/pmd/issues/1495): \[java] Rule to detect overly verbose array initializiation
    *   [#1684](https://github.com/pmd/pmd/issues/1684): \[java] Properly whitelist serialPersistentFields
    *   [#1804](https://github.com/pmd/pmd/issues/1804): \[java] NPE in UnnecessaryLocalBeforeReturnRule
*   python
    *   [#1810](https://github.com/pmd/pmd/issues/1810): \[python] \[cpd] Parse error when using Python 2 backticks
*   matlab
    *   [#1830](https://github.com/pmd/pmd/issues/1830): \[matlab] \[cpd] Parse error with comments
    *   [#1793](https://github.com/pmd/pmd/issues/1793): \[java] CommentDefaultAccessModifier not working for classes

### API Changes

#### Deprecated APIs

##### For removal

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
    *   [`net.sourceforge.pmd.lang.apex.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.15.0/net/sourceforge/pmd/lang/apex/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.java.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.15.0/net/sourceforge/pmd/lang/java/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.ecmascript.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-javascript/6.15.0/net/sourceforge/pmd/lang/ecmascript/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.jsp.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-jsp/6.15.0/net/sourceforge/pmd/lang/jsp/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.plsql.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-plsql/6.15.0/net/sourceforge/pmd/lang/plsql/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.vf.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-visualforce/6.15.0/net/sourceforge/pmd/lang/vf/ast/DumpFacade.html#)
    *   [`net.sourceforge.pmd.lang.vm.ast.AbstractVmNode#dump`](https://javadoc.io/page/net.sourceforge.pmd/pmd-vm/6.15.0/net/sourceforge/pmd/lang/vm/ast/AbstractVmNode.html#dump(String,boolean,Writer))
    *   [`net.sourceforge.pmd.lang.xml.ast.DumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-xml/6.15.0/net/sourceforge/pmd/lang/xml/ast/DumpFacade.html#)
*   The method [`LanguageVersionHandler#getDumpFacade`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.15.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDumpFacade(Writer,String,boolean)) will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of [`LanguageVersionHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.15.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#).

### External Contributions

*   [#1647](https://github.com/pmd/pmd/pull/1647): \[java] Rule to detect overly verbose array initialization - [Victor](https://github.com/IDoCodingStuffs)
*   [#1762](https://github.com/pmd/pmd/pull/1762): \[java] LoggerIsNotStaticFinal and ProperLogger - make class-name configurable - [Ivo Šmíd](https://github.com/bedla)
*   [#1798](https://github.com/pmd/pmd/pull/1798): \[java] Make CommentDefaultAccessModifier work for top-level classes - [Boris Petrov](https://github.com/boris-petrov)
*   [#1799](https://github.com/pmd/pmd/pull/1799): \[java] MethodReturnsInternalArray does not work in inner classes - Fixed #1738 - [Srinivasan Venkatachalam](https://github.com/Srini1993)
*   [#1802](https://github.com/pmd/pmd/pull/1802): \[python] \[cpd] Add support for Python 2 backticks - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1803](https://github.com/pmd/pmd/pull/1803): \[dart] \[cpd] Dart escape sequences - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1807](https://github.com/pmd/pmd/pull/1807): \[ci] Fix missing local branch issues when executing pmd-regression-tester - [BBG](https://github.com/djydewang)
*   [#1813](https://github.com/pmd/pmd/pull/1813): \[matlab] \[cpd] Matlab comments - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1816](https://github.com/pmd/pmd/pull/1816): \[apex] Fix ApexDoc handling with inner classes - [Jeff Hube](https://github.com/jeffhube)
*   [#1817](https://github.com/pmd/pmd/pull/1817): \[apex] Add configurable naming convention rules - [Jeff Hube](https://github.com/jeffhube)
*   [#1819](https://github.com/pmd/pmd/pull/1819): \[cpp] \[cpd] Add support for digit separators - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1820](https://github.com/pmd/pmd/pull/1820): \[cpp] \[cpd] Improve support for raw string literals - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1821](https://github.com/pmd/pmd/pull/1821): \[matlab] \[cpd] Matlab question mark token - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1822](https://github.com/pmd/pmd/pull/1822): \[matlab] \[cpd] Double quoted string - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1837](https://github.com/pmd/pmd/pull/1837): \[core] Minor performance improvements - [Michael Hausegger](https://github.com/TheRealHaui)
*   [#1838](https://github.com/pmd/pmd/pull/1838): \[dart] [cpd] Improved string tokenization - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1840](https://github.com/pmd/pmd/pull/1840): \[java] Whitelist serialPersistentFields - [Marcel Härle](https://github.com/marcelhaerle)

## 28-April-2019 - 6.14.0

The PMD team is pleased to announce PMD 6.14.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Dart support](#dart-support)
    * [Updated PMD Designer](#updated-pmd-designer)
* [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Dart support

Thanks to the contribution from [Maikel Steneker](https://github.com/maikelsteneker), and built on top of the ongoing efforts to fully support Antlr-based languages,
PMD now has CPD support for [Dart](https://www.dartlang.org/).

Being based on a proper Antlr grammar, CPD can:
*   ignore comments
*   ignore imports / libraries
*   honor [comment-based suppressions](pmd_userdocs_cpd.html#suppression)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/blob/6.14.0/CHANGELOG.md).

### Modified Rules

*   The Java rule [`AssignmentToNonFinalStatic`](https://pmd.github.io/pmd-6.14.0/pmd_rules_java_errorprone.html#assignmenttononfinalstatic) (`java-errorprone`) will now report on each
    assignment made within a constructor rather than on the field declaration. This makes it easier for developers to
    find the offending statements.

*   The Java rule [`NoPackage`](https://pmd.github.io/pmd-6.14.0/pmd_rules_java_codestyle.html#nopackage) (`java-codestyle`) will now report additionally enums
    and annotations that do not have a package declaration.

### Fixed Issues

*   all
    *   [#1515](https://github.com/pmd/pmd/issues/1515): \[core] Module pmd-lang-test is missing javadoc artifact
    *   [#1788](https://github.com/pmd/pmd/issues/1788): \[cpd] \[core] Use better `ClassLoader` for `ServiceLoader` in `LanguageFactory`
    *   [#1794](https://github.com/pmd/pmd/issues/1794): \[core] Ruleset Compatibility fails with excluding rules
*   go
    *   [#1751](https://github.com/pmd/pmd/issues/1751): \[go] Parsing errors encountered with escaped backslash
*   java
    *   [#1532](https://github.com/pmd/pmd/issues/1532): \[java] NPE with incomplete auxclasspath
    *   [#1691](https://github.com/pmd/pmd/issues/1691): \[java] Possible Data Race in JavaTypeDefinitionSimple.getGenericType
    *   [#1729](https://github.com/pmd/pmd/issues/1729): \[java] JavaRuleViolation loses information in `className` field when class has package-private access level
*   java-bestpractices
    *   [#1190](https://github.com/pmd/pmd/issues/1190): \[java] UnusedLocalVariable/UnusedPrivateField false-positive
    *   [#1720](https://github.com/pmd/pmd/issues/1720): \[java] UnusedImports false positive for Javadoc link with array type
*   java-codestyle
    *   [#1755](https://github.com/pmd/pmd/issues/1775): \[java] False negative in UnnecessaryLocalBeforeReturn when splitting statements across multiple lines
    *   [#1782](https://github.com/pmd/pmd/issues/1782): \[java] NoPackage: False Negative for enums
*   java-design
    *   [#1760](https://github.com/pmd/pmd/issues/1760): \[java] UseObjectForClearerAPI flags private methods

### API Changes

No changes.

### External Contributions

*   [#1745](https://github.com/pmd/pmd/pull/1745): \[doc] Fixed some errors in docs - [0xflotus](https://github.com/0xflotus)
*   [#1746](https://github.com/pmd/pmd/pull/1746): \[java] Update rule to prevent UnusedImport when using JavaDoc with array type - [itaigilo](https://github.com/itaigilo)
*   [#1752](https://github.com/pmd/pmd/pull/1752): \[java] UseObjectForClearerAPI Only For Public - [Björn Kautler](https://github.com/Vampire)
*   [#1761](https://github.com/pmd/pmd/pull/1761): \[dart] \[cpd] Added CPD support for Dart - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1776](https://github.com/pmd/pmd/pull/1776): \[java] Show more detailed message when can't resolve field type - [Andrey Fomin](https://github.com/andrey-fomin)
*   [#1781](https://github.com/pmd/pmd/pull/1781): \[java] Location change in AssignmentToNonFinalStatic - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1789](https://github.com/pmd/pmd/pull/1789): \[cpd] \[core] Use current classloader instead of Thread's classloader - [Andreas Schmid](https://github.com/aaschmid)
*   [#1791](https://github.com/pmd/pmd/pull/1791): \[dart] \[cpd] Dart escaped string - [Maikel Steneker](https://github.com/maikelsteneker)

## 31-March-2019 - 6.13.0

The PMD team is pleased to announce PMD 6.13.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Call For Logo](#call-for-logo)
    * [Java 12 Support](#java-12-support)
    * [Quickstart Ruleset for Apex](#quickstart-ruleset-for-apex)
    * [PMD Designer](#pmd-designer)
    * [Improved Apex Support](#improved-apex-support)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Command Line Interface](#command-line-interface)
    * [Deprecated API](#deprecated-api)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Call For Logo

We are still searching for a new logo for PMD for the next major release.

Learn more about how to participate on [github issue 1663](https://github.com/pmd/pmd/issues/1663).

#### Java 12 Support

This release of PMD brings support for Java 12. PMD can parse the new [Switch Expressions](http://openjdk.java.net/jeps/325)
and resolve the type of such an expression.

Note: The Switch Expressions are a preview language feature of OpenJDK 12 and are not enabled by default. In order to
analyze a project with PMD that uses these language features, you'll need to enable it via the new environment
variable `PMD_JAVA_OPTS`:

    export PMD_JAVA_OPTS=--enable-preview
    ./run.sh pmd ...

#### Quickstart Ruleset for Apex

PMD provides now a quickstart ruleset for Salesforce.com Apex, which you can use as a base ruleset to
get your custom ruleset started. You can reference it with `rulesets/apex/quickstart.xml`.
You are strongly encouraged to [create your own ruleset](https://pmd.github.io/pmd-6.12.0/pmd_userdocs_making_rulesets.html)
though.

The quickstart ruleset has the intention, to be useful out-of-the-box for many projects. Therefore it
references only rules, that are most likely to apply everywhere.

Any feedback would be greatly appreciated.

#### PMD Designer

The rule designer's codebase has been moved out of the main repository and
will be developed at [pmd/pmd-designer](https://github.com/pmd/pmd-designer)
from now on. The maven coordinates will stay the same for the time being.
The designer will still be shipped with PMD's binaries.

#### Improved Apex Support

*   Many AST nodes now expose more information which makes it easier to write XPath-based rules for Apex. Here are
    some examples:
    *   `Annotation[@Resolved = false()]` finds unsupported annotations.
    *   `AnnotationParameter[@Name='RestResource'][@Value='/myurl']` gives access to
        annotation parameters.
    *   `CatchBlockStatement[@ExceptionType='Exception'][@VariableName='e']` finds catch
        block for specific exception types.
    *   `Field[@Type='String']` find all String fields, `Field[string-length(@Name) < 5]`
        finds all fields with short names and `Field[@Value='a']` find alls fields, that are
        initialized with a specific value.
    *   `LiteralExpression[@String = true()]` finds all String literals. There are attributes
        for each type: `@Boolean`, `@Integer`, `@Double`, `@Long`, `@Decimal`, `@Null`.
    *   `Method[@Constructor = true()]` selects all constructors. `Method[@ReturnType = 'String']`
        selects all methods that return a String.
    *   The `ModifierNode` node has a couple of attributes to check for the existence of specific
        modifiers: `@Test`, `@TestOrTestSetup`, `@WithSharing`, `@WithoutSharing`, `@InheritedSharing`,
        `@WebService`, `@Global`, `@Override`.
    *   Many nodes now expose their type. E.g. with `Parameter[@Type='Integer']` you can find all
        method parameters of type Integer. The same attribute `Type` exists as well for:
        `NewObjectExpression`, `Property`, `VariableDeclaration`.
    *   `VariableExpression[@Image='i']` finds all variable usages of the variable "i".

#### New Rules

*   The new Java rule [`AvoidUncheckedExceptionsInSignatures`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_design.html#avoiduncheckedexceptionsinsignatures) (`java-design`) finds methods or constructors
    that declare unchecked exceptions in their `throws` clause. This forces the caller to handle the exception,
    even though it is a runtime exception.

*   The new Java rule [`DetachedTestCase`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_errorprone.html#detachedtestcase) (`java-errorprone`) searches for public
    methods in test classes, which are not annotated with `@Test`. These methods might be test cases where
    the annotation has been forgotten. Because of that those test cases are never executed.

*   The new Java rule [`WhileLoopWithLiteralBoolean`](https://pmd.github.io/pmd-6.13.0/pmd_rules_java_bestpractices.html#whileloopwithliteralboolean) (`java-bestpractices`) finds
    Do-While-Loops and While-Loops that can be simplified since they use simply `true` or `false` as their
    loop condition.

*   The new Apex rule [`ApexAssertionsShouldIncludeMessage`](https://pmd.github.io/pmd-6.13.0/pmd_rules_apex_bestpractices.html#apexassertionsshouldincludemessage) (`apex-bestpractices`)
    searches for assertions in unit tests and checks, whether they use a message argument.

*   The new Apex rule [`ApexUnitTestMethodShouldHaveIsTestAnnotation`](https://pmd.github.io/pmd-6.13.0/pmd_rules_apex_bestpractices.html#apexunittestmethodshouldhaveistestannotation) (`apex-bestpractices`)
    searches for methods in test classes, which are missing the `@IsTest` annotation.

*   The new PLSQL rule [`AvoidTabCharacter`](https://pmd.github.io/pmd-6.13.0/pmd_rules_plsql_codestyle.html#avoidtabcharacter) (`plsql-codestyle`) checks, that there are
    no tab characters ("\t") in the source file.

*   The new PLSQL rule [`LineLength`](https://pmd.github.io/pmd-6.13.0/pmd_rules_plsql_codestyle.html#linelength) (`plsql-codestyle`) helps to enforce a maximum
    line length.

### Fixed Issues

*   doc
    *   [#1721](https://github.com/pmd/pmd/issues/1721): \[doc] Documentation provides an invalid property configuration example
*   java
    *   [#1537](https://github.com/pmd/pmd/issues/1537): \[java] Java 12 support
*   java-bestpractices
    *   [#1701](https://github.com/pmd/pmd/issues/1701): \[java] UseTryWithResources does not handle multiple argument close methods
*   java-codestyle
    *   [#1527](https://github.com/pmd/pmd/issues/1527): \[java] UseUnderscoresInNumericLiterals false positive on floating point numbers
    *   [#1674](https://github.com/pmd/pmd/issues/1674): \[java] documentation of CommentDefaultAccessModifier is wrong
*   java-errorprone
    *   [#1570](https://github.com/pmd/pmd/issues/1570): \[java] AvoidDuplicateLiterals warning about deprecated separator property when not used
*   plsql
    *   [#1510](https://github.com/pmd/pmd/issues/1510): \[plsql] Support XMLTABLE functions
    *   [#1716](https://github.com/pmd/pmd/issues/1716): \[plsql] Support access to whole plsql code
    *   [#1731](https://github.com/pmd/pmd/issues/1731): \[pslql] ParseException when parsing ELSIF
    *   [#1733](https://github.com/pmd/pmd/issues/1733): \[plsql] % not supported in "TestSearch%notfound"
    *   [#1734](https://github.com/pmd/pmd/issues/1734): \[plsql] TooManyMethods false-negative
    *   [#1735](https://github.com/pmd/pmd/issues/1735): \[plsql] False-negatives for TO_DATE_TO_CHAR, TO_DATEWithoutDateFormat, TO_TIMESTAMPWithoutDateFormat

### API Changes

#### Command Line Interface

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

#### Deprecated API

*   [`CodeClimateRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.13.0/net/sourceforge/pmd/renderers/CodeClimateRule.html#) is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

### External Contributions

*   [#1694](https://github.com/pmd/pmd/pull/1694): \[apex] New rules for test method and assert statements - [triandicAnt](https://github.com/triandicAnt)
*   [#1697](https://github.com/pmd/pmd/pull/1697): \[doc] Update CPD documentation - [Matías Fraga](https://github.com/matifraga)
*   [#1704](https://github.com/pmd/pmd/pull/1704): \[java] Added AvoidUncheckedExceptionsInSignatures Rule - [Bhanu Prakash Pamidi](https://github.com/pamidi99)
*   [#1706](https://github.com/pmd/pmd/pull/1706): \[java] Add DetachedTestCase rule - [David Burström](https://github.com/davidburstromspotify)
*   [#1709](https://github.com/pmd/pmd/pull/1709): \[java] Detect while loops with literal booleans conditions - [David Burström](https://github.com/davidburstromspotify)
*   [#1717](https://github.com/pmd/pmd/pull/1717): \[java] Fix false positive in useTryWithResources when using a custom close method with multiple arguments - [Rishabh Jain](https://github.com/jainrish)
*   [#1724](https://github.com/pmd/pmd/pull/1724): \[doc] Correct property override example - [Felix W. Dekker](https://github.com/FWDekker)
*   [#1737](https://github.com/pmd/pmd/pull/1737): \[java] fix escaping of CommentDefaultAccessModifier documentation - [itaigilo](https://github.com/itaigilo)

## 24-February-2019 - 6.12.0

The PMD team is pleased to announce PMD 6.12.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Call For Logo](#call-for-logo)
    * [CPD Suppression for Antlr-based languages](#cpd-suppression-for-antlr-based-languages)
    * [PL/SQL Grammar improvements](#pl-sql-grammar-improvements)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Call For Logo

PMD’s logo was great for a long time. But now we want to take the opportunity with the next major release to change
our logo in order to use a more "politically correct" one.

Learn more about how to participate on [github issue 1663](https://github.com/pmd/pmd/issues/1663).

#### CPD Suppression for Antlr-based languages

[ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini)
keep working on bringing full Antlr support to PMD. For this release, they have implemented
token filtering in an equivalent way as we did for JavaCC languages, adding support for CPD
suppressions through `CPD-OFF` and `CPD-ON` comments for all Antlr-based languages.

This means, you can now ignore arbitrary blocks of code on:
* Go
* Kotlin
* Swift

Simply start the suppression with any comment (single or multiline) containing `CPD-OFF`,
and resume again with a comment containing `CPD-ON`.

More information is available in [the user documentation](pmd_userdocs_cpd.html#suppression).

#### PL/SQL Grammar improvements

*   In this release, many more parser bugs in our PL/SQL support have been fixed. This adds more complete
    support for UPDATE statements and subqueries and hierarchical queries in SELECT statements.
*   Support for analytic functions such as LISTAGG has been added.
*   Conditions in WHERE clauses support now REGEX_LIKE and multiset conditions.

#### New Rules

*   The new Java rule [`UseTryWithResources`](https://pmd.github.io/pmd-6.12.0/pmd_rules_java_bestpractices.html#usetrywithresources) (`java-bestpractices`) searches
    for try-blocks, that could be changed to a try-with-resources statement. This statement ensures that
    each resource is closed at the end of the statement and is available since Java 7.

#### Modified Rules

*   The Apex rule [`MethodNamingConventions`](https://pmd.github.io/pmd-6.12.0/pmd_rules_apex_codestyle.html#methodnamingconventions) (`apex-codestyle`) has a new
    property `skipTestMethodUnderscores`, which is by default disabled. The new property allows for ignoring
    all test methods, either using the `testMethod` modifier or simply annotating them `@isTest`.

### Fixed Issues

*   all
    *   [#1462](https://github.com/pmd/pmd/issues/1462): \[core] Failed build on Windows with source zip archive
    *   [#1559](https://github.com/pmd/pmd/issues/1559): \[core] CPD: Lexical error in file (no file name provided)
    *   [#1671](https://github.com/pmd/pmd/issues/1671): \[doc] Wrong escaping in suppressing warnings for nopmd-comment
    *   [#1693](https://github.com/pmd/pmd/pull/1693): \[ui] Improved error reporting for the designer
*   java-bestpractices
    *   [#808](https://github.com/pmd/pmd/issues/808): \[java] AccessorMethodGeneration false positives with compile time constants
    *   [#1405](https://github.com/pmd/pmd/issues/1405): \[java] New Rule: UseTryWithResources - Replace close and IOUtils.closeQuietly with try-with-resources
    *   [#1555](https://github.com/pmd/pmd/issues/1555): \[java] UnusedImports false positive for method parameter type in @see Javadoc
*   java-codestyle
    *   [#1543](https://github.com/pmd/pmd/issues/1543): \[java] LinguisticNaming should ignore overriden methods
    *   [#1547](https://github.com/pmd/pmd/issues/1547): \[java] AtLeastOneConstructorRule: false-positive with lombok.AllArgsConstructor
    *   [#1624](https://github.com/pmd/pmd/issues/1624): \[java] UseDiamondOperator false positive with var initializer
*   java-design
    *   [#1641](https://github.com/pmd/pmd/issues/1641): \[java] False-positive with Lombok and inner classes
*   java-errorprone
    *   [#780](https://github.com/pmd/pmd/issues/780): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors
*   java-multithreading
    *   [#1633](https://github.com/pmd/pmd/issues/1633): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat
*   java-performance
    *   [#1632](https://github.com/pmd/pmd/issues/1632): \[java] ConsecutiveLiteralAppends false positive over catch
*   plsql
    *   [#1587](https://github.com/pmd/pmd/issues/1587): \[plsql] ParseException with EXISTS
    *   [#1589](https://github.com/pmd/pmd/issues/1589): \[plsql] ParseException with subqueries in WHERE clause
    *   [#1590](https://github.com/pmd/pmd/issues/1590): \[plsql] ParseException when using hierarchical query clause
    *   [#1656](https://github.com/pmd/pmd/issues/1656): \[plsql] ParseException with analytic functions, trim and subqueries
*   designer
    *   [#1679](https://github.com/pmd/pmd/issues/1679): \[ui] No default language version selected

### API Changes

No changes.

### External Contributions

*   [#1623](https://github.com/pmd/pmd/pull/1623): \[java] Fix lombok.AllArgsConstructor support - [Bobby Wertman](https://github.com/CasualSuperman)
*   [#1625](https://github.com/pmd/pmd/pull/1625): \[java] UnusedImports false positive for method parameter type in @see Javadoc - [Shubham](https://github.com/Shubham-2k17)
*   [#1628](https://github.com/pmd/pmd/pull/1628): \[java] LinguisticNaming should ignore overriden methods - [Shubham](https://github.com/Shubham-2k17)
*   [#1634](https://github.com/pmd/pmd/pull/1634): \[java] BeanMembersShouldSerializeRule does not recognize lombok accessors - [Shubham](https://github.com/Shubham-2k17)
*   [#1635](https://github.com/pmd/pmd/pull/1635): \[java] UnsynchronizedStaticFormatter reports commons lang FastDateFormat - [Shubham](https://github.com/Shubham-2k17)
*   [#1637](https://github.com/pmd/pmd/pull/1637): \[java] Compile time constants initialized by literals avoided by AccessorMethodGenerationRule - [Shubham](https://github.com/Shubham-2k17)
*   [#1640](https://github.com/pmd/pmd/pull/1640): \[java] Update instead of override classHasLombokAnnotation flag - [Phokham Nonava](https://github.com/fluxroot)
*   [#1644](https://github.com/pmd/pmd/pull/1644): \[apex] Add property to allow apex test methods to contain underscores - [Tom](https://github.com/tomdaly)
*   [#1645](https://github.com/pmd/pmd/pull/1645): \[java] ConsecutiveLiteralAppends false positive - [Shubham](https://github.com/Shubham-2k17)
*   [#1646](https://github.com/pmd/pmd/pull/1646): \[java] UseDiamondOperator doesn't work with var - [Shubham](https://github.com/Shubham-2k17)
*   [#1654](https://github.com/pmd/pmd/pull/1654): \[core] Antlr token filter - [Tomi De Lucca](https://github.com/tomidelucca)
*   [#1655](https://github.com/pmd/pmd/pull/1655): \[kotlin] Kotlin tokenizer refactor - [Lucas Soncini](https://github.com/lsoncini)
*   [#1686](https://github.com/pmd/pmd/pull/1686): \[doc] Replaced wrong escaping with ">" - [Himanshu Pandey](https://github.com/hpandeycodeit)

## 27-January-2019 - 6.11.0

The PMD team is pleased to announce PMD 6.11.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Updated Apex Support](#updated-apex-support)
    * [PL/SQL Grammar improvements](#pl/sql-grammar-improvements)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Updated Apex Support

*   The Apex language support has been bumped to version 45 (Spring '19). All new language features are now properly
    parsed and processed.
*   Many nodes now expose more informations, such as the operator for BooleanExpressions. This makes these operators
    consumable by XPath rules, e.g. `//BooleanExpression[@Operator='&&']`.

#### PL/SQL Grammar improvements

*   In this release, many parser bugs in our PL/SQL support have been fixed. This adds e.g. support for
    table collection expressions (`SELECT * FROM TABLE(expr)`).
*   Support for parsing insert statements has been added.
*   More improvements are planned for the next release of PMD.

#### New Rules

*   The new Java rule [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter) (`java-multithreading`) detects
    unsynchronized usages of static `java.text.Format` instances. This rule is a more generic replacement of the
    rule [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) which focused just on `DateFormat`.

*   The new Java rule [`ForLoopVariableCount`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_bestpractices.html#forloopvariablecount) (`java-bestpractices`) checks for
    the number of control variables in a for-loop. Having a lot of control variables makes it harder to understand
    what the loop does. The maximum allowed number of variables is by default 1 and can be configured by a
    property.

*   The new Java rule [`AvoidReassigningLoopVariables`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_bestpractices.html#avoidreassigningloopvariables) (`java-bestpractices`) searches
    for loop variables that are reassigned. Changing the loop variables additionally to the loop itself can lead to
    hard-to-find bugs.

*   The new Java rule [`UseDiamondOperator`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_codestyle.html#usediamondoperator) (`java-codestyle`) looks for constructor
    calls with explicit type parameters. Since Java 1.7, these type parameters are not necessary anymore, as they
    can be inferred now.

#### Modified Rules

*   The Java rule [`LocalVariableCouldBeFinal`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_codestyle.html#localvariablecouldbefinal) (`java-codestyle`) has a new
    property `ignoreForEachDecl`, which is by default disabled. The new property allows for ignoring
    non-final loop variables in a for-each statement.

#### Deprecated Rules

*   The Java rule [`UnsynchronizedStaticDateFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticdateformatter) has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general
    [`UnsynchronizedStaticFormatter`](https://pmd.github.io/pmd-6.11.0/pmd_rules_java_multithreading.html#unsynchronizedstaticformatter).

### Fixed Issues

*   core
    *   [#1196](https://github.com/pmd/pmd/issues/1196): \[core] CPD results not consistent between runs
    *   [#1496](https://github.com/pmd/pmd/issues/1496) \[core] Refactor metrics to be dealt with generically from pmd-core
*   apex
    *   [#1542](https://github.com/pmd/pmd/pull/1542): \[apex] Include the documentation category
    *   [#1546](https://github.com/pmd/pmd/issues/1546): \[apex] PMD parsing exception for Apex classes using 'inherited sharing' keyword
    *   [#1568](https://github.com/pmd/pmd/pull/1568): \[apex] AST node attribute @Image not usable / always null in XPath rule / Designer
*   java
    *   [#1556](https://github.com/pmd/pmd/issues/1556): \[java] Default methods should not be considered abstract
    *   [#1578](https://github.com/pmd/pmd/issues/1578): \[java] Private field is detected as public inside nested classes in interfaces
*   java-bestpractices
    *   [#658](https://github.com/pmd/pmd/issues/658): \[java] OneDeclarationPerLine: False positive for loops
    *   [#1518](https://github.com/pmd/pmd/issues/1518): \[java] New rule: AvoidReassigningLoopVariable
    *   [#1519](https://github.com/pmd/pmd/issues/1519): \[java] New rule: ForLoopVariableCount
*   java-codestyle
    *   [#1513](https://github.com/pmd/pmd/issues/1513): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop
    *   [#1517](https://github.com/pmd/pmd/issues/1517): \[java] New Rule: UseDiamondOperator
*   java-errorprone
    *   [#1035](https://github.com/pmd/pmd/issues/1035): \[java] ReturnFromFinallyBlock: False positive on lambda expression in finally block
    *   [#1549](https://github.com/pmd/pmd/issues/1549): \[java] NPE in PMD 6.8.0 InvalidSlf4jMessageFormat
*   java-multithreading
    *   [#1533](https://github.com/pmd/pmd/issues/1533): \[java] New rule: UnsynchronizedStaticFormatter
*   plsql
    *   [#1507](https://github.com/pmd/pmd/issues/1507): \[plsql] Parse Exception when using '||' operator in where clause
    *   [#1508](https://github.com/pmd/pmd/issues/1508): \[plsql] Parse Exception when using SELECT COUNT(\*)
    *   [#1509](https://github.com/pmd/pmd/issues/1509): \[plsql] Parse Exception with OUTER/INNER Joins
    *   [#1511](https://github.com/pmd/pmd/issues/1511): \[plsql] Parse Exception with IS NOT NULL
    *   [#1526](https://github.com/pmd/pmd/issues/1526): \[plsql] ParseException when using TableCollectionExpression
    *   [#1583](https://github.com/pmd/pmd/issues/1583): \[plsql] Update Set Clause should allow multiple columns
    *   [#1586](https://github.com/pmd/pmd/issues/1586): \[plsql] Parse Exception when functions are used with LIKE
    *   [#1588](https://github.com/pmd/pmd/issues/1588): \[plsql] Parse Exception with function calls in WHERE clause

### API Changes

* [`StatisticalRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/lang/rule/stat/StatisticalRule.html#) and the related helper classes and base rule classes
  are deprecated for removal in 7.0.0. This includes all of [`net.sourceforge.pmd.stat`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/stat/package-summary.html#) and [`net.sourceforge.pmd.lang.rule.stat`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/lang/rule/stat/package-summary.html#),
  and also [`AbstractStatisticalJavaRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.11.0/net/sourceforge/pmd/lang/java/rule/AbstractStatisticalJavaRule.html#), [`AbstractStatisticalApexRule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-apex/6.11.0/net/sourceforge/pmd/lang/apex/rule/AbstractStatisticalApexRule.html#) and the like.
  The methods [`Report#addMetric`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/Report.html#addMetric(net.sourceforge.pmd.stat.Metric)) and [`metricAdded`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/ThreadSafeReportListener.html#metricAdded(net.sourceforge.pmd.stat.Metric))
  will also be removed.
* [`setProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/properties/PropertySource.html#setProperty(net.sourceforge.pmd.properties.MultiValuePropertyDescriptor,Object[])) is deprecated,
  because [`MultiValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.11.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#) is deprecated as well

### External Contributions

*   [#1503](https://github.com/pmd/pmd/pull/1503): \[java] Fix for ReturnFromFinallyBlock false-positives - [RishabhDeep Singh](https://github.com/rishabhdeepsingh)
*   [#1514](https://github.com/pmd/pmd/pull/1514): \[java] LocalVariableCouldBeFinal: allow excluding the variable in a for-each loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1516](https://github.com/pmd/pmd/pull/1516): \[java] OneDeclarationPerLine: Don't report multiple variables in a for statement. - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1520](https://github.com/pmd/pmd/pull/1520): \[java] New rule: ForLoopVariableCount: check the number of control variables in a for loop - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1521](https://github.com/pmd/pmd/pull/1521): \[java] Upgrade to ASM7 for JDK 11 support - [Mark Pritchard](https://github.com/markpritchard)
*   [#1530](https://github.com/pmd/pmd/pull/1530): \[java] New rule: AvoidReassigningLoopVariables - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1534](https://github.com/pmd/pmd/pull/1534): \[java] This is the change regarding the usediamondoperator #1517 - [hemanshu070](https://github.com/hemanshu070)
*   [#1545](https://github.com/pmd/pmd/pull/1545): \[doc] fixing dead links + tool to check for dead links automatically - [Kris Scheibe](https://github.com/kris-scheibe)
*   [#1551](https://github.com/pmd/pmd/pull/1551): \[java] InvalidSlf4jMessageFormatRule should not throw NPE for enums - [Robbie Martinus](https://github.com/rmartinus)
*   [#1552](https://github.com/pmd/pmd/pull/1552): \[core] Upgrading Google Gson from 2.5 to 2.8.5 - [Thunderforge](https://github.com/Thunderforge)
*   [#1553](https://github.com/pmd/pmd/pull/1553): \[core] Upgrading System Rules dependency from 1.8.0 to 1.19.0 - [Thunderforge](https://github.com/Thunderforge)
*   [#1554](https://github.com/pmd/pmd/pull/1554): \[plsql] updates should allow for multiple statements - [tashiscool](https://github.com/tashiscool)
*   [#1584](https://github.com/pmd/pmd/pull/1584): \[core] Fixes 1196: inconsistencies of clones returned by different CPD executions for the same files  - [Bruno Ferreira](https://github.com/bmbferreira)

## 09-December-2018 - 6.10.0

The PMD team is pleased to announce PMD 6.10.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Kotlin support for CPD](#kotlin-support-for-cpd)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
    * [Properties framework](#properties-framework)
        * [Changes to how you define properties](#changes-to-how-you-define-properties)
        * [Architectural simplifications](#architectural-simplifications)
        * [Changes to the PropertyDescriptor interface](#changes-to-the-propertydescriptor-interface)
    * [Deprecated APIs](#deprecated-apis)
        * [For internalization](#for-internalization)
        * [For removal](#for-removal)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Kotlin support for CPD

Thanks to [Maikel Steneker](https://github.com/maikelsteneker), CPD now supports [Kotlin](https://kotlinlang.org/).
This means, you can use CPD to find duplicated code in your Kotlin projects.

#### New Rules

*   The new Java rule [`UseUnderscoresInNumericLiterals`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_codestyle.html#useunderscoresinnumericliterals) (`java-codestyle`)
    verifies that numeric literals over a given length (4 chars by default, but configurable) are using
    underscores every 3 digits for readability. The rule only applies to Java 7+ codebases.

#### Modified Rules

*   The Java rule [`JUnitTestsShouldIncludeAssert`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_bestpractices.html#junittestsshouldincludeassert) (`java-bestpractices`)
    now also detects [Soft Assertions](https://github.com/joel-costigliola/assertj-core).

*   The property `exceptionfile` of the rule [`AvoidDuplicateLiterals`](https://pmd.github.io/pmd-6.10.0/pmd_rules_java_errorprone.html#avoidduplicateliterals) (`java-errorprone`)
    has been deprecated and will be removed with 7.0.0. Please use `exceptionList` instead.

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1328](https://github.com/pmd/pmd/issues/1328): \[ci] Building docs for release fails
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
    *   [#1468](https://github.com/pmd/pmd/issues/1468): \[doc] Missing escaping leads to XSS
    *   [#1471](https://github.com/pmd/pmd/issues/1471): \[core] XMLRenderer: ProcessingErrors from exceptions without a message missing
    *   [#1477](https://github.com/pmd/pmd/issues/1477): \[core] Analysis cache fails with wildcard classpath entries
*   java
    *   [#1460](https://github.com/pmd/pmd/issues/1460): \[java] Intermittent PMD failure : PMD processing errors while no violations reported
*   java-bestpractices
    *   [#647](https://github.com/pmd/pmd/issues/647): \[java] JUnitTestsShouldIncludeAssertRule should support `this.exception` as well as just `exception`
    *   [#1435](https://github.com/pmd/pmd/issues/1435): \[java] JUnitTestsShouldIncludeAssert: Support AssertJ soft assertions
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis
    *   [#1440](https://github.com/pmd/pmd/issues/1440): \[java] CommentDefaultAccessModifierRule shows incorrect message
*   java-design
    *   [#1151](https://github.com/pmd/pmd/issues/1151): \[java] ImmutableField false positive with multiple constructors
    *   [#1483](https://github.com/pmd/pmd/issues/1483): \[java] Cyclo metric should count conditions of for statements correctly
*   java-errorprone
    *   [#1512](https://github.com/pmd/pmd/issues/1512): \[java] InvalidSlf4jMessageFormatRule causes NPE in lambda and static blocks
*   plsql
    *   [#1454](https://github.com/pmd/pmd/issues/1454): \[plsql] ParseException for IF/CASE statement with >=, <=, !=


### API Changes

#### Properties framework





The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

##### Changes to how you define properties


* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
  construction through builders. The builder hierarchy, currently found in the package [`net.sourceforge.pmd.properties.builders`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/package-summary.html#),
  is being replaced by the simpler [`PropertyBuilder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyBuilder.html#). Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like [`IntegerProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerProperty.html#) and [`StringMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringMultiProperty.html#) will gradually
  all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the [`PropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#)
  interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class [`PropertyFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#) will become
  from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
  by a corresponding method on `PropertyFactory`:
    * [`IntegerProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerProperty.html#) is replaced by [`PropertyFactory#intProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#intProperty(java.lang.String))
        * [`IntegerMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/IntegerMultiProperty.html#) is replaced by [`PropertyFactory#intListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#intListProperty(java.lang.String))

    * [`FloatProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FloatProperty.html#) and [`DoubleProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/DoubleProperty.html#) are both replaced by [`PropertyFactory#doubleProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleProperty(java.lang.String)).
      Having a separate property for floats wasn't that useful.
        * Similarly, [`FloatMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FloatMultiProperty.html#) and [`DoubleMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/DoubleMultiProperty.html#) are replaced by [`PropertyFactory#doubleListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#doubleListProperty(java.lang.String)).

    * [`StringProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringProperty.html#) is replaced by [`PropertyFactory#stringProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringProperty(java.lang.String))
        * [`StringMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/StringMultiProperty.html#) is replaced by [`PropertyFactory#stringListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#stringListProperty(java.lang.String))

    * [`RegexProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/RegexProperty.html#) is replaced by [`PropertyFactory#regexProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#regexProperty(java.lang.String))

    * [`EnumeratedProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#) is replaced by [`PropertyFactory#enumProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumProperty(java.lang.String,java.util.Map))
        * [`EnumeratedProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedProperty.html#) is replaced by [`PropertyFactory#enumListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#enumListProperty(java.lang.String,java.util.Map))

    * [`BooleanProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/BooleanProperty.html#) is replaced by [`PropertyFactory#booleanProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#booleanProperty(java.lang.String))
        * Its multi-valued counterpart, [`BooleanMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/BooleanMultiProperty.html#), is not replaced, because it doesn't have a use case.

    * [`CharacterProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/CharacterProperty.html#) is replaced by [`PropertyFactory#charProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#charProperty(java.lang.String))
        * [`CharacterMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/CharacterMultiProperty.html#) is replaced by [`PropertyFactory#charListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#charListProperty(java.lang.String))

    * [`LongProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/LongProperty.html#) is replaced by [`PropertyFactory#longIntProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntProperty(java.lang.String))
        * [`LongMultiProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/LongMultiProperty.html#) is replaced by [`PropertyFactory#longIntListProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyFactory.html#longIntListProperty(java.lang.String))

    * [`MethodProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/MethodProperty.html#), [`FileProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/FileProperty.html#), [`TypeProperty`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/TypeProperty.html#) and their multi-valued counterparts
      are discontinued for lack of a use-case, and have no planned replacement in 7.0.0 for now.
      <!-- TODO complete that as we proceed. -->


Here's an example:
```java
// Before 7.0.0, these are equivalent:
IntegerProperty myProperty = new IntegerProperty("score", "Top score value", 1, 100, 40, 3.0f);
IntegerProperty myProperty = IntegerProperty.named("score").desc("Top score value").range(1, 100).defaultValue(40).uiOrder(3.0f);

// They both map to the following in 7.0.0
PropertyDescriptor<Integer> myProperty = PropertyFactory.intProperty("score").desc("Top score value").require(inRange(1, 100)).defaultValue(40);
```

You're highly encouraged to migrate to using this new API as soon as possible, to ease your migration to 7.0.0.



##### Architectural simplifications

* [`EnumeratedPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/EnumeratedPropertyDescriptor.html#), [`NumericPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/NumericPropertyDescriptor.html#), [`PackagedPropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PackagedPropertyDescriptor.html#),
  and the related builders (in [`net.sourceforge.pmd.properties.builders`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/package-summary.html#)) will be removed.
  These specialized interfaces allowed additional constraints to be enforced on the
  value of a property, but made the property class hierarchy very large and impractical
  to maintain. Their functionality will be mapped uniformly to [`PropertyConstraint`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/constraints/PropertyConstraint.html#)s,
  which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
  related methods [`PropertyTypeId#isPropertyNumeric`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyNumeric()) and
  [`PropertyTypeId#isPropertyPackaged`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#isPropertyPackaged()) are also deprecated.

* [`MultiValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/MultiValuePropertyDescriptor.html#) and [`SingleValuePropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/SingleValuePropertyDescriptor.html#)
  are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
  between single- and multi-valued properties. The method [`PropertyDescriptor#isMultiValue`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()) will be removed
  accordingly.

##### Changes to the PropertyDescriptor interface

* [`preferredRowCount`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#preferredRowCount()) is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods [`uiOrder`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#uiOrder()) and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method [`propertyErrorFor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#propertyErrorFor(net.sourceforge.pmd.Rule)) is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `[`valueFrom(String)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#valueFrom(java.lang.String)) and `String `[`asDelimitedString`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#asDelimitedString(java.lang.Object))`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* [`isMultiValue`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isMultiValue()) and [`type`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#type()) are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* [`errorFor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#errorFor(java.lang.Object)) is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

#### Deprecated APIs








##### For internalization

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package [`net.sourceforge.pmd.lang.ast.xpath`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/ast/xpath/package-summary.html#))
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only [`Attribute`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/ast/xpath/Attribute.html#) remains public API.

*   The classes [`PropertyDescriptorField`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptorField.html#), [`PropertyDescriptorBuilderConversionWrapper`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/builders/PropertyDescriptorBuilderConversionWrapper.html#), and the methods
    [`PropertyDescriptor#attributeValuesById`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#attributeValuesById), [`PropertyDescriptor#isDefinedExternally`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html#isDefinedExternally()) and [`PropertyTypeId#getFactory`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyTypeId.html#getFactory()).
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class [`ValueParserConstants`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/ValueParserConstants.html#) and the interface [`ValueParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/ValueParser.html#).

*   All classes from [`net.sourceforge.pmd.lang.java.metrics.impl.visitors`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/metrics/impl/visitors/package-summary.html#) are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    [`JavaParserVisitorAdapter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorAdapter.html#) should be directly subclassed.

*   [`LanguageVersionHandler#getDataFlowHandler()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowHandler()), [`LanguageVersionHandler#getDFAGraphRule()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDFAGraphRule())

*   [`VisitorStarter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/VisitorStarter.html#)

##### For removal

*   All classes from [`net.sourceforge.pmd.properties.modules`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/modules/package-summary.html#) will be removed.

*   The interface [`Dimensionable`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/Dimensionable.html#) has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from [`ASTLocalVariableDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTLocalVariableDeclaration.html#) and [`ASTFieldDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#) have
    also been deprecated:

    *   [`ASTFieldDeclaration`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#) won't be a [`TypeNode`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/TypeNode.html#) come 7.0.0, so
        [`getType`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getType()) and
        [`getTypeDefinition`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTFieldDeclaration.html#getTypeDefinition()) are deprecated.

    *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`[`ASTVariableDeclaratorId`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/ASTVariableDeclaratorId.html#)`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

    *   In [`net.sourceforge.pmd.lang.java.ast`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/package-summary.html#): [`JavaParserDecoratedVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserDecoratedVisitor.html#), [`JavaParserControllessVisitor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitor.html#),
        [`JavaParserControllessVisitorAdapter`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserControllessVisitorAdapter.html#), and [`JavaParserVisitorDecorator`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.10.0/net/sourceforge/pmd/lang/java/ast/JavaParserVisitorDecorator.html#) are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

    *   [`CppHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppHandler.html#)
    *   [`CppLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppLanguageModule.html#)
    *   [`CppParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cpp/6.10.0/net/sourceforge/pmd/lang/cpp/CppParser.html#)
    *   [`CsLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-cs/6.10.0/net/sourceforge/pmd/lang/cs/CsLanguageModule.html#)
    *   [`FortranLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-fortran/6.10.0/net/sourceforge/pmd/lang/fortran/FortranLanguageModule.html#)
    *   [`GroovyLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-groovy/6.10.0/net/sourceforge/pmd/lang/groovy/GroovyLanguageModule.html#)
    *   [`MatlabHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabHandler.html#)
    *   [`MatlabLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabLanguageModule.html#)
    *   [`MatlabParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-matlab/6.10.0/net/sourceforge/pmd/lang/matlab/MatlabParser.html#)
    *   [`ObjectiveCHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCHandler.html#)
    *   [`ObjectiveCLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCLanguageModule.html#)
    *   [`ObjectiveCParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-objectivec/6.10.0/net/sourceforge/pmd/lang/objectivec/ObjectiveCParser.html#)
    *   [`PhpLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-php/6.10.0/net/sourceforge/pmd/lang/php/PhpLanguageModule.html#)
    *   [`PythonHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonHandler.html#)
    *   [`PythonLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonLanguageModule.html#)
    *   [`PythonParser`](https://javadoc.io/page/net.sourceforge.pmd/pmd-python/6.10.0/net/sourceforge/pmd/lang/python/PythonParser.html#)
    *   [`RubyLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-ruby/6.10.0/net/sourceforge/pmd/lang/ruby/RubyLanguageModule.html#)
    *   [`ScalaLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-scala/6.10.0/net/sourceforge/pmd/lang/scala/ScalaLanguageModule.html#)
    *   [`SwiftLanguageModule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-swift/6.10.0/net/sourceforge/pmd/lang/swift/SwiftLanguageModule.html#)


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
  in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
  found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
  removal:
    * In [`Rule`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#): [`isDfa()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isDfa()), [`isTypeResolution()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isTypeResolution()), [`isMultifile()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/Rule.html#isMultifile()) and their
      respective setters.
    * In [`RuleSet`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#): [`usesDFA(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesDFA(net.sourceforge.pmd.lang.Language)), [`usesTypeResolution(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)), [`usesMultifile(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSet.html#usesMultifile(net.sourceforge.pmd.lang.Language))
    * In [`RuleSets`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#): [`usesDFA(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesDFA(net.sourceforge.pmd.lang.Language)), [`usesTypeResolution(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesTypeResolution(net.sourceforge.pmd.lang.Language)), [`usesMultifile(Language)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/RuleSets.html#usesMultifile(net.sourceforge.pmd.lang.Language))
    * In [`LanguageVersionHandler`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#): [`getDataFlowFacade()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getDataFlowFacade()), [`getSymbolFacade()`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade()), [`getSymbolFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getSymbolFacade(java.lang.ClassLoader)),
      [`getTypeResolutionFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getTypeResolutionFacade(java.lang.ClassLoader)), [`getQualifiedNameResolutionFacade(ClassLoader)`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/lang/LanguageVersionHandler.html#getQualifiedNameResolutionFacade(java.lang.ClassLoader))

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)
*   [#1434](https://github.com/pmd/pmd/pull/1434): \[java] JUnitTestsShouldIncludeAssert: Recognize AssertJ soft assertions as valid assert statements - [Loïc Ledoyen](https://github.com/ledoyen)
*   [#1439](https://github.com/pmd/pmd/pull/1439): \[java] Avoid FileInputStream and FileOutputStream - [reudismam](https://github.com/reudismam)
*   [#1441](https://github.com/pmd/pmd/pull/1441): \[kotlin] [cpd] Added CPD support for Kotlin - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1447](https://github.com/pmd/pmd/pull/1447): \[fortran] Use diamond operator in impl - [reudismam](https://github.com/reudismam)
*   [#1453](https://github.com/pmd/pmd/pull/1453): \[java] Adding the fix for #1440. Showing correct message for CommentDefaultAccessmodifier. - [Rohit Kumar](https://github.com/stationeros)
*   [#1457](https://github.com/pmd/pmd/pull/1457): \[java] Adding test for Issue #647 - [orimarko](https://github.com/orimarko)
*   [#1464](https://github.com/pmd/pmd/pull/1464): \[doc] Fix XSS on documentation web page - [Maxime Robert](https://github.com/marob)
*   [#1469](https://github.com/pmd/pmd/pull/1469): \[core] Configurable max loops in DAAPathFinder - [Alberto Fernández](https://github.com/albfernandez)
*   [#1494](https://github.com/pmd/pmd/pull/1494): \[java] 1151: Rephrase ImmutableField documentation in design.xml - [Robbie Martinus](https://github.com/rmartinus)
*   [#1504](https://github.com/pmd/pmd/pull/1504): \[java] NPE in InvalidSlf4jMessageFormatRule if a logger call with a variable as parameter is not inside a method or constructor - [kris-scheibe](https://github.com/kris-scheibe)

## 28-October-2018 - 6.9.0

The PMD team is pleased to announce PMD 6.9.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Improved Golang CPD Support](#improved-golang-cpd-support)
    * [New Rules](#new-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Improved Golang CPD Support

Thanks to the work of [ITBA](https://www.itba.edu.ar/) students [Matías Fraga](https://github.com/matifraga),
[Tomi De Lucca](https://github.com/tomidelucca) and [Lucas Soncini](https://github.com/lsoncini),
Golang is now backed by a proper Antlr Grammar. This means CPD is now better at detecting duplicates,
as comments are recognized as such and ignored.

#### New Rules

*   The new PLSQL rule [`CodeFormat`](https://pmd.github.io/pmd-6.9.0/pmd_rules_plsql_codestyle.html#codeformat) (`plsql-codestyle`) verifies that
    PLSQL code is properly formatted. It checks e.g. for correct indentation in select statements and verifies
    that each parameter is defined on a separate line.

### Fixed Issues

*   all
    *   [#649](https://github.com/pmd/pmd/issues/649): \[core] Exclude specific files from command line
    *   [#1272](https://github.com/pmd/pmd/issues/1272): \[core] Could not find or load main class when using symlinked run.sh
    *   [#1377](https://github.com/pmd/pmd/issues/1377): \[core] LanguageRegistry uses default class loader when invoking ServiceLocator
    *   [#1394](https://github.com/pmd/pmd/issues/1394): \[doc] How to configure "-cache <path>"
    *   [#1412](https://github.com/pmd/pmd/issues/1412): \[doc] Broken link to adding new cpd language documentation
*   apex
    *   [#1396](https://github.com/pmd/pmd/issues/1396): \[apex] ClassCastException caused by Javadoc
*   java
    *   [#1330](https://github.com/pmd/pmd/issues/1330): \[java] PMD crashes with java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/xml/ws/Service
*   java-bestpractices
    *   [#1202](https://github.com/pmd/pmd/issues/1202): \[java] GuardLogStatement: "There is log block not surrounded by if" doesn't sound right
    *   [#1209](https://github.com/pmd/pmd/issues/1209): \[java] UnusedImports false positive for static import with package-private method usage
    *   [#1343](https://github.com/pmd/pmd/issues/1343): \[java] Update CommentDefaultAccessModifierRule to extend AbstractIgnoredAnnotationRule
    *   [#1365](https://github.com/pmd/pmd/issues/1365): \[java] JUnitTestsShouldIncludeAssert false positive
    *   [#1404](https://github.com/pmd/pmd/issues/1404): \[java] UnusedImports false positive with static ondemand import with method call
*   java-codestyle
    *   [#1199](https://github.com/pmd/pmd/issues/1199): \[java] UnnecessaryFullyQualifiedName doesn't flag same package FQCNs
    *   [#1356](https://github.com/pmd/pmd/issues/1356): \[java] UnnecessaryModifier wrong message public-\>static
*   java-design
    *   [#1369](https://github.com/pmd/pmd/issues/1369): \[java] Processing error (ClassCastException) if a TYPE\_USE annotation is used on a base class in the "extends" clause
*   jsp
    *   [#1402](https://github.com/pmd/pmd/issues/1402): \[jsp] JspTokenManager has a problem about jsp scriptlet
*   documentation
    *   [#1349](https://github.com/pmd/pmd/pull/1349): \[doc] Provide some explanation for WHY duplicate code is bad, like mutations

### API Changes

*   PMD has a new CLI option `-ignorelist`. With that, you can provide a file containing a comma-delimit list of files,
    that should be excluded during analysis. The ignorelist is applied after the files have been selected
    via `-dir` or `-filelist`, which means, if the file is in both lists, then it will be ignored.
    Note: there is no corresponding option for the Ant task, since the feature is already available via
    Ant's FileSet include/exclude filters.

### External Contributions

*   [#1338](https://github.com/pmd/pmd/pull/1338): \[core] \[cpd] Generalize ANTLR tokens preparing support for ANTLR token filter - [Matías Fraga](https://github.com/matifraga) and [Tomi De Lucca](https://github.com/tomidelucca)
*   [#1361](https://github.com/pmd/pmd/pull/1361): \[doc] Update cpd.md with information about risks - [David M. Karr](https://github.com/davidmichaelkarr)
*   [#1366](https://github.com/pmd/pmd/pull/1366): \[java] Static Modifier on Internal Interface pmd #1356 - [avishvat](https://github.com/vishva007)
*   [#1368](https://github.com/pmd/pmd/pull/1368): \[doc] Updated outdated note in the building documentation. - [Maikel Steneker](https://github.com/maikelsteneker)
*   [#1374](https://github.com/pmd/pmd/pull/1374): \[java] Simplify check for 'Test' annotation in JUnitTestsShouldIncludeAssertRule. - [Will Winder](https://github.com/winder)
*   [#1375](https://github.com/pmd/pmd/pull/1375): \[java] Add missing null check AbstractJavaAnnotatableNode - [Will Winder](https://github.com/winder)
*   [#1376](https://github.com/pmd/pmd/pull/1376): \[all] Upgrading Apache Commons IO from 2.4 to 2.6 - [Thunderforge](https://github.com/Thunderforge)
*   [#1378](https://github.com/pmd/pmd/pull/1378): \[all] Upgrading Apache Commons Lang 3 from 3.7 to 3.8.1 - [Thunderforge](https://github.com/Thunderforge)
*   [#1382](https://github.com/pmd/pmd/pull/1382): \[all] Replacing deprecated IO methods with ones that specify a charset - [Thunderforge](https://github.com/Thunderforge)
*   [#1383](https://github.com/pmd/pmd/pull/1383): \[java] Improved message for GuardLogStatement rule - [Felix Lampe](https://github.com/fblampe)
*   [#1386](https://github.com/pmd/pmd/pull/1386): \[go] \[cpd] Add CPD support for Antlr based grammar on Golang - [Matías Fraga](https://github.com/matifraga)
*   [#1398](https://github.com/pmd/pmd/pull/1398): \[all] Upgrading SLF4J from 1.7.12 to 1.7.25 - [Thunderforge](https://github.com/Thunderforge)
*   [#1400](https://github.com/pmd/pmd/pull/1400): \[java] Fix Issue 1343: Update CommentDefaultAccessModifierRule - [CrazyUnderdog](https://github.com/CrazyUnderdog)
*   [#1401](https://github.com/pmd/pmd/pull/1401): \[all] Replacing IOUtils.closeQuietly(foo) with try-with-resources statements - [Thunderforge](https://github.com/Thunderforge)
*   [#1406](https://github.com/pmd/pmd/pull/1406): \[jsp] Fix issue 1402: JspTokenManager has a problem about jsp scriptlet - [JustPRV](https://github.com/JustPRV)
*   [#1411](https://github.com/pmd/pmd/pull/1411): \[core] Add ignore file path functionality - [Jon Moroney](https://github.com/darakian)
*   [#1414](https://github.com/pmd/pmd/pull/1414): \[doc] Fix broken link. Fixes #1412 - [Johan Hammar](https://github.com/johanhammar)


## 30-September-2018 - 6.8.0

The PMD team is pleased to announce PMD 6.8.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    * [Drawing a line between private and public API](#drawing-a-line-between-private-and-public-api)
      * [`.internal` packages and `@InternalApi` annotation](#`.internal`-packages-and-`@internalapi`-annotation)
      * [`@ReservedSubclassing`](#`@reservedsubclassing`)
      * [`@Experimental`](#`@experimental`)
      * [`@Deprecated`](#`@deprecated`)
      * [The transition](#the-transition)
    * [Quickstart Ruleset](#quickstart-ruleset)
    * [New Rules](#new-rules)
    * [Modified Rules](#modified-rules)
    * [PLSQL](#plsql)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Drawing a line between private and public API

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

###### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

###### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

###### `@Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
on them in any production code. They are purely to allow broad testing and feedback.

###### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.

###### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.


#### Quickstart Ruleset

PMD 6.8.0 provides a first quickstart ruleset for Java, which you can use as a base ruleset to get your
custom ruleset started. You can reference it with `rulesets/java/quickstart.xml`.
You are strongly encouraged to [create your own ruleset](https://pmd.github.io/pmd-6.7.0/pmd_userdocs_making_rulesets.html)
though.

The quickstart ruleset has the intention, to be useful out-of-the-box for many projects. Therefore it
references only rules, that are most likely to apply everywhere.

Any feedback would be greatly appreciated.


#### New Rules

*   The new Apex rule [`ApexDoc`](https://pmd.github.io/pmd-6.8.0/pmd_rules_apex_documentation.html#apexdoc) (`apex-documentation`)
    enforces the inclusion of ApexDoc on classes, interfaces, properties and methods; as well as some
    sanity rules for such docs (no missing parameters, parameters' order, and return value). By default,
    method overrides and test classes are allowed to not include ApexDoc.


#### Modified Rules

*   The rule [`MissingSerialVersionUID`](https://pmd.github.io/pmd-6.8.0/pmd_rules_java_errorprone.html#missingserialversionuid) (`java-errorprone`) has been modified
    in order to recognize also missing `serialVersionUID` fields in abstract classes, if they are serializable.
    Each individual class in the inheritance chain needs an own serialVersionUID field. See also [Should an abstract class have a serialVersionUID](https://stackoverflow.com/questions/893259/should-an-abstract-class-have-a-serialversionuid).
    This change might lead to additional violations in existing code bases.


#### PLSQL

The grammar for PLSQL has been revamped in order to fully parse `SELECT INTO`, `UPDATE`, and `DELETE`
statements. Previously such statements have been simply skipped ahead, now PMD is parsing them, giving access
to the individual parts of a SELECT-statement, such as the Where-Clause. This might produce new parsing errors
where PMD previously could successfully parse PLSQL code. If this happens, please report a new [issue](https://github.com/pmd/pmd/issues/new) to get this problem fixed.


### Fixed Issues

*   apex-bestpractices
    *   [#1348](https://github.com/pmd/pmd/issues/1348): \[apex] AvoidGlobalModifierRule gives warning even when its a webservice - false positive
*   java-codestyle
    *   [#1329](https://github.com/pmd/pmd/issues/1329): \[java] FieldNamingConventions: false positive in serializable class with serialVersionUID
    *   [#1334](https://github.com/pmd/pmd/issues/1334): \[java] LinguisticNaming should support AtomicBooleans
*   java-errorprone
    *   [#1350](https://github.com/pmd/pmd/issues/1350): \[java] MissingSerialVersionUID false-positive on interfaces
    *   [#1352](https://github.com/pmd/pmd/issues/1352): \[java] MissingSerialVersionUID false-negative with abstract classes
*   java-performance
    *   [#1325](https://github.com/pmd/pmd/issues/1325): \[java] False positive in ConsecutiveLiteralAppends
*   plsql
    *   [#1279](https://github.com/pmd/pmd/pull/1279): \[plsql] Support for SELECT INTO


### API Changes

*   A couple of methods and fields in `net.sourceforge.pmd.properties.AbstractPropertySource` have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in `net.sourceforge.pmd.properties.PropertySource` have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of `net.sourceforge.pmd.lang.rule.RuleReference` has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.


### External Contributions

*   [#1309](https://github.com/pmd/pmd/pull/1309): \[core] \[CPD] Decouple Antlr Tokenizer implementation from any CPD language supported with Antlr - [Matías Fraga](https://github.com/matifraga)
*   [#1314](https://github.com/pmd/pmd/pull/1314): \[apex] Add validation of ApexDoc comments - [Jeff Hube](https://github.com/jeffhube)
*   [#1339](https://github.com/pmd/pmd/pull/1339): \[ci] Improve danger message - [BBG](https://github.com/djydewang)
*   [#1340](https://github.com/pmd/pmd/pull/1340): \[java] Derive correct classname for non-public non-classes - [kris-scheibe](https://github.com/kris-scheibe)
*   [#1357](https://github.com/pmd/pmd/pull/1357): \[doc] Improve Codacy description - [Daniel Reigada](https://github.com/DReigada)


## 02-September-2018 - 6.7.0

The PMD team is pleased to announce PMD 6.7.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Modified Rules](#modified-rules)
    *   [New Rules](#new-rules)
    *   [Deprecated Rules](#deprecated-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Modified Rules

*   The Java rule {% rule java/bestpractices/OneDeclarationPerLine %} (`java-bestpractices`) has been revamped to
    consider not only local variable declarations, but field declarations too.

#### New Rules

*   The new Java rule {% rule java/codestyle/LinguisticNaming %} (`java-codestyle`)
    detects cases, when a method name indicates it returns a boolean (such as `isSmall()`) but it doesn't.
    Besides method names, the rule also checks field and variable names. It also checks, that getters return
    something but setters won't. The rule has several properties with which it can be customized.

*   The new PL/SQL rule {% rule plsql/codestyle/ForLoopNaming %} (`plsql-codestyle`)
    enforces a naming convention for "for loops". Both "cursor for loops" and "index for loops" are covered.
    The rule can be customized via patterns. By default, short variable names are reported.

*   The new Java rule {% rule java/codestyle/FieldNamingConventions %} (`java-codestyle`)
    detects field names that don't comply to a given convention. It defaults to standard Java convention of using camelCase,
    but can be configured with ease for e.g. constants or static fields.

*   The new Apex rule {% rule apex/codestyle/OneDeclarationPerLine %} (`apex-codestyle`) enforces declaring a
    single field / variable per line; or per statement if the `strictMode` property is set.
    It's an Apex equivalent of the already existing Java rule of the same name.

#### Deprecated Rules

*   The Java rules `VariableNamingConventions` (java-codestyle), `MIsLeadingVariableName` (java-codestyle),
    `SuspiciousConstantFieldName` (java-codestyle), and `AvoidPrefixingMethodParameters` (java-codestyle) are
    now deprecated, and will be removed with version 7.0.0. They are replaced by the more general
    {% rule java/codestyle/FieldNamingConventions %}, {% rule java/codestyle/FormalParameterNamingConventions %}, and
    {% rule java/codestyle/LocalVariableNamingConventions %}.

### Fixed Issues

*   core
    *   [#1191](https://github.com/pmd/pmd/issues/1191): \[core] Test Framework: Sort violations by line/column
    *   [#1283](https://github.com/pmd/pmd/issues/1283): \[core] Deprecate ReportTree
    *   [#1288](https://github.com/pmd/pmd/issues/1288): \[core] No supported build listeners found with Gradle
    *   [#1300](https://github.com/pmd/pmd/issues/1300): \[core] PMD stops processing file completely, if one rule in a rule chain fails
    *   [#1317](https://github.com/pmd/pmd/issues/1317): \[ci] Coveralls hasn't built the project since June 25th
*   java-bestpractices
    *   [#940](https://github.com/pmd/pmd/issues/940): \[java] JUnit 4 false positives for JUnit 5 tests
    *   [#1267](https://github.com/pmd/pmd/pull/1267): \[java] MissingOverrideRule: Avoid NoClassDefFoundError with incomplete classpath
    *   [#1323](https://github.com/pmd/pmd/issues/1323): \[java] AvoidUsingHardCodedIP ignores match pattern
    *   [#1327](https://github.com/pmd/pmd/pull/1327): \[java] AvoidUsingHardCodedIP false positive for ":bee"
*   java-codestyle
    *   [#1255](https://github.com/pmd/pmd/issues/1255): \[java] UnnecessaryFullyQualifiedName false positive: static method on shadowed implicitly imported class
    *   [#1258](https://github.com/pmd/pmd/issues/1285): \[java] False positive "UselessParentheses" for parentheses that contain assignment
*   java-errorprone
    *   [#1078](https://github.com/pmd/pmd/issues/1078): \[java] MissingSerialVersionUID rule does not seem to catch inherited classes
*   java-performance
    *   [#1291](https://github.com/pmd/pmd/issues/1291): \[java] InvalidSlf4jMessageFormat false positive: too many arguments with string concatenation operator
    *   [#1298](https://github.com/pmd/pmd/issues/1298): \[java] RedundantFieldInitializer - NumberFormatException with Long
*   jsp
    *   [#1274](https://github.com/pmd/pmd/issues/1274): \[jsp] Support EL in tag attributes
    *   [#1276](https://github.com/pmd/pmd/issues/1276): \[jsp] add support for jspf and tag extensions
*   plsql
    *   [#681](https://github.com/pmd/pmd/issues/681): \[plsql] Parse error with Cursor For Loop

### API Changes

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

### External Contributions

*   [#109](https://github.com/pmd/pmd/pull/109): \[java] Add two linguistics rules under naming - [Arda Aslan](https://github.com/ardaasln)
*   [#1254](https://github.com/pmd/pmd/pull/1254): \[ci] \[GSoC] Integrating the danger and pmdtester to travis CI - [BBG](https://github.com/djydewang)
*   [#1258](https://github.com/pmd/pmd/pull/1258): \[java] Use typeof in MissingSerialVersionUID - [krichter722](https://github.com/krichter722)
*   [#1264](https://github.com/pmd/pmd/pull/1264): \[cpp] Fix NullPointerException in CPPTokenizer:99 - [Rafael Cortês](https://github.com/mrfyda)
*   [#1277](https://github.com/pmd/pmd/pull/1277): \[jsp] #1276 add support for jspf and tag extensions - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1275](https://github.com/pmd/pmd/pull/1275): \[jsp] Issue #1274 - Support EL in tag attributes - [Jordi Llach](https://github.com/jordillachmrf)
*   [#1278](https://github.com/pmd/pmd/pull/1278): \[ci] \[GSoC] Use pmdtester 1.0.0.pre.beta3 - [BBG](https://github.com/djydewang)
*   [#1289](https://github.com/pmd/pmd/pull/1289): \[java] UselessParentheses: Fix false positive with assignments - [cobratbq](https://github.com/cobratbq)
*   [#1290](https://github.com/pmd/pmd/pull/1290): \[docs] \[GSoC] Create the documentation about pmdtester - [BBG](https://github.com/djydewang)
*   [#1256](https://github.com/pmd/pmd/pull/1256): \[java] #940 Avoid JUnit 4 false positives for JUnit 5 tests - [Alex Shesterov](https://github.com/vovkss)
*   [#1315](https://github.com/pmd/pmd/pull/1315): \[apex] Add OneDeclarationPerStatement rule - [Jeff Hube](https://github.com/jeffhube)


## 29-July-2018 - 6.6.0

The PMD team is pleased to announce PMD 6.6.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [Java 11 Support](#java-11-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### Java 11 Support

PMD is now able to parse the local-variable declaration syntax `var xxx`, that has been
extended for lambda parameters with Java 11 via
[JEP 323: Local-Variable Syntax for Lambda Parameters](http://openjdk.java.net/jeps/323).

#### New Rules

*   The new Java rule [`LocalVariableNamingConventions`](pmd_rules_java_codestyle.html#localvariablenamingconventions)
    (`java-codestyle`) detects local variable names that don't comply to a given convention. It defaults to standard
    Java convention of using camelCase, but can be configured. Special cases can be configured for final variables
    and caught exceptions' names.

*   The new Java rule [`FormalParameterNamingConventions`](pmd_rules_java_codestyle.html#formalparameternamingconventions)
    (`java-codestyle`) detects formal parameter names that don't comply to a given convention. It defaults to
    standard Java convention of using camelCase, but can be configured. Special cases can be configured for final
    parameters and lambda parameters (considering whether they are explicitly typed or not).

#### Modified Rules

*   The Java rules [`AccessorClassGeneration`](pmd_rules_java_bestpractices.html#accessorclassgeneration) and
    [`AccessorMethodGeneration`](pmd_rules_java_bestpractices.html#accessormethodgeneration) (both in category
    `java-bestpractices`) have been modified to be only valid up until Java 10. Java 11 adds support for
    [JEP 181: Nest-Based Access Control](http://openjdk.java.net/jeps/181) which avoids the generation of
    accessor classes / methods altogether.

### Fixed Issues

*   core
    *   [#1178](https://github.com/pmd/pmd/issues/1178): \[core] "Unsupported build listener" in gradle build
    *   [#1225](https://github.com/pmd/pmd/issues/1225): \[core] Error in sed expression on line 82 of run.sh while detecting installed version of Java
*   doc
    *   [#1215](https://github.com/pmd/pmd/issues/1215): \[doc] TOC links don't work?
*   java-codestyle
    *   [#1211](https://github.com/pmd/pmd/issues/1211): \[java] CommentDefaultAccessModifier false positive with nested interfaces (regression from 6.4.0)
    *   [#1216](https://github.com/pmd/pmd/issues/1216): \[java] UnnecessaryFullyQualifiedName false positive for the same name method
*   java-design
    *   [#1217](https://github.com/pmd/pmd/issues/1217): \[java] CyclomaticComplexityRule counts ?-operator twice
    *   [#1226](https://github.com/pmd/pmd/issues/1226): \[java] NPath complexity false negative due to overflow
*   plsql
    *   [#980](https://github.com/pmd/pmd/issues/980): \[plsql] ParseException for CREATE TABLE
    *   [#981](https://github.com/pmd/pmd/issues/981): \[plsql] ParseException when parsing VIEW
    *   [#1047](https://github.com/pmd/pmd/issues/1047): \[plsql] ParseException when parsing EXECUTE IMMEDIATE
*   ui
    *   [#1233](https://github.com/pmd/pmd/issues/1233): \[ui] XPath autocomplete arrows on first and last items

### API Changes

*   The `findDescendantsOfType` methods in `net.sourceforge.pmd.lang.ast.AbstractNode` no longer search for
    exact type matches, but will match subclasses, too. That means, it's now possible to look for abstract node
    types such as `AbstractJavaTypeNode` and not only for it's concrete subtypes.

### External Contributions

* [#1182](https://github.com/pmd/pmd/pull/1182): \[ui] XPath AutoComplete - [Akshat Bahety](https://github.com/akshatbahety)
* [#1231](https://github.com/pmd/pmd/pull/1231): \[doc] Minor typo fix in installation.md - [Ashish Rana](https://github.com/ashishrana160796)
* [#1250](https://github.com/pmd/pmd/pull/1250): \[ci] \[GSoC] Upload baseline of pmdtester automatically - [BBG](https://github.com/djydewang)


## 26-June-2018 - 6.5.0

The PMD team is pleased to announce PMD 6.5.0.

This is a minor release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rules

*   The new Apex rule [`AvoidNonExistentAnnotations`](pmd_rules_apex_errorprone.html#avoidnonexistentannotations) (`apex-errorprone`)
    detects usages non-officially supported annotations. Apex supported non existent annotations for legacy reasons.
    In the future, use of such non-existent annotations could result in broken Apex code that will not compile.
    A full list of supported annotations can be found [here](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm)

#### Modified Rules

*   The Java rule [UnnecessaryModifier](pmd_rules_java_codestyle.html#unnecessarymodifier) (`java-codestyle`)
    now detects enum constrcutors with explicit `private` modifier. The rule now produces better error messages
    letting you know exactly which modifiers are redundant at each declaration.

### Fixed Issues
*   all
    *   [#1119](https://github.com/pmd/pmd/issues/1119): \[doc] Make the landing page of the documentation website more useful
    *   [#1168](https://github.com/pmd/pmd/issues/1168): \[core] xml renderer schema definitions (#538) break included xslt files
    *   [#1173](https://github.com/pmd/pmd/issues/1173): \[core] Some characters in CPD are not shown correctly.
    *   [#1193](https://github.com/pmd/pmd/issues/1193): \[core] Designer doesn't start with run.sh
*   ecmascript
    *   [#861](https://github.com/pmd/pmd/issues/861): \[ecmascript] InnaccurateNumericLiteral false positive with hex literals
*   java
    *   [#1074](https://github.com/pmd/pmd/issues/1074): \[java] MissingOverrideRule exception when analyzing PMD under Java 9
    *   [#1174](https://github.com/pmd/pmd/issues/1174): \[java] CommentUtil.multiLinesIn() could lead to StringIndexOutOfBoundsException
*   java-bestpractices
    *   [#651](https://github.com/pmd/pmd/issues/651): \[java] SwitchStmtsShouldHaveDefault should be aware of enum types
    *   [#869](https://github.com/pmd/pmd/issues/869): \[java] GuardLogStatement false positive on return statements and Math.log
*   java-codestyle
    *   [#667](https://github.com/pmd/pmd/issues/667): \[java] Make AtLeastOneConstructor Lombok-aware
    *   [#1154](https://github.com/pmd/pmd/pull/1154): \[java] CommentDefaultAccessModifierRule FP with nested enums
    *   [#1158](https://github.com/pmd/pmd/issues/1158): \[java] Fix IdenticalCatchBranches false positive
    *   [#1186](https://github.com/pmd/pmd/issues/1186): \[java] UnnecessaryFullyQualifiedName doesn't detect java.lang FQ names as violations
*   java-design
    *   [#1200](https://github.com/pmd/pmd/issues/1200): \[java] New default NcssCount method report level is drastically reduced from values of deprecated NcssMethodCount and NcssTypeCount
*   xml
    *   [#715](https://github.com/pmd/pmd/issues/715): \[xml] ProjectVersionAsDependencyVersion false positive

### API Changes

*   The utility class `net.sourceforge.pmd.lang.java.ast.CommentUtil` has been deprecated and will be removed
    with PMD 7.0.0. Its methods have been intended to parse javadoc tags. A more useful solution will be added
    around the AST node `FormalComment`, which contains as children `JavadocElement` nodes, which in
    turn provide access to the `JavadocTag`.

    All comment AST nodes (`FormalComment`, `MultiLineComment`, `SingleLineComment`) have a new method
    `getFilteredComment()` which provide access to the comment text without the leading `/*` markers.

*   The method `AbstractCommentRule.tagsIndicesIn()` has been deprecated and will be removed with
    PMD 7.0.0. It is not very useful, since it doesn't extract the information
    in a useful way. You would still need check, which tags have been found, and with which
    data they might be accompanied.

### External Contributions

*   [#836](https://github.com/pmd/pmd/pull/836): \[apex] Add a rule to prevent use of non-existent annotations - [anand13s](https://github.com/anand13s)
*   [#1159](https://github.com/pmd/pmd/pull/1159): \[ui] Allow to setup the auxclasspath in the designer - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1169](https://github.com/pmd/pmd/pull/1169): \[core] Update stylesheets with a default namespace - [Matthew Duggan](https://github.com/mduggan)
*   [#1183](https://github.com/pmd/pmd/pull/1183): \[java] fixed typos in rule remediation - [Jake Hemmerle](https://github.com/jakehemmerle)
*   [#1206](https://github.com/pmd/pmd/pull/1206): \[java] Recommend StringBuilder next to StringBuffer - [krichter722](https://github.com/krichter722)


## 29-May-2018 - 6.4.0

The PMD team is pleased to announce PMD 6.4.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Java 10 Support](#java-10-support)
    *   [XPath Type Resolution Functions](#xpath-type-resolution-functions)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
*   [Fixed Issues](#fixed-issues)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Java 10 Support

PMD is now able to understand local-variable type inference as introduced by Java 10.
Simple type resolution features are available, e.g. the type of the variable `s` is inferred
correctly as `String`:

    var s = "Java 10";

#### XPath Type Resolution Functions

For some time now PMD has supported Type Resolution, and exposed this functionality to XPath rules for the Java language
with the `typeof` function. This function however had a number of shortcomings:

*   It would take a first arg with the name to match if types couldn't be resolved. In all cases this was `@Image`
    but was still required.
*   It required 2 separate arguments for the Fully Qualified Class Name and the simple name of the class against
    which to test.
*   If only the Fully Qualified Class Name was provided, no simple name check was performed (not documented,
    but abused on some rules to "fix" some false positives).

In this release we are deprecating `typeof` in favor of a simpler `typeIs` function, which behaves exactly as the
old `typeof` when given all 3 arguments.

`typeIs` receives a single parameter, which is the fully qualified name of the class to test against.

So, calls such as:

```ruby
//ClassOrInterfaceType[typeof(@Image, 'junit.framework.TestCase', 'TestCase')]
```

can now we expressed much more concisely as:

```ruby
//ClassOrInterfaceType[typeIs('junit.framework.TestCase')]
```

With this change, we also allow to check against array types by just appending `[]` to the fully qualified class name.
These can be repeated for arrays of arrays (e.g. `byte[][]` or `java.lang.String[]`).

Additionally, we introduce the companion function `typeIsExactly`, that receives the same parameters as `typeIs`,
but checks for exact type matches, without considering the type hierarchy. That is, the test
`typeIsExactly('junit.framework.TestCase')` will match only if the context node is an instance of `TestCase`, but
not if it's an instance of a subclass of `TestCase`. Be aware then, that using that method with abstract types will
never match.

#### New Rules

*   The new Java rule [`HardCodedCryptoKey`](pmd_rules_java_security.html#hardcodedcryptokey) (`java-security`)
    detects hard coded keys used for encryption. It is recommended to store keys outside of the source code.

*   The new Java rule [`IdenticalCatchBranches`](pmd_rules_java_codestyle.html#identicalcatchbranches) (`java-codestyle`)
    finds catch blocks,
    that catch different exception but perform the same exception handling and thus can be collapsed into a
    multi-catch try statement.

#### Modified Rules

*   The Java rule [JUnit4TestShouldUseTestAnnotation](pmd_rules_java_bestpractices.html#junit4testshouldusetestannotation) (`java-bestpractices`)
    has a new parameter "testClassPattern". It is used to distinguish test classes from other classes and
    avoid false positives. By default, any class, that has "Test" in its name, is considered a test class.

*   The Java rule [CommentDefaultAccessModifier](pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) (`java-codestyle`)
    allows now by default the comment "`/* package */` in addition to "`/* default */`. This behavior can
    still be adjusted by setting the property `regex`.

### Fixed Issues

*   all
    *   [#1018](https://github.com/pmd/pmd/issues/1018): \[java] Performance degradation of 250% between 6.1.0 and 6.2.0
    *   [#1145](https://github.com/pmd/pmd/issues/1145): \[core] JCommander's help text for option -min is wrong
*   java
    *   [#672](https://github.com/pmd/pmd/issues/672): \[java] Support exact type matches for type resolution from XPath
    *   [#743](https://github.com/pmd/pmd/issues/743): \[java] Prepare for Java 10
    *   [#1077](https://github.com/pmd/pmd/issues/1077): \[java] Analyzing enum with lambda passed in constructor fails with "The enclosing scope must exist."
    *   [#1115](https://github.com/pmd/pmd/issues/1115): \[java] Simplify xpath typeof syntax
    *   [#1131](https://github.com/pmd/pmd/issues/1131): \[java] java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/faces/application/FacesMessage$Severity
*   java-bestpractices
    *   [#527](https://github.com/pmd/pmd/issues/572): \[java] False Alarm of JUnit4TestShouldUseTestAnnotation on Predicates
    *   [#1063](https://github.com/pmd/pmd/issues/1063): \[java] MissingOverride is triggered in illegal places
*   java-codestyle
    *   [#720](https://github.com/pmd/pmd/issues/720): \[java] ShortVariable should whitelist lambdas
    *   [#955](https://github.com/pmd/pmd/issues/955): \[java] Detect identical catch statements
    *   [#1114](https://github.com/pmd/pmd/issues/1114): \[java] Star import overwritten by explicit import is not correctly handled
    *   [#1064](https://github.com/pmd/pmd/issues/1064): \[java] ClassNamingConventions suggests to add Util suffix for simple exception wrappers
    *   [#1065](https://github.com/pmd/pmd/issues/1065): \[java] ClassNamingConventions shouldn't prohibit numbers in class names
    *   [#1067](https://github.com/pmd/pmd/issues/1067): \[java] \[6.3.0] PrematureDeclaration false-positive
    *   [#1096](https://github.com/pmd/pmd/issues/1096): \[java] ClassNamingConventions is too ambitious on finding utility classes
*   java-design
    *   [#824](https://github.com/pmd/pmd/issues/824): \[java] UseUtilityClass false positive when extending
    *   [#1021](https://github.com/pmd/pmd/issues/1021): \[java] False positive for `DoNotExtendJavaLangError`
    *   [#1097](https://github.com/pmd/pmd/pull/1097): \[java] False negative in AvoidThrowingRawExceptionTypes
*   java-performance
    *   [#1051](https://github.com/pmd/pmd/issues/1051): \[java] ConsecutiveAppendsShouldReuse false-negative
    *   [#1098](https://github.com/pmd/pmd/pull/1098): \[java] Simplify LongInstantiation, IntegerInstantiation, ByteInstantiation, and ShortInstantiation using type resolution
    *   [#1125](https://github.com/pmd/pmd/issues/1125): \[java] Improve message of InefficientEmptyStringCheck for String.trim().isEmpty()
*   doc
    *   [#999](https://github.com/pmd/pmd/issues/999): \[doc] Add a header before the XPath expression in rules
    *   [#1082](https://github.com/pmd/pmd/issues/1082): \[doc] Multifile analysis doc is invalid
*   vf-security
    *   [#1100](https://github.com/pmd/pmd/issues/1100): \[vf] URLENCODE is ignored as valid escape method

### API Changes

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methdos in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.


### External Contributions

*   [#966](https://github.com/pmd/pmd/pull/966): \[java] Issue #955: add new rule to detect identical catch statement - [Clément Fournier](https://github.com/oowekyala) and [BBG](https://github.com/djydewang)
*   [#1046](https://github.com/pmd/pmd/pull/1046): \[java] New security rule for finding hard-coded keys used for cryptographic operations - [Sergey Gorbaty](https://github.com/sgorbaty)
*   [#1101](https://github.com/pmd/pmd/pull/1101): \[java] Fixes false positive for `DoNotExtendJavaLangError`  - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1106](https://github.com/pmd/pmd/pull/1106): \[vf] URLENCODE is ignored as valid escape method - [Robert Sösemann](https://github.com/rsoesemann)
*   [#1126](https://github.com/pmd/pmd/pull/1126): \[java] Improve implementation hint in InefficientEmptyStringCheck - [krichter722](https://github.com/krichter722)
*   [#1129](https://github.com/pmd/pmd/pull/1129): \[java] Adjust InefficientEmptyStringCheck documentation - [krichter722](https://github.com/krichter722)
*   [#1137](https://github.com/pmd/pmd/pull/1137): \[ui] Removes the need for RefreshAST - [Akshat Bahety](https://github.com/akshatbahety)



## 29-April-2018 - 6.3.0

The PMD team is pleased to announce PMD 6.3.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Tree Traversal Revision](#tree-traversal-revision)
    *   [Naming Rules Enhancements](#naming-rules-enhancements)
    *   [CPD Suppression](#cpd-suppression)
    *   [Swift 4.1 Support](#swift-41-support)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
    *   [Deprecated Rules](#deprecated-rules)
*   [Fixed Issues](#fixed-issues)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Tree Traversal Revision

As described in [#904](https://github.com/pmd/pmd/issues/904), when searching for child nodes of the AST methods
such as `hasDescendantOfType`, `getFirstDescendantOfType` and `findDescendantsOfType` were found to behave inconsistently,
not all of them honoring find boundaries; that is, nodes that define a self-contained entity which should be considered separately
(think of lambdas, nested classes, anonymous classes, etc.). We have modified these methods to ensure all of them honor
find boundaries.

This change implies several false positives / unexpected results
(ie: `ASTBlockStatement` falsely returning `true` to `isAllocation()`)
have been fixed; and lots of searches are now restricted to smaller search areas, which improves performance
(depending on the project, we have measured up to 10% improvements during Type Resolution, Symbol Table analysis,
and some rules' application).

#### Naming Rules Enhancements

*   [ClassNamingConventions](pmd_rules_java_codestyle.html#classnamingconventions) (`java-codestyle`)
    has been enhanced to allow granular configuration of naming
    conventions for different kinds of type declarations (eg enum or abstract
    class). Each kind of declaration can use its own naming convention
    using a regex property. See the rule's documentation for more info about
    configuration and default conventions.

*   [MethodNamingConventions](pmd_rules_java_codestyle.html#methodnamingconventions) (`java-codestyle`)
    has been enhanced in the same way.

#### CPD Suppression

Back in PMD 5.6.0 we introduced the ability to suppress CPD warnings in Java using comments, by
including `CPD-OFF` (to start ignoring code), or `CPD-ON` (to resume analysis) during CPD execution.
This has proved to be much more flexible and versatile than the old annotation-based approach,
and has since been the preferred way to suppress CPD warnings.

On this occasion, we are extending support for comment-based suppressions to many other languages:

*   C/C++
*   Ecmascript / Javascript
*   Matlab
*   Objective-C
*   PL/SQL
*   Python

So for instance, in Python we could now do:

```python
class BaseHandler(object):
    def __init__(self):
        # some unignored code

        # tell cpd to start ignoring code - CPD-OFF

        # mission critical code, manually loop unroll
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);
        GoDoSomethingAwesome(x + x / 2);

        # resume CPD analysis - CPD-ON

        # further code will *not* be ignored
```

Other languages are equivalent.

#### Swift 4.1 Support

Thanks to major contributions from [kenji21](https://github.com/kenji21) the Swift grammar has been updated to
support Swift 4.1. This is a major update, since the old grammar was quite dated, and we are sure all iOS
developers will enjoy it.

Unfortunately, this change is not compatible. The grammar elements that have been removed (ie: the keywords `__FILE__`,
`__LINE__`, `__COLUMN__` and `__FUNCTION__`) are no longer supported. We don't usually introduce such
drastic / breaking changes in minor releases, however, given that the whole Swift ecosystem pushes hard towards
always using the latest versions, and that Swift needs all code and libraries to be currently compiling against
the same Swift version, we felt strongly this change was both safe and necessary to be shipped as soon as possible.
We had great feedback from the community during the process but if you have a legitimate use case for older Swift
versions, please let us know [on our Issue Tracker](https://github.com/pmd/pmd/issues).

#### New Rules

*   The new Java rule [InsecureCryptoIv](pmd_rules_java_security.html#insecurecryptoiv) (`java-security`)
    detects hard coded initialization vectors used in cryptographic operations. It is recommended to use
    a randomly generated IV.

#### Modified Rules

*   The Java rule [UnnecessaryConstructor](pmd_rules_java_codestyle.html#unnecessaryconstructor) (`java-codestyle`)
    has been rewritten as a Java rule (previously it was a XPath-based rule). It supports a new property
    `ignoredAnnotations` and ignores by default empty constructors,
    that are annotated with `javax.inject.Inject`. Additionally, it detects now also unnecessary private constructors
    in enums.

*   The property `checkNativeMethods` of the Java rule [MethodNamingConventions](pmd_rules_java_codestyle.html#methodnamingconventions) (`java-codestyle`)
    is now deprecated, as it is now superseded by `nativePattern`. Support for that property will be maintained until
    7.0.0.

*   The Java rule [ControlStatementBraces](pmd_rules_java_codestyle.html#controlstatementbraces) (`java-codestyle`)
    supports a new boolean property `checkSingleIfStmt`. When unset, the rule won't report `if` statements which lack
    braces, if the statement is not part of an `if ... else if` chain. This property defaults to true.

#### Deprecated Rules

*   The Java rule AbstractNaming (`java-codestyle`) is deprecated
    in favour of [ClassNamingConventions](pmd_rules_java_codestyle.html#classnamingconventions).
    See [Naming rules enhancements](#naming-rules-enhancements).

### Fixed Issues

*   all
    *   [#695](https://github.com/pmd/pmd/issues/695): \[core] Extend comment-based suppression to all JavaCC languages
    *   [#988](https://github.com/pmd/pmd/issues/988): \[core] FileNotFoundException for missing classes directory with analysis cache enabled
    *   [#1036](https://github.com/pmd/pmd/issues/1036): \[core] Non-XML output breaks XML-based CLI integrations
*   apex-errorprone
    *   [#776](https://github.com/pmd/pmd/issues/776): \[apex] AvoidHardcodingId false positives
*   documentation
    *   [#994](https://github.com/pmd/pmd/issues/994): \[doc] Delete duplicate page contributing.md on the website
    *   [#1057](https://github.com/pmd/pmd/issues/1057): \[doc] Documentation of ignoredAnnotations property is misleading
*   java
    *   [#894](https://github.com/pmd/pmd/issues/894): \[java] Maven PMD plugin fails to process some files without any explanation
    *   [#899](https://github.com/pmd/pmd/issues/899): \[java] JavaTypeDefinitionSimple.toString can cause NPEs
    *   [#1020](https://github.com/pmd/pmd/issues/1020): \[java] The CyclomaticComplexity rule runs forever in 6.2.0
    *   [#1030](https://github.com/pmd/pmd/pull/1030): \[java] NoClassDefFoundError when analyzing PMD with PMD
    *   [#1061](https://github.com/pmd/pmd/issues/1061): \[java] Update ASM to handle Java 10 bytecode
*   java-bestpractices
    *   [#370](https://github.com/pmd/pmd/issues/370): \[java] GuardLogStatementJavaUtil not considering lambdas
    *   [#558](https://github.com/pmd/pmd/issues/558): \[java] ProperLogger Warnings for enums
    *   [#719](https://github.com/pmd/pmd/issues/719): \[java] Unused Code: Java 8 receiver parameter with an internal class
    *   [#1009](https://github.com/pmd/pmd/issues/1009): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5
*   java-codestyle
    *   [#1003](https://github.com/pmd/pmd/issues/1003): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject)
    *   [#1023](https://github.com/pmd/pmd/issues/1023): \[java] False positive for useless parenthesis
    *   [#1004](https://github.com/pmd/pmd/issues/1004): \[java] ControlStatementBraces is missing checkIfStmt property
*   java-design
    *   [#1056](https://github.com/pmd/pmd/issues/1056): \[java] Property ignoredAnnotations does not work for SingularField and ImmutableField
*   java-errorprone
    *   [#629](https://github.com/pmd/pmd/issues/629): \[java] NullAssignment false positive
    *   [#816](https://github.com/pmd/pmd/issues/816): \[java] SingleMethodSingleton false positives with inner classes
*   java-performance
    *   [#586](https://github.com/pmd/pmd/issues/586): \[java] AvoidUsingShortType erroneously triggered on overrides of 3rd party methods
*   swift
    *   [#678](https://github.com/pmd/pmd/issues/678): \[swift]\[cpd] Exception when running for Swift 4 code (KeyPath)

### External Contributions

*   [#778](https://github.com/pmd/pmd/pull/778): \[swift] Support Swift 4 grammar - [kenji21](https://github.com/kenji21)
*   [#1002](https://github.com/pmd/pmd/pull/1002): \[doc] Delete duplicate page contributing.md on the website - [Ishan Srivastava](https://github.com/ishanSrt)
*   [#1008](https://github.com/pmd/pmd/pull/1008): \[core] DOC: fix closing tag for &lt;pmdVersion> - [stonio](https://github.com/stonio)
*   [#1010](https://github.com/pmd/pmd/pull/1010): \[java] UnnecessaryConstructor triggered on required empty constructor (Dagger @Inject) - [BBG](https://github.com/djydewang)
*   [#1012](https://github.com/pmd/pmd/pull/1012): \[java] JUnitAssertionsShouldIncludeMessage - False positive with assertEquals and JUnit5 - [BBG](https://github.com/djydewang)
*   [#1024](https://github.com/pmd/pmd/pull/1024): \[java] Issue 558: Properlogger for enums - [Utku Cuhadaroglu](https://github.com/utkuc)
*   [#1041](https://github.com/pmd/pmd/pull/1041): \[java] Make BasicProjectMemoizer thread safe. - [bergander](https://github.com/bergander)
*   [#1042](https://github.com/pmd/pmd/pull/1042): \[java] New security rule: report usage of hard coded IV in crypto operations - [Sergey Gorbaty](https://github.com/sgorbaty)
*   [#1044](https://github.com/pmd/pmd/pull/1044): \[java] Fix for issue #816 - [Akshat Bahety](https://github.com/akshatbahety)
*   [#1048](https://github.com/pmd/pmd/pull/1048): \[core] Make MultiThreadProcessor more space efficient - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#1062](https://github.com/pmd/pmd/pull/1062): \[core] Update ASM to version 6.1.1 - [Austin Shalit](https://github.com/AustinShalit)


## 26-March-2018 - 6.2.0

The PMD team is pleased to announce PMD 6.2.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Ecmascript (JavaScript)](#ecmascript-javascript)
    *   [Disable Incremental Analysis](#disable-incremental-analysis)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
*   [Fixed Issues](#fixed-issues)
*   [API Changes](#api-changes)
*   [External Contributions](#external-contributions)

### New and noteworthy

#### Ecmascript (JavaScript)

The [Rhino Library](https://github.com/mozilla/rhino) has been upgraded from version 1.7.7 to version 1.7.7.2.

Detailed changes for changed in Rhino can be found:
* [For 1.7.7.2](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1772)
* [For 1.7.7.1](https://github.com/mozilla/rhino/blob/master/RELEASE-NOTES.md#rhino-1771)

Both are bugfixing releases.

#### Disable Incremental Analysis

Some time ago, we added support for [Incremental Analysis](pmd_userdocs_incremental_analysis.html). On PMD 6.0.0, we
started to add warns when not using it, as we strongly believe it's a great improvement to our user's experience as
analysis time is greatly reduced; and in the future we plan to have it enabled by default. However, we realize some
scenarios don't benefit from it (ie: CI jobs), and having the warning logged can be noisy and cause confusion.

To this end, we have added a new flag to allow you to explicitly disable incremental analysis. On CLI, this is
the new `-no-cache` flag. On Ant, there is a `noCache` attribute for the `<pmd>` task.

On both scenarios, disabling the cache takes precedence over setting a cache location.

#### New Rules

*   The new Java rule [`MissingOverride`](pmd_rules_java_bestpractices.html#missingoverride)
    (category `bestpractices`) detects overridden and implemented methods, which are not marked with the
    `@Override` annotation. Annotating overridden methods with `@Override` ensures at compile time that
    the method really overrides one, which helps refactoring and clarifies intent.

*   The new Java rule [`UnnecessaryAnnotationValueElement`](pmd_rules_java_codestyle.html#unnecessaryannotationvalueelement)
    (category `codestyle`) detects annotations with a single element (`value`) that explicitely names it.
    That is, doing `@SuppressWarnings(value = "unchecked")` would be flagged in favor of
    `@SuppressWarnings("unchecked")`.

*   The new Java rule [`ControlStatementBraces`](pmd_rules_java_codestyle.html#controlstatementbraces)
    (category `codestyle`) enforces the presence of braces on control statements where they are optional.
    Properties allow to customize which statements are required to have braces. This rule replaces the now
    deprecated rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and
    `IfElseStmtMustUseBraces`. More than covering the use cases of those rules, this rule also supports
    `do ... while` statements and `case` labels of `switch` statements (disabled by default).

#### Modified Rules

*   The Java rule `CommentContentRule` (`java-documentation`) previously had the property `wordsAreRegex`. But this
    property never had been implemented and is removed now.

*   The Java rule `UnusedPrivateField` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the field should be ignored. By default `@java.lang.Deprecated`
    and `@javafx.fxml.FXML` are ignored.

*   The Java rule `UnusedPrivateMethod` (`java-bestpractices`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default `@java.lang.Deprecated`
    is ignored.

*   The Java rule `ImmutableField` (`java-design`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default several `lombok`
    annotations are ignored

*   The Java rule `SingularField` (`java-design`) now has a new `ignoredAnnotations` property
    that allows to configure annotations that imply the method should be ignored. By default several `lombok`
    annotations are ignored

#### Deprecated Rules

*   The Java rules `WhileLoopMustUseBraces`, `ForLoopMustUseBraces`, `IfStmtMustUseBraces`, and `IfElseStmtMustUseBraces`
    are deprecated. They will be replaced by the new rule `ControlStatementBraces`, in the category `codestyle`.

### Fixed Issues

*   all
    *   [#928](https://github.com/pmd/pmd/issues/928): \[core] PMD build failure on Windows
*   java-bestpracrtices
    *   [#907](https://github.com/pmd/pmd/issues/907): \[java] UnusedPrivateField false-positive with @FXML
    *   [#963](https://github.com/pmd/pmd/issues/965): \[java] ArrayIsStoredDirectly not triggered from variadic functions
*   java-codestyle
    *   [#974](https://github.com/pmd/pmd/issues/974): \[java] Merge \*StmtMustUseBraces rules
    *   [#983](https://github.com/pmd/pmd/issues/983): \[java] Detect annotations with single value element
*   java-design
    *   [#832](https://github.com/pmd/pmd/issues/832): \[java] AvoidThrowingNullPointerException documentation suggestion
    *   [#837](https://github.com/pmd/pmd/issues/837): \[java] CFGs of declared but not called lambdas are treated as parts of an enclosing method's CFG
    *   [#839](https://github.com/pmd/pmd/issues/839): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors
    *   [#968](https://github.com/pmd/pmd/issues/968): \[java] UseUtilityClassRule reports false positive with lombok NoArgsConstructor
*   documentation
    *   [#978](https://github.com/pmd/pmd/issues/978): \[core] Broken link in CONTRIBUTING.md
    *   [#992](https://github.com/pmd/pmd/issues/992): \[core] Include info about rule doc generation in "Writing Documentation" md page

### API Changes

*    A new CLI switch, `-no-cache`, disables incremental analysis and the related suggestion. This overrides the
     `-cache` option. The corresponding Ant task parameter is `noCache`.

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

### External Contributions

* [#941](https://github.com/pmd/pmd/pull/941): \[java] Use char notation to represent a character to improve performance - [reudismam](https://github.com/reudismam)
* [#943](https://github.com/pmd/pmd/pull/943): \[java] UnusedPrivateField false-positive with @FXML - [BBG](https://github.com/djydewang)
* [#951](https://github.com/pmd/pmd/pull/951): \[java] Add ignoredAnnotations property to unusedPrivateMethod rule - [BBG](https://github.com/djydewang)
* [#952](https://github.com/pmd/pmd/pull/952): \[java] SignatureDeclareThrowsException's IgnoreJUnitCompletely property not honored for constructors - [BBG](https://github.com/djydewang)
* [#958](https://github.com/pmd/pmd/pull/958): \[java] Refactor how we ignore annotated elements in rules - [BBG](https://github.com/djydewang)
* [#965](https://github.com/pmd/pmd/pull/965): \[java] Make Varargs trigger ArrayIsStoredDirectly - [Stephen](https://github.com/pmd/pmd/issues/907)
* [#967](https://github.com/pmd/pmd/pull/967): \[doc] Issue 959: fixed broken link to XPath Rule Tutorial - [Andrey Mochalov](https://github.com/epidemia)
* [#969](https://github.com/pmd/pmd/pull/969): \[java] Issue 968 Add logic to handle lombok private constructors with utility classes - [Kirk Clemens](https://github.com/clem0110)
* [#970](https://github.com/pmd/pmd/pull/970): \[java] Fixed inefficient use of keySet iterator instead of entrySet iterator - [Andrey Mochalov](https://github.com/epidemia)
* [#984](https://github.com/pmd/pmd/pull/984): \[java] issue983 Add new UnnecessaryAnnotationValueElement rule - [Kirk Clemens](https://github.com/clem0110)
* [#989](https://github.com/pmd/pmd/pull/989): \[core] Update Contribute.md to close Issue #978 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#990](https://github.com/pmd/pmd/pull/990): \[java] Updated Doc on AvoidThrowingNullPointerException to close Issue #832 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)
* [#993](https://github.com/pmd/pmd/pull/993): \[core] Update writing_documentation.md to fix Issue #992 - [Bolarinwa Saheed Olayemi](https://github.com/refactormyself)


## 25-February-2018 - 6.1.0

The PMD team is pleased to announce PMD 6.1.0.

This is a minor release.

### Table Of Contents

*   [New and noteworthy](#new-and-noteworthy)
    *   [Designer UI](#designer-ui)
*    [Fixed Issues](#fixed-issues)
*    [API Changes](#api-changes)
     *   [Changes to the Node interface](#changes-to-the-node-interface)
     *   [Changes to CPD renderers](#changes-to-cpd-renderers)
*    [External Contributions](#external-contributions)

### New and noteworthy

#### Designer UI

The Designer now supports configuring properties for XPath based rule development.
The Designer is still under development and any feedback is welcome.

You can start the designer via `run.sh designer` or `designer.bat`.

### Fixed Issues

*   all
    *   [#569](https://github.com/pmd/pmd/issues/569): \[core] XPath support requires specific toString implementations
    *   [#795](https://github.com/pmd/pmd/issues/795): \[cpd] java.lang.OutOfMemoryError
    *   [#848](https://github.com/pmd/pmd/issues/848): \[doc] Test failures when building pmd-doc under Windows
    *   [#872](https://github.com/pmd/pmd/issues/872): \[core] NullPointerException at FileDataSource.glomName()
    *   [#854](https://github.com/pmd/pmd/issues/854): \[ci] Use Java9 for building PMD
*   doc
    *   [#791](https://github.com/pmd/pmd/issues/791): \[doc] Documentation site reorganisation
    *   [#891](https://github.com/pmd/pmd/issues/891): \[doc] Apex @SuppressWarnings should use single quotes instead of double quotes
    *   [#909](https://github.com/pmd/pmd/issues/909): \[doc] Please add new PMD Eclipse Plugin to tool integration section
*   java
    *   [#825](https://github.com/pmd/pmd/issues/825): \[java] Excessive\*Length ignores too much
    *   [#888](https://github.com/pmd/pmd/issues/888): \[java] ParseException occurs with valid '<>' in Java 1.8 mode
    *   [#920](https://github.com/pmd/pmd/pull/920): \[java] Update valid identifiers in grammar
*   java-bestpractices
    *   [#784](https://github.com/pmd/pmd/issues/784): \[java] ForLoopCanBeForeach false-positive
    *   [#925](https://github.com/pmd/pmd/issues/925): \[java] UnusedImports false positive for static import
*   java-design
    *   [#855](https://github.com/pmd/pmd/issues/855): \[java] ImmutableField false-positive with lambdas
*   java-documentation
    *   [#877](https://github.com/pmd/pmd/issues/877): \[java] CommentRequired valid rule configuration causes PMD error
*   java-errorprone
    *   [#885](https://github.com/pmd/pmd/issues/885): \[java] CompareObjectsWithEqualsRule trigger by enum1 != enum2
*   java-performance
    *   [#541](https://github.com/pmd/pmd/issues/541): \[java] ConsecutiveLiteralAppends with types other than string
*   scala
    *   [#853](https://github.com/pmd/pmd/issues/853): \[scala] Upgrade scala version to support Java 9
*   xml
    *   [#739](https://github.com/pmd/pmd/issues/739): \[xml] IllegalAccessException when accessing attribute using Saxon on JRE 9


### API Changes

#### Changes to the Node interface

The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
A default implementation is provided in `AbstractNode`, to stay compatible
with existing implementors.

The `toString` method of a Node is not changed for the time being, and still produces
the name of the XPath node. That behaviour may however change in future major releases,
e.g. to produce a more useful message for debugging.

#### Changes to CPD renderers

The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface `net.sourceforge.pmd.cpd.renderer.CPDRenderer`
has been introduced to replace it. The main difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on large projects, with many duplications,
it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

`net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

### External Contributions

*   [#790](https://github.com/pmd/pmd/pull/790): \[java] Added some comments for JDK 9 - [Tobias Weimer](https://github.com/tweimer)
*   [#803](https://github.com/pmd/pmd/pull/803): \[doc] Added SpotBugs as successor of FindBugs - [Tobias Weimer](https://github.com/tweimer)
*   [#828](https://github.com/pmd/pmd/pull/828): \[core] Add operations to manipulate a document - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#830](https://github.com/pmd/pmd/pull/830): \[java] UseArraysAsList: Description added - [Tobias Weimer](https://github.com/tweimer)
*   [#845](https://github.com/pmd/pmd/pull/845): \[java] Fix false negative PreserveStackTrace on string concatenation - [Alberto Fernández](https://github.com/albfernandez)
*   [#868](https://github.com/pmd/pmd/pull/868): \[core] Improve XPath documentation && make small refactors - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#875](https://github.com/pmd/pmd/pull/875): \[core] Support shortnames when using filelist - [John Zhang](https://github.com/johnjiabinzhang)
*   [#886](https://github.com/pmd/pmd/pull/886): \[java] Fix #885 - [Matias Comercio](https://github.com/MatiasComercio)
*   [#900](https://github.com/pmd/pmd/pull/900): \[core] Use the isEmpty method instead of comparing the value of size() to 0 - [reudismam](https://github.com/reudismam)
*   [#914](https://github.com/pmd/pmd/pull/914): \[doc] Apex @SuppressWarnings documentation updated - [Akshat Bahety](https://github.com/akshatbahety)
*   [#918](https://github.com/pmd/pmd/pull/918): \[doc] Add qa-eclipse as new tool - [Akshat Bahety](https://github.com/akshatbahety)
*   [#927](https://github.com/pmd/pmd/pull/927): \[java]\[doc] Fix example of AbstractClassWithoutAnyMethod - [Kazuma Watanabe](https://github.com/wata727)


## 21-January-2018 - 6.0.1

The PMD team is pleased to announce PMD 6.0.1.

This is a bug fixing release.

### Table Of Contents

* [Additional information about the new introduced rule categories](#additional-information-about-the-new-introduced-rule-categories)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### Additional information about the new introduced rule categories

With the release of PMD 6.0.0, all rules have been sorted into one of the following eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

Please note, that not every category in every language may have a rule. There might be categories with no
rules at all, such as `category/java/security.xml`, which has currently no rules.
There are even languages, which only have rules of one category (e.g. `category/xml/errorprone.xml`).

You can find the information about available rules in the generated rule documentation, available
at <https://pmd.github.io/6.0.1/>.

In order to help migrate to the new category scheme, the new name for the old, deprecated rule names will
be logged as a warning. See [PR #865](https://github.com/pmd/pmd/pull/865). Please note, that the deprecated
rule names will keep working throughout PMD 6. You can upgrade to PMD 6 without the immediate need
to migrate your current ruleset. That backwards compatibility will be maintained until PMD 7.0.0 is released.

### Fixed Issues

*   all
    *   [#842](https://github.com/pmd/pmd/issues/842): \[core] Use correct java bootclasspath for compiling
*   apex-errorprone
    *   [#792](https://github.com/pmd/pmd/issues/792): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes
*   apex-security
    *   [#788](https://github.com/pmd/pmd/issues/788): \[apex] Method chaining breaks ApexCRUDViolation
*   doc
    *   [#782](https://github.com/pmd/pmd/issues/782): \[doc] Wrong information in the Release Notes about the Security ruleset
    *   [#794](https://github.com/pmd/pmd/issues/794): \[doc] Broken documentation links for 6.0.0
*   java
    *   [#793](https://github.com/pmd/pmd/issues/793): \[java] Parser error with private method in nested classes in interfaces
    *   [#814](https://github.com/pmd/pmd/issues/814): \[java] UnsupportedClassVersionError is failure instead of a warning
    *   [#831](https://github.com/pmd/pmd/issues/831): \[java] StackOverflow in JavaTypeDefinitionSimple.toString
*   java-bestpractices
    *   [#783](https://github.com/pmd/pmd/issues/783): \[java] GuardLogStatement regression
    *   [#800](https://github.com/pmd/pmd/issues/800): \[java] ForLoopCanBeForeach NPE when looping on `this` object
*   java-codestyle
    *   [#817](https://github.com/pmd/pmd/issues/817): \[java] UnnecessaryModifierRule crashes on valid code
*   java-design
    *   [#785](https://github.com/pmd/pmd/issues/785): \[java] NPE in DataClass rule
    *   [#812](https://github.com/pmd/pmd/issues/812): \[java] Exception applying rule DataClass
    *   [#827](https://github.com/pmd/pmd/issues/827): \[java] GodClass crashes with java.lang.NullPointerException
*   java-performance
    *   [#841](https://github.com/pmd/pmd/issues/841): \[java] InsufficientStringBufferDeclaration NumberFormatException
*   java-typeresolution
    *   [#866](https://github.com/pmd/pmd/issues/866): \[java] rulesets/java/typeresolution.xml lists non-existent rules

### API Changes

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.

### External Contributions

*   [#796](https://github.com/pmd/pmd/pull/796): \[apex] AvoidDirectAccessTriggerMap incorrectly detects array access in classes - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#799](https://github.com/pmd/pmd/pull/799): \[apex] Method chaining breaks ApexCRUDViolation - [Robert Sösemann](https://github.com/up2go-rsoesemann)


## 15-December-2017 - 6.0.0

The PMD team is pleased to announce PMD 6.0.0.

This is a major release.

### Table Of Contents

* [New and noteworthy](#new-and-noteworthy)
    *   [New Rule Designer](#new-rule-designer)
    *   [Java 9 support](#java-9-support)
    *   [Revamped Apex CPD](#revamped-apex-cpd)
    *   [Java Type Resolution](#java-type-resolution)
    *   [Metrics Framework](#metrics-framework)
    *   [Error Reporting](#error-reporting)
    *   [Apex Rule Suppression](#apex-rule-suppression)
    *   [Rule Categories](#rule-categories)
    *   [New Rules](#new-rules)
    *   [Modified Rules](#modified-rules)
    *   [Deprecated Rules](#deprecated-rules)
    *   [Removed Rules](#removed-rules)
    *   [Java Symbol Table](#java-symbol-table)
    *   [Apex Parser Update](#apex-parser-update)
    *   [Incremental Analysis](#incremental-analysis)
    *   [Rule and Report Properties](#rule-and-report-properties)
* [Fixed Issues](#fixed-issues)
* [API Changes](#api-changes)
* [External Contributions](#external-contributions)

### New and noteworthy

#### New Rule Designer

Thanks to [Clément Fournier](https://github.com/oowekyala), we now have a new rule designer GUI, which
is based on JavaFX. It replaces the old designer and can be started via

*   `bin/run.sh designer` (on Unix-like platform such as Linux and Mac OS X)
*   `bin\designer.bat` (on Windows)

Note: At least Java8 is required for the designer. The old designer is still available
as `designerold` but will be removed with the next major release.

#### Java 9 support

The Java grammar has been updated to support analyzing Java 9 projects:

*   private methods in interfaces are possible
*   The underscore "\_" is considered an invalid identifier
*   Diamond operator for anonymous classes
*   The module declarations in `module-info.java` can be parsed
*   Concise try-with-resources statements are supported

Java 9 support is enabled by default. You can switch back to an older java version
via the command line, e.g. `-language java -version 1.8`.

#### Revamped Apex CPD

We are now using the Apex Jorje Lexer to tokenize Apex code for CPD. This change means:

*   All comments are now ignored for CPD. This is consistent with how other languages such as Java and Groovy work.
*   Tokenization honors the language specification, which improves accuracy.

CPD will therefore have less false positives and false negatives.

#### Java Type Resolution

As part of Google Summer of Code 2017, [Bendegúz Nagy](https://github.com/WinterGrascph) worked on type resolution
for Java. For this release he has extended support for method calls for both instance and static methods.

Method shadowing and overloading are supported, as are varargs. However, the selection of the target method upon
the presence of generics and type inference is still work in progress. Expect it in forecoming releases.

As for fields, the basic support was in place for release 5.8.0, but has now been expanded to support static fields.

#### Metrics Framework

As part of Google Summer of Code 2017, [Clément Fournier](https://github.com/oowekyala) is worked
on the new metrics framework for object-oriented metrics.

There are already a couple of metrics (e.g. ATFD, WMC, Cyclo, LoC) implemented. More metrics are planned.
Based on those metrics, rules like "GodClass" detection could be implemented more easily.
The following rules benefit from the metrics framework: NcssCount (java), NPathComplexity (java),
CyclomaticComplexity (both java and apex).

The Metrics framework has been abstracted and is available in `pmd-core` for other languages. With this
PMD release, the metrics framework is supported for both Java and Apex.

#### Error Reporting

A number of improvements on error reporting have taken place, meaning changes to some of the report formats.

Also of note, the xml report now provides a XML Schema definition, allowing easier parsing and validation.

##### Processing Errors

Processing errors can now provide not only the message previously included on some reports, but also a full stacktrace.
This will allow better error reports when providing feedback to the PMD team and help in debugging issues.

The report formats providing full stacktrace of errors are:

*   html
*   summaryhtml
*   textcolor
*   vbhtml
*   xml

##### Configuration Errors

For a long time reports have been notified of configuration errors on rules, but they have remained hidden.
On a push to make these more evident to users, and help them get the best results out of PMD, we have started
to include them on the reports.

So far, only reports that include processing errors are showing configuration errors. In other words, the report formats
providing configuration error reporting are:

*   csv
*   html
*   summaryhtml
*   text
*   textcolor
*   vbhtml
*   xml

As we move forward we will be able to detect and report more configuration errors (ie: incomplete `auxclasspath`)
and include them to such reports.

#### Apex Rule Suppression

Apex violations can now be suppressed very similarly to how it's done in Java, by making use of a
`@SuppressWarnings` annotation.

Supported syntax includes:

```
@SupressWarnings('PMD') // to supress all Apex rules
@SupressWarnings('all') // to supress all Apex rules
@SupressWarnings('PMD.ARuleName') // to supress only the rule named ARuleName
@SupressWarnings('PMD.ARuleName, PMD.AnotherRuleName') // to supress only the rule named ARuleName or AnotherRuleName
```

Notice this last scenario is slightly different to the Java syntax. This is due to differences in the Apex grammar for annotations.

#### Rule Categories

All built-in rules have been sorted into one of eight categories:

1.  **Best Practices**: These are rules which enforce generally accepted best practices.
2.  **Code Style**: These rules enforce a specific coding style.
3.  **Design**: Rules that help you discover design issues.
4.  **Documentation**: These rules are related to code documentation.
5.  **Error Prone**: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
6.  **Multithreading**: These are rules that flag issues when dealing with multiple threads of execution.
7.  **Performance**: Rules that flag suboptimal code.
8.  **Security**: Rules that flag potential security flaws.

These categories help you to find rules and figure out the relevance and impact for your project.

All rules have been moved accordingly, e.g. the rule "JumbledIncrementer", which was previously defined in the
ruleset "java-basic" has now been moved to the "Error Prone" category. The new rule reference to be used is
`<rule ref="category/java/errorprone.xml/JumbledIncrementer"/>`.

The old rulesets like "java-basic" are still kept for backwards-compatibility but will be removed eventually.
The rule reference documentation has been updated to reflect these changes.

#### New Rules

*   The new Java rule `NcssCount` (category `design`) replaces the three rules "NcssConstructorCount", "NcssMethodCount",
    and "NcssTypeCount". The new rule uses the metrics framework to achieve the same. It has two properties, to
    define the report level for method and class sizes separately. Constructors and methods are considered the same.

*   The new Java rule `DoNotExtendJavaLangThrowable` (category `errorprone`) is a companion for the
    `java-strictexception.xml/DoNotExtendJavaLangError`, detecting direct extensions of `java.lang.Throwable`.

*   The new Java rule `ForLoopCanBeForeach` (category `errorprone`) helps to identify those for-loops that can
    be safely refactored into for-each-loops available since java 1.5.

*   The new Java rule `AvoidFileStream` (category `performance`) helps to identify code relying on `FileInputStream` / `FileOutputStream`
    which, by using a finalizer, produces extra / unnecessary overhead to garbage collection, and should be replaced with
    `Files.newInputStream` / `Files.newOutputStream` available since java 1.7.

*   The new Java rule `DataClass` (category `design`) detects simple data-holders without behaviour. This might indicate
    that the behaviour is scattered elsewhere and the data class exposes the internal data structure,
    which breaks encapsulation.

*   The new Apex rule `AvoidDirectAccessTriggerMap` (category `errorprone`) helps to identify direct array access to triggers,
    which can produce bugs by either accessing non-existing indexes, or leaving them out. You should use for-each-loops
    instead.

*   The new Apex rule `AvoidHardcodingId` (category `errorprone`) detects hardcoded strings that look like identifiers
    and flags them. Record IDs change between environments, meaning hardcoded ids are bound to fail under a different
    setup.

*   The new Apex rule `CyclomaticComplexity` (category `design`) detects overly complex classes and methods. The
    report threshold can be configured separately for classes and methods.

*   A whole bunch of new rules has been added to Apex. They all fit into the category `errorprone`.
    The 5 rules are migrated for Apex from the equivalent Java rules and include:
    * `EmptyCatchBlock` to detect catch blocks completely ignoring exceptions.
    * `EmptyIfStmt` for if blocks with no content, that can be safely removed.
    * `EmptyTryOrFinallyBlock` for empty try / finally blocks that can be safely removed.
    * `EmptyWhileStmt` for empty while loops that can be safely removed.
    * `EmptyStatementBlock` for empty code blocks that can be safely removed.

*   The new Apex rule `AvoidSoslInLoops` (category `performance`) is the companion of the old
    `AvoidSoqlInLoops` rule, flagging SOSL (Salesforce Object Search Language) queries when within
    loops, to avoid governor issues, and hitting the database too often.

#### Modified Rules

*   The Java rule `UnnecessaryFinalModifier` (category `codestyle`, former ruleset `java-unnecessarycode`)
    has been merged into the rule `UnnecessaryModifier`. As part of this, the rule  has been revamped to detect more cases.
    It will now flag anonymous class' methods marked as final (can't be overridden, so it's pointless), along with
    final methods overridden / defined within enum instances. It will also flag `final` modifiers on try-with-resources.

*   The Java rule `UnnecessaryParentheses` (category `codestyle`, former ruleset `java-controversial`)
    has been merged into `UselessParentheses` (category `codestyle`, former ruleset `java-unnecessary`).
    The rule covers all scenarios previously covered by either rule.

*   The Java rule `UncommentedEmptyConstructor` (category `documentation`, former ruleset `java-design`)
    will now ignore empty constructors annotated with `javax.inject.Inject`.

*   The Java rule `AbstractClassWithoutAnyMethod` (category `bestpractices`, former ruleset `java-design`)
    will now ignore classes annotated with `com.google.auto.value.AutoValue`.

*   The Java rule `GodClass` (category `design', former ruleset `java-design`) has been revamped to use
    the new metrics framework.

*   The Java rule `LooseCoupling` (category `bestpractices`, former ruleset `java-coupling`) has been
    replaced by the typeresolution-based implementation.

*   The Java rule `CloneMethodMustImplementCloneable` (category `errorprone`, former ruleset `java-clone`)
    has been replaced by the typeresolution-based
    implementation and is now able to detect cases if a class implements or extends a Cloneable class/interface.

*   The Java rule `UnusedImports` (category `bestpractices`, former ruleset `java-imports`) has been
    replaced by the typeresolution-based
    implementation and is now able to detect unused on-demand imports.

*   The Java rule `SignatureDeclareThrowsException` (category `design`, former ruleset 'java-strictexception')
    has been replaced by the
    typeresolution-based implementation. It has a new property `IgnoreJUnitCompletely`, which allows all
    methods in a JUnit testcase to throw exceptions.

*   The Java rule `NPathComplexity` (category `design`, former ruleset `java-codesize`) has been revamped
    to use the new metrics framework.
    Its report threshold can be configured via the property `reportLevel`, which replaces the now
    deprecated property `minimum`.

*   The Java rule `CyclomaticComplexity` (category `design`, former ruleset `java-codesize`) has been
    revamped to use the new metrics framework.
    Its report threshold can be configured via the properties `classReportLevel` and `methodReportLevel` separately.
    The old property `reportLevel`, which configured the level for both total class and method complexity,
    is deprecated.

*   The Java rule `CommentRequired` (category `documentation`, former ruleset `java-comments`)
    has been revamped to include 2 new properties:
    *   `accessorCommentRequirement` to specify documentation requirements for getters and setters (default to `ignored`)
    *   `methodWithOverrideCommentRequirement` to specify documentation requirements for methods annotated with `@Override` (default to `ignored`)

*   The Java rule `EmptyCatchBlock` (category `errorprone`, former ruleset `java-empty`) has been changed to ignore
    exceptions named `ignore` or `expected` by default. You can still override this behaviour by setting the `allowExceptionNameRegex` property.

*   The Java rule `OptimizableToArrayCall` (category `performance`, former ruleset `design`) has been
    modified to fit for the current JVM implementations: It basically detects now the opposite and suggests to
    use `Collection.toArray(new E[0])` with a zero-sized array.
    See [Arrays of Wisdom of the Ancients](https://shipilev.net/blog/2016/arrays-wisdom-ancients/).

#### Deprecated Rules

*   The Java rules `NcssConstructorCount`, `NcssMethodCount`, and `NcssTypeCount` (ruleset `java-codesize`) have been
    deprecated. They will be replaced by the new rule `NcssCount` in the category `design`.

*   The Java rule `LooseCoupling` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name
    from category `bestpractices` instead.

*   The Java rule `CloneMethodMustImplementCloneable` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `errorprone` instead.

*   The Java rule `UnusedImports` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `bestpractices` instead.

*   The Java rule `SignatureDeclareThrowsException` in ruleset `java-typeresolution` is deprecated. Use the rule
    with the same name from category `design` instead.

*   The Java rule `EmptyStaticInitializer` in ruleset `java-empty` is deprecated. Use the rule `EmptyInitializer`
    from the category `errorprone`, which covers both static and non-static empty initializers.`

*   The Java rules `GuardDebugLogging` (ruleset `java-logging-jakarta-commons`) and `GuardLogStatementJavaUtil`
    (ruleset `java-logging-java`) have been deprecated. Use the rule `GuardLogStatement` from the
    category `bestpractices`, which covers all cases regardless of the logging framework.

#### Removed Rules

*   The deprecated Java rule `UseSingleton` has been removed from the ruleset `java-design`. The rule has been renamed
    long time ago to `UseUtilityClass` (category `design`).

#### Java Symbol Table

A [bug in symbol table](https://github.com/pmd/pmd/pull/549/commits/0958621ca884a8002012fc7738308c8dfc24b97c) prevented
the symbol table analysis to properly match primitive arrays types. The issue [affected the `java-unsedcode/UnusedPrivateMethod`](https://github.com/pmd/pmd/issues/521)
rule, but other rules may now produce improved results as consequence of this fix.

#### Apex Parser Update

The Apex parser version was bumped, from `1.0-sfdc-187` to `210-SNAPSHOT`. This update let us take full advantage
of the latest improvements from Salesforce, but introduces some breaking changes:

*   `BlockStatements` are now created for all control structures, even if no brace is used. We have therefore added
    a `hasCurlyBrace` method to differentiate between both scenarios.
*   New AST node types are available. In particular `CastExpression`, `ConstructorPreamble`, `IllegalStoreExpression`,
    `MethodBlockStatement`, `Modifier`, `MultiStatement`, `NestedExpression`, `NestedStoreExpression`,
    `NewKeyValueObjectExpression` and `StatementExecuted`
*   Some nodes have been removed. Such is the case of `TestNode`, `DottedExpression` and `NewNameValueObjectExpression`
    (replaced by `NewKeyValueObjectExpression`)

All existing rules have been updated to reflect these changes. If you have custom rules, be sure to update them.

For more info about the included Apex parser, see the new pmd module "pmd-apex-jorje", which packages and provides
the parser as a binary.

#### Incremental Analysis

The incremental analysis feature first introduced in PMD 5.6.0 has been enhanced. A few minor issues have been fixed,
and several improvements have been performed to make it more accurate.

The cache will now detect changes to the JARs referenced in the `auxclasspath` instead of simply looking at their paths
and order. This means that if you are referencing a JAR you are overwriting in some way, the incremental analysis can
now detect it and invalidate it's cache to avoid false reports.

Similarly, any changes to the execution classpath of PMD will invalidate the cache. This means that if you have custom
rules packaged in a jar, any changes to it will invalidate the cache automatically.

We have also improved logging on the analysis code, allowing better insight into how the cache is performing,
under debug / verbose builds you can even see individual hits / misses to the cache (and the reason for any miss!)

Finally, as this feature keeps maturing, we are gently pushing this forward. If not using incremental analysis,
a warning will now be produced suggesting users to adopt it for better performance.

#### Rule and Report Properties

The implementation around the properties support for rule properties and report properties has been revamped
to be fully typesafe. Along with that change, the support classes have been moved into an own
package `net.sourceforge.pmd.properties`. While there is no change necessary in the ruleset XML files,
when using/setting values for rules, there are adjustments necessary when declaring properties in Java-implemented
rules.

Rule properties can be declared both for Java based rules and XPath rules.
This is now very well documented in [Working with properties](pmd_userdocs_extending_defining_properties.html).

With PMD 6.0.0, multivalued properties are now also possible with XPath rules.

### Fixed Issues

*   all
    *   [#394](https://github.com/pmd/pmd/issues/394): \[core] PMD exclude rules are failing with IllegalArgumentException with non-default minimumPriority
    *   [#532](https://github.com/pmd/pmd/issues/532): \[core] security concerns on URL-based rulesets
    *   [#538](https://github.com/pmd/pmd/issues/538): \[core] Provide an XML Schema for XML reports
    *   [#600](https://github.com/pmd/pmd/issues/600): \[core] Nullpointer while creating cache File
    *   [#604](https://github.com/pmd/pmd/issues/604): \[core] Incremental analysis should detect changes to jars in classpath
    *   [#608](https://github.com/pmd/pmd/issues/608): \[core] Add DEBUG log when applying incremental analysis
    *   [#618](https://github.com/pmd/pmd/issues/618): \[core] Incremental Analysis doesn't close file correctly on Windows upon a cache hit
    *   [#643](https://github.com/pmd/pmd/issues/643): \[core] PMD Properties (dev-properties) breaks markup on CodeClimateRenderer
    *   [#680](https://github.com/pmd/pmd/pull/680): \[core] Isolate classloaders for runtime and auxclasspath
    *   [#762](https://github.com/pmd/pmd/issues/762): \[core] Remove method and file property from available property descriptors for XPath rules
    *   [#763](https://github.com/pmd/pmd/issues/763): \[core] Turn property descriptor util into an enum and enrich its interface
*   apex
    *   [#265](https://github.com/pmd/pmd/issues/265): \[apex] Make Rule suppression work
    *   [#488](https://github.com/pmd/pmd/pull/488): \[apex] Use Apex lexer for CPD
    *   [#489](https://github.com/pmd/pmd/pull/489): \[apex] Update Apex compiler
    *   [#500](https://github.com/pmd/pmd/issues/500): \[apex] Running through CLI shows jorje optimization messages
    *   [#605](https://github.com/pmd/pmd/issues/605): \[apex] java.lang.NoClassDefFoundError in the latest build
    *   [#637](https://github.com/pmd/pmd/issues/637): \[apex] Avoid SOSL in loops
    *   [#760](https://github.com/pmd/pmd/issues/760): \[apex] EmptyStatementBlock complains about missing rather than empty block
    *   [#766](https://github.com/pmd/pmd/issues/766): \[apex] Replace old Jorje parser with new one
    *   [#768](https://github.com/pmd/pmd/issues/768): \[apex] java.lang.NullPointerException from PMD
*   cpp
    *   [#448](https://github.com/pmd/pmd/issues/448): \[cpp] Write custom CharStream to handle continuation characters
*   java
    *   [#1454](https://sourceforge.net/p/pmd/bugs/1454/): \[java] OptimizableToArrayCall is outdated and invalid in current JVMs
    *   [#1513](https://sourceforge.net/p/pmd/bugs/1513/): \[java] Remove deprecated rule UseSingleton
    *   [#328](https://github.com/pmd/pmd/issues/328): \[java] java.lang.ClassFormatError: Absent Code attribute in method that is not native or abstract in class file javax/servlet/jsp/PageContext
    *   [#487](https://github.com/pmd/pmd/pull/487): \[java] Fix typeresolution for anonymous extending object
    *   [#496](https://github.com/pmd/pmd/issues/496): \[java] processing error on generics inherited from enclosing class
    *   [#510](https://github.com/pmd/pmd/issues/510): \[java] Typeresolution fails on a simple primary when the source is loaded from a class literal
    *   [#527](https://github.com/pmd/pmd/issues/527): \[java] Lombok getter annotation on enum is not recognized correctly
    *   [#534](https://github.com/pmd/pmd/issues/534): \[java] NPE in MethodTypeResolution for static methods
    *   [#603](https://github.com/pmd/pmd/issues/603): \[core] incremental analysis should invalidate upon Java rule plugin changes
    *   [#650](https://github.com/pmd/pmd/issues/650): \[java] ProcesingError analyzing code under 5.8.1
    *   [#732](https://github.com/pmd/pmd/issues/732): \[java] LinkageError with aux classpath
*   java-basic
    *   [#565](https://github.com/pmd/pmd/pull/565): \[java] False negative on DontCallThreadRun when extending Thread
*   java-comments
    *   [#396](https://github.com/pmd/pmd/issues/396): \[java] CommentRequired: add properties to ignore @Override method and getters / setters
    *   [#536](https://github.com/pmd/pmd/issues/536): \[java] CommentDefaultAccessModifierRule ignores constructors
*   java-controversial
    *   [#388](https://github.com/pmd/pmd/issues/388): \[java] controversial.AvoidLiteralsInIfCondition 0.0 false positive
    *   [#408](https://github.com/pmd/pmd/issues/408): \[java] DFA not analyzing asserts
    *   [#537](https://github.com/pmd/pmd/issues/537): \[java] UnnecessaryParentheses fails to detect obvious scenario
*   java-design
    *   [#357](https://github.com/pmd/pmd/issues/357): \[java] UncommentedEmptyConstructor consider annotations on Constructor
    *   [#438](https://github.com/pmd/pmd/issues/438): \[java] Relax AbstractClassWithoutAnyMethod when class is annotated by @AutoValue
    *   [#590](https://github.com/pmd/pmd/issues/590): \[java] False positive on MissingStaticMethodInNonInstantiatableClass
*    java-logging
     *   [#457](https://github.com/pmd/pmd/issues/457): \[java] Merge all log guarding rules
     *   [#721](https://github.com/pmd/pmd/issues/721): \[java] NPE in PMD 5.8.1 InvalidSlf4jMessageFormat
*   java-sunsecure
    *   [#468](https://github.com/pmd/pmd/issues/468): \[java] ArrayIsStoredDirectly false positive
*   java-unusedcode
    *   [#521](https://github.com/pmd/pmd/issues/521): \[java] UnusedPrivateMethod returns false positives with primitive data type in map argument
*   java-unnecessarycode
    *   [#412](https://github.com/pmd/pmd/issues/412): \[java] java-unnecessarycode/UnnecessaryFinalModifier missing cases
    *   [#676](https://github.com/pmd/pmd/issues/676): \[java] java-unnecessarycode/UnnecessaryFinalModifier on try-with-resources

### API Changes

*   The class `net.sourceforge.pmd.lang.dfa.NodeType` has been converted to an enum.
    All node types are enum members now instead of int constants. The names for node types are retained.

*   The *Properties API* (rule and report properties) has been revamped to be fully typesafe. This is everything
    around `net.sourceforge.pmd.properties.PropertyDescriptor`.

    Note: All classes related to properties have been moved into the package `net.sourceforge.pmd.properties`.

*   The rule classes `net.sourceforge.pmd.lang.apex.rule.apexunit.ApexUnitTestClassShouldHaveAsserts`
    and `net.sourceforge.pmd.lang.apex.rule.apexunit.ApexUnitTestShouldNotUseSeeAllDataTrue` have been
    renamed to `ApexUnitTestClassShouldHaveAssertsRule` and `ApexUnitTestShouldNotUseSeeAllDataTrueRule`,
    respectively. This is to comply with the naming convention, that each rule class should be suffixed with "Rule".

    This change has no impact on custom rulesets, since the rule names themselves didn't change.

*   The never implemented method `PMD.processFiles(PMDConfiguration, RuleSetFactory, Collection<File>, RuleContext, ProgressMonitor)` along with the interface `ProgressMonitor` has been removed.

*   The method `PMD.setupReport(RuleSets, RuleContext, String)` is gone. It was used to report dysfunctional
    rules. But PMD does this now automatically before processing the files, so there is no need for this
    method anymore.

*   All APIs deprecated in older versions are now removed. This includes:
    *    `Renderer.getPropertyDefinitions`
    *    `AbstractRenderer.defineProperty(String, String)`
    *    `AbstractRenderer.propertyDefinitions`
    *    `ReportListener`
    *    `Report.addListener(ReportListener)`
    *    `SynchronizedReportListener`
    *    `CPDConfiguration.CPDConfiguration(int, Language, String)`
    *    `CPDConfiguration.getRendererFromString(String)`
    *    `StreamUtil`
    *    `StringUtil.appendXmlEscaped(StringBuilder, String)`
    *    `StringUtil.htmlEncode(String)`


*   Several methods in `net.sourceforge.pmd.util.CollectionUtil` have been deprecated and will be removed in PMD 7.0.0. In particular:
    *    `CollectionUtil.addWithoutDuplicates(T[], T)`
    *    `CollectionUtil.addWithoutDuplicates(T[], T[])`
    *    `CollectionUtil.areSemanticEquals(T[], T[])`
    *    `CollectionUtil.areEqual(Object, Object)`
    *    `CollectionUtil.arraysAreEqual(Object, Object)`
    *    `CollectionUtil.valuesAreTransitivelyEqual(Object[], Object[])`


*   Several methods in `net.sourceforge.pmd.util.StringUtil` have been deprecated and will be removed in PMD 7.0.0. In particular:
    *    `StringUtil.startsWithAny(String, String[])`
    *    `StringUtil.isNotEmpty(String)`
    *    `StringUtil.isEmpty(String)`
    *    `StringUtil.isMissing(String)`
    *    `StringUtil.areSemanticEquals(String, String)`
    *    `StringUtil.replaceString(String, String, String)`
    *    `StringUtil.replaceString(String, char, String)`
    *    `StringUtil.substringsOf(String, char)`
    *    `StringUtil.substringsOf(String, String)`
    *    `StringUtil.asStringOn(StringBuffer, Iterator, String)`
    *    `StringUtil.asStringOn(StringBuilder, Object[], String)`
    *    `StringUtil.lpad(String, int)`

*   The class `net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition` is now abstract, and has been enhanced
    to provide several new methods.

*   The constructor of `net.sourceforge.pmd.RuleSetFactory`, which took a `ClassLoader` is deprecated.
    Please use the alternative constructor with the `net.sourceforge.pmd.util.ResourceLoader` instead.

*   The following GUI related classes have been deprecated and will be removed in PMD 7.0.0.
    The tool "bgastviewer", that could be started via the script `bgastviewer.bat` or `run.sh bgastviewer` is
    deprecated, too, and will be removed in PMD 7.0.0.
    Both the "old designer" and "bgastviewer" are replaced by the [New Rule Designer](#new-rule-designer).
    *   `net.sourceforge.pmd.util.designer.CodeEditorTextPane`
    *   `net.sourceforge.pmd.util.designer.CreateXMLRulePanel`
    *   `net.sourceforge.pmd.util.designer.Designer`
    *   `net.sourceforge.pmd.util.designer.DFAPanel`
    *   `net.sourceforge.pmd.util.designer.LineGetter`
    *   `net.sourceforge.pmd.util.viewer.Viewer`
    *   `net.sourceforge.pmd.util.viewer.gui.ActionCommands`
    *   `net.sourceforge.pmd.util.viewer.gui.ASTPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.EvaluationResultsPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.MainFrame`
    *   `net.sourceforge.pmd.util.viewer.gui.ParseExceptionHandler`
    *   `net.sourceforge.pmd.util.viewer.gui.SourceCodePanel`
    *   `net.sourceforge.pmd.util.viewer.gui.XPathPanel`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.ASTNodePopupMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.AttributesSubMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.SimpleNodeSubMenu`
    *   `net.sourceforge.pmd.util.viewer.gui.menu.XPathFragmentAddingItem`
    *   `net.sourceforge.pmd.util.viewer.model.ASTModel`
    *   `net.sourceforge.pmd.util.viewer.model.AttributeToolkit`
    *   `net.sourceforge.pmd.util.viewer.model.SimpleNodeTreeNodeAdapter`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModel`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModelEvent`
    *   `net.sourceforge.pmd.util.viewer.model.ViewerModelListener`
    *   `net.sourceforge.pmd.util.viewer.util.NLS`

*   The following methods in `net.sourceforge.pmd.Rule` have been deprecated and will be removed in PMD 7.0.0.
    All methods are replaced by their bean-like counterparts
    *   `void setUsesDFA()`. Use `void setDfa(boolean)` instead.
    *   `boolean usesDFA()`. Use `boolean isDfa()` instead.
    *   `void setUsesTypeResolution()`. Use `void setTypeResolution(boolean)` instead.
    *   `boolean usesTypeResolution()`. Use `boolean isTypeResolution()` instead.
    *   `void setUsesMultifile()`. Use `void setMultifile(boolean)` instead.
    *   `boolean usesMultifile()`. Use `boolean isMultifile()` instead.
    *   `boolean usesRuleChain()`. Use `boolean isRuleChain()` instead.

### External Contributions

*   [#287](https://github.com/pmd/pmd/pull/287): \[apex] Make Rule suppression work - [Robert Sösemann](https://github.com/up2go-rsoesemann)
*   [#420](https://github.com/pmd/pmd/pull/420): \[java] Fix UR anomaly in assert statements - [Clément Fournier](https://github.com/oowekyala)
*   [#482](https://github.com/pmd/pmd/pull/482): \[java] Metrics testing framework + improved capabilities for metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#484](https://github.com/pmd/pmd/pull/484): \[core] Changed linux usage to a more unix like path - [patriksevallius](https://github.com/patriksevallius)
*   [#486](https://github.com/pmd/pmd/pull/486): \[java] Add basic method typeresolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#492](https://github.com/pmd/pmd/pull/492): \[java] Typeresolution for overloaded methods - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#495](https://github.com/pmd/pmd/pull/495): \[core] Custom rule reinitialization code - [Clément Fournier](https://github.com/oowekyala)
*   [#479](https://github.com/pmd/pmd/pull/479): \[core] Typesafe and immutable properties - [Clément Fournier](https://github.com/oowekyala)
*   [#499](https://github.com/pmd/pmd/pull/499): \[java] Metrics memoization tests - [Clément Fournier](https://github.com/oowekyala)
*   [#501](https://github.com/pmd/pmd/pull/501): \[java] Add support for most specific vararg method type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#502](https://github.com/pmd/pmd/pull/502): \[java] Add support for static field type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#505](https://github.com/pmd/pmd/pull/505): \[java] Followup on metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#506](https://github.com/pmd/pmd/pull/506): \[java] Add reduction rules to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#511](https://github.com/pmd/pmd/pull/511): \[core] Prepare abstraction of the metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#512](https://github.com/pmd/pmd/pull/512): \[java] Add incorporation to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#513](https://github.com/pmd/pmd/pull/513): \[java] Fix for maximally specific method selection - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#514](https://github.com/pmd/pmd/pull/514): \[java] Add static method type resolution - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#517](https://github.com/pmd/pmd/pull/517): \[doc] Metrics documentation - [Clément Fournier](https://github.com/oowekyala)
*   [#518](https://github.com/pmd/pmd/pull/518): \[core] Properties refactoring: factorized enumerated property - [Clément Fournier](https://github.com/oowekyala)
*   [#523](https://github.com/pmd/pmd/pull/523): \[java] Npath complexity metric and rule - [Clément Fournier](https://github.com/oowekyala)
*   [#524](https://github.com/pmd/pmd/pull/524): \[java] Add support for explicit type arguments with method invocation - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#525](https://github.com/pmd/pmd/pull/525): \[core] Fix line ending and not ignored files issues - [Matias Comercio](https://github.com/MatiasComercio)
*   [#528](https://github.com/pmd/pmd/pull/528): \[core] Fix typo - [Ayoub Kaanich](https://github.com/kayoub5)
*   [#529](https://github.com/pmd/pmd/pull/529): \[java] Abstracted the Java metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#530](https://github.com/pmd/pmd/pull/530): \[java] Fix issue #527: Lombok getter annotation on enum is not recognized correctly - [Clément Fournier](https://github.com/oowekyala)
*   [#533](https://github.com/pmd/pmd/pull/533): \[core] improve error message - [Dennis Kieselhorst](https://github.com/deki)
*   [#535](https://github.com/pmd/pmd/pull/535): \[apex] Fix broken Apex visitor adapter - [Clément Fournier](https://github.com/oowekyala)
*   [#542](https://github.com/pmd/pmd/pull/542): \[java] Metrics abstraction - [Clément Fournier](https://github.com/oowekyala)
*   [#545](https://github.com/pmd/pmd/pull/545): \[apex] Apex metrics framework - [Clément Fournier](https://github.com/oowekyala)
*   [#548](https://github.com/pmd/pmd/pull/548): \[java] Metrics documentation - [Clément Fournier](https://github.com/oowekyala)
*   [#550](https://github.com/pmd/pmd/pull/550): \[java] Add basic resolution to type inference - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#553](https://github.com/pmd/pmd/pull/553): \[java] Refactored ParserTst into a static utility class + add getSourceFromClass - [Clément Fournier](https://github.com/oowekyala)
*   [#554](https://github.com/pmd/pmd/pull/554): \[java] Fix #537: UnnecessaryParentheses fails to detect obvious scenario - [Clément Fournier](https://github.com/oowekyala)
*   [#555](https://github.com/pmd/pmd/pull/555): \[java] Changed metrics/CyclomaticComplexityRule to use WMC when reporting classes - [Clément Fournier](https://github.com/oowekyala)
*   [#556](https://github.com/pmd/pmd/pull/556): \[java] Fix #357: UncommentedEmptyConstructor consider annotations on Constructor - [Clément Fournier](https://github.com/oowekyala)
*   [#557](https://github.com/pmd/pmd/pull/557): \[java] Fix NPath metric not counting ternaries correctly - [Clément Fournier](https://github.com/oowekyala)
*   [#563](https://github.com/pmd/pmd/pull/563): \[java] Add support for basic method type inference for strict invocation - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#566](https://github.com/pmd/pmd/pull/566): \[java] New rule in migrating ruleset: ForLoopCanBeForeach - [Clément Fournier](https://github.com/oowekyala)
*   [#567](https://github.com/pmd/pmd/pull/567): \[java] Last API change for metrics (metric options) - [Clément Fournier](https://github.com/oowekyala)
*   [#570](https://github.com/pmd/pmd/pull/570): \[java] Model lower, upper and intersection types - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#573](https://github.com/pmd/pmd/pull/573): \[java] Data class rule - [Clément Fournier](https://github.com/oowekyala)
*   [#576](https://github.com/pmd/pmd/pull/576): \[doc]\[java] Add hint for Guava users in InefficientEmptyStringCheck - [mmoehring](https://github.com/mmoehring)
*   [#578](https://github.com/pmd/pmd/pull/578): \[java] Refactored god class rule - [Clément Fournier](https://github.com/oowekyala)
*   [#579](https://github.com/pmd/pmd/pull/579): \[java] Update parsing to produce upper and lower bounds - [Bendegúz Nagy](https://github.com/WinterGrascph)
*   [#580](https://github.com/pmd/pmd/pull/580): \[core] Add AbstractMetric to topple the class hierarchy of metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#581](https://github.com/pmd/pmd/pull/581): \[java] Relax AbstractClassWithoutAnyMethod when class is annotated by @AutoValue - [Niklas Baudy](https://github.com/vanniktech)
*   [#583](https://github.com/pmd/pmd/pull/583): \[java] Documentation about writing metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#585](https://github.com/pmd/pmd/pull/585): \[java] Moved NcssCountRule to codesize.xml - [Clément Fournier](https://github.com/oowekyala)
*   [#587](https://github.com/pmd/pmd/pull/587): \[core] Properties refactoring: Move static constants of ValueParser to class ValueParserConstants - [Clément Fournier](https://github.com/oowekyala)
*   [#588](https://github.com/pmd/pmd/pull/588): \[java] XPath function to compute metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#598](https://github.com/pmd/pmd/pull/598): \[java] Fix #388: controversial.AvoidLiteralsInIfCondition 0.0 false positive - [Clément Fournier](https://github.com/oowekyala)
*   [#602](https://github.com/pmd/pmd/pull/602): \[java] \[apex] Separate multifile analysis from metrics - [Clément Fournier](https://github.com/oowekyala)
*   [#620](https://github.com/pmd/pmd/pull/620): \[core] Moved properties to n.s.pmd.properties - [Clément Fournier](https://github.com/oowekyala)
*   [#625](https://github.com/pmd/pmd/pull/625): \[apex] empty code ruleset for apex - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#632](https://github.com/pmd/pmd/pull/632): \[apex] Add AvoidDirectAccessTriggerMap rule to the style set - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#644](https://github.com/pmd/pmd/pull/644): \[core] Prevent internal dev-properties from being displayed on CodeClimate renderer - [Filipe Esperandio](https://github.com/filipesperandio)
*   [#660](https://github.com/pmd/pmd/pull/660): \[apex] avoid sosl in loops - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#661](https://github.com/pmd/pmd/pull/661): \[apex] avoid hardcoding id's - [Jan Aertgeerts](https://github.com/JAertgeerts)
*   [#666](https://github.com/pmd/pmd/pull/666): \[java] Add DoNotExtendJavaLangThrowable rule - [Robert Painsi](https://github.com/robertpainsi)
*   [#668](https://github.com/pmd/pmd/pull/668): \[core] Fix javadoc warnings on pmd-core - [Clément Fournier](https://github.com/oowekyala)
*   [#669](https://github.com/pmd/pmd/pull/669): \[core] Builder pattern for properties - [Clément Fournier](https://github.com/oowekyala)
*   [#675](https://github.com/pmd/pmd/pull/675): \[java] Fix in Java grammar: Try with final resource node error - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#679](https://github.com/pmd/pmd/pull/679): \[core] Token scheme generalization - [Gonzalo Ibars Ingman](https://github.com/gibarsin)
*   [#694](https://github.com/pmd/pmd/pull/694): \[core] Add minor fixes to root pom - [Matias Comercio](https://github.com/MatiasComercio)
*   [#696](https://github.com/pmd/pmd/pull/696): \[core] Add remove operation over nodes - [Matias Comercio](https://github.com/MatiasComercio)
*   [#711](https://github.com/pmd/pmd/pull/711): \[ui] New rule designer - [Clément Fournier](https://github.com/oowekyala)
*   [#722](https://github.com/pmd/pmd/pull/722): \[java] Move NPathComplexity from metrics to design - [Clément Fournier](https://github.com/oowekyala)
*   [#723](https://github.com/pmd/pmd/pull/723): \[core] Rule factory refactoring - [Clément Fournier](https://github.com/oowekyala)
*   [#726](https://github.com/pmd/pmd/pull/726): \[java] Fix issue #721 (NPE in InvalidSlf4jMessageFormat) - [Clément Fournier](https://github.com/oowekyala)
*   [#727](https://github.com/pmd/pmd/pull/727): \[core] Fix #725: numeric property descriptors now check their default value - [Clément Fournier](https://github.com/oowekyala)
*   [#733](https://github.com/pmd/pmd/pull/733): \[java] Some improvements to CommentRequired - [Clément Fournier](https://github.com/oowekyala)
*   [#734](https://github.com/pmd/pmd/pull/734): \[java] Move CyclomaticComplexity from metrics to design - [Clément Fournier](https://github.com/oowekyala)
*   [#736](https://github.com/pmd/pmd/pull/736): \[core] Make Saxon support multi valued XPath properties - [Clément Fournier](https://github.com/oowekyala)
*   [#737](https://github.com/pmd/pmd/pull/737): \[doc] Fix NPathComplexity documentation bad rendering - [Clément Fournier](https://github.com/oowekyala)
*   [#744](https://github.com/pmd/pmd/pull/744): \[doc] Added Apex to supported languages - [Michał Kuliński](https://github.com/coola)
*   [#746](https://github.com/pmd/pmd/pull/746): \[doc] Fix typo in incremental analysis log message - [Clément Fournier](https://github.com/oowekyala)
*   [#749](https://github.com/pmd/pmd/pull/749): \[doc] Update the documentation for properties - [Clément Fournier](https://github.com/oowekyala)
*   [#758](https://github.com/pmd/pmd/pull/758): \[core] Expose the full mapping from property type id to property extractor - [Clément Fournier](https://github.com/oowekyala)
*   [#764](https://github.com/pmd/pmd/pull/764): \[core] Prevent method and file property use in XPath rules - [Clément Fournier](https://github.com/oowekyala)
*   [#771](https://github.com/pmd/pmd/pull/771): \[apex] Fix Apex metrics framework failing on triggers, refs #768 - [Clément Fournier](https://github.com/oowekyala)
*   [#774](https://github.com/pmd/pmd/pull/774): \[java] Avoid using FileInput/Output - see JDK-8080225 - [Chas Honton](https://github.com/chonton)
