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

*   The Java rule {% rule "java/errorprone/DoNotCallSystemExit" %} has been renamed to
    {% rule "java/errorprone/DoNotTerminateVM" %}, since it checks for all the following calls:
    `System.exit(int)`, `Runtime.exit(int)`, `Runtime.halt(int)`. All these calls terminate
    the Java VM, which is bad, if the VM runs an application server which many independent applications.

### Fixed Issues

*   core
    *   [#2831](https://github.com/pmd/pmd/pull/2831): \[core] Fix XMLRenderer newlines when running under IBM Java
*   java-errorprone
    *   [#2157](https://github.com/pmd/pmd/issues/2157): \[java] Improve DoNotCallSystemExit: permit call in main(), flag System.halt
    *   [#2764](https://github.com/pmd/pmd/issues/2764): \[java] CloseResourceRule does not recognize multiple assignment done to resource
*   miscellaneous
    *   [#2823](https://github.com/pmd/pmd/issues/2823): \[doc] Renamed/Moved rules are missing in documentation
*   vf (Salesforce VisualForce)
    *   [#2765](https://github.com/pmd/pmd/issues/2765): \[vf] Attributes with dot cause a VfParseException

### API Changes

### External Contributions

*   [#2803](https://github.com/pmd/pmd/pull/2803): \[java] Improve DoNotCallSystemExit (Fixes #2157) - [Vitaly Polonetsky](https://github.com/mvitaly)
*   [#2809](https://github.com/pmd/pmd/pull/2809): \[java] Move test config from file to test class - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2810](https://github.com/pmd/pmd/pull/2810): \[core] Move method "renderTempFile" to XMLRendererTest - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2811](https://github.com/pmd/pmd/pull/2811): \[java] CloseResource - Fix #2764: False-negative when re-assigning variable - [Andi Pabst](https://github.com/andipabst)
*   [#2813](https://github.com/pmd/pmd/pull/2813): \[core] Use JUnit's TemporaryFolder rule - [Stefan Birkner](https://github.com/stefanbirkner)
*   [#2829](https://github.com/pmd/pmd/pull/2829): \[doc] Small correction in pmd\_report\_formats.md - [Gustavo Krieger](https://github.com/gustavopcassol)
*   [#2834](https://github.com/pmd/pmd/pull/2834): \[vf] Allow attributes with dot in Visualforce - [rmohan20](https://github.com/rmohan20)

{% endtocmaker %}

