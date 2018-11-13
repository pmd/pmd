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

#### New Rules

*   The new Java rule {% rule "java/codestyle/UseUnderscoresInNumericLiterals" %} (`java-codestyle`)
    verifies that numeric literals over a given length (4 chars by default, but configurable) are using
    underscores every 3 digits for readability. The rule only applies to Java 7+ codebases.

### Fixed Issues
*   all
    *   [#1284](https://github.com/pmd/pmd/issues/1284): \[doc] Keep record of every currently deprecated API

*   all
    *   [#1318](https://github.com/pmd/pmd/issues/1318): \[test] Kotlin DSL to ease test writing
    *   [#1341](https://github.com/pmd/pmd/issues/1341): \[doc] Documentation Error with Regex Properties
*   java-codestyle
    *   [#1232](https://github.com/pmd/pmd/issues/1232): \[java] Detector for large numbers not separated by _
    *   [#1372](https://github.com/pmd/pmd/issues/1372): \[java] false positive for UselessQualifiedThis

### API Changes

{% jdoc_context "core @.lang.ast.xpath" %}

* The implementation of the adapters for the XPath engines Saxon and Jaxen (package {% jdoc_package @ %})
  are now deprecated. They'll be moved to an internal package come 7.0.0. Only {% jdoc @.Attribute %} remains public API.

{% endjdoc_context %}

### External Contributions

*   [#1384](https://github.com/pmd/pmd/pull/1384): \[java] New Rule - UseUnderscoresInNumericLiterals - [RajeshR](https://github.com/rajeshggwp)
*   [#1424](https://github.com/pmd/pmd/pull/1424): \[doc] #1341 Updating Regex Values in default Value Property - [avishvat](https://github.com/vishva007)
*   [#1428](https://github.com/pmd/pmd/pull/1428): \[core] Upgrading JCommander from 1.48 to 1.72 - [Thunderforge](https://github.com/Thunderforge)
*   [#1430](https://github.com/pmd/pmd/pull/1430): \[doc] Who really knows regex? - [Dem Pilafian](https://github.com/dpilafian)

{% endtocmaker %}

{% include note.html content="The release notes of previous versions are available [here](pmd_release_notes_old.html)" %}

