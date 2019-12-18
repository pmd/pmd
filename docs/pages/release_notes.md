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

*   The Apex rule {% rule "apex/errorprone/ApexCSRF" %} (`apex-errorprone`) has been moved from category
    "Security" to "Error Prone". The Apex runtime already prevents DML statements from being executed, but only
    at runtime. So, if you try to do this, you'll get an error at runtime, hence this is error prone. See also
    the discussion on [#2064](https://github.com/pmd/pmd/issues/2064).

### Fixed Issues

*   apex
    *   [#2092](https://github.com/pmd/pmd/issues/2092): \[apex] ApexLexer logs visible when Apex is the selected language upon starting the designer
*   core
    *   [#2096](https://github.com/pmd/pmd/issues/2096): \[core] Referencing category errorprone.xml produces deprecation warnings for InvalidSlf4jMessageFormat
*   java
    *   [#1861](https://github.com/pmd/pmd/issues/1861): \[java] Be more lenient with version numbers

### API Changes

### External Contributions

*   [#2088](https://github.com/pmd/pmd/pull/2088): \[java] Add more version shortcuts for older java - [Henning Schmiedehausen](https://github.com/hgschmie)
*   [#2089](https://github.com/pmd/pmd/pull/2089): \[core] Minor unrelated improvements to code - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)
*   [#2091](https://github.com/pmd/pmd/pull/2091): \[core] Fix pmd warnings (IdenticalCatchCases) - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)

{% endtocmaker %}

