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

#### Renamed Rules

*   The Java rule [`DoNotCallSystemExit`](https://pmd.github.io/latest/pmd_rules_java_errorprone.html#donotcallsystemexit) has been renamed to
    {% rule "java/errorprone/DoNotTerminateVM" %}, since it checks for all the following calls:
    `System.exit(int)`, `Runtime.exit(int)`, `Runtime.halt(int)`. All these calls terminate
    the Java VM, which is bad, if the VM runs an application server which many independent applications.

### Fixed Issues

* core
    * [#2831](https://github.com/pmd/pmd/pull/2831): \[core] Fix XMLRenderer newlines when running under IBM Java
* java-errorprone
    * [#2157](https://github.com/pmd/pmd/issues/2157): \[java] Improve DoNotCallSystemExit: permit call in main(), flag System.halt

### API Changes

### External Contributions

*   [#2803](https://github.com/pmd/pmd/pull/2803): \[java] Improve DoNotCallSystemExit (Fixes #2157) - [Vitaly Polonetsky](https://github.com/mvitaly)
*   [#2809](https://github.com/pmd/pmd/pull/2809): \[java] Move test config from file to test class - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2810](https://github.com/pmd/pmd/pull/2810): \[core] Move method "renderTempFile" to XMLRendererTest - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2813](https://github.com/pmd/pmd/pull/2813): \[core] Use JUnit's TemporaryFolder rule - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2829](https://github.com/pmd/pmd/pull/2829): \[doc] Small correction in pmd\_report\_formats.md - [Gustavo Krieger](https://github.com/gustavopcassol)

{% endtocmaker %}

