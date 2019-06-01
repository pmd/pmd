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

#### Modified Rules

*   The Java rules {% rule "java/multithreading/UnsynchronizedStaticFormatter" %} and
    {% rule "java/multithreading/UnsynchronizedStaticDateFormatter" %} (`java-multithreading`)
    now prefer synchronized blocks by default. They will raise a violation, if the synchronization is implemented
    on the method level. To allow the old behavior, the new property `allowMethodLevelSynchronization` can
    be enabled.

### Fixed Issues

*   java-multithreading
    *   [#1814](https://github.com/pmd/pmd/issues/1814): \[java] UnsynchronizedStaticFormatter documentation and implementation wrong
    *   [#1815](https://github.com/pmd/pmd/issues/1815): \[java] False negative in UnsynchronizedStaticFormatter

### API Changes

### External Contributions

*   [#1829](https://github.com/pmd/pmd/pull/1829): \[java] Fix false negative in UnsynchronizedStaticFormatter - [Srinivasan Venkatachalam](https://github.com/Srini1993)

{% endtocmaker %}

