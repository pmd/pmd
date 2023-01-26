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

* {% jdoc !!apex::lang.apex.ast.ApexRootNode#getApexVersion() %} has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.
* {% jdoc !!core::cpd.CPDConfiguration#setEncoding(java.lang.String) %} and
  {% jdoc !!core::cpd.CPDConfiguration#getEncoding() %}. Use the methods
  {% jdoc core::AbstractConfiguration#getSourceEncoding() %} and
  {% jdoc core::AbstractConfiguration#setSourceEncoding(java.lang.String) %} instead. Both are available
  for `CPDConfiguration` which extends `AbstractConfiguration`.
* {% jdoc test::cli.BaseCLITest %} and {% jdoc test::cli.BaseCPDCLITest %} have been deprecated for removal without
  replacement. CLI tests should be done in pmd-core only (and in PMD7 in pmd-cli). Individual language modules
  shouldn't need to test the CLI integration logic again. Instead, the individual language modules should test their
  functionality as unit tests.
* {% jdoc core::cpd.CPDConfiguration.LanguageConverter %}

* {% jdoc !!core::lang.document.FileCollector#addZipFile(java.nio.file.Path) %} has been deprecated. It is replaced
  by {% jdoc !!core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} which directly adds the
  content of the zip file for analysis.

* {% jdoc !!core::PMDConfiguration#setReportShortNames(boolean) %} and
  {% jdoc !!core::PMDConfiguration#isReportShortNames() %} have been deprecated for removal.
  Use {% jdoc !!core::PMDConfiguration#addRelativizeRoot(java.nio.file.Path) %} instead.

##### Internal APIs

* {% jdoc core::renderers.CSVWriter %}
* Some fields in {% jdoc test::ant.AbstractAntTestHelper %}

##### Experimental APIs

* CPDReport has a new method which limited mutation of a given report:
  * {%jdoc core::cpd.CPDReport#filterMatches(net.sourceforge.pmd.util.Predicate) %} creates a new CPD report
    with some matches removed with a given predicate based filter.

### External Contributions
* [#4110](https://github.com/pmd/pmd/pull/4110): \[apex] Feature/unused variable bind false positive with dynamic SOQL - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)
* [#4125](https://github.com/pmd/pmd/pull/4125): \[core] New report format html-report-v2.xslt to provide html with datatable and chart features - [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) - (@mohan-chinnappan-n)
* [#4280](https://github.com/pmd/pmd/pull/4280): \[apex] Deprecate ApexRootNode.getApexVersion - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4285](https://github.com/pmd/pmd/pull/4285): \[java] CommentDefaultAccessModifier - add co.elastic.clients.util.VisibleForTesting as default suppressed annotation - [Matthew Luckam](https://github.com/mluckam) (@mluckam)

{% endtocmaker %}

