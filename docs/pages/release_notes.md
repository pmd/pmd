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
* java-codestyle
    * [#4201](https://github.com/pmd/pmd/issues/4201): \[java] CommentDefaultAccessModifier should consider lombok's @<!-- -->Value
* java-design
    * [#4200](https://github.com/pmd/pmd/issues/4200): \[java] ClassWithOnlyPrivateConstructorsShouldBeFinal should consider lombok's @<!-- -->Value

### API Changes

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)
* [#4198](https://github.com/pmd/pmd/pull/4198): \[doc] Add supported CPD languages - [Jeroen van Wilgenburg](https://github.com/jvwilge) (@jvwilge)
* [#4202](https://github.com/pmd/pmd/pull/4202): \[java] Fix #4200 and #4201: ClassWithOnlyPrivateConstructorsShouldBeFinal, CommentDefaultAccessModifier: Exclude lombok @<!-- -->Value annotation - [Lynn](https://github.com/LynnBroe) (@LynnBroe)

{% endtocmaker %}

