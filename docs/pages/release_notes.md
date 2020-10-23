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

*   The new Apex rule {% rule "apex/performance/OperationWithLimitsInLoop" %} (`apex-performance`)
    finds operations in loops that may hit governor limits such as DML operations, SOQL
    queries and more. The rule replaces the three rules "AvoidDmlStatementsInLoops", "AvoidSoqlInLoops",
    and "AvoidSoslInLoops".

#### Renamed Rules

*   The Java rule {% rule "java/errorprone/DoNotCallSystemExit" %} has been renamed to
    {% rule "java/errorprone/DoNotTerminateVM" %}, since it checks for all the following calls:
    `System.exit(int)`, `Runtime.exit(int)`, `Runtime.halt(int)`. All these calls terminate
    the Java VM, which is bad, if the VM runs an application server which many independent applications.

#### Deprecated Rules

*   The Apex rules {% rule "apex/performance/AvoidDmlStatementsInLoops" %},
    {% rule "apex/performance/AvoidSoqlInLoops" %} and {% rule "apex/performance/AvoidSoslInLoops" %}
    (`apex-performance`) are deprecated in favour of the new rule
    {% rule "apex/performance/OperationWithLimitsInLoop" %}. The deprecated rules will be removed
    with PMD 7.0.0.

### Fixed Issues

*   apex-performance
    *   [#1713](https://github.com/pmd/pmd/issues/1713): \[apex] Mark Database DML statements in For Loop
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
*   [#2816](https://github.com/pmd/pmd/pull/2816): \[apex] Detect 'Database' method invocations inside loops - [Jeff Bartolotta](https://github.com/jbartolotta-sfdc)
*   [#2829](https://github.com/pmd/pmd/pull/2829): \[doc] Small correction in pmd\_report\_formats.md - [Gustavo Krieger](https://github.com/gustavopcassol)
*   [#2834](https://github.com/pmd/pmd/pull/2834): \[vf] Allow attributes with dot in Visualforce - [rmohan20](https://github.com/rmohan20)
*   [#2842](https://github.com/pmd/pmd/pull/2842): \[core] Bump antlr4 from 4.7 to 4.7.2 - [Adrien Lecharpentier](https://github.com/alecharp)
*   [#2865](https://github.com/pmd/pmd/pull/2865): \[java] (doc) Update ExcessiveImports example code for clarity - [Gustavo Krieger](https://github.com/gustavopcassol)
*   [#2866](https://github.com/pmd/pmd/pull/2866): \[java] (doc) Fix example for CouplingBetweenObjects - [Gustavo Krieger](https://github.com/gustavopcassol)

{% endtocmaker %}

