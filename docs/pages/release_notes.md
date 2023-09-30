---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date }} - {{ site.pmd.version }}

We're excited to bring you the next major version of PMD!

Since this is a big release, we provide here only a concise version of the release notes. We prepared a separate
page with the full [Detailed Release Notes for PMD 7.0.0]({{ baseurl }}pmd_release_notes_pmd7.html).

<div style="border: 1px solid; border-radius: .25rem; padding: .75rem 1.25rem;" role="alert">
<strong>ℹ️ Release Candidates</strong>
<p>PMD 7.0.0 is finally almost ready. In order to gather feedback, we are going to ship a couple of release candidates.
These are officially available on GitHub and Maven Central and can be used as usual (e.g. as a dependency).
We encourage you to try out the new features, but keep in mind that we may introduce API breaking changes between
the release candidates. It should be stable enough if you don't use custom rules.</p>

<p>We have still some tasks planned for the next release candidates.
You can see the progress in <a href="https://github.com/pmd/pmd/issues/3898">PMD 7 Tracking Issue #3898</a>.</p>

<p>If you find any problem or difficulty while updating from PMD 6, please provide feedback via our
<a href="https://github.com/pmd/pmd/issues/new/choose">issue tracker</a>. That way we can improve the experience
for all.</p>
</div>

{% tocmaker is_release_notes_processor %}

### Changes since 7.0.0-rc3

This section lists the most important changes from the last release candidate.
The remaining section describes the complete release notes for 7.0.0.

#### New and Noteworthy

##### Migration Guide for PMD 7

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html).

##### Apex Jorje Updated

