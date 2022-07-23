---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

<!-- NOTE: THESE RELEASE NOTES ARE THOSE FROM MASTER -->
<!-- They were copied to avoid merge conflicts when merging back master -->
<!-- the 7_0_0_release_notes.md is the page to be used when adding new 7.0.0 changes -->


## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

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
* core
    * [#4021](https://github.com/pmd/pmd/pull/4021): \[core] CPD: Add total number of tokens to XML reports
    * [#4031](https://github.com/pmd/pmd/issues/4031): \[core] If report is written to stdout, stdout should not be closed
* java
    * [#4015](https://github.com/pmd/pmd/issues/4015): \[java] Support JDK 19
* java-bestpractices
    * [#3455](https://github.com/pmd/pmd/issues/3455): \[java] WhileLoopWithLiteralBoolean - false negative with complex expressions
* java-design
    * [#3729](https://github.com/pmd/pmd/issues/3729): \[java] TooManyMethods ignores "real" methods which are named like getters or setters
    * [#3949](https://github.com/pmd/pmd/issues/3949): \[java] FinalFieldCouldBeStatic - false negative with unnecessary parenthesis
* java-performance
    * [#3625](https://github.com/pmd/pmd/issues/3625): \[java] AddEmptyString - false negative with empty var
* test
    * [#3302](https://github.com/pmd/pmd/pull/3302): \[test] Improve xml test schema
    * [#3758](https://github.com/pmd/pmd/issues/3758): \[test] Move pmd-test to java 8
    * [#3976](https://github.com/pmd/pmd/pull/3976): \[test] Extract xml schema module

### API Changes

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

* The experimental Java AST class {% jdoc java::lang.java.ast.ASTGuardedPattern %} has been deprecated and
  will be removed. It was introduced for Java 17 and Java 18 Preview as part of pattern matching for switch,
  but it is no longer supported with Java 19 Preview.
* The interface {% jdoc core::cpd.renderer.CPDRenderer %} is deprecated. For custom CPD renderers
  the new interface {% jdoc core::cpd.renderer.CPDReportRenderer %} should be used.
* The class {% jdoc test::testframework.TestDescriptor %} is deprecated, replaced with {% jdoc test-schema::testframework.RuleTestDescriptor %}.
* Many methods of {% jdoc test::testframework.RuleTst %} have been deprecated as internal API.

#### Experimental APIs

* To support the Java preview language features "Pattern Matching for Switch" and "Record Patterns", the following
  AST nodes have been introduced as experimental:
    * {% jdoc java::lang.java.ast.ASTSwitchGuard %}
    * {% jdoc java::lang.java.ast.ASTRecordPattern %}
    * {% jdoc java::lang.java.ast.ASTComponentPatternList %}

#### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {%jdoc !!core::cpd.CPDConfiguration#setRenderer(net.sourceforge.pmd.cpd.Renderer) %}
* {%jdoc !!core::cpd.CPDConfiguration#setCPDRenderer(net.sourceforge.pmd.cpd.renderer.CPDRenderer) %}
* {%jdoc !!core::cpd.CPDConfiguration#getRenderer() %}
* {%jdoc !!core::cpd.CPDConfiguration#getCPDRenderer() %}
* {%jdoc !!core::cpd.CPDConfiguration#getRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc !!core::cpd.CPDConfiguration#getCPDRendererFromString(java.lang.String,java.lang.String) %}
* {%jdoc core::cpd.renderer.CPDRendererAdapter %}

### External Contributions
* [#3984](https://github.com/pmd/pmd/pull/3984): \[java] Fix AddEmptyString false-negative issue - [@LiGaOg](https://github.com/LiGaOg)
* [#3988](https://github.com/pmd/pmd/pull/3988): \[java] Modify WhileLoopWithLiteralBoolean to meet the missing case #3455 - [@VoidxHoshi](https://github.com/VoidxHoshi)
* [#3992](https://github.com/pmd/pmd/pull/3992): \[java] FinalFieldCouldBeStatic - fix false negative with unnecessary parenthesis - [@dalizi007](https://github.com/dalizi007)
* [#3994](https://github.com/pmd/pmd/pull/3994): \[java] TooManyMethods - improve getter/setter detection (#3729) - [@341816041](https://github.com/341816041)
* [#4017](https://github.com/pmd/pmd/pull/4017): Add Gherkin support to CPD - [@ASBrouwers](https://github.com/ASBrouwers)
* [#4021](https://github.com/pmd/pmd/pull/4021): \[core] CPD: Add total number of tokens to XML reports - [@maikelsteneker](https://github.com/maikelsteneker)

{% endtocmaker %}

