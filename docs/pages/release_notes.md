---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

### Fixed Issues

### API Changes

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

##### Internal APIs

* {% jdoc core::renderers.CSVWriter %}
* Some fields in {% jdoc test::ant.AbstractAntTestHelper %}

### External Contributions
* [#4280](https://github.com/pmd/pmd/pull/4280): \[apex] Deprecate ApexRootNode.getApexVersion - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4285](https://github.com/pmd/pmd/pull/4285): \[java] CommentDefaultAccessModifier - add co.elastic.clients.util.VisibleForTesting as default suppressed annotation - [Matthew Luckam](https://github.com/mluckam) (@mluckam)

{% endtocmaker %}

