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

#### New report format html-report-v2.xslt

Thanks to @mohan-chinnappan-n a new PMD report format has been added which features a data table
with charting functions. It uses an XSLT stylesheet to convert PMD's XML format into HTML.

See [the example report](report-examples/html-report-v2.html).

### Fixed Issues

### API Changes

#### Deprecated APIs

##### For removal

* {% jdoc !!apex::lang.apex.ast.ApexRootNode#getApexVersion() %} has been deprecated for removal. The version returned is
  always `Version.CURRENT`, as the apex compiler integration doesn't use additional information which Apex version
  actually is used. Therefore, this method can't be used to determine the Apex version of the project
  that is being analyzed.

##### Internal APIs

* {% jdoc core::renderers.CSVWriter %}

### External Contributions
* [#4125](https://github.com/pmd/pmd/pull/4125): \[core] New report format html-report-v2.xslt to provide html with datatable and chart features - [Mohan Chinnappan](https://github.com/mohan-chinnappan-n) - (@mohan-chinnappan-n)
* [#4280](https://github.com/pmd/pmd/pull/4280): \[apex] Deprecate ApexRootNode.getApexVersion - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4285](https://github.com/pmd/pmd/pull/4285): \[java] CommentDefaultAccessModifier - add co.elastic.clients.util.VisibleForTesting as default suppressed annotation - [Matthew Luckam](https://github.com/mluckam) (@mluckam)

{% endtocmaker %}

