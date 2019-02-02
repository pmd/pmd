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

*   java-bestpractices
    *   [#808](https://github.com/pmd/pmd/issues/808): \[java] AccessorMethodGeneration false positives with compile time constants
    *   [#1555](https://github.com/pmd/pmd/issues/1555): \[java] UnusedImports false positive for method parameter type in @see Javadoc
*   java-codestyle
    *   [#1543](https://github.com/pmd/pmd/issues/1543): \[java] LinguisticNaming should ignore overriden methods
    *   [#1547](https://github.com/pmd/pmd/issues/1547): \[java] AtLeastOneConstructorRule: false-positive with lombok.AllArgsConstructor

### API Changes

### External Contributions

*   [#1623](https://github.com/pmd/pmd/pull/1623): \[java] Fix lombok.AllArgsConstructor support - [Bobby Wertman](https://github.com/CasualSuperman)
*   [#1625](https://github.com/pmd/pmd/pull/1625): \[java] UnusedImports false positive for method parameter type in @see Javadoc - [Shubham](https://github.com/Shubham-2k17)
*   [#1628](https://github.com/pmd/pmd/pull/1628): \[java] LinguisticNaming should ignore overriden methods - [Shubham](https://github.com/Shubham-2k17)
*   [#1637](https://github.com/pmd/pmd/pull/1637): \[java] Compile time constants initialized by literals avoided by AccessorMethodGenerationRule - [Shubham](https://github.com/Shubham-2k17)

{% endtocmaker %}

