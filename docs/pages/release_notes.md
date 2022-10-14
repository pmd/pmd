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
* The new Apex rule {% rule apex/bestpractices/ApexUnitTestClassShouldHaveRunAs %} ensures that unit tests
  use [System.runAs()](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_testing_tools_runas.htm)
  at least once. This makes the tests more robust, and independent from the user running it.

```xml
<rule ref="category/apex/bestpractices.xml/ApexUnitTestClassShouldHaveRunAs"/>
```

The rule is part of the quickstart.xml ruleset.

### Fixed Issues
* apex
    * [#4149](https://github.com/pmd/pmd/issues/4149): \[apex] New rule: ApexUnitTestClassShouldHaveRunAs
* doc
    * [#4144](https://github.com/pmd/pmd/pull/4144) \[doc] Update docs to reflect supported languages
* java-codestyle
    * [#4139](https://github.com/pmd/pmd/issues/4139): \[java] UnnecessaryFullyQualifiedName FP when the same simple class name exists in the current package
* java-documentation
    * [#4141](https://github.com/pmd/pmd/issues/4141): \[java] UncommentedEmptyConstructor FP when constructor annotated with @<!-- -->Autowired

### API Changes

### External Contributions
* [#4142](https://github.com/pmd/pmd/pull/4142): \[java] fix #4141 Update UncommentedEmptyConstructor - ignore @<!-- -->Autowired annotations - [Lynn](https://github.com/LynnBroe) (@LynnBroe)
* [#4150](https://github.com/pmd/pmd/pull/4150): \[apex] New rule ApexUnitTestClassShouldHaveRunAs #4149 - [Thomas Prouvot](https://github.com/tprouvot) (@tprouvot)

{% endtocmaker %}

