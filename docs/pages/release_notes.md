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

* pmd-java
    * [#2708](https://github.com/pmd/pmd/issues/2708): \[java] False positive FinalFieldCouldBeStatic when using lombok Builder.Default
    * [#2738](https://github.com/pmd/pmd/issues/2738): \[java] Custom rule with @ExhaustiveEnumSwitch throws NPE
    * [#2756](https://github.com/pmd/pmd/issues/2756): \[java] TypeTestUtil fails with NPE for anonymous class
    * [#2759](https://github.com/pmd/pmd/issues/2759): \[java] False positive in UnusedAssignment
    * [#2767](https://github.com/pmd/pmd/issues/2767): \[java] IndexOutOfBoundsException when parsing an initializer BlockStatement


### API Changes

### External Contributions

* [#2747](https://github.com/pmd/pmd/pull/2747): \[java] Don't trigger FinalFieldCouldBeStatic when field is annotated with lombok @Builder.Default - [Ollie Abbey](https://github.com/ollieabbey)
* [#2773](https://github.com/pmd/pmd/pull/2773): \[java] issue-2738: Adding null check to avoid npe when switch case is default - [Nimit Patel](https://github.com/nimit-patel)

{% endtocmaker %}

