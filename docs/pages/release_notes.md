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
    * [#3999](https://github.com/pmd/pmd/issues/3999): \[cli] All files are analyzed despite parameter `--file-list`
    * [#4009](https://github.com/pmd/pmd/issues/4009): \[core] Cannot build PMD with Temurin 17
* java-design
    * [#3823](https://github.com/pmd/pmd/issues/3823): \[java] ImmutableField: Do not flag fields in @Entity
    * [#3981](https://github.com/pmd/pmd/issues/3981): \[java] ImmutableField reports fields annotated with @Value (Spring)
    * [#3998](https://github.com/pmd/pmd/issues/3998): \[java] ImmutableField reports fields annotated with @Captor (Mockito)
    * [#4004](https://github.com/pmd/pmd/issues/4004): \[java] ImmutableField reports fields annotated with @GwtMock (GwtMockito) and @Spy (Mockito)
    * [#4008](https://github.com/pmd/pmd/issues/4008): \[java] ImmutableField not reporting fields that are only initialized in the declaration
    * [#4011](https://github.com/pmd/pmd/issues/4011): \[java] ImmutableField: Do not flag fields annotated with @Inject
    * [#4020](https://github.com/pmd/pmd/issues/4020): \[java] ImmutableField reports fields annotated with @FindBy and @FindBys (Selenium)
* java-errorprone
    * [#3936](https://github.com/pmd/pmd/issues/3936): \[java] AvoidFieldNameMatchingMethodName should consider enum class
    * [#3937](https://github.com/pmd/pmd/issues/3937): \[java] AvoidDuplicateLiterals - uncompilable test cases

### API Changes

### External Contributions
* [#3985](https://github.com/pmd/pmd/pull/3985): \[java] Fix false negative problem about Enum in AvoidFieldNameMatchingMethodName #3936 - [@Scrsloota](https://github.com/Scrsloota)
* [#3993](https://github.com/pmd/pmd/pull/3993): \[java] AvoidDuplicateLiterals - Add the method "buz" definition to test cases - [@dalizi007](https://github.com/dalizi007)
* [#4002](https://github.com/pmd/pmd/pull/4002): \[java] ImmutableField - Ignore fields annotated with @Value (Spring) or @Captor (Mockito) - [@jjlharrison](https://github.com/jjlharrison)
* [#4006](https://github.com/pmd/pmd/pull/4006): \[doc] Fix eclipse plugin update site URL - [@shiomiyan](https://github.com/shiomiyan)
* [#4010](https://github.com/pmd/pmd/pull/4010): \[core] Bump kotlin to version 1.7.0 - [@maikelsteneker](https://github.com/maikelsteneker)

{% endtocmaker %}