With the new version of Apex Jorje, the new language constructs like User Mode Database Operations
can be parsed now. PMD should now be able to parse Apex code up to version 59.0 (Winter '23).

##### Java 21 Support

This release of PMD brings support for Java 21. There are the following new standard language features,
that are supported now:

* [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

PMD also supports the following preview language features:

* [JEP 430: String Templates (Preview)](https://openjdk.org/jeps/430)
* [JEP 443: Unnamed Patterns and Variables (Preview)](https://openjdk.org/jeps/443)
* [JEP 445: Unnamed Classes and Instance Main Methods (Preview)](https://openjdk.org/jeps/445)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `21-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-21-preview ...

Note: Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

#### Fixed issues

* miscellaneous
  * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
  * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
* core
  * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
  * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
  * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
  * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
  * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
  * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
  * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
  * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
  * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
* cli
  * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
* doc
  * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
  * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
  * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
* apex
  * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
  * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
  * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-security
  * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* java
  * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
  * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
* java-bestpractices
  * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG

#### API Changes

##### pmd-java

* Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

##### Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

##### New Programmatic API for CPD

See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#new-programmatic-api-for-cpd)
and [PR #4397](https://github.com/pmd/pmd/pull/4397) for details.

##### Removed classes and methods

The following previously deprecated classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractTokenizer` ➡️ use {%jdoc core::cpd.AnyTokenizer %} instead
  * `net.sourceforge.pmd.cpd.CPD` ➡️ use {% jdoc cli::cli.PmdCli %} from `pmd-cli` module for CLI support or use
    {%jdoc core::cpd.CpdAnalysis %} for programmatic API
  * `net.sourceforge.pmd.cpd.GridBagHelper` (now package private)
  * `net.sourceforge.pmd.cpd.TokenEntry.State`
  * `net.sourceforge.pmd.lang.document.CpdCompat`
  * `net.sourceforge.pmd.properties.BooleanMultiProperty`
  * `net.sourceforge.pmd.properties.BooleanProperty`
  * `net.sourceforge.pmd.properties.CharacterMultiProperty`
  * `net.sourceforge.pmd.properties.CharacterProperty`
  * `net.sourceforge.pmd.properties.DoubleMultiProperty`
  * `net.sourceforge.pmd.properties.DoubleProperty`
  * `net.sourceforge.pmd.properties.EnumeratedMultiProperty`
  * `net.sourceforge.pmd.properties.EnumeratedProperty`
  * `net.sourceforge.pmd.properties.EnumeratedPropertyDescriptor`
  * `net.sourceforge.pmd.properties.FileProperty` (note: without replacement)
  * `net.sourceforge.pmd.properties.FloatMultiProperty`
  * `net.sourceforge.pmd.properties.FloatProperty`
  * `net.sourceforge.pmd.properties.IntegerMultiProperty`
  * `net.sourceforge.pmd.properties.IntegerProperty`
  * `net.sourceforge.pmd.properties.LongMultiProperty`
  * `net.sourceforge.pmd.properties.LongProperty`
  * `net.sourceforge.pmd.properties.MultiValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.NumericPropertyDescriptor`
  * `net.sourceforge.pmd.properties.PropertyDescriptorField`
  * `net.sourceforge.pmd.properties.RegexProperty`
  * `net.sourceforge.pmd.properties.SingleValuePropertyDescriptor`
  * `net.sourceforge.pmd.properties.StringMultiProperty`
  * `net.sourceforge.pmd.properties.StringProperty`
  * `net.sourceforge.pmd.properties.ValueParser`
  * `net.sourceforge.pmd.properties.ValueParserConstants`
  * `net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiPackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper`
  * `net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SinglePackagedPropertyBuilder`
  * `net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder`
  * `net.sourceforge.pmd.properties.modules.EnumeratedPropertyModule`
  * `net.sourceforge.pmd.properties.modules.NumericPropertyModule`

The following previously deprecated methods have been removed:

* pmd-core
  * `net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder#delim(char)`
  * `net.sourceforge.pmd.properties.PropertySource#setProperty(...)`
  * `net.sourceforge.pmd.properties.PropertyTypeId#factoryFor(...)`
  * `net.sourceforge.pmd.properties.PropertyTypeId#typeIdFor(...)`
  * `net.sourceforge.pmd.properties.PropertyDescriptor`: removed methods errorFor, type, isMultiValue,
     uiOrder, compareTo, isDefinedExternally, valueFrom, asDelimitedString

The following methods have been removed:

* pmd-core
  * {%jdoc core::cpd.CPDConfiguration %}
    * `#sourceCodeFor(File)`, `#postConstruct()`, `#tokenizer()`, `#filenameFilter()` removed
  * {%jdoc core::cpd.Mark %}
    * `#getSourceSlice()`, `#setLineCount(int)`, `#getLineCount()`, `#setSourceCode(SourceCode)` removed
    * `#getBeginColumn()`, `#getBeginLine()`, `#getEndLine()`, `#getEndColumn()` removed
      ➡️ use {%jdoc core::cpd.Mark#getLocation() %} instead
  * {%jdoc core::cpd.Match %}
    * `#LABEL_COMPARATOR` removed
    * `#setMarkSet(...)`, `#setLabel(...)`, `#getLabel()`, `#addTokenEntry(...)` removed
    * `#getSourceCodeSlice()` removed
      ➡️ use {%jdoc !!core::cpd.CPDReport#getSourceCodeSlice(net.sourceforge.pmd.cpd.Mark) %} instead
  * {%jdoc core::cpd.TokenEntry %}
    * `#getEOF()`, `#clearImages()`, `#getIdentifier()`, `#getIndex()`, `#setHashCode(int)` removed
    * `#EOF` removed ➡️ use {%jdoc core::cpd.TokenEntry#isEof() %} instead
  * {%jdoc core::lang.ast.Parser.ParserTask %}
    * `#getFileDisplayName()` removed ➡️ use {%jdoc core::lang.ast.Parser.ParserTask#getFileId() %} instead
      (`getFileId().getAbsolutePath()`)

The following classes have been removed:

* pmd-core
  * `net.sourceforge.pmd.cpd.AbstractLanguage`
  * `net.sourceforge.pmd.cpd.AnyLanguage`
  * `net.sourceforge.pmd.cpd.Language`
  * `net.sourceforge.pmd.cpd.LanguageFactory`
  * `net.sourceforge.pmd.cpd.MatchAlgorithm` (now package private)
  * `net.sourceforge.pmd.cpd.MatchCollector` (now package private)
  * `net.sourceforge.pmd.cpd.SourceCode` (and all inner classes like `FileCodeLoader`, ...)
  * `net.sourceforge.pmd.cpd.token.TokenFilter`

##### Moved packages

* pmd-core
  * {%jdoc core::net.sourceforge.pmd.properties.NumericConstraints %} (old package: `net.sourceforge.pmd.properties.constraints.NumericConstraints`)
  * {%jdoc core::net.sourceforge.pmd.properties.PropertyConstraint %} (old package: `net.sourceforge.pmd.properties.constraints.PropertyConstraint`)
    * not experimental anymore
  * {%jdoc ant::ant.ReportException %} (old package: `net.sourceforge.pmd.cpd`, moved to module `pmd-ant`)
    * it is now a RuntimeException
  * {%jdoc core::cpd.CPDReportRenderer %} (old package: `net.sourceforge.pmd.cpd.renderer`)
  * {%jdoc core::cpd.impl.AntlrTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token`)
  * {%jdoc core::cpd.impl.BaseTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token.internal`)
  * {%jdoc core::cpd.impl.JavaCCTokenFilter %} (old package: `net.sourceforge.pmd.cpd.token`)

##### Changed types and other changes

* pmd-core
  * {%jdoc core::net.sourceforge.pmd.properties.PropertyDescriptor %} is now a class (was an interface)
    and it is not comparable anymore.
  * {%jdoc !!core::AbstractConfiguration#setSourceEncoding(java.nio.charset.Charset) %}
    * previously this method took a simple String for the encoding.
  * {%jdoc core::PmdConfiguration %} and {%jdoc core::cpd.CPDConfiguration %}
    * many getters and setters have been moved to the parent class {%jdoc core::AbstractConfiguration %}
  * {%jdoc !!core::cpd.CPDListener#addedFile(int) %}
    * no `File` parameter anymore
  * {%jdoc !!core::cpd.CPDReport#getNumberOfTokensPerFile() %} returns a `Map` of `FileId,Integer` instead of `String`
  * {%jdoc !!core::cpd.CPDReport#filterMatches(java.util.function.Predicate) %} now takes a `java.util.function.Predicate`
    as parameter
  * {%jdoc core::cpd.Tokenizer %}
    * constants are now {%jdoc core::properties.PropertyDescriptor %} instead of `String`,
      to be used as language properties
    * {%jdoc core::cpd.Tokenizer#tokenize(net.sourceforge.pmd.lang.document.TextDocument, net.sourceforge.pmd.cpd.TokenFactory) %}
      changed parameters. Now takes a {%jdoc core::lang.document.TextDocument %} and a {%jdoc core::cpd.TokenFactory %}
      (instead of `SourceCode` and `Tokens`)
  * {% jdoc core::lang.Language %}
    * method `#createProcessor(LanguagePropertyBundle)` moved to {%jdoc core::lang.PmdCapableLanguage %}
  * {% jdoc !!core::util.StringUtil#linesWithTrimIndent(net.sourceforge.pmd.lang.document.Chars) %} now takes a `Chars`
    instead of a `String`.
* All language modules (like pmd-apex, pmd-cpp, ...)
  * consistent package naming: `net.sourceforge.pmd.lang.<langId>.cpd`
  * adapted to use {% jdoc core::cpd.CpdCapableLanguage %}
  * consistent static method `#getInstance()`
  * removed constants like `ID`, `TERSE_NAME` or `NAME`. Use `getInstance().getName()` etc. instead

##### Internal APIs

* {% jdoc core::cpd.Tokens %}
* {% jdoc core::net.sourceforge.pmd.properties.PropertyTypeId %}

##### Deprecated API

* {% jdoc !!core::lang.Language#getTerseName() %} ➡️ use {% jdoc core::lang.Language#getId() %} instead

* The method {%jdoc !!java::lang.java.ast.ASTPattern#getParenthesisDepth() %} has been deprecated and will be removed.
  It was introduced for supporting parenthesized patterns, but that was removed with Java 21. It is only used when
  parsing code as java-19-preview.

##### Experimental APIs

* To support the Java preview language features "String Templates" and "Unnamed Patterns and Variables", the following
  AST nodes have been introduced as experimental:
  * {% jdoc java::lang.java.ast.ASTTemplateExpression %}
  * {% jdoc java::lang.java.ast.ASTTemplate %}
  * {% jdoc java::lang.java.ast.ASTTemplateFragment %}
  * {% jdoc java::lang.java.ast.ASTUnnamedPattern %}
* The AST nodes for supporting "Record Patterns" and "Pattern Matching for switch" are not experimental anymore:
  * {% jdoc java::lang.jast.ast.ASTRecordPattern %}
  * {% jdoc java::lang.jast.ast.ASTPatternList %} (Note: it was renamed from `ASTComponentPatternList`)
  * {% jdoc java::lang.jast.ast. %} (Note: it was renamed from `ASTSwitchGuard`)


#### External Contributions
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)

### 🚀 Major Features and Enhancements

#### New official logo

The new official logo of PMD:

![New PMD Logo]({{ baseurl }}images/logo/pmd-logo-300px.png)

#### Revamped Java module

* Java grammar substantially refactored - more correct regarding the Java Language Specification (JLS)
* Built-in rules have been upgraded for the changed AST
* Rewritten type resolution framework and symbol table correctly implements the JLS
* AST exposes more semantic information (method calls, field accesses)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html#revamped-java).

Contributors: [Clément Fournier](https://github.com/oowekyala) (@oowekyala),
[Andreas Dangel](https://github.com/adangel) (@adangel),
[Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Revamped Command Line Interface

* unified and consistent Command Line Interface for both Linux/Unix and Windows across our different utilities
* single script `pmd` (`pmd.bat` for Windows) to launch the different utilities:
    * `pmd check` to run PMD rules and analyze a project
    * `pmd cpd` to run CPD (copy paste detector)
    * `pmd designer` to run the PMD Rule Designer
* progress bar support for `pmd check`
* shell completion

![Demo]({{ baseurl }}images/userdocs/pmd-demo.gif)

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

Contributors: [Juan Martín Sotuyo Dodero](https://github.com/jsotuyod) (@jsotuyod)

#### Full Antlr support

* [Antlr](https://www.antlr.org/) based grammars can now be used to build full-fledged PMD rules.
* Previously, Antlr grammar could only be used for CPD
* New supported languages: Swift and Kotlin

For more information, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog](https://github.com/pmd/pmd-designer/releases/tag/7.0.0-rc1).

#### New CPD report format cpdhtml-v2.xslt

Thanks to @mohan-chinnappan-n a new CPD report format has been added which features a data table.
It uses an XSLT stylesheet to convert CPD's XML format into HTML.

See [the example report]({{ baseurl }}report-examples/cpdhtml-v2.html).

### 🎉 Language Related Changes

Note that this is just a concise listing of the highlight.
For more information on the languages, see the [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

#### New: Swift support

* use PMD to analyze Swift code with PMD rules
* initially 4 built-in rules

Contributors: [Lucas Soncini](https://github.com/lsoncini) (@lsoncini),
[Matías Fraga](https://github.com/matifraga) (@matifraga),
[Tomás De Lucca](https://github.com/tomidelucca) (@tomidelucca)

#### New: Kotlin support (experimental)

* use PMD to analyze Kotlin code with PMD rules
* Support for Kotlin 1.8 grammar
* initially 2 built-in rules

#### New: CPD support for TypeScript

Thanks to a contribution, CPD now supports the TypeScript language. It is shipped
with the rest of the JavaScript support in the module `pmd-javascript`.

Contributors: [Paul Guyot](https://github.com/pguyot) (@pguyot)

#### New: CPD support for Julia

Thanks to a contribution, CPD now supports the Julia language. It is shipped
in the new module `pmd-julia`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: CPD support for Coco

Thanks to a contribution, CPD now supports Coco, a modern programming language
designed specifically for building event-driven software. It is shipped in the new
module `pmd-coco`.

Contributors: [Wener](https://github.com/wener-tiobe) (@wener-tiobe)

#### New: Java 21 Support

This release of PMD brings support for Java 21. There are the following new standard language features,
that are supported now:

* [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
* [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)

PMD also supports the following preview language features:

* [JEP 430: String Templates (Preview)](https://openjdk.org/jeps/430)
* [JEP 443: Unnamed Patterns and Variables (Preview)](https://openjdk.org/jeps/443)
* [JEP 445: Unnamed Classes and Instance Main Methods (Preview)](https://openjdk.org/jeps/445)

In order to analyze a project with PMD that uses these language features,
you'll need to enable it via the environment variable `PMD_JAVA_OPTS` and select the new language
version `21-preview`:

    export PMD_JAVA_OPTS=--enable-preview
    pmd check --use-version java-21-preview ...

Note: Support for Java 19 preview language features have been removed. The version "19-preview" is no longer available.

#### Changed: JavaScript support

* latest version supports ES6 and also some new constructs (see [Rhino](https://github.com/mozilla/rhino)])
* comments are retained

#### Changed: Language versions

* more predefined language versions for each supported language
* can be used to limit rule execution for specific versions only with `minimumLanguageVersion` and
  `maximumLanguageVersion` attributes.

#### Changed: CPP can now ignore identifiers in sequences (CPD)

* new command line option for CPD: `--ignore-sequences`.
* This option is used for CPP only: with the already existing option `--ignore-literal-sequences`, only
  literals were ignored. The new option additional ignores identifiers as well in sequences.
* See [PR #4470](https://github.com/pmd/pmd/pull/4470) for details.

#### Changed: Apex Jorje Updated

With the new version of Apex Jorje, the new language constructs like User Mode Database Operations
can be parsed now. PMD should now be able to parse Apex code up to version 59.0 (Winter '23).

#### Changed: Rule properties

* The old deprecated classes like `IntProperty` and `StringProperty` have been removed. Please use
  {% jdoc core::properties.PropertyFactory %} to create properties.
* All properties which accept multiple values now use a comma (`,`) as a delimiter. The previous default was a
  pipe character (`|`). The delimiter is not configurable anymore. If needed, the comma can be escaped
  with a backslash.
* The `min` and `max` attributes in property definitions in the XML are now optional and can appear separately
  or be omitted.

### 🌟 New and changed rules

#### New Rules

**Apex**
* {% rule apex/design/UnusedMethod %} finds unused methods in your code.

**Java**
* {% rule java/codestyle/UnnecessaryBoxing %} reports boxing and unboxing conversions that may be made implicit.

**Kotlin**
* {% rule kotlin/bestpractices/FunctionNameTooShort %}
* {% rule kotlin/errorprone/OverrideBothEqualsAndHashcode %}

**Swift**
* {% rule swift/bestpractices/ProhibitedInterfaceBuilder %}
* {% rule swift/bestpractices/UnavailableFunction %}
* {% rule swift/errorprone/ForceCast %}
* {% rule swift/errorprone/ForceTry %}

#### Changed Rules

**General changes**

* All statistical rules (like ExcessiveClassLength, ExcessiveParameterList) have been simplified and unified.
  The properties `topscore` and `sigma` have been removed. The property `minimum` is still there, however the type is not
  a decimal number anymore but has been changed to an integer. This affects rules in the languages Apex, Java, PLSQL
  and Velocity Template Language (vm):
    * Apex: {% rule apex/design/ExcessiveClassLength %}, {% rule apex/design/ExcessiveParameterList %},
      {% rule apex/design/ExcessivePublicCount %}, {% rule apex/design/NcssConstructorCount %},
      {% rule apex/design/NcssMethodCount %}, {% rule apex/design/NcssTypeCount %}
    * Java: {% rule java/design/ExcessiveImports %}, {% rule java/design/ExcessiveParameterList %},
      {% rule java/design/ExcessivePublicCount %}, {% rule java/design/SwitchDensity %}
    * PLSQL: {% rule plsql/design/ExcessiveMethodLength %}, {% rule plsql/design/ExcessiveObjectLength %},
      {% rule plsql/design/ExcessivePackageBodyLength %}, {% rule plsql/design/ExcessivePackageSpecificationLength %},
      {% rule plsql/design/ExcessiveParameterList %}, {% rule plsql/design/ExcessiveTypeLength %},
      {% rule plsql/design/NcssMethodCount %}, {% rule plsql/design/NcssObjectCount %},
      {% rule plsql/design/NPathComplexity %}
    * VM: {% rule vm/design/ExcessiveTemplateLength %}

* The general property `violationSuppressXPath` which is available for all rules to
  [suppress warnings]({{ baseurl }}pmd_userdocs_suppressing_warnings.html) now uses XPath version 3.1 by default.
  This version of the XPath language is mostly identical to XPath 2.0. In PMD 6, XPath 1.0 has been used.
  If you upgrade from PMD 6, you need to verify your `violationSuppressXPath` properties.

**Apex General changes**

* The properties `cc_categories`, `cc_remediation_points_multiplier`, `cc_block_highlighting` have been removed
  from all rules. These properties have been deprecated since PMD 6.13.0.
  See [issue #1648](https://github.com/pmd/pmd/issues/1648) for more details.

**Java General changes**

* Violations reported on methods or classes previously reported the line range of the entire method
  or class. With PMD 7.0.0, the reported location is now just the identifier of the method or class.
  This affects various rules, e.g. {% rule java/design/CognitiveComplexity %}.

  The report location is controlled by the overrides of the method {% jdoc core::lang.ast.Node#getReportLocation() %}
  in different node types.

  See [issue #4439](https://github.com/pmd/pmd/issues/4439) and [issue #730](https://github.com/pmd/pmd/issues/730)
  for more details.

**Java Best Practices**

* {% rule java/bestpractices/ArrayIsStoredDirectly %}: Violations are now reported on the assignment and not
  anymore on the formal parameter. The reported line numbers will probably move.
* {% rule java/bestpractices/AvoidReassigningLoopVariables %}: This rule might not report anymore all
  reassignments of the control variable in for-loops when the property `forReassign` is set to `skip`.
  See [issue #4500](https://github.com/pmd/pmd/issues/4500) for more details.
* {% rule java/bestpractices/LooseCoupling %}: The rule has a new property to allow some types to be coupled
  to (`allowedTypes`).
* {% rule java/bestpractices/UnusedLocalVariable %}: This rule has some important false-negatives fixed
  and finds many more cases now. For details see issues [#2130](https://github.com/pmd/pmd/issues/2130),
  [#4516](https://github.com/pmd/pmd/issues/4516), and [#4517](https://github.com/pmd/pmd/issues/4517).

**Java Codestyle**

* {% rule java/codestyle/MethodNamingConventions %}: The property `checkNativeMethods` has been removed. The
  property was deprecated since PMD 6.3.0. Use the property `nativePattern` to control whether native methods
  should be considered or not.
* {% rule java/codestyle/ShortVariable %}: This rule now also reports short enum constant names.
* {% rule java/codestyle/UseDiamondOperator %}: The property `java7Compatibility` has been removed. The rule now
  handles Java 7 properly without a property.
* {% rule java/codestyle/UnnecessaryFullyQualifiedName %}: The rule has two new properties,
  to selectively disable reporting on static field and method qualifiers. The rule also has been improved
  to be more precise.
* {% rule java/codestyle/UselessParentheses %}: The rule has two new properties which control how strict
  the rule should be applied. With `ignoreClarifying` (default: true) parentheses that are strictly speaking
  not necessary are allowed, if they separate expressions of different precedence.
  The other property `ignoreBalancing` (default: true) is similar, in that it allows parentheses that help
  reading and understanding the expressions.

**Java Design**

* {% rule java/design/CyclomaticComplexity %}: The property `reportLevel` has been removed. The property was
  deprecated since PMD 6.0.0. The report level can now be configured separated for classes and methods using
  `classReportLevel` and `methodReportLevel` instead.
* {% rule java/design/ImmutableField %}: The property `ignoredAnnotations` has been removed. The property was
  deprecated since PMD 6.52.0.
* {% rule java/design/LawOfDemeter %}: The rule has a new property `trustRadius`. This defines the maximum degree
  of trusted data. The default of 1 is the most restrictive.
* {% rule java/design/NPathComplexity %}: The property `minimum` has been removed. It was deprecated since PMD 6.0.0.
  Use the property `reportLevel` instead.
* {% rule java/design/SingularField %}: The properties `checkInnerClasses` and `disallowNotAssignment` have been removed.
  The rule is now more precise and will check these cases properly.
* {% rule java/design/UseUtilityClass %}: The property `ignoredAnnotations` has been removed.

**Java Documentation**

* {% rule java/documentation/CommentContent %}: The properties `caseSensitive` and `disallowedTerms` are removed. The
  new property `forbiddenRegex` can be used now to define the disallowed terms with a single regular
  expression.
* {% rule java/documentation/CommentRequired %}:
    * Overridden methods are now detected even without the `@Override`
      annotation. This is relevant for the property `methodWithOverrideCommentRequirement`.
      See also [pull request #3757](https://github.com/pmd/pmd/pull/3757).
    * Elements in annotation types are now detected as well. This might lead to an increased number of violations
      for missing public method comments.
* {% rule java/documentation/CommentSize %}: When determining the line-length of a comment, the leading comment
  prefix markers (e.g. `*` or `//`) are ignored and don't add up to the line-length.
  See also [pull request #4369](https://github.com/pmd/pmd/pull/4369).

**Java Error Prone**

* {% rule java/errorprone/AvoidDuplicateLiterals %}: The property `exceptionfile` has been removed. The property was
  deprecated since PMD 6.10.0. Use the property `exceptionList` instead.
* {% rule java/errorprone/DontImportSun %}: `sun.misc.Signal` is not special-cased anymore.
* {% rule java/errorprone/EmptyCatchBlock %}: `CloneNotSupportedException` and `InterruptedException` are not
  special-cased anymore. Rename the exception parameter to `ignored` to ignore them.
* {% rule java/errorprone/ImplicitSwitchFallThrough %}: Violations are now reported on the case statements
  rather than on the switch statements. This is more accurate but might result in more violations now.

#### Removed Rules

Many rules, that were previously deprecated have been finally removed.
See [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html) for the complete list.

### 🚨 API

The API of PMD has been growing over the years and needed some cleanup. The goal is, to
have a clear separation between a well-defined API and the implementation, which is internal.
This should help us in future development.

Also, there are some improvement and changes in different areas. For the detailed description
of the changes listed here, see [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

* Miscellaneous smaller changes and cleanups
* XPath 3.1 support for XPath-based rules
* Node stream API for AST traversal
* Metrics framework
* Testing framework
* Language Lifecycle and Language Properties
* Rule Properties
* New Programmatic API for CPD

### 💥 Compatibility and migration notes

A detailed documentation of required changes are available in the
[Migration Guide for PMD 7]({{ baseurl }}pmd_userdocs_migrating_to_pmd7.html).

See also [Detailed Release Notes for PMD 7]({{ baseurl }}pmd_release_notes_pmd7.html).

### 🐛 Fixed Issues

* miscellaneous
    * [#881](https://github.com/pmd/pmd/issues/881):   \[all] Breaking API changes for 7.0.0
    * [#896](https://github.com/pmd/pmd/issues/896):   \[all] Use slf4j
    * [#1431](https://github.com/pmd/pmd/pull/1431):   \[ui] Remove old GUI applications (designerold, bgastviewer)
    * [#1451](https://github.com/pmd/pmd/issues/1451): \[core] RulesetFactoryCompatibility stores the whole ruleset file in memory as a string
    * [#2496](https://github.com/pmd/pmd/issues/2496): Update PMD 7 Logo on landing page
    * [#2497](https://github.com/pmd/pmd/issues/2497): PMD 7 Logo page
    * [#2498](https://github.com/pmd/pmd/issues/2498): Update PMD 7 Logo in documentation
    * [#3797](https://github.com/pmd/pmd/issues/3797): \[all] Use JUnit5
    * [#4462](https://github.com/pmd/pmd/issues/4462): Provide Software Bill of Materials (SBOM)
    * [#4460](https://github.com/pmd/pmd/pull/4460):   Fix assembly-plugin warnings
    * [#4582](https://github.com/pmd/pmd/issues/4582): \[dist] Download link broken
    * [#4691](https://github.com/pmd/pmd/issues/4691): \[CVEs] Critical and High CEVs reported on PMD and PMD dependencies
* ant
    * [#4080](https://github.com/pmd/pmd/issues/4080): \[ant] Split off Ant integration into a new submodule
* core
    * [#880](https://github.com/pmd/pmd/issues/880):   \[core] Make visitors generic
    * [#1204](https://github.com/pmd/pmd/issues/1204): \[core] Allow numeric properties in XML to be within an unbounded range
    * [#1622](https://github.com/pmd/pmd/pull/1622):   \[core] NodeStream API
    * [#1687](https://github.com/pmd/pmd/issues/1687): \[core] Deprecate and Remove XPath 1.0 support
    * [#1785](https://github.com/pmd/pmd/issues/1785): \[core] Allow abstract node types to be valid rulechain visits
    * [#1825](https://github.com/pmd/pmd/pull/1825):   \[core] Support NoAttribute for XPath
    * [#2038](https://github.com/pmd/pmd/issues/2038): \[core] Remove DCD
    * [#2218](https://github.com/pmd/pmd/issues/2218): \[core] `isFindBoundary` should not be an attribute
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#2239](https://github.com/pmd/pmd/issues/2239): \[core] Merging Javacc build scripts
    * [#2500](https://github.com/pmd/pmd/issues/2500): \[core] Clarify API for ANTLR based languages
    * [#2518](https://github.com/pmd/pmd/issues/2518): \[core] Language properties
    * [#2602](https://github.com/pmd/pmd/issues/2602): \[core] Remove ParserOptions
    * [#2614](https://github.com/pmd/pmd/pull/2614):   \[core] Upgrade Saxon, add XPath 3.1, remove Jaxen
    * [#2696](https://github.com/pmd/pmd/pull/2696):   \[core] Remove DFA
    * [#2821](https://github.com/pmd/pmd/issues/2821): \[core] Rule processing error filenames are missing paths
    * [#2873](https://github.com/pmd/pmd/issues/2873): \[core] Utility classes in pmd 7
    * [#2885](https://github.com/pmd/pmd/issues/2885): \[core] Error recovery mode
    * [#3203](https://github.com/pmd/pmd/issues/3203): \[core] Replace RuleViolationFactory implementations with ViolationDecorator
    * [#3692](https://github.com/pmd/pmd/pull/3692):   \[core] Analysis listeners
    * [#3782](https://github.com/pmd/pmd/issues/3782): \[core] Language lifecycle
    * [#3815](https://github.com/pmd/pmd/issues/3815): \[core] Update Saxon HE to 10.7
    * [#3893](https://github.com/pmd/pmd/pull/3893):   \[core] Text documents
    * [#3902](https://github.com/pmd/pmd/issues/3902): \[core] Violation decorators
    * [#3918](https://github.com/pmd/pmd/issues/3918): \[core] Make LanguageRegistry non static
    * [#3919](https://github.com/pmd/pmd/issues/3919): \[core] Merge CPD and PMD language
    * [#3922](https://github.com/pmd/pmd/pull/3922):   \[core] Better error reporting for the ruleset parser
    * [#4035](https://github.com/pmd/pmd/issues/4035): \[core] ConcurrentModificationException in DefaultRuleViolationFactory
    * [#4120](https://github.com/pmd/pmd/issues/4120): \[core] Explicitly name all language versions
    * [#4204](https://github.com/pmd/pmd/issues/4204): \[core] Provide a CpdAnalysis class as a programmatic entry point into CPD
    * [#4301](https://github.com/pmd/pmd/issues/4301): \[core] Remove deprecated property concrete classes
    * [#4302](https://github.com/pmd/pmd/issues/4302): \[core] Migrate Property Framework API to Java 8
    * [#4323](https://github.com/pmd/pmd/issues/4323): \[core] Refactor CPD integration
    * [#4353](https://github.com/pmd/pmd/pull/4353):   \[core] Micro optimizations for Node API
    * [#4365](https://github.com/pmd/pmd/pull/4365):   \[core] Improve benchmarking
    * [#4397](https://github.com/pmd/pmd/pull/4397):   \[core] Refactor CPD
    * [#4420](https://github.com/pmd/pmd/pull/4420):   \[core] Remove PMD.EOL
    * [#4425](https://github.com/pmd/pmd/pull/4425):   \[core] Replace TextFile::pathId
    * [#4454](https://github.com/pmd/pmd/issues/4454): \[core] "Unknown option: '-min'" but is referenced in documentation
    * [#4611](https://github.com/pmd/pmd/pull/4611):   \[core] Fix loading language properties from env vars
    * [#4621](https://github.com/pmd/pmd/issues/4621): \[core] Make `ClasspathClassLoader::getResource` child first
* cli
    * [#2234](https://github.com/pmd/pmd/issues/2234): \[core] Consolidate PMD CLI into a single command
    * [#3828](https://github.com/pmd/pmd/issues/3828): \[core] Progress reporting
    * [#4079](https://github.com/pmd/pmd/issues/4079): \[cli] Split off CLI implementation into a pmd-cli submodule
    * [#4423](https://github.com/pmd/pmd/pull/4423):   \[cli] Fix NPE when only `--file-list` is specified
    * [#4482](https://github.com/pmd/pmd/issues/4482): \[cli] pmd.bat can only be executed once
    * [#4484](https://github.com/pmd/pmd/issues/4484): \[cli] ast-dump with no properties produce an NPE
* doc
    * [#2501](https://github.com/pmd/pmd/issues/2501): \[doc] Verify ANTLR Documentation
    * [#4294](https://github.com/pmd/pmd/issues/4294): \[doc] Migration Guide for upgrading PMD 6 ➡️ 7
    * [#4303](https://github.com/pmd/pmd/issues/4303): \[doc] Document new property framework
    * [#4438](https://github.com/pmd/pmd/issues/4438): \[doc] Documentation links in VS Code are outdated
    * [#4521](https://github.com/pmd/pmd/issues/4521): \[doc] Website is not mobile friendly
* testing
    * [#2435](https://github.com/pmd/pmd/issues/2435): \[test] Remove duplicated Dummy language module
    * [#4234](https://github.com/pmd/pmd/issues/4234): \[test] Tests that change the logging level do not work

Language specific fixes:

* apex
    * [#1937](https://github.com/pmd/pmd/issues/1937): \[apex] Apex should only have a single RootNode
    * [#1648](https://github.com/pmd/pmd/issues/1648): \[apex,vf] Remove CodeClimate dependency
    * [#1750](https://github.com/pmd/pmd/pull/1750):   \[apex] Remove apex statistical rules
    * [#2836](https://github.com/pmd/pmd/pull/2836):   \[apex] Remove Apex ProjectMirror
    * [#3973](https://github.com/pmd/pmd/issues/3973): \[apex] Update parser to support new 'as user' keywords (User Mode for Database Operations)
    * [#4427](https://github.com/pmd/pmd/issues/4427): \[apex] ApexBadCrypto test failing to detect inline code
    * [#4453](https://github.com/pmd/pmd/issues/4453): \[apex] \[7.0-rc1] Exception while initializing Apexlink (Index 34812 out of bounds for length 34812)
* apex-design
    * [#2667](https://github.com/pmd/pmd/issues/2667): \[apex] Integrate nawforce/ApexLink to build robust Unused rule
    * [#4509](https://github.com/pmd/pmd/issues/4509): \[apex] ExcessivePublicCount doesn't consider inner classes correctly
    * [#4596](https://github.com/pmd/pmd/issues/4596): \[apex] ExcessivePublicCount ignores properties
* apex-security
    * [#4646](https://github.com/pmd/pmd/issues/4646): \[apex] ApexSOQLInjection does not recognise SObjectType or SObjectField as safe variable types
* java
    * [#520](https://github.com/pmd/pmd/issues/520):   \[java] Allow `@SuppressWarnings` with constants instead of literals
    * [#864](https://github.com/pmd/pmd/issues/864):   \[java] Similar/duplicated implementations for determining FQCN
    * [#905](https://github.com/pmd/pmd/issues/905):   \[java] Add new node for anonymous class declaration
    * [#910](https://github.com/pmd/pmd/issues/910):   \[java] AST inconsistency between primitive and reference type arrays
    * [#997](https://github.com/pmd/pmd/issues/997):   \[java] Java8 parsing corner case with annotated array types
    * [#998](https://github.com/pmd/pmd/issues/998):   \[java] AST inconsistencies around FormalParameter
    * [#1019](https://github.com/pmd/pmd/issues/1019): \[java] Breaking Java Grammar changes for PMD 7.0.0
    * [#1124](https://github.com/pmd/pmd/issues/1124): \[java] ImmutableList implementation in the qname codebase
    * [#1128](https://github.com/pmd/pmd/issues/1128): \[java] Improve ASTLocalVariableDeclaration
    * [#1150](https://github.com/pmd/pmd/issues/1150): \[java] ClassOrInterfaceType AST improvements
    * [#1207](https://github.com/pmd/pmd/issues/1207): \[java] Resolve explicit types using FQCNs, without hitting the classloader
    * [#1367](https://github.com/pmd/pmd/issues/1367): \[java] Parsing error on annotated inner class
    * [#1661](https://github.com/pmd/pmd/issues/1661): \[java] About operator nodes
    * [#2366](https://github.com/pmd/pmd/pull/2366):   \[java] Remove qualified names
    * [#2819](https://github.com/pmd/pmd/issues/2819): \[java] GLB bugs in pmd 7
    * [#3642](https://github.com/pmd/pmd/issues/3642): \[java] Parse error on rare extra dimensions on method return type on annotation methods
    * [#3763](https://github.com/pmd/pmd/issues/3763): \[java] Ambiguous reference error in valid code
    * [#3749](https://github.com/pmd/pmd/issues/3749): \[java] Improve `isOverridden` in ASTMethodDeclaration
    * [#3750](https://github.com/pmd/pmd/issues/3750): \[java] Make symbol table support instanceof pattern bindings
    * [#3752](https://github.com/pmd/pmd/issues/3752): \[java] Expose annotations in symbol API
    * [#4237](https://github.com/pmd/pmd/pull/4237):   \[java] Cleanup handling of Java comments
    * [#4317](https://github.com/pmd/pmd/issues/4317): \[java] Some AST nodes should not be TypeNodes
    * [#4359](https://github.com/pmd/pmd/issues/4359): \[java] Type resolution fails with NPE when the scope is not a type declaration
    * [#4367](https://github.com/pmd/pmd/issues/4367): \[java] Move testrule TypeResTest into internal
    * [#4383](https://github.com/pmd/pmd/issues/4383): \[java] IllegalStateException: Object is not an array type!
    * [#4401](https://github.com/pmd/pmd/issues/4401): \[java] PMD 7 fails to build under Java 19
    * [#4405](https://github.com/pmd/pmd/issues/4405): \[java] Processing error with ArrayIndexOutOfBoundsException
    * [#4583](https://github.com/pmd/pmd/issues/4583): \[java] Support JDK 21 (LTS)
* java-bestpractices
    * [#342](https://github.com/pmd/pmd/issues/342):   \[java] AccessorMethodGeneration: Name clash with another public field not properly handled
    * [#755](https://github.com/pmd/pmd/issues/755):   \[java] AccessorClassGeneration false positive for private constructors
    * [#770](https://github.com/pmd/pmd/issues/770):   \[java] UnusedPrivateMethod yields false positive for counter-variant arguments
    * [#807](https://github.com/pmd/pmd/issues/807):   \[java] AccessorMethodGeneration false positive with overloads
    * [#833](https://github.com/pmd/pmd/issues/833):   \[java] ForLoopCanBeForeach should consider iterating on this
    * [#1189](https://github.com/pmd/pmd/issues/1189): \[java] UnusedPrivateMethod false positive from inner class via external class
    * [#1205](https://github.com/pmd/pmd/issues/1205): \[java] Improve ConstantsInInterface message to mention alternatives
    * [#1212](https://github.com/pmd/pmd/issues/1212): \[java] Don't raise JUnitTestContainsTooManyAsserts on JUnit 5's assertAll
    * [#1422](https://github.com/pmd/pmd/issues/1422): \[java] JUnitTestsShouldIncludeAssert false positive with inherited @<!-- -->Rule field
    * [#1455](https://github.com/pmd/pmd/issues/1455): \[java] JUnitTestsShouldIncludeAssert: False positives for assert methods named "check" and "verify"
    * [#1563](https://github.com/pmd/pmd/issues/1563): \[java] ForLoopCanBeForeach false positive with method call using index variable
    * [#1565](https://github.com/pmd/pmd/issues/1565): \[java] JUnitAssertionsShouldIncludeMessage false positive with AssertJ
    * [#1747](https://github.com/pmd/pmd/issues/1747): \[java] PreserveStackTrace false-positive
    * [#1969](https://github.com/pmd/pmd/issues/1969): \[java] MissingOverride false-positive triggered by package-private method overwritten in another package by extending class
    * [#1998](https://github.com/pmd/pmd/issues/1998): \[java] AccessorClassGeneration false-negative: subclass calls private constructor
    * [#2130](https://github.com/pmd/pmd/issues/2130): \[java] UnusedLocalVariable: false-negative with array
    * [#2147](https://github.com/pmd/pmd/issues/2147): \[java] JUnitTestsShouldIncludeAssert - false positives with lambdas and static methods
    * [#2464](https://github.com/pmd/pmd/issues/2464): \[java] LooseCoupling must ignore class literals: ArrayList.class
    * [#2542](https://github.com/pmd/pmd/issues/2542): \[java] UseCollectionIsEmpty can not detect the case `foo.bar().size()`
    * [#2650](https://github.com/pmd/pmd/issues/2650): \[java] UseTryWithResources false positive when AutoCloseable helper used
    * [#2796](https://github.com/pmd/pmd/issues/2796): \[java] UnusedAssignment false positive with call chains
    * [#2797](https://github.com/pmd/pmd/issues/2797): \[java] MissingOverride long-standing issues
    * [#2806](https://github.com/pmd/pmd/issues/2806): \[java] SwitchStmtsShouldHaveDefault false-positive with Java 14 switch non-fallthrough branches
    * [#2822](https://github.com/pmd/pmd/issues/2822): \[java] LooseCoupling rule: Extend to cover user defined implementations and interfaces
    * [#2843](https://github.com/pmd/pmd/pull/2843):   \[java] Fix UnusedAssignment FP with field accesses
    * [#2882](https://github.com/pmd/pmd/issues/2882): \[java] UseTryWithResources - false negative for explicit close
    * [#2883](https://github.com/pmd/pmd/issues/2883): \[java] JUnitAssertionsShouldIncludeMessage false positive with method call
    * [#2890](https://github.com/pmd/pmd/issues/2890): \[java] UnusedPrivateMethod false positive with generics
    * [#2946](https://github.com/pmd/pmd/issues/2946): \[java] SwitchStmtsShouldHaveDefault false positive on enum inside enums
    * [#3672](https://github.com/pmd/pmd/pull/3672):   \[java] LooseCoupling - fix false positive with generics
    * [#3675](https://github.com/pmd/pmd/pull/3675):   \[java] MissingOverride - fix false positive with mixing type vars
    * [#3858](https://github.com/pmd/pmd/issues/3858): \[java] UseCollectionIsEmpty should infer local variable type from method invocation
    * [#4433](https://github.com/pmd/pmd/issues/4433): \[java] \[7.0-rc1] ReplaceHashtableWithMap on java.util.Properties
    * [#4492](https://github.com/pmd/pmd/issues/4492): \[java] GuardLogStatement gives false positive when argument is a Java method reference
    * [#4503](https://github.com/pmd/pmd/issues/4503): \[java] JUnitTestsShouldIncludeAssert: false negative with TestNG
    * [#4516](https://github.com/pmd/pmd/issues/4516): \[java] UnusedLocalVariable: false-negative with try-with-resources
    * [#4517](https://github.com/pmd/pmd/issues/4517): \[java] UnusedLocalVariable: false-negative with compound assignments
    * [#4518](https://github.com/pmd/pmd/issues/4518): \[java] UnusedLocalVariable: false-positive with multiple for-loop indices
    * [#4634](https://github.com/pmd/pmd/issues/4634): \[java] JUnit4TestShouldUseTestAnnotation false positive with TestNG
* java-codestyle
    * [#1208](https://github.com/pmd/pmd/issues/1208): \[java] PrematureDeclaration rule false-positive on variable declared to measure time
    * [#1429](https://github.com/pmd/pmd/issues/1429): \[java] PrematureDeclaration as result of method call (false positive)
    * [#1480](https://github.com/pmd/pmd/issues/1480): \[java] IdenticalCatchBranches false positive with return expressions
    * [#1673](https://github.com/pmd/pmd/issues/1673): \[java] UselessParentheses false positive with conditional operator
    * [#1790](https://github.com/pmd/pmd/issues/1790): \[java] UnnecessaryFullyQualifiedName false positive with enum constant
    * [#1918](https://github.com/pmd/pmd/issues/1918): \[java] UselessParentheses false positive with boolean operators
    * [#2134](https://github.com/pmd/pmd/issues/2134): \[java] PreserveStackTrace not handling `Throwable.addSuppressed(...)`
    * [#2299](https://github.com/pmd/pmd/issues/2299): \[java] UnnecessaryFullyQualifiedName false positive with similar package name
    * [#2391](https://github.com/pmd/pmd/issues/2391): \[java] UseDiamondOperator FP when expected type and constructed type have a different parameterization
    * [#2528](https://github.com/pmd/pmd/issues/2528): \[java] MethodNamingConventions - JUnit 5 method naming not support ParameterizedTest
    * [#2739](https://github.com/pmd/pmd/issues/2739): \[java] UselessParentheses false positive for string concatenation
    * [#2748](https://github.com/pmd/pmd/issues/2748): \[java] UnnecessaryCast false positive with unchecked cast
    * [#2973](https://github.com/pmd/pmd/issues/2973): \[java] New rule: UnnecessaryBoxing
    * [#3195](https://github.com/pmd/pmd/pull/3195):   \[java] Improve rule UnnecessaryReturn to detect more cases
    * [#3218](https://github.com/pmd/pmd/pull/3218):   \[java] Generalize UnnecessaryCast to flag all unnecessary casts
    * [#3221](https://github.com/pmd/pmd/issues/3221): \[java] PrematureDeclaration false positive for unused variables
    * [#3238](https://github.com/pmd/pmd/issues/3238): \[java] Improve ExprContext, fix FNs of UnnecessaryCast
    * [#3500](https://github.com/pmd/pmd/pull/3500):   \[java] UnnecessaryBoxing - check for Integer.valueOf(String) calls
    * [#4268](https://github.com/pmd/pmd/issues/4268): \[java] CommentDefaultAccessModifier: false positive with TestNG annotations
    * [#4273](https://github.com/pmd/pmd/issues/4273): \[java] CommentDefaultAccessModifier ignoredAnnotations should include "org.junit.jupiter.api.extension.RegisterExtension" by default
    * [#4357](https://github.com/pmd/pmd/pull/4357):   \[java] Fix IllegalStateException in UseDiamondOperator rule
    * [#4432](https://github.com/pmd/pmd/issues/4432): \[java] \[7.0-rc1] UnnecessaryImport - Unused static import is being used
    * [#4455](https://github.com/pmd/pmd/issues/4455): \[java] FieldNamingConventions: false positive with lombok's @<!-- -->UtilityClass
    * [#4487](https://github.com/pmd/pmd/issues/4487): \[java] UnnecessaryConstructor: false-positive with @<!-- -->Inject and @<!-- -->Autowired
    * [#4511](https://github.com/pmd/pmd/issues/4511): \[java] LocalVariableCouldBeFinal shouldn't report unused variables
    * [#4512](https://github.com/pmd/pmd/issues/4512): \[java] MethodArgumentCouldBeFinal shouldn't report unused parameters
    * [#4557](https://github.com/pmd/pmd/issues/4557): \[java] UnnecessaryImport FP with static imports of overloaded methods
* java-design
    * [#1014](https://github.com/pmd/pmd/issues/1014): \[java] LawOfDemeter: False positive with lambda expression
    * [#1605](https://github.com/pmd/pmd/issues/1605): \[java] LawOfDemeter: False positive for standard UTF-8 charset name
    * [#2160](https://github.com/pmd/pmd/issues/2160): \[java] Issues with Law of Demeter
    * [#2175](https://github.com/pmd/pmd/issues/2175): \[java] LawOfDemeter: False positive for chained methods with generic method call
    * [#2179](https://github.com/pmd/pmd/issues/2179): \[java] LawOfDemeter: False positive with static property access - should treat class-level property as global object, not dot-accessed property
    * [#2180](https://github.com/pmd/pmd/issues/2180): \[java] LawOfDemeter: False positive with Thread and ThreadLocalRandom
    * [#2182](https://github.com/pmd/pmd/issues/2182): \[java] LawOfDemeter: False positive with package-private access
    * [#2188](https://github.com/pmd/pmd/issues/2188): \[java] LawOfDemeter: False positive with fields assigned to local vars
    * [#2536](https://github.com/pmd/pmd/issues/2536): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal can't detect inner class
    * [#3668](https://github.com/pmd/pmd/pull/3668):   \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal - fix FP with inner private classes
    * [#3754](https://github.com/pmd/pmd/issues/3754): \[java] SingularField false positive with read in while condition
    * [#3786](https://github.com/pmd/pmd/issues/3786): \[java] SimplifyBooleanReturns should consider operator precedence
    * [#3840](https://github.com/pmd/pmd/issues/3840): \[java] LawOfDemeter disallows method call on locally created object
    * [#4238](https://github.com/pmd/pmd/pull/4238):   \[java] Make LawOfDemeter not use the rulechain
    * [#4254](https://github.com/pmd/pmd/issues/4254): \[java] ImmutableField - false positive with Lombok @<!-- -->Setter
    * [#4434](https://github.com/pmd/pmd/issues/4434): \[java] \[7.0-rc1] ExceptionAsFlowControl when simply propagating
    * [#4456](https://github.com/pmd/pmd/issues/4456): \[java] FinalFieldCouldBeStatic: false positive with lombok's @<!-- -->UtilityClass
    * [#4477](https://github.com/pmd/pmd/issues/4477): \[java] SignatureDeclareThrowsException: false-positive with TestNG annotations
    * [#4490](https://github.com/pmd/pmd/issues/4490): \[java] ImmutableField - false negative with Lombok @<!-- -->Getter
    * [#4549](https://github.com/pmd/pmd/pull/4549):   \[java] Make LawOfDemeter results deterministic
* java-documentation
    * [#4369](https://github.com/pmd/pmd/pull/4369):   \[java] Improve CommentSize
    * [#4416](https://github.com/pmd/pmd/pull/4416):   \[java] Fix reported line number in CommentContentRule
* java-errorprone
    * [#659](https://github.com/pmd/pmd/issues/659):   \[java] MissingBreakInSwitch - last default case does not contain a break
    * [#1005](https://github.com/pmd/pmd/issues/1005): \[java] CloneMethodMustImplementCloneable triggers for interfaces
    * [#1669](https://github.com/pmd/pmd/issues/1669): \[java] NullAssignment - FP with ternay and null as constructor argument
    * [#1899](https://github.com/pmd/pmd/issues/1899): \[java] Recognize @<!-- -->SuppressWanings("fallthrough") for MissingBreakInSwitch
    * [#2320](https://github.com/pmd/pmd/issues/2320): \[java] NullAssignment - FP with ternary and null as method argument
    * [#2532](https://github.com/pmd/pmd/issues/2532): \[java] AvoidDecimalLiteralsInBigDecimalConstructor can not detect the case `new BigDecimal(Expression)`
    * [#2579](https://github.com/pmd/pmd/issues/2579): \[java] MissingBreakInSwitch detects the lack of break in the last case
    * [#2880](https://github.com/pmd/pmd/issues/2880): \[java] CompareObjectsWithEquals - false negative with type res
    * [#2893](https://github.com/pmd/pmd/issues/2893): \[java] Remove special cases from rule EmptyCatchBlock
    * [#2894](https://github.com/pmd/pmd/issues/2894): \[java] Improve MissingBreakInSwitch
    * [#3071](https://github.com/pmd/pmd/issues/3071): \[java] BrokenNullCheck FP with PMD 6.30.0
    * [#3087](https://github.com/pmd/pmd/issues/3087): \[java] UnnecessaryBooleanAssertion overlaps with SimplifiableTestAssertion
    * [#3100](https://github.com/pmd/pmd/issues/3100): \[java] UseCorrectExceptionLogging FP in 6.31.0
    * [#3173](https://github.com/pmd/pmd/issues/3173): \[java] UseProperClassLoader false positive
    * [#3351](https://github.com/pmd/pmd/issues/3351): \[java] ConstructorCallsOverridableMethod ignores abstract methods
    * [#3400](https://github.com/pmd/pmd/issues/3400): \[java] AvoidUsingOctalValues FN with underscores
    * [#3843](https://github.com/pmd/pmd/issues/3843): \[java] UseEqualsToCompareStrings should consider return type
    * [#4063](https://github.com/pmd/pmd/issues/4063): \[java] AvoidBranchingStatementAsLastInLoop: False-negative about try/finally block
    * [#4356](https://github.com/pmd/pmd/pull/4356):   \[java] Fix NPE in CloseResourceRule
    * [#4449](https://github.com/pmd/pmd/issues/4449): \[java] AvoidAccessibilityAlteration: Possible false positive in AvoidAccessibilityAlteration rule when using Lambda expression
    * [#4457](https://github.com/pmd/pmd/issues/4457): \[java] OverrideBothEqualsAndHashcode: false negative with anonymous classes
    * [#4493](https://github.com/pmd/pmd/issues/4493): \[java] MissingStaticMethodInNonInstantiatableClass: false-positive about @<!-- -->Inject
    * [#4505](https://github.com/pmd/pmd/issues/4505): \[java] ImplicitSwitchFallThrough NPE in PMD 7.0.0-rc1
    * [#4510](https://github.com/pmd/pmd/issues/4510): \[java] ConstructorCallsOverridableMethod: false positive with lombok's @<!-- -->Value
    * [#4513](https://github.com/pmd/pmd/issues/4513): \[java] UselessOperationOnImmutable various false negatives with String
    * [#4514](https://github.com/pmd/pmd/issues/4514): \[java] AvoidLiteralsInIfCondition false positive and negative for String literals when ignoreExpressions=true
    * [#4546](https://github.com/pmd/pmd/issues/4546): \[java] OverrideBothEqualsAndHashCode ignores records
* java-multithreading
    * [#2537](https://github.com/pmd/pmd/issues/2537): \[java] DontCallThreadRun can't detect the case that call run() in `this.run()`
    * [#2538](https://github.com/pmd/pmd/issues/2538): \[java] DontCallThreadRun can't detect the case that call run() in `foo.bar.run()`
    * [#2577](https://github.com/pmd/pmd/issues/2577): \[java] UseNotifyAllInsteadOfNotify falsely detect a special case with argument: `foo.notify(bar)`
    * [#4483](https://github.com/pmd/pmd/issues/4483): \[java] NonThreadSafeSingleton false positive with double-checked locking
* java-performance
    * [#1224](https://github.com/pmd/pmd/issues/1224): \[java] InefficientEmptyStringCheck false negative in anonymous class
    * [#2587](https://github.com/pmd/pmd/issues/2587): \[java] AvoidArrayLoops could also check for list copy through iterated List.add()
    * [#2712](https://github.com/pmd/pmd/issues/2712): \[java] SimplifyStartsWith false-positive with AssertJ
    * [#3486](https://github.com/pmd/pmd/pull/3486):   \[java] InsufficientStringBufferDeclaration: Fix NPE
    * [#3848](https://github.com/pmd/pmd/issues/3848): \[java] StringInstantiation: false negative when using method result
    * [#4070](https://github.com/pmd/pmd/issues/4070): \[java] A false positive about the rule RedundantFieldInitializer
    * [#4458](https://github.com/pmd/pmd/issues/4458): \[java] RedundantFieldInitializer: false positive with lombok's @<!-- -->Value
* kotlin
    * [#419](https://github.com/pmd/pmd/issues/419):   \[kotlin] Add support for Kotlin
    * [#4389](https://github.com/pmd/pmd/pull/4389):   \[kotlin] Update grammar to version 1.8
* swift
    * [#1877](https://github.com/pmd/pmd/pull/1877):   \[swift] Feature/swift rules
    * [#1882](https://github.com/pmd/pmd/pull/1882):   \[swift] UnavailableFunction Swift rule
* xml
    * [#1800](https://github.com/pmd/pmd/pull/1800):   \[xml] Unimplement org.w3c.dom.Node from the XmlNodeWrapper

### ✨ External Contributions

* [#1658](https://github.com/pmd/pmd/pull/1658): \[core] Node support for Antlr-based languages - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1698](https://github.com/pmd/pmd/pull/1698): \[core] [swift] Antlr Base Parser adapter and Swift Implementation - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1774](https://github.com/pmd/pmd/pull/1774): \[core] Antlr visitor rules - [Lucas Soncini](https://github.com/lsoncini) (@lsoncini)
* [#1877](https://github.com/pmd/pmd/pull/1877): \[swift] Feature/swift rules - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1881](https://github.com/pmd/pmd/pull/1881): \[doc] Add ANTLR documentation - [Matías Fraga](https://github.com/matifraga) (@matifraga)
* [#1882](https://github.com/pmd/pmd/pull/1882): \[swift] UnavailableFunction Swift rule - [Tomás de Lucca](https://github.com/tomidelucca) (@tomidelucca)
* [#2830](https://github.com/pmd/pmd/pull/2830): \[apex] Apexlink POC - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#3866](https://github.com/pmd/pmd/pull/3866): \[core] Add CLI Progress Bar - [@JerritEic](https://github.com/JerritEic) (@JerritEic)
* [#4402](https://github.com/pmd/pmd/pull/4402): \[javascript] CPD: add support for Typescript using antlr4 grammar - [Paul Guyot](https://github.com/pguyot) (@pguyot)
* [#4403](https://github.com/pmd/pmd/pull/4403): \[julia] CPD: Add support for Julia code duplication  - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4412](https://github.com/pmd/pmd/pull/4412): \[doc] Added new error msg to ConstantsInInterface - [David Ljunggren](https://github.com/dague1) (@dague1)
* [#4426](https://github.com/pmd/pmd/pull/4426): \[cpd] New XML to HTML XLST report format for PMD CPD - [mohan-chinnappan-n](https://github.com/mohan-chinnappan-n) (@mohan-chinnappan-n)
* [#4428](https://github.com/pmd/pmd/pull/4428): \[apex] ApexBadCrypto bug fix for #4427 - inline detection of hard coded values - [Steven Stearns](https://github.com/sfdcsteve) (@sfdcsteve)
* [#4431](https://github.com/pmd/pmd/pull/4431): \[coco] CPD: Coco support for code duplication detection - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4444](https://github.com/pmd/pmd/pull/4444): \[java] CommentDefaultAccessModifier - ignore org.junit.jupiter.api.extension.RegisterExtension by default - [Nirvik Patel](https://github.com/nirvikpatel) (@nirvikpatel)
* [#4450](https://github.com/pmd/pmd/pull/4450): \[java] Fix #4449 AvoidAccessibilityAlteration: Correctly handle Lambda expressions in PrivilegedAction scenarios - [Seren](https://github.com/mohui1999) (@mohui1999)
* [#4452](https://github.com/pmd/pmd/pull/4452): \[doc] Update PMD_APEX_ROOT_DIRECTORY documentation reference - [nwcm](https://github.com/nwcm) (@nwcm)
* [#4470](https://github.com/pmd/pmd/pull/4470): \[cpp] CPD: Added strings as literal and ignore identifiers in sequences - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4474](https://github.com/pmd/pmd/pull/4474): \[java] ImmutableField: False positive with lombok (fixes #4254) - [Pim van der Loos](https://github.com/PimvanderLoos) (@PimvanderLoos)
* [#4488](https://github.com/pmd/pmd/pull/4488): \[java] Fix #4477: A false-positive about SignatureDeclareThrowsException - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4494](https://github.com/pmd/pmd/pull/4494): \[java] Fix #4487: A false-positive about UnnecessaryConstructor and @<!-- -->Inject and @<!-- -->Autowired - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4495](https://github.com/pmd/pmd/pull/4495): \[java] Fix #4493: false-positive about MissingStaticMethodInNonInstantiatableClass and @<!-- -->Inject - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4507](https://github.com/pmd/pmd/pull/4507): \[java] Fix #4503: A false negative about JUnitTestsShouldIncludeAssert and testng - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4520](https://github.com/pmd/pmd/pull/4520): \[doc] Fix typo: missing closing quotation mark after CPD-END - [João Dinis Ferreira](https://github.com/joaodinissf) (@joaodinissf)
* [#4528](https://github.com/pmd/pmd/pull/4528): \[apex] Update to apexlink - [Kevin Jones](https://github.com/nawforce) (@nawforce)
* [#4533](https://github.com/pmd/pmd/pull/4533): \[java] Fix #4063: False-negative about try/catch block in Loop - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4536](https://github.com/pmd/pmd/pull/4536): \[java] Fix #4268: CommentDefaultAccessModifier - false positive with TestNG's @<!-- -->Test annotation - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4537](https://github.com/pmd/pmd/pull/4537): \[java] Fix #4455: A false positive about FieldNamingConventions and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4538](https://github.com/pmd/pmd/pull/4538): \[java] Fix #4456: A false positive about FinalFieldCouldBeStatic and UtilityClass - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4540](https://github.com/pmd/pmd/pull/4540): \[java] Fix #4457: false negative about OverrideBothEqualsAndHashcode - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4541](https://github.com/pmd/pmd/pull/4541): \[java] Fix #4458: A false positive about RedundantFieldInitializer and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4542](https://github.com/pmd/pmd/pull/4542): \[java] Fix #4510: A false positive about ConstructorCallsOverridableMethod and @<!-- -->Value - [AnnaDev](https://github.com/LynnBroe) (@LynnBroe)
* [#4553](https://github.com/pmd/pmd/pull/4553): \[java] Fix #4492: GuardLogStatement gives false positive when argument is a Java method reference - [Anastasiia Koba](https://github.com/anastasiia-koba) (@anastasiia-koba)
* [#4637](https://github.com/pmd/pmd/pull/4637): \[java] fix #4634 - JUnit4TestShouldUseTestAnnotation false positive with TestNG - [Krystian Dabrowski](https://github.com/krdabrowski) (@krdabrowski)
* [#4649](https://github.com/pmd/pmd/pull/4649): \[apex] Add SObjectType and SObjectField to list of injectable SOQL variable types - [Richard Corfield](https://github.com/rcorfieldffdc) (@rcorfieldffdc)
* [#4651](https://github.com/pmd/pmd/pull/4651): \[doc] Add "Tencent Cloud Code Analysis" in Tools / Integrations - [yale](https://github.com/cyw3) (@cyw3)
* [#4664](https://github.com/pmd/pmd/pull/4664): \[cli] CPD: Fix NPE when only `--file-list` is specified - [Wener](https://github.com/wener-tiobe) (@wener-tiobe)
* [#4665](https://github.com/pmd/pmd/pull/4665): \[java] Doc: Fix references AutoClosable -> AutoCloseable - [Andrey Bozhko](https://github.com/AndreyBozhko) (@AndreyBozhko)

### 📈 Stats
* 5007 commits
* 658 closed tickets & PRs
* Days since last release: 122

{% endtocmaker %}

