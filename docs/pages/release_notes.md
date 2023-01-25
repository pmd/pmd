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
* core
  * [#4026](https://github.com/pmd/pmd/issues/4026): \[cli] Filenames printed as absolute paths in the report despite parameter `--short-names`

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

* {% jdoc !!core::lang.document.FileCollector#addZipFile(java.nio.file.Path) %} has been deprecated. It is replaced
  by {% jdoc !!core::lang.document.FileCollector#addZipFileWithContent(java.nio.file.Path) %} which directly adds the
  content of the zip file for analysis.

* {% jdoc !!core::PMDConfiguration#setReportShortNames(boolean) %} and
  {% jdoc !!core::PMDConfiguration#isReportShortNames() %} have been deprecated for removal.
  Use {% jdoc !!core::PMDConfiguration#addRelativizeRoot(java.nio.file.Path) %} instead.

##### Internal APIs

* {% jdoc core::renderers.CSVWriter %}

### External Contributions
* [#4280](https://github.com/pmd/pmd/pull/4280): \[apex] Deprecate ApexRootNode.getApexVersion - [Aaron Hurst](https://github.com/aaronhurst-google) (@aaronhurst-google)
* [#4285](https://github.com/pmd/pmd/pull/4285): \[java] CommentDefaultAccessModifier - add co.elastic.clients.util.VisibleForTesting as default suppressed annotation - [Matthew Luckam](https://github.com/mluckam) (@mluckam)

{% endtocmaker %}

