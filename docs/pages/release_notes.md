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
* java-design
    * [#3981](https://github.com/pmd/pmd/issues/3981): \[java] ImmutableField reports fields annotated with @Value (Spring)
    * [#3998](https://github.com/pmd/pmd/issues/3998): \[java] ImmutableField reports fields annotated with @Captor (Mockito)
    * [#3824](https://github.com/pmd/pmd/issues/3824): \[java] UnusedPrivateField: Do not flag fields annotated with @Version
    * [#3825](https://github.com/pmd/pmd/issues/3825): \[java] UnusedPrivateField: Do not flag fields annotated with @Id or @EmbeddedId
* java-errorprone
    * [#3936](https://github.com/pmd/pmd/issues/3936): \[java] AvoidFieldNameMatchingMethodName should consider enum class
    * [#3937](https://github.com/pmd/pmd/issues/3937): \[java] AvoidDuplicateLiterals - uncompilable test cases

### API Changes

### External Contributions
* [#3985](https://github.com/pmd/pmd/pull/3985): \[java] Fix false negative problem about Enum in AvoidFieldNameMatchingMethodName #3936 - [@Scrsloota](https://github.com/Scrsloota)
* [#3993](https://github.com/pmd/pmd/pull/3993): \[java] AvoidDuplicateLiterals - Add the method "buz" definition to test cases - [@dalizi007](https://github.com/dalizi007)
* [#4002](https://github.com/pmd/pmd/pull/4002): \[java] ImmutableField - Ignore fields annotated with @Value (Spring) or @Captor (Mockito) - [@jjlharrison](https://github.com/jjlharrison)
* [#4003](https://github.com/pmd/pmd/pull/4003): \[java] UnusedPrivateField - Ignore fields annotated with @Id/@EmbeddedId/@Version (JPA) or @Mock/@Spy/@MockBean (Mockito/Spring) - [@jjlharrison](https://github.com/jjlharrison)

{% endtocmaker %}

