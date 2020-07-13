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

#### Scala cross compilation

Up until now the PMD Scala module has been compiled against scala 2.13 only by default.
However, this makes it impossible to use pmd as a library in scala projects,
that use scala 2.12, e.g. in sbt plugins. Therefore PMD now provides cross compiled pmd-scala
modules for both versions: **scala 2.12** and **scala 2.13**.

The new modules have new maven artifactIds. The old artifactId `net.sourceforge.pmd:pmd-scala:{{ site.pmd.version }}`
is still available, but is deprecated from now on. It has been demoted to be just a delegation to the new
`pmd-scala_2.13` module and will be removed eventually.

The coordinates for the new modules are:

```
<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.12</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>

<dependency>
    <groupId>net.sourceforge.pmd</groupId>
    <artifactId>pmd-scala_2.13</artifactId>
    <version>{{ site.pmd.version }}</version>
</dependency>
```

The command line version of PMD continues to use **scala 2.13**.

#### New Rules

*   The new Java Rule {% rule "java/codestyle/UnnecessaryCast" %} (`java-codestyle`)
    finds casts that are unnecessary while accessing collection elements.

### Fixed Issues

*   c#
    *   [#2551](https://github.com/pmd/pmd/issues/2551): \[c#] CPD suppression with comments doesn't work
*   java-design
    *   [#2563](https://github.com/pmd/pmd/pull/2563): \[java] UselessOverridingMethod false negative with already public methods
*   scala
    *   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13

### API Changes

*   The maven module `net.sourceforge.pmd:pmd-scala` is deprecated. Use `net.sourceforge.pmd:pmd-scala_2.13`
    or `net.sourceforge.pmd:pmd-scala_2.12` instead.

### External Contributions

*   [#2547](https://github.com/pmd/pmd/pull/2547): \[scala] Add cross compilation for scala 2.12 and 2.13 - [João Ferreira](https://github.com/jtjeferreira)
*   [#2567](https://github.com/pmd/pmd/pull/2567): \[c#] Fix CPD suppression with comments doesn't work - [Lixon Lookose](https://github.com/LixonLookose)
*   [#2590](https://github.com/pmd/pmd/pull/2590): Update libraries snyk is referring to as `unsafe` - [Artem Krosheninnikov](https://github.com/KroArtem)

{% endtocmaker %}

